package drosa.phil.levels;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.phil.LineType;

public class LevelResult {
	
	Calendar cal = Calendar.getInstance();
	LineType level = LineType.DO;
	double levelEntry =1.0000;
	int offset = 2;
	int maxPips = 10;
	int bePips = 5;
	int result = 0; //-1,0,1
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	public LineType getLevel() {
		return level;
	}
	public void setLevel(LineType level) {
		this.level = level;
	}
	public double getLevelEntry() {
		return levelEntry;
	}
	public void setLevelEntry(double levelEntry) {
		this.levelEntry = levelEntry;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getMaxPips() {
		return maxPips;
	}
	public void setMaxPips(int maxPips) {
		this.maxPips = maxPips;
	}
	public int getBePips() {
		return bePips;
	}
	public void setBePips(int bePips) {
		this.bePips = bePips;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public void copy(LevelResult levelResult) {
		// TODO Auto-generated method stub
		this.result = levelResult.getResult();
	}
	
	
	
}
