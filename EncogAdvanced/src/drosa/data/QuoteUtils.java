package drosa.data;

import java.util.ArrayList;
import java.util.Date;

import drosa.finances.Quote;
import drosa.utils.DateUtils;

public class QuoteUtils {

	public static void extractQuoteInfo(ArrayList<Quote> data, Date[] dates, double[] inOpen,
			double[] inHigh, double[] inLow, double[] inClose) {
		// TODO Auto-generated method stub
		int j=0;
		/*for (int i=data.size()-1;i>=0;i--){
			Quote q = data.get(i);
			dates[j]=q.getDate();
			inOpen[j]=q.getOpen();
			inHigh[j]=q.getHigh();
			inLow[j]=q.getLow();
			inClose[j]=q.getClose();
			j++;
			//System.out.println(DateUtils.datePrint(dates[i]));
		}*/
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			dates[j]=q.getDate();
			inOpen[j]=q.getOpen();
			inHigh[j]=q.getHigh();
			inLow[j]=q.getLow();
			inClose[j]=q.getClose();
			j++;
			//System.out.println(DateUtils.datePrint(dates[i]));
		}
		
	}

}
