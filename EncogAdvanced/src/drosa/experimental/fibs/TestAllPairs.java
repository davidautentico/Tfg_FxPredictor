package drosa.experimental.fibs;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestAllPairs {
	
	public static void testHLBreak(ArrayList<QuoteShort> data,int begin,int end,int h1,int h2,int tp,int sl,int offset,boolean breakMode){
		
		int wins=0;
		int losses = 0;
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		int lastHigh = -1;
		int lastLow = -1;
		Calendar cal = Calendar.getInstance();
		PositionShort pos = new PositionShort();
		if (end>=data.size()-1) end = data.size()-1;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay!=-1){
					lastHigh = max;
					lastLow = min;
					lastHigh += offset;
					lastLow -= offset;
				}
				min = 999999;
				max = -999999;
				lastDay = day;
			}
			
			if (h1<=h && h<=h2){
				int entry = -1;
				int tpValue = -1;
				int slValue = -1;
				PositionType positionType = PositionType.NONE;
				int win = 0;
				if (lastHigh!=-1 && (q.getHigh5()>=lastHigh && q.getOpen5()<lastHigh)){
					entry	= lastHigh;
					if (breakMode){
						slValue = entry-10*sl;
						tpValue = entry+10*tp;		
						positionType = PositionType.LONG;
					}else{
						slValue = entry+10*sl;
						tpValue = entry-10*tp;		
						positionType = PositionType.SHORT;
					}
				}
				
				if (lastLow!=-1 && (q.getLow5()<=lastLow && q.getOpen5()>lastLow)){
					entry	= lastLow;
					if (breakMode){
						slValue = entry+10*sl;
						tpValue = entry-10*tp;	
						positionType = PositionType.SHORT;
					}else{
						slValue = entry-10*sl;
						tpValue = entry+10*tp;	
						positionType = PositionType.LONG;
					}
				}
				
				if (entry!=-1){
					pos.setPositionType(positionType);
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					TradingUtils.testPosition(pos, data, i, end,false);	
					win = pos.getWin();
				}
				if (win==1) wins++;
				else if (win==-1) losses++;
			}
			
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double pf = (winPer*tp)/(lossPer*sl);
		double exp = (winPer*tp-lossPer*sl)/100.0;
		
		System.out.println(tp+" "+sl+" "+offset+" || "+total+" "+PrintUtils.Print2(winPer)+" "+PrintUtils.Print2(pf)+" "+PrintUtils.Print2(exp));
	}

	public static void main(String[] args) {
		
		String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		String path5m1   = "c:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		String path5m2   = "c:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		String path5m3   = "c:\\fxdata\\USDCAD_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		String path5m4   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		String path5m5   = "c:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		String path5m6   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		//String path5m     = "c:\\fxdata\\EURJPY_UTC_1 Min_Bid_2003.08.03_2015.03.12.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.02.18.csv";
		//String path5m   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2015.02.18.csv";
				
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(path5m0);paths.add(path5m1);paths.add(path5m2);paths.add(path5m3);
		paths.add(path5m4);paths.add(path5m5);paths.add(path5m6);
		
		int total = paths.size();
		total = 1;
		for (int i=0;i<total;i++){
			String path5m = paths.get(i);
			ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			//System.out.println("total data: "+data.size());
			
			/*for (int i=0;i<data.size();i++){
				QuoteShort q = data.get(i);
				System.out.println(q.toString());
			}*/
			
			int begin = 400000;
			int end = 900000;
			int h1 = 16;
			int h2 = 17;
			int tp = 5;
			int sl = 5;
			for (h1=0;h1<=23;h1++){
				h2 = h1+0;
				for (int offset=0;offset<=0;offset+=10){
					for (tp=5;tp<=5;tp++){
						for (sl=3*tp;sl<=3*tp;sl+=1*tp){
							//sl = 2*tp;
							TestAllPairs.testHLBreak(data, begin, end, h1, h2, tp, sl,offset,false);
						}
					}
				}
			}
		}
	}

}
