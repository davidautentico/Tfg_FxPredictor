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

public class GlobalStrat {
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int thr,
			int minDistance,
			int entryDistance,
			int maxDistance,
			int debug
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
		ArrayList<Integer> entriesArr = new ArrayList<Integer>();
		double range = 1000.0;
		int point = -1;
		int barTries = 0;
		int dayIndex = 0;
		int mode = 0;
		int doValue = -1;
		int acc10 = 0;
		int total10=0;
		int comm = 10;
		int entry = -1;
		int size = 0;
		int pivotH = -1;
		int pivotL = -1;
		int target = 0;
		
		
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
			
				lastDay = day;
				totalDays++;
				high = -1;
				low = -1;
				point = -1;
				barTries = 0;
				dayIndex = 0;
				doValue = q.getOpen5();
			}
			
			//VEMOS POSIBLES ENTRADAS
			if (mode==0){				
				if (maxMins.get(i)>=thr){
					entry = q.getClose5();
					size=1;
					pivotH = entry;
					pivotL = entry;
					target = entry-100;
					mode = -1;
					
					if (debug==1)
						System.out.println("[OPEN TARGET] "+entry+" "+target+" || "+q.toString());
				}else if (maxMins.get(i)<=-thr){
					entry = q.getClose5();
					size=1;
					pivotH = entry;
					pivotL = entry;
					target = entry+100;
					//mode = 1;
				}
			}else if (mode==1){
				
				int actualDistance = q.getClose5()-pivotL;
				
				if (q.getLow5()<=pivotL){
					//calculo del averagePrice
					int diffPips = pivotH-pivotL;
					int entries = diffPips / entryDistance;
					int avg = entry;
					int initial = entry;
					size = 1;
					for (int e=1;e<=entries;e++){
						avg += initial - e*entryDistance;
						size++;
					}
					int avgPrice = avg/size;
										
					pivotL = q.getLow5();
					
					target = avgPrice+100;
					
					if (actualDistance>=maxDistance){
						if (q.getClose5()-avgPrice>=0){
							diffs.add(pivotH-pivotL);
							winPips += (entries+1)*(q.getClose5()-avgPrice);
							wins++;
						}else{
							lostPips += -(entries+1)*(q.getClose5()-avgPrice);
							losses++;
						}
						entriesArr.add(size);
						mode = 0;
					}
				}
				
				if (q.getHigh5()>=target 
						&& mode==1
						){
					//if (debug==1)
					//System.out.println("[TARGET CLOSED] "+target+" || "+(pivotH-pivotL)+" || "+q.toString());
					
										
					//calculo del averagePrice
					int diffPips = pivotH-pivotL;
					int entries = diffPips / entryDistance;
					int avg = entry;
					int initial = entry;
					size = 1;
					for (int e=1;e<=entries;e++){
						avg += initial - e*entryDistance;
						size++;
					}
					int avgPrice = avg/size;
					
					if ((pivotH-pivotL)>=minDistance){
						diffs.add(pivotH-pivotL);
						if (q.getClose5()-avgPrice>=0){
							diffs.add(pivotH-pivotL);
							winPips += (entries+1)*(q.getClose5()-avgPrice);							
							wins++;
						}else{
							lostPips += -(entries+1)*(q.getClose5()-avgPrice);
							losses++;
						}
						mode = 0;
						entriesArr.add(size);
					}
					
					if (debug==2)
						System.out.println("[TARGET CLOSED] "+target
								+" || "+q.getClose5()
								+" || "+(avg/size)
								+" || "+(pivotH-pivotL)
								+" || "+q.toString());
				}
			}else if (mode==-1){
				
				int actualDistance = q.getClose5()-pivotL;
				if (q.getHigh5()>=pivotH){
					//calculo del averagePrice
					int diffPips = pivotH-pivotL;
					int entries = diffPips / entryDistance;
					int avg = entry;
					int initial = entry;
					size = 1;
					for (int e=1;e<=entries;e++){
						avg += initial + e*entryDistance;
						size++;
					}
					int avgPrice = avg/size;
					
					
					pivotH = q.getHigh5();
					
					target = avgPrice-100;
					
					if (actualDistance>=maxDistance){
						diffs.add(pivotH-pivotL);
						if (-q.getClose5()+avgPrice>=0){
							winPips += (entries+1)*(-q.getClose5()+avgPrice);
							wins++;
							
							if (debug==1)
								System.out.println("[CLOSED DISTANCE WIN] "+target+" "+pivotH+" "+pivotL
										+" || "+(pivotH-pivotL)
										+" || "+(-q.getClose5()+avgPrice)
										+" || "+q.toString());
						}else{
							lostPips += -(entries+1)*(-q.getClose5()+avgPrice);
							losses++;
							
							if (debug==1)
								System.out.println("[CLOSED DISTANCE LOSS] "+target+" "+pivotH+" "+pivotL
										+" || "+(pivotH-pivotL)
										+" || "+(-q.getClose5()+avgPrice)
										+" || "+q.toString());
						}
						entriesArr.add(size);
						mode = 0;
					}else{					
						if (debug==1)
							System.out.println("[**NEW TARGET] "+target+" "+pivotH+" "+pivotL+" || "+(pivotH-pivotL)+" || "+q.toString());
					}
				}
				
				if (q.getLow5()<=target 
						&& mode==-1
						){
					
					
					if ((pivotH-pivotL)>=minDistance)
						diffs.add(pivotH-pivotL);
					
					//calculo del averagePrice
					int diffPips = pivotH-pivotL;
					int entries = diffPips / entryDistance;
					int avg = entry;
					int initial = entry;
					size = 1;
					for (int e=1;e<=entries;e++){
						avg += initial + e*entryDistance;
						size++;
					}
					
					int avgPrice = avg/size;
					
					if ((pivotH-pivotL)>=minDistance){
						diffs.add(pivotH-pivotL);
						if (-q.getClose5()+avgPrice>=0){
							diffs.add(pivotH-pivotL);
							winPips += (entries+1)*(-q.getClose5()+avgPrice);
							wins++;
							if (debug==1)
								System.out.println("[CLOSED WIN] "+target+" "+pivotH+" "+pivotL
										+" || "+(pivotH-pivotL)
										+" || "+(-q.getClose5()+avgPrice)
										+" || "+q.toString());
						}else{
							lostPips += -(entries+1)*(-q.getClose5()+avgPrice);
							losses++;
							if (debug==1)
							System.out.println("[CLOSED LOSS] "+target+" "+pivotH+" "+pivotL
									+" || "+(pivotH-pivotL)
									+" || "+(-q.getClose5()+avgPrice)
									+" || "+q.toString());
						}
						entriesArr.add(size);
						mode = 0;
					}
					
					if (debug==2)
						System.out.println("[TARGET CLOSED] "+target
								+" || "+q.getClose5()
								+" || "+(avg/size)
								+" || "+(pivotH-pivotL)
								+" || "+q.toString());
				}
			}
			

		}
			

		int trades = wins+losses;
		double avg = MathUtils.average(diffs);
		double std = Math.sqrt(MathUtils.variance(diffs));
		double perWin = wins*100.0/trades;
		double avgE = MathUtils.average(entriesArr);
		/*
		double avg2 = MathUtils.average(speeds);
		double pf = winPips*1.0/lostPips;
		double pfr = lostPips*1.0/winPips;*/
		System.out.println(
				""+" "+y1+" "+y2+" "+h1+" "+h2+" "+minDistance+" "+entryDistance+" "+maxDistance
				+" || "
				+" "+diffs.size()+" "+totalDays
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(std, false)
				+" || "+PrintUtils.Print2dec(perWin, false)
				+" || "+PrintUtils.Print2dec((winPips-lostPips)*0.1/trades, false)
				+" || "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)
				+" || "+PrintUtils.Print2dec(avgE, false)
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurusd_5 Mins_Bid_2004.01.01_2018.08.27.csv";
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
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+23;
					for (int period=1000;period<=1000;period+=25){
						for (double per=0.0;per<=0.0;per+=0.05){
							for (int barTries = 40;barTries<=40;barTries++){
								for (int minDistance = 0;minDistance<=0;minDistance+=100)
									for (int entryDistance = 50;entryDistance<=50;entryDistance+=50){
										for (int maxDistance = 100;maxDistance<=40000;maxDistance+=1000){
											GlobalStrat.doTest("", data, maxMins, y1, y2, 0, 11, h1, h2,period,minDistance,entryDistance,maxDistance,0);
										}
									}
							}
								
						}
					}
				}
			}
			
		}


	}

}
