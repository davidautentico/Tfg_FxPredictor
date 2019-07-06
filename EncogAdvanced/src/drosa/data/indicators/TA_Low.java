package drosa.data.indicators;

import java.util.ArrayList;

import drosa.finances.Quote;

public class TA_Low extends TAIndicator {

	int pos;
	
	public TA_Low(ArrayList<Quote> data,int n){
		this.data = data;
		pos = n;
	}
	

	public int getPos() {
		return pos;
	}



	public void setPos(int pos) {
		this.pos = pos;
	}



	@Override
	public double[][] getValue() {
		
		if (data==null) return null;
		if (pos<0) return null;
		if (data.size()-pos<0) return null;
		
		double[][] d = new double[data.size()-pos][1];
		int begin=pos;
		int m=0;
		for (int i=begin;i<data.size();i++){
			Quote q = data.get(i-pos);
			d[m++][0] =q.getLow();
		}
		return d;
	}



	@Override
	public int getMaxLag() {
		// TODO Auto-generated method stub
		return pos;
	}


	@Override
	public double getValue(int index) {
		// TODO Auto-generated method stub
		if (data==null) return 0;
		
		double d = 0;
		int begin=index+pos;
		if (begin<0) begin=0;
		if (begin>data.size()-1) begin = data.size()-1;
		d = data.get(begin).getLow();
		
		return d;
	}

}
