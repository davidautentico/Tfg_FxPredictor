package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MeanRevertingStrategy2 {

	
	public static int isQuoteMaxorMin(ArrayList<Quote> data,int index,int hbars){
		
		int res = 0;
		Quote ref = data.get(index);
		int begin = index-hbars;
		int end = index+hbars;
		boolean min = true;
		boolean max = true;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ref.getDate().getTime());
		//System.out.println("EXAMINAR: "+DateUtils.datePrint(cal)+" "+PrintUtils.getOHLC(ref));
		for (int i=begin;i<=end;i++){
			if (i==index){
				res=9;
				continue;
			}
			Quote q = data.get(i);
			if (min && TradingUtils.getPipsDiff(ref.getLow(), q.getLow())>=0){
				//System.out.println("FALLO min: "+DateUtils.datePrint(cal)+" "+PrintUtils.getOHLC(q));
				min = false;
			}
			if ( max && TradingUtils.getPipsDiff(q.getHigh(),ref.getHigh())>=0){
				//System.out.println("FALLO max: "+DateUtils.datePrint(cal)+" "+PrintUtils.getOHLC(q));
				max = false;
			}
			if (!min && !max){
				break;
			}
		}
		
		if (min) return -1;
		if (max) return 1;
		return res;
	}
	
	public static void stats(ArrayList<Quote> data,Calendar from,Calendar to,int hBars){
	
		int total = 0;
		int totalMin=0;
		int totalMax=0;
		int totalCandidates=0;
		int totalSimpleFail=0;
		int bars = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=hBars;i<data.size()-hBars;i++){
			Quote q = data.get(i);
			cal.setTimeInMillis(q.getDate().getTime());
			if (cal.getTimeInMillis()<from.getTimeInMillis() ){
				continue;
			}
			if (cal.getTimeInMillis()>to.getTimeInMillis()){
					//System.out.println("break");
					break;
			}
			bars++;
			int maxmin = isQuoteMaxorMin(data,i,hBars);
			if (maxmin==-1){
				totalMin++;
			}
			if (maxmin==1){
				totalMax++;
			}
			if (maxmin==9){
				totalCandidates++;
			}
			if (maxmin==0){
				totalSimpleFail++;
			}
			if (maxmin!=0 && maxmin!=9){
				//System.out.println(DateUtils.datePrint(cal)+" "+PrintUtils.getOHLC(q)+" OK "+maxmin);
				total++;
			}else{
				//System.out.println(DateUtils.datePrint(cal)+" "+PrintUtils.getOHLC(q)+" FAIL "+maxmin);
			}
		}
		double per = total*100.0/bars;
		System.out.println("hBars= "+hBars
				+" candidates= "+totalCandidates
				+" wins= "+total
				+" tot= "+bars
				+" perWin= "+PrintUtils.Print2dec(per, false));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.08.06.csv";
		//String path5m = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.07.25.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		//String path5m = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2005.01.01_2014.07.23.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> data15 = ConvertLib.convert(data, 3);
  		ArrayList<Quote> data60 = ConvertLib.convert(data, 12);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
  		
  		
  		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		int year = 2012;
		from.set(2012, 0, 1);
		to.set(2014,  11, 31);
		
		for (int hBars=2;hBars<=50;hBars++){
			MeanRevertingStrategy2.stats(data15,from,to,hBars);
			//MeanRevertingStrategy2.stats(data60,from,to,hBars);
			//MeanRevertingStrategy2.stats(dailyData,from,to,hBars);
		}	
	}

}
