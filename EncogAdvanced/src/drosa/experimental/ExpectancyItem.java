package drosa.experimental;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class ExpectancyItem {
	
	int period = -1;
	int h = 0;
	double tp = -1;
	double sl = -1;
	int totalTrades = 0;
	double exp = 0.0;
	double winPer = 0.0;
	double maxRisk = 0.0;
	double profitPerTrade = 0.0;
	
	
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public double getTp() {
		return tp;
	}
	public void setTp(double tp) {
		this.tp = tp;
	}
	public double getSl() {
		return sl;
	}
	public void setSl(double sl) {
		this.sl = sl;
	}
	public double getExp() {
		return exp;
	}
	public void setExp(double exp) {
		this.exp = exp;
	}
		
	public int getTotalTrades() {
		return totalTrades;
	}
	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}
		
	
	public double getProfitPerTrade() {
		return profitPerTrade;
	}
	public void setProfitPerTrade(double profitPerTrade) {
		this.profitPerTrade = profitPerTrade;
	}
	public double getMaxRisk() {
		return maxRisk;
	}
	public void setMaxRisk(double maxRisk) {
		this.maxRisk = maxRisk;
	}
	public double getWinPer() {
		return winPer;
	}
	public void setWinPer(double winPer) {
		this.winPer = winPer;
	}
	public String toString(){
		return this.period+" "+this.h+" "+(int)this.tp+" "+(int)this.sl+" "+this.totalTrades
				+" "+PrintUtils.Print2(winPer)
				+" "+PrintUtils.Print2(exp);
	}
	public static ArrayList<ExpectancyItem> calculateExp(ArrayList<MaxMinItem> arrayMaxMin,
			ArrayList<TradeResultSimple> tradeResults,int maxPeriod,int begin,int end,double tp,double sl){
		
		ArrayList<ExpectancyItem> array = new ArrayList<ExpectancyItem>();
		for (int i=0;i<=(maxPeriod)*24+23;i++) array.add(null);	
		int end2 = end;
		if (end>tradeResults.size()-1) end2 = tradeResults.size()-1;
		ArrayList<Integer> totalTrades = new ArrayList<Integer>(); 
		ArrayList<Integer> totalWins = new ArrayList<Integer>();
		for (int j=0;j<=23;j++){ totalTrades.add(0);totalWins.add(0);}
		
		for (int period = 12; period<=maxPeriod;period+=12){
			for (int j=0;j<=23;j++){ totalTrades.set(j, 0);totalWins.set(j, 0);}
			for (int i=begin;i<=end2;i++){
	  			if (i-1>arrayMaxMin.size()-1) break;	  			
	  			TradeResultSimple res = tradeResults.get(i);
	  			MaxMinItem maxMin = arrayMaxMin.get(i-1); 
	  			int h = res.getOpenCal().get(Calendar.HOUR_OF_DAY);	
  				if (maxMin.getMax()>=period){  					
  					if (res.getSellResult()==1){
  						int tw = totalWins.get(h);
  						totalWins.set(h, tw+1);
  					}
  					int tt = totalTrades.get(h);
					totalTrades.set(h, tt+1);
  				}else if (Math.abs(maxMin.getMin())>=period){  					
  					if (res.getBuyResult()==1){
  						int tw = totalWins.get(h);
  						totalWins.set(h, tw+1);
  					}
  					int tt = totalTrades.get(h);
					totalTrades.set(h, tt+1);
  				}		  				
			}//begin
			
			for (int j=0;j<=23;j++){
				int h = j;
				int tt = totalTrades.get(j);
				int tw = totalWins.get(j);
				double comm = 1.4;
		  		double winPer = tw*100.0/tt;
		  		//System.out.println("win: "+tt +" "+tw);
		  		double exp = (winPer*tp/100.0)-((100.0-winPer)/100.0)*sl-comm;
		  		ExpectancyItem item = new ExpectancyItem();
		  		item.setH(h);
		  		item.setPeriod(period);
		  		item.setSl(sl);
		  		item.setTp(tp);
		  		item.setExp(exp);
		  		item.setTotalTrades(tt);
		  		array.set(period*24+h, item);//period-0, period-1,....period-23
			}
		}
		
		
		return array;
	}
	
	public static ArrayList<ExpectancyItem> calculateExp2(ArrayList<QuoteShort> data,
			int maxPeriod,int begin,int end,int tp,int sl,double comm,boolean debug){
		
		ArrayList<ExpectancyItem> array = new ArrayList<ExpectancyItem>();
		for (int i=0;i<=(maxPeriod)*24+23;i++) array.add(null);			
		for (int period = 12; period<=maxPeriod;period+=12){ //para cada periodo	
			ArrayList<QuoteShort> maxMin = TradingUtils.calculateMaxMinByBarShort(data,period);	
			for (int j=0;j<=23;j++){ //para cada hora
				int h = j;				
		  		double exp = TestNewHighsLowsGeneric.testHighLow(data, maxMin, begin, end, h, h, tp, sl, period,0.5,comm,debug);
		  		ExpectancyItem item = new ExpectancyItem();
		  		item.setH(h);
		  		item.setPeriod(period);
		  		item.setSl(sl);
		  		item.setTp(tp);
		  		item.setExp(exp);
		  		item.setTotalTrades(-1);
		  		array.set(period*24+h, item);//period-0, period-1,....period-23
		  		//System.out.println(item.toString());
			}
		}				
		return array;
	}
	
}
