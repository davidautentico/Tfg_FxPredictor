package drosa.indicators;

import java.util.ArrayList;

import drosa.finances.Quote;

public class MinMax {

	public static int getValue(ArrayList<Quote> data, int current,int period){
		int result =-1;
		
		int begin = current-period+1;
		if (begin<=0)
			begin =0;
		
		int end = current;
		
		int min=-1;
		int max=-1;
		double minV=9999999;
		double maxV=-9999999;
		for (int i=begin;i<=end;i++){
			Quote q = data.get(i);
			if (q.getHigh()>=maxV){
				maxV=q.getHigh();
				max=i;
			}
			
			if (q.getLow()<=minV){
				minV=q.getLow();
				min=i;
			}
			
		}
		
		if (min==end){
			return 0;
		}
		
		if (max==end){
			return 1;
		}
		
		return result;
	}
}
