package drosa.experimental.zigzag;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestZigZag {
	
	public static void analysis(ArrayList<QuoteShort> data,int begin,int end,int h1,int h2,int period){
		
		if (begin<period) begin = period;
		if (end>data.size()-1); end = data.size()-1;
		
		ArrayList<Integer> changes = new ArrayList<Integer>();
		changes.add(0);
		int actualLeg=0;//1:up,0:none,-1:down
		int lastDay  = -1;
		int totalDays = 0;
		int avgLeg = 0;
		Calendar cal = Calendar.getInstance();
		int actualHigh = data.get(begin-period).getHigh5();
		int actualLow = data.get(begin-period).getLow5();
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				totalDays++;
				lastDay = day;
			}
			
			QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, i-period, i-1);
			
			boolean update = false;
			if (actualLeg==0 || actualLeg==-1){
				if (q.getHigh5()>qMaxMin.getHigh5()){
					int legLow = actualHigh-actualLow;
					if (h>=h1 && h<=h2){
						changes.add(1);
						avgLeg+=legLow;
					}
					actualLeg = 1;
					update = true;
					actualHigh = q.getHigh5();
				}else{//no hay cambio actualizo el low
					if (q.getLow5()<actualLow)
						actualLow = q.getLow5();
				}
			}			
			if (!update && (actualLeg==0 || actualLeg==1)){
				if (q.getLow5()<qMaxMin.getLow5()){
					int legHigh = actualHigh-actualLow;
					if (h>=h1 && h<=h2){
						changes.add(-1);
						avgLeg+=legHigh;
					}
					actualLeg = -1;
					update = true;
					actualLow = q.getLow5();
				}else{//no hay cambio, actualizo el high
					if (q.getHigh5()>actualHigh)
						actualHigh = q.getHigh5();
				}
			}
		}
		
		double avgPips = avgLeg*1.0/changes.size();
		double avgPerDay = changes.size()*1.0/totalDays;
		System.out.println("changes of direction: "+changes.size()
				+" "+PrintUtils.Print2(avgPips*0.1)
				+" "+PrintUtils.Print2(avgPerDay)
				+" "+PrintUtils.Print2(avgPerDay*avgPips*0.1)
				);
	}
	
public static void analysis2(ArrayList<QuoteShort> data,int begin,int end,int h1, int bars,int period){
		
		if (begin<period) begin = period;
		if (end>data.size()-1); end = data.size()-1;
		
		ArrayList<Integer> changes = new ArrayList<Integer>();
		changes.add(0);
		int actualLeg=0;//1:up,0:none,-1:down
		int lastDay  = -1;
		int totalDays = 0;
		int avgLeg = 0;
		Calendar cal = Calendar.getInstance();
		int actualHigh = data.get(begin-period).getHigh5();
		int actualLow = data.get(begin-period).getLow5();
		boolean enabled = false;
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				totalDays++;
				lastDay = day;
				enabled = false;
			}
			
			if (!enabled && h!=h1) continue;
			else{
				enabled = true;
				end = i+bars;
			}
			
			
			
			QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, i-period, i-1);
			
			boolean update = false;
			if (actualLeg==0 || actualLeg==-1){
				if (q.getHigh5()>qMaxMin.getHigh5()){
					int legLow = actualHigh-actualLow;
					changes.add(1);
					avgLeg+=legLow;
					actualLeg = 1;
					update = true;
					actualHigh = q.getHigh5();
				}else{//no hay cambio actualizo el low
					if (q.getLow5()<actualLow)
						actualLow = q.getLow5();
				}
			}			
			if (!update && (actualLeg==0 || actualLeg==1)){
				if (q.getLow5()<qMaxMin.getLow5()){
					int legHigh = actualHigh-actualLow;
					changes.add(-1);
					avgLeg+=legHigh;
					actualLeg = -1;
					update = true;
					actualLow = q.getLow5();
				}else{//no hay cambio, actualizo el high
					if (q.getHigh5()>actualHigh)
						actualHigh = q.getHigh5();
				}
			}
		}
		
		double avgPips = avgLeg*1.0/changes.size();
		double avgPerDay = changes.size()*1.0/totalDays;
		System.out.println("changes of direction: "+changes.size()
				+" "+PrintUtils.Print2(avgPips*0.1)
				+" "+PrintUtils.Print2(avgPerDay)
				+" "+PrintUtils.Print2(avgPerDay*avgPips*0.1)
				);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.31.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		
		
		int h1 = 10;
		int h2 = 14;
		int period = 12;
		
		/*for (int begin=1;begin<=800000;begin+=100000){
			int end = begin + 100000;
			TestZigZag.analysis(data,begin,end, h1, h2, period);
		}*/
		int begin = 800000;
		int end = data.size()-1;
		for (h1=0;h1<=23;h1++){
			h2=h1+9;
			TestZigZag.analysis(data,begin,end, h1, h2, period);
			
		}
		/*int bars = 288;
		for (int h=0;h<=23;h++){
			TestZigZag.analysis2(data,begin,end, h1, bars, period);
		}*/
		
		
	}

}
