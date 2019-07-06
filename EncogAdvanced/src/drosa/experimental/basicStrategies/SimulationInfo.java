package drosa.experimental.basicStrategies;

import java.util.ArrayList;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class SimulationInfo {
	
	double additionalBalance = 0;
	double initialBalance = 0;
	double maxBalance = 0;
	double balance = 0;
	double equitity = 0;
	double margin = 0;
	double freeMargin = 0;
	double marginLevel = 0;
	int leverage = 0;
	double maxDD = 0;
	
	double wins$$ = 0.0;
	double losses$$ = 0.0;
	int wins = 0;
	int losses = 0;
	int fullLosses = 0;
	double minMarginLevel = 99999999;
	double comm = 00; 
	int accStages = 0;
	ArrayList<Integer> stagesArr = new ArrayList<Integer>();
	
	ArrayList<Integer> ordenStages = new ArrayList<Integer>();
	ArrayList<Integer> ordenTotal = new ArrayList<Integer>();
	ArrayList<Double> ordenWins$$ = new ArrayList<Double>();
	ArrayList<Double> ordenLosses$$ = new ArrayList<Double>();
	
	public SimulationInfo(double aBalance,int aLeverage){
		initialBalance = aBalance;
		leverage = aLeverage;
		balance = aBalance;
		equitity = aBalance;
		maxBalance = balance;
		margin=0;
		freeMargin = aBalance;
		
		for (int i=0;i<=2000;i++){
			ordenStages.add(0);
			ordenTotal.add(0);
			ordenWins$$.add(0.0);
			ordenLosses$$.add(0.0);
		}
	}
	
	
						
	public double getAdditionalBalance() {
		return additionalBalance;
	}






	public void setAdditionalBalance(double additionalBalance) {
		this.additionalBalance = additionalBalance;
	}






	public int getFullLosses() {
		return fullLosses;
	}






	public void setFullLosses(int fullLosses) {
		this.fullLosses = fullLosses;
	}






	public ArrayList<Integer> getOrdenStages() {
		return ordenStages;
	}






	public void setOrdenStages(ArrayList<Integer> ordenStages) {
		this.ordenStages = ordenStages;
	}






	public ArrayList<Integer> getOrdenTotal() {
		return ordenTotal;
	}






	public void setOrdenTotal(ArrayList<Integer> ordenTotal) {
		this.ordenTotal = ordenTotal;
	}






	public ArrayList<Double> getOrdenWins$$() {
		return ordenWins$$;
	}






	public void setOrdenWins$$(ArrayList<Double> ordenWins$$) {
		this.ordenWins$$ = ordenWins$$;
	}






	public ArrayList<Double> getOrdenLosses$$() {
		return ordenLosses$$;
	}






	public void setOrdenLosses$$(ArrayList<Double> ordenLosses$$) {
		this.ordenLosses$$ = ordenLosses$$;
	}






	public double getInitialBalance() {
		return initialBalance;
	}






	public void setInitialBalance(double initialBalance) {
		this.initialBalance = initialBalance;
	}






	public double getMaxBalance() {
		return maxBalance;
	}






	public void setMaxBalance(double maxBalance) {
		this.maxBalance = maxBalance;
	}






	public double getBalance() {
		return balance;
	}






	public void setBalance(double balance) {
		this.balance = balance;
	}






	public double getEquitity() {
		return equitity;
	}






	public void setEquitity(double equitity) {
		this.equitity = equitity;
	}






	public double getMargin() {
		return margin;
	}






	public void setMargin(double margin) {
		this.margin = margin;
	}






	public double getFreeMargin() {
		return freeMargin;
	}






	public void setFreeMargin(double freeMargin) {
		this.freeMargin = freeMargin;
	}






	public double getMarginLevel() {
		return marginLevel;
	}






	public void setMarginLevel(double marginLevel) {
		this.marginLevel = marginLevel;
	}






	public int getLeverage() {
		return leverage;
	}






	public void setLeverage(int leverage) {
		this.leverage = leverage;
	}






	public double getMaxDD() {
		return maxDD;
	}






	public void setMaxDD(double maxDD) {
		this.maxDD = maxDD;
	}






	public double getWins$$() {
		return wins$$;
	}






	public void setWins$$(double wins$$) {
		this.wins$$ = wins$$;
	}






	public double getLosses$$() {
		return losses$$;
	}






	public void setLosses$$(double losses$$) {
		this.losses$$ = losses$$;
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






	public double getMinMarginLevel() {
		return minMarginLevel;
	}






	public void setMinMarginLevel(double minMarginLevel) {
		this.minMarginLevel = minMarginLevel;
	}






	public double getComm() {
		return comm;
	}






	public void setComm(double comm) {
		this.comm = comm;
	}






	//tpPips en pips
	public int getPositionMicroLots(int entry,double tpPips,double riskTarget){
		
		int microLots = -1;
		
		if (marginLevel<100.0) return 0;//solo se permite abrir operaciones si marginLevel>=100.0
		
		double maxMicroLots = 100*((freeMargin*leverage)/(entry));//100*numero de lotes, 1 lot = 100 microlots
		
		double riskTarget$$ = equitity*riskTarget/100.0;
		microLots = (int) ((riskTarget$$*10)/tpPips);
		
		if (microLots>maxMicroLots) return 0;
		
		//al menos 1
		if (microLots==0) microLots = 1;
		
		//fixed en debug
		//microLots = 1;
				
		//se decuenta el margen cuando se abre
		double marginPos =( (entry/100000.0)*microLots*1000.0)/this.leverage;
		margin 		+= marginPos;//el margen se ve afectado	
		freeMargin	-= marginPos;
				
		return microLots;
	}
	
	public int getPositionMicroLotsAcc(int entry,double tpPips, double target$$,boolean isAddIn,int debug) {
		int microLots = -1;
		
		if (margin>0){
			marginLevel = (equitity*100.0)/margin;
			
			if (debug==1){
				System.out.println("[getPositionMicroLots] marginLevel"
							+" "+PrintUtils.Print2dec(equitity, false)	
							+" "+PrintUtils.Print2dec(margin, false)	
							+" "+PrintUtils.Print2dec(marginLevel, false)					
							);
			}
			
			if (marginLevel<100.0){
				if (!isAddIn) return 0;
				//agrego lo necesario
				double eqNeeded = (marginLevel*margin)/100.0;
				double addi = eqNeeded-equitity;
				additionalBalance +=addi;
				balance += addi;	
				equitity += addi;
				freeMargin = equitity-margin;
			}
		}
		
		double maxMicroLots = 100*((freeMargin*leverage)/(entry));//100*numero de lotes, 1 lot = 100 microlots
		
		microLots = (int) Math.ceil((target$$*10)/(tpPips));
		
		//if (microLots>maxMicroLots) microLots=(int) maxMicroLots;
		
		if (microLots>maxMicroLots){
			if (!isAddIn) return 0;
			
			//necesitamos un minimo de incremento de capital o asumir perdidas
			double amountNeed = ((microLots*0.01)*100000.0)/leverage;
			double addi = amountNeed-freeMargin;
			additionalBalance +=addi;
			balance += addi;	
			equitity += addi;
			freeMargin = equitity-margin;
		}
		
		//se decuenta el margen cuando se abre
		double marginPos = ((entry/100000.0)*microLots*1000.0)/this.leverage;
		margin 		+= marginPos;//el margen se ve afectado	
		freeMargin	-= marginPos;
		
		if (debug==1){
			System.out.println("[getPositionMicroLots] "
						+microLots+" "+PrintUtils.Print2dec(target$$, false)					
						);
		}
				
		return microLots;
	}
	
		
	//Actualizamos una posicion a cerrada
	public void updateClosed(String msg,Transaction t,int microLots,int miniPips,
			double accLosses,
			int fullLoss,
			int debug){
		double prevBalance = balance;		
		double amount = microLots*0.1*(miniPips-comm)*0.1+accLosses;//comision es el -20	
		double marginPos = (microLots*1000.0)/leverage;
		
		balance		+= amount;//solo cambia cuando se cierra
		margin		-= marginPos;
		freeMargin  = equitity-margin;
		String resStr = "WIN";
		
		double ordenWin$$= this.ordenWins$$.get(t.getOrden());
		double ordenLoss$$= this.ordenLosses$$.get(t.getOrden());
		if (amount>=0){
			wins$$ += amount;
			wins++;
			
			this.ordenWins$$.set(t.getOrden(), ordenWin$$+amount);
		}else{
			losses$$ += -amount;
			losses++;
			
			this.ordenLosses$$.set(t.getOrden(), ordenLoss$$-amount);
			
			resStr = "LOSS";
		}
		//debug=1;
		if (fullLoss==1){
			fullLosses++;
			resStr+=" FULL";
		}
		
		
		
		if (balance>=maxBalance){
			maxBalance = balance;
		}else{
			double dd = 100.0-balance*100.0/maxBalance;
			if (dd>=maxDD) maxDD = dd;
		}
		
		//añadimos pasta si cae por debajo de un umbral
		if (balance<=initialBalance*0.10){
			double addi = initialBalance*0.10-balance;
			additionalBalance +=addi;
			balance += addi;
			equitity += addi;
			freeMargin = equitity-margin;
		}
		
		if (debug==1){
			String marginlevelStr = "---";
			if (margin>0)
				marginlevelStr = PrintUtils.Print2dec((equitity*100.0)/margin,false);
			
			System.out.println("[UPDATED CLOSED "+resStr+" ] "+debug+" ||| "
						+msg+" | "
						+"mc="+microLots+" mp="+miniPips
						+" amount="+PrintUtils.Print2dec(microLots*0.1*(miniPips-comm)*0.1, false)
						+" accLosses="+PrintUtils.Print2dec(accLosses, false)
						+" || "
						+" pb="+PrintUtils.Print2dec(prevBalance, false)
						+" b="+PrintUtils.Print2dec(balance, false)
						+" e="+PrintUtils.Print2dec(equitity, false)
						+" m="+PrintUtils.Print2dec(margin, false)
						+" fm="+PrintUtils.Print2dec(freeMargin, false)
						+" ml="+marginlevelStr
						);
		}
		
		int actualStages = this.ordenStages.get(t.getOrden());
		int totalStages = this.ordenTotal.get(t.getOrden());
		
		this.ordenStages.set(t.getOrden(), actualStages+t.getStage());
		this.ordenTotal.set(t.getOrden(), totalStages+1);
		
		accStages+=t.getStage();
		stagesArr.add(t.getStage());
	}
	
	public void updateFloating(int microLots,int miniPips,int debug){
		
		double amount = microLots*0.1*miniPips*0.1;		
		double marginPos = (microLots*1000.0)/leverage;
		
		equitity	+= amount;					
		margin 		+= marginPos;
		freeMargin  = equitity-margin;//el margen tambien se ve afactado por lo ganado o perdido
		
		if (margin>0){
			marginLevel = (equitity*100.0)/margin;
			
			if (marginLevel<=this.minMarginLevel) minMarginLevel = marginLevel;
		}
		
		if (debug==1){
			System.out.println("[updateFloating] "
						+microLots+" "+miniPips
						+" || "
						+" "+PrintUtils.Print2dec(equitity, false)
						+" "+PrintUtils.Print2dec(margin, false)
						+" "+PrintUtils.Print2dec(freeMargin, false)
						);
		}
	}

	public void reset() {
		this.equitity = balance;
		this.freeMargin = balance;
		this.margin = 0;		
	}
	
	public void updateInfo(ArrayList<Transaction> transactions, int value) {
		
		this.equitity = balance;
		this.freeMargin = balance;
		this.margin = 0;
		int netMicroLots = 0;
		//1) sumamos posiciones consolidadas
		for (int i=0;i<transactions.size();i++){
			Transaction t = transactions.get(i);
			//primero sumamos posiciones consolidadas
			//balance += t.getNetPosition();//solo cuando se cierra la transaccion
			equitity += t.getNetPosition();
			if (t.getActualMode()==1){
				netMicroLots += t.getCurrentMicroLots();
			}else if (t.getActualMode()==-1){
				netMicroLots -= t.getCurrentMicroLots();
			}
		}
		
		//2) calculo de equitity posiciones flotantes
		for (int i=0;i<transactions.size();i++){
			Transaction t = transactions.get(i);
			double eqActual = 0.0;
			
			if (t.getActualMode()==1){
				eqActual = (value-t.getRefH())*0.1*t.getCurrentMicroLots()*0.1;
			}else if (t.getActualMode()==-1){
				eqActual = (t.getRefL()-value)*0.1*t.getCurrentMicroLots()*0.1;
			}
			//primero sumamos posiciones consolidadas
			equitity += eqActual;
		}
		//3) Calculo de margin
		if (netMicroLots==0){
			margin		= 0.0;
			freeMargin 	= equitity;
		}else{
			if (netMicroLots>0){
				int microLeft = netMicroLots;
				for (int i=transactions.size()-1;i>=0;i--){
					Transaction t = transactions.get(i);					
					if (t.getActualMode()==1){
						if (t.getCurrentMicroLots()>=microLeft){
							double marginPos = ((t.getRefH()/100000.0)*t.getCurrentMicroLots()*1000.0)/this.leverage;
							this.margin += marginPos;
							break;
						}else{
							double marginPos = ((t.getRefH()/100000.0)*t.getCurrentMicroLots()*1000.0)/this.leverage;
							this.margin += marginPos;
							microLeft -= t.getCurrentMicroLots();
						}
					}		
					if (microLeft==0) break;
				}
			}else if (netMicroLots<0){
				int microLeft = -netMicroLots;
				for (int i=transactions.size()-1;i>=0;i--){
					Transaction t = transactions.get(i);					
					if (t.getActualMode()==-1){
						if (t.getCurrentMicroLots()>=microLeft){
							double marginPos = ((t.getRefL()/100000.0)*t.getCurrentMicroLots()*1000.0)/this.leverage;
							this.margin += marginPos;
							break;
						}else{
							double marginPos = ((t.getRefL()/100000.0)*t.getCurrentMicroLots()*1000.0)/this.leverage;
							this.margin += marginPos;
							microLeft -= t.getCurrentMicroLots();
						}
					}		
					if (microLeft==0) break;
				}
			}
		}	
		//4) freeMargin y marginLevel
		freeMargin = equitity-margin;
		marginLevel =999999.9;
		if (margin>0){
			marginLevel= (equitity*100.0)/margin; 
		}
	}
	
	public String getAccountInfo(){
		
		return ""
		+" ib="+PrintUtils.Print2dec2(initialBalance+additionalBalance, true)
		+" b="+PrintUtils.Print2dec2(balance, true)
		+" e="+PrintUtils.Print2dec2(equitity, true)
		+" m="+PrintUtils.Print2dec2(margin, true)
		+" fm="+PrintUtils.Print2dec2(freeMargin, true)
		+" ml="+PrintUtils.Print2dec2(marginLevel, true)
		;
	}

	public String getReport() {
		// TODO Auto-generated method stub
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = wins$$/losses$$;
		double profitPer = balance*100.0/(initialBalance+additionalBalance)-100.0;
		double avgStage = MathUtils.average(stagesArr);
		double dtStage = Math.sqrt(MathUtils.variance(stagesArr));
		return ""+total
				+" "+wins+" "+losses+" "+fullLosses
				+" "+PrintUtils.Print2dec(winPer, false)
		+" "+PrintUtils.Print2dec2(initialBalance+additionalBalance, true)
		+" "+PrintUtils.Print2dec2(balance, true)
		+" "+PrintUtils.Print2dec(pf, false)
		+" || "+PrintUtils.Print2dec(avgStage, false)+" "+PrintUtils.Print2dec(dtStage, false)
		//+" || "+PrintUtils.Print2dec(wins$$, false)+" "+PrintUtils.Print2dec(losses$$, false)
		+" || "+PrintUtils.Print2dec(profitPer, false)
		+" || "+PrintUtils.Print2dec(maxDD, false)
		+" || "+PrintUtils.Print2dec(profitPer/maxDD, false)
		+" || "+PrintUtils.Print2dec(minMarginLevel, false)
		;
	}
	
	public void recalculateMargin(ArrayList<Transaction> ts){
		
		int netMicroLots = 0;
		for (int i=0;i<ts.size();i++){
			Transaction t = ts.get(i);
			
			if (t.getActualMode()==1){
				netMicroLots += t.getCurrentMicroLots();
			}else{
				netMicroLots += -t.getCurrentMicroLots();
			}
		}
		
		if (netMicroLots==0){
			freeMargin = equitity;
			margin = 0;
		}else{
			if (netMicroLots>0){
				int microLotsLeft = netMicroLots;
				for (int i=ts.size()-1;i>=0;i--){
					Transaction t = ts.get(i);
					if (t.getActualMode()==1){
						if (t.getCurrentMicroLots()>=microLotsLeft){
							//margin = t.get
							break;
						}
					}
				}
			}else if (netMicroLots<0){
				
			}
		}
	}

	public void printOrdenStats(int n){
		
		
		for (int i=0;i<=n;i++){
			int stages = this.ordenStages.get(i);
			int total = this.ordenTotal.get(i);
			double ordenWin$$= this.ordenWins$$.get(i);
			double ordenLoss$$= this.ordenLosses$$.get(i);
			if (total>0){
				System.out.println(i
						+" || "+total
						+" "+PrintUtils.Print2dec(stages*1.0/total,false)
						+" "+PrintUtils.Print2dec(ordenWin$$/ordenLoss$$,false)
						);
			}else{
				System.out.println(i+" || ---");
			}
		}
		
	}

	
	

}
