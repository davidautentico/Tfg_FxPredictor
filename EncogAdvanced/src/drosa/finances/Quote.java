package drosa.finances;

import java.util.Date;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class Quote {

		Date date = new Date();
		int index;
		String symbol;
		double Open=0.0;
		double High=0.0;
		double Low=0.0;
		double Close=0.0;
		long Volume=0;
		double adjClose=0.0;
		double adjOpen=0.0;
		double adjHigh=0.0;
		double adjLow=0.0;
		
		//días que se hicieron los máximos y los mínimos
		int minDay=0;
		int maxDay=0;
		int minMonth=0;
		int maxMonth=0;
		int barsBetween=0;
		
		//extra value
		int extra = 0;
		
		public Quote(double open, double high, double low, double close) {
			// TODO Auto-generated constructor stub
			this.Open = open;
			this.High = high;
			this.Low = low;
			this.Close = close;
			this.index=-1;
			this.extra = 0;
		}
		public Quote() {
			// TODO Auto-generated constructor stub
			this.Open = -9999;
			this.High = -9999;
			this.Low = -9999;
			this.Close = -9999;
			this.extra = 0;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		
		
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getSymbol() {
			return symbol;
		}
		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}
		public double getOpen() {
			return Open;
		}
		public void setOpen(double open) {
			Open = open;
		}
		public double getHigh() {
			return High;
		}
		public void setHigh(double high) {
			High = high;
		}
		public double getLow() {
			return Low;
		}
		public void setLow(double low) {
			Low = low;
		}
		public double getClose() {
			return Close;
		}
		public void setClose(double close) {
			Close = close;
		}
		public long getVolume() {
			return Volume;
		}
		public void setVolume(long volume) {
			Volume = volume;
		}
		public double getAdjClose() {
			return adjClose;
		}
		public void setAdjClose(Float adjClose) {
			this.adjClose = adjClose;
		}
		public int getMinDay() {
			return minDay;
		}
		public void setMinDay(int minDay) {
			this.minDay = minDay;
		}
		public int getMaxDay() {
			return maxDay;
		}
		public void setMaxDay(int maxDay) {
			this.maxDay = maxDay;
		}
		public int getMinMonth() {
			return minMonth;
		}
		public void setMinMonth(int minMonth) {
			this.minMonth = minMonth;
		}
		public int getMaxMonth() {
			return maxMonth;
		}
		public void setMaxMonth(int maxMonth) {
			this.maxMonth = maxMonth;
		}
		public void setAdjClose(double adjClose) {
			this.adjClose = adjClose;
		}
		public int getBarsBetween() {
			return barsBetween;
		}
		public void setBarsBetween(int barsBetween) {
			this.barsBetween = barsBetween;
		}
		public double getAdjOpen() {
			return adjOpen;
		}
		public void setAdjOpen(double adjOpen) {
			this.adjOpen = adjOpen;
		}
		public double getAdjHigh() {
			return adjHigh;
		}
		public void setAdjHigh(double adjHigh) {
			this.adjHigh = adjHigh;
		}
		public double getAdjLow() {
			return adjLow;
		}
		public void setAdjLow(double adjLow) {
			this.adjLow = adjLow;
		}
		
		public int getExtra() {
			return extra;
		}
		public void setExtra(int extra) {
			this.extra = extra;
		}
		public void copy(Quote last) {
			// TODO Auto-generated method stub
			this.date.setTime(last.getDate().getTime());
			this.symbol=last.symbol;
			this.Open=last.Open;
			this.High=last.High;
			this.Low=last.Low;
			this.Close=last.Close;
			this.Volume=last.Volume;
			this.adjClose=last.adjClose;
			this.adjOpen=last.adjOpen;
			this.adjHigh=last.High;
			this.adjLow=last.adjLow;
			this.extra = last.extra;
		}
		public void mult(int mult) {
			// TODO Auto-generated method stub
			this.Open*=mult;
			this.High*=mult;
			this.Low*=mult;
			this.Close*=mult;
		}
		
		public String toString(){
			String str= DateUtils.datePrint(this.date)
					+" "+ PrintUtils.Print(this.Open)
					+" "+ PrintUtils.Print(this.High)
					+" "+ PrintUtils.Print(this.Low)
					+" "+ PrintUtils.Print(this.Close)
					;
			return str;
			
		}
		
		
}
