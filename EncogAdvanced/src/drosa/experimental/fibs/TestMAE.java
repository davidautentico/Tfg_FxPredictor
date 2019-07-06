package drosa.experimental.fibs;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.SuperStrategy;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMAE {
	
	
	public static void testMAE(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int nBarh,
			int begin,int end,String hours,
			int futureBars
			){
		
		double accRatio = 0;
		int total = 0;
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		if (begin<=0) begin = 0;
		if (end>=data.size()-1-futureBars) end = data.size()-1-futureBars;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			int allowed = allowedHours.get(h);
			int maxMin = maxMins.get(i);
			//int nBarh	= nBars.get(h);	
			int futureValue = data.get(i+futureBars).getClose5();
			if (allowed==1){
				if (maxMin>=nBarh){ //interesa mas bajo SHORT
					int diff = q.getClose5()-futureValue;
					accRatio+=diff;
					total++;
				}else if (maxMin<=-nBarh){ //Interesa mas alto LONG
					int diff = futureValue-q.getClose5();
					accRatio+=diff;
					total++;
				}
			}			
		}
		
		double avgRatio = accRatio/total;
		System.out.println(
				begin
				+" "+end
				+" "+nBarh
				+" "+futureBars
				+" || "+ PrintUtils.Print2(avgRatio)
				);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.03.12.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2015.03.12.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathEURJPY);//paths.add(pathUSDJPY);
		paths.add(pathGBPJPY);
		
		int limit = 0;
		for (int i= 0;i<=limit;i++){
			String path = paths.get(i);			
			ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			System.out.println("data: "+data.size());
			int begin = 400000;
			int end = data.size();
			begin = 1;
			//end = 400000;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int futureBars=1;futureBars<=50000;futureBars+=10){
				for (int nBar=100;nBar<=100;nBar+=10){			
					//TestMAE.testMAE(data, maxMins, nBar, begin, end, "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23", futureBars);
					TestMAE.testMAE(data, maxMins, nBar, begin, end, "0 1 2 3 4 5 6 7 8 9", futureBars);
				}
			}
		}
	}

}
