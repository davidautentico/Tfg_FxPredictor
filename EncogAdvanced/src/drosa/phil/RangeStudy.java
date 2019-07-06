package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

/**
 * Estudio de la cantidad de dias que se mueven en el rango del  dia anterior
 * @author drosa
 *
 */
public class RangeStudy {

	/**
	 * Devuelve el número de quotes que están dentro del rango que ayer
	 * @param data
	 * @param yesterday
	 * @return
	 */
	public static double rangePerYesterday(ArrayList<Quote> data,Quote yesterday){
		
		double per = -1;
		int count = 0;
		/*System.out.println("TODAY YESTERDAY HIGH LOW: "
				+" "+ DateUtils.datePrint(data.get(0).getDate())
				+" "+ DateUtils.datePrint(yesterday.getDate())
				+" "+ PrintUtils.Print4dec(yesterday.getHigh())
				+" "+ PrintUtils.Print4dec(yesterday.getLow())
				);*/
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			//System.out.println(DateUtils.datePrint(q.getDate()));
			/*if (yesterday.getHigh()<q.getHigh()
					|| q.getLow()<yesterday.getLow()){
				count++;	
			}*/
			if (q.getLow()>yesterday.getHigh() || q.getHigh()<yesterday.getLow())
				count++;
		}
		return 100.0-(count*100.0/data.size());
	}
	
	private static Quote getYesterday(ArrayList<Quote> data, Calendar date){
		
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			if (DateUtils.isSameDay(cal,date))
				return data.get(i-1);
		}
			return null;
	}
	
	public static double studyRangesContinuation(ArrayList<Quote> data,ArrayList<Quote> dailyData,Calendar from,Calendar to){
		
		int actualDay = -1;
		Calendar actualDate	= Calendar.getInstance();
		Calendar yesterDay	= Calendar.getInstance(); 
		ArrayList<Quote> dayQuotes = new ArrayList<Quote>();
		Quote yesterday = null;
		int count =0;
		double avg=0;
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			//System.out.println("day total "+i+" "+data.size());
			if (actualDay!=-1){
				//System.out.println("calculando..0");
				yesterday 	= getYesterday(dailyData,actualDate);
			}
			actualDate.setTime(q.getDate());
			int day = actualDate.get(Calendar.DAY_OF_YEAR);
			//System.out.prin
			if (actualDate.getTimeInMillis()>to.getTimeInMillis()) return avg/count;
			
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() 
					|| actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			if (day!=actualDay){			
				if (yesterday!=null){
					double percent 		= rangePerYesterday(dayQuotes,yesterday);
					avg+=percent;
					count++;
					/*System.out.println("Percent avg: "+DateUtils.datePrint(actualDate)
							+" "+PrintUtils.Print2dec(percent)
							+" "+PrintUtils.Print2dec(avg/count)
							);*/
				}
				dayQuotes.clear();	
				actualDay = day;
			}	
			dayQuotes.add(q);
		}
		if (count>0) return avg/count;
		return -1;
	}
	
	public static ArrayList<Double> getAvgRange(ArrayList<Quote> data,Calendar from,Calendar to,
			int dayL,int dayH){
		
		int count=0;
		double avg=0;
		ArrayList<Double> avgs = new ArrayList<Double>();
		Calendar actualDate = Calendar.getInstance();
		int count50=0;
		int count60=0;
		int count70=0;
		int count80=0;
		int count90=0;
		int count100=0;
		int count110=0;
		int count120=0;
		int count130=0;
		int count140=0;
		int count150=0;
		for (int i=0;i<data.size();i++){
			//System.out.println("dia: "+i);
			Quote q = data.get(i);
			actualDate.setTime(q.getDate());
			int day = actualDate.get(Calendar.DAY_OF_WEEK);
			int h   = actualDate.get(Calendar.HOUR_OF_DAY);
			//System.out.prin
			if (day<dayL || day>dayH){
				//System.out.println("continuamos dia: "+day+" "+DateUtils.datePrint(q.getDate()));
				continue;
			}
			if (actualDate.getTimeInMillis()>to.getTimeInMillis()){
				//System.out.println("salimos: "+avg);
				avgs.add(avg/count);
				
				avgs.add((double) count50*100.0/count);
				avgs.add((double) count60*100.0/count);
				avgs.add((double) count70*100.0/count);
				avgs.add((double) count80*100.0/count);
				avgs.add((double) count90*100.0/count);
				avgs.add((double) count100*100.0/count);
				avgs.add((double) count110*100.0/count);
				avgs.add((double) count120*100.0/count);
				avgs.add((double) count130*100.0/count);
				avgs.add((double) count140*100.0/count);
				avgs.add((double) count150*100.0/count);
				return avgs;
			}
			
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() 
					|| actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			double  actualR = TradingUtils.getPipsDiff(q.getHigh(), q.getLow());
			//System.out.println("actualR: "+actualR);
			avg += actualR;
			
			if (actualR<=50){ count50++;}
			if (actualR<=60){ count60++;}
			if (actualR<=70){ count70++;}
			if (actualR<=80){ count80++;}
			if (actualR<=90){ count90++;}
			if (actualR<=100){ count100++;}
			if (actualR<=110){ count110++;}
			if (actualR<=120){ count120++;}
			if (actualR<=130){ count130++;}
			if (actualR<=140){ count140++;}
			if (actualR<=150){ count150++;}
			
			count++;
		}
		
		if (count>0){
			avgs.add(avg/count);
			avgs.add((double) count50*100.0/count);
			avgs.add((double) count60*100.0/count);
			avgs.add((double) count70*100.0/count);
			avgs.add((double) count80*100.0/count);
			avgs.add((double) count90*100.0/count);
			avgs.add((double) count100*100.0/count);
			avgs.add((double) count110*100.0/count);
			avgs.add((double) count120*100.0/count);
			avgs.add((double) count130*100.0/count);
			avgs.add((double) count140*100.0/count);
			avgs.add((double) count150*100.0/count);
			return avgs;
		}
		return null;
	}
	
	public static  double daysDOdiff(ArrayList<Quote> dailyData,Calendar from,Calendar to,int low,int high){
		int actualDay = -1;
		Calendar actualDate	= Calendar.getInstance();
		Calendar yesterDay	= Calendar.getInstance(); 
		ArrayList<Quote> dayQuotes = new ArrayList<Quote>();
		Quote yesterday = null;
		int count =0;
		int total=0;
		double avg=0;
		
		for (int i=0;i<dailyData.size();i++){
			Quote q = dailyData.get(i);
			actualDate.setTime(q.getDate());
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() 
					|| actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			int actualRange0 = TradingUtils.getPipsDiff(q.getHigh(), q.getOpen());
			int actualRange1 = TradingUtils.getPipsDiff(q.getOpen(),q.getLow());
			if ((actualRange0>=low && actualRange0<=high)
				|| (actualRange1>=low && actualRange1<=high))
				count++;
			total++;
		}
		
		if (total>0)
			return count*100.0/total;
		else return -1;
	}
	
	public static ArrayList<Double> getOverlap2days(ArrayList<Quote> dailyData,Calendar from,Calendar to,boolean HLenabled){
		
		ArrayList<Double> overlap = new ArrayList<Double>();
		Calendar actualDate = Calendar.getInstance();
		
		int count = 0;
		double avgHL=0;
		double avgLH=0;
		int count10=0;
		int count20=0;
		int count30=0;
		int count40=0;
		int count50=0;
		int count60=0;
		int count70=0;
		int count80=0;
		int count90=0;
		int count100=0;
		int count110=0;
		int count120=0;
		int count130=0;
		int count140=0;
		int count150=0;
		for (int i=1;i<dailyData.size();i++){
			Quote q1 = dailyData.get(i-1);
			Quote q = dailyData.get(i);
			actualDate.setTime(q.getDate());
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() 
					|| actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			double HL = TradingUtils.getPipsDiff(q1.getHigh(), q.getLow());
			double LH = TradingUtils.getPipsDiff(q.getHigh(), q1.getLow());
			
			double pips = TradingUtils.getPipsDiff(q1.getClose(), q1.getOpen()); 
			//if (q1.getClose()<q1.getOpen()) continue; //bear
			if (pips<100) continue; //bear
			//System.out.println("HL: "+HL+" "+PrintUtils.Print(q1)+" "+PrintUtils.Print(q));
			count++;
			if (HLenabled){
				avgHL += HL;
				if (HL>=10){ count10++;}
				if (HL>=20){ count20++;}
				if (HL>=30){ count30++;}
				if (HL>=40){ count40++;}
				if (HL>=50){ count50++;}
				if (HL>=60){ count60++;}
				if (HL>=70){ count70++;}
				if (HL>=80){ count80++;}
				if (HL>=90){ count90++;}
				if (HL>=100){ count100++;}
				if (HL>=110){ count110++;}
				if (HL>=120){ count120++;}
				if (HL>=130){ count130++;}
				if (HL>=140){ count140++;}
				if (HL>=150){ count150++;}
			}
			
			if (!HLenabled){
				avgLH += LH;			
				if (LH>=10){ count10++;}
				if (LH>=20){ count20++;}
				if (LH>=30){ count30++;}
				if (LH>=40){ count40++;}
				if (LH>=50){ count50++;}
				if (LH>=60){ count60++;}
				if (LH>=70){ count70++;}
				if (LH>=80){ count80++;}
				if (LH>=90){ count90++;}
				if (LH>=100){ count100++;}
				if (LH>=110){ count110++;}
				if (LH>=120){ count120++;}
				if (LH>=130){ count130++;}
				if (LH>=140){ count110++;}
				if (LH>=150){ count150++;}
			}
		}
		
		if (HLenabled)
			overlap.add(avgHL/count);
		else
			overlap.add(avgLH/count);
		overlap.add(count10*100.0/count);
		overlap.add(count20*100.0/count);
		overlap.add(count30*100.0/count);
		overlap.add(count40*100.0/count);
		overlap.add(count50*100.0/count);
		overlap.add(count60*100.0/count);
		overlap.add(count70*100.0/count);
		overlap.add(count80*100.0/count);
		overlap.add(count90*100.0/count);
		overlap.add(count100*100.0/count);
		overlap.add(count110*100.0/count);
		overlap.add(count120*100.0/count);
		overlap.add(count130*100.0/count);
		overlap.add(count140*100.0/count);
		overlap.add(count150*100.0/count);
		
		return overlap;
	}
	
public static ArrayList<Double> getHourOverlapReached(ArrayList<Quote> data,
		Calendar from,Calendar to,
		int lowR,int highR,double per){
	
	ArrayList<Integer> overlapsCount = new ArrayList<Integer>();
	ArrayList<Double> overlapsH = new ArrayList<Double>();
	for (int i=0;i<=23;i++)overlapsCount.add(0);
	int count=0;
	double lastH=-999;
	double lastL=-999;
	double actualH=-999;
	double actualL=-999;
	int lastDay= -1;
	Calendar actualDate = Calendar.getInstance();
	boolean dayFinish=false;
	for (int i=0;i<data.size();i++){
		Quote q = data.get(i);
		actualDate.setTime(q.getDate());
		int h = actualDate.get(Calendar.HOUR_OF_DAY);
		int day = actualDate.get(Calendar.DAY_OF_YEAR);
		
		if (actualDate.getTimeInMillis()<from.getTimeInMillis() 
				|| actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
		
		if (day!=lastDay){
			lastH = actualH;
			lastL = actualL;
			actualH=0;
			actualL=9999;
			lastDay = day;
			dayFinish=false;
		}
		
		
		if (q.getLow()<actualL) actualL = q.getLow();
		if (q.getHigh()>actualH) actualH = q.getHigh();
		
		if (lastH>0 && lastL>0 && !dayFinish){
			int maxOverLap = RangeStudy.maxOverlap(lastH, lastL, actualH, actualL);
			int yRange = TradingUtils.getPipsDiff(lastH,lastL);
			System.out.println("lastr over: "+yRange+" "+maxOverLap);
			double overlapPer = maxOverLap*100.0/yRange;
	
			
			if (overlapPer>=per){
				dayFinish=true;//ya hemos alcanzado el objetivo
				int c = overlapsCount.get(h);
				overlapsCount.set(h, c+1);
				System.out.println("h: "+h);
				count++;
			}
			
		}
		
		
	}
	
	for (int i=0;i<=23;i++){
		int c = overlapsCount.get(i);
		overlapsH.add(c*100.0/count);
	}

	return overlapsH;
}

public static int maxOverlap(double yH,double yL,double actualH,double actualL){

	
	double HL = TradingUtils.getPipsDiff(yH, actualL);
	double LH = TradingUtils.getPipsDiff(actualH, yL);
	double highDiff = Math.abs(TradingUtils.getPipsDiff(yH, actualH));
	double lowDiff  = Math.abs(TradingUtils.getPipsDiff(yL, actualL));
	double lastDayRange = TradingUtils.getPipsDiff(yH, yL);
	double todayDayRange = TradingUtils.getPipsDiff(actualH, actualL);
	double pipsDiff = highDiff+lowDiff;
	int overlap = (int) HL;
	if (LH>overlap) overlap=(int) LH;

	overlap-=(highDiff+lowDiff);
	
	if (actualH>=yH && yL>=actualL) return (int) lastDayRange;
	if (actualH<=yH && yL<=actualL) return (int) todayDayRange;
	if (actualH<=yL) return (int) 0;
	if (actualL>=yH) return (int) 0;
	
	return overlap;
}
	
public static ArrayList<Double> getMaxOverlap2days(ArrayList<Quote> dailyData,Calendar from,Calendar to,
		int day,int lowR,int highR,boolean percent){
		
		ArrayList<Double> overlaps = new ArrayList<Double>();
		Calendar actualDate = Calendar.getInstance();
		
		int count = 0;
		double avgOverlap=0;
		int count10=0;
		int count20=0;
		int count30=0;
		int count40=0;
		int count50=0;
		int count60=0;
		int count70=0;
		int count80=0;
		int count90=0;
		int count100=0;
		int count110=0;
		int count120=0;
		int count130=0;
		int count140=0;
		int count150=0;
		for (int i=1;i<dailyData.size();i++){
			Quote q1 = dailyData.get(i-1);
			Quote q = dailyData.get(i);
			actualDate.setTime(q.getDate());
			int d = actualDate.get(Calendar.DAY_OF_WEEK);
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() 
					|| actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			double HL = TradingUtils.getPipsDiff(q1.getHigh(), q.getLow());
			double LH = TradingUtils.getPipsDiff(q.getHigh(), q1.getLow());
			double highDiff = Math.abs(TradingUtils.getPipsDiff(q1.getHigh(), q.getHigh()));
			double lowDiff  = Math.abs(TradingUtils.getPipsDiff(q1.getLow(), q.getLow()));
			
			double lastDayRange = TradingUtils.getPipsDiff(q1.getHigh(), q1.getLow());
			double todayDayRange = TradingUtils.getPipsDiff(q.getHigh(), q.getLow());
			double pipsDiff = highDiff+lowDiff;
			double overlap = HL;
			
			double OL = TradingUtils.getPipsDiff(q1.getOpen(), q1.getLow());
			double HO = TradingUtils.getPipsDiff(q1.getHigh(), q1.getOpen());
			
			if (d!=day) continue;
			
			if (lastDayRange<lowR || lastDayRange>highR) continue;
			//if (OL>15) continue; 
			//if (HO>10) continue;
			if (LH>overlap) overlap=LH;
			
			if (q.getHigh()>q1.getHigh() && q.getLow()<q1.getLow()){
				overlap = lastDayRange;
				//overlap-=(highDiff+lowDiff);
			}else if (q.getHigh()<q1.getHigh() && q.getLow()>q1.getLow()){
				overlap = todayDayRange;
				//overlap-=(highDiff+lowDiff);
			}else{
				overlap-=(highDiff+lowDiff);
			}
			
			if (!percent){
				avgOverlap += overlap;
				if (overlap>=10){ count10++;}
				if (overlap>=20){ count20++;}
				if (overlap>=30){ count30++;}
				if (overlap>=40){ count40++;}
				if (overlap>=50){ count50++;}
				if (overlap>=60){ count60++;}
				if (overlap>=70){ count70++;}
				if (overlap>=80){ count80++;}
				if (overlap>=90){ count90++;}
				if (overlap>=100){ count100++;}
				if (overlap>=110){ count110++;}
				if (overlap>=120){ count120++;}
			}else{
				double rangeRel = overlap*100.0/lastDayRange;
				avgOverlap += rangeRel;
				if (rangeRel>=10){ count10++;}
				if (rangeRel>=20){ count20++;}
				if (rangeRel>=30){ count30++;}
				if (rangeRel>=40){ count40++;}
				if (rangeRel>=50){ count50++;}
				if (rangeRel>=60){ count60++;}
				if (rangeRel>=70){ count70++;}
				if (rangeRel>=80){ count80++;}
				if (rangeRel>=90){ count90++;}
				if (rangeRel>=100){ count100++;}
				if (rangeRel>=110){ count110++;}
				if (rangeRel>=120){ count120++;}
			}
			count++;

			
						
		}
		
		//overlaps.add(avgOverlap/count);
		overlaps.add(count10*100.0/count);
		overlaps.add(count20*100.0/count);
		overlaps.add(count30*100.0/count);
		overlaps.add(count40*100.0/count);
		overlaps.add(count50*100.0/count);
		overlaps.add(count60*100.0/count);
		overlaps.add(count70*100.0/count);
		overlaps.add(count80*100.0/count);
		overlaps.add(count90*100.0/count);
		overlaps.add(count100*100.0/count);
		
		return overlaps;
	}

public static ArrayList<Double> getConsecutiveOverlap(ArrayList<Quote> dailyData,Calendar from,Calendar to,
		int lowR,int highR){
		
		ArrayList<Double> overlaps = new ArrayList<Double>();
		Calendar actualDate = Calendar.getInstance();

		for (int i=1;i<dailyData.size();i++){
			Quote q1 = dailyData.get(i-1);
			Quote q = dailyData.get(i);
			actualDate.setTime(q.getDate());
			int d = actualDate.get(Calendar.DAY_OF_WEEK);
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() 
					|| actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			double HL = TradingUtils.getPipsDiff(q1.getHigh(), q.getLow());
			double LH = TradingUtils.getPipsDiff(q.getHigh(), q1.getLow());
			double highDiff = Math.abs(TradingUtils.getPipsDiff(q1.getHigh(), q.getHigh()));
			double lowDiff  = Math.abs(TradingUtils.getPipsDiff(q1.getLow(), q.getLow()));
			
			double lastDayRange = TradingUtils.getPipsDiff(q1.getHigh(), q1.getLow());
			double todayDayRange = TradingUtils.getPipsDiff(q.getHigh(), q.getLow());
			double overlap = RangeStudy.maxOverlap(q1.getHigh(), q1.getLow(),q.getHigh(), q.getLow());
			
			int HC = TradingUtils.getPipsDiff(q1.getHigh(), q1.getClose());
			int LC = TradingUtils.getPipsDiff(q1.getClose(),q1.getLow());

			//if (HC>2 && q1.getClose()>=q1.getOpen()) continue;
			//if (LC>2 && q1.getClose()<=q1.getOpen()) continue;
			
			double rangeRel = overlap*100.0/lastDayRange;
			System.out.println("Rangerel: "+DateUtils.datePrint(q.getDate())
					+" "+PrintUtils.Print(rangeRel)
					);
			overlaps.add(rangeRel);
			
		}
		return overlaps;
	}

	public static int studyConsecutive(ArrayList<Double> overlaps,int len,double per){
		
		int consec=0;
		int total=0;
		for (int i=0;i<overlaps.size();i++){
			boolean succ=true;
			int end = i+len-1;
			if (end>=overlaps.size()-1) end = overlaps.size()-1;
			int count=0;
			for (int j=i;j<=end;j++){
				double overlap = overlaps.get(j);
				if (overlap>=per){
					succ=false;
					break;
				}
				count++;
			}
			
			if (succ && count>=len){
				total++;
			}
		}
		return total;
	}
	
	public static int studyConsecutive2(ArrayList<Double> overlaps,int len,double per){
		
		int consec=0;
		int total=0;
		for (int i=0;i<overlaps.size();i++){
			boolean succ=true;
			int end = i+len-1;
			if (end>=overlaps.size()-1) end = overlaps.size()-1;
			int count=0;
			for (int j=i;j<=end;j++){
				double overlap = overlaps.get(j);
				if (overlap<=per){
					succ=false;
					break;
				}
				count++;
			}
			
			if (succ && count>=len){
				total++;
			}
		}
		return total;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata";
		String file5m = path+"\\"+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.17.csv";
		//String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2010.12.31_2013.10.01.csv";
		//String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2005.01.01_2013.08.31.csv";
		String fileEur5m	= path+"\\"+"EURUSD_5 Mins_Bid_2005.01.01_2013.12.03.csv";
		String fileEur15m	= path+"\\"+"EURUSD_15 Mins_Bid_2005.01.01_2013.12.04.csv";
		String fileEur30m	= path+"\\"+"EURUSD_30 Mins_Bid_2005.01.01_2013.12.04.csv";	
		String fileEur1h	= path+"\\"+"EURUSD_Hourly_Bid_2005.01.01_2013.12.04.csv";
	    String fileEur1w	= path+"\\"+"EURUSD_Weekly_Bid_2004.12.27_2013.11.25.csv";
	    String fileEur1mn	= path+"\\"+"EURUSD_Monthly_Bid_2005.01.01_2013.11.01.csv";
		
	    String fileGbp5m	= path+"\\"+"GBPUSD_5 Mins_Bid_2005.01.01_2013.12.03.csv";
		String fileAud5m	= path+"\\"+"AUDUSD_5 Mins_Bid_2005.01.01_2013.12.03.csv";
		//String file5m = path+"\\"+"GBPUSD_5 Mins_Bid_2006.01.01_2013.11.06.csv";

		
		ArrayList<Quote> dataI 			= DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
  		data 		= ConvertLib.createDailyData(data);
  	
		
		int yearF      	 	= 2003;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2014;
		int monthL 			= Calendar.DECEMBER;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		Calendar from2 = Calendar.getInstance();
		Calendar to2   = Calendar.getInstance();
		from.set(yearF, 0, 1);
		to.set(yearL, 6, 31);
		to2.setTimeInMillis(from.getTimeInMillis());
		
		int d  = Calendar.MONDAY+4;
		int h1 = 0;
		int h2 = 23;
		System.out.println("data : "+data.size());
		for (int day = d;day<=d+0;day++){
			from.set(yearF, 0, 1);
			to2.setTimeInMillis(from.getTimeInMillis());
			while (to2.getTimeInMillis()<=to.getTimeInMillis()){
				from.setTimeInMillis(to2.getTimeInMillis());
				from.add(Calendar.MONTH, -4);
				String rangeStr ="";
				//ArrayList<Double> ranges = getAvgRange(data,from,to2,day,day,h1,h2);
				ArrayList<Double> ranges = getAvgRange(data,from,to2,day,day);
				for (int i=0;i<ranges.size();i++){
					double range = ranges.get(i);
					rangeStr+= " "+PrintUtils.Print(range);
				}
				System.out.println(DateUtils.getYMD(from)
						+"-"+DateUtils.getYMD(to2)
						+"-"+day
						+" "+rangeStr);
				to2.add(Calendar.MONTH, 1);
			}
		}
		
		/*int d = Calendar.MONDAY;
		System.out.println("data : "+dailyData.size());
		//for (int day = d;day<=d;day++){
			while (to2.getTimeInMillis()<=to.getTimeInMillis()){
				from.setTimeInMillis(to2.getTimeInMillis());
				from.add(Calendar.MONTH, -6);
				String rangeStr ="";
				for (int day = d;day<=d+4;day++){
					ArrayList<Double> ranges = getAvgRange(dailyData,from,to2,day,day);
					rangeStr+= " "+PrintUtils.Print(ranges.get(0));
				}
				System.out.println(DateUtils.getYMD(from)+"-"+DateUtils.getYMD(to2)
						+" "+rangeStr);
				to2.add(Calendar.MONTH, 1);
			}
		//}*/
		
		/*int day = Calendar.MONDAY;
		
			while (to2.getTimeInMillis()<=to.getTimeInMillis()){
				from.setTimeInMillis(to2.getTimeInMillis());
				from.add(Calendar.MONTH, -24);
				
				for (int d=Calendar.MONDAY;d<=Calendar.FRIDAY;d++){
					//ArrayList<Double> ranges = getMaxOverlap2days(dailyData,from,to2,true);
					ArrayList<Double> ranges = getMaxOverlap2days(data,from,to2,d,50,100,true);
					String rangeStr ="";
					for (int i=0;i<ranges.size();i++){
					//for (int i=0;i<1;i++){
						double range = ranges.get(i);
						rangeStr+= " "+PrintUtils.Print(range);
					}
					System.out.println(DateUtils.getYMD(from)
							+"-"+DateUtils.getYMD(to2)
							+" "+rangeStr);
				}
				to2.add(Calendar.MONTH, 1);
			}*/
		
		
		/*while (to2.getTimeInMillis()<=to.getTimeInMillis()){
			from.setTimeInMillis(to2.getTimeInMillis());
			from.add(Calendar.MONTH, -12);
			String rangeStr ="";
			ArrayList<Double> ranges = getMaxOverlap2days(dailyData,from,to2,true);

			//ArrayList<Double> hours = getHourOverlapReached(data,from,to2,80,120,30);
			for (int i=0;i<hours.size();i++){
			//for (int i=0;i<1;i++){
				double range = hours.get(i);
				rangeStr+= " "+PrintUtils.Print(range);
			}
			System.out.println(DateUtils.getYMD(from)
					+"-"+DateUtils.getYMD(to2)
					+" "+rangeStr);
			to2.add(Calendar.MONTH, 1);
		}*/
		

		/*String rangeStr ="";
		ArrayList<Double> overlaps=  getConsecutiveOverlap(data,from,to,30,300);
		int totalOver = overlaps.size();
		System.out.println("days all<x all>x");
		for (int i=2;i<=5;i++){
			for (int j=10;j<=100;j+=1){
				int total1 = studyConsecutive(overlaps,i,j);
				int total2 = studyConsecutive2(overlaps,i,j);
				System.out.println(i+" "+j
						+" "+PrintUtils.Print(total1*100.0/totalOver)+"%"
						+" "+PrintUtils.Print(total2*100.0/totalOver)+"%"
						+" "+total1
						+" "+total2
						);
			}
		}*/
		
		
		/*for (int i=0;i<ranges.size();i++){
		//for (int i=0;i<1;i++){
			double range = ranges.get(i);
			rangeStr+= " "+PrintUtils.Print(range);
		}
		System.out.println(DateUtils.getYMD(from)
					"-"+DateUtils.getYMD(to2)
					+" "+rangeStr);
		*/	
		
		
		System.out.println("Finished..");
	}

}
