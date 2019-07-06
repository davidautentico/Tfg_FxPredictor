package drosa.experimental.CoreStrategies;

import java.util.Calendar;

public class LegInfo {

	int index1 = 0;
	int index2 = 0;	
	int hleg = -1;
	int pips = 0;
	double factor = 0;
	int mode = 0;
	
	int index1x = -1;
	int index2x = -1;
	int index3x = -1;
	int index4x = -1;
	int index5x = -1;
	
	
	
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getIndex1() {
		return index1;
	}
	public void setIndex1(int index1) {
		this.index1 = index1;
	}
	public int getIndex2() {
		return index2;
	}
	public void setIndex2(int index2) {
		this.index2 = index2;
	}
	public int getPips() {
		return pips;
	}
	public void setPips(int pips) {
		this.pips = pips;
	}
			
	public double getFactor() {
		return factor;
	}
	public void setFactor(double factor) {
		this.factor = factor;
	}
	public int getHleg() {
		return hleg;
	}
	public void setHleg(int hleg) {
		this.hleg = hleg;
	}
	
	
	
	public int getIndex1x() {
		return index1x;
	}
	public void setIndex1x(int index1x) {
		this.index1x = index1x;
	}
	public int getIndex2x() {
		return index2x;
	}
	public void setIndex2x(int index2x) {
		this.index2x = index2x;
	}
	public int getIndex3x() {
		return index3x;
	}
	public void setIndex3x(int index3x) {
		this.index3x = index3x;
	}
	public int getIndex4x() {
		return index4x;
	}
	public void setIndex4x(int index4x) {
		this.index4x = index4x;
	}
	public int getIndex5x() {
		return index5x;
	}
	public void setIndex5x(int index5x) {
		this.index5x = index5x;
	}
	public void setParams(int mode,int index1, int index2, int pips, int hleg,double factor) {
		// TODO Auto-generated method stub
		this.mode = mode;
		this.index1 = index1;
		this.index2 = index1;
		this.pips = pips;
		this.hleg = hleg;
		this.factor = factor;
	}
	public void setFactorIndexes(int index1x, int index2x, int index3x, int index4x, int index5x) {
		this.index1x = index1x;
		this.index2x = index2x;
		this.index3x = index3x;
		this.index4x = index4x;
		this.index5x = index5x;
		
	}
	public int getFactorIdx(int idx) {
		// TODO Auto-generated method stub
		
		if (idx==1) return this.index1x;
		if (idx==2) return this.index2x;
		if (idx==3) return this.index3x;
		if (idx==4) return this.index4x;
		if (idx==5) return this.index5x;
		
		return 0;
	}
	
	
	
}
