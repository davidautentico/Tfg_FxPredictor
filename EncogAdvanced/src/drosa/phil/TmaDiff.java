package drosa.phil;

import java.util.Calendar;

public class TmaDiff {
	Calendar cal = Calendar.getInstance();
	int diffUp=0;
	int diffDown=0;
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	public int getDiffUp() {
		return diffUp;
	}
	public void setDiffUp(int diffUp) {
		this.diffUp = diffUp;
	}
	public int getDiffDown() {
		return diffDown;
	}
	public void setDiffDown(int diffDown) {
		this.diffDown = diffDown;
	}
	
}
