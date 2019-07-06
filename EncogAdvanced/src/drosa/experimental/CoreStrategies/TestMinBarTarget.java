package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMinBarTarget {

	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int maxBars,
			int backBars,
			int minPips,
			int targetPips
			){
		//

		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		QuoteShort qAdverse = new QuoteShort();
		int lastDay = -1;
		double accAverage = 0;
		int accAdverse = 0;
		int accElapsedTime = 0;
		int avgDistance = 0;
		int totalTrades = 0;
		int total = 0;
		int total0 = 0;
		int total1 = 0;
		int total2 = 0;
		int total3 = 0;
		int total4 = 0;
		int total5 = 0;
		int total6 = 0;
		int total12 = 0;
		int total24 = 0;
		int total36 = 0;
		int total48 = 0;
		
		ArrayList<Double> adverses = new ArrayList<Double>(); 
		for (int i=backBars;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				int index = i+0;
				if (index>=data.size()-1) index = data.size()-1;
				int diffOpen = data.get(index).getOpen5()-data.get(index-backBars).getOpen5();
				int mode = 0;
				if (diffOpen>=minPips*10){
					mode = -1;
				}else if (diffOpen<=-minPips*10){
					mode = 1;
				}
				
				if (mode==0){
					//lastDay = day;//probar comentando esta linea
					continue;
				}
				totalTrades++;
				int lastValue = data.get(index).getOpen5();
				for (int b=index;b<=data.size()-1;b++){
					q = data.get(index);
					QuoteShort qb = data.get(b);
					
					QuoteShort.getCalendar(calqm, qb);
					int hb = calqm.get(Calendar.HOUR_OF_DAY);
					//if (hb<h1 || hb>h2){
						//continue;
					//}
					
					int begin = b;
					int end = begin+maxBars;
					if (end>=data.size()-1) end = data.size()-1;
										
					int distance = 0;
					
					int indexTarget = -1;
					int adverse = 0;
					boolean isTrade = false;
					int trade = 0;
					
					if (b>index){
						if (mode==-1){
							if (qb.getOpen5()<lastValue) continue;
						}
						if (mode==1){
							if (qb.getOpen5()>lastValue) continue;
						}
					}
					
					lastValue = qb.getOpen5();
					
					//PRUEBA: intentar solo vender
					if (mode==-1
							){
						//indexTarget = TradingUtils.getMaxMinIndex(data, begin, end, qb.getOpen5()-targetPips*10, false);
						indexTarget = TradingUtils.getMaxMinIndexAdverse(data, begin, end, qb.getOpen5()-targetPips*10,qAdverse, false);
						adverse = qAdverse.getOpen5()-qb.getOpen5();
						
						//diff = qb.getOpen5()-qm.getLow5();
						distance = qb.getOpen5()-q.getOpen5();
						isTrade = true;
						trade = -1;
						//System.out.println("[SHORT] "+diffOpen+" "+indexTarget);
					}else if (mode==1) 
						{
						//indexTarget = TradingUtils.getMaxMinIndex(data, begin, end, qb.getOpen5()+targetPips*10, true);
						indexTarget = TradingUtils.getMaxMinIndexAdverse(data, begin, end, qb.getOpen5()+targetPips*10,qAdverse, true);
						adverse = qb.getOpen5()-qAdverse.getOpen5();
						
						//diff = qm.getHigh5()-qb.getOpen5();
						distance = q.getOpen5()-qb.getOpen5();
						isTrade = true;
						trade = 1;
					}
					
					if (isTrade){
						System.out.println("[TRY] "+trade+" "+qb.toString());
						if (indexTarget>=0){ //si ese b supera los target anotamos y vemos cual es la media
							int time = b-index;
							if (time<=0) total0++;
							if (time<=1) total1++;
							if (time<=2) total2++;
							if (time<=3) total3++;
							if (time<=4) total4++;
							if (time<=5) total5++;
							if (time<=6) total6++;
							if (time<=12) total12++;
							if (time<=24) total24++;
							if (time<=36) total36++;
							if (time<=48) total48++;
							
							if (time>=6){
								//System.out.println("[ABOVE 6] "+trade+" "+time+" || "+q.toString()+" || "+PrintUtils.Print2dec(adverse*0.1,false)+" || "+data.get(indexTarget).toString());
							}
							System.out.println("[TRADE WIN] "+trade+" "+adverse+" || "+qb.toString()+" || "+qAdverse.getOpen5()+" || "+distance);
							avgDistance += distance; 									
							accAverage+=time;
							accAdverse += adverse;
							accElapsedTime += indexTarget-b;
							adverses.add(adverse*0.1);
							total++;
							break;
						}						
					}
				}
				lastDay = day;
			}			
		}
		
	
		double avg = accAverage*1.0/total;
		double per0 = total0*100.0/total;
		double per1 = total1*100.0/total;
		double per2 = total2*100.0/total;
		double per3 = total3*100.0/total;
		double per4 = total4*100.0/total;
		double per5 = total5*100.0/total;
		double per6 = total6*100.0/total;
		double per12 = total12*100.0/total;
		double per24 = total24*100.0/total;
		double per36 = total36*100.0/total;
		double per48 = total48*100.0/total;
		
		double perWin = total*100.0/totalTrades;
		
		String header1 = h1+" "+h2+" "+maxBars+" "+targetPips+" "+backBars+" "+minPips
				+"  || "
				+" "+totalTrades+" "+total+" "+" || "+PrintUtils.Print2dec(perWin, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(per0, false)
				+" "+PrintUtils.Print2dec(per1, false)
				+" "+PrintUtils.Print2dec(per2, false)
				+" "+PrintUtils.Print2dec(per3, false)
				+" "+PrintUtils.Print2dec(per4, false)
				+" "+PrintUtils.Print2dec(per5, false)
				+" || "+PrintUtils.Print2dec(per6, false)
				+" "+PrintUtils.Print2dec(per12, false)
				+" "+PrintUtils.Print2dec(per24, false)
				//+" "+PrintUtils.Print2dec(per36, false)
				//+" "+PrintUtils.Print2dec(per48, false)
				+" || "+PrintUtils.Print2dec(avgDistance*0.1/total, false)
				//+" || "+PrintUtils.Print2dec(perWin, false)
				+" || "+PrintUtils.Print2dec(accAdverse*0.1/total, false)
				+" || "+PrintUtils.Print2dec(accElapsedTime*0.1/total, false)
				+" ||| "
				;
		
		MathUtils.summary_complete(header1, adverses);
	
	}
	
	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\gbpUSD_UTC_5 Mins_Bid_2003.05.04_2016.10.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.10.27.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int avgDistance = 0;
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
			
			for (int y1=2016;y1<=2016;y1++){
				int y2 = y1+0;
				for (int h1=0;h1<=0;h1++){
					for (int h2=h1;h2<=h1;h2++){
						for (int thr=500;thr<=500;thr+=1){
							for (int maxBars=240;maxBars<=240;maxBars+=120){
								//for (int backBars=12;backBars<=48*12;backBars+=12){
								for (int backBars=100;backBars<=100;backBars+=12){
									for (int tp=15;tp<=15;tp+=5){
										//for (int minPips=(int) (0.4*backBars);minPips<=0.4*backBars;minPips+=1*backBars){
										for (int minPips=100;minPips<=100;minPips+=10){
											TestMinBarTarget.doTest("", data, maxMins, y1, y2, h1, h2, thr, maxBars,backBars,minPips, tp);
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
