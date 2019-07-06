package drosa.phil;

import java.util.ArrayList;

public class LineArray {
	String id;
	ArrayList<PhilDay> pDays = new ArrayList<PhilDay>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<PhilDay> getpDays() {
		return pDays;
	}
	public void setpDays(ArrayList<PhilDay> pDays) {
		this.pDays = pDays;
	}
	
	public void addPhilDay(PhilDay pDay){
		pDays.add(pDay);
	}
	
}
