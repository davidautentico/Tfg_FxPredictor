package drosa.experimental.basicStrategies;

import java.util.ArrayList;

import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.GlobalStats;
import drosa.experimental.MaxMinRisk;
import drosa.experimental.EAS.TestEAs;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestAlgo1 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String config507161="155,144,450,710,520,240,140,380,720,220,0,7500,250,0,600,0,0,0,0,0,340,240,0,1200";
			//String path5m0  = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2015.02.22.csv";
			//String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.12.31_2015.04.13.csv";
			//String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.12.31_2015.04.06.csv";
			String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2011.12.31_2012.03.30.csv";
			String path5m1   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.01.31_2012.04.29.csv";
			String path5m2   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.02.29_2012.05.30.csv";
			String path5m3   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.03.31_2012.06.29.csv";
			String path5m4   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.04.30_2012.07.30.csv";
			String path5m5   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.05.31_2012.08.30.csv";
			String path5m6   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.06.30_2012.09.29.csv";
			String path5m7   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.07.31_2012.10.30.csv";
			String path5m8   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.08.31_2012.11.29.csv";
			String path5m9   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.09.30_2012.12.30.csv";
			String path5m10   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.01.31_2013.04.29.csv";
			String path5m11   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.02.28_2013.05.30.csv";
			String path5m12   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.03.31_2013.06.29.csv";
			String path5m13   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.04.30_2013.07.30.csv";
			String path5m14   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.05.31_2013.08.30.csv";
			String path5m15   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.06.30_2013.09.29.csv";
			String path5m16   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.07.31_2013.10.30.csv";
			String path5m17   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.08.31_2013.11.29.csv";
			String path5m18   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.09.30_2013.12.30.csv";
			String path5m19   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.01.31_2014.04.29.csv";
			String path5m20   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.02.28_2014.05.30.csv";
			String path5m21   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.03.31_2014.06.29.csv";
			String path5m22   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.04.30_2014.07.30.csv";
			String path5m23   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.05.31_2014.08.30.csv";
			String path5m24   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.06.30_2014.09.29.csv";
			String path5m25   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.07.31_2014.10.30.csv";
			String path5m26   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.08.31_2014.11.29.csv";
			String path5m27   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.09.30_2014.12.30.csv";
			String path5m28   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.10.31_2015.01.30.csv";
			String path5m29   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.11.30_2015.02.27.csv";
			String path5m30   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.11.30_2015.04.21.csv";
			
			String path5m31   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2011.12.31_2012.06.29.csv";
			String path5m32   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.06.30_2012.12.30.csv";
			String path5m33   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.12.31_2013.06.29.csv";
			String path5m34   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.06.30_2013.12.30.csv";
			String path5m35   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.12.31_2014.06.29.csv";
			String path5m36   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.06.30_2014.12.30.csv";
			
			String path5m37   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2004.12.30.csv";
			String path5m38   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2004.12.31_2005.12.30.csv";
			String path5m39   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2005.12.31_2006.12.30.csv";
			
			
			String path5m40   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2004.01.01_2004.12.31.csv";
			String path5m41   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2005.01.01_2005.12.31.csv";
			String path5m42   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2006.01.01_2006.12.31.csv";
			String path5m43   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2007.01.01_2007.12.31.csv";
			String path5m44   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.01.01_2008.12.31.csv";
			String path5m45   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2009.01.01_2009.12.31.csv";
			String path5m46   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2010.01.01_2010.12.31.csv";
			String path5m47   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2011.01.01_2011.12.31.csv";
			String path5m48   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.01.01_2012.12.31.csv";
			String path5m49   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.01.01_2013.12.31.csv";
			String path5m50   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.01.01_2015.08.15.csv";
			String path5m51   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2004.01.01_2015.08.15.csv";
			String path5m52   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2014.01.01_2015.08.15.csv";
			//String path5m53   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2004.01.01_2015.08.15.csv";
			String path5m53   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.08.20.csv";
			String path5m54   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2007.01.01_2011.12.31.csv";
			//String path5m50   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.12.31_2015.04.24.csv";
			//String path5m50   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.12.31_2015.04.25.csv";
			//String path5m51   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.12.31_2015.06.05.csv";
			//String path5m51   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.06.05.csv";
			//String path5m51   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.08.29_2015.06.04.csv";
			//String path5m51   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.04.28.csv";
			//String path5m50   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.08.27_2015.04.23.csv";
			
			//String path5m51   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2006.12.31_2015.07.30.csv";
			//String path5m52   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2012.12.31_2015.05.02.csv";
			//String path5m53   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2010.12.31_2015.04.24.csv";
			//String path5m54   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2011.12.31_2015.04.24.csv";
			String path5m55   = "c:\\fxdata\\EURUSD_5 Mins_Bid_2004.01.01_2019.02.27.csv";
			String path5m56   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.12.31_2015.04.24.csv";
			//pepper
			String path5m57   = "c:\\fxdata\\EURUSD5_pepper_2013_08_29_2015_09_01.csv";
			String path5m58   = "c:\\fxdata\\EURUSD5_pepper_2013_08_29_2015_05_29.csv";
			//String path5m59   = "c:\\fxdata\\EURUSD5_pepper_2014_01_01_2015_05_08.csv";
			
			String path5m59   = "C:\\fxdata\\EURUSD5_pepper_2014_01_01_2014_12_31.csv";
			String path5m60   = "c:\\fxdata\\EURUSD5_pepper_2014_12_24_2015_10_29.csv";
			String path5m61   = "c:\\fxdata\\EURUSD5_pepper_2012_11_30_2015_04_21.csv";
			String path5m62   = "c:\\fxdata\\EURUSD5_pepper_2014_01_01_2015_09_01.csv";	
			String path5m63   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2009.01.01_2015.05.27.csv";
			String path5m64   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.05.27.csv";
			String path5m65   = "c:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2008.12.31_2015.04.21.csv";
			
			ArrayList<String> paths = new ArrayList<String>();
			paths.add(path5m0);
			paths.add(path5m1);
			paths.add(path5m2);
			paths.add(path5m3);
			paths.add(path5m4);
			paths.add(path5m5);
			paths.add(path5m6);
			paths.add(path5m7);
			paths.add(path5m8);
			paths.add(path5m9);
			paths.add(path5m10);
			paths.add(path5m11);
			paths.add(path5m12);
			paths.add(path5m13);
			paths.add(path5m14);
			paths.add(path5m15);
			paths.add(path5m16);
			paths.add(path5m17);
			paths.add(path5m18);
			paths.add(path5m19);
			paths.add(path5m20);
			paths.add(path5m21);
			paths.add(path5m22);
			paths.add(path5m23);
			paths.add(path5m24);
			paths.add(path5m25);
			paths.add(path5m26);
			paths.add(path5m27);
			paths.add(path5m28);
			paths.add(path5m29);
			paths.add(path5m30);
			paths.add(path5m31);
			paths.add(path5m32);
			paths.add(path5m33);
			paths.add(path5m34);
			paths.add(path5m35);
			paths.add(path5m36);
			paths.add(path5m37);
			paths.add(path5m38);
			paths.add(path5m39);
			paths.add(path5m40);
			paths.add(path5m41);
			paths.add(path5m42);
			paths.add(path5m43);
			paths.add(path5m44);
			paths.add(path5m45);
			paths.add(path5m46);
			paths.add(path5m47);
			paths.add(path5m48);
			paths.add(path5m49);
			paths.add(path5m50);
			paths.add(path5m51);
			paths.add(path5m52);
			paths.add(path5m53);
			paths.add(path5m54);
			paths.add(path5m55);
			paths.add(path5m56);
			paths.add(path5m57);
			paths.add(path5m58);
			paths.add(path5m59);
			paths.add(path5m60);
			paths.add(path5m61);
			paths.add(path5m62);
			paths.add(path5m63);
			paths.add(path5m64);
			for (int i=55;i<=55;i+=1){
				String provider ="";
				Sizeof.runGC ();
				ArrayList<QuoteShort> dataI 		= null;
				ArrayList<QuoteShort> dataS 		= null;
				String path5m = paths.get(i);
				System.out.println(path5m);
				if (path5m.contains("pepper")){
					//dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
					//dataS 		= dataI;
					provider="pepper";
				}else{
					dataI 		= DAO.retrieveDataShort5m(path5m, DataProvider.DUKASCOPY_FOREX4);									
					dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
									
					//data = data5m;
					provider="dukasc";
				}								
				ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS);  			  		
				//ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
				ArrayList<QuoteShort> data = null;
				data = data5m;
				System.out.println("total data: "+data.size());
				//data = hourlyDataS;
				
				String header ="";
				if (path5m.contains("EURUSD")) header="EURUSD";
				if (path5m.contains("GBPUSD")) header="GBPUSD";
				if (path5m.contains("AUDUSD")) header="AUDUSD";
				if (path5m.contains("GBPAUD")) header="GBPAUD";
				if (path5m.contains("EURGBP")) header="EURGBP";
				if (path5m.contains("EURAUD")) header="EURAUD";
				if (path5m.contains("USDCAD")) header="USDCAD";
				if (path5m.contains("AUDNZD")) header="AUDNZD";
				
				//QuoteShort.saveToDisk(data5mS,"c:\\data5digits.csv");
				int begin = data.size()-400000;
				begin     = 1;
				int end   = data.size();
				
				begin   =  1;
				end     =  900000;
				
				//para 1 minuto
				//begin   =  2000000;
				//end     =  8900000;
				
				int boxes = 1;
				if (end>data.size()) end = data.size();
				int boxSpread = (end-begin)/boxes;
				int binSize = end-begin;
				//binSize = MainEATest.MONTHS_12;//3m = 17280,34560
				
				int h1 = 0;
				int h2 = 23;
				int dayWeek1 = Calendar.MONDAY+1;
				int dayWeek2 = Calendar.MONDAY+1;
				
				GlobalStats globalStats = new GlobalStats();
				ArrayList<MaxMinRisk> maxMinRisks = new ArrayList<MaxMinRisk>();
				ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
				//System.out.println("calculados maxMinsAbsolutos: "+maxMinsExt.size());
				
				int total = 0;
				int totalLessZero = 0;
				int totalUp15 = 0;
				int totalUp2 = 0;
				int totalUp5 = 0;
				int totalUp10 = 0;
				int totalUp20 = 0;
				int totalUp50 = 0;
				int totalUp100 = 0;
				int totalUp500 = 0;
				int extraBalances = 0;
				String totalHeader="";
				double comm = 1.4;
				double maxLeverageAllowed = 50;
				double balance = 4000;
				dayWeek1 = Calendar.MONDAY+0;
				dayWeek2 = Calendar.MONDAY+4;
				
				ArrayList<Integer> nBarsH = new ArrayList<Integer>();
				for (int m=0;m<=23;m++) nBarsH.add(99999999);
				nBarsH.set(0, 132);nBarsH.set(1, 156);nBarsH.set(2, 444);
				nBarsH.set(3, 720);nBarsH.set(4, 768);nBarsH.set(5, 720);
				nBarsH.set(6, 72);nBarsH.set(7, 252);nBarsH.set(8, 564);
				nBarsH.set(9, 204);nBarsH.set(11, 7500);
				nBarsH.set(12, 250);nBarsH.set(14, 600);
				nBarsH.set(20, 280);nBarsH.set(21, 240);
				nBarsH.set(23, 144);
				
				//MainEATest.loadBarsConfig(nBarsH,config507161);
	
				//Para 1 minuto
				//for (double slATR=0.10;slATR<=0.10;slATR+=0.01){
					for (double tpATR=0.15;tpATR<=0.15;tpATR+=0.01){
					//for (double tpATR=slATR*2;tpATR<=slATR*2;tpATR+=1*slATR){
						//for (double slATR=0.24;slATR<=0.24;slATR+=0.10){
							for (double slATR=3.0*tpATR;slATR<=3.0*tpATR;slATR+=0.5*tpATR){
						//for (double slATR=0.10;slATR<=0.30;slATR+=0.01){
						double factor = slATR/tpATR;
						String header1 = PrintUtils.Print2(tpATR)+" "+PrintUtils.Print2(slATR);
						total = 0;
						totalLessZero = 0;
						totalUp15 = 0;
						totalUp2 = 0;
						totalUp5 = 0;
						totalUp10 = 0;
						totalUp20 = 0;
						totalUp50 = 0;
						totalUp100 = 0;
						totalUp500 = 0;
						totalHeader=PrintUtils.Print2dec(tpATR,false,2)+" "+PrintUtils.Print2dec(slATR,false,2);
						for (int b = 0;b<boxes;b++){
							int begin1 = begin+b*binSize;
							int end1   = begin1+binSize;
							//System.out.println(begin1+" "+end1+" "+b*boxSpread+" "+b+" "+boxSpread);
							if (end1>data.size()){
								b=boxes-1;
								end1= data.size()-1;
								begin1 = end1-binSize;
							}
							if (b==boxes-1){
								end1= data.size()-1;
								begin1 = end1-binSize;
								//System.out.println(begin1+" "+end1);
							}
							for (int nBars=12;nBars<=12;nBars+=12){ //3800 para nueva versión	
								for (int h4=23;h4<=23;h4++){
									//nBarsH.set(h4, nBars);//prueba
									//System.out.println(" ");
									//for (int hr=0;hr<=23;hr+=1) nBarsH.set(hr, nBars);//prueba
									//MaxMinRisk.load(maxMinRisks,"5000 11.0"+","+String.valueOf(nBars)+" 8.0");
									String header2 = h4+" "+nBars;
									for (double maxLeverage=400;maxLeverage<=400;maxLeverage+=10)
									for (double risk=0.25;risk<=5.0;risk+=0.25){
										for (double extraRisk=1.83*risk;extraRisk<=1.83*risk;extraRisk+=1.6){
										//for (double extraRisk=2.0;extraRisk<=2.0;extraRisk+=1.6){
										//for (double extraRisk=0.0;extraRisk<=0.0;extraRisk+=0.5){
											header=" "+provider+" "+PrintUtils.Print2dec(risk,false,2)+" "+PrintUtils.Print2dec(extraRisk,false,2)
													+" "+nBars;
											MaxMinRisk.load(maxMinRisks,String.valueOf(nBars)+" "+String.valueOf(risk));
											for (h1=17;h1<=17;h1++){
												h2=h1+0;
												for (int nATR=5;nATR<=5;nATR+=1){
													//for (double tpATR=0.11;tpATR<=0.11;tpATR+=0.01){
														//for (int sl=(int) (tp*3.0);sl<=tp*3.0;sl+=tp*0.5){
														//for (double slATR=1.5*tpATR;slATR<=3.0*tpATR;slATR+=0.01){
														//for (double slATR=0.25;slATR<=0.25;slATR+=0.01){
															//for (int pips=22;pips<=22;pips+=1){
															for (double pipsATR=0.20;pipsATR<=0.20;pipsATR+=0.01){	//0.14 0.31 0.2
																for (int expiration=5;expiration<=5;expiration+=1){
																	for (int maxAllowed=5;maxAllowed<=5;maxAllowed+=1){
																		for (double factorTP=10.0;factorTP<=10;factorTP+=0.1){
																		for (int minimumPips = 10;minimumPips<=10;minimumPips++){	
																			for (int minBarRange1 = 0;minBarRange1<=0;minBarRange1+=10){	
																				int minBarRange2= minBarRange1+9999;
																				for (comm=2.0;comm<=2.0;comm+=0.1){
																					double pf=0.0;
																					for (int hClose1=11;hClose1<=11;hClose1++){
																						int hClose2 = hClose1+0;
																						//for (int month1=Calendar.JANUARY;month1<=Calendar.JANUARY+11;month1++){
																							//int month2=month1+0;
																						double yieldacc = 0;
																						int totalYield = 0;
																						int totalPos = 0;
																						for (int y1=2015;y1<=2015;y1+=1){
																							int y2=y1+4;
																							totalYield++;
																							for (dayWeek1=Calendar.MONDAY;dayWeek1<=Calendar.MONDAY;dayWeek1++){
																								dayWeek2 = dayWeek1+4;
																								for (int maxMinutes=5;maxMinutes<=5;maxMinutes+=5){
																									//int dayMonth2=dayMonth1+0;
																									//for (int y1=2014;y1<=2016;y1+=1){
																									for (int month1=Calendar.JANUARY+0;month1<=Calendar.JANUARY+0;month1+=1){
																										int month2=month1+11;																									
																										for (double maxPerProfitThr = 0;maxPerProfitThr<=0;maxPerProfitThr+=5){
																											for (double maxPerLossThr = 10 ; maxPerLossThr<=10;maxPerLossThr+=5){
																												for (int slTime=500;slTime<=500;slTime+=10){
																													for (int extraTotalOpen=5;extraTotalOpen<=5;extraTotalOpen++){
																														for (int extraParamHour=9;extraParamHour<=9;extraParamHour++){
																															pf = TestEAs.EA_tradingReverseAsianATR(header, data, 
																																	maxMinsExt, begin1, end1,y1,y2, 
																																	//"0 1 2 3 4 5 6 7 8 9 11 12 14 20 21 23", dayWeek1, dayWeek2, 
																																	//"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23", dayWeek1, dayWeek2, 
																																	"0 1 2 3 4 5 6 7 8 9 23", 
																																	//"11 12 14 20 21", dayWeek1, dayWeek2, 
																																	//"0 1 2 3 4 5 6 7 8 9 23", 
																																	//"0 1 2 23",
																																	//String.valueOf(h1),
																																	hClose1,hClose2,
																																	month1,month2,
																																	maxMinutes,
																																	dayWeek1, dayWeek2,
																																	//String.valueOf(h1),
																																	//"0 9 23", dayWeek1, dayWeek2,
																																	nBarsH, 0.7, 0.7,20, 
																																	nATR, slATR, tpATR,minimumPips,
																																	minBarRange1,minBarRange2,
																																	expiration, maxAllowed, 
																																	slTime,
																																	balance, risk, extraRisk,maxLeverage, 
																																	maxPerProfitThr,
																																	maxPerLossThr,
																																	false,extraTotalOpen,extraParamHour,
																																	comm,false,globalStats);//5%-16% 5-0.11-0.25-5-5
																															
																															//globalStats.analysis3(header1,factor);
																															//globalStats.analysis2(globalStats,2,false);
																															//GlobalStats.analysis(data,globalStats);
																															//globalStats.analysis5(data,globalStats,300);
																															yieldacc += pf;
																															if (pf>=1.0) totalPos++;
																															total++;
																															if (pf<1.5) totalLessZero++;
																															if (pf>=1.5) totalUp15++;
																															if (pf>=2.0) totalUp2++;
																															if (pf>=5.0) totalUp5++;
																															if (pf>=10.0) totalUp10++;
																															if (pf>=20.0) totalUp20++;
																															if (pf>=50.0) totalUp50++;
																															if (pf>=100.0) totalUp100++;
																															if (pf>=500.0) totalUp500++;
																															if (globalStats.getBalanceNeeded()>balance) extraBalances++;
																														}//extraparamhour
																													}//extraparammode
																												}
																											}
																										}
																									}
																								}
																							}//dayweek
																						}//year
																						//System.out.println(header2+" || "+totalPos+" "+PrintUtils.Print2dec(yieldacc/totalYield, false));
																					}//hClose1
																				}//comm
																			}//minBarRange
																		}
																		}
																	}
																}
															}//pipsATR
														//}//sl
													//}//tp
												}//Natr
											}//h1
										}//extraRisk
									}
								}
							}//nbars
						}//boxes
						boxes = total;
						//System.out.println("extraBalances: "+extraBalances+" "+PrintUtils.Print2(extraBalances*100.0/total));
						/*System.out.println("binSize boxes <1.0 >1.5 >2.0 >5.0 >10.0 >20.0 >50.0 >100.0 >500.0 || "
								+totalHeader+" "
								+boxes+" "
								+PrintUtils.Print2Int(binSize, 6)+" "
								+PrintUtils.Print2(totalLessZero*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp15*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp2*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp5*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp10*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp20*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp50*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp100*100.0/boxes)+" "
								+PrintUtils.Print2(totalUp500*100.0/boxes)+" "
								);*/
					}//sl
				}//tp
			}
	}

}
