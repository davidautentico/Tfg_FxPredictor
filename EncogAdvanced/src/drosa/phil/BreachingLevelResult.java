package drosa.phil;

import java.util.Calendar;

import drosa.utils.DateUtils;

public class BreachingLevelResult {

	Calendar day = null;
	int pipsBreaching=0;
	int targetPips=0;
	int maxPips=0;
	int success=-1;//1,0,-1
	LineType line = null;
	
	
	public Calendar getDay() {
		return day;
	}
	public void setDay(Calendar day) {
		this.day = day;
	}
	public int getPipsBreaching() {
		return pipsBreaching;
	}
	public void setPipsBreaching(int pipsBreaching) {
		this.pipsBreaching = pipsBreaching;
	}
	public int getTargetPips() {
		return targetPips;
	}
	public void setTargetPips(int targetPips) {
		this.targetPips = targetPips;
	}
	public int getMaxPips() {
		return maxPips;
	}
	public void setMaxPips(int maxPips) {
		this.maxPips = maxPips;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int res) {
		this.success = res;
	}
	public LineType getLine() {
		return line;
	}
	public void setLine(LineType line) {
		this.line = line;
	}
	
	public void copy(BreachingLevelResult res){
		this.line 			= res.line;
		this.maxPips 		= res.maxPips;
		this.pipsBreaching 	= res.pipsBreaching;
		this.targetPips 	= res.targetPips;
		this.success		= res.success;
		day = Calendar.getInstance();
		this.day.setTimeInMillis(res.getDay().getTimeInMillis());
	}
	
	public String toString(){
		return DateUtils.getYMD(day)+" "+this.line.name()+" "+this.pipsBreaching+" "+this.targetPips
				+" "+this.maxPips+" "+this.success;
	}

}
