package drosa.strategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class PeakIntervalCalculator {
	
	
	
	public static ArrayList<Quote> calculatePeaks(ArrayList<Quote> data,int interval,boolean ishigh){
		ArrayList<Quote> peaks = new ArrayList<Quote>();
		
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			if (isPeak(data,i-interval,i,ishigh)){
				if (ishigh)
					System.out.println(interval+" HIGH "+DateUtils.datePrint(q.getDate())+">>> "+PrintUtils.Print(q.getHigh())+" "+DateUtils.datePrint(q.getDate()));
				else
					System.out.println(interval+" LOW "+DateUtils.datePrint(q.getDate())+">>> "+PrintUtils.Print(q.getLow())+" "+DateUtils.datePrint(q.getDate()));
				peaks.add(q);
			}
		}
		
		return peaks;
	}

	private static boolean isPeak(ArrayList<Quote> data, int begin, int end,
			boolean ishigh) {
		// TODO Auto-generated method stub
		if (begin<0) begin=0;
		if (end>data.size()) end=data.size();
		Quote q0 = data.get(end);
		for (int i=begin;i<end;i++){
			Quote q = data.get(i);
			if (ishigh){
				if (q.getHigh()>q0.getHigh()) return false;
			}else{
				if (q.getLow()<q0.getLow()) return false;
			}
		}
		return true;
	}

	private static Quote calculateMaxMin(ArrayList<Quote> data, int begin, int end,
			boolean high) {
		// TODO Auto-generated method stub
		if (begin<0) begin=0;
		if (end>data.size()) end=data.size();
		Quote max =new Quote();
		max.setHigh(0);
		Quote min = new Quote();
		min.setLow(9999);
		for (int i=begin;i<end;i++){
			Quote q = data.get(i);
			if (q.getLow()<min.getLow()){
				min.copy(q);
			}
			if (q.getHigh()>max.getHigh()){
				max.copy(q);
			}
		}
		
		if (high) return max;

		return min;
	}
	
	private static void addTrades(ArrayList<Quote> daysCompleted,
			ArrayList<Quote> days) {
		// TODO Auto-generated method stub
		for (int i=0;i<days.size();i++){
			Quote q = days.get(i);
			Quote qNew = new Quote();
			qNew.copy(q);
			//System.out.println("añadiendo: "+DateUtils.datePrint(qNew.getDate()));
			daysCompleted.add(qNew);
		}
		
	}
	
	public static void calculateStrongMarkets(SQLConnectionUtils sql,int interval,GregorianCalendar from,GregorianCalendar to){
		
		GregorianCalendar iter = new GregorianCalendar();
		GregorianCalendar cal = new GregorianCalendar();
		iter.setTime(from.getTime());
		
		while (iter.getTimeInMillis()<=to.getTimeInMillis()){
			cal.setTime(iter.getTime());
			System.out.println("***DAY: "+DateUtils.datePrint(iter.getTime()));
			ArrayList<PeakInterval> peaks = DAO.retrievePeaks(sql,interval, cal);
			String strongMarkets="";
			if (peaks.size()>0){
				PeakInterval peak = peaks.get(0);
				Date initial = peak.getTradetime();
				Calendar cal0 = Calendar.getInstance();
				Calendar cal1 = Calendar.getInstance();
				cal0.setTime(initial);
				System.out.println(DateUtils.datePrint(initial)+" "+peak.getSymbol()+" "+peak.getPeakType());
				strongMarkets=peak.getSymbol()+peak.getPeakType();
				boolean ok=true;
				for (int i=1;i<peaks.size() && ok;i++){
					peak = peaks.get(i);
					cal1.setTime(peak.getTradetime());
					if (cal0.getTimeInMillis()>cal1.getTimeInMillis()) 
						ok=false;
					else{
						strongMarkets+=','+peak.getSymbol()+peak.getPeakType();
						System.out.println(DateUtils.datePrint(initial)+" "+peak.getSymbol()+" "+peak.getPeakType());
					}
				}
				DAO.insertStrongMarkets(sql, iter.getTime(),interval, strongMarkets);
			}
			System.out.println("strongMarkets: "+strongMarkets);
			
			iter.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
	
	public static void main(String[] args) {
	
		String symbol="usdjpy";
		SQLConnectionUtils sql = new SQLConnectionUtils();
		sql.init("forexdata_forex");
		//sql.init("dukascopy_forex");
		
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		int interval=40;
		
		/*ORBStudy orb = new ORBStudy();
		ArrayList<Quote> daysCompleted = new ArrayList<Quote>();
		for (int y=1986;y<=2012;y++){
			fromDate.set(y, 0, 1);
			toDate.set(y, 11, 31);
			ArrayList<Quote> data= DAO.retrieveQuotes2(sql,symbol+"_15m",symbol,fromDate,toDate,true);
			ArrayList<Quote> days=orb.createDailyData(data,8);
			addTrades(daysCompleted,days);
		}
		for (int i=0;i<daysCompleted.size();i++){
			Quote q = daysCompleted.get(i);
			System.out.println(DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
		}
		
		ArrayList<Quote> highs = PeakIntervalCalculator.calculatePeaks(daysCompleted,interval, true);
		for (int i=0;i<highs.size();i++){
			Quote q = highs.get(i);
			DAO.insertPeakInterval(sql, "peakInterval", symbol, q, interval, true);
		}
		ArrayList<Quote> lows = PeakIntervalCalculator.calculatePeaks(daysCompleted,interval, false);
		for(int i=0;i<lows.size();i++){
			Quote q = lows.get(i);
			DAO.insertPeakInterval(sql, "peakInterval", symbol, q, interval, false);
		}
		System.out.println("total days total lows, total highs "+daysCompleted.size()+" "+highs.size()+" "+lows.size());
		*/
		fromDate.set(1986, 0, 1,0,0,0);
		toDate.set(2012, 11, 31,0,0,0);
		PeakIntervalCalculator.calculateStrongMarkets(sql,interval, fromDate, toDate);
	}

	

}
