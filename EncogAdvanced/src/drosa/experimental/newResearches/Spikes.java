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

public class Spikes {
	
	public static void doStudyTrade(ArrayList<QuoteShort> data,
			int h1,int h2,
			int barSize,
			int offset,
			int tp,int sl,
			int maxTrades){
	
		int lastDay = -1;
		int actualOpen = -1;
		Calendar cal = Calendar.getInstance();
		Calendar sourceCal = Calendar.getInstance();
		int dayTrades = 0;
		int actualTrade = 0;
		int actualBarOpen = 0;
		int actualBarH = -1;
		int actualBarL = -1;
		int actualBarSize = 0;
		int actualBarMin = -1;
		int actualEntry = 0;
		int count = 0;
		int count0 = 0;
		int count1_5=0;
		int acc0 = 0;
		int acc1 = 0;
		int maxDiff = -1;
		int index = 0;
		int wins = 0;
		int losses = 0;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			//detector nuevos dias
			if (day!=lastDay){
				actualOpen = q.getOpen5();
				lastDay = day;
				//System.out.println("[DIA] "+q.toString());
				dayTrades = 0;
				actualTrade = 0;
				actualEntry = -1;
			}	
			
			//detector nuevas barras de 5min
			if (min%5==0 && min!=actualBarMin){				
				actualBarOpen = q.getOpen5();
				actualBarH = -1;
				actualBarL = -1;
				actualBarMin = min;		
				actualTrade = 0;
			}
			

			if (actualTrade==0){
				if (h>=h1 && h<=h2
						&& dayTrades<maxTrades){
					int diffH = q.getHigh5()-actualOpen;
					int diffL = actualOpen-q.getLow5();
					//System.out.println(q.toString()+" || "+actualOpen+" "+diffH);					
					if (diffH>=offset*10
							){
						int actualHO = q.getHigh5()-actualBarOpen; 
						if (actualHO>=barSize*10){
							int tpi = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1,q.getHigh5()-tp*10, false);
							int sli = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1,q.getHigh5()+sl*10, true);
							actualTrade = 1;
							dayTrades++;
							if (sli!=-1){
								if (tpi==-1){
									losses++;
									count++;
								}else if (sli<=tpi){
									losses++;
									count++;
								}else{
									wins++;
									count++;
								}
							}else{
								if (tpi!=-1){
									wins++;
									count++;
								}
							}

						}
					}else if (diffL>=offset*10){
						int actualOL= actualBarOpen-q.getLow5(); 
						if (actualOL>=barSize*10){
							int tpi = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1,q.getLow5()+tp*10, true);
							int sli = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1,q.getLow5()-sl*10, false);
							actualTrade = -1;
							dayTrades++;
							if (sli!=-1){
								if (tpi==-1){
									losses++;
									count++;
								}else if (sli<=tpi){
									losses++;
									count++;
								}else{
									wins++;
									count++;
								}
							}else{
								if (tpi!=-1){
									wins++;
									count++;
								}
							}
						}
					}
				}
			}
		}
		
		double avg = (wins*tp-losses*sl)*1.0/count;
		double winPer = wins*100.0/count;
		System.out.println(
				h1+" "+h2
				+" "+offset+" "+barSize
				+" "+tp+" "+sl
				+" || "
				+" "+count
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(avg, false)
				+" || "+PrintUtils.Print2(avg*count, false)
				);
	}

	
	public static void doStudy(ArrayList<QuoteShort> data,ArrayList<QuoteShort> data5m,
			int h1,int h2,
			int barSize,
			int offset,int maxTrades){
	
		int lastDay = -1;
		int actualOpen = -1;
		Calendar cal = Calendar.getInstance();
		Calendar sourceCal = Calendar.getInstance();
		int dayTrades = 0;
		int actualTrade = 0;
		int actualBarOpen = 0;
		int actualBarH = -1;
		int actualBarL = -1;
		int actualBarSize = 0;
		int actualBarMin = -1;
		int actualEntry = 0;
		int count = 0;
		int count0 = 0;
		int count1_5=0;
		int acc0 = 0;
		int acc1 = 0;
		int maxDiff = -1;
		int index = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			//detector nuevos dias
			if (day!=lastDay){
				actualOpen = q.getOpen5();
				lastDay = day;
				//System.out.println("[DIA] "+q.toString());
				dayTrades = 0;
				actualTrade = 0;
				actualEntry = -1;
			}	
			
			//detector nuevas barras de 5min
			if (min%5==0 && min!=actualBarMin){
				//System.out.println(q.toString());
				//para estadisticas en la misma barra 
				if (actualTrade==1){
					index = TradingUtils.getMinuteIndex(data5m,sourceCal, cal,index);
					QuoteShort q5 = data5m.get(index);
					acc0+=maxDiff;
					count0++;
					maxDiff=-1;
					
					int maxDiff1 = actualEntry-q5.getLow5();
					if (maxDiff1>=maxDiff){
						acc1+=maxDiff1;
						if (maxDiff1>=50) count1_5++;
					}
					else{
						acc1+=maxDiff;
						if (maxDiff>=50) count1_5++;
					}
					//System.out.println(q.toString()+" || "+q5.toString());
				}else if (actualTrade==-1){
					//index = TradingUtils.findQuoteShort(data5m, cal, index);
					index = TradingUtils.getMinuteIndex(data5m,sourceCal, cal,index);
					QuoteShort q5 = data5m.get(index);
					acc0+=maxDiff;
					count0++;
					maxDiff=-1;
					
					int maxDiff1 = q5.getHigh5()-actualEntry; 
					if (maxDiff1>=maxDiff){
						acc1+=maxDiff1;
						if (maxDiff1>=50) count1_5++;
					}
					else{
						acc1+=maxDiff;
						if (maxDiff>=50) count1_5++;
					}
				}
				actualBarOpen = q.getOpen5();
				actualBarH = -1;
				actualBarL = -1;
				actualBarMin = min;		
				actualTrade = 0;
			}
			

			if (actualTrade==0){
				if (h>=h1 && h<=h2
						&& dayTrades<maxTrades){
					int diffH = q.getHigh5()-actualOpen;
					int diffL = actualOpen-q.getLow5();
					//System.out.println(q.toString()+" || "+actualOpen+" "+diffH);					
					if (diffH>=offset*10
							){
						int actualHO = q.getHigh5()-actualBarOpen; 
						if (actualHO>=barSize*10){
							//System.out.println("[HIGH] "+q.toString()+" || "+actualOpen+" "+diffH+" "+actualHO);
							actualEntry = q.getHigh5();
							actualTrade=1;//es high
							count++;
						}
					}else if (diffL>=offset*10){
						int actualOL= actualBarOpen-q.getLow5(); 
						if (actualOL>=barSize*10){
							//System.out.println("[LOW] "+q.toString()+" || "+actualOpen+" "+diffL+" "+actualOL);
							actualEntry = q.getLow5();
							actualTrade=-1; //es low
							count++;
						}
					}
				}
			}else if (actualTrade==1){
				int actualDiff = actualEntry-q.getLow5();
				if (maxDiff==-1 || actualDiff>=maxDiff)
					maxDiff = actualDiff;
			}else if (actualTrade==-1){
				int actualDiff = q.getHigh5()-actualEntry;
				if (maxDiff==-1 || actualDiff>=maxDiff)
					maxDiff = actualDiff;
			}
			
		}
		
		double avg = acc0*0.1/count0;
		double avg1 = acc1*0.1/count0;
		double winPer = count1_5*100.0/count0;
		System.out.println(offset+" "+barSize
				+" || "
				+" "+count0
				+" "+PrintUtils.Print2(avg, false)
				+" "+PrintUtils.Print2(avg1, false)
				+" "+PrintUtils.Print2(winPer, false)
				);
	}

	public static void main(String[] args) throws Exception {
		
		String pathEURUSD5 = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.04.07.csv";
		
		ArrayList<Quote>	dataI1 		= DAO.retrieveData(pathEURUSD5, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote>    dataS1 		= TestLines.calculateCalendarAdjusted(dataI1);									
  		ArrayList<Quote> data5m1 	= TradingUtils.cleanWeekendData(dataS1); 			  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m1);				
		ArrayList<QuoteShort> data5 = null;
		data5 = data5mS;
		
		String pathEURUSD13_1 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.01.31_2013.02.27.csv";
		String pathEURUSD13_2 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.02.28_2013.03.30.csv";
		String pathEURUSD13_3 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.03.31_2013.04.29.csv";
		String pathEURUSD13_4 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.04.30_2013.05.30.csv";
		String pathEURUSD13_5 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.05.31_2013.06.29.csv";
		String pathEURUSD13_6 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.06.30_2013.07.30.csv";
		String pathEURUSD13_7 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.07.31_2013.08.30.csv";
		String pathEURUSD13_8 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.08.31_2013.09.29.csv";
		String pathEURUSD13_9 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.09.30_2013.10.30.csv";
		String pathEURUSD13_10 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.10.31_2013.11.29.csv";
		String pathEURUSD13_11 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.11.30_2013.12.30.csv";
		String pathEURUSD14_0 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2013.12.31_2014.01.30.csv";
		String pathEURUSD14_1 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.01.31_2014.02.27.csv";
		String pathEURUSD14_2 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.02.28_2014.03.30.csv";
		String pathEURUSD14_3 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.03.31_2014.04.29.csv";
		String pathEURUSD14_4 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.04.30_2014.05.30.csv";
		String pathEURUSD14_5 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.05.31_2014.06.29.csv";
		String pathEURUSD14_6 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.06.30_2014.07.30.csv";
		String pathEURUSD14_7 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.07.31_2014.08.30.csv";
		String pathEURUSD14_8 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.08.31_2014.09.29.csv";
		String pathEURUSD14_9 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.09.30_2014.10.30.csv";
		String pathEURUSD14_10 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.10.31_2014.11.29.csv";
		String pathEURUSD14_11 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.11.30_2014.12.30.csv";
		String pathEURUSD15_0 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2014.12.31_2015.01.30.csv";
		String pathEURUSD15_1 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.01.31_2015.02.27.csv";
		String pathEURUSD15_2 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.02.28_2015.03.30.csv";
		String pathEURUSD15_3 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.03.31_2015.04.29.csv";
		String pathEURUSD15_4 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.04.30_2015.05.30.csv";
		String pathEURUSD15_5 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.05.31_2015.06.29.csv";
		String pathEURUSD15_6 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.06.30_2015.07.30.csv";
		String pathEURUSD15_7 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.07.31_2015.08.30.csv";
		String pathEURUSD15_8 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.08.31_2015.09.29.csv";
		String pathEURUSD15_9 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.09.30_2015.10.30.csv";
		String pathEURUSD15_10 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.10.31_2015.11.29.csv";
		String pathEURUSD15_11 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.11.30_2015.12.30.csv";
		String pathEURUSD16_0 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2015.12.31_2016.01.30.csv";
		String pathEURUSD16_1 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2016.01.31_2016.02.28.csv";
		String pathEURUSD16_2 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2016.02.29_2016.03.30.csv";
		String pathEURUSD16_3 = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2016.03.31_2016.04.07.csv";

		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD13_1);
		paths.add(pathEURUSD13_2);paths.add(pathEURUSD13_3);
		paths.add(pathEURUSD13_4);paths.add(pathEURUSD13_5);
		paths.add(pathEURUSD13_6);paths.add(pathEURUSD13_7);
		paths.add(pathEURUSD13_8);paths.add(pathEURUSD13_9);
		paths.add(pathEURUSD13_10);paths.add(pathEURUSD13_11);
		paths.add(pathEURUSD14_0);paths.add(pathEURUSD14_1);
		paths.add(pathEURUSD14_2);
		paths.add(pathEURUSD14_3);paths.add(pathEURUSD14_4);
		paths.add(pathEURUSD14_5);paths.add(pathEURUSD14_6);
		paths.add(pathEURUSD14_7);paths.add(pathEURUSD14_8);
		paths.add(pathEURUSD14_9);paths.add(pathEURUSD14_10);
		paths.add(pathEURUSD14_11);paths.add(pathEURUSD15_0);
		paths.add(pathEURUSD15_1);paths.add(pathEURUSD15_2);
		paths.add(pathEURUSD15_3);paths.add(pathEURUSD15_4);
		paths.add(pathEURUSD15_5);
		paths.add(pathEURUSD15_6);paths.add(pathEURUSD15_7);
		paths.add(pathEURUSD15_8);paths.add(pathEURUSD15_9);
		paths.add(pathEURUSD15_10);paths.add(pathEURUSD15_11);
		paths.add(pathEURUSD16_0);paths.add(pathEURUSD16_1);
		paths.add(pathEURUSD16_2);paths.add(pathEURUSD16_3);
		int total = 0;
		int limit = paths.size()-1;
		//limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			Sizeof.runGC ();
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			//System.out.println("data: "+data.size());
			
			for (int offset=30;offset<=30;offset+=10){
				for (int barSize=10;barSize<=10;barSize+=1){
					//Spikes.doStudy(data,data5, 0, 23, barSize,offset,1);
					for (int h2=9;h2<=9;h2++){
						int h1 = 0;
						for (int sl=80;sl<=80;sl+=1){
							Spikes.doStudyTrade(data, h1, h2, barSize,offset,10,sl,20);
						}
					}
				}
			}
			dataS.clear();
			dataI.clear();
			data.clear();
		}
			
	}

}
