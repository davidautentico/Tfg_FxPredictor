package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.EAS.DaveTrade2019;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TickStudy2019 {
	
	public static void testSpreads(){
		
		String path0 ="C:\\fxdata\\";
		String pathEURUSD04 = path0+"EURUSD_Ticks_2004.01.01_2004.01.31.csv";
		String pathEURUSD05 = path0+"EURUSD_Ticks_2005.01.01_2005.01.31.csv";
		String pathEURUSD06 = path0+"EURUSD_Ticks_2006.01.01_2006.01.31.csv";
		String pathEURUSD07 = path0+"EURUSD_Ticks_2007.01.01_2007.01.31.csv";
		String pathEURUSD08 = path0+"EURUSD_Ticks_2008.01.01_2008.01.31.csv";
		String pathEURUSD09 = path0+"EURUSD_Ticks_2009.01.01_2009.01.31.csv";
		String pathEURUSD10 = path0+"EURUSD_Ticks_2010.01.01_2010.01.31.csv";
		String pathEURUSD11 = path0+"EURUSD_Ticks_2011.01.01_2011.01.31.csv";
		String pathEURUSD12 = path0+"EURUSD_Ticks_2012.01.01_2012.01.31.csv";
		String pathEURUSD13 = path0+"EURUSD_Ticks_2013.01.01_2013.01.31.csv";
		String pathEURUSD14 = path0+"EURUSD_Ticks_2014.01.01_2014.01.31.csv";
		String pathEURUSD15 = path0+"EURUSD_Ticks_2015.01.01_2015.01.31.csv";
		String pathEURUSD16 = path0+"EURUSD_Ticks_2016.01.01_2016.01.31.csv";
		String pathEURUSD17 = path0+"EURUSD_Ticks_2017.01.01_2017.01.31.csv";
		String pathEURUSD18 = path0+"EURUSD_Ticks_2018.01.01_2018.01.31.csv";
		String pathEURUSD19 = path0+"EURUSD_Ticks_2010.01.01_2010.06.30.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD04);paths.add(pathEURUSD05);paths.add(pathEURUSD06);paths.add(pathEURUSD07);
		paths.add(pathEURUSD08);paths.add(pathEURUSD09);paths.add(pathEURUSD10);paths.add(pathEURUSD11);
		paths.add(pathEURUSD12);paths.add(pathEURUSD13);paths.add(pathEURUSD14);paths.add(pathEURUSD15);
		paths.add(pathEURUSD16);paths.add(pathEURUSD17);paths.add(pathEURUSD18);paths.add(pathEURUSD19);
		
		ArrayList<QuoteShort> dataI 		= new ArrayList<QuoteShort>();
		ArrayList<QuoteShort> dataS 		= null;
		Calendar cal = Calendar.getInstance();
		for (int h1=0;h1<=23;h1++){
			System.out.println(h1+"...");
			
			for (int i=paths.size()-1;i<=paths.size()-1;i++){	
				String path = paths.get(i);			
				dataI.clear();
				Tick.readFromDiskToQuoteShort(dataI, path, 7);
				TestLines.calculateCalendarAdjustedSinside(dataI);
					int acc=0;
					int count = 0;
					for (int j=0;j<dataI.size();j++){
						QuoteShort q = dataI.get(j);
						QuoteShort.getCalendar(cal, q);
						int h = cal.get(Calendar.HOUR_OF_DAY);
						int min = cal.get(Calendar.MINUTE);
						if (h==h1 && (min>=15 || h!=0)){
							acc += Math.abs(dataI.get(j).getAsk()-dataI.get(j).getBid());
							count++;
						}
					}
					System.out.println(path+" || "+PrintUtils.Print2dec(acc*0.1/count, false));
				}
				
			}
	}
	
	public static int getSpread(int year,int h){
		int spread = 20;
		
		if (h==0){
			if (year>=2014) spread= 18;
			else spread = 22;
		}
		if (h==1){
			if (year>=2014) spread= 12;
		}
		if (h==2){
			if (year>=2014) spread= 11;
		}
		if (h==3){
			if (year>=2014) spread= 10;
		}
		if (h==3){
			if (year>=2014) spread= 10;
		}
		if (h>=4 && h<=22){
			if (year>=2014) spread = 10;
		}
		if (h==23){
			if (year>=2014) spread= 14;
		}
		
		return spread;
	}
	
	public static double testPercent2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int m1,int m2,
			int h1,int h2,int min1,int min2,
			double tpf,double slf,
			int n,
			double perMin,double perMax,
			int thr,
			int arange,
			int pipsThr,
			double maxDD,
			int minSl,
			int minDesv,
			boolean isFloating,
			boolean isReversing,
			double aRisk,
			double maxOpenRisk,
			double aBalanceInicial,
			int debug
			){
		
		//ArrayList<ArrayList<Integer> spreads
		HashMap<Integer,ArrayList<Integer>> spreads = new HashMap<Integer,ArrayList<Integer>>();
		
		
		double balanceInicial = aBalanceInicial;
		double balance = aBalanceInicial;
		double maxBalance = balance;
		double equitity = balance;
		double maxBalanceDD = 0.0;
		double balanceAdded = 0;
		
		Calendar cal = Calendar.getInstance();
		
		int comm = 15;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		int mode = 0;
		int high = -1;
		int low = -1;
		int range = 800;
		int tp = (int) (range*tpf);
		int sl = (int) (range*tpf); 
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		for (int i=n;i<data.size()-1;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			 
			 
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
			
			qLast = q;
			comm = TickStudy2019.getSpread(y, h);
			
			if (day!=lastDay){
				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-n,rangeArr.size()-1);
					
					tp = (int) (tpf*range);	
					if (tp<=40) tp=40;
					sl = (int) (slf*range);	
					if (sl<=40) sl=40;
				}
				
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			if (h>=h1 && h<=h2 
					&& min>=min1 && min<=min2
					&& (high-low)>=arange
					&& (h>0 || min>=15)
					){
				
		
				//double avg = MathUtils.average(closeArr, i-n, i-1);				
				double avg = data.get(i-n).getOpen5();//n ultimas vars
				
				
				int diffUp = (int) (q.getOpen5()-avg);
				int diffDown = (int) (avg-q.getOpen5());
				
				double per1 = diffUp*100.0/range;
				double per2 = diffDown*100.0/range;
				int diffH1 = q1.getClose5()-q1.getOpen5();
				int diffHC = q1.getHigh5()-q1.getClose5();
				int diffL1 = q1.getOpen5()-q1.getClose5();
				int diffLC = q1.getOpen5()-q1.getLow5();
	
				int isOk = 0;
				if (per1>=perMin && per1<=perMax){
					isOk = 1;
				}else if (per2>=perMin && per2<=perMax){
					isOk = 2;
				}
				
				if (isOk>0){
					if (h==0 && min<15) isOk =0;
				}
				
				double per3 = diffUp*100.0/range;
				double per4 = diffDown*100.0/range;
				double perMin2 = 3600.0;
				double perMax2 = 3600.0;
				
				int maxMin = maxMins.get(i-1);
				int diffdo = q.getOpen5()-doValue;
				double actualOpenRiskPer = actualOpenRisk*100.0/equitity;
				if (true
						&& isOk==1
						&& actualOpenRiskPer<=maxOpenRisk
						){
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
					}else{
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
		
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)
									+" "+entry
									+" || "+PrintUtils.Print2dec(riskPip, false)
									+" "+PrintUtils.Print2dec(equitity, false)
									+" || "+(winPips-lostPips)+" "+actualFloatingPips
									);
						}
						
						mode = 1;
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}else if (true
						&& isOk==2
						&& actualOpenRiskPer<maxOpenRisk
						){
													
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
					}else{					
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)
							+" "+entry
							+" || "+PrintUtils.Print2dec(riskPip, false)
							+" "+PrintUtils.Print2dec(equitity, false)
							+" || "+(winPips-lostPips)+" "+actualFloatingPips
							);
						}
						
						mode = -1;
					}					
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}	
			}
						
			int j = 0;
			int yr = y-y1;
			int ymr = yr*12+month;
			mWinPipsO.set(ymr, 0l);
			mLostPipsO.set(ymr, 0l);
			boolean closeAll = false;
			if (month!=lastCloseMonth){
				if (lastCloseMonth!=-1){					
					closeAll = true;
				}
				lastCloseMonth = month;
			}
			closeAll = false;
			equitity = balance;
			actualOpenRisk = 0;
			int closedLosses = 0;
			int totalClosedLossesPips = 0;
			actualFloatingPips = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;

						if (q.getHigh5()>=p.getTp()){
							pips = p.getTp()-p.getEntry();
							isClose = true;
						}else if (q.getLow5()<=p.getSl()){
							pips = p.getSl()-p.getEntry()-comm;
							isClose = true;
													
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (duration>=288 && floatingPips<=100
								&& h>=15 && h<=16
								){
							pips =floatingPips;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						if (q.getLow5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry();
							isClose = true;
						}else if (q.getHigh5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry()-comm;
							isClose = true;
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (duration>=288 && floatingPips<=100
								&& h>=15 && h<=16
								){
							pips =floatingPips;
							//isClose = true;
						}
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							equitity += win$$;
							
							accPositions += p.getPip$$();
														
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							equitity += pip$$;
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						//acumulamos floating
						if (floatingPips>=0){							
							int yo = y-y1;
							
							long ma = mWinPipsO.get(yo*12+month);
							mWinPipsO.set(yo*12+month, ma+floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[WIN floating] "+floatingPips+" ["+yo*12+month+"] "+(ma+floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							int yo = y-y1;							
							long ma = mLostPipsO.get(yo*12+month);
							mLostPipsO.set(yo*12+month, ma-floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[LOSS floating] "+floatingPips+" ["+yo*12+month+"] "+(ma-floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}						
						actualFloatingPips += floatingPips;
						
						isClose = false;
						//trailing Stop
						if (p.getPositionType()==PositionType.LONG){
							//actualOpenRisk
							
							if (q.getClose5()>=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((q.getClose5()-p.getEntry())*(maxDD/100.0));
								int newSl = p.getEntry()+pipsToTrail;
								if (newSl>=p.getSl() 
										&& newSl<=q.getClose5()-20
										&& newSl>=p.getEntry()+minSl
										){
									p.setSl(p.getEntry()+pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(p.getEntry()-p.getSl())*0.1;
						}else if (p.getPositionType()==PositionType.SHORT){
							if (q.getClose5()<=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((-q.getClose5()+p.getEntry())*(maxDD/100.0));
								
								int newSl = p.getEntry()-pipsToTrail;
								if (newSl<=p.getSl() 
										&& newSl>=q.getClose5()+20
										&& newSl<=p.getEntry()-minSl
										){
									p.setSl(p.getEntry()-pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(-p.getEntry()+p.getSl())*0.1;
						}
						if (isClose){
							positions.remove(j);
						}else{
							j++;
						}
					}
				}
			}//positions
			
			
			double amount = equitity;
			double minEquitity = 1000;
			if (amount<balanceInicial
					&& amount<=minEquitity
					){
				balanceAdded += minEquitity-amount ;
				balance += minEquitity-amount ;
				equitity += minEquitity-amount ;
			}
			
			if (balance>maxBalance){
				maxBalance = amount ;
				if (debug==1){
					System.out.println("[NEW MAXEQUITITY] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							);
				}
		    }
			
			amount = balance;
			double actualDD = 100.0-amount*100.0/maxBalance;
			if (actualDD>maxBalanceDD){
				maxBalanceDD = actualDD;
				if (debug==1){
					System.out.println("[NEW MAXDD] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+positions.size()
							+" "+PrintUtils.Print2dec(amount, false)
							+" "+PrintUtils.Print2dec(balance, false)
							+" "+PrintUtils.Print2dec(maxBalance, false)
							+" "+PrintUtils.Print2dec(actualDD, false)
							+" || "+PrintUtils.Print2dec(actualOpenRisk, false)
							);
				}
			}
			
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		int unclosedPositions = 0;
		if (isFloating){
			int j = 0;
			q = q1;
			QuoteShort.getCalendar(cal, q1);
			month = cal.get(Calendar.MONTH);
			 y = cal.get(Calendar.YEAR);
			//System.out.println(month);
			unclosedPositions = positions.size();
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						pips = q.getClose5()-p.getEntry()-comm;
						isClose = true;
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = -q.getClose5()+p.getEntry()-comm;
						isClose = true;
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							if (balance>=maxBalance){
								maxBalance = balance;
							}
							
							if (debug==1){
								System.out.println("[WIN] "+pips
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}else{
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}
							
							if (debug==1){
								System.out.println("[LOST] "+pips
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						j++;
					}
				}
			}
		}//isFloating
				
		int ywins=0;
		int yTotal = 0;
		for (int i=0;i<=(y2-y1);i++){
			double pf = yearWinPips.get(i)*1.0/yearLostPips.get(i);
			if (pf>=1.0
					|| (yearWinPips.get(i)>=0 && yearLostPips.get(i)==0) 
					){
				ywins++;
			}
			if (yearWinPips.get(i)>0 || yearLostPips.get(i)>0) yTotal++;
		}
		
		int mwins=0;
		int mTotal = 0;
		long accW = 0;
		long accL = 0;
		long lastPips = 0;
		long lastMonth = 0;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			double pf = mWinPips.get(i)*1.0/mLostPips.get(i);
			double pfreal = (mWinPips.get(i)+mWinPipsO.get(i))*1.0/(mLostPips.get(i)+mLostPipsO.get(i));
			boolean isWin = false;
			boolean isTotal = false;
			accW += mWinPips.get(i);
			accL += mLostPips.get(i);
			
			long accMonth = accW-accL+mWinPipsO.get(i)-mLostPipsO.get(i);
			
			long netMonth = accMonth-lastMonth;
			
			
			if (netMonth>0){
				mwins++;
				//System.out.println("[win] "+accMonth+" "+lastMonth);
			}else if (netMonth<0){
				//System.out.println("[loss] "+accMonth+" "+lastMonth);
			}
			lastMonth = accMonth;
			
			long global = mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i);
			if ((mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i))>0){				
				mTotal++;
			}
		}
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		//if (pf>=1.3 && avg>=3.8)
		double perDays = totalTradeDays*100.0/totalDays;
		double factor = perDays*1.0/pf;
		double fm = maxBalance/(balanceInicial+balanceAdded);
		double fmdd = (maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD;
		
		ArrayList<Integer> hs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hs.add(0);
		int total = 0;
		for (int i=0;i<closedTimes.size();i++){
			long millis = closedTimes.get(i);
			cal.setTimeInMillis(millis);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int acc = hs.get(h);
			hs.set(h, acc+closedPips.get(i));
			total+=closedPips.get(i);
		}
		
		String values = "";
		for (int i=0;i<=23;i++){
			double pr = hs.get(i)*100.0/total;
			values = values +" "+PrintUtils.Print2dec(pr, false);
		}
		
		//System.out.println(values);
		
		if (true 
				//&& pf>=1.6
				&& (debug==0
					|| (
						debug==10
						 //&& ywins>=12 
						//&& mwins>=0 && perDays>=40 && avg>=0.0 
						//&& avg>=4.0 
						//&& factor>=50
						//&& mwins>=80 
						&& pf>=1.40
						&& perDays>=40.0
						//&& maxBalanceDD<=90.0
						//&& fm>=1000
						//&& fmdd>=1000.0
						)
					)
				) 
		System.out.println(
				header
				+" "+y1+" "+y2
				+" "+h1+" "+h2
				+" "+PrintUtils.Print2dec(tpf, false)
				+" "+PrintUtils.Print2dec(slf, false)
				+" "+n
				+" "+PrintUtils.Print2dec(perMin, false)
				+" "+PrintUtils.Print2dec(perMax, false)
				//+" "+PrintUtils.Print2dec(maxDD, false)
				//+" "+minSl
				+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+PrintUtils.Print2dec(maxOpenRisk, false)
				+" || "
				+" "+trades
				//+" "+unclosedPositions
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				//+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				//+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				//+" "+PrintUtils.Print2dec(accPositions/trades, false)
				+" || "				
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(1.0/pf, false)
				//+" || "+mwins+" / "+mTotal
				+" || "+PrintUtils.Print2dec(perDays, false)
				//+" || "+PrintUtils.Print2dec(perDays*1.0/pf, false)
				+" ||| "
				+" "+PrintUtils.Print2dec2(balanceInicial, true)
				+" "+PrintUtils.Print2dec2(balanceInicial+balanceAdded, true)
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec((maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec2(maxBalance/(balanceInicial+balanceAdded), true)
				);	
		
		if (true
				//&& (maxBalanceDD>=100.0 || 
				&& perDays<40.0
				) return -1.0;
		
		return fmdd;		
	}
	
	public static double getRisk(double per1){
		
		double aRisk = 0.0;
		if (per1>=90.0){
			aRisk = 5.4;
		}else if (per1>=85.0){
			aRisk = 4.80;
		}else if (per1>=80.0){
			aRisk = 4.20;
		}else if (per1>=70.0){
			aRisk = 3.60;
		}else if (per1>=65.0){
			aRisk = 2.8;
		}else if (per1>=60.0){
			aRisk = 2.40;
		}else if (per1>=55.0){
			aRisk = 1.70;
		}else if (per1>=50.0){
			aRisk = 1.10;
		}else if (per1>=45.0){
			aRisk = 0.80;
		}else if (per1>=40.0){
			aRisk = 0.5;
		}else if (per1>=30.0){
			aRisk = 0.3;
		}else if (per1>=25.0){
			aRisk = 0.2;
		}
		
		aRisk = 0.0;
		if (per1>=30.0){
			aRisk = 0.50;
		}else if (per1>=50){
			aRisk = 1.25;
		}else if (per1>=70){
			aRisk = 2.75;
		}else if (per1>=80){
			aRisk = 8.5;
		}
		
		return aRisk;
		
	}
	
	public static double testPercentComplete(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,int min1,int min2,
			double tpf,int sl,int n,
			double per,
			int thr,
			int arange,
			int pipsThr,
			double maxDD,
			int minSl,
			boolean isFloating,
			boolean isReversing,
			//double aRisk,
			double maxOpenRisk,
			double aBalanceInicial,
			int debug
			){
		double balanceInicial = aBalanceInicial;
		double balance = aBalanceInicial;
		double maxBalance = balance;
		double equitity = balance;
		double maxBalanceDD = 0.0;
		double balanceAdded = 0;
		
		Calendar cal = Calendar.getInstance();
		
		int comm = 25;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		int mode = 0;
		int high = -1;
		int low = -1;
		int range = 800;
		int tp = (int) (range*tpf);
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		for (int i=n;i<data.size()-1;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			qLast = q;
			
			if (day!=lastDay){
				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-n,rangeArr.size()-1);
					
					tp = (int) (tpf*range);	
					if (tp<=40) tp=40;
				}
				
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			if (h>=h1 && h<=h2 && min>=min1 && min<=min2
					&& (high-low)>=arange
					){
				
		
				double avg = MathUtils.average(closeArr, i-n, i-1);
				avg = data.get(i-n).getOpen5();//n ultimas vars
				
				
				int diffUp = (int) (q.getOpen5()-avg);
				int diffDown = (int) (avg-q.getOpen5());
				
				double per1 = diffUp*100.0/range;
				double per2 = diffDown*100.0/range;
				
				int maxMin = maxMins.get(i-1);
				int diffdo = q.getOpen5()-doValue;
				double actualOpenRiskPer = actualOpenRisk*100.0/equitity;
				if (true
						//&& maxMin<=-thr
						&& mode>=0
						//&& diffdo<=-800
						&& per1>=per
						&& actualOpenRiskPer<=maxOpenRisk
						
						//&& per2>=per
						//&& per2>=per
						){
					//System.out.println(actualOpenRisk+" "+equitity+" "+actualOpenRiskPer);
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double aRisk = DaveTrade2019.getRisk(per1);
						
						if (aRisk>0){						
							double riskPosition = balance*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
							
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.SHORT);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							p.setOpenIndex(i);
							positions.add(p);
						}
					}else{
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double aRisk = DaveTrade2019.getRisk(per1);
						
						if (aRisk>0){	
							double riskPosition = balance*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
			
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.LONG);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							p.setOpenIndex(i);
							positions.add(p);
						
							if (debug==1){
								System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)
										+" "+entry
										+" "+PrintUtils.Print2dec(riskPip, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
							
							mode = 1;
						}
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}else if (true
						//&& maxMin>=thr
						&& mode<=0
						//&& diffdo>=800
						&& per2>=per
						//&& per1>=per
						&& actualOpenRiskPer<maxOpenRisk
						){
													
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double aRisk = DaveTrade2019.getRisk(per2);
						
						if (aRisk>0){	
							double riskPosition = equitity*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
							
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.LONG);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							positions.add(p);
						}
					}else{					
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double aRisk = DaveTrade2019.getRisk(per2);
						
						if (aRisk>0){	
							double riskPosition = equitity*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
							
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.SHORT);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							positions.add(p);
							
							if (debug==1){
								System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)
								+" "+entry
								+" "+PrintUtils.Print2dec(riskPip, false)
								+" "+PrintUtils.Print2dec(equitity, false)
								);
							}
							
							mode = -1;
						}
					}
					
																			
					//if (h==0) mode = -1;
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}	
			}
			
			
			int j = 0;
			int yr = y-y1;
			int ymr = yr*12+month;
			mWinPipsO.set(ymr, 0l);
			mLostPipsO.set(ymr, 0l);
			boolean closeAll = false;
			if (month!=lastCloseMonth){
				if (lastCloseMonth!=-1){					
					closeAll = true;
				}
				lastCloseMonth = month;
			}
			closeAll = false;
			equitity = balance;
			actualOpenRisk = 0;
			int closedLosses = 0;
			int totalClosedLossesPips = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;

						if (q.getHigh5()>=p.getTp()){
							pips = p.getTp()-p.getEntry()-comm;
							isClose = true;
						}else if (q.getLow5()<=p.getSl()){
							pips = p.getSl()-p.getEntry()-comm;
							isClose = true;
													
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (duration>=288 && floatingPips<=100
								&& h>=15 && h<=16
								){
							pips =floatingPips;
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						if (q.getLow5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry()-comm;
							isClose = true;
						}else if (q.getHigh5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry()-comm;
							isClose = true;
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (duration>=288 && floatingPips<=100
								&& h>=15 && h<=16
								){
							pips =floatingPips;
							//isClose = true;
						}
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							equitity += win$$;
							/*if (equitity>=maxBalance){
								maxBalance = equitity;
							}*/
							
							accPositions += p.getPip$$();
														
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							equitity += pip$$;
							/*double actualDD = 100.0-equitity*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}*/
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						//acumulamos floating
						if (floatingPips>=0){							
							int yo = y-y1;
							
							long ma = mWinPipsO.get(yo*12+month);
							mWinPipsO.set(yo*12+month, ma+floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[WIN floating] "+floatingPips+" ["+yo*12+month+"] "+(ma+floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							int yo = y-y1;
							
							long ma = mLostPipsO.get(yo*12+month);
							mLostPipsO.set(yo*12+month, ma-floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[LOSS floating] "+floatingPips+" ["+yo*12+month+"] "+(ma-floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						isClose = false;
						//trailing Stop
						if (p.getPositionType()==PositionType.LONG){
							//actualOpenRisk
							
							if (q.getClose5()>=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((q.getClose5()-p.getEntry())*(maxDD/100.0));
																							
								int newSl = p.getEntry()+pipsToTrail;
								if (newSl>=p.getSl() 
										&& newSl<=q.getClose5()-20
										&& newSl>=p.getEntry()+minSl
										){
									p.setSl(p.getEntry()+pipsToTrail);
								}	
							}
							actualOpenRisk += p.getPip$$()*(p.getEntry()-p.getSl())*0.1;
						}else if (p.getPositionType()==PositionType.SHORT){
							if (q.getClose5()<=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((-q.getClose5()+p.getEntry())*(maxDD/100.0));
								

								int newSl = p.getEntry()-pipsToTrail;
								if (newSl<=p.getSl() 
										&& newSl>=q.getClose5()+20
										&& newSl<=p.getEntry()-minSl
										){
									p.setSl(p.getEntry()-pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(-p.getEntry()+p.getSl())*0.1;
						}
						if (isClose){
							positions.remove(j);
						}else{
							j++;
						}
					}
				}
			}//positions
			
			
			double amount = equitity;
			
			if (amount<balanceInicial){
				balanceAdded += balanceInicial-amount ;
				balance += balanceInicial-amount ;
				equitity += balanceInicial-amount ;
			}
			
			if (amount>maxBalance){
				maxBalance = amount ;
				if (debug==1){
					System.out.println("[NEW MAXEQUITITY] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							);
				}
		    }
			
			double actualDD = 100.0-amount*100.0/maxBalance;
			if (actualDD>maxBalanceDD){
				maxBalanceDD = actualDD;
				if (debug==1){
					System.out.println("[NEW MAXDD] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							+" "+PrintUtils.Print2dec(balance, false)
							+" "+PrintUtils.Print2dec(maxBalance, false)
							+" "+PrintUtils.Print2dec(actualDD, false)
							+" || "+PrintUtils.Print2dec(actualOpenRisk, false)
							);
				}
			}
			
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		if (isFloating){
			int j = 0;
			q = q1;
			QuoteShort.getCalendar(cal, q1);
			month = cal.get(Calendar.MONTH);
			 y = cal.get(Calendar.YEAR);
			//System.out.println(month);
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						pips = q.getClose5()-p.getEntry()-comm;
						isClose = true;
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = -q.getClose5()+p.getEntry()-comm;
						isClose = true;
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							if (balance>=maxBalance){
								maxBalance = balance;
							}
							
							if (debug==1){
								System.out.println("[WIN] "+pips
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}else{
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}
							
							if (debug==1){
								System.out.println("[LOST] "+pips
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						j++;
					}
				}
			}
		}//isFloating
				
		int ywins=0;
		int yTotal = 0;
		for (int i=0;i<=(y2-y1);i++){
			double pf = yearWinPips.get(i)*1.0/yearLostPips.get(i);
			if (pf>=1.0
					|| (yearWinPips.get(i)>=0 && yearLostPips.get(i)==0) 
					){
				ywins++;
			}
			if (yearWinPips.get(i)>0 || yearLostPips.get(i)>0) yTotal++;
		}
		
		int mwins=0;
		int mTotal = 0;
		long accW = 0;
		long accL = 0;
		long lastPips = 0;
		long lastMonth = 0;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			double pf = mWinPips.get(i)*1.0/mLostPips.get(i);
			double pfreal = (mWinPips.get(i)+mWinPipsO.get(i))*1.0/(mLostPips.get(i)+mLostPipsO.get(i));
			boolean isWin = false;
			boolean isTotal = false;
			accW += mWinPips.get(i);
			accL += mLostPips.get(i);
			
			long accMonth = accW-accL+mWinPipsO.get(i)-mLostPipsO.get(i);
			
			long netMonth = accMonth-lastMonth;
			
			
			if (netMonth>0){
				mwins++;
				//System.out.println("[win] "+accMonth+" "+lastMonth);
			}else if (netMonth<0){
				//System.out.println("[loss] "+accMonth+" "+lastMonth);
			}
			lastMonth = accMonth;
			
			long global = mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i);
			if ((mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i))>0){				
				mTotal++;
				
				/*System.out.println(mWinPips.get(i)+" "+mWinPipsO.get(i)
					+" | "+mLostPips.get(i)+" "+mLostPipsO.get(i)
						+" || "+PrintUtils.Print2dec(pfreal, false)
						+" ||| "+accW+" "+accL
				);*/
			}
			
			/*if (isWin && !isTotal){
				System.out.println(mWinPips.get(i)
						+" "+mLostPips.get(i)
						+" "+mWinPipsO.get(i)
						+" "+mLostPipsO.get(i)
						+" || "+global
				);
			}*/
		}
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		//if (pf>=1.3 && avg>=3.8)
		double perDays = totalTradeDays*100.0/totalDays;
		double factor = perDays*1.0/pf;
		double fm = maxBalance/(balanceInicial+balanceAdded);
		
		ArrayList<Integer> hs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hs.add(0);
		int total = 0;
		for (int i=0;i<closedTimes.size();i++){
			long millis = closedTimes.get(i);
			cal.setTimeInMillis(millis);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int acc = hs.get(h);
			hs.set(h, acc+closedPips.get(i));
			total+=closedPips.get(i);
		}
		
		String values = "";
		for (int i=0;i<=23;i++){
			double pr = hs.get(i)*100.0/total;
			values = values +" "+PrintUtils.Print2dec(pr, false);
		}
		
		if (true && debug!=10
				&& perDays>=30.0
				&& maxBalanceDD<=30.0
				) 
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+PrintUtils.Print2dec(tpf, false)
				+" "+sl
				+" "+n
				//+" "+PrintUtils.Print2dec(per, false)				
				+" "+PrintUtils.Print2dec(maxDD, false)
				//+" "+minSl
				//+" "+PrintUtils.Print2dec(aRisk, false)
				+" "+PrintUtils.Print2dec(maxOpenRisk, false)
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				//+" "+PrintUtils.Print2dec(accPositions/trades, false)
				+" || "				
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(1.0/pf, false)
				//+" || "+mwins+" / "+mTotal
				+" || "+PrintUtils.Print2dec(perDays, false)
				//+" || "+PrintUtils.Print2dec(perDays*1.0/pf, false)
				+" ||| "
				+" "+PrintUtils.Print2dec2(balanceInicial, true)
				+" "+PrintUtils.Print2dec2(balanceInicial+balanceAdded, true)
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec((maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec2(maxBalance/(balanceInicial+balanceAdded), true)
				);	
		
		return maxBalance/(balanceInicial+balanceAdded);		
	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		
		//String pathEURUSD = path0+"eurusd_5 Mins_Bid_2009.01.01_2018.12.19.csv";
		
		//String pathEURUSD = path0+"EURUSD_15 Mins_Bid_2009.01.01_2018.12.12.csv";
		String pathEURUSD170 = path0+"EURUSD_Ticks_2017.01.01_2017.01.31.csv";
		String pathEURUSD1700 = path0+"EURUSD_Ticks_2017.02.01_2017.02.28.csv";
		String pathEURUSD171 = path0+"EURUSD_Ticks_2017.03.01_2017.03.31.csv";
		String pathEURUSD172 = path0+"EURUSD_Ticks_2017.04.01_2017.04.30.csv";
		String pathEURUSD173 = path0+"EURUSD_Ticks_2017.05.01_2017.05.30.csv";
		String pathEURUSD174 = path0+"EURUSD_Ticks_2017.06.01_2017.06.30.csv";
		String pathEURUSD175 = path0+"EURUSD_Ticks_2017.07.01_2017.07.31.csv";
		String pathEURUSD176 = path0+"EURUSD_Ticks_2017.08.01_2017.08.30.csv";
		String pathEURUSD177 = path0+"EURUSD_Ticks_2017.09.01_2017.09.30.csv";
		String pathEURUSD178 = path0+"EURUSD_Ticks_2017.10.01_2017.10.30.csv";
		String pathEURUSD179 = path0+"EURUSD_Ticks_2017.11.01_2017.11.30.csv";
		String pathEURUSD1710 = path0+"EURUSD_Ticks_2017.12.01_2017.12.26.csv";
		String pathEURUSD0 = path0+"EURUSD_Ticks_2018.01.01_2018.01.31.csv";
		String pathEURUSD00 = path0+"EURUSD_Ticks_2018.02.01_2018.02.28.csv";
		String pathEURUSD1 = path0+"EURUSD_Ticks_2018.03.01_2018.03.31.csv";
		String pathEURUSD2 = path0+"EURUSD_Ticks_2018.04.01_2018.04.30.csv";
		String pathEURUSD3 = path0+"EURUSD_Ticks_2018.05.01_2018.05.31.csv";
		String pathEURUSD4 = path0+"EURUSD_Ticks_2018.06.01_2018.06.30.csv";
		String pathEURUSD5 = path0+"EURUSD_Ticks_2018.07.01_2018.07.31.csv";
		String pathEURUSD6 = path0+"EURUSD_Ticks_2018.08.01_2018.08.30.csv";
		String pathEURUSD7 = path0+"EURUSD_Ticks_2018.09.01_2018.09.30.csv";
		String pathEURUSD8 = path0+"EURUSD_Ticks_2018.10.01_2018.10.30.csv";
		String pathEURUSD9 = path0+"EURUSD_Ticks_2018.11.01_2018.11.30.csv";
		String pathEURUSD10 = path0+"EURUSD_Ticks_2018.12.01_2018.12.26.csv";
		
		String pathEURUSD1801 = path0+"EURUSD_1 Min_Bid_2009.01.01_2018.12.26.csv";
		//EURUSD_Ticks_2018.07.01_2018.12.26.csv
		
		String pathEURUSD_bricks5 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_50_bricks.csv";
		String pathEURUSD_bricks10 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_100_bricks.csv";
		String pathEURUSD_bricks20 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_200_bricks.csv";
		String pathEURUSD_bricks40 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_400_bricks.csv";
		String pathEURUSD_bricks60 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_600_bricks.csv";
		String pathEURUSD_bricks80 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_800_bricks.csv";
		String pathEURUSD_bricks100 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1000_bricks.csv";
		String pathEURUSD_bricks150 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1500_bricks.csv";
		
		
		//String pathEURUSD = path0+"EURUSD_Ticks_2018.10.04_2018.10.04.csv_50_bricks.csv";
		
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		/*paths.add(pathEURUSD170);
		paths.add(pathEURUSD1700);
		paths.add(pathEURUSD171);
		paths.add(pathEURUSD172);
		paths.add(pathEURUSD173);
		paths.add(pathEURUSD174);
		paths.add(pathEURUSD175);
		paths.add(pathEURUSD176);
		paths.add(pathEURUSD177);
		paths.add(pathEURUSD178);
		paths.add(pathEURUSD179);
		paths.add(pathEURUSD1710);
		paths.add(pathEURUSD0);
		paths.add(pathEURUSD00);
		paths.add(pathEURUSD1);
		paths.add(pathEURUSD2);
		paths.add(pathEURUSD3);
		paths.add(pathEURUSD4);
		paths.add(pathEURUSD5);
		paths.add(pathEURUSD6);
		paths.add(pathEURUSD7);
		paths.add(pathEURUSD8);
		paths.add(pathEURUSD9);*/
		paths.add(pathEURUSD1801);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		//limit = 0;
		String provider ="";
		try {
			Sizeof.runGC ();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		//FFNewsClass.readNews(pathNews,news,0);
		ArrayList<QuoteShort> data = null;
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			Sizeof.runGC ();
			String path = paths.get(i);	
			
			dataI 		= new ArrayList<QuoteShort>();
			
			TickStudy2019.testSpreads();			
			//Tick.readFromDiskToQuoteShort(dataI, path, 6);			
		}

	}

}
