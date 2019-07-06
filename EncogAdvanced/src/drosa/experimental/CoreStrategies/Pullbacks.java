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

public class Pullbacks {
	
	public static void doTrade(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int dayWeek1,
			int thr1,int thr2,
			int nbars,
			int tp,int sl,
			int minPips,
			double comm,
			int factor
			){
	
		int lastFailedDay= -1;
		int totalFailedDays = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int avgLossRange =0;
		
		int actualMax = -1;
		int actualMin = -1;
		int hmax = 0;
		int hmin = 0;
		int lastDay = -1;
		int diffDay = 0;
		int dayWins = 0;
		int totalDays = 0;
		int dayTrades = 0;
		boolean longEnabled = false;
		boolean shortEnabled = false;
		Calendar cal = Calendar.getInstance();
		Calendar calr = Calendar.getInstance();
		int days =0;
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size()-nbars;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//if (dayWeek!=dayWeek1) continue;
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				//System.out.println("[NEW DAY] "+DateUtils.datePrint(cal)+" "+day);
				actualMax = -1;
				actualMin = -1;
				hmax = 0;
				hmin = 0;	
				
				if (dayTrades>0){
					//System.out.println("[day] "+DateUtils.datePrint(cal)+" "+dayTrades+" "+dayWeek +" || "+diffDay);
					if (diffDay>0){						
						dayWins++;
					}
					totalDays++;
					diffDay = 0;
					dayTrades = 0;
				}				
				longEnabled = false;
				shortEnabled = false;
				lastDay = day;	
				days++;
			}
			
			
			int actualRange = actualMax -actualMin;
			int maxMin = maxMins.get(i-1);
			if (actualMax!=-1 
					&& actualRange>=minPips*10
					&& dayTrades==0 
					&& longEnabled){
				int diffToMax = actualMax - q.getOpen5();
				if (maxMin<=-thr2){
					int valueTP = q.getOpen5()+tp*factor;
					int valueSL = q.getOpen5()-sl*factor;
					//int diff = data.get(i+nbars).getClose5()-q.getOpen5();
					TradingUtils.getMaxMinShortTPSL(data, qm, calr, i, i+nbars, valueTP, valueSL, false);
					int diff = qm.getClose5()-q.getOpen5();
					diff -=comm*factor;
					diffDay += diff;
					dayTrades++;
					//System.out.println("[LONG] "+DateUtils.datePrint(cal)+" || "+diff);
					if (diff>=0){
						winPips += diff;
						wins++;						
					}else{
						lostPips += -diff;
						losses++;
						if (day!=lastFailedDay){
							totalFailedDays++;
							lastFailedDay = day;
						}
					}
				}
			}else if (actualMin!=-1 
					&& dayTrades==0 
					&& actualRange>=minPips*10
					&&  shortEnabled){
				int diffToMin = q.getOpen5()-actualMin;
				if (maxMin>=thr2){
					int valueTP = q.getOpen5()-tp*factor;
					int valueSL = q.getOpen5()+sl*factor;
					//int diff = data.get(i+nbars).getClose5()-q.getOpen5();
					TradingUtils.getMaxMinShortTPSL(data, qm, calr, i, i+nbars, valueTP, valueSL, false);
					int diff = q.getOpen5()-qm.getClose5();
					diff -=comm*factor;
					diffDay += diff;
					dayTrades++;
					//System.out.println("[SHORT] "+DateUtils.datePrint(cal)+" || "+diff+" || "+day);
					if (diff>=0){
						winPips += diff;
						wins++;
					}else{
						lostPips += -diff;
						losses++;
						if (day!=lastFailedDay){
							totalFailedDays++;
							lastFailedDay = day;
						}
						avgLossRange += actualMax-actualMin;
					}
				}
			}
			
			maxMin = maxMins.get(i);			
			if (q.getHigh5()>=actualMax || actualMax==-1){
				actualMax = q.getHigh5();
				hmax = h;
			}
			if (q.getLow5()<=actualMin || actualMin==-1){
				actualMin = q.getLow5();
				hmin = h;
			}
			
			if (maxMin>=thr1){
				if (h>=h1 && h<=h2){
					longEnabled = true;
					shortEnabled = false;
				}
			}
			
			if (maxMin<=-thr1){
				if (h>=h1 && h<=h2){
					longEnabled = false;
					shortEnabled = true;
				}
			}
			
		}
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*(1.0/factor)/trades;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/trades;
		double dayPer = dayWins*100.0/totalDays;
		double avgRR = avg*100.0/sl;
		double avgLR = avgLossRange*0.1/losses;
		
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+tp+" "+sl
				+" "+thr1+" "+thr2
				+" "+nbars
				+" "+minPips
				+" || "
				+" "+PrintUtils.Print2Int(trades,5)
				+" "+PrintUtils.Print2Int(wins,5)+" "+PrintUtils.Print2Int(losses,5)
				+" "+PrintUtils.Print2Int(totalFailedDays,3)
				//+" "+PrintUtils.Print2dec(avgLR,false)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(totalDays*100.0/days, false)+" || "+PrintUtils.Print2dec(dayPer, false)
				+" || "+PrintUtils.Print2dec(trades*(pf-1.0),false)
				+" || "+PrintUtils.Print2dec(avgRR,false)
				+" || "+PrintUtils.Print2dec(avgRR*trades,false)
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\usdjpy_UTC_5 Mins_Bid_2003.05.04_2016.08.03.csv";
		
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
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			//SimpleContinuation
			for (int y1=2003;y1<=2003;y1+=1){
				int y2 = y1+13;
				for (int h1=16;h1<=16;h1++){
					int h2=h1+0;
					for (int thr1=100;thr1<=10000;thr1+=100){
						for (int thr2=1;thr2<=1;thr2+=1){
							for (int nbars = 99999;nbars<=99999;nbars+=12){
							//for (int nbars = 12;nbars<=48*12;nbars+=12){
								for (int minPips=0;minPips<=0;minPips+=10){
									//DailyBreak.doTrade(data, y1, y2, h1, h2, tp);
									//DailyBreak.doTrade2(data, y1, y2, h1, h2, tp);
									for (int dayWeek1=Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
										for (int tp=5;tp<=5;tp+=1){
											for (int sl=(int) (20.0*tp);sl<=20.0*tp;sl+=1.0*tp){
												double comm = 0.0;
												Pullbacks.doTrade(data,maxMins, y1, y2, h1, h2,dayWeek1, thr1,thr2, nbars,tp,sl,
														minPips,comm,10);
											}
										}										
									}
								}
							}
						}
					}
				}
			}
		
		}//limit


	}

}
