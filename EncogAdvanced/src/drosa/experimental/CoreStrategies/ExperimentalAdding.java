package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class ExperimentalAdding {

	public static void doTest(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,int diffPips,int tp){
		
		//se meten unidades siempre que esten diff pips por encima del avgValue
		int maxOpenUnidades = 0;
		int winUs = 0;
		int totalUnidades = 0;
		int avgValue = 0;
		int mode = 0;
		int lastValue = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q1);
			
			int maxMin = maxMins.get(i-1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h>=h1 && h<=h2){
				if (totalUnidades == 0
						){
					if (maxMin >= thr){
						avgValue = q.getOpen5();
						lastValue = q.getOpen5();
						totalUnidades++;
						mode = -1;
						//System.out.println("[NEW SHORT] 1 ||"+avgValue);
					}else if (maxMin <=-thr){
						avgValue = q.getOpen5();
						lastValue = q.getOpen5();
						totalUnidades++;
						mode = 1;
					}
				}else{
					if (mode==1){
						int diff = q.getHigh5()-avgValue;
						if (diff>=tp*10){
							winUs += totalUnidades;
							//if (totalUnidades>=50)
								//System.out.println("[PROFIT LONG] "+totalUnidades+" || "+winUs);
							if (totalUnidades>=maxOpenUnidades) maxOpenUnidades = totalUnidades;
							totalUnidades = 0;
							avgValue = 0;
							mode = 0;
						}else if (q.getLow5()<=lastValue-10*diffPips){
							//añadimos una nueva posicion, bajando el avgValue
							avgValue = (totalUnidades*avgValue+1*(lastValue-10*diffPips))/(totalUnidades+1);
							lastValue = lastValue-10*diffPips;
							totalUnidades++;
						}
					}else if (mode==-1){
						int diff = avgValue-q.getLow5();
						if (diff>=tp*10){
							winUs += totalUnidades;
							//if (totalUnidades>=50)
								//System.out.println("[PROFIT SHORT] "+totalUnidades+" || "+winUs);
							if (totalUnidades>=maxOpenUnidades) maxOpenUnidades = totalUnidades;
							totalUnidades = 0;
							avgValue = 0;
							mode = 0;						
						}else if (q.getHigh5()>=lastValue+10*diffPips){
							//añadimos una nueva posicion, bajando el avgValue
							avgValue = (totalUnidades*avgValue+1*(lastValue+10*diffPips))/(totalUnidades+1);
							lastValue = lastValue+10*diffPips; 
							totalUnidades++;
							//if (totalUnidades>=50)
								//System.out.println("[ADDING SHORT] "+totalUnidades+" || "+" "+avgValue+" || "+(q.getClose5()-avgValue)+" "+totalUnidades);
						}
					}				
				}
			}
		}
		
		System.out.println(
				
				h1+" "+h2+" "+thr+" "+tp+" "+diffPips
				+" || "+maxOpenUnidades
				);
		
	}
	
	
	
	public static void main(String[] args) throws Exception {
String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.06.28.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			//SimpleContinuation
			/*for (int h1=16;h1<=16;h1++){
				int h2=h1+6;
				for (int thr=1000;thr<=10000;thr+=1000){
					for (int nbars = 90;nbars<=90;nbars+=10){
						CoreContinuation.doTestSimpleContinuation(data, maxMins, h1, h2, thr, nbars);
					}
				}
			}*/
			
			//Pullback
			for (int y1=2003;y1<=2003;y1++){
				int y2 = y1+13;
				for (int h1=0;h1<=0;h1++){
					int h2=h1+9;
					for (int thr=1000;thr<=100000;thr+=1000){
						for (int tp = 10;tp<=10;tp+=10){
							for (int diffPips= 50;diffPips<=50;diffPips+=10){
								ExperimentalAdding.doTest(data, maxMins, y1, y2, h1, h2, thr, diffPips, tp);									
							}
						}						
					}
				}
			}
		}//limit
	}
}
