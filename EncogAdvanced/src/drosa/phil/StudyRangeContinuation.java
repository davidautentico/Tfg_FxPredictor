package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyRangeContinuation {
	
	
	public static ArrayList<Integer> hourlyRange(ArrayList<Quote> data,Calendar from,Calendar to,int hl,int hh){
		
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		Calendar actualDate = Calendar.getInstance();
		int actualDay 	= -1;
		int actualRange = 0;
		Quote qRange = new Quote();
		qRange.setLow(9999);
		qRange.setHigh(-9999);
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			actualDate.setTime(q.getDate());
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() || actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			int h	= actualDate.get(Calendar.HOUR_OF_DAY);
			int day = actualDate.get(Calendar.DAY_OF_YEAR);
			
			//System.out.println("hour: "+h);
			if (day!=actualDay){
				if (actualDay!=-1){
					//System.out.println("day actualDay: "+day+" "+actualDay);
					actualRange = TradingUtils.getPipsDiff(qRange.getHigh(), qRange.getLow());
					ranges.add(actualRange);
					qRange.setLow(9999);
					qRange.setHigh(-9999);
				}					
				actualDay=day;
			}
			
			if (h>=hl && h<=hh){
				if (qRange.getHigh()<-999 || TradingUtils.getPipsDiff(q.getHigh(), qRange.getHigh())>=0) {
					qRange.setHigh(q.getHigh());
				}
				if (qRange.getLow()>999 || TradingUtils.getPipsDiff(qRange.getLow(),q.getLow())>=0) {
					qRange.setLow(q.getLow());
				}				
			}
			
		}
		
		//actualRange = TradingUtils.getPipsDiff(qRange.getHigh(), qRange.getLow());
		//ranges.add(actualRange);
		
						
		return ranges;
	}
	
public static ArrayList<Integer> hourlyRangefromDO(ArrayList<Quote> data,Calendar from,Calendar to,int hl,int hh){
		
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		Calendar actualDate = Calendar.getInstance();
		int actualDay 	= -1;
		int actualRange = 0;
		Quote qRange = new Quote();
		qRange.setLow(9999);
		qRange.setHigh(-9999);
		double rangeDO = -9999;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			actualDate.setTime(q.getDate());
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() || actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			int h	= actualDate.get(Calendar.HOUR_OF_DAY);
			int day = actualDate.get(Calendar.DAY_OF_YEAR);
			
			//System.out.println("hour: "+h);
			if (day!=actualDay){
				if (actualDay!=-1){
					if (rangeDO>0){
						//System.out.println("day actualDay: "+day+" "+actualDay);
						actualRange = TradingUtils.getPipsDiff(qRange.getClose(), rangeDO);
						ranges.add(actualRange);
						rangeDO =-999;
					}
				}					
				actualDay=day;
			}
			
			if (h==hl){
				rangeDO = q.getOpen();
			}	
			if (h==hh){
				qRange.setClose(q.getClose());
			}
		}
		
		//actualRange = TradingUtils.getPipsDiff(qRange.getHigh(), qRange.getLow());
		//ranges.add(actualRange);
		
						
		return ranges;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path   = "c:\\fxdata";
		String file1H = path+"\\"+"EURUSD_Hourly_Bid_2009.01.01_2013.09.30.csv";
		String symbol    	="EURUSD";
		int yearF      	 	= 2009;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2013;
		int monthL 			= Calendar.DECEMBER;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		
		from.set(yearF, monthF, 1);
		to.set(yearL, monthL, 31);
		
		ArrayList<Quote> dataI 			= DAO.retrieveData(file1H, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
  		//ArrayList<Quote> dailyData 		= ConvertLib.createDailyData(data);
  		
  		
  		for (int hl1=0;hl1<=15;hl1++){  			
  			for (int hh1=15;hh1<=16;hh1++){
  				int hl2 = hh1+1;
  				//int hh2 = hl2+2;
  				int hh2 = 23;
  				//System.out.println("hour: "+hl1+ " "+hh1);
  				ArrayList<Integer> range1 = StudyRangeContinuation.hourlyRangefromDO(data,from,to,hl1,hh1);
  		  		ArrayList<Integer> range2 = StudyRangeContinuation.hourlyRangefromDO(data,from,to,hl2,hh2);
  		  		
  		  		int totalPos12=0;
		  		int totalPos1=0; 
  		  		for (int r1Th=50;r1Th<=120;r1Th+=10){
  		  			for (int r2Th=0;r2Th<=0;r2Th+=1){
  				  		//int totalPos12=0;
  				  		//int totalPos1=0;  		
  				  		for (int i =0;i<range1.size();i++){
  				  			int r1 = range1.get(i);
  				  			int r2 = range2.get(i);
  				  			//System.out.println(range1.get(i)+" "+range2.get(i));
  				  			if (r1>r1Th){
  				  				totalPos1++;
  				  				if (r2>r2Th){
  				  					totalPos12++;
  				  				}
  				  			}
  				  		}
  				  		//System.out.println(hl1+"-"+hh1+" "+hl2+"-"+hh2+" "+r1Th+" "+r2Th+" : "+totalPos1+" "+totalPos12+" "+PrintUtils.Print2(totalPos12*100.0/totalPos1));
  		  			}
  		  		}
  		  	System.out.println(hl1+"-"+hh1+" "+hl2+"-"+hh2+" : "+totalPos1+" "+totalPos12+" "+PrintUtils.Print2(totalPos12*100.0/totalPos1));
  	  		}
  		}
  		
		
	}
	

}
