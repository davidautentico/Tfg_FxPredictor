package drosa.experimental.moya;

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

public class TestMoya {

	
	public static void doTrade(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int minPips,
			int comm,
			boolean debug
			){
		
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
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
		int lastPrice = 0;
		int lastOpen = 0;
		int totalProfit = 0;
		int trades=0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			
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
			
			
			int diff = q1.getClose5()-q1.getOpen5();
			//if (lastOpen>0) diff = q1.getClose5()-lastOpen;
			
			if (diff>=minPips*10){
				
				if (mode==0){
					//entramos short
					if (debug)
					System.out.println(DateUtils.datePrint(cal1)
							+" || "+q1.getOpen5()+" "+q1.getClose5()
							+" || "+diff+" || SHORT "+q.getOpen5());
					
					if (h>=h1 && h<=h2){
						mode = 1;
						//lastPrice = q.getOpen5();
						lastPrice = q1.getClose5();
						lastOpen =  q1.getClose5();
					}
				}else{
					int profit = 0;
					if (mode==1){
						profit = q.getClose5()-lastPrice-comm;
						trades++;
					}else if (mode==-1){
						profit = lastPrice-q.getClose5()-comm;
						trades++;
					}
					
					totalProfit += profit;
					if (debug)
					System.out.println(DateUtils.datePrint(cal1)
							+" || "+q1.getOpen5()+" "+q1.getClose5()
							+" || "+diff+" || SHORT "+q.getOpen5()+" || "+profit+" || "+totalProfit);
					
					
					if (h>=h1 && h<=h2){
						mode = 1;
						//lastPrice = q.getOpen5();
						lastPrice = q1.getClose5();
						lastOpen =  q1.getClose5();
					}else{
						mode = 0;
					}
				}
				
			}else if (diff<=-minPips*10){
				if (mode==0){
					//entramos short
					if (debug)
					System.out.println(DateUtils.datePrint(cal1)
							+" || "+q1.getOpen5()+" "+q1.getClose5()
							+" || "+diff+" || LONG "+q.getOpen5());
					mode = -1;
					lastPrice = q1.getClose5();
					lastOpen =  q1.getClose5();
				}else{
					int profit = 0;
					if (mode==1){
						profit = q.getClose5()-lastPrice-comm;
						trades++;
					}else if (mode==-1){
						profit = lastPrice-q.getClose5()-comm;
						trades++;
					}
					
					totalProfit += profit;
					if (debug)
					System.out.println(DateUtils.datePrint(cal1)
							+" || "+q1.getOpen5()+" "+q1.getClose5()
							+" || "+diff+" || LONG "+q.getOpen5()+" || "+profit+" || "+totalProfit);
					
					if (h>=h1 && h<=h2){
						mode = -1;
						//lastPrice = q.getOpen5();
						lastPrice = q1.getClose5();
						lastOpen =  q1.getClose5();
					}else{
						mode = 0;
					}
				}
				
			}
			
		}
		System.out.println(minPips+" || "
				+trades+" "+totalProfit+" "+PrintUtils.Print2dec(totalProfit*0.1/trades, false)
				);
	}
	
	
	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\gbpUSD_UTC_1 Min_Bid_2009.01.01_2016.12.24.csv";
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.12.26.csv";
		
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
			
			System.out.println("total data: "+data.size());
			
			for (int minPips=3;minPips<=30;minPips++){
				TestMoya.doTrade("", data, null, 2004, 2016, 16, 23,minPips,0,false);
			}
		}

	}

}
