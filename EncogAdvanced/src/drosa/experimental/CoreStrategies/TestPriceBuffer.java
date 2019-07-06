package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestPriceBuffer {
	
	public static int getMinMaxBuff(ArrayList<Integer> maxMins, int begin, int end,
			int thr) {
		
		int index = -1;
		
		if (begin<=0) begin = 0;
		if (end>=maxMins.size()-1) end = maxMins.size()-1;
		
		for (int i=end;i>=begin;i--){
			int maxMin = maxMins.get(i);
			
			if (maxMin>=thr) return i;
			if (maxMin<=-thr) return i;
		}
				
		return index;
	}

	public static void doTest(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,int nbars,
			int tp,int sl,
			int maxBars
			){
	
		/*int maxPrice = 200000;//2.00000
		int minPrice =  70000;//0.70000
		HashMap<Integer,Integer> pricesH = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> pricesL = new HashMap<Integer,Integer>();
		for (int price=minPrice;price<=maxPrice;price+=10){
			pricesH.put(price, 0);
			pricesL.put(price, 0);
		}*/
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			if ((h==0 && min>=10) || (h!=0 && h>=h1 && h<=h2)){
				int index = getMinMaxBuff(maxMins,i-nbars,i-1,thr);
				
				if (index>=0){
					//System.out.println(index);
					int maxMin = maxMins.get(index);
					boolean isTrade = false;
					int diffPips = 0;
					if (maxMin>=thr){
						int valueTP = q.getOpen5()-10*tp;
						int valueSL = q.getOpen5()+10*sl;
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+maxBars, valueTP, valueSL, false);
						diffPips = q.getOpen5()-qm.getClose5();
						isTrade = true;
					}else if (maxMin<=-thr){
						int valueTP = q.getOpen5()+10*tp;
						int valueSL = q.getOpen5()-10*sl;
						TradingUtils.getMaxMinShortTPSL(data, qm, calqm, i, i+maxBars, valueTP, valueSL, false);
						diffPips = qm.getClose5()-q.getOpen5();
						isTrade = true;
					}		
					
					if (isTrade){
						if (diffPips>=0){
							wins++;
							winPips += diffPips;
						}else{
							losses++;
							lostPips += -diffPips;
						}
					}
				}
			}
			
			
			//actualizacion high
			//for (int j=minPrice;j<=q.getHigh5();j+=10) pricesH.put(j, i);
			//actualizacion low
			//for (int j=maxPrice;j>=q.getLow5();j-=10) pricesL.put(j, i);			
		}
		

		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		
		//if (pf>=2.0 && avg>=4.5 && trades>=400)
		System.out.println(
				
				h1+" "+thr+" "+nbars+" "+tp+" "+sl+" "+maxBars
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.10.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			//0,1,2: 200,6,8,48,240 
			//0: 	140,10,11,88,240
			//1: 	190,12,11,88,240			
			//2: 	180,12,11,88,240			
			//3:	400,12,11,88,240 			
			//4:	450,1,11,88,84  			
			//5:    450,6,10,50,120 
			//6:    200,3,20,60,36  
			//7:	500,1,20,60,36
			//8: 	550,4,13,66,12 
			//9:	500,2,8,56,17
			//19:   500,1,20,60,228
			//20:  1200,2,17,51,192
			//22:   850,2,10,60,228
			//23: 	200,6,11,88,144
			for (int y1=2012;y1<=2012;y1++){
				int y2 = y1+4;
				for (int h1=8;h1<=8;h1++){
					int h2 = h1+0;
					for (int thr=10;thr<=600;thr+=10){
						for (int nbars=1;nbars<=1;nbars+=1){
							for (int tp=11;tp<=11;tp++){
								for (int sl=8*tp;sl<=8*tp;sl+=1*tp){
									for (int maxBars=240;maxBars<=240;maxBars+=12){
										TestPriceBuffer.doTest(data,maxMins,y1,y2,h1,h2,thr,nbars,tp,sl,maxBars);
									}
								}
							}
							
						}
					}
				}
			}
		}

		System.out.println("programa finalizado");
	}

}
