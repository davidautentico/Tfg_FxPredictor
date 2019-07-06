package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class GlobalStats {
	
	ArrayList<Integer> winLossesSeq = new ArrayList<Integer>();
	double balance = 0;
	double maxBalance = 0;
	double maxDD = 0;
	int total = 0;
	int wins = 0;
	int losses = 0;
	double totalRiskPips = 0;
	double tp = 0;
	double sl = 0;
	ArrayList<PositionShort> positions = null;
	ArrayList<Calendar> balancesCal = new ArrayList<Calendar>();
	ArrayList<Double> balancesArray = new ArrayList<Double>();
	ArrayList<Double> extraNeedArray = new ArrayList<Double>();
	double balanceNeeded = 0;
	private double winPips = 0;
	private double lostPips = 0;
	private double avgWinRR;
	private double avgLossRR;
	private int specialTotalTrades = 0;
	private int specialWinsTrades = 0;
	private double specialWinsPips = 0;
	private double specialLostPips = 0;

	
	public int getMaxConsecutiveWins(){
		int actualStreak = 0;
		int maxStreak = 0;
		for (int i=0;i<winLossesSeq.size();i++){
			int w = winLossesSeq.get(i);
			if (w==1){
				actualStreak++;
				if (actualStreak>maxStreak) maxStreak = actualStreak;
			}else if (w==-1){
				//System.out.println("actualStreak: "+actualStreak);
				actualStreak=0;
			}
		}
		return maxStreak;
	}
	
	public int getMaxConsecutiveLosses(){
		int actualStreak = 0;
		int maxStreak = 0;
		for (int i=0;i<winLossesSeq.size();i++){
			int w = winLossesSeq.get(i);
			if (w==-1){
				actualStreak++;
				if (actualStreak>maxStreak) maxStreak = actualStreak;
			}else if (w==1){
				actualStreak=0;
			}
		}
		return maxStreak;
	}
			
	
	public double getSpecialWinsPips() {
		return specialWinsPips;
	}

	public void setSpecialWinsPips(double specialWinsPips) {
		this.specialWinsPips = specialWinsPips;
	}

	public double getSpecialLostPips() {
		return specialLostPips;
	}

	public void setSpecialLostPips(double specialLostPips) {
		this.specialLostPips = specialLostPips;
	}

	public int getSpecialTotalTrades() {
		return specialTotalTrades;
	}

	public void setSpecialTotalTrades(int specialTotalTrades) {
		this.specialTotalTrades = specialTotalTrades;
	}

	public int getSpecialWinsTrades() {
		return specialWinsTrades;
	}

	public void setSpecialWinsTrades(int specialWinsTrades) {
		this.specialWinsTrades = specialWinsTrades;
	}

	public double getMaxBalance() {
		return maxBalance;
	}

	public void setMaxBalance(double maxBalance) {
		this.maxBalance = maxBalance;
	}

	public double getMaxDD() {
		return maxDD;
	}

	public void setMaxDD(double maxDD) {
		this.maxDD = maxDD;
	}

	public double getAvgWinRR() {
		return avgWinRR;
	}

	public double getAvgLossRR() {
		return avgLossRR;
	}

	public ArrayList<Integer> getWinLossesSeq() {
		return winLossesSeq;
	}

	public void setWinLossesSeq(ArrayList<Integer> winLossesSeq) {
		this.winLossesSeq = winLossesSeq;
	}

	public void addWinLossSeq(int win){
		winLossesSeq.add(win);
	}
	
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}
	
	
	
	public double getTp() {
		return tp;
	}

	public void setTp(double tp) {
		this.tp = tp;
	}

	public double getSl() {
		return sl;
	}

	public void setSl(double sl) {
		this.sl = sl;
	}

	public void addWins(int wins){
		this.wins+=wins;
	}
	public void addLosses(int losses){
		this.losses+=losses;
	}
	public void addWinPips(double pips){
		this.winPips+=pips;
	}
	public void addLostPips(double pips){
		this.lostPips+=pips;
	}
	
	public double getWinPips() {
		return winPips;
	}

	public void setWinPips(double winPips) {
		this.winPips = winPips;
	}

	public double getLostPips() {
		return lostPips;
	}

	public void setLostPips(double lostPips) {
		this.lostPips = lostPips;
	}

	public ArrayList<PositionShort> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<PositionShort> positions) {
		this.positions = positions;
	}
	
	
	
	public ArrayList<Calendar> getBalancesCal() {
		return balancesCal;
	}

	public void setBalancesCal(ArrayList<Calendar> balancesCal) {
		this.balancesCal = balancesCal;
	}

	public ArrayList<Double> getBalancesArray() {
		return balancesArray;
	}

	public void setBalancesArray(ArrayList<Double> balancesArray) {
		this.balancesArray = balancesArray;
	}

	public ArrayList<Double> getExtraNeedArray() {
		return extraNeedArray;
	}

	public void setExtraNeedArray(ArrayList<Double> extraNeedArray) {
		this.extraNeedArray = extraNeedArray;
	}

	public double getBalanceNeeded() {
		return balanceNeeded;
	}

	public void setBalanceNeeded(double balanceNeeded) {
		this.balanceNeeded = balanceNeeded;
	}

	public void copy(ArrayList<PositionShort> orgPositions){
		if (orgPositions.size()==0) return;
		if (this.positions==null) positions = new ArrayList<PositionShort>();
		positions.clear();
		for (int i=0;i<orgPositions.size();i++){
			PositionShort p = orgPositions.get(i);
			PositionShort newP = new PositionShort();
			newP.copy(p);
			this.positions.add(newP);
		}
	}

	/**
	 * Análisis de las estadisticas
	 * @param globalStats
	 */
	public static void analysis(ArrayList<QuoteShort> data,GlobalStats globalStats) {
		// TODO Auto-generated method stub
		ArrayList<Integer> values = new ArrayList<Integer>();
		ArrayList<Integer> totals = new ArrayList<Integer>();
		for (int i=0;i<=500;i++) values.add(0);
		for (int i=0;i<=500;i++) totals.add(0);
		ArrayList<PositionShort> positions = globalStats.getPositions();
		double maxAtr =0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			long openIndex = p.getOpenIndex();
			double currentATR = p.getCurrentAtr();
			int win = p.getWin();
			if (openIndex>0){
				int lastBarSize = data.get((int)openIndex-1).getHigh5()-data.get((int)openIndex-1).getLow5();
				double lastBarSizeAtr = lastBarSize*0.1/currentATR;
				//ver la correlacion entre wins, tamño de la barra y currentATR
				//System.out.println(PrintUtils.Print2((int)lastBarSize*0.1)
				//		+" "+PrintUtils.Print2(currentATR)+" "+PrintUtils.Print2(lastBarSizeAtr)+" "+win);
				
				//
				if (lastBarSizeAtr>maxAtr) maxAtr = lastBarSizeAtr;
				int index = (int) (lastBarSizeAtr/0.01);
				int totalValues = values.get(index);
				int total = totals.get(index);
				values.set(index, totalValues+win);
				totals.set(index, total+1);
			}
		}
		for (int i=0;i<values.size();i++){
			int value = values.get(i);
			int total = totals.get(i);
			double index = i*0.01;
			double avg = value*1.0/total;
			if (total>0)
				System.out.println(PrintUtils.Print2(index)+" "+total+" "+PrintUtils.Print2(avg));
		}
		System.out.println("max size: "+PrintUtils.Print2(maxAtr));
	}
	
	/**
	 * Análisis de las estadisticas
	 * @param globalStats
	 */
	public void analysis2(GlobalStats globalStats,int period,boolean debug) {
		// TODO Auto-generated method stub
		ArrayList<Double> gains = new ArrayList<Double>();
		int lastDay = -1;
		int lastMonth = -1;
		int lastWeek = -1;
		double lastBalance = 0;
		for (int i=1;i<balancesArray.size();i++){
			Calendar cal = balancesCal.get(i);
			Calendar cal1 = balancesCal.get(i-1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int month = cal.get(Calendar.MONTH);
			double balance = balancesArray.get(i);
			double balance1 = balancesArray.get(i-1);
			double extra = extraNeedArray.get(i);
			double percentGain = 0.0;
			
			if (day!=lastDay && period==0){
				if (i>0 && lastDay!=-1){
					percentGain = balance1*100.0/lastBalance-100.0;		
					if (debug)
					System.out.println(DateUtils.datePrint(cal1)+" "+PrintUtils.Print2dec2(balance1, true)
							+" "+PrintUtils.Print2(extra)+" "+PrintUtils.Print2(percentGain));
					gains.add(percentGain);
				}
				lastDay = day;
				lastBalance = balance1;
			}
			
			if (week!=lastWeek && period==1){
				if (i>0 && lastWeek!=-1){
					percentGain = balance*100.0/lastBalance-100.0;	
					if (debug)
					System.out.println(DateUtils.datePrint(cal)+" "+PrintUtils.Print2dec2(balance, true)
							+" "+PrintUtils.Print2(extra)+" "+PrintUtils.Print2(percentGain));
					gains.add(percentGain);
				}
				lastWeek = week;
				lastBalance = balance;
			}
			
			if (month!=lastMonth && period==2){
				if (i>0 && lastMonth!=-1){
					percentGain = balance*100.0/lastBalance-100.0;	
					if (debug)
					System.out.println(DateUtils.datePrint(cal)+" "+PrintUtils.Print2dec2(balance, true)
							+" "+PrintUtils.Print2(extra)+" "+PrintUtils.Print2(percentGain));
					gains.add(percentGain);
				}
				lastMonth = month;
				lastBalance = balance;
			}
		}
		
		if (period==0){
			double balance1 = balancesArray.get(balancesArray.size()-1);
			Calendar cal1 = balancesCal.get(balancesCal.size()-1);
			double percentGain = balance1*100.0/lastBalance-100.0;		
			if (debug){
				System.out.println(DateUtils.datePrint(cal1)+" "+PrintUtils.Print2dec2(balance1, true)
						+" "+PrintUtils.Print2(percentGain));
				gains.add(percentGain);
			}
		}
		String header = "trades: "+balancesArray.size();
		MathUtils.summary_complete(header, gains);
	}
	
	public void analysis3(String header1,double factor) {
		// TODO Auto-generated method stub
		ArrayList<Integer> hours = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		ArrayList<Integer> winstotal = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hours.add(0);
		for (int i=0;i<=23;i++) wins.add(0);
		for (int i=0;i<=23;i++) winstotal.add(0);
		ArrayList<PositionShort> positions = this.getPositions();
		int maxWins = 0;
		int actualWins = 0;
		double avgWins = 0;
		double avgLosses = 0;
		int totalAvgWins = 0;
		int totalAvgLosses = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		//System.out.println("total positions: "+positions.size());
		for (int i=0;i<positions.size();i++){
			Calendar cal = positions.get(i).getOpenCal();
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int total = hours.get(h);
			hours.set(h,total+1);
			int twins = winstotal.get(h);
			int pwins = wins.get(h);
			winstotal.set(h, twins+1);
			int win = positions.get(i).getWin();
			if (win==1){
				wins.set(h,pwins+1);
				if (actualLosses>maxLosses) maxLosses = actualLosses;
				if (actualLosses>0){
					avgLosses += actualLosses;
					totalAvgLosses++;
				}
				actualWins++;
				actualLosses=0;
			}else if (win==-1){
				if (actualWins>maxWins) maxWins = actualWins;
				if (actualWins>0){
					avgWins += actualWins;
					totalAvgWins++;
				}
				actualLosses++;
				actualWins=0;
			}
		}
		int total = 0;
		int totalWins = 0;
		for (int i=0;i<=23;i++){
			total+= hours.get(i);
			totalWins += wins.get(i);
		}
		
		double aw = avgWins*1.0/totalAvgWins;
		double al = avgLosses*1.0/totalAvgLosses;
		double factorwl = aw/al;
		for (int i=0;i<=0;i++){
			System.out.println(
					//i
					header1
					+" || "
					+" "+maxWins+" "+maxLosses
					+" "+PrintUtils.Print2(avgWins*1.0/totalAvgWins)
					+" "+PrintUtils.Print2(avgLosses*1.0/totalAvgLosses)
					+" "+PrintUtils.Print2(aw*1.0/al)
					+" "+PrintUtils.Print2(factorwl*1.0/factor)
					//+" || "
					//+" "+PrintUtils.Print2(hours.get(i)*100.0/total)
					//+" "+PrintUtils.Print2(wins.get(i)*100.0/winstotal.get(i))
					);
		}
	}

	public void addBalance(Calendar cal, double actualBalance,
			double extraNeeded) {
		// TODO Auto-generated method stub
		Calendar newCal = Calendar.getInstance();
		newCal.setTimeInMillis(cal.getTimeInMillis());
		
		balancesCal.add(newCal);
		balancesArray.add(actualBalance);
		this.extraNeedArray.add(extraNeeded);
	}

	public void reset() {
		// TODO Auto-generated method stub
		if (this.balancesArray!=null) this.balancesArray.clear();
		if (this.balancesCal!=null) this.balancesCal.clear();
		if (this.extraNeedArray!=null) this.extraNeedArray.clear();
	}

	public void printTradesSeq() {
		// TODO Auto-generated method stub
		for (int i = 0; i<this.winLossesSeq.size();i++){
			int res = winLossesSeq.get(i);
			System.out.println(res);
		}
	}
	
	public SimulatedResults simulateSeq(ArrayList<Integer> seq, double balanceInitial, double perWin,double perLoss) {
		// TODO Auto-generated method stub
		
		double balance = balanceInitial;
		double maxBalance = balanceInitial;
		double maxDD = 0;
		for (int i = 0; i<this.winLossesSeq.size();i++){
			int res = winLossesSeq.get(i);
			if (res==1){
				balance += balance*perWin*0.01;
			}else if (res==-1){
				balance -= balance*perLoss*0.01;
			}
			
			if (balance>=maxBalance){
				maxBalance = balance;
			}else{
				double dd = 100.0-balance*100.0/maxBalance;
				if (dd>=maxDD) maxDD = dd;
			}
			/*System.out.println(res
					+" "+PrintUtils.Print2dec2(balance, true)
					);*/
		}
		
		SimulatedResults sim = new SimulatedResults();
		sim.setMaxBalance(maxBalance);
		sim.setBalance(balance);
		sim.setMaxDD(maxDD);
		
		return sim;
	}

	public double printSummary(String header) {
		// TODO Auto-generated method stub
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double pf = winPips*1.0/lostPips;
		double avgPips = (winPips-lostPips)/total;
		double avgRiskPips = totalRiskPips*1.0/total;
		double R = this.avgWinRR/this.avgLossRR;
		double kelly = winPer*0.01-(lossPer*0.01/R);
		double per = (avgPips*1.0/avgRiskPips)*kelly;
		double kellyAmount = Math.pow(1*(1+per),total);
		System.out.println(
				header
				+" || "
				+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winPips-lostPips, false)
				+" "+PrintUtils.Print2dec(winPips, false)
				+" "+PrintUtils.Print2dec(lostPips, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(avgRiskPips, false)
				+" || "+PrintUtils.Print2dec(avgPips*1.0/avgRiskPips, false)
				+" "+PrintUtils.Print2dec( this.avgWinRR, false)
				+" "+PrintUtils.Print2dec(this.avgLossRR, false)
				+" "+PrintUtils.Print2dec(kelly, false)
				+" "+PrintUtils.Print2dec(kellyAmount, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
		
		return pf;
	}

	public void addRiskPips(double totalRiskPips) {
		// TODO Auto-generated method stub
		this.totalRiskPips+=totalRiskPips;
	}

	public double getTotalRiskPips() {
		return totalRiskPips;
	}

	public void setTotalRiskPips(double totalRiskPips) {
		this.totalRiskPips = totalRiskPips;
	}

	public void setAvgWinRR(double avgWinRR) {
		// TODO Auto-generated method stub
		this.avgWinRR = avgWinRR;
	}

	public void setAvgLossRR(double avgLossRR) {
		// TODO Auto-generated method stub
		this.avgLossRR = avgLossRR;
	}

	public void analysis4(ArrayList<QuoteShort> data, GlobalStats globalStats) {
		// TODO Auto-generated method stub
		int badTrades = 0;
		ArrayList<PositionShort> positions = globalStats.getPositions();
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			long openIndex = p.getOpenIndex();
			long closeIndex = p.getCloseIndex();
			//if (openIndex<closeIndex)
				//System.out.println(openIndex+" "+closeIndex);
			if (openIndex==closeIndex 
					&& p.getWinPips()==-1
					){
				badTrades++;
			}
		}
		double badPer = badTrades*100.0/positions.size();
		System.out.println(positions.size()+" "+PrintUtils.Print2dec(badPer, false));
	}
	
	public void analysis5(ArrayList<QuoteShort> data, GlobalStats globalStats,int num) {
		// TODO Auto-generated method stub
		int badTrades = 0;
		ArrayList<PositionShort> positions = globalStats.getPositions();
		ArrayList<Double> pers = new ArrayList<Double>();
		for (int i=0;i<=positions.size()-num;i++){
		
			int count = 0;
			int wins = 0;
			for (int j=i;j<i+num;j++){
				PositionShort p = positions.get(j);
				if (p.getPositionStatus() == PositionStatus.CLOSE){
					count++;
					if (p.getWin()==1) wins++;
				}
			}
			pers.add(wins*100.0/count);
			System.out.println(count+","+PrintUtils.Print2dec(wins*100.0/count, false));
			i = i+num;
		}
		
		MathUtils.summary_mean_sd("", pers);
		
	}

	public void addSpecialTotalTrades(int total) {
		// TODO Auto-generated method stub
		this.specialTotalTrades += total;
	}

	public void addSpecialWinsTrades(int total) {
		// TODO Auto-generated method stub
		this.specialWinsTrades+=total;
	}

	public void addSpecialWinPips(double pips) {
		this.specialWinsPips += pips;
		
	}

	public void addSpecialLostPips(double pips) {
		// TODO Auto-generated method stub
		this.specialLostPips += pips;
	}
	
	
}
