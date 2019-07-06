package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.StrategyResultEx;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MomentumStudySystem {
	
	public static void testRangesProbPips$$(
			String header,
			ArrayList<QuoteShort>  data,
			ArrayList<FFNewsClass> news,
			int y1,int y2,
			int m1,int m2,
			int d1,int d2,
			int h1,int h2,
			int h3,int h4,
			ArrayList<Integer> maxMins,
			int thr1,
			int thr2,
			int thr3,
			int bars,
			int tp, 
			int sl,
			double risk,
			int maxPositions,
			int comm,
			int debug,
			boolean print,
			StrategyResultEx stats
			){
		
		double balance = 10000;
		double maxBalance = balance;
		double actualBalance = balance;
		double maxDD = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		

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
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int acc = 0;
		int count = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		int totalGreater = 0;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		double mean = 50;
		double dt=5;
		double ref0 = 45;
		double ref1 = 55;
		double ref2 = 60;
		double ref3 = 65;
		double ref4 = 70;
		double ref5 = 75;
		
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (dayWeek<d1 || dayWeek>d2 ) continue;
			if (day!=lastDay){
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (h<h1 || h> h2){
				mode=0;
			}
			
			if (h>=h1 && h<=h2){
				if (maxMin>=thr1){
					mode = 1;
					//System.out.println("[MODE LONG]" +maxMin+" "+DateUtils.datePrint(cal));
				}else if (maxMin<=-thr1) {
					mode = -1;
				}
			}else{
				/*if (maxMin>=thr3
						&& mode==-1
						){
					mode = 0;
					//System.out.println("[MODE LONG]" +maxMin+" "+DateUtils.datePrint(cal));
				}else if (maxMin<=-thr3
						&& mode==1
						) {
					mode = 0;
				}*/
			}
			
			if (true
					//&& h>=h3 && h<=h4
					){//prueba, ventana de trading
				if (mode ==1) {
					if (maxMin<=-thr2){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setTp(q.getOpen5() + 10 *tp);
						pos.setSl(q.getOpen5() - 10 *sl);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setPositionType(PositionType.LONG);
						pos.setOpenIndex(i);
						
						double riskPosition = (actualBalance*risk)/100.0;
						double riskPip = riskPosition/sl;
						int microLots = (int) (riskPip/0.1);//0.10$/pip
						
						/*System.out.println(
								PrintUtils.Print2dec(actualBalance, false,3)
								+" "+PrintUtils.Print2dec(riskPip, false,3)
								+" "+PrintUtils.Print2dec(microLots, false,3)
								);
						*/
						pos.setMicroLots(microLots);
						positions.add(pos);
						//System.out.println("[open long]" +maxMin+" "+DateUtils.datePrint(cal));
					}
				}else if (mode==-1) {
					if (maxMin>=thr2){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setTp(q.getOpen5() - 10 *tp);
						pos.setSl(q.getOpen5() + 10 *sl);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setPositionType(PositionType.SHORT);
						pos.setOpenIndex(i);
						
						double riskPosition = (actualBalance*risk)/100.0;
						double riskPip = riskPosition/sl;
						int microLots = (int) (riskPip/0.1);//0.10$/pip
						
						//ej riskPosition=1000 sl = 20 pips riskPip = 50 microlots = 500
						
						pos.setMicroLots(microLots);
						positions.add(pos);
					}
				}
			}
			//}//h1 //h2
			
			int j=0;
			while (j<positions.size()) {
				PositionShort p = positions.get(j);
				boolean isClosed = false;
				int pips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					if (p.getPositionType()==PositionType.LONG) {
					
						if (q.getLow5()<=p.getSl()) {
							isClosed = true;
							pips -= sl*10;
						}else if (q.getHigh5()>=p.getTp()) {
							isClosed = true;
							pips = tp*10;
						}else if (i>=p.getOpenIndex()+bars) {
							isClosed = true;
							pips = q.getClose5()-p.getEntry();
						}
					}else if (p.getPositionType()==PositionType.SHORT) {
						if (q.getHigh5()>=p.getSl()) {
							isClosed = true;
							pips -= sl*10;
						}else if (q.getLow5()<=p.getTp()) {
							isClosed = true;
							pips = tp*10;
						}else if (i>=p.getOpenIndex()+bars) {
							isClosed = true;
							pips = p.getEntry()-q.getClose5();
						}
					}
				}
				
				if (isClosed) {
					pips -= comm;
					
					actualBalance += (p.getMicroLots()*0.1)*(pips*0.1);
					if (actualBalance>=maxBalance) maxBalance = actualBalance;
					double dd = 100.0-actualBalance*100.0/maxBalance;
					if (dd>=maxDD) maxDD = dd;
					
					/*System.out.println(
							" "+PrintUtils.Print2dec(pips, false,3)
							+" "+p.getMicroLots()
							+" "+PrintUtils.Print2dec(actualBalance, false,3)
							);*/
					
					if (pips>=0) {
						wins++;
						winPips += pips;
					}else {
						losses++;
						lostPips += -pips;
					}
					
					positions.remove(j);
				}else {
					j++;
				}
			}
		}
		
		double pf = winPips*1.0/(lostPips);
		int total = wins+losses;
		double winPer = wins*100.0/total;
		if (print 
			//&& ((pf>=1.60 && total>=1000) || (total>=2000 && pf>=1.5))
				){
			System.out.println(header+" "+" || "+
					h1+" "+h2+" "+h3+" "+h4
					+" || "+thr1+" "+thr2+" "+thr3
					+" || "+tp+" "+sl+" "+bars
					+" || "
					+" "+total
					+" "+PrintUtils.Print2dec(winPer, false,3)
					+" "+PrintUtils.Print2dec2(actualBalance, true)	
					+" "+PrintUtils.Print2dec(maxDD, false,3)
					+" "+PrintUtils.Print2dec(pf, false,3)
					);
		}
		
		stats.setProfitFactor(pf);
		stats.setExpectancy(acc*0.1/count);
		stats.setTotalTrades(count);
		
		/*double winPer = wins*100.0/count;
		System.out.println(
				h1+" "+h2+" "+thr+" "+tp+" "+sl+" "+bars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false,3)	
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false,3)
				+" "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false,3)
				);*/
	}

	public static void main(String[] args) throws Exception {
		//String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
		String path0 ="C:\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
		String pathNews = path0+"News.csv";
		
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
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		FFNewsClass.readNews(pathNews,news,0);
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			dataNoise = data;
			
			//USDJPY 160 48 48 10 90 264 // 160 60 60 10 90 264
			
			String header = "";
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataNoise);
			StrategyResultEx stats = new StrategyResultEx();
			for (int thr1=120;thr1<=120;thr1+=100) {
				for (int thr2=48;thr2<=48;thr2+=12) {
					for (int thr3=0;thr3<=0;thr3+=12){ 
						for (int bars=120;bars<=120;bars+=12) {
							for (int tp=10;tp<=10;tp+=1) {
								for (int sl=1*tp;sl<=30*tp;sl+=1.0*tp) {
									for (int h1=16;h1<=16;h1++) {
										int h2 = h1+7;
										for (double risk=1.0;risk<=1.0;risk+=0.5){
											for (int y1=2004;y1<=2004;y1++){
												int y2 = y1+13;
												MomentumStudySystem.testRangesProbPips$$("", 
														dataNoise,
														news,
														y1, y2, 
														0, 11, 
														Calendar.MONDAY,Calendar.MONDAY+4,
														h1, h2,0,23, 
														maxMins, 
														thr1,thr2,thr3,
														bars,tp,sl,
														risk,100,
														20, 0,true,stats
												);
											}//y
										}//risk
									}//h1
								}//sl
							}//tp
						}//bars
					}//thr3
				}
			}
		}//limit

	}

}
