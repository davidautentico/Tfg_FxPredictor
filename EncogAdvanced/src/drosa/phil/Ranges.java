package drosa.phil;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.TradingUtils;

public class Ranges {
	
	
	public static void writeDailyRangeToDisk(String file5M,String outFile){
		
		ArrayList<Quote> dataI = DAO.retrieveData(file5M, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
  		
  		try{			
  			PrintWriter writer;		
			writer = new PrintWriter(outFile, "UTF-8");
  					
			Calendar cal = Calendar.getInstance();  		
			for (int i = 0;i<dailyData.size();i++){
				Quote q = dailyData.get(i);  		
				int range = TradingUtils.getPipsDiff(q.getHigh(), q.getLow());
				cal.setTime(q.getDate());
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
  				writer.println(DateUtils.datePrint(cal)
  							+","+range
  							);  											
  			}
			writer.close();  	
  		}catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  					
  	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		String file5M	=	"c:\\fxdata\\EURUSD_5 Mins_Bid_2006.01.01_2013.11.13.csv";
		System.out.println("writing data..");
		Ranges.writeDailyRangeToDisk(file5M, "c:\\fxdata\\EURUSD_ranges.txt");
		System.out.println("end..");
	}

}
