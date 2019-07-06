package drosa.experimental.addin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestEurAdd {
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,			
			int y1,int y2,
			int h1,int h2,
			int thr,
			int target,
			int minDiff,
			int debug
			){
		
		
		
		
		int lastDay = -1;
	
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		
		int wins = 0;
		int mode = 0;
		int lots = 0;
		double avgPrice = 0;
		int lastPrice=0;
		ArrayList<Integer> arrayLots = new ArrayList<Integer>();
		for (int i=0;i<=10;i++) arrayLots.add(0);
		boolean isTested = false;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				isTested = false;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (mode==0 && !isTested){
				if (h>=h1 && h<=h2){
					if (maxMin>=thr){
						isTested = true;
						mode = -1;
						lots = 1;
						avgPrice = q.getOpen5();
						lastPrice =  q.getOpen5();
						if (debug==1){
							System.out.println("[ENTRY SHORT] "+lastPrice
									+" "+PrintUtils.Print2dec(avgPrice, false)
									+" "+lots
									);
						}
					}else if (maxMin<=-thr){
						isTested = true;
						mode = 1;
						lots = 1;
						avgPrice = q.getOpen5();
						lastPrice =  q.getOpen5();
						if (debug==1){
							System.out.println("[ENTRY LONG] "+lastPrice
									+" "+PrintUtils.Print2dec(avgPrice, false)
									+" "+lots
									);
						}
					}
				}				
			}
						
			if (mode==1){//vamos LONG
				double pips = (q.getHigh5()-avgPrice)*0.1;
				if (pips*lots>=target){
					wins++;
					mode = 0;
					
					int idx = lots;
					if (lots>=10) idx=10;
					int tot = arrayLots.get(idx);
					arrayLots.set(idx, tot+1);
					
					if (debug==3
							&& lots>=10
							){
						System.out.println("[WIN LONG] "+lastPrice
								+" "+PrintUtils.Print2dec(avgPrice, false)
								+" "+lots
								+" || "+PrintUtils.Print2dec(pips, false)+" "+PrintUtils.Print2dec(pips*lots, false)+" || "+wins
								);
					}
				}else{//vemos si hay que subir EL LOTE
					int diff = lastPrice-q.getClose5();
					if (diff>=minDiff){
						//añadimos lote
						lastPrice = lastPrice-minDiff;
						int newLots = 1;
						avgPrice = (avgPrice*lots+lastPrice*newLots)*1.0/(lots+newLots);
						lots+=newLots;
						
						
						if (debug==1
								//&& lots>=10
								){
							System.out.println("[ADD LONG] "+lastPrice
									+" "+PrintUtils.Print2dec(avgPrice, false)
									+" "+lots
									+" || "+PrintUtils.Print2dec(pips, false)+" "+PrintUtils.Print2dec(pips*lots, false)
									);
						}
					}					
				}
			}else if (mode==-1){
				double pips = (avgPrice-q.getLow5())*0.1;
				if (pips*lots>=target){
					wins++;
					mode = 0;
					
					int idx = lots;
					if (lots>=10) idx=10;
					int tot = arrayLots.get(idx);
					arrayLots.set(idx, tot+1);
					
					if (debug==3
							&& lots>=11
							){
						System.out.println("[WIN SHORT] "+lastPrice
								+" "+PrintUtils.Print2dec(avgPrice, false)
								+" "+lots
								+" || "+PrintUtils.Print2dec(pips, false)+" "+PrintUtils.Print2dec(pips*lots, false)+" || "+wins
								);
					}
				}else{//vemos si hay que subir EL LOTE
					int diff = q.getClose5()-lastPrice;
					if (diff>=minDiff){
						//añadimos lote
						int newLots = 1;
						lastPrice = lastPrice+minDiff;
						avgPrice = (avgPrice*lots+lastPrice*newLots)*1.0/(lots+newLots);
						lots+=newLots;
						
						if (debug==1
								//&& lots>=10
								){
							System.out.println("[ADD SHORT] "+lastPrice
									+" "+PrintUtils.Print2dec(avgPrice, false)
									+" "+lots
									+" || "+PrintUtils.Print2dec(pips, false)+" "+PrintUtils.Print2dec(pips*lots, false)
									);
						}
					}	
				}
			}//
			
			if (mode!=0 && lots>=10){
				int idx = lots;
				if (lots>=10) idx=10;
				int tot = arrayLots.get(idx);
				arrayLots.set(idx, tot+1);
				lots = 0;
				mode = 0;
			}
			
			
		}
		
		double per10 = arrayLots.get(10)*100.0/wins;
		System.out.println(
				thr+" "+h1+" "+h2
				+" || "+wins
				//+" || "+PrintUtils.getArrayStr(arrayLots,1)
				+" || "+PrintUtils.Print2dec(per10, false)
				);
		
	}
	
	
	public static void doTest2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,			
			int y1,int y2,
			int h1,int h2,
			int thr,
			int maxBars,
			int minPips,
			int exdiff,
			int debug
			){
		
		
		
		
		int lastDay = -1;
	
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		
		int wins = 0;
		int losses = 0;
		int mode = 0;
		int lots = 0;
		double avgPrice = 0;
		int lastPrice=0;
		ArrayList<Integer> arrayLots = new ArrayList<Integer>();
		for (int i=0;i<=10;i++) arrayLots.add(0);
		boolean isTested = false;
		int accDiff = 0;
		int accPos = 0;
		int accNeg = 0;
		ArrayList<Integer> days = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double ma0 =-1;
		double ma1 =-1;
		double ma2 =-1;
		double ma3 =-1;
		double ma4 =-1;
		double ma5 =-1;
		double std0 = -1;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					int range = max-min;
					days.add(range);
					ma0 = MathUtils.average(days, days.size()-14, days.size()-1);
					ma1 = MathUtils.average(days, days.size()-15, days.size()-2);
					ma2 = MathUtils.average(days, days.size()-16, days.size()-3);
					ma3 = MathUtils.average(days, days.size()-17, days.size()-4);
					ma4 = MathUtils.average(days, days.size()-18, days.size()-5);
					ma5 = MathUtils.average(days, days.size()-19, days.size()-6);
					
					std0 = Math.sqrt(MathUtils.variance(days, days.size()-14, days.size()-1));
					max = -1;
					min = -1;
				}
				isTested = false;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			//if (mode==0 && !isTested){
				if (h>=h1 && h<=h2){
					int diffHC = q1.getHigh5()-q1.getClose5();
					int diffCL = q1.getClose5()-q1.getLow5();
					int diffOC = q1.getOpen5()-q1.getClose5();
					
					if (maxMin>=thr
							&& diffHC>=exdiff
							&& (ma0>-1 && q1.getClose5()<ma0-1*std0 //&& ma1<ma0 && ma2<ma1 && ma3<ma2 && ma4<ma3 && ma5<ma4
									)
							//&& diffOC>=20
							){
						
						TradingUtils.getMaxMinShort(data, qm, calqm, i, i+maxBars);						
						int pips = q.getOpen5()-qm.getLow5();
						int diff = (q.getOpen5()-qm.getLow5())-(qm.getHigh5()-q.getOpen5());
						
						accPos += q.getOpen5()-qm.getLow5();
						accNeg += qm.getHigh5()-q.getOpen5();
						if (pips>=minPips){
							wins++;
							accDiff += diff;
						}else{
							losses++;
							accDiff += diff;
						}
					}else if (maxMin<=-thr
							&& diffCL>=exdiff
							&& (ma0>-1 && q1.getClose5()>ma0+1*std0 //&& ma1>ma0 && ma2>ma1 && ma3>ma2 && ma4>ma3 && ma5>ma4
									)
							//&& diffOC<=-20
							){
						TradingUtils.getMaxMinShort(data, qm, calqm, i, i+maxBars);
						int pips = qm.getHigh5()-q.getOpen5();
						int diff = (qm.getHigh5()-q.getOpen5())-(q.getOpen5()-qm.getLow5());
						
						accPos += qm.getHigh5()-q.getOpen5();
						accNeg += q.getOpen5()-qm.getLow5();
						if (pips>=minPips){
							wins++;
							accDiff += diff;
						}else{
							losses++;
							accDiff += diff;
						}
					}
				}				
				
				if (max==-1 || q.getHigh5()>=max) max = q.getHigh5();
				if (min==-1 || q.getLow5()<=min) min = q.getLow5();
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		System.out.println(
				thr+" "+h1+" "+h2+" "+maxBars
				+" || "+total
				//+" || "+PrintUtils.getArrayStr(arrayLots,1)
				+" || "+PrintUtils.Print2dec(winPer, false)+" || "+PrintUtils.Print2dec(accDiff*0.1/total, false)
				+"  || "+PrintUtils.Print2dec(accPos*1.0/accNeg, false)
				);
		
	}
	
	public static void doTest3(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,			
			int y1,int y2,
			int h1,int h2,
			int thr,
			int maxBars,
			int tp,
			int sl,
			int exdiff,
			double stdFactor,
			double aPf,
			double aTrades,
			int comm,
			int debug
			){
		
		
		
		
		int lastDay = -1;
	
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		
		int wins = 0;
		int losses = 0;
		int mode = 0;
		int lots = 0;
		double avgPrice = 0;
		int lastPrice=0;
		ArrayList<Integer> arrayLots = new ArrayList<Integer>();
		for (int i=0;i<=10;i++) arrayLots.add(0);
		boolean isTested = false;
		int accDiff = 0;
		int accPos = 0;
		int accNeg = 0;
		ArrayList<Integer> days = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double ma0 =-1;
		double ma1 =-1;
		double ma2 =-1;
		double ma3 =-1;
		double ma4 =-1;
		double ma5 =-1;
		double ma6 =-1;
		double ma7 =-1;
		double ma8 =-1;
		double std0 = -1;
		double slope = -1;
		double diffMas =-1;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int mins = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					int range = max-min;
					ma0 = MathUtils.average(days, days.size()-14*288, days.size()-1);
					ma1 = MathUtils.average(days, days.size()-15, days.size()-2);
					ma2 = MathUtils.average(days, days.size()-16, days.size()-3);
					ma3 = MathUtils.average(days, days.size()-17, days.size()-4);
					ma4 = MathUtils.average(days, days.size()-18, days.size()-5);
					ma5 = MathUtils.average(days, days.size()-19, days.size()-6);
					ma6 = MathUtils.average(days, days.size()-20, days.size()-7);
					ma7 = MathUtils.average(days, days.size()-21, days.size()-8);
					ma8 = MathUtils.average(days, days.size()-22, days.size()-9);
					
					//slope = MathUtils.calculateSlope2(days,14*288,5);
					
					//diffMas = ma0-ma1;
					
					std0 = Math.sqrt(MathUtils.variance(days, days.size()-14*288, days.size()-1));
					
					max = -1;
					min = -1;
				}
				isTested = false;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			//if (mode==0 && !isTested){
				if (y>=y1 && y<=y2 && h>=h1 && h<=h2 && (h!=0 || mins>=15)){
					int diffHC = q1.getHigh5()-q1.getClose5();
					int diffCL = q1.getClose5()-q1.getLow5();
					int diffOC = q1.getOpen5()-q1.getClose5();
					
					if (maxMin>=thr
							&& diffHC>=exdiff
							&& (true
									&& ma0>-1 
									&& q1.getClose5()<ma0-stdFactor*std0 
									//&& q1.getClose5()>ma0									
								)
							//&& diffOC>=20
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()-tp*10;
						int valueSL = q.getOpen5()+sl*10;						
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+maxBars, valueTP, valueSL, false);						
						int pips = entry-qm.getClose5()-comm;
						if (pips>=0){
							wins++;
							accPos += pips;
						}else{
							losses++;
							accNeg += -pips;
						}
					}else if (maxMin<=-thr
							&& diffCL>=exdiff
							&& (true
									&& ma0>-1 
									&& q1.getClose5()>ma0+stdFactor*std0 
									//&& q1.getClose5()<ma0
									
									)
							//&& diffOC<=-20
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()+tp*10;
						int valueSL = q.getOpen5()-sl*10;						
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+maxBars, valueTP, valueSL, false);						
						int pips = qm.getClose5()-entry-comm;
						if (pips>=0){
							wins++;
							accPos += pips;
						}else{
							losses++;
							accNeg += -pips;
						}
					}
				}				
				
				if (max==-1 || q.getHigh5()>=max) max = q.getHigh5();
				if (min==-1 || q.getLow5()<=min) min = q.getLow5();
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (accPos-accNeg)*0.1/total;
		double pf = accPos*1.0/accNeg;
		
		if (debug==5 || (total>=aTrades && pf>=aPf))
		System.out.println(
				thr+" "+h1+" "+h2+" "+maxBars+" "+tp+" "+sl+" "+PrintUtils.Print2dec(stdFactor, false)
				+" || "+total
				//+" || "+PrintUtils.getArrayStr(arrayLots,1)
				+" || "+PrintUtils.Print2dec(winPer, false)
				+"  || "+PrintUtils.Print2dec(accPos*1.0/accNeg, false)+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	
	
	public static double doTest4(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,			
			int y1,int y2,
			ArrayList<EurAddConfig> configs,
			int ndays,
			double aPf,
			double aTrades,
			int comm,
			int debug,
			AddinStats stats
			){
				
		
		int lastDay = -1;
	
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		
		int wins = 0;
		int losses = 0;
		int mode = 0;
		int lots = 0;
		double avgPrice = 0;
		int lastPrice=0;
		ArrayList<Integer> arrayLots = new ArrayList<Integer>();
		for (int i=0;i<=10;i++) arrayLots.add(0);
		boolean isTested = false;
		int accDiff = 0;
		int accPos = 0;
		int accNeg = 0;
		ArrayList<Integer> days = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double ma0 =-1;
		double ma1 =-1;
		double ma2 =-1;
		double ma3 =-1;
		double ma4 =-1;
		double ma5 =-1;
		double ma6 =-1;
		double ma7 =-1;
		double ma8 =-1;
		double std0 = -1;
		double slope = -1;
		double diffMas =-1;
		int maxLosses = 0;
		int actualLosses = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int mins = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					int range = max-min;
					ma0 = MathUtils.average(days, days.size()-ndays*288, days.size()-1);					
					std0 = Math.sqrt(MathUtils.variance(days, days.size()-ndays*288, days.size()-1));					
					max = -1;
					min = -1;
				}
				isTested = false;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			EurAddConfig config = configs.get(h);
			if (config!=null){
				int thr = config.getThr();
				int tp = config.getTp();
				int sl = config.getSl();
				int maxBars = config.getMaxBars();
				double stdFactor = config.getStdFactor();
				
				if (y>=y1 && y<=y2 && (h!=0 || mins>=15)){
					if (maxMin>=thr
							&& (true
									&& ma0>-1 
									&& q1.getClose5()<ma0-stdFactor*std0 
									//&& q1.getClose5()>ma0									
								)
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()-tp*10;
						int valueSL = q.getOpen5()+sl*10;						
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+maxBars, valueTP, valueSL, false);						
						int pips = entry-qm.getClose5()-comm;
						if (pips>=0){
							wins++;
							accPos += pips;
							actualLosses = 0;
						}else{
							losses++;
							accNeg += -pips;
							
							actualLosses++;
							if (actualLosses>=maxLosses){
								maxLosses = actualLosses;
							}
						}
					}else if (maxMin<=-thr
							&& (true
									&& ma0>-1 
									&& q1.getClose5()>ma0+stdFactor*std0 
									//&& q1.getClose5()<ma0
									
									)
							//&& diffOC<=-20
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()+tp*10;
						int valueSL = q.getOpen5()-sl*10;						
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+maxBars, valueTP, valueSL, false);						
						int pips = qm.getClose5()-entry-comm;
						if (pips>=0){
							wins++;
							accPos += pips;
							actualLosses = 0;
						}else{
							losses++;
							accNeg += -pips;
							
							if (actualLosses>=maxLosses){
								maxLosses = actualLosses;
							}
						}
					}
				}
			}
			
				
			if (max==-1 || q.getHigh5()>=max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<=min) min = q.getLow5();
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (accPos-accNeg)*0.1/total;
		double pf = accPos*1.0/accNeg;
		
		if (debug==5 || (total>=aTrades && pf>=aPf))
		System.out.println(
				header
				+" || "+total
				//+" || "+PrintUtils.getArrayStr(arrayLots,1)
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(accPos*1.0/accNeg, false)+" "+PrintUtils.Print2dec(avg, false)
				+" || "+maxLosses
				);
		
		stats.setPf(pf);
		stats.setTrades(total);
		return pf;
		
	}
	
	
	public static double doTest4$$(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,			
			int y1,int y2,
			ArrayList<EurAddConfig> configs,
			int ndays,
			double aPf,
			double aTrades,
			double balance,
			double risk,
			int comm,
			int debug,
			AddinStats stats
			){
				
		
		int lastDay = -1;
	
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		
		int wins = 0;
		int losses = 0;
		int mode = 0;
		int lots = 0;
		double actualBalance = balance;
		double maxBalance = balance;
		double actualEquitity = balance;
		double maxDD = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Integer> arrayLots = new ArrayList<Integer>();
		for (int i=0;i<=10;i++) arrayLots.add(0);
		int accPos = 0;
		int accNeg = 0;
		ArrayList<Integer> days = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double ma0 =-1;
	
		double std0 = -1;
		int maxLosses = 0;
		int actualLosses = 0;
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int mins = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					int range = max-min;
					ma0 = MathUtils.average(days, days.size()-ndays*288, days.size()-1);					
					std0 = Math.sqrt(MathUtils.variance(days, days.size()-ndays*288, days.size()-1));					
					max = -1;
					min = -1;
					
					if (debug==3){
						System.out.println(DateUtils.datePrint(cal)
								+" || "+PrintUtils.Print2dec(ma0, false)+" "+PrintUtils.Print2dec(std0, false)
								);
					}
				}
				lastDay = day;
			}
			
			//ma0 = MathUtils.average(days, days.size()-ndays*288, days.size()-1);					
			//std0 = Math.sqrt(MathUtils.variance(days, days.size()-ndays*288, days.size()-1));
			
			int maxMin = maxMins.get(i-1);
			
			EurAddConfig config = configs.get(h);
			if (config!=null){
				int thr = config.getThr();
				int tp = config.getTp();
				int sl = config.getSl();
				int maxBars = config.getMaxBars();
				double stdFactor = config.getStdFactor();
				
				if (y>=y1 && y<=y2 && (h!=0 || mins>=15)){
					double risk$$ = actualEquitity*risk/100.0;
					double pipValue = risk$$/sl;//aproximado
					if (maxMin>=thr
							&& (true
									&& ma0>-1 
									&& q1.getClose5()<ma0-stdFactor*std0 
									//&& q1.getClose5()>ma0									
								)
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()-tp*10;
						int valueSL = q.getOpen5()+sl*10;
						
						PositionCore newPos = new PositionCore();
						newPos.setPositionType(PositionType.SHORT);
						newPos.setEntry(entry);
						newPos.setTp(valueTP);
						newPos.setSl(valueSL);
						newPos.setPositionStatus(PositionStatus.OPEN);
						newPos.setMaxIndex(i+maxBars);
						newPos.setPipValue(pipValue);
						positions.add(newPos);	
						if (debug==3){
							System.out.println(DateUtils.datePrint(cal)
									+" || [NEW SHORT] "+entry
									);
						}
					}else if (maxMin<=-thr
							&& (true
									&& ma0>-1 
									&& q1.getClose5()>ma0+stdFactor*std0 
									//&& q1.getClose5()<ma0
									
									)
							//&& diffOC<=-20
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()+tp*10;
						int valueSL = q.getOpen5()-sl*10;						
						PositionCore newPos = new PositionCore();
						newPos.setPositionType(PositionType.LONG);
						newPos.setEntry(entry);
						newPos.setTp(valueTP);
						newPos.setSl(valueSL);
						newPos.setPositionStatus(PositionStatus.OPEN);
						newPos.setMaxIndex(i+maxBars);
						newPos.setPipValue(pipValue);
						positions.add(newPos);	
						if (debug==3){
							System.out.println(DateUtils.datePrint(cal)
									+" || [NEW LONG] "+entry
									);
						}
					}
				}
			}
			
			
			//evaluacion trades			
			int j = 0;		
			actualEquitity = actualBalance;
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				if (pos.getPositionType()==PositionType.SHORT){
					if (i>=pos.getMaxIndex()){						
						isClosed = true;
					}else{
						if (q.getHigh5()>=pos.getSl()){
							isClosed = true;
						}else if (q.getLow5()<=pos.getTp()){
							isClosed = true;
						}
					}
					
					if (isClosed){
						pips = pos.getEntry()-q.getClose5();
					}
				}else if (pos.getPositionType()==PositionType.LONG){
					if (i>=pos.getMaxIndex()){						
						isClosed = true;
					}else{
						if (q.getLow5()<=pos.getSl()){
							isClosed = true;
						}else if (q.getHigh5()>=pos.getTp()){
							isClosed = true;
						}
					}
					if (isClosed){
						pips = q.getClose5()-pos.getEntry();
					}
				}
				
				//actualizacion equitity
				actualEquitity = actualEquitity + (pips-comm)*0.1*pos.getPipValue();
				
				if (isClosed){
					pips-=comm;
					if (pips>=0){
						wins++;
						winPips+=pips;
					}else{
						losses++;
						lostPips+=-pips;
					}
							
					actualBalance += pips*0.1*pos.getPipValue();
						
					if (debug==2){
						System.out.println("[CLOSED] "+DateUtils.datePrint(cal)+" || "+PrintUtils.Print2dec(pips, false)+" "+PrintUtils.Print2dec(actualBalance, false)+" || "+pos.toString());
					}
					//if (actualBalance<=0) break;
						
					if (actualBalance>=maxBalance){
						maxBalance = actualBalance;
					}else{
						double dd = 100.0-actualBalance*100.0/maxBalance;
						if (dd>=maxDD){
								maxDD = dd;
						}
					}						
					positions.remove(j);
				}else{
					j++;
				}
			}
				
			if (max==-1 || q.getHigh5()>=max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<=min) min = q.getLow5();
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = accPos*1.0/accNeg;
		
		
		if (debug==5 || (total>=aTrades && pf>=aPf))
		System.out.println(
				header
				+" || "+total
				//+" || "+PrintUtils.getArrayStr(arrayLots,1)
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)+" "+PrintUtils.Print2dec(avg, false)
				+" || "+maxLosses
				+" || "+PrintUtils.Print2dec2(actualBalance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, false)
				+" "+PrintUtils.Print2dec(actualBalance*100.0/balance-100.0, false)
				+" "+PrintUtils.Print2dec(maxDD, false)
				);
		
		stats.setPf(pf);
		stats.setTrades(total);
		return pf;
		
	}
	
	public static double doTest4b$$(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,			
			int y1,int y2,
			ArrayList<EurAddConfig> configs,
			int ndays,
			double aStd,
			double aPf,
			double aTrades,
			double balance,
			double risk,
			int comm,
			int debug,
			AddinStats stats
			){
				
		
		int lastDay = -1;
	
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		
		int wins = 0;
		int losses = 0;
		int mode = 0;
		int lots = 0;
		double actualBalance = balance;
		double maxBalance = balance;
		double actualEquitity = balance;
		double maxDD = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Integer> arrayLots = new ArrayList<Integer>();
		for (int i=0;i<=10;i++) arrayLots.add(0);
		int accPos = 0;
		int accNeg = 0;
		ArrayList<Integer> days = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double ma0 =-1;
	
		double std0 = -1;
		int maxLosses = 0;
		int actualLosses = 0;
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int mins = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					int range = max-min;
					ma0 = MathUtils.average(days, days.size()-ndays*288, days.size()-1);					
					std0 = Math.sqrt(MathUtils.variance(days, days.size()-ndays*288, days.size()-1));					
					max = -1;
					min = -1;
					
					if (debug==3){
						System.out.println(DateUtils.datePrint(cal)
								+" || "+PrintUtils.Print2dec(ma0, false)+" "+PrintUtils.Print2dec(std0, false)
								);
					}
				}
				lastDay = day;
			}
			
			//ma0 = MathUtils.average(days, days.size()-ndays*288, days.size()-1);					
			//std0 = Math.sqrt(MathUtils.variance(days, days.size()-ndays*288, days.size()-1));
			
			int maxMin = maxMins.get(i-1);
			
			EurAddConfig config = configs.get(h);
			if (config!=null){
				int thr = config.getThr();
				int tp = config.getTp();
				int sl = config.getSl();
				int maxBars = config.getMaxBars();
				double stdFactor = config.getStdFactor();
				
				stdFactor = aStd;
				
				if (y>=y1 && y<=y2 && (h!=0 || mins>=15)){
					double risk$$ = actualEquitity*risk/100.0;
					double pipValue = risk$$/sl;//aproximado
					if (maxMin>=thr
							&& (true
									&& ma0>-1 
									&& q1.getClose5()<ma0-stdFactor*std0 
									//&& q1.getClose5()>ma0									
								)
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()-tp*10;
						int valueSL = q.getOpen5()+sl*10;
						
						PositionCore newPos = new PositionCore();
						newPos.setPositionType(PositionType.SHORT);
						newPos.setEntry(entry);
						newPos.setTp(valueTP);
						newPos.setSl(valueSL);
						newPos.setPositionStatus(PositionStatus.OPEN);
						newPos.setMaxIndex(i+maxBars);
						newPos.setPipValue(pipValue);
						positions.add(newPos);	
						if (debug==3){
							System.out.println(DateUtils.datePrint(cal)
									+" || [NEW SHORT] "+entry
									);
						}
					}else if (maxMin<=-thr
							&& (true
									&& ma0>-1 
									&& q1.getClose5()>ma0+stdFactor*std0 
									//&& q1.getClose5()<ma0
									
									)
							//&& diffOC<=-20
							){
						int entry = q.getOpen5();
						int valueTP = q.getOpen5()+tp*10;
						int valueSL = q.getOpen5()-sl*10;						
						PositionCore newPos = new PositionCore();
						newPos.setPositionType(PositionType.LONG);
						newPos.setEntry(entry);
						newPos.setTp(valueTP);
						newPos.setSl(valueSL);
						newPos.setPositionStatus(PositionStatus.OPEN);
						newPos.setMaxIndex(i+maxBars);
						newPos.setPipValue(pipValue);
						positions.add(newPos);	
						if (debug==3){
							System.out.println(DateUtils.datePrint(cal)
									+" || [NEW LONG] "+entry
									);
						}
					}
				}
			}
			
			
			//evaluacion trades			
			int j = 0;		
			actualEquitity = actualBalance;
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				if (pos.getPositionType()==PositionType.SHORT){
					if (i>=pos.getMaxIndex()){						
						isClosed = true;
					}else{
						if (q.getHigh5()>=pos.getSl()){
							isClosed = true;
						}else if (q.getLow5()<=pos.getTp()){
							isClosed = true;
						}
					}
					
					if (isClosed){
						pips = pos.getEntry()-q.getClose5();
					}
				}else if (pos.getPositionType()==PositionType.LONG){
					if (i>=pos.getMaxIndex()){						
						isClosed = true;
					}else{
						if (q.getLow5()<=pos.getSl()){
							isClosed = true;
						}else if (q.getHigh5()>=pos.getTp()){
							isClosed = true;
						}
					}
					if (isClosed){
						pips = q.getClose5()-pos.getEntry();
					}
				}
				
				//actualizacion equitity
				actualEquitity = actualEquitity + (pips-comm)*0.1*pos.getPipValue();
				
				if (isClosed){
					pips-=comm;
					if (pips>=0){
						wins++;
						winPips+=pips;
					}else{
						losses++;
						lostPips+=-pips;
					}
							
					actualBalance += pips*0.1*pos.getPipValue();
						
					if (debug==2){
						System.out.println("[CLOSED] "+DateUtils.datePrint(cal)+" || "+PrintUtils.Print2dec(pips, false)+" "+PrintUtils.Print2dec(actualBalance, false)+" || "+pos.toString());
					}
					//if (actualBalance<=0) break;
						
					if (actualBalance>=maxBalance){
						maxBalance = actualBalance;
					}else{
						double dd = 100.0-actualBalance*100.0/maxBalance;
						if (dd>=maxDD){
								maxDD = dd;
						}
					}						
					positions.remove(j);
				}else{
					j++;
				}
			}
				
			if (max==-1 || q.getHigh5()>=max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<=min) min = q.getLow5();
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = accPos*1.0/accNeg;
		
		
		if (debug==5 || (total>=aTrades && pf>=aPf))
		System.out.println(
				header
				+" || "+total
				//+" || "+PrintUtils.getArrayStr(arrayLots,1)
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)+" "+PrintUtils.Print2dec(avg, false)
				+" || "+maxLosses
				+" || "+PrintUtils.Print2dec2(actualBalance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, false)
				+" "+PrintUtils.Print2dec(actualBalance*100.0/balance-100.0, false)
				+" "+PrintUtils.Print2dec(maxDD, false)
				);
		
		stats.setPf(pf);
		stats.setTrades(total);
		return pf;
		
	}

	public static void main(String[] args) throws Exception {
		
String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2017.03.29.csv";
		
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
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			//ESTRATEGIAS
			//22 Y 23 : 130 120 50-250 0
			//0 : 50 120 10-180
			//1 : 70 120 70-210
			//5-7 : 200 60 15-45 7.0
			//5-9: 300 60 15-75 1.0 (729/1.90)
			//9: 210 120 15-120
			
			//prueba con 15-45
			//0: 20 180 15 90 7.0
			//1:  90 24 15 45 7.0
			//2
			//3: 140  96 15 45 10.0
			//4  140  84 15 45 9.0
			//5  230  60 15 45 0.0
			//6  120 108 15 45 9.0
			//7  170  12 15 45 5.0
			//8  170  24 15 45 9.0
			//9  210  12 15 45 0.0
			
			//10 260  96 15 45 8.0			
			//11 600  84 15 45 6.0
			//12 210  12 15 45 10.0
			//19 210  96 15 45 10.0  
			//20 200  96 15 45 8.0
			//22 146 132 15 45 8.0
			//23 160  60 15 45 5.0
			
			ArrayList<EurAddConfig> configs = new ArrayList<EurAddConfig>();
			for (int j=0;j<=23;j++) configs.add(null);
			/*configs.set(0, new EurAddConfig(0,90,120,55,85,3.5));
			configs.set(1, new EurAddConfig(1,140,24,35,45,0.0));//1.96 294.87			
			//configs.set(2, new EurAddConfig(2,20,180,15,90,7.0));
			configs.set(3, new EurAddConfig(3,150,72,15,85,7.25));//1.98 299.27
			configs.set(4, new EurAddConfig(4,140,72,15,45,9.0));//1.98 299.27
			configs.set(5, new EurAddConfig(5,230,60,15,45,0.0));
			configs.set(6, new EurAddConfig(6,120,108,15,45,9.0));
			configs.set(7, new EurAddConfig(7,170,12,15,45,2.0));
			configs.set(8, new EurAddConfig(8,190,24,6,26,9.0));//2.23 253
			configs.set(9, new EurAddConfig(9,230,12,5,32,5.0));
			configs.set(22, new EurAddConfig(22,146,132,15,45,8.0));
			configs.set(23, new EurAddConfig(23,160,60,15,45,5.0));*/
			
			configs.set(0, new EurAddConfig(0,90,120,55,85,3.5));
			configs.set(1, new EurAddConfig(1,140,24,35,45,0.0));//1.96 294.87			
			//configs.set(2, new EurAddConfig(2,20,180,15,90,7.0));
			configs.set(3, new EurAddConfig(3,150,72,15,85,7.25));//1.98 299.27
			configs.set(4, new EurAddConfig(4,140,72,15,45,9.0));//1.98 299.27
			configs.set(5, new EurAddConfig(5,230,60,15,45,0.0));
			configs.set(6, new EurAddConfig(6,120,108,15,45,9.0));
			configs.set(7, new EurAddConfig(7,170,12,15,45,2.0));
			configs.set(8, new EurAddConfig(8,190,24,6,26,9.0));//2.23 253
			configs.set(9, new EurAddConfig(9,230,12,5,32,5.0));
			configs.set(22, new EurAddConfig(22,146,132,15,45,8.0));
			configs.set(23, new EurAddConfig(23,160,60,15,45,5.0));
			
			configs.set(2, new EurAddConfig(2,400,102,11,85,7.0));
			configs.set(10, new EurAddConfig(10,560,102,9,90,10.0));//2.15 268
			configs.set(11, new EurAddConfig(11,250,102,37,75,18.0));//2.20 275
			configs.set(12, new EurAddConfig(12,500,156,52,75,13.0));//2.32 282
			configs.set(13, new EurAddConfig(13,600,156,68,75,10.0));//2.35 286
			configs.set(14, new EurAddConfig(14,600,42,7,50,6.0));//2.36 294.67
			configs.set(20, new EurAddConfig(20,650,42,7,50,10.0));//2.36 297.47
			
			AddinStats stats = new AddinStats();
			/*for (int h=20;h<=20;h++){
				EurAddConfig c = configs.get(h);
				for (int thr=100;thr<=100;thr+=50){
					for (int maxBars=6;maxBars<=6;maxBars+=6){
						for (int tp=5;tp<=5;tp+=2){
							for (int sl=10;sl<=10;sl+=10){
								for (double stdFactor=0.0;stdFactor<=0.0;stdFactor+=1.0){
									for (int nDays=14;nDays<=14;nDays++){
										//c.setThr(thr);
										//c.setMaxBars(maxBars);
										//c.setTp(tp);
										//c.setSl(sl);
										//c.setStdFactor(stdFactor);
										String header = thr+" "+maxBars+" "+tp+" "+sl+" "+PrintUtils.Print2dec(stdFactor, false)+" || "+nDays;
										int positives = 0;
										double acc = 0;
										int accTrades = 0;
										for (int y1=2003;y1<=2015;y1++){
											int y2 = y1+2;
											
											double pf = TestEurAdd.doTest4(header, data, maxMins, y1, y2, configs,nDays,5.0,200,20, 5,stats);
											if (pf>=1.0){
												positives++;
												acc+=pf;
												accTrades += stats.getTrades();
											}
										}
										if (positives>=15){
											System.out.println(header+" || "+positives
													+" "+PrintUtils.Print2dec(acc*1.0/positives, false)
													+" "+PrintUtils.Print2dec(accTrades*1.0/positives, false)
													);
										}
									}
								}
							}
						}
					}
				}
			}*/
			
			//for (int j=0;j<=23;j++) configs.add(null);
			for (int j=0;j<=23;j++) configs.set(j,null);
			EurAddConfig.generateGenericConfigs(configs,0,0,100,999,15,45,0.0);
			
			for (int y1=2003;y1<=2003;y1++){
				int y2 = y1+14;
				double balance = 10000;
				double risk = 7.0;
				for (risk=5.0;risk<=5.0;risk+=0.5){					
					for (int h=22;h<=22;h++){
						for (int j=0;j<=23;j++) configs.set(j,null);
						for (int thr=0;thr<=500;thr+=25){
							EurAddConfig.generateGenericConfigs(configs,h,h,thr,48,15,45,0.0);
							for (double aStd=0.0;aStd<=0.0;aStd+=1.0){
								String header = thr+" ";
								//TestEurAdd.doTest4b$$(header, data, maxMins, y1, y2, configs,14,aStd,5.0,200,balance,risk,20, 5,stats);
								//TestEurAdd.doTest4$$("", data, maxMins, y1, y2, configs,14,5.0,200,balance,risk,20, 5,stats);
							}
						}
					}
				}
			}
		
			
		}
		
		
	}

}
