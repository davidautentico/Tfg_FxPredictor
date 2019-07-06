package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class HighLows {
	
	public static void doTestYesterdayLowHigh(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int h1,int h2,
			int nbars,
			int sl
			){
		
		ArrayList<Integer> hl = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hl.add(0);
		int lastDay = -1;
		int actualHigh = -1;
		int actualLow = -1;
		int lastHigh = -1;
		int lastLow = -1;
		boolean canTest = true;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		int total = 0;
		int acc = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int actualLosses = 0;
		QuoteShort qm = new QuoteShort();
		for (int i=0;i<data.size()-nbars;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort qn = data.get(i+nbars);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay!=-1){
					
				}
				lastHigh = actualHigh;
				lastLow = actualLow;
				actualHigh = -1;
				actualLow = -1;
				canTest = true;
				lastDay = day;
			}
			
			
			int maxMin = maxMins.get(i);
			boolean doTrade = false;
			int diff = 0;
			if (canTest){
				if (lastHigh!=-1 && q.getHigh5()>=lastHigh){
					hl.set(h, hl.get(h)+1);
					if (h>=h1 && h<=h2){
						//System.out.println("maxMinH "+maxMin);
						acc += Math.abs(maxMin);
						total++;
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i+1, data.size()-1, q1.getOpen5()-30*10, q1.getOpen5()+sl*10, false);
						diff = q1.getOpen5()-qm.getClose5();
						doTrade = true;
					}
					canTest = false;
				}
				if (lastLow!=-1 && q.getLow5()<=lastLow){
					hl.set(h, hl.get(h)+1);
					if (h>=h1 && h<=h2){
						//System.out.println("maxMinL "+maxMin);
						acc += Math.abs(maxMin);
						total++;
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i+1, data.size()-1, q1.getOpen5()+30*10, q1.getOpen5()-sl*10, false);
						diff = qm.getClose5()-q1.getOpen5();
						doTrade = true;
					}
					canTest = false;
				}
			}
			
			if (doTrade){
				if (diff>=0){
					wins++;
					winPips += diff;
					actualLosses=0;
					//System.out.println("[WIN] "+h);
				}else{
					losses++;
					lostPips += -diff;
					actualLosses++;
					//if (actualLosses>=3)
						//System.out.println("[LOSS] "+h+" || "+actualLosses);
				}
			}
			
			if (q.getHigh5()>=actualHigh || actualHigh == -1) actualHigh = q.getHigh5();
			if (q.getLow5()<=actualLow || actualLow == -1) actualLow = q.getLow5();
		}
		
		String touches="";
		for (int i=0;i<=9;i++){
			touches += hl.get(i)+" "; 
		}
		
	
		double winPer = wins*100.0 / total;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/total;
		System.out.println(
				h1+" "+h2+" "+nbars+" "+sl
				+" || "+touches
				+" || "+total+" "+PrintUtils.Print2dec(acc*1.0/total, false)
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
	}

	public static void main(String[] args) throws Exception {
String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.06.28.csv";
		
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
			
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+9;
				for (int nbars=12;nbars<=12;nbars+=12){
					for (int sl=10;sl<=100;sl+=10){
						HighLows.doTestYesterdayLowHigh(data,maxMins,h1,h2,nbars,sl);
					}
				}
			}
		
		}//limit

	}

}
