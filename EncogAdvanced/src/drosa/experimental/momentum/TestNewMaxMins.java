package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestNewMaxMins {

	public static void test(String header,ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,int tp,int sl,
			int distance,
			int diff,
			int minLegSize,
			int actualSize1,int actualSize2,
			boolean debug
			){
	
		int wins = 0;
		int total = 0;
		//LEGS
		int actualLeg = 0;
		int actualHigh = -1;
		int actualLow = -1;
		int indexHigh = -1;
		int indexLow = -1;
		int index1 = -1;
		int index2 = -1;
		int index0 = -1;
		int actualDayTrends = 0;
		int lastDay = -1;
		int totalDays = 0;
		int totalDayTrades = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			if (h<h1 || h>h2) continue;
			
			if (day!=lastDay){
				index0=i;
				index1=i;
				index2=i;
				actualHigh = -1;
				actualLow = -1;
				actualLeg = 0;
				actualDayTrends = 0;
				lastDay = -1;
				totalDays++;
				lastDay = day;
				totalDayTrades=0;
			}
			
			if (index1==-1){
				index0 = 0;
				index1 = i;
				index2 = i;
			}
			//actualizacion de legs
			QuoteShort ql1 = data.get(index1);
			QuoteShort ql2 = data.get(index2);	
			
			QuoteShort maxMin = maxMins.get(i-1);//hasta el anterior
			int highDiffq = q.getHigh5()-maxMin.getHigh5();
			int lowDiffq = maxMin.getLow5()-q.getLow5();
			int CO = q.getClose5()-q.getOpen5();
			int actualSizeH = (int) ((q.getOpen5()-ql1.getLow5())*0.1);
			int actualSizeL = (int) ((ql1.getHigh5()-q.getOpen5())*0.1);
			if (highDiffq>=distance*10
					&& actualSizeH>=actualSize1 && actualSizeH<=actualSize2
					//&& CO>diff*10
					){//SHORT
				int tpValue = q1.getOpen5()-10*tp;
				int slValue = q1.getOpen5()+10*sl;
				int indexH = TradingUtils.getMaxMinIndex(data, i+1, i+1+4000,slValue, true);
				int indexL = TradingUtils.getMaxMinIndex(data, i+1, i+1+4000,tpValue, false);
				if (indexH!=-1 || indexL!=-1){
					if (indexL>=0 && (indexL<indexH || indexH==-1))
						wins++;
					total++;
				}
			}else if (lowDiffq>=distance*10
					&& actualSizeL>=actualSize1 && actualSizeL<=actualSize2
					//&& CO<-diff*10
					){//LONG
				int tpValue = q1.getOpen5()+10*tp;
				int slValue = q1.getOpen5()-10*sl;
				int indexH = TradingUtils.getMaxMinIndex(data, i+1, i+1+4000,tpValue, true);
				int indexL = TradingUtils.getMaxMinIndex(data, i+1, i+1+4000,slValue, false);
				if (indexH!=-1 || indexL!=-1){
					if (indexH>=0 && (indexH<indexL || indexL==-1))
						wins++;
					total++;
				}
			}
			
			//gestionde LEGS
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
				indexHigh = i;
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow = q.getLow5();
				indexLow = i;
			}
			
			if (actualLeg==0){
				double longDiff0 = (q.getHigh5()-data.get(indexLow).getLow5())*0.1;
				double shortDiff0 = (data.get(indexHigh).getHigh5()-q.getLow5())*0.1;
				//System.out.println(longDiff0+" "+shortDiff0+" "+i+" "+index1);
				if (longDiff0>=minLegSize 
						&& indexLow!=i
						){
					index1 = indexLow;
					actualLeg = 1;
					index2 = i;
					actualDayTrends++;
					if (debug)
					System.out.println("[NUEVA LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" || "+q.getClose5());
				}else if (shortDiff0>=minLegSize  
						&& indexHigh!=i
						){
					index1 = indexHigh;
					actualLeg = -1;
					index2 = i;
					actualDayTrends++;
					if (debug)
					System.out.println("[NUEVA LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" || "+q.getClose5());
				}
			}else if (actualLeg==1){
				int lowDiff = (int) ((ql2.getHigh5()-q.getLow5())*0.1);
				//System.out.println("LONG "+shortDiff);
				if (q.getHigh5()>=ql2.getHigh5()){
					int actualSize = (int) ((q.getHigh5()-ql1.getLow5())*0.1);
					if (index0<=0 && actualSize>=minLegSize) index0 = i;
					index2 = i;
					//if (h==9 || h==10)
					//System.out.println("[AMPLIACION LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" "+actualSize);
				}else if (lowDiff>=minLegSize){
					actualLeg = -1;
					index0 = -1;
					index1 = index2;
					index2 = i;
					actualDayTrends++;				
					if (debug)
						System.out.println("[NUEVA LEG] "+DateUtils.datePrint(cal)+" "+actualLeg
								+" "+ql2.getHigh5()+" "+q.getLow5()
								+" || "+actualDayTrends
								+" || "+q.getClose5()
								);
				}
			}else if (actualLeg==-1){//BEAR
				int highDiff = (int) ((q.getHigh5() -ql2.getLow5())*0.1);
				if (q.getLow5()<=ql2.getLow5()){
					int actualSize = (int) ((ql1.getHigh5()-q.getLow5())*0.1);
					if (index0<=0 && actualSize>=minLegSize) index0 = i;
					index2 = i;
					//System.out.println("[AMPLIACION LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" "+actualSize);
				}else if (highDiff>=minLegSize){
					actualLeg = 1;
					index0 = -1;
					index1 = index2;
					index2 = i;
					actualDayTrends++;				
					if (debug)
						System.out.println("[NUEVA LEG] "+DateUtils.datePrint(cal)+" "+actualLeg
								+" "+ql2.getLow5()+" "+q.getHigh5()
								+" || "+actualDayTrends
								+" || "+q.getClose5()
								);
				}
			}					
		}
		
		int losses = total-wins;
		double perWin = wins*100.0/total;
		double pf = (wins*tp)*1.0/(losses*sl);
		double avg = (wins*tp-losses*sl)*1.0/total;
		System.out.println(header
				+" || "
				+" "+PrintUtils.Print2Int(total,5)
				+" "+PrintUtils.Print2Int(wins,5)
				+" "+PrintUtils.Print2dec(perWin, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void main(String[] args) {
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.10.21.csv";	
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		
		for (int i = 0;i<=limit;i++){
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
			
			System.out.println("total data: "+data.size());
			for (int nBars=50;nBars<=5000;nBars+=50){
				ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinNBars(data, nBars); 
				
				for (int distance=0;distance<=0;distance++){
					for (int h1=0;h1<=0;h1++){
						for (int h2=h1+9;h2<=h1+9;h2++){
							for (int tp=18;tp<=18;tp++){
								for (int sl=2*tp;sl<=2*tp;sl+=tp){
									for (int y1=2003;y1<=2003;y1++){
										int y2 = y1+12;
										for (int diff=0;diff<=0;diff++){
											for (int legthr1=10;legthr1<=10;legthr1++){
												for (int legthr2=legthr1+36;legthr2<=legthr1+36;legthr2++){
													String header = tp+" "+sl+" "+h1+" "+h2+" "+nBars+" "+distance+" "+legthr1+" "+legthr2;
													TestNewMaxMins.test(header,data, maxMins,y1,y2, h1, h2, tp, sl, distance,diff,20,legthr1,legthr2,false);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}//nBars
		}//limit
		
	}
}
