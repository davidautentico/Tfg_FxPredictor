package drosa.strategies;

import java.util.ArrayList;
import java.util.Date;

public class QuoteClassification {
	
	Date date;
	MarketStructure ms = MarketStructure.NONE;
	int interval =0;
	ArrayList<Double> factors = new ArrayList<Double>();
	
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public MarketStructure getMs() {
		return ms;
	}
	public void setMs(MarketStructure ms) {
		this.ms = ms;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public ArrayList<Double> getFactors() {
		return factors;
	}
	public void setFactors(ArrayList<Double> factors) {
		this.factors = factors;
	}
	public void addFactor(double factor,int interval){
		if (factors.size()<interval){
			int diff=interval-factors.size();
			for (int i=0;i<diff;i++){
				factors.add(-1.0);
			}
		}
		if ((interval-1)>=0)
			factors.set(interval-1, factor);		
	}
	
	public void Copy(QuoteClassification q) {
		// TODO Auto-generated method stub
		this.date = q.date;
		this.interval = q.interval;		
		this.ms = q.ms;
		this.factors.clear();
		for (int i=0;i<q.getFactors().size();i++){
			if (this.factors.size()<(i+1)){
				this.factors.add(q.getFactors().get(i));
			}else{
				this.factors.set(i, q.getFactors().get(i));
			}
		}
	}
}
