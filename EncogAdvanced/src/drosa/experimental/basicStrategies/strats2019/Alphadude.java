package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
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

public class Alphadude {
	
	public static void doTest(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int n,
			double ftp,
			double fsl,
			ArrayList<Integer> strat,//
			ArrayList<Integer> dayPipsArr,
			boolean isMomentum,
			double aRisk,
			int debug){
		
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
		int mode = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
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
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();
		/*for (int i=0;i<=n-1;i++){
			openArr.add(data.get(i).getOpen5());
		}*/
		
		
		int dayTrade = 0;
		int totalDaysTrade = 0;
		mode = 0;
		int modeIdx = 0;
		int dayPips = 0;
		int lastPips = 0;
		
		for (int i=100;i<data.size()-2;i++){
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
			
			comm = 20;
			
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
				lastHigh = high;
				lastLow = low;
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				dayPips = 0;
				totalDays++;
			}
			
						
			openArr.add(q.getOpen5());
					
			ishOk = strat.get(h) >0;
			
			//int diffUp = q.getOpen5()-data.get(i-6).getOpen5();			
			if (ishOk && high>=0){
				int maxMin = maxMins.get(i-1);
				//TradingUtils.getMaxMinShort(data, qm, calqm, i-n, i-1);
				
				//int diffUp = qm.getHigh5()-q1.getHigh5();
				//int diffDown = q1.getLow5()-qm.getLow5();
				
				//int diffUp = qm.getHigh5()-q.getOpen5();
				//int diffDown = q.getOpen5()-qm.getLow5();
				
				//comprobamos que hay un número de pips posibles a hacer
				int per = strat.get(h);
				int targetPips = 0;
				int actualRange = high-low;
				targetPips = (int) (actualRange*(per*0.01));
				int targetPrice = 0;
				mode = 0;
				//System.out.println(targetPips);
				//if (diffUp>=per*range){
					if (maxMin>=n
							//&& q1.getClose5()>=q.getOpen5()
							//&& q1.getHigh5()-q.getOpen5()<=50
							){
						targetPrice = q1.getHigh5()-targetPips;
						//if (q.getOpen5()-targetPrice>=100){
							mode = 1;
						//}
					}else if (maxMin<=-n
							//&& q1.getClose5()<=q.getOpen5()
							
							){
							//&& -q1.getLow5()+q.getOpen5()<=50

						targetPrice = q1.getLow5()+targetPips;
						//if (targetPrice-q.getOpen5()>=100){
							mode = -1;
						//}
					}
				//}
				int tpPips = (int) (ftp*range);
				int slPips = (int) (fsl*range);
				if (tpPips<=40) tpPips = 40;
				
				if (mode==1 
						){
				//if (spread<=-minPips){
				
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);					
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);					
					p.setPositionType(PositionType.SHORT);
					//p.setTp(targetPrice);
					p.setTp(p.getEntry()-tpPips);
					p.setSl(p.getEntry()+slPips);					
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(2000*0.1);
					int microLots = (int) (riskPip/0.10);
					p.setMicroLots(microLots);		
					p.setExtraParam(strat.get(h));
					dayTrade = 1;
					positions.add(p);
				}else if (mode==-1
						){
				//}else if(spread>=minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);					
					p.setPositionType(PositionType.LONG);
					//p.setTp(targetPrice);
					p.setTp(p.getEntry()+tpPips);
					p.setSl(p.getEntry()-slPips);					
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(2000*0.1);
					int microLots = (int) (riskPip/0.10);
					p.setMicroLots(microLots);		
					p.setExtraParam(strat.get(h));
					dayTrade = 1;
					positions.add(p);
				}
			}//H
			
			//actualRangePosition
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();	
			int diffH = high-q.getClose5();
			int diffL = q.getClose5()-low;
			int actualRange = high - low;
			double perH = diffH*100.0/actualRange;
			double perL = diffL*100.0/actualRange;
			
			int j = 0;
			boolean closeAll = false;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				int percent = p.getExtraParam();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					//spread = smaValue - q.getClose5();					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  q.getClose5()-p.getEntry();
						//time exits
						if (h==40
								){
							pips =  q.getClose5()-p.getEntry();
							isClose = true;
						}else if (q.getHigh5()>=p.getTp()){
							pips =  p.getTp()-p.getEntry();
							isClose = true;
						}else if (q.getLow5()<=p.getSl()){
							pips =  p.getSl()-p.getEntry();
							isClose = true;
						}else if (h==23 && min==55){
							pips =  q.getClose5()-p.getEntry();
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-q.getClose5();
						//time exits
						if (h==40
								){
							pips =  p.getEntry()-q.getClose5();
							isClose = true;
						}else if (q.getLow5()<=p.getTp()){
							pips =  p.getEntry()-p.getTp();
							isClose = true;
						}else if (q.getHigh5()>=p.getSl()){
							pips =  p.getEntry()-p.getSl();
							isClose = true;
						}else if (h==23 && min==55){
							pips =  p.getEntry()-q.getClose5();
							//isClose = true;
						}
					}
					
					if (isClose){
						
						pips-=comm;
						
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
							
							//actualizamos balance
							//double win$$ = p.getPip$$()*pips*0.1;
							//balance += win$$;
							//equitity += win$$;
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
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
		Iterator it = yWinPips.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer,Integer> pair = (Map.Entry)it.next();
	        int year = pair.getKey();
	        int wPips = pair.getValue();
	        int lPips = 0;
	        if (yLostPips.containsKey(year))
	        	lPips = yLostPips.get(year);
	        int netPips = wPips-lPips;
	        if (netPips>=0) posYears++;
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		double winPer = wins*100.0/trades;
		
		if (debug==2
				|| (avg>=0.0 && posYears>=10 && maxDD<70.0 && trades>=100 && perDays>=0.0)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "
				+" "+posYears
				+" "+trades	
				+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(pf, false)
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
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.03.08.csv";
		
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
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
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);				
			dataI 		= new ArrayList<QuoteShort>();			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			Calendar cal = Calendar.getInstance();
			System.out.println("path: "+path+" "+data.size());
			double aMaxFactorGlobal = -9999;
			
			ArrayList<Integer> strat = new ArrayList<Integer>();
			for (int j=0;j<=23;j++) strat.add(-1);
			
			for (int h1=10;h1<=10;h1++){
				int h2 = h1+0;
				for (int n=0;n<=8000;n+=50){
					for (double ftp=0.20;ftp<=0.20;ftp+=0.1){
						for (double fsl=1*ftp;fsl<=1*ftp;fsl+=0.1){
							for (int per=10;per<=10;per+=10){
								for (int j=0;j<=23;j++) strat.set(j,-1);
								for (int j=h1;j<=h2;j++){
									strat.set(j,per);
								}
								ArrayList<Integer> dayPips1 = new ArrayList<Integer>();
								for (double aRisk = 0.50;aRisk<=0.50;aRisk+=0.10){
									String str = h1+" "+h2+" "+n+" "+per
											+" "+PrintUtils.Print2dec(ftp, false)
											+" "+PrintUtils.Print2dec(fsl, false)
											;
									for (int y1=2004;y1<=2004;y1++){
										int y2 = y1+15;
										Alphadude.doTest(str, data,maxMins, y1, y2, 0, 11,n,ftp,fsl,strat,dayPips1,false,aRisk, 2);
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
