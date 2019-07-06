package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.utils.DateUtils;
import drosa.utils.FileUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class FirstStrikeDaily {
	
	public static void test(ArrayList<QuoteShort> data,
			int h1,int h2,
			int entry,int tp,int sl,boolean debug){
		
		double winPips = 0;
		double lostPips = 0;
		int wins = 0;
		int losses = 0;
		int buyEntry = -1;
		int buyTP = -1;
		int buySL = -1;
		int sellEntry = -1;
		int sellTP = -1;
		int sellSL = -1;
		int slPips = 0;
		int tradeActive = 0;
		int lastWeek = 0;
		int lastDay = -1;
		boolean canTrade = true;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int year = cal.get(Calendar.YEAR);
			
			if (year<2000) continue;
			
			if (week!=lastWeek && h>=h1 && h<=h2){
				double pips = 0;
				if (tradeActive==1){//buy
					pips = (q.getOpen5()-buyEntry)*0.1;
				}else if (tradeActive==-1){//sell
					pips = (sellEntry-q.getOpen5())*0.1;
				}
				
				if (tradeActive!=0){
					String msg="";
					if (pips>=0){
						wins++;
						winPips += pips;
						
					}else{
						losses++;
						lostPips += -pips;
						//System.out.println("[CLOSE] "+DateUtils.datePrint(cal)+" "+pips);
					}
					if (debug)
					System.out.println("[CLOSE] "+pips);
				}
				//nueva semana
				buyEntry = q.getOpen5()+10*entry;
				//buySL = q.getOpen5()-10*sl;
				buyTP = buyEntry+10*tp;
				buySL = buyEntry-10*sl;
				sellEntry = q.getOpen5()-10*entry;
				//sellSL = q.getOpen5()+10*sl;
				sellTP = sellEntry-10*tp;
				sellSL = sellEntry+10*sl;
				slPips = sl;
				tradeActive = 0;
				canTrade = true;
				lastWeek = week;
			}
			
			if (debug)
			System.out.println(q.toString());
			
			if (tradeActive==0 && canTrade){//no hay trade aun
				if (q.getHigh5()>=buyEntry){
					tradeActive = 1;
					canTrade = false;
					if (debug)
					System.out.println("[OPEN BUY] "+buyEntry+" "+buySL);
				}else if (q.getLow5()<=sellEntry){
					tradeActive = -1;
					canTrade = false;
					if (debug)
					System.out.println("[OPEN SELL] "+sellEntry+" "+sellSL);
				}
				
			}
			
			if (tradeActive!=0){
				boolean closed = false;
				int win = 0;
				String msg="";
				if (tradeActive==1){
					if (q.getLow5()<=buySL){
						msg="[CLOSE BUY SL] "+buyEntry+" "+buySL+" "+(-slPips);
						closed = true;
						win = -1;
					}else if (q.getHigh5()>=buyTP){
						msg="[CLOSE BUY TP] "+buyEntry+" "+buyTP+" "+(tp);
						closed = true;
						win = 1;
					}
				}else if (tradeActive==-1){
					if (q.getHigh5()>=sellSL){
						msg="[CLOSE SELL SL] "+sellEntry+" "+sellSL+" "+(-slPips);
						closed = true;
						win = -1;
					}else if (q.getLow5()<=sellTP){
						msg="[CLOSE SELL TP] "+sellEntry+" "+sellTP+" "+(tp);
						closed = true;
						win = 1;
					}
				}
				if (closed){
					if (debug)
					System.out.println(msg);
					if (win==-1){
						losses++;
						lostPips += slPips;
					}else if (win==1){
						wins++;
						winPips += tp;
					}
					tradeActive=0;
				}
			}
		}//for
		
		int totalTrades = wins+losses;
		double winPer = wins*100.0/totalTrades;
		double pf = winPips*1.0/lostPips;
		double avgPips = (winPips-lostPips)/totalTrades;
		
		System.out.println(entry
				+" "+tp
				+" "+sl
				+" || "+totalTrades+" "+wins+" "+losses
				+" "+PrintUtils.Print2(winPer,false)
				+" "+PrintUtils.Print2(avgPips,false)
				+" "+PrintUtils.Print2(pf,false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		
		ArrayList<ArrayList<QuoteShort>> datas = new ArrayList<ArrayList<QuoteShort>>();
		ArrayList<String> files = new ArrayList<String>();
		//String	path1 = "c:\\fxdata\\EURUSD_pepper_daily.csv";
		String	path1 = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.05.csv";
		String	path2 = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.05.csv";
		String	path3 = "c:\\fxdata\\usdjpy_UTC_5 Mins_Bid_2003.12.31_2015.09.05.csv";
		String	path4 = "c:\\fxdata\\audusd_UTC_5 Mins_Bid_2003.12.31_2015.09.05.csv";
		files.add(path1);
		files.add(path2);
		files.add(path3);
		files.add(path4);
	
		int limit = files.size()-1;
		limit = 0;
		//calculo de los arrays de datos
		for (int i=0;i<=limit;i++){
			Sizeof.runGC ();
			String fileName = files.get(i);
			ArrayList<QuoteShort> data = FileUtils.extractData(fileName);
			System.out.println("total data: "+data.size());
			for (int h1=0;h1<=0;h1++){
				int h2=h1+9;
				for (int entry=20;entry<=20;entry+=5){
					int tp = entry;
					for (tp=200;tp<=200;tp+=5){
						//for (int sl=entry*50;sl<=entry*50;sl+=entry){
						for (int sl=5;sl<=100;sl+=5){
							FirstStrikeDaily.test(data,h1,h2, entry,tp,sl,false);
						}
					}
				}
			}
			//datas.add(data);
			//System.out.println("añadido: "+fileName);
		}
		
		
	}

}
