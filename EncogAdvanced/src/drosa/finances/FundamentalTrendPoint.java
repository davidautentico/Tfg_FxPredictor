package drosa.finances;

import java.util.Date;

import drosa.utils.FundamentalType;

public class FundamentalTrendPoint {

		Date date;
		float value;
		FundamentalType type;
		int lowInterval;
		int highInterval;
		
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public float getValue() {
			return value;
		}
		public void setValue(float value) {
			this.value = value;
		}
		public FundamentalType getType() {
			return type;
		}
		public void setType(FundamentalType type) {
			this.type = type;
		}
		public int getLowInterval() {
			return lowInterval;
		}
		public void setLowInterval(int lowInterval) {
			this.lowInterval = lowInterval;
		}
		public int getHighInterval() {
			return highInterval;
		}
		public void setHighInterval(int highInterval) {
			this.highInterval = highInterval;
		}
		
		
		
		
}
