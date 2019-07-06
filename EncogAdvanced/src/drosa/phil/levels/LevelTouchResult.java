package drosa.phil.levels;

import drosa.phil.LineType;

public class LevelTouchResult {

		boolean touched = true;
		LineType level = LineType.NONE;
		double value = 1.0000;
		boolean up = true;
		private double originalLevelValue;
		
		public boolean isTouched() {
			return touched;
		}
		public void setTouched(boolean touched) {
			this.touched = touched;
		}
		public LineType getLevel() {
			return level;
		}
		public void setLevel(LineType level) {
			this.level = level;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public boolean isUp() {
			return up;
		}
		public void setUp(boolean up) {
			this.up = up;
		}
		
		public double getOriginalLevelValue() {
			return originalLevelValue;
		}
		public void setOriginalLevelValue(double value) {
			// TODO Auto-generated method stub
			this.originalLevelValue = value;
			
		}						
}
