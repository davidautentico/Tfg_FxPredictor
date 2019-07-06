package drosa.experimental.mer071898;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestSequencies {
	
	
	private static void studySequencies(ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,int h1,int h2, int maxMin
			,ArrayList<Integer> aMult) {
		// TODO Auto-generated method stub
		ArrayList<Integer> entriesProfitAcc = new ArrayList<Integer>();
		ArrayList<Integer> entriesTotal = new ArrayList<Integer>();
		ArrayList<Integer> entries = new ArrayList<Integer>();
		for (int i=0;i<=500;i++){
			entriesProfitAcc.add(0);
			entriesTotal.add(0);
		}
		int avgProfit = 0;
		int maxDiff = 0;
		int avgDiff = 0;
		int avgSeq = 0;
		int maxSeq = 0;
		int totalSeq = 0;
		int actualSeq = 0;
		int lastValue = 0;
		Calendar cal = Calendar.getInstance();
		int initialIndex = 0;
		int endIndex = 0;
		for (int i=0;i<maxMins.size()-1;i++){
			QuoteShort qd = data.get(i);
			QuoteShort qd1 = data.get(i+1);
			QuoteShort q = maxMins.get(i);
			QuoteShort q1 = maxMins.get(i+1);
			QuoteShort.getCalendar(cal, qd);
			int value = q.getExtra();
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			int actualValue = 0;
			if (value>=maxMin) actualValue = 1;
			if (value<=-maxMin) actualValue = -1;
			
			if (actualValue!=0){
				//System.out.println("actualValue: "+value+" "+actualValue);
				if (actualValue==lastValue){
					actualSeq++;
					endIndex = i;
					entries.add(qd1.getOpen5());
					/*if (actualSeq>=73){
						System.out.println("[>135] "+DateUtils.datePrint(cal)
								+" "+actualValue
								+" "+actualSeq+" || "+qd.toString()+" || "+qd1.toString()
								);
					}*/
				}else{
					//System.out.println("finalizada seq "+lastValue+" "+actualSeq);
					QuoteShort q0 = data.get(initialIndex+1);//se abre a la siguiente
					QuoteShort qn = data.get(endIndex); 
					QuoteShort.getCalendar(cal, q0);
					int hinicial = cal.get(Calendar.HOUR_OF_DAY);
					if (h1<=hinicial && hinicial<=h2){//estadisticas si solo estan en ese rango horario, la inicial
						if (actualSeq>maxSeq) maxSeq = actualSeq;
						avgSeq+=actualSeq;
						totalSeq++;
						
						int diff = 0;
						boolean isBull = true;
						if (lastValue==1){//selling
							diff = qn.getHigh5()-q0.getOpen5();
							
							//System.out.println("diff: "+diff);
						}else if (lastValue==-1){//buying
							diff = q0.getOpen5()-qn.getLow5();
							isBull = false;
							//System.out.println("diff: "+diff);
						}
						avgDiff+=diff;
						if (diff>=maxDiff) maxDiff=diff;
						
						//calcular el profitacc por posicion y calculamos el total de trades por posicion
						//para al final calcular el profit medio por posicion
						int profit = calculateProfit(entries,entriesProfitAcc,qd1.getOpen5(),isBull,aMult);
						for (int e=0;e<entries.size();e++){
							entriesTotal.set(e,entriesTotal.get(e)+1);
						}
						avgProfit+=profit;
						//if (actualSeq>=73)
						//	System.out.println("[>135] reverse "+DateUtils.datePrint(cal)+" "+actualSeq+" || "+qd.toString());
					}
					
					
					initialIndex = i;
					endIndex = i;
					actualSeq=1;
					entries.clear();
				}
				lastValue = actualValue;
			}
		}
		System.out.println(h1+" "+h2+" "+maxMin
				+" || "+totalSeq
				+" "+PrintUtils.Print2dec(avgSeq*1.0/totalSeq,false)
				+" "+ maxSeq
				+" || "+PrintUtils.Print2dec(avgDiff*0.1/totalSeq,false)
				+" "+PrintUtils.Print2dec(maxDiff*0.1,false)
				+" || "+PrintUtils.Print2dec(avgProfit*0.1/totalSeq,false)
				);
		
		/*for (int i=0;i<=80;i++){
			int total = entriesTotal.get(i);
			int acc = entriesProfitAcc.get(i);
			System.out.println((i+1)+" || "+total+" "+PrintUtils.Print2dec(acc*0.1/total,false));
		}*/
	}

	private static int calculateProfit(ArrayList<Integer> entries, 
			ArrayList<Integer> entriesProfitAcc,
			int open,boolean isBull,ArrayList<Integer> aMult) {
		// TODO Auto-generated method stub
		int profit = 0;
		int mult=1;
		for (int i=0;i<entries.size();i++){
			int diff = entries.get(i)-open;//bull es selling
			if (!isBull){//buying
				diff = open-entries.get(i);
			}
			mult = aMult.get(i);
			profit+=mult*diff;
			entriesProfitAcc.set(i, entriesProfitAcc.get(i)+diff);
			
		}
		return profit;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2007.01.01_2015.08.06.csv";
		String path5m1   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2007.01.01_2015.08.06.csv";
		String path5m2   = "c:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2007.01.01_2015.08.06.csv";
		String path5m3   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2007.01.01_2015.08.06.csv";
		String path5m4   = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2011.12.31_2015.08.06.csv";
		String path5m5   = "c:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2011.12.31_2015.08.06.csv";
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(path5m0);
		paths.add(path5m1);
		paths.add(path5m2);
		paths.add(path5m3);
		paths.add(path5m4);
		paths.add(path5m5);
		
		for (int i=0;i<=0;i+=1){
			String provider ="";
			Sizeof.runGC ();
			ArrayList<Quote> dataI 		= null;
			ArrayList<Quote> dataS 		= null;
			String path5m = paths.get(i);
			System.out.println(path5m);
			if (path5m.contains("pepper")){
				dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				provider="pepper";
			}else{
				dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="dukasc";
			}								
	  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 			  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			
			ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			
			/*for (int j=0;j<data.size();j++){
				System.out.println(data.get(j).toString());
			}*/
			/*for (int j=0;j<data.size();j++){
				QuoteShort maxMin = maxMinsExt.get(j);
				System.out.println(maxMin.getExtra());
			}*/
			ArrayList<Integer> aMult1 = new ArrayList<Integer>();
			ArrayList<Integer> aMult2 = new ArrayList<Integer>();
			ArrayList<Integer> aMult3 = new ArrayList<Integer>();
			ArrayList<Integer> aMult4 = new ArrayList<Integer>();
			ArrayList<Integer> aMult5 = new ArrayList<Integer>();
			for (int m=0;m<=500;m++){
				aMult1.add(1);
				aMult2.add(1);
				aMult3.add(1);
				aMult4.add(1);
				aMult5.add(1);
			}
			for (int m=10;m<=500;m++) aMult2.set(m,2);
			for (int m=10;m<=500;m++) aMult3.set(m,3);
			for (int m=10;m<=500;m++) aMult4.set(m,4);
			for (int m=10;m<=500;m++) aMult5.set(m,5);
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+23;
				for (int maxMin=100;maxMin<=3000;maxMin+=100){
					studySequencies(data,maxMinsExt,h1,h2,maxMin,aMult1);	
					//studySequencies(data,maxMinsExt,h1,h2,maxMin,aMult2);
					//studySequencies(data,maxMinsExt,h1,h2,maxMin,aMult3);
					//studySequencies(data,maxMinsExt,h1,h2,maxMin,aMult4);
					//studySequencies(data,maxMinsExt,h1,h2,maxMin,aMult5);
				}
			}
		}//main for
		
		
		System.out.println("FINALIZADO");
	}

	

}
