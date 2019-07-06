package drosa.data.indicators;

import java.util.ArrayList;

import drosa.finances.Quote;

public abstract class TAIndicator {

	ArrayList<Quote> data = null;
	
	public ArrayList<Quote> getData() {
		return data;
	}
	public void setData(ArrayList<Quote> data) {
		this.data = data;
	}
	
	public abstract double[][] getValue();
	public abstract double getValue(int index);
	public abstract int getMaxLag();
}
