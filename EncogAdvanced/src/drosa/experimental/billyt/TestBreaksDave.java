package drosa.experimental.billyt;

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

public class TestBreaksDave {
	
	public static void testPlantilla(String header,ArrayList<QuoteShort> data,
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
				
				if (lastDay!=-1){
					lastHigh = actualHigh;
					lastLow = actualLow;
				}
				
				actualHigh = -1;
				actualLow = -1;
				lastDay = -1;
			}
			
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow= q.getLow5();
			}
		}
	}
	
	
	public static void test(String header,ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int tp,int sl,	
			double testRange,
			int dayOffset,
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
		int countR = 0;
		int accPipsR = 0;
		
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
		int winsR = 0;
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
		int lastIdx = 0;
		boolean rangeTested = false;
		ArrayList<Integer> hours = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hours.add(0);
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					lastHigh = actualHigh;
					lastLow = actualLow;
				}				
				mode = 0;
				canTrade = 1;
				actualHigh = -1;
				actualLow = -1;
				lastIdx = TradingUtils.getLastDayQuote(data,i,cal,dayOffset);	
				rangeTested = false;
				lastDay = day;
			}
			
			
			if (mode==0
					&& canTrade==1
					&& h>=h1 && h<=h2
					){
				if (lastHigh!=-1 
						&& q.getOpen5()<lastHigh
						&& q.getHigh5()>=lastHigh){
					mode = 1;
					count++;
				}
				if (lastLow!=-1 
						&& q.getOpen5()>lastLow
						&& q.getLow5()<=lastLow){
					mode = -1;
					count++;
				}
			}
			
			if (mode==1){
				if (q.getHigh5()>=lastHigh+tp*10){
					wins++;
					canTrade=0;					
					int tot = hours.get(h);
					hours.set(h, tot+1);
					mode = 0;
				}else{	
					if (!rangeTested){
						int diff = lastHigh-q1.getOpen5();
						int lastRange = lastHigh-lastLow;
						
						double diffPer = diff*100.0/lastRange;
						if (diffPer>=testRange){											
							int pips = data.get(lastIdx).getClose5()-q1.getOpen5();
							accPipsR += pips;
							countR++;
							
							if (pips>=0){
								winsR++;
							}
							rangeTested = true;
						}
					}
				}
			}
			
			if (mode==-1){
				if (q.getLow5()<=lastLow-tp*10){
					wins++;
					canTrade=0;
					int tot = hours.get(h);
					hours.set(h, tot+1);
					mode = 0;
				}else{
					if (!rangeTested){
						int diff = q1.getOpen5()-lastLow;
						int lastRange = lastHigh-lastLow;
						
						double diffPer = diff*100.0/lastRange;	
						if (diffPer>=testRange){
							int pips = q1.getOpen5()-data.get(lastIdx).getClose5();
							accPipsR += pips;
							countR++;
							if (pips>=0){
								winsR++;
							}
							rangeTested = true;
						}
					}
				}
			}
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow= q.getLow5();
			}
		}
		
		
		double winPer = wins*100.0/count;
		double winRPer = winsR*100.0/countR;
		
		String str ="";
		for (int i=0;i<=23;i++){
			int tot = hours.get(i);
			str+=tot+" ";
		}
		
		double avgPipsRange = accPipsR*0.1/countR;
		System.out.println(
				tp+" "+PrintUtils.Print2dec(testRange, false)
				+" || "+count+" "+wins+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+str
				+" || "+countR+" "+" "+PrintUtils.Print2dec(winRPer, false)+" "+PrintUtils.Print2dec(avgPipsRange, false)
				);
	}
	
	public static void test2(String header,ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,int hClose,
			int tp,int sl,	
			int maxTrades,
			int topRetraced,
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
		int countR = 0;
		int accPipsR = 0;
		
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
		int winsR = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		int mode = 0;
		int entry = -1;
		int canTrade = 1;
		int lastDay = -1;
		int accRetraced = 0;
		int dayTrades = 0;
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		for (int i=0;i<=50;i++) streaks.add(0);
		int actualStreak = 0;
		int range = 20;
		int lastIdx = 0;
		boolean rangeTested = false;
		ArrayList<Integer> hours = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hours.add(0);
		dayTrades = 0;
		int maxRetraced = 0;
		int accWinRetraced = 0;
		int accLossRetraced = 0;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (day!=lastDay){
				
				if (lastDay!=-1){
					if (mode==1){
						int diff = lastHigh-q.getOpen5();
						accLossRetraced += diff;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [HIGH LOSS] "+diff);
						}
					}else if (mode==-1){
						int diff = q.getOpen5()-lastLow;
						accLossRetraced += diff;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [LOW LOSS] "+diff);
						}
					} 
					
					lastHigh = actualHigh;
					lastLow = actualLow;					
				}				
				mode = 0;
				canTrade = 1;
				actualHigh = -1;
				actualLow = -1;
				maxRetraced = 0;
				//lastIdx = TradingUtils.getLastDayQuote(data,i,cal,dayOffset);	
				rangeTested = false;
				dayTrades = 0;
				lastDay = day;
			}
			
			
			int diffH = q.getHigh5()-lastHigh;
			int diffL = lastLow-q.getLow5();
			
			//entradas
			if (mode==0
					&& dayTrades<maxTrades
					&& h>=h1 && h<=h2
					){
				if (q.getOpen5()<= lastHigh && diffH>=0){
					mode=1;					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [HIGH HIT] "+lastHigh);
					}
					dayTrades++;
					count++;
				}
				if (q.getOpen5()>= lastLow && diffL>=0){
					mode=-1;					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [LOW HIT] "+lastLow);
					}
					dayTrades++;
					count++;
				}
			}
			
			if (mode==1){
				if (diffH>=tp*10){
					mode=0;	
					wins++;
					accWinRetraced += maxRetraced;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [HIGH WIN] "+lastHigh);
					}
				}else{
					int diffHO = lastHigh-q.getOpen5();
					if (diffHO>=300){
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [HIGH RETRACED] "+diffHO);
						}
					}
					if (diffHO>=maxRetraced){
						accRetraced += diffHO;
						maxRetraced = diffHO;
					}
					
					if (diffHO>=topRetraced*10){
						accLossRetraced += topRetraced*10;
						mode=0;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [HIGH TOP RETRACED] "+(topRetraced*10)+" || "+accLossRetraced);
						}
					}else{
						if (h>=hClose){							
							accLossRetraced += (lastHigh-q.getClose5());
							if (debug==1){
								System.out.println(DateUtils.datePrint(cal)+" || [HIGH HOUR CLOSE] "+(lastHigh-q.getClose5())+" || "+accLossRetraced);
							}
							mode=0;
						}
					}
				}
			}
			
			if (mode==-1){
				if (diffL>=tp*10){
					mode=0;	
					wins++;
					accWinRetraced += maxRetraced;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [LOW WIN] "+lastHigh);
					}
				}else{
					int diffOL = q.getOpen5()-lastLow;
					if (diffOL>=300){
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [LOW RETRACED] "+diffOL);
						}
					}
					if (diffOL>=maxRetraced){
						accRetraced += diffOL;
						maxRetraced = diffOL;
					}
					
					if (diffOL>=topRetraced*10){
						accLossRetraced += topRetraced*10;
						mode=0;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [LOW TOP RETRACED] "+(topRetraced*10)+" || "+accLossRetraced);
						}
					}else{
						if (h>=hClose){							
							accLossRetraced += (q.getClose5()-lastLow);
							if (debug==1){
								System.out.println(DateUtils.datePrint(cal)+" || [LOW HOUR CLOSE] "+(q.getClose5()-lastLow)+" || "+accLossRetraced);
							}
							mode=0;
						}
					}
				}
			}
			
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow= q.getLow5();
			}
		}
		
		
		double winPer = wins*100.0/count;
		double winRPer = winsR*100.0/countR;
		
		String str ="";
		for (int i=0;i<=23;i++){
			int tot = hours.get(i);
			str+=tot+" ";
		}
		
		double avgPipsRange = accPipsR*0.1/countR;
		double avgRetraced = accRetraced*0.1/count; 
		
		losses = count-wins;
		double avgLossRetraced = accLossRetraced*0.1/losses;
		double pf = tp*winPer/((100.0-winPer)*avgLossRetraced);
		
		double avg =(tp*winPer-((100.0-winPer)*avgLossRetraced))/(100.0) ;
		System.out.println(
				tp+" "+topRetraced
				+" || "+count
				+" "+wins
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(accWinRetraced*0.1/count, false)
				+" "+PrintUtils.Print2dec(accRetraced*0.1/count, false)
				+" "+PrintUtils.Print2dec(accLossRetraced*0.1/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				);
	}
	
	public static void test3(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int hClose,
			int tp,int sl,	
			int maxTrades,
			int topRetraced,
			int thr,
			int offset,
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
		int countR = 0;
		int accPipsR = 0;
		
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
		int winsR = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		int mode = 0;
		int entry = -1;
		int canTrade = 1;
		int lastDay = -1;
		int accRetraced = 0;
		int dayTrades = 0;
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		for (int i=0;i<=50;i++) streaks.add(0);
		int actualStreak = 0;
		int range = 20;
		int lastIdx = 0;
		boolean rangeTested = false;
		ArrayList<Integer> hours = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hours.add(0);
		dayTrades = 0;
		int maxRetraced = 0;
		int accWinRetraced = 0;
		int accLossRetraced = 0;
		entry = 0;
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (day!=lastDay){
				
				if (lastDay!=-1){
					if (mode==1){
						int diff = entry-q.getOpen5();
						accLossRetraced += diff;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [HIGH LOSS] "+diff);
						}
					}else if (mode==-1){
						int diff = q.getOpen5()-entry;
						accLossRetraced += diff;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [LOW LOSS] "+diff);
						}
					} 
					
					lastHigh = actualHigh;
					lastLow = actualLow;					
				}				
				mode = 0;
				canTrade = 1;
				actualHigh = -1;
				actualLow = -1;
				maxRetraced = 0;
				//lastIdx = TradingUtils.getLastDayQuote(data,i,cal,dayOffset);	
				rangeTested = false;
				dayTrades = 0;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			
			//entradas
			if (mode==0
					&& dayTrades<maxTrades
					&& h>=h1 && h<=h2
					){
				if (true 
						&& maxMin>=thr
						&& q1.getClose5()<= q1.getOpen5()-offset
						//&& diffH>=0
						){
					mode=1;	
					entry = q.getOpen5();
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [HIGH HIT] "+lastHigh);
					}
					dayTrades++;
					count++;
				}
				if (true
						&& maxMin<=-thr
						&& q1.getClose5()>= q1.getOpen5()+offset
						//&& q.getOpen5()>= lastLow 
						//&& diffL>=0
						){
					mode=-1;	
					entry = q.getOpen5();
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [LOW HIT] "+lastLow);
					}
					dayTrades++;
					count++;
				}
			}
			
			int diffH = q.getHigh5()-entry;
			int diffL = entry-q.getLow5();
			
			if (mode==1){
				if (diffH>=tp*10){
					mode=0;	
					wins++;
					accWinRetraced += maxRetraced;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [HIGH WIN] "+entry);
					}
				}else{
					int diffHO = entry-q.getOpen5();
					if (diffHO>=300){
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [HIGH RETRACED] "+diffHO);
						}
					}
					if (diffHO>=maxRetraced){
						accRetraced += diffHO;
						maxRetraced = diffHO;
					}
					
					if (diffHO>=topRetraced*10){
						accLossRetraced += topRetraced*10;
						mode=0;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [HIGH TOP RETRACED] "+(topRetraced*10)+" || "+accLossRetraced);
						}
					}else{
						/*if (maxMin<=-thr){							
							accLossRetraced += (lastHigh-q.getClose5());
							if (debug==1){
								System.out.println(DateUtils.datePrint(cal)+" || [HIGH BAR CLOSE] "+(lastHigh-q.getClose5())+" || "+accLossRetraced);
							}
							mode=0;
						}*/
					}
				}
			}
			
			if (mode==-1){
				if (diffL>=tp*10){
					mode=0;	
					wins++;
					accWinRetraced += maxRetraced;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || [LOW WIN] "+entry);
					}
				}else{
					int diffOL = q.getOpen5()-entry;
					if (diffOL>=300){
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [LOW RETRACED] "+diffOL);
						}
					}
					if (diffOL>=maxRetraced){
						accRetraced += diffOL;
						maxRetraced = diffOL;
					}
					
					if (diffOL>=topRetraced*10){
						accLossRetraced += topRetraced*10;
						mode=0;
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal)+" || [LOW TOP RETRACED] "+(topRetraced*10)+" || "+accLossRetraced);
						}
					}else{
						/*if (maxMin>=thr){						
							accLossRetraced += (q.getClose5()-lastLow);
							if (debug==1){
								System.out.println(DateUtils.datePrint(cal)+" || [LOW BAR CLOSE] "+(q.getClose5()-lastLow)+" || "+accLossRetraced);
							}
							mode=0;
						}*/
					}
				}
			}
			
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow= q.getLow5();
			}
		}
		
		
		double winPer = wins*100.0/count;
		double winRPer = winsR*100.0/countR;
		
		String str ="";
		for (int i=0;i<=23;i++){
			int tot = hours.get(i);
			str+=tot+" ";
		}
		
		double avgPipsRange = accPipsR*0.1/countR;
		double avgRetraced = accRetraced*0.1/count; 
		
		losses = count-wins;
		double avgLossRetraced = accLossRetraced*0.1/losses;
		double pf = tp*winPer/((100.0-winPer)*avgLossRetraced);
		
		double avg =(tp*winPer-((100.0-winPer)*avgLossRetraced))/(100.0) ;
		System.out.println(
				tp+" "+topRetraced
				+" || "+count
				+" "+wins
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(accWinRetraced*0.1/count, false)
				+" "+PrintUtils.Print2dec(accRetraced*0.1/count, false)
				+" "+PrintUtils.Print2dec(accLossRetraced*0.1/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				);
	}
	
	
	public static void test4(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int hClose,
			int tp,int sl,	
			int maxTrades,
			int thr,
			int offset,
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
		
		for (int i=5;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (day!=lastDay){
				
				if (lastDay!=-1){					
					int diff = 0;
					if (mode==1){
						diff = q1.getClose5()-entry;
						if (debug==1)
							System.out.println("[LONG CLOSE] "+diff+" "+q.getOpen5()+" "+entry);
					}
					if (mode==-1){
						diff = entry-q1.getClose5();
						if (debug==1)
							System.out.println("[SHORT CLOSE] "+diff+" "+q.getOpen5()+" "+entry);
					}
					diff-=comm;
					if (mode!=0){
						if (diff>=0){
							wins++;
							winPips += diff;
						}else{
							losses++;
							lostPips+=-diff;
						}
						mode=0;
					}
					
					lastHigh = actualHigh;
					lastLow = actualLow;
				}	
				dayTrades= 0;
				mode =0;
				actualHigh = -1;
				actualLow = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			
			if (mode==0
				&& dayTrades<maxTrades	
				&& h>=h1 && h<=h2 && (h>0 || (h==0 && min>=15))
					){				
				if (true
						&& maxMin>=thr
						){
					mode = -1;
					entry = q.getOpen5();
					dayTrades++;
					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[SHORT] "+entry);
					}
				}
				if (true
						&& maxMin<=-thr
						){
					mode = 1;
					entry = q.getOpen5();
					dayTrades++;
					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[LONG] "+entry);
					}
				}
			}
			
			if (mode==-1){
				int diff = entry-q.getLow5();
				if (diff>=tp*10+comm){
					winPips += tp*10;
					wins++;
					mode=0;
					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[SHORT WIN] "+entry+" || "+q.toString());
					}
				}
			}
			
			if (mode==1){
				int diff = q.getHigh5()-entry;
				if (diff>=tp*10+comm){
					winPips += tp*10;
					wins++;
					mode=0;
					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[LONG WIN] "+entry+" || "+q.toString());
					}
				}
			}
			
			//HOUR CLOSE
			if (h==hClose){
				int diff=0;
				if (mode==1){
					diff = q1.getClose5()-entry;
					if (debug==1)
						System.out.println("[LONG CLOSE] "+diff+" "+q.getOpen5()+" "+entry);
				}
				if (mode==-1){
					diff = entry-q1.getClose5();
					if (debug==1)
						System.out.println("[SHORT CLOSE] "+diff+" "+q.getOpen5()+" "+entry);
				}
				if (mode!=0){
					if (diff>=0){
						wins++;
						winPips += diff;
					}else{
						losses++;
						lostPips+=-diff;
					}
					mode=0;
				}
			}
			
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow= q.getLow5();
			}
		}
		
		
		int total = wins+losses;
		double winPer = wins*100.0/total;

		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/total;
		
		System.out.println(
				tp+" "+thr+" "+h1+" "+h2+" "+hClose
				+" || "+total
				+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				);
	}
	
	public static double test5(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int maxTime,
			int tp,int sl,	
			int maxTrades,
			int thr,
			int offset,
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
		int iEntry = -1;
		int lastDay2 = -1;
		for (int i=5;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (day!=lastDay){				
				if (lastDay!=-1){															
					lastHigh = actualHigh;
					lastLow = actualLow;
				}	
				dayTrades= 0;
				actualHigh = -1;
				actualLow = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			int spread = q.getHigh5()-q.getLow5();
			
			if (spread>=1000 && day!=lastDay2 && h>0 && h<=5){
				System.out.println(DateUtils.datePrint(cal)+" || [BIG BAR] "+q.toString()+" || "+spread);
				lastDay2 = day;
			}
			
			if (mode==0
				&& dayTrades<maxTrades	
				&& h>=h1 && h<=h2 && (h>0 || (h==0 && min>=15))
					){				
				if (true
						&& maxMin>=thr
						){
					mode = -1;
					entry = q.getOpen5();
					dayTrades++;
					iEntry = i;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[SHORT] "+entry);
					}
				}
				if (true
						&& maxMin<=-thr
						){
					mode = 1;
					entry = q.getOpen5();
					dayTrades++;
					iEntry = i;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[LONG] "+entry);
					}
				}
			}
			
			if (mode==-1){
				int diff = entry-q.getLow5();
				int diffSL = q.getHigh5()-entry;
				
				if (diffSL>=sl*10){
					losses++;
					lostPips+=diffSL+comm;
					mode = 0;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[SHORT SL] "+entry+" || "+q.toString());
					}
				}
				if (diff>=tp*10+comm){
					winPips += tp*10;
					wins++;
					mode=0;
					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[SHORT WIN] "+entry+" || "+q.toString());
					}
				}
			}
			
			if (mode==1){
				int diff = q.getHigh5()-entry;
				int diffSL = entry-q.getLow5();
				
				if (diffSL>=sl*10){
					losses++;
					lostPips+=diffSL+comm;
					mode = 0;
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[LONG SL] "+entry+" || "+q.toString());
					}
				}else if (diff>=tp*10+comm){
					winPips += tp*10;
					wins++;
					mode=0;
					
					if (debug==1){
						System.out.println(DateUtils.datePrint(cal)+" || "+"[LONG WIN] "+entry+" || "+q.toString());
					}
				}
			}
			
			int span = i-iEntry;
			if (span>=maxTime){
				int diff=0;
				if (mode==1){
					diff = q1.getClose5()-entry;
					if (debug==1)
						System.out.println("[LONG CLOSE] "+diff+" "+q.getOpen5()+" "+entry);
				}
				if (mode==-1){
					diff = entry-q1.getClose5();
					if (debug==1)
						System.out.println("[SHORT CLOSE] "+diff+" "+q.getOpen5()+" "+entry);
				}
				if (mode!=0){
					diff-= comm;
					if (diff>=0){
						wins++;
						winPips += diff;
					}else{
						losses++;
						lostPips+=-diff;
					}
					mode=0;
				}
			}
			
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow= q.getLow5();
			}
		}
		
		
		int total = wins+losses;
		double winPer = wins*100.0/total;

		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/total;
		
		
		if (debug==3)
		System.out.println(
				tp+" "+sl+" "+thr+" "+h1+" "+h2+" "+maxTime
				+" || "+total
				+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				);
		
		return pf;
	}

	public static void main(String[] args) throws Exception {
		
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_5 Mins_Bid_2003.01.01_2017.03.24.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_5 Mins_Bid_2003.01.01_2017.03.24.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_5 Mins_Bid_2003.01.01_2017.03.24.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_5 Mins_Bid_2003.01.01_2017.03.24.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathGBPUSD);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		paths.add(pathEURJPY);paths.add(pathGBPJPY);

		
		int total = 2;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<QuoteShort> dailyData 		= null;
		limit = 0;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println(data.size());
			
			for (int h1=0;h1<=23;h1++){
				int h2 = h1;
				for (int tp=10;tp<=80;tp+=10){
					for (int sl=40;sl<=100;sl+=10){
						for (int thr=100;thr<=800;thr+=100){
							for (int maxTime=12;maxTime<=300;maxTime+=12){
								String header = h1+" || "+tp+" "+sl+" "+thr+" "+maxTime;
								int positives = 0;
								double avgPF = 0;
								int count = 0;
								String pfStr ="";
								for (int y1=2009;y1<=2016;y1++){
									int y2=y1;
									double pf = TestBreaksDave.test5("", data,maxMins, y1, y2, h1, h2,maxTime,tp,sl,1,thr,0,20,0);
									pfStr+=" "+PrintUtils.Print2dec(pf, false);
									if (pf>=1.01){
										positives++;
										avgPF += pf;
										
										count++;
									}
								}
								
								if (positives>=7){
									System.out.println(header+" || "+positives+" "+PrintUtils.Print2dec(avgPF/count, false)+" || "+pfStr);
								}
							}
						}
					}
				}
			}
		
			
		}

	}

}
