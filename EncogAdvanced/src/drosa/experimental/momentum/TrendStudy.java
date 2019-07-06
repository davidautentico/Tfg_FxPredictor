package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrendStudy {
	
	public static ArrayList<Trend> calculateTrends(ArrayList<QuoteShort> data,
			int begin,int end, int minSize){
		
		ArrayList<Trend> trends = new ArrayList<Trend>();
		if (begin<1) begin = 1;
		if (end>data.size()-1) end = data.size()-1;
		
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		int actualLeg = 0;
		int index1=-1;
		int index2=-1;
		QuoteShort q1 = null;
		QuoteShort q2 = null;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				if (actualLeg!=0){
					Trend t = new Trend(q1,q,index1,i,actualLeg);
					trends.add(t);
				}
				
				index1=i;
				index2=i;
				actualLeg = 0;
				lastDay = day;
			}
			
			if (index1==-1){
				index1 = i;
				index2 = i;
			}
			
			q1 = data.get(index1);
			q2 = data.get(index2);
			double longDiff = (q.getHigh5()-q1.getLow5())*0.1;
			double shortDiff = (q1.getHigh5()-q.getLow5())*0.1;
			if (actualLeg==0){
				if (longDiff>=minSize){
					actualLeg = 1;
					index2 = i;
				}else if (shortDiff>=minSize){
					actualLeg = -1;
					index2 = i;
				}
			}else if (actualLeg==1){
				double actualDiff = (q2.getHigh5()-q1.getLow5())*0.1;
				if (longDiff>=actualDiff){
					index2 = i;
				}else if (shortDiff>=minSize){
					Trend t = new Trend(q1,q,index1,index2,1);
					trends.add(t);
					actualLeg = -1;
					index1 = index2;
					index2 = i;
				}
			}else if (actualLeg==-1){
				double actualDiff = (q1.getHigh5()-q2.getLow5())*0.1;
				if (shortDiff>=actualDiff){
					index2 = i;
				}else if (longDiff>=minSize){
					Trend t = new Trend(q1,q,index1,index2,1);
					trends.add(t);
					actualLeg = 1;
					index1 = index2;
					index2 = i;
				}
			}
			
		}
		
		
		return trends;
	}
	
	/**
	 * calcula las trends de >minSize para cada dia
	 * @param data
	 * @param minSize
	 */
	public static void calculateDailyTrends(ArrayList<QuoteShort> data,
			int begin,int end,
			int year1,int year2,
			int hi,int hf,
			int tp,int sl,
			int minSize,boolean debug,boolean studyDebug){
		
		int wins = 0;
		int totalTrades = 0;
		double avgLeg = 0;
		ArrayList<Trend> trends = new ArrayList<Trend>();
		if (begin<1) begin = 1;
		if (end>data.size()-1) end = data.size()-1;
		ArrayList<Integer> hourDailyT = new ArrayList<Integer>();
		ArrayList<Integer> hourTrends = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hourDailyT.add(0);
		for (int i=0;i<=287;i++) hourTrends.add(0);
		ArrayList<ArrayList<Double>> hoursSize = new ArrayList<ArrayList<Double>>();
		for (int i=0;i<=23;i++){
			hoursSize.add(new ArrayList<Double>());
		}
		ArrayList<ArrayList<Integer>> dayTrends = new ArrayList<ArrayList<Integer>>();
		//ArrayList<Integer> trends = null;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar calIndex1 = Calendar.getInstance();
		Calendar calIndex2 = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int actualLeg=0;//1:up,0:none,-1:down
		int index1 = 0;
		int index2 = 0;
		int min50 = 0;
		int total = 0;
		int days50 = 0;
		double accAvgTrends = 0;
		int totalT=0;
		QuoteShort q1 = null;
		QuoteShort q2 = null;
		int lastTrendH = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q_1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q_1);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int month = cal.get(Calendar.MONTH);
			if (year<year1 || year>year2) continue;
			//if (month!=Calendar.JANUARY) continue;
			
			//System.out.println(q.toString());
			
			if (day!=lastDay){
				if (lastDay!=-1){
					if (actualLeg==1){
						int actualSize = (int) ((q2.getHigh5()-q1.getLow5())*0.1);
						
						Trend t = new Trend(q1,q2,actualLeg);
						trends.add(t);
						
						if (actualSize>=50) min50++;
						if (debug)
						System.out.println("BULL "+DateUtils.datePrint(cal)+" "+actualSize);
						QuoteShort.getCalendar(calIndex1,q1);
						int h = calIndex1.get(Calendar.HOUR_OF_DAY);
						int min = calIndex1.get(Calendar.MINUTE);
						int minPos = min/5;
						hourTrends.set(h*12+minPos, hourTrends.get(h*12+minPos)+1);
						//if (h!=lastTrendH){
							lastTrendH = h;
							hourDailyT.add(h);
						//}
						hoursSize.get(h).add((double) actualSize);
						//System.out.println("LEG "+actualLeg+" "+q1.toString()+" "+q2.toString()+" || "+actualSize);
					}
					if (actualLeg==-1){
						int actualSize = (int) ((q1.getHigh5()-q2.getLow5())*0.1);
						
						Trend t = new Trend(q1,q2,actualLeg);
						trends.add(t);
						
						if (actualSize>=50) min50++;
						if (debug)
						System.out.println("SELL "+DateUtils.datePrint(cal)+" "+actualSize);
						QuoteShort.getCalendar(calIndex1,q1);
						int h = calIndex1.get(Calendar.HOUR_OF_DAY);
						int min = calIndex1.get(Calendar.MINUTE);
						int minPos = min/5;
						hourTrends.set(h*12+minPos, hourTrends.get(h*12+minPos)+1);
						//if (h!=lastTrendH){
							lastTrendH = h;
							hourDailyT.add(h);
						//}
						hoursSize.get(h).add((double) actualSize);
						//System.out.println("LEG "+actualLeg+" "+q1.toString()+" "+q2.toString()+" || "+actualSize);
					}
					if (min50>0) days50++;
					total++;
					//if (debug)
					//System.out.println(DateUtils.datePrint(cal1)+" "+trends.size()+" "+min50+" "+PrintUtils.Print2dec(avgTrend, false));
					dayTrends.add(hourDailyT);//todas las horas de cada dia
				}
				min50 = 0;
				actualLeg = 0; //inicializacion
				index1 = i;
				index2 = i;
				hourDailyT = new ArrayList<Integer>();
				lastDay = day;
			}
			
			int h0 = cal.get(Calendar.HOUR_OF_DAY);
			int min0 = cal.get(Calendar.MINUTE);
			//if (h0==0 && min0==0) continue;
			
			q1 = data.get(index1);
			q2 = data.get(index2);
			QuoteShort.getCalendar(calIndex1,q1);
			QuoteShort.getCalendar(calIndex2,q2);
			if (actualLeg==0){//no habia leg
				int highDiff = (int) ((q.getHigh5() -q1.getLow5())*0.1); 
				int lowDiff  = (int) ((q1.getHigh5()-q.getLow5())*0.1);
				
				if (highDiff>=minSize){
					actualLeg=1;
					index2 = i;
				}else if (lowDiff>=minSize){
					actualLeg=-1;
					index2 = i;
				}
			}else if (actualLeg==1){//leg bull
				int lowDiff = (int) ((q2.getHigh5()-q.getLow5())*0.1);
				if (q.getHigh5()>=q2.getHigh5()){ //si superamos el maximo de index2 actualizamos
					index2 = i;//movemos solo el segundo
				}else if (lowDiff>=minSize){//reversal
					int actualSize = (int) ((q2.getHigh5()-q1.getLow5())*0.1);
					
					Trend t = new Trend(q1,q2,actualLeg);
					trends.add(t);
					
					if (actualSize>=50) min50++;
					if (debug)
					System.out.println("BULL "+DateUtils.datePrint(cal)+" "+actualSize+" || "+actualSize);
					
					int h = calIndex1.get(Calendar.HOUR_OF_DAY);
					int min = calIndex1.get(Calendar.MINUTE);
					int minPos = min/5;
					hourTrends.set(h*12+minPos, hourTrends.get(h*12+minPos)+1);
					//if (h!=lastTrendH){
						lastTrendH = h;
						hourDailyT.add(h);
					//}
					hoursSize.get(h).add((double) actualSize);
					//System.out.println("LEG "+actualLeg+" "+q1.toString()+" "+q2.toString()+" || "+actualSize);
					actualLeg=-1;//bear
					//movemos los puntos
					index1=index2;
					index2=i;
					
					int sellTarget = q.getLow5()-tp*10;
					int sellFail = q.getLow5()+sl*10;
					int tpIndex = TradingUtils.getMaxMinIndex(data, i+1, end, sellTarget, false);
					int slIndex = TradingUtils.getMaxMinIndex(data, i+1, end, sellFail, true);
				
					int win=1;
					if (slIndex==-1 && tpIndex==-1){
						
					}else{
						totalTrades++;
						avgLeg+=actualSize;
						if (slIndex==-1){
							wins++;
						}else if (tpIndex!=-1){
							if (tpIndex<=slIndex){
								wins++;
							}
						}
						//System.out.println("LEG "+actualLeg+" "+q1.toString()+" "+q2.toString()
						//		+" || "+actualSize+" "+sellTarget+" || "+win+" "+slIndex+" "+tpIndex);
					}
				
				}
			}else if (actualLeg==-1){//leg bear
				int highDiff = (int) ((q.getHigh5() -q2.getLow5())*0.1);
				if (q.getLow5()<=q2.getLow5()){ //si superamos el maximo de index2 actualizamos
					index2 = i;//movemos solo el segundo
				}else if (highDiff>=minSize){//reversal
					int actualSize = (int) ((q1.getHigh5()-q2.getLow5())*0.1);
					
					Trend t = new Trend(q1,q2,actualLeg);
					trends.add(t);
					
					if (actualSize>=50) min50++;
					if (debug)
					System.out.println("SELL "+DateUtils.datePrint(cal)+" "+actualSize);
					QuoteShort.getCalendar(calIndex1,q1);
					int h = calIndex1.get(Calendar.HOUR_OF_DAY);
					int min = calIndex1.get(Calendar.MINUTE);
					int minPos = min/5;
					hourTrends.set(h*12+minPos, hourTrends.get(h*12+minPos)+1);
					//if (h!=lastTrendH){
						lastTrendH = h;
						hourDailyT.add(h);
					//}
					hoursSize.get(h).add((double) actualSize);
					//System.out.println("LEG "+actualLeg+" "+q1.toString()+" "+q2.toString()+" || "+actualSize);
					
					actualLeg=1;//bull
					//movemos los puntos
					index1=index2;
					index2=i;
					
					int buyTarget = q.getHigh5()+tp*10;
					int buyFail = q.getHigh5()-sl*10;
					int tpIndex = TradingUtils.getMaxMinIndex(data, i+1, end, buyTarget, true);
					int slIndex = TradingUtils.getMaxMinIndex(data, i+1, end, buyFail, false);
					int win=1;
					if (slIndex==-1 && tpIndex==-1){
						
					}else{
						totalTrades++;
						avgLeg+=actualSize;
						if (slIndex==-1){
							wins++;
						}else if (tpIndex!=-1){
							if (tpIndex<=slIndex){
								wins++;
							}
						}
						//System.out.println("LEG "+actualLeg+" "+q1.toString()+" "+q2.toString()
						//		+" || "+actualSize+" "+buyTarget+" || "+win+" "+slIndex+" "+tpIndex);
					}
				}
			}
			
		}
		String header = minSize+" "+hi+" "+hf+" "+tp+" "+sl;
		double winPer = wins*100.0/totalTrades;
		double exp = (winPer*tp-(100.0-winPer)*sl)/100.0;
		System.out.println(header+" || "
				+" "+totalTrades
				+" "+PrintUtils.Print2dec(wins*100.0/totalTrades, false)
				+" "+PrintUtils.Print2dec(exp, false)
				+" || "+PrintUtils.Print2dec(avgLeg*1.0/totalTrades, false)
				);
		//String header = minSize+" "+hi+" "+hf;
		//TrendStudy.study(header,trends,hi,hf,minSize,studyDebug);
	}

	private static void study2(String header,ArrayList<Trend> trends,int hi,int hf,int minSize,boolean debug) {
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			int type = t.getType();
			QuoteShort q1 = t.getQ1();
			QuoteShort q2 = t.getQ2();
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal2, q2);
			double trendSize = t.getSize();
			
			if (trendSize>=minSize){
				if (type==1){
					double tpValue =
					double slValue = 
				}
				if (type==-1){
					
				}
			}
			
		}
	}
	private static void study(String header,ArrayList<Trend> trends,int hi,int hf,int minSize,boolean debug) {
	
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar calt1 = Calendar.getInstance();
		Calendar calt2 = Calendar.getInstance();
		for (int h=hi;h<=hi;h++){
			int cases = 0;
			int casesh = 0;
			int casesh1 = 0;
			int casesh2 =0;
			int cases30 = 0;
			int cases40 = 0;
			int cases50 = 0;
			int cases60 = 0;
			int cases70 = 0;
			int cases80 = 0;
			double avgDiff = 0;
			double avgTrend = 0;
			for (int i=0;i<trends.size()-2;i++){
				Trend t = trends.get(i);
				Trend t1 = trends.get(i+1);
				Trend t2 = trends.get(i+2);
				int type = t.getType();
				QuoteShort q1 = t.getQ1();
				QuoteShort q2 = t.getQ2();
				QuoteShort.getCalendar(cal1, q1);
				QuoteShort.getCalendar(cal2, q2);
				QuoteShort.getCalendar(calt1, t1.getQ1());
				QuoteShort.getCalendar(calt2, t2.getQ1());
				double minDiff = (cal2.getTimeInMillis()-cal1.getTimeInMillis())/60000;
				
				int h1 = cal1.get(Calendar.HOUR_OF_DAY);
				int ht1 = calt1.get(Calendar.HOUR_OF_DAY);
				int ht2 = calt2.get(Calendar.HOUR_OF_DAY);
				//if (debug)
					//System.out.println(t.toString()+" || "+t1.toString());
				if (h1>=hi && h1<=hf
						//&& ht1>=h //si no es la ultima...
						){
					if (debug)
						System.out.println(t.toString()+" || "+t1.toString());
					cases++;
					avgDiff+=minDiff;
					avgTrend += t.getSize();
					if (t.getSize()>=30) cases30++;
					if (t.getSize()>=2*minSize) cases40++;
					if (t.getSize()>=1.5*minSize) cases50++;
					if (t.getSize()>=60) cases60++;
					if (t.getSize()>=70) cases70++;
					if (t.getSize()>=80) cases80++;
					if (ht1==h){//misma hora
						casesh++;
					}
					if (ht1==h+1){//h+1
						casesh1++;
					}
					if (ht1==h+2){//h+2
						casesh2++;
					}
				}
			}
			double avgTime = avgDiff*1.0/cases;
			double avgSize = avgTrend*1.0/cases;
			double winPer30 = cases30*100.0/cases;
			double winPer40 = cases40*100.0/cases;
			double winPer50 = cases50*100.0/cases;
			double winPer60 = cases60*100.0/cases;
			double winPer70 = cases70*100.0/cases;
			double winPer80 = cases80*100.0/cases;
			double exp40 = (winPer40*(2*minSize-minSize)-(100.0-winPer40)*minSize)/100.0;
			double exp30 = (winPer30*(30-minSize)-(100.0-winPer30)*minSize)/100.0;
			double exp50 = (winPer50*(1.5*minSize-minSize)-(100.0-winPer50)*minSize)/100.0;
			double exp60 = (winPer60*(60-minSize)-(100.0-winPer60)*minSize)/100.0;
			double exp70 = (winPer70*(70-minSize)-(100.0-winPer70)*minSize)/100.0;
			double exp80 = (winPer80*(80-minSize)-(100.0-winPer80)*minSize)/100.0;
			System.out.println(
					header+" "+hi+" "+hf
					+" || "
					+" "+PrintUtils.Print2Int(cases,4)
					+" "+PrintUtils.Print2dec(avgTime, false,3)
					+" "+PrintUtils.Print2dec(avgSize, false,3)
					+" "+PrintUtils.Print2dec(casesh*100.0/cases, false,3)
					+" "+PrintUtils.Print2dec(casesh1*100.0/cases, false,3)
					+" "+PrintUtils.Print2dec(casesh2*100.0/cases, false,3)
					+" || "
					+" "+PrintUtils.Print2dec(avgSize/minSize, false,3)
					//+" "+PrintUtils.Print2dec(cases30*100.0/cases, false,3)+"("+PrintUtils.Print2dec(exp30,false)+")" 
					+" "+PrintUtils.Print2dec(cases40*100.0/cases, false,3)+"("+PrintUtils.Print2dec(exp40,false)+")" 
					+" "+PrintUtils.Print2dec(cases50*100.0/cases, false,3)+"("+PrintUtils.Print2dec(exp50,false)+")" 
					//+" "+PrintUtils.Print2dec(cases60*100.0/cases, false,3)+"("+PrintUtils.Print2dec(exp60,false)+")" 
					//+" "+PrintUtils.Print2dec(cases70*100.0/cases, false,3)+"("+PrintUtils.Print2dec(exp70,false)+")" 
					//+" "+PrintUtils.Print2dec(cases80*100.0/cases, false,3)+"("+PrintUtils.Print2dec(exp80,false)+")" 
					);
		}
		
	}

	public static void main(String[] args) {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2010.12.31_2015.09.22.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2013.12.31_2015.09.15.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
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
		
		for (int i =0;i<=limit;i++){
			String path = paths.get(i);			
			ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			
			String header=path.substring(10, 16);
			
			int boxSize=data.size()/1-1;
			int begin1 = 1;
			int end = data.size();
			//System.out.println("datasize: "+data.size()+" "+boxSize);
			for (int begin=begin1;begin<=end-boxSize;begin+=boxSize){
				int end1 = begin+boxSize;
				//System.out.println(begin1 + " "+end1);+
				for (int year1=2004;year1<=2004;year1++){
					int year2=year1+11;
					for (int h1=1;h1<=1;h1++){
						int h2 = h1+23;
						for (int minSize=20;minSize<=20;minSize+=10){
							//TrendStudy.calculateDailyTrends(data,begin,end1,year1,year2,0,9, minSize,false);
							//TrendStudy.calculateDailyTrends(data,begin,end1,year1,year2,10,15, minSize,false);
							for (int tp=(int) (minSize*1.0);tp<=minSize*10.0;tp+=minSize*0.5){
								for (int sl=tp*1;sl<=tp*1;sl+=tp){
									TrendStudy.calculateDailyTrends(data,begin,end1,year1,year2,h1,h2,tp,sl, minSize,false,false);
								}
							}
						}
					}
				}
			}
		}
	}

}
