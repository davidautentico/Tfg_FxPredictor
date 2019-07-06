package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.experimental.zznbrum.TrendClass;
import drosa.finances.QuoteShort;

public class TestNoTestedLines {
	
	public void doTestHour(
		ArrayList<QuoteShort> data,
		int y1,int y2,
		int h1,int h2,
		int minPips,
		int debug
		){
	
	//ArrayList<Double> trendsIndex = new ArrayList<Double>();
	
	int mode = 0;
	int index1 = 0;
	int index2 = 0;
	int index3 = 0;
	int lastDay = -1;
	int dayOpen = -1;
	ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
	Calendar cal = Calendar.getInstance();
	Calendar cal1 = Calendar.getInstance();
	Calendar cal2 = Calendar.getInstance();
	Calendar cal3 = Calendar.getInstance();
	QuoteShort.getCalendar(cal1, data.get(0));
	int dayOrder = 0;
	int accOpen = 0;
	int countOpen = 0;
	
	int count20 = 0;
	int count40 = 0;
	int count60 = 0;
	int count80 = 0;
	int count100 = 0;
	int count120 = 0;
	int count140 = 0;
	int count160 = 0;
	int doValue = -1;
	ArrayList<Integer> entries = new ArrayList<Integer>();
	ArrayList<Integer> modes = new ArrayList<Integer>();
	ArrayList<Integer> tested = new ArrayList<Integer>();
	for (int i=0;i<data.size();i++){
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		//System.out.println(q.toString());
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int min = cal.get(Calendar.MINUTE);
		int year = cal.get(Calendar.YEAR);
		if (year<y1 || year>y2) continue;
		if (day!=lastDay){
			dayOpen = q.getOpen5();
			
			lastDay = day;
			dayOrder=0;
			doValue = q.getOpen5();
		}
		
		if (h>=h1 
				&& h<=h2
				&& min==0
				){
			int entry = q.getOpen5();
			int tpValue = entry-minPips;
			mode = -1;
			if (doValue>entry){
				tpValue = entry+minPips;
				mode = 1;
			}
			entries.add(entry);
			modes.add(mode);
			tested.add(0);
		}
	}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
