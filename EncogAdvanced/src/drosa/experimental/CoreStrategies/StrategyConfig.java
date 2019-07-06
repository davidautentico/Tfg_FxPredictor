package drosa.experimental.CoreStrategies;

public class StrategyConfig {
	
	boolean enabled = false;
	int hour;
	int thr;
	int tp;
	int sl;
	int barsBack;
	int maxBars;
	
	public StrategyConfig(){
		
	}
	
	public StrategyConfig(StrategyConfig aConfig){
		this.enabled	= aConfig.enabled;
		this.hour 		= aConfig.hour;
		this.thr 		= aConfig.thr;
		this.tp 		= aConfig.tp;
		this.sl 		= aConfig.sl;
		this.barsBack 	= aConfig.barsBack;
		this.maxBars 	= aConfig.maxBars;
	}
	
	public void copy (StrategyConfig aConfig){
		this.enabled	= aConfig.enabled;
		this.hour 		= aConfig.hour;
		this.thr 		= aConfig.thr;
		this.tp 		= aConfig.tp;
		this.sl 		= aConfig.sl;
		this.barsBack 	= aConfig.barsBack;
		this.maxBars 	= aConfig.maxBars;
	}
	
	public void setParams(int h,int thr,int tp,int sl,boolean enabled){
		this.hour = h;
		this.thr = thr;
		this.tp = tp;
		this.sl = sl;
		this.enabled = enabled;
	}
	public void setParams(int h,int thr,int tp,int sl,int maxBars,boolean enabled){
		this.hour = h;
		this.thr = thr;
		this.tp = tp;
		this.sl = sl;
		this.maxBars = maxBars;
		this.enabled = enabled;
	}
	public void setParams(int h,int thr,int tp,int sl,int maxBars,int barsBack,boolean enabled){
		this.hour = h;
		this.thr = thr;
		this.tp = tp;
		this.sl = sl;
		this.maxBars = maxBars;
		this.barsBack = barsBack;
		this.enabled = enabled;		
	}
		
	public int getMaxBars() {
		return maxBars;
	}
	public void setMaxBars(int maxBars) {
		this.maxBars = maxBars;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getThr() {
		return thr;
	}
	public void setThr(int thr) {
		this.thr = thr;
	}
	public int getTp() {
		return tp;
	}
	public void setTp(int tp) {
		this.tp = tp;
	}
	public int getSl() {
		return sl;
	}
	public void setSl(int sl) {
		this.sl = sl;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getBarsBack() {
		return barsBack;
	}
	public void setBarsBack(int barsBack) {
		this.barsBack = barsBack;
	}
			
	public String toString(){
		return this.hour + " " + this.enabled
				 + " " + this.thr 
		 + " " + this.tp 
		 + " " + this.sl
		 + " " + this.barsBack 
		 + " " + this.maxBars;
	}

	public void multiplyBars(int factor) {
		this.barsBack *= factor;
		this.maxBars *=factor;
		this.thr *= factor;		
	}
}
