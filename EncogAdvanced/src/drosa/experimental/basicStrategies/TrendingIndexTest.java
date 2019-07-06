package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrendingIndexTest {
	
	public static void doTest9(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int closeStage,
			int tp,int sl,
			int maxAllowedLosses,
			double risk,
			int spread,
			double pipsComm,
			int debug
			){
		
		double balance = 1000;
		double riskPerPosition =risk; 
		double factor = (tp*1.0/(sl+pipsComm));
		double maxBalance = 1000;
		double maxDD = 0;
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int partialLosses = 0;
		int partialLostPips = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips1 = 0;
		int lostPips1 = 0;
		double accSizes = 0;
		double winPips = 0;
		double lostPips = 0;
	
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		double tradeEntryIndex = 0;
		int tradeEntryMode = 0;
	
		int actualStage = -1;
		int lastStage = -1;
		
		int tradeTP = 0;
		int tradeSL = 0;
		int maxStage = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int maxLosses0 = 0;
		int actualLosses0 = 0;
		double tradeLots = 0;
		double accLoss0 = calculateLoss(closeStage,sl*1.0/tp);
		
		double lots = 0;
		double lotsLosses = 0;
		double lotsWins = 0;
		double lotsAccLosses = 0;
		double maxAccLosses = 0;
		
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			int maxMin = maxMins.get(i-1);
			
			if (h>=h1 && h<=h2
					//&& dayWeek == Calendar.MONDAY
					//&& dayWeek != Calendar.TUESDAY
					//&& dayWeek == Calendar.WEDNESDAY
					//&& dayWeek == Calendar.FRIDAY
					//&& dayWeek == Calendar.FRIDAY
					){
				
				if (true
						&& tradeEntryMode==0
						//&& trendIndex>=test
						&& maxMin>=thr
						//&& q1.getClose5()<q1.getOpen5()
						){
					tradeEntryMode = -1;
					tradeEntryIndex = trendIndex;
					tradeEntry = q.getOpen5();
					tradeTP = tradeEntry - tp;//-20;
					tradeSL = tradeEntry + sl-spread;
					actualStage = 1;
					if (debug==5)
					System.out.println("[SHORT] "
							+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
							+" || "+q.toString()
					);
				}
				if (true
						&& tradeEntryMode==0
						//&& trendIndex<=-test
						&& maxMin<=-thr
						//&& q1.getClose5()>q1.getOpen5()
						){
					tradeEntryMode = 1;
					tradeEntry = q.getOpen5();
					tradeEntryIndex = trendIndex;
					tradeTP = tradeEntry + tp;//+20;
					tradeSL = tradeEntry - sl+spread;
					actualStage = 1;
					if (debug==5)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
							+" || "+q.toString()
					);
				}				
			}
			
			actualLosses = 0;
			if (tradeEntryMode!=0){
				int res =0;
				double pips = 0;
				for (int j=i;j<=data.size()-1;j++){
					q = data.get(j);
					if (tradeEntryMode == 1){//vamos long
						if (q.getLow5()<=tradeSL){
							if (actualStage>=maxStage) maxStage = actualStage;
							if (actualStage==closeStage 
									){
								losses++;
								actualStage=0;
								tradeEntryMode = 0;
								actualLosses++;
								if (actualLosses>=maxLosses) maxLosses= actualLosses;
								res=-1;
								break;
							}else{					
								actualStage++;
								if (actualStage>=maxStage) maxStage = actualStage;
								tradeEntry = q.getClose5();
								tradeEntryMode = 1;
								tradeEntryIndex = trendIndex;
								tradeTP = tradeEntry + tp;//+20;
								tradeSL = tradeEntry - sl+spread;			
							}//actualStage
						}else if (q.getHigh5()>=tradeTP){
							wins++;					
							tradeEntryMode = 0;
							actualLosses = 0;				
							actualStage=0;
							res=1;
							break;
							//winLots += pips/tp;
						}		
					}else if (tradeEntryMode == -1){
						if (q.getHigh5()>=tradeSL){
							if (actualStage>=maxStage) maxStage = actualStage;
							//System.out.println(actualStage);
							if (actualStage==closeStage
									){
								losses++;
								actualStage=0;
								tradeEntryMode = 0;
								actualLosses++;
								res=-1;
								break;
							}else{	
								actualStage++;
								if (actualStage>=maxStage) maxStage = actualStage;
								tradeEntry = q.getClose5();
								tradeEntryMode = -1;
								tradeEntryIndex = trendIndex;
								tradeTP = tradeEntry - tp;//-20;
								tradeSL = tradeEntry + sl-spread;
							}
						}else if (q.getLow5()<=tradeTP){
							wins++;
							tradeEntryMode = 0;
							actualLosses = 0;
							actualStage=0;
							res = 1;
							break;
						}										
					}											
				}//for
				if (res==-1){
					actualLosses0++;
					if (actualLosses0>=maxLosses0) maxLosses0 =actualLosses0;
					
					pips = sl+pipsComm;
					double lossPer = riskPerPosition*pips/sl;
					balance = balance*(1-lossPer/100.0);
					
					double dd = 100.0-balance*100.0/maxBalance;
					if (dd>=maxDD) maxDD = dd;
					
					lostPips += pips;
				}else if (res==1){
					actualLosses0=0;
					pips = tp-pipsComm;
					double winPer = riskPerPosition*pips/sl;
					balance = balance*(1+(winPer/100.0));
					
					if (balance>=maxBalance) maxBalance = balance;
					
					if (pips>=0){
						winPips+=pips;
					}else{
						lostPips += -pips;
					}
				}
			}
		}
		
		int total = wins+losses;
		double pf = winPips1*1.0/lostPips1;
		double winPer = wins*100.0/total;
		double avg = (winPips1-lostPips1)*0.1/total;
		double avgWin = winPips1*0.1/wins;
		double avgLoss = lostPips1*0.1/(losses);
		double avgPartialLoss = partialLostPips*0.1/(partialLosses);
		 factor = sl*1.0/tp;
		
		//factor = avgPartialLoss/avgWin;
		double accLoss2 = calculateLoss(maxStage,factor);
		
		if (closeStage>1)
			pf = wins*1.0/(losses*accLoss2);
		
		pf = lotsWins/lotsLosses;
		//accLoss2 = maxAccLosses;
		
		double pf2 = (wins*1.0)/(losses*accLoss2);
		pf =(wins*(tp-pipsComm)*1.0)/(losses*(sl+pipsComm));
		pf = winPips/lostPips;
		avg =(winPips-lostPips)*0.1/total;
		//double money = 1000*(1+)
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+closeStage
				+" "+thr
				+" "+tp+" "+sl+" "+PrintUtils.Print2dec(factor,false)
				+" || "
				+" "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+maxStage
				+" "+PrintUtils.Print2dec(factor,false)
				+" || "+PrintUtils.Print2dec(pf,false)
				+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(avg,false)
				+" || "+PrintUtils.Print2dec(pf2,false)
				//+PrintUtils.Print2dec(lotsWins,false)
				//+" "+PrintUtils.Print2dec(lotsLosses,false)
				//+" "+PrintUtils.Print2dec(maxAccLosses,false)
				+" || "
				//+" "+PrintUtils.Print2dec(lotsLosses/losses,false)
				
				//+" || "+PrintUtils.Print2dec(avgWin,false)
				//+" || "+PrintUtils.Print2dec(avgLoss,false)
				//+" || "+PrintUtils.Print2dec(avgPartialLoss,false)
				+" || "+maxLosses0
				//+" || "+winPips+" "+lostPips
				//+" || "+PrintUtils.Print2dec(avgWin,false)
				//+" "+PrintUtils.Print2dec(avgLoss,false)
				//+" || "+PrintUtils.Print2dec(factor,false)
				+" || "+PrintUtils.Print2dec(accLoss2,false)
				//+" || "+PrintUtils.Print2dec(winPips*1.0/lostPips,false)
				//+" || "+PrintUtils.Print2dec((winPips-lostPips)*0.1/total,false)
				+" || "+PrintUtils.Print2dec2(balance,true)
				+" "+PrintUtils.Print2dec2(maxBalance,true)
				+" "+PrintUtils.Print2dec(maxDD,false)
				//+" ||| "+PrintUtils.Print2dec(maxLots,false)
				//+" ||| "+PrintUtils.Print2dec(maxLoss*0.1,false)
		);
	}
	
	public static void doTest8(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int closeStage,
			int tp,int sl,
			int maxAllowedLosses,
			double risk,
			int spread,
			int debug
			){
		
		double balance = 1000;
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int partialLosses = 0;
		int partialLostPips = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips1 = 0;
		int lostPips1 = 0;
		double accSizes = 0;
	
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		double tradeEntryIndex = 0;
		int tradeEntryMode = 0;
	
		int actualStage = -1;
		int lastStage = -1;
		
		int tradeTP = 0;
		int tradeSL = 0;
		int maxStage = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		double tradeLots = 0;
		double accLoss0 = calculateLoss(closeStage,sl*1.0/tp);
		
		double lots = 0;
		double lotsLosses = 0;
		double lotsWins = 0;
		double lotsAccLosses = 0;
		double maxAccLosses = 0;
		int totalAmbiguas=0;
		
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			int maxMin = maxMins.get(i-ref);
			
			if (tradeEntryMode==0 
					&& h>=h1 && h<=h2
					//&& dayWeek == Calendar.MONDAY
					//&& dayWeek == Calendar.TUESDAY
					){
				if (true
						//&& trendIndex>=test
						&& maxMin>=thr
						){
					tradeEntryMode = -1;
					tradeEntryIndex = trendIndex;
					tradeEntry = q.getClose5();
					tradeTP = tradeEntry - tp;//-20;
					tradeSL = tradeEntry + sl-spread;
					actualStage = 1;
					if (debug==5)
					System.out.println("[SHORT] "
							+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
							+" || "+q.toString()
					);
				}else if (true
						//&& trendIndex<=-test
						&& maxMin<=-thr
						){
					tradeEntryMode = 1;
					tradeEntry = q.getClose5();
					tradeEntryIndex = trendIndex;
					tradeTP = tradeEntry + tp;//+20;
					tradeSL = tradeEntry - sl+spread;
					actualStage = 1;
					if (debug==5)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
							+" || "+q.toString()
					);
				}				
			}else if (tradeEntryMode == 1){//vamos long
				if (q.getLow5()<=tradeSL && q.getHigh5()>=tradeTP){
					totalAmbiguas++;
					tradeEntryMode = 0;
					actualLosses = 0;				
					actualStage=0;
				}
				
				if (q.getLow5()<=tradeSL){
					if (actualStage>=maxStage) maxStage = actualStage;
					if (actualStage==closeStage 
							){
						losses++;
						actualStage=0;
						tradeEntryMode = 0;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses= actualLosses;
					}else{					
						actualStage++;
						if (actualStage>=maxStage) maxStage = actualStage;
						tradeEntry = q.getClose5();
						tradeEntryMode = 1;
						tradeEntryIndex = trendIndex;
						tradeTP = tradeEntry + tp;//+20;
						tradeSL = tradeEntry - sl+spread;			
					}//actualStage
				}else if (q.getHigh5()>=tradeTP){
					wins++;					
					tradeEntryMode = 0;
					actualLosses = 0;				
					actualStage=0;
					//winLots += pips/tp;
				}		
			}else if (tradeEntryMode == -1){
				if (q.getLow5()<=tradeTP && q.getHigh5()>=tradeSL){
					totalAmbiguas++;
					tradeEntryMode = 0;
					actualLosses = 0;				
					actualStage=0;
				}
				if (q.getHigh5()>=tradeSL){
					if (actualStage>=maxStage) maxStage = actualStage;
					//System.out.println(actualStage);
					if (actualStage==closeStage
							){
						losses++;
						actualStage=0;
						tradeEntryMode = 0;
						actualLosses++;
					}else{	
						actualStage++;
						if (actualStage>=maxStage) maxStage = actualStage;
						tradeEntry = q.getClose5();
						tradeEntryMode = -1;
						tradeEntryIndex = trendIndex;
						tradeTP = tradeEntry - tp;//-20;
						tradeSL = tradeEntry + sl-spread;
					}
				}else if (q.getLow5()<=tradeTP){
					wins++;
					tradeEntryMode = 0;
					actualLosses = 0;
					actualStage=0;
				}										
			}			
		}
		
		int total = wins+losses;
		double pf = winPips1*1.0/lostPips1;
		double winPer = wins*100.0/total;
		double avg = (winPips1-lostPips1)*0.1/total;
		double avgWin = winPips1*0.1/wins;
		double avgLoss = lostPips1*0.1/(losses);
		double avgPartialLoss = partialLostPips*0.1/(partialLosses);
		double factor = sl*1.0/tp;
		
		//factor = avgPartialLoss/avgWin;
		double accLoss2 = calculateLoss(maxStage,factor);
		
		if (closeStage>1)
			pf = wins*1.0/(losses*accLoss2);
		
		pf = lotsWins/lotsLosses;
		//accLoss2 = maxAccLosses;
		
		//pf = (wins*1.0)/(losses*accLoss2);
		
		//double money = 1000*(1+)
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+closeStage
				+" "+thr
				+" "+tp+" "+sl+" "+PrintUtils.Print2dec(factor,false)
				+" || "
				+" "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+maxStage
				+" "+PrintUtils.Print2dec(factor,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" || "
				+" "+totalAmbiguas
				//+PrintUtils.Print2dec(lotsWins,false)
				//+" "+PrintUtils.Print2dec(lotsLosses,false)
				//+" "+PrintUtils.Print2dec(maxAccLosses,false)
				+" || "
				//+" "+PrintUtils.Print2dec(lotsLosses/losses,false)
				
				//+" || "+PrintUtils.Print2dec(avgWin,false)
				//+" || "+PrintUtils.Print2dec(avgLoss,false)
				//+" || "+PrintUtils.Print2dec(avgPartialLoss,false)
				+" || "+maxLosses
				//+" || "+winPips+" "+lostPips
				//+" || "+PrintUtils.Print2dec(avgWin,false)
				//+" "+PrintUtils.Print2dec(avgLoss,false)
				//+" || "+PrintUtils.Print2dec(factor,false)
				+" || "+PrintUtils.Print2dec(accLoss2,false)
				//+" || "+PrintUtils.Print2dec(winPips*1.0/lostPips,false)
				//+" || "+PrintUtils.Print2dec((winPips-lostPips)*0.1/total,false)
				//+" || "+PrintUtils.Print2dec2(balance,true)
				//+" ||| "+PrintUtils.Print2dec(maxLots,false)
				//+" ||| "+PrintUtils.Print2dec(maxLoss*0.1,false)
		);
	}
	
	
	public static void doTest7(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int closeStage,
			int tp,int sl,
			int debug
			){
		
		double risk = 20.0;//towin
		double balance = 1000;
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int partialLosses = 0;
		int partialLostPips = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips1 = 0;
		int lostPips1 = 0;
		double accSizes = 0;
		int count2 = 0;
		int counta2 = 0;
		int counta3 = 0;
		int counta4=0;
		int counta5=0;
		int counta6=0;
		int counta7=0;
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		double tradeEntryIndex = 0;
		int tradeEntryMode = 0;
	
		int actualStage = -1;
		int lastStage = -1;
		
		int tradeTP = 0;
		int tradeSL = 0;
		int maxStage = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int spread = 20;
		double tradeLots = 0;
		double accLoss0 = calculateLoss(closeStage,sl*1.0/tp);
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			//double trendIndex1 = indexes.get(i-1);
			double trendIndex2 = indexes.get(i-ref);
			int maxMin = maxMins.get(i-ref);
			
			if (tradeEntryMode==0 
					&& h>=h1 && h<=h2
					
					){
				
				if (true
						//&& trendIndex>=test
						&& maxMin>=thr
						){
					tradeEntryMode = -1;
					tradeEntryIndex = trendIndex;
					tradeEntry = q.getClose5();
					tradeTP = tradeEntry - tp;//-20;
					tradeSL = tradeEntry + sl-spread;
					actualStage = 1;
					
					//para que si gana gane 1
					tradeLots = 1.0;
					if (debug==1)
					System.out.println("[SHORT] "
							+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
							+" || "+q.toString()
					);
				}else if (true
						//&& trendIndex<=-test
						&& maxMin<=-thr
						){
					tradeEntry = q.getClose5();
					tradeEntryMode = 1;
					tradeEntryIndex = trendIndex;
					tradeTP = tradeEntry + tp;//+20;
					tradeSL = tradeEntry - sl+spread;
					actualStage = 1;
					
					//para que si gana gane 1
					tradeLots = 1.0;
					if (debug==1)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
							+" || "+q.toString()
					);
				}				
			}else if (tradeEntryMode == 1){//vamos long
				int pips = q.getClose5()-tradeEntry;
				
				if (q.getClose5()<=tradeSL){//ha tocado SL
					if (actualStage>=maxStage) maxStage = actualStage;
					if (actualStage==closeStage){
						losses++;
						actualStage=0;
						tradeEntryMode = 0;
						//partialLosses++;
						//se pierde lo acumulado
						lostPips1 += -pips;//tp*accLoss0;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses= actualLosses;
						balance = balance *(1-(risk*accLoss0)/100);
						
						//lotLosses += lotAccLosses + lots
								
						//double lots = pips*-1.0/tp;
						//lotsLosses += lots;
						
						if (debug==2) {
							System.out.println("[LONG LOSS] "
								+pips+" "+winPips1+" "+lostPips1	
							);
						}
						
						if (debug==1)
							System.out.println("[LONG LOSS] "
									+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
									+" || "+pips
									+" || "+q.toString()
							);
					}else{					
						actualStage++;
						if (actualStage>=maxStage) maxStage = actualStage;
						tradeEntry = q.getClose5();
						tradeEntryMode = -1;
						tradeEntryIndex = trendIndex;
						tradeTP = tradeEntry + tp;//+20;
						tradeSL = tradeEntry - sl+spread;
						
						partialLosses++;
						partialLostPips += -pips;
						
						//basado en tp ajusto el factor actual
						
					}
				}else if (q.getClose5()>=tradeTP){
					winPips1 += pips;
					
					if (debug==1) {
						System.out.println("[LONG WIN] "
							+pips+" "+winPips1+" "+lostPips1+" || "+q.toString()	
						);
					}
					
					wins++;
					actualStage=0;
					tradeEntryMode = 0;
					actualLosses = 0;
					balance = balance*(1+risk/100);
					
					//winLots += pips/tp;
				}
							
			}else if (tradeEntryMode == -1){
				int pips = tradeEntry-q.getClose5();
				if (q.getClose5()>=tradeSL){
					if (actualStage>=maxStage) maxStage = actualStage;
					//System.out.println(actualStage);
					if (actualStage==closeStage){
						losses++;
						actualStage=0;
						tradeEntryMode = 0;
						//se pierde lo acumulado
						lostPips1 += -pips;//tp*accLoss0;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses= actualLosses;
						balance = balance *(1-(risk*accLoss0)/100);
						
						//winPips += pips;
						
						if (debug==2) {
							System.out.println("[SHORT LOSS] "
								+pips+" "+winPips1+" "+lostPips1	
							);
						}
						
						if (debug==1)
							System.out.println("[SHORT LOSS] "
									+trendIndex+" "+tradeEntry+" "+tradeTP+" "+tradeSL
									+" || "+pips
									+" || "+q.toString()
							);
					}else{	
						actualStage++;
						if (actualStage>=maxStage) maxStage = actualStage;
						tradeEntry = q.getClose5();
						tradeEntryMode = -1;
						tradeEntryIndex = trendIndex;
						tradeTP = tradeEntry - tp;//-20;
						tradeSL = tradeEntry + sl-spread;
						
						partialLosses++;
						partialLostPips += -pips;
						
						//tradeLots = tradeLots*sl/tp
					}
				}else if (q.getClose5()<=tradeTP){
					winPips1 += pips;
					wins++;
					actualStage=0;
					tradeEntryMode = 0;
					//se gana siempre uno, ajustando el lotsize
					//winPips += tp;
					actualLosses = 0;
					balance = balance*(1+risk/100);
					
					if (debug==1) {
						System.out.println("[SHORT WIN] "
							+pips+" "+winPips1+" "+lostPips1+" "+q.toString()	
						);
					}
					
					//winLots += pips*1.0/tp;
				}
															
			}			
		}
		
		int total = wins+losses;
		double pf = winPips1*1.0/lostPips1;
		double winPer = wins*100.0/total;
		double avg = (winPips1-lostPips1)*0.1/total;
		double avgWin = winPips1*0.1/wins;
		double avgLoss = lostPips1*0.1/(losses);
		double avgPartialLoss = partialLostPips*0.1/(partialLosses);
		double factor = sl*1.0/tp;
		
		factor = avgPartialLoss/avgWin;
		double accLoss2 = calculateLoss(maxStage,factor);
		
		if (closeStage>1)
			pf = wins*1.0/(losses*accLoss2);
		
		//double money = 1000*(1+)
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+closeStage
				+" "+thr
				+" "+tp+" "+sl+" "+PrintUtils.Print2dec(factor,false)
				+" || "
				+" "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+maxStage
				+" "+PrintUtils.Print2dec(factor,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" || "+PrintUtils.Print2dec(avgWin,false)
				+" || "+PrintUtils.Print2dec(avgLoss,false)
				+" || "+PrintUtils.Print2dec(avgPartialLoss,false)
				+" || "+maxLosses
				//+" || "+winPips+" "+lostPips
				//+" || "+PrintUtils.Print2dec(avgWin,false)
				//+" "+PrintUtils.Print2dec(avgLoss,false)
				//+" || "+PrintUtils.Print2dec(factor,false)
				+" || "+PrintUtils.Print2dec(accLoss2,false)
				//+" || "+PrintUtils.Print2dec(winPips*1.0/lostPips,false)
				//+" || "+PrintUtils.Print2dec((winPips-lostPips)*0.1/total,false)
				+" || "+PrintUtils.Print2dec2(balance,true)
				//+" ||| "+PrintUtils.Print2dec(maxLots,false)
				//+" ||| "+PrintUtils.Print2dec(maxLoss*0.1,false)
		);
	}
	
	public static void doTest6(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int closeStage,
			double closeDiff,
			int debug
			){
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int partialLosses = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips = 0;
		int lostPips = 0;
		double accSizes = 0;
		int count2 = 0;
		int counta2 = 0;
		int counta3 = 0;
		int counta4=0;
		int counta5=0;
		int counta6=0;
		int counta7=0;
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		double tradeEntryIndex = 0;
		int tradeEntryMode = 0;
		double stage_1 = 0;
		double stage1 = 0;
		double stage2 = 0;
		double stage3 = 0;
		double stage4 = 0;
		double stage5 = 0;
		double stage6 = 0;
		int actualStage = -1;
		int lastStage = -1;
		double actualLots = 1.0;
		double accLoss = 0;
		double maxLots = 1;
		double maxLoss = 0;
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			//double trendIndex1 = indexes.get(i-1);
			double trendIndex2 = indexes.get(i-ref);
			int maxMin = maxMins.get(i-ref);
			
			
			
			if (actualTrendMode==1){				
				if ( trendIndex<0){
					actualTrendMode = -1;
					/*System.out.println("[CHANGE TO DOWN] "
						//+DateUtils.datePrint(cal)
						//+" || "
						+trendIndex+"  "+trendIndex2
						+" "+q.toString()+" || "+qref.toString()
						);*/
				}
			}else if (actualTrendMode==-1){
				if ( trendIndex>0){
					actualTrendMode = 1;
					/*System.out.println("[CHANGE TO UP] "
							+trendIndex+"  "+trendIndex2
							+" "+q.toString()+" || "+qref.toString()
					);*/
				}
			}else{
				if ( trendIndex<0){
					actualTrendMode = -1;
				}else if ( trendIndex>0){
						actualTrendMode = 1;					
				}
			}
			
			if (tradeEntryMode==0 
					&& h>=h1 && h<=h2
					){
				
				if (trendIndex>=test){
					tradeEntry = q.getClose5();
					tradeEntryMode = -1;
					tradeEntryIndex = trendIndex;
					
					stage_1 = trendIndex-1;
					stage1 = trendIndex+1;
					stage2 = trendIndex+2;
					stage3 = trendIndex+3;
					stage4 = trendIndex+4;
					stage5 = trendIndex+5;
					stage6 = trendIndex+6;
					accLoss = 0;
					actualLots = 1.0;
					actualStage = 0;
					lastStage = 0;
					if (debug==1)
					System.out.println("[SHORT] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}else if (trendIndex<=-test){
					tradeEntry = q.getClose5();
					tradeEntryMode = 1;
					tradeEntryIndex = trendIndex;
					
					stage_1 = trendIndex+1;
					stage1 = trendIndex-1;
					stage2 = trendIndex-2;
					stage3 = trendIndex-3;
					stage4 = trendIndex-4;
					stage5 = trendIndex-5;
					stage6 = trendIndex-6;
					
					accLoss = 0;
					actualLots = 1;
					actualStage = 0;
					lastStage = 0;
					if (debug==1)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}				
			}else if (tradeEntryMode == 1){//vamos long
				
				double diffIndex = trendIndex - tradeEntryIndex;
				double actualWin = actualLots*(q.getClose5() - tradeEntry);//podria ser negativo
				double netPips = actualWin-accLoss;
				
				if (netPips<=-maxLoss) maxLoss = -netPips;
				
				if (true//diffIndex>=closeDiff 
						&& netPips>=2000
							//netPips>=50
						 //|| (actualStage>=2 && netPips>=0)
						){
					if (netPips>=0){
						winPips += netPips;
						if (debug==3)
							System.out.println("[WIN LONG STAGE "+actualStage+" ] "
									+trendIndex+" "+tradeEntryIndex+" "+( q.getClose5() - tradeEntry)
									+" || "
									+" "+PrintUtils.Print2dec(actualLots,false)
									+" "+PrintUtils.Print2dec(actualWin,false)
									+" || "+q.toString()
									
																
							);
						actualLots = 1.0;
						wins++;
						tradeEntryMode = 0;
					}else{
						losses++;
						tradeEntryMode = 0;
						actualLots = 1.0;
						lostPips += accLoss;
					}
				}else{
					if (diffIndex<=-1.0){
						int mult = (int) (diffIndex/-1.0);
						actualStage = actualStage+mult;
						int pips = q.getClose5() - tradeEntry; 
						//lostPips += -pips;
						partialLosses++;
						
						double actualLoss = actualLots*(-pips);
						accLoss += actualLoss;
						actualLots = actualLots*1.0;
						if (actualLots>=maxLots) maxLots=actualLots;
						if (debug==3)
						System.out.println("[LONG STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex
								+" || "
								+" "+PrintUtils.Print2dec(actualLoss,false)
								+" "+PrintUtils.Print2dec(accLoss,false)
								+" "+PrintUtils.Print2dec(actualLots,false)
								+" || "+q.toString()
								+" || "+pips
						);
						
						
						
						tradeEntryIndex = trendIndex;
						tradeEntry = q.getClose5();
						
						if (actualStage>=closeStage){
							losses++;
							tradeEntryMode = 0;
							actualLots = 1.0;
							lostPips += accLoss;
						}
					}
				}				
			}else if (tradeEntryMode == -1){
				
				double diffIndex = tradeEntryIndex-trendIndex;
				double actualWin = actualLots*(tradeEntry-q.getClose5());
				double netPips = actualWin-accLoss;
				
				if (netPips<=-maxLoss) maxLoss = -netPips;
				
				if (true
						//&& diffIndex>=closeDiff
						&& netPips>=2000
						//netPips>=50
						// || (actualStage>=2 && netPips>=0)
						){
					
					
					
					if (netPips>=0){
						winPips += netPips;
						if (debug==3)
							System.out.println("[WIN SHORT STAGE "+actualStage+" ] "
									+trendIndex+" "+tradeEntryIndex+" "+( q.getClose5() - tradeEntry)
									+" || "
									+" "+PrintUtils.Print2dec(actualLots,false)
									+" "+PrintUtils.Print2dec(actualWin,false)
									+" || "+q.toString()
									
																
							);
						actualLots = 1.0;
						wins++;
						tradeEntryMode = 0;
					}else{
						losses++;
						tradeEntryMode = 0;
						actualLots = 1.0;
						lostPips += accLoss;
					}
				}else{
					if (diffIndex<=-1.0){
						int mult = (int) (diffIndex/-1.0);
						actualStage = actualStage+mult;
						int pips = tradeEntry-q.getClose5(); 
						//lostPips += -pips;
						partialLosses++;
						double actualLoss = actualLots*(-pips);
						accLoss += actualLoss;
						actualLots = actualLots*1.0;
						if (actualLots>=maxLots) maxLots=actualLots;
						if (debug==3)
						System.out.println("[SHORT STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex
								+" || "
								+" "+PrintUtils.Print2dec(actualLoss,false)
								+" "+PrintUtils.Print2dec(accLoss,false)
								+" "+PrintUtils.Print2dec(actualLots,false)
								+" || "+q.toString()
								+" || "+pips
						);
						
						
						
						tradeEntryIndex = trendIndex;
						tradeEntry = q.getClose5();
						
						if (actualStage>=closeStage){
							losses++;
							tradeEntryMode = 0;
							actualLots = 1.0;
							lostPips += accLoss;
						}
					}										
				}			
			}
			
			
		}
		
		int total = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/(partialLosses);
		double factor = avgLoss/avgWin;
		
		double accLoss2 = calculateLoss(closeStage,factor);
		
		//pf = wins*1.0/(losses*accLoss);
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+closeStage
				+" "+PrintUtils.Print2dec(closeDiff,false)
				+" || "
				+" "+total+" "+wins+" "+losses+" "+partialLosses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(pf,false)
				//+" || "+winPips+" "+lostPips
				+" || "+PrintUtils.Print2dec(avgWin,false)
				+" "+PrintUtils.Print2dec(avgLoss,false)
				+" || "+PrintUtils.Print2dec(factor,false)
				+" || "+PrintUtils.Print2dec(accLoss2,false)
				+" ||| "+PrintUtils.Print2dec(maxLots,false)
				+" ||| "+PrintUtils.Print2dec(maxLoss*0.1,false)
		);
	}
	
	public static void doTest5(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int closeStage,
			double closeDiff,
			int debug
			){
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int partialLosses = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips = 0;
		int lostPips = 0;
		double accSizes = 0;
		int count2 = 0;
		int counta2 = 0;
		int counta3 = 0;
		int counta4=0;
		int counta5=0;
		int counta6=0;
		int counta7=0;
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		double tradeEntryIndex = 0;
		int tradeEntryMode = 0;
		double stage_1 = 0;
		double stage1 = 0;
		double stage2 = 0;
		double stage3 = 0;
		double stage4 = 0;
		double stage5 = 0;
		double stage6 = 0;
		int actualStage = -1;
		int lastStage = -1;
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			//double trendIndex1 = indexes.get(i-1);
			double trendIndex2 = indexes.get(i-ref);
			int maxMin = maxMins.get(i-ref);
			
			
			
			if (actualTrendMode==1){				
				if ( trendIndex<0){
					actualTrendMode = -1;
					/*System.out.println("[CHANGE TO DOWN] "
						//+DateUtils.datePrint(cal)
						//+" || "
						+trendIndex+"  "+trendIndex2
						+" "+q.toString()+" || "+qref.toString()
						);*/
				}
			}else if (actualTrendMode==-1){
				if ( trendIndex>0){
					actualTrendMode = 1;
					/*System.out.println("[CHANGE TO UP] "
							+trendIndex+"  "+trendIndex2
							+" "+q.toString()+" || "+qref.toString()
					);*/
				}
			}else{
				if ( trendIndex<0){
					actualTrendMode = -1;
				}else if ( trendIndex>0){
						actualTrendMode = 1;					
				}
			}
			
			if (tradeEntryMode==0 
					&& h>=h1 && h<=h2
					){
				
				if (trendIndex>=test){
					tradeEntry = q.getClose5();
					tradeEntryMode = -1;
					tradeEntryIndex = trendIndex;
					
					stage_1 = trendIndex-1;
					stage1 = trendIndex+1;
					stage2 = trendIndex+2;
					stage3 = trendIndex+3;
					stage4 = trendIndex+4;
					stage5 = trendIndex+5;
					stage6 = trendIndex+6;
					
					actualStage = 0;
					lastStage = 0;
					if (debug==1)
					System.out.println("[SHORT] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}else if (trendIndex<=-test){
					tradeEntry = q.getClose5();
					tradeEntryMode = 1;
					tradeEntryIndex = trendIndex;
					
					stage_1 = trendIndex+1;
					stage1 = trendIndex-1;
					stage2 = trendIndex-2;
					stage3 = trendIndex-3;
					stage4 = trendIndex-4;
					stage5 = trendIndex-5;
					stage6 = trendIndex-6;
					
					actualStage = 0;
					lastStage = 0;
					if (debug==1)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}				
			}else if (tradeEntryMode == 1){//vamos long
				
				double diffIndex = (q.getClose5() - tradeEntry)*1.0/(200.0);//trendIndex - tradeEntryIndex;
				
				if (diffIndex>=closeDiff){
					
					winPips += q.getClose5() - tradeEntry; 
					if (debug==2)
						System.out.println("[WIN LONG STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex+" "+( q.getClose5() - tradeEntry)
								+" || "+q.toString()
															
						);
					wins++;
					tradeEntryMode = 0;
				}else{
					if (diffIndex<=-1.0){
						int mult = (int) (diffIndex/-1.0);
						actualStage = actualStage+mult;
						int pips = q.getClose5() - tradeEntry; 
						lostPips += -pips;
						partialLosses++;
						if (debug==1)
						System.out.println("[LONG STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex
								+" || "+q.toString()
								+" || "+pips
						);
						
						
						
						tradeEntryIndex = trendIndex;
						tradeEntry = q.getClose5();
						
						if (actualStage>=closeStage){
							losses++;
							tradeEntryMode = 0;
						}
					}
				}				
			}else if (tradeEntryMode == -1){
				
				double diffIndex = (tradeEntry-q.getClose5())*1.0/(200.0);
				
				if (diffIndex>=closeDiff){
					if (debug==1)
					System.out.println("[WINS SHORT STAGE "+actualStage+" ] "
							+trendIndex+" "+tradeEntryIndex
							+" || "+q.toString()
					);
					winPips += tradeEntry-q.getClose5(); 
					wins++;
					tradeEntryMode = 0;
				}else{
					if (diffIndex<=-1.0){
						int mult = (int) (diffIndex/-1.0);
						actualStage = actualStage+mult;
						int pips = tradeEntry-q.getClose5(); 
						lostPips += -pips;
						partialLosses++;
						if (debug==1)
						System.out.println("[SHORT STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex
								+" || "+q.toString()
								+" || "+pips
						);
						
						
						
						tradeEntryIndex = trendIndex;
						tradeEntry = q.getClose5();
						
						if (actualStage>=closeStage){
							losses++;
							tradeEntryMode = 0;
						}
					}										
				}			
			}
			
			
		}
		
		int total = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/(partialLosses);
		double factor = avgLoss/avgWin;
		
		double accLoss = calculateLoss(closeStage,factor);
		
		pf = wins*1.0/(losses*accLoss);
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+closeStage
				+" "+PrintUtils.Print2dec(closeDiff,false)
				+" || "
				+" "+total+" "+wins+" "+losses+" "+partialLosses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(pf,false)
				//+" || "+winPips+" "+lostPips
				+" || "+PrintUtils.Print2dec(avgWin,false)
				+" "+PrintUtils.Print2dec(avgLoss,false)
				+" || "+PrintUtils.Print2dec(factor,false)
				+" || "+PrintUtils.Print2dec(accLoss,false)
		);
	}
	
	public static void doTest4(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int closeStage,
			double closeDiff,
			int debug
			){
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int partialLosses = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips = 0;
		int lostPips = 0;
		double accSizes = 0;
		int count2 = 0;
		int counta2 = 0;
		int counta3 = 0;
		int counta4=0;
		int counta5=0;
		int counta6=0;
		int counta7=0;
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		double tradeEntryIndex = 0;
		int tradeEntryMode = 0;
		double stage_1 = 0;
		double stage1 = 0;
		double stage2 = 0;
		double stage3 = 0;
		double stage4 = 0;
		double stage5 = 0;
		double stage6 = 0;
		int actualStage = -1;
		int lastStage = -1;
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			//double trendIndex1 = indexes.get(i-1);
			double trendIndex2 = indexes.get(i-ref);
			int maxMin = maxMins.get(i-ref);
			
			
			
			if (actualTrendMode==1){				
				if ( trendIndex<0){
					actualTrendMode = -1;
					/*System.out.println("[CHANGE TO DOWN] "
						//+DateUtils.datePrint(cal)
						//+" || "
						+trendIndex+"  "+trendIndex2
						+" "+q.toString()+" || "+qref.toString()
						);*/
				}
			}else if (actualTrendMode==-1){
				if ( trendIndex>0){
					actualTrendMode = 1;
					/*System.out.println("[CHANGE TO UP] "
							+trendIndex+"  "+trendIndex2
							+" "+q.toString()+" || "+qref.toString()
					);*/
				}
			}else{
				if ( trendIndex<0){
					actualTrendMode = -1;
				}else if ( trendIndex>0){
						actualTrendMode = 1;					
				}
			}
			
			if (tradeEntryMode==0 
					&& h>=h1 && h<=h2
					){
				
				if (trendIndex>=test){
					tradeEntry = q.getClose5();
					tradeEntryMode = -1;
					tradeEntryIndex = trendIndex;
					
					stage_1 = trendIndex-1;
					stage1 = trendIndex+1;
					stage2 = trendIndex+2;
					stage3 = trendIndex+3;
					stage4 = trendIndex+4;
					stage5 = trendIndex+5;
					stage6 = trendIndex+6;
					
					actualStage = 0;
					lastStage = 0;
					if (debug==1)
					System.out.println("[SHORT] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}else if (trendIndex<=-test){
					tradeEntry = q.getClose5();
					tradeEntryMode = 1;
					tradeEntryIndex = trendIndex;
					
					stage_1 = trendIndex+1;
					stage1 = trendIndex-1;
					stage2 = trendIndex-2;
					stage3 = trendIndex-3;
					stage4 = trendIndex-4;
					stage5 = trendIndex-5;
					stage6 = trendIndex-6;
					
					actualStage = 0;
					lastStage = 0;
					if (debug==1)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}				
			}else if (tradeEntryMode == 1){//vamos long
				
				double diffIndex = trendIndex - tradeEntryIndex;
				
				if (diffIndex>=closeDiff){
					
					winPips += q.getClose5() - tradeEntry; 
					if (debug==2)
						System.out.println("[WIN LONG STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex+" "+( q.getClose5() - tradeEntry)
								+" || "+q.toString()
															
						);
					wins++;
					tradeEntryMode = 0;
				}else{
					if (diffIndex<=-1.0){
						int mult = (int) (diffIndex/-1.0);
						actualStage = actualStage+mult;
						int pips = q.getClose5() - tradeEntry; 
						lostPips += -pips;
						partialLosses++;
						if (debug==1)
						System.out.println("[LONG STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex
								+" || "+q.toString()
								+" || "+pips
						);
						
						
						
						tradeEntryIndex = trendIndex;
						tradeEntry = q.getClose5();
						
						if (actualStage>=closeStage){
							losses++;
							tradeEntryMode = 0;
						}
					}
				}				
			}else if (tradeEntryMode == -1){
				
				double diffIndex = tradeEntryIndex-trendIndex;
				
				if (diffIndex>=closeDiff){
					if (debug==1)
					System.out.println("[WINS SHORT STAGE "+actualStage+" ] "
							+trendIndex+" "+tradeEntryIndex
							+" || "+q.toString()
					);
					winPips += tradeEntry-q.getClose5(); 
					wins++;
					tradeEntryMode = 0;
				}else{
					if (diffIndex<=-1.0){
						int mult = (int) (diffIndex/-1.0);
						actualStage = actualStage+mult;
						int pips = tradeEntry-q.getClose5(); 
						lostPips += -pips;
						partialLosses++;
						if (debug==1)
						System.out.println("[SHORT STAGE "+actualStage+" ] "
								+trendIndex+" "+tradeEntryIndex
								+" || "+q.toString()
								+" || "+pips
						);
						
						
						
						tradeEntryIndex = trendIndex;
						tradeEntry = q.getClose5();
						
						if (actualStage>=closeStage){
							losses++;
							tradeEntryMode = 0;
						}
					}										
				}			
			}
			
			
		}
		
		int total = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/(partialLosses);
		double factor = avgLoss/avgWin;
		
		double accLoss = calculateLoss(closeStage,factor);
		
		pf = wins*1.0/(losses*accLoss);
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+closeStage
				+" "+PrintUtils.Print2dec(closeDiff,false)
				+" || "
				+" "+total+" "+wins+" "+losses+" "+partialLosses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(pf,false)
				//+" || "+winPips+" "+lostPips
				+" || "+PrintUtils.Print2dec(avgWin,false)
				+" "+PrintUtils.Print2dec(avgLoss,false)
				+" || "+PrintUtils.Print2dec(factor,false)
				+" || "+PrintUtils.Print2dec(accLoss,false)
		);
	}
	
	private static double calculateLoss(int closeStage, double factor) {
		// TODO Auto-generated method stub
		
		double accLoss = 0;
		double units = 0;
		for (int i=1;i<=closeStage;i++){
			units = accLoss +1;
			double loss = units*factor;
			accLoss+=loss;
			
		}
		return accLoss ;
	}

	public static void doTest3(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int debug
			){
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips = 0;
		int lostPips = 0;
		double accSizes = 0;
		int count2 = 0;
		int counta2 = 0;
		int counta3 = 0;
		int counta4=0;
		int counta5=0;
		int counta6=0;
		int counta7=0;
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		double tradeEntryIndex = 0;
		int tradeEntryMode = 0;
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			//double trendIndex1 = indexes.get(i-1);
			double trendIndex2 = indexes.get(i-ref);
			int maxMin = maxMins.get(i-ref);
			
			
			
			if (actualTrendMode==1){
				
				if ( trendIndex<0){
					actualTrendMode = -1;
					/*System.out.println("[CHANGE TO DOWN] "
						//+DateUtils.datePrint(cal)
						//+" || "
						+trendIndex+"  "+trendIndex2
						+" "+q.toString()+" || "+qref.toString()
						);*/
				}
			}else if (actualTrendMode==-1){
				if ( trendIndex>0){
					actualTrendMode = 1;
					/*System.out.println("[CHANGE TO UP] "
							+trendIndex+"  "+trendIndex2
							+" "+q.toString()+" || "+qref.toString()
					);*/
				}
			}else{
				if ( trendIndex<0){
					actualTrendMode = -1;
				}else if ( trendIndex>0){
						actualTrendMode = 1;					
				}
			}
			
			if (tradeEntryMode==0 
					&& h>=h1 && h<=h2
					){
				
				if (trendIndex>=test){
					tradeEntry = q.getClose5();
					tradeEntryMode = -1;
					tradeEntryIndex = trendIndex;
					if (debug==1)
					System.out.println("[SHORT] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}else if (trendIndex<=-test){
					tradeEntry = q.getClose5();
					tradeEntryMode = 1;
					tradeEntryIndex = trendIndex;
					if (debug==1)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}				
			}else if (tradeEntryMode == 1){
				
				int actualPips = q.getClose5()-tradeEntry;
				boolean isClosed = false;
				if (trendIndex>=1.0
						&& actualPips>=200
						){
					isClosed = true;				
				}else if (trendIndex<=-test-5.0){
					isClosed = true;
				}		
				
				if (isClosed){
					if (actualPips>=0){
						winPips += actualPips;
						wins++;
						tradeEntryMode=0;					
					}else{
						lostPips += -actualPips;
						losses++;
						tradeEntryMode=0;
					}	
				}
			}else if (tradeEntryMode == -1){
				int actualPips = tradeEntry-q.getClose5();
				boolean isClosed = false;
				
				if (trendIndex<=-1.0
						&& actualPips>=200
						){
					isClosed = true;				
				}else if (trendIndex>=test+5.0){
					isClosed = true;
				}		
				
				if (isClosed){
					if (actualPips>=0){
						winPips += actualPips;
						wins++;
						tradeEntryMode=0;					
					}else{
						lostPips += -actualPips;
						losses++;
						tradeEntryMode=0;
					}	
				}
			}
			
			
		}
		
		int total = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" || "+winPips+" "+lostPips
				+" || "+PrintUtils.Print2dec(avg,false)
		);
	}
	
	public static void doTest2(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr,
			int debug
			){
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips = 0;
		int lostPips = 0;
		double accSizes = 0;
		int count2 = 0;
		int counta2 = 0;
		int counta3 = 0;
		int counta4=0;
		int counta5=0;
		int counta6=0;
		int counta7=0;
		Calendar cal = Calendar.getInstance();
		ref=1;
		int actualTrendMode = 0;
		int tradeEntry = 0;
		int tradeEntryMode = 0;
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			//double trendIndex1 = indexes.get(i-1);
			double trendIndex2 = indexes.get(i-ref);
			int maxMin = maxMins.get(i-ref);
			
			
			
			if (actualTrendMode==1){
				
				if ( trendIndex<0){
					actualTrendMode = -1;
					/*System.out.println("[CHANGE TO DOWN] "
						//+DateUtils.datePrint(cal)
						//+" || "
						+trendIndex+"  "+trendIndex2
						+" "+q.toString()+" || "+qref.toString()
						);*/
				}
			}else if (actualTrendMode==-1){
				if ( trendIndex>0){
					actualTrendMode = 1;
					/*System.out.println("[CHANGE TO UP] "
							+trendIndex+"  "+trendIndex2
							+" "+q.toString()+" || "+qref.toString()
					);*/
				}
			}else{
				if ( trendIndex<0){
					actualTrendMode = -1;
				}else if ( trendIndex>0){
						actualTrendMode = 1;					
				}
			}
			
			if (tradeEntryMode==0 
					&& h>=h1 && h<=h2
					){
				
				if (trendIndex>=test){
					tradeEntry = q.getClose5();
					tradeEntryMode = -1;
					if (debug==1)
					System.out.println("[SHORT] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}else if (trendIndex<=-test){
					tradeEntry = q.getClose5();
					tradeEntryMode = 1;
					if (debug==1)
					System.out.println("[LONG] "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
				}				
			}else if (tradeEntryMode == 1){
				if (trendIndex>=1.0){
					int pips = q.getClose5()-tradeEntry;
					tradeEntryMode=0;
					if (debug==1)
					System.out.println("[LONG CLOSED] "+pips+" "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
					if (pips>=0){
						winPips += pips;
						wins++;
					}else{
						lostPips += -pips;
						losses++;
					}
				}
			}else if (tradeEntryMode == -1){
				if (trendIndex<=-1.0){
					int pips = tradeEntry-q.getClose5();
					tradeEntryMode=0;
					if (debug==1)
					System.out.println("[SHORT CLOSED] "+pips+" "+trendIndex+" "+tradeEntry
							+" || "+q.toString()
					);
					if (pips>=0){
						winPips += pips;
						wins++;
					}else{
						lostPips += -pips;
						losses++;
					}
				}
			}
			
			
		}
		
		int total = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/total;
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(pf,false)
		);
		
		/*double winPer = wins*100.0/count;
		double avg = diff*0.1/count;
		double pf = winPips*1.0/lostPips;
		double pf2 = lostPips*1.0/winPips;
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+ref
				+" "+thr
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(pf2,false)
				+" || "+" "+PrintUtils.Print2dec(accMaxMin/count,false)
				+" || "+" "+PrintUtils.Print2dec(accSizes*1.0/count2,false)
				+" || "+PrintUtils.Print2dec(counta2*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta3*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta4*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta5*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta6*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta7*100.0/count2,false)
				);*/
	}
	
	public static void doTest(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<Double> indexes,
			int y1,int y2,
			int h1,int h2,
			double test,
			int ref,
			int thr
			){
		
		int count = 0;
		int wins = 0;
		int diff = 0;
		int accMaxMin = 0;
		int winPips = 0;
		int lostPips = 0;
		double accSizes = 0;
		int count2 = 0;
		int counta1 = 0;
		int counta2 = 0;
		int counta3 = 0;
		int counta4=0;
		int counta5=0;
		int counta6=0;
		int counta7=0;
		int counta8=0;
		int counta9=0;
		Calendar cal = Calendar.getInstance();
		for (int i=ref;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort qref = data.get(i-ref);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			double trendIndex = indexes.get(i);
			//double trendIndex1 = indexes.get(i-1);
			double trendIndex2 = indexes.get(i-ref);
			int maxMin = maxMins.get(i-ref);
			
			
			/*if (h>=h1 && h<=h2){
				if (Math.abs(trendIndex2)>=test){
					count++;
					if (trendIndex2>0 && trendIndex>=trendIndex2){
						wins++;
					}else if (trendIndex2<0 && trendIndex<=trendIndex2){
							wins++;
					}
				}
			}*/
			
			
			
			if (h>=h1 && h<=h2){
				count2++;
				accSizes += Math.abs(trendIndex);
				//if (Math.abs(trendIndex)<=2.0) counta1++;
				if (Math.abs(trendIndex)<=2.0) counta2++;
				if (Math.abs(trendIndex)<=3.0) counta3++;
				if (Math.abs(trendIndex)<=4.0) counta4++;
				if (Math.abs(trendIndex)<=5.0) counta5++;
				if (Math.abs(trendIndex)<=6.0) counta6++;
				if (Math.abs(trendIndex)<=7.0) counta7++;
				if (Math.abs(trendIndex)<=8.0) counta8++;
				if (Math.abs(trendIndex)<=9.0) counta9++;
				if (true
						&& Math.abs(trendIndex2)>=test
						&& Math.abs(maxMin)>=thr
						){
					
					if (trendIndex2>0){
						accMaxMin+=Math.abs(maxMin);
						count++;
						
						int diffC = q.getClose5()-qref.getClose5();
						diff += q.getClose5()-qref.getClose5(); 
												
						if (diffC>=0){
							wins++;	
							winPips +=diffC;
						}else{
							lostPips -= diffC;
						}
					}else if (trendIndex2<0){ 
						accMaxMin+=Math.abs(maxMin);
						count++;
						int diffC = qref.getClose5()-q.getClose5(); 
						diff += qref.getClose5()-q.getClose5(); 
						if (diffC>=0){
							wins++;	
							winPips +=diffC;
						}else{
							lostPips -= diffC;
						}
					}
				}
			}
			
			
			
			/*if (Math.abs(trendIndex)>=5.0)
			System.out.println(
					DateUtils.datePrint(cal)
					+" "+maxMin
					+" "+PrintUtils.Print2dec(trendIndex,false)
					+" || "+q.toString()
					);*/
			
		}
		
		double winPer = wins*100.0/count;
		double avg = diff*0.1/count;
		double pf = winPips*1.0/lostPips;
		double pf2 = lostPips*1.0/winPips;
		System.out.println(
				h1 +" "+h2
				+" "+PrintUtils.Print2dec(test,false)
				+" "+ref
				+" "+thr
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" "+PrintUtils.Print2dec(pf2,false)
				+" || "+" "+PrintUtils.Print2dec(accMaxMin/count,false)
				+" || "+" "+PrintUtils.Print2dec(accSizes*1.0/count2,false)
				+" || "
				//+" "+PrintUtils.Print2dec(counta1*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta2*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta3*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta4*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta5*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta6*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta7*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta8*100.0/count2,false)
				+" "+PrintUtils.Print2dec(counta9*100.0/count2,false)
				);
	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
				//String path0 ="C:\\fxdata\\";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
				
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
				String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
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
					ArrayList<Double> indexes = TradingUtils.calculateTrendingIndex(dataNoise, 200);
					
					

					System.out.println(data.size()+" || "+indexes.size());
					
					for (int ref = 1;ref<=1;ref+=1){
						for (double test=0.0;test<=0.0;test+=0.5){
							for (int closeStage=1;closeStage<=1;closeStage+=1){
								for (double closeDiff=1.0;closeDiff<=1.0;closeDiff+=0.1){
									for (int maxMin=500;maxMin<=500;maxMin+=100){
										for (int y1=2012;y1<=2012;y1++){
											int y2 = y1+5;
											for (int h1=0;h1<=23;h1++){
												int h2 = h1+0;
												//TrendingIndexTest.doTest(dataNoise, maxMins, indexes,y1,y2,h1,h2,test,ref,maxMin);
												//TrendingIndexTest.doTest4(dataNoise, maxMins, indexes,y1,y2,h1,h2,test,ref,maxMin,
														//closeStage,closeDiff,0);
												//TrendingIndexTest.doTest4(dataNoise, maxMins, indexes,y1,y2,h1,h2,test,ref,maxMin,
														//closeStage,closeDiff,0);
												//TrendingIndexTest.doTest6(dataNoise, maxMins, indexes,y1,y2,h1,h2,test,ref,maxMin,
														//closeStage,closeDiff,0);
												for (int tp=150;tp<=150;tp+=10){
													for (int sl=3*tp;sl<=3*tp;sl+=1*tp) {
												
												//for (int sl=50;sl<=400;sl+=10) {
													//for (int tp=28*sl;tp<=28*sl;tp+=1*sl){
														for (double risk=1.0;risk<=1.0;risk+=1.0){
															TrendingIndexTest.doTest9(dataNoise, 
																	maxMins, indexes,
																	y1,y2,h1,h2,
																	test,ref,maxMin,
																	closeStage,tp,sl,999999,risk,
																	//15,7.5,
																	0,20,
																	0);//1 pip de spread + 0.75 de comm
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
					
					/*for (int j=0;j<indexes.size();j++){
						double leg = indexes.get(j);
						if (Math.abs(leg)>=16.0)
						System.out.println(PrintUtils.Print2dec(indexes.get(j),false));
					}*/
				}

	}

}
