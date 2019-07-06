package drosa.experimental.claudia;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class TestScalpingATR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.23.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.23.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2015.03.23.csv";
		
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.23.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.23.csv";
		String pathGBPCAD = "C:\\fxdata\\GBPCAD_UTC_5 Mins_Bid_2006.01.02_2015.03.23.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2005.10.07_2015.03.23.csv";
		String pathCADJPY= "C:\\fxdata\\CADJPY_UTC_5 Mins_Bid_2004.10.25_2015.03.23.csv";
		String pathNZDUSD= "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.08.03_2015.03.23.csv";
		String pathNZDJPY= "C:\\fxdata\\NZDJPY_UTC_5 Mins_Bid_2006.01.02_2015.03.23.csv";*/
		
		/*String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.03.23.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2003.05.04_2015.03.23.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2003.08.03_2015.03.23.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2015.03.23.csv";*/
		
		/*String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";*/
		
		for (int year=2013;year<=2013;year++){
			//int year = 2013;
			
			//String fileName = "1 Min_Bid_"+year+".csv";
			//String fileName = "1 Min_Bid_"+year+".01.01_"+year+".12.31.csv";
			//String fileName ="2004.01.01_2015.03.29.csv";//completo
			//String fileName ="2012.12.31_2015.04.04.csv";
			//String fileName ="2010.12.31_2012.12.30.csv";
			//String fileName ="2013.01.01_2015.04.05.csv";
			//String fileName ="2011.01.01_2015.04.05.csv";
			//String fileName ="2009.01.01_2011.12.31.csv";
			//String fileName ="1 Min_Bid_2015.01.01_2015.04.05.csv";
			
			//String fileName ="5 Mins_Bid_2003.05.04_2015.04.05.csv";
			//String fileName = "5 Mins_Bid_2008.12.31_2015.04.07.csv";
			//String fileName ="5 Mins_Bid_2012.12.31_2015.04.07.csv";
			//String fileName ="5 Mins_Bid_2008.12.31_2012.12.30.csv";
			//String fileName   ="1 Min_Bid_2013.01.01_2015.04.05.csv";
			//String fileName ="1 Min_Bid_2003.05.04_2008.12.30.csv";
			//String fileName ="1 Min_Bid_2008.12.31_2012.12.30.csv";
			//String fileName ="1 Min_Bid_2009.01.01_2015.04.06.csv";
			//String fileName ="1 Min_Bid_2013.12.31_2015.04.08.csv";
			//String fileName ="1 Min_Bid_2003.05.04_2015.04.08.csv";
			//String fileName ="1 Min_Bid_2012.12.31_2015.04.09.csv";
			//String fileName   ="1 Min_Bid_2011.12.31_2015.04.09.csv";
			//String fileName   ="1 Min_Bid_2010.12.31_2015.04.09.csv";
			//String fileName   ="1 Min_Bid_2003.05.04_2008.12.30.csv";
			//String fileName   ="1 Min_Bid_2008.12.31_2015.04.10.csv";
			//String fileName   ="1 Min_Bid_2012.12.31_2015.04.10.csv";
			//String fileName   ="1 Min_Bid_2010.12.31_2015.04.10.csv";
			String fileName   ="1 Min_Bid_2008.12.31_2015.04.09.csv";
			//String fileName ="1 Min_Bid_2003.05.04_2015.04.08.csv";
			
			String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_"+fileName;
			String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_"+fileName;
			String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_"+fileName;
			String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_"+fileName;
			String pathUSDCAD = "C:\\fxdata\\USDCAD_UTC_"+fileName;
			String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_"+fileName;
			String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_"+fileName;
			String pathEURGBP = "C:\\fxdata\\EURGBP_UTC_"+fileName;
			String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_"+fileName;
			String pathCADJPY = "C:\\fxdata\\CADJPY_UTC_"+fileName;
			String pathAUDJPY = "C:\\fxdata\\AUDJPY_UTC_"+fileName;
			String pathNZDJPY = "C:\\fxdata\\NZDJPY_UTC_"+fileName;
			String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_"+fileName;
			String pathGBPCAD = "C:\\fxdata\\GBPCAD_UTC_"+fileName;
			String pathGBPAUD = "C:\\fxdata\\GBPAUD_UTC_"+fileName;
			//String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_"+fileName;
			//String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_"+fileName;
			
			ArrayList<String> paths = new ArrayList<String>();
			ArrayList<String> names = new ArrayList<String>();
			//paths.add(pathEURUSD);
			paths.add(pathGBPUSD);
			paths.add(pathAUDUSD);
			paths.add(pathUSDJPY);
			//paths.add(pathUSDCAD);
			//paths.add(pathNZDUSD);
			paths.add(pathEURJPY);
			//paths.add(pathEURGBP);
			paths.add(pathEURAUD);
			//paths.add(pathCADJPY);
			//paths.add(pathAUDJPY);
			//paths.add(pathNZDJPY);
			paths.add(pathGBPJPY);
			paths.add(pathGBPCAD);
			paths.add(pathGBPAUD);

			
			int limit = paths.size()-1;
			int initial = 2;
			//limit       = 4;
			for (int i=initial;i<=limit;i++){
				String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
				//System.out.println(pairName);
				ArrayList<Quote> dataI 		= DAO.retrieveData(paths.get(i), DataProvider.DUKASCOPY_FOREX);
				ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
		  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
				ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
				ArrayList<QuoteShort> data = null;
				data = data5mS;
				//System.out.println("total data: "+data.size());
				ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
				
				int begin = 4000000;
				int end   = 5000000;
				int tp = 10;
				int sl = 20;
				double comm = 1.4;
				
				for (begin=1;begin<=1;begin+=1000000){
					end = begin +40000000;
					for (int h1=16;h1<=16;h1++){
						int h2 = h1+8;
						for (int minPips=1;minPips<=1;minPips+=5){
							for (int bars1=1;bars1<=1;bars1+=600){
								for (int bars = 250;bars<=250;bars+=10){
									//for (sl=20;sl<=20;sl+=1){
									for (int nATR=5;nATR<=5;nATR++){
										for (double tpATR=0.11;tpATR<=0.11;tpATR+=0.01){				
											//for (double slATR=0.25;slATR<=0.25;slATR+=10.0){
											for (double slATR=3*tpATR;slATR<=3*tpATR;slATR+=1.0*tpATR){
												for (int maxPositions=20;maxPositions<=20;maxPositions+=1){
													String header = pairName+" "+String.valueOf(year);
													//TestScalping.testDirectionScalp(data, maxMins, begin, end, h1, h2, sl, tp,maxPositions,minPips, bars1,bars,false);
													for (int minSeparationPips=-999999;minSeparationPips<=-999999;minSeparationPips++){
														double maxRisk = 100.0/maxPositions;
														//maxRisk = 40.0;
														maxRisk = 1.0;
														double initialRisk = maxRisk;
														//initialRisk = 0.5;
														//maxRisk = 5.0;
														for (double risk=initialRisk;risk<=maxRisk;risk+=0.50){
															for (comm=2.0;comm<=2.0;comm+=0.10){
																for (int day1=Calendar.MONDAY+0;day1<=Calendar.MONDAY+0;day1++){
																	int day2 = day1+4;
																	//TestScalping.testDirectionScalp$$(header,data, maxMins, begin, end,
																		//h1, h2,day1,day2, sl, tp,maxPositions,minPips,minSeparationPips, bars1,bars,500,risk,comm,true,true,false);
																	TestScalping.testDirectionScalp$$ATR(header,data, maxMins, begin, end,
																		h1, h2,day1,day2,nATR, slATR, tpATR,maxPositions,minPips,minSeparationPips, bars1,bars,1000,risk,comm,false,true,false);//sequencia suma +1 -1 +1 -1
																	//configuracion 100% al año con DD<40% : 6-80-30-2.5%
																	//configuracion arriesgada DD<60% 6-80-15-5%																
																	//TestScalping.testDirectionScalp$$v3(header,data, maxMins, begin, end,
																			//h1, h2,day1,day2, sl, tp,maxPositions,minPips,minSeparationPips, bars1,bars,500,risk,comm,false,true,false,false);
																}
															}//comm
															//TestScalping.testDirectionScalp$$v2(header,data, maxMins, begin, end, h1, h2, sl, tp,maxPositions,minPips,minSeparationPips, bars1,bars,5,risk,comm,true,false);
														}//risk
													}
												}//maxpositions
											}//slatr					
										}//tpatr
									}//natr
								}//bars
							}
						}
						//System.out.println(h1);
					}//h
				}
			}//paths
		}//year
	}


}
