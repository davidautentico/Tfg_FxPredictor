package drosa.experimental.maxmins;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.ticks.TestMaxMins;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMinutes {
	
	
	private static int testQuoteSL(ArrayList<QuoteShort> data, int idx, int entry, int tp, int sl, int mode) {
		for (int i=idx;i<=data.size()-1;i++){
			QuoteShort q = data.get(i);
						
			if (mode==-1){
				int difftp = entry-q.getLow5();
				int diffsl = q.getHigh5()-entry;
				if (difftp>=10*tp){
					return 1;
				}else if (diffsl>=10*sl){
					return -1;
				}
			}else if (mode==1){
				int difftp = q.getHigh5()-entry;
				int diffsl = entry-q.getLow5();
				if (difftp>=10*tp){
					return 1;
				}else if (diffsl>=10*sl){
					return -1;
				}
			}
		}
				
		return 0;
	}
	
	public static void testMaxMins(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int h1,int h2,int tp,int sl,
			int thr
			){
		
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		int high = -1; //maximo bid
		int low = -1; //minimo ask
		int entry = 0;
		
		int trades = 0;
		int wins = 0;
		int losses = 0;
		int days = 0;
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i+1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			
			if (day!=lastDay){	
				if (lastDay!=-1){
					//System.out.println("hmax hmin "+hmax+" "+hmin);
					days++;
				}
				mode = 0;
				high = -1; //maximo bid
				low = -1; //minimo ask
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i);
			if (mode==0){
				int diffhl = high-low;
				if (h>=h1 && h<=h2){	
					 
					int res = 0;
					if (high!=-1 
							&& maxMin>=thr
							//&& q.getHigh5()>=high
							){
						//entro en bid con sell						
						entry = q1.getOpen5();
						mode = -1;
						int diffEntry = Math.abs(high-entry);
						//if (diffEntry<=50)
							res = TestMinutes.testQuoteSL(data,i+1,entry, tp, sl,mode);
					}
					if (low!=-1 
							&& maxMin<=-thr
							//&& q.getLow5()<=low
							){
						//entro en ask con buy
						entry = q1.getOpen5();
						mode = 1;
						
						int diffEntry = Math.abs(low-entry);
						//if (diffEntry<=50)
							res = TestMinutes.testQuoteSL(data,i+1,entry, tp, sl,mode);
					}
					if (res==1){
						wins++;
						trades++;
						mode = 0;
					}else if (res==-1){
						losses++;
						trades++;
						mode = 0;
					}
				}
			}
			
									
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				//hmax = h;
			}
			if (low==-1 ||q.getLow5()<=low){
				low = q.getLow5();
				//hmin = h;
			}							
		}
		
		if (trades>0){
			double winPer = wins*100.0/trades;
			double pf = (wins*tp*1.0)/(losses*sl);
			double avg = (wins*tp-losses*sl)*1.0/trades;
			
			
			System.out.println(
					
					h1+" "+h2+" "+tp+" "+sl+" "+thr
					+" || "
					+" "+days
					+" "+trades
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(avg, false)
					);
		}else{
			System.out.println(
					
					h1+" "+h2+" "+tp+" "+sl+" "+thr
					+" || "
					+"------ "
					);
			
		}

	}
	
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String pathEURUSD= "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.12.31_2017.09.08.csv";
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int p=0;p<=paths.size()-1;p++){
			String pathc = paths.get(p);
			dataI 		= DAO.retrieveDataShort5m(pathc, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
				
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+0;
					for (int tp=5;tp<=40;tp++){
						for (int sl=tp*1;sl<=tp*6;sl+=1*tp){
							for (int thr=500;thr<=500;thr+=50){
								TestMinutes.testMaxMins(data,maxMins, h1, h2, tp, sl,thr);
							}
						}
					}
				}
			
		}

	}

}
