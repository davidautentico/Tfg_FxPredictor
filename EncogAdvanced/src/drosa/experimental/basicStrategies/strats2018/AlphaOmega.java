package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class AlphaOmega {
	
	//objetivo ADR
	//a partir de H2, el objetivo es salir airoso
	public static void doSimpleMeanReversion2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minPipsEntry,			
			int maxLayers,
			int maxAllowedLoss,
			int debug,
			int yearsCriteria,
			int debugCriteria
			){
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winPips = 0;
		int lostPips = 0;
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
		
		int mode = 0;
		int high = -1;
		int low = -1;
		int highIdx = -1;
		int lowIdx = -1;
		mode = 0;
		int accDiff = 0;
		int ref = -1;
		int fullLosses = 0;
		ArrayList<Integer> arrWins = new ArrayList<Integer>();
		int actualUnits = 0;
		int avgPrice = 0;
		int lastEntry = 0;
		int winProfit = 0;
		int lossProfit = 0;
		int maxUnits = 0;
		int dayWins = 0;
		int dayLosses = 0;
		int dayLosses$ = 0;
		int accPips = 0;
		int spread = 20;
		lastYear = -1;
		int actualLayers = 0;
		int comm = 15;
		int worstLoss = 0;
		
		double balance = 0;
		double maxBalance = 0;
		double maxDD = 0;
		double maxDiff = 0;
		int pipsTarget = 20;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (y<y1 || y>y2) continue;
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfYear = winsYear*1.0/lossYear;
					if (pfYear>=1.0) countYears++;
				}
				winsYear = 0;
				lossYear = 0;
				lastYear = y;
			}
			
			if (lastDay!=day){				
				if (lastDay!=-1){
					//si hay unidades se cierran										
					if (mode==1 || mode==-1){
						int pips = 0;
						if (mode==-1){
							pips = avgPrice-q.getOpen5();
						}else if (mode==1){
							pips = q.getOpen5()-avgPrice;
						}
						int pipsProfit =actualUnits*pips-comm*actualUnits;
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winsYear += pipsProfit;
							winProfit += pipsProfit;
							wins++;
							dayWins++;
							
							accPips += pips;
							if (debug==1){
								System.out.println("[*DAY CLOSE WIN] "+q.getOpen5()
									+" || "+actualUnits+" "+avgPrice
									+" || "+pips
									+" || "+pipsProfit
									);
							}
						}else{
							lossYear += -pipsProfit;
							lossProfit += -pipsProfit;
							losses++;
							dayLosses++;
							accPips += pips;
							
							if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
							
							if (debug==1){
								System.out.println("[*DAY CLOSE LOSS] "+q.getOpen5()
									+" || "+actualUnits+" "+avgPrice
									+" || "+pips
									+" || "+pipsProfit
									);
							}
						}
					}					
				}		
				pipsTarget = 200;
				ref = -1;
				mode = 0;
				highIdx =-1;
				lowIdx = -1;
				high = -1;
				low = -1;
				avgPrice = 0;
				actualUnits = 0;
				actualLayers = 0;
				lastDay = day;
			}
			
			if (h>h2){
				pipsTarget = 20;
			}
			
			//actualDiff
			int actualDiff = high-low;
			double midPrice = 0;
			
			//condiciones de salida
			if (mode==-1){
				int pips = avgPrice-q.getOpen5();
				int pipsProfit =actualUnits*pips-comm*actualUnits;
				
				if (pips>=pipsTarget || -pipsProfit>=maxAllowedLoss){
					if (pipsProfit>=0){
						winProfit += pipsProfit;
						wins++;
						winsYear += pipsProfit;
					}else{
						lossProfit += -pipsProfit;
						losses++;
						lossYear += -pipsProfit;
						
						if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
					}
					
					if (debug==1){
						System.out.println("[*SHORT CLOSE] "+q.getOpen5()
							+" || "+actualUnits+" "+avgPrice
							+" || "+pips
							+" || "+pipsProfit
							);
					}	
					lastEntry = 0;
					actualUnits = 0;
					mode = 0;
				}				
			}
			if (mode==1){
				int pips = q.getOpen5()-avgPrice;
				int pipsProfit =actualUnits*pips-comm*actualUnits;
				
				if (pips>=pipsTarget || -pipsProfit>=maxAllowedLoss){
					if (pipsProfit>=0){
						winProfit += pipsProfit;
						wins++;
						winsYear += pipsProfit;
					}else{
						lossProfit += -pipsProfit;
						losses++;
						lossYear += -pipsProfit;
						
						if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
					}
					
					if (debug==1){
						System.out.println("[*LONG CLOSE] "+q.getOpen5()
							+" || "+actualUnits+" "+avgPrice
							+" || "+pips
							+" || "+pipsProfit
							);
					}
					lastEntry = 0;
					actualUnits = 0;
					mode = 0;
				}				
			}
			
			int maxMin = maxMins.get(i-1);
			
			//CONDICIONES DE ENTRADA
			if (actualLayers<maxLayers){					
				if (mode<=0 && maxMin>=thr){
					boolean canContinue = true;
					if (mode==0 && h>=h2) canContinue = false;
					if (canContinue)
						if (lastEntry==0 || q.getOpen5()-lastEntry>=minPipsEntry){						
							mode = -1;						
							int newUnits = actualUnits;
							if (newUnits==0) newUnits = 1;
							avgPrice = ((newUnits)*q.getOpen5()+actualUnits*avgPrice)/(actualUnits+newUnits);
							actualUnits += newUnits;
							lastEntry = q.getOpen5();
							actualLayers++;
							if (actualUnits>=maxUnits) maxUnits = actualUnits;
							
							if (debug==1){
								System.out.println("[SHORT NEW LAYER] "+q.getOpen5()+" || "+actualUnits+" "+avgPrice);
							}
						}
				}else if (mode>=0 && maxMin<=-thr){
					boolean canContinue = true;
					if (mode==0 && h>=h2) canContinue = false;
					if (canContinue)
					if (lastEntry==0 || lastEntry-q.getOpen5()>=minPipsEntry){
						mode = 1;
						int newUnits = actualUnits; 
						if (newUnits==0) newUnits = 1;
						avgPrice = ((newUnits)*q.getOpen5()+actualUnits*avgPrice)/(actualUnits+newUnits);
						actualUnits += newUnits;
						lastEntry = q.getOpen5();
						actualLayers++;
						if (actualUnits>=maxUnits) maxUnits = actualUnits;
						
						if (debug==1){
							System.out.println("[LONG NEW LAYER] "+q.getOpen5()+" || "+actualUnits+" "+avgPrice);
						}
					}
				}				
			}//actualLayers<maxLayers
			
					
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				highIdx = i;
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
				lowIdx = i;
			}
		}//
		

		trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winProfit*1.0/lossProfit;
		double avgPips = accPips*0.1/trades;
		double pftodd = maxBalance*1.0/maxDiff;
		double avgProfit = (winProfit-lossProfit)*0.1/trades; 
		double factor = maxAllowedLoss/avgProfit;
				
		if (debugCriteria==0){
		System.out.println(
				thr+" "+minPipsEntry+" "+h1+" "+h2
				+" "+maxLayers+" "+maxAllowedLoss
				+" || "
				+" "+countYears
				+" "+trades
				+" "+wins+" "+losses
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(avgProfit, false)
				+" "+PrintUtils.Print2dec(winProfit, false)
				+" "+PrintUtils.Print2dec(lossProfit, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(factor, false)
				+" || "+dayWins+" "+dayLosses
				+" || "+maxUnits
				+" || "+" "+PrintUtils.Print2dec(accPips*0.1/trades, false)
				+" || "+" "+PrintUtils.Print2dec(worstLoss*0.1, false)
				+" || "+" "+PrintUtils.Print2dec(maxBalance, false)
				+" "+PrintUtils.Print2dec(maxDiff, false)
				+" || "+" "+PrintUtils.Print2dec(maxBalance*1.0/maxDiff, false)
				);
		}else if (debugCriteria==1 
				&& countYears>=yearsCriteria
				&& pf>=1.0
				//&& factor <50.0
				//&& avgPips>=3.0
				//&& trades >=1000
				//&& pftodd>=10.0
				){
			System.out.println(
			thr+" "+minPipsEntry+" "+h1+" "+h2
			+" "+maxLayers+" "+maxAllowedLoss
			+" || "
			+" "+countYears
			+" || "+trades
			+" "+wins+" "+losses
			+" || "+PrintUtils.Print2dec(winPer, false)
			+" "+PrintUtils.Print2dec(winProfit, false)
			+" "+PrintUtils.Print2dec(lossProfit, false)
			+" || "+PrintUtils.Print2dec(pf, false)
			+" || "+PrintUtils.Print2dec(factor, false)
			+" || "+dayWins+" "+dayLosses
			+" || "+maxUnits
			+" || "+PrintUtils.Print2dec(avgProfit, false)
			+" "+PrintUtils.Print2dec(winProfit*0.1/wins, false)
			+" "+PrintUtils.Print2dec(lossProfit*0.1/losses, false)
			+" "+PrintUtils.Print2dec(worstLoss*0.1, false)
			+" || "+" "+PrintUtils.Print2dec(maxBalance, false)
			+" "+PrintUtils.Print2dec(maxDiff, false)
			+" || "+" "+PrintUtils.Print2dec(maxBalance*1.0/maxDiff, false)
			);
		}
		
	}
	
	public static void doSimpleMeanReversion(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int minPips,
			int minPipsEntry,
			double percent1,
			double percent2,
			int maxLayers,
			int maxAllowedLoss,
			int debug,
			int yearsCriteria,
			int debugCriteria
			){
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winPips = 0;
		int lostPips = 0;
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
		
		int mode = 0;
		int high = -1;
		int low = -1;
		int highIdx = -1;
		int lowIdx = -1;
		mode = 0;
		int accDiff = 0;
		int ref = -1;
		int fullLosses = 0;
		ArrayList<Integer> arrWins = new ArrayList<Integer>();
		int actualUnits = 0;
		int avgPrice = 0;
		int lastEntry = 0;
		int winProfit = 0;
		int lossProfit = 0;
		int maxUnits = 0;
		int dayWins = 0;
		int dayLosses = 0;
		int dayLosses$ = 0;
		int accPips = 0;
		int spread = 20;
		lastYear = -1;
		int actualLayers = 0;
		int comm = 15;
		int worstLoss = 0;
		
		double balance = 0;
		double maxBalance = 0;
		double maxDD = 0;
		double maxDiff = 0;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (y<y1 || y>y2) continue;
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfYear = winsYear*1.0/lossYear;
					if (pfYear>=1.0) countYears++;
				}
				winsYear = 0;
				lossYear = 0;
				lastYear = y;
			}
			
			if (lastDay!=day){				
				if (lastDay!=-1){
					//si hay unidades se cierran	
					
					
					if (mode==1 || mode==-1){
						int pips = 0;
						if (mode==-1){
							pips = avgPrice-q.getOpen5();
						}else if (mode==1){
							pips = q.getOpen5()-avgPrice;
						}
						int pipsProfit =actualUnits*pips-comm*actualUnits;
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winsYear += pipsProfit;
							winProfit += pipsProfit;
							wins++;
							dayWins++;
							
							accPips += pips;
							if (debug==1){
								System.out.println("[*DAY CLOSE WIN] "+q.getOpen5()
									+" || "+actualUnits+" "+avgPrice
									+" || "+pips
									);
							}
						}else{
							lossYear += -pipsProfit;
							lossProfit += -pipsProfit;
							losses++;
							dayLosses++;
							accPips += pips;
							
							if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
							
							if (debug==1){
								System.out.println("[*DAY CLOSE LOSS] "+q.getOpen5()
									+" || "+actualUnits+" "+avgPrice
									+" || "+pips
									);
							}
						}
					}
					
				}				
				ref = -1;
				mode = 0;
				highIdx =-1;
				lowIdx = -1;
				high = -1;
				low = -1;
				avgPrice = 0;
				actualUnits = 0;
				actualLayers = 0;
				lastDay = day;
			}
			
			//actualDiff
			int actualDiff = high-low;
			double midPrice = 0;
			
						
			//CONDICIONES DE SALIDA
			if (mode==-1){ //estamos vendiendo, el precio se va para arriba
				double diffHigh = high - q.getOpen5();			
				double actualPercent = diffHigh*100.0/actualDiff;
				if (h<h2){
					int pips = avgPrice-q.getOpen5();
					
					if (actualPercent>=percent1 
							//&& pips>=spread
							){
						//cerramos
						
						int pipsProfit =actualUnits*pips-comm*actualUnits;
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winProfit += pipsProfit;
							wins++;
							winsYear += pipsProfit;
						}else{
							lossProfit += -pipsProfit;
							losses++;
							lossYear += -pipsProfit;
							
							if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
						}
						accPips += pips;
						if (debug==1){
							System.out.println("[*SHORT CLOSE p1] "+q.getOpen5()
								+" || "+actualUnits+" "+avgPrice
								+" || "+pips
								);
						}
						
						mode=2;
					}
				}else if (h>=h2){
					int pips = avgPrice-q.getOpen5();
					if (actualPercent>=percent2 
							//&& pips>=spread
							){
						//cerramos
						
						int pipsProfit =actualUnits*pips-comm*actualUnits;
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winProfit += pipsProfit;
							wins++;
							winsYear += pipsProfit;
						}else{
							lossProfit += -pipsProfit;
							losses++;
							lossYear += -pipsProfit;
							
							if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
						}
						accPips += pips;
						if (debug==1){
							System.out.println("[*SHORT CLOSE p2] "+q.getOpen5()
								+" || "+actualUnits+" "+avgPrice
								+" || "+pips
								);
						}
						
						
						mode=2;
					}
				}//SHORTS
				//SEGUNDA CONDICION DE SALIDA
				if (mode==-1){
					int pips = avgPrice-q.getOpen5();
					int pipsProfit =actualUnits*pips-comm*actualUnits;
					
					if (pipsProfit<0 && -pipsProfit*0.1>=maxAllowedLoss){
						
						pipsProfit = -maxAllowedLoss*10; //no seguro de que esto sea correcto
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winProfit += pipsProfit;
							wins++;
							winsYear += pipsProfit;
						}else{
							lossProfit += -pipsProfit;
							losses++;
							lossYear += -pipsProfit;
							
							if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
						}
						accPips += pips;
						if (debug==1){
							System.out.println("[*LONG CLOSE p3] "+q.getOpen5()
								+" || "+actualUnits+" "+avgPrice
								+" || "+pips
								);
						}
						
						mode=0;
						
					}
				}
			}
			
			if (mode==1){
				double diffLow = q.getOpen5()-low;			
				double actualPercent = diffLow*100.0/actualDiff;
				if (h<h2){
					int pips = q.getOpen5()-avgPrice;
					if (actualPercent>=percent1 
							//&& pips>=spread
							){
						//cerramos
						
						int pipsProfit =actualUnits*pips-comm*actualUnits;
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winProfit += pipsProfit;
							wins++;
							winsYear += pipsProfit;
						}else{
							lossProfit += -pipsProfit;
							losses++;
							lossYear += -pipsProfit;
							
							if (-pipsProfit*0.1>=worstLoss) worstLoss = -pipsProfit;
						}
						accPips += pips;
						if (debug==1){
							System.out.println("[*LONG CLOSE p1] "+q.getOpen5()
								+" || "+actualUnits+" "+avgPrice
								+" || "+pips
								);
						}
						mode=0;
					}
				}else if (h>=h2){
					int pips = q.getOpen5()-avgPrice;
					if (actualPercent>=percent2 
							//&& pips>=spread
							){
						//cerramos
						int pipsProfit =actualUnits*pips-comm*actualUnits;
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winProfit += pipsProfit;
							wins++;
							winsYear += pipsProfit;
						}else{
							lossProfit += -pipsProfit;
							losses++;
							lossYear += -pipsProfit;
							
							if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
						}
						accPips += pips;
						if (debug==1){
							System.out.println("[*LONG CLOSE p2] "+q.getOpen5()
								+" || "+actualUnits+" "+avgPrice
								+" || "+pips
								);
						}
						
						mode=0;
					}
				}
				
				if (mode==1){
					int pips = q.getOpen5()-avgPrice;
					int pipsProfit =actualUnits*pips-comm*actualUnits;
					
					if (pipsProfit<0 && -pipsProfit*0.1>=maxAllowedLoss){
						
						pipsProfit = -maxAllowedLoss*10; //no seguro de que esto sea correcto
						
						balance += pipsProfit;
						if (balance>=maxBalance) maxBalance = balance;
						else{
							double diff = maxBalance-balance;
							if (diff>=maxDiff) maxDiff = diff;
						}
						
						if (pipsProfit>=0){
							winProfit += pipsProfit;
							wins++;
							winsYear += pipsProfit;
						}else{
							lossProfit += -pipsProfit;
							losses++;
							lossYear += -pipsProfit;
							
							if (-pipsProfit>=worstLoss) worstLoss = -pipsProfit;
						}
						accPips += pips;
						if (debug==1){
							System.out.println("[*LONG CLOSE p3] "+q.getOpen5()
								+" || "+actualUnits+" "+avgPrice
								+" || "+pips
								);
						}
						
						mode=0;
						
					}
				}
			}
			
			//CONDICIONES DE ENTRADA
			if (actualLayers< maxLayers){
				if (mode==0){
					if (high>=-1 
							&& actualDiff>=minPips
							&& h<h2//no se ha lelgado a la hora del pivote
							){
						
						if (highIdx == i-1){
							//entrada short
							mode = -1;
							actualUnits = 1;
							avgPrice = q.getOpen5();
							lastEntry = q.getOpen5();
							actualLayers++;
							if (debug==1){
								System.out.println("[SHORT FIRST LAYER] "+q.getOpen5()+" || "+actualUnits+" "+avgPrice);
							}
						}else if (lowIdx==i-1){
							mode = 1;
							actualUnits = 1;
							avgPrice = q.getOpen5();
							lastEntry = q.getOpen5();
							actualLayers++;
							if (debug==1){
								System.out.println("[LONG FIRST LAYER] "+q.getOpen5()+" || "+actualUnits+" "+avgPrice);
							}
						}
					}
				}else if (mode==-1){//sube
					if (q.getOpen5()-lastEntry>=minPipsEntry){
						int newUnits = actualUnits;
						avgPrice = ((newUnits)*q.getOpen5()+actualUnits*avgPrice)/(actualUnits+newUnits);
						actualUnits += newUnits;
						lastEntry = q.getOpen5();
						actualLayers++;
						if (actualUnits>=maxUnits) maxUnits = actualUnits;
						
						if (debug==1){
							System.out.println("[SHORT NEW LAYER] "+q.getOpen5()+" || "+actualUnits+" "+avgPrice);
						}
					}
				}else  if (mode==1){
					if (lastEntry-q.getOpen5()>=minPipsEntry){
						int newUnits = actualUnits; 
						avgPrice = ((newUnits)*q.getOpen5()+actualUnits*avgPrice)/(actualUnits+newUnits);
						actualUnits += newUnits;
						lastEntry = q.getOpen5();
						actualLayers++;
						if (actualUnits>=maxUnits) maxUnits = actualUnits;
						
						if (debug==1){
							System.out.println("[LONG NEW LAYER] "+q.getOpen5()+" || "+actualUnits+" "+avgPrice);
						}
					}
				}
			}
			
			
		
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				highIdx = i;
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
				lowIdx = i;
			}
		}//
		

		trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winProfit*1.0/lossProfit;
		double avgPips = accPips*0.1/trades;
		double pftodd = maxBalance*1.0/maxDiff;
		double avgProfit = (winProfit-lossProfit)*0.1/trades; 
		double factor = maxAllowedLoss/avgProfit;
				
		if (debugCriteria==0){
		System.out.println(
				minPips+" "+minPipsEntry+" "+h1+" "+h2
				+" "+maxLayers+" "+maxAllowedLoss
				+" "+PrintUtils.Print2dec(percent1, false)
				+" "+PrintUtils.Print2dec(percent2, false)
				+" || "
				+" "+countYears
				+" "+trades
				+" "+wins+" "+losses
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(avgProfit, false)
				+" "+PrintUtils.Print2dec(winProfit, false)
				+" "+PrintUtils.Print2dec(lossProfit, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(factor, false)
				+" || "+dayWins+" "+dayLosses
				+" || "+maxUnits
				+" || "+" "+PrintUtils.Print2dec(accPips*0.1/trades, false)
				+" || "+" "+PrintUtils.Print2dec(worstLoss*0.1, false)
				+" || "+" "+PrintUtils.Print2dec(maxBalance, false)
				+" "+PrintUtils.Print2dec(maxDiff, false)
				+" || "+" "+PrintUtils.Print2dec(maxBalance*1.0/maxDiff, false)
				);
		}else if (debugCriteria==1 
				&& countYears>=yearsCriteria
				&& pf>=1.0
				//&& factor <50.0
				//&& avgPips>=3.0
				//&& trades >=1000
				//&& pftodd>=10.0
				){
			System.out.println(
			minPips+" "+minPipsEntry+" "+h1+" "+h2
			+" "+PrintUtils.Print2dec(percent1, false)
			+" "+PrintUtils.Print2dec(percent2, false)
			+" "+maxLayers+" "+maxAllowedLoss
			+" || "
			+" "+countYears
			+" || "+trades
			+" "+wins+" "+losses
			+" || "+PrintUtils.Print2dec(winPer, false)
			+" "+PrintUtils.Print2dec(winProfit, false)
			+" "+PrintUtils.Print2dec(lossProfit, false)
			+" || "+PrintUtils.Print2dec(pf, false)
			+" || "+PrintUtils.Print2dec(factor, false)
			+" || "+dayWins+" "+dayLosses
			+" || "+maxUnits
			+" || "+PrintUtils.Print2dec(avgProfit, false)
			+" "+PrintUtils.Print2dec(winProfit*0.1/wins, false)
			+" "+PrintUtils.Print2dec(lossProfit*0.1/losses, false)
			+" "+PrintUtils.Print2dec(worstLoss*0.1, false)
			+" || "+" "+PrintUtils.Print2dec(maxBalance, false)
			+" "+PrintUtils.Print2dec(maxDiff, false)
			+" || "+" "+PrintUtils.Print2dec(maxBalance*1.0/maxDiff, false)
			);
		}
		
	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.12.31_2018.02.15.csv";
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
			
			for (int h1=0;h1<=0;h1++){
				for (int h2=10;h2<=10;h2++){
					for (int thr=0;thr<=10000;thr+=100){
						for (int minPipsEntry=20;minPipsEntry<=20;minPipsEntry+=10){
							for (int maxLayers=20;maxLayers<=20;maxLayers+=1){
								for (int y1=2009;y1<=2009;y1++){
									int y2 = y1+9;
									int maxAllowedLoss = 1000;
									for (maxAllowedLoss=50000;maxAllowedLoss<=50000;maxAllowedLoss+=1000){
										AlphaOmega.doSimpleMeanReversion2("", data, maxMins, y1,y2, h1, h2,
											thr,minPipsEntry,maxLayers,maxAllowedLoss,0,0,0);
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
