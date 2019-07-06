package drosa.experimental.claudia;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestScalpingHourly {

	
	public static void testHourlyRetest(ArrayList<QuoteShort> data,
			int begin,int end,
			int h1,int h2,
			int tp,int sl,
			int bodyPips,int wickPips){
		
		int wins = 0;
		int losses = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastHour = -1;
		int actualOpen = -1;
		int actualClose = -1;
		int actualMax = -1;
		int actualMin = -1;
		int diffBody = 0;
		int diffWick = 0;
		int openNewTrade = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h!=lastHour){
				openNewTrade = 0;
				if (lastHour!=-1){
					diffBody = actualClose-actualOpen;
					boolean bull = true;
					if (diffBody>=0){
						diffWick = actualMax-actualClose;
						bull = true;
					}
					else{
						diffWick = actualClose-actualMin;
						bull = false;
					}
					if (Math.abs(diffBody)>=bodyPips && diffWick>=wickPips && lastHour>=h1 && lastHour<=h2){
						if (bull) openNewTrade = 1;
						else openNewTrade = -1;
					}
				}
				actualOpen = q.getOpen5();
				actualMax = q.getHigh5();
				actualMin = q.getLow5();
				actualClose = q.getClose5();
				lastHour= h;
			}
			
			int entry = -1;
			int slValue = -1;
			int tpValue = -1;
			PositionType positionType = PositionType.NONE;
			if (openNewTrade==1){
				entry = q.getOpen5();
				tpValue = entry+10*tp;
				slValue = entry-10*sl;
				positionType = PositionType.LONG;
			}else if (openNewTrade==-1){
				entry = q.getOpen5();
				tpValue = entry-10*tp;
				slValue = entry+10*sl;
				positionType = PositionType.SHORT;
			}
			
			if (entry!=-1){
				openNewTrade = 0;
				PositionShort pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(slValue);
				pos.setTp(tpValue);
				pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setPositionType(positionType);
				positions.add(pos);
				//System.out.println(pos.toString2());
			}
			
			//updatePositions
			int s = 0;
			//if (positions.size()>0) System.out.println(q1.toString());
			while (s<positions.size()){
				PositionShort p = positions.get(s);
				boolean removed = false;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					boolean closed = false;
					if (p.getPositionType()==PositionType.LONG){
						if (q.getLow5()<=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							p.setWin(-1);
							losses++;
							closed	= true;
						}else if (q.getHigh5()>=p.getTp()){
							p.setPositionStatus(PositionStatus.CLOSE);
							p.setWin(1);
							wins++;
							closed	= true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q.getHigh5()>=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							p.setWin(-1);
							losses++;
							closed	= true;
						}else if (q.getLow5()<=p.getTp()){
							p.setPositionStatus(PositionStatus.CLOSE);
							p.setWin(1);
							wins++;
							closed  = true;
						}
					} 
					if (closed){
						//System.out.println(p.toString2());
						positions.remove(s);//borramos y no avanzamos
						removed = true;
					}
				}//open
				if (!removed){
					s++;
				}
			}//while
			//actualizamos maximos y cierre
			if (q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (q.getLow5()<=actualMin) actualMin = q.getLow5();
			actualClose = q.getClose5();
		}
		
		int total = wins+losses;
		double perWin = wins*100.0/total;
		double pf = ((wins*tp*1.0)/(losses*sl));
		double exp = (wins*tp-losses*sl)*1.0/total;
		
		System.out.println(h1+" "+h2+" "+tp+" "+sl+" "+bodyPips+" "+wickPips
				+" || "+total
				+" "+PrintUtils.Print2dec(perWin, false, 2)
				+" "+PrintUtils.Print2dec(pf, false, 2)
				+" "+PrintUtils.Print2dec(exp, false, 2)
				);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName   ="1 Min_Bid_2010.12.31_2015.04.23.csv";
		//String fileName   ="1 Min_Bid_2011.12.31_2015.04.23.csv";
		//String fileName   ="1 Min_Bid_2012.12.31_2015.04.23.csv";
		//String fileName   ="1 Min_Bid_2013.12.31_2015.04.23.csv";
		//String fileName   ="1 Min_Bid_2014.12.31_2015.04.23.csv";
		String fileNameP   ="2013_08_29_2015_04_23.csv";
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_"+fileName;
		String pathEURUSDp = "C:\\fxdata\\EURUSD5_pepper_"+fileNameP;
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_"+fileName;
		String pathGBPUSDp = "C:\\fxdata\\GBPUSD1_pepper_"+fileNameP;
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_"+fileName;
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_"+fileName;
		String pathUSDJPYp = "C:\\fxdata\\USDJPY1_pepper_"+fileNameP;
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		
		int limit = paths.size()-1;
		int initial = 1;
		limit       = 1;
		for (int i=initial;i<=limit;i++){
			String path5m = paths.get(i);
			String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
			ArrayList<Quote> dataI 		=  null;
			ArrayList<Quote> dataS 		= null;
			if (path5m.contains("pepper")){
				dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
			}else{
				dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			}		
			ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			
			int begin = 0;
			int end = data.size()-1;
			for (int h1=0;h1<=23;h1++){
				int h2 = h1+5;
				//for (int sl=5;sl<=5;sl++){
				//for (int tp=1*sl;tp<=1*sl;tp+=1*sl){
				for (int tp=10;tp<=10;tp+=1){
					for (int sl=10;sl<=10;sl++){
						for (int bodyPips = 200;bodyPips<=200;bodyPips+=10){
							for (int wickPips = 100;wickPips<=100;wickPips+=10){
								TestScalpingHourly.testHourlyRetest(data, begin, end, h1, h2, tp, sl, bodyPips, wickPips);
							}
						}
					}
				}
			}
		}
	}

}
