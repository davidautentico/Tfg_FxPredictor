package drosa.experimental.newResearches;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class AsianReverse {
	
	public static void doStudyShapes(ArrayList<QuoteShort> data,
			int h1,int h2,
			int barSize,
			int maxTrades,
			int offset
			){
		
		int lastDay = -1;
		int dayTrades = 0;
		int actualOpen = 0;
		int accDiff = 0;
		int accDiff1 = 0;
		int accDiff2 = 0;
		int count = 0;
		int count0_5=0;
		int count1_5=0;
		int count2_5=0;
		Calendar cal = Calendar.getInstance();
		
		for (int i=0;i<data.size()-3;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort q2 = data.get(i+2);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (day!=lastDay){
				dayTrades = 0;
				lastDay = day;
				actualOpen = q.getOpen5();
			}
			
			int diffHOpen = q.getHigh5()-actualOpen;
			int diffLOpen = actualOpen-q.getLow5();
			
			if (dayTrades<maxTrades 
					&& h>=h1 && h<=h2
					//&& barSize>=abarSize
					){
				int diffHO = q.getHigh5()-q.getOpen5();
				int diffOL = q.getOpen5()-q.getLow5();
				int diffHC = q.getHigh5()-q.getClose5();
				int diffCL = q.getClose5()-q.getLow5();
				
				int diffHL1 = q.getHigh5()-q1.getLow5();
				int diffLH1 = q1.getHigh5()-q.getLow5();
				
				int diffHL2 = q.getHigh5()-q2.getLow5();
				int diffLH2 = q2.getHigh5()-q.getLow5();
				
				if (diffHO>=barSize*10 & diffHO<=barSize*10+50
						&& diffHOpen>=offset*10
						){
					accDiff+=diffHC;
					accDiff1+=diffHL1;
					accDiff2+=diffHL2;
					count++;
					if (diffHC>=50) count0_5++;
					if (diffHL1>=50) count1_5++;
					if (diffHL2>=50) count2_5++;
				}
				if (diffOL>=barSize*10 & diffOL<=barSize*10+50
						&& diffLOpen>=offset*10
						){
					accDiff+=diffCL;
					accDiff1+=diffLH1;
					accDiff1+=diffLH2;
					count++;
					if (diffCL>=50) count0_5++;
					if (diffLH1>=50) count1_5++;
					if (diffLH2>=50) count2_5++;
				}
			}			
		}
		
		double avg = accDiff*0.1/count;
		double avg1 = accDiff1*0.1/count;
		double win0_5 = count0_5*100.0/count;
		double win1_5 = count1_5*100.0/count;
		double win2_5 = count2_5*100.0/count;
		System.out.println(
				h1+" "+h2
				+" "+barSize
				+" "+offset
				+" "+maxTrades
				+" || "+count
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avg1, false)
				+" || "
				+PrintUtils.Print2dec(win0_5, false)
				+" "+PrintUtils.Print2dec(win1_5, false)
				+" "+PrintUtils.Print2dec(win2_5, false)
				);
		
	}
	
	public static void doStudy(ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int h1,int h2,int offset,
			int predictionSize,
			int maxTrades,
			int abarSize
			){
		
		int lastDay = -1;
		int dayTrades = 0;
		int actualOpen = 0;
		int accDiff = 0;
		int count = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (day!=lastDay){
				dayTrades = 0;
				lastDay = day;
				actualOpen = q.getOpen5();
			}
			
			int barSize =Math.abs(q.getHigh5()-q.getLow5());
			int shapeHC =Math.abs(q.getHigh5()-q.getClose5());
			int shapeCL =Math.abs(q.getClose5()-q.getLow5());
			if (dayTrades<maxTrades 
					&& h>=h1 && h<=h2
					//&& barSize>=abarSize
					){
				//QuoteShort mm = maxMins.get(i);
				int diffH = q.getHigh5()-actualOpen;
				int diffL = actualOpen-q.getLow5();
								
				if (diffH>=offset*10
						&& shapeHC>=abarSize
						){
					QuoteShort res = TradingUtils.getMaxMinShort(data, i+1, i+predictionSize);
					//int diffH2 = res.getHigh5()-q1.getOpen5();
					//int diffL2 = q1.getOpen5()-res.getLow5();
					int diffH2 = res.getClose5()-q1.getOpen5();
					int diffL2 = q1.getOpen5()-res.getClose5();
					accDiff += diffL2-diffH2;
					count++;
					dayTrades++;
					//System.out.println("HIGH actualOpen q1.getOpen close5 diff : "+actualOpen+" "+q.getHigh5()+" "+q1.getOpen5()+" "+res.getClose5()+" "+(diffL2-diffH2));
				}else if (diffL>=offset*10
						&& shapeCL>=abarSize
						){
					QuoteShort res = TradingUtils.getMaxMinShort(data, i+1, i+predictionSize);
					//int diffH2 = res.getHigh5()-q1.getOpen5();
					//int diffL2 = q1.getOpen5()-res.getLow5();
					int diffH2 = res.getClose5()-q1.getOpen5();
					int diffL2 = q1.getOpen5()-res.getClose5();
					accDiff += diffH2-diffL2;
					count++;
					//System.out.println("LOW actualOpen q1.getOpen close5 diff : "+actualOpen+" "+q.getLow5()+" "+q1.getOpen5()+" "+res.getClose5()+" "+(diffH2-diffL2));
					dayTrades++;
				}				
			}			
		}
		
		double avg = accDiff*0.1/count;
		System.out.println(
				h1+" "+h2
				+" "+offset
				+" "+predictionSize
				+" "+maxTrades
				+" || "+count+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";

		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
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
			
			for (int maxTrades=1;maxTrades<=1;maxTrades++){
				for (int offset=40;offset<=40;offset+=5){
					for (int predictionSize=120;predictionSize<=120;predictionSize+=5){
						for (int h1=0;h1<=23;h1++){
							int h2 = h1+23;
							for (int barSize=15;barSize<=15;barSize+=10){
								//AsianReverse.doStudy(data, null, h1, h2, offset, predictionSize, maxTrades,barSize);
								AsianReverse.doStudyShapes(data, h1, h2, barSize, maxTrades,offset);
							}
						}
						
					}
				}
			}
		}
	}

}
