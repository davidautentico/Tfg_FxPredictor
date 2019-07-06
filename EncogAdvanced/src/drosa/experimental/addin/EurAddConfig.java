package drosa.experimental.addin;

import java.util.ArrayList;

public class EurAddConfig {
	
	int h = -1;
	int thr = -1;
	int maxBars = -1;
	int tp = -1;
	int sl = -1;
	double stdFactor = -1;
	
	public EurAddConfig(int aH,int aThr,int aMaxBars,int aTp,int aSl,double aStdFactor){
		
		this.h = aH;
		this.thr = aThr;
		this.maxBars = aMaxBars;
		this.tp = aTp;
		this.sl = aSl;
		this.stdFactor = aStdFactor;
	}
	
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getThr() {
		return thr;
	}
	public void setThr(int thr) {
		this.thr = thr;
	}
	public int getMaxBars() {
		return maxBars;
	}
	public void setMaxBars(int maxBars) {
		this.maxBars = maxBars;
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
	public double getStdFactor() {
		return stdFactor;
	}
	public void setStdFactor(double stdFactor) {
		this.stdFactor = stdFactor;
	}

	public static void generateGenericConfigs(ArrayList<EurAddConfig> configs, int h1, int h2, int thr, int maxBars, int tp, int sl,
			double stdfactor) {
		// TODO Auto-generated method stub
		
		for (int i=h1;i<=h2;i++){
			EurAddConfig config = configs.get(i);
			if (config==null){
				config = new EurAddConfig(i,thr,maxBars,tp,sl,stdfactor);
				configs.set(i, config);
			}
			else{
				config.setParams(i,thr,maxBars,tp,sl,stdfactor);
			}
		}
		
	}

	private void setParams(int aH,int aThr,int aMaxBars,int aTp,int aSl,double aStdFactor){
		
		this.h = aH;
		this.thr = aThr;
		this.maxBars = aMaxBars;
		this.tp = aTp;
		this.sl = aSl;
		this.stdFactor = aStdFactor;
	}
	
	

}
