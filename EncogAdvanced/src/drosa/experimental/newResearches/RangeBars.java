package drosa.experimental.newResearches;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.TradingUtils;

public class RangeBars {

	public static void createRangeBars(ArrayList<QuoteShort> data,ArrayList<QuoteShort> rangeBars,int pips,boolean debug){
		
		ArrayList<Double> sizes = new ArrayList<Double>();
		rangeBars.clear();
		
		int j = -1;
		QuoteShort actual = null;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			int open = q.getOpen5();
			int high = q.getHigh5();
			int low = q.getLow5();
			int close = q.getClose5();
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int size = q.getHigh5()-q.getLow5();
			
		
			if (actual==null){  //iniciamos con el open, lo único seguro..
				actual = new QuoteShort();
				actual.setCal(cal);
				actual.setOpen5(open);		
				actual.setHigh5(high);
				actual.setLow5(low);
				actual.setClose5(close);
			}else{
				if (q.getHigh5()>=actual.getHigh5()){
					actual.setHigh5(q.getHigh5());
				}
				if (q.getLow5()<=actual.getLow5()){
					actual.setLow5(q.getLow5());
				}
				actual.setClose5(close);
			}
			
			//si el tamaño es mayor se añade y se cierra
			int actualSize = actual.getHigh5()-actual.getLow5();
			if (actualSize>=pips*10){
				sizes.add(actualSize*0.1);
				rangeBars.add(actual);
				actual = new QuoteShort();
				actual.setCal(cal1);
				actual.setOpen5(close);
				actual.setHigh5(close);
				actual.setLow5(close);
				actual.setClose5(close);
			}
		}
		
		if (actual!=null)
			rangeBars.add(actual);
		
		if (debug){
			MathUtils.summary_mean_sd("15 || ", sizes);
		}

	}
	
	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2016.03.28.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";
		
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= null;
		ArrayList<Quote> dataS 		= null;
		for (int i = 0;i<=limit;i++){
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
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			
			ArrayList<QuoteShort> rangeBars = new ArrayList<QuoteShort>();

			RangeBars.createRangeBars(data,rangeBars ,10,true);
			
			
		}
	}

}
