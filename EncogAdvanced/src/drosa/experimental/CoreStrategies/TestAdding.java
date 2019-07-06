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
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestAdding {
	
	public static double getPositionsPips(ArrayList<PositionCore> positions,QuoteShort q,double comm,boolean debug){
		
		//evalucion entradas actuales
		double actualPips = 0;
		int price = q.getClose5();
		int j = 0;
		while (j<positions.size()){
			PositionCore p = positions.get(j);
			
			if (p.getPositionType()==PositionType.LONG){
				double diff = (price-p.getEntry()-comm*10)*p.getPipValue();
				actualPips += diff;	
				if (debug)
					System.out.println("[LONG] "+p.getEntry()+" || "+q.toString()+" "+diff);
			}else if (p.getPositionType()==PositionType.SHORT){
				double diff = (p.getEntry()-price-comm*10)*p.getPipValue();
				actualPips += diff;	
				if (debug)
					System.out.println("[SHORT] "+p.getEntry()+" || "+q.toString()+" "+diff);
			}
			j++;
		}
		
		
		
		return actualPips;
	}
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,ArrayList<Integer> maxMins,
			int y1,int y2,
			HashMap<Integer,Integer> hours,
			int target,int maxLoss,
			double factor,
			boolean debug){
		
	
		int maxLosses = 0;
		int actualLosses = 0;
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		boolean isFailedDay = false;
		boolean isTradedDay = true;
		int totalFailedDays = 0;
		int totalWinDays = 0;
		int dayTrades = 0;
		int lastHigh = -1;
		int lastLow = -1;
		int actualMax = -1;
		int actualMin = -1;
		double actualPips = 0;
		QuoteShort q1 = data.get(0);
		QuoteShort q = data.get(0);
		for (int i=1; i<data.size();i++){
			q1 = data.get(i-1);
			q = data.get(i);
			
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				if (isTradedDay){
					if (isFailedDay){
						totalFailedDays++;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}else{
						totalWinDays++;
						actualLosses = 0;
					}
				}
				dayTrades = 0;
				isFailedDay = false;
				isTradedDay = false;
				lastDay = day;
				
				lastHigh = actualMax;
				lastLow = actualMin;
				actualMax = -1;
				actualMin = -1;
			}
			
			PositionType actualDirection = PositionType.NONE;
			if (positions.size()>0){
				actualDirection = positions.get(0).positionType;
			}
			
			boolean canTrade = true;
			
			if (hours.containsValue(h)){
				if (h==0 && min<15) canTrade=false;
				if (h==23 && min>=55) canTrade=false;
			}
			
			if (hours.containsKey(h) 
					//&& dayTrades<1
					){			
				int maxMin = maxMins.get(i-1);	
				int diffH = actualMax-q.getOpen5();
				int diffL = q.getOpen5()-actualMin;
				//int diffH = lastHigh-q.getOpen5();
				//int diffL = q.getOpen5()-lastLow;
				//evaluar entrada de nuevas operaciones
				PositionCore pos = null;
				int thr = hours.get(h); 
				double pipValue = positions.size()*(factor);
				if (maxMin>=thr
						//&& diffH<=maxPips*10
						//&& (actualDirection == PositionType.SHORT || actualDirection== PositionType.NONE)
						){
					pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setPipValue(pipValue);
					//pos.setTp(q.getOpen5()-10*tp);
					//pos.setSl(q.getOpen5()+10*sl);
					pos.setPositionType(PositionType.SHORT);
					positions.add(pos);
					if (debug)
						System.out.println("[SHORT] "+q.getOpen5()+" || "+q1.toString());
					dayTrades++;
				}else if (maxMin<=-thr
						//&& diffL<=maxPips*10
						//&& (actualDirection == PositionType.LONG || actualDirection== PositionType.NONE)
						){
					pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setPipValue(pipValue);
					//pos.setTp(q.getOpen5()+10*tp);
					//pos.setSl(q.getOpen5()-10*sl);
					pos.setPositionType(PositionType.LONG);
					positions.add(pos);
					if (debug)
						System.out.println("[LONG] "+q.getOpen5()+" || "+q1.toString());
					dayTrades++;
				}
			}
			
			
			//evalucion entradas actuales
			boolean canEvaluate = true;			
			if (h==0 && min<15) canEvaluate = false;
			if (h==23 && min>55) canEvaluate = false;
			
			if (canEvaluate){
				actualPips = TestAdding.getPositionsPips(positions,q,0.75,false);	
				actualPips += (-10);//spread
				if (positions.size()>0){
					//System.out.println("actualPips : "+positions.size()+" || "+actualPips+" || "+q.toString());
					if (actualPips>=target*10){
						wins++;
						winPips += actualPips;
						positions.clear();
						actualPips = 0;
					}else if (actualPips<-maxLoss*10){
						losses++;
						lostPips += -actualPips;
						if (debug)
							System.out.println("[LOSS] "+q.toString()+" || "+actualPips+" || "+positions.size());
						positions.clear();
						actualPips = 0;
					}
				}else{
					actualPips = 0;
				}
			}
		
			
			if (actualMax== -1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (actualMin== -1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
		}
		//System.out.println("actualPips : "+positions.size()+" || "+actualPips+" || "+q.toString());
		//actualPips = TestAdding.getPositionsPips(positions,q,true);
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double avgW = (winPips)*0.1/wins;
		double avgL = (lostPips)*0.1/losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		int totalTradedDays = totalWinDays + totalFailedDays;
		double winDays = totalWinDays*100.0/(totalTradedDays);
		System.out.println(
				header+" "+
				y1+" "+y2
				//+" "+PrintUtils.Print2Int(h1, 2)+" "+PrintUtils.Print2Int(h2, 2)+" "+PrintUtils.Print2Int(h3, 2)
				+" "+hours.keySet().toString()
				//+" "+thr
				//+" "+tp
				//+" "+sl
				+" "+target
				+" "+maxLoss
				+" || "
				+PrintUtils.Print2Int(trades, 5)+" "+PrintUtils.Print2Int(wins, 5)+" "+PrintUtils.Print2Int(losses, 5)
				+" "+PrintUtils.Print2dec(winPer, false)						
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgW, false)
				+" "+PrintUtils.Print2dec(avgL, false)
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(winDays, false)+" ("+PrintUtils.Print2Int(totalFailedDays,4)+"/"+PrintUtils.Print2Int(totalTradedDays,4)+") "	
				+" "+maxLosses
				+" || "+PrintUtils.Print2dec(actualPips*0.1, false)
				);
		
	}
	
	public static void doTest2(
			String header,
			ArrayList<QuoteShort> data,ArrayList<Integer> maxMins,
			int y1,int y2,
			HashMap<Integer,Integer> hours,
			int target,int maxLoss,int pipsDiff,int maxLots,
			boolean debug){
		
		
		int wins = 0;
		int losses = 0;
		double totalProfit = 0;
		int mode = 0;
		double avgPosition = 0;
		int lots = 0;
		double comm = 0;
		int dayTrades = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();	
		QuoteShort q1 = data.get(0);
		QuoteShort q = data.get(0);
		for (int i=1; i<data.size();i++){
			q1 = data.get(i-1);
			q = data.get(i);
			
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				if (mode!=0){
					double profit = 0;
					if (mode==1){
						int diff = (int) (q.getOpen5()-avgPosition);
						profit = diff*0.1*lots*10;
					}else if (mode==-1){
						int diff = (int) (avgPosition-q.getOpen5());
						profit = diff*0.1*lots*10;
					}
					
					if (profit>=0){						
						wins++;
						totalProfit += profit;
						avgPosition = 0;
						lots = 0;
						dayTrades = 0;
						mode = 0;
					}else {						
						losses++;
						totalProfit += profit;
						avgPosition = 0;
						lots = 0;
						dayTrades = 0;
						mode = 0;
					}
				}
				
				comm = 0;
				mode = 0;
				avgPosition = 0;
				lots = 0;
				dayTrades = 0;
				lastDay = day;				
			}
			
			
			
			boolean canTrade = true;
			
			if (hours.containsValue(h)){
				if (h==0 && min<15) canTrade=false;
				if (h==23 && min>=55) canTrade=false;
			}
			
			if (mode==0){//no hay operaciones
				if (hours.containsKey(h) 
						//&& dayTrades<1
						){			
					int maxMin = maxMins.get(i-1);	
					
					int thr = hours.get(h); 
					if (maxMin>=thr
						){
						if (debug)
							System.out.println("[OPEN LONG] "+q.getOpen5()+" || "+q1.toString());
						
						mode = 1;
						avgPosition = q.getOpen5();
						lots = 1;
						comm = 5.23;
						dayTrades++;
					}else if (maxMin<=-thr
							//&& diffL<=maxPips*10
							//&& (actualDirection == PositionType.LONG || actualDirection== PositionType.NONE)
							){				
						if (debug)
							System.out.println("[OPEN SHORT] "+q.getOpen5()+" || "+q1.toString());
						mode = -1;
						avgPosition = q.getOpen5();
						lots = 1;
						comm = 5.23;
						dayTrades++;
					}
				}
			}else if (mode==1 
					){
				if (h>=23){
					if (lots<2*maxLots){
						if (q.getOpen5()<=avgPosition-pipsDiff*10){//cada 5 pips		
							
							avgPosition = (avgPosition*lots+q.getOpen5()*3*lots)/(lots+3*lots);
							lots+=3*lots;
							comm += 5.23;
							dayTrades++;
							if (debug)
								System.out.println("[ADDING 2*LONG] "+q.toString()+" || "+avgPosition+" "+lots);
						}
					}
				}else{
					if (lots<maxLots){
						if (q.getOpen5()<=avgPosition-pipsDiff*10){//cada 5 pips		
							
							avgPosition = (avgPosition*lots+q.getOpen5()*1)/(lots+1);
							lots++;
							comm += 5.23;
							dayTrades++;
							if (debug)
								System.out.println("[ADDING LONG] "+q.toString()+" || "+avgPosition+" "+lots);
						}
					}
				}
			}else if (mode==-1
					){
				if (h>=23){
					if (lots<2*maxLots){
						if (q.getOpen5()>=avgPosition+pipsDiff*10){//cada 5 pips					
							avgPosition = (avgPosition*lots+q.getOpen5()*3*lots)/(lots+3*lots);
							lots+=3*lots;
							comm += 5.23;
							dayTrades++;
							if (debug)
								System.out.println("[ADDING 2*SHORT] "+q.toString()+" || "+avgPosition+" "+lots);
						}
					}
				}else{
					if (lots<maxLots){
						if (q.getOpen5()>=avgPosition+pipsDiff*10){//cada 5 pips					
							avgPosition = (avgPosition*lots+q.getOpen5()*1)/(lots+1);
							lots++;
							comm += 5.23;
							dayTrades++;
							if (debug)
								System.out.println("[ADDING SHORT] "+q.toString()+" || "+avgPosition+" "+lots);
						}
					}
				}
			}
			
			
			//evalucion entradas actuales
			boolean canEvaluate = true;			
			if (h==0 && min<15) canEvaluate = false;
			if (h==23 && min>55) canEvaluate = false;
			
			if (canEvaluate){				
				if (mode!=0){
					double profit = 0;
					if (mode==1){
						int diff = (int) (q.getClose5()-avgPosition);
						profit = diff*0.1*lots*10;
					}else if (mode==-1){
						int diff = (int) (avgPosition-q.getClose5());
						profit = diff*0.1*lots*10;
					}
					
					profit -= 5.23*lots;//comm
					if (profit>=target){						
						wins++;
						totalProfit += profit;
						avgPosition = 0;
						lots = 0;
						dayTrades = 0;
						comm = 0;
						mode = 0;
					}else if (profit<=-maxLoss){
						if (debug)
							System.out.println("[LOSS] "+q.toString()+" || "+avgPosition+" "+lots+" "+profit);
						losses++;
						totalProfit += profit;
						avgPosition = 0;
						lots = 0;
						dayTrades = 0;
						comm = 0;
						mode = 0;
						
					}
				}
				
				
			}				
		}
		
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		
		System.out.println(
				header
				+" "+target
				+" "+maxLoss
				+" "+pipsDiff
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(totalProfit, false)
				+" "+PrintUtils.Print2dec(winPer, false)
				
				);
	
	}
	
	public static void main(String[] args) throws Exception {
			//String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.07.04.csv";
			String pathEURUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.08.03.csv";
				
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
					
					//22-23 12/60 11*12
					HashMap<Integer,Integer> hours = new HashMap<Integer,Integer>();
					for (int y1=2003;y1<=2003;y1+=1){
						int y2 = y1+13;
						for (int h1=16;h1<=16;h1++){
							int h2=h1+0;
							hours.put(16, 29);							
							for (int h3=99;h3<=99;h3++){
								for (int thr= 1;thr<= 200;thr+= 1){
									String header = String.valueOf(thr);
									hours.clear();
									hours.put(h1, thr);
									for (int maxPips=400;maxPips<=400;maxPips+=1){
										for (int maxLoss=10000;maxLoss<=10000;maxLoss+=1000){
											for (int pipsDiff=10;pipsDiff<=10;pipsDiff++){
												for (int maxLots=10;maxLots<=10;maxLots++){
													TestAdding.doTest2(header,data, maxMins,y1,y2, hours,maxPips,maxLoss,pipsDiff,maxLots,false);
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
