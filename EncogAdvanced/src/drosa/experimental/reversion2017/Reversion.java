package drosa.experimental.reversion2017;

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

public class Reversion {
	
	public static void test(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int tp,int sl,
			int thr,
			int maxBars,
			int comm,
			int debug
			){
		
	
		
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		for (int i=0;i<=20;i++) streaks.add(0);
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualLow = -1;
		int actualHigh = -1;
		int order = 0;
		int minIdx = 0;
		int mode = 0;
		boolean canTrade = false;
		int entry = 0;
		int tpOriginal = tp;
		
		double totalProfit = 0;
		double totalLoss = 0;
		double totalCommissions = 0.0;
		double actualProfit = 0;
		double actualTarget = 0;
		double actualRisk = 0;
		int miniLots = 0;//1 minilot=0.1
		int maxMiniLots = 0;
		int actualTrades = 0;
	
		int totalTrades = 0;
		int wins = 0;
		int losses = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		
		double commLot$$ = 0.08;
		
		int accAdv = 0;
		int count = 0;
		int accFail = 0;
		int accFailClose = 0;
		int fails = 0;
		int accWins = 0;
		int closeValue = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){	
				lastHigh = actualHigh;
				lastLow = actualLow;
				actualHigh = -1;
				actualLow = -1;
				order = 0;
				lastDay = day;
				minIdx = i;
				canTrade = true;
				//System.out.println("[DAY] "+DateUtils.datePrint(cal1)+" || "+lastHigh+" "+lastLow+" || "+q1.toString());
			}
			
			
			if (h<h1 || h>h2) continue;
			
			int maxMin = maxMins.get(i-1);
			
			if (maxMin>=thr){				
				entry = q.getOpen5();
				int maxAdv = 0;
				int win = 0;
				int end = i+maxBars;
				if (end>=data.size()-1) end = data.size()-1;
				for (int j=i;j<=end;j++){
					QuoteShort qj = data.get(j);
					int diffL = entry-qj.getLow5();
					int diffH = qj.getHigh5()-entry;
					closeValue = qj.getClose5();
					if (diffH>=maxAdv){
						maxAdv = diffH;
						if (diffH>=sl*10){
							break;
						}
					}
					if (diffL>=tp*10){
						accAdv += maxAdv;
						count++;
						win=1;
						accWins+=(tp)*10-comm;
						break;
					}					
				}
				
				if (win==0){
					int failClose = closeValue-entry+comm;
					if (failClose>=0){
						fails++;
						accFail+=maxAdv;
						accFailClose += failClose;
						if (debug==1){
							System.out.println("[FAIL] "+failClose+" "+DateUtils.datePrint(cal));
						}
					}else{
						accWins+=-failClose;						
					}
				}
												
			}else if (maxMin<=-thr){
				entry = q.getOpen5();
				int maxAdv = 0;
				int win = 0;
				int end = i+maxBars;
				if (end>=data.size()-1) end = data.size()-1;
				for (int j=i;j<=end;j++){
					QuoteShort qj = data.get(j);
					int diffL = entry-qj.getLow5();
					int diffH = qj.getHigh5()-entry;
					closeValue = qj.getClose5();
					if (diffL>=maxAdv){
						maxAdv = diffL;
						if (diffL>=sl*10){
							break;
						}
					}
					if (diffH>=tp*10){
						accAdv += maxAdv;
						count++;
						win=1;
						accWins+=(tp)*10-comm;
						break;
					}					
				}
				
				if (win==0){
					int failClose = entry-closeValue+comm;
					if (failClose>=0){
						fails++;
						accFail+=maxAdv;
						accFailClose+=failClose;
						if (debug==1){
							System.out.println("[FAIL] "+failClose+" "+DateUtils.datePrint(cal));
						}
					}else{
						accWins+=-failClose;
					}
				}
			}						
		}
		
		double pf = accWins*0.1/(accFailClose*0.1);
		System.out.println(
			   h1+" "+h2+" "+tp+" "+sl+" "+thr+" "+maxBars
			   +" || "
			   +" "+count
			   +" "+PrintUtils.Print2dec(accAdv*0.1/count, false)
			   +" || "+fails+" "+PrintUtils.Print2dec(accFail*0.1/fails, false)+" "+PrintUtils.Print2dec(accFailClose*0.1/fails, false)
			+" || "+PrintUtils.Print2dec(pf, false)+" "+accWins+" "+accFailClose
				);
	}

	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2017.03.20.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int y1=2003;y1<=2003;y1++){
				int y2 = y1+14;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+9;
					
					for (int tp=12;tp<=12;tp+=1){
						for (int sl=1000;sl<=1000;sl+=1){
							for (int thr=0;thr<=5000;thr+=100){
								for (int maxBars=24;maxBars<=24;maxBars+=1){
									Reversion.test(data, maxMins, y1, y2, h1, h2, tp,sl, thr, maxBars,20, 0);
								}
							}
						}
					}
					
				}
			}
			
			
		}

	}

}
