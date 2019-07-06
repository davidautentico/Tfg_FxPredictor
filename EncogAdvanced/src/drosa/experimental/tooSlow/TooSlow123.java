package drosa.experimental.tooSlow;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TooSlow123 {
	
	
	public static void doTrade(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int dayWeek1,int dayWeek2,
			int maxTrades,
			int tp,
			int minPips,
			boolean debug
			){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int high = -1;
		int low = -1;
		int lastH = -1;
		int highH = -1;
		int lowH = -1;
		int hOpen = -1;
		int mode = 0;
		int dayTrades = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				dayTrades = 0;
				mode = 0;
				highH = 0;
				lowH = 0;
				high = -1;
				low = -1;
				lastDay = day;
			}
			
			//comprobamos si es nueva hora
			if (h!=lastH){
				lastH = h;
				hOpen = q.getOpen5();
				dayTrades = 0;
				mode = 0;
			}
			
			//actualizamos highs and lows
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				highH = h;
				mode = 0;
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
				lowH = h;
				mode = 0;
			}
			
			
			//comprobamos si estamos en la hora siguiete
			if (mode==0){
				if (highH==h-1){
					int diffToHigh = high-q.getHigh5();
					int diffToHigh2 = high-hOpen;
					if (diffToHigh<=minPips*10
							&& diffToHigh2>=100
							&& h>=h1 && h<=h2
							){//testeo valido del high anterior
						mode = 1;
						//System.out.println("[TESTED HIGH] "+highH+" "+high+" "+h+" "+q.getHigh5()+" || "+hOpen);
					}
				}
				//la hora anterior fue un minimo
				if (lowH==h-1){
					int diffToLow = q.getLow5()-low;
					int diffToLow2 = hOpen-low;
					if (diffToLow<=minPips*10
							&& diffToLow2>=100
							&& h>=h1 && h<=h2
							){
						mode = -1;
						System.out.println("[TESTED LOW] "+DateUtils.datePrint(cal)+" "+lowH+" "+low+" "+h+" "+q.getLow5()+" || "+hOpen);
					}
				}
			}else if (mode==1){//para ir a high
				if (q.getLow5()<=hOpen && q.getHigh5()>=hOpen
						&& dayTrades<maxTrades
						){ //SHORT triggered at hOpen
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, hOpen,hOpen-10*tp, high, false);
					
					int pips = hOpen-qm.getClose5();
					if (pips>=0){
						winPips+=pips;
						wins++;
						//System.out.println("[SHORT WIN] "+DateUtils.datePrint(cal));
					}else{
						lostPips += -pips;
						losses++;
						//System.out.println("[SHORT LOST] "+DateUtils.datePrint(cal)+" || "+q.toString()+" || "+qm.getHigh5()+" "+qm.getLow5());
					}
					dayTrades++;
				}
			}else if (mode==-1){//para ir a low
				if (q.getHigh5()>=hOpen && q.getLow5()<=hOpen
						&& dayTrades<maxTrades
						){ //LONG triggered at hOpen
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, hOpen,hOpen+10*tp, low, false);
				
					int pips = qm.getClose5()-hOpen;
					if (pips>=0){
						winPips+=pips;
						wins++;
						//System.out.println("[LONG WIN] "+DateUtils.datePrint(cal));
					}else{
						lostPips += -pips;
						losses++;
						System.out.println("[LONG LOST] "+DateUtils.datePrint(cal)+" || "+q.toString()+" || "+qm.getHigh5()+" "+qm.getLow5());
					}
					dayTrades++;
				}
			}
		}
		
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg =(winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+tp
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.12.24.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2016.01.01_2016.12.26.csv";
		
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
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			for (int y1=2016;y1<=2016;y1+=1){
				int y2 = y1+0;
				for (int h1=16;h1<=16;h1++){
					int h2 = 23;
					for (int maxTrades=1;maxTrades<=1;maxTrades++){
						for (int minPips=8;minPips<=8;minPips++){
							for (int tp=5;tp<=5;tp++){
								for (int dayWeek1 = Calendar.MONDAY;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
									int dayWeek2 = dayWeek1+4;
									TooSlow123.doTrade("", data, maxMins, y1, y2,h1, h2, dayWeek1, dayWeek2,maxTrades,tp,minPips,false);
								}
							}
						}
					}
				}
				
			}
		}
		

	}

}
