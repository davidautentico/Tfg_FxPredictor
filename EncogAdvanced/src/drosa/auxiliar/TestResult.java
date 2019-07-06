package drosa.auxiliar;

public class TestResult {
	
	int positiveCases=0;
	int totalCases=0;
	
	public int getPositiveCases() {
		return positiveCases;
	}
	public void setPositiveCases(int positiveCases) {
		this.positiveCases = positiveCases;
	}
	public int getTotalCases() {
		return totalCases;
	}
	public void setTotalCases(int totalCases) {
		this.totalCases = totalCases;
	}
	
	public void incTotalCases() {
		totalCases++;
	}
	public void incPositiveCases() {
		positiveCases++;
	}
	
	
}
