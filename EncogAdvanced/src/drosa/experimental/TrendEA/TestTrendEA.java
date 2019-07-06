package drosa.experimental.TrendEA;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.CurrencyType;
import drosa.experimental.PositionShort;
import drosa.experimental.StatsDebugOptions;
import drosa.experimental.SuperStrategy;
import drosa.experimental.SystemStats;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestTrendEA {

	
	public static void test(ArrayList<QuoteShort> data,
			int begin,int end,
			String hours,int tp,int sl,
			int offset,
			int openDiffPos,
			double riskPerTrade,double comm){
		
		int totalDays = 0;
		int totalMaxMins = 0;
		//stats
		SystemStats systemStats = new SystemStats(100.0,400,1,
						openDiffPos,comm,CurrencyType.USD_BASED,tp,sl,true);
		//positions
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		//hours
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		
		int begin2 = begin;
		if (begin<0) begin2 = 0;
		int end2 = end;		
		if (end2>=data.size()-1) end2 = data.size()-2;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int max = -9999;
		int min = 9999999;
		int firstMax = -999999;
		int firstMin = 9999999;
		int countMax = 0;
		int countMin = 0;
		int lastPointHour =-1;
		int lastPointType = 0; //-1:min 1: max
		PositionShort pos = null;
		int entryValue = 0;
		int slValue = 0;
		int tpValue = 0;
		boolean first = false;
		//bucle principal
		for (int i=begin2;i<end2;i++){
			QuoteShort q = data.get(i); //quote para ver si max or min
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int h   = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				
				if (day!=-1){	
					int diffMax = max-firstMax;
					int diffMin = firstMin-min;
					if (diffMax>=50 && firstMax!=-999999){
						//System.out.println("Max Superado "+firstMax+" "+max+" "+(max-firstMax));
						countMax++;
					}
					else if (diffMin>=50 && firstMin!=999999){
						//System.out.println("Min Superado "+firstMin+" "+min+" "+(firstMin-min));
						countMin++;
					}
				}
				
				//System.out.println("****NUEVO DIA****");
				first = true;
				totalDays++;
				max      = -999999;
				min      =  999999;
				firstMax = -999999;
				firstMin =  999999;
				lastPointHour = -1;
				lastPointType = 0;
													
				lastDay = day;
			}
			int mode  = -1;
			//ACTUALIZAMOS MAXIMOS Y MINIMOS
			//nuevo maximo
			if (q.getHigh5()>max){
				max = q.getHigh5();
				lastPointHour = h;
				lastPointType = 1;
				mode = 1;
				PositionShort.removePositions(positions, PositionStatus.PENDING);				
			}
			//nuevo minimo
			if (q.getLow5()<min){
				min = q.getLow5();
				lastPointHour = h;
				lastPointType = -1;
				mode = 0;
				PositionShort.removePositions(positions, PositionStatus.PENDING);
			}
			
			int allowed = allowedHours.get(h);
			if (allowed==1 && ((h==0 && minute>=10) || h>0) && mode>=0){
				//con cada maximo movemos las pending orders
				
				if (mode==1){//maximo
					entryValue = q.getClose5()-offset*10;
					slValue = entryValue-sl*10;
					tpValue = entryValue+tp*10;					
					if (first){
						totalMaxMins++;
						firstMax = q.getHigh5();
						//System.out.println("actualizo firstMax");
					}
					first = false;
					//System.out.println("NUEVO MAXIMO ENTRY: "+max+" "+entryValue);
				}else if (mode==0){//minimo
					entryValue = q.getClose5()+offset*10;
					slValue = entryValue+sl*10;
					tpValue = entryValue-tp*10;
					
					if (first){
						totalMaxMins++;
						firstMin = q.getLow5();
						//System.out.println("actualizo firstMin");
					}
					first = false;
					//System.out.println("NUEVO MINIMO ENTRY: "+min+" "+entryValue);
				}
				pos = new PositionShort();
				pos.setPositionStatus(PositionStatus.PENDING);
				pos.setEntry(entryValue);
				pos.setSl(slValue);
				pos.setTp(tpValue);
				pos.setRisk(riskPerTrade);
				pos.setPendingIndex(i+1);
				pos.setOpenDiff(openDiffPos);
				if (mode==0) pos.setPositionType(PositionType.SHORT);
				if (mode==1) pos.setPositionType(PositionType.LONG);
				positions.add(pos);
			}
			//actualizamos con qi
			//systemStats.update(positions, q, i, StatsDebugOptions.ONLY_SUMMARY);
			systemStats.update(positions, q1, i+1, StatsDebugOptions.ONLY_SUMMARY);
		}
		//systemStats.printSummary(tp+" "+sl+" "+offset);		
		systemStats.finish(positions);
		
		System.out.println("totalDays totalMaxMins "+totalDays
				+" "+PrintUtils.Print2(totalMaxMins*100.0/totalDays)
				+" "+PrintUtils.Print2((countMax+countMin)*100.0/totalMaxMins)
				);
	}
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.10.31.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.10.31.csv";
		//String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";		
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS);   		
		ArrayList<QuoteShort> data5mS  = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		ArrayList<QuoteShort> data = null;		
		data = data5mS;
		
		//SETTINGS
		int begin		= 400000;	
		int numBins 	= 1;
		int binSpread 	= 99999999;
		int end   		= data.size();		
		int tp1 = 12;
		int tp2 = 12;
		int tpInc = 100;
		int sl1 = 20;
		int sl2 = 20;
		int slInc = 1;
		double risk1 = 0.1;
		double risk2 = 0.1;
		double riskInc = 1.0;
		int maxPos1 = 5;
		int maxPos2 = 5;
		double capital = 100;		
		double off1 = 0.7;
		double off2 = 0.7;
		double offInc = 0.1;
		int openDiffdf1 = 0;
		int openDiffdf2 = 0;
		int openDiffInc = 1;
		int hCloseOffset1 = 0;
		int hCloseOffset2 = 0;
		int offsetOC1 = 0;
		int offsetOC2 = 0;
		int offsetOCInc = 1;
		int brokerLeverage = 400;
		int bar1 = 200;
		int bar2 = 200;
		int barInc = 5;
		double comm = 1.6;
		boolean digits5=true;
		int h1 = 11;
		int h2 = 14;
		
		System.out.println("data: "+data.size());
		for (h1=0;h1<=23;h1++){
			h2 = h1+0;
			//hours+=
			System.out.println("*****H "+h1+"*****");
			for (int tp=5;tp<=5;tp+=1){
				for (int sl=(int) (tp*3);sl<=tp*3;sl+=tp*1.0){
					for (int off=tp;off<=tp;off+=1){
						for (int openDiff=15;openDiff<=15;openDiff++){
							//TestTrendEA.test(data,begin,end,"0", tp, sl,off, openDiff, 40.0,comm);
							//TestTrendEA.test(data,begin,end,"0", tp, sl,off, openDiff, 40.0);
							TestTrendEA.test(data,begin,end,String.valueOf(h1), tp, sl,off, openDiff, 15.0,comm);
						}
					}
				}
			}
		}
		
	}

}
