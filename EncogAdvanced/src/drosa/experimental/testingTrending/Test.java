package drosa.experimental.testingTrending;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.billyt.TestDailyBreakout2;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Test {

	public static void calculateDiff(String header,ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int year1,int year2,
			int thr,int maxPeriod){
		
		ArrayList<Double> tradingPips = new ArrayList<Double>();
		double avgDiffC = 0;
		double avgDiff = 0;
		int total=0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-maxPeriod-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int year = cal.get(Calendar.YEAR);
			if (year<year1|| year>year2) continue;
			
			int maxMin = maxMins.get(i).getExtra();
			
			if (maxMin>=thr){
				QuoteShort qn = TradingUtils.getMaxMinShort(data, i+1, i+maxPeriod);
				int diffC = data.get(i+maxPeriod).getClose5()-q1.getOpen5();
				int diffH = qn.getHigh5()-q1.getOpen5();
				int diffL = q1.getOpen5()-qn.getLow5();
				int diff = (diffH-diffL);
				avgDiff+=diff;
				avgDiffC+=diffC;
				total++;
				tradingPips.add(diffC*0.1);
			}else if (maxMin<=-thr){//low
				QuoteShort qn = TradingUtils.getMaxMinShort(data, i+1, i+maxPeriod);
				int diffC = q1.getOpen5()-data.get(i+maxPeriod).getClose5();
				int diffH = qn.getHigh5()-q1.getOpen5();
				int diffL = q1.getOpen5()-qn.getLow5();
				int diff = (diffL-diffH);
				avgDiff+=diff;
				avgDiffC+=diffC;
				total++;
				tradingPips.add(diffC*0.1);
			}
		}
		
		System.out.println(
				header
				+" "+thr+" "+maxPeriod
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(avgDiff*0.1/total, false)
				+" "+PrintUtils.Print2dec(avgDiffC*0.1/total, false)
				);
		/*for (int i=0;i<tradingPips.size();i++){
			double pips = tradingPips.get(i);
			System.out.println(PrintUtils.Print2dec(pips, true));
		}*/
	}
	
	public static double calculateDiffSL(String header,ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int year1,int year2,
			int thr,int slPeriod,int maxPeriod,double comm,boolean debug,boolean print){
		
		int maxLosses = 0;
		int actualLosses = 0;
		int wins = 0;
		int losses = 0;
		double winPips = 0;
		double lostPips = 0;
		ArrayList<Double> tradingPips = new ArrayList<Double>();
		double avgDiffC = 0;
		double avgDiff = 0;
		double avgStop = 0;
		int total=0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-maxPeriod-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int year = cal.get(Calendar.YEAR);
			if (year<year1|| year>year2) continue;
			
			int maxMin = maxMins.get(i).getExtra();
			
			if (maxMin>=thr){
				QuoteShort qnSL = TradingUtils.getMaxMinShort(data, i-slPeriod, i);
				QuoteShort qn = TradingUtils.getMaxMinShort(data, i+1, i+maxPeriod);
				int sl = qnSL.getLow5();
				double pipsSL = Math.abs((q1.getOpen5()-sl)*0.1);
				avgStop += pipsSL;
				int diffC = (int) (data.get(i+maxPeriod).getClose5()-q1.getOpen5()-comm*10.0);
				if (qn.getLow5()<=sl){
					losses++;
					lostPips+=pipsSL+comm;
					
					actualLosses++;
					if (actualLosses>maxLosses) maxLosses++;
				}else if (diffC>=0){
					wins++;
					winPips+=diffC*0.1;
					if (debug){
						System.out.println("[WIN] "+DateUtils.datePrint(cal)+" "+diffC);
					}
					actualLosses = 0;
				}else if (diffC<0){
					losses++;
					lostPips+=-diffC*0.1;
					
					actualLosses++;
					if (actualLosses>maxLosses) maxLosses++;
				}
				
				int diffH = qn.getHigh5()-q1.getOpen5();
				int diffL = q1.getOpen5()-qn.getLow5();
				int diff = (diffH-diffL);
				avgDiff+=diff;
				avgDiffC+=diffC;
				total++;
				tradingPips.add(diffC*0.1);
			}else if (maxMin<=-thr){//low
				QuoteShort qnSL = TradingUtils.getMaxMinShort(data, i-slPeriod, i);
				QuoteShort qn = TradingUtils.getMaxMinShort(data, i+1, i+maxPeriod);
				int sl = qnSL.getHigh5();
				double pipsSL = Math.abs((q1.getOpen5()-sl)*0.1);
				avgStop += pipsSL;
				int diffC = (int) (q1.getOpen5()-data.get(i+maxPeriod).getClose5()-comm*10.0);
				if (qn.getHigh5()>=sl){
					losses++;
					lostPips+=pipsSL+comm;
				}else if (diffC>=0){
					wins++;
					winPips+=diffC*0.1;
					if (debug){
						System.out.println("[WIN] "+DateUtils.datePrint(cal)+" "+diffC);
					}
				}else if (diffC<0){
					losses++;
					lostPips+=-diffC*0.1;
				}
				
				int diffH = qn.getHigh5()-q1.getOpen5();
				int diffL = q1.getOpen5()-qn.getLow5();
				int diff = (diffL-diffH);
				avgDiff+=diff;
				avgDiffC+=diffC;
				total++;
				tradingPips.add(diffC*0.1);
			}
		}
		
		double winPer = wins*100.0/total;
		double pf = winPips/lostPips;
		double avgPips = (winPips-lostPips)/total;
		double winRatio = (winPips/wins)/(lostPips/losses);
		
		if (print)
		System.out.println(
				header
				+" "+thr+" "+maxPeriod+" "+slPeriod
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(avgDiff*0.1/total, false)
				+" "+PrintUtils.Print2dec(avgDiffC*0.1/total, false)
				+" "+PrintUtils.Print2dec(avgStop/total, false)
				+" || "
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winRatio, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+maxLosses+" "+PrintUtils.Print2dec((lostPips*maxLosses)/losses, false)
				+" || "+PrintUtils.Print2dec(avgPips/(avgStop/total), false)
				);
		/*for (int i=0;i<tradingPips.size();i++){
			double pips = tradingPips.get(i);
			System.out.println(PrintUtils.Print2dec(pips, true));
		}*/
		
		return avgPips;
	}
	
	public static double calculateDiffSLBE(String header,ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int year1,int year2,
			int thr,int slPeriod,int maxPeriod,double comm,boolean debug,boolean print){
		
		double pipsAcumm = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int wins = 0;
		int losses = 0;
		int bes=0;
		double winPips = 0;
		double lostPips = 0;
		ArrayList<Double> tradingPips = new ArrayList<Double>();
		double avgDiffC = 0;
		double avgDiff = 0;
		double avgStop = 0;
		double avgStopBE = 0;
		int totalBE = 0;
		int total=0;
		int lastDay = -1;
		boolean canTrade = true;
		Calendar cal = Calendar.getInstance();
		
		int i = 0;
		while (i<data.size()-maxPeriod-1){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int year = cal.get(Calendar.YEAR);
			if (year<year1|| year>year2){
				i++;
				continue;
			}
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				canTrade=true;
				lastDay = day;
			}
			if (!canTrade){
				i++;
				continue;
			}
			int maxMin = maxMins.get(i).getExtra();
			
			int win = -2;
			int indexSL = -1;
			int indexBE = -1;
			int indexBEvalue = -1;
			double gainedPips = 0;
			double pipsSL = 0;
			String codOP = "";
			if (maxMin>=thr){
				codOP = "BUY";
				gainedPips = 0;
				//canTrade=false;
				QuoteShort qnSL = TradingUtils.getMaxMinShort(data, i-slPeriod, i);
				int sl = qnSL.getLow5();
				pipsSL = Math.abs((q1.getOpen5()-sl)*0.1);
				int be = (int) (q1.getOpen5()+pipsSL*10);
				int beValue = (int) (q1.getOpen5()+comm*10);
				
				indexSL = TradingUtils.getMaxMinIndex(data, i+1, i+maxPeriod, sl, false); //indice donde se toca el SL
				indexBE = TradingUtils.getMaxMinIndex(data, i+1, i+maxPeriod, be, true); //indice donde se toca el BE
				indexBEvalue = TradingUtils.getMaxMinIndex(data, indexBE+1, i+maxPeriod, beValue, false); //indice donde se toca el BE
			}else if (maxMin<=-thr){//low
				codOP = "SELL";
				gainedPips = 0;
				//canTrade=false;
				QuoteShort qnSL = TradingUtils.getMaxMinShort(data, i-slPeriod, i);
				int sl = qnSL.getHigh5();
				pipsSL = Math.abs((q1.getOpen5()-sl)*0.1);
				int be = (int) (q1.getOpen5()-pipsSL*10);
				int beValue = (int)(q1.getOpen5()+comm*10);
				
				indexSL = TradingUtils.getMaxMinIndex(data, i+1, i+maxPeriod, sl, true);
				indexBE = TradingUtils.getMaxMinIndex(data, i+1, i+maxPeriod, be, false); //indice donde se toca el SL
				indexBEvalue = TradingUtils.getMaxMinIndex(data, indexBE+1, i+maxPeriod, beValue, true); //indice donde se toca el BE
			}
			//analisis
			if (indexSL>=0 && (indexBE>=indexSL || indexBE==-1)){//complete loss
				win=-1;
				lostPips+=pipsSL+comm;
				actualLosses++;
				if (actualLosses>maxLosses) maxLosses = actualLosses;
				gainedPips=-pipsSL-comm;
				
				//i=indexSL;
			}
			if (indexBE>=0 && (indexSL>=indexBE || indexSL==-1)){//complete at least BE
				//bes++;
				//i=indexBE; //para coger todas a partir del BE (prueba)
				if (indexBEvalue>=0){//BE
					bes++;
					win = 2;
					gainedPips = 0;
					
					//i=indexBEvalue;
					// no pips
				}else{
					int diffC = (int) (Math.abs(q1.getOpen5()-data.get(i+maxPeriod).getClose5())*0.1-comm);
					
					if (diffC>=0){
						win=1;
						winPips += diffC;
						actualLosses = 0;
						gainedPips = diffC;
					}else{ //al menos BE
						bes++;
						win = 2;
						gainedPips = 0;
					}
					
					//i=i+maxPeriod;
				}
			}
			//agrupacion resultados
			if (win>=-1){
				pipsAcumm += gainedPips;
				if (win==-1){
					if (debug)
						System.out.println("[LOSS "+codOP+"] "+DateUtils.datePrint(cal)+" "+q1.getOpen5()
								+" "+PrintUtils.Print2(gainedPips, false)
								+" "+PrintUtils.Print2(pipsAcumm, false)
								+" || "+actualLosses
								);
					losses++;
				}else if (win==1){
					if (debug)
						System.out.println("[WIN "+codOP+"] "+DateUtils.datePrint(cal)+" "+q1.getOpen5()
								+" "+PrintUtils.Print2(gainedPips, false)
								+" "+PrintUtils.Print2(pipsAcumm, false)
								);
					wins++;
				}else if (win==2){
					if (debug){
						System.out.println("[BE "+codOP+"] "+DateUtils.datePrint(cal)+" "+q1.getOpen5()
								+" "+PrintUtils.Print2(gainedPips, false)
								+" "+PrintUtils.Print2(pipsAcumm, false)
								);
					}
				}
				avgStop+=pipsSL;
				//System.out.println(indexSL+" "+indexBE+" "+indexBEvalue+" || "+win);
				total++;
			}else{//no han habido operaciones
				//i++;
			}
			i++;
		}
		
	
		double lossPer = losses*100.0/total;
		double bePer = bes*100.0/total;
		double winPer = 100.0-lossPer-bePer;
		double pf = winPips/lostPips;
		double avgPips = (winPips-lostPips)/(wins+losses);
		double winRatio = (winPips/wins)/(lostPips/losses);
		double avgLoss = lostPips/losses;
		double avgWin = winPips/wins;
		double R = avgWin/avgLoss;
		double kelly = winPer*0.01-(lossPer*0.01/R);
		/*double bePer = bes*100.0/total;
		
		double avgPips = (winPips-lostPips)/total;
		double winRatio = (winPips/wins)/(lostPips/losses);*/
		
		if (print)
		System.out.println(
				header
				+" "+thr+" "+maxPeriod+" "+slPeriod
				+" || "
				+" "+total
				//+" "+PrintUtils.Print2dec(avgDiffC*0.1/total, false)
				//+" "+PrintUtils.Print2dec(avgStop/total, false)
				//+" "+PrintUtils.Print2dec(avgStopBE/totalBE, false)
				+" || "
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(bePer, false)
				+" "+PrintUtils.Print2dec(lossPer, false)
				+" || "+PrintUtils.Print2dec(winRatio, false)
				+" "+PrintUtils.Print2dec(avgWin, false)
				+" "+PrintUtils.Print2dec(avgLoss, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(pf, false)
				
				+" || "+maxLosses+" "+PrintUtils.Print2dec((lostPips*maxLosses)/losses, false)
				//+" "+PrintUtils.Print2dec(kelly, false)
				+" || "+PrintUtils.Print2dec((winPips-lostPips)/avgStop, false)
				+"  "+PrintUtils.Print2dec(avgWin/(avgStop/total), false)
				+"  "+PrintUtils.Print2dec(avgLoss/(avgStop/total), false)
				);
		return pf;
	}
	
	public static void calculateFirsStrikeSL(String header,ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			int year1,int year2,int h1,int h2,
			int thr,int slPeriod,int maxPeriod,double comm){
		
		int wins = 0;
		int losses = 0;
		double winPips = 0;
		double lostPips = 0;
		ArrayList<Double> tradingPips = new ArrayList<Double>();
		double avgDiffC = 0;
		double avgDiff = 0;
		int total=0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-maxPeriod-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int year = cal.get(Calendar.YEAR);
			if (year<year1|| year>year2) continue;
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			if (dayWeek!=Calendar.MONDAY) continue;
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h<h1 || h>h2) continue;
			
			int maxMin = maxMins.get(i).getExtra();
			
			if (maxMin>=thr){
				QuoteShort qnSL = TradingUtils.getMaxMinShort(data, i-slPeriod, i);
				QuoteShort qn = TradingUtils.getMaxMinShort(data, i+1, i+maxPeriod);
				int sl = qnSL.getLow5();
				double pipsSL = Math.abs((q1.getOpen5()-sl)*0.1);
				
				int diffC = (int) (data.get(i+maxPeriod).getClose5()-q1.getOpen5()-comm*10.0);
				if (qn.getLow5()<=sl){
					losses++;
					lostPips+=pipsSL+comm;
				}else if (diffC>=0){
					wins++;
					winPips+=diffC*0.1;
				}else if (diffC<0){
					losses++;
					lostPips+=-diffC*0.1;
				}
				
				int diffH = qn.getHigh5()-q1.getOpen5();
				int diffL = q1.getOpen5()-qn.getLow5();
				int diff = (diffH-diffL);
				avgDiff+=diff;
				avgDiffC+=diffC;
				total++;
				tradingPips.add(diffC*0.1);
			}else if (maxMin<=-thr){//low
				QuoteShort qnSL = TradingUtils.getMaxMinShort(data, i-slPeriod, i);
				QuoteShort qn = TradingUtils.getMaxMinShort(data, i+1, i+maxPeriod);
				int sl = qnSL.getHigh5();
				double pipsSL = Math.abs((q1.getOpen5()-sl)*0.1);
				
				int diffC = (int) (q1.getOpen5()-data.get(i+maxPeriod).getClose5()-comm*10.0);
				if (qn.getHigh5()>=sl){
					losses++;
					lostPips+=pipsSL+comm;
				}else if (diffC>=0){
					wins++;
					winPips+=diffC*0.1;
				}else if (diffC<0){
					losses++;
					lostPips+=-diffC*0.1;
				}
				
				int diffH = qn.getHigh5()-q1.getOpen5();
				int diffL = q1.getOpen5()-qn.getLow5();
				int diff = (diffL-diffH);
				avgDiff+=diff;
				avgDiffC+=diffC;
				total++;
				tradingPips.add(diffC*0.1);
			}
		}
		
		double winPer = wins*100.0/total;
		double pf = winPips/lostPips;
		double avgPips = (winPips-lostPips)/total;
		double winRatio = (winPips/wins)/(lostPips/losses);
		
		System.out.println(
				header
				+" "+thr+" "+maxPeriod+" "+slPeriod
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(avgDiff*0.1/total, false)
				+" "+PrintUtils.Print2dec(avgDiffC*0.1/total, false)
				+" || "
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winRatio, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		/*for (int i=0;i<tradingPips.size();i++){
			double pips = tradingPips.get(i);
			System.out.println(PrintUtils.Print2dec(pips, true));
		}*/
	}
	
	public static void main(String[] args) {
		
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_30 Mins_Bid_2003.12.31_2015.09.17.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathEURJPY);
		paths.add(pathGBPUSD);paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		//limit = 0;
		int thr = 3400;//fijado para 30m
		int maxPeriodSL = 40;//fijado para 30min
		int maxPeriod = 4700;//fijado para 30 min
		for (int year1=2004;year1<=2015;year1++){
		//for (maxPeriod=10;maxPeriod<=300;maxPeriod+=10){
		//for (maxPeriodSL=10;maxPeriodSL<=300;maxPeriodSL+=10){
			pfs.clear();
			for (int i = 0;i<=limit;i++){
				String path = paths.get(i);			
				ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
				ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
				//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
				ArrayList<QuoteShort> data = null;
				dataI.clear();
				dataS.clear();
				data5m.clear();
				data = data5mS;
			
				String header=path.substring(10, 16);
				ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
					//for (int year1=2004;year1<=2004;year1+=1){
						int year2=year1+0;
						//year2=2015;
						for (thr=6000;thr<=6000;thr+=1){
							for (maxPeriod=110;maxPeriod<=110;maxPeriod+=100){
								//Test.calculateDiff(header,data, maxMins,year1,year2, thr, maxPeriod);
								for (maxPeriodSL=70;maxPeriodSL<=70;maxPeriodSL+=10){
									double pips =  Test.calculateDiffSL(header,data, maxMins,year1,year2, thr, maxPeriodSL,maxPeriod,5.0,false,false);
									//double pf = Test.calculateDiffSLBE(header,data, maxMins,year1,year2, thr, maxPeriodSL,maxPeriod,5.0,false,true);
									//double pips = stats.split(" ");
									//double pips = values[0];
									pfs.add(pips);
								}
							}
						}
					//}
			}//currencies
			String header =thr+" "+maxPeriod+" "+maxPeriodSL; 
			MathUtils.summary_mean_sd(header, pfs);
		}//periodSL
	}
}
