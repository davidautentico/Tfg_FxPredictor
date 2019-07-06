package drosa.phil;

import java.util.Calendar;

public class PriceTestResult {
	
	Calendar cal = Calendar.getInstance();
	Calendar closeTime = Calendar.getInstance();
	int win = 0;
	private int index;
	int maxPositive = 0;
	int maxNegative = 0;
	private int pipsDiff;
	private int lastDiff;
	

	public Calendar getCal() {
		return cal;
	}
		
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	
	
	public Calendar getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Calendar closeTime) {
		this.closeTime = closeTime;
	}
	
	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public void copy(PriceTestResult priceTestResult) {
		// TODO Auto-generated method stub
		this.win = priceTestResult.win;
		this.cal.setTimeInMillis(priceTestResult.getCal().getTimeInMillis());
		this.closeTime.setTimeInMillis(priceTestResult.getCloseTime().getTimeInMillis());
		this.index = priceTestResult.getIndex();
		this.maxPositive = priceTestResult.getMaxPositive();
		this.maxNegative = priceTestResult.getMaxNegative();
		this.pipsDiff 	 = priceTestResult.getPipsDiff();
		this.lastDiff    = priceTestResult.getLastDiff();
	}

	public void setIndex(int index) {
		// TODO Auto-generated method stub
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public int getMaxPositive() {
		return maxPositive;
	}

	public void setMaxPositive(int maxPositive) {
		this.maxPositive = maxPositive;
	}

	public int getMaxNegative() {
		return maxNegative;
	}

	public void setMaxNegative(int maxNegative) {
		this.maxNegative = maxNegative;
	}

	public void setPipsDiff(int pipsDiff) {
		// TODO Auto-generated method stub
		this.pipsDiff = pipsDiff;
	}

	public int getPipsDiff() {
		return pipsDiff;
	}

	public void setLastDiff(int lastDiff) {
		// TODO Auto-generated method stub
		this.lastDiff = lastDiff;
	}

	public int getLastDiff() {
		return lastDiff;
	}
	
	
	
	
}
