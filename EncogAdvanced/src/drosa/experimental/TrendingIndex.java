package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrendingIndex {

	public static void calculateTrendingIndex(ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int h,
			int nbars,
			int l
			){
		
		double trendAvg = 0;
		int total = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int hd = cal.get(Calendar.HOUR_OF_DAY);
			
			QuoteShort maxMin = maxMins.get(i);
			if (maxMin.getExtra()>=nbars && hd==h){//high
				QuoteShort qmax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int mx = qmax.getHigh5()-q1.getOpen5();
				int mn = q1.getOpen5()-qmax.getLow5();
				trendAvg+=(mx-mn);
				total++;
			}
			if (maxMin.getExtra()<=-nbars && hd==h){//low
				QuoteShort qmax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int mx = qmax.getHigh5()-q1.getOpen5();
				int mn = q1.getOpen5()-qmax.getLow5();
				trendAvg+=(mn-mx);
				total++;
			}
		}
		System.out.println(h+" "+nbars+" "+l+" "+total+" "+PrintUtils.Print2(trendAvg/total));
	}
	
	public static void calculateTrendingIndex2(ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int h,
			int nbars,
			int l
			){
		
		double trendAvg = 0;
		int total = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-1-l;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int hd = cal.get(Calendar.HOUR_OF_DAY);
			
			QuoteShort maxMin = maxMins.get(i);
			if (maxMin.getExtra()>=nbars && hd==h){//high
				QuoteShort qmax = data.get(i+l);
				int gain = qmax.getClose5()-q1.getOpen5();
				trendAvg+=gain;
				total++;
			}
			if (maxMin.getExtra()<=-nbars && hd==h){//low
				QuoteShort qmax = data.get(i+l);
				int gain = q1.getOpen5()-qmax.getClose5();
				trendAvg+=gain;
				total++;
			}
		}
		System.out.println(h+" "+nbars+" "+l+" "+total
				+" "+PrintUtils.Print2(trendAvg/total)
				+" "+PrintUtils.Print2(trendAvg)
				);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.31.csv";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
  		ArrayList<Quote> hourlyData 	= ConvertLib.convert(data5m, 12);
  		//ArrayList<Quote> dailyData 	= ConvertLib.createDailyData(data5m);
  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		ArrayList<QuoteShort> hourlyDataS    = QuoteShort.convertQuoteArraytoQuoteShort(hourlyData);
		//ArrayList<QuoteShort> dailyDataS  = QuoteShort.convertQuoteArraytoQuoteShort(dailyData);
		
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		
		ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
		for (int nbars=50;nbars<=10000;nbars+=50){
			for (int l=1000;l<=1000;l+=100){
				for (int h=16;h<=16;h++){
					//TrendingIndex.calculateTrendingIndex(data, maxMins, h,nbars,l);
					TrendingIndex.calculateTrendingIndex2(data, maxMins, h,nbars,l);
				}
			}
		}
	}

}
