package drosa.phil.tma;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.IndicatorLib;
import drosa.phil.TMA;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyTma2 {

	
	public static void testTMA(ArrayList<Quote> data,ArrayList<TMA> tma,Calendar from,Calendar to,
			int dayH,int dayL,int h1,int h2,int pipsAbove){
	
		int pips=0;
		int barsCount=0;
		int totalDays = 0;
		int totalDayTouched = 0;
		int lastDay = -1;
		int lastTouched = -1;
		int lastIndex = 0;
		Calendar cal = Calendar.getInstance();		
		ArrayList<Integer> distances = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int h 	 = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			if (h<h1 || h>h2) continue;
			if (actualDay!=lastDay){
				totalDays++;
				lastDay = actualDay;
			}			
			int index = TMA.find(tma,year,month,actualDay,h,-1,-1,lastIndex);
			
			if (index>=0){
				TMA t = tma.get(index);
				lastIndex = index;
				
				int diffPipsH = TradingUtils.getPipsDiff(q.getHigh(), t.getUpper());
				int diffPipsL = TradingUtils.getPipsDiff(t.getLower(),q.getLow());
				if (diffPipsH>=pipsAbove || diffPipsL>=pipsAbove){
					barsCount++;					
					if (actualDay!= lastTouched){
						totalDayTouched++;
						lastTouched = actualDay;
					}
					if (diffPipsH>=diffPipsL){
						pips+=diffPipsH;
						distances.add(diffPipsH);
					}else{
						pips += diffPipsL;
						distances.add(diffPipsL);
					}
				}
			}
			
			if (actualDay!=lastDay) totalDays++;
		}
		
		System.out.println("h1 h2 barsCount totalTouched totalDays: "
				+h1+" "+h2
				+" "+barsCount
				+" "+totalDayTouched+" "+totalDays+" "+PrintUtils.Print2(totalDayTouched*100.0/totalDays)+"%"
				+" "+pips+" "+PrintUtils.Print2(pips*1.0/barsCount)	
				+" "+MathUtils.PrintPercents(distances)
				);
		//MathUtils.summary(" ", distances);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata";
		String file5m = path+"\\"+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.05.30.csv";
		
		ArrayList<Quote> dataI 			= DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> data1h 		= ConvertLib.convert(data, 12);
  		//data 		= ConvertLib.createDailyData(data);
  		Calendar from = Calendar.getInstance();
  		Calendar to = Calendar.getInstance();
  		int d1 = Calendar.MONDAY;
  		int d2 = Calendar.MONDAY+0;
  		int h1 = 0;
  		int h2 = 23;  		
  		double bandFactor = 2.9;
		int atrPeriod = 100;
		int halfLength = 56;
		int pipsAbove = 0;
		
		/*for (int i=0;i<data1h.size();i++){
			System.out.println(PrintUtils.Print(data1h.get(i)));
		}*/
		from.set(2013,9,1);
		to.set(2014,11,31);
		
		ArrayList<TMA> tma1h = IndicatorLib.calculateTMA_Array(data1h, 0,data1h.size()-1,bandFactor,halfLength,atrPeriod);
		
		
		for (d1=Calendar.MONDAY+4;d1<=Calendar.MONDAY+4;d1++){
			d2 = d1+0;
			for (h1=0;h1<=0;h1++){
				h2 = h1+23;
				StudyTma2.testTMA(data, tma1h, from, to, d1, d2, h1, h2, pipsAbove);
			}
		}
		
	}
}
