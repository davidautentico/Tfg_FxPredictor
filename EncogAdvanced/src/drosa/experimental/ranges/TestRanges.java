package drosa.experimental.ranges;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestRanges {
	
	public static void doStudyHoursPeakSystem(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int hTest,
			int minPips,
			int thr,
			int debug
			){
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
	
		int peakH = -1;
		int peakL = -1;
		
		int hourH = 0;
		int hourL = 0;
		int lastDay = -1;
		int hMode = 0;
		int countTest = 0;
		int wins = 0;
		int testPeak = 0;
		int testPeakValid = 0;
		int referencePoint = 0;
		int accLoss = 0;
		int accWin = 0;
		int testPeaki = 0;
		int newsi=0;
		int tpvalue = -1;
		int slvalue = -1;
		int dayOpen = 0;
		ArrayList<Integer> peaks1 = new ArrayList<Integer>();
		ArrayList<Integer> peaks2 = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			peaks1.add(0);
			peaks2.add(0);
		}
		
		int accChanges = 0;
		int dayChanges = 0;
		int zerochanges=0;
		int totalDays=0;
		int impact = 0;
		ArrayList<Integer> changeList = new ArrayList<Integer>();
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (h<h1 || h> h2) continue;
			
			if (day!=lastDay){
				
					
				if (lastDay!=-1){
					if (hourH<=hourL){
						int count = peaks1.get(hourH);
						peaks1.set(hourH, count+1);											
						count = peaks2.get(hourL);
						peaks2.set(hourL, count+1);
					}else{
						int count = peaks1.get(hourL);
						peaks1.set(hourL, count+1);
						count = peaks2.get(hourH);
						peaks2.set(hourH, count+1);						
					}
				}
				if (debug==1){
					System.out.println("[NEW DAY] "+DateUtils.datePrint(cal));
				}
				
				if (impact<=0){
					totalDays++;
					if (dayChanges==0){
						zerochanges++;
					}
					accChanges+=dayChanges;
					changeList.add(dayChanges);
					if (debug>=1){
						if (dayChanges==0){
							System.out.println("[****DAY TREND****] "+DateUtils.datePrint(cal)+" || "+zerochanges+" / "+totalDays);
						}
					}
				}
				
				peakH = -1;
				peakL = -1;				
				hourH = 0;
				hourL = 0;
				lastDay = day;
				hMode = 0;
				dayOpen = q.getOpen5();
				dayChanges = 0;

				impact = FFNewsClass.getDayImpact(news, cal, newsi);
				newsi++;
			}
			
			int maxMin = maxMins.get(i);
						
			if (peakH==-1
					|| q.getHigh5()>=peakH
					
					){	
				
				int diff = q.getHigh5()-dayOpen;
				if (hMode==-1){
					diff = q.getHigh5()-peakL;
				}
				if (diff>=minPips
						//&& impact<=0
						){
					if (hMode==-1){
						if (debug==1){
							System.out.println("  [HIGH CHANGE] "+q.toString());
						}
						dayChanges++;
					}
					if (hMode==0 && debug==1){
						System.out.println("[HIGH stablished] "+q.toString());
					}
					hMode=1;
				}
				if (debug==1){
					System.out.println("[NEW HIGH] "+q.toString());
				}			
				peakH = q.getHigh5();//voy actualizando los picos
				hourH = h;
			}
			if (peakL==-1 
					|| q.getLow5()<=peakL 
					){
				
				int diff = dayOpen-q.getLow5();
				if (hMode==1){
					diff = peakH-q.getLow5();
				}
				if (diff>=minPips
						//&& impact<=0
						){
					if (hMode==1){
						if (debug==1){
							System.out.println("  [LOW CHANGE] "+q.toString());
						}
						dayChanges++;
					}
					if (hMode==0 && debug==1){
						System.out.println("[LOW stablished] "+q.toString());
					}
					hMode=-1;
				}
				
				if (debug==1){
					System.out.println("[NEW LOW] "+q.toString());
				}
				peakL = q.getLow5();//voy actualizando los picos
				hourL = h;
			}
			
		}
		
		int total = 0;
		for (int i=0;i<=23;i++){
			total+= peaks1.get(i);
			//str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			//str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
		}
		
		String header="";
		String str1 ="";
		String str2="";
		String str3 ="";
		String str4="";
		String str5 = "";
		int acc1 = 0;
		int acc2 = 0;
		
		int globalTotal = 2*total;
		for (int i=0;i<=23;i++){
			header += PrintUtils.Print2Int(i, 3)+" ";
			str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
			acc1 += peaks1.get(i);
			acc2 += peaks2.get(i);
			str3 += PrintUtils.Print2dec(acc1*100.0/total, false, 3)+" ";
			str4 += PrintUtils.Print2dec(acc2*100.0/total, false, 3)+" ";
			
			int ht = peaks1.get(i)+peaks2.get(i);
			str5 += i+"="+PrintUtils.Print2dec(ht*100.0/globalTotal, false, 3)+" ";
		}
		
		
		//System.out.println(header);
		//System.out.println(str3);
		//System.out.println(str4);
		//System.out.println(str5);
		
		double per = wins*100.0/countTest;
		double per2 = (accLoss*0.1)/(countTest-wins);
		double pf = (accWin*1.0/accLoss);
		int losses = countTest-wins;
		double avg = (accWin-accLoss)*0.1/countTest;
		header = minPips
				+" || "+
				zerochanges+" / "+totalDays				
				+" "+PrintUtils.Print2dec(zerochanges*100.0/totalDays, false, 3)
				+" "+PrintUtils.Print2dec(accChanges*1.0/totalDays, false, 3); 
		 
		MathUtils.summary(header, changeList);
		
		/*System.out.println(
				minPips
				+" || "+
				zerochanges+" / "+totalDays				
				+" "+PrintUtils.Print2dec(zerochanges*100.0/totalDays, false, 3)
				+" "+PrintUtils.Print2dec(accChanges*1.0/totalDays, false, 3)
				);*/
		/*System.out.println(
				hTest
				//+" "+minDiff
				//+" "+minPips
				+" "+thr
				+" || "+countTest
				+" "+PrintUtils.Print2dec(per, false, 3)
				//+" "+(countTest-wins)+" "+accLoss
				+" "+PrintUtils.Print2dec(per2, false, 3)
				+" || "+PrintUtils.Print2dec(pf, false, 3)
				+" "+PrintUtils.Print2dec(avg, false, 3)
				);*/
	}
	
	public static void doStudyHoursPeakMaxMin(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int hTest,
			int minDiff,
			int minPips,
			int thr,
			int debug
			){
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
	
		int peakH = -1;
		int peakL = -1;
		
		int hourH = 0;
		int hourL = 0;
		int lastDay = -1;
		int hTested = 0;
		int countTest = 0;
		int wins = 0;
		int testPeak = 0;
		int testPeakValid = 0;
		int referencePoint = 0;
		int accLoss = 0;
		int accWin = 0;
		int testPeaki = 0;
		int newsi=0;
		int actualLosses = 0;
		int maxLosses = 0;
		ArrayList<Integer> peaks1 = new ArrayList<Integer>();
		ArrayList<Integer> peaks2 = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			peaks1.add(0);
			peaks2.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (h<h1 || h> h2) continue;
			
			if (day!=lastDay){
				
					
				if (lastDay!=-1){
					if (hourH<=hourL){
						int count = peaks1.get(hourH);
						peaks1.set(hourH, count+1);											
						count = peaks2.get(hourL);
						peaks2.set(hourL, count+1);
					}else{
						int count = peaks1.get(hourL);
						peaks1.set(hourL, count+1);
						count = peaks2.get(hourH);
						peaks2.set(hourH, count+1);						
					}
					
					if (hTested==1){
						int pips =q1.getClose5()-testPeakValid;
						accLoss += pips;
						if (debug==1){
							System.out.println("[LOSS] "+pips+" || "+testPeakValid+" || "+q1.toString());
						}
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
						//System.out.println(pips+" || "+accLoss);
					}else  if (hTested==-1){
						int pips = testPeakValid-q1.getClose5();
						accLoss += pips;
						if (debug==1){
							System.out.println("[LOSS] "+pips+" || "+testPeakValid+" || "+q1.toString());
						}
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
						//System.out.println(pips+" || "+accLoss);
					}
				}
				if (debug==1){
					System.out.println("[NEW DAY] "+DateUtils.datePrint(cal));
				}
				
				int impact = FFNewsClass.getDayImpact(news, cal, newsi);
				newsi++;
				
				peakH = -1;
				peakL = -1;				
				hourH = 0;
				hourL = 0;
				lastDay = day;
				hTested = 0;
				
			}
			
			int maxMin = maxMins.get(i);
		
						
			if (peakH==-1 
					//|| q.getHigh5()>=peakH+minDiff
					|| q.getHigh5()>=peakH
					//&& impact<=0
					){	
				if (peakH!=-1
						&& maxMin>=thr
						//&& impact<=9999
						){				
					if (hTest==h && hTested==0){
						hTested = 1;
						testPeak = peakH;
						testPeakValid = q.getClose5(); //el anterior pico + minDiff pips, dónde se pondría el limit SELL
						countTest++;
						testPeaki = i;
						if (debug==1){
							System.out.println("[HIGH PEAKVALID TESTED] "+testPeakValid+" || "+q.toString());
						}
					}
				}
				peakH = q.getHigh5();//voy actualizando los picos
				hourH = h;

				if (debug==1){
					System.out.println("[HIGH UPDATED] "+peakH+" || "+q.toString());
				}
			}
			if (peakL==-1 
					//|| q.getLow5()<=peakL-minDiff
					|| q.getLow5()<=peakL 
					//&& impact<=0
					){
				if (peakL!=-1
						&& maxMin<=-thr
						//&& impact<=0
						){
					if (hTest==h && hTested==0){
						hTested = -1;
						testPeak = peakL;
						testPeakValid = q.getClose5(); //el anterior pico - minDiff pips, dónde se pondría el limit BUY
						countTest++;
						testPeaki = i;
						if (debug==1){
							System.out.println("[LOW PEAKVALID TESTED] "+testPeakValid+" || "+q.toString());
						}
					}
				}
				peakL = q.getLow5();//voy actualizando los picos
				hourL = h;

				if (debug==1){
					System.out.println("[LOW UPDATED] "+peakL+" || "+q.toString());
				}
			}
			
			if (hTested==1 && 
					((q.getLow5()<=testPeakValid-minPips && i>testPeaki)
					 || (q.getClose5()<=testPeakValid-minPips))					
					){
				wins++;
				hTested = 2;
				accWin += minPips;
				actualLosses=0;
				if (debug==1){
					System.out.println("[HIGH PEAKVALID WIN!] "+testPeakValid+" || "+q.toString());
				}
				
				if (debug==2 && i==testPeaki){
					System.out.println("[HIGH PEAKVALID WIN! CONCURRED] "+testPeakValid+" || "+q.toString());
				}
			}
			if (hTested==-1 && 
					((q.getHigh5()>=testPeakValid+minPips && i>testPeaki)
							 || (q.getClose5()>=testPeakValid+minPips))	
					){
				wins++;
				hTested = 2;
				accWin += minPips;
				actualLosses=0;
				if (debug==1){
					System.out.println("[LOW PEAKVALID WIN!] "+testPeakValid+" || "+q.toString());
				}
				
				if (debug==2 && i==testPeaki){
					System.out.println("[ LOW PEAKVALID WIN! CONCURRED] "+testPeakValid+" || "+q.toString());
				}
			}
		}
		
		int total = 0;
		for (int i=0;i<=23;i++){
			total+= peaks1.get(i);
			//str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			//str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
		}
		
		String header="";
		String str1 ="";
		String str2="";
		String str3 ="";
		String str4="";
		String str5 = "";
		int acc1 = 0;
		int acc2 = 0;
		
		int globalTotal = 2*total;
		for (int i=0;i<=23;i++){
			header += PrintUtils.Print2Int(i, 3)+" ";
			str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
			acc1 += peaks1.get(i);
			acc2 += peaks2.get(i);
			str3 += PrintUtils.Print2dec(acc1*100.0/total, false, 3)+" ";
			str4 += PrintUtils.Print2dec(acc2*100.0/total, false, 3)+" ";
			
			int ht = peaks1.get(i)+peaks2.get(i);
			str5 += i+"="+PrintUtils.Print2dec(ht*100.0/globalTotal, false, 3)+" ";
		}
		
		
		//System.out.println(header);
		//System.out.println(str3);
		//System.out.println(str4);
		//System.out.println(str5);
		
		double per = wins*100.0/countTest;
		double avgL = (accLoss*0.1)/(countTest-wins);
		double pf = (accWin*1.0/accLoss);
		int losses = countTest-wins;
		double avg = (accWin-accLoss)*0.1/countTest;
		double f = avgL*10/minPips;
		double maxRisk = Math.pow(f, maxLosses);
		
		double totalRisk = TestRanges.calculateTotalRisk(minPips,avgL,maxLosses);
		System.out.println(
				hTest+" "+minDiff+" "+minPips+" "+thr
				+" || "+countTest
				+" "+PrintUtils.Print2dec(per, false, 3)
				//+" "+(countTest-wins)+" "+accLoss
				+" "+PrintUtils.Print2dec(avgL, false, 3)
				+" || "+PrintUtils.Print2dec(pf, false, 3)
				+" "+PrintUtils.Print2dec(avg, false, 3)
				+" || "+maxLosses
				+" "+PrintUtils.Print2dec(totalRisk, false, 3)
				);
	}
	
	public static void doStudyHoursPeakTPSLSystemComplete(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int hTest,
			int tp,
			int sl,
			int thr,
			boolean isModeBars,
			int debug,
			boolean debugPrint,
			int cappedLosses,
			double riskWin,
			double riskFullLoss
			){
		
		double initialBalance = 10000;
		double balance = 10000;
		double maxBalance = 10000;
		double maxDD = 0;
		double riskAcc = 0;//riesgo acumulado
		double actualRisk =riskWin; 
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
	
		int lastDay = -1;
		int hTested = 0;
		int countTest = 0;
		int wins = 0;
		int testPeak = 0;
		int entry = 0;
		int tpValue = 0;
		int slValue = 0;
		int referencePoint = 0;
		int accLoss = 0;
		int accWin = 0;
		int testPeaki = 0;
		int newsi=0;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;
		int hMode = 0;
		int totalTests = 0;
		int countdebug = 0;
		int countDayTrades = 0;
		int totalDays = 0;
		int dayTrades = 0;
		ArrayList<Integer> lossesArr = new ArrayList<Integer>();
		ArrayList<Integer> peaks1 = new ArrayList<Integer>();
		ArrayList<Integer> peaks2 = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			peaks1.add(0);
			peaks2.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			//if (h<h1 || h> h2) continue;
			
			if (day!=lastDay){
				
					
				if (lastDay!=-1){
					countDayTrades +=dayTrades;
					totalDays++;
				}
				if (debug==1){
					//System.out.println("[NEW DAY] "+DateUtils.datePrint(cal));
				}
								
				lastDay = day;
				if (hMode==2) hMode = 0;
				dayTrades = 0;
			}
			
			int maxMin = maxMins.get(i-1);
						
			if (hMode==0 
					&& (h>=h1 && h<=h2)
					//&& (h==0 && min>=15 || h!=0)
					//&& h==hTest
					){
				
				if (maxMin<=-thr){
					entry = q.getOpen5();
					tpValue = entry+tp*10;
					slValue = entry - (sl+1)*10;
					hMode = 1;
					totalTests++;
					dayTrades++;
				}else if (maxMin>=thr){
					entry = q.getOpen5();
					tpValue = entry-tp*10;
					slValue = entry + (sl-1)*10;
					hMode = -1;
					totalTests++;
					dayTrades++;
				}
			}
			
			if (hMode==1){
				if (q.getHigh5()>=tpValue){
					hMode=0;
					winPips += tp*10;
					lossesArr.add(actualLosses);
					/*if (debug==1){
						if (debugPrint){
							System.out.println("[FULL LOSS >="+cappedLosses+" ] "+actualLosses+" || "+countdebug
									+" || "+PrintUtils.Print2dec2(balance, true)
									+" || "+q1.toString()
									);
						}
						countdebug++;
					}*/
					actualLosses=0;
					wins++;
					
					balance = balance*(1+(riskWin*1.0)/100);
					if (balance>=maxBalance) maxBalance = balance;
					if (debug==1 && debugPrint){
						System.out.println("[WIN>="+cappedLosses+" ] "
								+" || "+PrintUtils.Print2dec2(balance, true)
								+" || "+q1.toString()
								);
					}
				}else if (q.getLow5()<=slValue){
					actualLosses++;
					if (actualLosses>=maxLosses) maxLosses = actualLosses;										
					lostPips += sl*10;
					entry = q.getClose5();
					tpValue = entry+tp*10;
					slValue = entry - (sl+1)*10;
					hMode = 1;
					//asumimos perdidas aqui
					if (actualLosses>=cappedLosses){
						//asumimos perdidas
						balance = balance*(1+(riskFullLoss*1.0)/100);
						double dd = 100.0-balance*100.0/maxBalance;
						if (dd>=maxDD) maxDD = dd;
						if (debug==1){
							if (debugPrint){
								System.out.println("[FULL LOSS >="+cappedLosses+" ] "+actualLosses+" || "+countdebug
										+" || "+PrintUtils.Print2dec2(balance, true)
										+" || "+q.toString()
										);
							}
							countdebug++;
							hMode = 0;
						}
						actualLosses = 0;						
					}
				}				
			}
			if (hMode==-1){
				if (q.getLow5()<=tpValue){
					hMode=0;
					winPips += tp*10;
					lossesArr.add(actualLosses);
					/*if (debug==1){
						if (actualLosses>=adebugLosses){
							if (debugPrint){
								System.out.println("[LOSS >="+adebugLosses+" ] "+actualLosses+" || "+q1.toString());
							}
							 countdebug++;
						}
					}*/
					actualLosses=0;
					wins++;
					
					balance = balance*(1+(riskWin*1.0)/100);
					if (balance>=maxBalance) maxBalance = balance;
					if (debug==1 && debugPrint){
						System.out.println("[WIN>="+cappedLosses+" ] "
								+" || "+PrintUtils.Print2dec2(balance, true)
								+" || "+q1.toString()
								);
					}
				}else if (q.getHigh5()>=slValue){
					actualLosses++;
					if (actualLosses>=maxLosses) maxLosses = actualLosses;					
					lostPips += sl*10;
					entry = q.getClose5();
					tpValue = entry - tp*10;
					slValue = entry + (sl-1)*10;
					hMode = -1;
					if (actualLosses>=cappedLosses){
						balance = balance*(1+riskFullLoss/100);
						double dd = 100.0-balance*100.0/maxBalance;
						if (dd>=maxDD) maxDD = dd;
						if (debug==1){
							if (debugPrint){
								System.out.println("[FULL LOSS >="+cappedLosses+" ] "+actualLosses+" || "+countdebug
										+" || "+PrintUtils.Print2dec2(balance, true)
										+" || "+q.toString()
										);
							}
							countdebug++;
							hMode = 0;
						}
						actualLosses = 0;
					}
				}				
			}
		
		}
		
		int total = 0;
		for (int i=0;i<=23;i++){
			total+= peaks1.get(i);
			//str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			//str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
		}
		
		String header="";
		String str1 ="";
		String str2="";
		String str3 ="";
		String str4="";
		String str5 = "";
		int acc1 = 0;
		int acc2 = 0;
		
		int globalTotal = 2*total;
		for (int i=0;i<=23;i++){
			header += PrintUtils.Print2Int(i, 3)+" ";
			str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
			acc1 += peaks1.get(i);
			acc2 += peaks2.get(i);
			str3 += PrintUtils.Print2dec(acc1*100.0/total, false, 3)+" ";
			str4 += PrintUtils.Print2dec(acc2*100.0/total, false, 3)+" ";
			
			int ht = peaks1.get(i)+peaks2.get(i);
			str5 += i+"="+PrintUtils.Print2dec(ht*100.0/globalTotal, false, 3)+" ";
		}
		
		double mean = MathUtils.average(lossesArr);
		double dt = Math.sqrt(MathUtils.variance(lossesArr));
		double maxEnter = 4*(mean+dt);
		int totallosses = totalTests-wins;
		double pf = Math.abs((wins*riskWin)/(totallosses*riskFullLoss));
		String sumStr = h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+cappedLosses
				+" || "
				+" "+totalTests
				+" "+totalDays
				+" "+PrintUtils.Print2dec(countDayTrades*1.0/totalDays, false, 3)
				+" "+wins
				+" "+maxLosses
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec(maxDD, false, 3)
				+" "+PrintUtils.Print2dec(pf, false, 3)
				+" || "
				+PrintUtils.Print2dec(maxEnter, false, 3)
				+" "+countdebug+" "+PrintUtils.Print2dec(totalTests*1.0/maxEnter, false, 3)
				+" || "
				
				;
		
		
		MathUtils.summary(sumStr, lossesArr);
		/*System.out.println(
				h1+" "+h2+" "+thr+" "+tp+" "+sl
				+" || "
				+" "+totalTests
				+" "+wins
				+" "+maxLosses
				);*/
		
		//System.out.println(header);
		//System.out.println(str3);
		//System.out.println(str4);
		//System.out.println(str5);
		
		/*int total = wins+losses;
		double per = wins*100.0/countTest;
		double avgL = (accLoss*0.1)/(countTest-wins);
		double pf = winPips*1.0/lostPips;
		int losses = countTest-wins;
		double avg = winPips
		double f = avgL*1.0/tp;
		double maxRisk = Math.pow(f, maxLosses);
		
		double totalRisk = TestRanges.calculateTotalRisk(minPips,avgL,maxLosses);
		System.out.println(
				hTest+" "+minDiff+" "+minPips+" "+thr
				+" || "+countTest
				+" "+PrintUtils.Print2dec(per, false, 3)
				//+" "+(countTest-wins)+" "+accLoss
				+" "+PrintUtils.Print2dec(avgL, false, 3)
				+" || "+PrintUtils.Print2dec(pf, false, 3)
				+" "+PrintUtils.Print2dec(avg, false, 3)
				+" || "+maxLosses
				+" "+PrintUtils.Print2dec(totalRisk, false, 3)
				);*/
	}
	
	public static void doStudyHoursPeakTPSLSystem(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int hTest,
			int tp,
			int sl,
			int thr,
			boolean isModeBars,
			int debug,
			boolean debugPrint,
			int cappedLosses,
			double riskWin,
			double riskFullLoss,
			int aSpread
			){
		
		double initialBalance = 10000;
		double balance = 10000;
		double maxBalance = 10000;
		double maxDD = 0;
		double riskAcc = 0;
		double actualRisk =riskWin; 
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
	
		int lastDay = -1;
		int hTested = 0;
		int countTest = 0;
		int wins = 0;
		int testPeak = 0;
		int entry = 0;
		int tpValue = 0;
		int slValue = 0;
		int referencePoint = 0;
		int accLoss = 0;
		int accWin = 0;
		int testPeaki = 0;
		int newsi=0;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;
		int hMode = 0;
		int totalTests = 0;
		int countdebug = 0;
		int countDayTrades = 0;
		int totalDays = 0;
		int dayTrades = 0;
		int spread = aSpread;
		ArrayList<Integer> lossesArr = new ArrayList<Integer>();
		ArrayList<Integer> peaks1 = new ArrayList<Integer>();
		ArrayList<Integer> peaks2 = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			peaks1.add(0);
			peaks2.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			//if (h<h1 || h> h2) continue;
			
			if (day!=lastDay){
				
					
				if (lastDay!=-1){
					countDayTrades +=dayTrades;
					totalDays++;
				}
				if (debug==1){
					//System.out.println("[NEW DAY] "+DateUtils.datePrint(cal));
				}
								
				lastDay = day;
				if (hMode==2) hMode = 0;
				dayTrades = 0;
			}
			
			int maxMin = maxMins.get(i-1);
						
			if (hMode==0 
					&& (h>=h1 && h<=h2)
					//&& (h==0 && min>=15 || h!=0)
					//&& h==hTest
					){
				
				if (maxMin<=-thr){
					entry = q.getOpen5();
					tpValue = entry+tp*10;
					slValue = entry - sl*10+spread*10;
					hMode = 1;
					totalTests++;
					dayTrades++;
				}else if (maxMin>=thr){
					entry = q.getOpen5();
					tpValue = entry-tp*10;
					slValue = entry + sl*10-spread*10;
					hMode = -1;
					totalTests++;
					dayTrades++;
				}
			}
			
			if (hMode==1){
				if (q.getHigh5()>=tpValue){
					hMode=0;
					winPips += tp*10;
					lossesArr.add(actualLosses);
					/*if (debug==1){
						if (debugPrint){
							System.out.println("[FULL LOSS >="+cappedLosses+" ] "+actualLosses+" || "+countdebug
									+" || "+PrintUtils.Print2dec2(balance, true)
									+" || "+q1.toString()
									);
						}
						countdebug++;
					}*/
					actualLosses=0;
					wins++;
					
					balance = balance*(1+(riskWin*1.0)/100);
					if (balance>=maxBalance) maxBalance = balance;
					if (debug==1 && debugPrint){
						System.out.println("[WIN>="+cappedLosses+" ] "
								+" || "+PrintUtils.Print2dec2(balance, true)
								+" || "+q1.toString()
								);
					}
				}else if (q.getLow5()<=slValue){
					actualLosses++;
					if (actualLosses>=maxLosses) maxLosses = actualLosses;										
					lostPips += sl*10;
					entry = q.getClose5();
					tpValue = entry+tp*10;
					slValue = entry - sl*10+spread*10;
					hMode = 1;
					//asumimos perdidas aqui
					if (actualLosses>=cappedLosses){
						//asumimos perdidas
						balance = balance*(1+(riskFullLoss*1.0)/100);
						double dd = 100.0-balance*100.0/maxBalance;
						if (dd>=maxDD) maxDD = dd;
						if (debug==1){
							if (debugPrint){
								System.out.println("[FULL LOSS >="+cappedLosses+" ] "+actualLosses+" || "+countdebug
										+" || "+PrintUtils.Print2dec2(balance, true)
										+" || "+q.toString()
										);
							}
							countdebug++;
							hMode = 0;
						}
						actualLosses = 0;						
					}
				}				
			}
			if (hMode==-1){
				if (q.getLow5()<=tpValue){
					hMode=0;
					winPips += tp*10;
					lossesArr.add(actualLosses);
					/*if (debug==1){
						if (actualLosses>=adebugLosses){
							if (debugPrint){
								System.out.println("[LOSS >="+adebugLosses+" ] "+actualLosses+" || "+q1.toString());
							}
							 countdebug++;
						}
					}*/
					actualLosses=0;
					wins++;
					
					balance = balance*(1+(riskWin*1.0)/100);
					if (balance>=maxBalance) maxBalance = balance;
					if (debug==1 && debugPrint){
						System.out.println("[WIN>="+cappedLosses+" ] "
								+" || "+PrintUtils.Print2dec2(balance, true)
								+" || "+q1.toString()
								);
					}
				}else if (q.getHigh5()>=slValue){
					actualLosses++;
					if (actualLosses>=maxLosses) maxLosses = actualLosses;					
					lostPips += sl*10;
					entry = q.getClose5();
					tpValue = entry - tp*10;
					slValue = entry + sl*10-spread*10;
					hMode = -1;
					if (actualLosses>=cappedLosses){
						balance = balance*(1+riskFullLoss/100);
						double dd = 100.0-balance*100.0/maxBalance;
						if (dd>=maxDD) maxDD = dd;
						if (debug==1){
							if (debugPrint){
								System.out.println("[FULL LOSS >="+cappedLosses+" ] "+actualLosses+" || "+countdebug
										+" || "+PrintUtils.Print2dec2(balance, true)
										+" || "+q.toString()
										);
							}
							countdebug++;
							hMode = 0;
						}
						actualLosses = 0;
					}
				}				
			}
		
		}
		
		int total = 0;
		for (int i=0;i<=23;i++){
			total+= peaks1.get(i);
			//str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			//str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
		}
		
		String header="";
		String str1 ="";
		String str2="";
		String str3 ="";
		String str4="";
		String str5 = "";
		int acc1 = 0;
		int acc2 = 0;
		
		int globalTotal = 2*total;
		for (int i=0;i<=23;i++){
			header += PrintUtils.Print2Int(i, 3)+" ";
			str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
			acc1 += peaks1.get(i);
			acc2 += peaks2.get(i);
			str3 += PrintUtils.Print2dec(acc1*100.0/total, false, 3)+" ";
			str4 += PrintUtils.Print2dec(acc2*100.0/total, false, 3)+" ";
			
			int ht = peaks1.get(i)+peaks2.get(i);
			str5 += i+"="+PrintUtils.Print2dec(ht*100.0/globalTotal, false, 3)+" ";
		}
		
		double mean = MathUtils.average(lossesArr);
		double dt = Math.sqrt(MathUtils.variance(lossesArr));
		double maxEnter = 4*(mean+dt);
		int totallosses = totalTests-wins;
		double pf = Math.abs((wins*riskWin)/(totallosses*riskFullLoss));
		double winPer = wins*100.0/totalTests;
		String sumStr = h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+cappedLosses
				+" || "
				+" "+PrintUtils.Print2dec(totalTests/maxLosses, false, 3)
				+" || "
				+" "+totalTests
				+" "+totalDays
				+" "+PrintUtils.Print2dec(countDayTrades*1.0/totalDays, false, 3)
				+" "+wins+" "+PrintUtils.Print2dec(winPer, false, 3)
				+" "+maxLosses
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec(maxDD, false, 3)
				+" "+PrintUtils.Print2dec(pf, false, 3)
				+" || "
				+PrintUtils.Print2dec(maxEnter, false, 3)
				+" "+countdebug+" "+PrintUtils.Print2dec(totalTests*1.0/maxEnter, false, 3)
				+" || "
				
				;
		
		
		MathUtils.summary(sumStr, lossesArr);
		/*System.out.println(
				h1+" "+h2+" "+thr+" "+tp+" "+sl
				+" || "
				+" "+totalTests
				+" "+wins
				+" "+maxLosses
				);*/
		
		//System.out.println(header);
		//System.out.println(str3);
		//System.out.println(str4);
		//System.out.println(str5);
		
		/*int total = wins+losses;
		double per = wins*100.0/countTest;
		double avgL = (accLoss*0.1)/(countTest-wins);
		double pf = winPips*1.0/lostPips;
		int losses = countTest-wins;
		double avg = winPips
		double f = avgL*1.0/tp;
		double maxRisk = Math.pow(f, maxLosses);
		
		double totalRisk = TestRanges.calculateTotalRisk(minPips,avgL,maxLosses);
		System.out.println(
				hTest+" "+minDiff+" "+minPips+" "+thr
				+" || "+countTest
				+" "+PrintUtils.Print2dec(per, false, 3)
				//+" "+(countTest-wins)+" "+accLoss
				+" "+PrintUtils.Print2dec(avgL, false, 3)
				+" || "+PrintUtils.Print2dec(pf, false, 3)
				+" "+PrintUtils.Print2dec(avg, false, 3)
				+" || "+maxLosses
				+" "+PrintUtils.Print2dec(totalRisk, false, 3)
				);*/
	}
	
	private static double calculateTotalRisk(int minPips, double avgL, int maxLosses) {
		// TODO Auto-generated method stub
		
		double accLoss = 0;
		double size = 1;
		double factor = avgL*10/minPips;
		for (int i=1;i<=maxLosses;i++){
			double loss = size*factor;
			accLoss += loss;
			size = accLoss;
		}
		return accLoss;
	}

	public static void doStudyHoursPeak2(ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int hTest,
			int minDiff,
			int minPips,
			int debug
			){
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
	
		int peakH = -1;
		int peakL = -1;
		
		int hourH = 0;
		int hourL = 0;
		int lastDay = -1;
		int hTested = 0;
		int countTest = 0;
		int wins = 0;
		int testPeak = 0;
		int testPeakValid = 0;
		int referencePoint = 0;
		int accLoss = 0;
		int accWin = 0;
		int testPeaki = 0;
		ArrayList<Integer> peaks1 = new ArrayList<Integer>();
		ArrayList<Integer> peaks2 = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			peaks1.add(0);
			peaks2.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (h<h1 || h> h2) continue;
			
			if (day!=lastDay){
				
					
				if (lastDay!=-1){
					if (hourH<=hourL){
						int count = peaks1.get(hourH);
						peaks1.set(hourH, count+1);											
						count = peaks2.get(hourL);
						peaks2.set(hourL, count+1);
					}else{
						int count = peaks1.get(hourL);
						peaks1.set(hourL, count+1);
						count = peaks2.get(hourH);
						peaks2.set(hourH, count+1);						
					}
					
					if (hTested==1){
						int pips =q1.getClose5()-testPeakValid;
						accLoss += pips;
						if (debug==1){
							System.out.println("[LOSS] "+pips+" || "+testPeakValid+" || "+q1.toString());
						}
						//System.out.println(pips+" || "+accLoss);
					}else  if (hTested==-1){
						int pips = testPeakValid-q1.getClose5();
						accLoss += pips;
						if (debug==1){
							System.out.println("[LOSS] "+pips+" || "+testPeakValid+" || "+q1.toString());
						}
						//System.out.println(pips+" || "+accLoss);
					}
				}
				if (debug==1){
					System.out.println("[NEW DAY] "+DateUtils.datePrint(cal));
				}
				
				peakH = -1;
				peakL = -1;				
				hourH = 0;
				hourL = 0;
				lastDay = day;
				hTested = 0;
			}
			
			
			if (peakH==-1 
					//|| q.getHigh5()>=peakH+minDiff
					|| q.getHigh5()>=peakH
					){	
				if (peakH!=-1
						&& q.getHigh5()>=peakH+minDiff
						){				
					if (hTest==h && hTested==0){
						hTested = 1;
						testPeak = peakH;
						testPeakValid = peakH+minDiff; //el anterior pico + minDiff pips, dónde se pondría el limit SELL
						countTest++;
						testPeaki = i;
						if (debug==1){
							System.out.println("[HIGH PEAKVALID TESTED] "+testPeakValid+" || "+q.toString());
						}
					}
				}
				peakH = q.getHigh5();//voy actualizando los picos
				hourH = h;

				if (debug==1){
					System.out.println("[HIGH UPDATED] "+peakH+" || "+q.toString());
				}
			}
			if (peakL==-1 
					//|| q.getLow5()<=peakL-minDiff
					|| q.getLow5()<=peakL 
					){
				if (peakL!=-1
						&& q.getLow5()<=peakL-minDiff
						){
					if (hTest==h && hTested==0){
						hTested = -1;
						testPeak = peakL;
						testPeakValid = peakL-minDiff; //el anterior pico - minDiff pips, dónde se pondría el limit BUY
						countTest++;
						testPeaki = i;
						if (debug==1){
							System.out.println("[LOW PEAKVALID TESTED] "+testPeakValid+" || "+q.toString());
						}
					}
				}
				peakL = q.getLow5();//voy actualizando los picos
				hourL = h;

				if (debug==1){
					System.out.println("[LOW UPDATED] "+peakL+" || "+q.toString());
				}
			}
			
			if (hTested==1 && 
					((q.getLow5()<=testPeakValid-minPips && i>testPeaki)
					 || (q.getClose5()<=testPeakValid-minPips))					
					){
				wins++;
				hTested = 2;
				accWin += minPips;
				
				if (debug==1){
					System.out.println("[HIGH PEAKVALID WIN!] "+testPeakValid+" || "+q.toString());
				}
				
				if (debug==2 && i==testPeaki){
					System.out.println("[HIGH PEAKVALID WIN! CONCURRED] "+testPeakValid+" || "+q.toString());
				}
			}
			if (hTested==-1 && 
					((q.getHigh5()>=testPeakValid+minPips && i>testPeaki)
							 || (q.getClose5()>=testPeakValid+minPips))	
					){
				wins++;
				hTested = 2;
				accWin += minPips;
				if (debug==1){
					System.out.println("[LOW PEAKVALID WIN!] "+testPeakValid+" || "+q.toString());
				}
				
				if (debug==2 && i==testPeaki){
					System.out.println("[ LOW PEAKVALID WIN! CONCURRED] "+testPeakValid+" || "+q.toString());
				}
			}
		}
		
		int total = 0;
		for (int i=0;i<=23;i++){
			total+= peaks1.get(i);
			//str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			//str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
		}
		
		String header="";
		String str1 ="";
		String str2="";
		String str3 ="";
		String str4="";
		String str5 = "";
		int acc1 = 0;
		int acc2 = 0;
		
		int globalTotal = 2*total;
		for (int i=0;i<=23;i++){
			header += PrintUtils.Print2Int(i, 3)+" ";
			str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
			acc1 += peaks1.get(i);
			acc2 += peaks2.get(i);
			str3 += PrintUtils.Print2dec(acc1*100.0/total, false, 3)+" ";
			str4 += PrintUtils.Print2dec(acc2*100.0/total, false, 3)+" ";
			
			int ht = peaks1.get(i)+peaks2.get(i);
			str5 += i+"="+PrintUtils.Print2dec(ht*100.0/globalTotal, false, 3)+" ";
		}
		
		
		//System.out.println(header);
		//System.out.println(str3);
		//System.out.println(str4);
		//System.out.println(str5);
		
		double per = wins*100.0/countTest;
		double per2 = (accLoss*0.1)/(countTest-wins);
		double pf = (accWin*1.0/accLoss);
		int losses = countTest-wins;
		double avg = (accWin-accLoss)*0.1/countTest;
		System.out.println(
				hTest+" "+minDiff+" "+minPips
				+" || "+countTest
				+" "+PrintUtils.Print2dec(per, false, 3)
				//+" "+(countTest-wins)+" "+accLoss
				+" "+PrintUtils.Print2dec(per2, false, 3)
				+" || "+PrintUtils.Print2dec(pf, false, 3)
				+" "+PrintUtils.Print2dec(avg, false, 3)
				);
	}
	
	
	public static void doStudyHoursPeak(ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2){
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
	
		int peakH = -1;
		int peakL = -1;
		
		int hourH = 0;
		int hourL = 0;
		int lastDay = -1;
		
		ArrayList<Integer> peaks1 = new ArrayList<Integer>();
		ArrayList<Integer> peaks2 = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			peaks1.add(0);
			peaks2.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (h<h1 || h> h2) continue;
			
			if (day!=lastDay){		
				
				if (lastDay!=-1){
					if (hourH<=hourL){
						int count = peaks1.get(hourH);
						peaks1.set(hourH, count+1);
						count = peaks2.get(hourL);
						peaks2.set(hourL, count+1);
					}else{
						int count = peaks1.get(hourL);
						peaks1.set(hourL, count+1);
						count = peaks2.get(hourH);
						peaks2.set(hourH, count+1);
					}
				}
				peakH = -1;
				peakL = -1;				
				hourH = 0;
				hourL = 0;
				lastDay = day;
			}
			
			
			if (peakH==-1 || q.getHigh5()>=peakH){
				peakH = q.getHigh5();
				hourH = h;
			}
			if (peakL==-1 || q.getLow5()<=peakL){
				peakL = q.getLow5();
				hourL = h;
			}
		}
		
		int total = 0;
		for (int i=0;i<=23;i++){
			total+= peaks1.get(i);
			//str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			//str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
		}
		
		String header="";
		String str1 ="";
		String str2="";
		String str3 ="";
		String str4="";
		String str5 = "";
		int acc1 = 0;
		int acc2 = 0;
		
		int globalTotal = 2*total;
		for (int i=0;i<=23;i++){
			header += PrintUtils.Print2Int(i, 3)+" ";
			str1 += PrintUtils.Print2Int(peaks1.get(i),3)+" ";
			str2 += PrintUtils.Print2Int(peaks2.get(i),3)+" ";
			acc1 += peaks1.get(i);
			acc2 += peaks2.get(i);
			str3 += PrintUtils.Print2dec(acc1*100.0/total, false, 3)+" ";
			str4 += PrintUtils.Print2dec(acc2*100.0/total, false, 3)+" ";
			
			int ht = peaks1.get(i)+peaks2.get(i);
			str5 += i+"="+PrintUtils.Print2dec(ht*100.0/globalTotal, false, 3)+" ";
		}
		
		
		System.out.println(header);
		System.out.println(str3);
		System.out.println(str4);
		System.out.println(str5);
	}
	
	public static void doTest(ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2){
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int maxH = -1;
		int minH = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int count0 = 0;
		int count10=0;
		int count20=0;
		int count30=0;
		int count40=0;
		int count50=0;
		int count60=0;
		int count70=0;
		int count80=0;
		int count90=0;
		int count100=0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (h<h1 || h> h2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					ranges.add(maxH-minH);
					int range = maxH-minH;
					if (range<=90) count0++;
					else if (range>=500) count100++;
					else if (range<=190) count10++;
					else if (range<=290) count20++;
					else if (range<=390) count30++;
					else if (range<=490) count40++;
					/*else if (range<=590) count50++;
					else if (range<=690) count60++;
					else if (range<=790) count70++;
					else if (range<=890) count80++;
					else if (range<=990) count90++;*/
					
					//System.out.println(DateUtils.datePrint(cal1)+" "+(maxH-minH));
				}
				
				maxH = -1;
				minH = -1;
				
				lastDay = day;
			}
			
			
			if (h>=h1 && h<=h2){							
				if (maxH==-1 || q.getHigh5()>=maxH){
					maxH = q.getHigh5();
				}
				if (minH==-1 || q.getLow5()<=minH){
					minH = q.getLow5();
				}
			}
			
			
		}
		
		String header = h1+" "+h2
					+" || "
					+" "+count0
					+" "+count10
					+" "+count20
					+" "+count30
					+" "+count40
					//+" "+count50
					//+" "+count60
					//+" "+count70
					//+" "+count80
					//+" "+count90
					+" "+count100
					;
		MathUtils.summary(header, ranges);
	}

	public static void main(String[] args) throws Exception {
		//String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
				String path0 ="C:\\fxdata\\";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
				String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.13.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.12.31_2017.10.26.csv";
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
					
					//TestRanges.doStudyHoursPeak(dataNoise, y1, y2, m1, m2, h1, h2);
					ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataNoise);
					for (int h1=0;h1<=0;h1++){
						int h2 = h1+0;
						for ( h2=h1+9;h2<=h1+9;h2++){
							for (int y1=2003;y1<=2003;y1++){
								int y2 = y1+14;
								//TestRanges.doStudyHoursPeak(dataNoise, y1, y2, 0, 11, h1, h2);
								//TestRanges.doTest(data, y1, y2, 0, 11, h1, h2);
								for (int minPips=30;minPips<=30;minPips+=5){
									for (int minDiff=(int) (0.5*minPips);minDiff<=0.5*minPips;minDiff+=0.5*minPips){
									//for (int minPips=1*minDiff;minPips<=1*minDiff;minPips+=1*minDiff){
										//TestRanges.doStudyHoursPeak2(dataNoise, y1, y2, 0, 11, 0, 23,h1,minDiff,minPips,0);
										for (int thr=0;thr<=5000;thr+=100){
											//TestRanges.doStudyHoursPeakMaxMin(dataNoise, maxMins,news, y1, y2, 0, 11, 0, 23,h1,minDiff,minPips,thr,0);
											//TestRanges.doStudyHoursPeakSystem(dataNoise, maxMins,news, y1, y2, 0, 11, 0, 23,h1,minPips,thr,0);
											//TestRanges.doStudyHoursPeakTPSL(dataNoise, maxMins,news, y1, y2, 0, 11, h1, h2,h1,minDiff,minPips,thr,true,9,1,false);
											for (int nperdidas=1;nperdidas<=1;nperdidas++){
												for (int aSpread=0;aSpread<=0;aSpread++){
													TestRanges.doStudyHoursPeakTPSLSystem(dataNoise, maxMins,news, y1, y2, 0, 11, h1, h2,h1,
															minDiff,minPips,thr,true,1,false,nperdidas,0.6,-76,aSpread);
													//TestRanges.doStudyHoursPeakTPSLComplete(dataNoise, maxMins,news, y1, y2, 0, 11, h1, h2,h1,
															//minDiff,minPips,thr,true,1,false,nperdidas,0.2,-0.6);
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
