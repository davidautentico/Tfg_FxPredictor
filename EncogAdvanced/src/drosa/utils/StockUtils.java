package drosa.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import drosa.finances.Quote;
import drosa.finances.SplitData;
import drosa.finances.Stock;


public class StockUtils {
			
	public static String constructURL(String symbol, Calendar start,
			Calendar end, TimeFrame tf)
	{
			String tfStr ="d";
			
			switch(tf){
				case DAILY: 
					tfStr = "d";
					break;
						
				case WEEKLY: 
					tfStr = "w";
					break;
					
				case MONTHLY: 
					tfStr = "m";
					break;
			}
			return "http://ichart.finance.yahoo.com/table.csv" + "?s="+
					symbol + "&a="+ Integer.toString(start.get(Calendar.MONTH))+
					"&b=" + Integer.toString(start.get(Calendar.DAY_OF_MONTH))+
					"&c=" + Integer.toString(start.get(Calendar.YEAR))+
					"&d=" + Integer.toString(end.get(Calendar.MONTH))+
					"&e=" + Integer.toString(end.get(Calendar.DAY_OF_MONTH))+
					"&f=" + Integer.toString(end.get(Calendar.YEAR))+
					"&g="+tfStr+"&ignore=.csv";
	}
	
	public static String constructSplitURL(String symbol, Calendar start,
			Calendar end)
	{
			
			return "http://ichart.finance.yahoo.com/x" + "?s="+
					symbol + "&a="+ Integer.toString(start.get(Calendar.MONTH))+
					"&b=" + Integer.toString(start.get(Calendar.DAY_OF_MONTH))+
					"&c=" + Integer.toString(start.get(Calendar.YEAR))+
					"&d=" + Integer.toString(end.get(Calendar.MONTH))+
					"&e=" + Integer.toString(end.get(Calendar.DAY_OF_MONTH))+
					"&f=" + Integer.toString(end.get(Calendar.YEAR))+
					"&g=v&y=0&z=30000";
	}
			
	public static List<Quote> getEodCSVData(String symbol,GregorianCalendar gc2,GregorianCalendar gc,TimeFrame tf)
	{
		String urlString = constructURL(symbol,gc2,gc,tf);
		System.out.println(urlString);
		return UrlUtils.urlGet(urlString);
	}
	
	public static List<Quote> getEodCSVData(String symbol,GregorianCalendar gc2,GregorianCalendar gc,boolean eodAdjusted)
	{
		String urlString = constructURL(symbol,gc2,gc,TimeFrame.DAILY);
		System.out.println(urlString);
		return UrlUtils.urlGetEodData(urlString,eodAdjusted);
	}
	
	public static List<SplitData> getSplitCSVData(String symbol,GregorianCalendar gc2,GregorianCalendar gc)
	{
		String urlString = constructSplitURL(symbol,gc2,gc);
		System.out.println(urlString);
		return UrlUtils.urlGetSplitData(urlString);
	}
	
		
	private static String constructIntradayURL(String symbol) {
		
		//http://finance.yahoo.com/d/quotes.csv?s=SAN.MC+REP.MC+TEF.MC&f=snl1d1t1cv
		return "http://finance.yahoo.com/d/quotes.csv?s="+
				symbol + "&f=sl1d1t1v";
	}
	
	private static String constructIntradayURL(List<String> symbolList) {
		// TODO Auto-generated method stub
		String list;
		
		list = symbolList.get(0);
		for (int i=1;i<symbolList.size();i++){
			list+="+"+symbolList.get(i);
		}
		return "http://finance.yahoo.com/d/quotes.csv?s="+list + "&f=sl1d1t1v";
	}

	public static  List<String> getIntradayData(String symbol)
	{
		String urlString = constructIntradayURL(symbol);
		System.out.println(urlString);
		return UrlUtils.urlGet2(urlString);
	}
	
	public static void storeStockIntoBBDD(Stock s){
		
	}

	public static List<String> getIntradayData(List<String> symbolList) {
		// TODO Auto-generated method stub
		String urlString = constructIntradayURL(symbolList);
		System.out.println(urlString);
		return UrlUtils.urlGet2(urlString);
		
	}

	
}
