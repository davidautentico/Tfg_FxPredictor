package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class PythonUtils {

	public static void main(String[] args) {
		
		String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2009.01.01_2019.01.27.csv";
		String pathEURUSD2 = path0+"EURUSD_clean.csv";
		
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		dataI 		= DAO.retrieveDataShort5m(pathEURUSD , DataProvider.DUKASCOPY_FOREX4);			
		TestLines.calculateCalendarAdjustedSinside(dataI);			
		dataS = TradingUtils.cleanWeekendDataS(dataI);  
		ArrayList<QuoteShort> data = dataS;
		
		QuoteShort.saveToDiskDetails(data, pathEURUSD2);
		
		System.out.println("finalizado");

	}

}
