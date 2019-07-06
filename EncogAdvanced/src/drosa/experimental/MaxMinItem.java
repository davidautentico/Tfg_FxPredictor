package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class MaxMinItem {
	
	Calendar cal = Calendar.getInstance();
	int max = 0; //>0 highs <0 lows
	int min = 0; //>0 highs <0 lows
	
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	
	public void copy(MaxMinItem or){
		this.cal.setTimeInMillis(or.getCal().getTimeInMillis());
		this.max = or.getMax();
		this.min = or.getMin();
	}
	
	public static ArrayList<MaxMinItem> calculateMaxMin(ArrayList<Quote> data,int maxRetrace){
	
		ArrayList<MaxMinItem> array = new ArrayList<MaxMinItem>();
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTimeInMillis(q.getDate().getTime());
			int y = cal.get(Calendar.YEAR);
			int mn = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			int end = i-maxRetrace;
			if (end<0) end = 0;
			int begin= i-1;
			int lastMaxPeriod = 0;
			int maxPeriod = 0;
			int lastMinPeriod = 0;
			int minPeriod = 0;
			boolean canMax = true;
			boolean canMin = true;
			//System.out.println("begin end "+begin+" "+end);
			boolean exit = false;
			for (int j=begin;j>=end && !exit;j--){//hacia atrás
				Quote qj = data.get(j);
				if (y==2014 && mn==Calendar.JULY && d==22){
					//System.out.println("q y qj: "+PrintUtils.Print(q)+" "+PrintUtils.Print(qj));
				}
				if (canMax && qj.getHigh()<q.getHigh()){
					maxPeriod++;
					//if (y==2014) System.out.println("maxPeriod: "+maxPeriod);
				}
				
				if (canMin && qj.getLow()>q.getLow()){
					minPeriod--;
					//if (y==2014) System.out.println("minPeriod: "+minPeriod);
				}
				
				if (lastMaxPeriod == maxPeriod){//si no cambia no hay nuevo mínimo / máximo
					canMax = false;
				}
				if (lastMinPeriod == minPeriod){//si no cambia no hay nuevo mínimo / máximo
					canMin = false;
				}
				
				if (!canMax && !canMin){
					MaxMinItem mm = new MaxMinItem();
					mm.getCal().setTimeInMillis(q.getDate().getTime());
					mm.setMax(maxPeriod);
					mm.setMin(minPeriod);
					array.add(mm);
					//if (y==2014) System.out.println("añadido: "+mm.toString());
					exit = true;
				}
				
				lastMaxPeriod = maxPeriod;
				lastMinPeriod = minPeriod;
			}
			if (!exit){
				MaxMinItem mm = new MaxMinItem();
				mm.getCal().setTimeInMillis(q.getDate().getTime());
				mm.setMax(maxPeriod);
				mm.setMin(minPeriod);
				array.add(mm);
			}
		}
		
		return array;
	}
	
	public static ArrayList<MaxMinItem> filter(ArrayList<MaxMinItem> data,int filter){
		ArrayList<MaxMinItem> array = new ArrayList<MaxMinItem>();
		
		for (int i=0;i<data.size();i++){
			MaxMinItem item = data.get(i);
			if (Math.abs(item.getMax())>=filter || Math.abs(item.getMax())>=filter){
				MaxMinItem item2 = new MaxMinItem();
				item2.copy(item);
				array.add(item2);
			}
		}
		
		return array;
	}
	
	public String toString(){
		
		String res = DateUtils.datePrint(this.cal)+" "+this.max+" "+this.min;
		return res;
	}

}
