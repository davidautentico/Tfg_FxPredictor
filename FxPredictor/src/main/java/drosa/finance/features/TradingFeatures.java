package drosa.finance.features;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finance.classes.QuoteShort;
import drosa.finance.utils.MathUtils;

public class TradingFeatures {

	public static ArrayList<Integer> getHours(ArrayList<QuoteShort> data) {
		// TODO Auto-generated method stub
		
		ArrayList<Integer> hours = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			hours.add(h);
		}
		
		return null;
	}

	public static ArrayList<Integer> getAtrArray(ArrayList<QuoteShort> data) {
	
		ArrayList<Integer> atrArr = new ArrayList<Integer>();
		int lastDay = -1;
		int high=-1;
		int low = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				high = -1;
				low = -1;
				lastDay = -1;
			}
			
			if (high==-1 || q.getOpen5()>=high) high = q.getOpen5();
			if (low==-1 || q.getOpen5()<=low) low = q.getOpen5();
			
			int atr=0;
			if (high!=-1 && low!=-1){
				atr = high-low;
			}
			atrArr.add(atr);
		}
		
		
		return atrArr;
	}

	public static ArrayList<Integer> getSmaDiff(ArrayList<QuoteShort> data,
			int n) {
				
		ArrayList<Integer> smaDiff = new ArrayList<Integer>();
		
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			
			values.add(q.getOpen5());
			int avg = (int) MathUtils.average(values, values.size()-n, values.size()-1);
			
			int diff = q.getOpen5()-avg;
			smaDiff.add(diff);
		}
		return null;
	}

	public static ArrayList<Integer> calculateMaxMinByBarShortAbsoluteInt(
			ArrayList<QuoteShort> data) {
		
		ArrayList<Integer> maxMins = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			int maxMin = 0;
			//maxMin.setExtra(0);
			int modeH = 0;
			int modeL = 0;
			int nbarsH = 0;
			int nbarsL = 0;
			if (i%20000==0){
				//System.out.println("calculados "+i);
			}
			//QuoteShort qmax = TradingUtils.getMaxMinShort(data, i-170000, i-1);
			for (int j=i-1;j>=0;j--){
				QuoteShort qj = data.get(j);
				boolean isHigh = false;
				boolean isLow = false;
				
				if (q.getHigh5()>qj.getHigh5()){
					isHigh = true;
				}
				if (q.getLow5()<qj.getLow5()){
					isLow = true;
				}
				
				//System.out.println();
				
				if (modeH==0 || modeH==1){
					if (isHigh){
						modeH=1;
						nbarsH++;
					}else{
						modeH=-1;
					}
				}
				if (modeL==0 || modeL==1){
					if (isLow){
						modeL=1;
						nbarsL++;
					}else{
						modeL=-1;
					}
				}
				
				if (!isHigh) modeH = -1;
				if (!isLow)  modeL = -1;
				
				if (!isHigh && !isLow) break;
				if (modeH==-1 && modeL==-1) break;
			}
			
			if (nbarsH>=nbarsL) maxMin=nbarsH;
			if (nbarsH<nbarsL) maxMin=-nbarsL;
			maxMins.add(maxMin);
		}
		return maxMins;
	}

}
