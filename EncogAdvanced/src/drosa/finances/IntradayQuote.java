package drosa.finances;

import java.util.Date;

public class IntradayQuote {

		String symbol;
		Date dateTime;
		float price;
		long volume=-1;
		long contracts=-1;
		
		public String getSymbol() {
			return symbol;
		}
		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}
		public float getPrice() {
			return price;
		}
		public void setPrice(float price) {
			this.price = price;
		}
		public long getVolume() {
			return volume;
		}
		public void setVolume(long volume) {
			this.volume = volume;
		}
		public Date getDateTime() {
			return dateTime;
		}
		public void setDateTime(Date dateTime) {
			this.dateTime = dateTime;
		}
		public long getContracts() {
			return contracts;
		}
		public void setContracts(long contracts) {
			this.contracts = contracts;
		}
		
		
}
