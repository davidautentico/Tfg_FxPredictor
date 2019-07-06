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
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestRanges {
	
	private static double testTransactionsMargin(String aHeader, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			int y1, int y2, 
			int m1,int m2,
			int h1, int h2,
			int aMin,
			int thrTp,
			int minTp,
			double maxAllowedLoss,
			int maxStages,
			int maxTransactions,
			double riskTarget,
			double riskMaxLoss,
			int aRange,	
			double aBalance,
			int leverage,
			int aComm,
			int debug) {
		
		double initialBalance = aBalance;
		double balance = initialBalance;
		double maxBalance = balance;
		double equitity = balance;
		double margin = 0.0;
		double freeMargin = equitity-margin;
		int comm = aComm;
		
		SimulationInfo si = new SimulationInfo(initialBalance,50);
								
		Calendar cal = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count = 0;
		int dayOpen = 0;

		int accBars = 0;
		int countUp = 0;
		int countDown = 0;
		int counth = 0;
		int lasth=-1;
		int lasth1=-1;
		int count5 = 0;
		int lastDay = -1;
		//int touchesThr = 3;
		ArrayList<Integer> bars = new ArrayList<Integer>();
		ArrayList<Integer> touches = new ArrayList<Integer>();
		dayOpen = 0;
		
		double totalWinSize = 0;
		double totalLossSize = 0;
		double maxAccLoss = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int high = -1;
		int low = -1;
		
		int transactionID = 0;
		ArrayList<Integer> dayTrades = new ArrayList<Integer>();
		int maxDay = (30)*365;//30 años..
		for (int i=0;i<=maxDay;i++) dayTrades.add(0);
		
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		
		int actualRef = 0;
		int actualRefH = 0;
		int actualRefL = 0;
		int refHtp = 0;
		int refLtp = 0;
		int wins = 0;
		int losses = 0;
		double winsFactor = 0.0;
		double lossFactor = 0.0;
		
		int accRanges = 0;
		int totalDays = 0;
		int lastHTrade = -1;
		int lastRefTraded = -1;
		long lastRefTimeTraded = -1;
		long actualRefTime = -1;
		double maxMicroLots = 1;
		int maxStage = 1;
		double maxAccGlobal = 0;
		int trefHigh= -1;
		int trefLow = -1;
		
		int totalMonths = 0;
		int totalYears = 0;
		double equitityYear1 = initialBalance;
		double equitityMonth1 = initialBalance;
		int totalMonthsL = 0;
		int totalYearsL = 0;
		int lastYear = -1;
		int lastMonth = -1;
		int debugUpdatedClosed = 0;
		if (debug==50 || debug==30){
			debugUpdatedClosed = 1;
		}
		boolean canChange = true;
		for (int i=1;i<data.size()-1;i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (y!=lastYear) {
				if (lastYear!=-1) {
					if (si.getBalance()<equitityYear1) {
						totalYearsL++;
					}
				}
				equitityYear1 = si.getBalance();//si.getEquitity();
				lastYear = y;
				totalYears++;
			}
			
			if (month!=lastMonth) {
				if (lastMonth!=-1) {
					if (si.getBalance()<equitityMonth1) {
						totalMonthsL++;
					}
				}
				equitityMonth1 = si.getBalance();
				lastMonth = month;
				totalMonths++;
			}
			
			if (day!=lastDay) {		
				
				if (lastDay!=-1){
					accRanges += high-low;
					totalDays++;
				}
				dayOpen = q.getOpen5();
				lasth1=-1;
				lastDay = day;
				high = -1;
				low = -1;
				
			}
			
			//buscamos la referencia
			if (h>=h1 && h<=h2
					&& min==aMin
					&& canChange
					//&& (trefHigh==-1 ||q.getOpen5()>=trefHigh+50)//la idea detras de esto es no meter muchas transacciones dentro
					//&& (trefLow==-1 || q.getOpen5()<=trefLow-50)
					){
				//referencia actual para entradas y salidas en el sistema
				actualRef	= q.getOpen5();
				actualRefH 	= q.getOpen5() + thrTp;
				actualRefL 	= q.getOpen5() - thrTp;
				actualRefTime = cal.get(Calendar.DAY_OF_YEAR);
				refHtp = actualRefH + minTp;
				refLtp = actualRefL - minTp;
				canChange = false;//no cambio la referencia hasta que la actual no sea tradeada
				if (debug==99){
					System.out.println("[new ref] "
							+DateUtils.datePrint(cal)
							+" "+actualRef
							+" | "+q.toString()
							);
				}
			}
			
			//reglas de entrada
			long actualRefTimeTraded = cal.get(Calendar.DAY_OF_YEAR);
			int range = TradingUtils.getRange(data,i,5*240);//rango de los ultimos 3 dias
			if (transactions.size()<maxTransactions
					&& actualRefTimeTraded!=lastRefTimeTraded
					&& range<=aRange
					){
				//reglas de entrada para nuevas transacciones
				if (true
						&& q.getOpen5()<=actualRefH 
						&& q.getHigh5()>=actualRefH){
					Transaction newTrans = new Transaction();
					newTrans.setRef(actualRef);
					newTrans.setRefH(actualRefH);
					newTrans.setRefL(actualRefL );
					newTrans.setRefHtp(refHtp);
					newTrans.setRefLtp(refLtp);
					newTrans.setId(transactionID+1);
					newTrans.setOrden(transactions.size());
					transactionID++;
					//calculamos microlots de acuerdo al equitity y al riesgo
					int microLots = si.getPositionMicroLots(actualRefH,minTp*0.1, riskTarget);
										
					if (microLots>0){
						//si.updateFloating(microLots, 0, 0);
						
						newTrans.setTarget$$(microLots*0.1*minTp*0.1);
						newTrans.setMaxAllowed$$(maxAllowedLoss*microLots*0.1*minTp*0.1);
						newTrans.setCurrentMicroLots(microLots);//deberia estar basado en el riesgo
						newTrans.setActualMode(1);//long
						newTrans.setStage(1);
						transactions.add(newTrans);
						canChange = true;									
						lastRefTraded = actualRef;
						lastRefTimeTraded = actualRefTime;
						if (debug==1 || debug==50){
							System.out.println("[NEW TRANSACTION LONG] id="+newTrans.getId()
							+" || mc="+microLots
							+" "+si.getAccountInfo()
							+" target="+PrintUtils.Print2dec(microLots*0.1*minTp*0.1, false)
									//+" || "+actualRefH+" "+actualRefL+" "+refHtp+" "+refLtp+" || "+minTp
								+" || H/HTP "+newTrans.getRefH()+" "+newTrans.getRefHtp()
								+" || L/LTP "+newTrans.getRefL()+" "+newTrans.getRefLtp()
									+" || "+q.toString());
						}
					}
				}else if  (true
						&& q.getOpen5()>=actualRefL 
						&& q.getLow5()<=actualRefL){
					Transaction newTrans = new Transaction();
					newTrans.setRef(actualRef);
					newTrans.setRefH(actualRefH);
					newTrans.setRefL(actualRefL );
					newTrans.setRefHtp(refHtp);
					newTrans.setRefLtp(refLtp);
					newTrans.setId(transactionID+1);
					newTrans.setOrden(transactions.size());
					transactionID++;
					int microLots = si.getPositionMicroLots(actualRefL,minTp*0.1, riskTarget);					
					
					if (microLots>0){
						canChange = true;	
						//definimos objetivo y maxima perdida por transacion
						newTrans.setTarget$$(microLots*0.1*minTp*0.1);
						newTrans.setMaxAllowed$$(maxAllowedLoss*microLots*0.1*minTp*0.1);
						
						newTrans.setCurrentMicroLots(microLots);//deberia estar basado en el riesgo
						newTrans.setActualMode(-1);//short
						newTrans.setStage(1);
						transactions.add(newTrans);
						lastRefTraded = actualRef;
						lastRefTimeTraded = actualRefTime;
						
						if (debug==1 || debug==50){
							System.out.println("[NEW TRANSACTION SHORT] id="+newTrans.getId()
									+" || mc="+microLots
									+" "+si.getAccountInfo()
									+" target="+PrintUtils.Print2dec(microLots*0.1*minTp*0.1, false)
									+" || H/HTP "+newTrans.getRefH()+" "+newTrans.getRefHtp()
									+" || L/LTP "+newTrans.getRefL()+" "+newTrans.getRefLtp()
									+" || "+q.toString()
								);
						}
					}
				}
			}
			
			//reglas de salida 
			//CLARO, 1 ACTUALIZO TODAS LAS POSICIONES (en la apertura)
			//actualizamos equitity
			//reseteo de todo 
			si.reset();			
			si.updateInfo(transactions,q.getOpen5());
			if (debug==50){
				System.out.println(DateUtils.datePrint(cal)
						+" || "+si.getAccountInfo()
						);
			}
			
		
			
			// DESPUES TOMO DECISIONES
			int j = 0;
			double accGlobal = 0.0;
			trefHigh = -1;
			trefLow = -1;
			while (j<transactions.size()){
				Transaction t = transactions.get(j);
				boolean isClose = false;
				
				if (t.getActualMode()==1){
										
					if (q.getHigh5()>=t.getRefHtp()){
						//cerramos actualizamos net
						int pips = t.getRefHtp()-t.getRefH();						
						isClose = true;
						actualLosses = 0;						
						//actualizamos info global
						String msg =" win "
						+" id="+t.getId()
						+" stage="+t.getStage()
						+" "+PrintUtils.Print2dec(t.getTarget$$(), false)+" "+PrintUtils.Print2dec(t.getMaxAllowed$$(), false);
						//solo estoy anotando las full-closed a nivel de transaccion
						si.updateClosed(msg,t,t.getCurrentMicroLots(), pips,t.getNetPosition(),0,debugUpdatedClosed);								
					}else if (q.getLow5()<=t.getRefL()){
						//neteamos perdidas
						int pips = Math.abs(t.getRefL()-t.getRefH());
						double netPips$$ = -(pips+comm)*0.1*t.getCurrentMicroLots()*0.1;//perdidas en $$												
						double currentNetPosition = t.getNetPosition()+netPips$$;
						t.setNetPosition(currentNetPosition);
						//SE ESTUDIA SI ES FACTIBLE SEGUIR AÑADIENDO Y SI NO SE CIERRA
						int microLots = si.getPositionMicroLotsAcc(q.getClose5(),minTp*0.1,-currentNetPosition+t.getTarget$$(),false,0);
						if (microLots>0){						
							//cambio de direccion
							t.setActualMode(-1);//AHORA SHORT
							t.setCurrentMicroLots(microLots);
							t.incStage();
							
							if (t.getStage()>=maxStage) maxStage = t.getStage();
							if (microLots>=maxMicroLots) maxMicroLots = microLots;
							
							if (debug==1){
								String marginlevelStr = "---";
								if (si.getMargin()>0)
									marginlevelStr = PrintUtils.Print2dec((si.getEquitity()*100.0)/si.getMargin(),false);
								System.out.println("[TRANSACTION CHANGE DIRECTION TO SHORT]  "+i
										+" id="+t.getId()
										+" s="+t.getStage()
										+" p="+pips
										+" cm="+t.getCurrentMicroLots()
										+" net="+PrintUtils.Print2dec(t.getNetPosition(), false)
										+" b="+PrintUtils.Print2dec(si.getBalance(), false)
										+" e="+PrintUtils.Print2dec(si.getEquitity(), false)
										+" m="+PrintUtils.Print2dec(si.getMargin(), false)
										+" fm="+PrintUtils.Print2dec(si.getFreeMargin(), false)
										+" ml="+marginlevelStr
										+" || "+q.toString()
								);																
							}
						}else{
							//cerramos
							isClose = true;
							actualLosses = 0;						
							//actualizamos info global
							String msg =" loss margin "
									+" id="+t.getId()
									+" stage="+t.getStage()
									+" target="+PrintUtils.Print2dec(t.getTarget$$(), false)+" maxAllowed"+PrintUtils.Print2dec(t.getMaxAllowed$$(), false);
							//solo estoy anotando las full-closed a nivel de transaccion
							si.updateClosed(msg,t,t.getCurrentMicroLots(), 0,t.getNetPosition(),0,debugUpdatedClosed);
							
							//dejamos la posicion abierta o cerramos=?¿
							if (debug==1){
								System.out.println("[CANCELED (NO MARGIN) TRANSACTION CHANGE DIRECTION TO SHORT]  "+t.getId()
										+" p="+pips
										+" cm="+t.getCurrentMicroLots()
										+" net="+PrintUtils.Print2dec(t.getNetPosition(), false)
										+" || "+q.toString()
								);
							}
						}//q.getLow5()<=t.getRefL()
					}else{
						//evaluamos net posicion al close y vemos si hay que cerrar
						if (t.getFloatingPosition$$(q.getClose5())<=-t.getMaxAllowed$$()
								|| t.getStage()>=maxStages
								){
						//if (t.getStage()>=maxStage){//cerramos
							//cerramos
							double floating = t.getFloatingPosition$$(q.getClose5());
							int pips = q.getClose5()-t.getRefH();
							double netPips$$ = pips*0.1*t.getCurrentMicroLots()*0.1;
							double currentNetPosition = t.getNetPosition()+netPips$$;
							t.setNetPosition(currentNetPosition);//consolidamos
							isClose = true;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							if (debug==1){
								System.out.println("[TRANSACTION LOSSES CLOSED (LONG)]  "+t.getId()
										+" "+PrintUtils.Print2dec(floating, false)
										+" || cm="+t.getCurrentMicroLots()
										+" actualNet="+netPips$$
										+" net="+PrintUtils.Print2dec(t.getNetPosition(), false)
										//+" || "+PrintUtils.Print2dec(maxAllowedLoss, false)
										+" || "+q.toString()
								);
							}
							
							//actualizamos info global
							String msg =" loss "
							+" id="+t.getId()
							+" stage="+t.getStage()
							+" "+DateUtils.datePrint(cal)
							+" target="+PrintUtils.Print2dec(t.getTarget$$(), false)+" maxLoss="+PrintUtils.Print2dec(t.getMaxAllowed$$(), false);
							si.updateClosed(msg,t,t.getCurrentMicroLots(), pips,t.getNetPosition(),1,debugUpdatedClosed);
						}
					}
				}else if (t.getActualMode()==-1){
					if (q.getLow5()<=t.getRefLtp()){
						//cerramos actualizamos net
						int pips = t.getRefL()-t.getRefLtp();						
						isClose = true;
						actualLosses = 0;						
						//actualizamos info
						String msg =" win "
							+" id="+t.getId()
							+" stage="+t.getStage()
								+" "+PrintUtils.Print2dec(t.getTarget$$(), false)+" "+PrintUtils.Print2dec(t.getMaxAllowed$$(), false);
						si.updateClosed(msg,t,t.getCurrentMicroLots(), pips,t.getNetPosition(),0,debugUpdatedClosed );
					}else if (q.getHigh5()>=t.getRefH()){
						//neteamos perdidas
						int pips = Math.abs(t.getRefL()-t.getRefH());
						double netPips$$ = -(pips+comm)*0.1*t.getCurrentMicroLots()*0.1;//perdidas en $$	
						double currentNetPosition = t.getNetPosition()+netPips$$;
						t.setNetPosition(currentNetPosition);
						
						int microLots = si.getPositionMicroLotsAcc(q.getClose5(),minTp*0.1,-currentNetPosition+t.getTarget$$(),false,0);
						if (microLots>0){
							//cambio de direccion
							t.setActualMode(1);//AHORA SHORT
							//incrementamos microlots
							t.setCurrentMicroLots(microLots);
							t.incStage();
							
							if (t.getStage()>=maxStage) maxStage = t.getStage();
							if (microLots>=maxMicroLots) maxMicroLots = microLots;
							
							if (debug==1){
								String marginlevelStr = "---";
								if (si.getMargin()>0)
									marginlevelStr = PrintUtils.Print2dec((si.getEquitity()*100.0)/si.getMargin(),false);
								System.out.println("[TRANSACTION CHANGE DIRECTION TO LONG]  "+i
										+" id="+t.getId()
										+" s="+t.getStage()
										+" p="+pips
										+" cm="+t.getCurrentMicroLots()
										+" net="+PrintUtils.Print2dec(t.getNetPosition(), false)
										+" b="+PrintUtils.Print2dec(si.getBalance(), false)
										+" e="+PrintUtils.Print2dec(si.getEquitity(), false)
										+" m="+PrintUtils.Print2dec(si.getMargin(), false)
										+" fm="+PrintUtils.Print2dec(si.getFreeMargin(), false)
										+" ml="+marginlevelStr
										+" || "+q.toString()
								);
							}
						}else{
							//cerramos
							isClose = true;
							actualLosses = 0;
							//actualizamos info global
							String msg =" loss margin "
							+" id="+t.getId()
							+" stage="+t.getStage()
							+" target="+PrintUtils.Print2dec(t.getTarget$$(), false)
							+" maxAllowed="+PrintUtils.Print2dec(t.getMaxAllowed$$(), false);
							//solo estoy anotando las full-closed a nivel de transaccion
							si.updateClosed(msg,t,t.getCurrentMicroLots(), 0,t.getNetPosition(),0,debugUpdatedClosed );
							
							if (debug==1){
								System.out.println("[CANCELED (NO MARGIN) TRANSACTION CHANGE DIRECTION TO LONG]  "+t.getId()
										+" p="+pips
										+" cm="+t.getCurrentMicroLots()
										+" net="+PrintUtils.Print2dec(t.getNetPosition(), false)
										+" || "+q.toString()
								);
							}
						}
					}else{
						//evaluamos net posicion al close y vemos si hay que cerrar
						if (t.getFloatingPosition$$(q.getClose5())<=-t.getMaxAllowed$$()
								|| t.getStage()>=maxStages
								){
							//cerramos
							int pips = t.getRefL()-q.getClose5();
							double netPips$$ = pips*0.1*t.getCurrentMicroLots()*0.1;
							double currentNetPosition = t.getNetPosition()+netPips$$;
							t.setNetPosition(currentNetPosition);//consolidamos
							isClose = true;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							if (debug==1){
								System.out.println("[TRANSACTION LOSSES CLOSED  (SHORT)] "+t.getId()
											+" || cm="+t.getCurrentMicroLots()
											+" actualNet="+netPips$$
											+" net="+PrintUtils.Print2dec(t.getNetPosition(), false)
										+" || "+q.toString()
								);
							}
							
							//actualizamos info global
							String msg =""
							+" id="+t.getId()
							+" stage="+t.getStage()
							+" "+PrintUtils.Print2dec(t.getTarget$$(), false)+" "+PrintUtils.Print2dec(t.getMaxAllowed$$(), false);
							si.updateClosed(msg,t,t.getCurrentMicroLots(), pips,t.getNetPosition(),1,debugUpdatedClosed );
						}
					}
				}//exit rules
				
				if (isClose){
					if (t.getNetPosition()>=0){
						wins++;
						winsFactor +=t.getNetPositionFactor(); 
					}else{
						losses++;
						lossFactor += -t.getNetPositionFactor(); 
					}
					/*if (debug==1){
						System.out.println("[TRANSACTION CLOSed] "+transactions.get(j).getId()
								+" || "+q.toString()
						);
					}*/
					transactions.remove(j);
				}else{//no cerrada
					j++;
					accGlobal += t.getFloatingPositionFactor(q.getClose5());
					if (accGlobal<=maxAccGlobal){
						maxAccGlobal = accGlobal;
					}
					
					if (trefHigh==-1 || t.getRef()>=trefHigh) trefHigh = t.getRef();
					if (trefLow==-1 || t.getRef()<=trefLow) trefLow = t.getRef();
				}
			}
			
		
			
			//high low da
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}

			
		}//for
		
	
		int totalseq = wins+losses;
		double winPer = wins*100.0/totalseq;
		double pf = winsFactor/lossFactor;
		double avg = (winsFactor-lossFactor)/totalseq;
		double avgPips = avg*(2*thrTp)*0.1;
		
		/*String header = 
				y1+" "+y2+" "+m1+" "+m2+" | "
				+" "+h1+" "+h2
				+" "+minTp
				+" "+thrTp
				+" "+maxTransactions
				+" "+PrintUtils.Print2dec(maxAllowedLoss,false)
				+" || "
				+" "+totalseq 
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(avgPips,false)
				+" || "
				+" "+PrintUtils.Print2dec(accRanges*0.1/totalDays,false)
				+" "+maxLosses
				+" || "
				+" "+maxStage
				+" "+PrintUtils.Print2dec(maxMicroLots,false)
				+" || "+PrintUtils.Print2dec(maxAccGlobal,false)
				+" "+PrintUtils.Print2dec(maxAccGlobal/maxTransactions,false)
				
				//+" "+PrintUtils.Print2dec(maxAccLoss,false)
				//+" "+PrintUtils.Print2dec(avgDayTrades,false)
				//+" "+maxLosses
		;*/
		
		String header = y1+" "+y2+" "+m1+" "+m2+" | "
				+" "+h1+" "+h2+" "+aMin
				+" "+minTp
				+" "+thrTp
				+" "+maxTransactions
				+" "+aRange
				+" "+PrintUtils.Print2dec(riskTarget,false)
				+" "+PrintUtils.Print2dec(maxAllowedLoss,false)
				+" || "
				+si.getReport();
		
		pf = si.getWins$$()/si.getLosses$$();
		
		int monthsW = totalMonths-totalMonthsL;
		int yearsW= totalYears-totalYearsL;
		aHeader += " y="+yearsW+"/"+totalYears+" m="+monthsW+"/"+totalMonths;
		
		if (debug==80){
			if (yearsW>=7){
				System.out.println(aHeader+" || "+header);
			}
		}else{
			
			if (debug==7){
				si.printOrdenStats(200);
			}else{		
				if (debug!=5){
					if (debug!=10){
						System.out.println(aHeader+" || "+header);
					}else{
						
						
						if (pf>=1.0){
							System.out.println(aHeader+" || "+header);
						}
					}
				}
			}
		}
			
		if (si.getLosses$$()<=0) return 2.0;
		
		return pf;
		
		/*int wins = count-count5;
		double factor = (2*thrTp)*1.0/(minTp);
		double count5loss = TradingUtils.calculateAccLoss(touchesThr,factor,baseComm);
		double pf = wins*1.0/(count5*count5loss);

		double pf$$ = totalWinSize/totalLossSize;
		
		double avg = accBars*1.0/count;
		
		double per = countUp*100.0/(countUp+countDown);
		
		double dt = Math.sqrt(MathUtils.variance(touches));
		
		double avgDayCount = accdaytrades*1.0/countdays;
		
		double accLossAvg = accLossSum/(count+losses);
		
		double benchmark = avgDayCount*accLossAvg;
		
		double benchmark2 = wins*1.0/benchmark;
		
		double avgDayTrades = accdaytrades*1.0/countdays;
		
		String header = h1+" "+h2
				+" "+minDiff+" "+minTp+" "+thrTp+" "+touchesThr
				+" ||| "
				+" || "+PrintUtils.Print2dec(pf$$,false)
				+" "+PrintUtils.Print2dec(accLossAvg,false)
				+" "+PrintUtils.Print2dec(maxAccLoss,false)
				+" "+PrintUtils.Print2dec(avgDayTrades,false)
				+" "+maxLosses
				+" || "+count+" "+PrintUtils.Print2dec(avg,false)+" "+losses
				+" || "+maxcount
				
				+" "+PrintUtils.Print2dec(benchmark,false)
				+" || "+PrintUtils.Print2dec(benchmark2,false)
				+" || "+counth
				+" "+PrintUtils.Print2dec(counth*1.0/count,false)
				+" "+PrintUtils.Print2dec(dt,false)
				+" "+count5
				+" || "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(factor,false)
				+" "+PrintUtils.Print2dec(count5loss,false)
				
				+" "+PrintUtils.Print2dec(totalWinSize,false)
				+" "+PrintUtils.Print2dec(totalLossSize,false)
				+" || "+PrintUtils.Print2dec(accLossAvg,false)
				+" || "+PrintUtils.Print2dec(maxAccLoss,false)
				+" || "+PrintUtils.Print2dec(per,false)
				;
		
		boolean isPrinted = false;
		if ( maxAccLoss<=200.0
				&& (
						((pf$$>=4.6 && benchmark2>=10.0)
						|| (maxAccLoss<=80.0 && pf$$>=1.8 && maxLosses*maxAccLoss<=400)
						//|| (maxAccLoss<=200.0 && pf$$>=2.5 && benchmark2>=4.0)
						)
					)
			){
			isPrinted = true;
			MathUtils.summary(header, bars);
		}
		
		if (!isPrinted
				&& (debug==1 || debug==5)
				){
			MathUtils.summary(header, bars);
		}*/
	}
	
	private static double testTransactions(String aHeader, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			int y1, int y2, 
			int m1,int m2,
			int h1, int h2,
			int thrTp,
			int minTp,
			double maxAllowedLoss,
			int maxTransactions,
			int debug) {
		
		
		Calendar cal = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count = 0;
		int dayOpen = 0;

		int accBars = 0;
		int countUp = 0;
		int countDown = 0;
		int counth = 0;
		int lasth=-1;
		int lasth1=-1;
		int count5 = 0;
		int lastDay = -1;
		//int touchesThr = 3;
		ArrayList<Integer> bars = new ArrayList<Integer>();
		ArrayList<Integer> touches = new ArrayList<Integer>();
		dayOpen = 0;
		
		double totalWinSize = 0;
		double totalLossSize = 0;
		double maxAccLoss = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		double accLossSum=0;
		double factorSize = (2*thrTp)*1.0/(minTp);
		//1u = minTP, xu = 20.0;
		double baseComm = 15.0/minTp;
		int losses=0;
		int high = -1;
		int low = -1;
		
		ArrayList<Integer> dayTrades = new ArrayList<Integer>();
		int maxDay = (30)*365;//30 años..
		for (int i=0;i<=maxDay;i++) dayTrades.add(0);
		
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		
		int actualRef = 0;
		int actualRefH = 0;
		int actualRefL = 0;
		int refHtp = 0;
		int refLtp = 0;
		int wins = 0;
		double winsFactor = 0.0;
		double lossFactor = 0.0;
		
		int accRanges = 0;
		int totalDays = 0;
		int lastHTrade = -1;
		int lastRefTraded = -1;
		double maxMicroLots = 1;
		int maxStage = 1;
		double maxAccGlobal = 0;
		for (int i=1;i<data.size()-1;i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (day!=lastDay) {		
				
				if (lastDay!=-1){
					accRanges += high-low;
					totalDays++;
				}
				dayOpen = q.getOpen5();
				lasth1=-1;
				lastDay = day;
				high = -1;
				low = -1;
				
			}
			
			//buscamos la referencia
			if (h>=h1 && h<=h2
					&& min==0
					){
				//referencia actual para entradas y salidas en el sistema
				actualRef	= q.getOpen5();
				actualRefH 	= q.getOpen5() + thrTp;
				actualRefL 	= q.getOpen5() - thrTp;
				refHtp = actualRefH + minTp;
				refLtp = actualRefL - minTp;
			}
			
			//reglas de entrada
			int actualRefTraded = actualRef;
			if (transactions.size()<maxTransactions
					&& actualRefTraded!=lastRefTraded
					){
				//reglas de entrada para nuevas transacciones
				if (q.getOpen5()<=actualRefH && q.getHigh5()>=actualRefH){
					Transaction newTrans = new Transaction();
					newTrans.setRef(actualRef);
					newTrans.setRefH(actualRefH);
					newTrans.setRefL(actualRefL );
					newTrans.setRefHtp(refHtp);
					newTrans.setRefLtp(refLtp);
					newTrans.setCurrentMicroLots(1);//deberia estar basado en el riesgo
					newTrans.setActualMode(1);//long
					newTrans.setStage(1);
					transactions.add(newTrans);
					lastRefTraded = actualRef;
					
					if (debug==1){
						System.out.println("[NEW TRANSACTION LONG] "
								//+" || "+actualRefH+" "+actualRefL+" "+refHtp+" "+refLtp+" || "+minTp
								+" || "+newTrans.getRefH()+" "+newTrans.getRefHtp()
								+" || "+newTrans.getRefL()+" "+newTrans.getRefLtp()
								+" || "+q.toString());
					}
				}else if  (q.getOpen5()>=actualRefL && q.getLow5()<=actualRefL){
					Transaction newTrans = new Transaction();
					newTrans.setRef(actualRef);
					newTrans.setRefH(actualRefH);
					newTrans.setRefL(actualRefL );
					newTrans.setRefHtp(refHtp);
					newTrans.setRefLtp(refLtp);
					newTrans.setCurrentMicroLots(1);//deberia estar basado en el riesgo
					newTrans.setActualMode(-1);//short
					newTrans.setStage(1);
					transactions.add(newTrans);
					lastRefTraded = actualRef;
					
					if (debug==1){
						System.out.println("[NEW TRANSACTION SHORT] "
								+" || "+newTrans.getRefH()+" "+newTrans.getRefHtp()
								+" || "+newTrans.getRefL()+" "+newTrans.getRefLtp()
								+" || "+q.toString()
							);
					}
				}
			}
			
			//reglas de salida 
			int j = 0;
			double accGlobal = 0.0;
			while (j<transactions.size()){
				Transaction t = transactions.get(j);
				boolean isClose = false;
				
				if (t.getActualMode()==1){
					if (q.getHigh5()>=t.getRefHtp()){
						//cerramos actualizamos net
						int pips = t.getRefHtp()-t.getRefH();
						double netPips$$ = pips*t.getCurrentMicroLots();
						double currentNetPosition = t.getNetPosition()+netPips$$;
						t.setNetPosition(currentNetPosition);						
						isClose = true;
						actualLosses = 0;
						if (debug==1){
							System.out.println("[TRANSACTION LONG WIN]  "
									+" || "+t.getRefH()+" "+t.getRefHtp()
									+" || "+t.getRefL()+" "+t.getRefLtp()
									+" "+t.getCurrentMicroLots()
									+" "+PrintUtils.Print2dec(t.getNetPositionFactor(), false)
									+" || "+q.toString()
							);
						}
					}else if (q.getLow5()<=t.getRefL()){
						//neteamos perdidas
						int pips = Math.abs(t.getRefL()-t.getRefH());
						double netPips$$ = -pips*t.getCurrentMicroLots();
						double currentNetPosition = t.getNetPosition()+netPips$$;
						t.setNetPosition(currentNetPosition);
						//cambio de direccion
						t.setActualMode(-1);//AHORA SHORT
						//incrementamos microlots
						double newMicroLots = (-t.getNetPosition() + 1*minTp)*1.0/minTp;//para ganar un microlot*pipstarget
						t.setCurrentMicroLots((int) newMicroLots);
						t.incStage();
						
						if (t.getStage()>=maxStage) maxStage = t.getStage();
						if (newMicroLots>=maxMicroLots) maxMicroLots = newMicroLots;
						
						if (debug==1){
							System.out.println("[TRANSACTION CHANGE DIRECTION TO SHORT]  "
									+" "+pips
									+" "+t.getCurrentMicroLots()
									+" "+PrintUtils.Print2dec(t.getNetPosition(), false)
									+" "+PrintUtils.Print2dec(t.getNetPositionFactor(), false)
									+" || "+q.toString()
							);
						}
					}else{
						//evaluamos net posicion al close y vemos si hay que cerrar
						if (t.getFloatingPositionFactor(q.getClose5())<=-maxAllowedLoss){
							//cerramos
							double floating = t.getFloatingPosition$$(q.getClose5());
							int pips = q.getClose5()-t.getRefH();
							double netPips$$ = pips*t.getCurrentMicroLots();
							double currentNetPosition = t.getNetPosition()+netPips$$;
							t.setNetPosition(currentNetPosition);//consolidamos
							isClose = true;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							if (debug==1){
								System.out.println("[TRANSACTION LOSSES CLOSED (LONG)]  "
										+" "+PrintUtils.Print2dec(floating, false)
										+" || "+t.getCurrentMicroLots()
										+" "+netPips$$
										+" "+PrintUtils.Print2dec(t.getFloatingPositionFactor(q.getClose5()), false)
										+" "+PrintUtils.Print2dec(t.getNetPositionFactor(), false)
										//+" || "+PrintUtils.Print2dec(maxAllowedLoss, false)
										+" || "+q.toString()
								);
							}
						}
					}
				}else if (t.getActualMode()==-1){
					if (q.getLow5()<=t.getRefLtp()){
						//cerramos actualizamos net
						int pips = t.getRefL()-t.getRefLtp();
						double netPips$$ = pips*t.getCurrentMicroLots();
						double currentNetPosition = t.getNetPosition()+netPips$$;
						t.setNetPosition(currentNetPosition);
						
						isClose = true;
						actualLosses = 0;
						if (debug==1){
							System.out.println("[TRANSACTION SHORT WIN]  "
									//+" || "+t.getRefH()+" "+t.getRefHtp()
									//+" || "+t.getRefL()+" "+t.getRefLtp()
									+" "+t.getCurrentMicroLots()
									+" "+PrintUtils.Print2dec(t.getNetPositionFactor(), false)
									+" || "+q.toString()
							);
						}
					}else if (q.getHigh5()>=t.getRefH()){
						//neteamos perdidas
						int pips = Math.abs(t.getRefL()-t.getRefH());
						double netPips$$ = -pips*t.getCurrentMicroLots();
						double currentNetPosition = t.getNetPosition()+netPips$$;
						t.setNetPosition(currentNetPosition);
						//cambio de direccion
						t.setActualMode(1);//AHORA SHORT
						//incrementamos microlots
						double newMicroLots = (-t.getNetPosition() + 1*minTp)*1.0/minTp;//para ganar un microlot*pipstarget
						t.setCurrentMicroLots((int) newMicroLots);
						t.incStage();
						
						if (t.getStage()>=maxStage) maxStage = t.getStage();
						if (newMicroLots>=maxMicroLots) maxMicroLots = newMicroLots;
						
						if (debug==1){
							System.out.println("[TRANSACTION CHANGE DIRECTION TO LONG]  "
									+" "+pips
									+" "+t.getCurrentMicroLots()
									+" "+PrintUtils.Print2dec(t.getNetPosition(), false)
									+" "+PrintUtils.Print2dec(t.getNetPositionFactor(), false)
									+" || "+q.toString()
							);
						}
					}else{
						//evaluamos net posicion al close y vemos si hay que cerrar
						if (t.getFloatingPositionFactor(q.getClose5())<=-maxAllowedLoss){
							//cerramos
							int pips = t.getRefL()-q.getClose5();
							double netPips$$ = pips*t.getCurrentMicroLots();
							double currentNetPosition = t.getNetPosition()+netPips$$;
							t.setNetPosition(currentNetPosition);//consolidamos
							isClose = true;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							if (debug==1){
								System.out.println("[TRANSACTION LOSSES CLOSED  (SHORT)]  "
										+" "+t.getCurrentMicroLots()
										+" "+netPips$$
										+" "+PrintUtils.Print2dec(t.getFloatingPositionFactor(q.getClose5()), false)
										+" "+PrintUtils.Print2dec(t.getNetPositionFactor(), false)
										+" || "+q.toString()
								);
							}
						}
					}
				}//exit rules
				
				if (isClose){
					if (t.getNetPosition()>=0){
						wins++;
						winsFactor +=t.getNetPositionFactor(); 
					}else{
						losses++;
						lossFactor += -t.getNetPositionFactor(); 
					}
					transactions.remove(j);
				}else{
					j++;
					accGlobal += t.getFloatingPositionFactor(q.getClose5());
					if (accGlobal<=maxAccGlobal){
						maxAccGlobal = accGlobal;
					}
				}
			}
			
		
			
			//high low da
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}

			
		}//for
		
	
		int totalseq = wins+losses;
		double winPer = wins*100.0/totalseq;
		double pf = winsFactor/lossFactor;
		double avg = (winsFactor-lossFactor)/totalseq;
		double avgPips = avg*(2*thrTp)*0.1;
		
		String header = 
				y1+" "+y2+" "+m1+" "+m2+" | "
				+" "+h1+" "+h2
				+" "+minTp
				+" "+thrTp
				+" "+maxTransactions
				+" "+PrintUtils.Print2dec(maxAllowedLoss,false)
				+" || "
				+" "+totalseq 
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(avgPips,false)
				+" || "
				+" "+PrintUtils.Print2dec(accRanges*0.1/totalDays,false)
				+" "+maxLosses
				+" || "
				+" "+maxStage
				+" "+PrintUtils.Print2dec(maxMicroLots,false)
				+" || "+PrintUtils.Print2dec(maxAccGlobal,false)
				+" "+PrintUtils.Print2dec(maxAccGlobal/maxTransactions,false)
				
				//+" "+PrintUtils.Print2dec(maxAccLoss,false)
				//+" "+PrintUtils.Print2dec(avgDayTrades,false)
				//+" "+maxLosses
		;
		
		if (debug!=5){
			if (debug!=10){
				System.out.println(aHeader+" || "+header);
			}else{
				if (pf>=1.6){
					System.out.println(aHeader+" || "+header);
				}
			}
		}
			
		
		return pf;
		
		/*int wins = count-count5;
		double factor = (2*thrTp)*1.0/(minTp);
		double count5loss = TradingUtils.calculateAccLoss(touchesThr,factor,baseComm);
		double pf = wins*1.0/(count5*count5loss);

		double pf$$ = totalWinSize/totalLossSize;
		
		double avg = accBars*1.0/count;
		
		double per = countUp*100.0/(countUp+countDown);
		
		double dt = Math.sqrt(MathUtils.variance(touches));
		
		double avgDayCount = accdaytrades*1.0/countdays;
		
		double accLossAvg = accLossSum/(count+losses);
		
		double benchmark = avgDayCount*accLossAvg;
		
		double benchmark2 = wins*1.0/benchmark;
		
		double avgDayTrades = accdaytrades*1.0/countdays;
		
		String header = h1+" "+h2
				+" "+minDiff+" "+minTp+" "+thrTp+" "+touchesThr
				+" ||| "
				+" || "+PrintUtils.Print2dec(pf$$,false)
				+" "+PrintUtils.Print2dec(accLossAvg,false)
				+" "+PrintUtils.Print2dec(maxAccLoss,false)
				+" "+PrintUtils.Print2dec(avgDayTrades,false)
				+" "+maxLosses
				+" || "+count+" "+PrintUtils.Print2dec(avg,false)+" "+losses
				+" || "+maxcount
				
				+" "+PrintUtils.Print2dec(benchmark,false)
				+" || "+PrintUtils.Print2dec(benchmark2,false)
				+" || "+counth
				+" "+PrintUtils.Print2dec(counth*1.0/count,false)
				+" "+PrintUtils.Print2dec(dt,false)
				+" "+count5
				+" || "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(factor,false)
				+" "+PrintUtils.Print2dec(count5loss,false)
				
				+" "+PrintUtils.Print2dec(totalWinSize,false)
				+" "+PrintUtils.Print2dec(totalLossSize,false)
				+" || "+PrintUtils.Print2dec(accLossAvg,false)
				+" || "+PrintUtils.Print2dec(maxAccLoss,false)
				+" || "+PrintUtils.Print2dec(per,false)
				;
		
		boolean isPrinted = false;
		if ( maxAccLoss<=200.0
				&& (
						((pf$$>=4.6 && benchmark2>=10.0)
						|| (maxAccLoss<=80.0 && pf$$>=1.8 && maxLosses*maxAccLoss<=400)
						//|| (maxAccLoss<=200.0 && pf$$>=2.5 && benchmark2>=4.0)
						)
					)
			){
			isPrinted = true;
			MathUtils.summary(header, bars);
		}
		
		if (!isPrinted
				&& (debug==1 || debug==5)
				){
			MathUtils.summary(header, bars);
		}*/
	}
	
	/**
	 * Breakout de rectangulso
	 * @param string
	 * @param data
	 * @param maxMins
	 * @param y1
	 * @param y2
	 * @param m1
	 * @param m2
	 * @param h1
	 * @param h2
	 * @param minDiff
	 * @param thrTp
	 * @param minTp
	 * @param touchesThr
	 * @param debug
	 */
	private static void testBreakout(String string, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			int y1, int y2, 
			int m1,int m2,
			int h1, int h2,
			int maxRange,
			int nbars,
			int debug) {
		
		
		Calendar cal = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int count = 0;
		int dayOpen = 0;

		int accBars = 0;
		int countUp = 0;
		int countDown = 0;
		int counth = 0;
		int lasth=-1;
		int lasth1=-1;
		int count5 = 0;
		int lastDay = -1;
		//int touchesThr = 3;
		ArrayList<Integer> bars = new ArrayList<Integer>();
		ArrayList<Integer> touches = new ArrayList<Integer>();
		dayOpen = 0;
		
		double totalWinSize = 0;
		double totalLossSize = 0;
		double maxAccLoss = 0;
		double accLossSum=0;
		//double factorSize = (2*thrTp)*1.0/(minTp);
		//1u = minTP, xu = 20.0;
		//double baseComm = 15.0/minTp;
		int losses=0;
		int high = -1;
		int low = -1;
		int wins = 0;
		int winPips = 0;
		int lostPips = 0;
		
		ArrayList<Integer> dayTrades = new ArrayList<Integer>();
		int maxDay = (30)*365;//30 años..
		for (int i=0;i<=maxDay;i++) dayTrades.add(0);
		int ref = 0;
		for (int i=1;i<data.size()-1;i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (day!=lastDay) {			
				dayOpen = q.getOpen5();
				lasth1=-1;
				lastDay = day;
				high = -1;
				low = -1;
			}
			
			int range = high-low;
			
			if (min==0){
				ref = q.getOpen5();
			}
			
						
			if (h>=h1 && h<=h2
					&& min==0
					){
				//miramos el rango de las ultimas nbars
				TradingUtils.getMaxMinShort(data, qm, calqm, i-nbars, i-1);
				
				int rangeBars = qm.getHigh5()-qm.getLow5();
				
				int rangeTarget = 1*rangeBars;//3 veces el rango comprendido, ampliacion del rango en 3
				
				if (rangeBars<=maxRange){//el numero de pips es menor que el maximo
					count++;
					
					//vemos las posibles entradas
					
					int entry = -1;
					int slValue = -1;
					int tpValue = -1;
					int mode = 0;
					int distH = qm.getHigh5()-q.getOpen5();
					int distL = q.getOpen5()-qm.getLow5();
					int mid = (qm.getHigh5()-qm.getLow5())/2;
					
					//System.out.println(qm.getHigh5()+" - "+qm.getLow5()+" || "+q.toString());
					if (q.getOpen5()>=ref){//un minimo stop de al menos 5 pips
						//BUY //hacia el techo de arriba
						entry = q.getOpen5();
						//tpValue = q.getOpen5()+ (qm.getHigh5()-q.getOpen5())+rangeTarget;
						tpValue = q.getOpen5()+ 4*(q.getOpen5()-qm.getLow5());
						//tpValue = qm.getHigh5();
						slValue = qm.getLow5();						
						mode = 1;
						
						//modo reverse
						/*entry = q.getOpen5();
						//tpValue = q.getOpen5()+ (qm.getHigh5()-q.getOpen5())+rangeTarget;
						slValue = q.getOpen5()+ 10*(q.getOpen5()-qm.getLow5());
						//tpValue = qm.getHigh5();
						tpValue = qm.getLow5();	
						mode = -1;*/
					}else if (q.getOpen5()<=ref){//un minimo stop de al menos 5 pips
						//SELL hacia el suelo
						entry = q.getOpen5();
						tpValue = q.getOpen5()- 4*(qm.getHigh5()-q.getOpen5());
						//tpValue = q.getOpen5()- (q.getOpen5()-qm.getLow5())-rangeTarget;
						//tpValue = qm.getLow5();
						slValue = qm.getHigh5();
						mode = -1;
						
						//modo reverse
						/*entry = q.getOpen5();
						tpValue = q.getOpen5()+ 10*(qm.getHigh5()-q.getOpen5());
						//tpValue = q.getOpen5()- (q.getOpen5()-qm.getLow5())-rangeTarget;
						//tpValue = qm.getLow5();
						slValue = qm.getHigh5();	
						mode = 1;*/
					}
					
					if (mode!=0){
						
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, entry, tpValue, slValue, false);
						
						int pips = 0;
						if (mode==1) pips = qm.getClose5()-q.getOpen5();
						else if (mode==-1) pips = q.getOpen5()-qm.getClose5();
						
						if (pips>=0){
							wins++;
							winPips += pips;
						}else{
							losses++;
							lostPips += -pips;
						}
						
					}					
				}
			}//h1
		}//main for
		
		int total = wins+losses;
		
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		
		System.out.println(
				h1+" "+h2
				+" "+nbars
				+" "+maxRange
				+" ||" 
				+" "+count
				+" "+total
				+" "+PrintUtils.Print2dec(winPer,false)
				+" || "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" || "
				+" "+PrintUtils.Print2dec(winPips*0.1/wins,false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses,false)
				);
	}
	
	private static double test1$$(String header, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			int y1, int y2, 
			int m1,int m2,
			int h1, int h2,
			int minDiff,
			int thrTp,
			int minTp,
			int touchesThr,
			int nbars,
			int maxPips,
			double maxAccLossAllowed,
			int debug) {
		
		
		Calendar cal = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count = 0;
		int dayOpen = 0;

		int accBars = 0;
		int countUp = 0;
		int countDown = 0;
		int counth = 0;
		int lasth=-1;
		int lasth1=-1;
		int count5 = 0;
		int lastDay = -1;
		//int touchesThr = 3;
		ArrayList<Integer> bars = new ArrayList<Integer>();
		ArrayList<Integer> touches = new ArrayList<Integer>();
		dayOpen = 0;
		
		double totalWinSize = 0;
		double totalLossSize = 0;
		double maxAccLoss = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		double accLossSum=0;
		double factorSize = (2*thrTp)*1.0/(minTp);
		//1u = minTP, xu = 20.0;
		double baseComm = 15.0/minTp;
		int losses=0;
		int high = -1;
		int low = -1;
		
		ArrayList<Integer> dayTrades = new ArrayList<Integer>();
		int maxDay = (30)*365;//30 años..
		for (int i=0;i<=maxDay;i++) dayTrades.add(0);
		
		for (int i=1;i<data.size()-1;i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (day!=lastDay) {			
				dayOpen = q.getOpen5();
				lasth1=-1;
				lastDay = day;
				high = -1;
				low = -1;
			}
			
			int range = high-low;
			
		
						
			if (h>=h1 && h<=h2 
					&& (
							min==0 
							&& sec==0
						//|| min==30 
						//|| min==15 || min==45
						) 
					//&& range>=minDiff
					//&& sec==0
					//&& h!=lasth1 //1 cada hora
					){
				
				TradingUtils.getMaxMinShort(data, qm, calqm, i-nbars, i-1);
				int rangeBars = qm.getHigh5()-qm.getLow5();
				
				if (true
						&& rangeBars>=maxPips
						){
					int entry = q.getOpen5();
					
					int thrUph = entry+thrTp;
					int thrDownh = entry-thrTp;
					int thrUp = thrUph+minTp;
					int thrDown = thrDownh-minTp;
										
					int valid = 0;
					lasth=0;
					int totaltouches =0;
					
					double size= 1.0;
					double accLossSize = 0.0;
					double accComm = 0.0;
					
					if (debug==1)
						System.out.println("***TEST***: "+entry+" "+thrUp+" "+thrDown+" || "+thrUph+" "+thrDownh);
					int actualDay = -1;
					int lastActualDay = -1;
					for (int j=i;j<data.size();j++){
						if (data.get(j).getHigh5()>=thrUph){
							
							//hay trade para este dia
							QuoteShort.getCalendar(calqm, data.get(j));
							actualDay = calqm.get(Calendar.DAY_OF_YEAR);
							int year = calqm.get(Calendar.YEAR);
							int refDay = (year-y1+1)*365+actualDay;
							if (actualDay!=lastActualDay){
								int actualCount = dayTrades.get(refDay);
								dayTrades.set(refDay, actualCount+1);
								lastActualDay =actualDay;//para no volver a sumar en este dia
							}
							
							valid = 0;
							if (lasth==-1 || lasth==0){
								counth++;
								totaltouches++;
								valid=1;
								accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							}
							lasth = 1;
							
							
							if (valid==1){
								if (debug==1){
									System.out.println("touched up0 : "+counth+" "+valid
											+" "+range
											+" || dayCount= "+dayTrades.get(refDay)+ "("+refDay+")"
											+" || "+PrintUtils.Print2dec(accLossSize,false)
											+" || "+q.getOpen5()
											+" || "+data.get(j).toString()
											
											);
								}
							}
							
						}else if (data.get(j).getLow5()<=thrDownh){
							
							//hay trade para este dia
							QuoteShort.getCalendar(calqm, data.get(j));
							actualDay = calqm.get(Calendar.DAY_OF_YEAR);
							int year = calqm.get(Calendar.YEAR);
							int refDay = (year-y1+1)*365+actualDay;
							if (actualDay!=lastActualDay){								
								int actualCount = dayTrades.get(refDay);
								dayTrades.set(refDay, actualCount+1);
								lastActualDay =actualDay;//para no volver a sumar en este dia
							}
							
							
							valid = 0;
							if (lasth==1 || lasth==0){
								counth++;
								totaltouches++;
								valid=1;
								accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							}
							lasth = -1;

							if (valid==1){
								if (debug==1){
									System.out.println("touched down0 : "+counth+" "+valid
											+" "+range
											+" || dayCount= "+dayTrades.get(refDay)
											+" || "+PrintUtils.Print2dec(accLossSize,false)
											+" || "+q.getOpen5()
											+" || "+data.get(j).toString()
											);
								}
							}
							
						}
						
						
						
						if (data.get(j).getHigh5()>=thrUp){
							count++;
							countUp++;//continuacion
							accBars+=j-i;
							bars.add(j-i);
							int finishMode = -1;
							accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							accLossSum += accLossSize;
							double sizePlayed = (accLossSize+1.0)+(accLossSize+1.0)*baseComm;	
							if (totaltouches>=touchesThr){
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalLossSize += accLossSize;
								finishMode = 0;
								count5++;
							}else{
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalWinSize += sizePlayed-accLossSize-(sizePlayed*baseComm);
								finishMode = 1;
							}
							if (debug==1){
								System.out.println("break Up "+finishMode
										+" || "+count+" "+losses
										+" || "+PrintUtils.Print2dec(sizePlayed-accLossSize-(sizePlayed*baseComm),false)
										+"  "+PrintUtils.Print2dec(sizePlayed,false)
										+"  "+PrintUtils.Print2dec(accLossSize,false)
										+"  "+PrintUtils.Print2dec(baseComm,false)
										+" || "+PrintUtils.Print2dec(totalWinSize,false)
										+" || "+PrintUtils.Print2dec(totalLossSize,false)
										+" || "+data.get(j).toString() 
										);
							}
							touches.add(totaltouches);
							
							actualLosses = 0;
							
							//i=j;
							break;
						}else if (data.get(j).getLow5()<=thrDown){
							count++;
							countDown++;//no continuacion
							accBars+=j-i;
							bars.add(j-i);
							int finishMode = -1;
							accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							accLossSum += accLossSize;
							double sizePlayed = (accLossSize+1.0)+(accLossSize+1.0)*baseComm;	
							if (totaltouches>=touchesThr){
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalLossSize += accLossSize;
								count5++;
							}else{
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalWinSize += sizePlayed-accLossSize-(sizePlayed*baseComm);
								finishMode = 1;
							}
							if (debug==1){
								System.out.println("break down "
										+" || "+count+" "+losses
										+" || "+PrintUtils.Print2dec(sizePlayed-accLossSize-(sizePlayed*baseComm),false)
										+"  "+PrintUtils.Print2dec(sizePlayed,false)
										+"  "+PrintUtils.Print2dec(accLossSize,false)
										+"  "+PrintUtils.Print2dec(baseComm,false)
										+" || "+PrintUtils.Print2dec(totalWinSize,false)
										+" || "+PrintUtils.Print2dec(totalLossSize,false)
										+" || "+data.get(j).toString()  										
										);
							}
							touches.add(totaltouches);
							if (totaltouches>=touchesThr) count5++;
							
							actualLosses = 0;
							//i=j;
							break;
						}
						
						//tercera condición de salida, que se haya llegado al stop
						accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
						if (accLossSize>=maxAccLoss){
							//System.out.println("[NEW RECORD LOSS] "
							//		+"  "+PrintUtils.Print2dec(accLossSize,false)
							//);
						}
						
						if (totaltouches>=touchesThr
								|| accLossSize>=(maxAccLossAllowed/1.0)
								){
							
							accLossSum += accLossSize;
							if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
							totalLossSize += accLossSize;
							count5++;
							losses++;
							if (debug==1){
								System.out.println("***STOP ALCANZADO***"+" "+range
										+" || "+count+" "+losses
										+" || "+PrintUtils.Print2dec(totalWinSize,false)
										+" || "+PrintUtils.Print2dec(totalLossSize,false)
										+" || "+data.get(j).toString() 
										);
							}
							
							actualLosses++;
							
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							//i=j;
							break;//salimos
						}
					}
				}
				
				lasth1=h;
			}//h
			
			//high low da
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}

			
		}//for
		
		//estudio de dayTrades
		int accdaytrades = 0;
		int countdays=0;
		int maxcount = 0;
		for (int i=0;i<dayTrades.size();i++){
			int countday = dayTrades.get(i);
			if (countday>=1){
				accdaytrades+=countday;
				countdays++;
				if (countday>=maxcount) maxcount = countday;
			}
		}
		
		
		int wins = count-count5;
		double factor = (2*thrTp)*1.0/(minTp);
		double count5loss = TradingUtils.calculateAccLoss(touchesThr,factor,baseComm);
		double pf = wins*1.0/(count5*count5loss);

		double pf$$ = totalWinSize/totalLossSize;
		
		if (totalLossSize==0 && totalWinSize>=100){
			pf$$ = 9.0;//convencion
		}
		
		double avg = accBars*1.0/count;
		
		double per = countUp*100.0/(countUp+countDown);
		
		double dt = Math.sqrt(MathUtils.variance(touches));
		
		double avgDayCount = accdaytrades*1.0/countdays;
		
		double accLossAvg = accLossSum/(count+losses);
		
		double benchmark = avgDayCount*accLossAvg;
		
		double benchmark2 = wins*1.0/benchmark;
		
		double avgDayTrades = accdaytrades*1.0/countdays;
		
		String header2 = h1+" "+h2
				+" "+minDiff+" "+minTp+" "+thrTp+" "+touchesThr
				+" "+PrintUtils.Print2dec(maxAccLossAllowed,false)
				//+" ||| "
				+" || "
				+count
				+" "+(count-losses)
				+" "+losses
				+" "+PrintUtils.Print2dec(pf$$,false)
				+" "+PrintUtils.Print2dec(accLossAvg,false)
				+" "+PrintUtils.Print2dec(maxAccLoss,false)
				+" "+PrintUtils.Print2dec(avgDayTrades,false)
				+" "+maxLosses
				+" || "+PrintUtils.Print2dec(maxLosses*maxAccLoss,false)
				+" || "+PrintUtils.Print2dec(count*1.0/(maxLosses*maxAccLoss),false)
				//+" || "+count+" "+PrintUtils.Print2dec(avg,false)+" "+losses
				//+" || "+maxcount
				
				//+" "+PrintUtils.Print2dec(benchmark,false)
				//+" || "+PrintUtils.Print2dec(benchmark2,false)
				//+" || "+counth
				//+" "+PrintUtils.Print2dec(counth*1.0/count,false)
				//+" "+PrintUtils.Print2dec(dt,false)
				//+" "+count5
				//+" || "+PrintUtils.Print2dec(pf,false)
				//+" "+PrintUtils.Print2dec(factor,false)
				//+" "+PrintUtils.Print2dec(count5loss,false)
				
				//+" "+PrintUtils.Print2dec(totalWinSize,false)
				//+" "+PrintUtils.Print2dec(totalLossSize,false)
				//+" || "+PrintUtils.Print2dec(accLossAvg,false)
				//+" || "+PrintUtils.Print2dec(maxAccLoss,false)
				//+" || "+PrintUtils.Print2dec(per,false)
				+" || "
				;
		
		if (debug!=40){
			boolean isPrinted = false;
			if (debug!=20 && debug!=5 && maxAccLoss<=200.0
					&& (
							((pf$$>=4.6 && benchmark2>=10.0)
							|| (maxAccLoss<=80.0 && pf$$>=1.8 && maxLosses*maxAccLoss<=400)
							//|| (maxAccLoss<=200.0 && pf$$>=2.5 && benchmark2>=4.0)
							)
						)
				){
				isPrinted = true;
				MathUtils.summary(header, bars);
			}
			
			if (!isPrinted
					&& (debug==1 || debug==5)
					){
				System.out.println(
						header2
						+" || "
						+count+" "+losses
						+" "+PrintUtils.Print2dec(pf$$,false)
						+" "+PrintUtils.Print2dec(accLossAvg,false)
						+" "+PrintUtils.Print2dec(maxAccLoss,false)
						+" "+PrintUtils.Print2dec(avgDayTrades,false)
						+" "+maxLosses
						);
			}
			
			if (!isPrinted && debug==20){
			System.out.println(
					header
					+" || "
					+count+" "+losses
					+" "+PrintUtils.Print2dec(pf$$,false)
					+" "+PrintUtils.Print2dec(accLossAvg,false)
					+" "+PrintUtils.Print2dec(maxAccLoss,false)
					+" "+PrintUtils.Print2dec(avgDayTrades,false)
					+" "+maxLosses
					);
			}
		}
		
		return pf$$;
	}
	
	private static double test1b$$(String header, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			int y1, int y2, 
			int m1,int m2,
			int h1, int h2,
			int minDiff,
			int thrTp,
			int minTp,
			int touchesThr,
			int nbars,
			int maxPips,
			double maxAccLossAllowed,
			int debug) {
		
		
		Calendar cal = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count = 0;
		int dayOpen = 0;

		int accBars = 0;
		int countUp = 0;
		int countDown = 0;
		int counth = 0;
		int lasth=-1;
		int lasth1=-1;
		int count5 = 0;
		int lastDay = -1;
		//int touchesThr = 3;
		ArrayList<Integer> bars = new ArrayList<Integer>();
		ArrayList<Integer> touches = new ArrayList<Integer>();
		dayOpen = 0;
		
		double totalWinSize = 0;
		double totalLossSize = 0;
		double maxAccLoss = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		double accLossSum=0;
		double factorSize = (2*thrTp)*1.0/(minTp);
		//1u = minTP, xu = 20.0;
		double baseComm = 15.0/minTp;
		int losses=0;
		int high = -1;
		int low = -1;
		
		ArrayList<Integer> dayTrades = new ArrayList<Integer>();
		int maxDay = (30)*365;//30 años..
		for (int i=0;i<=maxDay;i++) dayTrades.add(0);
		
		for (int i=1;i<data.size()-1;i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (day!=lastDay) {			
				dayOpen = q.getOpen5();
				lasth1=-1;
				lastDay = day;
				high = -1;
				low = -1;
			}
			
			int range = high-low;
			
		
						
			if (h>=h1 && h<=h2 
					&& (
							min==0 
						//|| min==30 
						//|| min==15 || min==45
						) 
					//&& range>=minDiff
					//&& sec==0
					//&& h!=lasth1 //1 cada hora
					){
				
				TradingUtils.getMaxMinShort(data, qm, calqm, i-nbars, i-1);
				int rangeBars = qm.getHigh5()-qm.getLow5();
				
				if (true
						&& rangeBars>=maxPips
						){
					int entry = q.getOpen5();
					
					int thrUph = entry+thrTp;
					int thrDownh = entry-thrTp;
					int thrUp = thrUph+minTp;
					int thrDown = thrDownh-minTp;
										
					int valid = 0;
					lasth=0;
					int totaltouches =0;
					
					double size= 1.0;
					double accLossSize = 0.0;
					double accComm = 0.0;
					
					if (debug==1)
						System.out.println("***TEST***: "+entry+" "+thrUp+" "+thrDown+" || "+thrUph+" "+thrDownh);
					int actualDay = -1;
					int lastActualDay = -1;
					for (int j=i;j<data.size();j++){
						if (data.get(j).getHigh5()>=thrUph){
							
							//hay trade para este dia
							QuoteShort.getCalendar(calqm, data.get(j));
							actualDay = calqm.get(Calendar.DAY_OF_YEAR);
							int year = calqm.get(Calendar.YEAR);
							int refDay = (year-y1+1)*365+actualDay;
							if (actualDay!=lastActualDay){
								int actualCount = dayTrades.get(refDay);
								dayTrades.set(refDay, actualCount+1);
								lastActualDay =actualDay;//para no volver a sumar en este dia
							}
							
							valid = 0;
							if (lasth==-1 || lasth==0){
								counth++;
								totaltouches++;
								valid=1;
								accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							}
							lasth = 1;
							
							
							if (valid==1){
								if (debug==1){
									System.out.println("touched up0 : "+counth+" "+valid
											+" "+range
											+" || dayCount= "+dayTrades.get(refDay)+ "("+refDay+")"
											+" || "+PrintUtils.Print2dec(accLossSize,false)
											+" || "+q.getOpen5()
											+" || "+data.get(j).toString()
											
											);
								}
							}
							
						}else if (data.get(j).getLow5()<=thrDownh){
							
							//hay trade para este dia
							QuoteShort.getCalendar(calqm, data.get(j));
							actualDay = calqm.get(Calendar.DAY_OF_YEAR);
							int year = calqm.get(Calendar.YEAR);
							int refDay = (year-y1+1)*365+actualDay;
							if (actualDay!=lastActualDay){								
								int actualCount = dayTrades.get(refDay);
								dayTrades.set(refDay, actualCount+1);
								lastActualDay =actualDay;//para no volver a sumar en este dia
							}
							
							
							valid = 0;
							if (lasth==1 || lasth==0){
								counth++;
								totaltouches++;
								valid=1;
								accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							}
							lasth = -1;

							if (valid==1){
								if (debug==1){
									System.out.println("touched down0 : "+counth+" "+valid
											+" "+range
											+" || dayCount= "+dayTrades.get(refDay)
											+" || "+PrintUtils.Print2dec(accLossSize,false)
											+" || "+q.getOpen5()
											+" || "+data.get(j).toString()
											);
								}
							}
							
						}
						
						
						
						if (data.get(j).getHigh5()>=thrUp){
							count++;
							countUp++;//continuacion
							accBars+=j-i;
							bars.add(j-i);
							int finishMode = -1;
							accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							accLossSum += accLossSize;
							double sizePlayed = (accLossSize+1.0)+(accLossSize+1.0)*baseComm;	
							if (totaltouches>=touchesThr){
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalLossSize += accLossSize;
								finishMode = 0;
								count5++;
							}else{
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalWinSize += sizePlayed-accLossSize-(sizePlayed*baseComm);
								finishMode = 1;
							}
							if (debug==1){
								System.out.println("break Up "+finishMode
										+" || "+count+" "+losses
										+" || "+PrintUtils.Print2dec(sizePlayed-accLossSize-(sizePlayed*baseComm),false)
										+"  "+PrintUtils.Print2dec(sizePlayed,false)
										+"  "+PrintUtils.Print2dec(accLossSize,false)
										+"  "+PrintUtils.Print2dec(baseComm,false)
										+" || "+PrintUtils.Print2dec(totalWinSize,false)
										+" || "+PrintUtils.Print2dec(totalLossSize,false)
										+" || "+data.get(j).toString() 
										);
							}
							touches.add(totaltouches);
							
							actualLosses = 0;
							
							//i=j;
							break;
						}else if (data.get(j).getLow5()<=thrDown){
							count++;
							countDown++;//no continuacion
							accBars+=j-i;
							bars.add(j-i);
							int finishMode = -1;
							accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
							accLossSum += accLossSize;
							double sizePlayed = (accLossSize+1.0)+(accLossSize+1.0)*baseComm;	
							if (totaltouches>=touchesThr){
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalLossSize += accLossSize;
								count5++;
							}else{
								if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
								totalWinSize += sizePlayed-accLossSize-(sizePlayed*baseComm);
								finishMode = 1;
							}
							if (debug==1){
								System.out.println("break down "
										+" || "+count+" "+losses
										+" || "+PrintUtils.Print2dec(sizePlayed-accLossSize-(sizePlayed*baseComm),false)
										+"  "+PrintUtils.Print2dec(sizePlayed,false)
										+"  "+PrintUtils.Print2dec(accLossSize,false)
										+"  "+PrintUtils.Print2dec(baseComm,false)
										+" || "+PrintUtils.Print2dec(totalWinSize,false)
										+" || "+PrintUtils.Print2dec(totalLossSize,false)
										+" || "+data.get(j).toString()  										
										);
							}
							touches.add(totaltouches);
							if (totaltouches>=touchesThr) count5++;
							
							actualLosses = 0;
							//i=j;
							break;
						}
						
						//tercera condición de salida, que se haya llegado al stop
						accLossSize = TradingUtils.calculateAccLoss(totaltouches,factorSize,baseComm);
						if (accLossSize>=maxAccLoss){
							//System.out.println("[NEW RECORD LOSS] "
							//		+"  "+PrintUtils.Print2dec(accLossSize,false)
							//);
						}
						
						if (totaltouches>=touchesThr
								|| accLossSize>=(maxAccLossAllowed/1.0)
								){
							
							accLossSum += accLossSize;
							if (accLossSize>=maxAccLoss) maxAccLoss = accLossSize;
							totalLossSize += accLossSize;
							count5++;
							losses++;
							if (debug==1){
								System.out.println("***STOP ALCANZADO***"+" "+range
										+" || "+count+" "+losses
										+" || "+PrintUtils.Print2dec(totalWinSize,false)
										+" || "+PrintUtils.Print2dec(totalLossSize,false)
										+" || "+data.get(j).toString() 
										);
							}
							
							actualLosses++;
							
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							//i=j;
							break;//salimos
						}
					}
				}
				
				lasth1=h;
			}//h
			
			//high low da
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}

			
		}//for
		
		//estudio de dayTrades
		int accdaytrades = 0;
		int countdays=0;
		int maxcount = 0;
		for (int i=0;i<dayTrades.size();i++){
			int countday = dayTrades.get(i);
			if (countday>=1){
				accdaytrades+=countday;
				countdays++;
				if (countday>=maxcount) maxcount = countday;
			}
		}
		
		
		int wins = count-count5;
		double factor = (2*thrTp)*1.0/(minTp);
		double count5loss = TradingUtils.calculateAccLoss(touchesThr,factor,baseComm);
		double pf = wins*1.0/(count5*count5loss);

		double pf$$ = totalWinSize/totalLossSize;
		
		if (totalLossSize==0 && totalWinSize>=100){
			pf$$ = 9.0;//convencion
		}
		
		double avg = accBars*1.0/count;
		
		double per = countUp*100.0/(countUp+countDown);
		
		double dt = Math.sqrt(MathUtils.variance(touches));
		
		double avgDayCount = accdaytrades*1.0/countdays;
		
		double accLossAvg = accLossSum/(count+losses);
		
		double benchmark = avgDayCount*accLossAvg;
		
		double benchmark2 = wins*1.0/benchmark;
		
		double avgDayTrades = accdaytrades*1.0/countdays;
		
		String header2 = h1+" "+h2
				+" "+minDiff+" "+minTp+" "+thrTp+" "+touchesThr
				+" "+PrintUtils.Print2dec(maxAccLossAllowed,false)
				//+" ||| "
				+" || "
				+count
				+" "+(count-losses)
				+" "+losses
				+" "+PrintUtils.Print2dec(pf$$,false)
				+" "+PrintUtils.Print2dec(accLossAvg,false)
				+" "+PrintUtils.Print2dec(maxAccLoss,false)
				+" "+PrintUtils.Print2dec(avgDayTrades,false)
				+" "+maxLosses
				+" || "+PrintUtils.Print2dec(maxLosses*maxAccLoss,false)
				+" || "+PrintUtils.Print2dec(count*1.0/(maxLosses*maxAccLoss),false)
				//+" || "+count+" "+PrintUtils.Print2dec(avg,false)+" "+losses
				//+" || "+maxcount
				
				//+" "+PrintUtils.Print2dec(benchmark,false)
				//+" || "+PrintUtils.Print2dec(benchmark2,false)
				//+" || "+counth
				//+" "+PrintUtils.Print2dec(counth*1.0/count,false)
				//+" "+PrintUtils.Print2dec(dt,false)
				//+" "+count5
				//+" || "+PrintUtils.Print2dec(pf,false)
				//+" "+PrintUtils.Print2dec(factor,false)
				//+" "+PrintUtils.Print2dec(count5loss,false)
				
				//+" "+PrintUtils.Print2dec(totalWinSize,false)
				//+" "+PrintUtils.Print2dec(totalLossSize,false)
				//+" || "+PrintUtils.Print2dec(accLossAvg,false)
				//+" || "+PrintUtils.Print2dec(maxAccLoss,false)
				//+" || "+PrintUtils.Print2dec(per,false)
				+" || "
				;
		
		if (debug!=40){
			boolean isPrinted = false;
			if (debug!=20 && debug!=5 && maxAccLoss<=200.0
					&& (
							((pf$$>=4.6 && benchmark2>=10.0)
							|| (maxAccLoss<=80.0 && pf$$>=1.8 && maxLosses*maxAccLoss<=400)
							//|| (maxAccLoss<=200.0 && pf$$>=2.5 && benchmark2>=4.0)
							)
						)
				){
				isPrinted = true;
				MathUtils.summary(header, bars);
			}
			
			if (!isPrinted
					&& (debug==1 || debug==5)
					){
				System.out.println(
						header2
						+" || "
						+count+" "+losses
						+" "+PrintUtils.Print2dec(pf$$,false)
						+" "+PrintUtils.Print2dec(accLossAvg,false)
						+" "+PrintUtils.Print2dec(maxAccLoss,false)
						+" "+PrintUtils.Print2dec(avgDayTrades,false)
						+" "+maxLosses
						);
			}
			
			if (!isPrinted && debug==20){
			System.out.println(
					header
					+" || "
					+count+" "+losses
					+" "+PrintUtils.Print2dec(pf$$,false)
					+" "+PrintUtils.Print2dec(accLossAvg,false)
					+" "+PrintUtils.Print2dec(maxAccLoss,false)
					+" "+PrintUtils.Print2dec(avgDayTrades,false)
					+" "+maxLosses
					);
			}
		}
		
		return pf$$;
	}
	
	private static void test1(String string, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			int y1, int y2, 
			int m1,int m2,
			int h1, int h2,
			int minDiff,
			int thrTp,
			int minTp,
			int touchesThr,
			int debug) {
		
		
		Calendar cal = Calendar.getInstance();	
		int count = 0;
		int dayOpen = 0;

		int accBars = 0;
		int countUp = 0;
		int countDown = 0;
		int counth = 0;
		int lasth=-1;
		int lasth1=-1;
		int count5 = 0;
		int lastDay = -1;
		int high = -1;
		int low = -1;
		//int touchesThr = 3;
		ArrayList<Integer> bars = new ArrayList<Integer>();
		ArrayList<Integer> touches = new ArrayList<Integer>();
		dayOpen = 0;
		for (int i=1;i<data.size()-1;i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (day!=lastDay) {			
				dayOpen = q.getOpen5();
				lasth1=-1;
				lastDay = day;
				high = -1;
				low = -1;
			}
			
			int range = high-low;
			
			
			if (h>=h1 && h<=h2 
					&& min==0 
					//&& sec==0
					//&& h!=lasth1 //1 cada hora
					){
				if (range<=minDiff){
					int entry = q.getOpen5();
					
					int thrUph = entry+thrTp;
					int thrDownh = entry-thrTp;
					int thrUp = thrUph+minTp;
					int thrDown = thrDownh-minTp;
					
					
					int valid = 0;
					lasth=0;
					int totaltouches =0;
					if (debug==1)
					System.out.println("***TEST***: "+entry+" "+thrUp+" "+thrDown+" || "+thrUph+" "+thrDownh);
					for (int j=i;j<data.size();j++){
						if (data.get(j).getHigh5()>=thrUph){
							valid = 0;
							if (lasth==-1 || lasth==0){
								counth++;
								totaltouches++;
								valid=1;
							}
							lasth = 1;
							
							if (valid==1){
								if (debug==1)
								System.out.println("touched up : "+counth+" "+valid
										+" || "+q.getOpen5()
										+" || "+data.get(j).toString()
										
										);
							}
						}else if (data.get(j).getLow5()<=thrDownh){
							valid = 0;
							if (lasth==1 || lasth==0){
								counth++;
								totaltouches++;
								valid=1;
							}
							lasth = -1;
							
							if (valid==1){
								if (debug==1)
								System.out.println("touched down : "+counth+" "+valid
										+" || "+q.getOpen5()
										+" || "+data.get(j).toString()
										);
							}
						}
						
						if (data.get(j).getHigh5()>=thrUp){
							count++;
							countUp++;//continuacion
							accBars+=j-i;
							bars.add(j-i);
							if (debug==1)
							System.out.println("break Up "+data.get(j).toString() );
							
							touches.add(totaltouches);
							if (totaltouches>=touchesThr) count5++;
							break;
						}else if (data.get(j).getLow5()<=thrDown){
							count++;
							countDown++;//no continuacion
							accBars+=j-i;
							bars.add(j-i);
							if (debug==1)
							System.out.println("break down "+data.get(j).toString() );
							
							touches.add(totaltouches);
							if (totaltouches>=touchesThr) count5++;
							break;
						}
					}
				}else if (range>=minDiff){
					int entry = q.getOpen5();
					
					int thrUph = entry+thrTp;
					int thrDownh = entry-thrTp;
					
					int thrUp = thrUph+minTp;
					int thrDown = thrDownh-minTp;
					
					int valid = 0;
					lasth=0;
					int totaltouches =0;
					if (debug==1)
					System.out.println("***TEST***: "+entry+" "+thrUp+" "+thrDown+" || "+thrUph+" "+thrDownh);
					for (int j=i;j<data.size();j++){
						
						if (data.get(j).getHigh5()>=thrUph){
							valid = 0;
							if (lasth==-1 || lasth==0){
								counth++;
								totaltouches++;
								valid=1;
							}
							lasth = 1;
							
							if (valid==1){
								if (debug==1)
								System.out.println("touched up : "+counth+" "+valid
										+" || "+q.getOpen5()
										+" || "+data.get(j).toString()										
										);
							}
						}else if (data.get(j).getLow5()<=thrDownh){
							valid = 0;
							if (lasth==1 || lasth==0){
								counth++;
								totaltouches++;
								valid = 1;
							}
							lasth = -1;
							
							if (valid==1){
								if (debug==1)
								System.out.println("touched down : "+counth+" "+valid
										+" || "+q.getOpen5()
										+" || "+data.get(j).toString()
										);
							}
						}
						
						if (data.get(j).getHigh5()>=thrUp){
							count++;
							countDown++; //no continuacion
							accBars+=j-i;
							bars.add(j-i);
							touches.add(totaltouches);
							if (totaltouches>=touchesThr) count5++;
							if (debug==1)
							System.out.println("break Up "+data.get(j).toString() );
							
							break;
						}else if (data.get(j).getLow5()<=thrDown){
							count++;
							countUp++;//continuacion
							accBars+=j-i;
							bars.add(j-i);
							touches.add(totaltouches);
							if (totaltouches>=touchesThr) count5++;
							if (debug==1)
							System.out.println("break down "+data.get(j).toString() );
							break;
						}
					}
				} 
				
				lasth1=h;
			}//h
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
			
		}//for
		
		
		int wins = count-count5;
		double factor = (2*thrTp)*1.0/(minTp);
		double count5loss = TradingUtils.calculateAccLoss(touchesThr,factor,0.0);
		double pf = wins*1.0/(count5*count5loss);
		
		double avg = accBars*1.0/count;
		
		double per = countUp*100.0/(countUp+countDown);
		
		double dt = Math.sqrt(MathUtils.variance(touches));
		String header = h1+" "+h2
				+" "+minDiff+" "+minTp+" "+thrTp+" "+touchesThr
				+" ||| "
				+count+" "+PrintUtils.Print2dec(avg,false)
				+" || "+counth
				+" "+PrintUtils.Print2dec(counth*1.0/count,false)
				+" "+PrintUtils.Print2dec(dt,false)
				+" "+count5
				+" || "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(factor,false)
				+" "+PrintUtils.Print2dec(count5loss,false)
				+" || "+PrintUtils.Print2dec(per,false)
				;
		MathUtils.summary_completeInt(header, touches);
		
		/*System.out.println(
				h1+" "+h2
				+" "+minDiff+" "+minTp
				+" || "
				+count+" "+PrintUtils.Print2dec(avg,false)
				);*/
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.12.04.csv";
		String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2008.12.31_2018.01.02.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_30 Secs_Bid_2012.12.31_2017.12.11.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.12.04.csv";
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
			
			ArrayList<Integer> zzs = new ArrayList<Integer>(); 
			
			//eurusd 100 16 200 30
			//gbpusd  50 18 200 30
			//audusd 100 10 150 30
			//usdjpy  50 7 300 30
				for (int h1=0;h1<=23;h1++){
					for (int h2=h1+0;h2<=h1+0;h2++){
					System.out.println("testing...hour..."+h1);
					for (int minDiff=0;minDiff<=0;minDiff+=10){
						for (int thrTp=100;thrTp<=100;thrTp+=50){
							//System.out.println("testing..."+thrTp);
							for (int factor=10;factor<=10;factor+=1){
								//for (int minTp=factor*(2*thrTp);minTp<=factor*(2*thrTp);minTp+=1*thrTp){
								for (int minTp=factor*thrTp;minTp<=factor*thrTp;minTp+=1*thrTp){
									for (double aRisk=0.20;aRisk<=0.20;aRisk+=0.05){
										for (double maxAllowedLoss=50000.0;maxAllowedLoss<=50000.0;maxAllowedLoss+=50.0){
											for (int maxTransactions = 20;maxTransactions<=20;maxTransactions+=5){
												for (int aRange=9999;aRange<=9999;aRange+=200){
													for (int aMin=0;aMin<=0;aMin+=5){
														for (double aBalance=50000;aBalance<=50000;aBalance+=1000){
															for (int aComm=20;aComm<=20;aComm+=10){
																for (int maxStages=25;maxStages<=25;maxStages+=5){
																	int count = 0;
																	for (int y1=2009;y1<=2009;y1++){
																		int y2 = y1+8;
																		for (int m1=0;m1<=0;m1+=1){
																			int m2 = m1+11;
																			double pf =TestRanges.testTransactionsMargin("mStages= "+maxStages,data, maxMins, y1, y2,
																					//TestRanges.test1("",data, maxMins, 2012, 2017,
																							m1,m2,
																							h1, h2,0,
																							thrTp, minTp,
																							maxAllowedLoss,
																							maxStages,
																							maxTransactions,
																							aRisk,
																							10.0,
																							aRange,
																							aBalance,																					
																							50,
																							aComm,
																							0);
																			
																			if (pf>=1.0){
																				count++;
																			}	
																		}
																	}
																	
																	if (count>=50){
																		double pf =TestRanges.testTransactionsMargin("c="+count,data, maxMins, 2017, 2017,
																					//TestRanges.test1("",data, maxMins, 2012, 2017,
																							0,11,
																							h1, h2,
																							aMin,
																							thrTp, minTp,
																							maxAllowedLoss,
																							1000,
																							maxTransactions,
																							aRisk,
																							10.0,
																							aRange,
																							aBalance,
																							50,
																							aComm,
																							0);
																	}
																}//maxStages
															}//acomm
														}//aBalance
													}//amin
												}//arANGE
											}//maxtransactions
										}//maxAllowedLoss
									}//touches
								}//tp
							}//diff
						}//h2
					}//h1
				}
			}
			
			
		}
					
	}

}
