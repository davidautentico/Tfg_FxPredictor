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
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMoves {
	
	public static int doTestTrading5(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			ArrayList<MaxMinConfig> configs,
			boolean reverseMode,
			boolean printAlways,
			int aMonthsTarget,
			int debug) {
		

		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winsFloating=0;
		int lossesFloating=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		boolean isTested = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;

		int winsFloatingPips = 0;
		int lostFloatingPips = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		double avgRange = 600.0;
		int tpf = 200;
		int slf = 200;
		int comm = 00;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int lastMonth = -1;
		int totalMonths = 0;
		int winMonths = 0;
		int winsM = 0;
		int lossM = 0;
		int countMonths = 0;
		int hl = 0;
		int DO = 0;
		 maxLosses = 0;
		actualLosses = 0;
		for (int i=240;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (month!=lastMonth){
				
				int totalM = winsM + lossM;
				double pfm = winsM*1.0/lossM;
				if (pfm>=1.0){
					countMonths++;
				}
				
				winsM = 0;
				lossM = 0;
				lastMonth = month;
			}
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfY = winsYear*1.0/lossYear;
					double avgy = (winsYear-lossYear)*0.1/tradesYear;
					if (pfY>=1.0 
							&& avgy>=0.5
							) countYears++;
				}
				winsYear = 0;
				lossYear = 0;
				tradesYear = 0;
				high = -1;
				low = -1;
				lastYear = y;
				hl=0;
			}
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay
					&& min==15
					) {	
				
				if (lastDay!=-1){
					int dayRange = high-low;
					totalDays++;
					ranges.add(dayRange);
					avgRange = MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
				}
				
				DO = q.getOpen5();
				high = -1;
				low = -1;
				lastDay = day;
				isTested = false;
			}//day
			
						
			int maxMin = maxMins.get(i-1);
			
			int hAllowed = 0;
			
			hAllowed = configs.get(h).isActive() ? 1:0;
						
			if (hAllowed==1
					){
				int DOLow = DO-low;
				//thr tp  sl y hclose es configurable
				int thr = configs.get(h).getThr1();
				tpf = configs.get(h).getTp();
				slf = configs.get(h).getSl();
				int hc = configs.get(h).gethClose();
				if (maxMin>=thr
						//&& DOLow>=maxDiff
						//&& q1.getHigh5()-q1.getClose5()<=10
						){
					PositionShort pos = new PositionShort();
					
					int entry = q.getOpen5();
					int slValue = entry + slf;
					int tpValue = entry - tpf;
					pos.setPositionType(PositionType.SHORT);
					
					if (!reverseMode){
						slValue = entry - slf;
						tpValue = entry + tpf;
						pos.setPositionType(PositionType.LONG);
					}
					pos.setExpiredTime(hc);				
					pos.setEntry(entry);
					pos.setTp(tpValue);
					pos.setSl(slValue);
					pos.setPositionStatus(PositionStatus.OPEN);					
					positions.add(pos);				
				}else{
					
					int DOHigh= high-DO;
					
					if (maxMin<=-thr
							//&& DOHigh>=maxDiff
							//&& q1.getClose5()-q1.getLow5()<=10
							){
						PositionShort pos = new PositionShort();
						
						int entry = q.getOpen5();
						int slValue = entry - slf;
						int tpValue = entry + tpf;
						pos.setPositionType(PositionType.LONG);
						if (!reverseMode){
							slValue =  entry + slf;
							tpValue = entry - tpf;
							pos.setPositionType(PositionType.SHORT);
						}
						pos.setExpiredTime(hc);	
						pos.setEntry(entry);
						pos.setTp(tpValue);
						pos.setSl(slValue);
						pos.setPositionStatus(PositionStatus.OPEN);
						positions.add(pos);
					}
					//isTested=true;
				}
			}
						
			int j=0;
			winsFloatingPips  = 0;
			lostFloatingPips = 0;
			winsFloating = 0;
			lossesFloating = 0;
			while (j<positions.size()){				
				boolean isClosed = false;
				int closeValue = q.getClose5();
				PositionShort p = positions.get(j);
				int h3 = p.getExpiredTime();
				if (p.getPositionType()==PositionType.SHORT){
					if (q.getHigh5()>=p.getSl()){
						isClosed = true;
						closeValue = p.getSl();
					}else if (q.getLow5()<=p.getTp()){
						isClosed = true;
						closeValue = p.getTp();
					}else{
						//criterios de cierre nuevos
						if (h==h3){
							isClosed = true;
							closeValue = q.getOpen5();
						}						
					}
				}else if (p.getPositionType()==PositionType.LONG){
					if (q.getLow5()<=p.getSl()){
						isClosed = true;
						closeValue = p.getSl();
					}else if (q.getHigh5()>=p.getTp()){
						isClosed = true;
						closeValue = p.getTp();
					}else{
						//criterios de cierre nuevos
						if (h==h3){
							isClosed = true;
							closeValue = q.getOpen5();
						}
					}
				}
				
				if (!isClosed){
					j++;
					
					int pips = closeValue-p.getEntry();
					if (p.getPositionType()==PositionType.SHORT)
						pips = p.getEntry()-closeValue;
					
					pips -=comm;
					if (pips>=0){
						winsFloatingPips += pips;
						winsFloating++;
					}else{
						lostFloatingPips += -pips;
						lossesFloating++;
					}
					
					
				}else{					
					int pips = closeValue-p.getEntry();
					if (p.getPositionType()==PositionType.SHORT)
						pips = p.getEntry()-closeValue;
					
					pips -=comm;
					if (pips>=0){
						winPips += pips;
						wins++;
						winsYear += pips;
						winsM += pips;
						
						actualLosses = 0;
					}else{
						lostPips += -pips;
						losses++;
						lossYear += -pips;
						lossM += -pips;
						
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}
					positions.remove(j);
					tradesYear++;
				}
			}
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}
		}//for
		
		double pf=9999.0;
		if (accLosses>0){
			pf = accWins*1.0/accLosses;
		}
		double avg = (accWins-accLosses)*1.0/(wins+losses+others);
		trades = wins+losses;
		
		pf = winPips*1.0/lostPips;
		avg = (winPips-lostPips)*0.1/trades;
		
		double pfFinal = (winPips+winsFloatingPips)*1.0/(lostPips+lostFloatingPips);
		int tradesFinal = wins+losses+winsFloating+lossesFloating;
		double avgFinal =  ((winPips+winsFloatingPips)-(lostPips+lostFloatingPips))*0.1/(tradesFinal);
		
		if ((
				countMonths>=aMonthsTarget
			) 
			|| printAlways
				){
			if (printAlways){
				if (debug!=100){
					System.out.println(header
							+" | "+y1+" "+y2
							+" || "
							+" "+trades
							+" "+wins
							+" "+losses
							+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
							+" || "+PrintUtils.Print2dec(pf, false)
							+" "+PrintUtils.Print2dec(avg, false)
							+" || "+countYears+" "+countMonths
							+" ||| "+positions.size()
							+" || "+PrintUtils.Print2dec(pfFinal, false)
							+" "+PrintUtils.Print2dec(avgFinal, false)
							+" || "+maxLosses
							);
				}
			}else{
				System.out.println(header
						+" | "+y1+" "+y2
						+" || "
						+" "+trades
						+" "+wins
						+" "+losses
						+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
						+" || "+PrintUtils.Print2dec(pf, false)
						+" "+PrintUtils.Print2dec(avg, false)
						+" || "+countYears+" "+countMonths
						+" ||| "+positions.size()
						+" || "+PrintUtils.Print2dec(pfFinal, false)
						+" "+PrintUtils.Print2dec(avgFinal, false)
						+" || "+maxLosses
						);
			}
		  return countYears;
		}
		
		return -1;		
	}
	
	public static int doTestTrading3(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,int h3,
			int thr,
			double tpFactor,
			double slFactor,
			double sizeCandleFactor,
			int maxBars,
			int maxDiff,
			boolean reverseMode,
			boolean factorMode,
			boolean printAlways,
			int debug) {
		

		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winsFloating=0;
		int lossesFloating=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		boolean isTested = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;

		int winsFloatingPips = 0;
		int lostFloatingPips = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		double avgRange = 600.0;
		int tpf = 200;
		int slf = 200;
		int comm = 20;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		int DO = 0;
		for (int i=240;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfY = winsYear*1.0/lossYear;
					double avgy = (winsYear-lossYear)*0.1/tradesYear;
					if (pfY>=1.0 
							&& avgy>=0.5
							) countYears++;
				}
				winsYear = 0;
				lossYear = 0;
				tradesYear = 0;
				high = -1;
				low = -1;
				lastYear = y;
				hl=0;
			}
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay
					&& min==15
					) {	
				
				if (lastDay!=-1){
					int dayRange = high-low;
					totalDays++;
					ranges.add(dayRange);
					avgRange = MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					
					tpf = (int) (avgRange*tpFactor);
					slf = (int) (avgRange*slFactor);
				}
				
				DO = q.getOpen5();
				high = -1;
				low = -1;
				lastDay = day;
				isTested = false;
			}//day
			
			//en debug
			if (!factorMode){
				tpf = (int) tpFactor;
				slf = (int) slFactor;
			}
			
			int maxMin = maxMins.get(i-1);
						
			if (h>=h1 && h<=h2
					&& !isTested
					&& (h>0 || h==0 && min>=15)
					){
				int DOLow = DO-low;
				
				if (maxMin>=thr
						//&& DOLow>=maxDiff
						){
					PositionShort pos = new PositionShort();
					
					int entry = q.getOpen5();
					int slValue = entry + slf;
					int tpValue = entry - tpf;
					pos.setPositionType(PositionType.SHORT);
					
					if (!reverseMode){
						slValue = entry - slf;
						tpValue = entry + tpf;
						pos.setPositionType(PositionType.LONG);
					}
									
					pos.setEntry(entry);
					pos.setTp(tpValue);
					pos.setSl(slValue);
					pos.setPositionStatus(PositionStatus.OPEN);					
					positions.add(pos);				
				}else{
					
					int DOHigh= high-DO;
					
					if (maxMin<=-thr
							//&& DOHigh>=maxDiff
							){
						PositionShort pos = new PositionShort();
						
						int entry = q.getOpen5();
						int slValue = entry - slf;
						int tpValue = entry + tpf;
						pos.setPositionType(PositionType.LONG);
						if (!reverseMode){
							slValue =  entry + slf;
							tpValue = entry - tpf;
							pos.setPositionType(PositionType.SHORT);
						}
						
						pos.setEntry(entry);
						pos.setTp(tpValue);
						pos.setSl(slValue);
						pos.setPositionStatus(PositionStatus.OPEN);
						positions.add(pos);
					}
					//isTested=true;
				}
			}
						
			int j=0;
			winsFloatingPips  = 0;
			lostFloatingPips = 0;
			winsFloating = 0;
			lossesFloating = 0;
			while (j<positions.size()){				
				boolean isClosed = false;
				int closeValue = q.getClose5();
				PositionShort p = positions.get(j);
				
				if (p.getPositionType()==PositionType.SHORT){
					if (q.getHigh5()>=p.getSl()){
						isClosed = true;
						closeValue = p.getSl();
					}else if (q.getLow5()<=p.getTp()){
						isClosed = true;
						closeValue = p.getTp();
					}else{
						//criterios de cierre nuevos
						if (h==h3){
							isClosed = true;
							closeValue = q.getOpen5();
						}						
					}
				}else if (p.getPositionType()==PositionType.LONG){
					if (q.getLow5()<=p.getSl()){
						isClosed = true;
						closeValue = p.getSl();
					}else if (q.getHigh5()>=p.getTp()){
						isClosed = true;
						closeValue = p.getTp();
					}else{
						//criterios de cierre nuevos
						if (h==h3){
							isClosed = true;
							closeValue = q.getOpen5();
						}
					}
				}
				
				if (!isClosed){
					j++;
					
					int pips = closeValue-p.getEntry();
					if (p.getPositionType()==PositionType.SHORT)
						pips = p.getEntry()-closeValue;
					
					pips -=comm;
					if (pips>=0){
						winsFloatingPips += pips;
						winsFloating++;
					}else{
						lostFloatingPips += -pips;
						lossesFloating++;
					}
					
					
				}else{					
					int pips = closeValue-p.getEntry();
					if (p.getPositionType()==PositionType.SHORT)
						pips = p.getEntry()-closeValue;
					
					pips -=comm;
					if (pips>=0){
						winPips += pips;
						wins++;
						winsYear += pips;
						
					}else{
						lostPips += -pips;
						losses++;
						lossYear += -pips;
					}
					positions.remove(j);
					tradesYear++;
				}
			}
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}
		}//for
		
		double pf=9999.0;
		if (accLosses>0){
			pf = accWins*1.0/accLosses;
		}
		double avg = (accWins-accLosses)*1.0/(wins+losses+others);
		trades = wins+losses;
		
		pf = winPips*1.0/lostPips;
		avg = (winPips-lostPips)*0.1/trades;
		
		double pfFinal = (winPips+winsFloatingPips)*1.0/(lostPips+lostFloatingPips);
		int tradesFinal = wins+losses+winsFloating+lossesFloating;
		double avgFinal =  ((winPips+winsFloatingPips)-(lostPips+lostFloatingPips))*0.1/(tradesFinal);
		
		if ((
				(countYears>=8 && pf>=1.5 && avg>=0.0) 
				|| (countYears>=9 && pf>=1.0 && avg>=0.0)  
			) 
			|| printAlways
				){
			if (printAlways){
				if (debug!=100){
					System.out.println(
							y1+" "+y2
							+" "+h1+" "+h2+" "+h3
							+" "+thr
							+" "+PrintUtils.Print2dec(tpFactor, false)
							+" "+PrintUtils.Print2dec(slFactor, false)
							+" "+maxBars
							+" || "
							+" "+trades
							+" "+wins
							+" "+losses
							+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
							+" || "+PrintUtils.Print2dec(pf, false)
							+" "+PrintUtils.Print2dec(avg, false)
							+" || "+countYears
							+" ||| "+positions.size()
							+" || "+PrintUtils.Print2dec(pfFinal, false)
							+" "+PrintUtils.Print2dec(avgFinal, false)
							);
				}
			}else{
				System.out.println(
						y1+" "+y2
						+" "+h1+" "+h2+" "+h3
						+" "+thr
						+" "+PrintUtils.Print2dec(tpFactor, false)
						+" "+PrintUtils.Print2dec(slFactor, false)
						+" "+maxBars
						+" || "
						+" "+trades
						+" "+wins
						+" "+losses
						+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
						+" || "+PrintUtils.Print2dec(pf, false)
						+" "+PrintUtils.Print2dec(avg, false)
						+" || "+countYears
						+" ||| "+positions.size()
						+" || "+PrintUtils.Print2dec(pfFinal, false)
						+" "+PrintUtils.Print2dec(avgFinal, false)
						);
			}
		  return countYears;
		}
		
		return -1;		
	}
	
	
	public static int doTestTrading4(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,int h3,
			int thr,
			double tpFactor,
			double slFactor,
			double sizeCandleFactor,
			int maxBars,
			int maxDiff,
			boolean reverseMode,
			boolean factorMode,
			boolean printAlways,
			int debug) {
		

		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winsFloating=0;
		int lossesFloating=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		boolean isTested = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;

		int winsFloatingPips = 0;
		int lostFloatingPips = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		double avgRange = 600.0;
		int tpf = 200;
		int slf = 200;
		int comm = 0;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		int DO = 0;
		
		int lastMax = -1;
		int lastMin = -1;
		int lastOpenIdx = -1;
		for (int i=240;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfY = winsYear*1.0/lossYear;
					double avgy = (winsYear-lossYear)*0.1/tradesYear;
					if (pfY>=1.0 
							&& avgy>=3.0
							) countYears++;
				}
				winsYear = 0;
				lossYear = 0;
				tradesYear = 0;
				high = -1;
				low = -1;
				lastYear = y;
				hl=0;
			}
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay
					&& min==15
					) {	
				
				if (lastDay!=-1){
					int dayRange = high-low;
					totalDays++;
					ranges.add(dayRange);
					avgRange = MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					
					tpf = (int) (avgRange*tpFactor);
					slf = (int) (avgRange*slFactor);
				}
				
				DO = q.getOpen5();
				high = -1;
				low = -1;
				lastDay = day;
				isTested = false;
			}//day
			
			//en debug
			if (!factorMode){
				tpf = (int) tpFactor;
				slf = (int) slFactor;
			}
			
			int maxMin = maxMins.get(i-1);
			
		
		
			if (h>=h1 && h<=h2
					&& !isTested
					//&& min==0
					){
				int DOLow = DO-low;
				
				if (lastMax!=-1 && q.getHigh5()>=lastMax 
						//maxMin>=thr
						//&& DOLow>=maxDiff
						){
					PositionShort pos = new PositionShort();
					
					int entry = lastMax;//q.getOpen5();
					int slValue = entry + slf;
					int tpValue = entry - tpf;
					pos.setPositionType(PositionType.SHORT);
					
					if (!reverseMode){
						slValue = entry - slf;
						tpValue = entry + tpf;
						pos.setPositionType(PositionType.LONG);
					}
									
					pos.setEntry(entry);
					pos.setTp(tpValue);
					pos.setSl(slValue);
					pos.setPositionStatus(PositionStatus.OPEN);					
					positions.add(pos);			
					
					lastOpenIdx = i;
				}else{
					
					int DOHigh= high-DO;
					
					if (lastMin!=-1 && q.getLow5()<=lastMin //maxMin<=-thr
							//&& DOHigh>=maxDiff
							){
						PositionShort pos = new PositionShort();
						
						int entry = lastMin;//q.getOpen5();
						int slValue = entry - slf;
						int tpValue = entry + tpf;
						pos.setPositionType(PositionType.LONG);
						if (!reverseMode){
							slValue =  entry + slf;
							tpValue = entry - tpf;
							pos.setPositionType(PositionType.SHORT);
						}
						
						pos.setEntry(entry);
						pos.setTp(tpValue);
						pos.setSl(slValue);
						pos.setPositionStatus(PositionStatus.OPEN);
						positions.add(pos);
						
						lastOpenIdx = i;
					}
					//isTested=true;
				}
			}
						
			int j=0;
			winsFloatingPips  = 0;
			lostFloatingPips = 0;
			winsFloating = 0;
			lossesFloating = 0;
			while (j<positions.size()){				
				boolean isClosed = false;
				int closeValue = q.getClose5();
				PositionShort p = positions.get(j);
				
				if (p.getPositionType()==PositionType.SHORT){
					if (q.getHigh5()>=p.getSl()){
						isClosed = true;
						closeValue = p.getSl();
					}else if (q.getLow5()<=p.getTp()){
						isClosed = true;
						closeValue = p.getTp();
					}else{
						//criterios de cierre nuevos
						if (h==h3){
							isClosed = true;
							closeValue = q.getOpen5();
							if (i==lastOpenIdx)
								closeValue = q.getClose5();
						}						
					}
				}else if (p.getPositionType()==PositionType.LONG){
					if (q.getLow5()<=p.getSl()){
						isClosed = true;
						closeValue = p.getSl();
					}else if (q.getHigh5()>=p.getTp()){
						isClosed = true;
						closeValue = p.getTp();
					}else{
						//criterios de cierre nuevos
						if (h==h3){
							isClosed = true;
							closeValue = q.getOpen5();
							if (i==lastOpenIdx)
								closeValue = q.getClose5();
						}
					}
				}
				
				if (!isClosed){
					j++;
					
					int pips = closeValue-p.getEntry();
					if (p.getPositionType()==PositionType.SHORT)
						pips = p.getEntry()-closeValue;
					
					pips -=comm;
					if (pips>=0){
						winsFloatingPips += pips;
						winsFloating++;
					}else{
						lostFloatingPips += -pips;
						lossesFloating++;
					}
					
					
				}else{					
					int pips = closeValue-p.getEntry();
					if (p.getPositionType()==PositionType.SHORT)
						pips = p.getEntry()-closeValue;
					
					pips -=comm;
					if (pips>=0){
						winPips += pips;
						wins++;
						winsYear += pips;
						
					}else{
						lostPips += -pips;
						losses++;
						lossYear += -pips;
					}
					positions.remove(j);
					tradesYear++;
				}
			}
			
			if (high==-1 || q.getHigh5()>high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<low){
				low = q.getLow5();								
			}
			
			//actualizamos el ultimo pico
			maxMin = maxMins.get(i);			
			if (maxMin>=thr){
				lastMax = q.getHigh5();
			}else if (maxMin<=-thr){
				lastMin = q.getLow5();
			}
			
		}//for
		
		double pf=9999.0;
		if (accLosses>0){
			pf = accWins*1.0/accLosses;
		}
		double avg = (accWins-accLosses)*1.0/(wins+losses+others);
		trades = wins+losses;
		
		pf = winPips*1.0/lostPips;
		avg = (winPips-lostPips)*0.1/trades;
		
		double pfFinal = (winPips+winsFloatingPips)*1.0/(lostPips+lostFloatingPips);
		int tradesFinal = wins+losses+winsFloating+lossesFloating;
		double avgFinal =  ((winPips+winsFloatingPips)-(lostPips+lostFloatingPips))*0.1/(tradesFinal);
		
		if ((countYears>=5 
				&& pf>=1.3 && avg>=4.0
				) 
				|| printAlways
				){
			
			if (printAlways
					&& debug!=100
					){
				System.out.println(
						y1+" "+y2
						+" "+h1+" "+h2+" "+h3
						+" "+thr
						+" "+PrintUtils.Print2dec(tpFactor, false)
						+" "+PrintUtils.Print2dec(slFactor, false)
						+" "+maxBars
						+" || "
						+" "+trades
						+" "+wins
						+" "+losses
						+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
						+" || "+PrintUtils.Print2dec(pf, false)
						+" "+PrintUtils.Print2dec(avg, false)
						+" || "+countYears
						+" ||| "+positions.size()
						+" || "+PrintUtils.Print2dec(pfFinal, false)
						+" "+PrintUtils.Print2dec(avgFinal, false)
						);
			}
		  return countYears;
		}
		return -1;
	}
	
	public static void doTestTrading2(ArrayList<QuoteShort> data,
			int y1,int y2,
			int thr,int minPips,int debug) {
		

		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
		int indexDay = 0;
		int win=0;
		int total2 = 0;
		int count2=0;
		int total3=0;
		int count3=0;
		int acc=0;
		int totalDays=0;
		int actualLeg = 0;
		int index1 = 0;
		int index2=0;
		int legSize = 0;
		int tradeMode=0;
		int count4=0;
		int win4=0;
		int total4=0;
		int loss4=0;
		int cases = 0;
		int casesWins=0;
		int casesLosses=0;
		int accReverse = 0;
		int reverseCases=0;
		int accCloses=0;
		int countCloses=0;
		int lastLevel=0;
		
		int microLots=0;
		int trade = 0;
		int tradePrice= 0;
		
		int lossesAcc = 0;
		int winsAcc=0;
		int losses5=0;
		int wins5=0;
		
		int trades = 0;
		int wins=0;
		int losses=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay
					&& min==15
					) {
				if (lastDay!=-1) {
					
					if (trade!=0) {
						if (win4==1) {
							wins++;
						}else if (win4==-1) {
							losses++;
						}else {
							if (actualLeg==1) {
								int size = q1.getClose5()-data.get(index1).getLow5();
								int diff = size-(lastLevel);
								accOthers +=diff;
								others++;
								accSize+=size ;
								
								if (legSize>=1600){
									accLosses +=2668;
									System.out.println("1600");
								}
								else if (legSize>=1400){
									accLosses +=1324;
									System.out.println("1400");
								}
								else if (legSize>=1200){
									accLosses +=652;
								}else {
									if (legSize>=1000) {
										accLosses +=316;
									}else if (legSize>=800) {
										accLosses +=148;
									}else if (legSize>=600) {
										accLosses +=64;
									}else if (legSize>=400) {
										accLosses +=22;
									}
								}
								//System.out.println(size+" "+diff);
							}else if (actualLeg==-1) {
								int size = data.get(index1).getHigh5()-q1.getClose5();
								int diff = size-(lastLevel);
								accOthers +=diff;
								others++;
								accSize+=size ;
								//System.out.println(size+" "+diff);
								
								if (legSize>=1600){
									accLosses +=2668;
									System.out.println("1600");
								}
								else if (legSize>=1400){
									accLosses +=1324;
									System.out.println("1400");
								}
								else if (legSize>=1200){
									accLosses +=652;
								}else {
									if (legSize>=1000) {
										accLosses +=316;
									}else if (legSize>=800) {
										accLosses +=148;
									}else if (legSize>=600) {
										accLosses +=64;
									}else if (legSize>=400) {
										accLosses +=22;
									}
								}
							}
						}
						trades++;
					}
			
				}//lastDay
				totalDays++;
				win4=0;
				loss4=-1;
				indexDay = i;
				lastDay = day;
				index1=i;
				index2=i;
				lastLevel=0;
				actualLeg=0;
				//trading
				trade	= 0;
				tradePrice = 0;
				wins5=0;
				losses5=0;
				canContinue = true;
			}//day
			
			if (!canContinue) continue;
			QuoteShort qindex1 = data.get(index1);
			QuoteShort qindex2 = data.get(index2);
			if (actualLeg==0) {
				if (q.getHigh5()-qindex1.getLow5()>=200){
					actualLeg=1;
					index2=i;
					legSize = q.getHigh5()-qindex1.getLow5();
					win4=0;
					cases++;
					lastLevel=200;
					trade =-1;
					tradePrice = qindex1.getLow5()+200;
					microLots=1;
				}else if (qindex1.getHigh5()-q.getLow5()>=200) {
					actualLeg=-1;
					index2=i;
					legSize = qindex1.getHigh5()-q.getLow5();
					win4=0;
					cases++;
					lastLevel=200;
					trade = 1;
					tradePrice = qindex1.getHigh5()-200;
					microLots=1;
				}
			}else if (actualLeg==1) {
				if (q.getHigh5()>=qindex2.getHigh5()) {
					index2=i;
					legSize = q.getHigh5()-qindex1.getLow5();
					
					if (legSize>=200) lastLevel = 200;
					if (legSize>=400) lastLevel = 400;
					if (legSize>=600) lastLevel = 600;
					if (legSize>=800) lastLevel = 800;
					if (legSize>=1000) lastLevel = 1000;
					if (legSize>=1200) lastLevel = 1200;
					if (legSize>=1400) lastLevel = 1400;
					if (legSize>=1600) lastLevel = 1600;
					if (legSize>=1800) lastLevel = 1800;
					if (legSize>=2000) lastLevel = 2000;
					
					
					if (win4==0) {
						if (legSize>=thr) {
							win4=-1;
							canContinue=false;
							if (thr>=1600){
								accLosses +=2668;
							}
							else if (thr>=1400){
								accLosses +=1324;
							}
							else if (thr>=1200){
								accLosses +=652;
							}else {
								if (thr>=1000) {
									accLosses +=316;
								}else if (thr>=800) {
									accLosses +=148;
								}else if (thr>=600) {
									accLosses +=64;
								}else if (thr>=400) {
									accLosses +=22;
								}
							}
						}
					}
				}else{
					if (qindex2.getHigh5()-q.getLow5()>=200) {
						//tiene que ca
						if (win4==0) {
							if (lastLevel<thr) {
								win4=1;
								canContinue=false;
								int rev = qindex2.getHigh5()-qindex1.getLow5();
								int refdiff = rev-lastLevel;
								accref+=refdiff;
								
								accWins += 20-refdiff*0.1;
							}
						}
					}
				}
			}else if (actualLeg==-1) {
				if (q.getLow5()<=qindex2.getLow5()) {
					index2=i;
					legSize = qindex1.getHigh5()-q.getLow5();
					if (legSize>=200) lastLevel = 200;
					if (legSize>=400) lastLevel = 400;
					if (legSize>=600) lastLevel = 600;
					if (legSize>=800) lastLevel = 800;
					if (legSize>=1000) lastLevel = 1000;
					if (legSize>=1200) lastLevel = 1200;
					if (legSize>=1400) lastLevel = 1400;
					if (legSize>=1600) lastLevel = 1600;
					if (legSize>=1800) lastLevel = 1800;
					if (legSize>=2000) lastLevel = 2000;
					
					if (win4==0) {
						if (legSize>=thr) {
							win4=-1;
							canContinue=false;
							
							if (thr>=1600){
								accLosses +=2668;
							}
							else if (thr>=1400){
								accLosses +=1324;
							}
							else if (thr>=1200){
								accLosses +=652;
							}else {
								if (thr>=1000) {
									accLosses +=316;
								}else if (thr>=800) {
									accLosses +=148;
								}else if (thr>=600) {
									accLosses +=64;
								}else if (thr>=400) {
									accLosses +=22;
								}
							}
						}
					}
				}else{
					if (q.getHigh5()-qindex2.getLow5()>=200) {
						//tiene que ca
						if (win4==0) {
							if (lastLevel<thr) {
								win4=1;
								canContinue=false;
								int rev = qindex1.getHigh5()-qindex2.getLow5();
								int refdiff = rev-lastLevel;
								accref+=refdiff;
								
								accWins += 20-refdiff*0.1;
							}
						}
					}
				}
			}
		}//for
		
		double pf=9999.0;
		if (accLosses>0){
			pf = accWins*1.0/accLosses;
		}
		double avg = (accWins-accLosses)*1.0/(wins+losses+others);
		System.out.println(
				y1+" "+y2
				+" "+thr
				+" "+minPips
				+" || "
				+" "+totalDays
				+" "+trades
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" || "
				+" "+PrintUtils.Print2dec(accref*1.0/wins, false)
				+" || "+others
				+" "+PrintUtils.Print2dec(accOthers*0.1/others, false)
				+" "+PrintUtils.Print2dec(accSize*0.1/others, false)
				+" || "
				+" "+accWins
				+" "+accLosses
				+" || "
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	public static void doTestTrading(ArrayList<QuoteShort> data,
			int y1,int y2,
			int thr,int minPips,int debug) {
		

		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
		int indexDay = 0;
		int win=0;
		int total2 = 0;
		int count2=0;
		int total3=0;
		int count3=0;
		int acc=0;
		int totalDays=0;
		int actualLeg = 0;
		int index1 = 0;
		int index2=0;
		int legSize = 0;
		int tradeMode=0;
		int count4=0;
		int win4=0;
		int total4=0;
		int loss4=0;
		int cases = 0;
		int casesWins=0;
		int casesLosses=0;
		int accReverse = 0;
		int reverseCases=0;
		int accCloses=0;
		int countCloses=0;
		int lastLevel=0;
		
		int microLots=0;
		int trade = 0;
		int tradePrice= 0;
		
		int lossesAcc = 0;
		int winsAcc=0;
		int losses5=0;
		int wins5=0;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay
					&& min==15
					) {
				if (lastDay!=-1) {
					TradingUtils.getMaxMinMoves(data, qm, i-240, i-1);
					int longmove = qm.getHigh5();
					int shortmove = qm.getLow5();
					
					if (longmove>=thr) {
						total3++;
						win=1;
						acc+=longmove;
						if (shortmove<minPips) {
							count3++;
							win=-1;
							if (debug==2)
								System.out.println(DateUtils.datePrint(cal)
										+" || "+longmove+" "+shortmove
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
						}
					}else if (shortmove>=thr) {
						total3++;
						win=1;
						acc+=shortmove;
						if (longmove<minPips) {
							count3++;
							win=-1;
							if (debug==2)
								System.out.println(DateUtils.datePrint(cal)
										+" || "+longmove+" "+shortmove
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
						}
					} 
					
					if (win4>=0) {
						if (win4==1) {
							casesWins++;
						}else if (loss4==1) {
							casesLosses++;
						}else {
							if (actualLeg==1) {
								legSize = q1.getClose5()-data.get(index1).getLow5();
								accCloses +=legSize-lastLevel;
								countCloses++;
								if (debug==5)
									System.out.println(DateUtils.datePrint(cal)
											+" || "+legSize+" "+lastLevel
											//+" || "+PrintUtils.Print2dec(factor, false)
											);
							}else if (actualLeg==-1) {
								legSize = data.get(index1).getHigh5()-q1.getClose5();
								accCloses +=legSize-lastLevel;
								countCloses++;
								if (debug==5)
									System.out.println(DateUtils.datePrint(cal)
											+" || "+legSize+" "+lastLevel
											//+" || "+PrintUtils.Print2dec(factor, false)
											);
							}
						}
					}else {
						if (actualLeg==1) {
							legSize = q1.getClose5()-data.get(index1).getLow5();
							accCloses +=legSize-lastLevel;
							countCloses++;
							if (debug==5)
								System.out.println(DateUtils.datePrint(cal)
										+" || "+legSize+" "+lastLevel
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
						}else if (actualLeg==-1) {
							legSize = data.get(index1).getHigh5()-q1.getClose5();
							accCloses +=legSize-lastLevel;
							countCloses++;
							if (debug==5)
								System.out.println(DateUtils.datePrint(cal)
										+" || "+legSize+" "+lastLevel
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
						}
					}
				}//lastDay
				totalDays++;
				win4=-1;
				loss4=-1;
				indexDay = i;
				lastDay = day;
				index1=i;
				index2=i;
				lastLevel=0;
				actualLeg=0;
				//trading
				trade	= 0;
				tradePrice = 0;
				wins5=0;
				losses5=0;
			}//day
			
			QuoteShort qindex1 = data.get(index1);
			QuoteShort qindex2 = data.get(index2);
			if (actualLeg==0) {
				if (q.getHigh5()-qindex1.getLow5()>=200){
					actualLeg=1;
					
					index2=i;
					legSize = q.getHigh5()-qindex1.getLow5();
					win4=0;
					cases++;
					lastLevel=200;
					
					trade =-1;
					tradePrice = qindex1.getLow5()+200;
					microLots=1;
					
					if (debug==4) {
						System.out.println(DateUtils.datePrint(cal)
								+" [win0] || "+actualLeg+" "+legSize
								//+" || "+PrintUtils.Print2dec(factor, false)
								);
					}
					if (debug==6) 
					System.out.println(DateUtils.datePrint(cal)
							+" [LONG] || "+actualLeg+" "+legSize
							+" || "+tradePrice+" "+qindex1.getLow5()
							);
				}else if (qindex1.getHigh5()-q.getLow5()>=200) {
					actualLeg=-1;
					index2=i;
					legSize = qindex1.getHigh5()-q.getLow5();
					win4=0;
					cases++;
					lastLevel=200;
					
					trade =1;
					tradePrice = qindex1.getHigh5()-200;
					microLots=1;
					
					if (debug==4) {
						System.out.println(DateUtils.datePrint(cal)
								+" [win0] || "+actualLeg+" "+legSize
								//+" || "+PrintUtils.Print2dec(factor, false)
								);
					}
				}
			}else if (actualLeg==1) {
				if (q.getHigh5()>=qindex2.getHigh5()) {
					index2=i;
					legSize = q.getHigh5()-qindex1.getLow5();
					if (debug==6) 
					System.out.println(DateUtils.datePrint(cal)
							+" [amplia leg 1] || "+actualLeg+" "+legSize
							+" "+q.getHigh5()+" "+qindex1.getLow5()
							//+" || "+PrintUtils.Print2dec(factor, false)
							);
					
					if (legSize>=400) {
						lastLevel = 400;
						if (microLots==1) {
							microLots=2;
							tradePrice = qindex1.getLow5()+400;
						}
					}
					if (legSize>=600) {
						lastLevel = 600;
						//se asumen perdidas
						if (losses5==0) {
							lossesAcc += 1*400+1*200+2*20;//perdidas 1 + perdidas 2 comm
							losses5=1;
						}
					}
					if (legSize>=800) lastLevel = 800;
					if (legSize>=1000) lastLevel = 1000;
					if (legSize>=1200) lastLevel = 1200;
					if (legSize>=1400) lastLevel = 1400;
					if (legSize>=1600) lastLevel = 1600;
					if (legSize>=1800) lastLevel = 1800;
					if (legSize>=2000) lastLevel = 2000;
					
					if (debug==4) {
						System.out.println(DateUtils.datePrint(cal)
								+" [amplia leg 1] || "+actualLeg+" "+legSize
								//+" || "+PrintUtils.Print2dec(factor, false)
								);
					}
					
					if (win4==0 && loss4!=1) {
						if (legSize>=thr) {
							loss4=1;
							if (debug==3) {
								System.out.println(DateUtils.datePrint(cal)
										+" [LOSS] || "+actualLeg+" "+legSize
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
							}
						}
					}
				}else{
					if (qindex2.getHigh5()-q.getLow5()>=200) {
						//tiene que ca
						if (losses5==0 && wins5==0) {
							if (lastLevel<600) {
								int pricereverse = qindex2.getHigh5()-200;
								int pipsWin = tradePrice-pricereverse;
								
								winsAcc += pipsWin;
								wins5=1;
								if (debug==6) 
								System.out.println("reverse LONG: "
										+" || "+lastLevel
										+" || "+tradePrice+" "+pricereverse
										+" || "+pipsWin
										);
							}
						}
						
						//camnio de direccion
						if (win4==0 && loss4!=1) {
							if (legSize>=thr) {
								loss4=1;
								if (debug==3) {
									System.out.println(DateUtils.datePrint(cal)
											+" [LOSS 2] || "+actualLeg+" "+legSize
											//+" || "+PrintUtils.Print2dec(factor, false)
											);
								}
							}else {
								win4=1;
								accReverse +=legSize;
								reverseCases++;
								if (debug==4) {
									System.out.println(DateUtils.datePrint(cal)
											+" [win] || "+actualLeg+" "+legSize
											//+" || "+PrintUtils.Print2dec(factor, false)
											);
								}
							}
						}
					}
				}
			}else if (actualLeg==-1) {
				if (q.getLow5()<=qindex2.getLow5()) {
					index2=i;
					legSize = qindex1.getHigh5()-q.getLow5();
					if (legSize>=400) {
						lastLevel = 400;
						if (microLots==1) {
							microLots=2;
							tradePrice = qindex1.getHigh5()-400;
						}
					}
					if (legSize>=600) {
						lastLevel = 600;
						//se asumen perdidas
						if (losses5==0) {
							lossesAcc += 1*400+1*200+2*20;//perdidas 1 + perdidas 2 comm
							losses5=1;
						}
					}
					if (legSize>=800) lastLevel = 800;
					if (legSize>=1000) lastLevel = 1000;
					if (legSize>=1200) lastLevel = 1200;
					if (legSize>=1400) lastLevel = 1400;
					if (legSize>=1600) lastLevel = 1600;
					if (legSize>=1800) lastLevel = 1800;
					if (legSize>=2000) lastLevel = 2000;
					
					if (debug==4) {
						System.out.println(DateUtils.datePrint(cal)
								+" [amplia leg -1] || "+actualLeg+" "+legSize
								//+" || "+PrintUtils.Print2dec(factor, false)
								);
					}
					
					if (win4==0 && loss4!=1) {
						if (legSize>=thr) {
							loss4=1;
							if (debug==3) {
								System.out.println(DateUtils.datePrint(cal)
										+" [LOSS] || "+actualLeg+" "+legSize
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
							}
						}
					}
				}else{
					if (q.getHigh5()-qindex2.getLow5()>=200) {
						
						if (losses5==0 && wins5==0) {
							if (lastLevel<600) {
								int pricereverse = qindex2.getLow5()+200;
								int pipsWin = pricereverse-tradePrice;
								
								winsAcc += pipsWin;
								wins5=1;
							}
						}
						//camnio de direccion
						if (win4==0 && loss4!=1) {
							if (legSize>=thr) {
								loss4=1;
								if (debug==3) {
									System.out.println(DateUtils.datePrint(cal)
											+" [LOSS 2] || "+actualLeg+" "+legSize
											//+" || "+PrintUtils.Print2dec(factor, false)
											);
								}
							}else {
								win4=1;
								accReverse +=legSize;
								reverseCases++;
								if (debug==4) {
									System.out.println(DateUtils.datePrint(cal)
											+" [win] || "+actualLeg+" "+legSize
											//+" || "+PrintUtils.Print2dec(factor, false)
											);
								}
							}
						}
					}
				}
			}
		}//for
		
		System.out.println(
				y1+" "+y2
				+" "+thr
				+" "+minPips
				+" || "
				+" "+totalDays
				//+" "+total2+" "+count2
				//+" "+PrintUtils.Print2dec(count2*100.0/total2, false)
				//+" || "
				//+" "+total3+" "+count3
				//+" "+PrintUtils.Print2dec(count3*100.0/total3, false)
				//+" || "+PrintUtils.Print2dec(acc*1.0/total3, false)
				//+" || "
				+" "+cases
				+" "+(casesWins+casesLosses)
				+" "+casesWins
				+" "+casesLosses
				+" "+PrintUtils.Print2dec(casesWins*100.0/cases, false)
				+" || "+PrintUtils.Print2dec(accReverse*1.0/reverseCases, false)
				+" || "+countCloses+" "+PrintUtils.Print2dec(accCloses*1.0/countCloses, false)
				);
		
	}
	
	public static void doTest(ArrayList<QuoteShort> data,
			int y1,int y2,
			int thr,int minPips,int debug) {
		

		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
		int indexDay = 0;
		int win=0;
		int total2 = 0;
		int count2=0;
		int total3=0;
		int count3=0;
		int acc=0;
		int totalDays=0;
		for (int i=0;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				if (lastDay!=-1) {
					TradingUtils.getMaxMinMoves(data, qm, i-240, i-1);
					int longmove = qm.getHigh5();
					int shortmove = qm.getLow5();
					
					if (longmove<thr) {
						total3++;
						win=1;
						acc+=longmove;
						if (shortmove<minPips) {
							count3++;
							win=-1;
							if (debug==3)
								System.out.println(DateUtils.datePrint(cal)
										+" || "+longmove+" "+shortmove
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
						}
					}else if (shortmove<thr) {
						total3++;
						win=1;
						acc+=shortmove;
						if (longmove<minPips) {
							count3++;
							win=-1;
							if (debug==2)
								System.out.println(DateUtils.datePrint(cal)
										+" || "+longmove+" "+shortmove
										//+" || "+PrintUtils.Print2dec(factor, false)
										);
						}
					} 
				}//lastDay
				totalDays++;
				win=0;
				indexDay = i;
				lastDay = day;
			}//day
			
			if (win==0) {
				//para cada uno hacemos el test al close
				TradingUtils.getMaxMinMoves(data, qm, indexDay, i);
				int longmove = qm.getHigh5();
				int shortmove = qm.getLow5();
				
				if (longmove>=thr) {
					total2++;
					win=1;
					if (shortmove<minPips) {
						count2++;
						win=-1;
						if (debug==2)
							System.out.println(DateUtils.datePrint(cal)
									+" || "+longmove+" "+shortmove
									//+" || "+PrintUtils.Print2dec(factor, false)
									);
					}
				}else if (shortmove>=thr) {
					total2++;
					win=1;
					if (longmove<minPips) {
						count2++;
						win=-1;
						if (debug==2)
							System.out.println(DateUtils.datePrint(cal)
									+" || "+longmove+" "+shortmove
									//+" || "+PrintUtils.Print2dec(factor, false)
									);
					}
				} 
			}
			
		}//for
		
		System.out.println(
				y1+" "+y2
				+" "+thr
				+" "+minPips
				+" || "
				+" "+totalDays
				+" "+total2+" "+count2
				+" "+PrintUtils.Print2dec(count2*100.0/total2, false)
				+" || "
				+" "+total3+" "+count3
				+" "+PrintUtils.Print2dec(count3*100.0/total3, false)
				+" || "+PrintUtils.Print2dec(acc*1.0/total3, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_UTC_15 Mins_Bid_2003.12.31_2018.02.21.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_30 Mins_Bid_2003.12.31_2018.02.22.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_15 Secs_Bid_2010.12.31_2018.01.26.csv";
		
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
		//FFNewsClass.readNews(pathNews,news,0);
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
			
			System.out.println(data.size()+" "+maxMins.size());
			
			int y1=2012;
			int y2=2017;
	
			
			ArrayList<MaxMinConfig> configsEUR = new ArrayList<MaxMinConfig>();
			ArrayList<MaxMinConfig> configsAUD = new ArrayList<MaxMinConfig>();
			ArrayList<MaxMinConfig> configsGBP = new ArrayList<MaxMinConfig>();
			for (int j=0;j<=23;j++){
				configsEUR.add(new MaxMinConfig());
				configsAUD.add(new MaxMinConfig());
				configsGBP.add(new MaxMinConfig());
			}
			
			//15 MINS
			//EUR CONFIG
			configsEUR.get(0).setconfig2(0, 20, 30, -1, -1, 160, 2400, true);
			configsEUR.get(1).setconfig2(1, 10, 45, -1, -1, 200, 3000, true);
			configsEUR.get(2).setconfig2(2, 20, 70, -1, -1, 200, 800, true);
			configsEUR.get(3).setconfig2(3, 9, 235, -1, -1, 220, 2640, true);
			configsEUR.get(5).setconfig2(5, 10, 70, -1, -1, 150, 1800, true);
			configsEUR.get(6).setconfig2(6, 11, 240, -1, -1, 270, 2160, true);
			configsEUR.get(7).setconfig2(7, 10, 65, -1, -1, 170,1190, true);
			configsEUR.get(8).setconfig2(8, 9, 355, -1, -1, 100, 600, true);
			configsEUR.get(9).setconfig2(9, 5, 935, -1, -1, 230, 3220, true);
			configsEUR.get(23).setconfig2(23, 22, 280, -1, -1, 100, 500, true);
			//AUD CONFIG
			configsAUD.get(0).setconfig2(0, 16, 835, -1, -1, 100, 1000, true);
			configsAUD.get(1).setconfig2(1, 7, 1145, -1, -1, 110, 330, true);
			configsAUD.get(7).setconfig2(7, 6, 1825, -1, -1, 190, 1520, true);
			configsAUD.get(14).setconfig2(14, 6, 1340, -1, -1, 80, 1360, true);
			configsAUD.get(16).setconfig2(16, 13, 3100, -1, -1, 150, 1650, true);
			configsAUD.get(17).setconfig2(17, 10, 780, -1, -1, 100, 1600, true);
			configsAUD.get(18).setconfig2(18, 5, 645, -1, -1, 100,500, true);
			configsAUD.get(19).setconfig2(19, 17, 245, -1, -1, 110, 1100, true);
			configsAUD.get(20).setconfig2(20, 3, 640, -1, -1, 200, 2400, true);
			configsAUD.get(22).setconfig2(22, 2, 195, -1, -1, 140, 840, true);
			configsAUD.get(23).setconfig2(23, 20, 800, -1, -1, 100, 800, true);
			//GBP CONFIG
			configsGBP.get(0).setconfig2(0, 20, 20, -1, -1, 120, 2280, true);
			configsGBP.get(1).setconfig2(1, 10, 35, -1, -1, 200, 1600, true);
			configsGBP.get(2).setconfig2(2, 23, 180, -1, -1, 260, 260, true);
			configsGBP.get(3).setconfig2(3, 9, 145, -1, -1, 300,300, true);
			configsGBP.get(5).setconfig2(5, 10, 35, -1, -1, 140, 2380, true);
			configsGBP.get(6).setconfig2(6, 11, 150, -1, -1, 220,1980, true);
			configsGBP.get(7).setconfig2(7, 10, 80, -1, -1, 180,1440, true);
			configsGBP.get(8).setconfig2(8, 0, 665, -1, -1,  90,1350, true);
			configsGBP.get(9).setconfig2(9, 5, 2710, -1, -1, 220,540, true);
			configsGBP.get(23).setconfig2(23, 22, 170, -1, -1, 110,880, true);
			
			
			//30MIN
			//EUR CONFIG
			/*configsEUR.get(0).setconfig2(0, 20, 30, -1, -1, 170, 1190, true);
			configsEUR.get(1).setconfig2(1, 13, 30, -1, -1, 120, 2160, true);
			configsEUR.get(2).setconfig2(2, 20, 70, -1, -1, 160, 640, true);
			configsEUR.get(3).setconfig2(3, 9, 195, -1, -1, 220, 2640, true);
			configsEUR.get(5).setconfig2(5, 10, 80, -1, -1, 150, 2250, true);
			configsEUR.get(6).setconfig2(6, 11, 130, -1, -1, 300, 3600, true);
			configsEUR.get(7).setconfig2(7, 11, 85, -1, -1, 160,2240, true);
			configsEUR.get(8).setconfig2(8, 9, 340, -1, -1, 100, 600, true);
			configsEUR.get(9).setconfig2(9, 2, 725, -1, -1,  230,2990, true);
			configsEUR.get(23).setconfig2(23, 9, 85, -1, -1, 180, 540, true);
			//AUD CONFIG
			configsAUD.get(0).setconfig2(0, 16, 835, -1, -1, 100, 1000, true);
			configsAUD.get(1).setconfig2(1, 7, 1145, -1, -1, 110, 330, true);
			configsAUD.get(7).setconfig2(7, 6, 1825, -1, -1, 190, 1520, true);
			configsAUD.get(14).setconfig2(14, 6, 1340, -1, -1, 80, 1360, true);
			configsAUD.get(16).setconfig2(16, 13, 3100, -1, -1, 150, 1650, true);
			configsAUD.get(17).setconfig2(17, 10, 780, -1, -1, 100, 1600, true);
			configsAUD.get(18).setconfig2(18, 5, 645, -1, -1, 100,500, true);
			configsAUD.get(19).setconfig2(19, 17, 245, -1, -1, 110, 1100, true);
			configsAUD.get(20).setconfig2(20, 3, 640, -1, -1, 200, 2400, true);
			configsAUD.get(22).setconfig2(22, 2, 195, -1, -1, 140, 840, true);
			configsAUD.get(23).setconfig2(23, 20, 800, -1, -1, 100, 800, true);
			*/
			for (y1=2004;y1<=2004;y1++) {
				y2 = y1+14;
				//'A) Activo No Corriente'
				int hTest = 0;
				MaxMinConfig c = configsEUR.get(hTest);
				c.setActive(true);
				for (int thr=0;thr<=0;thr+=5){		
					//c.setThr1(thr);
					for (int tp=50;tp<=50;tp+=10){
						for (int sl=1*tp;sl<=1*tp;sl+=1*tp){
							//c.setTp(tp);
							//c.setSl(sl);
							for (int hclose = 0;hclose<=0;hclose++){
								//c.sethClose(hclose);
								String header = hTest+" | "+thr+" "+tp
										+" "+sl+""+" | "+hclose
										;
								 TestMoves.doTestTrading5(
										 	header,data,maxMins,
											y1, y2,
											configsEUR,
											true,
											true,
											0,
											0);
							}
						}
					}
					 
				}	
			}
			/*for (y1=2009;y1<=2009;y1++) {
				y2 = y1+9;
				for (int aBars=1;aBars<=1;aBars+=1000){
					//System.out.println("testing.."+aBars);
					for (double tpt=50;tpt<=500;tpt+=50) {
						for (double slt=3*tpt;slt<=20*tpt;slt+=1*tpt) {
							//System.out.println("testing.."+PrintUtils.Print2dec(tpt,false)
							//+" "+PrintUtils.Print2dec(slt,false));
							
							int countPrintedPointsTPSL = 0;
							for (int h1=17;h1<=17;h1++){
								int h2 = h1+6;
								int countPrintedPoints = 0; //5:1,6:2,7:6,8:8,9:10
								for (int bars=1000;bars<=1000;bars+=10) {
									for (double tp=tpt;tp<=tpt;tp+=0.1) {
										for (double sl=slt;sl<=slt;sl+=0.1) {
											for (int maxBars=999999;maxBars<=999999;maxBars++){
												for (double sizeCandleFactor=1.0;sizeCandleFactor<=1.0;sizeCandleFactor+=0.1){
													for (int maxDiff=0;maxDiff<=0;maxDiff+=50){
														for (int h3=0;h3<=23;h3++){
															int county = TestMoves.doTestTrading3(data,maxMins,
																	y1, y2,h1,h2,h3, 
																	bars, tp,sl,
																	sizeCandleFactor,
																	maxBars,maxDiff,
																	true,
																	false,
																	false,
																	0);
															if (county>=5){
																if (county==5){
																	countPrintedPoints += 1;
																}else if (county==6){
																	countPrintedPoints += 2;
																}else if (county==7){
																	countPrintedPoints += 6;
																}else if (county==8){
																	countPrintedPoints += 8;
																}else if (county==9){
																	countPrintedPoints += 10;
																}
															}//pf
														}//h3
													}//maxDiff
												}//ZCF
											}//MB
										}//SL
									}//tp
									//System.out.println("tested bars.."+bars);
								}//bars
								//System.out.println("[TESTED H1 ] "+h1+" total>=: "+countPrintedPoints);
								countPrintedPointsTPSL+=countPrintedPoints;
								//System.out.println("tested h1..");
							}//h1
							//System.out.println("[TESTED TP SL] "+tpt+" "+slt+" total>=: "+countPrintedPointsTPSL);
							//System.out.println("tested..");
						}//slt
					}//tpt
				}
			}*/
		
			
			
		}
	}

}
