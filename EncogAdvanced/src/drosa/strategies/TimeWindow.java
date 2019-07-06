package drosa.strategies;

public class TimeWindow {

		int minHour=00;
		int minMin=00;
		int maxHour=23;
		int maxMin=59;
		
		public TimeWindow(int minH, int minM, int maxH, int maxM) {
			// TODO Auto-generated constructor stub
			this.minHour=minH;
			this.minMin=minM;
			this.maxHour=maxH;
			this.maxMin=maxM;
		}
		public TimeWindow() {
			// TODO Auto-generated constructor stub
		}
		public int getMinHour() {
			return minHour;
		}
		public void setMinHour(int minHour) {
			this.minHour = minHour;
		}
		public int getMinMin() {
			return minMin;
		}
		public void setMinMin(int minMin) {
			this.minMin = minMin;
		}
		public int getMaxHour() {
			return maxHour;
		}
		public void setMaxHour(int maxHour) {
			this.maxHour = maxHour;
		}
		public int getMaxMin() {
			return maxMin;
		}
		public void setMaxMin(int maxMin) {
			this.maxMin = maxMin;
		}
		public String getDescription() {
			// TODO Auto-generated method stub
			String str=this.minHour+":"+this.minMin+
				"->"+this.maxHour+":"+this.maxMin;
			return str;
		}
		public void init(int minH, int minM, int maxH, int maxM) {
			// TODO Auto-generated method stub
			this.minHour=minH;
			this.minMin=minM;
			this.maxHour=maxH;
			this.maxMin=maxM;
		}
		
		
}
