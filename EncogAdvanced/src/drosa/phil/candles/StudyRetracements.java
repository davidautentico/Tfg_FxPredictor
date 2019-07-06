package drosa.phil.candles;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyRetracements {
	
	public static void testWicksProfitability(String header, ArrayList<Quote> dailyData,ArrayList<Quote> data,Calendar from, Calendar to,
			int minDiff1,int minDiff2,int indexQuote,int sl,int tp,boolean modeUp){
		
		int totalWins 	= 0;
		int totalLosses = 0;
		Calendar actualCal = Calendar.getInstance();
		for (int i=1;i<dailyData.size();i++){ //para cada dia
			Quote q = dailyData.get(i);
			Quote q1 = dailyData.get(i-1); //dia anterior
			actualCal.setTime(q1.getDate()); //dia anterior
			int h   = actualCal.get(Calendar.HOUR_OF_DAY);
			int day = actualCal.get(Calendar.DAY_OF_YEAR);
			
			if (from.getTimeInMillis()>actualCal.getTimeInMillis()) continue;
			if (to.getTimeInMillis()<actualCal.getTimeInMillis()) break;
			
			int diffCO = TradingUtils.getPipsDiff(q1.getClose(), q1.getOpen());
			int diffHC = TradingUtils.getPipsDiff(q1.getHigh(), q1.getClose());
			
			if (modeUp && (diffCO<0 || diffHC<minDiff1 || diffHC>minDiff2)) continue;
			
			actualCal.setTime(q.getDate()); //dia anterior
			ArrayList<Quote> dayQuotes = TradingUtils.getDayData(data, actualCal);
			double beginValue = dayQuotes.get(indexQuote).getOpen();
			double stopLoss   = beginValue+0.0001*sl;
			double takeProfit = beginValue-0.0001*tp;
			
			
			PriceTestResult ptr = TradingUtils.testPriceMovement(dayQuotes, indexQuote, dayQuotes.size()-1, beginValue, stopLoss, takeProfit, 0);
			
			if (ptr.isWin()) totalWins++;
			else totalLosses++;			
		}
		double perWin = totalWins*100.0/(totalWins+totalLosses);
		double perLoss = 100.0-perWin;
		double me = (perWin*tp-perLoss*sl)/100.0;
		int total = totalWins+totalLosses;
		System.out.println(header+" total= "+total+" win % "+PrintUtils.Print2dec(perWin, false)
				+" "+PrintUtils.Print2dec(me, false)
				);
	}
	
	public static void testHoursUpDownOverlap(String header, ArrayList<Quote> dailyData,ArrayList<Quote> data,Calendar from, Calendar to,
			double minRange,double minPer1,double minPer2,double diffPer,double diffHLPer,int h1,int h2, boolean modeUp){
		
		
		ArrayList<Integer> hours = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hours.add(0);
		int totalUps=0;
		int totalDowns=0;
		int totalAboveHighs=0;
		int total=0;
		double avg = 0;
		Calendar actualCal = Calendar.getInstance();
		for (int i=1;i<dailyData.size();i++){ //para cada dia
			Quote q = dailyData.get(i);
			Quote q1 = dailyData.get(i-1); //dia anterior
			actualCal.setTime(q1.getDate()); //dia anterior
			int h   = actualCal.get(Calendar.HOUR_OF_DAY);
			int day = actualCal.get(Calendar.DAY_OF_YEAR);
			//System.out.println("dates: "+DateUtils.datePrint(from)+" "+DateUtils.datePrint(to)
			//		+" "+DateUtils.datePrint(actualCal));
			if (from.getTimeInMillis()>actualCal.getTimeInMillis()){
				continue;
			}
			if (to.getTimeInMillis()<actualCal.getTimeInMillis()){
				System.out.println("break");
				break;
			}
			
			actualCal.setTime(q.getDate()); //dia anterior
			ArrayList<Quote> dayQuotes = TradingUtils.getDayData(data, actualCal);
			//System.out.println("quotes: "+DateUtils.datePrint(actualCal)+" "+dayQuotes.size());	
			//calculos dia anterior
			int diffHL = TradingUtils.getPipsDiff(q1.getHigh(), q1.getLow());
			int diffCO = TradingUtils.getPipsDiff(q1.getClose(), q1.getOpen());
			int diffHC = TradingUtils.getPipsDiff(q1.getHigh(), q1.getClose());
			int diffLC = TradingUtils.getPipsDiff(q1.getClose(), q1.getLow());	
			int diffCOq = TradingUtils.getPipsDiff(q.getClose(), q.getOpen());
			double minPerQ1 = diffHC*100.0/diffHL;
			
			if (modeUp){
				if (diffCO<0){
					continue;
				}
				if (minPerQ1<minPer1){
					continue; 
				}
				if (minPerQ1>minPer2){
					continue;
				}
				if (diffHL<minRange){
					continue;
				}
			}
		
			//System.out.println("pasamos : "+dayQuotes.size());
			
			Calendar calj = Calendar.getInstance();
			for (int j=0;j<dayQuotes.size();j++){
				Quote qj = dayQuotes.get(j);
				calj.setTime(qj.getDate()); //dia anterior
				int hj   = calj.get(Calendar.HOUR_OF_DAY);
				if (modeUp){
					if (qj.getHigh()>=q1.getHigh()){
						int countH = hours.get(hj);
						hours.set(hj, countH+1);
						totalAboveHighs++;
						if (hj>=h1 && hj<=h2){
							if (diffCOq>0) totalUps++;
							if (diffCOq<0) totalDowns++;
						}
						break;
					}					
				}				
			}
			total++;
			
		}//for
		double perHigh       = totalAboveHighs*100.0/total;
		double perUp = totalUps*100.0/(totalUps+totalDowns);
		String hoursStr =""; 
		int count = 0;
		for (int i=0;i<hours.size();i++) count+=hours.get(i);
		for (int i=0;i<hours.size();i++){
			double per = hours.get(i)*100.0/count;
			hoursStr += i+"= "+PrintUtils.Print2dec(per, false)+" ";
		}
		System.out.println(header +" totaldays= "+total+" %>high= "+PrintUtils.Print2dec(perHigh, false)
				+" "+PrintUtils.Print2dec(perUp, false)
				+" "+hoursStr);
	}
	
	public static void testHoursOverlap(String header, ArrayList<Quote> dailyData,ArrayList<Quote> data,Calendar from, Calendar to,
			double minRange,double minPer1,double minPer2,double diffPer,double diffHLPer, boolean modeUp){
		
		
		ArrayList<Integer> hours = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hours.add(0);
		
		int totalAboveHighs=0;
		int total=0;
		double avg = 0;
		Calendar actualCal = Calendar.getInstance();
		for (int i=1;i<dailyData.size();i++){ //para cada dia
			Quote q = dailyData.get(i);
			Quote q1 = dailyData.get(i-1); //dia anterior
			actualCal.setTime(q1.getDate()); //dia anterior
			int h   = actualCal.get(Calendar.HOUR_OF_DAY);
			int day = actualCal.get(Calendar.DAY_OF_YEAR);
			//System.out.println("dates: "+DateUtils.datePrint(from)+" "+DateUtils.datePrint(to)
			//		+" "+DateUtils.datePrint(actualCal));
			if (from.getTimeInMillis()>actualCal.getTimeInMillis()){
				continue;
			}
			if (to.getTimeInMillis()<actualCal.getTimeInMillis()){
				System.out.println("break");
				break;
			}
			
			actualCal.setTime(q.getDate()); //dia anterior
			ArrayList<Quote> dayQuotes = TradingUtils.getDayData(data, actualCal);
			//System.out.println("quotes: "+DateUtils.datePrint(actualCal)+" "+dayQuotes.size());	
			//calculos dia anterior
			int diffHL = TradingUtils.getPipsDiff(q1.getHigh(), q1.getLow());
			int diffCO = TradingUtils.getPipsDiff(q1.getClose(), q1.getOpen());
			int diffHC = TradingUtils.getPipsDiff(q1.getHigh(), q1.getClose());
			int diffLC = TradingUtils.getPipsDiff(q1.getClose(), q1.getLow());			
			double minPerQ1 = diffHC*100.0/diffHL;
			
			if (modeUp){
				if (diffCO<0){
					continue;
				}
				if (minPerQ1<minPer1){
					continue; 
				}
				if (minPerQ1>minPer2){
					continue;
				}
				if (diffHL<minRange){
					continue;
				}
			}
		
			//System.out.println("pasamos : "+dayQuotes.size());
			
			Calendar calj = Calendar.getInstance();
			for (int j=0;j<dayQuotes.size();j++){
				Quote qj = dayQuotes.get(j);
				calj.setTime(qj.getDate()); //dia anterior
				int hj   = calj.get(Calendar.HOUR_OF_DAY);
				if (modeUp){
					if (qj.getHigh()>=q1.getHigh()){
						int countH = hours.get(hj);
						hours.set(hj, countH+1);
						totalAboveHighs++;
						break;
					}					
				}				
			}
			total++;
			
		}//for
		double perHigh       = totalAboveHighs*100.0/total;
		String hoursStr =""; 
		int count = 0;
		for (int i=0;i<hours.size();i++) count+=hours.get(i);
		for (int i=0;i<hours.size();i++){
			double per = hours.get(i)*100.0/count;
			hoursStr += i+"= "+PrintUtils.Print2dec(per, false)+" ";
		}
		System.out.println(header +" totaldays= "+total+" %>high= "+PrintUtils.Print2dec(perHigh, false)+" "+hoursStr);
	}
	
	public static void testOverlap(String header, ArrayList<Quote> dailyData,Calendar from, Calendar to, double minPer1,double minPer2,double diffPer,double diffHLPer, boolean modeUp){
		
		int count10=0;int count20=0;int count30=0;int count40=0;int count50=0;
		int count60=0;int count70=0;int count80=0;int count90=0;int count100=0;
		
		int totalAboveHighs=0;
		int total=0;
		double avg = 0;
		Calendar actualCal = Calendar.getInstance();
		for (int i=1;i<dailyData.size();i++){
			Quote q = dailyData.get(i);
			Quote q1 = dailyData.get(i-1);
			actualCal.setTime(q.getDate());
			if (from.getTimeInMillis()>actualCal.getTimeInMillis()) continue;
			if (to.getTimeInMillis()<actualCal.getTimeInMillis()) break;
			
			int diffHL = TradingUtils.getPipsDiff(q1.getHigh(), q1.getLow());
			int diffCO = TradingUtils.getPipsDiff(q1.getClose(), q1.getOpen());
			int diffHC = TradingUtils.getPipsDiff(q1.getHigh(), q1.getClose());
			int diffLC = TradingUtils.getPipsDiff(q1.getClose(), q1.getLow());
			
			//if (diffHL<=70 || diffHL>=80) continue;
			double minPerQ1 = diffHC*100.0/diffHL;
			
			if (modeUp){
				boolean aboveHigh = false;
				
				if (diffCO>0 && minPerQ1>=minPer1 && minPerQ1<=minPer2){ //si es barra up y hay una diferencia entre H-C en porcentaje
					if (q.getHigh()>q1.getHigh()){
						aboveHigh = true;
						totalAboveHighs++;
					}
					total++;
					int diff = TradingUtils.getPipsDiff(q.getHigh(), q1.getClose());
					double per = diff*100.0/diffHC;
					if (per>=100.0) per = 100.0;
					
					if (per>=10.0) count10++;
					if (per>=20.0) count20++;
					if (per>=30.0) count30++;
					if (per>=40.0) count40++;
					if (per>=50.0) count50++;
					if (per>=60.0) count60++;
					if (per>=70.0) count70++;
					if (per>=80.0) count80++;
					if (per>=90.0) count90++;
					avg+=per;
					//System.out.println(per+" "+avg/total);
					//System.out.println("diffHC= "+diffHC+" nextDay= "+PrintUtils.Print2dec(per, false)
					//		+" aboveHigh= "+aboveHigh);
				}
			}
		}//for
		double perHigh       = totalAboveHighs*100.0/total;
		double perHighReached = avg/total;
		System.out.println(header+
				" minPer>= "+PrintUtils.Print2(minPer1)
				+" && <="+PrintUtils.Print2(minPer2)+" %>high: "+total+" "+PrintUtils.Print2dec(perHigh,false)+"%"
				+" avgHReached= "+PrintUtils.Print2dec(perHighReached,false)+"%"
				+" <=10%="+PrintUtils.Print2dec(count10*100.0/total,false)
				//+" <20%="+PrintUtils.Print2dec(count20*100.0/total,false)
				//+" <30%="+PrintUtils.Print2dec(count30*100.0/total,false)
				//+" <40%="+PrintUtils.Print2dec(count40*100.0/total,false)
				+" <=50%="+PrintUtils.Print2dec(count50*100.0/total,false)
				//+" <60%="+PrintUtils.Print2dec(count60*100.0/total,false)
				//+" <70%="+PrintUtils.Print2dec(count70*100.0/total,false)
				+" <=80%="+PrintUtils.Print2dec(count80*100.0/total,false)
				//+" <90%="+PrintUtils.Print2dec(count90*100.0/total,false)
				//+" <=100%="+PrintUtils.Print2dec(count100*100.0/total,false)
				);
	}

	
	public static int testRetraces(String header, ArrayList<Quote> data,Calendar from, Calendar to, int maxBars){
	
		int wins = 0;
		int losses=0;
		Calendar actualCal = Calendar.getInstance();
		for ( int i=0;i<data.size()-1;i++){
			Quote q = data.get(i);
			actualCal.setTime(q.getDate());
			if (from.getTimeInMillis()>actualCal.getTimeInMillis()) continue;
			if (to.getTimeInMillis()<actualCal.getTimeInMillis()) break;
			
			double highValue = q.getHigh();
			double lowValue = q.getLow();
			
			boolean highTouched = false;
			boolean lowTouched = false;
			int index=-1;
			int original = i;
			int end = i+1+maxBars;
			if (end>data.size()) end = data.size();
			for (int j=i+1;j<end;j++){
				Quote q1 = data.get(j);
				int hhDiff = TradingUtils.getPipsDiff(q1.getHigh(), highValue);
				int hlDiff = TradingUtils.getPipsDiff(highValue,q1.getLow());
				
				int llDiff = TradingUtils.getPipsDiff(lowValue,q1.getLow());
				int lhDiff = TradingUtils.getPipsDiff(q1.getHigh(),lowValue);
				
				if (!highTouched)
					if (hhDiff>=0 && hlDiff>=0) highTouched = true;
				if (!lowTouched)
					if (llDiff>=0 && lhDiff>=0) lowTouched = true;
				
				if (highTouched && lowTouched){
					index = j;
					break;
				}
				
			}
			
			if (highTouched && lowTouched) wins++;
			else losses++;
			double winPer = wins*100.0/(wins+losses);
			//System.out.println("wins: "+PrintUtils.Print2dec(winPer,false)+" "+DateUtils.datePrint(actualCal)+" "+original+" "+index);
		}
		double winPer = wins*100.0/(wins+losses);
		System.out.println(header+" maxBars= "+maxBars+" wins: "+PrintUtils.Print2dec(winPer,false)+" "+losses);
		return losses;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata";
		
		//String file5m = path+"\\"+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.03.20.csv";
		String file5m = path+"\\"+"EURUSD_UTC_5 Mins_Bid_2004.01.01_2014.03.20.csv";
		
		ArrayList<Quote> dataI 			= DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> data15m        = ConvertLib.convert(data, 3);
  		ArrayList<Quote> hourlyData      = ConvertLib.convert(data, 12);
  		ArrayList<Quote> dailyData      = ConvertLib.createDailyData(data);
  		ArrayList<Quote> weeklyData     = ConvertLib.createWeeklyData(data);
  		
  		int year = 2011;
  		Calendar from = Calendar.getInstance();
  		Calendar to = Calendar.getInstance();
  		
			
		double minPer1 = 40;
		double diffPer = 20;
		double diffHLPer = 20;		
		int sl = 30;
		int tp = 7;
		int minDiff1 = 10;
		int minDiff2 = 20;
		boolean modeUp = true;
		
		from.set(2013,0, 1);
		to.set(2014,11, 31);
		String header = "year= "+year;
		
		for (minDiff1=0;minDiff1<=100;minDiff1++){
			for (int indexQuote=5;indexQuote<=5;indexQuote++){
				minDiff2 = minDiff1+10;
				header = "year= "+year+" minDiff1= "+minDiff1+" minDiff2= "+minDiff2;
				StudyRetracements.testWicksProfitability(header,dailyData,data,from,to,minDiff1,minDiff2,indexQuote,sl,tp,modeUp);
			}
		}
		
		/*
		int h1 = 8;
		int h2 = 11;
		for (year=2014;year<=2014;year++){
			from.set(2004,0, 1);
			to.set(2014,11, 31);
			String header = "year= "+year;
			for (int minRange=0;minRange<=0;minRange+=5)
			for (minPer1=10;minPer1<=80;minPer1++){
				double minPer2 = minPer1+15.0;
				header = "year= "+year+"minRange= "+minRange+" min1= "+minPer1+" min2="+minPer2;
				//StudyRetracements.testOverlap(header,data,from,to,minPer1,minPer2, diffPer, diffHLPer, upMode);
				//StudyRetracements.testOverlap(header,data15m,from,to,minPer1,minPer2, diffPer, diffHLPer, upMode);
				//StudyRetracements.testOverlap(header,hourlyData,from,to,minPer1,minPer2, diffPer, diffHLPer, upMode);
				//StudyRetracements.testOverlap(header,dailyData,from,to,minPer1,minPer2, diffPer, diffHLPer, upMode);
				//StudyRetracements.testOverlap(header,weeklyData,from,to,minPer1,minPer2, diffPer, diffHLPer, upMode);
				//StudyRetracements.testHoursOverlap(header,dailyData,data,from,to,minRange,minPer1,minPer2, diffPer, diffHLPer, upMode);
				StudyRetracements.testHoursUpDownOverlap(header,dailyData,data,from,to,minRange,minPer1,minPer2, diffPer, diffHLPer,h1,h2, upMode);
				
			}
		}*/
		
  		/*int year = 2011;
  		int ndays = 5;
  		for (int m=0;m<=11;m++){
  			from.set(year,m, 1);
  			to.set(year, m, 31);
  			String header = "year= "+year+" month= "+m;
  			int maxBars=0;
  			for (maxBars=288*ndays;maxBars<=288*ndays;maxBars+=1440){
  				int losses = StudyRetracements.testRetraces(header,data,from,to,maxBars);
  				if (losses==0) break;
  			}
  		}*/
	}

}
