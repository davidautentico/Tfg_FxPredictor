package drosa.memory;

import java.io.File;
import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.TradeResultSimple;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class CalculateMemory {

	public static ArrayList<TradeResultSimple> readTradeResuls(String fileName){		
		ArrayList<TradeResultSimple> tradeResults = TradeResultSimple.readFromDisk(fileName);
		return tradeResults;
	}
	
	public static ArrayList<Quote> readQuotes(String path){		
		ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
		return dataI;
	}
	
	public static ArrayList<QuoteShort> readQuotesShort(String path){		
		ArrayList<QuoteShort> data 		= DAO.retrieveDataShort(path, DataProvider.DUKASCOPY_FOREX);
		return data;
	}
	
	public static ArrayList<QuoteShort> readQuotesShort2(String path){	
		File f1 = new File(path+"EURUSD_UTC_1 Sec_Bid_2008.12.01_2010.01.01.csv");
		File f2 = new File(path+"EURUSD_UTC_1 Sec_Bid_2009.12.01_2011.01.01.csv");
		File f3 = new File(path+"EURUSD_UTC_1 Sec_Bid_2010.12.01_2012.01.01.csv");
		File f4 = new File(path+"EURUSD_UTC_1 Sec_Bid_2011.12.01_2013.01.01.csv");
		File f5 = new File(path+"EURUSD_UTC_1 Sec_Bid_2012.12.01_2014.01.01.csv");
		File f6 = new File(path+"EURUSD_UTC_1 Sec_Bid_2013.12.01_2014.10.01.csv");
		
		ArrayList<File> files = new ArrayList<File>();
		files.add(f1);files.add(f2);files.add(f3);files.add(f4);files.add(f5);files.add(f6);
		
		ArrayList<QuoteShort> data = DAO.retrieveDataShort(files, DataProvider.DUKASCOPY_FOREX);
		return data;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path0 = "c:\\fxdata\\";
		String path = "c:\\fxdata\\TRADES EURUSD 30_09_2014\\";
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		String path1m2009 = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2014.10.10.csv";
		String path1s2009 = "c:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2009.12.01_2011.01.01.csv";
		int tp = 15;
		int sl = 15;
		String fileName = path+"trades_EURUSD_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)+".csv";				
		File f = new File(fileName);
		if (!f.exists()){
			return;
		}
		Sizeof.runGC ();
		long before = Sizeof.usedMemory();
		//ArrayList<Quote> data = readQuotes(path5m);
		//ArrayList<Quote> data = readQuotes(path1m2009);
		//ArrayList<QuoteShort> data = readQuotesShort(path1m2009);
		//ArrayList<QuoteShort> data = readQuotesShort(path1s2009);
		//ArrayList<QuoteShort> data = readQuotesShort2(path0);
		//QuoteShort.saveToDisk(data, "c:\\quoteshort.csv");
		ArrayList<QuoteShort> data0 = QuoteShort.readFromDisk("c:\\quoteshort2.csv");
		//QuoteShort.saveToDiskClean(data,"c:\\quoteshort2.csv");
		ArrayList<QuoteShort> dataS =  TestLines.calculateCalendarAdjustedShort(data0);
		ArrayList<QuoteShort> data = TradingUtils.cleanWeekendDataShort(dataS);
		QuoteShort.saveToDiskClean(data,"c:\\EURUSD_2009_01_01_2014_09_30.csv");
		//ArrayList<TradeResultSimple> data = readTradeResuls(fileName);
		
		/*ArrayList<QuoteShort> array = new ArrayList<QuoteShort>();
		//ArrayList<Quote> array = new ArrayList<Quote>();
		for (int i=0;i<10000000;i++){
			QuoteShort q = new QuoteShort();
			//Quote q = new Quote();
			array.add(q);
		}*/
		Sizeof.runGC ();
		long after = Sizeof.usedMemory();
		double KB = (after-before)/1024;
		double MB = KB/1024;
		System.out.println(after-before+" bytes "+KB+" KB "+MB+" MB "+" quotes: "+data.size());
	}

}
