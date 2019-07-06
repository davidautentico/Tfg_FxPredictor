package drosa.finances;

import java.util.Date;

import drosa.utils.QuoteDataType;

public class SplitData {

		String symbol;
		QuoteDataType type;		
		Date date;
		float value;
				
		public String getSymbol() {
			return symbol;
		}
		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}
		public QuoteDataType getType() {
			return type;
		}
		public void setType(QuoteDataType type) {
			this.type = type;
		}
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
		
		
		
}
