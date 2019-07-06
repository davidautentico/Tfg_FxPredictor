package drosa.indicators;

import java.util.ArrayList;

import java.util.List;

import drosa.finances.Quote;
import drosa.utils.PrintUtils;

public class SMA {


	public static double getValue(List<Quote> data,int begin,int end,boolean close) {
		// TODO Auto-generated method stub
		
		double sum=0.0;
		for (int i=begin;i<=end;i++){
			if (close){
				sum+=data.get(i).getClose();
			}else{
				sum+=data.get(i).getAdjClose();
			}
					
		}
		return sum/(end-begin+1);
	}
	
	public static double getValue(ArrayList<Quote> data,int pos,int period,boolean close) {
		// TODO Auto-generated method stub
		
		double sum=0.0;
		int end = pos;
		int begin = pos-period+1;
		if (begin<0)
			begin = 0;
		
		//System.out.println("primero y ultimo para la media "+period+"-> "+begin+" "+end+
		//			" "+PrintUtils.Print(data.get(begin).getClose())+
		//			" "+PrintUtils.Print(data.get(end).getClose()));
		for (int i=begin;i<=end;i++){
			if (close){
				sum+=data.get(i).getClose();
			}else{
				sum+=data.get(i).getAdjClose();
			}
					
		}
		return sum/(end-begin+1);
	}

}
