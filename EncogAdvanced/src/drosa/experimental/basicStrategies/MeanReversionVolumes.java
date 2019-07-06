package drosa.experimental.basicStrategies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.data.DataUtils;
import drosa.data.TickQuote;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.experimental.CoreStrategies.TestPriceBuffer;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MeanReversionVolumes {
	
	public static double doTest3(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			int maxPipsDiff,
			int maxReactionAllowed,
			int maxTrades,			
			double comm,
			boolean debug,
			boolean printSummary,
			int returnMode
			){
		//
				
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
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
			
			
			//modulo de entrada
			if (positions.size()<maxTrades
					&& h>=h1 && h<=h2
					){					
				int maxMin = maxMins.get(i-1);	
					
				double pipValue = 1.0;
				if (maxMin>=thr){
					//aqui vemos si en las N ultimas velas se ha usado este nivel o menos					
					int maxReaction = TradingUtils.getMaxReaction(data,i-2-48,i-2,q.getOpen5(),maxPipsDiff,-1);
					if (maxReaction>=maxReactionAllowed*10){
						//System.out.println("maxReaction: "+maxReaction+" || "+maxReactionAllowed);
					}
					if (maxReaction>=maxReactionAllowed*10){										
						PositionCore pos = new PositionCore();
						pos.setEntry(q.getOpen5());
						pos.setTp(q.getOpen5()-10*tp);
						pos.setSl(q.getOpen5()+10*sl);
						pos.setEntryIndex(i);
						pos.setPositionType(PositionType.SHORT);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						//pipValue
						pos.setPipValue(pipValue);										
						positions.add(pos);
					}
				}else if (maxMin<=-thr){
					int maxReaction = TradingUtils.getMaxReaction(data,i-2-48,i-2,q.getOpen5(),maxPipsDiff,1);
					
					if (maxReaction>=maxReactionAllowed*10){						
						PositionCore pos = new PositionCore();
						pos.setEntry(q.getOpen5());
						pos.setTp(q.getOpen5()+10*tp);
						pos.setSl(q.getOpen5()-10*sl);
						pos.setEntryIndex(i);
						pos.setPositionType(PositionType.LONG);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						//pipValue
						pos.setPipValue(pipValue);
						positions.add(pos);
					}
				}				
			}
						
			//evaluacion trades			
			int j = 0;			
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				if (pos.getPositionType()==PositionType.SHORT){					
					if (q.getHigh5()>=pos.getSl()){
						isClosed = true;
					}else if (q.getLow5()<=pos.getTp()){
						isClosed = true;
					}					
					if (isClosed){
						pips = pos.getEntry()-q.getClose5();
					}
				}else if (pos.getPositionType()==PositionType.LONG){					
					if (q.getLow5()<=pos.getSl()){
						isClosed = true;
					}else if (q.getHigh5()>=pos.getTp()){
						isClosed = true;
					}					
					if (isClosed){
						pips = q.getClose5()-pos.getEntry();
					}
				}
				
				if (isClosed){
					pips-=comm*10;
					if (pips>=0){
						wins++;
						winPips+=pips;
					}else{
						losses++;
						lostPips+=-pips;
					}
												
					if (debug){
						System.out.println("[CLOSED] "+DateUtils.datePrint(cal)+" || "+PrintUtils.Print2dec(pips, false)+" || "+pos.toString());
					}
					
					positions.remove(j);
				}else{
					j++;
				}
			}
		}//data
		
				
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
						
		if (printSummary)
		System.out.println(
				header
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "				
				);
					
		return 0; 
	}

	public static double doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			int maxTrades,			
			double comm,
			boolean debug,
			boolean printSummary,
			int returnMode
			){
		//
				
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
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
			
			
			//modulo de entrada
			if (positions.size()<maxTrades
					&& h>=h1 && h<=h2
					){					
				int maxMin = maxMins.get(i-1);	
					
				double pipValue = 1.0;
				if (maxMin>=thr){
					PositionCore pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()-10*tp);
					pos.setSl(q.getOpen5()+10*sl);
					pos.setEntryIndex(i);
					pos.setPositionType(PositionType.SHORT);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					//pipValue
					pos.setPipValue(pipValue);										
					positions.add(pos);
				}else if (maxMin<=-thr){
					PositionCore pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()+10*tp);
					pos.setSl(q.getOpen5()-10*sl);
					pos.setEntryIndex(i);
					pos.setPositionType(PositionType.LONG);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					//pipValue
					pos.setPipValue(pipValue);
					positions.add(pos);
				}				
			}
						
			//evaluacion trades			
			int j = 0;			
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				if (pos.getPositionType()==PositionType.SHORT){					
					if (q.getHigh5()>=pos.getSl()){
						isClosed = true;
					}else if (q.getLow5()<=pos.getTp()){
						isClosed = true;
					}					
					if (isClosed){
						pips = pos.getEntry()-q.getClose5();
					}
				}else if (pos.getPositionType()==PositionType.LONG){					
					if (q.getLow5()<=pos.getSl()){
						isClosed = true;
					}else if (q.getHigh5()>=pos.getTp()){
						isClosed = true;
					}					
					if (isClosed){
						pips = q.getClose5()-pos.getEntry();
					}
				}
				
				if (isClosed){
					pips-=comm*10;
					if (pips>=0){
						wins++;
						winPips+=pips;
					}else{
						losses++;
						lostPips+=-pips;
					}
												
					if (debug){
						System.out.println("[CLOSED] "+DateUtils.datePrint(cal)+" || "+PrintUtils.Print2dec(pips, false)+" || "+pos.toString());
					}
					
					positions.remove(j);
				}else{
					j++;
				}
			}
		}//data
		
				
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
						
		if (printSummary)
		System.out.println(
				header
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "				
				);
					
		return 0; 
	}
	
	public static double doTest2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<ArrayList<Double>>  volumesDaily,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			int maxTrades,	
			double minVolF,
			double comm,
			int debug,
			boolean printSummary,
			int returnMode
			){
		//
				
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int lastDayVol = -1;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			
			//modulo de entrada
			if (positions.size()<maxTrades
					&& h>=h1 && h<=h2
					){					
											
				if (day!=lastDay){
					if (lastDay!=-1){
						lastDayVol = lastDay;						
					}
					lastDay = day;
				}
				
				int maxMin = maxMins.get(i-1);
				double volTotal = 0;
				double avgVol = 0;
				if (lastDayVol!=-1){
					//System.out.println("day.. "+lastDayVol+" "+q1.getLow5()+" "+q1.getHigh5());
					volTotal = getVolTotal(volumesDaily,lastDayVol,q1.getLow5(),q1.getHigh5());
					avgVol = getAvgVol(volumesDaily,lastDayVol);
				}
				double pipValue = 1.0;
				if (maxMin>=thr
					&& volTotal>=avgVol*minVolF
						){
					PositionCore pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()-10*tp);
					pos.setSl(q.getOpen5()+10*sl);
					pos.setEntryIndex(i);
					pos.setPositionType(PositionType.SHORT);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					//pipValue
					pos.setPipValue(pipValue);										
					positions.add(pos);
					
					if (debug==2){
						System.out.println("volTotal : "+volTotal);
					}
				}else if (maxMin<=-thr
						&& volTotal>=avgVol*minVolF
						){
					PositionCore pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()+10*tp);
					pos.setSl(q.getOpen5()-10*sl);
					pos.setEntryIndex(i);
					pos.setPositionType(PositionType.LONG);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					//pipValue
					pos.setPipValue(pipValue);
					positions.add(pos);
					
					if (debug==2){
						System.out.println("volTotal : "+volTotal);
					}
				}				
			}
						
			//evaluacion trades			
			int j = 0;			
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				if (pos.getPositionType()==PositionType.SHORT){					
					if (q.getHigh5()>=pos.getSl()){
						isClosed = true;
					}else if (q.getLow5()<=pos.getTp()){
						isClosed = true;
					}					
					if (isClosed){
						pips = pos.getEntry()-q.getClose5();
					}
				}else if (pos.getPositionType()==PositionType.LONG){					
					if (q.getLow5()<=pos.getSl()){
						isClosed = true;
					}else if (q.getHigh5()>=pos.getTp()){
						isClosed = true;
					}					
					if (isClosed){
						pips = q.getClose5()-pos.getEntry();
					}
				}
				
				if (isClosed){
					pips-=comm*10;
					if (pips>=0){
						wins++;
						winPips+=pips;
					}else{
						losses++;
						lostPips+=-pips;
					}
												
					if (debug==1){
						System.out.println("[CLOSED] "+DateUtils.datePrint(cal)+" || "+PrintUtils.Print2dec(pips, false)+" || "+pos.toString());
					}
					
					positions.remove(j);
				}else{
					j++;
				}
			}
		}//data
		
				
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
						
		if (printSummary)
		System.out.println(
				header
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "				
				);
					
		return 0; 
	}


	
	private static double getVolTotal(ArrayList<ArrayList<Double>> volumesDaily, int day, int min, int max) {
		
		
		min = min/10;
		max = max/10;
		
		double total = 0;
		for (int p=min;p<=max;p++){
			if (volumesDaily.get(p).size()>0){
				try{
					double vol = volumesDaily.get(p).get(day);
					total+=vol;
				}catch(Exception e){
					System.out.println("[ERROR getVolTotal] "+p+" "+volumesDaily.get(p).size());
				}
			}
		}
		
		return total;
	}
	
	private static double getAvgVol(ArrayList<ArrayList<Double>> volumesDaily, int day) {
						
		double total = 0;
		int count = 0;
		for (int p=0;p<=volumesDaily.size()-1;p++){
			if (volumesDaily.get(p).size()>0){
				try{
					double vol = volumesDaily.get(p).get(day);
					if (vol>0){
						total+=vol;
						count++;
					}
				}catch(Exception e){
					System.out.println("[ERROR getVolTotal] "+p+" "+volumesDaily.get(p).size());
				}
			}
		}
		
		return total/count;
	}
	
private static ArrayList<ArrayList<Double>> loadVolume(String fileName) {
		
		
		ArrayList<ArrayList<Double>> volumes = new ArrayList<ArrayList<Double>>();
		for (int i=0;i<20000;i++) volumes.add(new ArrayList<Double>());
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    Calendar cal = Calendar.getInstance();
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String line;
	        int i=0;
	        QuoteShort lastQ = null;
	        while((line=br.readLine())!=null){
	        	String[] values = line.split(";");
	        	int price = Integer.valueOf(values[0]);
	        	ArrayList<Double> valuesD = volumes.get(price);//obtenemos el array para ese precio
	        	for (int v=1;v<=values.length-1;v++){
	        		valuesD.add(Double.valueOf(values[v]));//añadimos al array
	        	}
	        	
	        	i++;
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	   }	
		
		return  volumes;
	}


	public static void evaluateWithTick(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,ArrayList<TickQuote> dataTick,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl
			){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		Calendar cal4 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			int maxMin = maxMins.get(i);
			
			int mode = 0;
			int win = 0;
			if (h>=h1 && h<=h2){
				if (maxMin>=thr){
					mode = -1;
					//buscamos el ultimos high
					int index = i-maxMin-1;
					//valor a evaluar
					int value = data.get(index).getHigh5();
					//System.out.println("actual y last: "+i +" "+(index)+" || "+q.getHigh5()+" "+data.get(index).getHigh5()+" "+data.get(index-1).getHigh5());
					cal3.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),0, 0, 0);
					cal4.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),23, 59, 59);
					win = DataUtils.evaluateOrder(dataTick, cal3, cal4, value, tp, sl, -1,false);
				}else if (maxMin<=-thr){
					mode = 1;
					//buscamos el ultimos loe
					int index = i-Math.abs(maxMin)-1;
					//valor a evaluar
					int value = data.get(index).getLow5();
					cal3.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),0, 0, 0);
					cal4.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),23, 59, 59);					
					win = DataUtils.evaluateOrder(dataTick, cal3, cal4, value, tp, sl, 1,false);
				}
				
				if (mode!=0){
					if (win==1) wins++;
					if (win==-1) losses++;
				}								
			}		
		}
		
		int trades = wins+losses;
		
		double winPer = wins*100.0/trades;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		
		System.out.println(
				tp+" "+sl
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.12.02.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.12.02.csv";
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
			
			
			//for (int p=0;p<volumesDaily.size();p++){
				//System.out.println(p+" "+volumesDaily.get(p).size());
			//}
			//System.out.println("total vol data: "+volumesDaily.size());
			
			/*int factor = 1;
			for (int y1=2003;y1<=2003;y1++){
				int y2 = y1+13;
				//String volumePath = "C:\\fxdata\\volumes"+y1+".csv";
				//ArrayList<ArrayList<Double>>  volumesDaily = loadVolume(volumePath);
				for (int h1=0;h1<=9;h1++){
					int h2 = h1+0;
					for (int tp=5;tp<=90;tp+=5){
						for (int sl=45;sl<=45;sl+=5){
							for (int thr=500*factor;thr<=500*factor;thr+=100*factor){
								for (int maxTrades=5;maxTrades<=5;maxTrades++){
									for (double minVolF=0.0;minVolF<=0.0;minVolF+=5.0){
										for (int maxPipsDiff=20;maxPipsDiff<=20;maxPipsDiff++){
											for (int maxReactionAllowed=0;maxReactionAllowed<=0;maxReactionAllowed+=1){
												String header = y1+" "+y2+" "+h1+" "+h2+" "+tp+" "+sl+" "+thr
														+" "+PrintUtils.Print2dec(minVolF, false)+" "+maxPipsDiff+" "+maxReactionAllowed
														;
												//MeanReversionVolumes.doTest(header, data, maxMins, y1, y2, h1, h2, thr, tp, sl, maxTrades, 0.0, false,true,0);
												//MeanReversionVolumes.doTest2(header, data, maxMins,volumesDaily, y1, y2, h1, h2, thr, tp, sl, maxTrades,minVolF, 0.0, 0,true,0);
												//MeanReversionVolumes.doTest3(header, data, maxMins, y1, y2, h1, h2, thr, tp, sl, 
														//maxPipsDiff,maxReactionAllowed,maxTrades, 0.0, false,true,0);
											}
										}
									}
								}
							}
						}
					}
				}
			}*/
			
			String fileName = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.01.01_2016.06.30.csv";
			ArrayList<TickQuote> dataTick = DataUtils.retrieveTickQuotes(fileName,1);
			DataUtils.adjustTimeZone(dataTick);
			System.out.println("ticks: "+data.size()+" "+fileName);	
			for (int thr=50;thr<=100;thr+=10){
				MeanReversionVolumes.evaluateWithTick(data, maxMins, dataTick, 2016, 2016, 0, 0, thr,5,30);
			}
						
		}

	}

	

}
