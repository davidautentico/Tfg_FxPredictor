package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MeanRevertingStrategy1 {

	
	
	public static void test(String header,ArrayList<Quote> data,ArrayList<Quote> dailyData,ArrayList<Double> atr,
			Calendar from,Calendar to,int day1,int day2,int h1,int h2,
			int atrPeriod,double mult,double offset,double sl,double tp,boolean testAll){
		
		//stats
		int totalWins = 0;
		int totalLosses = 0;
		String tradesStr="";
		//calculo atr		
		int index = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dailyData.size();i++){ //para cada dia
			Quote q = dailyData.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			//System.out.println("entrado "+i+" "+DateUtils.datePrint(cal));
			//test tiempo
			if (cal.getTimeInMillis()<from.getTimeInMillis() ){
				continue;
			}
			if (cal.getTimeInMillis()>to.getTimeInMillis()){
					//System.out.println("break");
					break;
			}
			//if (h<h1 || h> h2) continue;
			if (day<day1 || day>day2){
				//System.out.println("days");
				continue;
			}
						
			double atrValue = atr.get(i);
			double aboveValue = q.getOpen()+mult*atrValue*offset;
			double belowValue = q.getOpen()-mult*atrValue*offset;
			/*System.out.println("atr above and below: "
					+" "+PrintUtils.Print4dec(atrValue)
					+" "+PrintUtils.Print4dec(aboveValue)
					+" "+PrintUtils.Print4dec(belowValue));
			*/
			ArrayList<Quote> dayData = TradingUtils.getDayData(data, cal);
			
			PriceTestResult res = null;
			/*System.out.println("dayIndex atr above and below: "
					+" "+index
					+" "+PrintUtils.Print4dec(atrValue)
					+" "+PrintUtils.Print4dec(aboveValue)
					+" "+PrintUtils.Print4dec(belowValue));*/
			Calendar calj = Calendar.getInstance();
			for (int j=0;j<dayData.size();j++){
				Quote qj = dayData.get(j);	
				calj.setTime(calj.getTime());
				int hj = calj.get(Calendar.HOUR_OF_DAY);
				if (hj<h1) continue;
				if (hj>h2) break;
				index = TradingUtils.getDayIndex(data, cal,index);
				int end = data.size()-1;
				int begin = index+j+1;
				
				if (qj.getHigh()>=aboveValue){
					//System.out.println("ABOVE");
					double entryValue = aboveValue;
					double slValue = entryValue+mult*atrValue*sl;
					double tpValue = entryValue-mult*atrValue*tp;
					res = TradingUtils.testPriceMovement(data, begin, end, entryValue,slValue,tpValue,0);
					if (res.isWin()){
						totalWins++;
						tradesStr+="W";
					}else{
						totalLosses++;
						tradesStr+="L";
					}	
					if (!testAll) break; //si no testeamos todos salimos
				}
				if (qj.getLow()<=belowValue){
					double entryValue = belowValue;
					double slValue = entryValue-mult*atrValue*sl;
					double tpValue = entryValue+mult*atrValue*tp;
					res = TradingUtils.testPriceMovement(data, begin, end, entryValue,slValue,tpValue,1);
					if (res.isWin()){
						totalWins++;
						tradesStr+="W";
					}else{
						totalLosses++;
						tradesStr+="L";
					}
					if (!testAll) break;//si no testeamos todos salimos
				}							
			}			
		}
		
		int total = totalWins+totalLosses;
		double winPer = totalWins*100.0/total;
		double lossPer = 100.0-winPer;
		double exp = winPer*tp -lossPer*sl;
		System.out.println(header
				+" offset= "+PrintUtils.Print2(offset)
				+" sl= "+PrintUtils.Print2(sl)
				+" tp= "+PrintUtils.Print2(tp)
				+" total= "+total
				+" winPer= "+PrintUtils.Print2(winPer)+"%"
				+" exp= "+PrintUtils.Print2(exp)
				+" trades= "+tradesStr
				);
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.08.06.csv";
		//String path5m = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.07.25.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		//String path5m = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2005.01.01_2014.07.23.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
  		//ArrayList<Quote> weeklyData = ConvertLib.createWeeklyData(dailyData);
  		//ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(dailyData);
  		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		int year = 2009;
		from.set(2012, 0, 1);
		to.set(2014,  11, 31);
		int day1 = Calendar.MONDAY+0;
		int day2 = Calendar.MONDAY+4;
		int h1 = 0;
		int h2 = 11;
		int atrPeriod = 5;
		double mult = 0.0001;
  		double offset = 0.2;
  		double sl = 0.5;
  		double tp = 0.1;
  		boolean testAll = false;
  		System.out.println("total days= "+dailyData.size());
  		ArrayList<Double> atr = MathUtils.calculateAtr(dailyData, atrPeriod);
  		String header = "year = "+year;
  		for (tp=0.1;tp<=0.5;tp+=0.1){
	  		for (sl= 0.5;sl<=1.5;sl+=0.1){
		  		for (offset=0.1;offset<=0.1;offset+=0.05){
			  		for (day1=Calendar.MONDAY+0;day1<=Calendar.MONDAY+0;day1++){
			  			day2=day1+2;
			  			MeanRevertingStrategy1.test(header,data, dailyData,atr, from, to, day1, day2, h1, h2, atrPeriod, mult, offset, sl, tp, testAll);
			  		}
		  		}  	
	  		}
  		}
	}

}
