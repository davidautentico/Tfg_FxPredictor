package drosa.experimental.meanReverting;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMeanReverting {
	
	
	public static void testMR(ArrayList<QuoteShort> data,ArrayList<Integer> maxMins, int begin,int end,int h1,int min,int tp,int sl,int minBars){
	
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int wins = 0;
		int losses = 0;
		
		if (end>=data.size()-1) end = data.size()-1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int DO = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int mi = cal.get(Calendar.MINUTE);
			
			if (day!=lastDay){
				DO = q.getOpen5();
				lastDay = day;
			}
			
			int diffDO = q.getOpen5()-DO;
			int hBar= maxMins.get(i-1);
			if (h==h1 && mi==min && DO!=-1){
				int entry = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType posType = PositionType.NONE;
				if (hBar>=minBars){//short
					entry = q.getOpen5();
					slValue = entry+sl*10;
					tpValue = entry-tp*10;
					posType = PositionType.SHORT;
				}else if (hBar<=-minBars){//long
					entry = q.getOpen5();
					slValue = entry-sl*10;
					tpValue = entry+tp*10;
					posType = PositionType.LONG;
				}
				
				if (entry!=-1){
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(posType);
					pos.setPositionStatus(PositionStatus.OPEN);
					positions.add(pos);
				}
			}
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean removed = false;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					boolean closed = false;
					if (p.getPositionType()==PositionType.SHORT){
						if (p.getSl()<=q.getHigh5()){
							losses++;
							closed = true;
						}else if (p.getTp()>=q.getLow5()){
							wins++;
							closed = true;
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (p.getSl()>=q.getLow5()){
							losses++;
							closed = true;
						}else if (p.getTp()<=q.getHigh5()){
							wins++;
							closed = true;
						}
					}
					if (closed){
						positions.remove(j);
						removed = true;
					}
				}
				if (!removed) j++;
			}
		}
		int totals = wins+losses;
		if (totals>0){
			double winPer = wins*100.0/totals;
			System.out.println(h1+" "+min+" "+tp+" "+sl+" "+minBars+" || "+totals+" "+PrintUtils.Print2dec(winPer,false,2));
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.05.csv";
		
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		System.out.println("total data: "+data.size());
		ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
		int begin = 400000;
		int end = 900000;
		
		for (int h=0;h<=23;h++){
			for (int m=0;m<=0;m+=5){
				for (int tp=10;tp<=10;tp++){
					int sl = tp;
					for (int minBars = 5000;minBars<=5000;minBars++){
						TestMeanReverting.testMR(data,maxMins, begin, end, h, m, tp, sl, minBars);
					}
				}
				
			}
		}
	}

}
