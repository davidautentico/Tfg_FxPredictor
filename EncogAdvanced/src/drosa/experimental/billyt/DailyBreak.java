package drosa.experimental.billyt;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DailyBreak {
	
	public static void test(ArrayList<QuoteShort> data,
			ArrayList<BreakInfo> breaks,
			int y1,int y2,int h1,int h2,
			int tp,int sl,
			int debug
			){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualLow = -1;
		int actualHigh = -1;
		int order = 0;
		int minIdx = 0;
		boolean canTrade = false;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){	
				lastHigh = actualHigh;
				lastLow = actualLow;
				actualHigh = -1;
				actualLow = -1;
				order = 0;
				lastDay = day;
				minIdx = i;
				canTrade = true;
				//System.out.println("[DAY] "+DateUtils.datePrint(cal1)+" || "+lastHigh+" "+lastLow+" || "+q1.toString());
			}
		
			//System.out.println(q.toString());
			
			if (h>=h1 && h<=h2
					&& i>=minIdx
				    && canTrade
					){
				if (lastHigh!=-1){
					int diffH = q.getHigh5()-lastHigh;
					if (diffH>=0 
							//&& q1.getHigh5()<lastHigh
							&& q.getOpen5()<=lastHigh
							){					
						BreakInfo bi = new BreakInfo();
						bi.setIndex(i);
						bi.setOrder(order++);
						bi.setWin(0);
						breaks.add(bi);
						
						for (int j=i;j<data.size()-1;j++){
							diffH = data.get(j).getHigh5()-lastHigh;
							int diffL = lastHigh-data.get(j).getLow5();
							if (diffH>=tp*10){
								bi.setWin(1);
								//if (bi.getOrder()==1 && breaks.get(breaks.size()-2).getWin()==-1)
								if (debug==1)
									System.out.println("H WIN: "+lastHigh+" || "+q.toString()+" || "+data.get(j).toString());
								minIdx = j;//avanzamos
								canTrade = false;
								break;
							}else if (diffL>=sl*10){
								bi.setWin(-1);
								//if (bi.getOrder()==1 && breaks.get(breaks.size()-1).getWin()==-1)
								if (debug==1)
									System.out.println("H LOSS "+lastHigh+" || "+q.toString()+" || "+data.get(j).toString());
								minIdx = j;
								break;
							}
						}										
					}//diffH
				}
				
				if (lastLow!=-1){
					int diffL = lastLow-q.getLow5();
					//System.out.println(q.toString()+" || "+diffL);
					if (diffL>=0 
							//&& q1.getLow5()>lastLow
							&& q.getOpen5()>=lastLow
							){	
						//if (debug==1)
							//System.out.println("TRIGGER LOW: "+lastLow+" || "+q.toString());
						BreakInfo bi = new BreakInfo();
						bi.setIndex(i);
						bi.setOrder(order++);
						bi.setWin(0);
						breaks.add(bi);
						
						for (int j=i;j<data.size()-1;j++){
							diffL = lastLow-data.get(j).getLow5();
							int diffH = data.get(j).getHigh5()-lastLow;
							if (diffL>=tp*10){
								bi.setWin(1);
								//if (bi.getOrder()==1 && breaks.get(breaks.size()-2).getWin()==-1)
								if (debug==1)
									System.out.println("L WIN: "+lastLow+" || "+q.toString()+" || "+data.get(j).toString());
								minIdx = j;
								canTrade = false;
								break;
							}else if (diffH>=sl*10){
								bi.setWin(-1);
								//if (bi.getOrder()==1 && breaks.get(breaks.size()-1).getWin()==-1)
								if (debug==1)
									System.out.println("L LOSS "+lastLow+" || "+q.toString()+" || "+data.get(j).toString());
								minIdx = j;
								break;
							}
						}										
					}
				}
			}
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh) actualHigh = q.getHigh5();
			if (actualLow==-1 || q.getLow5()<=actualLow) actualLow = q.getLow5();
		}
		
	}
	
	
	public static void testv2(ArrayList<QuoteShort> data,
			ArrayList<BreakInfo> breaks,
			int y1,int y2,int h1,int h2,
			int tp,int sl,
			boolean allAllowed,
			int debug
			){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int actualLow = -1;
		int actualHigh = -1;
		int order = 0;
		int minIdx = 0;
		int mode = 0;
		boolean canTrade = false;
		int entry = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){	
				lastHigh = actualHigh;
				lastLow = actualLow;
				actualHigh = -1;
				actualLow = -1;
				order = 0;
				lastDay = day;
				minIdx = i;
				canTrade = true;
				//System.out.println("[DAY] "+DateUtils.datePrint(cal1)+" || "+lastHigh+" "+lastLow+" || "+q1.toString());
			}
		
			//System.out.println(q.toString());
			int diffH = q.getHigh5()-lastHigh;
			int diffL = lastLow-q.getLow5();
			
			if (allAllowed)
				canTrade = true;
			if (canTrade){
				if (mode==0
						&& (h>0 || (h==0 && min>=15))
						//&& canTrade
						){
					if (lastHigh!=-1
							&& q.getOpen5()<=lastHigh
							&& diffH>=0
							){
						mode = 1;	
						entry = lastHigh;
						//if (debug==1)
							//System.out.println("H ENTRY: "+lastHigh+" || "+q.toString());
					}else if (lastLow!=-1
							&& q.getOpen5()>=lastLow
							&& diffL>=0
							){
						mode = -1;
						entry = lastLow;
						//if (debug==1)
							//System.out.println("L ENTRY: "+lastLow+" || "+q.toString());
					}
				}
			}
			
			if (mode==1){
				int diffL0 = entry-q.getLow5();
				if (diffH>=tp*10){
					BreakInfo bi = new BreakInfo();
					bi.setIndex(i);
					bi.setOrder(order++);
					bi.setWin(1);
					breaks.add(bi);
					
					if (debug==1)
						System.out.println("H WIN: "+entry+" || "+q.toString());
					
					canTrade = false;
					mode = 0;
				}else if (diffL0>=sl*10){
					BreakInfo bi = new BreakInfo();
					bi.setIndex(i);
					bi.setOrder(order++);
					bi.setWin(-1);
					breaks.add(bi);
					
					if (debug==1)
						System.out.println("[H LOSS] "+entry+" || "+q.toString());
					
					mode = 0;
				}
			}
			
			if (mode==-1){
				int diffH0 = q.getHigh5()-entry;
				if (diffL>=tp*10){
					BreakInfo bi = new BreakInfo();
					bi.setIndex(i);
					bi.setOrder(order++);
					bi.setWin(1);
					breaks.add(bi);
					
					if (debug==1)
						System.out.println("L WIN: "+entry+" || "+q.toString());
					
					canTrade = false;
					mode = 0;
				}else if (diffH0>=sl*10){
					BreakInfo bi = new BreakInfo();
					bi.setIndex(i);
					bi.setOrder(order++);
					bi.setWin(-1);
					breaks.add(bi);
					
					if (debug==1)
						System.out.println("[L LOSS] "+entry+" || "+q.toString());
					
					mode = 0;
				}
			}
			
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh) actualHigh = q.getHigh5();
			if (actualLow==-1 || q.getLow5()<=actualLow) actualLow = q.getLow5();
		}
		
	}
	
	private static void testWicks(ArrayList<QuoteShort> data,int y1,int y2) {

		int cases = 0;
		int acc = 0;
		Calendar cal = Calendar.getInstance();
		ArrayList<Integer> avgs = new ArrayList<Integer>();//0-9,10-19,
		for (int i=0;i<=10;i++){
			avgs.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			int diffH = q.getHigh5()-q1.getHigh5();
			int diffL = q1.getLow5()-q.getLow5();
			
			if (diffH>=0){
				acc += diffH;
				cases++;
				
				int idx = (int) ((diffH*0.1)/10-1);
				if (idx>10) idx=10;
				if (idx<0) idx=0;
				int tot = avgs.get(idx);
				avgs.set(idx, tot+1);
			}
			
			if (diffL>=0){
				acc += diffL;
				cases++;
				
				int idx = (int) ((diffL*0.1)/10-1);
				if (idx>10) idx=10;
				if (idx<0) idx=0;
				int tot = avgs.get(idx);
				avgs.set(idx, tot+1);
			}
								
		}
		
		
		double avg = acc*0.1/cases;
		System.out.println(
				y1+" "+y2+" || "
				+" "+cases+" "+PrintUtils.Print2dec(avg, false)
				);
		
		for (int i=0;i<=10;i++){
			System.out.println(avgs.get(i));
		}
		
	}
	
	private static void testWicks2(ArrayList<QuoteShort> data,int y1,int y2,int min,int range) {

		int casesMin = 0;
		int cases = 0;
		int cases0 = 0;
		int acc = 0;
		Calendar cal = Calendar.getInstance();
		ArrayList<Integer> avgs = new ArrayList<Integer>();//0-9,10-19,
		for (int i=0;i<=10;i++){
			avgs.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			int range1 = q1.getHigh5()-q1.getLow5();
			
			if (range1>=range*10) continue;
			
			int diffH = q.getHigh5()-q1.getHigh5();
			int diffL = q1.getLow5()-q.getLow5();
			
			if (diffH>=0){
				acc += diffH;
				cases++;								
			}
			
			if (diffL>=0){
				acc += diffL;
				cases++;
				
			}
			
			if (diffH>=0 || diffL>=0){
				cases0++;
				if (diffH<min*10 && diffL<min*10){
					casesMin++;
					//System.out.println(DateUtils.datePrint(cal));
				}
			}
								
		}
		
		
		double avg = acc*0.1/cases;
		System.out.println(
				y1+" "+y2+" || "
				+" "+cases+" "+casesMin
				+" "+PrintUtils.Print2dec(casesMin*100.0/cases0, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_1 Min_Bid_2003.01.01_2017.03.14.csv";
			
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
		ArrayList<QuoteShort> dailyData 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			
			ArrayList<BreakInfo> breaks = new ArrayList<BreakInfo>();
			
			dailyData  = ConvertLib.createDailyDataShort(data);
			
			//for (int range=2000;range<=2000;range+=10){
				//DailyBreak.testWicks2(dailyData,2014,2017,10,range);
			//}
			
			//eurusd: 15-20
			for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+8;
				for (int h1=0;h1<=0;h1++){
					for (int h2=23;h2<=23;h2++){
						for (int tp=1;tp<=90;tp+=1){
							for (int sl=30;sl<=30;sl+=1){
								breaks.clear();
								DailyBreak.testv2(data, breaks, y1, y2, h1, h2, tp, sl,false,0);
								String header = y1+" "+y2+" "+tp+" "+sl;
								//BreakInfo.analyze(header,breaks,tp,sl);
								BreakInfo.analyze7(header,breaks,tp,sl);
								//for (int diff=0;diff<=200;diff+=12)
								//BreakInfo.analyze2(breaks,diff);
							}
						}
					}
				}
				
			}
		
		}
		
		System.out.println("FINISH");
	}

	

}
