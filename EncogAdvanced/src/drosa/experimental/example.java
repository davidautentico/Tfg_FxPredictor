package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.Quote;

public class example {
	
	public void test(ArrayList<Quote> data,ArrayList<Quote> ATR,Calendar from,Calendar to,int day1,int day2,int h1,int h2,
			double sl,double tp){
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//test tiempo
			if (cal.getTimeInMillis()<from.getTimeInMillis() || cal.getTimeInMillis()>to.getTimeInMillis()) continue;
			if (h<h1 || h> h2) continue;
			if (day<day1 || day>day2) continue;
			
			
		}
		
	}
}
