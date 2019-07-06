package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.Quote;

public class SplitData {
	
	int hour = -1;
	ArrayList<Quote> data1 = new ArrayList<Quote>() ;
	ArrayList<Quote> data2 = new ArrayList<Quote>() ;
	
	public int getHour() {
		return hour;
	}
	public void setH(int hour) {
		this.hour = hour;
	}
	public ArrayList<Quote> getData1() {
		return data1;
	}
	public void setData1(ArrayList<Quote> data1) {
		this.data1 = data1;
	}
	public ArrayList<Quote> getData2() {
		return data2;
	}
	public void setData2(ArrayList<Quote> data2) {
		this.data2 = data2;
	}
	
	/**
	 * Primer array desde inicio hasta h, inclusive
	 * @param data
	 */
	public static SplitData split(ArrayList<Quote> data,int hSplit){
		
		SplitData split = new SplitData();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h<=hSplit){
				Quote q1 = new Quote();
				q1.copy(q);
				split.getData1().add(q1);
			}else{
				Quote q2 = new Quote();
				q2.copy(q);
				split.getData2().add(q2);
			}
		}
		
		return split;
	}
	
	
}
