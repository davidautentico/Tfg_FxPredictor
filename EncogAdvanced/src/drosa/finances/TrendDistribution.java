package drosa.finances;

public class TrendDistribution {

	Trend mainTrend;
	
	int upSubTrends;
	int downSubTrends;
	String subTrendsDistribution;
	
	
	public Trend getMainTrend() {
		return mainTrend;
	}
	public void setMainTrend(Trend mainTrend) {
		this.mainTrend = mainTrend;
	}
	public int getUpSubTrends() {
		return upSubTrends;
	}
	public void setUpSubTrends(int upSubTrends) {
		this.upSubTrends = upSubTrends;
	}
	public int getDownSubTrends() {
		return downSubTrends;
	}
	public void setDownSubTrends(int downSubTrends) {
		this.downSubTrends = downSubTrends;
	}
	public String getSubTrendsDistribution() {
		return subTrendsDistribution;
	}
	public void setSubTrendsDistribution(String subTrendsDistribution) {
		this.subTrendsDistribution = subTrendsDistribution;
	}
	
	
	
}
