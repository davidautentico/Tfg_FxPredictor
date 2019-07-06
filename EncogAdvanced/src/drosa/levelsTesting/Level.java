package drosa.levelsTesting;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.Quote;

public class Level {
	
	LevelType levelType = null;
	Calendar cal = null;
	Quote value = null;
	
	public LevelType getLevelType() {
		return levelType;
	}
	public void setLevelType(LevelType levelType) {
		this.levelType = levelType;
	}
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	public Quote getValue() {
		return value;
	}
	public void setValue(Quote value) {
		this.value = value;
	}
	
	
	/*public ArrayList<Level> calculateLevels(ArrayList<Quote> dailyData,ArrayList<Quote> weeklyData,LevelType levelType){
		ArrayList<Level> levels = new ArrayList<Level>();
		
		if (levelType==LevelType.DAY_HIGH || LevelType.DAY_LOW)
			return getLevels(dailyData,levelType);
		if (levelType==LevelType.WEEK_HIGH || LevelType.WEEK_LOW)
			return getLevels(weeklyData,levelType);
		
		return levels;
	}
	*/
}
