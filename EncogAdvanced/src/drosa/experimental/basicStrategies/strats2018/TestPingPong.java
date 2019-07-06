package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestPingPong {

	
	public static void doTest(ArrayList<QuoteShort> data, 
			int h1,int h2,int span,int debug){
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalDays = 0;
		int ref = -1;
		int upLimit = -1;
		int downLimit = -1;
		int upPP = 0;
		int downPP = 0;
		int buyTarget = -1;
		int sellTarget = -1;
		int actualMode = 0;
		int upTouches = 0;
		int downTouches = 0;
		int buys = 0;
		int sells = 0;
		int accDesv = 0;
		ArrayList<Integer> totalOrders = new ArrayList<Integer>();
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (day!=lastDay){
				
				lastDay = day;
			}
			
			if (actualMode==0){
				if (h==h1 && min==0){
					ref = q.getOpen5();
					upLimit = ref + 0*5*span;
					downLimit = ref - 0*5*span;
					actualMode = 0;
					upTouches=0;
					downTouches = 0;
					buys = 0;
					sells = 0;
				}
			}
			
			if (ref>=0){
				if (q.getHigh5()>=upLimit){
					if (actualMode<=0){
						actualMode=1;
						upTouches++;
						buys = sells +1;
						int actualLost = sells*span;
						int comm = buys*10;//se paga la mitad por que se cancelarian entre unos y otros
						buyTarget = upLimit + actualLost + span + comm;
					}
				}else if (q.getLow5()<=downLimit){
					if (actualMode>=0){
						actualMode=-1;
						downTouches++;
						sells = buys +1;
						int actualLost = buys*span;
						int comm = sells*10;//se paga la mitad por que se cancelarian entre unos y otros
						sellTarget = downLimit - actualLost - span + comm; 
					}
				}	
				
				if (actualMode==1){
					if (q.getHigh5()>=buyTarget){
						wins++;
						accDesv += (buyTarget-upLimit); 
						totalOrders.add(buys);
						actualMode = 0;
						ref = -1;
						if (debug==1)
						System.out.println("[TARGET BUY REACHED] BUYS SELLS "+buys+" "+sells+" | "+(buyTarget-upLimit)+" | "+q.toString());
					}
				}else if (actualMode==-1){
					if (q.getLow5()<=sellTarget){
						wins++;
						accDesv += (downLimit-sellTarget); 
						totalOrders.add(sells);
						actualMode = 0;
						ref = -1;
						if (debug==1)
						System.out.println("[TARGET SELL REACHED] BUYS SELLS "+buys+" "+sells+" | "+(downLimit-sellTarget)+" | "+q.toString());
					}
				}
			}
			
		}
		
		int count10 = 0;
		for (int i=0;i<totalOrders.size();i++){
			int value = totalOrders.get(i);
			if (value>=30){
				count10++;
			}
		}
		System.out.println(
				h1+" "+h2+" "+span
				+" "+wins+" "+PrintUtils.Print2dec(accDesv*1.0/(span*wins), false)
				+" || "+count10
				);
	}
	
	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurusd_5 Mins_Bid_2004.01.01_2018.11.30.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_1 Min_Bid_2009.01.01_2018.09.18.csv";
		
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
			
			for (int h1=0;h1<=23;h1++){
				for (int h2=h1;h2<=h1;h2++){
					for (int span=150;span<=150;span+=50){
						TestPingPong.doTest(data,h1,h2,span,0);
					}
				}
			}
		
		}

	}

}
