package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.zznbrum.TrendClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Momentum2 {
	
	public static void test(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int momentumThr,
			int pips,
			int backBars,
			int maxBars
			){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		int total = 0;
		int accDiff = 0;
		int wins20 = 0;
		for (int i=backBars;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (h<h1 || h>h2) continue;
			
			int maxMin = maxMins.get(i-1);

			if (day!=lastDay){
				
			}
			
			TradingUtils.getMaxMinShort(data, qm, calqm, i-backBars, i-1);
			
			int diffUp	= q.getOpen5()-qm.getLow5();
			int diffDown = qm.getHigh5()-q.getOpen5();
			
			
			
			if (maxMin>=thr 					
					&& diffUp>=momentumThr
					){
				int entry = q.getOpen5();
				int valueTP = q.getOpen5()+pips; 
				int valueSL = q.getOpen5()-999999;
				
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, valueTP, valueSL, false);
				
				
				if (qm.getOpen5()==1){
					wins++;
				}
				
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, q.getOpen5()+momentumThr, q.getOpen5()-momentumThr, false);
				if (qm.getClose5()>q.getOpen5()){
					wins20++;
				}
				
				total++;
			}else if (
					maxMin<=-thr
					&& diffDown>=momentumThr
					
					){
				int entry = q.getOpen5();
				int valueTP = q.getOpen5()-pips; 
				int valueSL = q.getOpen5()+999999;
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, valueTP, valueSL, false);
				
				if (qm.getOpen5()==1){
					wins++;
				}
				
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, q.getOpen5()-momentumThr, q.getOpen5()+momentumThr, false);
				if (qm.getClose5()<q.getOpen5()){
					wins20++;
				}
				
				total++;
			}
		}
		
		System.out.println(				
				y1+" "+y2+" "+thr+" "+momentumThr+" "+pips+" "+backBars+" "+maxBars
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(wins*100.0/total, false)				
				+" || "
				+" "+PrintUtils.Print2dec(wins20*100.0/total, false)	
				);
	}
	
	public static void test2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int momentumThr,
			int pips,
			int backBars,
			int maxBars
			){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		int total = 0;
		int accDiff = 0;
		int wins20 = 0;
		for (int i=backBars;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (h<h1 || h>h2) continue;
			
			int maxMin = maxMins.get(i-1);

			if (day!=lastDay){
				
			}
			
			TradingUtils.getMaxMinShort(data, qm, calqm, i-backBars, i-1);
			
			int diffUp	= q.getOpen5()-qm.getLow5();
			int diffDown = qm.getHigh5()-q.getOpen5();
			
			
			
			if (maxMin>=thr 					
					&& diffUp>=momentumThr
					){
				int entry = q.getOpen5();
				int valueTP = q.getOpen5()+pips; 
				int valueSL = q.getOpen5()-999999;
				
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, valueTP, valueSL, false);
				
				
				if (qm.getOpen5()==1){
					wins++;
				}
				
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, q.getOpen5()+momentumThr, q.getOpen5()-momentumThr, false);
				if (qm.getClose5()>q.getOpen5()){
					wins20++;
				}
				
				total++;
			}else if (
					maxMin<=-thr
					&& diffDown>=momentumThr
					
					){
				int entry = q.getOpen5();
				int valueTP = q.getOpen5()-pips; 
				int valueSL = q.getOpen5()+999999;
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, valueTP, valueSL, false);
				
				if (qm.getOpen5()==1){
					wins++;
				}
				
				TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+maxBars, entry, q.getOpen5()-momentumThr, q.getOpen5()+momentumThr, false);
				if (qm.getClose5()<q.getOpen5()){
					wins20++;
				}
				
				total++;
			}
		}
		
		System.out.println(				
				y1+" "+y2+" "+thr+" "+momentumThr+" "+pips+" "+backBars+" "+maxBars
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(wins*100.0/total, false)				
				+" || "
				+" "+PrintUtils.Print2dec(wins20*100.0/total, false)	
				);
	}

	public static void main(String[] args) throws Exception {
		
		
		String path0 ="C:\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
		//String pathEURUSD = path0+"eurusd_UTC_15 Mins_Bid_2003.12.31_2017.11.25.csv";
		String pathEURUSD = path0+"gbpusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_1 Min_Bid_2013.12.31_2017.11.22.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		FFNewsClass.readNews(pathNews,news,0);
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			dataNoise = data;
			
			String header = "";
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataNoise);
			ArrayList<Double> trendsIndex = new ArrayList<Double>();
			int size = 200;
			ArrayList<TrendClass> trends = TradingUtils.calculateTrendsHL(dataNoise, size,trendsIndex);
			
			int count = 0;
			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			double acc1 = 0;
			int countbad3 = 0;
			Calendar cal = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			for (int j=10;j<trends.size();j++){
				TrendClass t = trends.get(j);
				TrendClass t1 = trends.get(j-1);
				TrendClass t2 = trends.get(j-2);
				TrendClass t3 = trends.get(j-3);
				TrendClass t4 = trends.get(j-4);
				TrendClass t5 = trends.get(j-5);
				TrendClass t6 = trends.get(j-6);
				TrendClass t7 = trends.get(j-7);
				
				double f=Math.abs(t.getSize())*1.0/size;
				double f1=Math.abs(t1.getSize())*1.0/size;
				double f2=Math.abs(t2.getSize())*1.0/size;
				double f3=Math.abs(t3.getSize())*1.0/size;
				double f4=Math.abs(t4.getSize())*1.0/size;
				double f5=Math.abs(t5.getSize())*1.0/size;
				double f6=Math.abs(t6.getSize())*1.0/size;
				double f7=Math.abs(t7.getSize())*1.0/size;
				if (f<1.0) continue;
					if (f<=3.99){
						count1++;
						acc1+=Math.abs(f);
					}
					if (f<=2.99){
						count2++;
					}
					if (f<=3.99){
						count3++;
					}
					if (f<=4.99){
						count4++;
					}
				
					
				if (Math.abs(f)>=5.0
						&& Math.abs(f1)>=5.0
						&& Math.abs(f2)>=5.0
						//&& Math.abs(f4)>=4.0
						//&& Math.abs(f5)>=4.0
						//&& Math.abs(f6)>=4.0
						//&& Math.abs(f7)>=3.0
						){
					cal.setTimeInMillis(t2.getMillisIndex1());
					cal2.setTimeInMillis(t1.getMillisIndex1());
					countbad3++;
					System.out.println(DateUtils.datePrint(cal)+" "+DateUtils.datePrint(cal2)
							+" || "+t2.getSize()+" "+t1.getSize()+" "+t.getSize());
				}
				
				count++;
			}
			
			System.out.println(
					countbad3+" ||| "+
					PrintUtils.Print2dec(count1*100.0/count, false)
					+" "+PrintUtils.Print2dec(acc1*1.0/count1, false)
					+" || "+PrintUtils.Print2dec(count2*100.0/count, false)
					+" "+PrintUtils.Print2dec(count3*100.0/count, false)
					+" "+PrintUtils.Print2dec(count4*100.0/count, false)
					);
			
		}

	}

}
