package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.StrategyResultEx;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class PullBackAfterHighs {
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr1,
			int thr2,
			int nbars,
			int debug
			){
		//
	
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){				
				mode = 0;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (maxMin>=thr1){
				if (h>=h1 && h<=h2){
					mode = 1;
				}else if (mode==-1){
					mode = 0; //se desactiva
				}
			}else if (maxMin<=-thr1){
				if (h>=h1 && h<=h2){
					mode = -1;
				}else if (mode==-1){
					mode = 0; //se desactiva
				}
			}
			
					
			boolean isTrade = false;
			int pips = 0;
			if (mode==1){
				if (maxMin<=-thr2){
					TradingUtils.getMaxMinShort(data, qm, calqm, i, i+nbars);
					pips = (qm.getHigh5()-q.getOpen5())-(q.getOpen5()-qm.getLow5());
					isTrade = true;
				}			
			}else if (mode==-1){
				if (maxMin>=thr2){
					TradingUtils.getMaxMinShort(data, qm, calqm, i, i+nbars);
					pips = (q.getOpen5()-qm.getLow5())-(qm.getHigh5()-q.getOpen5());
					isTrade = true;
				}	
			}
					
			if (isTrade){
				totalPips +=pips;
				if (pips>=0){					
					wins++;
				}else{
					losses++;
				}
				isTrade = false;
			}																		
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = totalPips*0.1/trades;
		
		System.out.println(
				header
				+" || "+trades
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	public static void doTest2(
			StrategyResultEx strat,
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr1,
			int thr2,
			int nbars,
			int tp,
			int sl,
			int maxTrades,
			int diff,
			double comm,
			int debug
			){
		//
	
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalPips = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){	
				if (dailyPips<0
						&& debug==3
						){
					System.out.println(DateUtils.datePrint(cal1)+" || "+PrintUtils.Print2dec(dailyPips*0.1, false));					
				}
				dailyPips = 0;
				mode = 0;
				lastDay = day;
				actualPOI  = 0;
				dayTrades = 0;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (maxMin>=thr1){
				if (h>=h1 && h<=h2){
					mode = 1;
					actualPOI = q1.getHigh5();
					//System.out.println("ACTIVADO LONGS "+DateUtils.datePrint(cal1));
				}else if (mode==-1){
					mode = 0; //se desactiva
					//System.out.println("DESACTIVADO SHORTS "+DateUtils.datePrint(cal1));
				}
			}else if (maxMin<=-thr1){
				if (h>=h1 && h<=h2){
					mode = -1;
					actualPOI = q1.getLow5();
				}else if (mode==1){
					mode = 0; //se desactiva
				}
			}
			
					
			boolean isTrade = false;
			double pips = 0;
			if (dayTrades<=maxTrades){
				if (mode==1){
					int diffPOI = actualPOI-q.getOpen5();
					if (    true
							&& maxMin<=-thr2
							//&& diffPOI>=diff*10
							//&& q1.getOpen5()-diff*10<q1.getClose5()
							){
						//TradingUtils.getMaxMinShort(data, qm, calqm, i, i+nbars);
						//System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)+" "+q.getOpen5());
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+nbars, q.getOpen5()+10*tp, q.getOpen5()-10*sl, false);
						pips = qm.getClose5()-q.getOpen5();
						isTrade = true;
					}			
				}else if (mode==-1){
					int diffPOI = q.getOpen5()-actualPOI;
					if (	true
							&& maxMin>=thr2
							//&& diffPOI>=diff*10
							//&& q1.getOpen5()+diff*10>q1.getClose5()
							){
						//TradingUtils.getMaxMinShort(data, qm, calqm, i, i+nbars);
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i,i+nbars, q.getOpen5()-10*tp, q.getOpen5()+10*sl, false);
						pips = q.getOpen5()-qm.getClose5();
						isTrade = true;
					}	
				}
			}		
			if (isTrade){
				dayTrades++;
				pips -= comm*10;
				totalPips +=pips;
				dailyPips += pips;
				if (pips>=0){					
					wins++;
					winPips+=pips;
				}else{
					lostPips += -pips;
					losses++;
				}
				isTrade = false;
			}																		
		}
		
		int trades = wins+losses;
		
		double winPer = wins*100.0/trades;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		
		strat.setTotalWins(wins);
		strat.setWinPips(winPips);
		strat.setLossPips(lostPips);
		strat.setTotalTrades(trades);
		
		if (debug==0
				|| (debug==4 && pf>=1.60)
				|| (debug==5 && pf>=1.70 && trades>=100)
				){
			System.out.println(
					header
					+" || "+trades
					+" || "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(pf, false)
					);
		}
		
	}
	
	public static void doTest2$$(
			StrategyResultEx strat,
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int thr1,
			int thr2,
			int nbars,
			int tp,
			int sl,
			int maxTrades,
			int diff,
			double balance,
			double risk,
			double comm,
			int debug
			){
		//
	
		double extended = 0;
		double actualBalance = balance;
		double maxBalance = balance;
		double equitity = balance;
		double marginUsed = 0;
		double maxDD = 0;
		double minMarginLevel = -1;
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalPips = 0;
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
			
			if (day!=lastDay){	
				if (dailyPips<0
						&& debug==3
						){
					System.out.println(DateUtils.datePrint(cal1)+" || "+PrintUtils.Print2dec(dailyPips*0.1, false));					
				}
				
				
				if (isHigh || isLow){
					countd++;
					if (isHigh && isLow){
						countdb++;
					}
				}
				
				isHigh = false;
				isLow = false;
				
				dailyPips = 0;
				mode = 0;
				lastDay = day;
				actualPOI  = 0;
				dayTrades = 0;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (maxMin>=thr1){
				isHigh=true;
				if (h>=h1 && h<=h2){
					mode = 1;
					actualPOI = q1.getHigh5();
					//System.out.println("ACTIVADO LONGS "+DateUtils.datePrint(cal1));
				}else if (mode==-1){
					mode = 0; //se desactiva
					//System.out.println("DESACTIVADO SHORTS "+DateUtils.datePrint(cal1));
				}
			}else if (maxMin<=-thr1){
				isLow=true;
				if (h>=h1 && h<=h2){
					mode = -1;
					actualPOI = q1.getLow5();
				}else if (mode==1){
					mode = 0; //se desactiva
				}
			}
			
			
			if (mode!=0){
				boolean isTrade = false;
				double pips = 0;
				int actualTrades = 0;
				for (int p=0;p<positions.size();p++){
					if (positions.get(p).getPositionStatus()==PositionStatus.OPEN) actualTrades++;
				}
				
				if (actualTrades<=maxTrades){
					if (mode==1){
						int diffPOI = actualPOI-q.getOpen5();
						if (    true
								&& maxMin<=-thr2
								//&& diffPOI>=diff*10
								//&& q1.getOpen5()-diff*10<q1.getClose5()
								){
							
							//TradingUtils.getMaxMinShort(data, qm, calqm, i, i+nbars);
							//System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)+" "+q.getOpen5());
							//TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+nbars, q.getOpen5()+10*tp, q.getOpen5()-10*sl, false);
							//pips = qm.getClose5()-q.getOpen5();
							//isTrade = true;
							double maxRisk = equitity*risk/100;
							double pipValue = maxRisk/sl;//1=0.1 lot = 10000 USD-> 400:1 leverage = 25$$ de margen usado or cada 0.1 lot
							double marginUsedPosition = pipValue*25;
							double freeMargin = equitity-marginUsed;
							
							if (freeMargin>=marginUsedPosition){
								marginUsed += marginUsedPosition; 
										
								PositionCore pos = new PositionCore();
								pos.setPositionStatus(PositionStatus.OPEN);
								pos.setPositionType(PositionType.LONG);
								pos.setEntry(q.getOpen5());
								pos.setTp(q.getOpen5()+10*tp);
								pos.setSl(q.getOpen5()-10*sl);
								pos.setPipValue(pipValue);
								pos.setEntryIndex(i);
								if (debug==3){
									System.out.println(DateUtils.datePrint(cal1)+" || [LONG] "+" "+q.getOpen5()+" "+pos.getTp()+" "+pos.getSl() );					
								}
								positions.add(pos);
								
								/*if (marginUsed>0.01){
									double marginLevel = equitity*100.0/marginUsed;
									if (debug==3)
									System.out.println(
										   PrintUtils.Print2dec(pos.getPipValue(), false)+" "+PrintUtils.Print2dec(marginUsedPosition, false)
										   +" || "+PrintUtils.Print2dec(equitity, false)+" "+PrintUtils.Print2dec(marginUsed, false)+" "+PrintUtils.Print2dec(marginLevel, false)
											);
									if (minMarginLevel==-1 || marginLevel<minMarginLevel){
										minMarginLevel = marginLevel;
									}
								}*/
							}
						}			
					}else if (mode==-1){
						int diffPOI = q.getOpen5()-actualPOI;
						if (	true
								&& maxMin>=thr2
								//&& diffPOI>=diff*10
								//&& q1.getOpen5()+diff*10>q1.getClose5()
								){
							
							double maxRisk = equitity*risk/100;
							double pipValue = maxRisk/sl;
							double marginUsedPosition = pipValue*25;
							double freeMargin = equitity-marginUsed;
							
							if (freeMargin>=marginUsedPosition){
								marginUsed += marginUsedPosition; 
							
								PositionCore pos = new PositionCore();
								pos.setPositionStatus(PositionStatus.OPEN);
								pos.setPositionType(PositionType.SHORT);
								pos.setEntry(q.getOpen5());
								pos.setTp(q.getOpen5()-10*tp);
								pos.setSl(q.getOpen5()+10*sl);
								pos.setPipValue(pipValue);
								pos.setEntryIndex(i);
								if (debug==3
										){
									System.out.println(DateUtils.datePrint(cal1)+" || [SHORT] "+" "+q.getOpen5()+" "+pos.getTp()+" "+pos.getSl() );					
								}
								positions.add(pos);
								
								/*if (marginUsed>0.01){
									double marginLevel = equitity*100.0/marginUsed;
									if (debug==3)
									System.out.println(
										   PrintUtils.Print2dec(pos.getPipValue(), false)+" "+PrintUtils.Print2dec(marginUsedPosition, false)
										   +" || "+PrintUtils.Print2dec(equitity, false)+" "+PrintUtils.Print2dec(marginUsed, false)+" "+PrintUtils.Print2dec(marginLevel, false)
											);
									if (minMarginLevel==-1 || marginLevel<minMarginLevel){
										minMarginLevel = marginLevel;
									}
								}*/
							}
						}	
					}
				}		
			}
			
			//evaluacion posiciones
			int p = 0;
			while (p<positions.size()){
				
				PositionCore pos = positions.get(p);
				boolean isClosed = false;
				
				if (pos.getPositionStatus()==PositionStatus.OPEN){
				    int pipsTrade = 0;
					if (pos.getPositionType()==PositionType.LONG){
						if (q.getHigh5()>=pos.getTp()){
							pipsTrade = pos.getTp()-pos.getEntry();
							isClosed = true;
						}else if (q.getLow5()<=pos.getSl()){
							pipsTrade = pos.getSl()-pos.getEntry();
							isClosed = true;
						}
					}else if (pos.getPositionType()==PositionType.SHORT){
						if (q.getLow5()<=pos.getTp()){
							pipsTrade = pos.getEntry()-pos.getTp();
							isClosed = true;
						}else if (q.getHigh5()>=pos.getSl()){
							pipsTrade = pos.getEntry()-pos.getSl();
							isClosed = true;
						}
					}
					
					if (!isClosed){
						if (i>=pos.getEntryIndex()+nbars){
							if (pos.getPositionType()==PositionType.LONG){
								pipsTrade = q.getClose5()-pos.getEntry();
							}else if (pos.getPositionType()==PositionType.SHORT){
								pipsTrade = pos.getEntry()-q.getClose5();
							}
							isClosed = true;
						}
					}
					
					//procedimiento de borrado si se da la vuelta
					/*(if (!isClosed){
						if (mode==1){
							if (pos.getPositionType()==PositionType.SHORT){
								pipsTrade = pos.getEntry()-q.getClose5();
								//isClosed = true;
							}
							
						}
						if (mode==-1){
							if (pos.getPositionType()==PositionType.LONG){
								pipsTrade = q.getClose5()-pos.getEntry();
								//isClosed = true;
							}							
						}
					}*/
					
					//actualacion equitity ymargin level
					equitity = actualBalance + (pipsTrade-comm*10)*0.1*pos.getPipValue();
					double marginUsedPosition = pos.getPipValue()*25;
					if (marginUsed>0.01){
						double marginLevel = equitity*100.0/marginUsed;
						if (debug==3)
						System.out.println(
							   PrintUtils.Print2dec(pos.getPipValue(), false)+" "+PrintUtils.Print2dec(marginUsedPosition, false)
							   +" || "+PrintUtils.Print2dec(equitity, false)+" "+PrintUtils.Print2dec(marginUsed, false)+" "+PrintUtils.Print2dec(marginLevel, false)
								);
						if (minMarginLevel==-1 || marginLevel<minMarginLevel){
							minMarginLevel = marginLevel;
						}
					}
					
					if (isClosed){
						pipsTrade -=comm*10;
						
						actualBalance = actualBalance + pipsTrade*0.1*pos.getPipValue();
						marginUsed -= marginUsedPosition;//ya no esta usado
						
						//marginLevel
						//System.out.println(actualBalance+" "+pos.getPipValue()+" "+pipsTrade);
						if (actualBalance>=maxBalance){
							maxBalance = actualBalance;
						}else{
							
							double dd = actualBalance*100.0/maxBalance-100.0;
							if (Math.abs(dd)>=maxDD){
								maxDD = Math.abs(dd);
								//System.out.println(actualBalance+" "+pos.getPipValue()+" "+pipsTrade+" "+maxDD);
							}
							
							
						}
						
						if (pipsTrade>=0){
							wins++;
							winPips += pipsTrade;
							if (debug==3){
								System.out.println(DateUtils.datePrint(cal1)+" || [WIN] "+" "+pos.getPositionType().name()+" "+pos.getTp()+" "+pos.getSl() );					
							}
						}else{
							losses++;
							lostPips += -pipsTrade;
							if (debug==3){
								System.out.println(DateUtils.datePrint(cal1)+" || [LOSS] "+" "+pos.getPositionType().name()+" "+pos.getTp()+" "+pos.getSl() );					
							}
						}
						
						if (actualBalance <=1000){
							extended +=(10000-actualBalance);
							actualBalance = 10000;
						}
					}
					
				}//open
				
				if (isClosed){
					positions.remove(p);
				}else{
					p++;
				}
			}
			
		}
		
		int trades = wins+losses;
		
		double winPer = wins*100.0/trades;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		double yield = (winPips-lostPips)*0.1*100/(sl*trades);
		double maxPP = maxBalance*100.0/(balance+extended)-100.0;
		
		double perdb = countdb*100.0/countd;
		
		strat.setTotalWins(wins);
		strat.setWinPips(winPips);
		strat.setLossPips(lostPips);
		strat.setTotalTrades(trades);
		
		if (
				(
				debug==0 
				//&& pf>=1.5 && trades>=1000
				)
				|| (debug==4 && pf>=1.60)
				|| (debug==5 && pf>=1.70 && trades>=100)
				){
			System.out.println(
					header
					+" || "+PrintUtils.Print2Int(trades, 4)
					+" || "
					+" "+PrintUtils.Print2dec(winPer, false)

					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(yield, false)
					+" "+PrintUtils.Print2dec(maxDD, false)
					//+" "+PrintUtils.Print2dec(maxPP/maxDD, false)
					+" || "
					+" "+PrintUtils.Print2dec2(actualBalance, true)
					+" "+PrintUtils.Print2dec2(maxBalance, true)
					+" "+PrintUtils.Print2dec(balance+extended, false)
					+" || "+PrintUtils.Print2dec(maxPP, false)
					//+" "+PrintUtils.Print2dec(maxDD, false)
					+" "+PrintUtils.Print2dec(maxPP/maxDD, false)
					+" || "+PrintUtils.Print2dec(minMarginLevel, false)
					+" || "+countd+" "+countdb+" "+PrintUtils.Print2dec(perdb, false)
					);
		}
		
		if (debug==9){
			System.out.println(
					header
					+" || "+PrintUtils.Print2Int(trades,4)
					+" || "+PrintUtils.Print2dec(winPer, false,3)
					+" "+PrintUtils.Print2dec(actualBalance*100.0/balance-100.0, false,3)				
				);
		}
		
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		//String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2017.02.14.csv";
		//String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2017.02.23.csv";
		//String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2017.02.28.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_5 Mins_Bid_2009.01.01_2019.01.11.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2017.03.23.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2017.03.23.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		paths.add(pathEURJPY);
		paths.add(pathGBPJPY);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 5;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		StrategyResultEx st = new StrategyResultEx();
		
		ArrayList<ArrayList<QuoteShort>> dataArray = new ArrayList<ArrayList<QuoteShort>>();
		ArrayList<ArrayList<Integer>> maxMinsArray = new ArrayList<ArrayList<Integer>>();
		for (int i=0;i<=limit;i++){
			dataArray.add(null);
			maxMinsArray.add(null);
		}
		double balance = 10000;
		for (int y1=2004;y1<=2019;y1+=1){
			int y2 = y1+0;
			for (int month1=0;month1<=0;month1+=6){
				int month2 = month1+11;			
				for (int h1=16;h1<=16;h1++){//usdjpy: 159 48 145 8 110 25 9.5 --- gbpusd 90/60/168/15/70/25/8.0
					int h2 = h1+7;
					for (int thr1=180;thr1<=180;thr1+=12){
						for (int thr2=59;thr2<= 59;thr2+=1){
							for (int nbars=252;nbars<=252;nbars+=12){//252														
								for (int tp=9;tp<=9;tp+=1){
									for (int sl=153;sl<=153;sl+=1*tp){									
										for (int maxTrades = 10;maxTrades<=10;maxTrades++){
											for (int diff = 0;diff<=0;diff++){
												for (double risk = 7.0;risk<=7.0;risk+=0.5){
													String header = y1+" "+y2+" "+PrintUtils.Print2Int(month1, 2)+" "+PrintUtils.Print2Int(month2, 2)
															+" || "+h1+" "+h2+" "+thr1+" "+thr2
															+" "+nbars+" "+tp+" "+sl+" "+maxTrades
															+" "+diff+" "+PrintUtils.Print2dec(risk, false);
													int totalTrades = 0;
													double winPips = 0;
													double lostPips = 0;
													int wins = 0;
													limit = 3;
													for (int i = 3;i<=limit;i++){
														String path = paths.get(i);	
														if (dataArray.get(i)==null){										
																
															dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
															dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
															ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
															ArrayList<QuoteShort> data = null;
															data = data5m;										
															
															ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
															
															dataArray.set(i,data);
															maxMinsArray.set(i,maxMins);
														}
														
														//PARA 5 MIN 17 trades 20% de riesgo
														int factor = 1;
														if (path.contains("1 Min")){//140 TRADES 0.46
															//System.out.println("1 min mode");	
															//maxTrades = 25;
															//risk = 9.5;
															factor = 5;													
														}
														if (path.contains("5 Mins")){//140 TRADES 0.46
															//System.out.println("1 min mode");	
															//maxTrades = 17;
															//risk = 20.0;
															//factor = 5;													
														}
														
														
														ArrayList<QuoteShort>  data = dataArray.get(i);
														ArrayList<Integer> maxMins = maxMinsArray.get(i);
														//System.out.println("total data: "+data.size()+" "+maxMins.size());
														
														//eurusd: 456/54/180/5/60   -> spread avg 2012-2016: 0.40+comm
														//gbpusd: 120/54/40/13/110  -> spread avg 2012-2016: 1.00+comm
														//audusd: 192/54/210/10/70 -> spread avg 2012-2016: 1.15+comm
														//usdjpy: 146/48/130/8/110  -> spread avg 2012-2016: 0.50+comm
														double comm = 0.75 + 0.40;
														if (path.contains("GBPUSD")) comm = 0.75+1.25;
														if (path.contains("AUDUSD")) comm = 0.75+1.25;
														if (path.contains("USDJPY")) comm = 0.75+1.25;
														if (path.contains("GBPJPY")) comm = 0.75+1.75;
														
														//PullBackAfterHighs.doTest(header, data, maxMins, y1, y2, h1, h2, thr1, thr2, nbars, 0);													
														//PullBackAfterHighs.doTest2(st,header, data, maxMins, y1, y2, h1, h2, thr1, thr2, nbars,tp,sl,maxTrades,diff,comm, -1);
														PullBackAfterHighs.doTest2$$(st,header, data, maxMins, y1, y2,month1,month2, h1, h2, thr1*factor, thr2*factor, nbars*factor,tp,sl,maxTrades,diff,balance,risk,comm, 0);
														totalTrades += st.getTotalTrades();
														winPips += st.getWinPips();
														lostPips += st.getLossPips();
														wins+=st.getTotalWins();
												   }//limit	
													
													//Results
													double avgPips = (winPips-lostPips)*0.1/totalTrades;
													double pf = winPips/lostPips;							
													
													/*System.out.println(
															header
															+" || "
															+" "+totalTrades	
															+" "+PrintUtils.Print2dec(wins*100.0/totalTrades, false)
															+" "+PrintUtils.Print2dec(avgPips, false)
															+" "+PrintUtils.Print2dec(pf, false)
															);*/
												}//risk
											}//diff
										}//maxTrades
									}//sl								
								}//tp
							}//nbars
						}//thr2					
					}//thr1
				}//h1
			}//month1
		}//y1
		
		
	}

}
