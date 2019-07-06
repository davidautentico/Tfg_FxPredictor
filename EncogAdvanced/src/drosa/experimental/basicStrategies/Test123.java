package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Test123 {
	
	public static void doTest(String header,ArrayList<QuoteShort> data,
				ArrayList<Integer> maxMins,
				int y1,int y2,
				int h1,int h2,
				int maxPipsDiff,
				int tp,
				boolean printAlways,
				int modeTest,
				int debug) {
			

			Calendar cal = Calendar.getInstance();
			int lastDay = -1;
			QuoteShort qm = new QuoteShort();
		
			
			int trades = 0;
			int wins=0;
			int losses=0;
			int winsFloating=0;
			int lossesFloating=0;
			int others=0;
			int accOthers=0;
			int accref=0;
			int accSize=0;
			int accLosses = 0;
			int accWins = 0;
			boolean canContinue = false;
			boolean isTested = false;
			int actualLosses = 0;
			int maxLosses = 0;
			int winPips = 0;
			int lostPips = 0;

			int winsFloatingPips = 0;
			int lostFloatingPips = 0;
			int high = -1;
			int low = -1;
			int totalDays = 0;
			double avgRange = 600.0;
			int tpf = 200;
			int slf = 200;
			int comm = 0;
			ArrayList<Integer> ranges = new ArrayList<Integer>();
			
			ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
			
			int winsYear = 0;
			int lossYear = 0;
			int tradesYear = 0;
			int countYears = 0;
			int lastYear = -1;
			int hl = 0;
			int hHigh = 0;
			int hLow = 0;
			int hActualOpen = 0;
			int highShortState = 0;
			int hOpen = 0;
			int entryS = 0;
			int entryL = 0;
			int highTested = 0;
			int lowLongState = 0;
			int lowTested = 0;
			int count = 0;
			wins = 0;
			int accDiff = 0;
			for (int i=240;i<data.size();i++) {
				QuoteShort q = data.get(i);
				QuoteShort q1 = data.get(i-1);
				QuoteShort.getCalendar(cal, q);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int y = cal.get(Calendar.YEAR);
				int min= cal.get(Calendar.MINUTE);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				
				
				if (y!=lastYear){
					if (lastYear!=-1){
						double pfY = winsYear*1.0/lossYear;
						double avgy = (winsYear-lossYear)*0.1/tradesYear;
						if (pfY>=1.0 
								//&& avgy>=2.0
								) countYears++;
					}
					winsYear = 0;
					lossYear = 0;
					tradesYear = 0;
					high = -1;
					low = -1;
					lastYear = y;
					hl=0;
				}
				
				if (y<y1 || y>y2) continue;
				
				if (day!=lastDay
						&& min==15
						) {	
					
					if (lastDay!=-1){
						int dayRange = high-low;
						totalDays++;
						ranges.add(dayRange);
						avgRange = MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					}
					
					high = -1;
					low = -1;
					hHigh = 0;
					hLow = 0;
					lastDay = day;
					isTested = false;
					
					if (debug==1){
						System.out.println("[***NEW DAY***] "+DateUtils.datePrint(cal)
							+" || "+wins+" "+losses
							);
					}
					
					
					if (highShortState==2) {
						int pips = entryS-q.getOpen5();
						if (pips>=0) {
							wins++;
							winPips += pips;
							
							//System.out.println("HIGH end day] "+DateUtils.datePrint(cal)
							//+" "+pips+" || "+wins+" "+losses
							//+" || "+q.toString()
							//);
						}else {
							losses++;
							lostPips +=-pips;
							
							//System.out.println("HIGH end day] "+DateUtils.datePrint(cal)
							//+" "+pips+" || "+wins+" "+losses
							//+" || "+q.toString()
							//);
						}
					}
					if (lowLongState==2) {
						int pips = q.getOpen5()-entryL;
						if (pips>=0) {
							wins++;
							winPips += pips;
						}else {
							losses++;
							lostPips +=-pips;
						}
					}
					
					highShortState = 0;
					lowLongState = 0;
				}//day
				
				if (min==0){
					hOpen = q.getOpen5();
				}
				
				
				if (high==-1 || q.getHigh5()>high){
					
					if (highShortState == 2) {
						losses++;
						lostPips += (high+1)-entryS;
					}
					
					high = q.getHigh5();
					hHigh = h;
					highShortState = 0;
					highTested = q.getOpen5();
					if (debug==2){
						System.out.println("[NEW HIGH] "+DateUtils.datePrint(cal)
							+" "+high
							+" || "+q.toString()
							);
					}
				}
				
				if (low==-1 || q.getLow5()<low){
					
					if (lowLongState == 2) {
						losses++;
						lostPips += entryL-(low-1);
					}
					low = q.getLow5();
					hLow = h;
					lowLongState= 0;
					lowTested = q.getOpen5();
				}
				
				if (highShortState == 0 && modeTest>=0
						&& hHigh>=h1 && hHigh<=h2
						) {
					//vemos si se activa la nueva busqueda de posible entrada
					if (h-hHigh==1){//la anterior fue maximo del dia
						hActualOpen = hOpen;
						int diff = high-q.getHigh5();
						int diffOpen = q.getHigh5() - hOpen;
						if (diff>=0
								&& diffOpen>=maxPipsDiff
								){
							highShortState = 1;
							highTested = q.getHigh5();
							if (debug==1){
								System.out.println("[HIGH TESTED] "+DateUtils.datePrint(cal)
									+" "+high
									+" || "+q.toString()
									);
							}
						}
					}
				}
				
				if (highShortState == 1) {
					if (q.getLow5()<=hActualOpen){						
						int diffOpen = highTested - hActualOpen;
						if (true){//QUE HAYA MOVIMIENTO HACIA ARRIBA...
							entryS = hActualOpen;
							highShortState = 2;
							if (debug==1){
								System.out.println("[SHORT OPEN] "+DateUtils.datePrint(cal)
									+" "+q.getOpen5()
									+" || "+q.toString()
									);
							}
							count++;
							accDiff += high-entryS+1;
						}
					}
				}
				
				if (highShortState == 2) {
					int pips = entryS-q.getLow5();
					int win=0;
					String str="";
					if (pips>=tp){
						winPips+=tp;
						win=1;
						str = "WIN";
						highShortState =3; 
						wins++;
						if (debug==1){
							System.out.println("[SHORT PIPS "+str+" ] "+DateUtils.datePrint(cal)
								+" "+pips
								+" || "+q.toString()
								);
						}
					}
					if (debug==2){
						System.out.println("[SHORT PIPS "+str+" ] "+DateUtils.datePrint(cal)
							+" "+pips
							+" || "+q.toString()
							);
					}
				}

				//shorts
				if (lowLongState == 0 && modeTest<=0
						&& hLow>=h1 && hLow<=h2
						) {
					//vemos si se activa la nueva busqueda de posible entrada
					if (h-hLow==1){//la anterior fue maximo del dia
						hActualOpen = hOpen;
						int diff = q.getLow5()-low;//high-q.getHigh5();
						int diffOpen = hOpen-q.getLow5();//q.getHigh5() - hOpen;
						if (diff>=0
								&& diffOpen>=maxPipsDiff
								){
							lowLongState = 1;
							lowTested = q.getLow5();
							if (debug==1){
								System.out.println("[LOW TESTED] "+DateUtils.datePrint(cal)
									+" "+low
									+" || "+q.toString()
									);
							}
						}
					}
				}
				
				if (lowLongState == 1) {
					if (q.getHigh5()>=hActualOpen){				
						if (true){//QUE HAYA MOVIMIENTO HACIA ARRIBA...
							entryL = hActualOpen;
							lowLongState = 2;
							if (debug==1){
								System.out.println("[LONG OPEN] "+DateUtils.datePrint(cal)
									+" "+q.getOpen5()
									+" || "+q.toString()
									);
							}
							count++;
							accDiff += entryL-low+1;
						}
					}
				}
				
				if (lowLongState == 2) {
					int pips = q.getHigh5()-entryL;
					int win=0;
					String str="";
					if (pips>=tp){
						winPips+=tp;
						win=1;
						str = "WIN";
						lowLongState =3; 
						wins++;
						if (debug==1){
							System.out.println("[LONG PIPS "+str+" ] "+DateUtils.datePrint(cal)
								+" "+pips
								+" || "+q.toString()
								);
						}
					}
					if (debug==2){
						System.out.println("[long PIPS "+str+" ] "+DateUtils.datePrint(cal)
							+" "+pips
							+" || "+q.toString()
							);
					}
				}

			}//data
			
			losses = count-wins;
			int total = wins+losses;
			double winPer = wins*100.0/total;
			double reversePF = lostPips*1.0/winPips;
			System.out.println(
					header+"  "+tp
					+" || "+total+" "+wins+" "+losses+" | "+winPips+" "+lostPips
					+" "+PrintUtils.Print2dec(winPer, false)
					+" || "+PrintUtils.Print2dec(reversePF, false)
					);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
				String path0 ="C:\\fxdata\\";
				//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
				String pathEURUSD = path0+"eurusd_UTC_15 Secs_Bid_2016.12.31_2018.01.25.csv";
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
				//FFNewsClass.readNews(pathNews,news,0);
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
				
					ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
					
					System.out.println(data.size()+" "+maxMins.size());
					
					int h1 = 16;
					int h2 = 23;
					int maxPipsDiff = 50;
					
					for (int tp=30;tp<=500;tp+=10) {
						System.out.println("testing..."+tp);
						for (h1=0;h1<=0;h1++){
							h2 = h1+23;
							Test123.doTest("ALL",data, maxMins, 2017, 2018, h1, h2, maxPipsDiff,tp, true,0, 0);
						}
						//Test123.doTest("LOW",data, maxMins, 2017, 2018, h1, h2, maxPipsDiff,tp, true,-1, 0);
					}
				}
				

	}

}
