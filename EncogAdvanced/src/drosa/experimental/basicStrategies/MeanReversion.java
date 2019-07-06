package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MeanReversion {
	
	public static double doTest(
			String header,
			ArrayList<QuoteShort>  data,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			ArrayList<Integer> maxMins,
			int thr,
			int barsBack,
			int barsBackThr,
			int tp,
			int sl,			
			int bars,
			int comm,
			int aImpact,
			double aDebugAvg,
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
			
			if (maxMin>=thr){
				
				int impact = FFNewsClass.getDayImpact(news, cal, 0);
				
				//int idx = TradingUtils.getMaxMinIndex(data, i-barsBack, i-1, isLow);
				//int diff = q1.getHigh5()-data.get(idx).getLow5();
				
				//if (diff>=barsBackThr){
				
				if (impact<=aImpact) {
					int entry = q.getOpen5();
					int valueTP = entry - tp;
					int valueSL = entry + sl;
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+bars, entry, valueTP, valueSL, false);
					
					int pips = entry-qm.getClose5()-comm;
					
					if (pips>=0){
						wins++;
						winPips += pips;
					}else if (pips<0){
						losses++;
						lostPips += -pips;
					}	
				}
				//}				
			}else if (maxMin<=-thr){
				//int idx = TradingUtils.getMaxMinIndex(data, i-barsBack, i-1, isLow);
				//int diff = data.get(idx).getHigh()-q1.getLow5();
				
				//if (diff>=barsBackThr){
				int impact = FFNewsClass.getDayImpact(news, cal, 0);
				if (impact<=aImpact) {
					int entry = q.getOpen5();
					int valueTP = entry + tp;
					int valueSL = entry - sl;
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, i+bars, entry, valueTP, valueSL, false);
					
					int pips = qm.getClose5()-entry-comm;
					
					if (pips>=0){
						wins++;
						winPips += pips;
					}else if (pips<0){
						losses++;
						lostPips += -pips;
					}	
				}
				//}
			}								
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = winPips*1.0/(lostPips);
		double avg = (winPips-lostPips)*0.1/total;
		
		if (debug==1
				&& avg>=aDebugAvg
				) {
		System.out.println(
				y1+" "+y2+" "+m1+" "+m2
				+" "+h1+" "+h2
				+" "+thr
				+" "+tp
				+" "+sl
				+" "+bars
				+" "+comm
				+" "+aImpact
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+header
				);
			//return avg;
		}
		
		return avg;
	}

	public static void main(String[] args) throws Exception {
		
		//String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
		String path0 ="C:\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.12.31_2017.10.26.csv";
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
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataNoise);
			
			/*for (int y1=2010;y1<=2017;y1+=1){
				int y2 = y1+0;
				for (int m1=0;m1<=0;m1+=1){
					int m2 = m1+11;			
					for (int h1=0;h1<=0;h1++){//usdjpy: 159 48 145 8 110 25 9.5 --- gbpusd 90/60/168/15/70/25/8.0
						int h2 = h1+9;	
						//System.out.println(h1);
						int count = 0;
						for (int thr=9500;thr<=9500;thr+=100){
							for (int barsBackThr=0;barsBackThr<= 0;barsBackThr+=10){
								for (int barsBack=10000;barsBack<=10000;barsBack+=1){														
									for (int tp=200;tp<=200;tp+=50){
										for (int sl=8*tp;sl<=8*tp;sl+=1*tp){	
											for (int bars=300;bars<=300;bars+=30){
												for (int impact=999;impact<=999;impact+=3) {
													double avg = MeanReversion.doTest(data,news, 
															y1, y2, m1, m2, h1, h2, maxMins, thr, 
															barsBack, barsBackThr, 
															tp, sl,bars,impact,-99.0,1);
													if (avg>=2.5) count++;
												}
											}
										}
									}
								}
							}
						}//thr
						//System.out.println(h1+" || "+count);
					}//h
				}
			}*/
				//EURUSD
				//COMM 18
				// 0h  600 200 600 1890  //1.98 7.67
			    // 1h  700 500 3000 800  //1.55 8.46
				// 2h 2300 500 4500 2000  //1.36 7.81
				// 3h  3600 200 1800 30    //1.63 3.83
				// 4h  4000 200 200 1100  //1.21 1.84
				// 5h 2300 250 2250 270  //2.16 6.39
				// 6h 2600 200 1400 310  //2.40 7.44
				// 7h 1300 200  600 270  //2.15 6.94
	            // 8h 3600 200  600  60   //1.53 2.58
				// 9h: 1400 150 1200 65 //1.26  1.48
			
				// 0h:  600 20  60  810 //2.15  8.49
				// 1h:  700 50 250  810 //1.70 10.48
				// 2h: 2700 20  80  690 //1.33  3.57
				// 3h: 3600 20  80  210 //1.97  4.67
				// 4h: 2600 15  30  360 //1.52  3.48
				// 5h: 2300 15 250  270 //3.08  7.44
				// 6h: 2100 15  20  140 //3.34  7.80
				// 7h: 1300 20  60  270 //2.55  8.48
				// 8h: 3600 20  60   60 //2.01  4.33
				// 9h: 1400 150 1200 65 //1.26  1.48
			    
				//12h:
				//14h:
				//18h: 3700 15 150  300 //1.34 2.78
				//19h: 1300 20  60 1620 //1.38 3.91
				//20h:
				//21h:
				//22h: 4300 20  60 1110 //1.73 6.58
				//23h: 1300 45 270  615 //2.35 6.68
			
				for (int h1=23;h1<=23;h1++){//usdjpy: 159 48 145 8 110 25 9.5 --- gbpusd 90/60/168/15/70/25/8.0
					int h2 = h1+0;	
					//System.out.println(h1);
					int count = 0;
					for (int thr=1300;thr<=1300;thr+=100){
						for (int barsBackThr=0;barsBackThr<= 0;barsBackThr+=10){
							for (int barsBack=10000;barsBack<=10000;barsBack+=1){														
								for (int tp=200;tp<=200;tp+=50){
									for (int sl=3*tp;sl<=3*tp;sl+=1*tp){	
										for (int bars=100;bars<=2000;bars+=15){
											for (int impact=999;impact<=999;impact+=3) {
												count = 0;
												double acc = 0;
												String avgStr="";
												for (int y1=2004;y1<=2017;y1+=1){
													int y2 = y1+0;
													for (int m1=0;m1<=0;m1+=1){
														int m2 = m1+11;		
														double avg = MeanReversion.doTest("",data,news, 
																y1, y2, m1, m2, h1, h2, maxMins, thr, 
																barsBack, barsBackThr, 
																tp, sl,bars,18,990,-99.0,0);
														if (avg>=0.01) count++;
														acc +=avg;
														avgStr += PrintUtils.Print2dec(avg, false)+" ";
													}
												}//años
												double avgYears = acc*1.0/14;
												if (count>=0 || avgYears>=0.01){
													String header = count+"";
													MeanReversion.doTest(header,data,news, 
															2004, 2017, 0, 11, h1, h2, maxMins, thr, 
															barsBack, barsBackThr, 
															tp, sl,bars,18,990,-99.0,1);
													/*System.out.println(
														h1+" "+h2
														+" "+thr
														+" "+tp
														+" "+sl
														+" "+bars
														+" "+impact
														+" || "+count+" "+PrintUtils.Print2dec(avgYears, false)+" ["+avgStr.trim()+"]"
													);*/
												}//count
											}//impact
										}//bars
									}//sl
								}//tp
							}//barsBack
						}//barsBackThr
					}//thr
				}//h
		
				
		}

	}

}
