package drosa.experimental.fibs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.GlobalStats;
import drosa.experimental.PositionShort;
import drosa.experimental.SuperStrategy;
import drosa.experimental.TIMEFRAME;
import drosa.experimental.EAS.TestEAs;
import drosa.experimental.edge.Edge;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestAllFibs {
	
	public static void testBars$$(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Integer> nBars,
			int begin,int end,String hours,
			int bar,int nATR,double tpFactor,double slFactor,
			int minBarRange,int maxAllowed,int maxDOCrosses,int minPips,
			double balance,double riskPerTrade,
			double comm){
		
		//globalStats.reset();
		double actualBalance = balance;
		double maxBalance = balance;
		double maxDD = 0;
		double extraNeeded   = 0;
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		
		int totalPips = 0;
		int maxWins = 0;
		int maxLosses = 0;
		int actualWins = 0;
		int actualLosses = 0;
		int avgWins=0;
		int avgLosses=0;
		int totalAvgWins=0;
		int totalAvgLosses=0;
		int maxOpens = 0;
		int wins = 0;
		int losses = 0;
		int totalDays = 0;
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100.0;
		int DO = 0;
		int DOcrosses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		PositionShort pos = null;
		QuoteShort higherLower = new QuoteShort();
		higherLower.setHigh5(-1);
		higherLower.setLow5(-1);
		int opens=0;
		QuoteShort.getCalendar(cal, data.get(begin));
		//globalStats.addBalance(cal,actualBalance,extraNeeded);
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//para al calculo de rangos
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				DO = q.getOpen5();
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
				DOcrosses=0;
			}
			
			if (q.getLow5()<=DO && DO<=q.getHigh5())
				DOcrosses++;
			//int atrPips = (int) (atr*percent*0.01);
			//int highEntry = DO + atrPips*10;
			//int lowEntry  = DO - atrPips*10;
			double slTpFactor = slFactor/tpFactor;
			int tpPips = (int) (atr*tpFactor);
			int slPips = (int) (atr*slFactor);
			if (tpPips<=5){
				tpPips = 5;
				slPips = (int) (tpPips*slTpFactor);
			}
			//System.out.println(atr+" "+atrPips+" "+DO+" "+highEntry+" "+lowEntry);
			int maxMin = maxMins.get(i);
			int entry = -1;
			int stopLoss = -1;
			int takeProfit = -1;
			int longDiff = q.getClose5()-q.getLow5();
			int shortDiff = q.getHigh5()-q.getClose5();
			int diffOC 		= (q.getClose5()-q.getOpen5());	
			int barRange = q.getHigh5()-q.getLow5();
			PositionType posType = PositionType.NONE;
			int allowed = allowedHours.get(h);
			int nBarh	= nBars.get(h);	
			long microLots = 0;
			if (maxMin>=nBarh 
					&& barRange>=minBarRange
					&& q.getHigh5()>=DO
					//&& DOcrosses<=maxDOCrosses
					//&& shortDiff>=minPips
					//&& diffOC<=0//bueno <=
					&& allowed==1){
				entry      = q1.getOpen5();
				stopLoss   = entry+slPips*10;
				takeProfit = entry-tpPips*10;
				posType = PositionType.SHORT;
				//margen requerido
				double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,riskPerTrade,maxAllowed,slPips);
				if (actualBalance<minBalance){
					extraNeeded += minBalance-actualBalance;
					actualBalance = minBalance;
				}
				microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,maxAllowed,slPips);
			}
			
			if (maxMin<=-nBarh
					&& barRange>=minBarRange
					&& q.getLow5()<=DO
					//&& DOcrosses<=maxDOCrosses
					//&& longDiff>=minPips
					//&& diffOC>=0
					&& allowed==1){
				entry      = q1.getOpen5();
				stopLoss   = entry-slPips*10;
				takeProfit = entry+tpPips*10;
				posType = PositionType.LONG;
				//margen requerido
				double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,riskPerTrade,maxAllowed,slPips);
				if (actualBalance<minBalance){
					extraNeeded += minBalance-actualBalance;
					actualBalance = minBalance;
				}
				microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,maxAllowed,slPips);
			}
			
			opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
			if (entry!=-1 && opens<maxAllowed			
					){
				pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(stopLoss);
				pos.setTp(takeProfit);		
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenDiff(i);
				pos.setMicroLots(microLots);
				positions.add(pos);
				//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
			}
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int earnedPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (q1.getHigh5()>=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getSl()-p.getEntry();
						}else if (q1.getLow5()<=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getEntry()-p.getTp();
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (q1.getLow5()<=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getEntry()-p.getSl();
						}else if (q1.getHigh5()>=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getTp()-p.getEntry();
						}
					}	
					
					if (closed){		
						earnedPips -= comm*10;
						if (earnedPips*0.1>=0.0) win=1;
						else win = -1;
						double amount = earnedPips*0.1*p.getMicroLots()*0.1;
						actualBalance+= amount;
						if (actualBalance>maxBalance) maxBalance = actualBalance;
						else{
							double actualDD = 100.0-(actualBalance*100.0/maxBalance);
							if (actualDD>maxDD) maxDD = actualDD;
						}
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						/*System.out.println(DateUtils.datePrint(cal)+" "+p.toString()
								+" || "+PrintUtils.Print2(earnedPips)+" "+PrintUtils.Print2(amount)
										+" "+PrintUtils.Print2(actualBalance));*/
						totalPips += earnedPips;
						if (win==1){
							wins++;
							if (actualLosses>maxLosses) maxLosses = actualLosses;							
							avgLosses+=actualLosses;
							if (actualLosses>0){
								avgLosses+=actualLosses;
								totalAvgLosses++;
							}
							actualLosses = 0;
							actualWins++;
						}
						if (win==-1){
							losses++;
							if (actualWins>maxWins) maxWins = actualWins;
							if (actualWins>0){
								avgWins+=actualWins;
								totalAvgWins++;
							}
							actualWins = 0;
							actualLosses++;
						}
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;
					}
				}
				opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (opens>maxOpens) maxOpens = opens;
			}//for positions	
			
			PositionShort.getHigherLowerPos(positions,higherLower);
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}//for data
		
		int totals = wins+losses;
		double perWin = wins*100.0/totals;
		double perLoss = 100.0-perWin;
		double pf = (perWin*tpFactor*1.0)/(perLoss*slFactor);
		//double exp = (perWin*tpFactor*1.0-perLoss*slFactor)/100.0;
		double avgPips = totalPips*0.1/totals;
		QuoteShort.getCalendar(cal, data.get(begin));
		System.out.println(
				DateUtils.datePrint(cal)
				+" "+hours
				+" "+bar
				+" "+PrintUtils.Print2dec(tpFactor,false,2)
				+" "+PrintUtils.Print2dec(slFactor,false,2)
				+" "+maxAllowed
				+" "+PrintUtils.Print2dec(riskPerTrade,false,2)
				+" "+PrintUtils.Print2dec2(comm, true)
				+" || "
				+" "+totals
				+" "+PrintUtils.Print2(perWin)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2(avgPips)
				+" "+PrintUtils.Print2dec2(actualBalance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec2(balance+extraNeeded, true)
				+" "+PrintUtils.Print2dec2(maxDD, true)
				);	
		
	}
	
	public static void testBars(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int begin,int end,String hours,
			int bar,int nATR,double tpFactor,double slFactor,
			int maxAllowed,int maxDOCrosses,int minPips,
			double comm,boolean minutesDebug){
		
		ArrayList<Integer> minutesWin = new ArrayList<Integer>();
		ArrayList<Integer> totalsMinutes = new ArrayList<Integer>();
		for (int i=0;i<=12*23+12;i++){
			minutesWin.add(0);
			totalsMinutes.add(0);
		}
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		
		int totalPips = 0;
		int maxWins = 0;
		int maxLosses = 0;
		int actualWins = 0;
		int actualLosses = 0;
		int avgWins=0;
		int avgLosses=0;
		int totalAvgWins=0;
		int totalAvgLosses=0;
		int maxOpens = 0;
		int wins = 0;
		int losses = 0;
		int totalDays = 0;
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100.0;
		int DO = 0;
		int DOcrosses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		PositionShort pos = null;
		QuoteShort higherLower = new QuoteShort();
		higherLower.setHigh5(-1);
		higherLower.setLow5(-1);
		int opens=0;
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//para al calculo de rangos
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				DO = q.getOpen5();
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
				DOcrosses=0;
			}
			
			if (q.getLow5()<=DO && DO<=q.getHigh5())
				DOcrosses++;
			//int atrPips = (int) (atr*percent*0.01);
			//int highEntry = DO + atrPips*10;
			//int lowEntry  = DO - atrPips*10;
			double slTpFactor = slFactor/tpFactor;
			int tpPips = (int) (atr*tpFactor);
			int slPips = (int) (atr*slFactor);
			if (tpPips<=5){
				tpPips = 5;
				slPips = (int) (tpPips*slTpFactor);
			}
			//System.out.println(atr+" "+atrPips+" "+DO+" "+highEntry+" "+lowEntry);
			int maxMin = maxMins.get(i);
			int entry = -1;
			int stopLoss = -1;
			int takeProfit = -1;
			int shortDiff = q1.getOpen5()-higherLower.getHigh5();
			int longDiff = higherLower.getLow5()-q1.getOpen5();
			/*if (opens>0)
				System.out.println(shortDiff+" "+longDiff
						+" "+PrintUtils.Print2(higherLower.getHigh5())
						+" "+PrintUtils.Print2(higherLower.getLow5())
				);*/
			
			PositionType posType = PositionType.NONE;
			int allowed = allowedHours.get(h);
			if (maxMin>bar 
					&& q.getHigh5()>=DO
					//&& DOcrosses<=maxDOCrosses
					//&& (shortDiff>=minPips*10 || higherLower.getHigh5()==-1)
					&& allowed==1){
				
				entry      = q1.getOpen5();
				stopLoss   = entry+slPips*10;
				takeProfit = entry-tpPips*10;
				posType = PositionType.SHORT;
			}
			
			if (maxMin<-bar
					&& q.getLow5()<=DO
					//&& DOcrosses<=maxDOCrosses
					//&& (longDiff>=minPips*10 || higherLower.getLow5()==-1)
					&& allowed==1){
				//System.out.println(maxMin+" "+q.toString()+"|| "+q1.toString());
				entry      = q1.getOpen5();
				stopLoss   = entry-slPips*10;
				takeProfit = entry+tpPips*10;
				posType = PositionType.LONG;
			}
			
			opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
			if (entry!=-1 && opens<maxAllowed			
					){
				pos = new PositionShort();
				pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
				pos.setEntry(entry);
				pos.setSl(stopLoss);
				pos.setTp(takeProfit);		
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenDiff(i);
				positions.add(pos);
				//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
			}
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int earnedPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (q1.getHigh5()>=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getSl()-p.getEntry();
						}else if (q1.getLow5()<=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getEntry()-p.getTp();
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (q1.getLow5()<=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getEntry()-p.getSl();
						}else if (q1.getHigh5()>=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getTp()-p.getEntry();
						}
					}	
					
					if (closed){	
						int hopen = p.getOpenCal().get(Calendar.HOUR_OF_DAY);
						int mopen = p.getOpenCal().get(Calendar.MINUTE);
						int tradePosition = hopen*12+(int)mopen/5;
						//System.out.println("position: "+tradePosition);
						int totalWin = minutesWin.get(tradePosition);
						int total    = totalsMinutes.get(tradePosition);
						totalsMinutes.set(tradePosition, total+1);
						if (win==1)
							minutesWin.set(tradePosition, totalWin+1);
						
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						//System.out.println(DateUtils.datePrint(cal)+" "+p.toString()+" "+(wins+losses+1));
						totalPips += earnedPips;
						if (win==1){
							wins++;
							if (actualLosses>maxLosses) maxLosses = actualLosses;							
							avgLosses+=actualLosses;
							if (actualLosses>0){
								avgLosses+=actualLosses;
								totalAvgLosses++;
							}
							actualLosses = 0;
							actualWins++;
						}
						if (win==-1){
							losses++;
							if (actualWins>maxWins) maxWins = actualWins;
							if (actualWins>0){
								avgWins+=actualWins;
								totalAvgWins++;
							}
							actualWins = 0;
							actualLosses++;
						}
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;
					}
				}
				opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (opens>maxOpens) maxOpens = opens;
			}//for positions	
			
			PositionShort.getHigherLowerPos(positions,higherLower);
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}//for data
		
		int totals = wins+losses;
		double perWin = wins*100.0/totals;
		double perLoss = 100.0-perWin;
		double pf = (perWin*tpFactor*1.0)/(perLoss*slFactor);
		//double exp = (perWin*tpFactor*1.0-perLoss*slFactor)/100.0;
		double avgPips = totalPips*0.1/totals;
		double edge = Edge.calculateEdge(tpFactor, slFactor, perWin);
		double kelly = Edge.calculateKelly(tpFactor,slFactor,perWin);
		System.out.println(hours
				+" "+bar
				+" "+PrintUtils.Print2dec(tpFactor,false,2)
				+" "+PrintUtils.Print2dec(slFactor,false,2)
				+" "+maxAllowed
				+" "+maxDOCrosses
				+" || "
				+" "+totals
				+" "+PrintUtils.Print2(perWin)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2(avgPips)
				+" "+PrintUtils.Print2(edge)
				+" "+PrintUtils.Print2(kelly)
				);	
		if (minutesDebug){
			int maxMinute = 23*12+12;
			for (int i=0;i<=maxMinute;i++){
				int mins = minutesWin.get(i);
				int total = totalsMinutes.get(i);
				double perMWin = mins*100.0/total;
				int h =  i/12;
				int m = i%12;
				System.out.println(h+":"+(m*5)+" "+total+" "+PrintUtils.Print2(perMWin));
			}
		}
		
	}
	
	public static void testFib3(ArrayList<QuoteShort> data,int begin,int end,String hours,
			double percent,int nATR,int tp,int sl){
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		
		int wins = 0;
		int losses = 0;
		int totalDays = 0;
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100.0;
		int DO = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		PositionShort pos = null;
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//para al calculo de rangos
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				DO = q.getOpen5();
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
			}
			
			int atrPips = (int) (atr*percent*0.01);
			int highEntry = DO + atrPips*10;
			int lowEntry  = DO - atrPips*10;
			//System.out.println(atr+" "+atrPips+" "+DO+" "+highEntry+" "+lowEntry);
			int entry = -1;
			int stopLoss = -1;
			int takeProfit = -1;
			PositionType posType = PositionType.NONE;
			int allowed = allowedHours.get(h);
			if (q.getOpen5()<highEntry && q.getHigh5()>=highEntry && allowed==1){
				entry      = q.getClose5();
				stopLoss   = entry+sl*10;
				takeProfit = entry-tp*10;
				posType = PositionType.SHORT;
			}
			
			if (q.getOpen5()>lowEntry && q.getLow5()<=lowEntry && allowed==1){
				entry      = q.getClose5();
				stopLoss   = entry-sl*10;
				takeProfit = entry+tp*10;
				posType = PositionType.LONG;
			}
			
			if (entry!=-1){
				pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(stopLoss);
				pos.setTp(takeProfit);		
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenDiff(i);
				positions.add(pos);
				//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
			}
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (q1.getHigh5()>=p.getSl()){
							win = -1;
							closed = true;
						}else if (q1.getLow5()<=p.getTp()){
							win = 1;
							closed = true;
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (q1.getLow5()<=p.getSl()){
							win = -1;
							closed = true;
						}else if (q1.getHigh5()>=p.getTp()){
							win = 1;
							closed = true;
						}
					}	
					
					if (closed){						
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						if (win==1) wins++;
						if (win==-1) losses++;
						//System.out.println(p.toString());
						positions.remove(j);//borramos y no avanzamos
						//System.out.println(p.toString());
					}else{
						j++;
					}
				}
			}//for positions	
			
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}//for data
		
		int totals = wins+losses;
		double perWin = wins*100.0/totals;
		double perLoss = 100.0-perWin;
		double pf = (perWin*tp*1.0)/(perLoss*sl);
		double exp = (perWin*tp*1.0-perLoss*sl)/100.0;
		
		System.out.println(hours
				+" "+nATR
				+" "+tp+" "+sl
				+" "+PrintUtils.Print2(percent)
				+" || "
				+" "+totals
				+" "+PrintUtils.Print2(perWin)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2(exp)
				);
		
	}

	
	public static void testFib2(ArrayList<QuoteShort> data,int begin,int end,String hours,
			double percent,int nATR,int tp,int sl,int candleSize){
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		
		int wins = 0;
		int losses = 0;
		int totalDays = 0;
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100.0;
		int DO = 0;
		Calendar cal = Calendar.getInstance();
		PositionShort pos = null;
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//para al calculo de rangos
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				DO = q.getOpen5();
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
			}
			
			int atrPips = (int) (atr*percent*0.01);
			int highEntry = DO + atrPips*10;
			int lowEntry  = DO - atrPips*10;
			int actualSizeH = highEntry-q.getOpen5();
			int actualSizeL = q.getOpen5()-lowEntry;
			
			int entry = -1;
			int stopLoss = -1;
			int takeProfit = -1;
			PositionType posType = PositionType.NONE;
			int allowed = allowedHours.get(h);
			if (q.getHigh5()>=highEntry && q.getOpen5()<highEntry 
					&& actualSizeH>=candleSize*10
					&& allowed==1){
				entry      = highEntry;
				stopLoss   = entry+sl*10;
				takeProfit = entry-tp*10;
				posType = PositionType.SHORT;
			}
			
			if (q.getLow5()<=lowEntry && q.getOpen5()>lowEntry
					&& actualSizeL>=candleSize*10
					&& allowed==1){
				entry      = lowEntry;
				stopLoss   = entry-sl*10;
				takeProfit = entry+tp*10;
				posType = PositionType.LONG;
			}
			
			if (entry!=-1){
				pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(stopLoss);
				pos.setTp(takeProfit);		
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenIndex(i);
				positions.add(pos);
				//System.out.println(atr+" "+atrPips+" "+DO+" "+highEntry+" "+lowEntry);
				//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
			}
			int totalOrders = positions.size();
			int j = 0;
			while (j<positions.size()){
			//for (int j = 0;j<totalOrders;j++){
				//if (j>=positions.size()) break;
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (q.getHigh5()>=p.getSl()){
							win = -1;
							closed = true;
						}else if (i==p.getOpenIndex() && q.getClose5()<=p.getTp()){
							win = 1;
							closed = true;
						}else if (i!=p.getOpenIndex() && q.getLow5()<=p.getTp()){
							win = 1;
							closed = true;
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (q.getLow5()<=p.getSl()){
							win = -1;
							closed = true;
						}else if (i==p.getOpenIndex() && q.getClose5()>=p.getTp()){
							win = 1;
							closed = true;
						}else if (i!=p.getOpenIndex() && q.getHigh5()>=p.getTp()){
							win = 1;
							closed = true;
						}
					}	
					
					if (closed){						
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						if (win==1) wins++;
						if (win==-1) losses++;
						//System.out.println(p.toString());
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;//avanzamos
					}
				}
			}//for positions	
			
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}//for data
		
		/*int wins = 0;
		int losses = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (p.getWin()==1) wins++;
				if (p.getWin()==-1) losses++;
			}
		}*/
		int totals = wins+losses;
		double perWin = wins*100.0/totals;
		double perLoss = 100.0-perWin;
		double pf = (perWin*tp*1.0)/(perLoss*sl);
		double exp = (perWin*tp*1.0-perLoss*sl)/100.0;
		
		System.out.println(hours
				+" "+nATR
				+" "+tp+" "+sl
				+" "+PrintUtils.Print2(percent)
				+" || "
				+" "+totals
				+" "+PrintUtils.Print2(perWin)
				+" "+PrintUtils.Print2(pf)
				+" "+PrintUtils.Print2(exp)
				);
		
	}

	public static void testFib(ArrayList<QuoteShort> data,int begin,int end,String hours,
			double percent,int nATR,int tp,int sl){
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		
		int totalDays = 0;
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100.0;
		int DO = 0;
		Calendar cal = Calendar.getInstance();
		PositionShort pos = null;
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//para al calculo de rangos
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				DO = q.getOpen5();
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
			}
			
			int atrPips = (int) (atr*percent*0.01);
			int highEntry = DO + atrPips*10;
			int lowEntry  = DO - atrPips*10;
			//System.out.println(atr+" "+atrPips+" "+DO+" "+highEntry+" "+lowEntry);
			int entry = -1;
			int stopLoss = -1;
			int takeProfit = -1;
			PositionType posType = PositionType.NONE;
			int allowed = allowedHours.get(h);
			if (q.getHigh5()>=highEntry && q.getOpen5()<highEntry && allowed==1){
				entry      = q.getHigh5();
				stopLoss   = entry+sl*10;
				takeProfit = entry-tp*10;
				posType = PositionType.SHORT;
			}
			
			if (q.getLow5()<=lowEntry && q.getOpen5()>lowEntry && allowed==1){
				entry      = q.getLow5();
				stopLoss   = entry-sl*10;
				takeProfit = entry+tp*10;
				posType = PositionType.LONG;
			}
			
			if (entry!=-1){
				pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(stopLoss);
				pos.setTp(takeProfit);		
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenDiff(i);
				positions.add(pos);
			}
			
			for (int j = 0;j<positions.size();j++){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (i==p.getOpenIndex() && q.getClose5()<=p.getTp()){
							win = 1;
							closed = true;
						}
						if (i!=p.getOpenIndex() && q.getHigh5()>=p.getSl()){
							win = -1;
							closed = true;
						}
						if (i!=p.getOpenIndex() && q.getLow5()<=p.getTp()){
							win = 1;
							closed = true;
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (i==p.getOpenIndex() && q.getClose5()>=p.getTp()){
							win = 1;
							closed = true;
						}
						if (i!=p.getOpenIndex() && q.getLow5()<=p.getSl()){
							win = -1;
							closed = true;
						}
						if (i!=p.getOpenIndex() && q.getHigh5()>=p.getTp()){
							win = 1;
							closed = true;
						}
					}	
					
					if (closed){
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
					}
				}
			}//for positions	
			
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}//for data
		
		int wins = 0;
		int losses = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (p.getWin()==1) wins++;
				if (p.getWin()==-1) losses++;
			}
		}
		int totals = wins+losses;
		double perWin = wins*100.0/totals;
		
		System.out.println(nATR
				+" "+tp+" "+sl
				+" "+PrintUtils.Print2(percent)
				+" || "
				+" "+totals
				+" "+PrintUtils.Print2(perWin)
				);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//EURUSD
		String path30m  = "c:\\fxdata\\EURUSD_UTC_30 Mins_Bid";
		String path20m  = "c:\\fxdata\\EURUSD_UTC_20 Mins_Bid";
		String path15m  = "c:\\fxdata\\EURUSD_UTC_15 Mins_Bid";
		String path10m  = "c:\\fxdata\\EURUSD_UTC_10 Mins_Bid";
		String path5m  = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid";
		String path1m  = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid";
		String path30s = "c:\\fxdata\\EURUSD_UTC_30 Secs_Bid";
		String path15s = "c:\\fxdata\\EURUSD_UTC_15 Secs_Bid";
		String path10s = "c:\\fxdata\\EURUSD_UTC_10 Secs_Bid";
		String path5s  = "c:\\fxdata\\EURUSD_UTC_5 Secs_Bid";
		//String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.02.16.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.02.20.csv";
		//String path5m   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2015.02.20.csv";
		//String path5m   = "c:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.08.03_2015.02.20.csv";
		//String path5m   = "c:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2005.10.07_2015.02.20.csv";
		//String path5m   = "c:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.08.03_2015.02.20.csv";
		//String path1m   = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2015.02.19.csv";
		//String path30s   = "c:\\fxdata\\EURUSD_UTC_30 Secs_Bid_2009.01.01_2015.02.19.csv";
		//String path1m   = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.02.19.csv";
		//String path1m   = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2014.12.30.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";

		//boolean is5minute = true;
		TIMEFRAME mode = TIMEFRAME.MINUTES_1;
		ArrayList<TIMEFRAME> modes = new ArrayList<TIMEFRAME>();
		modes.add(TIMEFRAME.MINUTES_30);modes.add(TIMEFRAME.MINUTES_15);modes.add(TIMEFRAME.MINUTES_10);
		modes.add(TIMEFRAME.MINUTES_5);modes.add(TIMEFRAME.MINUTES_1);modes.add(TIMEFRAME.SECONDS_30);
		modes.add(TIMEFRAME.SECONDS_15);modes.add(TIMEFRAME.SECONDS_10);modes.add(TIMEFRAME.SECONDS_5);

		int tot = modes.size();
		tot=5;
		for (int m=4;m<tot;m++){
			//m=5;
			mode = modes.get(m);
			String path = path5m;
			int begin = 1;
			int end = 1;
			int factor = 1;
			if (mode==TIMEFRAME.MINUTES_30){
				path = path30m;
				begin = 1;
				end   = 9000000;
				factor = 1;
			}
			if (mode==TIMEFRAME.MINUTES_15){
				path = path15m;
				begin = 1;
				end   = 9000000;
				factor = 2;
			}
			if (mode==TIMEFRAME.MINUTES_10){
				path = path10m;
				begin = 1;
				end   = 9000000;
				factor = 3;
			}
			if (mode==TIMEFRAME.MINUTES_5){
				path = path5m;
				begin = 1;
				end   = 9000000;
				factor = 6;
			}else if (mode==TIMEFRAME.MINUTES_1){
				path = path1m;
				begin = 1;
				end   = 9000000;
				factor = 30;
			}else if (mode==TIMEFRAME.SECONDS_30){
				path=path30s;
				begin = 1;
				end   = 9000000;
				factor = 60;
			}else if (mode==TIMEFRAME.SECONDS_15){
				path=path15s;
				begin = 1;
				end   = 9000000;
				factor = 120;
			}else if (mode==TIMEFRAME.SECONDS_10){
				path=path10s;
				begin = 1;
				end   = 9000000;
				factor = 180;
			}else if (mode==TIMEFRAME.SECONDS_5){
				path=path5s;
				begin = 1;
				end   = 9000000;
				factor = 360;
			}
			//path +="_2004.12.31_2006.12.30.csv";
			//path +="_2006.12.31_2008.12.30.csv";
			//path +="_2009.12.31_2015.02.22.csv";
			//path +="_2008.12.31_2010.12.30.csv";
			//path +="_2011.10.31_2015.02.22.csv";
			//path +="_2011.12.31_2013.12.30.csv";
			//path +="_2012.12.31_2015.02.21.csv";
			//path +="_2012.12.31_2013.12.30.csv";
			//path +="_2011.12.31_2012.12.30.csv";
			//path +="_2012.12.31_2015.02.22.csv";
			//path +="_2012.12.31_2013.12.30.csv";
			//path +="_2013.12.31_2015.02.22.csv";
			//path +="_2014.10.31_2015.02.22.csv";
			  path +="_2008.12.31_2015.02.22.csv";
			
			File f = new File(path);
			if (!f.exists()) continue;
			ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			
			double comm = 1.7;
			System.out.println(mode+" total data: "+data.size());
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			ArrayList<Integer> nBars = new ArrayList<Integer>();
			for (int n=0;n<=23;n++) nBars.add(500);
			nBars.set(0, 85);nBars.set(1, 640);nBars.set(2, 2100);nBars.set(3, 7600);nBars.set(4, 3000);
			nBars.set(5, 2700);nBars.set(6, 600);nBars.set(7, 1400);nBars.set(8, 700);nBars.set(9, 2500);//1418m
			nBars.set(11, 29100);nBars.set(12, 21100);//550000m
			nBars.set(17, 21100);
			nBars.set(20, 3100);nBars.set(21, 200);nBars.set(22, 4500);nBars.set(23, 3230);
			int start = begin;
			start=1;
			end = data.size();
			for (begin = start;begin<=start;begin+=50000){
				for (int nATR=5;nATR<=5;nATR++){
					//for (int tp=10;tp<=10;tp++){
					for (double tp=0.10;tp<=0.10;tp+=0.01){
					//for (double sl=0.12;sl<=0.12;sl+=0.01){
						//for (int sl=(int) (1.0*tp);sl<=1*tp;sl+=1){
						for (double sl=3.0*tp;sl<=3.0*tp;sl+=0.1*tp){
						//for (double tp=0.12;tp<=0.12;tp+=10*sl){	
						//for (double sl=0.0;sl<=0.20;sl+=0.01){
							for (double per=50.0;per<=50;per+=0.2){
								//for (int bar=factor*83;bar<=factor*83;bar+=50){
								for (int bar=10;bar<=10;bar+=5){
									//nBars.set(23, bar);
									for (int maxAllowed=10;maxAllowed<=10;maxAllowed+=1){
										for (int maxDOCrosses=9999;maxDOCrosses<=9999;maxDOCrosses+=1){
											for (int minPips=0;minPips<=0;minPips++){
												for (int minBarRange=0;minBarRange<=200;minBarRange+=10){
													for (comm=1.6;comm<=1.6;comm+=0.05){
														for (double risk=3.0;risk<=3.0;risk+=0.2){
															for (int h=0;h<=0;h++){
																//TestAllFibs.testBars(data,maxMins, begin, end, "0 1 19 20 21 22 23", bar, nATR, tp, sl,
																//TestAllFibs.testBars(data,maxMins, begin, end, "0 1 2 3 4 5 6 7 8 9 23", bar, nATR, tp, sl,
																		//maxAllowed,maxDOCrosses,minPips,comm,false);
																TestAllFibs.testBars$$(data,maxMins,nBars, begin, end, "0 1 2 3 4 5 6 7 8 9", bar, nATR, tp, sl,
																//TestAllFibs.testBars$$(data,maxMins, begin, end, String.valueOf(h), bar, nATR, tp, sl,
																		minBarRange,maxAllowed,maxDOCrosses,minPips,500,risk,comm);
																//TestAllFibs.testBars(data,maxMins, begin, end, String.valueOf(h), bar, nATR, tp, sl,maxAllowed,comm);
																//(TestAllFibs.testFib3(data, begin, end, "0 1 2 3 4 5 6 7 8 9", per, nATR, tp, sl);
																//TestAllFibs.testFib2(data, begin, end, "0 1 2 3 4 5 6 7 8 9", per, nATR, tp, sl, candleSize);
																//TestAllFibs.testFib2(data, begin, end, "16 17 18 19 20 21 22 23", per, nATR, tp, sl, candleSize);
																//TestAllFibs.testBars(data,maxMins, begin, end, String.valueOf(h), bar, nATR, tp, sl);
															}
														}
													}
												}//minBarRange
											}
										}
									}
								}//bar
							}
						}
					}
				}
			}
		}//modes
	}

}
