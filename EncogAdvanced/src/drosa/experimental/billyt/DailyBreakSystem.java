package drosa.experimental.billyt;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DailyBreakSystem {
	
	public static void test(ArrayList<QuoteShort> data,
			int y1,int y2,int h1,int h2,
			int tp,int sl,
			double balance,
			double targetPercent,
			double comm,
			int debug
			){
		
		double initialBalance = balance;
		double extraNeeded = 0;
		
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		for (int i=0;i<=20;i++) streaks.add(0);
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualLow = -1;
		int actualHigh = -1;
		int order = 0;
		int minIdx = 0;
		int mode = 0;
		boolean canTrade = false;
		int entry = 0;
		int tpOriginal = tp;
		
		double totalProfit = 0;
		double totalLoss = 0;
		double totalCommissions = 0.0;
		double actualProfit = 0;
		double actualTarget = 0;
		double actualRisk = 0;
		int miniLots = 0;//1 minilot=0.1
		int maxMiniLots = 0;
		int actualTrades = 0;
		
		double maxBalance = initialBalance;
		double maxDD = 0.0;
		
		int totalTrades = 0;
		int wins = 0;
		int losses = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		
		double commLot$$ = 0.08;
		
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){	
				lastHigh = actualHigh;
				lastLow = actualLow;
				actualHigh = -1;
				actualLow = -1;
				order = 0;
				lastDay = day;
				minIdx = i;
				canTrade = true;
				//System.out.println("[DAY] "+DateUtils.datePrint(cal1)+" || "+lastHigh+" "+lastLow+" || "+q1.toString());
			}
		
			//System.out.println(q.toString());
			int diffH = q.getHigh5()-lastHigh;
			int diffL = lastLow-q.getLow5();
			
			if (canTrade){
				//no hay operaciones
				if (mode==0
						&& (h>0 || (h==0 && min>=15))
						){
					boolean isTrade = false;
					if (lastHigh!=-1
							&& q.getOpen5()<=lastHigh
							&& diffH>=0
							){
						mode = 1;	
						entry = lastHigh;	
						isTrade = true;
						
					}else if (lastLow!=-1
							&& q.getOpen5()>=lastLow
							&& diffL>=0
							){
						mode = -1;
						entry = lastLow;
						isTrade = true;
						//if (debug==1)
							//System.out.println("L ENTRY: "+lastLow+" || "+q.toString());
					}
					
					if (isTrade){
						
						//target actual a conseguir
						if (actualTrades==0){
							actualTarget = initialBalance*targetPercent/100.0;
						}else{
							//el actualTarget no cambia
						}
						//max MiniLots
						double margin1Lot = 100000.0/400.0;//margen por 1 lot = 10$/pip
						maxMiniLots = (int) ((initialBalance*1.0/margin1Lot)*100);//no arriego todo el balance..
						//maxMiniLots = (int) (initialBalance*0.8/(sl*0.1));//maxima perdida en 80% del balance
						//lotSize 1 minitLot = 0.1$
						miniLots = (int) (actualTarget/(tp*0.1-commLot$$));
						//System.out.println("MINILOTS: "+PrintUtils.Print2dec(actualTarget/(tp*0.1-0.08),false)+" || "+miniLots+" "+maxMiniLots);
						if (miniLots>=maxMiniLots){
							miniLots = maxMiniLots;
						}
						
						/*if (actualTrades==3){
							miniLots = maxMiniLots;
						}*/
						
						//actualRisk
						actualRisk = miniLots*0.1*sl;
						if (actualRisk>=initialBalance){
							miniLots = (int) (initialBalance/(0.1*sl));
						}
						
						
						
						
						//inicializamos profit						

						actualTrades++;
						
						if (debug==1 || debug==3)
							System.out.println("ENTRY: "+mode+" || "+entry+" || "+q.toString()
								+" || "+PrintUtils.Print2dec(initialBalance, false)
								+" "+PrintUtils.Print2dec(actualTarget, false)
								+" "+miniLots+" "+maxMiniLots
							);
					}
				}
			}
			
			int winResult = 0;
			if (mode==1){
				int diffL0 = entry-q.getLow5();
				if (diffH>=tp*10){										
					//if (debug==1)
						//System.out.println("H WIN: "+entry+" || "+q.toString());					
					canTrade = false;
					winResult = 1;
					mode = 0;
				}else if (diffL0>=sl*10){					
					//if (debug==1)
						//System.out.println("[H LOSS] "+entry+" || "+q.toString());	
					winResult = -1;
					mode = 0;
				}
			}
								
			if (mode==-1){
				int diffH0 = q.getHigh5()-entry;
				if (diffL>=tp*10){					
					//if (debug==1)
					//System.out.println("L WIN: "+entry+" || "+q.toString());					
					canTrade = false;
					winResult = 1;
					mode = 0;
				}else if (diffH0>=sl*10){					
					//if (debug==1)
						//System.out.println("[L LOSS] "+entry+" || "+q.toString());	
					winResult = -1;
					mode = 0;
				}
			}
									
			//evaluamos si se ha conseguido el objetivo
			if (winResult==1){
				double comm$$ = miniLots*commLot$$;
				
				actualTarget 		-= (tp)*miniLots*0.1; 
				totalProfit 		+= (tp)*miniLots*0.1;
				initialBalance 		+= (tp)*miniLots*0.1-comm$$;
				
				totalCommissions += comm$$;
				
				if (actualLosses>=1){
					int tot = streaks.get(actualLosses);
					streaks.set(actualLosses, tot+1);
				}
				actualLosses = 0;
				
				if (initialBalance>=maxBalance){
					maxBalance = initialBalance;
				}
				
				if (debug==1 || debug==3){
					System.out.println("WIN: "+entry+" || "+q.toString()
						+" || "+PrintUtils.Print2dec(initialBalance, false)
						+" "+PrintUtils.Print2dec(actualTarget, false)
						+" || "+PrintUtils.Print2dec(comm$$, false)+" "+PrintUtils.Print2dec(totalCommissions, false)
					);	
				}
				
				//if (actualTarget<=0.001){
					//reinicio de operaciones
					actualTrades = 0;
					//no se opera hasta el proximo dia
					canTrade = false;
					tp = tpOriginal;
					actualTarget = 0;
					mode = 0;
					wins++;
					totalTrades++;
					if (debug==1){
						System.out.println("[REINICIO WIN]: "+q.toString()
							+" || "+PrintUtils.Print2dec(initialBalance, false)
							+" "+PrintUtils.Print2dec(actualTarget, false)
							+" || "+PrintUtils.Print2dec(maxBalance, false)
							+" "+PrintUtils.Print2dec(maxDD, false)
						);	
					}
				//}
					
					if (debug==2){
						System.out.println("1");
					}
			}else if  (winResult==-1){
				
				double comm$$ = miniLots*commLot$$;
				//el target aumenta al perder..
				actualTarget		+= (sl)*miniLots*0.1+comm$$; 
				totalLoss 			+= (sl)*miniLots*0.1;	
				initialBalance 		+= -(sl)*miniLots*0.1-comm$$;
				
				totalCommissions += comm$$;
				
				canTrade = true;
				mode = 0;	
				losses++;
				totalTrades++;
				actualLosses++;
				
				//PRUEBA INCREMENTAMOS EL TP
				//tp = tp+5;
				
				if (actualLosses>=maxLosses) maxLosses = actualLosses;
				double actualDD = 100.0-initialBalance*100.0/maxBalance;
				if (actualDD>=maxDD) maxDD = actualDD;
				
				if (debug==1 || debug==3){
					System.out.println("<<LOSS>>: "+entry+" || "+q.toString()
						+" || "+PrintUtils.Print2dec(initialBalance, false)
						+" "+PrintUtils.Print2dec(actualTarget, false)
						+" || "+PrintUtils.Print2dec(actualDD, false)
						+" "+PrintUtils.Print2dec(maxDD, false)
						+" || "+PrintUtils.Print2dec(comm$$, false)+" "+PrintUtils.Print2dec(totalCommissions, false)+" || "+actualLosses
					);	
				}
				
				if (debug==2){
					System.out.println("-1");
				}
				if (actualTarget>initialBalance){
					//reinicio de operaciones
					actualTrades = 0;
					//no se opera hasta el proximo dia
					canTrade = false;
					tp = tpOriginal;
					actualTarget = 0;
					mode = 0;
				}
				
				if (actualTrades==3){
					tp = tpOriginal*2;
				}
				
				if (initialBalance<=1000){
					initialBalance = balance;
					extraNeeded += balance;
				}
			}
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh) actualHigh = q.getHigh5();
			if (actualLow==-1 || q.getLow5()<=actualLow) actualLow = q.getLow5();
		}
		
		if (actualLosses>=1){
			int tot = streaks.get(actualLosses);
			streaks.set(actualLosses, tot+1);
		}
		
		double winPer = wins*100.0/totalTrades;
		double profitPer = initialBalance*100.0/(balance+extraNeeded)-100.0;
		double pf = totalProfit*1.0/totalLoss;
		
		String streakStr="";
		for (int i=1;i<=10;i++){
			streakStr += streaks.get(i)+" ";
		}
		//if (debug==1){
		if (true
				//&& maxDD<80.0
				//&& profitPer>=100.0
				)
			System.out.println(" "
					+" "+y1+" "+y2+" "+tp+" "+sl
					+" "+PrintUtils.Print2dec(targetPercent, false)
				+" || "+maxLosses
				+" || "+totalTrades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(initialBalance, false)
				+" "+PrintUtils.Print2dec(extraNeeded, false)
				+" "+PrintUtils.Print2dec(profitPer, false)
				+" || "+PrintUtils.Print2dec(totalCommissions, false)
				+" "+PrintUtils.Print2dec(maxBalance, false)				
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				
				+" || "+streakStr.trim()
			);	
		//}
	}
	
	public static void testv2(ArrayList<QuoteShort> data,
			int y1,int y2,int h1,int h2,
			int tp,int sl,
			double balance,
			double targetPercent,
			double comm,
			int debug
			){
		
		double initialBalance = balance;
		double extraNeeded = 0;
		
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		for (int i=0;i<=20;i++) streaks.add(0);
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualLow = -1;
		int actualHigh = -1;
		int order = 0;
		int minIdx = 0;
		int mode = 0;
		boolean canTrade = false;
		int entry = 0;
		int tpOriginal = tp;
		
		double totalProfit = 0;
		double totalLoss = 0;
		double totalCommissions = 0.0;
		double actualProfit = 0;
		double actualTarget = 0;
		double actualRisk = 0;
		int miniLots = 0;//1 minilot=0.1
		int maxMiniLots = 0;
		int actualTrades = 0;
		
		double maxBalance = initialBalance;
		double maxDD = 0.0;
		
		int totalTrades = 0;
		int wins = 0;
		int losses = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		
		double commLot$$ = 0.08;

		int acc=0;
		int count = 0;
		int countHits = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){	
				lastHigh = actualHigh;
				lastLow = actualLow;
				actualHigh = -1;
				actualLow = -1;
				order = 0;
				lastDay = day;
				minIdx = i;
				canTrade = true;
				//System.out.println("[DAY] "+DateUtils.datePrint(cal1)+" || "+lastHigh+" "+lastLow+" || "+q1.toString());
			}
		
			//System.out.println(q.toString());
			int diffH = q.getHigh5()-lastHigh;
			int diffL = lastLow-q.getLow5();
			
			
			if (canTrade && h>=h1 && h<=h2){
				if (    mode!=1
						&& q.getOpen5()<lastHigh 
						&& diffH>=0
						){
					
					countHits++;
					if (mode==-1){
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
						int tot = streaks.get(actualLosses);
						streaks.set(actualLosses, tot+1);
						
						acc +=lastHigh-lastLow;
						count++;
						if (debug==1)
							System.out.println("[HIGH HIT (LOW LOSS)] "+lastHigh+" || "+actualLosses);	
						
					}else{
						if (debug==1)
							System.out.println("[HIGH HIT] "+lastHigh);
						actualLosses = 0;
					}				
					mode = 1;
				}else{
					if (    mode!=-1
							&& q.getOpen5()>lastLow 
							&& diffL>=0
							){
						countHits++;
						if (mode==1){
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							int tot = streaks.get(actualLosses);
							streaks.set(actualLosses, tot+1);
							
							acc +=lastHigh-lastLow;
							count++;
							
							if (debug==1)
								System.out.println("[LOW HIT (HIGH LOSS)] "+lastLow+" || "+actualLosses);
						}else{
							if (debug==1)
								System.out.println("[LOW HIT] "+lastLow);
							actualLosses = 0;
						}
						mode = -1;
					}
				}
			}
			
			if (mode==1){
				if (diffH>=(tp+comm)*10){
					mode = 0;
					canTrade = false;
					if (debug==1)
						System.out.println("[HIGH TP] "+lastHigh);
				}
			}
			
			
			if (mode==-1){
				if (diffL>=(tp+comm)*10){
					mode = 0;
					canTrade = false;
					if (debug==1)
						System.out.println("[LOW TP] "+lastLow);
				}
			}
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh) actualHigh = q.getHigh5();
			if (actualLow==-1 || q.getLow5()<=actualLow) actualLow = q.getLow5();
		}
		
		if (actualLosses>=1){
			int tot = streaks.get(actualLosses);
			streaks.set(actualLosses, tot+1);
		}
		
		String str = "";
		int totalG2 = 0;
		int totalLosses = 0;
		for (int i=1;i<=10;i++){
			str+=streaks.get(i)+" ";
			if (i>=2) totalG2+=streaks.get(i);
			totalLosses += streaks.get(i);
		}
		
		comm = 2.0;
		
		double avg = acc*0.1/count;
		double factor = avg/(tp);
		int totalWins = countHits-totalG2;
		
		double pf = totalWins*1.0/(totalG2*factor*factor);
		
		System.out.println(
				tp+" "+sl+" || "+maxLosses+ " || "+str
				+" || "+countHits+" "+PrintUtils.Print2dec(acc*0.1/count, false)
				+" || "+" "+countHits+" "+totalG2+" "+PrintUtils.Print2dec(pf, false)
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\gbpusd_1 Mins_Bid_2003.01.01_2017.03.14.csv";
		
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
		ArrayList<QuoteShort> dailyData 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			data = dataS;
			
			double balance = 20000;
			double targetPercent = 0.50;
			for (targetPercent=0.20;targetPercent<=0.20;targetPercent+=0.01){
				for (int y1=2009;y1<=2009;y1++){
					int y2 = y1+8;
					for (int h1=0;h1<=0;h1++){
						for (int h2=23;h2<=23;h2++){
							for (int tp=2;tp<=50;tp+=1){
								for (int sl=30;sl<=30;sl+=5){
									for (double comm=2.0;comm<=2.0;comm+=1.0){
										DailyBreakSystem.testv2(data, y1, y2, h1, h2, tp, sl, balance, targetPercent,comm,0);
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
