package drosa.indicators;


import java.util.ArrayList;
import java.util.List;

import drosa.finances.Quote;
import drosa.forecastcases.DataFeed;

public abstract class Indicator {

		DataFeed feed =null;
		
		int totalPatterns = 0;
		
		IndicatorType type;
		
		
	   
	    public IndicatorType getType() {
			return type;
		}

		public void setType(IndicatorType type) {
			this.type = type;
		}

		public int getTotalPatterns() {
			return totalPatterns;
		}

		public void setTotalPatterns(int totalPatterns) {
			this.totalPatterns = totalPatterns;
		}

		public DataFeed getFeed() {
			return feed;
		}

		public void setFeed(DataFeed feed) {
			this.feed = feed;
		}

		public abstract double getValue(List<Quote> data,int pos);
		
		
}
