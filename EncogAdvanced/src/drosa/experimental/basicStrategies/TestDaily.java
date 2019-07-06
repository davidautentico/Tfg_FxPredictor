package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestDaily {
	
	public static void testHL(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2) {
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays = 0;
		int acc = 0;
		int cases = 0;
		int max = -1;
		int min = -1;
		int hmax = -1;
		int hmin = -1;
		int maxValue = -1;
		int count = 0;
		int minValue = -1;
		for (int i=0;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
					int hfirst = hmax;
					if (hmax>hmin) {
						hfirst = hmin;
					}
					
					if (hfirst>=h1 && hfirst<=h2) {
						if (hfirst==hmax) {
							acc += maxValue-q.getOpen5();
							count++;
						}else if (hfirst==hmin) {
							acc += q.getOpen5()-minValue;
							count++;
						}
					}
				}
				
				max=-1;
				min=-1;
				totalDays++;
				lastDay = day;
			}
			
			if (max==-1 || q.getHigh5()>=max) {
				max = q.getHigh5();
				hmax = h;
				maxValue = q.getHigh5();
			}
			
			if (min==-1 || q.getLow5()<=min) {
				min = q.getLow5();
				hmin = h;
				minValue = q.getLow5();
			}
		}
		
		double per = count*100.0/totalDays;
		double avg = acc*0.1/count;
		System.out.println(
				h1+" "+h2
				+" || "
				+" "+totalDays
				+" "+count
				+" "+PrintUtils.Print2dec(per, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	public static void testHL2(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Integer> hls,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDiff,
			int bePips
			) {
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays = 0;
		int acc = 0;
		int cases = 0;
		int max = -1;
		int min = -1;
		int hmax = -1;
		int hmin = -1;
		int maxValue = -1;
		int count = 0;
		int minValue = -1;
		int triesH = 0;
		int triesL = 0;
		int wins = 0;
		int losses = 0;
		int accdiff = 0;
		int accwin=0;
		for (int i=0;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
					int hfirst = hmax;
					if (hmax>hmin) {
						hfirst = hmin;
					}
					
					if (hfirst>=h1 && hfirst<=h2) {
						if (hfirst==hmax) {
							acc += maxValue-q.getOpen5();
							count++;
						}else if (hfirst==hmin) {
							acc += q.getOpen5()-minValue;
							count++;
						}
					}
				}
				
				max=-1;
				min=-1;
				totalDays++;
				lastDay = day;
			}
			
			int hl = hls.get(i);
			int maxMin = maxMins.get(i);
			if (max==-1 || q.getHigh5()>=max) {
				
				
				if (h>=h1 && h<=h2 
						&& maxMin>=thr
						) {
					
					if (true
						&& (q.getHigh5()-q.getClose5()<=minDiff)	
							) {
						if (hl>0) {
							wins++;
							accwin += hl-q.getHigh5()+q.getClose5();//q.gethigh5-close
						}else {
							losses++;
							accdiff += q.getHigh5()-q.getClose5();
						}
					}
				}
				max = q.getHigh5();
				hmax = h;
				maxValue = q.getHigh5();
			}
			
			if (min==-1 || q.getLow5()<=min) {
				if (h>=h1 && h<=h2
						&& maxMin<=-thr
						) {
					if (true
							&& (q.getClose5()-q.getLow5()<=minDiff)	
								) {
						if (hl<0) {
							wins++;
							accwin +=-(hl-q.getLow5()+q.getClose5());
						}else {
							losses++;
							accdiff += q.getClose5()-q.getLow5();
						}
					}
				}
				
				min = q.getLow5();
				hmin = h;
				minValue = q.getLow5();
			}
		}//for
		
		double per = count*100.0/totalDays;
		double avg = acc*0.1/count;
		
		int total = wins+losses;
		double winper = wins*100.0/total;
		double avgwin = accwin*0.1/wins;
		double avgloss = accdiff*0.1/losses;
		double pf = (avgwin*winper)/(avgloss*(100.0-winper));
		System.out.println(
				h1+" "+h2+" "+thr
				+" || "
				+" "+totalDays
				+" "+count
				+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(per, false)
				//+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(accwin*0.1/wins, false)
				+" || "+PrintUtils.Print2dec(winper, false)
				+" || "+PrintUtils.Print2dec(accdiff*0.1/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
		
	}
	
	
	public static void testHL3(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Integer> hls,
			int y1,int y2,
			ArrayList<MaxMinConfig> configs,
			int bePips,
			double risk,
			boolean isReverse,
			int debug
			) {
		
		double balanceInicial =10000;
		double balance =10000;
		double maxbalance = 10000;
		double maxdd = 0;
		double riskSL = risk;
		double balanceNeed=0;
		double win$$=0;
		double lost$$=0;
		
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays = 0;
		int acc = 0;
		int cases = 0;
		int max = -1;
		int min = -1;
		int hmax = -1;
		int hmin = -1;
		int maxValue = -1;
		int count = 0;
		int minValue = -1;
		int triesH = 0;
		int triesL = 0;
		int wins = 0;
		int losses = 0;
		int accdiff = 0;
		int accwin=0;
		int winPips = 0;
		int lostPips = 0;
		int mode = 0;
		int entry = 0;
		int entrySL = 0;
		int entryTP = 0;
		int entryDay = 0;
		int maxProfit = 0;
		int actualProfit = 0;
		int comm = 20;
		double pipvalue = 0;
		int dayOpen = 0;
		for (int i=0;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
					if (mode!=0
							&& day>=entryDay+0
							) {
						int pips = 0;
						if (mode==1) {
							pips = q.getOpen5()-entry;
						}else {
							pips = entry-q.getOpen5();
						}
						pips-=comm;//comm
						if (pips>=0) {
							winPips += pips;
							wins++;
							
							//balance
							int p = pips;
							balance = balance + p*pipvalue;
							if (balance>maxbalance) {
								maxbalance = balance;
							}
							
							win$$ += p*pipvalue;
							if (debug==1){
								System.out.println("[WIN OPEN DAY] "
									+wins+" "+losses+" || "
									+p+" "+winPips+" "+lostPips
									+" "+PrintUtils.Print2dec(pipvalue, false)
									+" || "+PrintUtils.Print2dec(win$$, false)
									+" || "+PrintUtils.Print2dec(lost$$, false)
									+" || "+PrintUtils.Print2dec(balance, false)
								);
							}
						}else {
							lostPips += -pips;
							losses++;
							
							//balance
							int p = -pips;
							balance = balance - p*pipvalue;
							if (balance<maxbalance) {
								double dd = 100.0-balance*100.0/maxbalance;
								if (dd>=maxdd) maxdd = dd;
							}
							
							lost$$ += p*pipvalue;
							
							if (debug==1){
								System.out.println("[LOST OPEN DAY] "
										+wins+" "+losses+" || "
										+p+" "+winPips+" "+lostPips
										+" "+PrintUtils.Print2dec(pipvalue, false)
										+" || "+PrintUtils.Print2dec(win$$, false)
										+" || "+PrintUtils.Print2dec(lost$$, false)
										+" || "+PrintUtils.Print2dec(balance, false)
								);
							}
						}
						mode=0;
					}
					
					if (balance<1000) {
						balanceNeed += 1000-balance;
						balance = 1000;
					}
				}
				
				//mode = 0;
				dayOpen = q.getOpen5();
				max=-1;
				min=-1;
				totalDays++;
				lastDay = day;
			}
			
			
			if (mode==1) {
				actualProfit = q.getClose5()-entry;
				if (actualProfit>=maxProfit) maxProfit=actualProfit;
				if (q.getLow5()<=entrySL
						) {
					int pips = entrySL-entry;
					
					pips-=comm;//comm
					if (pips>=0) {
						winPips += pips;
						wins++;
						
						//balance
						int p = pips;
						balance = balance + p*pipvalue;
						if (balance>maxbalance) {
							maxbalance = balance;
						}
						
						win$$ += p*pipvalue;
					}else {
						lostPips += -pips;
						losses++;
						
						//balance
						int p = -pips;
						balance = balance - p*pipvalue;
						if (balance<maxbalance) {
							double dd = 100.0-balance*100.0/maxbalance;
							if (dd>=maxdd) maxdd = dd;
						}
						
						lost$$ += p*pipvalue;
					}
					mode=0;
				} else if (entryTP>0 
						&& q.getHigh5()>=entryTP 					
						){
					int pips = entryTP-entry;
					
					pips-=comm;//comm
					if (pips>=0) {
						winPips += pips;
						wins++;
						
						//balance
						int p = pips;
						balance = balance + p*pipvalue;
						if (balance>maxbalance) {
							maxbalance = balance;
						}
						
						win$$ += p*pipvalue;
					}else {
						lostPips += -pips;
						losses++;
						
						//balance
						int p = -pips;
						balance = balance - p*pipvalue;
						if (balance<maxbalance) {
							double dd = 100.0-balance*100.0/maxbalance;
							if (dd>=maxdd) maxdd = dd;
						}
						
						lost$$ += p*pipvalue;
					}
					mode=0;
				}else if (maxProfit>=999999 && actualProfit<=maxProfit*0.5){
					int pips = 0;
					if (mode==1) {
						pips = q.getClose5()-entry;
					}else {
						pips = entry-q.getClose5();
					}
					pips-=comm;//comm
					if (pips>=0) {
						winPips += pips;
						wins++;
						
						//balance
						int p = pips;
						balance = balance + p*pipvalue;
						if (balance>maxbalance) {
							maxbalance = balance;
						}
						
						win$$ += p*pipvalue;
					}else {
						lostPips += -pips;
						losses++;
						
						//balance
						int p = -pips;
						balance = balance - p*pipvalue;
						if (balance<maxbalance) {
							double dd = 100.0-balance*100.0/maxbalance;
							if (dd>=maxdd) maxdd = dd;
						}
						
						lost$$ += p*pipvalue;
					}
					mode=0;
				}else if (actualProfit>=bePips) {
					entrySL = entry+30;
				}
			}else if (mode == -1) {
				actualProfit = entry-q.getClose5();
				if (actualProfit>=maxProfit) maxProfit=actualProfit;
				
				if (q.getHigh5()>=entrySL
						) {
					int pips = entry-entrySL;
					
					pips-=comm;//comm
					if (pips>=0) {
						winPips += pips;
						wins++;
						
						//balance
						int p = pips;
						balance = balance + p*pipvalue;
						if (balance>maxbalance) {
							maxbalance = balance;
						}
						
						win$$ += p*pipvalue;
					}else {
						lostPips += -pips;
						losses++;
						
						//balance
						int p = -pips;
						balance = balance - p*pipvalue;
						if (balance<maxbalance) {
							double dd = 100.0-balance*100.0/maxbalance;
							if (dd>=maxdd) maxdd = dd;
						}
						
						lost$$ += p*pipvalue;
					}
					mode=0;
				}else if (entryTP>0 
								&& q.getLow5()<=entryTP 					
								){
							int pips = entry-entryTP;
							
							pips-=comm;//comm
							if (pips>=0) {
								winPips += pips;
								wins++;
								
								//balance
								int p = pips;
								balance = balance + p*pipvalue;
								if (balance>maxbalance) {
									maxbalance = balance;
								}
								
								win$$ += p*pipvalue;
							}else {
								lostPips += -pips;
								losses++;
								
								//balance
								int p = -pips;
								balance = balance - p*pipvalue;
								if (balance<maxbalance) {
									double dd = 100.0-balance*100.0/maxbalance;
									if (dd>=maxdd) maxdd = dd;
								}
								
								lost$$ += p*pipvalue;
							}
							mode=0;
				}else if (maxProfit>=999999 && actualProfit<=maxProfit*0.5){
					int pips = 0;
					if (mode==1) {
						pips = q.getClose5()-entry;
					}else {
						pips = entry-q.getClose5();
					}
					pips-=comm;//comm
					if (pips>=0) {
						winPips += pips;
						wins++;
						
						//balance
						int p = pips;
						balance = balance + p*pipvalue;
						if (balance>maxbalance) {
							maxbalance = balance;
						}
						
						win$$ += p*pipvalue;
					}else {
						lostPips += -pips;
						losses++;
						
						//balance
						int p = -pips;
						balance = balance - p*pipvalue;
						if (balance<maxbalance) {
							double dd = 100.0-balance*100.0/maxbalance;
							if (dd>=maxdd) maxdd = dd;
						}
						
						lost$$ += p*pipvalue;
					}
					mode=0;
				}else if (actualProfit>=bePips) {
					entrySL = entry+30;
				}
			}
			
			if (balance<1000) {
				balanceNeed += 1000-balance;
				balance = 1000;
			}
			
			int maxMin = maxMins.get(i);
			if (max==-1 
					|| q.getHigh5()>=max
					|| maxMin>=configs.get(h).getThr1()
					) {
				if (configs.get(h).isActive()
						&& maxMin>=configs.get(h).getThr1()
						) {
					if (mode==0) {
						entry = q.getClose5();
						entrySL = q.getHigh5()+configs.get(h).getFilter();
						entryDay = day;
						mode = -1;
						
						if (!isReverse){
							entrySL =   q.getClose5()-configs.get(h).getSl();
							entryTP = q.getClose5()+configs.get(h).getTp();
							if (dayOpen>=entry){
								/*System.out.println("ALARMA "
										+dayOpen+" || "+maxMin
										+" || "+q.toString()
										);*/
								entrySL = q.getClose5()+configs.get(h).getTp();
							}
							entryDay = day;
							mode = 1;
						}
						
						maxProfit = 0;
						
						int pipsSL = entrySL-entry;
						if (!isReverse) pipsSL =entry-entrySL;
						
						double risk$$ = balance*(riskSL/100.0);
						pipvalue = risk$$/pipsSL;
					}
				}
				max = q.getHigh5();
				hmax = h;
				maxValue = q.getHigh5();
			}
			
			if (min==-1 
					|| q.getLow5()<=min
					|| maxMin<=-configs.get(h).getThr1()
					) {
				if (configs.get(h).isActive()
						&& maxMin<=-configs.get(h).getThr1()
						//&& (q.getClose5()-q.getLow5())<=minDiff
						) {
					if (mode==0) {
						entry = q.getClose5();
						entrySL = q.getLow5()-configs.get(h).getFilter();
						entryDay = day;
						mode = 1;
						
						if (!isReverse){
							entrySL =   q.getClose5()+configs.get(h).getSl();;
							entryTP = q.getClose5()-configs.get(h).getTp();
							if (dayOpen<=entry){
								/*System.out.println("ALARMA "
										+dayOpen+" || "+maxMin
										+" || "+q.toString()
										);*/
								entrySL = q.getClose5()+configs.get(h).getTp();
							}
							entryDay = day;
							mode = -1;
						}
						
						maxProfit = 0;
						
						int pipsSL = entry-entrySL;
						if (!isReverse){
							pipsSL = entrySL-entry;
							if (pipsSL<=0){
								System.out.println("ALARMA "+mode+" "
								+entry+" "+entrySL
								+" || "+q.toString()+" || "+configs.get(h).getTp()
								);
							}
						}
						double risk$$ = balance*(riskSL/100.0);
						pipvalue = risk$$/pipsSL;
					}
				}
				
				min = q.getLow5();
				hmin = h;
				minValue = q.getLow5();
			}
		}//for
		
		double per = count*100.0/totalDays;
		double avg = acc*0.1/count;
		
		int total = wins+losses;
		double winper = wins*100.0/total;
		double avgwin =winPips*0.1/wins;
		double avgloss = lostPips*0.1/losses;
		double pf = winPips*1.0/lostPips;
		double pf$$ = win$$/lost$$;
		avg = (winPips-lostPips)*0.1/total;
		System.out.println(
				header
				//h1+" "+h2+" "+thr+" "+filter+" "+bePips
				+" || "
				+" "+totalDays
				//+" "+count
				+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winper, false)
				+" "+PrintUtils.Print2dec(avgwin, false)
				+" "+PrintUtils.Print2dec(avgloss, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(win$$,false)+" "+PrintUtils.Print2dec(lost$$,false)
				//+" "+PrintUtils.Print2dec(accwin*0.1/wins, false)
				//+" || "+PrintUtils.Print2dec(winper, false)
				//+" || "+PrintUtils.Print2dec(accdiff*0.1/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(pf$$, false)
				+" ||| "
				+" "+PrintUtils.Print2dec2(balanceInicial+balanceNeed, true)
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec(maxdd, false)
				);
		
	}
	
	public static void testHL4(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int thr,
			int tp,
			int sl,
			int bars,
			int minPips,
			double risk,
			boolean isReverse,
			int debug
			) {
		
		double balanceInicial =10000;
		double balance =10000;
		double maxbalance = 10000;
		double maxdd = 0;
		double riskSL = risk;
		double balanceNeed=0;
		double win$$=0;
		double lost$$=0;
		
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays = 0;
		int acc = 0;
		int cases = 0;
		int max = -1;
		int min = -1;
		int hmax = -1;
		int hmin = -1;
		int maxValue = -1;
		int count = 0;
		int minValue = -1;
		int triesH = 0;
		int triesL = 0;
		int wins = 0;
		int losses = 0;
		int accdiff = 0;
		int accwin=0;
		int winPips = 0;
		int lostPips = 0;
		int mode = 0;
		int entry = 0;
		int entrySL = 0;
		int entryTP = 0;
		int entryDay = 0;
		int maxProfit = 0;
		int actualProfit = 0;
		int comm = 20;
		double pipvalue = 0;
		int dayOpen = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int thrValue = 0;
		int thrMode = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int n= 200;
		for (int i=n;i<data.size();i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
															
				}
				
				//mode = 0;
				dayOpen = q.getOpen5();
				max=-1;
				min=-1;
				totalDays++;
				thrValue = 0;
				thrMode = 0;
				lastDay = day;				
			}
			
			int  maxMin = maxMins.get(i-1);
			int m = minPips;
			if (true					
					//&& thrMode==1
					&& maxMin>=thr
					){
				if (true						
						&& h>=h1 && h<=h2
						//&& q1.getHigh5()-q.getOpen5()>=50
						//&& q.getOpen5()-data.get(i-bars).getOpen5()>=minPips
						){
					PositionShort pos = new PositionShort();
					pos.setEntry(q.getOpen5());
					
					pos.setPositionStatus(PositionStatus.OPEN);
					
					pos.setPositionType(PositionType.LONG);
					pos.setSl(q.getOpen5()-sl);
					pos.setTp(q.getOpen5()+tp);
					if (isReverse){
						pos.setPositionType(PositionType.SHORT);
						pos.setSl(q.getOpen5()+sl);
						pos.setTp(q.getOpen5()-tp);
					}
					
					positions.add(pos);
				}
			}else if (maxMin<=-thr){
				if (true
						&& h>=h1 && h<=h2
						//&& q.getOpen5()-q1.getLow5()>=50
						//&& data.get(i-bars).getOpen5()-q.getOpen5()>=minPips
						){
					PositionShort pos = new PositionShort();
					pos.setEntry(q.getOpen5());
					pos.setPositionStatus(PositionStatus.OPEN);					
					pos.setPositionType(PositionType.SHORT);
					pos.setSl(q.getOpen5()+sl);
					pos.setTp(q.getOpen5()-tp);
					if (isReverse){												
						pos.setPositionType(PositionType.LONG);
						pos.setSl(q.getOpen5()-sl);
						pos.setTp(q.getOpen5()+tp);
					}
					positions.add(pos);
				}
			}
			
			int j=0;			
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean isClosed = false;
				int pips = 0;
				long microLots = 0;
				int pipsSL = 0;
				int floatingPips  = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN 
						//&& i>p.getOpenIndex()
						){		
					microLots = p.getMicroLots();
					pipsSL = Math.abs(p.getEntry()-p.getSl());
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;
						if (q.getLow5()<=p.getSl()){
							pips = -(p.getEntry()-p.getSl());
							isClosed = true;
							 
							if (debug==2) {
								System.out.println("[CLOSED LONG SL] "+DateUtils.datePrint(cal)
								+" "+pips
								+" || "+q.toString()
								);
							}
						}else if (q.getHigh5()>=p.getTp()){
							pips = p.getTp()-p.getEntry();
							isClosed = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = p.getEntry()-q.getClose5()-comm;
						if (q.getHigh5()>=p.getSl()){
							pips = -(p.getSl()-p.getEntry());
							isClosed = true;
							if (debug==2) {
								System.out.println("[CLOSED SHORT SL] "+DateUtils.datePrint(cal)
								+" "+q.getClose5()
								+" "+pips
								);
							}
						}else if (q.getLow5()<=p.getTp()){
							pips = p.getEntry()-p.getTp();
							isClosed = true;
						}
					}
				}
				
				if (isClosed){
					double dd = 0.0;
					pips -= comm;
					double profit = pips*0.1*p.getMicroLots()*0.1;
					if (profit>=0) {
						win$$ += profit;
					}else {
						lost$$ +=-profit;
					}					
					balance += pips*0.1*p.getMicroLots()*0.1;															
					if (pips>=0){
						winPips += pips;
						wins++;
						actualLosses = 0;	
						//System.out.println("winPips: "+pips+" "+winPips);
					}else{
						lostPips += -pips;
						losses++;
						actualLosses++;
						if (actualLosses>=maxLosses){
							maxLosses = actualLosses;							
						}												
					}					
					positions.remove(j);
				}else{								
					j++;
				}
			}
			
			 maxMin = maxMins.get(i-1);
			if (true
					&& thrMode==0
					&& h>=h1 && h<=h2
					){
				if (maxMin>=thr){
					thrValue = q.getHigh5();
					thrMode = 1;
				}else if (maxMin<=-thr){
					thrValue = q.getLow5();
					thrMode = -1;
				}
			}
			
		}//for
		
		double per = count*100.0/totalDays;
		double avg = acc*0.1/count;
		
		int total = wins+losses;
		double winper = wins*100.0/total;
		double avgwin =winPips*0.1/wins;
		double avgloss = lostPips*0.1/losses;
		double pf = winPips*1.0/lostPips;
		double pf$$ = win$$/lost$$;
		avg = (winPips-lostPips)*0.1/total;
		System.out.println(
				header
				//h1+" "+h2+" "+thr+" "+filter+" "+bePips
				+" || "
				+" "+total
				//+" "+count
				//+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winper, false)
				+" "+PrintUtils.Print2dec(avgwin, false)
				+" "+PrintUtils.Print2dec(avgloss, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+winPips+" "+lostPips
				//+" "+PrintUtils.Print2dec(win$$,false)+" "+PrintUtils.Print2dec(lost$$,false)
				//+" "+PrintUtils.Print2dec(accwin*0.1/wins, false)
				//+" || "+PrintUtils.Print2dec(winper, false)
				//+" || "+PrintUtils.Print2dec(accdiff*0.1/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(pf$$, false)
				+" ||| "
				//+" "+PrintUtils.Print2dec2(balanceInicial+balanceNeed, true)
				//+" "+PrintUtils.Print2dec2(balance, true)
				//+" "+PrintUtils.Print2dec(maxdd, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		//String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
		String path0 ="C:\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
		//String pathEURUSD = path0+"eurusd_UTC_15 Mins_Bid_2003.12.31_2017.11.25.csv";
		String pathEURUSD = path0+"usdjpy_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_1 Min_Bid_2013.12.31_2017.11.22.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
		String pathNews = path0+"News.csv";
		
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
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		FFNewsClass.readNews(pathNews,news,0);
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);

			ArrayList<Integer> hls = TestMaxMins.getHighsLows(data);
			
			ArrayList<MaxMinConfig> configs = new 	ArrayList<MaxMinConfig>();
			for (int c=0;c<=23;c++){
				MaxMinConfig config = new MaxMinConfig();
				config.setActive(false);
				config.setThr1(500);
				config.setFilter(500);
				configs.add(config);				
			}
			
			configs.get(0).setActive(true);
			configs.get(1).setActive(true);
			configs.get(2).setActive(true);
			configs.get(3).setActive(true);
			configs.get(4).setActive(true);
			configs.get(5).setActive(true);
			configs.get(6).setActive(true);
			configs.get(7).setActive(true);
			configs.get(8).setActive(true);
			configs.get(9).setActive(true);
			
			configs.get(0).setThr1(400);
			configs.get(0).setFilter(100);
			configs.get(1).setThr1(500);
			configs.get(1).setFilter(100);
			configs.get(2).setThr1(400);
			configs.get(2).setFilter(450);
			configs.get(3).setThr1(900);
			configs.get(3).setFilter(200);
			configs.get(4).setThr1(800);
			configs.get(4).setFilter(500);
			configs.get(5).setThr1(800);
			configs.get(5).setFilter(100);
			configs.get(6).setThr1(500);
			configs.get(6).setFilter(300);
			configs.get(7).setThr1(500);
			configs.get(7).setFilter(300);
			configs.get(8).setThr1(600);
			configs.get(8).setFilter(400);
			configs.get(9).setThr1(1200);
			configs.get(9).setFilter(100);
			

			for (int c=0;c<=23;c++){
				configs.get(c).setActive(false);
			}
			
			//not reverse
			configs.get(11).setThr1(1500);
			configs.get(11).setFilter(400);
			configs.get(11).setTp(400);
			configs.get(11).setSl(500);
			//configs.get(11).setActive(true);
			
			configs.get(15).setThr1(8000);
			configs.get(15).setFilter(1200);
			configs.get(15).setTp(1200);
			configs.get(15).setSl(600);
			//configs.get(15).setActive(true);
			
			configs.get(16).setThr1(1700);
			configs.get(16).setFilter(1100);
			configs.get(16).setTp(1100);
			configs.get(16).setSl(700);
			configs.get(16).setActive(true);
			
			configs.get(17).setThr1(3500);
			configs.get(17).setFilter(900);
			configs.get(17).setTp(900);
			configs.get(17).setSl(600);
			configs.get(17).setActive(true);
			
			configs.get(20).setThr1(2000);
			configs.get(20).setFilter(700);
			configs.get(20).setTp(700);
			configs.get(20).setSl(500);
			//configs.get(20).setActive(true);
			
			configs.get(21).setThr1(7000);
			configs.get(21).setFilter(1200);
			configs.get(21).setTp(1200);
			configs.get(21).setSl(500);
			//configs.get(21).setActive(true);
			
			configs.get(22).setThr1(6500);
			configs.get(22).setFilter(600);
			configs.get(22).setTp(600);
			configs.get(22).setSl(900);
			//configs.get(22).setActive(true);
			
			
			/*for (int y1=2004;y1<=2004;y1++) {
				int y2 = y1+13;
				for (int h1=16;h1<=16;h1++) {
					int h2 = h1+0;
					for (int thr=3000;thr<=3000;thr+=50) {
						
						for (int minDiff=999;minDiff<=999;minDiff+=10) {
							for (int filter=1200;filter<=1200;filter+=100) {
								for (int sl=1500;sl<=1500;sl+=100) {
									for (int bePips=999;bePips<=999;bePips+=10){
										for (double risk=1.0;risk<=20.0;risk+=0.5){
											//TestDaily.testHL(data, maxMins, 2009, 2017, h1, h2);
											String header = "";
											for (h1=16;h1<=17;h1++){
												configs.get(h1).setActive(true);
												configs.get(h1).setThr1(thr);
												configs.get(h1).setFilter(filter);
												configs.get(h1).setTp(filter);
												configs.get(h1).setSl(sl);
												header = ""+h1+" "+thr+" "+filter+" "+sl;
											}
											
											
											TestDaily.testHL3(header,data, maxMins,hls, y1, y2, 
													configs
													//h1, h2,thr,minDiff,filter
													,bePips,risk,
													false,
													0
													);
										}
									}//be
								}//sl
							}
						}
					}
				}
			}*/
			
			double risk = 1.0;
			for (int y1=2009;y1<=2009;y1++) {
				int y2 = y1+8;
				for (int h1=16;h1<=16;h1++) {
					int h2 = h1+6;
					for (int thr=0;thr<=2000;thr+=25) {
						for (int tp=150;tp<=150;tp+=10){
							for (int sl=3000;sl<=3000;sl+=1*tp){
								for (int bars=1;bars<=1;bars+=1){
									for (int minPips=10;minPips<=10;minPips+=10){
										String header = ""+h1+" "+thr+" "+tp+" "+sl;
										TestDaily.testHL4(header,data, 
												maxMins,y1, y2,h1,h2, 
												thr,tp,sl,
												bars,minPips,
												risk,
												false,
												0
										);
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
