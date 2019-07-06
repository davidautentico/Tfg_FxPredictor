package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;

public class SystemStats {

	double initialCapital = 0;
	double balance		= 0;
	double grossProfit  = 0;
	double grossLoss    = 0;
	double grossProfitNoComm  = 0;
	double grossLossNoComm    = 0;
	double maxBalance	= 0;
	double equitity		= 0;
	double maxEquitity	= 0;
	double amountNeeded = 0;
	double margin 		= 0;
	double marginLevel	= 0;
	double freeMargin   = 0;
	double maxDD		= 0;
	double comm         = 0;
	ArrayList<Double> riskPerTrades = null;
	int brokerLeverage  = 0;
	int tp				= 0;
	int sl				= 0;
	CurrencyType  currencyType = CurrencyType.USD_BASED;
	int maxConcurrent 	= 0;
	int openDiff 		= 0;
	int totalTrades 	= 0;
	int totalWins 		= 0;
	int totalLosses 	= 0;
	long totalPips      = 0;
	int movedBEpips     = 0;
	double movedTpPips		= 0;
	
	boolean digits5 = false;
	
	ArrayList<PositionShort> positions = null;
	
	public SystemStats(double balance,int brokerLeverage,
			int maxConcurrent,int openDiff,
			double comm,CurrencyType currencyType,int tp,int sl,int movedBEpips,double movedTpPips,boolean digits5){
		
		this.initialCapital = balance;
		this.balance  = balance;
		this.maxBalance = balance;
		this.equitity = balance;
		this.maxEquitity = balance;
		this.freeMargin = balance;
		this.amountNeeded = balance;
		this.maxConcurrent = maxConcurrent;
		this.totalTrades = 0;
		this.totalWins = 0;
		this.totalLosses = 0;
		this.grossProfit = 0;
		this.grossLoss = 0;
		//this.riskPerTrades = riskPerTrades;
		this.brokerLeverage = brokerLeverage;
		this.openDiff = openDiff;
		this.comm = comm;
		this.currencyType = currencyType;
		this.tp = tp;
		this.sl = sl;
		this.maxDD = 0;
		this.digits5 = digits5;
		this.movedBEpips = movedBEpips;
		this.movedTpPips = movedTpPips;
		
		//System.out.println("balance "+this.balance);
	}
			
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}




	public double getMaxEquitity() {
		return maxEquitity;
	}

	public void setMaxEquitity(double maxEquitity) {
		this.maxEquitity = maxEquitity;
	}

	public double getEquitity() {
		return equitity;
	}




	public void setEquitity(double equitity) {
		this.equitity = equitity;
	}




	public double getAmountNeeded() {
		return amountNeeded;
	}




	public void setAmountNeeded(double amountNeeded) {
		this.amountNeeded = amountNeeded;
	}




	public double getMargin() {
		return margin;
	}




	public void setMargin(double margin) {
		this.margin = margin;
	}




	public double getMarginLevel() {
		return marginLevel;
	}




	public void setMarginLevel(double marginLevel) {
		this.marginLevel = marginLevel;
	}




	public int getMaxConcurrent() {
		return maxConcurrent;
	}




	public void setMaxConcurrent(int maxConcurrent) {
		this.maxConcurrent = maxConcurrent;
	}




	public int getTotalTrades() {
		return totalTrades;
	}




	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}




	public int getTotalWins() {
		return totalWins;
	}




	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}




	public int getTotalLosses() {
		return totalLosses;
	}




	public void setTotalLosses(int totalLosses) {
		this.totalLosses = totalLosses;
	}



	public double getComm() {
		return comm;
	}

	public void setComm(double comm) {
		this.comm = comm;
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

	public int getBrokerLeverage() {
		return brokerLeverage;
	}

	public void setBrokerLeverage(int brokerLeverage) {
		this.brokerLeverage = brokerLeverage;
	}

	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}

	public int getSl() {
		return sl;
	}

	public void setSl(int sl) {
		this.sl = sl;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	public int getOpenDiff() {
		return openDiff;
	}

	public void setOpenDiff(int openDiff) {
		this.openDiff = openDiff;
	}



	public double getInitialCapital() {
		return initialCapital;
	}

	public void setInitialCapital(double initialCapital) {
		this.initialCapital = initialCapital;
	}

	public double getFreeMargin() {
		return freeMargin;
	}

	public void setFreeMargin(double freeMargin) {
		this.freeMargin = freeMargin;
	}

	public ArrayList<Double> getRiskPerTrades() {
		return riskPerTrades;
	}

	public void setRiskPerTrades(ArrayList<Double> riskPerTrades) {
		this.riskPerTrades = riskPerTrades;
	}

	public boolean isDigits5() {
		return digits5;
	}

	public void setDigits5(boolean digits5) {
		this.digits5 = digits5;
	}

	public ArrayList<PositionShort> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<PositionShort> positions) {
		this.positions = positions;
	}

	public void update(ArrayList<PositionShort> positions,
			QuoteShort q,long qIndex, StatsDebugOptions debug){
	
		double actualProfit = 0.0;
		double actualMargin = 0.0;
		boolean anyClosed = false;
		Calendar cal = Calendar.getInstance();		
		QuoteShort.getCalendar(cal, q);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		int qClose = q.getHigh();
		int qLow = q.getLow();
		int qHigh = q.getHigh();
		int qOpen = q.getOpen();
		int tpA = tp;
		int slA = sl;
		double commA = comm;
		if (digits5){
			qLow = q.getLow5();
			qHigh = q.getHigh5();
			qOpen = q.getOpen5();
			qClose = q.getClose5();
			tpA = tp*10;
			slA = sl*10;
			commA = comm*10;
		}
		
		double rate = 0;
		double lastBalance = balance;
		boolean pendingOpen = false;	
		double riskPerTrade = -1;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);	
			riskPerTrade = p.getRisk();
			if (p.getPositionStatus()==PositionStatus.PENDING){	
				long openDiff = qIndex-p.getPendingIndex();
				//System.out.println(p.getPositionStatus()+" "+openDiff);
				if (qLow<=p.getEntry() && p.getEntry()<=qHigh
						&& SystemStats.getTotalStatusPositions(positions,PositionStatus.OPEN)<maxConcurrent
						&& openDiff<=this.openDiff
						//&& openDiff<=p.getOpenDiff()
						//&& h>=0 && h<=9
						){	
					//System.out.println("ENTRADO");
					//prueba
					/*if (this.equitity<this.initialCapital){//revisar hacerlo todo con equitity
						this.amountNeeded+=(this.initialCapital-this.equitity);
						this.balance+=(this.initialCapital-this.equitity);
						this.equitity+=(this.initialCapital-this.equitity);						
					}*/
						
					int microLotSize = 1000;
					double rateB = qOpen/100000.0;
					rate = rateB;//CurrencyType.USD_BASED
					if (currencyType==CurrencyType.USD_FIRST){
						rate = 1.0/(rateB);
					}
					if (currencyType==CurrencyType.CROSS){
						rate = 1.30/rateB;//consideramos 1.30 estandar para EUR/USD
					}					
					//FIRST METHOD
					double amount1lotMargin = (1*microLotSize*rate/brokerLeverage);//lo que cuestan 2 microlot con margen 1:leverage(leverage del broker)					
					double minBalanceNeeded1 = amount1lotMargin*maxConcurrent;//min Balance para comprar maxConcurrent microLotes							
					double totalRisk        = maxConcurrent*riskPerTrade; //riesgo total de todas las posiciones
					if (totalRisk>=100.0){
						riskPerTrade = 100.0/maxConcurrent; //riesgo de cada posicion
					}
					double risk1lot = 1*0.1*sl;
					double minBalanceNeeded2 = (risk1lot*totalRisk/riskPerTrade)*100/totalRisk;//?¿revisar  //2lotes x maxConcurrentPos					
					double minBalanceNeeded  = minBalanceNeeded1;
					if (minBalanceNeeded2>minBalanceNeeded) minBalanceNeeded = minBalanceNeeded2;
					//
				
					//lo que falta para usar maxConcurrent microlot
					double amountTradeNeed = minBalanceNeeded-this.balance;
					if (amountTradeNeed>0){
						this.balance+=amountTradeNeed;
						this.amountNeeded+=amountTradeNeed;
						if (debug==StatsDebugOptions.COMPLETE_DEBUG)
						System.out.println("amount1lot amountTradedNeed: "
								+" bal1Need="+PrintUtils.Print2(minBalanceNeeded1)
								+" bal2Need="+PrintUtils.Print2(minBalanceNeeded2)
								+" "+PrintUtils.Print2(amountTradeNeed)
								);
					}
					//calculamos numero maximo de microlots que se pueden comprar en margen
					long totalMaxMicros = (long) (balance /(amount1lotMargin)); //max num microlots comprados a margen
					//a cuanto toca por posicion
					long totalMaxMicrosPerPos = totalMaxMicros / maxConcurrent; //por posicion
					//numero maximo de microlots usando SL y resptenado el totalRisk% del balance
					long microsSLPerPos = (long) ((this.balance*totalRisk/100.0) /(maxConcurrent*sl*0.1)); //usando el SL
					
					double minBalance = 1*maxConcurrent*sl*0.1*100/totalRisk;
					
					if (debug==StatsDebugOptions.COMPLETE_DEBUG)
					System.out.println("balance= "+PrintUtils.Print2(balance)
							+" maxMarginPerPos="+totalMaxMicrosPerPos
							+" maxSLPerPos = "+microsSLPerPos
							+" minBalance = "+minBalance
							+" result = "+PrintUtils.Print2((this.balance*totalRisk/100.0) /(maxConcurrent*sl*0.1))
							);
					//se coge la menor
					if (totalMaxMicrosPerPos<microsSLPerPos) microsSLPerPos = totalMaxMicrosPerPos; //usamos la menor
					
					if (microsSLPerPos>0){		
						//System.out.println("OPEN");
						p.setPositionStatus(PositionStatus.OPEN);
						p.setMicroLots(microsSLPerPos);
						p.setOpenIndex(qIndex);
						p.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						double marginPos = 1000*p.getMicroLots()*(rate)/brokerLeverage;
						p.setMargin(marginPos);
						pendingOpen = true;
						if (debug==StatsDebugOptions.COMPLETE_DEBUG){
							String extra = "";
							extra = "";							
							System.out.println(p.toString()
									+" margin= "+PrintUtils.Print2(marginPos)									
									);
						}
					}
				}
			}
			
			if (p.getPositionStatus()==PositionStatus.OPEN){		
				//System.out.println(q.toString());				
				boolean closed = false;
				double pipsWin = 0;
				int closeDiff = 0;
				int movedTPPips = (int) (this.sl*this.movedTpPips);
				if (p.getPositionType()==PositionType.LONG){
					int be      = p.getEntry()+this.movedBEpips*10;
					int tpMoved = p.getEntry()-movedTPPips*10;
					closeDiff = (qHigh-p.getEntry());					
					if (qLow<=p.getSl()){//SL
						closed = true;
						pipsWin = p.getSl()-p.getEntry();
					}else if (qHigh>=p.getTp() && !pendingOpen){//TP
						closed = true;
						pipsWin = p.getTp()-p.getEntry();
					}
					else if (qClose>=p.getTp() && pendingOpen){//TP
						closed = true;
						pipsWin = p.getTp()-p.getEntry();
					}else if (qClose>=be && this.movedBEpips>=4){//movemos a BE+1 el SL
						p.setSl(p.getEntry()+20);
					}else if (qLow<=tpMoved && qClose<=p.getEntry() && movedTPPips>=2){
						p.setTp(p.getEntry()+20);
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					//System.out.println("OPEN SHORT TO CHECK: "+p.toString2());
					int be = p.getEntry()-this.movedBEpips*10;
					int tpMoved = p.getEntry()+movedTPPips*10;
					closeDiff = (p.getEntry()-qLow);
					if (qHigh>=p.getSl()){//SL
						closed  = true;
						pipsWin = p.getEntry()-p.getSl();
					}else if (qLow<=p.getTp() && !pendingOpen){//TP
						closed  = true;
						pipsWin = p.getEntry()-p.getTp();
					}
					else if (qClose<=p.getTp() && pendingOpen){//TP
						closed = true;
						pipsWin = p.getEntry()-p.getTp();
					}else if (qClose>=be && this.movedBEpips>=4){//movemos a BE+1 el SL
						p.setSl(p.getEntry()-20);
					}else if (qHigh>=tpMoved && qClose>=p.getEntry() && movedTPPips>=2){
						p.setTp(p.getEntry()-20);
					}
				}
				if (closed){
					//System.out.println("CLOSE");
					p.setPositionStatus(PositionStatus.CLOSE);
					p.setCloseIndex(qIndex);
					p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
					totalTrades++;
					double profit = 0;	
					this.totalPips += pipsWin;
					double pipsAfterComm = pipsWin-comm*10;
					if (pipsAfterComm>=0){
						totalWins++;		
						p.setWin(1);
					}else{
						totalLosses++;
						p.setWin(0);
					}
					balance += p.getMicroLots()*0.1*(pipsAfterComm/10.0);
					profit   = p.getMicroLots()*0.1*(pipsAfterComm/10.0); 
					double profitNoComm   = p.getMicroLots()*0.1*(pipsWin/10.0);
					if (debug==StatsDebugOptions.COMPLETE_DEBUG){
						System.out.println(p.toString2()+" balance= "
								+" "+PrintUtils.Print2(lastBalance)
								+" "+PrintUtils.Print2(balance)
								+" "+PrintUtils.Print2(profit));
					}
					if (profit>=0){
						this.grossProfit += profit;
					}else{
						this.grossLoss += profit;
					}
					if (profitNoComm>=0){
						this.grossProfitNoComm += profitNoComm;
					}else{
						this.grossLossNoComm   += profitNoComm;
					}
					anyClosed = true;					
				}else{//not closed
					double margin = 1000*p.getMicroLots()*(rate)/brokerLeverage;  
					double profit = p.getMicroLots()*0.1*closeDiff*0.1;//revisar si como es closeDiff si es de 5 o de 4 digitos
					actualProfit += profit;
					actualMargin += p.getMargin();					
				}
			}
		}
		this.equitity    = this.balance+actualProfit;
		this.margin      = actualMargin;
		this.marginLevel = this.equitity*100.0/this.margin;
		this.freeMargin  = this.equitity-this.margin;
		double perWin = totalWins*100.0/totalTrades;
		
		double actualDD = 100.0-balance*100.0/maxBalance;
		if (actualDD>maxDD) maxDD = actualDD;
		if (this.balance>maxBalance) maxBalance = this.balance;
		if (this.equitity>maxEquitity) maxEquitity = this.equitity;
		
		if (this.margin>0 && marginLevel<20.0){
			if (debug==StatsDebugOptions.COMPLETE_DEBUG){
				System.out.println("MARGIN CALL: "+PrintUtils.Print2(this.equitity)+" "+
						PrintUtils.Print2(marginLevel)+"% "+PrintUtils.Print2(margin)+" "+PrintUtils.Print2(actualProfit));
			}
		}
		//if (debug && anyClosed)
		int totalOpen = getTotalStatusPositions(positions,PositionStatus.OPEN);
		if ((debug==StatsDebugOptions.COMPLETE_DEBUG && totalOpen>0 && anyClosed) 
				|| (debug==StatsDebugOptions.COMPLETE_DEBUG && totalOpen>0 && marginLevel<20.0))
			System.out.println(
					(int)(this.sl*this.movedTpPips)+" "
					+" "+PrintUtils.Print2dec2(this.balance,true)
					+" "+PrintUtils.Print2dec2(this.equitity,true)
					+" "+PrintUtils.Print2dec2(this.freeMargin,true)
					+" "+PrintUtils.Print2(this.marginLevel)+"%"
					+" "+totalTrades
					+" "+totalOpen
					+" "+PrintUtils.Print2(perWin)+"%"
					);
		if ((debug==StatsDebugOptions.ONLY_BALANCES || debug==StatsDebugOptions.SUMMARY_BALANCES) 
			&& anyClosed){
			System.out.println(PrintUtils.Print2(this.balance));
		}
	}

	public static int getTotalStatusPositions(ArrayList<PositionShort> positions,PositionStatus status) {
		// TODO Auto-generated method stub
		int count=0;
		for (int i=0;i<positions.size();i++){
			if (positions.get(i).getPositionStatus()==status)
				count++;
		}
		return count;
	}

	public void printSummary(String header) {
		// TODO Auto-generated method stub
		double perWin = totalWins*100.0/totalTrades;
		double exp = (perWin*tp-(100.0-perWin)*sl)/100.0;
		double pf = Math.abs(this.grossProfit/this.grossLoss);
		double pfNoCommissions = Math.abs(this.grossProfitNoComm/this.grossLossNoComm); 
		//double exp = ((tp*perWin)-sl*(100.0-perWin)/100.0;
			System.out.println(header
					//+" "+(int)(this.sl*this.movedTpPips)+" "
					+" "+this.maxConcurrent
					+" "+PrintUtils.Print2dec2(this.balance, true)
					+" "+PrintUtils.Print2dec2(this.grossProfit, true)
					+" "+PrintUtils.Print2dec2(Math.abs(this.grossLoss), true)
					+" "+PrintUtils.Print2dec2(this.amountNeeded, true)
					+" "+PrintUtils.Print2dec2(this.balance/this.amountNeeded,true)
					+" "+PrintUtils.Print2dec2(this.maxEquitity/this.amountNeeded,true)
					+" "+totalTrades
					+" "+PrintUtils.Print2(pf)
					//+" "+PrintUtils.Print2((perWin*tp)/((100.0-perWin)*sl))
					+" ("+PrintUtils.Print2(pfNoCommissions)+")"
					+" "+PrintUtils.Print2(perWin)+"%"
					+" "+PrintUtils.Print2(totalPips*0.1/totalTrades)
					+" "+PrintUtils.Print2dec2(maxEquitity,true)
					+" "+PrintUtils.Print2(maxDD)+"%"
					);
	}

	public void finish(ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		this.positions = positions;
	}

	public static void studyPositions(ArrayList<PositionShort> positions,int maxOpenDiff) {
		// TODO Auto-generated method stub
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=maxOpenDiff;i++) wins.add(0);
		for (int i=0;i<=maxOpenDiff;i++) totals.add(0);
		
		for (int i=0;i<positions.size();i++){
			PositionShort pos = positions.get(i);
			if (pos.getPositionStatus()!=PositionStatus.CLOSE) continue;
			long pendingIndex = pos.getPendingIndex();
			long openIndex = pos.getOpenIndex();
			
			long diff = openIndex-pendingIndex;
			int win = pos.getWin();
			
			int index = (int) diff;
			if (index>maxOpenDiff) index = maxOpenDiff;
			for (int j=0;j<=index;j++){
				int count = wins.get(j);
				int total = totals.get(j);
				if (win==1)
					wins.set(j, count+1);
				totals.set(j, total+1);
			}		
		}
		
		String res = String.valueOf(positions.size());
		for (int i=0;i<=maxOpenDiff;i++){
			double perWin = wins.get(i)*100.0/totals.get(i);
			res+=" "+PrintUtils.Print2(perWin)+"-"+totals.get(i);
		}
		System.out.println(res);
	}
	
	public static void studyPositionsHours(ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		ArrayList<Integer> wins = new ArrayList<Integer>();
		ArrayList<Integer> totals = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) wins.add(0);
		for (int i=0;i<=23;i++) totals.add(0);
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<positions.size();i++){
			PositionShort pos = positions.get(i);
			if (pos.getPositionStatus()!=PositionStatus.CLOSE) continue;
			
			int h= pos.getOpenCal().get(Calendar.HOUR_OF_DAY);
			int win = pos.getWin();
			int count = wins.get(h);
			int total = totals.get(h);
			if (win==1)
					wins.set(h, count+1);
			totals.set(h, total+1);	
		}
		
		String res = String.valueOf(positions.size());
		for (int i=0;i<=23;i++){
			double perWin = wins.get(i)*100.0/totals.get(i);
			res+=" "+PrintUtils.Print2(perWin)+"-"+totals.get(i);
		}
		System.out.println(res);
	}
	
	public static void studyPositionsSameIndex(ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		int totalSame = 0;
		int total = 0;
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<positions.size();i++){
			PositionShort pos = positions.get(i);
			if (pos.getPositionStatus()!=PositionStatus.CLOSE) continue;
			if (pos.getOpenIndex()==pos.getCloseIndex() && pos.getWin()==1) totalSame++;
			total++;
		}
		
		String res = PrintUtils.Print(totalSame)+" "+PrintUtils.Print2(totalSame*100.0/total);
	
		System.out.println(res);
	}

	public double calculateExpectancy(int tp, int sl) {
		// TODO Auto-generated method stub
		double perWin = totalWins*1.0/totalTrades;
		
		return perWin*tp-(1.0-perWin)*sl;
	}
	
	
	
}
