package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.StrategyResultEx;
import drosa.experimental.SystemStats;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MomentumStudy {
	
	public static void testRangesProbPips(
			String header,
			ArrayList<QuoteShort>  data,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int d1,int d2,
			int h1,int h2,
			ArrayList<Integer> maxMins,
			int thr1,
			int thr2,
			int bars,
			int tp, 
			int sl,
			int comm,
			int debug,
			boolean print,
			StrategyResultEx stats
			){
		

		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calt = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int acc = 0;
		int count = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		int totalGreater = 0;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		double mean = 50;
		double dt=5;
		double ref0 = 45;
		double ref1 = 55;
		double ref2 = 60;
		double ref3 = 65;
		double ref4 = 70;
		double ref5 = 75;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (dayWeek<d1 || dayWeek>d2 ) continue;
			//if (h<h1 || h> h2) continue;
			
			//if (h==0 && min<15) continue;//no se puede tradear antes
			//if (h==23 && min>=55) continue;
			if (h==h1 && day!=lastDay){
				if (lastDay!=-1){
					
										
					int range = high-low;
					
					if (range>=ref0){
						totalGreater++;
					}
					
					ranges.add(range);
					
					totalDays++;
					
				}				
				high = -1;
				low = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (h>=h1 && h<=h2){
				if (maxMin>=thr1){
					System.out.println("[MODE LONG]" +maxMin+" "+DateUtils.datePrint(cal));
					//int end = i+bars;//esto parece q rula
					int end = data.size()-1; 
					if (end>=data.size()-1) end = data.size()-1;
					for (int t=i;t<=end;t++){
						
						int maxMin2 = maxMins.get(t);
						if (maxMin2<=-thr2){
							QuoteShort qt = data.get(t+1);
							QuoteShort.getCalendar(calt, qt);
							count++;
							System.out.println("[open long]" +maxMin2+" "+DateUtils.datePrint(calt));							
							int end2 = t+1+bars;
							if (end2>=data.size()-1) end2 = data.size()-1;
							//TradingUtils.getMaxMinShort(data, qm, calqm, t+1, end2);
							
							int valueTP = qt.getOpen5() + 10*tp;
							int valueSL = qt.getOpen5() - 10*sl;
							TradingUtils.getMaxMinShortTPSL(data, qm, calqm, t+1, end2, valueTP, valueSL,false);
							
							//int diff = (qm.getHigh5()-qt.getOpen5())-(qt.getOpen5()-qm.getLow5());	
							int diff = qm.getClose5()-qt.getOpen5();
							diff -=comm;
							acc +=diff; 
							if ( diff>=0){
								wins++;
								winPips += diff;
							}else{
								lostPips +=-diff;
								losses++;
							}
							if (debug==1){
								System.out.println("[LONG] "+maxMin+" "+maxMin2+" "+diff
										+" || "+q.toString()
										+" || "+qt.toString()
										+" || "+qm.getClose5()
										);
							}
							i=t;
							break;
						}
					}					
				}else if (maxMin<=-thr1){
					//int end = i+bars;//esto parece q rula
					int end = data.size()-1;
					if (end>=data.size()-1) end = data.size()-1;
					for (int t=i;t<=end;t++){
						int maxMin2 = maxMins.get(t);
						if (maxMin2>=thr2){
							count++;
							int end2 = t+1+bars;
							if (end2>=data.size()-1) end2 = data.size()-1;
							//TradingUtils.getMaxMinShort(data, qm, calqm, t+1, end2);
							
							QuoteShort qt = data.get(t+1);
							int valueTP = qt.getOpen5() - 10*tp;
							int valueSL = qt.getOpen5() + 10*sl;
							TradingUtils.getMaxMinShortTPSL(data, qm, calqm, t+1, end2, valueTP, valueSL,false);
							//int diff = (qt.getOpen5()-qm.getLow5())-(qm.getHigh5()-qt.getOpen5());		
							int diff = qt.getOpen5()-qm.getClose5();
							diff -=comm;
							acc +=diff; 
							if ( diff>=0){
								wins++;
								winPips += diff;
							}else{
								lostPips +=-diff;
								losses++;
							}
							i=t;
							break;
						}
					}				
				}
			}
		}
		
		double pf = winPips*1.0/(lostPips);
		if (print){
			System.out.println(header+" "+" || "+
					h1+" "+h2+" "+thr1+" "+thr2
					+" "+tp+" "+sl+" "+bars+" "+comm
					+" || "
					+" "+count
					+" "+PrintUtils.Print2dec(wins*100.0/count, false,3)	
					+" "+PrintUtils.Print2dec(acc*0.1/count, false,3)
					+" "+PrintUtils.Print2dec(pf, false,3)
					);
		}
		
		stats.setProfitFactor(pf);
		stats.setExpectancy(acc*0.1/count);
		stats.setTotalTrades(count);
		
		/*double winPer = wins*100.0/count;
		System.out.println(
				h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+bars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false,3)	
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false,3)
				+" "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false,3)
				);*/
	}
	
	public static void testRangesProb(
			String header,
			ArrayList<QuoteShort>  data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			ArrayList<Integer> maxMins,
			int thr1,
			int thr2,
			int bars,
			int debug
			){
		

		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int acc = 0;
		int count = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		int totalGreater = 0;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		double mean = 50;
		double dt=5;
		double ref0 = 45;
		double ref1 = 55;
		double ref2 = 60;
		double ref3 = 65;
		double ref4 = 70;
		double ref5 = 75;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			//if (h<h1 || h> h2) continue;
			
			//if (h==0 && min<15) continue;//no se puede tradear antes
			//if (h==23 && min>=55) continue;
			if (h==h1 && day!=lastDay){
				if (lastDay!=-1){
					
										
					int range = high-low;
					
					if (range>=ref0){
						totalGreater++;
					}
					
					ranges.add(range);
					
					totalDays++;
					
				}				
				high = -1;
				low = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (h>=h1 && h<=h2){
				if (maxMin>=thr1){
					count++;
					
					int end = i+bars;
					if (end>=data.size()-1) end = data.size()-1;
					for (int j=i;j<=end;j++){
						maxMin = maxMins.get(j);
						if (maxMin<=-thr2){
							wins++;
							break;
						}
					}
				}else if (maxMin<=-thr1){
					count++;
						
					int end = i+bars;
					if (end>=data.size()-1) end = data.size()-1;
					for (int j=i;j<=end;j++){
						maxMin = maxMins.get(j);
						if (maxMin>=thr2){
							wins++;
							break;
						}
					}
				}
			}
		}
		
		System.out.println(
				h1+" "+h2+" "+thr1+" "+thr2+" "+bars
				+" || "
				+" "+count+" "+PrintUtils.Print2dec(wins*100.0/count, false,3)	
				);
		
		/*double winPer = wins*100.0/count;
		System.out.println(
				h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+bars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false,3)	
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false,3)
				+" "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false,3)
				);*/
	}
	
	public static void testRanges(
			String header,
			ArrayList<QuoteShort>  data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			ArrayList<Integer> maxMins,
			int thr,
			int tp,
			int sl,			
			int atrDays,
			int debug
			){
		

		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int acc = 0;
		int count = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		double mean = 50;
		double dt=5;
		double ref = 55;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			//if (h<h1 || h> h2) continue;
			
			//if (h==0 && min<15) continue;//no se puede tradear antes
			//if (h==23 && min>=55) continue;
			if (h==h1 && day!=lastDay){
				if (lastDay!=-1){
					int range = high-low;
					ranges.add(range);
					
					totalDays++;
					
					if (ranges.size()>=atrDays){
						mean = MathUtils.average(ranges, ranges.size()-atrDays, ranges.size()-1);
						dt = Math.sqrt(MathUtils.variance(ranges, ranges.size()-atrDays, ranges.size()-1));
						ref = mean+dt;
						if (debug==1){
							double avg = MathUtils.average(ranges, ranges.size()-atrDays, ranges.size()-1);
							MathUtils.summary(
									PrintUtils.Print2dec(avg*0.1, false)
									+" "+PrintUtils.Print2dec(ref, false)
									+" || ", ranges,ranges.size()-atrDays, ranges.size()-1);
							
						}
					}
				}				
				high = -1;
				low = -1;
				lastDay = day;
			}
			
			if (h>=h1 && h<=h2){
				if (high==-1 || q.getHigh5()>=high){
					high = q.getHigh5();
				}
				if (low==-1 || q.getLow5()<=high){
					low = q.getLow5();
				}
			}
		}
		
		/*double winPer = wins*100.0/count;
		System.out.println(
				h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+bars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false,3)	
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false,3)
				+" "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false,3)
				);*/
	}
	
	public static void testeContinuationTPSL(
			String header,
			ArrayList<QuoteShort>  data,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			ArrayList<Integer> maxMins,
			int thr,
			int tp,
			int sl,			
			int bars,
			int debug
			){
		

		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int acc = 0;
		int count = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (h<h1 || h> h2) continue;
			
			if (h==0 && min<15) continue;//no se puede tradear antes
			if (h==23 && min>=55) continue;
			
			int maxMin = maxMins.get(i-1);
			
			if (h>=h1 && h<=h2){
				if (maxMin>=thr){
					
					int entry = q.getOpen5();
					int valueTP = entry + 10*tp;
					int valueSL = entry - 10*sl;
					
					TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+bars, valueTP, valueSL,false);
					
					int diff = qm.getClose5() - entry;
					
					if (diff>=0){
						wins++;
						winPips += diff;
					}else{
						losses++;
						lostPips += -diff;
					}
					count++;
				}else if (maxMin<=-thr){
					int entry = q.getOpen5();
					int valueTP = entry - 10*tp;
					int valueSL = entry + 10*sl;
					
					TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+bars, valueTP, valueSL,false);
					
					int diff = entry-qm.getClose5();
					
					if (diff>=0){
						wins++;
						winPips += diff;
					}else{
						losses++;
						lostPips += -diff;
					}
					count++;
				}
			}
		}
		
		double winPer = wins*100.0/count;
		System.out.println(
				h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+bars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false,3)	
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false,3)
				+" "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false,3)
				);
	}
	
	public static void testeContinuation(
			String header,
			ArrayList<QuoteShort>  data,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			ArrayList<Integer> maxMins,
			int thr,
			int tp,
			int sl,			
			int bars,
			int debug
			){
		

		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int acc = 0;
		int count = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (h<h1 || h> h2) continue;
			
			if (h==0 && min<15) continue;//no se puede tradear antes
			if (h==23 && min>=55) continue;
			
			int maxMin = maxMins.get(i-1);
			
			if (h>=h1 && h<=h2){
				if (maxMin>=thr){
					
					int entry = q.getOpen5();
					
					
					TradingUtils.getMaxMinShort(data, qm, calqm, i, i+bars);
					
					int diff = (qm.getHigh5()-q.getOpen5())-(q.getOpen5()-qm.getLow5());					
					acc +=diff; 
					if ( (qm.getHigh5()-q.getOpen5())>=(q.getOpen5()-qm.getLow5()))
						wins++;
					count++;
				}else if (maxMin<=-thr){
					TradingUtils.getMaxMinShort(data, qm, calqm, i, i+bars);
					
					int diff = (q.getOpen5()-qm.getLow5())-(qm.getHigh5()-q.getOpen5());
					acc +=diff;
					if ( (q.getOpen5()-qm.getLow5())>=(qm.getHigh5()-q.getOpen5()))
						wins++;
					count++;
				}
			}
		}
		
		double winPer = wins*100.0/count;
		System.out.println(
				h1+" "+h2+" "+thr+" "+bars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false,3)	
				+" "+PrintUtils.Print2dec(acc*0.1/count, false,3)				
				);
	}
	
	private static void doOptimization(ArrayList<QuoteShort> dataNoise, ArrayList<Integer> maxMins,
			ArrayList<FFNewsClass> news, int y1, int y2, int h1, int h2, 
			int targetYears, double targetAvgYear, int debug) {
		
		int comm = 20;
		//10h 2200 34 12 60 145
		//11h 1500 34 12 60 177 
		//12h 1400 30 12 60 226
		//13h 900 34 15 45 18
		//14h 2600 53 14 42 49
		//15h 2800 24 15 75 52 
		//16h 1700 11 11 55 144 
		//17h 2200 15 7 56 270 2.50 002.05 // 
		//18h 300 19 7 56 189 1.94 1.66
		//19h 900 14 7 56 166 4.07 4.77
		//20h 1900 14 6 56 184 3.01 2.41
		//21h 1800 30 6 48 85 3.31 4.04
		//22h 1900 30 6 48 85 003.05 002.12 
		//23h 1700 47 15 60 82
		
		//10-13h 1900 25 6 54 161 // 1900 25 12 120 161 // 1900 25 12 48 161 // 2700 25 12 48
		
		h1 = 10;
		h2 = h1+13;
		StrategyResultEx stats = new StrategyResultEx();
		for (int thr1 = 1900; thr1<=1900;thr1+=100){
			for (int thr2 = 25; thr2<=25;thr2+=1){
				for (int bars= 161;bars<=161;bars+=1){
					for (int tp=12;tp<=12;tp+=1){
						for (int sl=(int) (4.0*tp);sl<=4*tp;sl+=1.0*tp){
							int count = 0;
							int totalTrades = 0;
							double avgAcc = 0;
							double pfAcc = 0;
							for (int y=y1;y<=y2;y++){
								stats.setExpectancy(0.0);
								stats.setTotalTrades(0);
								MomentumStudy.testRangesProbPips("", 
										dataNoise,
										news,
										y, y, 
										0, 11, 
										Calendar.MONDAY,Calendar.MONDAY+4,
										h1, h2, 
										maxMins, 
										thr1,thr2, bars,tp,sl,comm, 0,false,stats
								);
								
								if (stats.getExpectancy()>=targetAvgYear){
									count++;
									totalTrades += stats.getTotalTrades();
									avgAcc +=stats.getExpectancy(); 
									pfAcc += stats.getProfitFactor();
								}
							}
							
							if (count>=targetYears){
								String header =count+" ";
										//+" "+totalTrades
										//+" "+PrintUtils.Print2dec(pfAcc*1.0/count, false,3)
										//+" "+PrintUtils.Print2dec(avgAcc*1.0/count, false,3)+" ";
								MomentumStudy.testRangesProbPips(header, 
										dataNoise,
										news,
										y1, y2, 
										0, 11, 
										Calendar.MONDAY,Calendar.MONDAY+4,
										h1, h2, 
										maxMins, 
										thr1,thr2, bars,tp,sl,comm, 0,true,stats
								);
								/*System.out.println(h1+" "+h2+" "+thr1+" "+thr2+" "+bars+" "+tp+" "+sl
										+" || "+count
										+" "+totalTrades
										+" "+PrintUtils.Print2dec(pfAcc*1.0/count, false,3)
										+" "+PrintUtils.Print2dec(avgAcc*1.0/count, false,3)
										);*/
							}
						}
					}
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
			//String path0 ="C:\\fxdata\\";
			//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
			//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
			String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
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

				for (int h1=0;h1<=0;h1++){
					MomentumStudy.doOptimization(dataNoise,maxMins,news,2017,2017,h1,h1,0,0.0,0);
				}
				
				/*for (int y1=2004;y1<=2004;y1+=1){
					int y2 = y1+13;
					for (int m1=0;m1<=0;m1+=1){
						int m2 = m1+11;		
						for (int d1=Calendar.MONDAY;d1<=Calendar.MONDAY+0;d1++){
							int d2 = d1+4;
							for (int h1=16;h1<=16;h1++){
								int h2 = h1+0;
								for (int thr=0;thr<=5000;thr+=25){
									for (int thr2=28;thr2<=28;thr2+=1){
										for (int bars=210;bars<=210;bars+=1){
											//for (int sl=15;sl<=15;sl+=10){
												for (int tp=9;tp<=9;tp+=1){
													for (int sl=(int) (5.0*tp);sl<=5*tp;sl+=1.0*tp){
													//for (int thr2=24;thr2<=24;thr2++){												
														//MomentumStudy.testRangesProb(header, dataNoise, y1, y2, m1, m2, h1, h2, maxMins, thr, thr2,bars, 0);
													//}
													for (int pips=10;pips<=10;pips++){												
														MomentumStudy.testRangesProbPips(header, 
																dataNoise,
																news,
																y1, y2, 
																m1, m2, 
																d1,d2,
																h1, h2, 
																maxMins, 
																thr,thr2, bars,tp,sl, 0);
													}
												//for (int sl=30;sl<=30;sl++){
													//MomentumStudy.testeContinuation(header, dataNoise, null, y1, y2, m1, m2, h1, h2, maxMins, thr, 0, 0, bars, 0);
													//MomentumStudy.testeContinuationTPSL(header, dataNoise, null, y1, y2, m1, m2, h1, h2, maxMins, thr, tp, sl, bars, 0);
													//MomentumStudy.testRanges(header, dataNoise, y1, y2, m1, m2, h1, h2, maxMins, thr, tp, sl, 30, 0);
													
												}//tp
											}//sl										
										}//bars
									}//thr2
								}//h1
							}//d1
						}
					}
				}//y1*/
				
			}

	}

	

}
