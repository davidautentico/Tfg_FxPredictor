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

public class TestMaxReverse {
	
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int maxBars
			){
		//
		
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			int maxMin = maxMins.get(i-1);
			
			if (h>=h1 && h<=h2){
				int end = i+maxBars;
				if (end>data.size()-1) end = data.size()-1;
				int entry = q.getOpen5();
				int diff = 0;
				boolean isTraded = false;
				if (maxMin>=thr){					
					diff = entry-data.get(end).getClose5();
					isTraded = true;
				}else if (maxMin<=-thr){
					diff = data.get(end).getClose5()-entry;	
					isTraded = true;
				}
				
				if (isTraded){
					if (diff>=0){
						wins++;
						winPips+=diff;
					}else{
						losses++;
						lostPips+=-diff;
					}
				}
			}
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				
				h1+" "+h2+" "+thr+" "+maxBars
				+"  || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
	
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.09.20.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
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
			
			for (int y1=2003;y1<=2016;y1++){
				int y2 = y1+0;
				for (int h1=0;h1<=0;h1++){
					for (int h2=9;h2<=9;h2++){
						for (int thr=500;thr<=500;thr+=1){
							for (int maxBars=48;maxBars<=48;maxBars+=12){
								TestMaxReverse.doTest("", data, maxMins,y1, y2, h1, h2, thr, maxBars);
							}
						}
					}
				}
			}
		
		}

	}

}
