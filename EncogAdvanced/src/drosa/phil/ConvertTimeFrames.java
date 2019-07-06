package drosa.phil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class ConvertTimeFrames {
	
	public static void saveToDisk(ArrayList<Quote> data,String fileName){
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			String text = PrintUtils.Print4dec(q.getOpen())+','+
					 PrintUtils.Print4dec(q.getHigh())+','+
					 PrintUtils.Print4dec(q.getLow())+','+
					 PrintUtils.Print4dec(q.getClose());
			DataCleaning.appendToFile(fileName, text);
		}
	}
	
	public static void saveToDisk(ArrayList<Quote> data,String fileName,int hl,int hh){
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (hl<=h && h<=hh){
				String text = PrintUtils.Print4dec(q.getOpen())+','+
					 PrintUtils.Print4dec(q.getHigh())+','+
					 PrintUtils.Print4dec(q.getLow())+','+
					 PrintUtils.Print4dec(q.getClose());
				DataCleaning.appendToFile(fileName, text);
			}
		}
	}
	
	
	public static void printData(ArrayList<Quote> data){
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			
			System.out.println(DateUtils.datePrint(q.getDate())+" "+PrintUtils.Print(q));
		}
	}
	
	

	public static void main(String[] args) {
		String path = "C:\\fxdata\\";
		String symbol="GBPUSD";
		String file5M = path+symbol+"_5 Mins_Bid_2010.12.31_2013.10.01.csv";
		
		ArrayList<Quote> dataI 		= DAO.retrieveData(file5M, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 		= TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> data10m 	= ConvertLib.convert(data, 2);
  		ArrayList<Quote> data15m 	= ConvertLib.convert(data, 3);
  		ArrayList<Quote> data30m 	= ConvertLib.convert(data, 6);
  		ArrayList<Quote> data60m 	= ConvertLib.convert(data, 12);
  		ArrayList<Quote> data120m 	= ConvertLib.convert(data, 24);
  		ArrayList<Quote> data240m 	= ConvertLib.convert(data, 48);
  		ArrayList<Quote> data480m 	= ConvertLib.convert(data, 96);
  		ArrayList<Quote> data1440m 	= ConvertLib.convert(data, 288);

  		//ConvertTimeFrames.printData(data1440m);
  		for (int i=0;i<=16;i++){
  			int hl = i;
  			int hh = i+7;
  			ConvertTimeFrames.saveToDisk(data, path+"EURUSD_5m"+"_"+hl+"_"+hh+"_.csv",hl,hh);
  			ConvertTimeFrames.saveToDisk(data15m, path+"EURUSD_15m"+"_"+hl+"_"+hh+"_.csv",hl,hh);
  			ConvertTimeFrames.saveToDisk(data60m, path+"EURUSD_60m"+"_"+hl+"_"+hh+"_.csv",hl,hh);
  		}
  		/*ConvertTimeFrames.saveToDisk(data, path+"EURUSD_5m.csv");
  		ConvertTimeFrames.saveToDisk(data10m, path+"EURUSD_10m.csv");
  		ConvertTimeFrames.saveToDisk(data15m, path+"EURUSD_15m.csv");
  		ConvertTimeFrames.saveToDisk(data30m, path+"EURUSD_30m.csv");
  		ConvertTimeFrames.saveToDisk(data60m, path+"EURUSD_60m.csv");
  		ConvertTimeFrames.saveToDisk(data120m, path+"EURUSD_120m.csv");
  		ConvertTimeFrames.saveToDisk(data240m, path+"EURUSD_240m.csv");
  		ConvertTimeFrames.saveToDisk(data480m, path+"EURUSD_480m.csv");
  		ConvertTimeFrames.saveToDisk(data1440m, path+"EURUSD_1440m.csv");*/
  		
	}

}
