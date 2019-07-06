package drosa.finances;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import drosa.utils.DateUtils;
import drosa.utils.TrendType;

public class PeriodInformation {

		Date fromDate;
		Date toDate;
		
		float low=0;
		float high=0;
		
		Date firstDate;
		Date lastDate;
		
		Date dateLow;
		Date dateHigh;
		
		int tradingDaysBetween;
		
		float absDifference;
		
		float percentageVar;
		
		float speed;
		
		float speedAbsolute;
		
		float speedAbsoluteInit3d;
		float speedAbsoluteInit5d;
		float speedAbsoluteInit10d;
		float speedAbsoluteInit20d;
		float speedAbsoluteInit50d;
		float speedAbsoluteInit100d;
		float speedAbsoluteFinish3d;
		float speedAbsoluteFinish5d;
		float speedAbsoluteFinish10d;
		float speedAbsoluteFinish20d;
		float speedAbsoluteFinish50d;
		float speedAbsoluteFinish100d;
		
		TrendType trendType;
		
		List<Trend> trendsContainer = new ArrayList<Trend>();
		
		public Date getFromDate() {
			return fromDate;
		}
		public void setFromDate(Date fromDate) {
			this.fromDate = fromDate;
		}
		public Date getToDate() {
			return toDate;
		}
		public void setToDate(Date toDate) {
			this.toDate = toDate;
		}
		public float getLow() {
			return low;
		}
		public void setLow(float low) {
			this.low = low;
		}
		public float getHigh() {
			return high;
		}
		public void setHigh(float high) {
			this.high = high;
		}
		public Date getDateLow() {
			return dateLow;
		}
		public void setDateLow(Date dateLow) {
			this.dateLow = dateLow;
		}
		public Date getDateHigh() {
			return dateHigh;
		}
		public void setDateHigh(Date dateHigh) {
			this.dateHigh = dateHigh;
		}
		public int getTradingDaysBetween() {
			return tradingDaysBetween;
		}
		public void setTradingDaysBetween(int tradingDaysBetween) {
			this.tradingDaysBetween = tradingDaysBetween;
		}
								
		public float getAbsDifference() {
			return absDifference;
		}
		public void setAbsDifference(float absDifference) {
			this.absDifference = absDifference;
		}
		public float getPercentageVar() {
			return percentageVar;
		}
		public void setPercentageVar(float percentageVar) {
			this.percentageVar = percentageVar;
		}
		public float getSpeed() {
			return speed;
		}
		public void setSpeed(float speed) {
			this.speed = speed;
		}
						
		public float getSpeedAbsolute() {
			return speedAbsolute;
		}
		public void setSpeedAbsolute(float speedAbsolute) {
			this.speedAbsolute = speedAbsolute;
		}
		public TrendType getTrendType() {
			return trendType;
		}
		public void setTrendType(TrendType trendType) {
			this.trendType = trendType;
		}
						
		public Date getFirstDate() {
			return firstDate;
		}
		
		public void setFirstDate(Date firstDate) {
			this.firstDate = firstDate;
		}
		
		public Date getLastDate() {
			return lastDate;
		}
		
		public void setLastDate(Date lastDate) {
			this.lastDate = lastDate;
		}
		
		public List<Trend> getTrendsContainer() {
			return trendsContainer;
		}
		
		public void setTrendsContainer(List<Trend> trendsContainer) {
			this.trendsContainer = trendsContainer;
		}
		
		
		
		
		public float getSpeedAbsoluteInit3d() {
			return speedAbsoluteInit3d;
		}
		public void setSpeedAbsoluteInit3d(float speedAbsoluteInit3d) {
			this.speedAbsoluteInit3d = speedAbsoluteInit3d;
		}
		public float getSpeedAbsoluteInit5d() {
			return speedAbsoluteInit5d;
		}
		public void setSpeedAbsoluteInit5d(float speedAbsoluteInit5d) {
			this.speedAbsoluteInit5d = speedAbsoluteInit5d;
		}
		public float getSpeedAbsoluteInit10d() {
			return speedAbsoluteInit10d;
		}
		public void setSpeedAbsoluteInit10d(float speedAbsoluteInit10d) {
			this.speedAbsoluteInit10d = speedAbsoluteInit10d;
		}
		public float getSpeedAbsoluteInit20d() {
			return speedAbsoluteInit20d;
		}
		public void setSpeedAbsoluteInit20d(float speedAbsoluteInit20d) {
			this.speedAbsoluteInit20d = speedAbsoluteInit20d;
		}
		public float getSpeedAbsoluteInit50d() {
			return speedAbsoluteInit50d;
		}
		public void setSpeedAbsoluteInit50d(float speedAbsoluteInit50d) {
			this.speedAbsoluteInit50d = speedAbsoluteInit50d;
		}
		public float getSpeedAbsoluteInit100d() {
			return speedAbsoluteInit100d;
		}
		public void setSpeedAbsoluteInit100d(float speedAbsoluteInit100d) {
			this.speedAbsoluteInit100d = speedAbsoluteInit100d;
		}
		public float getSpeedAbsoluteFinish3d() {
			return speedAbsoluteFinish3d;
		}
		public void setSpeedAbsoluteFinish3d(float speedAbsoluteFinish3d) {
			this.speedAbsoluteFinish3d = speedAbsoluteFinish3d;
		}
		public float getSpeedAbsoluteFinish5d() {
			return speedAbsoluteFinish5d;
		}
		public void setSpeedAbsoluteFinish5d(float speedAbsoluteFinish5d) {
			this.speedAbsoluteFinish5d = speedAbsoluteFinish5d;
		}
		public float getSpeedAbsoluteFinish10d() {
			return speedAbsoluteFinish10d;
		}
		public void setSpeedAbsoluteFinish10d(float speedAbsoluteFinish10d) {
			this.speedAbsoluteFinish10d = speedAbsoluteFinish10d;
		}
		public float getSpeedAbsoluteFinish20d() {
			return speedAbsoluteFinish20d;
		}
		public void setSpeedAbsoluteFinish20d(float speedAbsoluteFinish20d) {
			this.speedAbsoluteFinish20d = speedAbsoluteFinish20d;
		}
		public float getSpeedAbsoluteFinish50d() {
			return speedAbsoluteFinish50d;
		}
		public void setSpeedAbsoluteFinish50d(float speedAbsoluteFinish50d) {
			this.speedAbsoluteFinish50d = speedAbsoluteFinish50d;
		}
		public float getSpeedAbsoluteFinish100d() {
			return speedAbsoluteFinish100d;
		}
		public void setSpeedAbsoluteFinish100d(float speedAbsoluteFinish100d) {
			this.speedAbsoluteFinish100d = speedAbsoluteFinish100d;
		}
		public void print() {
			// TODO Auto-generated method stub
			System.out.println("****Period Information***");
			System.out.println("FromDate: "+DateUtils.datePrint(this.fromDate));
			System.out.println("ToDate: "+DateUtils.datePrint(this.toDate));
			System.out.println("High: "+this.high+". DateHigh: "+DateUtils.datePrint(this.dateHigh));
			System.out.println("Low: "+this.low+". DateLow: "+DateUtils.datePrint(this.dateLow));
			System.out.println("TotalBars: "+this.getTradingDaysBetween());	
		}
		
		
		
		
}
