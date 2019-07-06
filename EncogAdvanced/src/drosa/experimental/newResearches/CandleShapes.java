package drosa.experimental.newResearches;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.SuperStrategy;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class CandleShapes {
	
	public static void addTrade(ArrayList<Short> actualTrades,int begin,int end1,int end2){
		
		if (begin<=0) begin = 0;
		if (end1> actualTrades.size()-1) end1 =  actualTrades.size()-1;
		
		for (int j=begin;j<=end1;j++){
			int trades = actualTrades.get(j);
			actualTrades.set(j,(short) (trades+1));
		}
		if (end2> actualTrades.size()-1) end2 =  actualTrades.size()-1;
		for (int j=end1+1;j<=end2;j++){
			int trades = actualTrades.get(j);
			if (trades>=1)
				actualTrades.set(j,(short) (trades-1));
			else
				actualTrades.set(j,(short) 0);
		}
	}
	
	public static int countSpikesSum(ArrayList<QuoteShort> data,int begin,int end,boolean isLong){
		int sum = 0;
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			
			if (isLong){
				int spikeHC = q.getHigh5()-q.getClose5();
				sum+=spikeHC;
			}else{
				int spikeCL = q.getClose5()-q.getLow5();
				sum+=spikeCL;
			}
		}
		
		return sum;
	}
	
	public static void doTradeSpikeBoxesSL(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			String hours,
			int thr,
			int boxSize,
			//int spikeSum,
			double spikeSumFactor,
			int predictionSize,
			double slFactor,
			int maxTrades,
			double comm,
			boolean debug
			){
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		
		ArrayList<Integer> sizes5 = new ArrayList<Integer>();
		ArrayList<Short> actualTrades = new ArrayList<Short>();
		for (int i=0;i<data.size();i++) actualTrades.add((short) 0);
		double maxRRWins = 0.0;
		double rrWins = 0.0;
		double maxDD = 0;
		
		int accSize5 = 0;
		int count5 = 0;
		int count = 0;
		int acc = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calj = Calendar.getInstance();
		Calendar calj1 = Calendar.getInstance();
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			int y = cal1.get(Calendar.YEAR);
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			
			if (y<y1 || y>y2) continue;
			
			int allowed = allowedHours.get(h);
			if (allowed==0) continue;
			
			accSize5 += q1.getHigh5()-q1.getLow5();
			sizes5.add(q1.getHigh5()-q1.getLow5());
			double avg5 = MathUtils.average(sizes5, sizes5.size()-1-5000, sizes5.size()-1);
			//if (avg5<30.0) avg5 = 30.0;
			int slPips = (int) (avg5*0.1*slFactor);
			if (slPips<15) slPips = 15;
			count5++;
			
			int currentTrades = actualTrades.get(i);
			if (currentTrades>maxTrades) continue;
			
			QuoteShort maxMin = maxMins.get(i-1);//de q-1
			if (maxMin.getExtra()>=thr){
				int spikesTotal  = CandleShapes.countSpikesSum(data, i-boxSize, i-1, true);
				double factor = spikesTotal*1.0/avg5;
				if (factor>=spikeSumFactor){
					//QuoteShort mm = TradingUtils.getMaxMinShort(data, calj, i, i+predictionSize);
					QuoteShort mm = TradingUtils.getMaxMinShortLimitSL(data, i, i+predictionSize,slPips, false);
					int lastIdx = mm.getExtra();
					CandleShapes.addTrade(actualTrades, i+1, lastIdx, lastIdx+2*predictionSize);
					int diff = q.getOpen5()-mm.getClose5();
					if (mm.getOpen5()==-1) diff = -slPips*10;
					diff-=comm*10;
					acc += diff;
					if (diff>=0){
						winPips += diff;
						rrWins += diff*0.1/slPips;
						if (debug)
							System.out.println("[SHORT WIN] "+diff+" "+PrintUtils.Print2(diff*0.1/slPips)+" "+rrWins);
						wins++;
						if (rrWins>=maxRRWins) maxRRWins = rrWins;
					}else{
						lostPips += -diff;
						rrWins += diff*0.1/slPips;
						if (debug)
							System.out.println("[SHORT LOSS] "+slPips+" "+diff+" "+PrintUtils.Print2(diff*0.1/slPips, false)+" "+rrWins);
						losses++;
						double actualDD = maxRRWins-rrWins;
						if (actualDD>=maxDD) maxDD = actualDD;
					}
					count++;
				}
			}else if (maxMin.getExtra()<=-thr){
				int spikesTotal  = CandleShapes.countSpikesSum(data, i-boxSize, i-1, false);
				double factor = spikesTotal*1.0/avg5;
				if (factor>=spikeSumFactor){
				//if (spikesTotal>=spikeSum*10){
					//QuoteShort mm = TradingUtils.getMaxMinShort(data, calj, i, i+predictionSize);
					QuoteShort mm = TradingUtils.getMaxMinShortLimitSL(data, i, i+predictionSize,slPips, true);
					int lastIdx = mm.getExtra();
					CandleShapes.addTrade(actualTrades, i+1, lastIdx, lastIdx+2*predictionSize);
					int diff = mm.getClose5()-q.getOpen5();
					if (mm.getOpen5()==-1) diff = -slPips*10;
					diff-=comm;
					acc += diff;
					if (diff>=0){
						winPips += diff;
						rrWins += diff*0.1/slPips;
						if (debug)
							System.out.println("[LONG WIN] "+diff+" "+PrintUtils.Print2(diff*0.1/slPips)+" "+rrWins);
						wins++;
						if (rrWins>=maxRRWins) maxRRWins = rrWins;
					}else{
						lostPips += -diff;
						rrWins += diff*0.1/slPips;
						if (debug)
							System.out.println("[LONG LOSS] "+diff+" "+PrintUtils.Print2(diff*0.1/slPips, false)+" "+rrWins);
						losses++;
						double actualDD = maxRRWins-rrWins;
						if (actualDD>=maxDD) maxDD = actualDD;
					}
					count++;
				}
			}
		}//data
		
		double winPer = wins*100.0/(count);
		double avg = acc*0.1/count;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		double pf = winPips*1.0/lostPips;
		double avg5 = accSize5*0.1/count5;
		System.out.println(
				y1+" "+y2
				+" "+hours
				+" "+thr
				+" "+boxSize
				//+" "+spikeSum
				+" "+PrintUtils.Print2dec(spikeSumFactor, false,2)
				+" "+PrintUtils.Print2dec(slFactor, false,2)
				+" "+predictionSize
				+" || "
				+" "+PrintUtils.Print2Int(count,4)
				+" "+PrintUtils.Print2dec(winPer, false,2)
				+" "+PrintUtils.Print2dec(avg, false,2)
				+" "+PrintUtils.Print2dec(avgWin, false,2)
				+" "+PrintUtils.Print2dec(avgLoss, false,2)
				+" "+PrintUtils.Print2dec(pf, false,2)
				+" || "+PrintUtils.Print2dec(avg5, false,2)
				+" || "
				+" "+PrintUtils.Print2dec(rrWins, false,2)
				+" "+PrintUtils.Print2dec(maxRRWins, false,2)
				+" "+PrintUtils.Print2dec(maxDD, false,2)
				+" ||| "+PrintUtils.Print2dec(maxRRWins*1.0/maxDD, false,2)
				);
	}
	
	public static void doTradeSpikeBoxes(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int boxSize,
			//int spikeSum,
			double spikeSumFactor,
			int predictionSize
			){
		
		ArrayList<Integer> sizes5 = new ArrayList<Integer>();
		int accSize5 = 0;
		int count5 = 0;
		int count = 0;
		int acc = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calj = Calendar.getInstance();
		Calendar calj1 = Calendar.getInstance();
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			int y = cal1.get(Calendar.YEAR);
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			
			if (y<y1 || y>y2) continue;
			if (h<h1 || h>h2) continue;
			
			accSize5 += q1.getHigh5()-q1.getLow5();
			sizes5.add(q1.getHigh5()-q1.getLow5());
			double avg5 = MathUtils.average(sizes5, sizes5.size()-1-2880, sizes5.size()-1);
			count5++;
			QuoteShort maxMin = maxMins.get(i-1);//de q-1
			if (maxMin.getExtra()>=thr){
				int spikesTotal  = CandleShapes.countSpikesSum(data, i-boxSize, i-1, true);
				double factor = spikesTotal*1.0/avg5;
				//System.out.println(avg5+" "+spikesTotal+" "+factor);
				//System.out.println(spikesTotal);
				//if (spikesTotal>=spikeSum*10){
				//if (spikesTotal>=spikeSum*10){
				if (factor>=spikeSumFactor){
					QuoteShort mm = TradingUtils.getMaxMinShort(data, calj, i, i+predictionSize);
					int diff = q.getOpen5()-mm.getClose5();
					acc += diff;
					if (diff>=0){
						winPips += diff;
						wins++;
					}else{
						lostPips += -diff;
						losses++;
					}
					count++;
				}
			}else if (maxMin.getExtra()<=-thr){
				int spikesTotal  = CandleShapes.countSpikesSum(data, i-boxSize, i-1, false);
				double factor = spikesTotal*1.0/avg5;
				if (factor>=spikeSumFactor){
				//if (spikesTotal>=spikeSum*10){
					QuoteShort mm = TradingUtils.getMaxMinShort(data, calj, i, i+predictionSize);
					int diff = mm.getClose5()-q.getOpen5();
					acc += diff;
					if (diff>=0){
						winPips += diff;
						wins++;
					}else{
						lostPips += -diff;
						losses++;
					}
					count++;
				}
			}
		}//data
		
		double winPer = wins*100.0/(count);
		double avg = acc*0.1/count;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		double pf = winPips*1.0/lostPips;
		double avg5 = accSize5*0.1/count5;
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+thr
				+" "+boxSize
				//+" "+spikeSum
				+" "+PrintUtils.Print2(spikeSumFactor, false)
				+" "+predictionSize
				+" || "
				+" "+count
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(avg, false)
				+" "+PrintUtils.Print2(avgWin, false)
				+" "+PrintUtils.Print2(avgLoss, false)
				+" "+PrintUtils.Print2(pf, false)
				+" || "+PrintUtils.Print2(avg5, false)
				);
	}

	public static void doTradeSpikes(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,int thr,
			int spikeSize,
			int predictionSize,
			int maxExpired,
			int offset,
			int limitSL,
			int limitTP,
			boolean debug
			){
	
		int count = 0;
		int acc = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calj = Calendar.getInstance();
		Calendar calj1 = Calendar.getInstance();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (y<y1 || y>y2) continue;
			if (h<h1 || h>h2) continue;
			
			QuoteShort maxMin = maxMins.get(i);
			int diffHC = q.getHigh5()-q.getClose5();
			int diffCL = q.getClose5()-q.getLow5();
			if (maxMin.getExtra()>=thr){
				if (diffHC<spikeSize*10) continue;
				QuoteShort mm = TradingUtils.getMaxMinShortLimitSLTP(data, i+1, i+predictionSize,limitSL,limitTP, false);
				//QuoteShort mm = TradingUtils.getMaxMinShort(data,calj1, j+0, j+predictionSize);
				//int diff = (q.getHigh5()+offset*10)-mm.getClose5();
				int diff=0;
				if (mm.getExtra()==1) 
					diff = limitTP*10;
				else if (mm.getExtra()==-1) diff = -limitSL*10;
				else if (mm.getExtra()==0){
					continue;
					//diff = (q.getHigh5()+offset*10)-mm.getOpen5();
				}				
				acc += diff;				
				if (diff>=0){
					winPips+=diff;
					wins++;
				}
				else{
					lostPips += -diff;
					losses++;
				}
				//System.out.println("SHORT diff : "+diff);
				count++;
			}else if (maxMin.getExtra()<=-thr){	
				if (diffCL<spikeSize*10) continue;
					QuoteShort mm = TradingUtils.getMaxMinShortLimitSLTP(data, i+1, i+predictionSize,limitSL,limitTP, true);
					//QuoteShort mm = TradingUtils.getMaxMinShort(data,calj1, j+0, j+predictionSize);
					//int diff = mm.getClose5()-(q.getLow5()-offset*10);
					
					int diff=0;
					if (mm.getExtra()==1) 
						diff = limitTP*10;
					else if (mm.getExtra()==-1) diff =-limitSL*10;
					else if (mm.getExtra()==0){
						continue;
						//diff = mm.getOpen5()-(q.getLow5()-offset*10);
					}
					
					acc += diff;
					
					if (diff>=0){
						winPips+=diff;
						wins++;
					}
					else{
						lostPips += -diff;
						losses++;
					}
					//System.out.println("LONG diff : "+diff);
					count++;				
			}
		}
		
		double winPer = wins*100.0/count;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		double avg = acc*0.1/count;
		double pf = winPips*1.0/lostPips;
		System.out.println(
				h1+" "+h2
				+" "+thr
				+" "+spikeSize
				+" "+predictionSize
				+" "+maxExpired
				+" "+offset
				+" "+limitTP
				+" "+limitSL
				+" || "
				+" "+count
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(avg, false)
				+" "+PrintUtils.Print2(avgWin, false)
				+" "+PrintUtils.Print2(avgLoss, false)
				+" "+PrintUtils.Print2(pf, false)
				);
		
	}
	
	public static void main(String[] args) throws Exception {
	
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.04.12.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		
		int limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= null;
		ArrayList<Quote> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="dukasc";
			}				
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			
			ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			
			/*for (int h1=0;h1<=0;h1++){
				int h2 = h1+9;
				for (int thr = 400;thr<=400;thr+=1){
					for (int spikeSize = 0;spikeSize<=0;spikeSize++){
						for (int predictionSize = 8000;predictionSize<=8000;predictionSize+=1){
							for (int maxExpired = 1;maxExpired<=1;maxExpired++){
								for (int offset= 0;offset<=0;offset++){
									for (int limitTP= 10;limitTP<=10;limitTP++){
									//for (int limitSL= 60;limitSL<=60;limitSL+=10){
										//for (int limitTP= limitSL;limitTP<=limitSL;limitTP++){
										for (int limitSL= (int) (3.0*limitTP);limitSL<=3.0*limitTP;limitSL+=limitTP){
											for (int year=2003;year<=2016;year++)
												CandleShapes.doTradeSpikes(data5mS, maxMins,year,year, h1, h2, thr, spikeSize, predictionSize, maxExpired,offset,limitSL,limitTP,false);
										}
									}
								}
							}
						}
					}					
				}
			}*/
			
			
			String hours ="0 1 2 3 4 5 6 7 8 9 23";
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+6;
				for (int thr = 400;thr<=400;thr+=100){
					for (int boxSize=1;boxSize<=10;boxSize++){//buena configuracion 48-22
						//for (int  spikeBoxesSize = 30;spikeBoxesSize<=30;spikeBoxesSize+=1){
						for (double  factor = 1;factor<=10;factor+=1.0){
							for (double slFactor = 5.5;slFactor<=5.5;slFactor+=0.5){
								for (int predictionSize = 290;predictionSize<=290;predictionSize+=10){								
									for (int y1=2003;y1<=2003;y1+=1){
										int y2 = y1+13;
										//CandleShapes.doTradeSpikeBoxes(data5mS, maxMins,y1,y2, h1, h2, thr, boxSize,spikeBoxesSize,predictionSize);								
										//CandleShapes.doTradeSpikeBoxes(data5mS, maxMins,y1,y2, h1, h2, thr, boxSize,factor,predictionSize);
										double comm = 2.0;
										for (int maxTrades=40;maxTrades<=40;maxTrades++){
											CandleShapes.doTradeSpikeBoxesSL(data5mS, maxMins,y1,y2,hours, thr, boxSize,factor,predictionSize,slFactor,maxTrades,comm,false);
										}
									}
								}
							}
						} //spikeBoxesSize		
					}//boxSize
				}
			}
		}

	}

}
