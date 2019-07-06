package drosa.experimental.swingtrade65;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestHourly {
	
	
	public static void doTest(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMin,
			int y1,int y2,
			int h1,int h2,
			int thr,
			double atrTp,double atrSL,
			int period,int maxTrades,int offset,int moveBE){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int dayTrades = 0;
		int DO = -1;
		int actualH = -1;
		int actualL = -1;
		int range = 100;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		double atr = 1000.0;
		int totalTpPips = 0;
		int totalSlPips = 0;
		PositionShort pos = new PositionShort();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				dayTrades = 0;
				lastDay = day;
				DO = q.getOpen5();
				if (actualH>=0 && actualL>=0)
					range = actualH-actualL;
				else range = 1000;
				
				ranges.add(range);
				
				atr = MathUtils.average(ranges, ranges.size()-1-period, ranges.size()-1);
				
				actualH = -1;
				actualL = -1;
				dayTrades = 0;
			}
			
			int tpPips = (int) (atr*atrTp);//base 5 digitos
			int slPips = (int) (atr*atrSL);//base 5 digitos
			if (dayTrades<=maxTrades 
					&& y>=y1 && y<=y2
					&& h>=h1 && h<=h2){
				int actualMaxMin = maxMin.get(i).getExtra();
				int entry = -1;
				int tpValue = -1;
				int slValue = -1;
				PositionType positionType = PositionType.NONE;
				if (actualMaxMin<=-thr){		
					entry = q1.getOpen5();
					slValue = entry-slPips;
					tpValue = entry+tpPips;
					positionType = PositionType.LONG;
				}else if(actualMaxMin>=thr){
					entry = q1.getOpen5();
					slValue = entry+slPips;
					tpValue = entry-tpPips;
					positionType = PositionType.SHORT;
				}
				if (entry!=-1){
					pos.setEntry(entry);
					pos.setTp(tpValue);
					pos.setSl(slValue);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setPositionType(positionType);
					PositionShort.evaluatePosition(data, i+1, data.size()-1, cal1, pos,moveBE, 99999, 2.0,true, false);
					totalTpPips += tpPips;
					totalSlPips += slPips;
					if (pos.getWinPips()>=0){
						wins++;
						winPips += pos.getWinPips();
					}else{
						losses++;
						lostPips -= pos.getWinPips();
					}
					dayTrades++;
				}
			}
			
			if (q.getHigh5()>=actualH || actualH==-1)
				actualH = q.getHigh5();
			if (q.getLow5()<=actualL || actualL==-1)
				actualL = q.getLow5();
		}//data
		
		int totalTrades = wins+losses;
		double winPer = wins*100.0/totalTrades;
		
		double pf = winPips*1.0/lostPips;
		double avg = (winPips*1.0-lostPips)/totalTrades;//viene ya multiplicado por 0.1
		double avgTp = totalTpPips*0.1/totalTrades;
		double avgSl = totalSlPips*0.1/totalTrades;
		
		System.out.println(
				h1+" "+h2
				+" "+PrintUtils.Print2dec(atrTp, false)
				+" "+PrintUtils.Print2dec(atrSL, false)
				+" "+thr
				+" "+moveBE
				+" || "
				+" "+PrintUtils.Print2Int(totalTrades,4)
				+" "+PrintUtils.Print2Int(wins,4)
				+" "+PrintUtils.Print2Int(losses,4)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "
				+" "+PrintUtils.Print2dec(avgTp, false)
				+" "+PrintUtils.Print2dec(avgSl, false)
				);
	}

	public static void main(String[] args) throws Exception {

		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);//paths.add(pathEURJPY);
		paths.add(pathGBPUSD);//paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
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
		  	ArrayList<Quote> data1h = ConvertLib.convert(data5m, 12);
			//ArrayList<QuoteShort> dataConverted       = QuoteShort.convertQuoteArraytoQuoteShort(data1h);
			ArrayList<QuoteShort> dataConverted       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = dataConverted;
			
			ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			for (int y1 = 2016;y1<=2016;y1+=1){
				int y2 = y1+0;
				for (int h1=0;h1<=0;h1++){
					for (int h2=h1+1;h2<=h1+1;h2++){
						for (double atrTp = 0.14;atrTp<=0.14;atrTp+=0.01){
						for (double atrSL = 0.30;atrSL<=0.30;atrSL+=0.05){
							//for (double atrTp = 1.0*atrSL;atrTp<=1.0*atrSL;atrTp+=0.10){
								for (int thr = 100;thr<=2000;thr+=100){
									for (int offset=0;offset<=0;offset++){
										for (int moveBE=200;moveBE<=200;moveBE++){
											for (int maxTrades = 5;maxTrades<=5;maxTrades++)
												TestHourly.doTest(data,  maxMinsExt, y1, y2, h1, h2, thr, atrTp, atrSL, 5, maxTrades,offset,moveBE);
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

}
