package drosa.experimental.volatility;

import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class MeasuringVolatility {

	
	public static ArrayList<Double> calculateVolatility(ArrayList<QuoteShort> data){
		
		ArrayList<Double> volatility = new ArrayList<Double>();
		
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q0 = data.get(i);
			int atr1 = (int) ((q1.getHigh5()-q1.getLow5())*0.1);
			int atr0 = (int) ((q0.getHigh5()-q0.getLow5())*0.1);
			System.out.println(atr1+" "+atr0);
		}
		
		return volatility;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m63   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2009.01.01_2015.05.27.csv";
		String path5m64   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.06.05.csv";
		
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= null;
		ArrayList<Quote> dataS 		= null;
		String path5m = path5m64;
		System.out.println(path5m);
		String provider ="";
		if (path5m.contains("pepper")){
			dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
			dataS 		= dataI;
			provider="pepper";
		}else{
			dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
			dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			provider="dukasc";
		}								
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 			  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data);
		
		MeasuringVolatility.calculateVolatility(dailyData);
		
	}

}
