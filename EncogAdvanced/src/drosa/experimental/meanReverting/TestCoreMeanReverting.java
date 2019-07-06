package drosa.experimental.meanReverting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestCoreMeanReverting {
	
	public static MeanRevertingStats doTradeEndDay(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMinsExt,
			HashMap<Integer,Integer> lastDayValueHash,
			int y1,int y2,
			int h1,int h2,
			int thr,int minC,
			boolean printResult){
		
		MeanRevertingStats stats = new MeanRevertingStats ();
		
		int acc = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int total = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (y<y1 || y>y2) continue;
			if (h<h1 || h>h2) continue;
			
			int dayNumber = cal.get(Calendar.YEAR)*365+cal.get(Calendar.DAY_OF_YEAR);			
			int maxMin = maxMinsExt.get(i-1);
			int dayClose = lastDayValueHash.get(dayNumber);
			
			int diff = -99999;
			if (maxMin>=thr){ //shorts
				diff = data.get(i).getOpen5()-dayClose;
			}else if (maxMin<=-thr){
				diff = dayClose-data.get(i).getOpen5();
			}
			
			if (diff!=-99999){
				acc+=diff;
				
				if (diff>=0){
					wins++;
					winPips+=diff;
				}else{
					lostPips+=-diff;
				}
				total++;
			}
		}
		
		double winPer = wins*100.0/total;
		double avg = acc*0.1/total;
		double pf = winPips*1.0/lostPips;
		if (printResult)
			System.out.println(
					y1+" "+y2
					+" "+h1+" "+h2
					+" "+thr
					+" || "
					+" "+total
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(pf, false)
					);
		
		
		stats.setTotalTrades(total);
		stats.setWins(wins);
		
		return stats;
	}
	
	
		
	public static MeanRevertingStats  doTrade(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMinsExt,
			int y1,int y2,
			int h1,int h2,
			int thr,int minC,
			double minPips,int bkBars,int fBars,int tp,int sl,
			int maxTrades,
			boolean printResult
			){
		
		MeanRevertingStats stats = new MeanRevertingStats ();
		ArrayList<Integer> avgs = new ArrayList<Integer>();
		int acc = 0;
		int total  = 0;
		int dayOpen = -1;
		int lastDay = -1;
		int wins = 0;
		int bes = 0;
		int besNeg = 0;
		int besPos = 0;
		double minbes=0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int dayTrades = 0;
		int actualRange = 100;
		int actualMax = -1;
		int actualMin = -1;
		for (int i=bkBars;i<data.size()-fBars;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				dayTrades = 0;
				dayOpen = q.getOpen5();
				int range = (int) ((actualMax-actualMin)*0.1); 
				avgs.add(range);	
				
				actualRange = (int) MathUtils.average(avgs, avgs.size()-21, avgs.size()-1);
				actualMax = -1;
				actualMin = -1;
				lastDay = day;
			}
			
			if (q.getHigh5()>=actualMax || actualMax==-1) actualMax = q.getHigh5();
			if (q.getLow5()<=actualMin || actualMin==-1) actualMin = q.getLow5();
			
			int minPipsAbs = (int) (actualRange*minPips);
			if (h==0 && minute<10) continue;
			if (dayTrades>=maxTrades) continue;
			
			int maxMin = maxMinsExt.get(i-1);
			if (h>=h1 && h<=h2){
				
				if (maxMin>=thr){
				//if (q.getOpen5()>=dayOpen){
					TradingUtils.getMaxMinShort(data, qm, calqm,i-thr,i-1);
					int diffMin = q.getOpen5()-qm.getLow5();
					double avgDiff = TradingUtils.getAvgDifference(data,i-bkBars,i-1,i,true,false);
					int diffHC = data.get(i-1).getHigh5()-data.get(i-1).getClose5();
					int diffOC = data.get(i-1).getOpen5()-data.get(i-1).getClose5();
					if (avgDiff>=minPipsAbs*10
							//&& diffMin>=minC*10
							//&& diffHC>=minC*10
							//&& diffOC<=minC*10
							){
						//solo para debug
						//TradingUtils.getAvgDifference(data,i-bkBars,i-1,i,true,true);
						//System.out.println("[AVG] "+PrintUtils.Print2dec(avgDiff, true));
						//debug
						
						int valueTP = q.getOpen5()-tp*10;
						int valueSL = q.getOpen5()+sl*10;
						//TradingUtils.getMaxMinShort(data, qm, cal, i, i+fBars);
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+fBars, valueTP,valueSL,false);
						//int maxDiff = q.getOpen5()-qm.getLow5();
						//int minDiff = qm.getHigh5()-q.getOpen5();
						//int diff = maxDiff-minDiff;
						int diff = q.getOpen5()-qm.getClose5();
						acc += diff;
						if (diff>=0){
							wins++;
							besPos += diff;
							double minutesDiff = (int) ((calqm.getTimeInMillis()-cal.getTimeInMillis())/(60*1000));
							minbes += minutesDiff;
						}else{
							besNeg += -diff;
						}
												
						//System.out.println("[SHORT] "+diff+" || "+valueTP+" "+q.toString()+" "+qm.toString()+" "+bes);
						total++;
						dayTrades++;
					}
				}else if (maxMin<=-thr){
					//if (q.getOpen5()<=dayOpen){
					TradingUtils.getMaxMinShort(data, qm, calqm,i-thr,i-1);
					int diffMax = qm.getHigh5()-q.getOpen5();
					double avgDiff = TradingUtils.getAvgDifference(data,i-bkBars,i-1,i,false,false);
					int diffCL = data.get(i-1).getClose5()- data.get(i-1).getLow5();
					int diffOC = data.get(i-1).getClose5()- data.get(i-1).getOpen5();
					if (avgDiff>=minPipsAbs*10
							//&& diffMax>=minC*10
							//&& diffCL>=minC*10
							//&& diffOC<=minC*10
							){
						//solo para debug
						//TradingUtils.getAvgDifference(data,i-bkBars,i-1,i,false,true);
						//System.out.println("[AVG] "+PrintUtils.Print2dec(avgDiff, true));
						//debug
						
						int valueTP = q.getOpen5()+tp*10;
						int valueSL = q.getOpen5()-sl*10;
						//TradingUtils.getMaxMinShort(data, qm, cal, i, i+fBars);
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+fBars, valueTP,valueSL,false);
						//int maxDiff = q.getOpen5()-qm.getLow5();
						//int minDiff = qm.getHigh5()-q.getOpen5();
						//int diff = maxDiff-minDiff;
						int diff = qm.getClose5()-q.getOpen5();
						acc += diff;
						if (diff>=0){
							wins++;
							besPos += diff;
							double minutesDiff = (int) ((calqm.getTimeInMillis()-cal.getTimeInMillis())/(60*1000));
							minbes += minutesDiff;
						}else{
							besNeg += -diff;
						}
						total++;
						dayTrades++;
					}
				}
			}
		}
		
		double winPer = wins*100.0/total;
		//double besPer = bes*100.0/total;
		//double besAvgTime = minbes/bes;
		if (printResult)
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+PrintUtils.Print2dec(minC,false)
				//+" "+PrintUtils.Print2dec(minPips,false)
				//+" "+bkBars				
				//+" "+fBars
				+" "+tp+" "+sl
				+" "+thr
				+" || "+total
				+" "+PrintUtils.Print2dec(acc*0.1/total, false)
				+" "+PrintUtils.Print2dec(winPer, false)
				//+" || "+PrintUtils.Print2dec(besPer, false)
				//+" "+PrintUtils.Print2dec(besAvgTime, false)
				+" || "+PrintUtils.Print2dec((besPos-besNeg)*0.1/total, false)
				+" "+PrintUtils.Print2dec((besPos*1.0/besNeg), false)
				);
		
		
		stats.setTotalTrades(total);
		stats.setAvgPips((besPos-besNeg)*0.1/total);
		stats.setPf(besPos*1.0/besNeg);
		stats.setWins(wins);
		return stats;
		
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.06.21.csv";
				//String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.12.31_2016.06.21.csv";
				//String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2016.06.21.csv";
				//String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2016.06.21.csv";
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(pathEURUSD);
				
				Sizeof.runGC ();
				ArrayList<QuoteShort> dataI 		= null;
				ArrayList<QuoteShort> dataS 		= null;
				int limit = 0;
				for (int i = 0;i<=limit;i++){
					String path = paths.get(i);		
					dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
					dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
					ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
					ArrayList<QuoteShort> data = null;
					data = data5m;
					
				
					HashMap<Integer,Integer> lastDayValueHash =TradingUtils.calculateLastDayValues(data);
					//ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
					ArrayList<Integer> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
					
						for (double minPips=0.00;minPips<=0.00;minPips+=0.01){//para 0 = 0.07/12|| 1 = 0.10/24|| 2=0.18/168|| 3=0.28/126|| 4=0.18/72 || 5 = 0.27/90 || 6= 0.21/120 || 7=0.24/180 || 8 = 0.28/132
							for (int bkBars=100000;bkBars<=100000;bkBars+=12){
								for (int fBars=20000;fBars<=20000;fBars+=15){
									for (int h1=0;h1<=16;h1++){
										int h2 = h1+7;
										for (int tp=5;tp<=5;tp++){
											for (int sl=1*tp;sl<=1*tp;sl+=tp){
												for (int maxTrades=1000;maxTrades<=1000;maxTrades++){
													int totalPositives = 0;
													double avg = 0;
													int avgCount = 0;
													double minAvg = 2.0;
													int minTrades = 1000;
													int totalTrades = 0;
													int totalWins = 0;
													for (int thr=500;thr<=500;thr+=50){
														for (int minC=0;minC<=0;minC+=10){
															for (int y1=2009;y1<=2009;y1++){
																int y2 = y1+7;
																
																TestCoreMeanReverting.doTradeEndDay(data, maxMinsExt, lastDayValueHash, y1, y2, h1, h2, thr, minC,true);
																
																/*MeanRevertingStats stats  = TestCoreMeanReverting.doTrade(data,maxMinsExt,y1,y2, h1, h2,thr,minC,
																		minPips, bkBars, fBars,tp,sl,maxTrades,true);
																double avf = stats.getAvgPips();
																totalTrades += stats.getTotalTrades();
																totalWins += stats.getWins();
																avg += avf;
																avgCount++;
																if (avf>=minAvg){
																	totalPositives++;
																}*/
															}	
															//double average = avg/avgCount; 
															/*if (average>=0.0 
																	&& totalTrades>=minTrades
																	//&& totalPositives>=(avgCount-3)
																	){
																int totalLosses = totalTrades-totalWins;
																double pf = (totalWins*tp)*1.0/(totalLosses*sl);
																System.out.println(
																		h1+" "+h2
																		+" "+PrintUtils.Print2dec(minPips, false)
																		+" "+bkBars
																		+" "+tp+" "+sl
																		+" || "
																		+" "+totalTrades
																		+" "+totalPositives+"/"+avgCount+" "+PrintUtils.Print2dec(average, false)
																		+" "+PrintUtils.Print2dec(average, false)
																		+" "+PrintUtils.Print2dec(pf, false)
																		);
															}*/
														}
													}
												}
											}//sl
										}
									}
								}
							}
						}
					
				}

	}

}
