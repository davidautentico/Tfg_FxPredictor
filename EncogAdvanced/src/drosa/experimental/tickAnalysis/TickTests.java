package drosa.experimental.tickAnalysis;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.data.DataUtils;
import drosa.data.TickQuote;
import drosa.utils.PrintUtils;

public class TickTests {

	public static void main(String[] args) {
		
		String fileName12 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.01.01_2012.12.31.csv";
		String fileName13 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2013.12.31.csv";
		String fileName14 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.01.01_2014.12.31.csv";
		String fileName15 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2015.12.31.csv";
		String fileName36 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2016.12.31.csv";
		String fileName56 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2016.12.31.csv";
		String fileName ="c:\\fxdata\\EURUSD_Ticks_2019.01.01_2019.03.20.csv";
		//String fileName ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.10.01_2016.11.01.csv";
		ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileName,1);
		
		System.out.println(fileName36+" "+data.size());
		Calendar cal = Calendar.getInstance();
		ArrayList<Integer> spreads = new ArrayList<Integer>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			spreads.add(0);
			counts.add(0);
		}
		for (int i=0;i<data.size();i++){
			TickQuote t = data.get(i);
			TickQuote.getCalendar(cal, t);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			min=0;
			int spreadDiff = Math.abs(t.getAsk()-t.getBid());
			
			spreads.set(h, spreads.get(h)+spreadDiff);
			counts.set(h, counts.get(h)+1);
		}
		
		for (int h=0;h<=23;h++){
			int hour = h/60; 
			int min = h-hour*60;
			double avg = spreads.get(h)*0.1/counts.get(h);
			System.out.println(h+" "+PrintUtils.Print2dec(avg,false)+" "+counts.get(h));
		}
		/*for (int i=0;i<data.size();i++){
			TickQuote t = data.get(i);
			TickQuote.getCalendar(cal, t);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int spreadDiff = Math.abs(t.getAsk()-t.getBid());
			
			spreads.set(h*60+min, spreads.get(h*60+min)+spreadDiff);
			counts.set(h*60+min, counts.get(h*60+min)+1);
		}
		
		for (int h=0;h<=23*60+59;h++){
			int hour = h/60; 
			int min = h-hour*60;
			double avg = spreads.get(h)*0.1/counts.get(h);
			System.out.println(h+" "+hour+" "+min+" "+PrintUtils.Print2dec(avg,false));
		}*/
		
		
	}

}
