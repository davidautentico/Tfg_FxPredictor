package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MaxMinHours {
	
	
	public static void queryHours(ArrayList<Quote> data, Calendar from,Calendar to,int dayL,int dayH
			,int hLimit,boolean maxmin,int range){

		
		double min = 9999;
		double max = -9999;
		int hmin = -1;
		int hmax = -1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays=0;
		int count=0;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){
				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDay!=lastDay){
				if (hmin>=0 && hmax>=0){
					double diff = TradingUtils.getPipsDiff(max, min);
					if (hmin<=hLimit && hmax<=hLimit && diff<=range)
						count++;
					totalDays++;
				}
				lastDay = actualDay;
				min = 9999;
				max = -9999;
				hmin=-1;
				hmax=-1;
				
			}
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}			
		}
		
		System.out.println(hLimit+" "+PrintUtils.Print2dec(count*100.0/totalDays,true));
		
	}

	public static int minmaxAvgRange(ArrayList<Quote> data, Calendar from,Calendar to,int dayL,int dayH,
			int h0,int h1,int maxPips,int debug){
		ArrayList<Integer> ranges 		= new ArrayList<Integer>();
		ArrayList<Integer> hoursAfter	= new ArrayList<Integer>();
		
		for (int i=0;i<=23;i++){
			hoursAfter.add(0);
		}
		
		double min = 9999;
		double max = -9999;
		int hmin = -1;
		int hmax = -1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int actualRange = 0;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDay!=lastDay){
				if (hmin>=0 && hmax>=0){
					
					int val = hmin; // hora minima
					double value = min; //el minimo
					 
					if (hmax<val){ //se alcanzo el maximo antes que el minimo					
						val = hmax;
						value = max;
					}
					
					//añadimos al rango si coinciden las horas que buscamos
					if (hmin>=h0 && hmax<=h1){
						ranges.add(actualRange);
						//System.out.println(val+" -> "+actualRange);
					}
															
					int count = hoursAfter.get(val);
					hoursAfter.set(val, count+1);
					//System.out.println(DateUtils.datePrint(data.get(i-1).getDate())+" "+val+" "+PrintUtils.Print4dec(value));
				}
				lastDay = actualDay;
				min = 9999;
				max = -9999;
				hmin=-1;
				hmax=-1;
			}
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}		
			
			if (h>=h0 && h<=h1){
				actualRange = TradingUtils.getPipsDiff(max, min);
			}
		}
		
		int totals =0;
		for (int i=0;i<=23;i++){
			totals+=hoursAfter.get(i);
		}
				
		int acc=0;
		String rangeStr ="";
		int total=0;
		for (int i=0;i<ranges.size();i++){			
			int range = ranges.get(i);
			if (range>maxPips){
				acc+=range;
				rangeStr+=" "+range;
				total++;
			}
		}
		if (debug==1)
			System.out.println("avg ranges: "+PrintUtils.Print2dec(acc/ranges.size(), false)+" || "+rangeStr+ " || "+total);
		if (debug==2)
			System.out.println("total : "+total);
		return total;
	}
	
	public static int timesmaxRange(ArrayList<Quote> data, Calendar from,Calendar to,int dayL,int dayH,
			int h0,int h1,int maxPips,int debug){
		ArrayList<Integer> ranges 		= new ArrayList<Integer>();
		ArrayList<Integer> hoursAfter	= new ArrayList<Integer>();
		
		for (int i=0;i<=23;i++){
			hoursAfter.add(0);
		}
		
		double min = 9999;
		double max = -9999;
		int hmin = -1;
		int hmax = -1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int actualRange = 0;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDay!=lastDay){
				if (hmin>=0 && hmax>=0){
					ranges.add(actualRange);
					//System.out.println(DateUtils.datePrint(data.get(i-1).getDate())+" "+val+" "+PrintUtils.Print4dec(value));
				}
				lastDay = actualDay;
				min = 9999;
				max = -9999;
				hmin=-1;
				hmax=-1;
			}
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}		
			
			if (h>=h0 && h<=h1){
				actualRange = TradingUtils.getPipsDiff(max, min);
			}
		}
		
		int totals =0;
		for (int i=0;i<=23;i++){
			totals+=hoursAfter.get(i);
		}
				
		int acc=0;
		int total=0;
		String rangeStr ="";
		for (int i=0;i<ranges.size();i++){
			int range = ranges.get(i);
			if (range>maxPips){		
				total++;
				acc+=range;
				rangeStr+=" "+range;
			}
		}
		if (debug==1)
			System.out.println("ranges: "+rangeStr+" || "+total);
		if (debug==2)
			System.out.println("total: "+total);
		return total;
	}
	
	public static int timesFinalmaxRange(ArrayList<Quote> data, Calendar from,Calendar to,int dayL,int dayH,
			int h0,int h1,int pipsL,int pipsH,int debug){
		ArrayList<Integer> ranges 		= new ArrayList<Integer>();
		ArrayList<Integer> finalRanges 		= new ArrayList<Integer>();
		ArrayList<Integer> hoursAfter	= new ArrayList<Integer>();
		
		for (int i=0;i<=23;i++){
			hoursAfter.add(0);
		}
		
		double min = 9999;
		double max = -9999;
		int hmin = -1;
		int hmax = -1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int actualRange = 0;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDay!=lastDay){
				if (hmin>=0 && hmax>=0){
					int finalRange = TradingUtils.getPipsDiff(max, min);
					finalRanges.add(finalRange);
					ranges.add(actualRange);
					//System.out.println(DateUtils.datePrint(data.get(i-1).getDate())+" "+val+" "+PrintUtils.Print4dec(value));
				}
				lastDay = actualDay;
				min = 9999;
				max = -9999;
				hmin=-1;
				hmax=-1;
			}
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}		
			
			if (h>=h0 && h<=h1){
				actualRange = TradingUtils.getPipsDiff(max, min);
			}
		}
		
		int totals =0;
		for (int i=0;i<=23;i++){
			totals+=hoursAfter.get(i);
		}
				
		int acc=0;
		int total=0;
		String rangeStr ="";
		String factorStr ="";
		int avgRange = 0;
		double factorAvg = 0;
		for (int i=0;i<ranges.size();i++){
			int range 		= ranges.get(i);
			int finalRange  = finalRanges.get(i);
			avgRange+=finalRange;
			if (range>=pipsL && range<=pipsH){		
				total++;
				acc+=range;
				rangeStr+=" "+range;
				factorAvg += finalRange*1.0/range;
				factorStr+=" "+PrintUtils.Print2dec(finalRange*1.0/range,false);
			}
		}
		if (debug==1)
			System.out.println("ranges: "+rangeStr+" || "+total);
		if (debug==2)
			System.out.println("pips total factorAvg: "+pipsL+"-"+pipsH+" "
						+total+" "+PrintUtils.Print2dec(factorAvg/total,false)+" "+factorStr);
		return total;
	}
	
	public static ArrayList<Double> minmaxAfter(ArrayList<Quote> data, Calendar from,Calendar to,int dayL,int dayH,
			int h0, int h1, boolean first,int debug){
		
		ArrayList<Double> percents = new ArrayList<Double>();
		ArrayList<Integer> hoursAfter = new ArrayList<Integer>();
		
		for (int i=0;i<=23;i++){
			hoursAfter.add(0);
			percents.add(0.0);
		}
		
		double min = 9999;
		double max = -9999;
		int hmin = -1;
		int hmax = -1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDay!=lastDay){
				if (hmin>=0 && hmax>=0){
					
					int val = hmin;
					double value = min;
					if (first && hmax<val){
						val = hmax;
						value = max;
					}
					if (!first && hmax>val){
						val = hmax;
					}
										
					int count = hoursAfter.get(val);
					hoursAfter.set(val, count+1);
					//System.out.println(DateUtils.datePrint(data.get(i-1).getDate())+" "+val+" "+PrintUtils.Print4dec(value));
				}
				lastDay = actualDay;
				min = 9999;
				max = -9999;
				hmin=-1;
				hmax=-1;
			}
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}			
		}
		
		int totals =0;
		for (int i=0;i<=23;i++){
			totals+=hoursAfter.get(i);
		}
				
		int acc=0;
		int accH=0;
		//System.out.println(DateUtils.datePrint(from)+" "+DateUtils.datePrint(to));
		for (int i=23;i>=0;i--){
			int val = hoursAfter.get(i);
			acc+=val;
			if (i>=h0 && i<=h1){
				accH+=val;
				if (debug==1){
					System.out.println("h"+i
					+" "+val
					+" "+PrintUtils.Print2dec(val*100.0/totals,false)+"%"
					//+" "+PrintUtils.Print2dec(acc*100.0/totals,false)+"%"
					);
				}
				//System.out.println("añadiendiendo: "+PrintUtils.Print2(val*100.0/totals));
				percents.set(i,val*100.0/totals);
			}
		}
		double avgRes = accH*100.0/totals;
		if (debug==2)
			System.out.println(DateUtils.datePrint(from)+" "+DateUtils.datePrint(to)+" TOTALS: "+totals+" "+accH+" "+PrintUtils.Print2dec(accH*100.0/totals,false)+"%");
		
		return percents ;
	}
	

	public static ArrayList<DayPoint> findFirstSecondPoint(ArrayList<Quote> data){
		
		int hmin = -1;
		int hmax = -1;
		double max = -9999;
		double min = 9999;
		Calendar cal = Calendar.getInstance();		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}				
		}
		
		DayPoint first = new DayPoint();
		DayPoint second = new DayPoint();
		if (hmin<=hmax){
			first.setFirst(true);
			first.setMin(true);
			first.setH(hmin);
			first.setValue(min);
			
			second.setFirst(false);
			second.setMin(false);
			second.setH(hmax);
			second.setValue(max);
		}else if (hmin>hmax){
			first.setFirst(true);
			first.setMin(false);
			first.setH(hmax);
			first.setValue(max);
			
			second.setFirst(false);
			second.setMin(true);
			second.setH(hmin);
			second.setValue(min);
		}
		
		ArrayList<DayPoint> points = new ArrayList<DayPoint>();
		points.add(first);
		points.add(second);
		
		return points;
	}
	
	public static void testFollowingMinMax(ArrayList<Quote> data, ArrayList<Quote> dailyData,
			Calendar from,Calendar to,int dayL,int dayH,
			int hSplit){
		
		int total = 0;
		int wins = 0;
		Calendar cal = Calendar.getInstance();
		Calendar lastCal = Calendar.getInstance();
		int lastDay = -1;
		for (int i=0;i<dailyData.size();i++){
			Quote q = dailyData.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			ArrayList<Quote> dayData = TradingUtils.getDayData(data, cal);
			//separo en dos
			SplitData splitData = SplitData.split(dayData, hSplit);
			ArrayList<DayPoint> points1 = findFirstSecondPoint(splitData.getData1());
			ArrayList<DayPoint> points2 = findFirstSecondPoint(splitData.getData2());
			
			DayPoint p1 = points1.get(1);
			DayPoint p20 = points2.get(0);
			DayPoint p21 = points2.get(1);
			//System.out.println("hour: "+p1.getH());
			if (p1.getH()==hSplit){//hour
				double p1Value = p1.getValue();
				double p20Value = p20.getValue();
				double p21Value = p21.getValue();
				if (p1.isMin()){
					if (p20Value<p1Value || p21Value<p1Value){
						wins++;
					}
				}else{ //max
					if (p20Value>p1Value || p21Value>p1Value){
						wins++;
					}
				}
				total++;
			}								
		}
		double winPer = wins*100.0/total;
		System.out.println(hSplit+" "+total+" "+PrintUtils.Print2(winPer));
	}
	
	public static void testHours(ArrayList<Quote> data, Calendar from,Calendar to,int dayL,int dayH){
		ArrayList<Integer> mins = new ArrayList<Integer>();
		ArrayList<Integer> maxs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			mins.add(0);
			maxs.add(0);
		}
		
		double min = 9999;
		double max = -9999;
		int hmin = -1;
		int hmax = -1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){
				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDay!=lastDay){
				if (hmin>=0 && hmax>=0){
					int actualMn = mins.get(hmin);
					int actualMx = maxs.get(hmax);				
					mins.set(hmin, actualMn+1);
					maxs.set(hmax, actualMx+1);
				}
				lastDay = actualDay;
				min = 9999;
				max = -9999;
				hmin=-1;
				hmax=-1;
			}
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}			
		}
		
		int totalMins =0;
		int totalMaxs = 0;
		int globalTotal=0;
		for (int i=0;i<=23;i++){
			totalMins+=mins.get(i);
			totalMaxs+=maxs.get(i);
		}
		globalTotal = totalMins+totalMaxs;
		
		for (int i=0;i<=23;i++){
			int minVal = mins.get(i);
			int maxVal = maxs.get(i);
			int total = minVal+maxVal;
			System.out.println("h"+i
					+" "+minVal+" "+maxVal+" "+total
					+" "+PrintUtils.Print2dec(total*100.0/globalTotal,false)+"%");
		}
	}
	
	public static void diffs(ArrayList<Quote> data, Calendar from,Calendar to,int dayL,int dayH){
		ArrayList<Integer> diffs = new ArrayList<Integer>();

		for (int i=0;i<=23;i++){
			diffs.add(0);
		}
		
		double min = 9999;
		double max = -9999;
		int hmin = -1;
		int hmax = -1;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){
				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDay!=lastDay){
				if (hmin>=0 && hmax>=0){
					int diff = Math.abs(hmax-hmin);
					int actualDiff = diffs.get(diff);
					diffs.set(diff, actualDiff+1);
				}
				lastDay = actualDay;
				min = 9999;
				max = -9999;
				hmin=-1;
				hmax=-1;
			}
			
			if (q.getHigh()>max){
				max = q.getHigh();
				hmax = h;
			}
			if (q.getLow()<min){
				min = q.getLow();
				hmin = h;
			}			
		}
		
		int totals = 0;
		for (int i=0;i<=23;i++){
			totals+=diffs.get(i);
		}
		
		
		for (int i=0;i<=23;i++){
			int diffT = diffs.get(i);
			System.out.println("h"+i
					+" "+PrintUtils.Print2dec(diffT*100.0/totals,false)+"%");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.10.31.csv";
		String path5m =  "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.31.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
  		
		int yearF      	 	= 2012;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2012;
		int monthL 			= Calendar.DECEMBER;
		int dL 				= Calendar.MONDAY+0;
		int dH 				= Calendar.MONDAY+0;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		Calendar from2 = Calendar.getInstance();
		Calendar to2 = Calendar.getInstance();
		from.set(2003, 0, 11);
		to.set(2014,  11, 11);
		int range=70;
		int hSplit = 7;
		
		/*for (hSplit = 0;hSplit<=22;hSplit++){
			for (dL=Calendar.MONDAY+2;dL<=Calendar.MONDAY+2;dL++){
				dH =dL+0;
				//for (hSplit = 10;hSplit<=10;hSplit++){
					MaxMinHours.testFollowingMinMax(data, dailyData, from, to, dL, dH, hSplit);
				//}
				//System.out.println("");
			}
			//System.out.println("");
		}*/
		
		//MaxMinHours.testHours(data, from, to, dL, dL+4);
		System.out.println("****MINIMOS****");
		ArrayList<Double> perMin = minmaxAfter(data, from,to,dL+0,dL+4,0,23,true,1);
		System.out.println("****MAXIMOS****");
		ArrayList<Double> perMax = minmaxAfter(data, from,to,dL+0,dL+4,0,23,false,1);
		
		/*System.out.println("HEATER");
		for (int i=0;i<24;i++){
			//System.out.println(i+" "+PrintUtils.Print2(perMin.get(i)));
			double diff = perMax.get(i)-perMin.get(i);
			System.out.println(i+" "+PrintUtils.Print2(perMin.get(i))+" "+PrintUtils.Print2(perMax.get(i))
					+" "+PrintUtils.Print2(diff));
		}*/
		
		/*for (int pips=10;pips<=200;pips+=10){
			//int total1 = minmaxAvgRange(data, from,to,dL+0,dL+4,0,17,pips,0);
			//int total2 = timesmaxRange(data, from,to,dL+0,dL+0,0,11,pips,0);
			int avgRange = timesFinalmaxRange(data, from,to,dL+0,dL+4,0,17,pips,pips+10,2);
			//System.out.println("pips total2 "+pips+" "+total2);
			//System.out.println("pips %: "+pips+" "+PrintUtils.Print2dec(total1*100.0/total2, false)+"%");
		}*/
		//for (int dl=Calendar.MONDAY+0;dl<=Calendar.MONDAY+0;dl++)
			//MaxMinHours.diffs(data, from, to, dL,dH);
			//for (int i=1;i<=24;i++)				
			//MaxMinHours.queryHours(data, from, to, dl, dl, i-1, true,range);
		
		/*for (int h1=0;h1<=23;h1+=1){
			int h2 = h1+4;
			from.set(2012, 0, 1,0,0,0);
			to.set(2013,  11, 31,23,59,59);
			from2.setTimeInMillis(from.getTimeInMillis());
			double avg=0;
			double total=0;
			while (from2.getTimeInMillis()<to.getTimeInMillis()){
				to2.setTime(from2.getTime());
				to2.set(Calendar.DAY_OF_MONTH,1);			
				to2.set(Calendar.HOUR_OF_DAY, 23);
				to2.set(Calendar.MINUTE, 59);
				to2.add(Calendar.MONTH, 1);
				to2.add(Calendar.DAY_OF_MONTH,-1);
				avg+=minmaxAfter(data, from2,to2,dL+0,dL+4,h1,h2,false,false);
				total++;
				from2.add(Calendar.MONTH, 1);
			}
			System.out.println(h1+" "+h2+" "+PrintUtils.Print2dec(avg/total, false));
		}*/
	  
	}
}
