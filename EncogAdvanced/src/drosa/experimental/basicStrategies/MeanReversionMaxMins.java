package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MeanReversionMaxMins {
	
	public static void doBasic4(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			int maxBars,
			int minPips,
			int distance,
			double comm,
			boolean debug
	){

		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		Calendar cal4 = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		boolean dayTraded = false;
		int actualLosses = 0;
		ArrayList<Integer> lossesArray = new ArrayList<Integer>();
		for (int i=0;i<=500;i++) lossesArray.add(0);
		int index = distance;
		if (index<=5) index=5;
		//int i = 1;
		while (index<data.size()-1){
			QuoteShort q1 = data.get(index-1);
			QuoteShort q2 = data.get(index-2);
			QuoteShort q3 = data.get(index-3);
			QuoteShort q4 = data.get(index-4);
			QuoteShort q = data.get(index);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q);
			QuoteShort.getCalendar(cal2, q2);
			QuoteShort.getCalendar(cal3, q3);
			QuoteShort.getCalendar(cal4, q4);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int hc1 = cal1.get(Calendar.HOUR_OF_DAY);
			int hc2 = cal2.get(Calendar.HOUR_OF_DAY);
			int hc3 = cal3.get(Calendar.HOUR_OF_DAY);
			int hc4 = cal4.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2){
				index++;
				continue;
			}
			
			
			if (day!=lastDay){
				dayTraded = false;
				lastDay = day;
			}
					
			int maxMin = maxMins.get(index-1);
			
			QuoteShort qdistance = data.get(0);
			if (distance>=0)
				qdistance = data.get(index-distance);
			
			//dayTraded = false;
			if (!dayTraded){
				boolean isHourOk = (h>=h1 && h<=h2) || (h>=h1 && h<=h2 && h==0 && min>=10);
				if (isHourOk){			
					boolean isTrade = false;
					int pips = 0;
					int distanceH = q.getOpen5()-qdistance.getOpen5();
					int distanceL = qdistance.getOpen5()-q.getOpen5();
					if (maxMin>=thr 
							&& (q1.getClose5()>=q1.getOpen5()+10*minPips)
							//&& (q1.getClose5()<q1.getOpen5()-10*minPips)
							//&& (hc2==h1 && q2.getClose5()>q2.getOpen5()+10*minPips)
							//&& (hc3==h1 && q3.getClose5()>q3.getOpen5())
							&& (distance==-1 || distanceH>=10*minPips))
					{ //resistencia sell
						int valueTP = (int) (q.getOpen5()-10*tp);
						int valueSL = (int) (q.getOpen5()+10*sl);
						cal1.setTimeInMillis(cal.getTimeInMillis());
						index = TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, index, index+maxBars, valueTP, valueSL, false);
						pips = (int) (q.getOpen5()-qm.getClose5()-10*comm);
						isTrade = true;		
						
						if (debug){
							System.out.println("[SELL] "+DateUtils.datePrint(cal1)
									+" || "+q.getOpen5()+" "+valueTP+" "+valueSL
									+" || "+pips+" || "+DateUtils.datePrint(cal));
						}
					}else if (maxMin<=-thr 
							&& (q1.getClose5()<=q1.getOpen5()-10*minPips)
							//&& (q1.getClose5()>q1.getOpen5()+10*minPips)
							//&& (hc2==h1 && q2.getClose5()<q2.getOpen5()-10*minPips)
							//&& (hc3==h1 && q3.getClose5()<q3.getOpen5())
							&& (distance==-1 || distanceL>=10*minPips)
							){ //soporte buy
						int valueTP = (int) (q.getOpen5()+10*tp);//+10*comm);
						int valueSL = (int) (q.getOpen5()-10*sl);//+10*comm);
						cal1.setTimeInMillis(cal.getTimeInMillis());
						index = TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, index, index+maxBars, valueTP, valueSL, false);
						pips = (int) (qm.getClose5()-q.getOpen5()-10*comm);
						isTrade = true;
						
						if (debug){
							System.out.println("[BUY] "+DateUtils.datePrint(cal1)
									+" || "+q.getOpen5()+" "+valueTP+" "+valueSL
									+" || "+pips+" || "+DateUtils.datePrint(cal));
						}
					}			
					
					if (isTrade){						
						if (pips>=0){
							winPips += pips;
							wins++;
							lossesArray.set(actualLosses, lossesArray.get(actualLosses)+1);
							actualLosses=0;
							//i=index;
						}else if (pips<0){
							lostPips += -pips;
							losses++;
							actualLosses++;
							//dayTraded = true;
							//i=index;
						}else{
							
						}
					}
				}//h	
			}
			index++;
		}//while
	
	
		int trades = wins+losses;
		
		double winPer = 0;
		double avg = 0;
		double pf = 0;
		
		String res="";
		for (int i=2;i<=5;i++){
			res+=" "+lossesArray.get(i);
		}
		
		if (trades>0){
			winPer = wins*100.0/trades;
			avg = (winPips-lostPips)*0.1/(trades);
			pf = winPips*1.0/lostPips;
		}
		System.out.println(
				header
				+" || "
				+" "+trades+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" || "+res
				);
	
	}
	
	public static void doBasic3(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			int maxBars,
			double comm,
			boolean debug
	){

		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		boolean dayTraded = false;
		int actualLosses = 0;
		ArrayList<Integer> lossesArray = new ArrayList<Integer>();
		for (int i=0;i<=500;i++) lossesArray.add(0);
		int index = 1;
		
		//int i = 1;
		while (index<data.size()-1){
			QuoteShort q = data.get(index);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2){
				index++;
				continue;
			}
			
			
			if (day!=lastDay){
				dayTraded = false;
				lastDay = day;
			}
					
			int maxMin = maxMins.get(index-1);
			
			//dayTraded = false;
			if (!dayTraded){
				boolean isHourOk = (h>=h1 && h<=h2) || (h>=h1 && h<=h2 && h==0 && min>=10);
				if (isHourOk){			
					boolean isTrade = false;
					int pips = 0;
					if (maxMin>=thr){ //resistencia sell
						int valueTP = (int) (q.getOpen5()-10*tp);
						int valueSL = (int) (q.getOpen5()+10*sl);
						cal1.setTimeInMillis(cal.getTimeInMillis());
						index = TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, index, index+maxBars, valueTP, valueSL, false);
						pips = (int) (q.getOpen5()-qm.getClose5()-10*comm);
						isTrade = true;		
						
						if (debug){
							System.out.println("[SELL] "+DateUtils.datePrint(cal1)
									+" || "+q.getOpen5()+" "+valueTP+" "+valueSL
									+" || "+pips+" || "+DateUtils.datePrint(cal));
						}
					}else if (maxMin<=-thr){ //soporte buy
						int valueTP = (int) (q.getOpen5()+10*tp);//+10*comm);
						int valueSL = (int) (q.getOpen5()-10*sl);//+10*comm);
						cal1.setTimeInMillis(cal.getTimeInMillis());
						index = TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, index, index+maxBars, valueTP, valueSL, false);
						pips = (int) (qm.getClose5()-q.getOpen5()-10*comm);
						isTrade = true;
						
						if (debug){
							System.out.println("[BUY] "+DateUtils.datePrint(cal1)
									+" || "+q.getOpen5()+" "+valueTP+" "+valueSL
									+" || "+pips+" || "+DateUtils.datePrint(cal));
						}
					}			
					
					if (isTrade){						
						if (pips>=0){
							winPips += pips;
							wins++;
							lossesArray.set(actualLosses, lossesArray.get(actualLosses)+1);
							actualLosses=0;
							//i=index;
						}else if (pips<0){
							lostPips += -pips;
							losses++;
							actualLosses++;
							dayTraded = true;
							//i=index;
						}else{
							
						}
					}
				}//h	
			}
			index++;
		}//while
	
	
		int trades = wins+losses;
		
		double winPer = 0;
		double avg = 0;
		double pf = 0;
		
		String res="";
		for (int i=2;i<=5;i++){
			res+=" "+lossesArray.get(i);
		}
		
		if (trades>0){
			winPer = wins*100.0/trades;
			avg = (winPips-lostPips)*0.1/(trades);
			pf = winPips*1.0/lostPips;
		}
		System.out.println(
				header
				+" || "
				+" "+trades+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" || "+res
				);
	
	}
	
	public static void doBasic2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			double comm
	){

		int wins = 0;
		int losses = 0;
		
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		boolean dayTraded = false;
		int actualLosses = 0;
		ArrayList<Integer> lossesArray = new ArrayList<Integer>();
		for (int i=0;i<=500;i++) lossesArray.add(0);
		int index = 1;
		
		//int i = 1;
		while (index<data.size()-1){
			QuoteShort q = data.get(index);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			
			if (day!=lastDay){
				dayTraded = false;
				lastDay = day;
			}
					
			int maxMin = maxMins.get(index-1);
			
			//dayTraded = false;
			if (!dayTraded){
				boolean isHourOk = (h>=h1 && h<=h2) || (h>=h1 && h<=h2 && h==0 && min>=10);
				if (isHourOk){			
					boolean isTrade = false;
					if (maxMin>=thr){ //resistencia
						int valueTP = (int) (q.getOpen5()-10*tp-10*comm);
						int valueSL = (int) (q.getOpen5()+10*sl-10*comm);
						index = TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, index, data.size()-1, valueTP, valueSL, false);
						isTrade = true;					
					}else if (maxMin<=-thr){ //soporte
						int valueTP = (int) (q.getOpen5()+10*tp+10*comm);
						int valueSL = (int) (q.getOpen5()-10*sl+10*comm);
						index = TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, index, data.size()-1, valueTP, valueSL, false);
						isTrade = true;
					}			
					
					if (isTrade && qm.getOpen5()!=0){
						
						if (qm.getOpen5()==1){
							wins++;
							lossesArray.set(actualLosses, lossesArray.get(actualLosses)+1);
							actualLosses=0;
							//i=index;
						}
						if (qm.getOpen5()==-1){
							losses++;
							actualLosses++;
							dayTraded = true;
							//i=index;
						}
					}
				}//h	
			}
			index++;
		}//while
	
	
		int trades = wins+losses;
		
		double winPer = 0;
		double avg = 0;
		double pf = 0;
		
		String res="";
		for (int i=2;i<=5;i++){
			res+=" "+lossesArray.get(i);
		}
		
		if (trades>0){
			winPer = wins*100.0/trades;
			avg = (tp*wins-sl*losses)*1.0/(trades);
			pf = tp*wins*1.0/(sl*losses);
		}
		System.out.println(
				header
				+" || "
				+" "+trades+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(pf,false)
				+" || "+res
				);
	
	}
	
	
	public static void doBasic1(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl
			){
		
		
		int wins = 0;
		int losses = 0;
		
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			
			int maxMin = maxMins.get(i-1);
			
			
			if (h>=h1 && h<=h2){
				
				boolean isTrade = false;
				if (maxMin>=thr){ //resistencia
					int valueTP = q.getOpen5()-10*tp;
					int valueSL = q.getOpen5()+10*sl;
					TradingUtils.getMaxMinShortTPSL(data, qm, cal, i, data.size()-1, valueTP, valueSL, false);
					isTrade = true;
				}else if (maxMin<=-thr){ //soporte
					int valueTP = q.getOpen5()+10*tp;
					int valueSL = q.getOpen5()-10*sl;
					TradingUtils.getMaxMinShortTPSL(data, qm, cal, i, data.size()-1, valueTP, valueSL, false);
					isTrade = true;
				}			
				
				if (isTrade && qm.getOpen5()!=0){
					if (qm.getOpen5()==1) wins++;
					if (qm.getOpen5()==-1) losses++;
				}
			}		
		}
		
		
		int trades = wins+losses;
		
		double winPer = 0;
		double avg = 0;
		double pf = 0;
		
		if (trades>0){
			winPer = wins*100.0/trades;
			avg = (tp*wins-sl*losses)*1.0/(trades);
			pf = tp*wins*1.0/(sl*losses);
		}
		System.out.println(
				header
				+" || "
				+" "+trades+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(pf,false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.11.21.csv";
		
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
		
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			for (int y1=2003;y1<=2003;y1++){
				int y2 = y1+13;
				for (int h1=9;h1<=9;h1++){
					int h2 = h1+0;
					for (int thr=550;thr<=550;thr+=25){
						for (int tp=20;tp<=20;tp+=5){
							for (int sl=80;sl<=80;sl+=10){
								for (int maxBars=24;maxBars<=24;maxBars+=3){
									//String header = y1+" "+y2+" "+h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+maxBars;
									
									//MeanReversionMaxMins.doBasic1(header, data, maxMins, y1, y2, h1, h2, thr, tp, sl);
									//MeanReversionMaxMins.doBasic2(header, data, maxMins, y1, y2, h1, h2, thr, tp, sl,2.0);
									for (double comm=2.0;comm<=2.0;comm+=0.5){
										//MeanReversionMaxMins.doBasic3(header, data, maxMins, y1, y2, h1, h2, thr, tp, sl,maxBars,comm,false);
										for (int distance=-1;distance<=-1;distance+=6){
											for (int minPips=0;minPips<=10;minPips+=1){
												String header = y1+" "+y2+" "+h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+maxBars+" "+minPips+" "+distance;
												MeanReversionMaxMins.doBasic4(header, data, maxMins, y1, y2, h1, h2, thr, tp, sl,maxBars,minPips,distance,comm,false);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
		}

	}

}
