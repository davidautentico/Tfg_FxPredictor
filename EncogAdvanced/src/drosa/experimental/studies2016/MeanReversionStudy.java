package drosa.experimental.studies2016;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.momentum.TestZZnbrumSystem;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MeanReversionStudy {

	public static void doStudy(ArrayList<QuoteShort> data,int y1,int y2,
			int h1,int h2,
			int h3,int h4,
			int minPips){
	
		ArrayList<QuoteShort> ranges = new ArrayList<QuoteShort>();
		ArrayList<QuoteShort> barTest = new ArrayList<QuoteShort>();
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int range = 0;
		QuoteShort q12 = new QuoteShort();
		QuoteShort q3 = new QuoteShort();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay!=-1){
					ranges.add(q12);
					barTest.add(q3);
				}
				q12 = new QuoteShort();
				q3 = new QuoteShort();
				range = 0;
				lastDay = day;
			}
			
			//vemos en que franja estamos
			if (h>=h1 && h<=h2){
				if (q12.getHigh5()==-1 || q.getHigh5()>=q12.getHigh5()){
					q12.setHigh5(q.getHigh5());
				}
				if (q12.getLow5()==-1 || q.getLow5()<=q12.getLow5()){
					q12.setLow5(q.getLow5());
				}
				if (q12.getOpen5() == -1)
					q12.setOpen5(q.getOpen5());
				q12.setClose5(q.getClose5());
			}else if (h>=h3 && h<=h4){
				if (q3.getHigh5()==-1 || q.getHigh5()>=q3.getHigh5()){
					q3.setHigh5(q.getHigh5());
				}
				if (q3.getLow5()==-1 || q.getLow5()<=q3.getLow5()){
					q3.setLow5(q.getLow5());
				}
				if (q3.getOpen5() == -1)
					q3.setOpen5(q.getOpen5());
				q3.setClose5(q.getClose5());
			}			
		}
		
		//estudio de las franjas
		//System.out.println("ranges: "+ranges.size());
		ArrayList<Integer> diffArray = new ArrayList<Integer>();
		int accDiff12 = 0;
		int accDiff = 0;
		int accDiff13 = 0;
		int cases = 0;
		int cases5 = 0;
		for (int i=0;i<ranges.size();i++){
			q12 = ranges.get(i);
			q3 = barTest.get(i);
			int diff = q12.getClose5()-q12.getOpen5();
			int diffOL = q3.getOpen5()-q3.getLow5();
			int diffOH = q3.getHigh5()-q3.getOpen5();
			//System.out.println(diff+" "+diffOL);
			if (diff>=minPips*10){//long
				accDiff12 +=diff;
				accDiff += diffOL;
				accDiff13 += (diffOL-diffOH); 
				cases++;				
				diffArray.add(diffOL);
				if (diffOL>=100){
					cases5++;
				}
			}else if (diff<=-minPips*10){//short
				accDiff12 += -diff;
				accDiff += diffOH;
				accDiff13 += (diffOH-diffOL); 
				cases++;
				diffArray.add(diffOH);
				if (diffOH>=100){
					cases5++;
				}
			}
		}
		
		double per5 = cases5*100.0/cases;
		double avgPips12 = accDiff12*0.1/cases;
		double avgPips = accDiff*0.1/cases;
		double factor = avgPips/avgPips12;
		double avgPips13 = accDiff13*0.1/cases;
		String header = 
				h1+" "+h2 +" "+h3+" "+h4+" "+minPips
				+" || "
				+" "+cases
				+" "+PrintUtils.Print2dec(avgPips12,false)
				+" "+PrintUtils.Print2dec(avgPips,false)
				+" "+PrintUtils.Print2dec(factor,false)
				+" "+PrintUtils.Print2dec(avgPips13,false)
				+" "+PrintUtils.Print2dec(per5,false)
				+" || "
				;
		MathUtils.summary(header, diffArray);

	}
	
	public static void main(String[] args) throws Exception {		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.02.19.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.02.19.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.02.19.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD );
		paths.add(pathAUDUSD);
		/*paths.add(pathEURJPY);
		paths.add(pathGBPUSD);paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);*/
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= null;
		ArrayList<Quote> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="dukasc";
			}				
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			
			int beginInicial = 1;
			int begin = beginInicial;
			int end = data.size()-1;
			int boxes = 1;
			int boxSize = end/boxes;
			
			double comm = 0.0;
			System.out.println("total data: "+data.size()+" "+boxSize);
			for (int y1=2003;y1<=2003;y1++){
				int y2 = y1+13;
				for (int h1=0;h1<=0;h1++){
					for (int h2=h1+8;h2<=h1+8;h2++){
						for (int h3=h2+1;h3<=h2+1;h3++){
							for (int h4=23;h4<=23;h4++){
								for (int minPips=0;minPips<=100;minPips++){
									MeanReversionStudy.doStudy(data, y1, y2, h1, h2, h3,h4, minPips);
								}
							}
						}
					}
				}
			}
		}
	}

}
