package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.zznbrum.TrendClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestSequences {
	
	public static ArrayList<Integer> test1(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,int thr,int tp,int sl,boolean isCont,int debug){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		double avg = 60;
		int total = 0;
		int accDiff = 0;
		int wins20 = 0;
		
		int entry = 0;
		int entryTP = 0;
		int entrySL = 0;
		int mode = 0;
		int seq = 0;
		ArrayList<Integer> trades = new ArrayList<Integer> ();
		ArrayList<Integer> sequences = new ArrayList<Integer> ();
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			//if (h<h1 || h>h2) continue;
			
			int maxMin = maxMins.get(i-1);

			if (day!=lastDay){
				
			}
			
			
			if (mode==0){
				if (h>=h1 && h<=h2){
					if (maxMin>=thr){						
						entry = q.getOpen5();
						entryTP = entry-tp-20;
						entrySL = entry+sl;
						//seq = 0;
						mode = -1;						
						
						if (debug==2) {
							System.out.println("[SELL at open] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
						}
					}else if (maxMin<=-thr){
						entry = q.getOpen5();
						entryTP = entry+tp+20;
						entrySL = entry-sl;
						//seq = 0;
						mode = 1;
						if (debug==2) {
							System.out.println("[LONG at open] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
						}
					}
				}
			}else if (mode==-1){
				if (q.getHigh5()>=entrySL){
					
					if (debug==2) {
						System.out.println("[LOSS SHORT] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
					}
					
					entry = q.getClose5();
					entryTP = entry + tp+20;
					entrySL = entry - sl;
					mode = 1;
					if (!isCont){
						entryTP = entry - tp-20;
						entrySL = entry + sl;
						mode = -1;
						
						if (debug==2) {
							System.out.println("[SHORT at close] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
						}
					}
					
					
					
					//System.out.println("loss "+seq);
					if (seq>=0){//ibamos ganando
						sequences.add(seq);
						seq = -1;
					}else{
						seq--;
					}
					trades.add(-1);
					
					mode = 0;
				}else if (q.getLow5()<=entryTP){
					//System.out.println("win "+seq);
					
					if (debug==2) {
						System.out.println("[WIN SHORT] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
					}
					
					if (seq>=0){
						seq++;
					}else{
						sequences.add(seq);
						seq = 1;
					}
					mode = 0;
					trades.add(1);
				}
			}else if (mode==1){
				if (q.getLow5()<=entrySL){
					
					if (debug==2) {
						System.out.println("[LOSS LONG] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
					}
					
					entry = q.getClose5();
					entryTP = entry - tp-20;
					entrySL = entry + sl;
					mode = -1;
					if (!isCont){
						entryTP = entry + tp+20;
						entrySL = entry - sl;
						mode = 1;
						
						if (debug==2) {
							System.out.println("[LONG at close] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
						}
					}
					if (seq>=0){
						sequences.add(seq);
						seq = -1;
					}else{
						seq--;
					}
					trades.add(-1);
					mode = 0;
					//System.out.println("loss");
				}else if (q.getHigh5()>=entryTP){
					
					if (debug==2) {
						System.out.println("[WIN LONG] "+entry+" "+entryTP+" "+entrySL+" || "+q.toString());
					}
					if (seq>0){
						seq++;
					}else{
						sequences.add(seq);
						seq = 1;
					}
					mode = 0;
					trades.add(1);
				}
			}
		}
		
		int wins = 0;
		int losses = 0;
		int winsSeq = 0;
		int lossesSeq = 0;
		int maxLosses = 0;
		int limit = 15;
		int countLimit = 0;
		double maxnet = 0;
		double actualnet = 0;
		double maxdd = 0;
		double factor = sl*1.0/tp;
		for (int i=0;i<sequences.size();i++){
			seq = sequences.get(i);
			
			if (debug==1){
				System.out.println(seq);
			}
			
			if (seq>0){
				wins+= seq;
			}
			else{
				losses += -seq;
				if (-seq>=maxLosses){
					maxLosses = -seq;
				}
				
				if (-seq>limit && debug==2){
					System.out.println("secuencia de +"+limit+": "+seq);
					countLimit++;
				}
			}
			
			if (seq>=0){
				actualnet +=seq*1;
			}else{
				actualnet +=seq*factor;
			}
			if (actualnet>=maxnet){
				maxnet = actualnet;
			}else{
				double loss = maxnet-actualnet;
				if (loss>=maxdd){
					maxdd=loss;
				}
			}
		}
		
		total = wins+losses;
		double winPer = wins*100.0/total;
		double perLimit = countLimit*100.0/sequences.size();//numero de secuencias por encima del limite
		
		//busqueda de
		if(debug>=2)
		System.out.println(
				" "
				+" "+h1+" "+h2
				+" "+tp+" "+sl
				+" "+thr
				+" || "
				+" "+total+" "+wins+" "+losses
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" "+maxLosses
				+" || "+sequences.size()+" "+PrintUtils.Print2dec(perLimit, false)
				+"  ||| "+PrintUtils.Print2dec(maxdd, false)
				);
		
		return trades;
	}

	public static void main(String[] args) throws Exception {
		//String path0 ="C:\\fxdata\\";
		String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
		//String pathEURUSD = path0+"eurusd_UTC_15 Mins_Bid_2003.12.31_2017.11.25.csv";
		String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_1 Min_Bid_2013.12.31_2017.11.22.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		FFNewsClass.readNews(pathNews,news,0);
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			dataNoise = data;
			
			String header = "";
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataNoise);

			int y1 = 2017;
			int y2 = 2017;
			int h1 = 0;
			int h2 = 23;
			
			boolean modeSL = false;
			int aValue = 200;
			
			/*for (y1=2017;y1<=2017;y1++){
				y2 = y1+0;
				for (aValue = 200;aValue<=200;aValue+=10){
					if (modeSL){
						for (int thr=0;thr<=0;thr+=50){
							//for (int sl=200;sl<=200;sl+=10){
								//for (int tp=1*sl;tp<=4*sl;tp+=1*sl){
							for (int tp=aValue;tp<=aValue;tp+=10){
								for (int sl=1*tp;sl<=1*tp;sl+=1*tp){
									TestSequences.test1(dataNoise, maxMins, y1, y2, h1, h2, thr, tp, sl, false,0);
								}
							}
						}
					}else{
						for (int thr=0;thr<=0;thr+=50){
							for (int sl=aValue;sl<=aValue;sl+=10){
								for (int tp=2*sl;tp<=2*sl;tp+=1*sl){
									TestSequences.test1(dataNoise, maxMins, y1, y2, h1, h2, thr, tp, sl, false,2);
								}
							}
						}
					}
				}
			}*/
			
			for (y1=2004;y1<=2017;y1++) {
				y2 = y1;
				for (int sl=200;sl<=200;sl+=10){
					for (int tp=1*sl;tp<=1*sl;tp+=1*sl){
						for (int thr=300;thr<=300;thr+=100) {
							TestSequences.test1(dataNoise, maxMins, y1, y2, 22,22, thr, tp, sl, true,10);
						}
					}
				}
			}
			/*for (int tp=200;tp<=200;tp+=10){
				for (int sl=1*tp;sl<=10*tp;sl+=1*tp){
					for (int thr=0;thr<=0;thr+=100) {
						TestSequences.test1(dataNoise, maxMins, 2009, 2017, 0,9, thr, tp, sl, false,10);
					}
				}
			}*/
			
			/*for (int sl=200;sl<=200;sl+=10){
				for (int tp=1*sl;tp<=1*sl;tp+=1*sl){
					ArrayList<Integer> trades = TestSequences.test1(dataNoise, maxMins, 2009, 2017, 0,9, 500, tp, sl, false,0);
					int seq = 0;
					ArrayList<Double> muestra = new ArrayList<Double>();
					for (int n=1;n<=15;n+=1){
						muestra.clear();
						int count0 = 0;
						for (int j=n;j<trades.size();j++){
							int sum = MathUtils.countPositives(trades, j-n, j-1);
							
							double probWin =(sum)*100.0/n;
							muestra.add(probWin);
							if (probWin<=0) count0++;
							//System.out.println(PrintUtils.Print2dec(probWin, false));						
						}
						MathUtils.summary_mean_sd(""+n+" || "+count0, muestra);
					}
				}
			}*/
			
			for (h1=10;h1<=0;h1++) {
				h2=h1+0;
				for (int sl=200;sl<=200;sl+=10){
					for (int tp=3*sl;tp<=3*sl;tp+=1*sl){
						for (int thr=500;thr<=500;thr+=100){
							ArrayList<Integer> trades = TestSequences.test1(dataNoise, maxMins, 2009, 2017, h1, h2, thr, tp, sl, false,0);
							int seq = 0;
							ArrayList<Double> muestra = new ArrayList<Double>();
							for (double maxLossA=1;maxLossA<=1;maxLossA+=1.0) {
								for (double aTargetInicial = 1.0;aTargetInicial<=1;aTargetInicial+=1.0) {
									for (int aEsperados=1;aEsperados<= 1;aEsperados++) {
										double wins$$ =0;
										double losses$$ = 0;
										for (int n=0;n<=0;n+=10){
											muestra.clear();
											double targetInicial = aTargetInicial;
											double esperados = aEsperados;
											int bloque = 100000;
											double accWin = 0;
											int countbloque = 1;
											double f = tp*1.0/sl;
											double accWinConsolidado = 0;
											double target = targetInicial;
											double sizeInicial = targetInicial/(f*esperados);
											double size = sizeInicial;
											double maxLoss=0;
											int maxBloquesLoss = 0;
											int bloquesLosses = 0;
											int bloqueIndexW = 0;
											int bloqueIndexL = 0;
											int debug = 0;
											for (int j=0;j<trades.size();j++){
												//int sum = MathUtils.countPositives(trades, j-n, j);
												
												//double probWin =(sum)*100.0/n;
												//muestra.add(probWin);
												//System.out.println(PrintUtils.Print2dec(probWin, false));
																								
												double res = 0;
												
												int trade= trades.get(j);
												if (trade>0){
													if (seq<=0){
														//System.out.println("NUEVA SECUENCIA");
														seq=1;
													}else{
														seq++;
													}
													
													res = (size*tp)/sl; 
													wins$$ +=res;
												}else{//pierdo
													if (seq>=0){
														//System.out.println("NUEVA SECUENCIA");
														seq=-1;
													}else{
														seq--;
													}
													
													res = -size; 
													losses$$ +=-res;
												}
												accWin += res;
													
												if (accWin<0) {
													if (-accWin>=maxLoss) maxLoss = -accWin;
												}
												if (accWin>=targetInicial){
													accWinConsolidado += accWin;
													bloqueIndexW++;
													if (debug==1)
													System.out.println("****CONSOLIDADO BLOQUE ganancias***"
															+" "+bloqueIndexW+" "+bloqueIndexL
															+" || "+countbloque+" || "+trade+" || "+seq
															+" ||| "
															+" "+PrintUtils.Print2dec(size, false)
															+" || "+PrintUtils.Print2dec(res, false)
															+" || "+PrintUtils.Print2dec(accWin, false)
															+" ||| "+PrintUtils.Print2dec(accWinConsolidado, false)
															);
													accWin = 0;
													countbloque=1;
													target = targetInicial;
													size = sizeInicial;
													bloquesLosses = 0;
												}else{
													if (debug==1)
													System.out.println(
															countbloque+" || "+trade+" || "+seq
															+" ||| "
															+" "+PrintUtils.Print2dec(size, false)
															+" || "+PrintUtils.Print2dec(res, false)
															+" || "+PrintUtils.Print2dec(accWin, false)
															+" ||| "+PrintUtils.Print2dec(accWinConsolidado, false)
															+" ||| "+PrintUtils.Print2dec(targetInicial-accWin, false)
															);
													
													countbloque++;
													if (res<0){//solo modifico el size en caso de perdida
														target = 1*targetInicial-accWin;
														double sizeE = target/(f*esperados);
														if (sizeE>=size){
															size = target/(f*esperados);
														}
														/*System.out.println(
																" ||| "+PrintUtils.Print2dec(target, false)
																+" "+(bloque-countbloque+1)
																);*/
													}	
													
												}	
												
												
												if (countbloque==bloque 
														|| accWin<=-maxLossA
														){//consolido
													
													accWinConsolidado += accWin;
													bloqueIndexL++;
													if (debug==1){
														System.out.println("****CONSOLIDADO BLOQUE***"
																+" "+bloqueIndexW+" "+bloqueIndexL
																+" || "+PrintUtils.Print2dec(accWin, false)
																+ " || "+PrintUtils.Print2dec(accWinConsolidado, false)
																);
													}
													accWin = 0;
													countbloque=0;
													target = targetInicial;
													size = sizeInicial;
													bloquesLosses++;
													if (maxBloquesLoss>=maxBloquesLoss) {
														maxBloquesLoss = bloquesLosses;
													}
												}
											}//for trades
											//MathUtils.summary_mean_sd(""+n, muestra);
											double pf = wins$$/losses$$;
											double money = accWinConsolidado+accWin;
											double pfsec = (bloqueIndexW*targetInicial)/(bloqueIndexL*maxLoss);
											System.out.println(
													tp+" "+sl+" "+targetInicial+" "+esperados
													+" || "
													+" "+trades.size()
													+" "+PrintUtils.Print2dec(money, false)
													+" "+PrintUtils.Print2dec(pf, false)
													+" || "+PrintUtils.Print2dec(maxLoss, false)
													+" || "+maxBloquesLoss
													+" || "+PrintUtils.Print2dec(money/maxLoss, false)
													+" || "+bloqueIndexW+" "+bloqueIndexL
													+" || "+PrintUtils.Print2dec(pfsec, false)
													);
										}//n
									}//aesperados
								}//atargetInicial
							}//MAXlOSSa
						}//thr
						
					}
				}
			}//
			
			
		}

	}

}
