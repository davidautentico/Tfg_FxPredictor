package drosa.phil.ranges;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestRanges2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path   = "c:\\fxdata";
		String file1H = path+"\\"+"EURUSD_Hourly_Bid_2003.05.04_2014.02.07.csv";
		String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2003.05.04_2014.01.07.csv";
				
		ArrayList<Quote> dataI 		 = DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		 = TestLines.calculateCalendarAdjusted(dataI);
		ArrayList<Quote> data5m 	 = TradingUtils.cleanWeekendData(dataS);
		ArrayList<Quote> dailyData   = ConvertLib.createDailyData(data5m);
		//ArrayList<Quote> dailyData   = ConvertLib.convert(data5m, 12);
		System.out.println("5m daily: "+data5m.size()+" "+dailyData.size());
		
		int offset = 400;
		int mode = 2;
		int year = 2005;
		int day1 = Calendar.MONDAY+0;
		int day2 = Calendar.MONDAY+4;
		int h1=16;
		int h2=18;
		
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		from.set(year, 0, 1);
		to.set(year, 11, 31);
		
		
		ArrayList<ExtremePoint> extremes = TestRanges.calculateDailyExtremes(data5m, dailyData);
		for (h1=0;h1<=0;h1++){
			h2=h1+23;
			ArrayList<Overlap> overlaps1 = null;
			for (year=2013;year<=2013;year++){
				//from.set(year, 0, 1);
				//to.set(year, 11, 31);
				from.set(2005, 0, 1);
				to.set(2013, 11, 31);
				for (day1=Calendar.MONDAY+1;day1<=Calendar.MONDAY+1;day1+=1){
					day2=day1;
					//1dia=  288
					//2dias= 576
					for (offset=350;offset<=350;offset+=10){
					//for (offset=288;offset<=288;offset+=10){
						String header = "year= "+year+" day1= "+day1;
						overlaps1 = TestRanges.calculateOverlap(header,data5m, dailyData, extremes, from, to, day1, day2,h1,h2, offset, mode);
						/*ArrayList<Double> overlaps2 = TestRanges.calculateOverlap(header,data5m, dailyData, extremes, from, to, day1+1, day2+1, offset, mode);
						ArrayList<Double> overlaps3 = TestRanges.calculateOverlap(header,data5m, dailyData, extremes, from, to, day1+2,day2+2, offset, mode);
						ArrayList<Double> overlaps4 = TestRanges.calculateOverlap(header,data5m, dailyData, extremes, from, to, day1+3, day2+3, offset, mode);
						ArrayList<Double> overlaps5 = TestRanges.calculateOverlap(header,data5m, dailyData, extremes, from, to, day1+4, day2+4, offset, mode);
						System.out.println(offset
								+" "+PrintUtils.Print2dec(mean1, true)
								+" "+PrintUtils.Print2dec(mean2, true)
								+" "+PrintUtils.Print2dec(mean3, true)
								+" "+PrintUtils.Print2dec(mean4, true)
								+" "+PrintUtils.Print2dec(mean5, true)
								);*/
						//studyOverlaps(overlaps1,4,90);
						//MathUtils.summary2("offset= "+offset, overlaps1);
					}
				}
				
				double percent = 60;
				for (percent=40;percent<=40;percent+=5){
					for (int i=1;i<=1;i++){
						String header = "year= "+year;
						header+=" nDays="+i+" <="+PrintUtils.Print2dec(percent, false);
						studyOverlaps(header,overlaps1,i,percent,true);
					}
				}
			}
			
		}
	}

	private static void studyOverlaps(String header, ArrayList<Overlap> overlaps,int lookback,double per,boolean print) {
		// TODO Auto-generated method stub
		int total=0;
		int fails=0;
		for (int i=0;i<overlaps.size();i++){
			//System.out.println(PrintUtils.Print2dec(overlaps.get(i),false));
			if (i>=lookback-1){
				if (overlapLess(overlaps,i-lookback+1,i,per)){
					//System.out.println(lookback+"<="+PrintUtils.Print2dec(per, false));
					if (print){
						Overlap oi = overlaps.get(i-lookback+1);
						System.out.println(">>"+oi.toString());
					}
					fails++;
				}	
				total++;
			}
		}
		System.out.println(header+" Fails: "+fails+"/"+total+" "+PrintUtils.Print2dec(fails*100.0/total, false)+'%');
	}

	private static boolean overlapLess(ArrayList<Overlap> overlaps, int begin,
			int end, double per) {
		// TODO Auto-generated method stub
		//System.out.println("begin end: "+begin+" "+end);
		for (int i=begin;i<=end;i++){
			double over = overlaps.get(i).getValue();
			if (over>per) return false;
		}
		return true;
	}

}
