package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrendingIndicatorStudy {

	public static double trendPercent(int ups,int downs){
		int total = ups+downs;
		
		double uP = ups*100.0/total;
		double dP = downs*100.0/total;
		double diff = uP-dP;
		if (uP<dP){
			diff = dP-uP;
		}
		return diff;
	}
	
	public static void trendingLevel(ArrayList<Quote> data,Calendar from, int dayL, int dayH, Calendar to,int pips){
		
		ArrayList<Integer> ups = new ArrayList<Integer>();
		ArrayList<Integer> downs = new ArrayList<Integer>();
		int lastDay   = -1;
		int actualDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int upCount		= 0;
		int downCount	= 0;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}	
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (day!=lastDay){
				if (lastDay!=-1){
					ups.add(upCount);
					downs.add(downCount);
					//System.out.println("up down: "+upCount+" "+downCount
					//		+" "+PrintUtils.Print(trendPercent(upCount,downCount)));
				}				
				upCount  = 0;
				downCount= 0;
				lastDay = day;				
			}
			
			int t=0;
			boolean upReached   = false;
			boolean downReached = false;
			for (int j=i+1;j<data.size();j++){
				Quote q1 = data.get(j);
				cal1.setTime(q1.getDate());
				int day1 = cal1.get(Calendar.DAY_OF_YEAR);
				if (day1!=day) break;
				//up test
				int upPips   = TradingUtils.getPipsDiff(q1.getHigh(), q.getLow());
				//down test
				int downPips = TradingUtils.getPipsDiff(q.getHigh(), q1.getLow());
				
				if (upPips>=pips){
					upReached = true;
				}
				if (downPips>=pips){
					downReached = true;
				}
				if (downReached && upReached) break;
				//t++;				
			}	
			if (upReached) upCount++;
			if (downReached) downCount++;
			//System.out.println("t: "+t);
		}
		int count100=0;
		int count90=0;
		int count80=0;
		int count70=0;
		int count60=0;
		int count50=0;
		int count40=0;
		int count30=0;
		int count20=0;
		int count10=0;
		int total30=0;
		double acc = 0;
		int totals=0;
		int globalTotal=0;
		for (int i=0;i<ups.size();i++){
			int u = ups.get(i);
			int d = downs.get(i);
			int t = u + d;
			double uP = u*100.0/t;
			double dP = d*100.0/t;
			double diff = uP-dP;
			if (uP<dP){
				diff = dP-uP;
			}
			if (diff>=0){
				acc+=diff;
				
				if (diff>=10){ count10++;}
				if (diff>=20){ count20++;}
				if (diff>=30){ count30++;}
				if (diff>=40){ count40++;}
				if (diff>=50){ count50++;}
				if (diff>=60){ count60++;}
				if (diff>=70){ count70++;}
				if (diff>=80){ count80++;}
				if (diff>=90){ count90++;}
				if (diff>=100){ count100++;}
				totals++;
				globalTotal+=t;
				//System.out.println("global total: "+globalTotal);
			}
		}
		double diff = acc*1.0/totals;
		double diff1 = (100-diff)/2;
		double diff2 = 100-diff1;
		System.out.println("Pips AVG Trending rate >=100: "
				+" "+pips
				+" "+PrintUtils.Print(acc*1.0/totals)+"% "
				+" ("+PrintUtils.Print2dec(diff1/diff1, false)				
				+"-"+PrintUtils.Print2dec(diff2/diff1, false)+")"
				+" "+PrintUtils.Print2dec(globalTotal*1.0/totals,false)
				+" "+PrintUtils.Print(count100*100.0/totals)+"%");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m = "c:\\fxdata\\EURUSD_5 Mins_Bid_2003.05.04_2014.01.07.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 	= TradingUtils.cleanWeekendData(dataS);
		
		int yearF      	 	= 2012;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2012;
		int monthL 			= Calendar.DECEMBER;
		int dL 				= Calendar.MONDAY;
		int dH 				= Calendar.MONDAY;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		Calendar from2 = Calendar.getInstance();
		Calendar to2 = Calendar.getInstance();
		from.set(2013,4, 1);
		to.set(2014,  0, 31);
		
		int dayL = Calendar.MONDAY;
		int dayH = Calendar.MONDAY;
		for (int i=10;i<=100;i+=5)
			TrendingIndicatorStudy.trendingLevel(data, from,dayL+2,dayH+2,to, i);
	}

}
