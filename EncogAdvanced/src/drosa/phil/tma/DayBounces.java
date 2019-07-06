package drosa.phil.tma;

import java.util.ArrayList;
import java.util.Calendar;

public class DayBounces {

	Calendar cal = Calendar.getInstance();
	
	ArrayList<Bounce> bounces = new ArrayList<Bounce> ();

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

	public ArrayList<Bounce> getBounces() {
		return bounces;
	}

	public void setBounces(ArrayList<Bounce> bounces) {
		this.bounces = bounces;
	}
	
	
}
