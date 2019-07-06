package drosa.phil;

import java.util.ArrayList;

public class TmaArray {
	String id;
	ArrayList<TMA> tmas = new ArrayList<TMA>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<TMA> getTmas() {
		return tmas;
	}
	public void setTmas(ArrayList<TMA> tmas) {
		this.tmas = tmas;
	}
	
	public void addTMA(TMA tma){
		tmas.add(tma);
	}
}
