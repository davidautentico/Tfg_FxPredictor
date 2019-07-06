package drosa.phil.candles;

import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.RangeStudy;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyCandles {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path   = "c:\\fxdata";
		String file1H = path+"\\"+"EURUSD_Hourly_Bid_2003.05.04_2014.02.07.csv";
		String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2003.05.04_2014.02.23.csv";
		
		ArrayList<Quote> dataI 			= DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 			= TradingUtils.cleanWeekendData(dataS);
		
		System.out.println("data5m: "+data5m.size());
		
		
		for (int r=5;r<=100;r++){
			int totals5=0;
			int totalCatched = 0;
			int totals52=0;
			int total=0;
			for (int i=2;i<data5m.size()-12;i++){
				Quote q1 = data5m.get(i-1);
				Quote q = data5m.get(i);
				Quote q0 = data5m.get(i+12);
				int range1 = TradingUtils.getPipsDiff(q1.getHigh(),q1.getLow());
				int diffH = TradingUtils.getPipsDiff(q1.getHigh(),q1.getClose());
				if (range1>=r && diffH<=0 && q1.getClose()>q1.getOpen()){
					//double overlap = RangeStudy.maxOverlap(q1.getHigh(), q1.getLow(),q.getHigh(), q.getLow());
					//System.out.println("overlap: "+overlap);
					double overlap = TradingUtils.getPipsDiff(q1.getClose(),q.getLow());
					double overlap2 = TradingUtils.getPipsDiff(q1.getClose(),q0.getLow());
					if (overlap>=5){
						int diffHO = TradingUtils.getPipsDiff(q.getHigh(),q.getOpen());
						if (diffHO>=10) totalCatched++;
						totals5++;
					}
					if (overlap2>=5){
						totals52++;
					}
					total++;
				}
			}
			System.out.println("total5: "+r+" "+total+" "+PrintUtils.Print2dec(totals5*100/total, false)
					+" "+PrintUtils.Print2dec(totals52*100/total, false)
					+" "+PrintUtils.Print2dec(totalCatched*100/totals5, false)
					);
		}
	}

}
