package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Test1secData {
	
	public static void testYesterdayHL(ArrayList<QuoteShort> data,
			ArrayList<Quote> dailyData,int begin,int end,int h1,int h2,
			int sl,int tp,int offset1,int offset2,
			boolean onlyFirst){
		//variables
		PriceTestResult resBuy = new PriceTestResult();
		PriceTestResult resSell = new PriceTestResult();
		ArrayList<MaxMinResult> mmArray = new ArrayList<MaxMinResult>();
		int totalTrades = 0;
		int totalWins = 0;
		int actualIndex = 0;
		int lastDay = -1;
		Calendar cal = null;
		boolean isMax = false;
		boolean isMin = false;
		int min = 99999;
		int max = -999;
		int end2 = end;
		if (end2>dailyData.size()-1) end2 = dailyData.size()-1;
		int index = 0;
		Calendar todayCal = Calendar.getInstance();
		Calendar calj = Calendar.getInstance();
		//bucle principal
		for (int i=begin;i<=end2;i++){
			Quote q = dailyData.get(i); //hoy
			Quote y = dailyData.get(i-1); //ayer
			todayCal.setTimeInMillis(q.getDate().getTime());
			actualIndex = TradingUtils.findQuoteShort(data, todayCal, index);			
			if (actualIndex>index) index = actualIndex;
			//System.out.println("index encontrada: "+index);
			//intraday data today
			ArrayList<QuoteShort> dayData = TradingUtils.getDayDataShort(data, todayCal,index);
					
			isMax = false;
			isMin = false;
			min = 30000;
			max = 0;
			short yesterdayH = (short) (y.getHigh()*10000);
			short yesterdayL = (short) (y.getLow()*10000);
			
			for (int j=0;j<dayData.size()-1;j++){
				QuoteShort qj = dayData.get(j);
				int highDiff = qj.getHigh()-yesterdayH;
				int maxDiff  = qj.getHigh()-max;
				int lowDiff  = yesterdayL-qj.getLow();
				int minDiff  = min-qj.getLow();
				//maximos y minimos
				if (highDiff>=offset1 && highDiff<=offset2){ //&& maxDiff>=1){ //new high
					//hacemos tests
					isMax = true;
					//max = qj.getHigh();
				}else if (lowDiff>=offset1 && lowDiff<=offset2){// && minDiff>=1){//new low
					//hacemos test
					isMin = true;
					//min = qj.getLow();
				}				
				if (maxDiff>=1) max = qj.getHigh();
				if (minDiff>=1) min = qj.getLow();
				
				QuoteShort.getCalendar(calj, qj);
				int hj = calj.get(Calendar.HOUR_OF_DAY);
				//System.out.println("h: "+hj);
								
				if (isMin || isMax){					
					if (hj>=h1 && hj<=h2){ //minimo o maximo
						/*System.out.println("minMax tocado "
								+" yHL "+PrintUtils.Print4dec(y.getHigh())+" "+PrintUtils.Print4dec(y.getLow())
								+" "+DateUtils.datePrint(calj)
								+" "+PrintUtils.getOHLC(qj));*/
						int end3   = data.size()-1;
						int begin3 = index+j+1;
						//System.out.println("begin: "+begin3);
						int entryValue = data.get(begin3).getOpen();
						MaxMinResult mm = new MaxMinResult();
						mm.getCal().setTimeInMillis(calj.getTimeInMillis());
		  				mm.setSl(sl);
		  				mm.setTp(tp);
						//buy
						if (isMin){							
							int slValue = entryValue-sl;
							int tpValue = entryValue+tp;							
							TradingUtils.testPriceMovementShort(resBuy,data, begin3, end3, entryValue,slValue,tpValue,1,false);
							int win = resBuy.getWin();
			  				mm.getCloseTime().setTimeInMillis(resBuy.getCloseTime().getTimeInMillis());							
							mm.setWin(win);
							if (resBuy.getWin()==1) totalWins++;
							/*System.out.println("minimo yesterday: "
									+" "+totalTrades
									+" "+DateUtils.datePrint(y.getDate())
									+" "+PrintUtils.Print4dec(y.getLow())
									+" "+DateUtils.datePrint(calj)
									+" "+PrintUtils.Print4dec(min)
									+" "+win
									);*/
						}else if (isMax){
							
							//sell
							int slValue = entryValue+sl;
							int tpValue = entryValue-tp;
							TradingUtils.testPriceMovementShort(resSell,data, begin3, end3, entryValue,slValue,tpValue,0,false);
							int win = resSell.getWin();
							mm.getCloseTime().setTimeInMillis(resBuy.getCloseTime().getTimeInMillis());							
							mm.setWin(win);
							if (resSell.getWin()==1) totalWins++;
							/*System.out.println("maximo yesterday: "
									+" "+totalTrades
									+" "+DateUtils.datePrint(y.getDate())
									+" "+PrintUtils.Print4dec(y.getHigh())
									+" "+DateUtils.datePrint(calj)
									+" "+PrintUtils.Print4dec(max)
									+" "+win
									);*/
						}
						totalTrades++;
						mmArray.add(mm);
						//break; //rompemos seguro para este dia
					}//hj
					if (onlyFirst) break;//sólo la primera del dia
				}
				isMax=false;
				isMin=false;
			}//dayDaya
		}//for dailyData	
		double comm = 1.4;
		double winPer = totalWins*100.0/totalTrades;
  		double exp = (winPer*tp/100.0)-((100.0-winPer)/100.0)*sl-comm;
  		double kelly = exp*100.0/tp;
  		int maxConcurrent = SuperStrategy.calculateConcurrent(mmArray,null,null,null,tp,sl);
		double maxRisk = 100.0/maxConcurrent;
  		//double maxRisk =20.0;
		if (kelly<maxRisk) maxRisk = kelly;
		double profitPerTrade = exp*(maxRisk/sl);
		if (exp<=0) profitPerTrade = 0;
  		double finalCapital = 200*Math.pow(1.0+(profitPerTrade/100.0),totalTrades);
		System.out.println(
				" "
				+" h1= "+h1
				+" h2= "+h2
				+" tp= "+tp
				+" sl= "+sl
				+" off= "+offset1 +"-"+offset2
				+" tt= "+totalTrades
				+" maxC= "+maxConcurrent
				+" w%= "+PrintUtils.Print2dec(winPer, false,3) 
				+" ex= "+PrintUtils.Print2dec(exp, false,2)
				+" profitPerTrade= "+PrintUtils.Print2dec(profitPerTrade, false,2)
				+" "+PrintUtils.Print2dec2(finalCapital, true)
				);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		String path1sec = "c:\\fxdata\\EURUSD_PEP_1SEC_2009_01_01_2014_09_30.csv";
		
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 		= TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData	= ConvertLib.createDailyData(data);
		
		Sizeof.runGC ();
		long before = Sizeof.usedMemory();
		ArrayList<QuoteShort> data1sec = QuoteShort.readFromDisk(path1sec);
		Sizeof.runGC ();
		long after = Sizeof.usedMemory();
		double KB = (after-before)/1024;
		double MB = KB/1024;
		System.out.println(after-before+" bytes "+KB+" KB "+MB+" MB "+" quotes: "+data1sec.size()+" days= "+dailyData.size());
		
		int begin = 2400;
		int end   = 2977;
		int h1    = 0;
		int h2    = 23;
		int sl    = 15;
		int tp    = 5;
		boolean onlyFirst = false;
		int offset = 1;
		
		for (int offset1=-15;offset1<=30;offset1++){
			int offset2 = offset1+0;
			for (tp = 15;tp<=15;tp+=1){
				for (sl=tp;sl<=tp;sl+=tp/2){
					for (h1=0;h1<=0;h1++){
						h2 = h1+9;
						Test1secData.testYesterdayHL(data1sec, dailyData, begin, end, h1, h2, sl, tp,offset1,offset2,onlyFirst);
					}
				}
			}
		}
	}

}
