package drosa.indicators;

import java.util.ArrayList;
import java.util.List;

import drosa.finances.Quote;

public class LowPrice extends Indicator {

	int delay= 0;
	
	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	@Override
	public double getValue(List<Quote> data,int pos) {
		// TODO Auto-generated method stub
		if ((pos -delay)<0) return -9999.9;
		return data.get(pos-delay).getLow();
	}
}
