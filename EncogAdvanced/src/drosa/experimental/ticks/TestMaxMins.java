package drosa.experimental.ticks;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.experimental.ticksStudy.TestTicks;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TestMaxMins {
	
	
	
	private static int testTickSL(ArrayList<Tick> ticks, int idx,int entry, int tp, int sl, int mode) {
		
		
		for (int i=idx;i<=ticks.size()-1;i++){
			Tick t = ticks.get(i);
						
			if (mode==-1){
				int diff = entry-t.getAsk();
				if (diff>=10*tp){
					return 1;
				}else if (diff<=-10*sl){
					return -1;
				}
			}else if (mode==1){
				int diff = t.getBid()-entry;
				if (diff>=10*tp){
					return 1;
				}else if (diff<=-10*sl){
					return -1;
				}
			}
		}
				
		return 0;
	}
	
	public static void testMaxMins(ArrayList<Tick> ticks,
			int h1,int h2,int tp,int sl,
			TickStats ts
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
		int hmax = -1;
		int hmin = -1;
				int days = 0;
		for (int i=0;i<ticks.size()-1;i++){
			
			Tick t = ticks.get(i);
			
			Tick.getCalendar(cal, t);
			
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
			
			if (mode==0){
				if (h>=h1 && h<=h2){		
					int res = 0;
					if (high!=-1 && t.getBid()>=high){
						//entro en bid con sell						
						entry = t.getBid();
						mode = -1;
						res = TestMaxMins.testTickSL(ticks,i+1,entry, tp, sl,mode);
					}
					if (low!=-1 && t.getAsk()<=low){
						//entro en ask con buy
						entry = t.getAsk();
						mode = 1;
						res = TestMaxMins.testTickSL(ticks,i+1,entry, tp, sl,mode);
					}
					if (res==1){
						wins++;
						trades++;
					}else if (res==-1){
						losses++;
						trades++;
					}
				}
			}
			
									
			if (high==-1 || t.getBid()>=high){
				high = t.getBid();
				hmax = h;
			}
			if (low==-1 || t.getAsk()<=low){
				low = t.getAsk();
				hmin = h;
			}
		}
		
		
		if (trades>0){
			double winPer = wins*100.0/trades;
			double pf = (wins*tp*1.0)/(losses*sl);
			double avg = (wins*tp-losses*sl)*1.0/trades;
			
			ts.setWins(ts.getWins()+wins);
			ts.setLosses(ts.getLosses()+losses);
			ts.setWinPips(ts.getWinPips()+tp*wins*10);
			ts.setLostPips(ts.getLostPips()+sl*losses*10);
			
			System.out.println(
					
					h1+" "+h2+" "+tp+" "+sl
					+" || "
					+" "+days
					+" "+trades
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" ||| "+ts.getReport("")
					);
		}else{
			System.out.println(
					
					h1+" "+h2+" "+tp+" "+sl
					+" || "
					+"------ "
					);
			
		}
		
		
	}

	

	public static void main(String[] args) {
		String path = "c:\\fxdata\\";
		
		String pathEURUSD_100 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.12.31_2010.02.28.csv";
		String pathEURUSD_101 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.02.28_2010.04.30.csv";
		String pathEURUSD_102 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.04.30_2010.06.30.csv";
		String pathEURUSD_103 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.06.30_2010.08.31.csv";
		String pathEURUSD_104 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.08.31_2010.10.31.csv";
		
		String pathEURUSD_110 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.12.31_2011.02.28.csv";
		String pathEURUSD_111 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.02.28_2011.04.30.csv";
		String pathEURUSD_112 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.04.30_2011.06.30.csv";
		String pathEURUSD_113 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.06.30_2011.08.31.csv";
		String pathEURUSD_114 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.08.31_2011.10.31.csv";
		String pathEURUSD_115 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.10.31_2011.12.31.csv";
		
		String pathEURUSD_120 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.12.31_2012.02.29.csv";
		String pathEURUSD_121 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.02.29_2012.04.30.csv";
		String pathEURUSD_122 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.04.30_2012.06.30.csv";
		String pathEURUSD_123 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.06.30_2012.08.31.csv";
		String pathEURUSD_124 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.08.31_2012.10.31.csv";
		String pathEURUSD_125 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.10.31_2012.12.31.csv";
		
		String pathEURUSD_0 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.12.31_2013.02.28.csv";
		String pathEURUSD_1 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.02.28_2013.04.30.csv";
		String pathEURUSD_2 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.04.30_2013.06.30.csv";
		String pathEURUSD_3 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.06.30_2013.08.31.csv";
		String pathEURUSD_4 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.08.31_2013.11.30.csv";
		String pathEURUSD_5 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.09.30_2013.12.31.csv";
		
		String pathEURUSD1 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.12.31_2014.03.31.csv";
		String pathEURUSD2 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.03.31_2014.06.30.csv";
		String pathEURUSD3 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.06.30_2014.09.30.csv";
		String pathEURUSD4 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.09.30_2014.11.30.csv";
		String pathEURUSD5 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.12.31_2015.02.28.csv";
		String pathEURUSD6 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.02.28_2015.04.30.csv";
		String pathEURUSD7 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.04.30_2015.06.30.csv";
		String pathEURUSD8 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.06.30_2015.08.31.csv";
		String pathEURUSD9 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.08.31_2015.10.31.csv";
		String pathEURUSD10 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.10.31_2015.12.31.csv";
		String pathEURUSD11 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.12.31_2016.02.29.csv";
		String pathEURUSD12 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.02.29_2016.04.30.csv";
		
		String pathEURUSD13 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.04.30_2016.06.30.csv";
		String pathEURUSD14 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.06.30_2016.08.31.csv";
		String pathEURUSD15 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.08.31_2016.10.31.csv";
		String pathEURUSD16 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.12.31_2017.02.28.csv";
		String pathEURUSD17 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2017.02.28_2017.04.30.csv";		
		String pathEURUSD18 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2017.04.30_2017.06.30.csv";
		
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD_100);
		paths.add(pathEURUSD_101);
		paths.add(pathEURUSD_102);
		paths.add(pathEURUSD_103);
		paths.add(pathEURUSD_104);
		paths.add(pathEURUSD_110);
		paths.add(pathEURUSD_111);
		paths.add(pathEURUSD_112);
		paths.add(pathEURUSD_113);
		paths.add(pathEURUSD_114);
		paths.add(pathEURUSD_115);
		paths.add(pathEURUSD_120);
		paths.add(pathEURUSD_121);
		paths.add(pathEURUSD_122);
		paths.add(pathEURUSD_123);
		paths.add(pathEURUSD_124);
		paths.add(pathEURUSD_125);
		paths.add(pathEURUSD_0);
		paths.add(pathEURUSD_1);
		paths.add(pathEURUSD_2);
		paths.add(pathEURUSD_3);
		paths.add(pathEURUSD_4);
		paths.add(pathEURUSD_5);
		paths.add(pathEURUSD1);
		paths.add(pathEURUSD2);
		paths.add(pathEURUSD3);
		paths.add(pathEURUSD4);
		paths.add(pathEURUSD5);
		paths.add(pathEURUSD6);
		paths.add(pathEURUSD7);
		paths.add(pathEURUSD8);
		paths.add(pathEURUSD9);
		paths.add(pathEURUSD10);
		paths.add(pathEURUSD11);
		paths.add(pathEURUSD12);
		paths.add(pathEURUSD13);
		paths.add(pathEURUSD14);
		paths.add(pathEURUSD15);
		paths.add(pathEURUSD16);
		paths.add(pathEURUSD17);
		paths.add(pathEURUSD18);
		
		
		
		TickStats ts = new TickStats();
		
		for (int h=16;h<=23;h++){
			ts.setLosses(0);
			ts.setWinPips(0);
			ts.setWins(0);
			ts.setLostPips(0);
			for (int p=0;p<=paths.size()-1;p++){
				String pathc = paths.get(p);
				for (int m1=0;m1<=0;m1+=4){
					int m2=14;
					ticks.clear();
					Tick.readFromDisk(ticks,pathc, 4,m1,m2);
					//System.out.println("ticks: "+ticks.size());
					
					for (int h1=h;h1<=h;h1++){
						int h2 = h1+h;
						for (int tp=20;tp<=20;tp++){
							for (int sl=tp*6;sl<=tp*6;sl+=1*tp){
								TestMaxMins.testMaxMins(ticks, h1, h2, tp, sl,ts);
							}
						}
					}
				}
			}
		}
		
		
		/*for (int year=2008;year<=2016;year++){
			String symbol = "eurusd";
			String yearPath = "_UTC_Ticks_Bid_"+String.valueOf(year)+".csv";
			String pathcomplete = path+symbol+yearPath;
			
			ticks.clear();
			Tick.readFromDisk(ticks,pathcomplete, 2);
			System.out.println("ticks: "+ticks.size());
			for (int h=0;h<=1;h++){
				TestTicks.decodeTickDNA(ticks,h);
			}
		}*/

	}

}
