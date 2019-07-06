package drosa.experimental.EA;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.experimental.CurrencyType;
import drosa.experimental.PositionShort;
import drosa.experimental.StatsDebugOptions;
import drosa.experimental.SuperStrategy;
import drosa.experimental.SystemStats;
import drosa.experimental.TestNewHighsLowsGeneric;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class AlgoFunctions {

	/*public static void checkAlgoByhours(ArrayList<QuoteShort> data,int begin,int end,
			int tp1,int tp2,int tpInc,int sl1,int sl2,int slInc,int h1,int h2,
			int bar,int leverage,
			int maxPos,
			double capital,CurrencyType currencyType,double comm,boolean printSummary,boolean printExp){
		
		double offPer = 0.4;
		int openDiff = 9999;
		Calendar cal = Calendar.getInstance();
		ArrayList<Integer> arrayAllowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) arrayAllowedHours.add(0);
		ArrayList<QuoteShort> maxMin = TradingUtils.calculateMaxMinByBarShort(data,bar);
		QuoteShort.getCalendar(cal, data.get(begin));
		
		
		for (int tp = tp1;tp<=tp2;tp+=tpInc){
			for (int sl=sl1;sl<=sl2;sl+=slInc){	
				int totalWins = 0;
				int totalTrades = 0;
				for (int h=h1;h<=h2;h++){
					SuperStrategy.convertAllowedTime(arrayAllowedHours, String.valueOf(h), 0, 23,leverage,0);
					String header = DateUtils.datePrint(cal)+" "+String.valueOf(bar)+" "+h
							+" "+String.valueOf(leverage)+" "+String.valueOf(maxPos)+" "+tp+" "+sl;
					SystemStats stats = TestNewHighsLowsGeneric.testHighLowReal(header,capital,
							arrayAllowedHours,data,maxMin,begin,end,tp, sl,bar,
							offPer,maxPos,openDiff,comm,
							currencyType,true,false,printSummary);	
					totalWins+=stats.getTotalWins();
					totalTrades+=stats.getTotalTrades();
				}
				double exp = (totalWins*tp*1.0-(totalTrades-totalWins)*sl*1.0)/totalTrades;
				if (printExp)
					System.out.println(tp+" "+sl+" "+PrintUtils.Print2(exp)+" "+PrintUtils.Print2((exp-comm)*tp/sl));
			}			
		}
	}*/
	
	
	public static void checkAlgoBySLTPBins(ArrayList<QuoteShort> data,ArrayList<QuoteShort> dailyData,
			String hours,String months,int begin,int end,int numBins,int binOffset,
			int tp1,int tp2,int tpInc,int sl1,int sl2,int slInc,
			int bar1,int bar2,int barInc,
			int brokerLeverage,
			double risk1,double risk2,double riskInc,
			double riskExtra1,double riskExtra2,double riskExtraInc,
			int pos1,int pos2,double off1,double off2,double offInc,
			int openDiff1,int openDiff2,int openDiffInc,int hCloseOffset1,int hCloseOffset2,
			int offsetOC1,int offsetOC2,int offsetOCInc,
			int diffOpenParam1,int diffOpenParam2,int diffOpenParamInc,
			double capital,CurrencyType currencyType,double comm,
			int bePips1,int bePips2,double tpPips1,double tpPips2,double tpPipsInc,
			boolean simple,boolean digits5) throws Exception{
		
		
		int binSize = (end-begin)/numBins;
		double offPer = 0.4;
		ArrayList<ArrayList<QuoteShort>> maxMins = new ArrayList<ArrayList<QuoteShort>>();
		ArrayList<Double> riskPerTrades = new ArrayList<Double>();for (int i=0;i<=23;i++) riskPerTrades.add(0.0);
		ArrayList<Double> riskExtraPerTrades = new ArrayList<Double>();for (int i=0;i<=23;i++) riskExtraPerTrades.add(0.0);
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		ArrayList<QuoteShort> maxMin   = null;
		ArrayList<QuoteShort> maxMin0  = null;ArrayList<QuoteShort> maxMin1  = null;ArrayList<QuoteShort> maxMin2  = null;
		ArrayList<QuoteShort> maxMin3  = null;ArrayList<QuoteShort> maxMin4  = null;ArrayList<QuoteShort> maxMin5  = null;
		ArrayList<QuoteShort> maxMin6  = null;ArrayList<QuoteShort> maxMin7  = null;ArrayList<QuoteShort> maxMin8  = null;
		ArrayList<QuoteShort> maxMin9  = null;ArrayList<QuoteShort> maxMin10 = null;ArrayList<QuoteShort> maxMin11 = null;
		ArrayList<QuoteShort> maxMin12 = null;ArrayList<QuoteShort> maxMin13 = null;ArrayList<QuoteShort> maxMin14 = null;
		ArrayList<QuoteShort> maxMin15 = null;ArrayList<QuoteShort> maxMin16 = null;ArrayList<QuoteShort> maxMin17 = null;
		ArrayList<QuoteShort> maxMin18 = null;ArrayList<QuoteShort> maxMin19 = null;ArrayList<QuoteShort> maxMin20 = null;
		ArrayList<QuoteShort> maxMin21 = null;ArrayList<QuoteShort> maxMin22 = null;ArrayList<QuoteShort> maxMin23 = null;
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedMonths.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		SuperStrategy.convertAllowedTime(allowedMonths,months,0,11,1,0);
		
		if (!simple){
			double defaultRisk = 2.0;
			//pruba
			/*maxMin0 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(0,defaultRisk);//mejor 15 o 155
			maxMin1 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(1,defaultRisk);
			maxMin2 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(2,defaultRisk);
			maxMin3 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(3,defaultRisk);
			maxMin4 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(4,defaultRisk);
			maxMin5 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(5,defaultRisk);
			maxMin6 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(6,defaultRisk);
			maxMin7 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(7,defaultRisk);
			maxMin8 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(8,defaultRisk);
			maxMin9 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(9,defaultRisk);
			maxMin23 = TradingUtils.calculateMaxMinByBarShort(data,bar1);riskPerTrades.set(23,defaultRisk);*/
			
			//Asian session
			if (allowedHours.get(0)==1)
				maxMin0 = TradingUtils.calculateMaxMinByBarShort(data,155);riskPerTrades.set(0, defaultRisk);//mejor 15 o 155
			if (allowedHours.get(1)==1)
				maxMin1 = TradingUtils.calculateMaxMinByBarShort(data,140);riskPerTrades.set(1,defaultRisk);
			if (allowedHours.get(2)==1)
				maxMin2 = TradingUtils.calculateMaxMinByBarShort(data,450);riskPerTrades.set(2,defaultRisk);
			if (allowedHours.get(3)==1)
				maxMin3 = TradingUtils.calculateMaxMinByBarShort(data,710);riskPerTrades.set(3,defaultRisk);
			if (allowedHours.get(4)==1)
				maxMin4 = TradingUtils.calculateMaxMinByBarShort(data,520);riskPerTrades.set(4,defaultRisk);
			if (allowedHours.get(5)==1)
				maxMin5 = TradingUtils.calculateMaxMinByBarShort(data,240);riskPerTrades.set(5,defaultRisk);
			if (allowedHours.get(6)==1)
				maxMin6 = TradingUtils.calculateMaxMinByBarShort(data,140);riskPerTrades.set(6,defaultRisk);
			if (allowedHours.get(7)==1)
				maxMin7 = TradingUtils.calculateMaxMinByBarShort(data,380);riskPerTrades.set(7,defaultRisk);
			if (allowedHours.get(8)==1)
				maxMin8 = TradingUtils.calculateMaxMinByBarShort(data,720);riskPerTrades.set(8,defaultRisk);
			if (allowedHours.get(9)==1)
				maxMin9 = TradingUtils.calculateMaxMinByBarShort(data,220);riskPerTrades.set(9,defaultRisk);
			if (allowedHours.get(23)==1)
				maxMin23 = TradingUtils.calculateMaxMinByBarShort(data,1200);riskPerTrades.set(23,defaultRisk);//1250
			if (allowedHours.get(10)==1)
				maxMin10 = TradingUtils.calculateMaxMinByBarShort(data,600);riskPerTrades.set(10,defaultRisk);
			if (allowedHours.get(11)==1)		
				maxMin11 = TradingUtils.calculateMaxMinByBarShort(data,600);riskPerTrades.set(11,defaultRisk);
				if (allowedHours.get(12)==1)
			maxMin12 = TradingUtils.calculateMaxMinByBarShort(data,500);riskPerTrades.set(12,defaultRisk);
			if (allowedHours.get(13)==1)
			maxMin13 = TradingUtils.calculateMaxMinByBarShort(data,500);riskPerTrades.set(13,defaultRisk);
			if (allowedHours.get(14)==1)
			maxMin14 = TradingUtils.calculateMaxMinByBarShort(data,500);riskPerTrades.set(14,defaultRisk);
			if (allowedHours.get(15)==1)
			maxMin15 = TradingUtils.calculateMaxMinByBarShort(data,1200);riskPerTrades.set(15,defaultRisk);
			if (allowedHours.get(16)==1)
			maxMin16 = TradingUtils.calculateMaxMinByBarShort(data,1200);riskPerTrades.set(16,defaultRisk);
			if (allowedHours.get(17)==1)
			maxMin17 = TradingUtils.calculateMaxMinByBarShort(data,800);riskPerTrades.set(17,defaultRisk);
			if (allowedHours.get(18)==1)
			maxMin18 = TradingUtils.calculateMaxMinByBarShort(data,500);riskPerTrades.set(18,defaultRisk);
			if (allowedHours.get(19)==1)
			maxMin19 = TradingUtils.calculateMaxMinByBarShort(data,500);riskPerTrades.set(19,defaultRisk);
			if (allowedHours.get(20)==1)
			maxMin20 = TradingUtils.calculateMaxMinByBarShort(data,500);riskPerTrades.set(20,defaultRisk);
			if (allowedHours.get(21)==1)
			maxMin21 = TradingUtils.calculateMaxMinByBarShort(data,800);riskPerTrades.set(21,defaultRisk);
			if (allowedHours.get(22)==1)
			maxMin22 = TradingUtils.calculateMaxMinByBarShort(data,1200);riskPerTrades.set(22,defaultRisk);
			//
		
			
			maxMins.add(maxMin0);maxMins.add(maxMin1);maxMins.add(maxMin2);
			maxMins.add(maxMin3);maxMins.add(maxMin4);maxMins.add(maxMin5);
			maxMins.add(maxMin6);maxMins.add(maxMin7);maxMins.add(maxMin8);
			maxMins.add(maxMin9);maxMins.add(maxMin10);maxMins.add(maxMin11);
			maxMins.add(maxMin12);maxMins.add(maxMin13);maxMins.add(maxMin14);
			maxMins.add(maxMin15);maxMins.add(maxMin16);maxMins.add(maxMin17);
			maxMins.add(maxMin18);maxMins.add(maxMin19);maxMins.add(maxMin20);
			maxMins.add(maxMin21);maxMins.add(maxMin22);maxMins.add(maxMin23);
		}
		
		
		
		SystemStats stats = null;
		
		
		for (int bar=bar1;bar<=bar2;bar+=barInc){
			//prueba temporal
			//maxMin0 = TradingUtils.calculateMaxMinByBarShort(data,bar);//prueba temporal, usado para probar uno
			//
			maxMins.set(0,maxMin0);
			
			if (simple){
				maxMin = TradingUtils.calculateMaxMinByBarShort(data,bar);
				maxMins.clear();
				maxMins.add(maxMin);
				riskPerTrades.set(6, 8.0);
			}
			for (double risk=risk1;risk<=risk2;risk+=riskInc){	
				for (double riskExtra=riskExtra1;riskExtra<=riskExtra2;riskExtra+=riskExtraInc){	
					for (int i=0;i<=23;i++){ 
						riskPerTrades.set(i, risk); 
						riskExtraPerTrades.set(i, riskExtra);
					}
					double avgPF = 0;
					int totalPF = 0;
					for (int tp = tp1;tp<=tp2;tp+=tpInc){
					//for (int tp = tp1;tp<=tp2;tp+=tpInc){
						//for (int sl=sl1;sl<=sl2;sl+=slInc){	
						for (int sl=(int) (tp*2.5);sl<=tp*2.5;sl+=tp*0.5){	
							double totalWins = 0;
							double totalTrades = 0;
							int countLess0 = 0;
							int countLess2 = 0;
							double totalAmountNeeded=0;
							double avgMaxFactor = 0;
							
							for (int positions = pos1;positions<=pos2;positions++){
								for (double off = off1;off<=off2;off+=offInc){														
									for (int openDiff = openDiff1;openDiff<=openDiff2;openDiff+=openDiffInc){	
										for (int offsetOC = offsetOC1;offsetOC<=offsetOC2;offsetOC+=offsetOCInc){
											for (int diffOpenParam = diffOpenParam1;diffOpenParam<=diffOpenParam2;diffOpenParam+=diffOpenParamInc){ 
												for (int bePips=bePips1;bePips<=bePips2;bePips++){
													for (double tpPips=tpPips1;tpPips<=tpPips2;tpPips+=tpPipsInc){
														for (int bin=0;bin<numBins;bin++){										
															Sizeof.runGC ();	
															int begin1 = begin+bin*binSize;
															int end1   = begin1+binOffset; 
															if (begin1>data.size()-1) continue;
															if (begin1+binOffset>data.size()-1) end1=data.size()-1;
															QuoteShort.getCalendar(cal, data.get(begin1));
															QuoteShort.getCalendar(cal2, data.get(end1));
															String  header = DateUtils.datePrint(cal)
																				 +" "+DateUtils.datePrint(cal2)
																				 +" "+bar//provisional para pruebas con 0
																				 +" "+bin
																				 +" "+openDiff
																				 +" "+offsetOC
																				 +" "+diffOpenParam
																				 +" "+PrintUtils.Print2dec(tpPips,false,2)
																				 +" "+PrintUtils.Print2(off)
																				 +" "+PrintUtils.Print2(comm)
																				 +" "+PrintUtils.Print2dec(risk,false,2)
																				 +" "+PrintUtils.Print2dec(riskExtra,false,2)
																				 +" "+String.valueOf(positions)
																				 +" "+tp
																				 +" "+sl;
															
															if (simple){
																 header = DateUtils.datePrint(cal)
																		 +" "+DateUtils.datePrint(cal2)
																		 +" "+bar
																		 +" "+bin
																		 +" "+openDiff
																		 +" "+offsetOC
																		 +" "+diffOpenParam
																		 +" "+PrintUtils.Print2(off)
																		 +" "+PrintUtils.Print2(comm)
																		 +" "+String.valueOf(risk)
																		 +" "+String.valueOf(riskExtra)
																		 +" "+String.valueOf(positions)
																		 +" "+tp
																		 +" "+sl;
															}
															
															
															stats = TestNewHighsLowsGeneric.testHighLowRealMaxMins(header,capital,
																				brokerLeverage,
																				allowedHours,allowedMonths,data,maxMins,riskPerTrades,riskExtraPerTrades,
																				begin1,end1,tp,sl,
																				off,positions,openDiff,offsetOC,diffOpenParam,
																				comm,
																				currencyType,bePips,tpPips,
																				true,simple,digits5,StatsDebugOptions.ONLY_SUMMARY);
																
															//PositionShort.studyDOdistance(dailyData,stats.getPositions());
															
															totalWins+=stats.getTotalWins();
															totalTrades+=stats.getTotalTrades();
															avgMaxFactor += stats.getMaxEquitity()/stats.getAmountNeeded();
															totalAmountNeeded += stats.getAmountNeeded();
															if (stats.getBalance()/stats.getAmountNeeded()<1.0) countLess0++;
															if (stats.getMaxEquitity()/stats.getAmountNeeded()<2.0) countLess2++;
														}//bin	
													}//tpPips
												}//bePips
											}//diffOpenParam
										}//offsetOC
									}//openDiff
								}//off
							}//positions
							avgPF+= (totalWins*tp)/((totalTrades-totalWins)*sl);
							totalPF++;
							double winPer = totalWins*100.0/totalTrades;
							double expAvg = (totalWins*tp-(totalTrades-totalWins)*sl)/(totalTrades);
							/*System.out.println(PrintUtils.Print2(risk)+" "+tp+" "+sl
									+" "+PrintUtils.Print2(expAvg)
									+" "+PrintUtils.Print2dec2(totalAmountNeeded/numBins,true)
									+" "+PrintUtils.Print2dec2(avgMaxFactor/numBins,true)
									+" "+PrintUtils.Print2(avgMaxFactor/totalAmountNeeded)
									+" "+countLess0+" "+countLess2);*/
						}//sl
					}//tp
					//System.out.println("avgPF = "+PrintUtils.Print2(avgPF*1.0/totalPF));
				}//extraRisk
			}//risk		
		}//bar
	}
	
	
	

}
