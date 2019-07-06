package drosa.utils;

import java.text.DecimalFormat;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import drosa.auxiliar.DayFrecuency;
import drosa.auxiliar.MonthFrecuency;
import drosa.auxiliar.MonthPeriod;
import drosa.auxiliar.TestResult;
import drosa.auxiliar.TimeFrecuency;
import drosa.auxiliar.WeekPeriod;
import drosa.finances.PeriodInformation;
import drosa.finances.Quote;
import drosa.finances.Trend;
import drosa.simulator.Trade;

public class PrintUtils {

	
	public static String getOHLC(Quote q){
		String str=PrintUtils.Print(q.getOpen())+
				" "+PrintUtils.Print(q.getHigh())+
				" "+PrintUtils.Print(q.getLow())+
				" "+PrintUtils.Print(q.getClose());
		
		
		return str;
	}
	
	public static void Print (List<Quote> data){
		for (int i=0;i<data.size();i++){
			Format formatter;
			formatter = new SimpleDateFormat("yyyy-MM-dd");		
			
			Quote q = data.get(i);
			String dateStr = formatter.format(q.getDate());
			System.out.println(dateStr+","+q.getOpen()+","+q.getHigh()+
					q.getLow()+","+q.getClose()+","+q.getVolume()+","+q.getAdjClose());			
		}
	}
	
	public static void Print(WeekPeriod w){
		List<TimeFrecuency> dfl = w.getTime();
		
		for (int i=0;i<dfl.size();i++){
			DayFrecuency df = (DayFrecuency) dfl.get(i);
			System.out.println("Día "+df.getDay().toString()+": "+df.getFrecuency());
		}
	}
	
	public static void Print(MonthPeriod m){
		List<TimeFrecuency> dfl = m.getTime();
		
		for (int i=0;i<dfl.size();i++){
			MonthFrecuency df = (MonthFrecuency) dfl.get(i);
			System.out.println("Mes "+df.getMonth().toString()+": "+df.getFrecuency());
		}
	}

	public static void Print(String symbol,TestResult tr) {
		// TODO Auto-generated method stub
		if (tr.getTotalCases()>0){
			float percent = (float)(tr.getPositiveCases()*100)/tr.getTotalCases();
			System.out.println("Symbol: "+symbol+".Positive/Total and %: "+tr.getPositiveCases()+"/"+tr.getTotalCases()
					+","+percent+" %");
		}
	}
	
	public static void Print(Trend t) {
		// TODO Auto-generated method stub
		PeriodInformation pi = t.getPeriod();
		
		System.out.println(t.getSymbol()+","+DateUtils.datePrint(pi.getFromDate())+","+
				DateUtils.datePrint(pi.getToDate())+","+pi.getTradingDaysBetween()+","+
				pi.getSpeedAbsolute()+","+pi.getTrendType().name());
	}
	
	
	public static String Print(double d) {
			// TODO Auto-generated method stub		
	           // DecimalFormat df = new DecimalFormat("#.####");                 
		   //return df.format(d).replace(',', '.');
				DecimalFormat df = new DecimalFormat("000.0000");
			   return df.format(d).replace(',', '.');
	            //Formatter df= new Formatter();	           
	            //return df.format("%.4f",d).toString().replace(',', '.');
	}
	
	public static String Print2dec(double d,boolean comma, int zeros) {
		DecimalFormat df = null;
		if (zeros>=6)
			df = new DecimalFormat("000000.00");
		if (zeros==5)
			df = new DecimalFormat("00000.00");
		if (zeros==4)
			df = new DecimalFormat("0000.00");
		if (zeros==3)
			df = new DecimalFormat("000.00");
		if (zeros==2)
			df = new DecimalFormat("00.00");
		if (zeros==1)
			df = new DecimalFormat("0.00");
		
		if (!comma)
			return df.format(d).replace(',', '.');
		return df.format(d);
	}
	
	public static String Print2Int(int d,int zeros) {
		DecimalFormat df = null;
		if (zeros>=6)
			df = new DecimalFormat("000000");
		if (zeros==5)
			df = new DecimalFormat("00000");
		if (zeros==4)
			df = new DecimalFormat("0000");
		if (zeros==3)
			df = new DecimalFormat("000");
		if (zeros==2)
			df = new DecimalFormat("00");
		if (zeros==1)
			df = new DecimalFormat("0");

		return df.format(d);
	}
	
	public static String Print2dec(double d,boolean comma) {
		DecimalFormat df = null;		
		df = new DecimalFormat("0.00");
				
		if (!comma)
			return df.format(d).replace(',', '.');
		return df.format(d);
	}
	
	public static String Print3dec(double d,boolean comma) {
		DecimalFormat df = null;		
		df = new DecimalFormat("0.000");
				
		if (!comma)
			return df.format(d).replace(',', '.');
		return df.format(d);
	}
	
	public static String Print4dec(double d) {
		DecimalFormat df = new DecimalFormat("#.0000");
		return df.format(d).replace(',', '.');
	}
		
	public static String Print5dec(double d) {
		DecimalFormat df = new DecimalFormat("#.00000");
		return df.format(d).replace(',', '.');
	}
	
		public static String Print3(double d) {
	            Formatter df= new Formatter();	           
	            return df.format("%.4f",d).toString().replace(',', '.');
		}
		
	public static String Print2(double d) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("#.##");
	   return df.format(d).replace(',', '.');
	}
	
	public static String Print2(double d,boolean comma) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("#.##");
		
	    String res = df.format(d).replace(',', '.');
	    
	    if (comma)
	    	res = df.format(d);
	    
	    return res;
	}
	
	
	
	public static String Print(Quote q) {//OHLC
		// TODO Auto-generated method stub
		String str = DateUtils.datePrint(q.getDate())
					+" "+Print4dec(q.getOpen())+"/"+Print4dec(q.getHigh())
					+"/"+Print4dec(q.getLow())+"/"+Print4dec(q.getClose());
	   return str;
	}

	public static String Print(double d, int i) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat(".##");
		 return df.format(d).replace(',', '.');
	}



	public static String PrintInt(int d) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("00000");
		   return df.format(d).replace(',', '.');
	}
	
	public static String PrintInt2(int d) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("00");
		   return df.format(d).replace(',', '.');
	}

	public static String Print2dec2(double d, boolean millions) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("000000.00");
		String suf="";
		double d1 = d;
		if (millions){
			if (d>=1000000000000.0){
				d1 = d*1.0/1000000000000.0;
				suf="b";
			}else if (d>=1000000){
					d1 = d*1.0/1000000;
					suf="m";
			}
		}
				
		return df.format(d1)+suf;
	}

	public static void print(ArrayList<Double> values) {
		for (int i=0;i<values.size();i++){
			System.out.println(PrintUtils.Print2(values.get(i), true));
		}
		
	}
	
	public static String getArrayStr(ArrayList<Integer> values,int begin) {
		String str="";
		for (int i=begin;i<values.size();i++){
			str+=values.get(i)+" ";
		}
		return str;
		
	}
	
	
}
