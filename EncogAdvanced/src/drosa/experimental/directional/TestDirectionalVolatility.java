package drosa.experimental.directional;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestDirectionalVolatility {
	
	public static void testDOHours(ArrayList<QuoteShort> data,
			int boxLen,
			int minDO1,int minDO2,
			int minBarLen,
			int h1,int h2){
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int actualRange = 100;
		int lastDay = -1;
		int max = -1;
		int min = -1;
		double sumDiff=0;
		int total = 0;
		int wins = 0;
		int dailyOpen = -1;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (max>=0 && min>=0){
					int range = max-min;
					dailyRanges.add(range);
					actualRange = range;
				}
				max = -1;
				min = -1;
				lastDay = day;
				dailyOpen = q.getOpen5();
			}
			
			QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, i+1, i+boxLen);
			int actualBarCO = q.getClose5()-q.getOpen5();
			int actualBarR = Math.abs(q.getHigh5()-q.getLow5());
			int diffDO = q.getClose5()-dailyOpen;
			int barLen = q.getHigh5()-q.getLow5();
			if (minBarLen<=barLen
					&& h>=h1 && h<=h2){
				if (
						actualBarCO>=0  && 
						diffDO>=minDO1 && diffDO<=minDO2
						//&& actualBarR>=minVol
						){//bull
					int maxPips = qMaxMin.getHigh5()-q1.getOpen5();
					int minPips = q1.getOpen5()-qMaxMin.getLow5();
					int diff = maxPips-minPips;
					sumDiff+=(diff*0.1);
					if (diff>=0) wins++;
					total++;
					/*System.out.println("[BUY] barLen q q1 max min"+" "+barLen+" "+q.toString()
							+" || "+q1.toString()+" || "+qMaxMin.getHigh5()+" || "+qMaxMin.getLow5()
							+" || "+maxPips+" "+minPips
							);*/
				}else if (
						actualBarCO<0 && 
						diffDO<=-minDO1 && diffDO>=-minDO2 
						//&& actualBarR>=minVol
						){//short
					int maxPips = q1.getOpen5()-qMaxMin.getLow5();
					int minPips = qMaxMin.getHigh5()-q1.getOpen5();
					int diff = maxPips-minPips;
					sumDiff+=(diff*0.1);
					if (diff>=0) wins++;
					total++;
				}
			}
			
			if (max==-1 || q.getHigh5()>max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<min) min = q.getLow5();
		}
		
		double avgDiff = sumDiff*1.0/total;
		double perWin = wins*100.0/total;
		System.out.println(
				h1+" "+h2
				//+" "+PrintUtils.Print2dec(minBarVol, false)
				//+" "+period
				+" "+boxLen
				+" "+minDO1
				+" "+minDO2
				+" "+minBarLen
				+" || "
				+total
				+" "+PrintUtils.Print2dec(avgDiff, false)
				+" "+PrintUtils.Print2dec(perWin, false)
				);
	}
	
	public static void testVolatilityBars(ArrayList<QuoteShort> data,double minBarVol,
			int period,int boxLen,
			int minDO1,int minDO2,
			int h1,int h2){
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int actualRange = 100;
		int lastDay = -1;
		int max = -1;
		int min = -1;
		double sumDiff=0;
		int total = 0;
		int wins = 0;
		int dailyOpen = -1;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (max>=0 && min>=0){
					int range = max-min;
					dailyRanges.add(range);
					actualRange = range;
				}
				max = -1;
				min = -1;
				lastDay = day;
				dailyOpen = q.getOpen5();
			}
			
			double avgRange = MathUtils.average(dailyRanges, dailyRanges.size()-period, dailyRanges.size()-1);
			double minVol = minBarVol*avgRange;
			QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, i+1, i+boxLen);
			int actualBarCO = q.getClose5()-q.getOpen5();
			int actualBarR = Math.abs(q.getHigh5()-q.getLow5());
			int diffDO = q.getClose5()-dailyOpen;
			if (h>=h1 && h<=h2){
				if (actualBarCO>=0 && diffDO>=minDO1 && diffDO<=minDO2
						&& actualBarR>=minVol){//bull
					int maxPips = qMaxMin.getHigh5()-q1.getOpen5();
					int minPips = q1.getOpen5()-qMaxMin.getLow5();
					int diff = maxPips-minPips;
					sumDiff+=(diff*0.1);
					if (diff>=0) wins++;
					total++;
				}else if (actualBarCO<0 && diffDO<=-minDO1 && diffDO>=-minDO2 
						&& actualBarR>=minVol){//short
					int maxPips = q1.getOpen5()-qMaxMin.getLow5();
					int minPips = qMaxMin.getHigh5()-q1.getOpen5();
					int diff = maxPips-minPips;
					sumDiff+=(diff*0.1);
					if (diff>=0) wins++;
					total++;
				}
			}
			
			if (max==-1 || q.getHigh5()>max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<min) min = q.getLow5();
		}
		
		double avgDiff = sumDiff*1.0/total;
		double perWin = wins*100.0/total;
		System.out.println(
				h1+" "+h2
				+" "+PrintUtils.Print2dec(minBarVol, false)
				+" "+period
				+" "+boxLen
				+" "+minDO1
				+" "+minDO2
				+" || "
				+total
				+" "+PrintUtils.Print2dec(avgDiff, false)
				+" "+PrintUtils.Print2dec(perWin, false)
				);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String path5m0   = "c:\\fxdata\\eurusd_UTC_5 Mins_Bid_2008.12.31_2015.06.04.csv";
		//String path5m0   = "c:\\fxdata\\gbpUSD_UTC_5 Mins_Bid_2003.12.31_2015.06.05.csv";
		//String path5m0   = "c:\\fxdata\\gbpUSD_UTC_5 Mins_Bid_2012.12.31_2015.06.04.csv";
		//String path5m0   = "c:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2010.12.31_2012.12.30.csv";
		//String path5m0   = "c:\\fxdata\\eurusd_UTC_5 Mins_Bid_2009.12.31_2015.06.14.csv";
		//String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2011.12.31_2015.06.14.csv";
		//String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2008.12.30.csv";
		//String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.12.31_2015.06.14.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(path5m0);
		for (int i=0;i<=0;i+=1){
			String provider ="";
			Sizeof.runGC ();
			ArrayList<Quote> dataI 		= null;
			ArrayList<Quote> dataS 		= null;
			String path5m = paths.get(i);
			System.out.println(path5m);
			if (path5m.contains("pepper")){
				dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				provider="pepper";
			}else{
				dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="dukasc";
			}								
	  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 			  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			
			for (int period=5;period<=5;period++){
				for (int minDO1=0;minDO1<=0;minDO1+=100){
					int minDO2= minDO1+999999;
					for (int minBarLen=500;minBarLen<=500;minBarLen+=10){
						for (double minBarVol=0.25;minBarVol<=0.25;minBarVol+=0.01){
							for (int boxLen=(int) (1*288);boxLen<=10*288;boxLen+=288){
								for (int h1=16;h1<=16;h1++){
									int h2 =h1+23;
									//TestDirectionalVolatility.testVolatilityBars(data, minBarVol, period, boxLen, minDO1,minDO2, h1, h2);
									TestDirectionalVolatility.testDOHours(data,boxLen,minDO1, minDO2,minBarLen,h1,h2);
								}
							}
						}
					}
				}
			}
		}
	}

}
