package drosa.experimental.billyt;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestContinuation {
	
	public static void doTest(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr
			){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();		
		ArrayList<QuoteShort> hoursHL= new ArrayList<QuoteShort>();		
		ArrayList<Integer> ranges= new ArrayList<Integer>();	
		
		int last4h = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualHigh = -1;
		int actualLow = -1;
		int actualHigh1 = -1;
		int actualLow1 = -1;
		int lastH = -1;
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
		QuoteShort qm1 = new QuoteShort();
		int mode = 0;
		int entry = -1;
		int count = 0;
		int acc = 0;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){	
				
				if (mode==1){
					acc += (actualHigh1-entry);//-(entry-actualLow1);
					//System.out.println(entry+" "+actualHigh1+" "+actualLow1+" || "+((actualHigh1-entry)-(entry-actualLow1)));
					count++;
				}else if (mode==-1){
					acc += (entry-actualLow1);//-(actualHigh1-entry);
					count++;
				}
				
				
				actualHigh = -1;
				actualLow = -1;
				actualHigh1 = -1;
				actualLow1 = -1;
				mode = 0;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i);
			if (mode==0){
				if (h>=h1 && h<=h2){
					if (actualHigh!=-1 
							&& maxMin>=thr
							&& q.getHigh5()>=actualHigh+0){
						entry = q.getClose5();						
						mode = 1;
					} else if (actualLow!=-1 
							&& maxMin<=-thr
							&& q.getLow5()<=actualLow-0){
						entry = q.getClose5();
						mode = -1;
					}
				}
			}
			
			
			if (mode!=0){
				if (actualHigh1==-1 || q.getHigh5()>=actualHigh1){
					actualHigh1 = q.getHigh5();
				}
				if (actualLow1==-1 || q.getLow5()<=actualLow1){
					actualLow1 = q.getLow5();
				}
			}
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow = q.getLow5();
			}
		}
		
		double avg = acc*1.0/count;
		
		System.out.println(
				y1+" "+y2+" "+h1+" "+h2
				+" || "
				+count +" "+PrintUtils.Print2dec(avg, false)
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.03.23.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2015_10_28_2015_01_04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2015_04_21.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
		//String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_1 Min_Bid_2003.05.04_2015.12.17.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2008.12.31_2017.03.23.csv";
		//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.12.15.csv";		
		//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2015.12.08.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2017.03.23.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2017.03.20.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathGBPUSD);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		paths.add(pathEURJPY);paths.add(pathGBPJPY);

		
		int total = 2;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<QuoteShort> dailyData 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			System.out.println(data.size());
			
			for (int y1=2003;y1<=2003;y1++){
				int y2 = y1+14;
				for (int h1=9;h1<=9;h1++){
					int h2 = h1;
					for (int thr=20;thr<=5000;thr+=20){
						TestContinuation.doTest("", data,maxMins, y1, y2, h1, h2,thr);
					}
				}
			}
		}

	}

}
