package drosa.strategies.summary;

import java.util.ArrayList;

import drosa.strategies.MarketType;
import drosa.strategies.StrategyBase;
import drosa.strategies.SymbolInfo;
import drosa.strategies.auxiliar.Position;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class StrategySummary {
	StrategyBase strategy = null;
	String description = "";
	
	//init
	double mult=1;
	double tickValue=1;
	double firstBalance=0;
	double initialBalance = 0;
	double maxPatrimonio = 0;
	double patrimonio = 0;
	double actualMargin = 0;
	double freeMargin =0;
	double commissions = 0;
	
	
	//Overall
	ArrayList<Position> allTrades = new ArrayList<Position>();
	ArrayList<Position> winTrades = new ArrayList<Position>();
	ArrayList<Position> looseTrades = new ArrayList<Position>();
	
	ArrayList<Double> totalPointsArray= new ArrayList<Double>();
	double totalPointsProfit=0.0;
	double totalNetProfit = 0.0;
	int totalTrades       = 0;
	double averageTrade$   = 0.0;
	double averageTrade   = 0.0;
	double avgBarsInTrade = 0.0;
	double avgBarsPerYear = 0.0;
	double maxPointsGained =0.0;
	double maxCloseOutDD  = 0.0;
	double maxCloseOutPer  = 0.0;
	double maxCloseOutDDDuration  = 0.0;
	double accountSizeRequeried = 0.0;
	double openEquity = 0.0;
	int currentStreak = 0;
	double profitFactor = 0.0; //($wins/losses)
	double winningPer = 0.0;
	double payoutRatio = 0.0; //(avg win/loss)
	double zScore =0.0; //(W/L Predictability)
	double maxIntradayDD = 0.0;
	double returnPer = 0.0;
	double kellyRatio = 0.0;
	double optimalF =0.0;
	double maxNetProfit=0;
	double maxRiskPerTrade=0;
	int totalRR1_1=0;
	double totalPipsRisked=0;
	double CAGR=0; //Compound Annual Growth
	double AAR=0; //aNUAL AVERAGE
	
	//winning trades
	int totalWins = 0;
	double totalwinPips=0;
	double grossProfit = 1.0;
	double averageWin = 0.0;
	double largestWin = 0.0;
	double largestDDInWin = 0.0;
	double avgDDInWin = 0.0;
	double avgRunUpInWin = 0.0;
	double avgRunDownInWin = 0.0;
	int currentConsecWins=0;
	int mostConsecWins = 0;
	int avgConsecWins = 0;
	int avgBarsInWins = 0;
	
	//lossing trades
	int totalLosses = 0;
	double totallossPips=0;
	double grossLoss = 1.0;
	double averageLoss = 0.0;
	double largestLoss = 0.0;
	double largestDDInLoss = 0.0;
	double avgDDInLoss = 0.0;
	double avgRunUpInLoss = 0.0;
	double avgRunDownInLoss = 0.0;
	int currentConsecLosses=0;
	int mostConsecLosses = 0;
	int avgConsecLosses = 0;
	int avgBarsInLosses = 0;
	private double spread;
	private double maxInitialBalance;
		
	public StrategySummary(double initialBalance) {
		// TODO Auto-generated constructor stub
		this.firstBalance = initialBalance;
		this.initialBalance = initialBalance;
		this.actualMargin = 0;
		this.patrimonio = initialBalance;
		this.freeMargin = initialBalance;
		this.maxPatrimonio=initialBalance;
		this.maxCloseOutDD=-999999;
		this.averageTrade=0;
	}
	
	
	public StrategyBase getStrategy() {
		return strategy;
	}


	public void setStrategy(StrategyBase strategy) {
		this.strategy = strategy;
	}


	
	public double getMult() {
		return mult;
	}


	public void setMult(double minTick) {
		this.mult = minTick;
	}

	
	

	public double getTickValue() {
		return tickValue;
	}


	public void setTickValue(double tickValue) {
		this.tickValue = tickValue;
	}


	public int getTotalRR1_1() {
		return totalRR1_1;
	}


	public void setTotalRR1_1(int totalRR1_1) {
		this.totalRR1_1 = totalRR1_1;
	}


	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getInitialBalance() {
		return initialBalance;
	}
	public void setInitialBalance(double initialBalance) {
		//System.out.println("modificacion initialbalance: "+initialBalance);
		this.initialBalance = initialBalance;
	}
	public double getPatrimonio() {
		return patrimonio;
	}
	public void setPatrimonio(double patrimonio) {
		this.patrimonio = patrimonio;
	}
	public double getActualMargin() {
		return actualMargin;
	}
	public void setActualMargin(double actualMargin) {
		this.actualMargin = actualMargin;
	}
	public double getFreeMargin() {
		return freeMargin;
	}
	public void setInitialMargin(double freeMargin) {
		this.freeMargin = freeMargin;
	}
	
	public ArrayList<Double> getTotalPointsArray() {
		return totalPointsArray;
	}
	public void setTotalPointsArray(ArrayList<Double> totalPointsArray) {
		this.totalPointsArray = totalPointsArray;
	}
	public double getTotalPointsProfit() {
		return totalPointsProfit;
	}
	public void setTotalPointsProfit(double totalPointsProfit) {
		this.totalPointsProfit = totalPointsProfit;
	}

	
	public double getTotalwinPips() {
		return totalwinPips;
	}


	public void setTotalwinPips(double totalwinPips) {
		this.totalwinPips = totalwinPips;
	}


	public double getTotallossPips() {
		return totallossPips;
	}


	public void setTotallossPips(double totallossPips) {
		this.totallossPips = totallossPips;
	}


	public double getMaxNetProfit() {
		return maxNetProfit;
	}
	public void setMaxNetProfit(double maxNetProfit) {
		this.maxNetProfit = maxNetProfit;
	}
	public double getTotalNetProfit() {
		return totalNetProfit;
	}
	public void setTotalNetProfit(double totalNetProfit) {
		this.totalNetProfit = totalNetProfit;
	}
	public int getTotalTrades() {
		return totalTrades;
	}
	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}
	public double getAverageTrade() {
		return averageTrade;
	}
	public void setAverageTrade(double averageTrade) {
		this.averageTrade = averageTrade;
	}
	public double getAvgBarsInTrade() {
		return avgBarsInTrade;
	}
	public void setAvgBarsInTrade(double avgBarsInTrade) {
		this.avgBarsInTrade = avgBarsInTrade;
	}
	public double getAvgBarsPerYear() {
		return avgBarsPerYear;
	}
	public void setAvgBarsPerYear(double avgBarsPerYear) {
		this.avgBarsPerYear = avgBarsPerYear;
	}
	public double getMaxCloseOutDD() {
		return maxCloseOutDD;
	}
	public void setMaxCloseOutDD(double maxCloseOutDD) {
		this.maxCloseOutDD = maxCloseOutDD;
	}
	public double getAccountSizeRequeried() {
		return accountSizeRequeried;
	}
	public void setAccountSizeRequeried(double accountSizeRequeried) {
		this.accountSizeRequeried = accountSizeRequeried;
	}
	public double getOpenEquity() {
		return openEquity;
	}
	public void setOpenEquity(double openEquity) {
		this.openEquity = openEquity;
	}
	public int getCurrentStreak() {
		return currentStreak;
	}
	public void setCurrentStreak(int currentStreak) {
		this.currentStreak = currentStreak;
	}
	public double getProfitFactor() {
		return profitFactor;
	}
	public void setProfitFactor(double profitFactor) {
		this.profitFactor = profitFactor;
	}
	public double getWinningPer() {
		return totalWins*100.0/totalTrades;
	}
	public void setWinningPer(double winningPer) {
		this.winningPer = winningPer;
	}
	public double getPayoutRatio() {
		return payoutRatio;
	}
	public void setPayoutRatio(double payoutRatio) {
		this.payoutRatio = payoutRatio;
	}
	public double getzScore() {
		return zScore;
	}
	public void setzScore(double zScore) {
		this.zScore = zScore;
	}
	public double getMaxIntradayDD() {
		return maxIntradayDD;
	}
	public void setMaxIntradayDD(double maxIntradayDD) {
		this.maxIntradayDD = maxIntradayDD;
	}
	public double getReturnPer() {
		return returnPer;
	}
	public void setReturnPer(double returnPer) {
		this.returnPer = returnPer;
	}
	public double getKellyRatio() {
		return kellyRatio;
	}
	public void setKellyRatio(double kellyRatio) {
		this.kellyRatio = kellyRatio;
	}
	public double getOptimalF() {
		return optimalF;
	}
	public void setOptimalF(double optimalF) {
		this.optimalF = optimalF;
	}
	public int getTotalWins() {
		return totalWins;
	}
	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}
	public double getGrossProfit() {
		return grossProfit;
	}
	public void setGrossProfit(double grossProfit) {
		this.grossProfit = grossProfit;
	}
	public double getAverageWin() {
		return averageWin;
	}
	public void setAverageWin(double averageWin) {
		this.averageWin = averageWin;
	}
	public double getLargestWin() {
		return largestWin;
	}
	public void setLargestWin(double largestWin) {
		this.largestWin = largestWin;
	}
	public double getLargestDDInWin() {
		return largestDDInWin;
	}
	public void setLargestDDInWin(double largestDDInWin) {
		this.largestDDInWin = largestDDInWin;
	}
	public double getAvgDDInWin() {
		return avgDDInWin;
	}
	public void setAvgDDInWin(double avgDDInWin) {
		this.avgDDInWin = avgDDInWin;
	}
	public double getAvgRunUpInWin() {
		return avgRunUpInWin;
	}
	public void setAvgRunUpInWin(double avgRunUpInWin) {
		this.avgRunUpInWin = avgRunUpInWin;
	}
	public double getAvgRunDownInWin() {
		return avgRunDownInWin;
	}
	public void setAvgRunDownInWin(double avgRunDownInWin) {
		this.avgRunDownInWin = avgRunDownInWin;
	}
	public int getMostConsecWins() {
		return mostConsecWins;
	}
	public void setMostConsecWins(int mostConsecWins) {
		this.mostConsecWins = mostConsecWins;
	}
	public int getAvgConsecWins() {
		return avgConsecWins;
	}
	public void setAvgConsecWins(int avgConsecWins) {
		this.avgConsecWins = avgConsecWins;
	}
	public int getAvgBarsInWins() {
		return avgBarsInWins;
	}
	public void setAvgBarsInWins(int avgBarsInWins) {
		this.avgBarsInWins = avgBarsInWins;
	}
	public int getTotalLosses() {
		return totalLosses;
	}
	public void setTotalLosses(int totalLosses) {
		this.totalLosses = totalLosses;
	}
	public double getGrossLoss() {
		return grossLoss;
	}
	public void setGrossLoss(double grossLoss) {
		this.grossLoss = grossLoss;
	}
	public double getAverageLoss() {
		return averageLoss;
	}
	public void setAverageLoss(double averageLoss) {
		this.averageLoss = averageLoss;
	}
	public double getLargestLoss() {
		return largestLoss;
	}
	public void setLargestLoss(double largestLoss) {
		this.largestLoss = largestLoss;
	}
	public double getLargestDDInLoss() {
		return largestDDInLoss;
	}
	public void setLargestDDInLoss(double largestDDInLoss) {
		this.largestDDInLoss = largestDDInLoss;
	}
	public double getAvgDDInLoss() {
		return avgDDInLoss;
	}
	public void setAvgDDInLoss(double avgDDInLoss) {
		this.avgDDInLoss = avgDDInLoss;
	}
	public double getAvgRunUpInLoss() {
		return avgRunUpInLoss;
	}
	public void setAvgRunUpInLoss(double avgRunUpInLoss) {
		this.avgRunUpInLoss = avgRunUpInLoss;
	}
	public double getAvgRunDownInLoss() {
		return avgRunDownInLoss;
	}
	public void setAvgRunDownInLoss(double avgRunDownInLoss) {
		this.avgRunDownInLoss = avgRunDownInLoss;
	}
	public int getMostConsecLosses() {
		return mostConsecLosses;
	}
	public void setMostConsecLosses(int mostConsecLosses) {
		this.mostConsecLosses = mostConsecLosses;
	}
	public int getAvgConsecLosses() {
		return avgConsecLosses;
	}
	public void setAvgConsecLosses(int avgConsecLosses) {
		this.avgConsecLosses = avgConsecLosses;
	}
	public int getAvgBarsInLosses() {
		return avgBarsInLosses;
	}
	public void setAvgBarsInLosses(int avgBarsInLosses) {
		this.avgBarsInLosses = avgBarsInLosses;
	}
	
	public double getCommissions() {
		return commissions;
	}
	public void setCommissions(double commissions) {
		this.commissions = commissions;
	}
	public void setFreeMargin(double freeMargin) {
		this.freeMargin = freeMargin;
	}
	
	
	
	public double getMaxRiskPerTrade() {
		return maxRiskPerTrade;
	}


	public void setMaxRiskPerTrade(double maxRiskPerTrade) {
		this.maxRiskPerTrade = maxRiskPerTrade;
	}


	public ArrayList<Position> getWinTrades() {
		return winTrades;
	}
	public void setWinTrades(ArrayList<Position> winTrades) {
		this.winTrades = winTrades;
	}
	public ArrayList<Position> getLooseTrades() {
		return looseTrades;
	}
	public void setLooseTrades(ArrayList<Position> looseTrades) {
		this.looseTrades = looseTrades;
	}
	public double getMaxPointsGained() {
		return maxPointsGained;
	}
	public void setMaxPointsGained(double maxPointsGained) {
		this.maxPointsGained = maxPointsGained;
	}
	public double getMaxCloseOutDDDuration() {
		return maxCloseOutDDDuration;
	}
	public void setMaxCloseOutDDDuration(double maxCloseOutDDDuration) {
		this.maxCloseOutDDDuration = maxCloseOutDDDuration;
	}
	public void printBalanceInfo() {
		// TODO Auto-generated method stub
		String str ="BalanceInicial "+PrintUtils.Print(this.initialBalance)+
			" Patrimonio "+PrintUtils.Print(this.patrimonio)+
			" Patrimonio neto "+PrintUtils.Print(this.patrimonio-this.commissions)+
			" Margen "+PrintUtils.Print(this.actualMargin)+
			" MargenLibre "+PrintUtils.Print(this.freeMargin)+
		    " Comisiones "+PrintUtils.Print(this.commissions);
			
		System.out.println(str);
	}
	
	public void printOverallInfo() {
		// TODO Auto-generated method stub
		String str =this.description+
			" Profit/DD "+PrintUtils.Print((this.totalNetProfit-this.commissions)/this.maxCloseOutDD)+
			" Profit N "+PrintUtils.Print(this.totalNetProfit-this.commissions)+	
			" Profit " +PrintUtils.Print(this.totalNetProfit)+
			" Comm " +PrintUtils.Print(this.commissions)+
			" Points " +PrintUtils.Print(this.totalPointsProfit)+
			" PF " +PrintUtils.Print(this.profitFactor)+
			" MaxCloseDD "+PrintUtils.Print(this.maxCloseOutDD)+
			" Trades "+PrintUtils.Print(this.totalTrades)+
			//" Wins "+PrintUtils.Print(this.totalWins)+
			" Wins(%) "+PrintUtils.Print(this.totalWins*100.0/this.totalTrades)+
			//" Losses "+PrintUtils.Print(this.totalLosses)+
			" Losses(%) "+PrintUtils.Print(this.totalLosses*100.0/this.totalTrades);
			
		System.out.println(str);
	}
	

	public void printStudyInfo(String header) {
		// TODO Auto-generated method stub
		String str ="Trades "+this.totalTrades+
			" Win% "+PrintUtils.Print(this.totalWins*100.0/this.totalTrades)+
			" avgTr "+PrintUtils.Print((this.totalwinPips-this.totallossPips)/this.totalTrades)+
			" Pips "+PrintUtils.Print(this.totalwinPips-this.totallossPips)+			
			//" PipsW "+PrintUtils.Print(this.totalwinPips)+
			//" PipsL "+PrintUtils.Print(this.totallossPips)+
			" wr "+PrintUtils.Print((this.totalwinPips/this.totalWins)/(this.totallossPips/this.totalLosses))+
			" pf "+PrintUtils.Print(this.totalwinPips/this.totallossPips)+
			" dd "+PrintUtils.Print(this.maxCloseOutDD)+"%"
			;
			
		System.out.println(header+" "+str);
	}
	public void updateCurrentTrade(Position pos,double actualValue) {
		
		double diff =0.0;
		if (pos.getType()==PositionType.LONG){ //win			
			diff = actualValue-pos.getEntryValue();
			double perW = diff*100.0/pos.getEntryValue();
			pos.setProfitPer(perW);
			if (perW<0.0 && pos.getMaxDDPer()<Math.abs(perW)){
				pos.setMaxDDPer(Math.abs(perW));
			
			}
			/*System.out.println("entrada y actual y porcentage: "
					+PrintUtils.Print(pos.getEntryValue())+
					" "+PrintUtils.Print(actualValue)+
					" "+PrintUtils.Print(perW));*/
		}else if (pos.getType()==PositionType.SHORT){//loss			
			diff = pos.getEntryValue()-actualValue;
			double perW = diff*100.0/pos.getEntryValue();
			pos.setProfitPer(perW);
			if (perW<0.0 && pos.getMaxDDPer()<Math.abs(perW)){
				pos.setMaxDDPer(Math.abs(perW));
			}
		}
	}
	public void openTrade(SymbolInfo symbolInfo, double actualValue) {
		// TODO Auto-generated method stub
		this.commissions+=30;//30 euros de comision+slippage, habra que revisarlo
	}
	public void closeTrade(Position pos, SymbolInfo info, double diff) {
		// TODO Auto-generated method stub
		this.totalTrades++;
		this.totalPointsProfit+=diff;
		this.totalNetProfit+=diff*info.getMult();
		Position wPos = new Position();
		
		wPos.setMaxDDPer(Math.abs(pos.getMaxDDPer()));
		wPos.setProfitPer(Math.abs(pos.getProfitPer()));
		if (diff>=0){ //victoria
			this.totalWins++;
			this.grossProfit+=Math.abs(diff*info.getMult());
			this.winTrades.add(wPos);
			//System.out.println("[wintrade] add maxddper: "+wPos.getMaxDDPer());
		}else{
			this.totalLosses++;
			this.grossLoss+=Math.abs(diff*info.getMult());
			this.looseTrades.add(wPos);
			//System.out.println("[losetrade] add maxddper: "+wPos.getMaxDDPer());
		}
		this.profitFactor=this.grossProfit/this.grossLoss;
		//System.out.println("grossProfit,grossLoss: "+PrintUtils.Print(this.grossProfit)+
		//		" "+PrintUtils.Print(this.grossLoss));
	}
	
	public void updatePointsArray(double totalPoints) {
		// TODO Auto-generated method stub
		this.totalPointsArray.add(totalPoints);
	}
	
	public void updateMaxDD(){
		
		double lastPoints = totalPointsArray.get(totalPointsArray.size()-1);		
		if (lastPoints>maxPointsGained){ // no hay drawdown
			maxPointsGained = lastPoints;
		}
		double actualDD=this.maxPointsGained-lastPoints;
		if (this.maxCloseOutDD<actualDD*10){
			this.maxCloseOutDD = actualDD*10;
		}
		
	}
	
	public void calculateMaxDD(){
		ArrayList<Double> drawdown= new ArrayList<Double>();
		ArrayList<Integer> duration = new ArrayList<Integer>();
		for (int i=0;i<totalPointsArray.size();i++){
			drawdown.add(0.0);
			duration.add(0);
		}
		double maxPoints=0;
		double maxDD=0.0;
		double maxDDD=0.0;
		for (int i=1;i<totalPointsArray.size();i++){
			double pointsActuales = totalPointsArray.get(i);			
			if (pointsActuales>maxPoints){ // no hay drawdown
				maxPoints = pointsActuales;
			}
			double actualDD=maxPoints-pointsActuales;
			drawdown.add(i,actualDD);
			if (actualDD<1.0){
				duration.add(0);
			}else{
				duration.add(i,duration.get(i-1)+1);
			}	
			
			if (maxDD<actualDD)
				maxDD = actualDD;
			if (maxDDD<duration.get(i));
				maxDDD = duration.get(i);
		}
		this.maxCloseOutDD =maxDD*10;//30 por trade; hay que configurar
		this.maxCloseOutDDDuration = maxDDD;
	}
	
	public ArrayList<Double>  getProfitArray(boolean wins){
		ArrayList<Double> array = new ArrayList<Double>();
		
		if (wins){
			for (int i=0;i<this.winTrades.size();i++){
				array.add(Math.abs(winTrades.get(i).getMaxProfitPips()));
			}
		}else{
			for (int i=0;i<this.looseTrades.size();i++){
				array.add(Math.abs(looseTrades.get(i).getMaxProfitPips()));
			}
		}
		return array;
	}
	
	public ArrayList<Double> getDDArray(boolean wins){
		ArrayList<Double> array = new ArrayList<Double>();
		
		if (wins){
			for (int i=0;i<this.winTrades.size();i++){
				array.add(winTrades.get(i).getMaxDDPips());
			}
		}else{
			for (int i=0;i<this.looseTrades.size();i++){
				array.add(looseTrades.get(i).getMaxDDPips());
			}
		}
		return array;
	}


	public void setSpread(double spread) {
		// TODO Auto-generated method stub
		this.spread = spread;
	}


	public double getSpread() {
		return spread;
	}

	

	public int getCurrentConsecWins() {
		return currentConsecWins;
	}


	public void setCurrentConsecWins(int currentConsecWins) {
		this.currentConsecWins = currentConsecWins;
	}


	public int getCurrentConsecLosses() {
		return currentConsecLosses;
	}


	public void setCurrentConsecLosses(int currentConsecLosses) {
		this.currentConsecLosses = currentConsecLosses;
	}

	

	public double getTotalPipsRisked() {
		return totalPipsRisked;
	}


	public void setTotalPipsRisked(double totalPipsRisked) {
		this.totalPipsRisked = totalPipsRisked;
	}


	public void addWins(Position pos) {
		// TODO Auto-generated method stub
		Position posNew = new Position();
		posNew.copy(pos);
		this.winTrades.add(posNew);
	}


	public void addLosses(Position pos) {
		// TODO Auto-generated method stub
		Position posNew = new Position();
		posNew.copy(pos);
		this.looseTrades.add(posNew);
	}
	
	public double getExpectancy(){
		
		return (this.totalwinPips-this.totallossPips)/this.totalPipsRisked;
	}
	
	public void addTrades(Position pos){
		Position posNew = new Position();
		posNew.copy(pos);
		this.allTrades.add(posNew);
	}

	public ArrayList<Position> getAllTrades() {
		return allTrades;
	}
	public void setAllTrades(ArrayList<Position> allTrades) {
		this.allTrades = allTrades;
	}
	public double getMaxPatrimonio() {
		return maxPatrimonio;
	}
	public void setMaxPatrimonio(double maxPatrimonio) {
		this.maxPatrimonio = maxPatrimonio;
	}


	public void setMaxInitialBalance(double maxInitialBalance) {
		this.maxInitialBalance = maxInitialBalance;
	}


	public double getMaxInitialBalance() {
		// TODO Auto-generated method stub
		return this.maxInitialBalance;
	}

	

	public double getAverageTrade$() {
		return averageTrade$;
	}

	
	
	public double getMaxCloseOutPer() {
		return maxCloseOutPer;
	}


	public void setMaxCloseOutPer(double maxCloseOutPer) {
		this.maxCloseOutPer = maxCloseOutPer;
	}


	public void setAverageTrade$(double averageTrade$) {
		this.averageTrade$ = averageTrade$;
	}


	public void accTotals(StrategySummary strat) {
		// TODO Auto-generated method stub
		this.patrimonio+=strat.patrimonio;
		this.averageTrade=(this.averageTrade*this.totalTrades+strat.averageTrade*strat.totalTrades)/(this.totalTrades+strat.totalTrades);
		this.totalTrades+=strat.totalTrades;
		this.totalWins+=strat.totalWins;
		this.totalLosses+=strat.totalLosses;
		this.totalwinPips+=strat.totalwinPips;
		this.totallossPips+=strat.totallossPips;
		this.grossProfit+=strat.grossProfit;
		this.grossLoss+=strat.grossLoss;
		
	}	
	
	public void addPatrimonio(double amount){
		this.patrimonio+=amount;
		
		if (this.maxPatrimonio<=this.patrimonio)
			this.maxPatrimonio=this.patrimonio;
		else{
			double diff= this.maxPatrimonio-this.patrimonio;
			double per=100.0-this.patrimonio*100.0/this.maxPatrimonio;
			if (diff>this.maxCloseOutDD)
				this.maxCloseOutDD=diff;
			if (per>this.maxCloseOutPer)
				this.maxCloseOutPer=per;
		}
	}

	
	
	public double getFirstBalance() {
		return firstBalance;
	}


	public void setFirstBalance(double firstBalance) {
		this.firstBalance = firstBalance;
	}


	public double getScore() {
		double patrChange= (this.patrimonio*100.0/this.firstBalance)-100.0;
		double score = this.CAGR/this.getMaxCloseOutDD();
		//double score = this.patrimonio;//para probar
		//double score = this.AAR/this.getMaxCloseOutDD();
		return score;
	}

	public void tradeDistribution(int lowRange,int highRange){
		//0-50 51-100 101-150 151-200 201-250 251-300 301-350 351-400 401-450 451-500 501-550 +551
		ArrayList<String> posRangeStr = new  ArrayList<String>();
		posRangeStr.add("0 to +50");
		posRangeStr.add("51 to +100");
		posRangeStr.add("101 to +150");
		posRangeStr.add("151 to +200");
		posRangeStr.add("201 to +250");		
		posRangeStr.add("251 to +300");
		posRangeStr.add("301 to +350");
		posRangeStr.add("351 to +400");
		posRangeStr.add("401 to +450");
		posRangeStr.add("451 to +500");		
		posRangeStr.add("501 to +550");
		posRangeStr.add("551 to +600");
		posRangeStr.add("601 to +650");
		posRangeStr.add("651 to +700");		
		posRangeStr.add("701 to +750");
		ArrayList<Integer> pipRangePos = new ArrayList<Integer> ();
		ArrayList<Integer> pipRangeAcc = new ArrayList<Integer> ();
		//-1 -50 | -51 -100 | -101 -151
		ArrayList<Integer> pipRangeNeg = new ArrayList<Integer> ();
		for (int i=1;i<=12;i++){
			pipRangePos.add(0);
			pipRangeAcc.add(0); 
			pipRangeNeg.add(0);
		}
				
		for (int i=0;i<this.winTrades.size();i++){
			Position pos = winTrades.get(i);
			if (pos.getPips()>=0){
				int pips = (int) pos.getPips();
				int initPos = (int) (pips/50)-1;
				int mod = (int) (pips%50);
				if (mod>0 || pips==0)
					initPos++;
				if (initPos>11) initPos=11;
				//System.out.println("pips tam: "+pips+" "+initPos+" "+pipRangePos.size());
				int pipsActual = pipRangePos.get(initPos);
				int accActual = pipRangeAcc.get(initPos);
				pipRangePos.set(initPos, pipsActual+pips);
				pipRangeAcc.set(initPos, accActual+1);				
			}			
		}
		
		/*for (int i=0;i<this.looseTrades.size();i++){
			Position pos = looseTrades.get(i);
			if (pos.getPips()<0){
				int pips = (int) Math.abs(pos.getPips());
				int initPos = (int) (pips/50)-1;
				int mod = (int) (pips%50);
				if (mod>0)
					initPos++;
				if (initPos>11) initPos=11;
				int pipsActual = pipRangePos.get(initPos);
				pipRangeNeg.set(initPos, pipsActual+(int)pos.getPips());
			}			
		}*/
		
		for (int i=0;i<pipRangePos.size();i++){
			double per = pipRangeAcc.get(i)*100.0/winTrades.size();
			System.out.println(posRangeStr.get(i)+" "+pipRangeAcc.get(i)+" "+PrintUtils.Print(per)+"% "+pipRangePos.get(i));
		}
		
	}

	public void fullReport(String header,String tail, int level) {
		// TODO Auto-generated method stub
		double patrChange= (this.patrimonio*100.0/this.firstBalance)-100.0;		
		if (level==0){
			System.out.println(header
					+" "
					+PrintUtils.Print(this.getPatrimonio())
					+" "+PrintUtils.Print(this.CAGR)
					+" "+this.getTotalTrades()
					+" "+PrintUtils.Print(Math.abs(this.getGrossProfit()/this.getGrossLoss()))
					+" "+PrintUtils.Print(this.getTotalwinPips()-this.getTotallossPips())
					+" "+PrintUtils.Print(this.getTotalWins()*100.0/this.getTotalTrades())+"%"				
					+" "+PrintUtils.Print(this.getTotalWins()*1.0/this.getTotalLosses())
					+" "+PrintUtils.Print(this.getAverageTrade())
					+" "+PrintUtils.Print(this.getPatrimonio()/this.getTotalTrades())+"$"
					+" "+PrintUtils.Print(this.getMaxCloseOutDD())
					+" "+PrintUtils.Print(this.getScore())
					+" conW conL "+this.getMostConsecWins()+" "+this.getMostConsecLosses()
					);
		}else if (level==1){
			System.out.println(header
					+" "
					+PrintUtils.Print(this.getPatrimonio())
					+" "+PrintUtils.Print(patrChange)+"%"
					+" "+PrintUtils.Print(this.AAR)+"%"
					+" "+PrintUtils.Print(this.CAGR)+"%"
					+" "+this.getTotalTrades()
					+" "+PrintUtils.Print(this.totalwinPips-this.totallossPips)
					+" "+PrintUtils.Print(this.getAverageTrade())
					+" "+PrintUtils.Print(Math.abs(this.getGrossProfit()/this.getGrossLoss()))					
					+" "+PrintUtils.Print(this.getMaxCloseOutDD())+"%"
					+" "+PrintUtils.Print(this.getScore())					
					+" "+tail
					);
		}
	}


	public double getCAGR() {
		return CAGR;
	}


	public void setCAGR(double cAGR) {
		CAGR = cAGR;
	}


	public double getAAR() {
		return AAR;
	}


	public void setAAR(double aAR) {
		AAR = aAR;
	}


	public void calculateStats(double AARacc, int numYears) {
		// TODO Auto-generated method stub
		double exp = 1.0/numYears;
		//System.out.println(this.patrimonio/this.firstBalance+" "+exp+" "+Math.pow(this.patrimonio/this.firstBalance,exp));
		this.CAGR = (Math.pow(this.patrimonio/this.firstBalance,exp)-1.0)*100.0;
		this.AAR = AARacc/numYears;
		//System.out.println(this.CAGR);
	}
}
