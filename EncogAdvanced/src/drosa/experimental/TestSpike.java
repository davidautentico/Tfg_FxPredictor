package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestSpike {
	
	public static void testSpikes(ArrayList<QuoteShort> data,int h1,int h2,int thr,int numBars){
		
		int acc = 0;
		int total=0;
		int avg = 0;
		int g5 = 0;
		int g10 = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<data.size()-numBars;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h<h1 ||h >h2) continue;
			boolean bull = true;
			short q1range = (short) (q1.getHigh()-q1.getLow());
			
			if (q1range>=thr){
				QuoteShort qMinMax= TradingUtils.getMaxMinShort(data, i, i+numBars-1);
				int diffH = q1.getHigh()-qMinMax.getLow();
				diffH = q1.getClose()-qMinMax.getLow();
				avg+=diffH;
				//avg+=diffL;
				total++;
				if (diffH>=5) g5++;
				if (diffH>=10) g10++;
			}
		}
		
		System.out.println(thr				
				+" "+h1+" "+h2
				+" "+total
				+" "+PrintUtils.Print2(avg*1.0/total)
				+" "+PrintUtils.Print2(g5*100.0/total)
				+" "+PrintUtils.Print2(g10*100.0/total)
				);
	}
	
public static void testSpikeTradingSystem(ArrayList<QuoteShort> data,
		int begin,int end,
		int d1,int d2,
		int h1,int h2,int thr,int tp,int sl,
		double offset,boolean debug){
		
		int begin1 = begin;
		int end1 = end;
		if (begin<1) begin1 = 1;
		if (end>data.size()-1) end1 = data.size()-1;
	
		int total=0;
		int wins = 0;
		
		Calendar cal = Calendar.getInstance();
		PositionShort pos = new PositionShort();
		for (int i=begin1;i<=end1;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int d = cal.get(Calendar.DAY_OF_WEEK);
			if (d<d1 || d>d2) continue;
			if (h<h1 ||h >h2) continue;
			short q1range = (short) (q1.getHigh()-q1.getLow());
			short q1Diff = (short) (q1.getClose()-q1.getOpen());
			short q1DiffH = (short) (q1.getHigh()-q1.getClose());
			short q1DiffL = (short) (q1.getClose()-q1.getLow());
			int mode = 0;
			short beginValue = 0;
			short stopLoss   = 0;
			short takeProfit = 0;
			pos.setWin(0);
			if (q1range>=thr && q1Diff>=0){
				//System.out.println("INDEX -1 "+(i-1)+" "+q1.toString());
				mode = 0;
				int offsetPips = (int) (q1DiffH*offset);
				offsetPips=(int) offset;
				
				beginValue = (short) (q.getOpen()+offsetPips);
				stopLoss = (short) (beginValue + sl);
				takeProfit = (short) (beginValue - tp);
				
				TradingUtils.testPriceMovementShort(pos, data, i, data.size()-1, beginValue, stopLoss, takeProfit, mode, false, debug);
				if (pos.getWin()==1) wins++;
				if (pos.getWin()!=0){
					total++;
					//System.out.println("total= "+total);
				}
				//TradingUtils.testPriceMovementShort(pos, data, i, data.size()-1, beginValue, stopLoss, takeProfit, 0, false, false);				
			}
			
			if (q1range>=thr && q1Diff<=0){
				mode = 1;
				//beginValue = q1.getLow();
				beginValue = q.getOpen();
				int offsetPips = (int) (q1DiffL*offset);
				offsetPips=(int) offset;
				beginValue = (short) (q.getOpen()-offsetPips);
				stopLoss = (short) (beginValue - sl);
				takeProfit = (short) (beginValue + tp);
				TradingUtils.testPriceMovementShort(pos, data, i, data.size()-1, beginValue, stopLoss, takeProfit, mode, false, debug);
				if (pos.getWin()==1) wins++;
				if (pos.getWin()!=0){
					total++;
					//System.out.println("total= "+total);
				}
				
				//TradingUtils.testPriceMovementShort(pos, data, i, data.size()-1, beginValue, stopLoss, takeProfit, 0, false, false);				
			}
			
		}
		
		double winPer = wins*100.0/total;
		double exp = (winPer*tp-(100.0-winPer)*sl)/100.0;
		System.out.println(thr
				+" "+d1+" "+d2
				+" "+h1+" "+h2
				+" "+tp+" "+sl
				+" "+PrintUtils.Print(offset)
				+" "+total
				+" "+PrintUtils.Print2(winPer)
				+" "+PrintUtils.Print2(exp)
				+" "+PrintUtils.Print2(total*exp)
				);
	}
	
	public static void main(String[] args) throws Exception {
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		//String path5m = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.09.30.csv";
		
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS);
  		//ArrayList<Quote> dailyData	= ConvertLib.createDailyData(data5m);
  		//ArrayList<Quote> data1h     = ConvertLib.convert(data5m, 12);
  		ArrayList<QuoteShort> data5mS = QuoteShort.convertQuoteArraytoQuoteShort(data5m);	
  		//ArrayList<QuoteShort> data1hS = QuoteShort.convertQuoteArraytoQuoteShort(data1h);
  		//ArrayList<QuoteShort> data1dS = QuoteShort.convertQuoteArraytoQuoteShort(dailyData);	
  		ArrayList<QuoteShort> data = null;
  		int h1 = 0;
  		int h2 = 9;
  		/*for (h1=0;h1<=23;h1++){
  			h2=h1;
	  		for (int i=15;i<=15;i++){
	  			for (int numBars=570;numBars<=570;numBars++)
	  				TestSpike.testSpikes(data5mS,h1,h2, i,numBars);
	  		}
  		}*/
  		//data = data1hS;
  		data=data5mS;
  		int begin = 400000;
  		int end = data.size()-1;
  		begin = end-400000;
  		int tp = 5;
  		int sl = 1000;
  		boolean debug = false;
  		for (int d1=Calendar.MONDAY;d1<=Calendar.MONDAY+0;d1++){
  			int d2 = d1+4;
	  		for (h1=0;h1<=0;h1++){
	  			h2=h1+9;
	  			for (int i=30;i<=30;i++){
	  				for (tp=3;tp<=15;tp++){
	  					for (sl=tp;sl<=tp;sl+=tp){
	  						//for (double offset=0.0;offset<=10.0;offset+=0.1)
	  						for (int offsetp=0;offsetp<=0;offsetp+=1)
	  							//testSpikeTradingSystem(data5mS,begin,end,h1,h2,i,tp,sl,offsetp);
	  							testSpikeTradingSystem(data,begin,end,d1,d2,h1,h2,i,tp,sl,offsetp,debug);
	  					}
	  				}
	  			}
	  		}
  		}
  		//TestSpike.testSpikes(data1hS, 10);
  		//TestSpike.testSpikes(data1dS, 10);		
	}
}
