package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrendStudy3 {
	
	public static void studyNext(ArrayList<Trend> trends,int minSize,int h1,int h2,int next){
		
		ArrayList<Double> avgs0 = new ArrayList<Double>();
		ArrayList<Double> avgs = new ArrayList<Double>();
		Calendar cal0 = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		double days = 0;
		
		for (int i=0;i<trends.size()-next;i++){
			double size0 = trends.get(i).getSize();
			double sizen = trends.get(i+next).getSize();
			int h = trends.get(i).getHour1(cal0);
			if (size0>=minSize && h>=h1 && h<=h2){
				//System.out.println(size0+" "+sizen);
				avgs.add(sizen);
			}
		}
		MathUtils.summary_mean_sd("trends "+minSize+" "+h1+" "+h2+" "+next, avgs);
	}
	
	public static void studyAvgs(ArrayList<Trend> trends,int period){
	
		ArrayList<Double> avgs0 = new ArrayList<Double>();
		ArrayList<Double> avgs = new ArrayList<Double>();
		Calendar cal0 = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		double days = 0;
		for (int i=period;i<trends.size();i++){
			avgs.clear();
			for (int j=i-period;j<=i;j++){
				avgs.add(trends.get(j).getSize());
			}
			QuoteShort.getCalendar(cal0, trends.get(i-period).getQ1());
			QuoteShort.getCalendar(cal1, trends.get(i).getQ1());
			days = (cal1.getTimeInMillis()-cal0.getTimeInMillis())/(86400*1000);
			double avg = MathUtils.averageD(avgs, 0, avgs.size());
			avgs0.add(avg);
			
			/*System.out.println(avgs0.size()
					+" "+PrintUtils.Print2dec(avg, false)
					+" || "+MathUtils.printArray(avgs)
					);*/
		}
		MathUtils.summary_mean_sd("trends avg "+period+" "+days, avgs0);
	}

	public static void main(String[] args) throws Exception {
		
		String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.06.03.csv";
		
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathEURJPY);
		paths.add(pathGBPUSD);paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
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
			ArrayList<Double> sizes = new ArrayList<Double>();
			for (int minSize=20;minSize<=20;minSize++){
				int thr = minSize;
				int tokenH = 23;
				ArrayList<Trend> trends = Trend.calculateTrends(data, 0, data.size()-1, minSize,thr,tokenH,true);
				
				
				/*for (int period = 10;period<=10;period+=1){
					TrendStudy3.studyAvgs(trends, period);
					
				}*/
				//Trend.printSummaryDaily("", trends, 0, 23);
				/*for (int testSize=40;testSize<=40;testSize++){
					for (int h1=0;h1<=0;h1++)
						TrendStudy3.studyNext(trends, testSize,h1,h1+9, 1);
				}*/
				/*for (int sizeThr = 1*minSize;sizeThr<=minSize*4;sizeThr++){
					sizes.clear();
					int total1 = 0;
					for (int t=0;t<trends.size()-3;t++){
						double size0 = trends.get(t).getSize();
						double size1 = trends.get(t+1).getSize();
						double size2 = trends.get(t+2).getSize();
						
						if (size0>=sizeThr){
							sizes.add(size1);
							if (size1>=1*sizeThr){								
								total1++;
								
							}else{
								
							}
							//System.out.println(size0+" "+size1+" || "+sizes.size()+" "+total1);
						}
						
					}
					int totalT = trends.size();
					int total0 = sizes.size();
					double per0 = total0*100.0/totalT;
					double per = total1*100.0/total0;
					System.out.println(
							"size "+sizeThr+" "+1*sizeThr
							+" || "
							+" "+totalT
							+" "+total0+" "+total1
							+" "+PrintUtils.Print2dec(per0, false)
							+" "+PrintUtils.Print2dec(per, false)
							);
					//MathUtils.summary_mean_sd("size "+sizeThr, sizes);
				}//sizeThr*/
			}
			
		}//limit

	}

}
