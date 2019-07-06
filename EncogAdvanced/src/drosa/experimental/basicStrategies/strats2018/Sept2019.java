package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Sept2019 {
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int period,
			double per,
			int maxBarTries,
			int minDistance
			){
		//
		
		
		int lastDay = -1;
		int lastDayPips = 0;
		int dayPips = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalDays = 0;
		int totalL = 0;
		int totalLL = 0;
		int totalW = 0;
		int totalWL = 0;
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		double ma0 = -1;
		double std0 = -1;
		ArrayList<Integer> days = new ArrayList<Integer>();
		int isTrade=0;
		int lastDayTrade = -1;
		int countDays = 0;
		int high = -1;
		int low = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		ArrayList<Double> speeds = new ArrayList<Double>();
		
		double range = 1000.0;
		int point = -1;
		int barTries = 0;
		int dayIndex = 0;
		int mode = 0;
		int doValue = -1;
		int acc10 = 0;
		int total10=0;
		int comm = 10;
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				
				int actualRange = high-low;
				
				range = MathUtils.average(ranges, ranges.size()-period, ranges.size()-1);
				
				int diff = (int) (actualRange-range);
				
				if (ranges.size()>=period){
					//diffs.add(diff);
					/*System.out.println(
							actualRange +" "+PrintUtils.Print2dec(range, false)+" || "+diff
							);*/
										
					//if (barTries<=maxBarTries){
					if (barTries>=0){
					//if (barTries==maxBarTries){
						if (mode==1){
							diff = -q.getOpen5()+point;
							diffs.add(diff);
							//System.out.println(q.getOpen5()+" "+point+" "+diff);
							
							diff = (barTries+1)*(diff-comm);
							
							if (diff>=0){
								wins++;	
								winPips +=diff;
							}else{
								lostPips += -diff;
							}
							//System.out.println(barTries+" || "+q.getOpen5()+" "+point+" || "+diff+" || "+winPips+" "+lostPips);
							if (barTries==maxBarTries){
								acc10+=diff;
								total10++;
							}
						}else if (mode==-1){
							diff = q.getOpen5()-point;
							diffs.add(diff);
							
							diff = (barTries+1)*(diff-comm);
							
							if (diff>=0){
								wins++;	
								winPips +=diff;
							}else{
								lostPips += -diff;
							}
							//System.out.println(barTries+" || "+q.getOpen5()+" "+point+" || "+diff);
							//System.out.println(barTries+" || "+q.getOpen5()+" "+point+" || "+diff+" || "+winPips+" "+lostPips);
							if (barTries==maxBarTries){
								acc10+=diff;
								total10++;
							}
						}
					}
				}
				ranges.add(actualRange);
				lastDay = day;
				totalDays++;
				high = -1;
				low = -1;
				point = -1;
				barTries = 0;
				dayIndex = 0;
				doValue = q.getOpen5();
				mode = 0;
			}
		
			dayIndex++;
			
			if (h>=h1 && h<=h2
					&& barTries<maxBarTries
					){
				int actualRange = high-low;
				if (high!=-1 && q.getHigh5()>=high 
						&& mode>=0
						//&& actualRange >= range*per
						&& maxMins.get(i)>=period
						&& q.getHigh5()-doValue>=minDistance
						){
					//point = q.getHigh5();
					if (barTries==0){
						point = q.getClose5();
						//System.out.println("[ENTRY LONG] "+barTries+" || "+q.getClose5()+" "+point);
						barTries = 1;
					}else{
						point = ((barTries)*point + (barTries)*q.getClose5())/(2*(barTries));
						//System.out.println("[ENTRY LONG] "+barTries+" || "+q.getClose5()+" "+point);
						barTries += (barTries);
					}
					mode = 1;
				}else if (low!=-1 && q.getLow5()<=low
						//&& actualRange >= range*per
						&& maxMins.get(i)<=-period
						&& mode<=0
						&& -q.getLow5()+doValue>=minDistance
						){
					//point = q.getLow5();
					if (barTries==0){
						point = q.getClose5();
						barTries = 1;
					}else{
						point = ((barTries)*point + (barTries)*q.getClose5())/(2*(barTries));

						barTries += (barTries);
					}
					mode = -1;
				}				
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
		}
		
		double avg = MathUtils.average(diffs);
		double std = Math.sqrt(MathUtils.variance(diffs));
		double perWin = wins*100.0/diffs.size();
		
		double avg2 = MathUtils.average(speeds);
		double pf = winPips*1.0/lostPips;
		double pfr = lostPips*1.0/winPips;
		System.out.println(
				period+" "+PrintUtils.Print2dec(per, false)
				+" "+maxBarTries
				+" "+y1+" "+y2+" "+h1+" "+h2
				+" || "
				+" "+diffs.size()+" "+totalDays
				+" "+PrintUtils.Print2dec(avg*0.1, false)
				+" "+PrintUtils.Print2dec(perWin, false)
				+" || "+PrintUtils.Print2dec(pf, false)+" "+PrintUtils.Print2dec(pfr, false)
				+" || "+(winPips-lostPips)+" "+acc10
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_5 Mins_Bid_2004.01.01_2018.12.10.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_1 Min_Bid_2009.01.01_2018.09.18.csv";
		
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
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			
			data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+9;
				for (int h1=2;h1<=2;h1++){
					int h2 = h1+0;
					for (int period=0;period<=400;period+=25){
						for (double per=0.0;per<=0.0;per+=0.05){
							for (int barTries = 40;barTries<=40;barTries++){
								for (int minDistance = 0;minDistance<=000;minDistance+=50)
									Sept2019.doTest("", data, maxMins, y1, y2, 0, 11, h1, h2, period,per,barTries,minDistance);
							}
								
						}
					}
				}
			}
			
		}

	}

}
