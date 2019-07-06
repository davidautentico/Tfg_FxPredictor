package drosa.experimental.billyt;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestBreaksNov17 {
	
	
	public static double calculateLosses(int maxLosses,double factor){
		
		double res = 0;
		double size = 1.0;
		double accLoss = 0;
		for (int i=0;i<maxLosses;i++){
			if (i==0) size = 1;
			else{
				size = accLoss;
			}
			accLoss += factor*(size+1); 
		}
		
		return accLoss;
	}
	
	public static void doTest(ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int minPips,
			int maxAdverse,
			int maxTests,
			int debug
			){
		
		int lastDay = -1;
		int min = -1;
		int max = -1;
		int lastMin = -1;
		int lastMax = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();
		double avgRange = 0;
		int totalDays = 0;
		int numTests = 0;
		int wins = 0;
		int losses = 0;
		boolean highTested = false;
		boolean lowTested = false;
		boolean canTestHigh = true;
		boolean canTestLow = true;
		int mode = 0;
		ArrayList<Integer> lossesArray = new ArrayList<Integer>();
		for (int i=0;i<=400;i++) lossesArray.add(0);
		int actualLosses = 0;
		int maxLoss = 0;
		int lastEntry = 0;
		ArrayList<Integer> triesTotal = new ArrayList<Integer>();
		ArrayList<Integer> triesWins = new ArrayList<Integer>();
		for (int i=0;i<=20;i++){
			triesTotal.add(0);
			triesWins.add(0);
		}
		int tp = minPips;
		int sl = maxAdverse;
		int winPips = 0;
		int lostPips = 0;
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q_1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal_1, q_1);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (day!=lastDay){
			
				if (lastDay!=-1){										
					lastMin  = min;
					lastMax = max;
				}
				
				min = -1;
				max = -1;
				canTestHigh	= true;
				canTestLow 	= true;
				numTests=0;
				if (mode==2) mode = 0;
				lastDay = day;
				
				if (debug==1){
					System.out.println("[DAY] "+lastMin+" "+lastMax+" || "+q.toString());
				}
			}
			
			if (max!=-1 && min!=-1
					&& (canTestHigh || canTestLow)
					&& mode == 0
					&& (h>=h1 && h<=h2)
					&& numTests<maxTests
					){
				if (canTestHigh){
					if (q.getHigh5()>=lastMax && q.getOpen5()<lastMax){
						mode = -1;
						lastEntry = lastMax;
						
						tp = minPips;
						sl = maxAdverse;
						/*if (numTests==0){
							mode = 1;
							tp = maxAdverse;
							sl = minPips;
						}*/
						
						if (debug==1){
							System.out.println("[TEST HIGH] "+lastMax+" || "+q.toString()+" || "+tp+" "+sl);
						}
					}
				}else if (canTestLow){
					if (q.getLow5()<=lastMin && q.getOpen5()>lastMin){
						mode = 1;
						lastEntry = lastMin;
						
						tp = minPips;
						sl = maxAdverse;
						/*if (numTests==0){
							mode = -1;
							tp = maxAdverse;
							sl = minPips;
						}*/
						
						if (debug==1){
							System.out.println("[TEST LOW] "+lastMin+" || "+q.toString()+" || "+tp+" "+sl);
						}
					}
				}
			}
			
			if (numTests<maxTests){
				if (mode==1){
					int diff = q1.getHigh5()-lastEntry;
					int diffa = lastEntry-q1.getLow5();
					if (diffa>=sl){
						losses++;
						canTestHigh = false;
						canTestLow = true;
						numTests++;
						mode = 0;
						if (debug==1){
							System.out.println("[TEST HIGH SL] "+lastMax+" "+diffa+" || "+q.toString()+" || SL="+sl);
						}
						actualLosses++;	
						
						
						int countTries = triesTotal.get(numTests);
						triesTotal.set(numTests, countTries+1);
						
						lostPips += sl;
					}else if (diff>=tp){
						wins++;
						numTests++;
						mode = 2;
						//prueba
						mode = 0;
						canTestHigh = true;
						canTestLow = true;
						if (debug==1){
							System.out.println("[TEST HIGH TP] "+lastMax+" || "+q.toString()+" ||| L="+actualLosses);
						}
						
						int count = lossesArray.get(actualLosses);
						lossesArray.set(actualLosses, count+1);
						if (actualLosses>=maxLoss) maxLoss = actualLosses;
						actualLosses = 0;	
						
						int countTries = triesTotal.get(numTests);
						int countWins = triesWins.get(numTests);
						triesTotal.set(numTests, countTries+1);
						triesWins.set(numTests, countWins+1);
						
						winPips += tp;
					}
				}
				
				if (mode==-1){
					int diff = lastEntry-q1.getLow5();
					int diffa = q1.getHigh5()-lastEntry;
					if (diffa>=sl){
						losses++;
						canTestHigh = true;
						canTestLow = false;
						numTests++;
						mode = 0;
						if (debug==1){
							System.out.println("[TEST LOW SL] "+lastMin+" "+diffa+" || "+q.toString()+" || SL="+sl);
						}
						actualLosses++;
						
						int countTries = triesTotal.get(numTests);
						triesTotal.set(numTests, countTries+1);
						
						lostPips += sl;
					}else if (diff>=tp){
						wins++;
						numTests++;
						mode = 2;
						//prueba
						mode = 0;
						canTestHigh = true;
						canTestLow = true;
						if (debug==1){
							System.out.println("[TEST LOW TP] "+lastMin+" || "+q.toString()+" ||| L="+actualLosses);
						}
						int count = lossesArray.get(actualLosses);
						lossesArray.set(actualLosses, count+1);
						if (actualLosses>=maxLoss) maxLoss = actualLosses;
						actualLosses = 0; 
						
						int countTries = triesTotal.get(numTests);
						int countWins = triesWins.get(numTests);
						triesTotal.set(numTests, countTries+1);
						triesWins.set(numTests, countWins+1);
						
						winPips += tp;
					}
				}
			}
						
			if (max==-1 || q.getHigh5()>=max){
				max = q.getHigh5();
			}
			if (min==-1 || q.getLow5()<=min){
				min = q.getLow5();
			}
		}
		
		
		
		int total = wins + losses;
		
		double winPer = wins*100.0/total;
		
		String str="";
		for (int i=0;i<=maxLoss+1;i++){
			str+=" "+lossesArray.get(i);
		}
		
		//double lf = (maxLoss+1)*(maxAdverse*1.0/minPips);
		double f = (maxAdverse*1.0/minPips);
		double lf = TestBreaksNov17.calculateLosses(maxLoss+1, f);
		double pf = winPips*1.0/lostPips;
		double avg = ((winPips)-(lostPips))*0.1/total;
		
		double perTries1 = triesWins.get(1)*100.0/triesTotal.get(1);
		double perTries2 = triesWins.get(2)*100.0/triesTotal.get(2);
		double perTries3 = triesWins.get(3)*100.0/triesTotal.get(3);
		double perTries4 = triesWins.get(4)*100.0/triesTotal.get(4);
		System.out.println(
				minPips
				+" "+maxAdverse
				+" "+h1+" "+h2
				+" || "
				+" "+total+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				//+" "+winPips+" "+lostPips
				+" || "+PrintUtils.Print2dec(pf, false)+" "+PrintUtils.Print2dec(avg, false)
				+" || "
				//+" "+PrintUtils.Print2dec(lf, false)
				//+" || "+str
				+" ||| "+PrintUtils.Print2dec(perTries1, false)
				+" "+PrintUtils.Print2dec(perTries2, false)
				+" "+PrintUtils.Print2dec(perTries3, false)
				+" "+PrintUtils.Print2dec(perTries4, false)
				);
	}

	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.12.15.csv";
				String pathEURUSD = "C:\\fxdata\\audUSD_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2015_10_28_2015_01_04.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2015_04_21.csv";
				String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
				//String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_1 Min_Bid_2003.05.04_2015.12.17.csv";
				String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
				//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.12.15.csv";		
				//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2015.12.08.csv";
				String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
				String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
				String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
				
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(pathEURUSD);paths.add(pathGBPUSD);
				paths.add(pathUSDJPY);paths.add(pathAUDUSD);
				paths.add(pathEURJPY);paths.add(pathGBPJPY);

				
				ArrayList<QuoteShort> dataI 		= null;
				ArrayList<QuoteShort> dataS 		= null;
				int limit = paths.size()-1;
				limit = 0;
				for (int i = 0;i<=limit;i++){
					Sizeof.runGC ();
					String path = paths.get(i);	
					dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
					//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
					TestLines.calculateCalendarAdjustedSinside(dataI);
					//TradingUtils.cleanWeekendDataSinside(dataI); 	
					dataS = TradingUtils.cleanWeekendDataS(dataI);  
					//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
					ArrayList<QuoteShort> data = null;
					//dataI.clear();
					//dataS.clear();
					//data5m.clear();
					data = dataS;
					System.out.println(data.size());
					int h1= 0;
					int h2=23;
					for (int tp=200;tp<=200;tp+=10){
						for (int sl=3*tp;sl<=3*tp;sl+=1*tp){
							for (h1=0;h1<=23;h1++){
								 h2 = h1+0;
								for (int tries=1;tries<=1;tries++){
									TestBreaksNov17.doTest(data, 2003, 2017,h1,h2,tp, sl, tries, 0);
								}
							}
						}
					}
				}

	}

}
