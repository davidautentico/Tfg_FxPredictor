package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestClaudia {
	
	public static void doTest2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int maxTrades,
			int maxTrades2,
			int thr1,
			int thr2,
			int thr3,
			int thr4,
			int minDistance,
			int tp$,
			int maxPipsToDie,			
			double maxLossRisk,		
			int maxBars,
			double minDayProfit,
			double comm,
			int debug,
			boolean printSummary
			){
		
		double balance = 10000;
		double amountAdded = 10000;
		double win$$ = 0;
		double lost$$=0;
		double maxLossFactor = 0;
		double totalProfit = 0;
		int actualTrades = 0;
		int lastTrade = -1;
		int mode = 0;
		int avgPrice = 0;
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
		int totalFactor10 = 0;
		int winFactor10 = 0;
		int lossFactor10 = 0;
		int lastDay = -1;
		int idTrade = 0;
		int wins = 0;
		int losses = 0;
		int doubts = 0;
		int winPips = 0;
		int lostPips = 0;
		int directionDay = 0;
		double survivorPer = -1;
		double pipsToDie = -1;
		double pipsToMaxLoss = -1;
		double pipsBE = -1;
		boolean isFactor10 = false;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		QuoteShort qmi = new QuoteShort();
		boolean isFail = false;
		boolean trade23inserted = false;
		double profitNegDay = 0;
		double profit = 0;
		double dayProfit = 0;
		double pipValue = 1;
		double target$ = tp$;	
		int totalFails2 = 0;
		int actualId = 0;
		int lastId = -1;
		int totalNewDays = 0;
		int totalFails = 0;
		int avgPips0 = 0;
		int avgPips1 = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR); 
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			
			if (day!=lastDay){	
				if (actualTrades>0 
						){
					int pips = 0;
					boolean isTrade = false;
					if (mode==1){
						pips = q.getOpen5()-avgPrice;
						isTrade = true;	
					}else if (mode==-1){
						pips = avgPrice-q.getOpen5();
						isTrade = true;
					}
					
					if (isTrade){
						profit = (pips*0.1)*actualTrades*pipValue;
						double actualPipValue = pipValue*actualTrades;
						if (profit<0
								//&& profit<dayProfit
								){
							profitNegDay += profit;
							//System.out.println("sumando neg : "+profit+" "+profitNegDay);
							pipsBE = -profit/actualPipValue;
							pipsToMaxLoss = (balance+profit) /actualPipValue;
							double surviveFactor = pipsToMaxLoss/pipsBE;
							
							int begin0 = i+60;
							int begin1 = i+60;
							int endj = begin0+1440;
							if (endj>=data.size()-1) endj=data.size()-1;
							for (int j=begin0;j<=endj;j++){
								//if (maxMins.get(j)>=thr4 && mode == 1){
								if (data.get(j).getOpen5()<=data.get(i).getOpen5()-thr4*10 && mode == 1){
									begin1 = j;
									break;
								}else 
									if (data.get(j).getOpen5()>=data.get(i).getOpen5()+thr4*10 && mode == -1){
									//if (maxMins.get(j)<=-thr4 && mode == -1){
									begin1 = j;
									break;
								}
							}
							TradingUtils.getMaxMinShort(data, qm, calqm, begin1, begin1+maxBars);
							TradingUtils.getMaxMinShort(data, qmi, calqm, begin0, begin0+maxBars);
							

							int pipsMax=-1;
							int pipsMax0=-1;
							QuoteShort qr = null;
							int entry = data.get(begin0).getOpen5();
							if (mode==1){
								pipsMax = qm.getHigh5()-data.get(begin1).getOpen5();
								pipsMax0 = qmi.getHigh5()-data.get(begin0).getOpen5();	
								int limitSL = (int) pipsToMaxLoss;
								int limitTP = (int) pipsBE;
								qr = TradingUtils.getMaxMinShortLimitSLTP(data,begin0, data.size()-1, limitSL,  limitTP, true);
							}else if (mode==-1){
								pipsMax = data.get(begin1).getOpen5()-qm.getLow5();
								pipsMax0 = data.get(begin0).getOpen5()-qmi.getLow5();	
								int limitSL = (int) pipsToMaxLoss;
								int limitTP = (int) pipsBE;
								qr = TradingUtils.getMaxMinShortLimitSLTP(data,begin0, data.size()-1, limitSL,  limitTP, false);
							}
							
							String qres = "NONE";
							if (qr!=null){
								if (qr.getExtra()==1){
									qres = "OK";
								}else if (qr.getExtra()==-1){
									qres = "FAIL";
									totalFails2++;
								}else if (qr.getExtra()==0){
									qres = "NONE2";
									totalFails2++;
								}
							}
							
							if (actualId!=lastId){
								totalNewDays++;
								lastId = actualId;
								
								if (pipsMax0!=-1 && pipsMax!=-1){
									avgPips0+=pipsMax0;
									avgPips1+=pipsMax;
								}
								
								String r = "NO";
								if (i!=begin1) r = "SI";
								if (debug==6)
								System.out.println("i y begin1 "+i+" "+begin1
										+" "+mode+" || "+data.get(i).toString()+" || "+data.get(begin1).toString()
										+" || "+r);
								String prediction = "OK";
								if (pipsMax*0.1<10 ){
									
									//los que fallan son los que teoticamente habria que sumar
									//pierdo todo
									/*balance = 10000;
									amountAdded += 10000;							
									losses++;*/
									
									prediction = "FAIL";
									totalFails++;
								}
								
							
								if ((debug==5 || debug==10)	
										&& (qres!="OK" || prediction=="FAIL")
										){																										
									System.out.println("[NEW DAY] "+q.toString()
										+" || "
										+" "+PrintUtils.Print2Int(actualId,7)
										//+" "+PrintUtils.Print2dec(profit, false,10)
										+" "+PrintUtils.Print2dec(pipsBE, false,4)
										+" "+PrintUtils.Print2dec(pipsToMaxLoss, false,6)
										+" "+PrintUtils.Print2dec(surviveFactor, false,6)
										+" ||| "+PrintUtils.Print2dec(pipsMax0*0.1, false,4)+" "+PrintUtils.Print2dec(pipsMax*0.1, false,4)
										+" || "+prediction+" || "+qres+" || "+mode
										);
								}//debug
							}//lastId
							doubts++;
						}//profit<0
						//desactivamos,hasta que se desarrolle el arma nuclear												
					}//isTrade													
				}//actualTrades>0
				//arma nuclear
				
				mode = 0;
				actualTrades = 0;	
				//reset
				dayProfit = 0;
				trade23inserted = false;
				directionDay = 0;
				lastDay = day;
			}
			
			if (actualTrades>=30){
				System.out.println(q.toString());
			}
					
			int maxMin = maxMins.get(i-1);
			if (h>=h1){
				if (maxMin>=thr1){
					directionDay = 1;
				}else if (maxMin<=-thr1){
					directionDay = -1;
				}
			}
			
			
			//evaluacion nuevas entradas
			if (actualTrades<maxTrades 
					&& dayProfit<minDayProfit
					//&& dayProfit<balance*0.01
					){
				if (h>=h1 && h<=h2){					
					if (mode==0){
						if (directionDay==1 && maxMin<=-thr2){									
							mode = 1;
							avgPrice = q.getOpen5();
							lastTrade = q.getOpen5();
							actualTrades = 1;
							isFactor10 = false;
							idTrade++;
							//obtencion del pipvalue
							pipValue = balance/maxPipsToDie;	
							actualId++;
							//target$ = tp$;
							target$ = pipValue*tp$;
							if (debug==3 || debug==10){
								System.out.println("[LONG OPEN 1] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" ||  "+pipValue+" "+target$);
							}
						}else if (directionDay==-1 && maxMin>=thr2){												
							mode = -1;
							avgPrice = q.getOpen5();
							lastTrade = q.getOpen5();
							actualTrades = 1;
							isFactor10 = false;
							idTrade++;
							//obtencion del pipvalue
							pipValue = balance/maxPipsToDie;
							actualId++;
							//target$ = tp$;
							target$ = pipValue*tp$; // 5 pips
							if (debug==3 || debug==10){
								System.out.println("[SHORT OPEN 1] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" ||  "+pipValue+" "+target$);
							}
						}
					}else if (mode==1){//busco longs
						int diff = lastTrade-q.getOpen5();
						if (diff>=minDistance*10){
							lastTrade = q.getOpen5();
							avgPrice = (actualTrades*avgPrice+q.getOpen5())/(actualTrades+1);
							actualTrades++;
							if (debug==3 || debug==10){
								System.out.println("[LONG ADD] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" || "+avgPrice);
							}
						}						
					}else if (mode==-1){
						int diff = q.getOpen5()-lastTrade;
						if (diff>=minDistance*10){
							lastTrade = q.getOpen5();
							avgPrice = (actualTrades*avgPrice+q.getOpen5())/(actualTrades+1);
							actualTrades++;
							if (debug==3 || debug==10){
								System.out.println("[SHORT ADD] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" || "+avgPrice);
							}
						}									
					}					
				}
			}
			
			//ver si se ha llegado al objetivo
			
			if (actualTrades>0){
				
				int pips = 0;
				boolean isTrade = false;
				if (mode==1){
					pips = q.getClose5()-avgPrice;
					isTrade = true;	
				}else if (mode==-1){
					pips = avgPrice-q.getClose5();
					isTrade = true;
				}
				
				if (isTrade){
					profit = (pips*0.1)*actualTrades*pipValue;
					
					double commValue = actualTrades*pipValue*comm; 
					
					if (profit>=target$+commValue){					
						actualTrades = 0;
						mode = 0;
						wins++;
						
						totalProfit	+= profit-commValue;
						dayProfit 	+= tp$-comm;
						balance 	+= profit-commValue;
						
						if (profit-commValue>=0)
							win$$ += profit-commValue; 
						else
							lost$$ += -(profit-commValue);
						if (isFactor10){
							winFactor10++;
							isFactor10 = false;
						}
						
						if (debug==4 || debug==10)
							System.out.println("[WIN] "+q.toString()+" || "+wins+" "+totalProfit+" || "+balance);
					}else{																							
						double maxLoss = balance*maxLossRisk/100.0;
						if (profit-commValue<=-maxLoss){						
							actualTrades = 0;
							mode = 0;
							losses++;
							totalProfit += profit-commValue;
																				
							lost$$ += -(profit+commValue);							
							balance += profit-commValue;
							
							if (debug==4 || debug==10)
								System.out.println("[LOSS] "+q.toString()+" || "+profit+" || "+q.toString()+" || "+balance);
							
							isFail = true;						
							if (isFactor10){
								lossFactor10++;
								isFactor10 = false;
							}
							
							if (balance<=100){
								balance = 10000;
								amountAdded += balance-100;							
							}
						}
					}				
				}//istrade				
			}
			
		}
		
		int trades = wins+losses;
		double pf = win$$/lost$$;
		double winPer = wins*100.0/trades;
		String res = "[WIN!!]";
		if (isFail) res = "[FAIL!!]";
		System.out.println(y1+" "+y2
				+" "+res
				+" "+header+" || "
				+" "+trades
				+" "+wins+" "+losses+" "+doubts
				+" "+PrintUtils.Print2dec( winPer, false)
				//+" "+PrintUtils.Print2dec(balance, false)
				+" "+PrintUtils.Print2dec2(balance-amountAdded, true)
				//+" "+PrintUtils.Print2dec(profitNegDay, false)
				+" || "+totalFactor10+" "+PrintUtils.Print2dec(winFactor10*100.0/totalFactor10, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+totalNewDays+" "+totalFails+" "+PrintUtils.Print2dec(totalFails*100.0/totalNewDays, false)
				+" "+PrintUtils.Print2dec(totalFails*100.0/trades,false)+" "+PrintUtils.Print2dec(totalFails2*100.0/trades,false)
				+" || "+PrintUtils.Print2dec(avgPips0*0.1/totalNewDays,false)+" "+PrintUtils.Print2dec(avgPips1*0.1/totalNewDays,false)
	    );
		
	}
	
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,
			int maxTrades,
			int maxTrades2,
			int thr1,
			int thr2,
			int thr3,
			int minDistance,
			double profitTargetF,
			double maxLossF,
			int abePips,
			double factorArmaNuclear,
			double factorPipValue,
			double comm,
			int debug,
			boolean printSummary
			){
		
		double balance = 10000;
		double amountAdded = 10000;
		double win$$ = 0;
		double lost$$=0;
		double maxLossFactor = 0;
		double totalProfit = 0;
		int actualTrades = 0;
		int lastTrade = -1;
		int mode = 0;
		int avgPrice = 0;
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
		int totalFactor10 = 0;
		int winFactor10 = 0;
		int lossFactor10 = 0;
		int lastDay = -1;
		int idTrade = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int directionDay = 0;
		double survivorPer = -1;
		double pipsToDie = -1;
		double pipsBE = -1;
		boolean isFactor10 = false;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		boolean isFail = false;
		boolean trade23inserted = false;
		double profit = 0;
		double actualTarget = 0;
		double dayProfit = 0;
		double pipValue = 1;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR); 
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			
			if (day!=lastDay){		
				double f = pipsToDie/pipsBE;
				if (actualTrades>0){
					if (f<10.0 && !isFactor10){
						isFactor10 = true;
						totalFactor10++;
						//System.out.println("totalfactor: "+totalFactor10);
					}
					
					int pips1 = 0;
					boolean isTrade1 = false;
					if (mode==1){
						pips1 = q.getClose5()-avgPrice;
						isTrade1 = true;	
					}else if (mode==-1){
						pips1 = avgPrice-q.getClose5();
						isTrade1 = true;
					}
					
					if (isTrade1){
						profit = (pips1*0.1)*actualTrades*pipValue;
					}
					
					pipsBE = 0;
					double fBE = 0;
					double actualPipValue = pipValue*actualTrades;
					if (profit<0){
						pipsBE = -profit/actualPipValue;
						double maxLoss = balance*maxLossF/100.0;
						pipsToDie = maxLoss/actualPipValue;
						fBE = pipsToDie/pipsBE;
						survivorPer = pipsToDie*100.0/(pipsBE+pipsToDie);
					}
							
					if (debug==1 || debug==10
								//&& f<10.0
								){						
							System.out.println("[NEW DAYACTUAL RESULT] "+DateUtils.datePrint(cal)
								+" || "+q.toString()
								+" ||| "
								+" "+idTrade
								+" "+PrintUtils.Print2dec(pipsBE, false)+" "+PrintUtils.Print2dec(pipsToDie, false)
								+" "+PrintUtils.Print2dec(fBE, false)
								+" "+PrintUtils.Print2dec(survivorPer, false)
							);						
					}
					
					//ALGO ARMA NUCLEAR
					if (fBE>=factorArmaNuclear && profit<0 && pipsBE>abePips){
						int totalTradesNeeded10 = (int) (Math.abs(profit)/(abePips*pipValue));
						if (totalTradesNeeded10>0){
							if (totalTradesNeeded10>=maxTrades2) totalTradesNeeded10 = maxTrades2; 
							int newTrades = totalTradesNeeded10-actualTrades;
							avgPrice = (avgPrice*actualTrades+q.getOpen5()*newTrades)/totalTradesNeeded10;
							
							actualTrades = totalTradesNeeded10;
							 actualPipValue = pipValue*actualTrades;													
							
							pipsBE = Math.abs(profit) /actualPipValue;
							if (debug==1 || debug==10
									//&& f<10.0
									){						
								System.out.println("[AUMENTAN NUMERO DE TRADES] "+DateUtils.datePrint(cal)
									+" || "+q.toString()
									+" ||| "
									+" "+PrintUtils.Print2dec(profit, false)
									+" "+actualTrades+" || "+" "+PrintUtils.Print2dec(actualPipValue, false)+" "+PrintUtils.Print2dec(pipsBE, false)+" || "+avgPrice
								);						
							}
							actualTarget = 0;
						}
					}
				}
				dayProfit = 0;
				trade23inserted = false;
				directionDay = 0;
				lastDay = day;
			}
			
			if (actualTrades>=30){
				System.out.println(q.toString());
			}
					
			int maxMin = maxMins.get(i-1);
			if (h>=h1){
				if (maxMin>=thr1){
					directionDay = 1;
				}else if (maxMin<=-thr1){
					directionDay = -1;
				}
			}
			
			
			//evaluacion nuevas entradas
			if (actualTrades<maxTrades && dayProfit<balance*0.01){
				if (h>=h1){					
					if (mode==0){
						if (directionDay==1 && maxMin<=-thr2){									
							mode = 1;
							avgPrice = q.getOpen5();
							lastTrade = q.getOpen5();
							actualTrades = 1;
							isFactor10 = false;
							idTrade++;
							actualTarget = balance*profitTargetF/100;
							//pipValue = actualTarget/10;//pips target=20
							
							pipValue = balance/factorPipValue;
							actualTarget = pipValue*50;
							if (debug==3 || debug==10){
								System.out.println("[LONG OPEN 1] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" ||  "+pipValue+" "+actualTarget);
							}
						}else if (directionDay==-1 && maxMin>=thr2){												
							mode = -1;
							avgPrice = q.getOpen5();
							lastTrade = q.getOpen5();
							actualTrades = 1;
							isFactor10 = false;
							idTrade++;
							actualTarget = balance*profitTargetF/100;
							pipValue = actualTarget/10;//pips target=20
							
							pipValue = balance/factorPipValue;
							actualTarget = pipValue*50;
							if (debug==3 || debug==10){
								System.out.println("[SHORT OPEN 1] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" ||  "+pipValue+" "+actualTarget);
							}
						}
					}else if (mode==1){//busco longs
						int diff = lastTrade-q.getOpen5();
						if (diff>=minDistance*10){
							lastTrade = q.getOpen5();
							avgPrice = (actualTrades*avgPrice+q.getOpen5())/(actualTrades+1);
							actualTrades++;
							if (debug==3 || debug==10){
								System.out.println("[LONG ADD] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" || "+avgPrice);
							}
						}						
					}else if (mode==-1){
						int diff = q.getOpen5()-lastTrade;
						if (diff>=minDistance*10){
							lastTrade = q.getOpen5();
							avgPrice = (actualTrades*avgPrice+q.getOpen5())/(actualTrades+1);
							actualTrades++;
							if (debug==3 || debug==10){
								System.out.println("[SHORT ADD] "+DateUtils.datePrint(cal)+" || "+q.getOpen5()+" || "+avgPrice);
							}
						}									
					}					
				}
			}
			
			//ver si se ha llegado al objetivo
			
			if (actualTrades>0){
				
				int pips = 0;
				boolean isTrade = false;
				if (mode==1){
					pips = q.getClose5()-avgPrice;
					isTrade = true;	
				}else if (mode==-1){
					pips = avgPrice-q.getClose5();
					isTrade = true;
				}
				
				if (isTrade){
					profit = (pips*0.1)*actualTrades*pipValue;
					
					double commValue = actualTrades*pipValue*comm; 
					
					if (profit>=actualTarget+commValue){					
						actualTrades = 0;
						mode = 0;
						wins++;
						
						totalProfit += profit-commValue;
						dayProfit += profit-commValue;
						balance += profit-commValue;
						if (profit-commValue>=0)
							win$$ += profit-commValue; 
						else
							lost$$ += -(profit-commValue);
						if (isFactor10){
							winFactor10++;
							isFactor10 = false;
						}
						
						if (debug==4 || debug==10)
						System.out.println("[WIN] "+q.toString()+" || "+wins+" "+totalProfit+" || "+balance);
					}else{
						double factor = profit/pipValue;
						
						if (factor<=maxLossFactor) maxLossFactor = factor;
					}
					
					double actualPipValue = pipValue*actualTrades;
					pipsBE = Math.abs(profit) /actualPipValue;
					pipsToDie = (pipValue*5000+profit)/actualPipValue;
					survivorPer = pipsToDie*100.0/(pipsToDie+pipsBE); 					
					double maxLoss = balance*maxLossF/100.0;
					if (profit-commValue<=-maxLoss){
						
						actualTrades = 0;
						mode = 0;
						totalProfit += profit-commValue;
						losses++;
												
						lost$$ += -(profit+commValue);
						
						balance += profit-commValue;
						
						if (debug==4 || debug==10)
							System.out.println("[LOSS] "+q.toString()+" || "+profit+" || "+q.toString()+" || "+balance);
						
						isFail = true;						
						if (isFactor10){
							lossFactor10++;
							isFactor10 = false;
						}
						
						if (balance<=100){
							balance = 10000;
							amountAdded += balance-100;							
						}
						//break;
					}
				
				}				
			}
			
		}
		
		int trades = wins+losses;
		double pf = win$$/lost$$;
		double winPer = wins*100.0/trades;
		String res = "[WIN!!]";
		if (isFail) res = "[FAIL!!]";
		System.out.println(y1+" "+y2
				+" "+res
				+" "+header
				+" "+trades
				+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec( winPer, false)
				+" "+PrintUtils.Print2dec(balance, false)
				+" "+PrintUtils.Print2dec(balance-amountAdded, false)
				+" || "+totalFactor10+" "+PrintUtils.Print2dec(winFactor10*100.0/totalFactor10, false)
				+" || "+PrintUtils.Print2dec(pf, false)
	    );
		
	}
		

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.11.02.csv";
		String pathGBPUSD = "C:\\fxdata\\gbpUSD_UTC_1 Min_Bid_2014.01.01_2016.11.13.csv";
		//String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.09.20.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathGBPUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 1;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 1;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			
			for (int y1=2014;y1<=2014;y1++){
				int y2 = y1+2;
				for (int h1=16;h1<=16;h1++){
					int h2 = 21;
					for (int maxTrades=1;maxTrades<=1;maxTrades++){
						for (int thr1=300;thr1<=300;thr1+=60){
							int thr3 = 0;
							for (int thr2=60;thr2<=60;thr2+=30){
								
								
								for (int minDistance=10;minDistance<=10;minDistance++){
									for (int tp$=5;tp$<=5;tp$+=5){
										for (int minDayProfit=100*tp$;minDayProfit<=100*tp$;minDayProfit+=1*tp$){
											for (double maxLossRisk=100;maxLossRisk<=100;maxLossRisk++){
												for (int maxBars=1440;maxBars<=1440;maxBars+=60){
													for (int thr4=0;thr4<=0;thr4+=5){
														//for (int minDayProfit=200;minDayProfit<=1000;minDayProfit+=1000){
														for (int maxPipsToDie=5000;maxPipsToDie<=5000;maxPipsToDie+=25){
															int maxTrades2 = 30;
															String header = minDistance
																	+" "+thr1+" "+maxTrades+" "+maxTrades2+" "+maxBars+" "+thr4+" "+thr2+" || "+tp$+" "+minDayProfit+" "+maxPipsToDie;
																	;
															
															TestClaudia.doTest2(header, data, maxMins, y1, y2, h1,h2, maxTrades,maxTrades2, thr1, thr2,thr3,thr4, minDistance, 
																	tp$,maxPipsToDie,maxLossRisk,maxBars,minDayProfit,0.0, 0,false);
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
				}
			}
			/*for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+7;
				for (int h1=16;h1<=16;h1++){
					for (int maxTrades=2;maxTrades<=2;maxTrades++){
						for (int maxTrades2=30;maxTrades2<=30;maxTrades2+=10){
							for (int thr1=300;thr1<=300;thr1+=1){
								int thr2 = 0;
								for (int thr3=0;thr3<=0;thr3+=30){
									for (int minDistance=10;minDistance<=10;minDistance+=5){
										for (double profitTargetF=0.1;profitTargetF<=0.1;profitTargetF+=1.0){
											//for (double pipValue=1;pipValue<=1;pipValue++){
												for (double maxLossF=100.0;maxLossF<=100;maxLossF+=1.0){
													for (int bePips=8;bePips<=8;bePips++){													
														for (double comm=2.0;comm<=2.0;comm+=0.5){
															for (double factorArmaNuclear=0.0;factorArmaNuclear<=0.0;factorArmaNuclear+=1.0){
																for (double factorPipValue=10000.0;factorPipValue<=10000.0;factorPipValue+=1000.0){
																	String header = minDistance+" "+PrintUtils.Print2dec(profitTargetF,false)+" "+maxLossF+" "+bePips
																			+" "+thr1+" "+maxTrades+" "+maxTrades2+" "+factorArmaNuclear+" "+factorPipValue
																			+" ||| ";
																	TestClaudia.doTest(header, data, maxMins, y1, y2, h1, maxTrades,maxTrades2, thr1, thr2,thr3, minDistance, 
																			profitTargetF,maxLossF,bePips,factorArmaNuclear,factorPipValue, comm, 10,false);
																}
															}
														}
													}
												}
											//}
										}								
									}
								}
							}
						}
						
					}
				}
			}*/
			
			
		}

	}

}
