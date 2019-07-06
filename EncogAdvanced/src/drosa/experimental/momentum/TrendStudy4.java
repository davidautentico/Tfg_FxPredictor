package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrendStudy4 {
	
	public static void studyNbars(ArrayList<QuoteShort> data,
			int h1,int h2,int minSize,int thr,int nbars,
			int maxTrades,
			boolean debug
			){
		
		Calendar calq = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int avgacc = 0;
		int total = 0;
		int wins = 0;
		int bes = 0;
		int minbesAcc = 0;
		int lastDay = -1;
		int actualLeg = 0;
		int index1 = 0;
		int index2 = 0;
		int dovalue = 0;
		int lastHighTrade = -1;
		int lastLowTrade = -1;
		int dayTrades = 0;
		for (int i=0;i<data.size()-nbars;i++){			
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(calq, q); 
			int day = calq.get(Calendar.DAY_OF_YEAR);
			int h = calq.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				//System.out.println(("[NUEVO DIA] "+i+" || "+q.toString()+" "+day+" "+dayTrades));
				dovalue = q.getOpen5();
				index1=i;
				index2=i;
				lastHighTrade = -1;
				lastLowTrade = -1;
				actualLeg = 0;
				dayTrades = 0;				
				lastDay = day;
			}
			
			int diffH  = q.getHigh5()-data.get(index1).getLow5();
			int diffL  = data.get(index1).getHigh5()-q.getLow5();
			int diffHL = data.get(index2).getHigh5()-q.getLow5();
			int diffLH = q.getHigh5()-data.get(index2).getLow5();
			if (actualLeg==0){								
				if (diffH>=10*minSize){
					actualLeg=1;
					index2 = i;
				}else if (diffL>=10*minSize){
					actualLeg=-1;
					index2 = i;
				}
			}else if (actualLeg==1){
				if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
				}else if (diffLH>=10*minSize){
					index1 = index2;
					index2 = i;
					actualLeg=-1;
					if (debug){
						//System.out.println("[LEG DOWN] "+DateUtils.datePrint(cal1)+" "+data.get(index1).toString()+" || "+data.get(index2).toString());
					}
				}
			}else if (actualLeg==-1){
				if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
				}else if (diffHL>=10*minSize){
					index1 = index2;
					index2 = i;
					actualLeg=1;
					if (debug){
						//System.out.println("[LEG UP] "+DateUtils.datePrint(cal1)+" "+data.get(index1).toString()+" || "+data.get(index2).toString());
					}
				}
			}
			
			if (h>=h1 && h<=h2
					&& dayTrades<maxTrades
					){
				if (actualLeg==1 						
						&& q.getHigh5()>=dovalue
						){
						int currentSize = data.get(i).getHigh5()-data.get(index1).getLow5();
						if (currentSize>=10*thr
								&& (data.get(i).getHigh5()>=lastHighTrade || lastHighTrade==-1)
								){
							
							TradingUtils.getMaxMinShortTP(data, qm, cal2, i+1, i+nbars, data.get(i+1).getOpen5()-10*10);						
							if (qm.getClose5()<=data.get(i+1).getOpen5()-10*10){
								bes++;
							}else{
								minbesAcc += qm.getHigh5()-data.get(i+1).getOpen5();
							}
							qm.copy(data.get(i+nbars));
							int maxDiff = data.get(i+1).getOpen5()-qm.getLow5();
							int minDiff = qm.getHigh5()-data.get(i+1).getOpen5();
							//avgacc += maxDiff-minDiff;
							int diff = data.get(i+1).getOpen5()-qm.getClose5();
							avgacc += diff;
							total++;
							if (maxDiff>=minDiff) wins++;
							lastHighTrade = data.get(i+1).getOpen5();	
							dayTrades++;
							if (debug){
								System.out.println("[SHORT] "+data.get(i+1).toString()+" "+diff+" || "+total+" "+dayTrades+" "+maxTrades);
							}
						}
				}else if (actualLeg==-1 
						&& q.getLow5()<=dovalue
						){
						int currentSize = data.get(index1).getHigh5()-data.get(i).getLow5();
						if (currentSize>=10*thr
								&& (data.get(i).getLow5()<=lastLowTrade || lastLowTrade==-1)
								){
							
							//TradingUtils.getMaxMinShort(data, qm, cal2, i+1, i+nbars);
							TradingUtils.getMaxMinShortTP(data, qm, cal2, i+1, i+nbars, data.get(i+1).getOpen5()+10*10);
							if (qm.getClose5()>=data.get(i+1).getOpen5()+10*10){
								bes++;
							}else{
								minbesAcc += data.get(i+1).getOpen5()-qm.getLow5();
							}
							
							qm.copy(data.get(i+nbars));
							int minDiff = data.get(i+1).getOpen5()-qm.getLow5();
							int maxDiff = qm.getHigh5()-data.get(i+1).getOpen5();
							//avgacc += maxDiff-minDiff;
							int diff = qm.getClose5()-data.get(i+1).getOpen5();
							avgacc += diff;
							total++;
							if (maxDiff>=minDiff) wins++;
							lastLowTrade = data.get(i+1).getOpen5();
							dayTrades++;
							if (debug){
								System.out.println("[LONG] "+data.get(i+1).toString()+" "+diff+" || "+total+" "+dayTrades+" "+maxTrades);
							}
						}
				}
			}//trading			
		}//for
		
		System.out.println(
				h1+" "+h2
				+" "+minSize+" "+thr+" "+nbars+" "+maxTrades
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(avgacc*0.1/total, false)
				+" "+PrintUtils.Print2dec(wins*100.0/total, false)
				+" || "+bes
				+" "+PrintUtils.Print2dec(bes*100.0/total, false)
				+" || "+(total-bes)+" "+PrintUtils.Print2dec(minbesAcc*0.1/(total-bes), false)
				//+" || "+bes*10+" "+minbesAcc*0.1
				+" || "+PrintUtils.Print2dec((bes*10.0)/(minbesAcc*0.1),false)
				);
	}
	
	public static void studyCountHour(ArrayList<Trend> trends,int h1,int h2){
		
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		int count5 = 0;
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int totalDay = 0;
		int days = 0;
		double avgSize0 = 0;
		double avgSize0_0 = 0;
		int total0 = 0;
		int total0_0 = 0;
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			
			QuoteShort.getCalendar(cal1, t.getQ1()); 
			int day = cal1.get(Calendar.DAY_OF_YEAR);
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			if (day!=lastDay){	
			
				if (totalDay>=1){
					count1++;
				}
				if (totalDay>=2) count2++;
				if (totalDay>=3) count3++;
				if (totalDay>=4) count4++;
				if (totalDay>=5) count5++;
				days++;
				totalDay = 0;
				lastDay = day;
			}
			
			if (h>=h1 && h<=h2){
				totalDay++;
				if (totalDay==1){
					avgSize0 += t.getSize();
					total0++;
				}
			}else if (h>h2){
				if (totalDay==1){
					avgSize0_0 += trends.get(i-1).getSize();
					total0_0++;
					totalDay++;
				}
			}
		}
		
		double count1Per = count1*100.0/days;
		double count2Per = count2*100.0/days;
		double count3Per = count3*100.0/days;
		double count4Per = count4*100.0/days;
		double count5Per = count5*100.0/days;
		
		System.out.println(
				h1+" "+h2
				+" || "
				+" "+days
				+" "+PrintUtils.Print2dec(count1Per, false)
				+" "+PrintUtils.Print2dec(count2Per, false)
				+" "+count2
				+" || "+PrintUtils.Print2dec(avgSize0/total0, false)
				+" || "+PrintUtils.Print2dec(avgSize0_0/total0_0, false)+" "+total0_0
				//+" "+PrintUtils.Print2dec(count3Per, false)
				//+" "+PrintUtils.Print2dec(count4Per, false)
				//+" "+PrintUtils.Print2dec(count5Per, false)
				);
	}

	public static void study(ArrayList<Trend> trends,int h1,int h2,int minTrendSize){
	
		int wins = 0;
		int total = 0;
		double avg1 = 0;
		double avg2 = 0;
		Calendar cal1 = Calendar.getInstance();
		for (int i=0;i<trends.size()-2;i++){
			Trend t = trends.get(i);
			Trend t1 = trends.get(i+1);
			Trend t2 = trends.get(i+2);
			
			int h = t.getHour1(cal1);
			if (h<h1 || h>h2) continue;
			if (t.getSize()>=minTrendSize){
				if (t2.getSize()>=t1.getSize()){
					wins++;
				}
				avg1 += t1.getSize();
				avg2 += t2.getSize();
				total++;
			}
		}
		
		double winPer = wins*100.0/total;
		
		System.out.println(
				minTrendSize
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(avg1/total, false)
				+" "+PrintUtils.Print2dec(avg2/total, false)
				+" "+PrintUtils.Print2dec(avg1/avg2, false)
				);
	}
	
	public static void main(String[] args) throws Exception {
		
		//String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.06.21.csv";
		String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2008.12.31_2016.06.21.csv";
		//String pathEURUSD ="C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2016.06.21.csv";
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		
		int limit = 0;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);		
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			/*for (int j=0;j<data.size();j++){
				System.out.println(data.get(j).toString());
			}*/
			//TrendStudy4.studyNbars(data,0, 9, 20,20, 1000,1,true);
			for (int minSize=20;minSize<=20;minSize++){
				int thr = minSize;
				int tokenH = 23;
								
				for (int minTrendSize=60;minTrendSize<=60;minTrendSize+=10){
					//ArrayList<Trend> trends = Trend.calculateTrends(data, 0, data.size()-1, minSize,minTrendSize,tokenH,true);
					//TrendStudy4.study(trends,0,9, minTrendSize);
					for (int h1=0;h1<=0;h1++){
						for (int h2=9;h2<=9;h2++){
							//TrendStudy4.studyCountHour(trends, h1, h2);
							for (int nbars=1;nbars<=240;nbars+=1){
								TrendStudy4.studyNbars(data,h1, h2, minSize,minTrendSize, nbars,10,false);
							}
						}
					}
				}
			}
			
		}//limit
		
		
	}

}
