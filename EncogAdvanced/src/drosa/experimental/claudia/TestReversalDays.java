package drosa.experimental.claudia;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestReversalDays {

	public static int getAvgPrice(int ref,int distance,int npos,int mode){
		
		int avg = -1;
		
		int first = ref+mode*distance*10;//primer elemento
		
		if (npos==1) return first;
		
		if (npos%2==0){//par
			int p1 = npos/2+0-1;//posicion avg
			int p2 = npos/2+1-1;//posicion avg
			double avg1 = first+mode*p1*distance*10;
			double avg2 = first+mode*p2*distance*10;
			
			avg = (int) ((avg1+avg2)/2);
		}else{//impar
			int p = npos/2+1-1;//posicion avg
			avg = first+mode*p*distance*10;
		}
		
		return avg;
	}
	
	public static void doTest(ArrayList<QuoteShort> data,int h1,int h2,int npositions,int nbars){
		
		int lastDay = -1;
		int max = -1;
		int min = -1;
		ArrayList<Double> diffs = new ArrayList<Double>();
		Calendar cal = Calendar.getInstance();
		int tradeMode = 0;
		for (int i=0;i<data.size()-npositions-nbars;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				max = -1;
				min = -1;
				tradeMode = 0;
				lastDay = day;
			}
			
			
			if (tradeMode!=2){
				if (q.getHigh5()>=max){				
					max = q.getHigh5();					
					if (h1<=h && h<=h2 && tradeMode==0){
						tradeMode = 1;
					}
				}				
				if (q.getLow5()>=max){				
					max = q.getHigh5();					
					if (h1<=h && h<=h2 && tradeMode==0){
						tradeMode = -1;
					}
				}
				
				if (tradeMode==1){
					for (int p=0;p<npositions;p++){
						QuoteShort qp = data.get(i+p+1);
						QuoteShort qpn = data.get(i+p+1+nbars);
						int diff = qpn.getClose5()-qp.getOpen5();
						diffs.add((double) diff);
					}
					tradeMode = 2;
				}else if (tradeMode==-1){
					for (int p=0;p<npositions;p++){
						QuoteShort qp = data.get(i+p+1);
						QuoteShort qpn = data.get(i+p+1+nbars);
						int diff = qp.getOpen5()-qpn.getClose5();
						diffs.add((double) diff);
					}
					tradeMode = 2;
				}
			}
		}
		MathUtils.summary_mean_sd("Reversals "+h1+" "+h2+" "+npositions+" "+nbars, diffs);
	}
	
public static void doTest2(ArrayList<QuoteShort> data,int h1,int h2){
		
		int lastDay = -1;
		int max = -1;
		int min = -1;
		ArrayList<Double> diffs = new ArrayList<Double>();
		Calendar cal = Calendar.getInstance();
		int tradeMode = 0;
		int tradeRef = 0;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay!=-1){
					if (tradeMode==1){
						int diff = tradeRef-max;
						diffs.add((double) diff);
					}else if (tradeMode==-1){
						int diff = min-tradeRef;
						diffs.add((double) diff);
					}
				}
				
				max = -1;
				min = -1;
				tradeMode = 0;
				lastDay = day;
			}
			
			
			if (tradeMode==0){
				if (q.getHigh5()>=max && max !=-1){													
					if (h1<=h && h<=h2 
							//&& max-min>=500
							&& tradeMode==0){
						tradeRef = max;
						tradeMode = 1;
					}
				}				
				if (q.getLow5()<=min && min !=-1){									
					if (h1<=h && h<=h2 
							//&& max-min>=500
							&& tradeMode==0){
						tradeRef = min;
						tradeMode = -1;
					}
				}
			}	
			
			if (q.getHigh5()>=max || max ==-1){				
				max = q.getHigh5();	
			}
			if (q.getLow5()<=min || min==-1){				
				min = q.getLow5();
			}
		}
		MathUtils.summary_mean_sd("Reversals "+h1+" "+h2, diffs);
	}

 public static void doTrade(ArrayList<QuoteShort> data,int h1,int h2,int distance,int target){
	
	int lastDay = -1;
	int max = -1;
	int min = -1;
	ArrayList<Double> diffs = new ArrayList<Double>();
	Calendar cal = Calendar.getInstance();
	int tradeMode = 0;
	int tradeRef = 0;
	int actualPos = 0;
	for (int i=0;i<data.size()-1;i++){
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		if (day!=lastDay){
			
			max = -1;
			min = -1;
			tradeMode = 0;
			actualPos = 0;
			lastDay = day;
		}
		
		
		if (tradeMode==0){
			if (q.getHigh5()>=max && max !=-1){													
				if (h1<=h && h<=h2 
						//&& max-min>=500
						&& tradeMode==0){
					tradeRef = max;
					tradeMode = 1;
				}
			}				
			if (q.getLow5()<=min && min !=-1){									
				if (h1<=h && h<=h2 
						//&& max-min>=500
						&& tradeMode==0){
					tradeRef = min;
					tradeMode = -1;
				}
			}
		}else{
			if (tradeMode==1){ //busco buys,gano en largo 20
				//comprobamos si llegamos al target
				double avgPrice = 
				//actualizamos posiciones
				int diffPips = tradeRef-q.getLow5();
				int npos = diffPips/distance; //total posiciones abiertas
				if (npos>=actualPos) actualPos = npos;
			}else if (tradeMode==-1){//busco sells,gano en sell 20
				//comprobamos si llegamos al target
				
				//actualizamos posiciones
				int diffPips = q.getHigh5()-tradeRef;
				int npos = diffPips/distance; //total posiciones abiertas
				if (npos>=actualPos) actualPos = npos;
			}
		}
		
		if (q.getHigh5()>=max || max ==-1){				
			max = q.getHigh5();	
		}
		if (q.getLow5()<=min || min==-1){				
			min = q.getLow5();
		}
	}
	MathUtils.summary_mean_sd("Reversals "+h1+" "+h2, diffs);
}
 
 public static void doTradeNoPulls(ArrayList<QuoteShort> data,
		 ArrayList<QuoteShort> maxMins,
		 int h1,int h2,int thr,int nbars){
		
		int lastDay = -1;
		int max = -1;
		int min = -1;
		ArrayList<Double> diffs = new ArrayList<Double>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int totalPips = 0;
		int totalTrades = 0;
		int wins = 0;
		QuoteShort qm = new QuoteShort();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){				
				max = -1;
				min = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i).getExtra();
			
			if (h1<=h && h<=h2){
				if (maxMin>=thr){
					TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					int diffH = qm.getHigh5()-q1.getOpen5();
					int diffL = q1.getOpen5()-qm.getLow5();
					totalPips += (diffH-diffL);
					if (diffH>=diffL) wins++;
					totalTrades++;
				}else if (maxMin<=-thr){
					TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					int diffH = qm.getHigh5()-q1.getOpen5();
					int diffL = q1.getOpen5()-qm.getLow5();
					totalPips += (diffL-diffH);
					if (diffL>=diffH) wins++;
					totalTrades++;
				}
			}		
		}
		
		double avg = totalPips*0.1/totalTrades;
		double winPer = wins*100.0/totalTrades;
		System.out.println(
				h1+" "+h2
				+" "+thr+" "+nbars
				+" || "
				+" "+totalTrades
				+" "+PrintUtils.Print2(avg, false)
				+" "+PrintUtils.Print2(winPer, false)
				);
	}
 
 public static void doTradePulls(ArrayList<QuoteShort> data,
		 ArrayList<QuoteShort> maxMins,
		 int y1,int y2,
		 int h1,int h2,int thr,int nbars,int minPull){
		
		int lastDay = -1;
		int max = -1;
		int min = -1;
		ArrayList<Double> diffs = new ArrayList<Double>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int totalPips = 0;
		int totalTrades = 0;
		int wins = 0;
		int tradeRef = 0;
		int tradeMode = 0;
		QuoteShort qm = new QuoteShort();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (year<y1 || year>y2) continue;
			
			if (day!=lastDay){	
				tradeRef = -1;
				tradeMode = 0;
				max = -1;
				min = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i).getExtra();
			
			//actualizamos puntos de referencia
			if (h1<=h && h<=h2){				
				if (maxMin>=thr){
					tradeRef = q.getHigh5();
					tradeMode=1;
				}else if (maxMin<=-thr){
					tradeRef = q.getLow5();
					tradeMode=-1;
				}
			}	
			
			//vemos si hay pull
			if (tradeMode==1){ //long
				int diff = tradeRef - q1.getOpen5();
				if (diff>=minPull*10){
					TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					int diffH = qm.getHigh5()-q1.getOpen5();
					int diffL = q1.getOpen5()-qm.getLow5();
					totalPips += (diffH-diffL);
					if (diffH>=diffL) wins++;
					totalTrades++;
				}				
			}else if (tradeMode==-1){//short
				int diff = q1.getOpen5()-tradeRef;
				if (diff>=minPull*10){
					TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					int diffH = qm.getHigh5()-q1.getOpen5();
					int diffL = q1.getOpen5()-qm.getLow5();
					totalPips += (diffL-diffH);
					if (diffL>=diffH) wins++;
					totalTrades++;
				}	
			}			
		}
		
		double avg = totalPips*0.1/totalTrades;
		double winPer = wins*100.0/totalTrades;
		System.out.println(
				h1+" "+h2
				+" "+thr
				+" "+nbars
				+" "+minPull
				+" || "
				+" "+totalTrades
				+" "+PrintUtils.Print2(avg, false)
				+" "+PrintUtils.Print2(winPer, false)
				);
	}
 
 public static void doTradePullsClose(ArrayList<QuoteShort> data,
		 ArrayList<QuoteShort> maxMins,
		 int y1,int y2,
		 int h1,int h2,int thr,int nbars,
		 int tp,
		 int minPull,
		 int maxTrades,
		 int breakDirection
		 ){
		
	 	int winPips = 0;
	 	int lostPips = 0;
		int lastDay = -1;
		int max = -1;
		int min = -1;
		ArrayList<Double> diffs = new ArrayList<Double>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int totalPips = 0;
		int totalAdverse = 0;
		int totalTrades = 0;
		int wins = 0;
		int tradeRef = 0;
		int tradeMode = 0;
		int dayTrades = 0;
		QuoteShort qm = new QuoteShort();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (year<y1 || year>y2) continue;
			
			if (day!=lastDay){	
				tradeRef = -1;
				tradeMode = 0;
				max = -1;
				min = -1;
				dayTrades = 0;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i).getExtra();
			
			//actualizamos puntos de referencia
			if (h1<=h && h<=h2 && tradeMode<2){				
				if (maxMin>=thr){
					tradeRef = q.getHigh5();
					tradeMode=1;
				}else if (maxMin<=-thr){
					tradeRef = q.getLow5();
					tradeMode=-1;
				}
			}	
			
			//vemos si hay pull
			if (tradeMode==1){ //long
				int diff = tradeRef - q1.getOpen5();
				if (diff>=minPull*10){
					int valueTP = q1.getOpen5()+breakDirection*tp*10;
					//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					TradingUtils.getMaxMinShortTP(data, qm, cal1, i+1, i+nbars,valueTP);
					int maxL =q1.getOpen5()-qm.getLow5();
					int diffH = qm.getClose5()-q1.getOpen5();
					if (breakDirection==-1){
						maxL = qm.getHigh5()-q1.getOpen5();
						diffH = q1.getOpen5()-qm.getClose5();
					}
					//int diffL = q1.getOpen5()-qm.getClose5();
					totalPips += (diffH);
					totalAdverse += maxL;
					int win = -1;
					if (diffH>=0){
						win = 1;
						wins++;
						winPips+=diffH;
					}else{
						lostPips+=-diffH;
					}
					totalTrades++;
					dayTrades++;
					if (dayTrades>=maxTrades) tradeMode=2;
					//System.out.println("[LONG] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffH+" "+maxL+" || "+win);
				}				
			}else if (tradeMode==-1){//short
				int diff = q1.getOpen5()-tradeRef;
				if (diff>=minPull*10){
					int valueTP = q1.getOpen5()-breakDirection*tp*10;
					//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					TradingUtils.getMaxMinShortTP(data, qm, cal1, i+1, i+nbars,valueTP);
					int maxH = qm.getHigh5()-q1.getOpen5();
					int diffL = q1.getOpen5()-qm.getClose5();
					if (breakDirection==-1){
						maxH = q1.getOpen5()-qm.getLow5();
						diffL = qm.getClose5()-q1.getOpen5();
					}
					totalPips += (diffL);
					totalAdverse += maxH;
					int win = -1;
					if (diffL>=0){
						win = 1;
						wins++;
						winPips += diffL;
					}else{
						lostPips += -diffL;
					}
					totalTrades++;
					dayTrades++;
					if (dayTrades>=maxTrades) tradeMode=2;
					
					//System.out.println("[SHORT] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffL+" || "+win);
				}	
			}			
		}
		
		double avg = totalPips*0.1/totalTrades;
		double avgAdverse = totalAdverse*0.1/totalTrades;
		double winPer = wins*100.0/totalTrades;
		double pf = winPips*1.0/lostPips;
				
		System.out.println(
				h1+" "+h2
				+" "+thr
				+" "+nbars
				+" "+minPull
				+" "+tp
				+" || "
				+" "+totalTrades
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgAdverse, false)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void main(String[] args) throws Exception {
		
		//ArrayList<QuoteShort> data = DAO.getData(fileName,"5m");
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2016_01_04_GAPS.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.11.25.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2015_10_28_2015_01_04.csv";
		
		//String pathEURUSD = "C:\\fxdata\\gbpjpy_UTC_5 Mins_Bid_2008.12.31_2015.10.11.csv";
		
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.12.31_2015.10.06.csv";		
		
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		paths.add(pathEURJPY);
		paths.add(pathGBPJPY);
		
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= null;
		ArrayList<Quote> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="dukasc";
			}				
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+7;
				for (int h1=0;h1<=0;h1++){
					int h2 = 2;				
					for (int thr = 500;thr<=500;thr+=100){
						for (int nbars=24;nbars<=24;nbars+=12){
							//TestReversalDays.doTradeNoPulls(data, maxMins, h1, h2, thr, nbars);
							for (int minPull=0;minPull<=0;minPull+=1){
								for (int maxtrades=1;maxtrades<=20;maxtrades++){
									for (int tp=20;tp<=20;tp++){
										TestReversalDays.doTradePullsClose(data, maxMins,y1,y2, h1, h2, thr, nbars,tp,minPull,maxtrades,-1);
									}
								}
							}
						}
					}
				}
			}
			
		}
	
	}

}
