package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import drosa.experimental.PositionShort;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMeanReversionTicks {
	
	public static void doTestAlphadudeStratsTicks(
			String header,
			ArrayList<Tick> data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			double aF,
			ArrayList<String> strat,//
			ArrayList<Integer> dayPipsArr,
			boolean isMomentum,
			double aRisk,
			int debug,
			boolean printDetails
			){
		
		Calendar cal = Calendar.getInstance();
		
		double initialBalance = 5000;
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double equitity = initialBalance;
		double maxEquitity = initialBalance;
		
		int comm = 0;
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
		
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		/*for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}*/
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		HashMap<Integer,ArrayList<Integer>> yTrades = new HashMap<Integer,ArrayList<Integer>>();
		HashMap<Integer,ArrayList<Integer>> mTrades = new HashMap<Integer,ArrayList<Integer>>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();

		int dayTrade = 0;
		int totalDaysTrade = 0;
		int mode20 = 0;
		int modeIdx20 = 0;
		int mode30 = 0;
		int modeIdx30 = 0;
		int mode40 = 0;
		int modeIdx40 = 0;
		int mode50 = 0;
		int modeIdx50 = 0;
		int mode60 = 0;
		int modeIdx60 = 0;
		int mode70 = 0;
		int modeIdx70 = 0;
		int mode80 = 0;
		int modeIdx80 = 0;
		int mode90 = 0;
		int modeIdx90 = 0;
		int mode100 = 0;
		int modeIdx100 = 0;
		int dayPips = 0;
		int lastPips = 0;
		
		ArrayList<Integer> ns = new ArrayList<Integer> ();
		ArrayList<Integer> nbars = new ArrayList<Integer> ();
		for (int i=0;i<strat.size();i++){
			String[] values = strat.get(i).split(" ");
			ns.add(Integer.valueOf(values[0]));
			nbars.add(Integer.valueOf(values[1]));
			//System.out.println(ns.get(i)+" "+nbars.get(i));
		}
	
		//guardamos valor actual de minutos y el minuto en cuestion
		int actualMin = data.get(0).getMm();		
		closeArr.add(data.get(0).getBid());
		
		for (int i=1;i<data.size()-2;i++){
			Tick t1 = data.get(i-1);
			Tick t = data.get(i);
			
			 y = t.getYear();
			int m = t.getMonth();
			int day = t.getDay();
			int h = t.getHh();
			int min = t.getMm();
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
						
			comm = 00;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
					int diffP = dayPips-lastPips;
					dayPipsArr.add(diffP);
				}			
				
				if (dayTrade==1) totalDaysTrade++;
				dayTrade = 0;
				high = -1;
				low = -1;
				lastDay = day;
				dayPips = 0;
				totalDays++;
			}
			//uso el bid como referencia para rangos
			if (high==-1 || t.getBid()>=high) high = t.getBid();
			if (low==-1 || t.getBid()<=low) low = t.getBid();	
			
			if (min!=actualMin){
				openArr.add(t.getBid());
				actualMin = min;
			}
			
			
			int smaValue20 = (int) MathUtils.average(openArr, openArr.size()-20*1,openArr.size()-1);
			int smaValue30 = (int) MathUtils.average(openArr, openArr.size()-30*1,openArr.size()-1);
			int smaValue40 = (int) MathUtils.average(openArr, openArr.size()-40*1,openArr.size()-1);
			int smaValue50 = (int) MathUtils.average(openArr, openArr.size()-50*1,openArr.size()-1);
			int smaValue60 = (int) MathUtils.average(openArr, openArr.size()-60*1,openArr.size()-1);
			int smaValue70 = (int) MathUtils.average(openArr, openArr.size()-70*1,openArr.size()-1);
			int smaValue80 = (int) MathUtils.average(openArr, openArr.size()-80*1,openArr.size()-1);
			int smaValue90 = (int) MathUtils.average(openArr, openArr.size()-90*1,openArr.size()-1);
			int smaValue100 = (int) MathUtils.average(openArr, openArr.size()-100*1,openArr.size()-1);
			
			if (t.getBid()>=smaValue20){				
				if (mode20<=0) modeIdx20 = i;
				mode20 = 1;
			}else{
				if (mode20>=0) modeIdx20 = i;
				mode20 = -1;
			}
			
			if (t.getBid()>=smaValue30){				
				if (mode30<=0) modeIdx30 = i;
				mode30 = 1;
			}else{
				if (mode30>=0) modeIdx30 = i;
				mode30 = -1;
			}
			
			if (t.getBid()>=smaValue40){				
				if (mode40<=0) modeIdx40 = i;
				mode40 = 1;
			}else{
				if (mode40>=0) modeIdx40 = i;
				mode40 = -1;
			}
			
			if (t.getBid()>=smaValue50){				
				if (mode50<=0) modeIdx50 = i;
				mode50 = 1;
			}else{
				if (mode50>=0) modeIdx50 = i;
				mode50 = -1;
			}
			
			if (t.getBid()>=smaValue60){				
				if (mode60<=0) modeIdx60 = i;
				mode60 = 1;
			}else{
				if (mode60>=0) modeIdx60 = i;
				mode60 = -1;
			}
			
			if (t.getBid()>=smaValue70){				
				if (mode70<=0) modeIdx70 = i;
				mode70 = 1;
			}else{
				if (mode70>=0) modeIdx70 = i;
				mode70 = -1;
			}
			
			if (t.getBid()>=smaValue80){				
				if (mode80<=0) modeIdx80 = i;
				mode80 = 1;
			}else{
				if (mode80>=0) modeIdx80 = i;
				mode80 = -1;
			}
			
			if (t.getBid()>=smaValue90){				
				if (mode90<=0) modeIdx90 = i;
				mode90 = 1;
			}else{
				if (mode90>=0) modeIdx90 = i;
				mode90 = -1;
			}
			
			if (t.getBid()>=smaValue100){				
				if (mode100<=0) modeIdx100 = i;
				mode100 = 1;
			}else{
				if (mode100>=0) modeIdx100 = i;
				mode100 = -1;
			}
					
			
			//valor de la sma
			for (int z = 0;z<strat.size();z++){
				int n = ns.get(z);
				int bars = nbars.get(z);
				ishOk = h>=h1 && h<=h2;
				
				int modeIdx = -1;
				int mode = -1;
				int smaValue = -1;
				
				if (n==50){
					modeIdx = modeIdx50;
					mode = mode50;
					smaValue = smaValue50;
				}else if (n==60){
					modeIdx = modeIdx60;
					mode = mode60;
					smaValue = smaValue60;
				}else if (n==70){
					modeIdx = modeIdx70;
					mode = mode70;
					smaValue = smaValue70;
				}else if (n==80){ 
					modeIdx = modeIdx80;
					mode = mode80;
					smaValue = smaValue80;
				}else if (n==20){
					modeIdx = modeIdx20;
					mode = mode20;
					smaValue = smaValue20;
				}else if (n==30){
					modeIdx = modeIdx30;
					mode = mode30;
					smaValue = smaValue30;
				}else if (n==40){
					modeIdx = modeIdx40;
					mode = mode40;
					smaValue = smaValue40;
				}else if (n==90){
					modeIdx = modeIdx90;
					mode = mode90;
					smaValue = smaValue90;
				}else if (n==100){
					modeIdx = modeIdx100;
					mode = mode100;
					smaValue = smaValue100;
				}
				
				
				if (ishOk
						&& positions.size()<=100
						){		
					//System.out.println("trade..");
					
					int dist = i-modeIdx;
					int minPips = (int) (aF*range);
					int slMinPips = (int) (1.0*range);
					int transactionCosts = TradingUtils.getTransactionCosts(y, h,true);
															
					if (mode==1 
							&& modeIdx>0 
							&& dist>=bars//si la candle es la suya
							&& t.getBid()-smaValue>=minPips
							){
					//if (spread<=-minPips){
						int entry = t.getBid();//vendo en el BID
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setMaxProfit(entry);
						
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
						
						p.setPositionType(PositionType.SHORT);
						p.setTp(p.getEntry()-100000);
						p.setSl((int) (p.getEntry()+0.6*range));
						if (isMomentum){
							p.setPositionType(PositionType.LONG);
							p.setTp(p.getEntry()+ 100 *minPips);
							p.setSl(p.getEntry()-minPips);
						}
						
						minPips = p.getSl()-p.getEntry();
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(minPips*0.1);
						int microLots = (int) (riskPip/0.10);
						p.setMicroLots(microLots);
						p.setTransactionCosts(transactionCosts);
						p.setExtraParam(n);
					
					
						dayTrade = 1;
						positions.add(p);
					}else if (mode==-1
							&& modeIdx>0 
							&& dist>=bars
							&& -t.getAsk()+smaValue>=minPips
							){
					//}else if(spread>=minPips){
						int entry = t.getAsk();//compro en el ASK
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setMaxProfit(entry);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
						
						p.setPositionType(PositionType.LONG);
						p.setTp(p.getEntry()+100000);
						p.setSl((int) (p.getEntry()-0.6*range));
						if (isMomentum){
							p.setPositionType(PositionType.SHORT);
							p.setTp(p.getEntry()- 999 *minPips);
							p.setSl(p.getEntry()+minPips);
						}
						
						minPips = p.getEntry()-p.getSl();
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(minPips*0.1);
						int microLots = (int) (riskPip/0.10);
						if (microLots<1) microLots = 1;
						p.setMicroLots(microLots);
						p.setTransactionCosts(transactionCosts);
						
						dayTrade = 1;
						positions.add(p);
					}
				}//H
			}
			
			int j = 0;
			boolean closeAll = false;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					int tcosts = p.getTransactionCosts();
					int n = p.getExtraParam();
					//int smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);
					
					int modeIdx = modeIdx50;
					int mode = mode50;
					int smaValue = smaValue50;
					
					if (n==20){
						modeIdx = modeIdx20;
						mode = mode20;
						smaValue = smaValue20;
					}
					if (n==30){
						modeIdx = modeIdx30;
						mode = mode30;
						smaValue = smaValue30;
					}
					if (n==40){
						modeIdx = modeIdx40;
						mode = mode40;
						smaValue = smaValue40;
					}
					if (n==50){
						modeIdx = modeIdx50;
						mode = mode50;
						smaValue = smaValue50;
					}
					if (n==60){
						modeIdx = modeIdx60;
						mode = mode60;
						smaValue = smaValue60;
					}
					if (n==70){
						modeIdx = modeIdx70;
						mode = mode70;
						smaValue = smaValue70;
					}
					if (n==80){ 
						modeIdx = modeIdx80;
						mode = mode80;
						smaValue = smaValue80;
					}
					if (n==90){ 
						modeIdx = modeIdx90;
						mode = mode90;
						smaValue = smaValue90;
					}
					if (n==100){ 
						modeIdx = modeIdx100;
						mode = mode100;
						smaValue = smaValue100;
					}
										
					boolean isClose = false;					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  t.getBid()-p.getEntry();
						if (mode==1
								){
							pips =  t.getBid()-p.getEntry();
							isClose = true;
						}

						if (!isClose){
							//time exits
							if (h==23 && min==55){
								pips =  t.getBid()-p.getEntry();
								isClose = true;
							}
							if (t.getBid()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
							}else if (t.getBid()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
							}else if (t.getBid()-p.getEntry()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(t.getBid()-p.getEntry()));
									int newSl = p.getEntry()+toTrail;
									if (newSl>=p.getSl() && t.getBid()-newSl>=20) p.setSl(newSl);
								}
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-t.getAsk();
						if (mode==-1
								){
							pips = p.getEntry()-t.getAsk();
							isClose = true;
						}
						
						if (!isClose){
							//time exits
							if (h==23 && min==55){
								pips = p.getEntry()-t.getAsk();
								isClose = true;
							}
							if (t.getAsk()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
							}else if (t.getAsk()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
							}else if (p.getEntry()-t.getAsk()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(-t.getAsk()+p.getEntry()));
									int newSl = p.getEntry()-toTrail;
									if (newSl<=p.getSl() && -t.getAsk()+newSl>=20) p.setSl(newSl);
								}
							}
						}
					}
					
					if (isClose){
						
						//tcosts = 0;
						pips-=tcosts;
						
						if (!yTrades.containsKey(y)) yTrades.put(y,new ArrayList<Integer>());
						ArrayList<Integer> trades = yTrades.get(y);
						trades.add(pips);
						
						//por mes
						if (!mTrades.containsKey(y)){
							mTrades.put(y,new ArrayList<Integer>());
							for (int w=0;w<=11;w++){
								mTrades.get(y).add(0);
							}
						}						
						trades = mTrades.get(y);
						int accm = trades.get(month);
						trades.set(month, accm+pips);
						
						dayPips += pips;
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
							int ya = yWinPips.get(y);
							yWinPips.put(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)+" || "+y
										//+" "+PrintUtils.Print2dec(win$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							//totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
							int ya = yLostPips.get(y);
							yLostPips.put(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							//double pip$$ = p.getPip$$()*pips*0.1;
							//balance += pip$$;
							//equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										//+" "+PrintUtils.Print2dec(pip$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						
						balance += p.getMicroLots()*0.10*pips*0.10;
						if (balance<=maxBalance){
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxDD) maxDD = actualDD;
						}else{
							maxBalance = balance;
						}
																		
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions
		}
		
		//estudio de years
		int posYears = 0;
		double accPf = 0;
		int countPf = 0;
		List sortedKeys=new ArrayList(yTrades.keySet());
		Collections.sort(sortedKeys);
		
		for (int k=0;k<sortedKeys.size();k++){		
		//Iterator it = yTrades.entrySet().iterator();
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = yTrades.get(year);//pair.getValue();
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);
	        	
	        	if (pips>=0) wPips+=pips;
	        	else lPips-=pips;
	        }
	        
	        double yPf = wPips*1.0/lPips;
	        int netPips = wPips-lPips;
	        double avgPips = (wPips-lPips)*0.1/trades.size();
	        if (avgPips>=1.0) posYears++;//al menos un pip de margen
	        if (lPips>0){
	        	accPf += wPips*1.0/lPips;
	        	countPf++;
	        	if (printDetails)
	        	System.out.println(year
	        			+" avgpf= "+PrintUtils.Print2dec(wPips*1.0/lPips, false)
	        			+" "+trades.size()
	        			+" "+PrintUtils.Print2dec(avgPips, false)
	        			+" "+wPips
	        			+" "+lPips
	        			);
	        }else if (wPips>0 && lPips==0){
	        	accPf += 2.0;
	        	countPf++;
	        	//posYears++;
	        	if (printDetails)
		        	System.out.println(year
		        			+" avgpf= -----"
		        			+" "+trades.size()
		        			+" "+PrintUtils.Print2dec(avgPips, false)
		        			+" "+wPips
		        			+" "+lPips
		        			);
	        }
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int posMonths = 0;
		int negMonths = 0;
		int totalMonths = 0;
		sortedKeys.clear();
		sortedKeys=new ArrayList(mTrades.keySet());
		Collections.sort(sortedKeys);
		//it = mTrades.entrySet().iterator();
		for (int k=0;k<sortedKeys.size();k++){	
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = mTrades.get(year);
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);	        	
	        	if (pips>0) posMonths++;
	        	if (pips!=0) totalMonths++;
	        }	      
	       // it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		double avgPf = accPf/countPf;
		
		if (debug==2
				|| (avg>=1.0 
				//&& pf>=2.0
				&& posYears>=7 
				&& ff>=7.0
				&& trades>=300 
				&& perDays>=0.0)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "
				+" "+PrintUtils.Print2dec(posMonths*100.0/totalMonths, false)
				+" "+posYears
				+" "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(avgPf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				);
	}

	public static void main(String[] args) {
		
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
		
		String pathEURUSD19 = path0+"EURUSD_Ticks_2019.01.01_2019.04.02.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD04);paths.add(pathEURUSD05);paths.add(pathEURUSD06);paths.add(pathEURUSD07);
		paths.add(pathEURUSD08);paths.add(pathEURUSD09);paths.add(pathEURUSD10);paths.add(pathEURUSD11);
		paths.add(pathEURUSD12);paths.add(pathEURUSD13);paths.add(pathEURUSD14);paths.add(pathEURUSD15);
		paths.add(pathEURUSD16);paths.add(pathEURUSD17);paths.add(pathEURUSD18);paths.add(pathEURUSD19);
		
		ArrayList<QuoteShort> dataI 		= new ArrayList<QuoteShort>();
		ArrayList<QuoteShort> dataS 		= null;
		Calendar cal = Calendar.getInstance();
		for (int i=paths.size()-1;i<=paths.size()-1;i++){	
			String path = paths.get(i);			
			dataI.clear();
			Tick.readFromDiskToQuoteShort(dataI, path, 7);
			TestLines.calculateCalendarAdjustedSinside(dataI);
		}
		
		
		
		ArrayList<String> strat4 = new ArrayList<String>();
		//para 0 y 2: 20-90 y 0
		strat4.add("20 0");
		//strat4.add("40 0");
		//strat4.add("50 0");
		//strat4.add("60 0");
		//strat4.add("70 0");
		//strat4.add("80 0");
		//strat4.add("90 0");
		//strat4.add("100 0");
		ArrayList<Integer> dayPips1 = new ArrayList<Integer>();
		for (double fMinPips=0.20;fMinPips<=0.20;fMinPips+=0.10){
			for (double aRisk = 0.10;aRisk<=0.10;aRisk+=0.10){						
				for (int y1=2009;y1<=2009;y1++){
					int y2 = y1+10;
					for (int n=20;n<=20;n+=10){
						for (int bars=0;bars<=0;bars+=2){
							String params1 = n+" "+bars;
							//strat4.set(0, params1);
							//String params2 = "50 "+bars;
							//strat4.set(1, params2);
							//String params3 = "60 "+bars;
							//strat4.set(2, params3);
							//String params4 = "70 "+bars;
							//strat4.set(3, params4);
							String str = params1+" "+PrintUtils.Print2dec(fMinPips, false);
							for (int h1=0;h1<=0;h1++){
								int h2 = h1+23;
								str = h1+" "+h2+" "+params1+" "+PrintUtils.Print2dec(fMinPips, false);
								TestMeanReversion.doTestAlphadudeStratsTicks(str, dataI, y1, y2, 0, 11,h1,h2,fMinPips, strat4,dayPips1,false,aRisk, 1,false);
							}								
						}
					}
				}
			}
		}

	}

}
