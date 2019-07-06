package drosa.experimental.billyt;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Test4HBreak {
	
	public static void test(String header,ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int tp,int sl,
			int nbars,int debug){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();
		
		ArrayList<QuoteShort> hoursHL= new ArrayList<QuoteShort>();
		
		ArrayList<Integer> ranges= new ArrayList<Integer>();
	
		int countB = 0;
		int accB = 0;
		int count = 0;
		int acc = 0;
		
		int last4h = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualHigh = -1;
		int actualLow = -1;
		int lastH = -1;
		QuoteShort qm = new QuoteShort();
		QuoteShort qm1 = new QuoteShort();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h!=lastH){				
				if (lastH!=-1){
					lastHigh = actualHigh;
					lastLow = actualLow;
					QuoteShort qH = new QuoteShort(); 
					qH.setHigh5(actualHigh);
					qH.setLow5(actualLow);
					hoursHL.add(qH);
					
					TradingUtils.getMaxMinShort(hoursHL, qm, cal_1, hoursHL.size()-nbars, hoursHL.size()-1);
					
					lastHigh = qm.getHigh5();
					lastLow = qm.getLow5();
					ranges.add(lastHigh-lastLow);
					
					if (debug==2){
						System.out.println("[new hour] "+DateUtils.datePrint(cal)+" || "+actualHigh+" "+actualLow);
					}
					
					
					if (h>=h1 && h<=h2){
						count++;
						acc += lastHigh-lastLow;
											
						//avg break
						TradingUtils.getMaxMinShort(hoursHL, qm1, cal_1, hoursHL.size()-1-nbars, hoursHL.size()-2);
						int diffH = actualHigh-qm1.getHigh5();
						int diffL = qm1.getLow5()-actualLow;
						
						if (hoursHL.size()>=nbars+1){
							if (diffH>=0){
								accB += diffH;
								countB++;
							}
							if (diffL>=0){
								accB += diffL;
								countB++;
							}
							if (debug==1 && (diffH>=0 || diffL>=0)){
								if (hoursHL.size()-1-nbars>=0)
									for (int j=hoursHL.size()-1-nbars;j<=hoursHL.size()-2;j++)
										System.out.println("[previous Hour] "+DateUtils.datePrint(cal)+" || "+hoursHL.get(j).getHigh5()+" "+hoursHL.get(j).getLow5());
								System.out.println("[BREAK] "+DateUtils.datePrint(cal)+" || "+qm1.getHigh5()+" "+qm1.getLow5()+" || "+actualHigh+" "+actualLow+" || "+diffH+" "+diffL );
							}
						}
					}
					
					
				}	
				actualHigh = -1;
				actualLow = -1;
				lastH = h;
			}
						
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow = q.getLow5();
			}
		}
		
		double avgRange = acc*0.1/count;
		double avgBreak = accB*0.1/countB;
		double factor = avgRange/avgBreak;
		System.out.println(
				y1+" "+y2+" "+h1+" "+h2
				+" "+nbars
				+" || "+PrintUtils.Print2dec(avgRange, false)
				+" || "+PrintUtils.Print2dec(accB*0.1/countB, false)
				+" || "+PrintUtils.Print2dec(factor, false)
				);
	}
	
	public static void test2(String header,ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int tp,int sl,
			int nbars,int debug){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();
		
		ArrayList<QuoteShort> hoursHL= new ArrayList<QuoteShort>();
		
		ArrayList<Integer> ranges= new ArrayList<Integer>();
	
		int countB = 0;
		int accB = 0;
		int count = 0;
		int acc = 0;
		
		int last4h = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualHigh = -1;
		int actualLow = -1;
		int lastH = -1;
		QuoteShort qm = new QuoteShort();
		QuoteShort qm0 = new QuoteShort();
		QuoteShort qm1 = new QuoteShort();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h!=lastH){				
				if (lastH!=-1){
					lastHigh = actualHigh;
					lastLow = actualLow;
					QuoteShort qH = new QuoteShort(); 
					qH.setHigh5(actualHigh);
					qH.setLow5(actualLow);
					hoursHL.add(qH);
					
					TradingUtils.getMaxMinShort(hoursHL, qm, cal_1, hoursHL.size()-nbars, hoursHL.size()-1);
					
					lastHigh = qm.getHigh5();
					lastLow = qm.getLow5();
					ranges.add(lastHigh-lastLow);
					
					if (debug==2){
						System.out.println("[new hour] "+DateUtils.datePrint(cal)+" || "+actualHigh+" "+actualLow);
					}
					
					
					if (h>=h1 && h<=h2){
						count++;
						acc += lastHigh-lastLow;
						
						if (hoursHL.size()>=8){
							int begin1	= hoursHL.size()-1-nbars;
							int end1 	= hoursHL.size()-1;
							int begin0 	= begin1-nbars;
							int end0 	= begin1-1;
							
							
							TradingUtils.getMaxMinShort(hoursHL, qm0, cal_1, begin0, end0);
							TradingUtils.getMaxMinShort(hoursHL, qm1, cal_1, begin1, end1);
							int diffH = qm1.getHigh5()-qm0.getHigh5();
							int diffL = qm0.getLow5()-qm1.getLow5();
							if (diffH>=0){
								accB += diffH;
								countB++;
							}
							if (diffL>=0){
								accB += diffL;
								countB++;
							}
							if (debug==1 && (diffH>=0 || diffL>=0)){
								if (hoursHL.size()-1-nbars>=0)
									for (int j=hoursHL.size()-1-nbars;j<=hoursHL.size()-2;j++)
										System.out.println("[previous Hour] "+DateUtils.datePrint(cal)+" || "+hoursHL.get(j).getHigh5()+" "+hoursHL.get(j).getLow5());
								System.out.println("[BREAK] "+DateUtils.datePrint(cal)+" || "+qm1.getHigh5()+" "+qm1.getLow5()+" || "+actualHigh+" "+actualLow+" || "+diffH+" "+diffL );
							}
							
						}
											
						//avg break
						/*TradingUtils.getMaxMinShort(hoursHL, qm1, cal_1, hoursHL.size()-1-nbars, hoursHL.size()-2);
						int diffH = actualHigh-qm1.getHigh5();
						int diffL = qm1.getLow5()-actualLow;
						
						if (hoursHL.size()>=nbars+1){
							if (diffH>=0){
								accB += diffH;
								countB++;
							}
							if (diffL>=0){
								accB += diffL;
								countB++;
							}
							if (debug==1 && (diffH>=0 || diffL>=0)){
								if (hoursHL.size()-1-nbars>=0)
									for (int j=hoursHL.size()-1-nbars;j<=hoursHL.size()-2;j++)
										System.out.println("[previous Hour] "+DateUtils.datePrint(cal)+" || "+hoursHL.get(j).getHigh5()+" "+hoursHL.get(j).getLow5());
								System.out.println("[BREAK] "+DateUtils.datePrint(cal)+" || "+qm1.getHigh5()+" "+qm1.getLow5()+" || "+actualHigh+" "+actualLow+" || "+diffH+" "+diffL );
							}
						}*/
					}
					
					
				}	
				actualHigh = -1;
				actualLow = -1;
				lastH = h;
			}
						
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow = q.getLow5();
			}
		}
		
		double avgRange = acc*0.1/count;
		double avgBreak = accB*0.1/countB;
		double factor = avgRange/avgBreak;
		
		int h10 = h1-nbars;
		int h11 = h1-1;
		int h00 = h10-nbars;
		int h01 = h10-1;
		System.out.println(
				y1+" "+y2+" "+h00+"-"+h01+" "+h10+"-"+h11
				+" || "
				+" "+nbars
				+" || "+PrintUtils.Print2dec(avgRange, false)
				+" || "+PrintUtils.Print2dec(accB*0.1/countB, false)
				+" || "+PrintUtils.Print2dec(factor, false)
				);
	}
	
	public static void test3(String header,ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int tp,int sl,
			int hTrading,
			int nbars,
			int minBar,
			int maxStrikes,
			int comm,
			int debug){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();
		
		ArrayList<QuoteShort> hoursHL= new ArrayList<QuoteShort>();
		
		ArrayList<Integer> ranges= new ArrayList<Integer>();
	
		int countB = 0;
		int accB = 0;
		int count = 0;
		int acc = 0;
		
		int last4h = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualHigh = -1;
		int actualLow = -1;
		int lastH = -1;
		QuoteShort qm = new QuoteShort();
		QuoteShort qm0 = new QuoteShort();
		QuoteShort qm1 = new QuoteShort();
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		int mode = 0;
		int entry = -1;
		int canTrade = 1;
		int lastDay = -1;
		
		int dayTrades = 0;
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		for (int i=0;i<=50;i++) streaks.add(0);
		int actualStreak = 0;
		int range = 20;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				//cerramos operaciones pendientes
					/*int pips = 0;
					if (mode==1){
						pips = q.getOpen5()-entry;
					}else if (mode==-1){
						pips = entry - q.getOpen5();						
					}
					
					pips-=comm*10;
					
					if (mode!=0){
						if (pips>=0){
							wins++;
							winPips += pips;
							if (debug==1){
								System.out.println("[CLOSE WIN] "+DateUtils.datePrint(cal)+" || "+(pips)+" || "+winPips+" "+lostPips+" || "+actualStreak);
							}
							if (actualStreak<0){
								int tot = streaks.get(-actualStreak);
								streaks.set(-actualStreak, tot+1);								
								actualStreak = 1;
							}else{
								actualStreak++;
							}
						}else{
							losses++;
							lostPips += -pips;
							if (debug==1){
								System.out.println("[CLOSE LOST] "+DateUtils.datePrint(cal)+" || "+(pips)+" || "+winPips+" "+lostPips+" || "+actualStreak);
							}
							if (actualStreak<0){
								actualStreak--;
							}else{
								actualStreak = -1;
							}
																												
						}												
					}*/
				if (mode==0){
					dayTrades = 0;
					mode = 0;
					canTrade=0;
				}
				lastDay = day;
			}
			
			if (h!=lastH){				
				if (lastH!=-1){
					//lastHigh = actualHigh;
					//lastLow = actualLow;
					QuoteShort qH = new QuoteShort(); 
					qH.setHigh5(actualHigh);
					qH.setLow5(actualLow);
					hoursHL.add(qH);
					
					if (h==hTrading 
							&& mode==0 //hasta que mode no sea 0 no cambiamos los topes
							){
						if (hoursHL.size()>=nbars){
							TradingUtils.getMaxMinShort(hoursHL, qm, cal_1, hoursHL.size()-nbars, hoursHL.size()-1);
							
							lastHigh = qm.getHigh5();
							lastLow = qm.getLow5();
							ranges.add(lastHigh-lastLow);
							
							range = lastHigh-lastLow;
							if (debug==3){
								System.out.println("[NEW BAR TEST] "+DateUtils.datePrint(cal)+" || "+lastHigh+" "+lastLow+" || "+(lastHigh-lastLow));
							}	
						}
					}
					
					if (debug==2){
						System.out.println("[new hour] "+DateUtils.datePrint(cal)+" || "+actualHigh+" "+actualLow);
					}										
				}						
				actualHigh = -1;
				actualLow = -1;
				lastH = h;
			}
			
			
			//en horario de trading se accede
			if (h>=h1 && h1<=h2 && range>=minBar*10){
				if (canTrade==0) canTrade=1;
			}
			
			int diffH = q.getHigh5()-lastHigh;
			int diffL = lastLow-q.getLow5();
			
			if (mode==0
					&& canTrade==1
					&& dayTrades<maxStrikes
					){
				if (lastHigh!=1 && diffH>=0 && q.getOpen5()<=lastHigh){
					entry = lastHigh;
					mode=1;
					dayTrades++;
					if (debug==3){
						System.out.println("[HIGH TEST] "+DateUtils.datePrint(cal)+" || "+entry);
					}
				}			
				if (lastLow!=1 && diffL>=0 && q.getOpen5()>=lastLow){
					entry = lastLow;
					mode=-1;
					dayTrades++;
					if (debug==3){
						System.out.println("[LOW TEST] "+DateUtils.datePrint(cal)+" || "+entry);
					}
				}
			}
			
			if (mode==1){
				int diffTP = q.getHigh5()-entry;

				if (diffTP>=(tp+comm)*10){				
					wins++;
					winPips += tp*10;
					mode = 0;
					canTrade = 2;//ya terminado, no mas trades hoy
					
					int nStreak = 0;
					if (actualStreak<0){
						int tot = streaks.get(-actualStreak);
						streaks.set(-actualStreak, tot+1);
						nStreak = tot+1;
						actualStreak = 1;
					}else{
						actualStreak++;
					}
					if (debug==1){
						System.out.println("[HIGH WIN] "+DateUtils.datePrint(cal)+" || "+entry+" "+tp*10+" || "+winPips+" "+lostPips+" || "+actualStreak+" "+nStreak);
					}
				}else{
					if (lastLow!=1 && diffL>=0 && q.getOpen5()>=lastLow ){
						int pips = entry-lastLow;
						lostPips += pips+comm*10;
						losses++;
						
						if (actualStreak<=0){
							actualStreak--;
						}else if (actualStreak>0){
							actualStreak = -1;
						}
						
						mode = 0;
						
						if (debug==1){
							System.out.println("[HIGH LOST] "+DateUtils.datePrint(cal)+" || "+entry+" "+(-pips)+" || "+winPips+" "+lostPips+" || "+actualStreak);

						}
						if (dayTrades<maxStrikes){
							entry = lastLow;
							mode=-1;
							dayTrades++;
							
							
							if (debug==3){
								System.out.println("[LOW TEST] "+DateUtils.datePrint(cal)+" || "+entry);
							}
							
														
							//caso de que en la misma barra que se intercambia se llegue al TP
							diffTP = entry-q.getLow5();						
							if (diffTP>=(tp+comm)*10){	
								wins++;
								winPips += tp*10;
								mode = 0;
								canTrade = 2;//ya terminado, no mas trades hoy
								if (debug==1){
									System.out.println("[LOW WIN] "+DateUtils.datePrint(cal)+" || "+entry+" "+tp*10+" || "+winPips+" "+lostPips+" || "+actualStreak+" "+streaks.get(-actualStreak));
								}
								
								if (actualStreak<0){
									int tot = streaks.get(-actualStreak);
									streaks.set(-actualStreak, tot+1);
									
									actualStreak = 1;
								}else{
									actualStreak++;
								}
							}
						}
					}
				}
			}
			
			if (mode==-1){
				int diffTP = entry-q.getLow5();
				
				if (diffTP>=(tp+comm)*10){	
					wins++;
					winPips += tp*10;
					mode = 0;
					canTrade = 2;//ya terminado, no mas trades hoy
					int aStreak = actualStreak;
					int nStreak = 0;
					
					
					if (actualStreak<0){
						int tot = streaks.get(-actualStreak);
						streaks.set(-actualStreak, tot+1);
						nStreak  = tot+1;
						actualStreak = 1;
					}else{
						actualStreak++;
					}
					
					if (debug==1){
						System.out.println("[LOW WIN] "+DateUtils.datePrint(cal)+" || "+entry+" "+tp*10+" || "+winPips+" "+lostPips+" || "+actualStreak+" "+nStreak);
					}
				}else{
					if (lastHigh!=1 && diffH>=0 && q.getOpen5()<=lastHigh){
						losses++;
						int pips = lastHigh-entry;
						lostPips += pips+comm*10;
						
						if (actualStreak<=0){
							actualStreak--;
						}else if (actualStreak>0){
							actualStreak = -1;
						}
						
						mode = 0;
						
						if (debug==1){
							System.out.println("[LOW LOST] "+DateUtils.datePrint(cal)+" || "+entry+" "+(-pips)+" || "+winPips+" "+lostPips+" || "+actualStreak);
						}
						
						if (dayTrades<maxStrikes){
							entry = lastHigh;
							mode=1;
							dayTrades++;
							
							if (debug==3){
								System.out.println("[HIGH TEST] "+DateUtils.datePrint(cal)+" || "+entry);
							}
							
							
							
							//caso de que en la misma barra que se intercambia se llegue al TP
							diffTP = q.getHigh5()-entry;
							if (diffTP>=(tp+comm)*10){				
								wins++;
								winPips += tp*10;
								mode = 0;
								canTrade = 2;//ya terminado, no mas trades hoy
								
								
								if (debug==1){
									System.out.println("[HIGH WIN] "+DateUtils.datePrint(cal)+" || "+entry+" "+tp*10+" || "+winPips+" "+lostPips+" || "+actualStreak+" "+streaks.get(-actualStreak));
								}
								
								if (actualStreak<0){
									int tot = streaks.get(-actualStreak);
									streaks.set(-actualStreak, tot+1);
									
									actualStreak = 1;
								}else{
									actualStreak++;
								}	
								
								
							}
						}
					}	
										
				}
			}
						
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow = q.getLow5();
			}
		}
		
		double avgRange = acc*0.1/count;
		double avgBreak = accB*0.1/countB;
		double factor = avgRange/avgBreak;
		
		int h10 = h1-nbars;
		int h11 = h1-1;
		int h00 = h10-nbars;
		int h01 = h10-1;
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		
		String streakStr="";
		int acc1 = 0;
		int acc2 = 0;
		for (int i=1;i<=30;i++){
			streakStr += streaks.get(i)+" ";
			if (i>=1) acc1 += streaks.get(i);
			if (i>=2) acc2 += streaks.get(i);
		}
		
		//2 intento ratio
		double per2 = 100.0-acc2*100.0/acc1;
		double pf2 = per2*(winPips*0.1/wins)/((100.0-per2)*(lostPips*0.1/losses));
		
		System.out.println(
				y1+" "+y2+" "+hTrading
				+" "+nbars+" "+minBar+" "+tp
				+"  ||  "+PrintUtils.Print2dec(avgRange, false)
				+" || "
				+" "+total+" "+wins+" "+losses
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				+" || "
				+" "+PrintUtils.Print2dec(per2, false)
				+" "+PrintUtils.Print2dec(pf2, false)
				+" || "+streakStr
				//+" || "+PrintUtils.Print2dec(avgRange, false)
				//+" || "+PrintUtils.Print2dec(accB*0.1/countB, false)
				//+" || "+PrintUtils.Print2dec(factor, false)
				);
	}

	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.12.15.csv";
				String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2017.03.23.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2015_10_28_2015_01_04.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2015_04_21.csv";
				String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
				//String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_1 Min_Bid_2003.05.04_2015.12.17.csv";
				String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2008.12.31_2017.03.23.csv";
				//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.12.15.csv";		
				//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2015.12.08.csv";
				String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2017.03.23.csv";
				String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
				String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2017.03.20.csv";
				
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(pathEURUSD);paths.add(pathGBPUSD);
				paths.add(pathUSDJPY);paths.add(pathAUDUSD);
				paths.add(pathEURJPY);paths.add(pathGBPJPY);

				
				int total = 2;
				ArrayList<Double> pfs = new ArrayList<Double>();
				int limit = paths.size()-1;
				limit = 0;
				String provider ="";
				Sizeof.runGC ();
				ArrayList<QuoteShort> dataI 		= null;
				ArrayList<QuoteShort> dataS 		= null;
				ArrayList<QuoteShort> dailyData 		= null;
				for (int i = 0;i<=limit;i++){
					String path = paths.get(i);			
					dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
					//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
					TestLines.calculateCalendarAdjustedSinside(dataI);
					//TradingUtils.cleanWeekendDataSinside(dataI); 	
					dataS = TradingUtils.cleanWeekendDataS(dataI);  
					ArrayList<QuoteShort> data = null;
					data = dataS;
					System.out.println(data.size());
					
					for (int y1=2009;y1<=2009;y1++){
						int y2 = y1+8;
						for (int ht=0;ht<=22;ht++){//ultima hora frontea
							for (int h1=ht+1;h1<=ht+1;h1++){
								int h2 = 20;
								for (int nbars=1;nbars<=1;nbars+=1){
									for (int tp=40;tp<=40;tp++){
										for (int minBar=20;minBar<=20;minBar++){
											for (int maxStrikes=2;maxStrikes<=2;maxStrikes++){
												Test4HBreak.test3("", data, y1, y2, h1, h2, tp, 0,ht, nbars,minBar,maxStrikes,2, 0);
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
