package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class ReverseDOWES {
	
	public static int doTrade(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int dayWeek1,
			int thr1,
			//int thr2,
			int nbars,
			int tp,int sl,
			double comm,
			int factor,
			int minDiff,
			boolean debug
			){
	
		int lastTradeIdxL = 0;
		int lastTradeIdxS = 0;
		int lastFailedDay= -1;
		int totalFailedDays = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		
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
		Calendar cal1 = Calendar.getInstance();
		Calendar calr = Calendar.getInstance();
		int days =0;
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size()-nbars;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
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
			
			
			int maxMin = maxMins.get(i-1);
			if (h>=h1 && h<=h2){
				if (maxMin<=-thr1
						&& (i-lastTradeIdxS)>=minDiff
						){
					//if (maxMin<=-thr){
						int valueTP = q.getOpen5()+tp*factor;
						int valueSL = q.getOpen5()-sl*factor;
						//int diff = data.get(i+nbars).getClose5()-q.getOpen5();
						TradingUtils.getMaxMinShortTPSL(data, qm, calr, i, i+nbars, valueTP, valueSL, false);
						int diff = qm.getClose5()-q.getOpen5();
						diff -=comm*factor;
						diffDay += diff;
						dayTrades++;
						//System.out.println("[LONG] "+DateUtils.datePrint(cal)+" || "+diff);
						lastTradeIdxS = i;
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
					//}
				}else if (maxMin>=thr1
						&& (i-lastTradeIdxL)>=minDiff
						
						){
					//if (maxMin>=thr2){
						int valueTP = q.getOpen5()-tp*factor;
						int valueSL = q.getOpen5()+sl*factor;
						//int diff = data.get(i+nbars).getClose5()-q.getOpen5();
						TradingUtils.getMaxMinShortTPSL(data, qm, calr, i, i+nbars, valueTP, valueSL, false);
						int diff = q.getOpen5()-qm.getClose5();
						diff -=comm*factor;
						diffDay += diff;
						dayTrades++;
						//System.out.println("[SHORT] "+DateUtils.datePrint(cal)+" || "+diff+" || "+day);
						lastTradeIdxL = i;
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
					//}
				}
			}				
		}
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*(1.0/factor)/trades;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/trades;
		double dayPer = dayWins*100.0/totalDays;
		
		if (debug)
		System.out.println(
				PrintUtils.Print2Int(h1,2)
				+" "+PrintUtils.Print2Int(h2,2)
				+" "+tp+" "+sl
				+" "+thr1
				//+" "+thr2
				+" "+nbars
				+" || "
				+" "+PrintUtils.Print2Int(trades,5)
				+" "+PrintUtils.Print2Int(wins,5)
				+" "+PrintUtils.Print2Int(losses,5)
				+" "+PrintUtils.Print2Int(totalFailedDays,4)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(winPips*(1.0/factor)/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*(1.0/factor)/losses, false)
				+" || "+PrintUtils.Print2dec(totalDays*100.0/days, false)+" || "+PrintUtils.Print2dec(dayPer, false)
				+" || "+PrintUtils.Print2dec(trades*(pf-1.0),false)
				);
		
		if (avg>=2.5 && pf>=1.5) return 1;
		return 0;
		
	}

	public static void main(String[] args) {
		String fileNameYM = "C:\\fxdata\\YM.txt";
		String fileNameES = "C:\\fxdata\\ES.txt";; 	 
				
		ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameYM, DataProvider.KIBOT);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES, DataProvider.KIBOTES);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES2010, DataProvider.DAVE);		
		ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
		
		
		System.out.println("Data: "+data.size());
		
		int factor = 1;
		
		//SimpleContinuation
		for (int y1=2009;y1<=2009;y1+=1){
			int y2 = y1+7;
			for (int h1=15;h1<=15;h1++){
				int h2=h1+3;
				int points = 0;
				for (int thr1=10;thr1<=1000;thr1+=10){
					for (int nbars = 240;nbars<=240;nbars+=60){
						for (int dayWeek1=Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
							for (int tp=500;tp<=500;tp+=1){
								for (int sl=100000;sl<=100000;sl+=100){
									double comm = 0.0;
									for (int minDiff=0;minDiff<=0;minDiff+=5){
										//Pullbacks.doTrade(data,maxMins, y1, y2, h1, h2,dayWeek1, thr1,thr2, nbars,tp,sl,comm,25);//ES
										points+=ReverseDOWES.doTrade(data,maxMins, y1, y2, h1, h2,dayWeek1, thr1, nbars,tp,sl,comm,factor,minDiff,true);//YM
									}
								}
							}										
						}
					}
				}//thr1
				//System.out.println(h1+" "+points);
			}//H
			System.out.println("");
		}//year
	}

}
