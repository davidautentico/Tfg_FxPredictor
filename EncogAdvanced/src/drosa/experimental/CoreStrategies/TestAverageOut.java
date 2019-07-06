package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestAverageOut {
	
	public static void test(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int dayWeek1,int dayWeek2,
			HashMap<Integer,StrategyConfig> config,
			int minPips,
			int maxLots,
			int minTarget,int maxLoss,
			double factorSize,
			int lotSizeH1,
			int lotSizeH2,
			int testDiff,
			int maxTrades,
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
		double lots = 0;
		int entryMode = 0;
		int dayDirection = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		double actualComm = 0;
		double dayLotSize = factorSize;
		boolean test = true;
		double avgD = 0.0;
		int totlaavgD = 0;
		int total10 = 0;
		int actualTrades = 0;
		boolean canTrade =true;
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
				
				//reinicio
				
				lots= 0;
				entryMode = 0;
				test = true;
				 dayLotSize = factorSize;
				lastDay = day;
				canTrade=true;
			}
			
			
			if (lots==0 && canTrade){
				if (config.containsKey(h)
						&& config.get(h).isEnabled()
						//&& dayTrades<1
						&& dayW>=dayWeek1 && dayW<=dayWeek2
						){
					StrategyConfig sc = config.get(h);
					int thr = sc.getThr();
					int maxMin = maxMins.get(i-1);
					if (maxMin>=thr
							&& entryMode>=0
							){
						dayDirection = 1;
						entryMode = 1;
						avgEntry = q.getOpen5();
						lots = 1;
						actualComm += 5.23*2;
						actualTrades++;
						if (debug){
							System.out.println("[LONG 1] "+avgEntry+" || "+lots);
						}
						canTrade = false;
					}else if (maxMin<=-thr
							&& entryMode<=0
							){
						dayDirection = -1;
						entryMode = -1;
						avgEntry = q.getOpen5();
						lots = 1;
						actualComm += 5.23*2;
						actualTrades++;
						if (debug){
							System.out.println("[SHORT 1] "+avgEntry+" || "+lots);
						}
						canTrade = false;
					}
				}
			}else if (lots>0){
				double lotSize=1;
				if (h>=lotSizeH1 && h<=lotSizeH2){
					
					int diff = 0;
					if (entryMode==1){
						diff = q.getOpen5()-avgEntry;
					}else if (entryMode==-1){
						diff = avgEntry-q.getOpen5();
					}
					
					if (diff!=0 && test){
						//System.out.println("[DIFF] entryMode : "+entryMode+" || "+diff*0.1);
						test = false;
						
						if (diff*0.1<=-testDiff){
							int diff2 = 0;
							TradingUtils.getMaxMinShort(data, qm, calqm, i, i+12*24);
							if (entryMode==1){
								diff2 = qm.getHigh5()-q.getOpen5();
							}else if (entryMode==-1){
								diff2 = q.getOpen5()-qm.getLow5();
							}
							
							//System.out.println(diff*0.1+" || "+diff2*0.1+" || "+(diff2-diff)*0.1);
							avgD +=(diff2-diff)*0.1;
							totlaavgD++;
							/*if ((diff2-diff)*0.1>=10){
								total10++;
							}*/
							if (diff2*0.1>=10){
								total10++;
							}
						}
					}
					
					lotSize = dayLotSize*lots;
					if (lotSize>0 && debug){
						//System.out.println("[x10] "+lotSize);
					}
					dayLotSize = 0;
					lotSize = 1;
				}
				//if (h>=0 && h<=3) lotSize = factorSize*lots;

				if (actualTrades<maxTrades && canTrade){
					if (entryMode>=0
							&& q.getOpen5()<=avgEntry-minPips*10
							){
						entryMode = 1;
						avgEntry = (int) ((avgEntry*lots + lotSize*q.getOpen5())*1.0/(lots+lotSize));
						lots += lotSize;
						actualComm += 5.23*2;
						actualTrades++;
						if (debug){
							//System.out.println("[LONG ADDING] "+avgEntry+" || "+lots);
						}
					
						
					}else if (entryMode<=0
							&& q.getOpen5()>=avgEntry+minPips*10
							){
						entryMode = -1;
						avgEntry = (int) ((avgEntry*lots + lotSize*q.getOpen5())*1.0/(lots+lotSize));
						lots += lotSize;
						actualComm += 5.23*2;
						actualTrades++;
						if (debug){
							//System.out.println("[SHORT ADDING "+avgEntry+" || "+lots);
						}
					
					}
				}
				
				 
			}
			
			//evaluacion
			if (lots>0){
				int diff = 0;
				if (entryMode==1){
					diff = q.getOpen5()-avgEntry;
				}else if (entryMode==-1){
					diff = avgEntry-q.getOpen5();
				}
				
				double profit = 0;
				if (entryMode == 1){
					profit = (q.getClose5() - avgEntry)*0.1*10*lots;
				}else if (entryMode == -1){
					profit = (avgEntry - q.getClose5())*0.1*10*lots;
				}
				
				//profit -= actualComm;
				
				if (profit>=minTarget){
					wins++;
					totalWins$ += profit;
					//totalWins$ += minTarget;
					
					if (debug){
						System.out.println("[WIN] "+avgEntry+" "+q.getClose5()+" || "+lots+" "+profit+" || "+totalWins$);
					}
					
					actualTrades= 0;
					entryMode = 0;
					lots = 0;
					avgEntry = 0;
				}else if (profit<=-99999999999.0){
					if (debug){
						System.out.println("[LOSS] "+avgEntry+" "+q.getClose5()+" || "+lots+" "+profit);
					}
					losses++;
					totalLosses$ += -profit;
					//avgDiff += diff; 						
					//avgDiffLots +=lots;
					actualTrades= 0;
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
		double gain = avgD*1.0/totlaavgD;
		double gain10 = total10*100.0/totlaavgD;
		
		System.out.println(
				header+" "+minPips+" "+maxLots+" "+minTarget
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)+" ("+losses+")"
				+" "+PrintUtils.Print2dec(totalProfit$, false)
				+" ["+PrintUtils.Print2dec(totalWins$, false)+" "+PrintUtils.Print2dec(totalLosses$, false)+"]"
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avgDiff*0.1/losses, false)
				+" "+PrintUtils.Print2dec(avgDiffLots*1.0/losses, false)
				+" || "+totlaavgD+" "+PrintUtils.Print2dec(gain, false)+" "+PrintUtils.Print2dec(gain10, false)
				);
		
		
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.08.03.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
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
				for (int h1=16;h1<=16;h1++){
					sc = config.get(h1);
					sc.setEnabled(true);
					for (int h3=99;h3<=99;h3++){
						for (int thr= 1;thr<= 1;thr+= 12){
							for (int nbars = 36;nbars<= 36;nbars+=1){
								for (int tp=10;tp<=10;tp++){
									for (int sl=(int) (9*tp);sl<=9*tp;sl+=tp){	
										sc.setParams(h1, thr, tp, sl, true);			
										String header = "[config] "+PrintUtils.Print2Int(thr, 4)+" "+PrintUtils.Print2Int(tp, 3)+" "+PrintUtils.Print2Int(sl, 3);
										for (int minPips=10;minPips<=10;minPips+=2){
											for (int dayWeek1 = Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
												int dayWeek2 = dayWeek1+4;
												for (int maxLoss=99999;maxLoss<=99999;maxLoss+=10){
													for (double factorSize=2.0;factorSize<=2.0;factorSize+=0.1){
														for (int hl1=23;hl1<=23;hl1++){
															int hl2=hl1;
															for (int testDiff=100;testDiff<=100;testDiff+=10){
																for (int maxTrades=1;maxTrades<=1;maxTrades++){
																	TestAverageOut.test(header, data, maxMins, y1, y2, dayWeek1, dayWeek2, config, minPips, 0,99999,maxLoss,
																			factorSize,hl1,hl2,testDiff,maxTrades, 0.0, false);
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
						}
					}
					sc = config.get(h1);sc.setEnabled(false);
				}
			}//years
		}

	}
}
