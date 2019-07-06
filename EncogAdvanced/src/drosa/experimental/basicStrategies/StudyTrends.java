package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.zznbrum.TrendClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyTrends {
	
	public static double doTest2(
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int trendSize,
			double factorTarget,
			double maxAccLoss,
			int maxStages,
			int debug
			){
	
		int wins = 0;
		int losses = 0;
		double winsfactor = 0.0;
		double lossfactor = 0.0;
		double maxRealLoss = 0.0;
		double maxSize = 1.0;
		Calendar cal = Calendar.getInstance();
		int actualLosses = 0;
		int maxLosses = 0;
		
		int mode = 0;
		int index1=0;
		int index2=0;
		int index3=0;
		
		int currentTradeMode = 0;
		int currentTrendId = 0;
		int entry = 0;
		int slvalue = 0;
		int valueTP = 0;
		int valueSL = 0;
		double actualSize = 0;
		double accLoss = 0;
		double maxAllowedLoss = maxAccLoss;
		
		int maxStage = 0;
		int trendId = 1;
		int currentStage = 0;
		int trendOpenIdx = -1;
		int maxTrends10=0;
		int actualTrends10=0;
		int totalDays=0;
		int totalDays15=0;
		int totalDays20=0;
		int totalDays25=0;
		int totalDays30=0;
		int lastDay = -1;
		int count10=0;
		int count15=0;
		int count20=0;
		int count25=0;
		int count30=0;
		int lastDay_2_20=-1;
		int lastDay20=-1;
		int lastCount = 0;
		int countDays20Followed = 0;
		int countDays20FollowedTotal = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
					if (count15>0) totalDays15++;
					if (count20>0) totalDays20++;
					if (count25>0) totalDays25++;
					if (count30>0) totalDays30++;
					
					if (count20==0 && lastDay20==0){
						countDays20Followed++;
						countDays20FollowedTotal += count20+lastDay20;
					}
					
					lastDay_2_20 = lastDay20 ;
					lastDay20 = count20;
					totalDays++;
				}
				
				count10=0;
				count15=0;
				count20=0;
				count25=0;
				count30=0;
				lastDay = day;
			}
			
			int diffHL = q.getHigh5()-data.get(index1).getLow5();
			int diffLH = data.get(index1).getLow5()-q.getLow5();
			int diffHLNew = data.get(index2).getHigh5()-q.getLow5();
			int diffLHNew = q.getHigh5()-data.get(index2).getLow5();
			
			if (mode==0) {
				if (diffHL>=trendSize) {
					mode = 1;
					index2 = i;
					trendId = 1;
					trendOpenIdx = i;
					if (debug==2)
					System.out.println("[TREND UP++] "
							+" "+data.get(index1).getLow5()
							+" "+data.get(index2).getHigh5()
					);
				}else if (diffLH>=trendSize) {
					mode = -1;
					index2 = i;
					trendId = 1;
					trendOpenIdx = i;
					if (debug==2)
					System.out.println("[TREND DOWN++] "
							+" "+data.get(index1).getHigh5()
							+" "+data.get(index2).getLow5()
					);
				}
			}else if (mode==1) {
				if (q.getHigh5()>data.get(index2).getHigh5()) {
					index2 = i;
					if (debug==2)
						System.out.println("[TREND UP++] "
								+" "+data.get(index1).getLow5()
								+" "+data.get(index2).getHigh5()
								+" || "+q.toString()
						);
				}else if (diffHLNew>=trendSize) {
					//cambio de trend UP a DOWN
					double f = (data.get(index2).getHigh5()-data.get(index1).getLow5())*1.0/trendSize;
					
					if (f>=1.5) count15++;
					if (f>=2.0) count20++;
					if (f>=2.5) count25++;
					if (f>=3.0) count30++;
					
					if (f<2.0) {
						actualTrends10++;
						if (actualTrends10>=maxTrends10) maxTrends10 = actualTrends10;
					}else {
						actualTrends10 = 0;
					}
					
					index1 = index2;
					index2 = i;
					trendOpenIdx = i;
					mode = -1;
					trendId++;
					if (debug==2)
					System.out.println("[TREND DOWN] "
							+" "+data.get(index1).getHigh5()
							+" "+data.get(index2).getLow5()
							+" || "+q.toString()
					);
				}
			}else if (mode==-1) {
				if (q.getLow5()<data.get(index2).getLow5()) {
					index2 = i;
					if (debug==2)
						System.out.println("[TREND DOWN++] "
								+" "+data.get(index1).getHigh5()
								+" "+data.get(index2).getLow5()
								+" || "+q.toString()
						);
				}else if (diffLHNew>=trendSize) {
					//cambio de trend de DOWN a UP
					double f = (data.get(index1).getHigh5()-data.get(index2).getLow5())*1.0/trendSize;
					
					if (f>=1.5) count15++;
					if (f>=2.0) count20++;
					if (f>=2.5) count25++;
					if (f>=3.0) count30++;
					
					if (f<2.0) {
						actualTrends10++;
						if (actualTrends10>=maxTrends10) maxTrends10 = actualTrends10;
					}else {
						actualTrends10 = 0;
					}
						
					index1 = index2;
					index2 = i;
					trendOpenIdx = i;
					mode = 1;
					trendId++;
					if (debug==2)
					System.out.println("[TREND UP] "
							+" "+data.get(index1).getLow5()
							+" "+data.get(index2).getHigh5()
							+" || "+q.toString()
					);
				}
			}
			
			//condiciones de entrada en primer luegar
			//vamos a suponer que vamos a poder entrar en zona precisa, (slippage..)
			if (currentTradeMode==0 
					&& h>=h1 && h<=h2
					&& y>=y1 && y<=y2
					&& trendId>currentTrendId
					&& i==trendOpenIdx
					){
				if (mode==1) {
					//se supone que el size sera mayor de 1.0..
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					entry = data.get(index1).getLow5()+trendSize;
					valueTP = (int) (entry + trendSize*factorTarget);
					currentTrendId = trendId;
					currentTradeMode=1;
					currentStage = 1;
					actualSize = 1.0;
					if (debug==2)
						System.out.println("[NEW LONG 0] "+entry+" "+valueTP
								+" || "+q.toString());
					accLoss=0;
				}else if (mode==-1) {
					entry = data.get(index1).getHigh5()-trendSize;
					valueTP = (int) (entry - trendSize*factorTarget);
					currentTrendId = trendId;
					currentTradeMode=-1;
					currentStage = 1;
					actualSize = 1.0;
					if (debug==2)
						System.out.println("[NEW SHORT 0] "+entry+" "+valueTP
								+" || "+q.toString());
					accLoss=0;
				}
			}
			
			if (currentTradeMode==1) {
				//vemos si pilla TP
				if (q.getHigh5()>=valueTP) {
					wins++;
					winsfactor += actualSize*factorTarget-accLoss;
					if (debug==2)
						System.out.println("[LONG WIN] "+(actualSize*factorTarget-accLoss)
								+" "+winsfactor
								+" || "+q.toString()
								);
					currentTradeMode = 0;
					accLoss=0;
					actualLosses = 0;
					currentStage = 0;
				}else if (mode==-1) {
					//si la trend ha cambiado, en cuanto cambia se cierra la posicion y se abre una
					//nueva en sentido contrario
					int downOpenValue = data.get(index1).getHigh5()-trendSize;
					double lossf = (downOpenValue-entry)*1.0/trendSize;
					accLoss += actualSize*((entry-downOpenValue)*1.0/trendSize);//lo nromal es que este en negativo
					if (debug==2)
						System.out.println("DOWN accLoss actualSize "
							+lossf+" "+accLoss+" "+actualSize
							+" || "+q.toString()
							);
					if (accLoss>=maxAccLoss || currentStage>=maxStages) {
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
						losses++;
						lossfactor += accLoss;
						
						if (debug==2 || debug==3)
							System.out.println("FULL LOSS SHORT "
									+actualLosses +" "+maxLosses+" "+currentStage
									+" "+accLoss
								+" || "+q.toString()
								);
						currentStage = 0;
						accLoss = 0;//para abrir desde el principip con 1.0
						
					}
					
					entry = data.get(index1).getHigh5()-trendSize;
					valueTP = (int) (entry - trendSize*factorTarget);
					currentTrendId = trendId;
					currentTradeMode=-1;
					currentStage++;
					if (currentStage>=maxStage) maxStage=currentStage;
					actualSize = (accLoss+factorTarget)/factorTarget;
					if (debug==2)
						System.out.println("[NEW SHORT] "
								+" stage= "+currentStage
								+" actualLosses= "+actualLosses
								+" "+entry
								+" "+valueTP+" || "+actualSize
								+" || "+q.toString()
								);
				}//
			}//current=1
			
			if (currentTradeMode==-1) {
				//vemos si pilla TP
				if (q.getLow5()<=valueTP) {
					wins++;
					winsfactor += actualSize*factorTarget-accLoss;
					currentTradeMode = 0;
					actualLosses = 0;
					accLoss=0;
					
					if (debug==2)
						System.out.println("[SHORT WIN] "
						+(actualSize*factorTarget-accLoss)+" "+winsfactor
						+" || "+q.toString()
									);
					currentStage = 0;
				}else if (mode==1) {
					//si la trend ha cambiado, en cuanto cambia se cierra la posicion y se abre una
					//nueva en sentido contrario
					int downOpenValue = data.get(index1).getLow5()+trendSize;
					double lossf = (entry-downOpenValue)*1.0/trendSize;
					accLoss += actualSize*((downOpenValue-entry)*1.0/trendSize);//lo nromal es que este en negativo

					if (debug==2)
						System.out.println("UP accLoss actualSize "
							+entry+" "+downOpenValue+" "+lossf
							+" "+accLoss+" "+actualSize
							+" || "+q.toString()
								);
					if (accLoss>=maxAccLoss || currentStage>=maxStages){
						actualLosses++;//fulllosses
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
						losses++;
						lossfactor += accLoss;
						accLoss = 0;//para abrir desde el principip con 1.0
						if (debug==2 || debug==3)
							System.out.println("FULL LOSS LONG "
								+actualLosses +" "+maxLosses+" "+currentStage
								+" || "+q.toString()
								);
						currentStage = 0;// se reinicia
					}
					
					entry = data.get(index1).getLow5()+trendSize;
					valueTP = (int) (entry + trendSize*factorTarget);
					currentTrendId = trendId;
					currentTradeMode=1;
					currentStage++;
					if (currentStage>=maxStage) maxStage=currentStage;
					//actualSize tiene que superar el accLoss y ademas asegurarse el factorTarget
					actualSize = (accLoss+factorTarget)/factorTarget;
					if (debug==2)
						System.out.println("[NEW LONG] "
								+" stage= "+currentStage
								+" actualLosses= "+actualLosses
								+" "+entry
								+" "+valueTP
								+" || "+actualSize
								+" || "+q.toString()
								);
				}
			}
		}//fori
		
		
		int totalSeq = wins+losses;
		double winPer = wins*100.0/totalSeq;
		double pf = winsfactor/lossfactor;
		double avgfactor = (winsfactor-lossfactor)/totalSeq;
		double avgPips = avgfactor*trendSize*0.1;
		
		boolean isPrinted = false;
		if (debug==0
				&& pf>=1.80
				){
			isPrinted = true;
		}
		
		if (debug==5 || debug==3 || debug==2) isPrinted = true;
		
		if (isPrinted)
		System.out.println(
				h1+" "+h2
				+" "+trendSize
				+" "+PrintUtils.Print2dec(factorTarget, false)
				+" "+PrintUtils.Print2dec(maxAccLoss, false)
				+" "+maxStages
				+" || "
				+" "+totalSeq
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avgfactor, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" || "
				+" "+PrintUtils.Print2dec(winsfactor/wins, false)
				+" "+PrintUtils.Print2dec(lossfactor/losses , false)
				+" || "
				//+" "+PrintUtils.Print2dec(maxSize, false)
				//+" "+PrintUtils.Print2dec(maxRealLoss , false)
				+" "+maxLosses
				+" "+maxStage
				+" || "
				+" "+maxTrends10
				);
		
		if (!isPrinted && debug==20) {
			System.out.println(
					trendSize
					+" || "
					+" "+totalDays 
					+" "+PrintUtils.Print2dec(trendId*1.0/totalDays , false)
					+" || "+(totalDays-totalDays15)
					+" "+(totalDays-totalDays20)
					+" "+(totalDays-totalDays25)
					+" "+(totalDays-totalDays30)
					+" || "+countDays20Followed
					+" "+PrintUtils.Print2dec(countDays20FollowedTotal*1.0/countDays20Followed , false)
					+" ||"
					+" "+PrintUtils.Print2dec(totalDays15*100.0/totalDays , false)
					+" "+PrintUtils.Print2dec(totalDays20*100.0/totalDays , false)
					+" "+PrintUtils.Print2dec(totalDays25*100.0/totalDays , false)
					+" "+PrintUtils.Print2dec(totalDays30*100.0/totalDays , false)
					);
		}
		
		return pf;
	}
	
	public static void doTest(ArrayList<TrendClass> trends,
			int h1,int h2,
			int trendSize,
			double factorTarget,
			double maxAccLoss,
			int debug
			){
	
		int wins = 0;
		int losses = 0;
		double winsfactor = 0.0;
		double lossfactor = 0.0;
		double maxRealLoss = 0.0;
		double maxSize = 1.0;
		Calendar cal = Calendar.getInstance();
		int actualLosses = 0;
		int maxLosses = 0;
		for (int i=0;i<trends.size();i++){
			
			TrendClass t = trends.get(i);
			cal.setTimeInMillis(t.getMillisOpen());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h>=h1 && h<=h2){
				
				double actualSize = 1.0;//size a ganar si se gana el factorTarget
				double accLoss = 0.0;
				//entra dentro del horario a tener en cuenta
				for (int j=i;j<trends.size();j++){
					TrendClass tj = trends.get(j);
					double trendFactor = Math.abs(tj.getSize()*1.0/trendSize);

					if (trendFactor>=1.0+factorTarget){
						winsfactor += (factorTarget*actualSize)-accLoss;
						wins++;
						actualLosses=0;
						if (debug==1){
							System.out.println("[WIN] "
								+" "+PrintUtils.Print2dec(trendFactor, false)
								+" || "
								+" "+PrintUtils.Print2dec(actualSize, false)
								+" "+PrintUtils.Print2dec(winsfactor, false)
								+" "+PrintUtils.Print2dec(lossfactor, false)
							);
						}
						break;
					}else{
						if (trendFactor>=2.0){ //al superar 2.0 el retrace llega a mas de 1.0 y reduce accLoss
							double winFactor = (trendFactor-2.0);
							accLoss -= winFactor*actualSize;
							
							if (debug==1){
								System.out.println("[INTERMEDIATE WIN] "
									+" "+PrintUtils.Print2dec(trendFactor, false)
									+" || "
									+" "+PrintUtils.Print2dec(actualSize, false)
									+" "+PrintUtils.Print2dec(winFactor*actualSize, false)
									+" "+PrintUtils.Print2dec(accLoss, false)
									+" "+PrintUtils.Print2dec(winsfactor, false)
									+" "+PrintUtils.Print2dec(lossfactor, false)
								);
							}
						}else{//aumenta accLoss
							double lossFactor = 1.0-(trendFactor-1.0);
							accLoss += lossFactor*actualSize;
							
							if (debug==1){
								System.out.println("[INTERMEDIATE LOSS] "
									+" "+PrintUtils.Print2dec(trendFactor, false)
									+" || "
									+" "+PrintUtils.Print2dec(actualSize, false)
									+" "+PrintUtils.Print2dec(lossFactor*actualSize, false)
									+" "+PrintUtils.Print2dec(accLoss, false)
									+" "+PrintUtils.Print2dec(winsfactor, false)
									+" "+PrintUtils.Print2dec(lossfactor, false)
								);
							}
						}
						
						if (accLoss>=maxRealLoss){
							maxRealLoss = accLoss;
						}
						
						
						if (accLoss>=maxAccLoss){
							lossfactor += accLoss;
							losses++;
							if (debug==1){
								System.out.println("[FULL LOSS] "
									+" "+PrintUtils.Print2dec(trendFactor, false)
									+" || "
									+" "+PrintUtils.Print2dec(accLoss, false)
									+" "+PrintUtils.Print2dec(winsfactor, false)
									+" "+PrintUtils.Print2dec(lossfactor, false)
								);
							}
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							
							break;
						}
					}
					//ajustamos el size
					actualSize = (accLoss + factorTarget)/(factorTarget);
					
					if (actualSize>=maxSize) maxSize = actualSize;
					
				}//forj				
			}//h			
		}//fori
		
		
		int totalSeq = wins+losses;
		double winPer = wins*100.0/totalSeq;
		double pf = winsfactor/lossfactor;
		double avgfactor = (winsfactor-lossfactor)/totalSeq;
		double avgPips = avgfactor*trendSize*0.1;
		
		boolean isPrinted = false;
		if (debug==0
				&& pf>=1.80
				){
			isPrinted = true;
		}
		
		if (debug==5) isPrinted = true;
		
		if (isPrinted)
		System.out.println(
				h1+" "+h2
				+" "+trendSize
				+" "+PrintUtils.Print2dec(factorTarget, false)
				+" "+PrintUtils.Print2dec(maxAccLoss, false)
				+" || "
				+" "+totalSeq+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avgfactor, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" || "
				+" "+PrintUtils.Print2dec(winsfactor/wins, false)
				+" "+PrintUtils.Print2dec(lossfactor/losses , false)
				+" || "
				+" "+PrintUtils.Print2dec(maxSize, false)
				+" "+PrintUtils.Print2dec(maxRealLoss , false)
				+" "+maxLosses
				);
	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
		
		//String pathEURUSD = path0+"usdjpy_UTC_1 Min_Bid_2008.12.31_2017.12.17.csv";
		String pathEURUSD = path0+"usdjpy_UTC_5 Mins_Bid_2003.05.04_2017.12.04.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_30 Secs_Bid_2012.12.31_2017.12.11.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.12.04.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
		//String pathEURUSD = path0+"eurusd_UTC_1 Min_Bid_2013.12.31_2017.11.22.csv";
		//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
		
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
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
			
			ArrayList<Double> trendsIndex = new ArrayList<Double>();
			
			for (int trendSize=200;trendSize<=200;trendSize+=50){
				System.out.println("testing trendSize.."+trendSize);
				ArrayList<TrendClass> trends = TradingUtils.calculateTrendsHL(data, trendSize, trendsIndex);
				
				for (int h1=8;h1<=8;h1++){
					int h2 = h1+3;
					//System.out.println("testing hour.."+h1);
					for (double factorTarget = 1.0;factorTarget<=1.0;factorTarget+=0.5){
						for (double maxAccLoss = 5.0;maxAccLoss<=5.0;maxAccLoss+=5.0){
						//for (int maxStages = 5;maxStages<=40;maxStages+=5){
							//StudyTrends.doTest(trends, h1, h2, trendSize, factorTarget, maxAccLoss,5);
							int count = 0;
							for (int y1=2004;y1<=2017;y1++) {
								int y2 = y1+0;
								double pf = StudyTrends.doTest2(data,y1,y2, h1, h2, 
										trendSize, factorTarget,maxAccLoss,99999,20);
								if (pf>=1.0) {
									count++;
								}
							}
							
							if (count>=5) {
								System.out.println(" "
										+" "+PrintUtils.Print2dec(maxAccLoss,false)
										+" "+PrintUtils.Print2dec(factorTarget,false)
										+" || "+count
										);
							}
						}
					}
				}
			}
			
			//	for (double factortowin = 1.0;factortowin<=1.0;factortowin+=0.5){
			/*Calendar cal = Calendar.getInstance();
					
					for (double factor =1.0;factor<=1.0;factor+=1.0){
						for (double factortowin = 1.0;factortowin<=1.0;factortowin+=0.5){
							for (double factortowin = 1.0;factortowin<=1.0;factortowin+=0.5){
								int count = 0;
								int cases = 0;
								double accFollow = 0;
								int wins = 0;
								int losses = 0;
								double accfactor = 0;
								int countfactor = 0;
								double accfactorW = 0;
								double accfactorL = 0;
								int fullWins = 0;
								ArrayList<Double> sizes = new ArrayList<Double>();
								double entryref = 1.0;
								double accVuelta = 0;
								double accPeak = 0;
								int wins2=0;
								int actualLosses = 0;
								int maxLosses = 0;
								double winsf = 0.0;
								double lossesf=0.0;
								double accVueltaLoss = 0.0;
								int wins3=0;
								int losses3=0;
								int debug = 0;
								ArrayList<Integer> lossesArr = new ArrayList<Integer>();
								for (int j=0;j<trends.size()-50;j++){
									double size = Math.abs(trends.get(j).getSize()*1.0/trendSize);
									double size1 = Math.abs(trends.get(j+1).getSize()*1.0/trendSize);
									int sizeClose = trends.get(j).getSizeClose();
									cal.setTimeInMillis(trends.get(j).getMillisOpen());
									int ht1 = cal.get(Calendar.HOUR_OF_DAY);
									cal.setTimeInMillis(trends.get(j+1).getMillisIndex1());
									int ht2 = cal.get(Calendar.HOUR_OF_DAY);
									if (size>=factor
											&& (ht1>=h1 || h1<=ht2)
											
											//&& ht2>=10
											){
										countfactor++;
										accPeak += size;
										if (size>=factor+factortowin) {
											wins2++;
											lossesArr.add(1);
											//lossesArr.add(-actualLosses);
											actualLosses = 0;
											winsf+=factortowin;
											wins3++;
										}else {
											lossesArr.add(-1);
											actualLosses++;
											if (actualLosses>=maxLosses) maxLosses=actualLosses;
											
											accfactorL+=size;
											losses++;
											
											double vueltaLoss = 1.0-(size-1.0);		
											accVueltaLoss += vueltaLoss;
											//lossesf += vueltaLoss;
											if (size>=2.0){
												winsf+=size-2.0;
												wins3++;
											}else{
												lossesf += vueltaLoss;
												losses3++;
											}
										}
										//System.out.println(sizeClose+" || "+trends.get(j).getSize());
									}
								}
								
								double avgVuelta = accfactorL/losses;
								double avgVueltaLoss = accVueltaLoss/losses;
								double accFactor = avgVueltaLoss/factortowin;
								for (int loss=7;loss<=7;loss++) {
									int c = 0;
									int seqwins=0;
									int seqloss=0;
									int actualseqloss = 0;
									double accLoss = TradingUtils.calculateAccLossTry(loss, accFactor, 0.0);
									for (int m=0;m<lossesArr.size();m++) {
										int l = lossesArr.get(m);
										if (l==1) {
											seqwins++;
											actualseqloss = 0;
										}else if (l==-1) {
											actualseqloss++;
											if (actualseqloss==loss) {
												seqloss++;
												actualseqloss = 0;//reiniciamos
											}
										}
									}
									
									int totalt = seqwins+seqloss;
									double avgTrade = (accfactorW-accfactorL)/totalt;
									double avgLoss = MathUtils.average(lossesArr);
									double dt = Math.sqrt(MathUtils.variance(lossesArr));
									double pf = seqwins/(accLoss*seqloss);
									
									double avgTransaction = (seqwins-accLoss*seqloss)/totalt;
									double avgPip = avgTransaction*trendSize*0.1;
									System.out.println(trendSize
											//+" "+PrintUtils.Print2dec(avgTest, false)
											+" "+h1
											+" "+PrintUtils.Print2dec(factortowin, false)
											+" "+loss
											+" || "+countfactor
											+" "+PrintUtils.Print2dec(accPeak*1.0/countfactor, false)
											+" "+wins2
											+" "+PrintUtils.Print2dec(wins2*100.0/countfactor, false)
											//+" || "+PrintUtils.Print2dec(avgLoss, false)
											//+" "+PrintUtils.Print2dec(dt, false)
											+" ||wl "+seqwins+" "+seqloss
											+" "+PrintUtils.Print2dec(seqloss*100.0/(seqwins+seqloss), false)
											+" "+PrintUtils.Print2dec(dt, false)
											+" "+PrintUtils.Print2dec(accfactorL/losses, false)
											+" "+PrintUtils.Print2dec(accLoss, false)
											+" || "+" "+PrintUtils.Print2dec(pf, false)
											+" "+PrintUtils.Print2dec((seqwins-accLoss*seqloss)/totalt, false)
											+" "+PrintUtils.Print2dec(avgTransaction, false)
											+" "+PrintUtils.Print2dec(avgPip, false)
											+" || "+PrintUtils.Print2dec(avgVueltaLoss, false)
											//+" || "+" "+PrintUtils.Print2dec(winsf/lossesf, false)
											//+" || "
											//+" "+(wins3+losses3)
											//+" "+PrintUtils.Print2dec(wins3*100.0/(wins3+losses3), false)
											//+" "+PrintUtils.Print2dec((winsf*wins3)/(lossesf*losses3), false)
											//+" "+PrintUtils.Print2dec(winsf/wins3, false)
											//+" "+PrintUtils.Print2dec(lossesf/losses3, false)
											//+" "+PrintUtils.Print2dec(winsf-lossesf, false)
											);
								}
							}//loss
						}//h
					}//factortowin
				}//factor*/
		}

	}

}
