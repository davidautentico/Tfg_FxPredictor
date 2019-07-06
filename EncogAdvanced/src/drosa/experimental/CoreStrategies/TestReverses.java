package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestReverses {
	
	
	
	public static void testTrading(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int dayWeek1,int dayWeek2,
			HashMap<Integer,StrategyConfig> config,
			int minPips,
			int maxLots,
			int minTarget,
			int iMax,
			double comm,boolean debug){
		
		ArrayList<Integer> adverses = new ArrayList<Integer>();
	
		
		int avgDiff = 0;
		int avgDiffLots = 0;
		int dayTrades = 0;
		double totalWins$ = 0;
		double totalLosses$ = 0;
		int wins = 0;
		int losses = 0;
		int avgEntry = -1;
		int lastDay = -1;
		int lots = 0;
		int entryMode = 0;
		int dayDirection = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		double actualComm = 0;
		for (int i=1; i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			
			int dayW = cal.get(Calendar.DAY_OF_WEEK);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				//evaluacion
				if (lots>0){
					double profit = 0;
					int diff = 0;
					if (entryMode == 1){
						diff = q.getOpen5() - avgEntry;
						profit = (q.getOpen5() - avgEntry)*0.1*10*lots;
					}else if (entryMode == -1){
						diff = avgEntry - q.getOpen5();
						profit = (avgEntry - q.getOpen5())*0.1*10*lots;
					}
					
					profit -= actualComm;
					
					if (profit<=-15000){
						losses++;
						totalLosses$ += -profit;
						if (debug)
							if (profit>=1000) System.out.println("[LOSS GREAT] "+DateUtils.datePrint(cal)+" || "+lots+" || "+profit);
					}else if (profit>=0){
						wins++;
						totalWins$ += profit;
						if (debug)
							if (profit>=1000) System.out.println("[WIN GREAT] "+DateUtils.datePrint(cal)+" || "+lots+" || "+profit);
					}else{						
						
						boolean isLost = true;
						int index = i+iMax;
						if (index>=data.size()-10){
							index = data.size()-10;
						}
						TradingUtils.getMaxMinShort(data, qm, calqm, i, index);
						if (entryMode==1){
							diff = data.get(index).getClose5()-avgEntry;
							if (qm.getHigh5()>=avgEntry){
								if (debug)
									System.out.println("[SOLVED] "+qm.getHigh5()+" "+PrintUtils.Print2dec(diff*0.1,false)+" "+lots+" "+PrintUtils.Print2dec(profit, false));
								isLost = false;
							}else{
								//if (debug)
									//System.out.println("[LOSS] "+PrintUtils.Print2dec(diff*0.1,false)+" "+lots+" "+PrintUtils.Print2dec(profit, false));							
							}
						}else if (entryMode==-1){
							diff = avgEntry-data.get(index).getClose5();
							if (qm.getLow5()<=avgEntry){
								if (debug)
									System.out.println("[SOLVED] "+qm.getLow5()+" "+PrintUtils.Print2dec(diff*0.1,false)+" "+lots+" "+PrintUtils.Print2dec(profit, false));
								isLost = false;
							}else{
								//if (debug)
									//System.out.println("[LOSS] "+PrintUtils.Print2dec(diff*0.1,false)+" "+lots+" "+PrintUtils.Print2dec(profit, false));								
							}
						}
						
						if (isLost){
							if (debug)
								System.out.println("[LOSS] "+PrintUtils.Print2dec(diff*0.1,false)+" "+lots+" "+PrintUtils.Print2dec(profit, false));
							losses++;
							totalLosses$ += -profit;
							avgDiff += diff; 						
							avgDiffLots +=lots;
						}
							
						//System.out.println("[LOSS] "+PrintUtils.Print2dec(diff*0.1,false)+" "+lots+" "+PrintUtils.Print2dec(profit, false));
						//
					}
				}
				
				actualComm = 0;
				dayTrades = 0;
				entryMode = 0;
				dayDirection = 0;
				lots = 0;
				avgEntry = 0;
				lastDay = day;
			}
			
	
			if (lots<maxLots){
				if (lots==0){
					if (config.containsKey(h)
							&& config.get(h).isEnabled()
							//&& dayTrades<1
							&& dayW>=dayWeek1 && dayW<=dayWeek2
							//&& maxBarSize<=900
							){			
						int maxMin = maxMins.get(i-1);	
						
						//evaluar entrada de nuevas operaciones
						StrategyConfig sc = config.get(h);
						int thr = sc.getThr();
						if (maxMin>=thr
								&& entryMode>=0
								){
							dayDirection = 1;
							entryMode = 1;
							avgEntry = (int) ((avgEntry*lots + 1*q.getOpen5())*1.0/(lots+1));
							lots += 1;
							actualComm += 5.23*2;
							if (debug){
								System.out.println("[LONG 1] "+avgEntry+" || "+lots);
							}
						}else if (maxMin<=-thr
								&& entryMode<=0
								){
							dayDirection = -1;
							entryMode = -1;
							avgEntry = (int) ((avgEntry*lots + 1*q.getOpen5())*1.0/(lots+1));
							lots += 1;
							actualComm += 5.23*2;
							if (debug){
								System.out.println("[SHORT 1] "+avgEntry+" || "+lots);
							}
						}
					}else{
						if (dayDirection==1
								){
							entryMode = 1;
							avgEntry = (int) ((avgEntry*lots + 1*q.getOpen5())*1.0/(lots+1));
							lots += 1;
							actualComm += 5.23*2;
							if (debug){
								System.out.println("[LONG 1s] "+avgEntry+" || "+lots);
							}
						}else if (dayDirection==-1
								){
							entryMode = -1;
							avgEntry = (int) ((avgEntry*lots + 1*q.getOpen5())*1.0/(lots+1));
							lots += 1;
							actualComm += 5.23*2;
							if (debug){
								System.out.println("[SHORT 1s] "+avgEntry+" || "+lots);
							}
						}
					}
				}else if (lots>0){
					if (entryMode>=0
							&& q.getOpen5()<=avgEntry-minPips*10
							){
						entryMode = 1;
						avgEntry = (int) ((avgEntry*lots + 1*q.getOpen5())*1.0/(lots+1));
						lots += 1;
						actualComm += 5.23*2;
						if (debug){
							System.out.println("[LONG ADDING] "+avgEntry+" || "+lots);
						}
					}else if (entryMode<=0
							&& q.getOpen5()>=avgEntry+minPips*10
							){
						entryMode = -1;
						avgEntry = (int) ((avgEntry*lots + 1*q.getOpen5())*1.0/(lots+1));
						lots += 1;
						actualComm += 5.23*2;
						if (debug){
							System.out.println("[SHORT ADDING "+avgEntry+" || "+lots);
						}
					}
				}
			}
			
			//evaluacion
			if (lots>0){
				double profit = 0;
				if (entryMode == 1){
					profit = (q.getClose5() - avgEntry)*0.1*10*lots;
				}else if (entryMode == -1){
					profit = (avgEntry - q.getClose5())*0.1*10*lots;
				}
				
				profit -= actualComm;
				
				if (profit>=minTarget){
					wins++;
					totalWins$ += profit;
					//totalWins$ += minTarget;
					
					if (debug){
						System.out.println("[WIN] "+avgEntry+" "+q.getClose5()+" || "+lots+" "+profit+" || "+totalWins$);
					}
					
					
					entryMode = 0;
					lots = 0;
					avgEntry = 0;
				}else if (profit<=-15000){
					if (debug){
						System.out.println("[LOSS 15k] "+avgEntry+" "+q.getClose5()+" || "+lots);
					}
					losses++;
					totalLosses$ += -profit;
					//avgDiff += diff; 						
					//avgDiffLots +=lots;
					
					entryMode = 0;
					lots = 0;
					avgEntry = 0;
				}
			}
			
			
		}
		
		int trades = wins+losses;
		double pf = Math.abs(totalWins$*1.0/totalLosses$);
		double winPer = wins*100.0/(wins+losses);
		double totalProfit$ = totalWins$-totalLosses$;
		
		System.out.println(
				header+" "+minPips+" "+maxLots+" "+minTarget+" "+iMax
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)+" ("+losses+")"
				+" "+PrintUtils.Print2dec(totalProfit$, false)
				+" ["+PrintUtils.Print2dec(totalWins$, false)+" "+PrintUtils.Print2dec(totalLosses$, false)+"]"
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avgDiff*0.1/losses, false)
				+" "+PrintUtils.Print2dec(avgDiffLots*1.0/losses, false)
				);
		
		
	}
	
	public static void test(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int dayWeek1,int dayWeek2,
			HashMap<Integer,StrategyConfig> config,
			int minPips,
			int mode,
			double comm,boolean debug){
		
		ArrayList<Integer> adverses = new ArrayList<Integer>();
		
		int lastDay = -1;
		int dayTrades = 0;
		int entryMode = 0;
		int entryValue = -1;
		int maxAdverse = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=1; i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			
			int dayW = cal.get(Calendar.DAY_OF_WEEK);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				if (dayTrades >= 1){
					if (maxAdverse==-1){
						adverses.add(0);
					}else{						
						if (entryMode==1){
							int diffPips = entryValue-maxAdverse;
							adverses.add(diffPips);
						}else if (entryMode==-1){
							int diffPips = maxAdverse-entryValue;
							adverses.add(diffPips);
						}
					}
				}
				
				dayTrades = 0;
				entryMode = 0;
				entryValue = -1;
				maxAdverse = -1;
				lastDay = day;
			}
			
	
			if (dayTrades==0){
				if (config.containsKey(h)
						&& config.get(h).isEnabled()
						//&& dayTrades<1
						&& dayW>=dayWeek1 && dayW<=dayWeek2
						//&& maxBarSize<=900
						){			
					int maxMin = maxMins.get(i-1);	
					
					//evaluar entrada de nuevas operaciones
					StrategyConfig sc = config.get(h);
					int thr = sc.getThr();
					int tp = sc.getTp();
					int sl = sc.getSl();
					PositionCore pos = null;
					if (maxMin>=thr
							){
						entryMode = 1;
						entryValue = q.getOpen5();
						dayTrades++;
					}else if (maxMin<=-thr
							){
						entryMode = -1;
						entryValue = q.getOpen5();						
						dayTrades++;
					}
				}
			}
			
			if (entryMode == 1){//long
				if (maxAdverse==-1 || q.getLow5()<=maxAdverse){
					maxAdverse = q.getLow5();
				}
			}else if (entryMode == -1){//short
				if (maxAdverse==-1 || q.getHigh5()>=maxAdverse){
					maxAdverse = q.getHigh5();
				}
			}
			
						
			
		}
		
		
		
		int trades = adverses.size();
		int may100 = 0;
		int acc = 0;
		for (int i=0;i<adverses.size();i++){
			acc += adverses.get(i);
			if (adverses.get(i)>=1000) may100++;
		}		
		double avg = (acc)*0.1/trades;
		
		
		System.out.println(
				header
				+" "+y1+" "+y2
				//+" "+minPips
				+" || "
				+" "+PrintUtils.Print2Int(trades, 5)						
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+may100+" "+PrintUtils.Print2dec(may100*100.0/adverses.size(), false)
				);
		
		
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\gbpUSD_UTC_5 Mins_Bid_2003.05.04_2016.10.04.csv";
		//String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_1 Min_Bid_2003.05.04_2016.09.20.csv";
		//String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			HashMap<Integer,StrategyConfig> config = new HashMap<Integer,StrategyConfig>();
			
			for (int s=0;s<=23;s++){
				StrategyConfig sc = new StrategyConfig();
				sc.setHour(s);
				sc.setEnabled(false);
				sc.setThr(500);
				sc.setTp(12);
				sc.setSl(36);
				config.put(s, sc);
			}
			//0: 60,10,50
			//1: 130,10,50
			//2: 420,14,28
			//3: 450,15,45
			//4: 700,15,30
			//5: 450,15,45
			//6: 200,15,45
			//7: 300,15,30
			//8: 550,15,45
			//9: 500,12,36
			//22:60,17,119
			//23: 80,10,60
			StrategyConfig sc = null;
			for (int y1=2009;y1<=2009;y1+=1){
				int y2 = y1+7;
				for (int h1=23;h1<=23;h1++){
					sc = config.get(h1);
					sc.setEnabled(true);
					for (int h3=99;h3<=99;h3++){
						for (int thr= 1;thr<= 1;thr+= 1){
							sc.setParams(h1, thr, 0,0, true);			
							String header = "[config] "+PrintUtils.Print2Int(thr, 4)+" "+PrintUtils.Print2Int(0, 3)+" "+PrintUtils.Print2Int(0, 3);
							for (int dayWeek1 = Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
								int dayWeek2 = dayWeek1+4;
								//TestReverses.test(header,data, maxMins,y1,y2,dayWeek1,dayWeek2, config,0,-1,0.0,false);
								for (int minAdverse=30;minAdverse<=30;minAdverse+=10){
									for (int minPositive=12;minPositive<=12;minPositive++){
										for (int nbars=1*24*12;nbars<=1*24*12;nbars+=24*12){
											for (int minPips=1;minPips<=1;minPips++){
												for (int maxLots=10;maxLots<=10;maxLots++){
													for (int minTarget=1;minTarget<=500;minTarget+=1){
														for (int iMax=60*24;iMax<=60*24;iMax+=60){
															TestReverses.testTrading(header,data, maxMins,y1,y2,dayWeek1,dayWeek2, config,minPips,maxLots,minTarget,iMax,0.0,false);
														}
													}
												}												
											}
											//TestReverses.test2(header,data, maxMins,y1,y2,dayWeek1,dayWeek2, config,minAdverse,minPositive,nbars,0.0,false);
										}
									}
								}
								
							}
											
						}
					}					
					sc = config.get(h1);sc.setEnabled(false);
				}
			}//years
		}

	}

}
