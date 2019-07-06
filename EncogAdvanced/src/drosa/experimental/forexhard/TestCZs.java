package drosa.experimental.forexhard;

import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class TestCZs {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String fileName5 = "_UTC_5 Mins_Bid_2014.12.31_2015.04.15.csv";
		
		//String fileName30 = "_UTC_30 Mins_Bid_2014.12.31_2015.04.15.csv";			
		String fileName15 = "_UTC_15 Mins_Bid_2012.12.31_2015.04.15.csv";
		//String fileName30 = "_UTC_30 Mins_Bid_2012.12.31_2015.04.15.csv";
		//String fileName15 = "_UTC_15 Mins_Bid_2007.12.31_2015.04.15.csv";
		//String fileName30 = "_UTC_30 Mins_Bid_2007.12.31_2015.04.15.csv";
		//String fileName15 = "_UTC_15 Mins_Bid_2003.05.04_2015.04.15.csv";
		//String fileName30 = "_UTC_30 Mins_Bid_2003.05.04_2015.04.15.csv";
		
		String pathEURUSD = "c:\\fxdata\\EURUSD"+fileName15;
		String pathGBPUSD = "c:\\fxdata\\EURUSD"+fileName15;
		String pathUSDJPY = "c:\\fxdata\\EURUSD"+fileName15;
		String pathAUDUSD = "c:\\fxdata\\EURUSD"+fileName15;
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
				
		int limit = paths.size()-1;
		int initial = 0;
		limit       = 0;
		for (int i=initial;i<=limit;i++){
			String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
			ArrayList<Quote> dataI 		= DAO.retrieveData(paths.get(i), DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			//System.out.println("total data: "+data.size());
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
			ConsolidationZone.detectConsolidationZones(data, 0, data.size(), 21, 28);
		}
		
	}

}
