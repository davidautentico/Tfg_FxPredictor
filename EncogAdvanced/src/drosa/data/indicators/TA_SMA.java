package drosa.data.indicators;

import java.util.ArrayList;

import drosa.finances.Quote;

public class TA_SMA extends TAIndicator {

	int nBars;
	CandlePart part;
	
	public TA_SMA(ArrayList<Quote> data,CandlePart part,int nBars){
		this.data = data;
		this.nBars = nBars;
		this.part = part;
	}
	
	@Override
	public double[][] getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getValue(int index) {
		// TODO Auto-generated method stub
		
		int begin= index-nBars;
		double accum=0.0;
		int total=0;
		if (begin<0) begin=0;
		if (index==0){
			Quote q = data.get(index);
			if (part==CandlePart.OPEN)
				accum+=q.getOpen();
			if (part==CandlePart.HIGH)
				accum+=q.getHigh();
			if (part==CandlePart.LOW)
				accum+=q.getLow();
			if (part==CandlePart.CLOSE)
				accum+=q.getClose();
			return accum;
		}
		for (int i=begin;i<index;i++){
			Quote q = data.get(i);
			if (part==CandlePart.OPEN)
				accum+=q.getOpen();
			if (part==CandlePart.HIGH)
				accum+=q.getHigh();
			if (part==CandlePart.LOW)
				accum+=q.getLow();
			if (part==CandlePart.CLOSE)
				accum+=q.getClose();
			total++;
		}
		
		return accum/total;
	}

	@Override
	public int getMaxLag() {
		// TODO Auto-generated method stub
		return 0;
	}

}
