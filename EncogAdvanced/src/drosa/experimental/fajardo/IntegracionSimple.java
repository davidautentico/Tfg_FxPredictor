package drosa.experimental.fajardo;

import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class IntegracionSimple {

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathGBPUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 1;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= null;
		ArrayList<Quote> dataS 		= null;
		for (int i = 1;i<=limit;i++){
			String path = paths.get(i);			
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="dukasc";
			}				
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			
			int days = 0;
			int avg = 0;
			ArrayList<Double> diffs = new ArrayList<Double>();
			
			for (int thr = 0;thr<=100;thr+=10){
				diffs.clear();
				int totalUp = 0;
				int totalDown = 0;
				int totalUpUp = 0;
				int totalDownDown = 0;
				for (int d=1;d<dailyData.size();d++){
					QuoteShort q1 = dailyData.get(d-1); 
					QuoteShort q = dailyData.get(d); 
					int diffRaw0 = q.getClose5()-q.getOpen5();
					int diffRaw1 = q1.getClose5()-q1.getOpen5();
					int diff = Math.abs(q.getClose5()-q.getOpen5()); 
					avg+=Math.abs(q.getClose5()-q.getOpen5());
					days++;
					
					diffs.add(diff*0.1);
					
					
					if (diffRaw1>=thr*10){
						totalUp++;
						if (diffRaw0<0){
							totalUpUp++;
						}
					}else if (diffRaw1<=-thr*10){
						totalDown++;
						if (diffRaw0>0){
							totalDownDown++;
						}
					}
				}//for d
			
				double perUpUp = totalUpUp*100.0/totalUp;
				double perDownDown = totalDownDown*100.0/totalDown;
				System.out.println(
						thr
						+" "+PrintUtils.Print2(avg*0.1/days, false)
						+" || "+totalUp+" "+PrintUtils.Print2(perUpUp, false)
						+" || "+totalDown+" "+PrintUtils.Print2(perDownDown, false)
						);
				//MathUtils.summary_mean_sd("", diffs);
			}//thr
		}//limit
	}//main

}
