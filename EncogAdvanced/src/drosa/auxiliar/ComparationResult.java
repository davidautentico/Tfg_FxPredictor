package drosa.auxiliar;

public class ComparationResult {

		boolean typeCompatible;
		int barsDifference;
		float absoluteSpeedDiff;		
		float similarity;
				
		public boolean getTypeCompatible() {
			return typeCompatible;
		}
		public void setTypeCompatible(boolean typeCompatible) {
			this.typeCompatible = typeCompatible;
		}
		public int getBarsDifference() {
			return barsDifference;
		}
		public void setBarsDifference(int barsDifference) {
			this.barsDifference = barsDifference;
		}
		public float getAbsoluteSpeedDiff() {
			return absoluteSpeedDiff;
		}
		public void setAbsoluteSpeedDiff(float absoluteSpeedDiff) {
			this.absoluteSpeedDiff = absoluteSpeedDiff;
		}
		public float getSimilarity() {
			return similarity;
		}
		public void setSimilarity(float similarity) {
			this.similarity = similarity;
		}	
						
}
