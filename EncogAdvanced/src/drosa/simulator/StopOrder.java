package drosa.simulator;

public class StopOrder {

		StopType type;
		double value;
		
		public StopType getType() {
			return type;
		}
		public void setType(StopType type) {
			this.type = type;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}		
}
