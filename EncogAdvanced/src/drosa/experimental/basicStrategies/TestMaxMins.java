package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.CoreStrategies.TestPriceBuffer;
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

public class TestMaxMins {
	
	
	public static void testThrs4(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			ArrayList<Integer> mas,
			ArrayList<FFNewsClass> news,
			ArrayList<MaxMinConfig> configs,
			int y1,int y2,
			double risk,
			int diff1,int diff2,
			int aImpact,
			int hourMode,
			int debug
			){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int comm = 20;
		double balanceInicial = 10000;
		double balance = balanceInicial;
		double balanceNeed = 0;
		double balanceFloating = 0;
		double maxBalance = balance;
		double maxDD = 0;
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		double avg200 = 0;
		
		int hFirst = 0;
		int hSecond = 0;
		int minH = 0;
		int maxH = 0;
		int totalDays = 0;
		int count = 0;
		int count2=0;
		int count2win=0;
		boolean isHighMake = false;
		boolean isLowMake = false;
		int maxThr = -999999999;
		int minThr = -999999999;
		int maxThrValue = 0;
		int minThrValue = 0;
		ArrayList<ArrayList<Integer>> daymakes = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> makes = null;
		int mode = 0;
		int entry = 0;
		int entrytp = 0;
		int entrysl = 0;
		int winPips = 0;
		int lostPips = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		double win$$ = 0;
		double loss$$ = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			int maxMin = maxMins.get(i);
			int ma = mas.get(i);
			if (day!=lastDay){
										
				if (lastDay!=-1){
					totalDays++;
					
					int range = max-min;
					ranges.add(range);
					int begin = data.size()-1-200;
					if (begin<=0) begin = 0;
					avg = MathUtils.average(ranges, begin, data.size()-1);
										
					hFirst = minH;
					hSecond = maxH;
					
					if (maxH<minH){
						hFirst = maxH;
						hSecond = minH;						
					}else{						
					}
										
					if (debug==1){
						System.out.println("[DAY ] "+DateUtils.datePrint(cal1)+" || "+hFirst+" "+hSecond								
								+" || "+maxThr+" "+minThr
								);
					}
					
					daymakes.add(makes);
				}
				
				makes = new ArrayList<Integer>();
				minH = 0;
				maxH = 0;
				maxThr = -999999999;
				minThr = -999999999;
				hFirst = 0;
				hSecond = 0;
				isHighMake = false;
				isLowMake = false;
				lastDay = day;
			}
			
			boolean hAllowed = configs.get(h).isActive();
			
			if (hAllowed){
				//int impact = FFNewsClass.getDayImpact(news, cal, 0);
				 maxMin = maxMins.get(i-1);
				 double riskFactor = 1.0;
				 int index = TestPriceBuffer.getMinMaxBuff(maxMins,i-40,i-1,configs.get(h).getThr1());
				 if (index>=i-diff1 && index<=i-diff2
						//&& impact==aImpact
						 ){
					 maxMin = maxMins.get(index);
					 
					 if (index==2) riskFactor = 0.73;
					 if (index==3) riskFactor = 0.73;
					 if (index==4) riskFactor = 0.6;
					 if (index>=5) riskFactor = 0.5;
					
					 
					 if (true
							 && maxMin>= configs.get(h).getThr1()
							 //&&maxThr>=thr1
							 ){
						 
						 if (minThr>=-configs.get(h).getThr2()){						 
							 entry = q.getOpen5();
							 int entrySL = q.getOpen5()+configs.get(h).getSl();
							 
							 PositionShort pos = new PositionShort();
							 pos.setEntry(q.getOpen5());
							 pos.setTp(q.getOpen5()-configs.get(h).getTp());
							 pos.setSl(entrySL);
							 pos.setPositionStatus(PositionStatus.OPEN);
							 pos.setPositionType(PositionType.SHORT);
							 pos.setOpenIndex(i);
							 pos.setExpiredTime(i+configs.get(h).getMaxbars());
							 						 
							 int pipsSL = Math.abs(entry-entrySL);
							 double risk$$ = (balance+balanceFloating)*((riskFactor*risk)/100);
							 double pips$$ = risk$$/(pipsSL*0.1);
							 int microLots = (int) (pips$$*10);
							 pos.setMicroLots(microLots);
								
							 positions.add(pos);						
						 }
						 
					 }else if (
							 true
							 && maxMin<=-configs.get(h).getThr1()
							 //&& minThr<=-thr1
							 ){
						 if (maxThr<=configs.get(h).getThr2()){
							 entry = q.getOpen5();
							 int entrySL = q.getOpen5()-configs.get(h).getSl();
							 
							 PositionShort pos = new PositionShort();
							 pos.setEntry(q.getOpen5());
							 pos.setTp(q.getOpen5()+configs.get(h).getTp());
							 pos.setSl(entrySL);
							 pos.setPositionStatus(PositionStatus.OPEN);
							 pos.setPositionType(PositionType.LONG);
							 pos.setOpenIndex(i);
							 pos.setExpiredTime(i+configs.get(h).getMaxbars());
							 
							 int pipsSL = Math.abs(entry-entrySL);
							 double risk$$ = (balance+balanceFloating)*((riskFactor*risk)/100);
							 double pips$$ = risk$$/(pipsSL*0.1);
							 int microLots = (int) (pips$$*10);
							 pos.setMicroLots(microLots);
							 
							 positions.add(pos);						
						 }
					 }
				 }//index
			}
			
			//evaluamos SL
			int j=0;
			balanceFloating = 0;
			
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
						}else if (true
								//&& maxMins.get(i)>=thr3 || floatingPips<=-diff
								&& (
									(i>=p.getExpiredTime())
									//|| (h==13 && floatingPips<=-50)
									)
								){
							
							pips = q.getClose5()-p.getEntry();
							if (pips<0)
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
						}else if (true
								 //&& maxMins.get(i)<=-thr3
								//|| floatingPips<=-diff
								&&
									( (i>=p.getOpenIndex()+p.getExpiredTime())
											//|| (h==13 && floatingPips<=-50)
									)
								){
							
							pips = p.getEntry()-q.getClose5();
							if (pips<0)
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
						loss$$ +=-profit;
					}
					
					balance += pips*0.1*p.getMicroLots()*0.1;
					
										
					if (pips>=0){
						winPips += pips;
						wins++;
						actualLosses = 0;
						
					}else{
						lostPips += -pips;
						losses++;
						actualLosses++;
						if (actualLosses>=10){
							//System.out.println("[LOSSES] "
							//		+DateUtils.datePrint(cal)+" "+actualLosses
							//);
						}
						if (actualLosses>=maxLosses){
							maxLosses = actualLosses;
							
						}
						
						
					}
					
					positions.remove(j);
				}else{
					//añadimos a floating										
					balanceFloating += floatingPips*0.1*p.getMicroLots()*0.1;
										
					j++;
				}
				
				double totalBalance = balance+balanceFloating;
				if (totalBalance<=maxBalance) {
					double dd = 100.0-(totalBalance*100.0)/maxBalance;
					if (dd>=maxDD) maxDD = dd;
				}else {
					maxBalance = totalBalance;
				}
				
				if (totalBalance<balanceInicial*0.5){
					double need = totalBalance-balance;
					balance += need;
					balanceNeed +=need;
				}
			}								
		
			
			
			
			if (maxThr ==-999999999 || maxMins.get(i)>=maxThr){
				maxThr = maxMins.get(i);
			}
			
			if (minThr ==-999999999 || maxMins.get(i)<=minThr){
				minThr = maxMins.get(i);
			}
		
		}
	

		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = winPips*1.0/lostPips;
		avg = (winPips-lostPips)*0.1/total;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		
		//double pf$$ = wins$$/losses$$;
		int totalAños = y2-y1+1;
		
		
		double tae = 100.0*(Math.pow(balance/(balanceInicial+balanceNeed), 1.0/totalAños)-1);
		double taeFactor = tae/maxDD;
		
		
		//if (taeFactor>=3.50)
		System.out.println(
				header
				+" || "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(risk, false)
				//+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgWin, false)
				+" || "+PrintUtils.Print2dec(avgLoss, false)
				+" || "+maxLosses
				+" || "+PrintUtils.Print2dec(balanceInicial+balanceNeed, false)
				+" || "+PrintUtils.Print2dec2(balance, true)
				+" || "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(taeFactor, false)
				);
							
		
	}
	
	public static void testThrs3(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			ArrayList<Integer> mas,
			ArrayList<FFNewsClass> news,
			ArrayList<Integer> hs,
			int y1,int y2,
			int h1,int h2,
			int h3,int h4,
			int tp,
			int sl,
			int thr1,
			int thr2,
			double risk,
			int hourMode,
			int debug
			){
		
		int comm = 20;
		double balanceInicial = 10000;
		double balance = balanceInicial;
		double balanceNeed = 0;
		double maxBalance = balance;
		double maxDD = 0;
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		double avg200 = 0;
		
		int hFirst = 0;
		int hSecond = 0;
		int minH = 0;
		int maxH = 0;
		int totalDays = 0;
		int count = 0;
		int count2=0;
		int count2win=0;
		boolean isHighMake = false;
		boolean isLowMake = false;
		int maxThr = -999999999;
		int minThr = -999999999;
		int maxThrValue = 0;
		int minThrValue = 0;
		ArrayList<ArrayList<Integer>> daymakes = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> makes = null;
		int mode = 0;
		int entry = 0;
		int entrytp = 0;
		int entrysl = 0;
		int winPips = 0;
		int lostPips = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			int maxMin = maxMins.get(i);
			int ma = mas.get(i);
			if (day!=lastDay){
										
				if (lastDay!=-1){
					totalDays++;
					
					int range = max-min;
					ranges.add(range);
					int begin = data.size()-1-200;
					if (begin<=0) begin = 0;
					avg = MathUtils.average(ranges, begin, data.size()-1);
										
					hFirst = minH;
					hSecond = maxH;
					
					if (maxH<minH){
						hFirst = maxH;
						hSecond = minH;						
					}else{						
					}
										
					if (debug==1){
						System.out.println("[DAY ] "+DateUtils.datePrint(cal1)+" || "+hFirst+" "+hSecond								
								+" || "+maxThr+" "+minThr
								);
					}
					
					daymakes.add(makes);
				}
				
				makes = new ArrayList<Integer>();
				minH = 0;
				maxH = 0;
				maxThr = -999999999;
				minThr = -999999999;
				hFirst = 0;
				hSecond = 0;
				isHighMake = false;
				isLowMake = false;
				lastDay = day;
			}
			
			
			if (h>=h1 && h<=h2){
				 maxMin = maxMins.get(i-1);
				 
				 if (true
						 && maxMin>=thr1
						 //&&maxThr>=thr1
						 ){
					 
					 if (minThr>=-thr2){
						 //trade
						 TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i, data.size()-1,
								 q.getOpen5(), q.getOpen5()-tp,q.getOpen5()+sl, false);
							
						int pips = q.getOpen5()-qm.getClose5()-comm;
						if (pips>=0){
							wins++;
							actualLosses = 0;
							winPips += pips;
							
							double winRisk = pips*risk/sl;
							
							balance = balance*(1+(winRisk/100));
							if (balance>=maxBalance) maxBalance = balance;
						}else{
							losses++;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							lostPips += -pips;
							double lossRisk = pips*risk/sl;
							
							balance = balance*(1-(lossRisk/100));
							double dd = 100.0-balance*100.0/maxBalance;
							if (dd>=maxDD) maxDD = dd;
							
							if (balance<5000){
								balance    += 5000-balance;
								balanceNeed += 5000-balance;
							}
						}
					 }
					 
				 }else if (
						 true
						 && maxMin<=-thr1
						 //&& minThr<=-thr1
						 ){
					 if (maxThr<thr2){
						 TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i, data.size()-1,
								 q.getOpen5(), q.getOpen5()+tp,q.getOpen5()-sl, false);
							
						 int pips = qm.getClose5()-q.getOpen5()-comm;
						if (pips>=0){
							wins++;
							actualLosses = 0;
							winPips += pips;
							
							double winRisk = pips*risk/sl;
							
							balance = balance*(1+(winRisk/100));
							if (balance>=maxBalance) maxBalance = balance;
						}else{
							losses++;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							lostPips += -pips;
							
							double lossRisk = -pips*risk/sl;							
							balance = balance*(1-(lossRisk/100));
							double dd = 100.0-balance*100.0/maxBalance;
							if (dd>=maxDD) maxDD = dd;
							
							if (balance<5000){
								balance    += 5000-balance;
								balanceNeed += 5000-balance;
							}
						}
					 }
				 }
			}
			
			
			if (maxThr ==-999999999 || maxMins.get(i)>=maxThr){
				maxThr = maxMins.get(i);
			}
			
			if (minThr ==-999999999 || maxMins.get(i)<=minThr){
				minThr = maxMins.get(i);
			}
		
		}
	

		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = winPips*1.0/lostPips;
		avg = (winPips-lostPips)*0.1/total;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		
		//double pf$$ = wins$$/losses$$;
		int totalAños = y2-y1+1;
		
		
		double tae = 100.0*(Math.pow(balance/(balanceInicial+balanceNeed), 1.0/totalAños)-1);
		double taeFactor = tae/maxDD;
		
		
		System.out.println(
				h1+" "+h2+" "+h3+" "+h4+
				" "+thr1+" "+thr2+" "+tp+" "+sl
				+" || "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				//+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)				
				+" || "+PrintUtils.Print2dec(balanceInicial+balanceNeed, false)
				+" || "+PrintUtils.Print2dec2(balance, true)
				+" || "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(taeFactor, false)
				+" || "+PrintUtils.Print2dec(avgWin, false)
				+" || "+PrintUtils.Print2dec(avgLoss, false)
				+" || "+maxLosses
				);
							
		
	}
	
	public static void testThrs2(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			ArrayList<Integer> mas,
			ArrayList<FFNewsClass> news,
			ArrayList<Integer> hs,
			int y1,int y2,
			int h1,int h2,
			int h3,int h4,
			int tp,
			int sl,
			double thrIndex,
			int thr,
			int diff,
			int hourMode,
			int debug
			){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		double avg200 = 0;
		
		int hFirst = 0;
		int hSecond = 0;
		int minH = 0;
		int maxH = 0;
		int totalDays = 0;
		int count = 0;
		int count2=0;
		int count2win=0;
		boolean isHighMake = false;
		boolean isLowMake = false;
		int maxThr = -999999999;
		int minThr = -999999999;
		int maxThrValue = 0;
		int minThrValue = 0;
		ArrayList<ArrayList<Integer>> daymakes = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> makes = null;
		int mode = 0;
		int entry = 0;
		int entrytp = 0;
		int entrysl = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			int maxMin = maxMins.get(i);
			int ma = mas.get(i);
			if (day!=lastDay){
								
				if (mode==1){
					int pips = q.getOpen5()-entry;
					mode=0;
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}
				}else if (mode==-1){					
					int pips = entry-q.getOpen5();
					mode=0;
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}								
				}
				
				if (lastDay!=-1){
					totalDays++;
					
					int range = max-min;
					ranges.add(range);
					int begin = data.size()-1-200;
					if (begin<=0) begin = 0;
					avg = MathUtils.average(ranges, begin, data.size()-1);
										
					hFirst = minH;
					hSecond = maxH;
					
					if (maxH<minH){
						hFirst = maxH;
						hSecond = minH;						
					}else{						
					}
										
					if (debug==1){
						System.out.println("[DAY ] "+DateUtils.datePrint(cal1)+" || "+hFirst+" "+hSecond								
								+" || "+maxThr+" "+minThr
								);
					}
					
					daymakes.add(makes);
				}
				
				makes = new ArrayList<Integer>();
				minH = 0;
				maxH = 0;
				maxThr = -999999999;
				minThr = -999999999;
				hFirst = 0;
				hSecond = 0;
				isHighMake = false;
				isLowMake = false;
				lastDay = day;
			}
			
			if (makes.size()>0){
				int lastMake = makes.get(makes.size()-1);
				if (mode==0){
					if (h>=h3 && h<=h4){										
						if (lastMake==1 
								&& maxThr<=-thr
								&& q.getOpen5()<maxThrValue//-diff
								){
							int lastHigh = maxThrValue;
							mode=1;
							entry = q.getOpen5();
							entrytp = lastHigh+diff;
						}
						if (lastMake==-1
								&& maxThr>=thr
								&& q.getOpen5()>minThrValue//+diff
								){
							int lastLow = minThrValue;
							mode=-1;
							entry = q.getOpen5();
							entrytp = lastLow-diff;
						}					
					}
				}
				
				if (mode==1){				
					if (q.getHigh5()>=entrytp){
						wins++;
						winPips += entrytp-entry;
						mode=0;
					}else if (lastMake==-1){
						int pips = q.getClose5()-entry;
						mode=0;
						if (pips>=0){
							wins++;
							winPips += pips;
						}else{
							losses++;
							lostPips += -pips;
						}
					}										
				}else if (mode==-1){					
					if (q.getLow5()<=entrytp){
						wins++;
						winPips += entry-entrytp;
						mode=0;
					}else if (lastMake==1){
						int pips = entry-q.getClose5();
						mode=0;
						if (pips>=0){
							wins++;
							winPips += pips;
						}else{
							losses++;
							lostPips += -pips;
						}
					}								
				}
			}
			
			
			
			//actualizaciones al close 
			maxMin = maxMins.get(i);
			
			if (maxThr==-999999999 || maxMin>=maxThr){
				maxThr = maxMin;
				maxH = h;
				maxThrValue = q.getHigh5();
				
				if (h>=h3 && h<=h4){
					isHighMake = true;
					if (debug==1){
						System.out.println("  [HIGH MAKE ] "+DateUtils.datePrint(cal)+" || "+maxThr+" || "+minThr
								);
					}
				}
				if (h>=h3 && h<=h4)
					makes.add(1);
			} else if (minThr==-999999999 || maxMin<=minThr){
				minThr = maxMin;
				minH = h;
				minThrValue = q.getLow5();
				
				if (h>=h3 && h<=h4){
					isLowMake = true;
					if (debug==1){
						System.out.println("  [LOW MAKE ] "+DateUtils.datePrint(cal)+" || "+maxThr+" || "+minThr
								);
					}
				}
				if (h>=h3 && h<=h4){	
					makes.add(-1);					
				}
			}else{
				//if (h>=h3 && h<=h4)
					//makes.add(0);
			}
		}
		
		
		int countmakes = 0;
		int makesW = 0;
		for (int i=0;i<daymakes.size();i++){
			makes = daymakes.get(i);
			for (int j=7;j<makes.size()-1;j++){
				if (makes.get(j)!=0){
					
					if (true
							&& makes.get(j)==makes.get(j-1)
							&& makes.get(j)==makes.get(j-2)
							&& makes.get(j)==makes.get(j-3)
							&& makes.get(j)==makes.get(j-4)
							&& makes.get(j)==makes.get(j-5)
							&& makes.get(j)==makes.get(j-6)
							&& makes.get(j)==makes.get(j-7)
							){
						countmakes++;
						if (makes.get(j)==makes.get(j+1)
								){
							makesW++;
						}
					}
				}
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = (winPips*1.0)/(lostPips);
		avg = (winPips-lostPips)*0.1/total;
		System.out.println(
				h1+" "+h2+" "+h3+" "+h4+
				" "+thr+" "+PrintUtils.Print2dec(thrIndex, false)
				+" || "
				+" "+countmakes
				+" "+PrintUtils.Print2dec(makesW*100.0/countmakes, false)
				+" || "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				//+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				//+" || "+PrintUtils.Print2dec(count*100.0/totalDays, false)
				//+" || "+PrintUtils.Print2dec(count2win*100.0/count2, false)
				);
							
		
	}
	
	public static void testThrs(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			ArrayList<Integer> mas,
			ArrayList<FFNewsClass> news,
			ArrayList<Integer> hs,
			int y1,int y2,
			int h1,int h2,
			int h3,int h4,
			int tp,
			int sl,
			double thrIndex,
			int thr,
			int hourMode,
			int debug
			){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		double avg200 = 0;
		
		int hFirst = 0;
		int hSecond = 0;
		int minH = 0;
		int maxH = 0;
		int totalDays = 0;
		int count = 0;
		int count2=0;
		int count2win=0;
		boolean isHighMake = false;
		boolean isLowMake = false;
		int maxThr = -999999999;
		int minThr = -999999999;
		ArrayList<ArrayList<Integer>> daymakes = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> makes = null;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			int maxMin = maxMins.get(i);
			int ma = mas.get(i);
			if (day!=lastDay){
				
				if (lastDay!=-1){
					totalDays++;
					
					int range = max-min;
					ranges.add(range);
					int begin = data.size()-1-200;
					if (begin<=0) begin = 0;
					avg = MathUtils.average(ranges, begin, data.size()-1);
										
					hFirst = minH;
					hSecond = maxH;
					
					if (maxH<minH){
						hFirst = maxH;
						hSecond = minH;						
					}else{						
					}
										
					if (debug==1){
						System.out.println("[DAY ] "+DateUtils.datePrint(cal1)+" || "+hFirst+" "+hSecond								
								+" || "+maxThr+" "+minThr
								);
					}
					
					daymakes.add(makes);
				}
				
				makes = new ArrayList<Integer>();
				minH = 0;
				maxH = 0;
				maxThr = -999999999;
				minThr = -999999999;
				hFirst = 0;
				hSecond = 0;
				isHighMake = false;
				isLowMake = false;
				lastDay = day;
			}
			
			maxMin = maxMins.get(i);
			
			if (maxThr==-999999999 || maxMin>=maxThr){
				maxThr = maxMin;
				maxH = h;
				
				if (h>=h3 && h<=h4){
					isHighMake = true;
					if (debug==1){
						System.out.println("  [HIGH MAKE ] "+DateUtils.datePrint(cal)+" || "+maxThr+" || "+minThr
								);
					}
				}
				if (h>=h3 && h<=h4)
					makes.add(1);
			} else if (minThr==-999999999 || maxMin<=minThr){
				minThr = maxMin;
				minH = h;
				if (h>=h3 && h<=h4){
					isLowMake = true;
					if (debug==1){
						System.out.println("  [LOW MAKE ] "+DateUtils.datePrint(cal)+" || "+maxThr+" || "+minThr
								);
					}
				}
				if (h>=h3 && h<=h4){	
					makes.add(-1);					
				}
			}else{
				//if (h>=h3 && h<=h4)
					//makes.add(0);
			}
		}
		
		
		int countmakes = 0;
		int makesW = 0;
		for (int i=0;i<daymakes.size();i++){
			makes = daymakes.get(i);
			for (int j=0;j<makes.size()-4;j++){
				if (makes.get(j)!=0){
					countmakes++;
					if (makes.get(j)==makes.get(j+1)
							&& makes.get(j)==makes.get(j+2)
							&& makes.get(j)==makes.get(j+3)
							&& makes.get(j)==makes.get(j+4)
							){
						makesW++;
					}
				}
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = (wins*tp)*1.0/(losses*sl);
		System.out.println(
				h1+" "+h2+" "+h3+" "+h4+
				" "+thr+" "+PrintUtils.Print2dec(thrIndex, false)
				+" || "
				+" "+countmakes
				+" "+PrintUtils.Print2dec(makesW*100.0/countmakes, false)
				//+" "+total
				//+" "+PrintUtils.Print2dec(winPer, false)
				//+" "+PrintUtils.Print2dec(pf, false)
				//+" || "+PrintUtils.Print2dec(count*100.0/totalDays, false)
				//+" || "+PrintUtils.Print2dec(count2win*100.0/count2, false)
				);
							
		
	}
	
	public static void testTrends(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			ArrayList<Integer> mas,
			ArrayList<FFNewsClass> news,
			ArrayList<Integer> hs,
			int y1,int y2,
			int h1,int h2,
			int h3,int h4,
			int tp,
			int sl,
			double thrIndex,
			int thr,
			int hourMode,
			int debug
			){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		double avg200 = 0;
		
		int hFirst = 0;
		int hSecond = 0;
		int minH = 0;
		int maxH = 0;
		int totalDays = 0;
		int count = 0;
		int count2=0;
		int count2win=0;
		boolean isHighMake = false;
		boolean isLowMake = false;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			int maxMin = maxMins.get(i);
			int ma = mas.get(i);
			if (day!=lastDay){
				
				if (lastDay!=-1){
					totalDays++;
					
					int range = max-min;
					ranges.add(range);
					int begin = data.size()-1-200;
					if (begin<=0) begin = 0;
					avg = MathUtils.average(ranges, begin, data.size()-1);
										
					hFirst = minH;
					hSecond = maxH;
					
					if (maxH<minH){
						hFirst = maxH;
						hSecond = minH;						
					}else{						
					}
					
					if (isHighMake){
						count2++;
						if (hFirst>=h3 && hFirst<=h4){
							count2win++;
						}
					}else if (isLowMake){
						count2++;
						if (hFirst>=h3 && hFirst<=h4){
							count2win++;
						}
					}
					
					
					if (hFirst>=h3 && hFirst<=h4){
						count++;
					}
					
					
					if (debug==1){
						System.out.println("[DAY ] "+DateUtils.datePrint(cal1)+" || "+hFirst+" "+hSecond
								+" || "+PrintUtils.Print2dec(count*100.0/totalDays, false)
								);
					}
				}
				minH = 0;
				maxH = 0;
				max = -1;
				min = -1;
				hFirst = 0;
				hSecond = 0;
				isHighMake = false;
				isLowMake = false;
				lastDay = day;
			}
			
			if (max==-1 || q.getHigh5()>=max){
				max = q.getHigh5();
				maxH = h;
				
				if (h>=h3 && h<=h4){
					isHighMake = true;
					if (debug==1){
						System.out.println("  [HIGH MAKE ] "+DateUtils.datePrint(cal)
								);
					}
				}
			}
			
			if (min==-1 || q.getLow5()<=min){
				min = q.getLow5();
				minH = h;
				if (h>=h3 && h<=h4){
					isLowMake = true;
					if (debug==1){
						System.out.println("  [LOW MAKE ] "+DateUtils.datePrint(cal)
								);
					}
				}
			}
			
			
			boolean isHAllowed = false;
			isHAllowed = hs.get(h)==1;
			if (hourMode==0){
				isHAllowed = false;
				if (h>=h1 && h<=h2) isHAllowed = true;
			}
			if (isHAllowed){				
				if (true						
						//&& trendIndex>=thrIndex						
						&& maxMin>=thr
						){
										
					if (true
							//&& q.getClose5()<q.getOpen5()-80
							){
					
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i+1, data.size()-1,q.getClose5(), q.getClose5()-tp, q.getClose5()+sl, false);
						
						if (qm.getOpen5()==1){
							wins++;
						}else{
							losses++;
						}
					}
				}else if (true
						//&& trendIndex<=-thrIndex
						&& maxMin<=-thr
						){
					if (true
							//&& q.getClose5()>q.getOpen5()+80
							){
						
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i+1, data.size()-1,q.getClose5(), q.getClose5()+tp, q.getClose5()-sl, false);
						
						if (qm.getOpen5()==1){
							wins++;
						}else{
							losses++;
						}
					}
				}
			}
		}
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = (wins*tp)*1.0/(losses*sl);
		System.out.println(
				h1+" "+h2+" "+h3+" "+h4+
				" "+thr+" "+PrintUtils.Print2dec(thrIndex, false)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(count*100.0/totalDays, false)
				+" || "+PrintUtils.Print2dec(count2win*100.0/count2, false)
				);
							
		
	}
	
	public static ArrayList<Integer> getHighsLows(ArrayList<QuoteShort> data){
		
		ArrayList<Integer> hls = new ArrayList<Integer>();
		
		
		Calendar cal = Calendar.getInstance();
		
		int lastDay = -1;
		int max = -1;
		int min = -1;
		int imax = -1;
		int imin = -1;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
					QuoteShort q1 = data.get(i-1);
					hls.set(imax, max-q1.getClose5());
					hls.set(imin, min-q1.getClose5());
				}
				max=-1;
				min=-1;
				lastDay = day;
			}
			
			if (max==-1 || q.getHigh5()>=max) {
				max = q.getHigh5();
				imax = i;
			}
			if (min==-1 || q.getLow5()<=min) {
				min=q.getLow5();
				imin=i;
			}
			
			hls.add(0);
		}
		
		return hls;
	}
	
	
public static void testHighsLows(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> hls,
		ArrayList<Integer> maxMins,
		int h1,int h2,int thr,int diff
		){

		Calendar cal = Calendar.getInstance();
		
		int lastDay = -1;
		int max = -1;
		int min = -1;
		int imax = -1;
		int imin = -1;
		int count = 0;
		int acc = 0;
		int acc2 = 0;
		int wins=0;
		int maxLoss = 0;
		int actualLoss = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
					//
				}
				max=-1;
				min=-1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i);
			if (max==-1 || q.getHigh5()>=max) {
				max = q.getHigh5();
				imax = i;
				
				if (h>=h1 && h<=h2 
						&& q.getClose5()>q.getOpen5()+diff
						&& maxMin>=thr
						//&& q.getHigh5()==q.getClose5()+diff
						) {
					count++;
					int diffAdjusted = q.getHigh5()-q.getClose5()+0;
					acc += diffAdjusted;
					int res = hls.get(i);
					if (res>0) {//es algo
						wins++;
						acc2+=res;		
						actualLoss = 0;
						winPips += res-(q.getHigh5()-q.getClose5());
					}else{
						actualLoss++;
						if (actualLoss>=maxLoss) maxLoss = actualLoss;
						lostPips += diffAdjusted;
					}
				}
			}
			if (min==-1 || q.getLow5()<=min) {
				min=q.getLow5();
				imin=i;
				if (h>=h1 && h<=h2
						&& q.getClose5()<q.getOpen5()-diff
						&& maxMin<=-thr
						//&& q.getLow5()==q.getClose5()-diff
						) {
					count++;
					int diffAdjusted = q.getClose5()-q.getLow5()+0; 
					acc += diffAdjusted;
					int res = hls.get(i);
					if (res<0){
						wins++;
						acc2+=-res;
						actualLoss = 0;
						winPips += -res-(q.getClose5()-q.getLow5());
					}else{
						actualLoss++;
						if (actualLoss>=maxLoss) maxLoss = actualLoss;
						lostPips += diffAdjusted;
					}
				}
			}
			
			hls.add(0);
		}
		
		int losses = count-wins;
		double winPer = wins*100.0/count;
		double lossPer = 100.0-winPer;
		double avgDiff = acc*0.1/count;
		double avgWin = acc2*0.1/wins-avgDiff;
		double pf = avgWin*winPer/(avgDiff*lossPer); 
		
		pf = winPips*1.0/lostPips;
		System.out.println(
				h1 +" "+h2+" "+diff+" "+thr
				+" || "+count+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avgDiff,false)
				+" || "+PrintUtils.Print2dec((100-winPer)*avgDiff/winPer,false)
				+" || "+PrintUtils.Print2dec(acc2*0.1/wins,false)
				+" || "+PrintUtils.Print2dec(pf,false)
				+" || "+PrintUtils.Print2dec(avgWin,false)
				+" || "+PrintUtils.Print2dec(avgDiff,false)
				+" || "+maxLoss
				
		);
	}


public static void testHighsLows2(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> hls,
		ArrayList<Integer> maxMins,
		int h1,int h2,int thr,int diff,
		int filter,int ref,int rangeThr
		){

		Calendar cal = Calendar.getInstance();
		
		int comm = 20;
		int lastDay = -1;
		int max = -1;
		int min = -1;
		int imax = -1;
		int imin = -1;
		int count = 0;
		int acc = 0;
		int acc2 = 0;
		int wins=0;
		int losses = 0;
		int maxLoss = 0;
		int actualLoss = 0;
		int winPips = 0;
		int lostPips = 0;
		
		int mode = 0;
		int entry = 0;
		int entrySL = 0;
		
		int rangeActual = 0;
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		
			if (day!=lastDay) {				
				if (lastDay!=-1) {
					QuoteShort q1 = data.get(i-1);
					int pips = 0;
					if (mode==1){//long
						pips = q1.getClose5()-entry;
					}else if (mode==-1){
						pips = entry-q1.getClose5();
					}
					
					if (mode!=0){
						pips -= comm;
						if (pips>=0){
							winPips += pips;
							wins++;
							actualLoss = 0;
						}else{
							lostPips += -pips;
							losses++;
							actualLoss++;
							if (actualLoss>=maxLoss) maxLoss = actualLoss;
						}
					}
				}
				rangeActual = 0;
				mode=0;
				entry = 0;
				max=-1;
				min=-1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-ref);//nos basamos en la referenca
			//el rango se calcula previamente
			rangeActual = max-min;
			//actualizacion ultimo maximo o minimo
			if (max==-1 || q.getHigh5()>=max) {
				max = q.getHigh5();
				imax = i;
			}
			if (min==-1 || q.getLow5()<=min) {
				min=q.getLow5();
				imin=i;
			}
			
			
			if (mode==0 
					&& rangeActual>=rangeThr
					){
				if (true
						&& imax==i-ref
						//&& maxMin>=thr
						) {					
					if (h>=h1 && h<=h2 
							//&& q.getClose5()>q.getOpen5()+0
							&& maxMin>=thr
						    //&& (q.getHigh5()-q.getClose5())>=100
							) {
						entry = q.getOpen5();
						if (ref==0)
							entry = q.getClose5();
						entrySL = qref.getHigh5();
						
						if (entrySL>entry){
							if ((-entry+qref.getHigh5())<=filter){
								entrySL = entry+filter;
							}
							mode=-1;
						}
					}
				}else if (true
						&& imin==i-ref
						&& maxMin<=-thr		
						) {
					if (h>=h1 && h<=h2
							//&& q.getClose5()<q.getOpen5()-0
							&& maxMin<=-thr							
							//&& (q.getClose5()-q.getLow5())>=100
							) {
						entry = q.getOpen5();
						if (ref==0)
							entry = q.getClose5();
						entrySL = qref.getLow5();
						if (entrySL<entry){
							if ((entry-qref.getLow5())<=filter){
								entrySL = entry-filter;
							}
							mode=1;
						}
					}
				}
			}else if (mode==1){
				if (q.getLow5()<=entrySL){
					lostPips += entry-entrySL+comm;
					losses++;
					mode = 0;
					actualLoss++;
					if (actualLoss>=maxLoss) maxLoss = actualLoss;
				}else{
					/*int pips = q.getClose5()-entry-comm;
					if (pips>=300){
						winPips += pips;
						wins++;
						actualLoss = 0;
						mode = 0;
					}*/
				}
			}else if (mode==-1){
				if (q.getHigh5()>=entrySL){
					lostPips += entrySL-entry+comm;
					losses++;
					mode = 0;
					actualLoss++;
					if (actualLoss>=maxLoss) maxLoss = actualLoss;
				}else{
					/*int pips = entry-q.getClose5()-comm;
					if (pips>=300){
						winPips += pips;
						wins++;
						actualLoss = 0;
						mode = 0;
					}*/
				}
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;		
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = avgWin*winPer/(avgLoss*lossPer); 
		
		pf = winPips*1.0/lostPips;
		System.out.println(
				h1 +" "+h2+" "+diff+" "+thr+" "+filter+" "+ref+" "+rangeThr
				+" || "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				//+" "+PrintUtils.Print2dec(avgDiff,false)
				//+" || "+PrintUtils.Print2dec((100-winPer)*avgDiff/winPer,false)
				//+" || "+PrintUtils.Print2dec(acc2*0.1/wins,false)
				+" || "+PrintUtils.Print2dec(pf,false)
				+" || "+PrintUtils.Print2dec(avg,false)
				+"  "+PrintUtils.Print2dec(avgWin,false)
				+"  "+PrintUtils.Print2dec(avgLoss,false)
				//+" || "+PrintUtils.Print2dec(avgDiff,false)
				+" || "+maxLoss
				
		);
	}


public static void testHighsLows3(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> hls,
		ArrayList<Integer> maxMins,
		int y1,int y2,
		int h1,int h2,int thr,int diff,
		int filter,int ref,int rangeThr,int hClose,
		int maxbars,int minProfit,int minDiff,
		double risk,
		boolean isReverseMode,
		int debug
		){

		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>(); 
		
		double balanceInicial = 10000;
		double balance = 10000;
		double balanceNeed = 0;
		double maxBalance = 10000;
		int comm = 0;
		int lastDay = -1;
		int max = -1;
		int min = -1;
		int imax = -1;
		int imin = -1;
		int count = 0;
		int acc = 0;
		int acc2 = 0;
		int wins=0;
		int losses = 0;
		int maxLoss = 0;
		int actualLoss = 0;
		int winPips = 0;
		int lostPips = 0;
		
		int mode = 0;
		int entry = 0;
		int entrySL = 0;
		
		int netPips = 0;
		int maxPips = 0;
		double maxDD = 0;
		double dd=0;
		double wins$$ = 0;
		double losses$$ = 0;
		int dayTrades = 0;
		int dayWins = 0;
		int dayLosses = 0;
		int dayPips = 0;
		int rangeActual = 0;
		int rangeActualLondon = 0;
		int londonref = 0;
		int londonup = 0;
		int londondown = 0;
		int maxLondon = 0;
		int minLondon = 0;
		int countLondon = 0;
		int count20 = 0;
		int count30 = 0;
		int count40=0;
		int count50=0;
		int count60=0;
		int count70=0;
		int count80=0;
		int touchesUp = 0;
		int touchesDown = 0;
		int touchesBoth = 0;
		int touchesCount=0;
		int winsL = 0;
		int lossesL = 0;
		ArrayList<Integer> rangesArr = new ArrayList<Integer>();
		QuoteShort qm = new QuoteShort();
		
		int tp = 150;
		int sl = tp*3;
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int minute = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
		
			if (day!=lastDay 
					&& (h==0 
						//&& minute==15
						)
					) {				
				if (lastDay!=-1) {
					QuoteShort q1 = data.get(i-1);
					//evalucacion posiciones pendientes
					int j=0;
					while (j<positions.size()){
						PositionShort p = positions.get(j);
						boolean isClosed = false;
						int pips = 0;
						if (p.getPositionStatus()==PositionStatus.OPEN
								&& i>=p.getOpenIndex()+maxbars
								){							
							if (p.getPositionType()==PositionType.LONG
								
									){
								pips = q.getOpen5()-p.getEntry();
								
								if (debug==2) {
									System.out.println("[CLOSED LONG ENDDAY] "
									+" "+DateUtils.datePrint(cal)
									+" "+q.getOpen5()
									+" "+pips
									);
								}
							}else if (p.getPositionType()==PositionType.SHORT){
								pips = p.getEntry()-q.getOpen5();
								
								if (debug==2) {
									System.out.println("[CLOSED SHORT ENDDAY] "
									+" "+DateUtils.datePrint(cal)
									+" "+q.getOpen5()
									+" "+pips
									);
								}
							}
							isClosed = true;
						}
						
						if (isClosed){
							pips -= comm;
							dayPips+=pips;
							double profit = pips*0.1*p.getMicroLots()*0.1;
							if (profit>=0) {
								wins$$ += profit;
								dayWins++;
							}else {
								losses$$ +=-profit;
								dayLosses++;
							}
							
							balance += pips*0.1*p.getMicroLots()*0.1;
							if (balance<=maxBalance) {
								dd = 100.0-balance*100.0/maxBalance;
								if (dd>=maxDD) maxDD = dd;
							}else {
								maxBalance = balance;
							}
							
							if (debug==3) {
								System.out.println("[BALANCE] "+DateUtils.datePrint(cal)
								+" "+pips
								+" || "+p.getMicroLots()
								//+" || "+q1.toString()
								//+" || "+q.toString()
								+" || "+PrintUtils.Print2dec(balance, false)
								+" || "+PrintUtils.Print2dec(profit, false)
								+" || "+PrintUtils.Print2dec(wins$$, false)
								+" || "+PrintUtils.Print2dec(losses$$, false)
								+" ||| "+PrintUtils.Print2dec(dd, false)
								+" "+PrintUtils.Print2dec(maxDD, false)
								);
							}
							
							
							
							if (pips>=0){
								winPips += pips;
								wins++;
								actualLoss = 0;
							}else{
								lostPips += -pips;
								losses++;
								actualLoss++;
								if (actualLoss>=maxLoss) maxLoss = actualLoss;
							}
							
							positions.remove(j);
						}else{
							j++;
						}
					}					
				}
				
				if (debug==3) {
					System.out.println("[DAY] "+DateUtils.datePrint(cal)
					+" "+dayTrades+" "+dayWins+" "+dayLosses+" "+dayPips
					+" || "+PrintUtils.Print2dec(dayWins*100.0/dayTrades, false)
					);
				}
				
				rangeActual = 0;
				mode=0;
				entry = 0;
				max=-1;
				min=-1;
				lastDay = day;
				
				dayTrades = 0;
				dayWins = 0;
				dayLosses = 0;
				dayPips = 0;
			}
			
			int maxMin = maxMins.get(i-1);
			int refOk = i-1;
			for (int r=i-1;r>=i-ref;r--){
				maxMin = maxMins.get(r);
				if (maxMin>=thr && imax==r){
					refOk = r;
					break;
				}else if (maxMin<=-thr && imin==r){
					refOk = r;
					break;
				}
			}
			qref = data.get(refOk);
			//el rango se calcula previamente, antes de actualizar maximos y minimos
			rangeActual = max-min;
			
			int rangeLondon = maxLondon-minLondon;
			
			if (h>=h1 && h<=h2){
				
				if (h==h1 && minute==0){
					londonref = q.getOpen5();
					londonup = londonref+diff;
					londondown = londonref-diff;
				}
				
				int isChanged = 0;
				if (maxLondon==-1 || q.getHigh5()>=maxLondon){
					maxLondon = q.getHigh5();
					isChanged = 1;
				}
				if (minLondon==-1 || q.getLow5()<=minLondon){
					minLondon = q.getLow5();
					isChanged = -1;
				}
				if (isChanged!=0){
					if (isChanged==1){
						
					}else if (isChanged==-1){
						
					}
					//System.out.println("[Day] "+DateUtils.datePrint(cal)+" || "+rangeLondon);
				}
				
				
				if (q.getOpen5()<londonup && q.getHigh5()>=londonup){
					touchesUp++;
					
					/*if (true
							&& q.getClose5()<q.getOpen5()
							){
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i+1, data.size()-1,q.getClose5(), q.getClose5()-tp, q.getClose5()+sl, false);
						
						if (qm.getOpen5()==1){
							winsL++;
						}else{
							lossesL++;
						}
					}*/
				}
				if (q.getOpen5()>londondown && q.getLow5()<=londondown){
					touchesDown++;
					
					/*if (true
							&& q.getClose5()>q.getOpen5()
							){
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i+1, data.size()-1,q.getClose5(), q.getClose5()+tp, q.getClose5()-sl, false);
						
						if (qm.getOpen5()==1){
							winsL++;
						}else{
							lossesL++;
						}
					}*/
				}
				if (true
						//cal.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY
						){
					if (true
							&& q.getHigh5()>=londonup
							//&& q.getOpen5()<londonup 
							){
											
						if (true
								&& q.getClose5()>q.getOpen5()+0
								&& maxMins.get(i)>=thr
								){
							TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i+1, data.size()-1,q.getClose5(), q.getClose5()-tp, q.getClose5()+sl, false);
							
							if (qm.getOpen5()==1){
								winsL++;
							}else{
								lossesL++;
							}
						}
					}
					
					if (true
							&& q.getLow5()<=londondown
							//&& q.getOpen5()>londondown
							){										
						if (true
								&& q.getClose5()<q.getOpen5()-0
								&& maxMins.get(i)<=-thr
								){
							TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i+1, data.size()-1,q.getClose5(), q.getClose5()+tp, q.getClose5()-sl, false);
							
							if (qm.getOpen5()==1){
								winsL++;
							}else{
								lossesL++;
							}
						}
					}
				}
			}else if (h==h2+1 && minute==0 && maxLondon!=-1 && minLondon!=-1){
				rangeLondon = maxLondon-minLondon;
				rangesArr.add(rangeLondon);
				if (rangeLondon<=200) count20++;
				if (rangeLondon<=300) count30++;
				if (rangeLondon<=400) count40++;
				if (rangeLondon<=500) count50++;
				if (rangeLondon<=600) count60++;
				if (rangeLondon<=700) count70++;
				if (rangeLondon<=800) count80++;
				countLondon++;
				
				if (touchesUp>0 || touchesDown>0) touchesCount++;
				//if (touchesUp>0 && touchesDown>0) touchesBoth++;
				
				if ((touchesDown+touchesUp)==1) touchesBoth++;
				
				int begin = rangesArr.size()-1-30;
				if (begin<0) begin = 0;
				int end = rangesArr.size()-1;
				double avg = MathUtils.average(rangesArr,begin,end);
				if (debug==10)
				System.out.println("[Day] "+DateUtils.datePrint(cal)+" || "+rangeLondon
						+" || "+touchesBoth
						+" || "+PrintUtils.Print2dec(touchesBoth*100.0/touchesCount, false)
						+" || "+touchesUp
						+" || "+touchesDown
						+" || "+countLondon
						+" "+PrintUtils.Print2dec(count20*100.0/countLondon, false)
						+" "+PrintUtils.Print2dec(count30*100.0/countLondon, false)
						+" "+PrintUtils.Print2dec(count40*100.0/countLondon, false)
						+" "+PrintUtils.Print2dec(count50*100.0/countLondon, false)
						+" "+PrintUtils.Print2dec(count60*100.0/countLondon, false)
						+" "+PrintUtils.Print2dec(count70*100.0/countLondon, false)
						+" "+PrintUtils.Print2dec(count80*100.0/countLondon, false)
						+" || "+PrintUtils.Print2dec(avg*0.1, false)
						);
				maxLondon = -1;
				minLondon = -1;
				touchesUp = 0;
				touchesDown = 0;
			}//end study
			
			
			
			
			//evaluamos entradas
			if (rangeActual>=rangeThr
					){
				if (true
						&& imax==refOk
						//&& maxMin>=thr
						) {					
					if (h>=h1 && h<=h2 
							&& ((h==0 && min>=minute) || (h>0))
							&& maxMin>=thr
							) {
						entry = q.getOpen5();
						//if (ref==0)
							//entry = q.getClose5();
						entrySL = qref.getHigh5();
						
						if (entrySL>entry){
							if ((-entry+qref.getHigh5())<=filter){
								entrySL = entry+filter;
							}
							mode=-1;
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							if (isReverseMode){
								p.setSl(entrySL);
								p.setPositionType(PositionType.SHORT);
							}else{
								p.setSl(entry-filter);
								p.setPositionType(PositionType.LONG);
							}

							p.setPositionStatus(PositionStatus.OPEN);
							p.setOpenIndex(i);
							
							int pipsSL = Math.abs(entry-entrySL);
							double risk$$ = balance*risk/100;
							double pips$$ = risk$$/(pipsSL*0.1);
							int microLots = (int) (pips$$*10);
							
							if (microLots>0) {
								p.setMicroLots(microLots);
								
								if ((debug==2 || debug==6) && i>=1 ) {
									QuoteShort q1 = data.get(i-1);
									System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)
									+" "+entry+" "+entrySL
									+" || "+maxMin
									//+" || "+q1.toString()
									//+" || "+q.toString()
									+" || "+pipsSL
									+" || "+PrintUtils.Print2dec(risk$$, false)
									+" || "+PrintUtils.Print2dec(pips$$, false)
									+" || "+microLots
									);
								}
								dayTrades++;
								positions.add(p);
							}
						}
					}
				}else if (true
						&& imin==refOk	
						) {
					if (h>=h1 && h<=h2
							&& maxMin<=-thr		
							) {
						entry = q.getOpen5();
						//if (ref==0)
							//entry = q.getClose5();
						entrySL = qref.getLow5();
						if (entrySL<entry){
							if ((entry-qref.getLow5())<=filter){
								entrySL = entry-filter;
							}
							mode=1;
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setSl(entrySL);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPositionType(PositionType.LONG);
							p.setOpenIndex(i);
							
						
							if (isReverseMode){
								p.setSl(entrySL);
								p.setPositionType(PositionType.LONG);
								
							}else{
								p.setSl(entry+filter);
								p.setPositionType(PositionType.SHORT);
							}
							
							int pipsSL = Math.abs(entry-entrySL);
							double risk$$ = balance*risk/100;
							double pips$$ = risk$$/(pipsSL*0.1);
							int microLots = (int) (pips$$*10);
							if (microLots>0) {
								p.setMicroLots(microLots);
								
								if  ((debug==2 || debug==6) && i>=1 ) {
									QuoteShort q1 = data.get(i-1);
									System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)
									+" "+entry+" "+entrySL
									+" || "+maxMin
									//+" || "+q1.toString()
									//+" || "+q.toString()
									+" || "+pipsSL
									+" || "+PrintUtils.Print2dec(risk$$, false)
									+" || "+PrintUtils.Print2dec(pips$$, false)
									+" || "+microLots
									);
								}
								dayTrades++;
								positions.add(p);
							}
						}
					}
				}
			}//rangeActual>=rangeThr
			
			//actualizacion ultimo maximo o minimo
			if (max==-1 || q.getHigh5()>=max) {
				max = q.getHigh5();
				imax = i;
				if (debug==3 && maxMins.get(i)>=thr && h>=h1 && h<=h2)
					System.out.println("[DAILY HIGH] "+maxMins.get(i)+" || "+q.toString());
			}
			if (min==-1 || q.getLow5()<=min) {
				min=q.getLow5();
				imin=i;
				if (debug==3 && maxMins.get(i)<=-thr && h>=h1 && h<=h2)
					System.out.println("[DAILY LOW] "+maxMins.get(i)+" || "+q.toString());
			}
			
			
			//evaluamos SL
			int j=0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean isClosed = false;
				int pips = 0;
				long microLots = 0;
				int pipsSL = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN 
						//&& i>p.getOpenIndex()
						){		
					microLots = p.getMicroLots();
					pipsSL = Math.abs(p.getEntry()-p.getSl());
					if (p.getPositionType()==PositionType.LONG){
						if (q.getLow5()<=p.getSl()){
							pips = -(p.getEntry()-p.getSl());
							isClosed = true;
							 
							if (debug==2) {
								System.out.println("[CLOSED LONG SL] "+DateUtils.datePrint(cal)
								+" "+pips
								+" || "+q.toString()
								);
							}
						}else{
							if (i>=p.getOpenIndex()+maxbars
									&& (q.getClose5()-p.getEntry())>=minProfit
									){
								pips = q.getClose5()-p.getEntry();
								isClosed = true;
								if (debug==2) {
									System.out.println("[CLOSED LONG MAXBARS/MINPROFIT] "
											+DateUtils.datePrint(cal)
									+" "+q.getClose5()
									+" "+pips
									);
								}
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q.getHigh5()>=p.getSl()){
							pips = -(p.getSl()-p.getEntry());
							isClosed = true;
							if (debug==2) {
								System.out.println("[CLOSED SHORT SL] "+DateUtils.datePrint(cal)
								+" "+q.getClose5()
								+" "+pips
								);
							}
						}else{
							if (i>=p.getOpenIndex()+maxbars
									&& (p.getEntry()-q.getClose5())>=minProfit
									){
								pips = p.getEntry()-q.getClose5();
								isClosed = true;
								if (debug==2) {
									System.out.println("[CLOSED SHORT MAXBARS/MINPROFIT] "+DateUtils.datePrint(cal)
									+" "+q.getClose5()
									+" "+pips
									);
								}
							}
						}
					}
				}
				
				if (isClosed){
					pips -= comm;
					dayPips+=pips;
					double profit = pips*0.1*p.getMicroLots()*0.1;
					if (profit>=0) {
						wins$$ += profit;
						dayWins++;
					}else {
						losses$$ +=-profit;
						dayLosses++;
					}
					
					balance += pips*0.1*p.getMicroLots()*0.1;
					if (balance<=maxBalance) {
						dd = 100.0-(balance*100.0)/maxBalance;
						if (dd>=maxDD) maxDD = dd;
					}else {
						maxBalance = balance;
					}
					
					
					
					if (debug==4
							//&& p.getMicroLots()>=500
							) {
						System.out.println("[BALANCE] "+DateUtils.datePrint(cal)
						+" "+pips+" || "+pipsSL
						+" || "+p.getMicroLots()
						//+" || "+q1.toString()
						//+" || "+q.toString()
						+" || "+PrintUtils.Print2dec(balance, false)
						+" || "+PrintUtils.Print2dec(profit, false)
						+" || "+PrintUtils.Print2dec(wins$$, false)
						+" || "+PrintUtils.Print2dec(losses$$, false)
						+" ||| "+PrintUtils.Print2dec(dd, false)
						+" "+PrintUtils.Print2dec(maxDD, false)
						);
					}
					
					if (debug==1 && i>=1) {
						QuoteShort q1 = data.get(i-1);
						System.out.println("[BALANCE] "+DateUtils.datePrint(cal)
						+" "+pips
						+" || "+p.getMicroLots()
						//+" || "+q1.toString()
						//+" || "+q.toString()
						+" || "+PrintUtils.Print2dec(balance, false)
						);
					}
					
					
					if (pips>=0){
						winPips += pips;
						wins++;
						actualLoss = 0;
						
					}else{
						lostPips += -pips;
						losses++;
						actualLoss++;
						if (actualLoss>=maxLoss) maxLoss = actualLoss;
						
					}
					
					positions.remove(j);
				}else{
					j++;
				}
				
				if (balance<balanceInicial*0.5){
					double need = balanceInicial-balance;
					balance += need;
					balanceNeed +=need;
				}
			}								
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;		
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = avgWin*winPer/(avgLoss*lossPer); 
		
		//double balance = 1000;
		double maxRisk = 30;
		int maxLosses = maxLoss;
		double maxLossAc = maxDD*0.1/avgLoss;
		double riskLoss = maxRisk/maxLossAc;
		double riskWin = riskLoss*avgWin/avgLoss;
		
		
		//balance = balance*Math.pow((1+riskWin/100),wins);
		//balance = balance*Math.pow((1-riskLoss/100),losses);
		
		pf = winPips*1.0/lostPips;
		double pf$$ = wins$$/losses$$;
		int totalAños = y2-y1+1;
		
		
		double tae = 100.0*(Math.pow(balance/(balanceInicial+balanceNeed), 1.0/totalAños)-1);
		double taeFactor = tae/maxDD;
		
		int totalL= winsL+lossesL;
		double pfL = winsL*tp*1.0/(lossesL*sl);
		double avgL = ((winsL*tp*0.1)-(lossesL*sl*0.1))/totalL;
		if (debug==20)
		System.out.println(
				h1+" "+h2+" "+diff
				+" || "+touchesCount
				+" || "+touchesBoth
				+" || "+PrintUtils.Print2dec(touchesBoth*100.0/touchesCount, false)
				+" || "+touchesUp
				+" || "+touchesDown
				+" || "+countLondon
				+" ||| "+totalL
				+" "+PrintUtils.Print2dec(winsL*100.0/totalL, false)
				+" "+PrintUtils.Print2dec(pfL, false)
				+" "+PrintUtils.Print2dec(avgL, false)
		);
		
		if (taeFactor>=10.0)
		System.out.println(
				h1 +" "+h2+" "+diff+" "+thr
				+" "+filter+" "+ref+" "+rangeThr
				+" "+maxbars
				+" "+PrintUtils.Print2dec(risk,false)
				+" || "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				//+" "+PrintUtils.Print2dec(avgDiff,false)
				//+" || "+PrintUtils.Print2dec((100-winPer)*avgDiff/winPer,false)
				//+" || "+PrintUtils.Print2dec(acc2*0.1/wins,false)
				+" || "+PrintUtils.Print2dec(pf,false)
				+"  "+PrintUtils.Print2dec(pf$$,false)
				+" || "+PrintUtils.Print2dec(avg,false)
				+"  "+PrintUtils.Print2dec(avgWin,false)
				+"  "+PrintUtils.Print2dec(avgLoss,false)
				//+" || "+PrintUtils.Print2dec(avgDiff,false)
				+" || "+maxLoss
				+" "+PrintUtils.Print2dec(riskLoss,false)
				+" || "+PrintUtils.Print2dec(taeFactor,false)
				+" || "+PrintUtils.Print2dec(tae,false)
				+" || "
				
				+" "+PrintUtils.Print2dec2(balanceNeed+balanceInicial, true)
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD,false)
				+" || "+PrintUtils.Print2dec2(wins$$, true)
				+" "+PrintUtils.Print2dec(losses$$,false)
				
		);
	}


	public static void main(String[] args) throws Exception {
		//String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
		String path0 ="C:\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
		//String pathEURUSD = path0+"eurusd_UTC_15 Mins_Bid_2003.12.31_2017.11.25.csv";
		String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
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
			dataNoise = data;
			
			//USDJPY 160 48 48 10 90 264 // 160 60 60 10 90 264
			
			String header = "";
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataNoise);
			//ArrayList<Integer> hls = TestMaxMins.getHighsLows(data);
			
			//System.out.println(data.size()+" || "+hls.size());
			
			//0h 132 900 1 60 180
			//1h 132 900 1 60 144
			//2h 500 900 1 0  240
			//3h 720 300 1 0   72
			//4h 768 300 1 0  204
			//5h 504 400 1 0   36
			//6h 384 600 1 0   24
			//7h 400 700 1 0   24
			//8h 600 500 1 0   12
			//9h 3000 900 1 0 24
			/*for (int y1=2004;y1<=2004;y1++){
				int y2 = y1+13;
				for (int h1=0;h1<=22;h1++) {
					for (int h2=h1+1;h2<=h1+1;h2++){
						for (int hClose=24;hClose<=24;hClose++){						
							for (int diff=300;diff<=300;diff+=100){
								for (int thr=400;thr<=400;thr+=100){
									for (int filter=1000;filter<=1000;filter+=100){
										for (int ref=1;ref<=1;ref++){
											for (int rangeThr=0;rangeThr<=0;rangeThr+=10){
												for (int maxBars=240;maxBars<=240;maxBars+=12) {
													for (int minProfit=0;minProfit<=0;minProfit+=10) {
														for (double risk=1.0;risk<=1.0;risk+=0.1) {
															for (int minDiff=0;minDiff<=0;minDiff+=10)
															TestMaxMins.testHighsLows3(dataNoise, hls,maxMins,
																y1,y2,
																h1, h2,
																thr,diff,filter,ref,
																rangeThr,hClose,maxBars,
																minProfit,minDiff,															
																risk,
																false,
																20);
														}
													}
												}
											}
										}
									}
								}
							}
						}//hclose
					}//h2
				}
			}*/
			
			ArrayList<Double> indexes	= TradingUtils.calculateTrendingIndex(dataNoise, 200);
			ArrayList<Integer> mas 		= TradingUtils.getMovingAverage(dataNoise,240*1);
			System.out.println(data.size()+" || "+indexes.size());
			ArrayList<Integer> hs = new ArrayList<Integer>();
			for (int s=0;s<=23;s++) hs.add(0);
			hs.set(0, 1);
			hs.set(1, 1);
			hs.set(2, 1);
			hs.set(3, 1);
			hs.set(5, 1);
			hs.set(6, 1);
			hs.set(7, 1);
			hs.set(8, 1);
			hs.set(9, 1);
			hs.set(22, 1);
			hs.set(23, 1);
			
			ArrayList<MaxMinConfig> configs = new ArrayList<MaxMinConfig>();
			for (int s=0;s<=23;s++){
				MaxMinConfig c = new MaxMinConfig();
				if (s==0) c.setconfig(0,132,4,100,250,250,true);//1.92 6.83
				if (s==1) c.setconfig(1,192,20,135,400,750,true);//1.96 12.78				
				if (s==2) c.setconfig(2,208,18,168,200,800,true);				
				if (s==3) c.setconfig(3,512,35,96,200,200,true);				
				if (s==4) c.setconfig(4,512,35,72,100,300,true);				
				if (s==5) c.setconfig(5,476,55,50,90,450,true);
				if (s==6) c.setconfig(6,476,55,50,90,450,true);
				if (s==7) c.setconfig(7,476,55,50,90,450,true);				
				if (s==8) c.setconfig(8,400,70,13,140,420,true);	
				if (s==9) c.setconfig(9,400,70,13,140,420,true);				
				if (s==23) c.setconfig(23,144,1500,348,100,800,true);//751 2.01
				configs.add(c);
			}
			
			
			for (int y1=2004;y1<=2017;y1++){
				int y2 = y1+0;
				//for (int d=0;d<=23;d++) configs.get(d).setActive(false);
				
				for (int h1=8;h1<=8;h1++) {
					MaxMinConfig c = configs.get(h1);
					MaxMinConfig c1 = configs.get(h1+1);
					MaxMinConfig c2 = configs.get(h1+2);
					for (int thr1=200;thr1<=200;thr1+=50){
						for (int thr2=10;thr2<=10;thr2+=5){
							for (int maxbars=0;maxbars<=0;maxbars+=1) {
								for (double risk=4.0;risk<=4.0;risk+=0.1){
									//for (int sl=150;sl<=450;sl+=10){
										//for (int tp=1*sl;tp<=10*sl;tp+=1*sl){
									for (int tp=50;tp<=50;tp+=10){
										for (int sl=1*tp;sl<=1*tp;sl+=1*tp){
											for (int diff2=1;diff2<=1;diff2++){
												//c.setThr1(thr1);
												//c1.setThr1(thr1);
												//c2.setThr1(thr1);
												
												//c.setThr2(thr2);
												//c1.setThr2(thr2);
												//c2.setThr2(thr2);
												
												//c.setMaxbars(maxbars);
												//c1.setMaxbars(maxbars);
												//c2.setMaxbars(maxbars);
												
												//c.setMaxbars(maxbars);
												//c1.setMaxbars(maxbars);
												//c.setTp(tp);
												//c.setSl(sl);
												//c1.setTp(tp);
												//c1.setSl(sl);
												//c2.setTp(tp);
												//c2.setSl(sl);
												
												//for (tp=150;tp<=250;tp+=50){
													//for (sl=1*tp;sl<=4*tp;sl+=1*tp){											
														//c.setconfig(h1, thr1, thr2, maxbars, tp, sl, true);
														//c1.setconfig(h1+1, thr1, thr2, maxbars, tp, sl, true);
														//c2.setconfig(h1+2, thr1, thr2, maxbars, tp, sl, true);
												for (int impact=3;impact<=3;impact+=3){
														header = c.toString()+" "+PrintUtils.Print2dec(risk, false);
														TestMaxMins.testThrs4(header,dataNoise, maxMins, indexes,mas,news,
																configs,
																y1, y2,																		
																risk,
																diff2,1,
																impact,
																1, 0);
												}
											}
										}
									}
								}
							}//maxbars									
						}
					}
				}
			}//for y			
		}

	}

}
