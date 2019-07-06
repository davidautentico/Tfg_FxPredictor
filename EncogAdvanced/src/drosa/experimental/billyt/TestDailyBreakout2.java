package drosa.experimental.billyt;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestDailyBreakout2 {

	public static void testBreak(String header,ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int tp,int sl,
			int maxTrades,
			int maxTime,
			int maxCloseDiff,
			int factor,
			int hClose,
			boolean isSameDirection,
			boolean tradeBreak,
			boolean debug
			){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int actualHigh = -1;
		int actualLow = -1;
		int lastDayHigh = -1;
		int lastDayLow = -1;
		int lastDayHigh1 = -1;
		int lastDayLow1 = -1;
		int lastDay = -1;
		int trade = 0;
		int tpValue = -1;
		int slValue = -1;
		int totalDayTrades = 0;
		boolean highBreak = false;
		boolean lowBreak = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int directionAllowed = 0;
		int indecisionTrades = 0;
		int slindecisionTrades = 0;
		int openIndex = -1;
		int entryValue = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();
		double avgRange = 0;
		int totalDays = 0;
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
				avgRange += actualHigh-actualLow;
				int r = actualHigh-actualLow;
				if (trade!=0){
					int pips = trade == 1 ? (q.getClose5()-entryValue) : (entryValue-q.getClose5());//es el q1 anterior
					if (pips>=0){
						wins++;
						winPips += pips*0.1;
						double pipsW = pips*0.1; 
						double factorWin = pipsW/(r*0.1); 
						/*System.out.println("[WIN] "
								+DateUtils.datePrint(cal_1)
								+" "+PrintUtils.Print2(pipsW)
								+" "+PrintUtils.Print2dec(winPips,false)
								+" "+PrintUtils.Print2dec(r*0.1,false)
								+" "+PrintUtils.Print2dec(factorWin,false)
								);*/
					}else{
						losses++;
						lostPips += -pips*0.1;
						double pipsL = -pips*0.1; 
						double factorLoss = pipsL/(r*0.1); 
						/*System.out.println("[LOSS] "
							+DateUtils.datePrint(cal_1)
							+" "+PrintUtils.Print2(-pips*0.1)
							+" "+PrintUtils.Print2dec(lostPips,false)
							+" "+PrintUtils.Print2dec(r*0.1,false)
							+" "+PrintUtils.Print2dec(factorLoss,false)
							);*/
					}
				}
				
				
				totalDays++;
				lastDayHigh1 = lastDayHigh;
				lastDayLow1 = lastDayLow;
				lastDayHigh = actualHigh;
				lastDayLow = actualLow;
				
				int hd = lastDayHigh-lastDayHigh1;
				int ld = lastDayLow1-lastDayLow;
				/*System.out.println(DateUtils.datePrint(cal_1)
						+" ["+lastDayHigh+" "+lastDayLow
						+"] ["+lastDayHigh1+" "+lastDayLow1
						+"] "+hd+" "+ld);*/
				
				if (lastDayHigh+factor*10>=q.getOpen5())
					lastDayHigh = lastDayHigh+factor*10; 
				if (lastDayLow-factor*10<=q.getOpen5())
					lastDayLow = lastDayLow-factor*10; 
				
				actualHigh = -1;
				actualLow = -1;
				
				
				highBreak = false;
				lowBreak = false;
				directionAllowed = 0;
				openIndex = -1;
				trade = 0;
				totalDayTrades = 0;
				lastDay = day;
			}
			
			if (totalDayTrades<maxTrades){//no metemos mas de los maximos
				if (actualHigh>=0 
						&& h1<=h && h<=h2){
						if (trade==0){
							if (directionAllowed>=0 && q.getOpen5()<=lastDayHigh && q.getHigh5()>=lastDayHigh){
								int closeDiff = lastDayHigh-q.getClose5();
								entryValue = lastDayHigh;
								if (closeDiff<=maxCloseDiff*10){
									tpValue = entryValue+tp*10;
									slValue = entryValue-sl*10;
									trade = 1;
									if (!tradeBreak){
										entryValue = q1.getOpen5();
										tpValue = entryValue-tp*10;
										slValue = entryValue+sl*10;
										trade = -1;
									}
									totalDayTrades++;
									highBreak = true;
									openIndex = i+1;
									if (debug)
									System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)+" "+lastDayHigh);
								}
							}else if (directionAllowed<=0 && q.getOpen5()>=lastDayLow && q.getLow5()<=lastDayLow){
								int closeDiff = q.getClose5()-lastDayLow;
								entryValue = lastDayLow;
								if (closeDiff<=maxCloseDiff*10){
									tpValue = entryValue-tp*10;
									slValue = entryValue+sl*10;
									trade = -1;
									if (!tradeBreak){
										entryValue = q1.getOpen5();
										tpValue = entryValue+tp*10;
										slValue = entryValue-sl*10;
										trade = 1;
									}									
									totalDayTrades++;
									lowBreak = true;
									openIndex = i+1;
									if (debug)
										System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)+" "+lastDayLow);
								}
							}
						}
				}
			}
		
			//evaluada con apertura
			if (trade!=0){
				int win = 0;
				int pips = 0;
				//evalucaion por hora
				if (h>=hClose 
						&& i>openIndex
						){
					pips = trade == 1 ? (q.getOpen5()-entryValue) : (entryValue-q.getOpen5());//es el q1 anterior	
					pips*=0.1;
					if (pips>=0){
						win = 1;						
					}else{
						win = -1;						
					}
					trade = 0;
				}
				
				if (trade==1){
					if (q1.getLow5()<=slValue){
						win = -1;
						pips = -sl;
						if (debug)
						System.out.println("[***FAIL LONG***] "+DateUtils.datePrint(cal)+" "+lastDayHigh+" || "+(-sl)+" "+(lostPips+sl));
					}else if (q1.getHigh5()>=tpValue
							|| (tradeBreak && q.getHigh5()>=tpValue)
							){
						win = 1;
						pips = tp;
						if (debug)
						System.out.println("[WIN LONG] "+DateUtils.datePrint(cal)+" "+lastDayHigh+" || "+(tp)+" "+(winPips+tp));
					}
				}else if (trade==-1){
					if (q1.getHigh5()>=slValue){
						win = -1;
						pips = -sl;
						if (debug)
						 System.out.println("[***FAIL SHORT***] "+DateUtils.datePrint(cal)+" "+lastDayLow+" || "+(-sl)+" "+(lostPips+sl));
					}else if (q1.getLow5()<=tpValue
							|| (tradeBreak && q.getLow5()<=tpValue)
							){	
						win = 1;
						pips = tp;
						if (debug)
						System.out.println("[WIN SHORT] "+DateUtils.datePrint(cal)+" "+lastDayLow+" || "+(tp)+" "+(winPips+tp));
					}
				}
				//acumulamos estadisticas
				if (win==1){
					//System.out.println("WIN "+trade+" "+DateUtils.datePrint(cal));
					wins++;
					winPips += Math.abs(pips);
					//System.out.println("WIN "+trade+" "+DateUtils.datePrint(cal)+" || "+Math.abs(pips));
					trade = 0;
					//paramos si ganamos
					//totalDayTrades = maxTrades;
					if (actualLosses>maxLosses) maxLosses = actualLosses;
					actualLosses = 0;
				}else if (win==-1){
					actualLosses++;
					
					losses++;
					lostPips += Math.abs(pips);
					//System.out.println("LOSS "+trade+" "+DateUtils.datePrint(cal)+" "+actualLosses+" || "+Math.abs(pips)+" || "+actualLosses);
					trade = 0;									
				}				
			}
						
			if (actualHigh==-1 || q.getHigh5()>=actualHigh) actualHigh = q.getHigh5();
			if (actualLow==-1 || q.getLow5()<=actualLow) actualLow = q.getLow5();
		}
		
		int totalTrades = wins+losses;
		double perWin = wins*100.0/totalTrades;
		double pf =winPips*1.0/lostPips;
		double avgPips = (winPips-lostPips)*1.0/totalTrades;
		//dadas por ganadas las indecisas
		int iwins=wins+indecisionTrades;
		int ilosses =losses-indecisionTrades;
		int itotalTrades = iwins+ilosses;
		int iwinPips =winPips+tp*indecisionTrades;
		int ilostPips =lostPips-sl*indecisionTrades;
		double iperWin = iwins*100.0/itotalTrades;
		double ipf = iwinPips*1.0/ilostPips;
		double iavgPips = (iwinPips-ilostPips)*1.0/itotalTrades;
		avgRange = avgRange*0.1/totalDays;
		System.out.println(
				header
				+" "+tp+" "+sl
				+" || "
				+totalTrades
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(perWin, false)
				+" "+PrintUtils.Print2dec(avgRange , false)
				+" "+PrintUtils.Print2dec(winPips,false)+" "+PrintUtils.Print2dec(lostPips,false)
				+" "+PrintUtils.Print2dec(winPips-lostPips,false)
				//+" "+PrintUtils.Print2dec(avgLosses*0.1/losses, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" || "
				+" "+itotalTrades
				+" "+iwins
				+" "+ilosses
				+" "+PrintUtils.Print2dec(iperWin, false)
				+" "+PrintUtils.Print2dec(iwinPips,false)+" "+PrintUtils.Print2dec(ilostPips,false)
				+" "+PrintUtils.Print2dec(ipf, false)
				+" "+PrintUtils.Print2dec(iavgPips, false)
				+" || "+maxLosses
				+" || "+indecisionTrades
				+" || "+slindecisionTrades
				);
	}
	
	public static void dailyReport(String header,ArrayList<QuoteShort> dailyData,int y1,int y2,int tp){
		
		int singleCases = 0;
		int singleWins = 0;
		int bothCases = 0;
		int bothWins = 0;
		int bothLosses = 0;
		int wins = 0;
		double winPips = 0;
		double lostPips = 0;
		double avgLosses = 0;
		int actualWins = 0;
		int actualLosses = 0;
		int maxWins = 0;
		int maxLosses = 0;
		int maxDaysFailed = 0;
		int actualDaysFailed = 0;
		double accBothPips = 0.0;
		double accSinglePips = 0.0;
		int accBothCases = 0;
		int accSingleCases = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<dailyData.size();i++){
			QuoteShort q_1 = dailyData.get(i-1);
			QuoteShort q = dailyData.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			
			int highDiff = q.getHigh5()-q_1.getHigh5();
			int lowDiff = q_1.getLow5()-q.getLow5();
			int highCloseDiff = q.getClose5()-q_1.getHigh5();
			int lowCloseDiff = q_1.getLow5()-q.getClose5();
			
			boolean isHighBreak = false;
			boolean isLowBreak = false;
			boolean isHighWin = false;
			boolean isLowWin = false;
			if (highDiff>=0){
				isHighBreak = true;
				if (highDiff>=tp*10){
					isHighWin = true;
					singleWins++;
					winPips+=tp;
					/*System.out.println("HIGH WIN "+tp+" "+PrintUtils.Print2dec(winPips,false)
							+" "+PrintUtils.Print2dec(lostPips,false)
							);*/					
				}else{//nowin
					
					//avgLosses+=highCloseDiff;
					if (highCloseDiff>=0) winPips+=highCloseDiff*0.1;
					else lostPips += -highCloseDiff*0.1;
					/*System.out.println("HIGH FAIL "+DateUtils.datePrint(cal)
							+" "+q.getHigh5()+" "+q.getClose5()
							+" || "+highCloseDiff
							+" "+PrintUtils.Print2dec(winPips,false)
							+" "+PrintUtils.Print2dec(lostPips,false)
							);*/
				}
				singleCases++;
			}
			
			if (lowDiff>=0){
				isLowBreak = true;
				if (lowDiff>=tp*10){
					isLowWin = true;
					singleWins++;
					winPips+=tp;
					/*System.out.println("LOW WIN "+tp+" "+PrintUtils.Print2dec(winPips,false)
							+" "+PrintUtils.Print2dec(lostPips,false)
							);*/
				}else{
					
					//avgLosses+=lowCloseDiff;
					if (lowCloseDiff>=0) winPips+=lowCloseDiff*0.1;
					else lostPips += -lowCloseDiff*0.1;
					/*System.out.println("LOW FAIL "+DateUtils.datePrint(cal)
							+" "+q.getLow5()+" "+q.getClose5()
							+" || "+lowCloseDiff
							+" "+PrintUtils.Print2dec(winPips,false)
							+" "+PrintUtils.Print2dec(lostPips,false)
							);*/
				}
				singleCases++;
			}

			if (isHighBreak && isLowBreak){
				if (!isHighWin && !isLowWin){
					bothLosses++;
				}
				if (isHighWin && isLowWin){
					bothWins++;
				}								
				bothCases++;
				//accBothPips += (highDiff+lowDiff)*0.1/2;
				if (highDiff>=lowDiff)
					accBothPips += (highDiff)*0.1/1;
				else accBothPips += (lowDiff)*0.1/1;
				accBothCases++;
			}else{
				if (highDiff>=0)
					accSinglePips += (highDiff)*0.1/1;
				else accSinglePips += (lowDiff)*0.1/1;
				accSingleCases++;
			}
			
			int totalDayWins = 0;
			int totalDayFails = 0;
			if (isHighBreak){
				if (!isHighWin){
					totalDayFails++;
				}else{
					totalDayWins++;
				}				
			}
			if (isLowBreak){
				if (!isLowWin)
					totalDayFails++;
				else totalDayWins++;
			}
			
			if (totalDayFails>0){
				actualDaysFailed++;
				if (actualDaysFailed>maxDaysFailed)
					maxDaysFailed = actualDaysFailed;
			}else{
				if (totalDayWins>0){//se corta la racha
					actualDaysFailed = 0;
				}
			}
		}
		
		double singleWinsPer = singleWins*100.0/singleCases;
		double bothWinsPer = bothWins*100.0/bothCases;
		double bothLossesPer = bothLosses*100.0/bothCases;
		int losses = singleCases-singleWins;
		double pf =winPips/lostPips;
		double avgPips = (winPips-lostPips)/singleCases;
		System.out.println(
				header
				+" "+tp+" || "
				+singleCases
				+" "+singleWins
				+" "+(singleCases-singleWins)
				+" "+PrintUtils.Print2dec(singleWinsPer, false)
				+" "+PrintUtils.Print2dec(winPips,false)+" "+PrintUtils.Print2dec(lostPips,false)
				//+" "+PrintUtils.Print2dec(avgLosses*0.1/losses, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" || "+bothCases
				+" "+PrintUtils.Print2dec(bothWinsPer, false)
				+" "+PrintUtils.Print2dec(bothLossesPer, false)
				+" || "+maxDaysFailed
				+" || "
				+" "+PrintUtils.Print2dec(accBothPips*1.0/accBothCases, false)
				+" "+PrintUtils.Print2dec(accSinglePips*1.0/accSingleCases, false)
				+" "+PrintUtils.Print2dec(lostPips*1.0/losses, false)
				);
	}
	
	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.12.15.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
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

		
		int limit = paths.size()-1;
		limit = 0;
		for (int i = 0;i<=limit;i++){
			Sizeof.runGC ();
			String path = paths.get(i);	
			ArrayList<Quote> dataI 		= null;
			ArrayList<Quote> dataS 		= null;
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				//provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				//provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				//provider="dukasc";
			}										
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	
		  	dataS.clear();
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			data5m.clear();
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			//dataI.clear();
			//dataS.clear();
			//data5m.clear();
			data = data5mS;
			System.out.println(data.size());
			for (int tp=5;tp<=5;tp+=5){
				for (int sl=10;sl<=80;sl+=5){
					for (int y1=2003;y1<=2003;y1++){
						int y2 = y1+13;
						String header = path.replace("C:\\fxdata\\", "").trim().substring(0,6);
						//TestDailyBreakout2.dailyReport(header,dailyData,y1,y2,tp);
						for (int maxTrades = 1;maxTrades<=1;maxTrades++){
							for (int h1=0;h1<=0;h1++){
								int h2 = h1 +23;
								for (int maxTime=10000;maxTime<=10000;maxTime++){
									for (int closeDiff=1000;closeDiff<=1000;closeDiff++){
										for (int offset=0;offset<=0;offset++){
											for (int hClose = 99;hClose<=99;hClose++)
												TestDailyBreakout2.testBreak(header, data, y1, y2, h1,h2,tp, sl, maxTrades,maxTime,closeDiff,offset,hClose,false,true,false);
										}
									}
								}
							}
						}
					}
				}
			}
			data.clear();
		}
	}

}
