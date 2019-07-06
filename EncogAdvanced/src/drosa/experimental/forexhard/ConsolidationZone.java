package drosa.experimental.forexhard;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.TradingUtils;

public class ConsolidationZone {
	
	int barBegin = 0;
	int barEnd = 0;
	int lowValue = 0;
	int highValue = 0;
	
	
	public int getBarBegin() {
		return barBegin;
	}
	public void setBarBegin(int barBegin) {
		this.barBegin = barBegin;
	}
	public int getBarEnd() {
		return barEnd;
	}
	public void setBarEnd(int barEnd) {
		this.barEnd = barEnd;
	}
	public int getLowValue() {
		return lowValue;
	}
	public void setLowValue(int lowValue) {
		this.lowValue = lowValue;
	}
	public int getHighValue() {
		return highValue;
	}
	public void setHighValue(int highValue) {
		this.highValue = highValue;
	}
	
	
	public static ArrayList<ConsolidationZone> detectConsolidationZones(ArrayList<QuoteShort> data,int begin,int end,int minBars,int maxPips){
		
		ArrayList<ConsolidationZone> czs = new ArrayList<ConsolidationZone>();
		
		if (begin<=minBars-1) begin = minBars-1;
		if (end>data.size()-1) end = data.size()-1;
		
		Calendar cal = Calendar.getInstance();
		Calendar calMinBars = Calendar.getInstance();
		int count = 0;
		int actualH = -1;
		int actualL = -1;
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort qBars = data.get(i-(minBars-1));
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(calMinBars, qBars);
			
			QuoteShort maxMin = TradingUtils.getMaxMinShort(data, i-(minBars-1),i);
			int czBegin = i-(minBars-1);
			int czEnd = i;
			
			int pipsDiff = maxMin.getHigh5()-maxMin.getLow5();
			boolean approved = true;
			if (actualH!=-1)
				if (actualH>=maxMin.getHigh5() && maxMin.getLow5()>=actualL) approved = false;
			if (pipsDiff*0.1<=maxPips && pipsDiff*0.1>0.0){
				if (approved){
					actualH = maxMin.getHigh5();
					actualL = maxMin.getLow5();
					System.out.println("[DETECTED] "+DateUtils.datePrint(calMinBars)+" "+DateUtils.datePrint(cal)+" "+czBegin+" "+czEnd+" "+pipsDiff+" "+actualH+" "+actualL);
					count++;
				}else{
					System.out.println("[EXTENDED] "+DateUtils.datePrint(cal)+" "+czBegin+" "+czEnd+" "+pipsDiff+" "+actualH+" "+actualL);
				}
			}else{
				System.out.println("[FAILED] "+DateUtils.datePrint(calMinBars)+" "+DateUtils.datePrint(cal)+" "+czBegin+" "+czEnd+" "+pipsDiff+" "+maxMin.getHigh5()+" "+maxMin.getLow5());				
			}
		}
		
		System.out.println("total: "+count);
		
		
		return czs;
	}
	

}
