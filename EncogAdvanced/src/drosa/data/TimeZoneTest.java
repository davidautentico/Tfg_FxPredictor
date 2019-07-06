package drosa.data;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import drosa.DAO.DAO;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TimeZoneTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			String fileName= "C:\\Users\\david\\Documents\\trading\\data\\dukascopy\\5m\\EURUSD_5 Mins_Bid_2003.08.11_2013.04.10.csv";
			
			ArrayList<Quote> data = DAO.retrieveData(fileName,3);
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				cal.setTime(q.getDate());
				if (cal.get(Calendar.YEAR)>=2013){
					Date date = q.getDate();
					DateFormat localDf = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);  
			        DateFormat gmtDf = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);  
			        DateFormat nyDf = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);  
			        DateFormat gmt_3 = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);  
			        Calendar cal3 = Calendar.getInstance();
			        gmtDf.setTimeZone(TimeZone.getTimeZone("GMT"));  
			        nyDf.setTimeZone(TimeZone.getTimeZone("America/New_York"));  
			        gmt_3.setTimeZone(TimeZone.getTimeZone("GMT+3")); 
			        cal3.setTime(date);
			        cal3.add(Calendar.HOUR_OF_DAY,3);
			        
			        System.out.println(i+" local : " + localDf.format(date));  
			        System.out.println(i+" GMT   : " + gmtDf.format(date));  
			        System.out.println(i+" NY    : " + nyDf.format(date)); 
			        System.out.println(i+" GMT+3    : " + gmt_3.format(date)); 
			        System.out.println(i+" cal GMT+3    : " + DateUtils.datePrint(cal3.getTime())); 
					System.out.println(i+" "+PrintUtils.getOHLC(q));
				}
			}
			
			
	}

}
