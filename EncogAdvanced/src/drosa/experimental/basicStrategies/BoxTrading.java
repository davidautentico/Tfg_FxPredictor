package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.zznbrum.TrendInfo;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class BoxTrading {
	
	public static void test2(ArrayList<QuoteShort> data,
			int y1,int y2,int h1,int h2,
			int rr,
			int maxTries,
			int debug){
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		int winPipsYear = 0;
		int lostPipsYear = 0;
		int high = -1;
		int low = -1;
		int range = 0;
		int buyTarget = -1;
		int sellTarget = -1;
		int mode = 0;
		int tries = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int seqLosses = 0;
		int seqWins = 0;
		
		int count2 = 0;
		int wins2 = 0;
		int losses2 = 0;
		int countIncompleted = 0;
		
		int highEntry = -1;
		int lowEntry = -1;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (y<y1 || y>y2) continue;
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfy = winPipsYear*1.0/lostPipsYear;
					if (pfy>=1.0) countYears++;
				}
				winPipsYear = 0;
				lostPipsYear = 0;
				lastYear = y;
			}
			
			if (day!=lastDay){
				if (mode==2 
						//|| mode==0
						){//hasta que no se gana o pierde no se actualiza?
					mode=0;
					tries = 0;
					high= -1;
					low = -1;
					highEntry = -1;
					lowEntry = -1;
					range = -1;
					if (debug==1)
						System.out.println(DateUtils.datePrint(cal)+" MODE A 0 || "+high+" "+low);
				}
				lastDay = day;
			}
			
			if (mode==0){
				if (h>=h1 && h<h2){
					if (high == -1 || q.getHigh5()>=high){
						high = q.getHigh5();
					}
					if (low == -1 || q.getLow5()<=low){
						low = q.getLow5();
					}					
				}
			}
			
			//REFERENCIAS
			if (h==h2 && min==0 && high>=0 && mode==0){
				range = high-low;
				buyTarget = high + rr*range;
				sellTarget = low - rr*range;
				highEntry = high;
				lowEntry = low;
				if (debug==1)
					System.out.println(DateUtils.datePrint(cal)+" || "+high+" "+low+" || "+buyTarget+" "+sellTarget);
			}
			
			//ENTRADAS
			if (highEntry!=-1 
					//&& range<=1000
					){
				if (mode==0 && tries<maxTries){
					if (q.getHigh5()>=highEntry){
						mode = 1;
						tries++;
						if (debug==1)
							System.out.println(DateUtils.datePrint(cal)
									+" || ENTRY HIGH "+tries
									+" "+highEntry+" "+lowEntry+" | "
									+" | "+q.toString());
					}else if (q.getLow5()<=lowEntry){
						mode = -1;
						tries++;
						if (debug==1)
							System.out.println(DateUtils.datePrint(cal)+" || ENTRY LOW "+tries
									+" "+highEntry+" "+lowEntry+" | "
									+" | "+q.toString());
					}
				}
			}
				
			//SALIDAS Y REENTRADAS	
			if (mode==1){
				if (q.getHigh5()>=buyTarget){
					mode=2;
					winPips += rr*range; 
					wins++;
					seqWins++;
					
					if (tries==maxTries){
						count2++;
						wins2++;
					}
					if (debug==1)
						System.out.println(DateUtils.datePrint(cal)+" || HIGH WIN "+tries+" || ");
				}else if (q.getLow5()<=low){
					mode=2;
					lostPips += range;
					losses++;
					
					if (tries==maxTries){
						count2++;
						losses2++;
					}
					
					if (debug==1)
						System.out.println(DateUtils.datePrint(cal)+" || HIGH LOST "+tries);
					if (tries<maxTries){
						mode = -1;
						tries++;
						if (q.getLow5()<=sellTarget){
							mode=2;
						}
					}else{
						seqLosses++;								
					}
				}
			}else if (mode==-1){
				if (q.getLow5()<=sellTarget){
					mode=2;
					winPips += rr*range; 
					wins++;
					seqWins++;
					
					if (tries==maxTries){
						count2++;
						wins2++;
					}
					if (debug==1)
						System.out.println(DateUtils.datePrint(cal)+" || LOW WIN "+tries);
				}else if (q.getHigh5()>=high){
					mode=2;
					lostPips += range;
					losses++;
					
					if (tries==maxTries){
						count2++;
						losses2++;
					}
					
					if (debug==1)
						System.out.println(DateUtils.datePrint(cal)+" || LOW LOST "+tries);
					if (tries<maxTries){
						mode = 1;
						tries++;
						if (q.getHigh5()>=buyTarget){
							mode=2;
						}
					}else{
						seqLosses++;
					}
				}
			}
		}
		
		if (mode==1 || mode==-1){
			int pips = 0;
			if (mode==1){
				pips = data.get(data.size()-1).getClose5()-high;
			}else if (mode==-1){
				pips = low-data.get(data.size()-1).getClose5();
			}
			
			if (pips>=0){
				winPips+=pips;
				wins++;
			}else{
				lostPips += -pips;
				losses++;
			}
			countIncompleted++;
		}
		
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = winPips*1.0/lostPips;
		
		int totalSeq = seqWins+seqLosses;
		double winSeqPer = seqWins*100.0/totalSeq;
		
		
		double winsPer2 = wins2*100.0/count2;
		double avg = (winPips-lostPips)*0.1/total;
		System.out.println(
				y1+" "+y2+" "+h1+" "+h2
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec2(winPer, false)
				+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "
				+" "+totalSeq+" "+seqLosses
				+" "+PrintUtils.Print2dec2(winSeqPer, false)
				+" || "+count2+" "+wins2+" "+losses2+" "+PrintUtils.Print2dec2(winsPer2, false)
				+" || "+countIncompleted
				);
	}
	
	public static void test(ArrayList<QuoteShort> data,
			int y1,int y2,int h1,int h2,
			int rr,
			int maxTries,
			int debug){
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		int winPipsYear = 0;
		int lostPipsYear = 0;
		int high = -1;
		int low = -1;
		int range = 0;
		int buyTarget = -1;
		int sellTarget = -1;
		int mode = 0;
		int tries = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int seqLosses = 0;
		int seqWins = 0;
		
		int count2 = 0;
		int wins2 = 0;
		int losses2 = 0;
		int countIncompleted = 0;
		int entry = 0;
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
			
			//if (month<4 || (month==4 && cal.get(Calendar.DAY_OF_MONTH)<11)) continue;
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfy = winPipsYear*1.0/lostPipsYear;
					if (pfy>=1.0) countYears++;
				}
				winPipsYear = 0;
				lostPipsYear = 0;
				lastYear = y;
			}
			
			if (day!=lastDay){
				
				if (lastDay!=-1){					
					if (mode==1 || mode==-1){
						int pips = 0;
						if (mode==1){
							pips = q1.getClose5()-high;
						}else if (mode==-1){
							pips = low-q1.getClose5();
						}
						
						if (pips>=0){
							winPips+=pips;
							wins++;
						}else{
							lostPips += -pips;
							losses++;
						}
						countIncompleted++;
					}
					
				}
				
				high = -1;
				low = -1;
				lastDay = day;
				mode=0;
				tries=0;
			}
			
			if (h>=h1 && h<h2){
				if (high == -1 || q.getHigh5()>=high){
					high = q.getHigh5();
				}
				if (low == -1 || q.getLow5()<=low){
					low = q.getLow5();
				}
				range = high-low;
				buyTarget = high + range;
				sellTarget = low - range;
			}
			
			if (h==h2 && min==0){
				if (debug==3)
					System.out.println(DateUtils.datePrint(cal)+" || "+high+" "+low);
			}
			
			if (h>=h2){
				if (mode==0 
						//&& range>=150 && range<=450
						&& tries<maxTries){
					if (q.getClose5()>=high){
						mode = 1;
						tries++;
						entry = q.getClose5();
						if (debug==3)
							System.out.println(DateUtils.datePrint(cal)+" || ENTRY HIGH "+tries);
					}else if (q.getClose5()<=low){
						entry = q.getClose5();
						mode = -1;
						tries++;
						if (debug==3)
							System.out.println(DateUtils.datePrint(cal)+" || ENTRY LOW "+tries);
					}
				}else if (mode==1){
					if (q.getHigh5()>=buyTarget){
						mode=2;
						winPips += buyTarget-entry; 
						wins++;
						seqWins++;
						
						if (tries==maxTries){
							count2++;
							wins2++;
						}
						if (debug==1)
							System.out.println(DateUtils.datePrint(cal)+" || [WIN] HIGH WIN "+tries
									+" "+(wins+losses)+" "+wins
									+" ||| "+high+" "+low+" | "+buyTarget+" "+sellTarget
									);
					}else if (q.getLow5()<=low){
						mode=2;
						lostPips += entry-low;
						losses++;
						
						if (tries==maxTries){
							count2++;
							losses2++;
						}
						
						if (debug==1)
							System.out.println(DateUtils.datePrint(cal)+" || [LOST] HIGH LOST "+tries
									+" "+(wins+losses)+" "+wins
									+" ||| "+high+" "+low+" | "+buyTarget+" "+sellTarget
									);
						if (tries<maxTries){
							mode = -1;
							tries++;
							if (q.getLow5()<=sellTarget){
								mode=2;
							}
						}else{
							seqLosses++;
						}
					}
				}else if (mode==-1){
					if (q.getLow5()<=sellTarget){
						mode=2;
						winPips += entry-sellTarget; 
						wins++;
						seqWins++;
						
						if (tries==maxTries){
							count2++;
							wins2++;
						}
						if (debug==1)
							System.out.println(DateUtils.datePrint(cal)+" || [WIN] LOW WIN "+tries
									+" "+(wins+losses)+" "+wins
									+" ||| "+high+" "+low+" | "+buyTarget+" "+sellTarget
									);
					}else if (q.getHigh5()>=high){
						mode=2;
						lostPips += high-entry;
						losses++;
						
						if (tries==maxTries){
							count2++;
							losses2++;
						}
						
						if (debug==1)
							System.out.println(DateUtils.datePrint(cal)+" || [LOST] LOW LOST "+tries
									+" "+(wins+losses)+" "+wins
									+" ||| "+high+" "+low+" | "+buyTarget+" "+sellTarget
									);
						if (tries<maxTries){
							mode = 1;
							tries++;
							if (q.getHigh5()>=buyTarget){
								mode=2;
							}
						}else{
							seqLosses++;
						}
					}
				}
			}//h2
		}
		
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = winPips*1.0/lostPips;
		
		int totalSeq = seqWins+seqLosses;
		double winSeqPer = seqWins*100.0/totalSeq;
		
		
		double winsPer2 = wins2*100.0/count2;
		double avg = (winPips-lostPips)*0.1/total;
		System.out.println(
				y1+" "+y2+" "+h1+" "+h2
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec2(winPer, false)
				+" "+winPips+" "+lostPips
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "
				+" "+totalSeq
				+" "+PrintUtils.Print2dec2(winSeqPer, false)
				+" || "+count2+" "+wins2+" "+losses2+" "+PrintUtils.Print2dec2(winsPer2, false)
				+" || "+countIncompleted
				);
	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		//String pathEURUSD = path0+"eurUSD_UTC_1 Min_Bid_2011.12.31_2018.01.16.csv";
		String pathEURUSD = path0+"eurjpy_UTC_5 Mins_Bid_2003.12.31_2018.01.22.csv";
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
			
			
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+4;
				for (int tries=1;tries<=1;tries++){
					for (int rr=1;rr<=1;rr++){
						BoxTrading.test(data, 2012, 2018, h1, h2, rr, tries, 0);
					}
				}
			}
		}

	}

}
