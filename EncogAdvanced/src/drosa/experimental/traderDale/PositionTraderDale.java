package drosa.experimental.traderDale;

public class PositionTraderDale {
	
	String fileName;
	String pair;
	int day;
	int month;
	int year;
	int mode;
	int entry;
	int tp;
	int sl;
	
	public PositionTraderDale(String aFileName,
			int aDay,int aMonth,int aYear,int aMode,int aEntry,int aTp,int aSl){
		
		this.fileName = aFileName;
		this.day = aDay;
		this.month = aMonth;
		this.year = aYear;
		this.entry = aEntry;
		this.mode = aMode;
		this.tp = aTp;
		this.sl = aSl;
		
	}
	
	

	public String getPair() {
		return pair;
	}



	public void setPair(String pair) {
		this.pair = pair;
	}



	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getEntry() {
		return entry;
	}

	public void setEntry(int entry) {
		this.entry = entry;
	}

	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}

	public int getSl() {
		return sl;
	}

	public void setSl(int sl) {
		this.sl = sl;
	}

	
	
}
