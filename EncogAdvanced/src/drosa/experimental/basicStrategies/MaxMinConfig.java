package drosa.experimental.basicStrategies;

public class MaxMinConfig {
	
	int h;
	int thr1=-1;
	int thr2=-1;
	int maxbars=-1;
	int tp;
	int sl;
	boolean active = false;
	int filter = 0;
	int hClose = -1;	
	double riskFactor;
	
	
	public int gethClose() {
		return hClose;
	}
	public void sethClose(int hClose) {
		this.hClose = hClose;
	}
	public int getThr1() {
		return thr1;
	}
	public void setThr1(int thr1) {
		this.thr1 = thr1;
	}
	public int getThr2() {
		return thr2;
	}
	public void setThr2(int thr2) {
		this.thr2 = thr2;
	}
	public int getMaxbars() {
		return maxbars;
	}
	public void setMaxbars(int maxbars) {
		this.maxbars = maxbars;
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
	public double getRiskFactor() {
		return riskFactor;
	}
	public void setRiskFactor(double riskFactor) {
		this.riskFactor = riskFactor;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
		
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	
	
	
	public int getFilter() {
		return filter;
	}
	public void setFilter(int filter) {
		this.filter = filter;
	}
	public String toString(){
		return h+" "+thr1+" "+thr2+" "+maxbars+" "+tp+" "+sl;
		
	}
	
	public void setconfig(int h,int thr1, int thr2, int maxbars, int tp, int sl, boolean isActive) {
		// TODO Auto-generated method stub
		this.h = h;
		this.thr1 = thr1;
		this.thr2 = thr2;
		this.maxbars = maxbars;
		this.tp = tp;
		this.sl = sl;
		this.active = isActive;
	}
	
	public void setconfig2(int h,int hClose,int thr1, int thr2, int maxbars, int tp, int sl, boolean isActive) {
		// TODO Auto-generated method stub
		this.h = h;
		this.hClose = hClose;
		this.thr1 = thr1;
		this.thr2 = thr2;
		this.maxbars = maxbars;
		this.tp = tp;
		this.sl = sl;
		this.active = isActive;
	}
	
	
}
