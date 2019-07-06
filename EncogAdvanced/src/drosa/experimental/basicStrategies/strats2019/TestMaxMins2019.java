package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

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
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMaxMins2019 {
	
	public static double doTest(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int lookBack,
			int maxBars,
			int tp,
			int sl,
			int debug,
			StratPerformance sp
			){
		int comm =25;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> winPipsArr = new ArrayList<Integer>();
		ArrayList<Integer> lostPipsArr = new ArrayList<Integer>();
		int yearIdx = -1;
		int lastYear = -1;
		int yearWinPips = 0;
		int yearLostPips = 0;
		boolean isDayTrade = false;
		int totalDays = 0;
		int totalDayTrades =0;
		for (int i=lookBack;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int y = cal.get(Calendar.YEAR);
			if (y<y1 || y>y2) continue;
			
			if (y!=lastYear){
				
				if (lastYear!=-1){
					double pfYear = yearWinPips*1.0/yearLostPips;
					winPipsArr.add(yearWinPips);
					lostPipsArr.add(yearLostPips);
				}
				
				yearWinPips = 0;
				yearLostPips = 0;
				yearIdx++;
				lastYear = y;
			}
			
			if (day!=lastDay){
				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-5,rangeArr.size()-1);
								
					lastHigh = high;
					lastLow = low;
				}		
				
				if (isDayTrade){
					totalDayTrades++;
				}
				
				totalDays++;
				isDayTrade = false;
				high = -1;
				low = -1;				
				lastDay = day;
			}
			
			//comisiones ajustables por horas
			comm = 25;
			if (h==0) comm = 30;
			if (h>=7) comm = 18;
			
			
			int maxMin = maxMins.get(i-1);
			int avg = data.get(i-lookBack).getOpen5();
			int diffUp = (int) (q.getOpen5()-avg);
			
			//tp = (int) (tpf*sl);
			//sl = (int) slf;
			//range = 800;//prueba a pelo..
			
			double per1 = diffUp*100.0/range;
			
			//entradas
			if (h>=h1 && h<=h2					
					){
				if (h!=0 || h==0 && min>=15){
					if (maxMin>=thr
							&& q1.getClose5()-q1.getOpen5()>=70
							){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setPositionType(PositionType.SHORT);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setSl(q.getOpen5()+sl);
						pos.setTp(q.getOpen5()-tp);
						pos.setOpenIndex(i);
						positions.add(pos);
						isDayTrade = true;
					}else if (maxMin<=-thr
							&& q1.getClose5()-q1.getOpen5()<=-70
							){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setPositionType(PositionType.LONG);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setSl(q.getOpen5()-sl);
						pos.setTp(q.getOpen5()+tp);
						pos.setOpenIndex(i);
						positions.add(pos);
						isDayTrade = true;
					}
				}
			}
			//posiciones
			int j=0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean isClose = false;
				long duration = i-p.getOpenIndex();
				int pips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					
					int floatingPips = 0;
					
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;
						
						if (q.getClose5()>=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());

						if (q.getClose5()>=p.getTp()){
							pips = q.getClose5()-p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()<=p.getSl()){
							pips = q.getClose5()-p.getEntry()-comm;
							isClose = true;
													
						}else if (duration>=maxBars
								//|| (per1<=-60.0)
								){
							pips =floatingPips;
							isClose = true;
						}else if (p.getMaxProfit()-q.getClose5()>=700
								){
							//pips =q.getClose5()-p.getEntry()-comm;
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						
						if (q.getClose5()<=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());
						
						if (q.getClose5()<=p.getTp()){
							pips = -q.getClose5()+p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()>=p.getSl()){
							pips = -q.getClose5()+p.getEntry()-comm;
							isClose = true;
						}else if (duration>=maxBars
								//|| (per1>=60.0)
								//|| (per1>=50) 
								){
							pips =floatingPips;
							isClose = true;
						}else if (-p.getMaxProfit()+q.getClose5()>=700
								){
							//pips = -q.getClose5()+p.getEntry()-comm;
							//isClose = true;
						}
					}
				}
				if (isClose){
					if (pips>=0){
						winPips += pips;
						yearWinPips += pips;
						wins++;
					}else{
						lostPips += -pips;
						yearLostPips += -pips;
						losses++;						
					}
					positions.remove(j);
				}else{
					j++;
				}
			}
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
		}

		
		int trades = wins+losses;
		double pfGlobal = winPips*1.0/lostPips;
		double avgPips = (winPips-lostPips)*0.1/trades;
		
		//agregamos ultimo año
		winPipsArr.add(yearWinPips);
		lostPipsArr.add(yearLostPips);
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;

		int totalPositives = 0;
		//evaluamos los años
		double accPf = 0;
		int cases = 0;
		for (int i=0;i<winPipsArr.size();i++){
			winPips = winPipsArr.get(i);
			lostPips = lostPipsArr.get(i);
			
			if (winPips>lostPips){
				totalPositives++;
				if (lostPips==0) accPf += 2.5;
				else accPf += winPips*1.0/lostPips;
			}else{
				accPf += winPips*1.0/lostPips;
			}
			cases++;
		}
		double dayTradePer = totalDayTrades*100.0/totalDays;
		pf = accPf/cases;
		if (debug==3){					
			if (totalPositives>=0 && pfGlobal>=1.0)
			System.out.println(
					h1 +" "+h2
					+" "+thr+" "+lookBack+" "+maxBars+" "+tp+" "+sl
					+" || "
					//+" "+trades
					//+" "+PrintUtils.Print2dec(winPer, false)
					+" "+totalPositives
					+" "+PrintUtils.Print2dec(pfGlobal, false)
					+" "+PrintUtils.Print2dec(accPf/cases, false)
					+" "+PrintUtils.Print2dec(dayTradePer, false)
					+" "+trades
					);
		}
		
		if (debug==0 
				//|| (pf>=1.35 && avg>=5.0)
				)
			System.out.println(
					h1 +" "+h2
					+" "+thr+" "+lookBack+" "+maxBars+" "+tp+" "+sl
					+" || "
					+" "+trades
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(avg, false)
					);
		
		if (debug==10
				&& pfGlobal>=1.0
				){
			if (pfGlobal>sp.getPf()
					|| totalPositives>sp.getYears()
					){
				System.out.println(
					h1 +" "+h2
					+" "+thr+" "+maxBars+" "+tp+" "+sl
					+" || "
					+" "+trades
					+" "+totalPositives
					+" "+PrintUtils.Print2dec(pfGlobal, false)
					+" "+PrintUtils.Print2dec(dayTradePer, false)
					+" ||| "
					+" "+PrintUtils.Print2dec(avgPips, false)
				);
			}
		}
		
		if (pfGlobal>sp.getPf()) sp.setPf(pfGlobal);
		if (totalPositives>=sp.getYears()) sp.setYears(totalPositives);
		
		if (totalPositives>=0) return pfGlobal;
		else return -1.0;
	}
	
	public static double doTestStrat(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			ArrayList<String> hours,
			double risk,
			boolean print
			){
		
		double initialBalance = 5000;
		double balanceNeeded = 0;
		double balance = initialBalance;
		double maxBalance = balance;
		double maxDD = 0;
		double equitity = balance;
		double maxEquitity =balance;
		double actualFloatPosition = 0;
		double maxEDD = 0;
		
		int comm =20;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> winPipsArr = new ArrayList<Integer>();
		ArrayList<Integer> lostPipsArr = new ArrayList<Integer>();
		int yearIdx = -1;
		int lastYear = -1;
		int yearWinPips = 0;
		int yearLostPips = 0;
		boolean isDayTrade = false;
		int totalDays = 0;
		int totalDayTrades =0;
		int maxNetpips = 0;
		int actualPips = 0;
		int maxPips = 0;
		int maxDiffPips = 0;
		int maxOpenOrders = 0;
		for (int i=12;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int y = cal.get(Calendar.YEAR);
			if (y<y1 || y>y2) continue;
			
			if (y!=lastYear){
				
				if (lastYear!=-1){
					double pfYear = yearWinPips*1.0/yearLostPips;
					winPipsArr.add(yearWinPips);
					lostPipsArr.add(yearLostPips);
					
					//double avgRange = MathUtils.average(rangeArr);
					//System.out.println(lastYear+" "+avgRange);
					rangeArr.clear();
				}
				
				yearWinPips = 0;
				yearLostPips = 0;
				yearIdx++;
				lastYear = y;
			}
			
			if (day!=lastDay){
				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					//range = (int) MathUtils.average(rangeArr, rangeArr.size()-5,rangeArr.size()-1);
								
					lastHigh = high;
					lastLow = low;
				}		
				
				if (isDayTrade){
					totalDayTrades++;
				}
				
				totalDays++;
				isDayTrade = false;
				high = -1;
				low = -1;				
				lastDay = day;
			}
			
			//comisiones ajustables por horas
			comm = 25;
			if (h==0) comm = 30;
			if (h>=7) comm = 18;
			
			
			int maxMin = maxMins.get(i-1);
			
			String config = hours.get(h);
			
			String[] values = config.split(" ");
			
			//System.out.println(config);
			int thr		= Integer.valueOf(values[0]);//thr
			int maxBars = Integer.valueOf(values[1]);//maxBars
			int tp 		= Integer.valueOf(values[2]);//tp
			int sl 		= Integer.valueOf(values[3]);//sl
			//entradas
			if (thr>=0				
					){
				if (h!=0 || h==0 && min>=15){
					if (maxMin>=thr
							//&& q1.getClose5()-q1.getOpen5()>=70
							){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setPositionType(PositionType.SHORT);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setSl(q.getOpen5()+sl);
						pos.setTp(q.getOpen5()-tp);
						pos.setOpenIndex(i);
						pos.setExpiredTime(i+maxBars);
						
						int miniLots = TradingUtils.calculateMiniLots(balance, sl, risk);
						if (miniLots<1) miniLots = 1;
						pos.setMicroLots(miniLots);
						
						//System.out.println("[SHORT19] "+q.toString());
						
						positions.add(pos);
						isDayTrade = true;
					}else if (maxMin<=-thr
							//&& q1.getClose5()-q1.getOpen5()<=-70
							){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setPositionType(PositionType.LONG);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setSl(q.getOpen5()-sl);
						pos.setTp(q.getOpen5()+tp);
						pos.setOpenIndex(i);
						pos.setExpiredTime(i+maxBars);
						
						int miniLots = TradingUtils.calculateMiniLots(balance, sl, risk);
						if (miniLots<1) miniLots = 1;
						pos.setMicroLots(miniLots);
						
						//System.out.println("[LONG19] "+q.toString());
						
						positions.add(pos);
						isDayTrade = true;
					}
				}
			}
			//posiciones
			int j=0;
			actualFloatPosition = 0;
			if (positions.size()>=maxOpenOrders) maxOpenOrders = positions.size();
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean isClose = false;
				long duration = i-p.getOpenIndex();
				int pips = 0;
				int floatingPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
														
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;
						
						if (q.getClose5()>=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());

						if (q.getClose5()>=p.getTp()){
							pips = p.getTp()-p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()<=p.getSl()){
							pips = p.getSl()-p.getEntry()-comm;
							isClose = true;
													
						}else if (i>=p.getExpiredTime()
								//|| (per1<=-60.0)
								){
							pips =floatingPips;
							isClose = true;
						}else if (p.getMaxProfit()-q.getClose5()>=700
								){
							//pips =q.getClose5()-p.getEntry()-comm;
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						
						if (q.getClose5()<=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());
						
						if (q.getClose5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry()-comm;
							isClose = true;
						}else if (i>=p.getExpiredTime()
								//|| (per1>=60.0)
								//|| (per1>=50) 
								){
							pips =floatingPips;
							isClose = true;
						}else if (-p.getMaxProfit()+q.getClose5()>=700
								){
							//pips = -q.getClose5()+p.getEntry()-comm;
							//isClose = true;
						}
					}
				}
				if (isClose){
					if (pips>=0){
						winPips += pips;
						yearWinPips += pips;
						wins++;
					}else{
						lostPips += -pips;
						yearLostPips += -pips;
						losses++;						
					}
					
					double profit = (0.1*pips*p.getMicroLots()*0.1);
					
					if (profit>=0){
						balance += profit;
						if (balance>=maxBalance) maxBalance = balance;
					}else{
						balance += profit;
						double dd = 100.0-balance*100.0/maxBalance;
						if (dd>=maxDD) maxDD = dd;
					}
					
					if (balance<1000){
						balanceNeeded  += 1000-balance;
						balance += balanceNeeded;
					}
					
					//System.out.println(profit+" "+balance);
					
					positions.remove(j);
				}else{
					double profit = (0.1*floatingPips*p.getMicroLots()*0.1);
					actualFloatPosition +=profit;									
					j++;
				}
				
				int nPips = winPips -lostPips;
				if (nPips>=maxPips) maxPips = nPips;
				else{
					int diffPips = maxPips -nPips;
					double per = diffPips*100.0/maxPips;
					if (diffPips>=maxDiffPips) maxDiffPips = diffPips;
				}
			}
			
			equitity = balance + actualFloatPosition;
			if (equitity>=maxEquitity) maxEquitity = equitity;
			else{
				double dd = 100.0-equitity*100.0/maxEquitity;
				if (dd>=maxEDD) maxEDD = dd;
			}
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
		}
		
		int trades = wins+losses;
		double pfGlobal = winPips*1.0/lostPips;
		double avgPips = (winPips-lostPips)*0.1/trades;
		
		//agregamos ultimo año
		winPipsArr.add(yearWinPips);
		lostPipsArr.add(yearLostPips);
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		
		int globalWinPips = winPips;
		int globalLostPips = lostPips;

		int totalPositives = 0;
		//evaluamos los años
		double accPf = 0;
		int cases = 0;
		for (int i=0;i<winPipsArr.size();i++){
			winPips = winPipsArr.get(i);
			lostPips = lostPipsArr.get(i);
			
			if (winPips>lostPips){
				totalPositives++;
				if (lostPips==0) accPf += 2.5;
				else accPf += winPips*1.0/lostPips;
			}else{
				accPf += winPips*1.0/lostPips;
			}
			cases++;
		}
		double dayTradePer = totalDayTrades*100.0/totalDays;
		pf = accPf/cases;
		
		double per=balance*100.0/initialBalance -100.0;
		if (print)
		System.out.println(
			y1+" "+y2+" "+PrintUtils.Print2dec(risk, false)
			+" || "+header
			+" || "
			+" "+trades
			+" "+totalPositives
			+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
			+" "+PrintUtils.Print2dec(pfGlobal, false)
			+" "+globalWinPips+" "+globalLostPips
			+" "+PrintUtils.Print2dec(dayTradePer, false)
			+" ||| "
			+" "+PrintUtils.Print2dec(avgPips, false)
			+" || "
			+" "+PrintUtils.Print2dec(initialBalance+balanceNeeded, false)
			+" "+PrintUtils.Print2dec2(balance,true)
			//+" "+PrintUtils.Print2dec2(maxBalance, true)
			+" "+PrintUtils.Print2dec2(maxEquitity, true)
			+" "+PrintUtils.Print2dec(maxEDD, false)
			+" || "+PrintUtils.Print2dec(per/maxEDD, false)
			+" || "+maxOpenOrders
			+" || "+PrintUtils.Print2dec2(maxEquitity, true)
			+" "+PrintUtils.Print2dec(maxEDD, false)
		);

		
		return per/maxEDD;
	}
	
	public static void doTestPeaks(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			int maxBars,
			boolean isReverse
			){
		
		int comm = 20;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> winPipsArr = new ArrayList<Integer>();
		ArrayList<Integer> lostPipsArr = new ArrayList<Integer>();
		int yearIdx = -1;
		int lastYear = -1;
		int yearWinPips = 0;
		int yearLostPips = 0;
		boolean isDayTrade = false;
		int totalDays = 0;
		int totalDayTrades =0;
		int maxNetpips = 0;
		int actualPips = 0;
		int maxPips = 0;
		int maxDiffPips = 0;
		int maxOpenOrders = 0;
		int maxWinPips = 0;
		int maxLostPips = 0;
		
		ArrayList<Integer> dayRes = new ArrayList<Integer>();
		ArrayList<Double> dds = new ArrayList<Double>();
		ArrayList<Integer> ddPips = new ArrayList<Integer>();
		ArrayList<Integer> ddWinPips = new ArrayList<Integer>();
		ArrayList<Integer> ddLostPips = new ArrayList<Integer>();
		ArrayList<Double> ddPfs = new ArrayList<Double>();
		double dayDD = 0.0;
		int dayDDPip = 0;
		for (int i=12;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int y = cal.get(Calendar.YEAR);
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					
					int diffW = maxWinPips-winPips;
					int diffL = maxLostPips-lostPips;
					int diffP = maxWinPips-(winPips-lostPips);
					ddPips.add(diffP);
					ddWinPips.add(winPips);
					ddLostPips.add(lostPips);
				}
				
				lastDay = day;
			}
			
			
			int maxMin = maxMins.get(i-1);
			//entradas
			if (thr>=0 && h>=h1 && h<=h2			
					){
				if (h!=0 || h==0 && min>=15){
					if ( maxMin>=thr							
							//&& q1.getClose5()-q1.getOpen5()>=70
							){
						
						if (isReverse){
							PositionShort pos = new PositionShort();
							pos.setEntry(q.getOpen5());
							pos.setPositionType(PositionType.SHORT);
							pos.setPositionStatus(PositionStatus.OPEN);
							pos.setSl(q.getOpen5()+sl);
							pos.setTp(q.getOpen5()-tp);
							pos.setOpenIndex(i);
							pos.setExpiredTime(i+maxBars);
							
							//int miniLots = TradingUtils.calculateMiniLots(balance, sl, risk);
							//if (miniLots<1) miniLots = 1;
							//pos.setMicroLots(miniLots);
							
							//System.out.println("[SHORT19] "+q.toString());
							
							positions.add(pos);
						}else{
							PositionShort pos = new PositionShort();
							pos.setEntry(q.getOpen5());
							pos.setPositionType(PositionType.LONG);
							pos.setPositionStatus(PositionStatus.OPEN);
							pos.setSl(q.getOpen5()-sl);
							pos.setTp(q.getOpen5()+tp);
							pos.setOpenIndex(i);
							pos.setExpiredTime(i+maxBars);
							
							//int miniLots = TradingUtils.calculateMiniLots(balance, sl, risk);
							//if (miniLots<1) miniLots = 1;
							//pos.setMicroLots(miniLots);
							
							//System.out.println("[LONG19] "+q.toString());
							
							positions.add(pos);
						}
						isDayTrade = true;
					}else if (maxMin<=-thr
							//&& q1.getClose5()-q1.getOpen5()<=-70
							){
						if (isReverse){
							PositionShort pos = new PositionShort();
							pos.setEntry(q.getOpen5());
							pos.setPositionType(PositionType.LONG);
							pos.setPositionStatus(PositionStatus.OPEN);
							pos.setSl(q.getOpen5()-sl);
							pos.setTp(q.getOpen5()+tp);
							pos.setOpenIndex(i);
							pos.setExpiredTime(i+maxBars);
							
							//int miniLots = TradingUtils.calculateMiniLots(balance, sl, risk);
							//if (miniLots<1) miniLots = 1;
							//pos.setMicroLots(miniLots);
							
							//System.out.println("[LONG19] "+q.toString());
							
							positions.add(pos);
							isDayTrade = true;
						}else{
							PositionShort pos = new PositionShort();
							pos.setEntry(q.getOpen5());
							pos.setPositionType(PositionType.SHORT);
							pos.setPositionStatus(PositionStatus.OPEN);
							pos.setSl(q.getOpen5()+sl);
							pos.setTp(q.getOpen5()-tp);
							pos.setOpenIndex(i);
							pos.setExpiredTime(i+maxBars);
							
							//int miniLots = TradingUtils.calculateMiniLots(balance, sl, risk);
							//if (miniLots<1) miniLots = 1;
							//pos.setMicroLots(miniLots);
							
							//System.out.println("[SHORT19] "+q.toString());
							
							positions.add(pos);
						}
					}
				}
			}
			//posiciones
			int j=0;
			if (positions.size()>=maxOpenOrders) maxOpenOrders = positions.size();
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean isClose = false;
				long duration = i-p.getOpenIndex();
				int pips = 0;
				int floatingPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
														
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;
						
						if (q.getClose5()>=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());

						if (q.getClose5()>=p.getTp()){
							pips = p.getTp()-p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()<=p.getSl()){
							pips = p.getSl()-p.getEntry()-comm;
							isClose = true;
													
						}else if (i>=p.getExpiredTime()
								//|| (per1<=-60.0)
								){
							pips =floatingPips;
							isClose = true;
						}else if (q.getClose5()-p.getEntry()>=200
								){
							int toTrail = (int) ((q.getClose5()-p.getEntry())*0.10);
							int newPos = p.getEntry()+toTrail;
							if (newPos>p.getSl())
								p.setSl(newPos);
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						
						if (q.getClose5()<=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());
						
						if (q.getClose5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry()-comm;
							isClose = true;
						}else if (i>=p.getExpiredTime()
								//|| (per1>=60.0)
								//|| (per1>=50) 
								){
							pips =floatingPips;
							isClose = true;
						}else if (-q.getClose5()+p.getEntry()>=200
								){
							int toTrail = (int) ((-q.getClose5()+p.getEntry())*0.10);
							int newPos = p.getEntry()-toTrail;
							if (newPos<p.getSl())
								p.setSl(newPos);
							//pips = -q.getClose5()+p.getEntry()-comm;
							//isClose = true;
						}
					}
				}
				if (isClose){
					if (pips>=0){
						winPips += pips;
						yearWinPips += pips;
						wins++;
					}else{
						lostPips += -pips;
						yearLostPips += -pips;
						losses++;						
					}
					
					double profit = (0.1*pips*p.getMicroLots()*0.1);
					
					positions.remove(j);
				}else{
					double profit = (0.1*floatingPips*p.getMicroLots()*0.1);									
					j++;
				}
				
				int nPips = winPips -lostPips;
				if (nPips>=maxPips) maxPips = nPips;
				else{
					int diffPips = maxPips -nPips;
					double per = diffPips*100.0/maxPips;
					if (diffPips>=maxDiffPips) maxDiffPips = diffPips;
				}
			}//while
			
			if (winPips-lostPips>=maxPips){
				maxPips = winPips-lostPips;
				maxWinPips = winPips;
				maxLostPips = lostPips;
			}
		}//data
		
		for (int af=0;af<=45000;af+=1000){
			int count = 0;
			double acc = 0;
			double accPf = 0;
			int accPfw = 0;
			int accPfl = 0;
			for (int i=0;i<ddPips.size();i++){
				int ddi = ddPips.get(i);			
				int wp = ddWinPips.get(i);
				int lp = ddLostPips.get(i);
				if (ddi>=af){
					//System.out.println(PrintUtils.Print2dec(acc*1.0/count, false));
					int j = i+30;
					if (j<=ddPips.size()-1){
						//System.out.println(PrintUtils.Print2dec(dds.get(j)-ddi, false));
						count++;
						acc+=ddPips.get(j)-ddi;
						accPf+=(ddWinPips.get(j)-wp)-(ddLostPips.get(j)-lp);
						accPfw+=(ddWinPips.get(j)-wp);
						accPfl+=(ddLostPips.get(j)-lp);
					}
				}
			}
								
			/*System.out.println(PrintUtils.Print2dec(af, false)
					+";"+count
					//+";"+PrintUtils.Print2dec(acc/count, false)
					+";"+PrintUtils.Print2dec(accPf/count, false)
					+";"+PrintUtils.Print2dec(accPfw*1.0/accPfl, false)
					+";"+PrintUtils.Print2dec(accPfw, false)
					+";"+PrintUtils.Print2dec(accPfl, false)
					);*/
		}
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		System.out.println(
				h1+" "+h2
				+" "+thr+" "+tp+" "+sl
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+winPips+" "+lostPips
				);
	}

	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.02.27.csv";
		
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
			
			//0   6 198 200 800 || 285 10 2.09 8.50
			//1   54 222 700 2800 || 241 10 2.14 6.77
			//2   54 168 400 1600 || 453 10 1.41 12.69
			//3   54 138 1000 2000 || 699 9 1.45 18.95
			//4  700  84 150  600 || 130  9 1.52 3.65
			//5  114 180 700 2800  || 259 9 1.55 7.54	
			//6  6 126 108 200 800 ||  176 10 1.75 5.31
			//7  12 30 300 900 || 455 9 1.48 13.42
			//8  475 12 200 200   || 280 14 1.40 5.69
			//9  204 36 500  1500 || 602  9 1.45 17.19
			//10 2900 360 500 1000 || 195 9 1.47 5.27
			//11 875 336 600 1800 || 340 10 1.45 8.73
			//12 3700 336 200 600 || 145 10 1.78 3.50
			//13 3000 336 300 600  || 129 9 1.71 3.58
			//14 1980 36 100 500 || 152 10 1.91 4.42
			//18 400 396 200 800 || 540 9 1.40 13.73
			//19 1260 144 600 1800 || 197 9 1.43 5.04 
			//20 1400 216 200 800 || 136 8 1.45 3.77
			//23  36 216 300 1500 || 263  9 1.79 7.92
			
			ArrayList<String> hours = new ArrayList<String>();
			for (int j=0;j<=23;j++){
				String str = "-1 -1 -1 -1";
				hours.add(str);
			}
			
			
			for (int h1=10;h1<=10;h1++){
				int h2 = h1+6;
				for (int thr=1000;thr<=1000;thr+=100){
					for (int tp=4000;tp<=4000;tp+=100){
						for (int y1=2004;y1<=2019;y1++){
							int y2 = y1+0;
							TestMaxMins2019.doTestPeaks("",data,maxMins,y1,y2,h1,h2,thr,tp,800,100000,false);
						}						
					}
				}
			}
			
			//optimizado 2009-2018
			/*hours.set(0,"25 36 200 400");hours.set(1,"54 222 700 2800");hours.set(2,"54 168 400 1600");
			hours.set(3,"54 138 1000 2000");hours.set(4,"700 84 150 600");hours.set(5,"114 180 700 2800");
			hours.set(6,"6 126 108 200 800");hours.set(7,"12 30 300 900");hours.set(8,"400 12 200 600");
			hours.set(9,"204 36 500 1500");hours.set(10,"2900 360 500 1000");hours.set(11,"875 336 600 1800");
			hours.set(12,"3700 336 200 600");hours.set(13,"3000 336 300 600");hours.set(14,"1980 36 100 500");
			hours.set(18,"400 396 200 800");hours.set(19,"1260 144 600 1800");hours.set(20,"1400 216 200 800");
			hours.set(23,"6 210 300 1200");*/
			//
			
			//0   25 264 400 2000 || 303 14 2.36 6.25 //50 60 200 400 ||  221 15 2.06 4.46			
			//1   50 180 1000 3000 || 416 14 1.79 7.97			
			//2   250 132 100 500 || 298 13 1.33 5.35			
			//3   225 144 1000 3000 || 366 13 1.28 6.64			
			//4  700  84 150  600 || 130  9 1.52 3.65			
			//5  525 60 600 3000  || 132 13 1.74 2.23				
			//6  300 72 200 1000 ||  159 14 3.42 3.02
			//7  200 36 500 500 || 201 14 1.60 3.97			
			//8  325 84 300 300  || 346 13 1.32 6.66			
			//9  350 288 1000 5000 || 718 12 1.34 12.76
			
			//23  25 240 500 2500 || 504 14 1.62 9.92
			
			/*hours.set(0,"188 215 350 1000");			
			hours.set(1,"145 110 600 900");
			hours.set(2,"500 140 200 400");
			hours.set(3,"1100 20 200 400");
			hours.set(4,"450 90 110 750");
			hours.set(5,"495 110 100 500");
			hours.set(6,"200 73 200 500");
			hours.set(7,"450 8 100 200");
			hours.set(8,"550 12 100 400");
			hours.set(9,"500 20 200 600");
			hours.set(23,"400 182 110 600");
			
			for (double risk = 0.1;risk<=3.0;risk+=0.1){
				for (int y1=2004;y1<=2004;y1++){
					int y2 = y1+15;
					TestMaxMins2019.doTestStrat("",data,maxMins,y1,y2,hours,risk,true);
				}
			}*/
			
			/*for (int y1=2009;y1<=2017;y1+=1){
				//training set
				int bestThr = -1;
				int bestMaxBars = 288;
				int bestTp = 800;
				int bestSl = 800;
				double bestFactor = -9999;
				for (int thr=12;thr<=200;thr+=12){
					for (int maxBars=9999;maxBars<=9999;maxBars+=36){
						for (int tp=100;tp<=500;tp+=100){
							for (int sl=1*tp;sl<=5*tp;sl+=1*tp){
								String str = thr+" "+maxBars+" "+tp+" "+sl;
								hours.set(0, str);
								//hours.set(1, str);
								//hours.set(2, str);
								
								String header =thr+" ";
								for (double risk = 3.0;risk<=3.0;risk+=0.1){
									for (int yt1=y1;yt1<=y1;yt1++){
										int yt2 = y1+1;
										double factor =TestMaxMins2019.doTestStrat(header,data,maxMins,yt1,yt2,hours,risk,false);
										if (factor>=bestFactor){
											bestFactor = factor;
											bestThr = thr;
											bestMaxBars = maxBars;
											bestTp = tp;
											bestSl = sl;
										}
									}
								}
							}
						}
					}
				}
				//test set
				String str =bestThr+" "+bestMaxBars+" "+bestTp+" "+bestSl;
				hours.set(0, str);
				//hours.set(1, str);
				//hours.set(2, str);
				TestMaxMins2019.doTestStrat("[TEST] "+str,data,maxMins,y1+2,y1+2,hours,1.0,true);
			}*/
			
			
			
			/*for (int h1=4;h1<=23;h1++){
				int h2 = h1+0;

				double bestPf = 0;
				StratPerformance sp = new StratPerformance();
				//int bestYears
				for (int thr=0;thr<=800;thr+=12){
					for (int lookBack = 12; lookBack<=12;lookBack++){
						for (int maxBars =12; maxBars<=288;maxBars+=12){
							for (int tp =100;tp<=800;tp+=100){
								for (int sl =1*tp;sl<=4*tp;sl+=1*tp){
									int totalPositives = 0;
									double accPf = 0;
									for (int y1=2009;y1<=2009;y1++){
										int y2 = y1+9;
										TestMaxMins2019.doTest(data,maxMins,y1,y2,h1,h2,thr,lookBack,maxBars,tp,sl,10,sp);
									}
								}
							}
						}
					}
				}
			}*/
		}
	}

}
