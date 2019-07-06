package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyHours {

	public static int doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int minRange,int maxRange,
			int thr,
			int debug) {
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winPips = 0;
		int lostPips = 0;
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int lastMonth = -1;
		int totalMonths = 0;
		int winMonths = 0;
		int winsM = 0;
		int lossM = 0;
		int countMonths = 0;
		
		int mode = 0;
		int hmax=-1;
		int hmin=-1;
		int modeRef=0;
		int high=-1;
		int low=-1;
		int cases = 0;	
		int totalDays=0;
		int ref = -1;
		int maxLosses = 0;
		int conLosses = 0;
		int conWins=0;
		int accWin = 0;
		int accLoss = 0;
		int href2=-1;
		int modeEnd=0;
		ArrayList<Integer> rachasWins = new ArrayList<Integer>();
		ArrayList<Integer> rachasLoss = new ArrayList<Integer>();
		
		int accP= 0;
		int accN = 0;
		int lossValue=0;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=day){
					
					int res=0;
					//int modeEnd = 1;//ha terminado en maximo
					if (Math.abs(modeEnd)!=3){
						if (Math.abs(modeEnd)!=2){
							modeEnd = 1;
							if (hmin>=hmax) modeEnd = -1;//ha terminado en minimo
						}
					}
					
					if (modeRef!=0){
						String str = "O";
						int diff = 0;
						int diffp = 0;
						if (modeRef!=modeEnd && Math.abs(modeEnd)!=3){
							str="X";
							
							//diff = q1.getClose5()-ref;
							diff = href2-ref;
							if (modeEnd<=-1){
								//diff = ref-q1.getClose5();
								diff = ref-href2;
							}
							accLoss+=diff;
							
							res = -diff;
						}else{
							
							if (modeEnd==3){
								diffp=minRange;
							}else if (modeEnd==-3){
								diffp=-maxRange;
							}else{
								if (modeRef==1){
									diffp = q1.getClose5()-ref;
									//accWin += q1.getClose5()-ref;
								}else{
									diffp = ref-q1.getClose5();
									accWin += ref-q1.getClose5();
								}
							}
							
							if (diffp>=0){
								accWin += diffp;																
							}else{
								str="X";
							}
							res=diffp;
						}
						cases++;
						
						if (res>=0){
							accP+=res;
							
							if (conLosses>=1)
								rachasLoss.add(conLosses);
							conWins++;
							conLosses=0;
						}else{
							accN+=-res;
														
							if (conWins>=1)
								rachasWins.add(conWins);
							conLosses++;
							conWins=0;
							losses++;
						}
						
						
						if (debug==1){
							System.out.println(DateUtils.datePrint(cal1)+" || "+str
									+" ["+conLosses+"]"
									+" || "
									+PrintUtils.Print2dec(res*0.1, false)
									+" || "+ modeRef+" "+modeEnd+" || "+hmin+" "+hmax
									+" ||| "+accP+" "+accN
							);
						}
					}
				}
				
				totalDays++;
				high=-1;
				low=-1;
				hmax=-1;
				hmin=-1;
				modeRef = 0;
				href2=-1;
				ref=-1;
				modeEnd=0;
				lossValue=0;
				lastDay = day;
			}
			
			
			int maxMin = maxMins.get(i-1);
			
			if (modeEnd==0){
				if (modeRef==1){
					if (q.getHigh5()-ref>=minRange){
						modeEnd=3;
					}else if (ref-q.getLow5()>=maxRange){
						modeEnd=-3;
					}
				}else if (modeRef==-1){
					if (ref-q.getLow5()>=minRange){
						modeEnd=3;
					}else if (q.getHigh5()-ref>=maxRange){
						modeEnd=-3;
					}
				}
			}
			
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				hmax = h;
				int range = high-low;
				
				if (modeEnd==0){
					if (modeRef==0 
							//&& range>=minRange 
							&& low!=-1
							&& data.get(i-6).getLow5()>data.get(i-12).getLow5()
							&& data.get(i-12).getLow5()>data.get(i-24).getLow5()
							//&& q.getClose5()-data.get(i-12).getLow5()>=300
							){
						if (h>=h1 && h<=h2){
							modeRef=1;
							ref = q.getClose5();
							if (debug==2){
								System.out.println("[LONG] "+DateUtils.datePrint(cal)+" "+ref);
							}
						}
					}else{
						if (modeRef==-1 && href2==-1){//ibamos ya hacia abajo,luego anotamos el end y el href2
							href2 = q.getClose5();
							modeEnd=2;
							if (debug==2){
								System.out.println("[LONG (from SHORT)] "+DateUtils.datePrint(cal)
										+" "+ref+" "+href2+" || "+(href2-ref)+" || "+(losses+1)
										);
							}
						}
					}
				}
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
				hmin = h;
				int range = high-low;
				
				if (modeEnd==0){
					if (modeRef==0 
							//&& range>=minRange 
							&& high!=-1
							//&& maxMin<=-thr
							&& data.get(i-6).getHigh5()<data.get(i-12).getHigh5()
							&& data.get(i-12).getHigh5()<data.get(i-24).getHigh5()
							//&& data.get(i-12).getHigh5()-q.getClose5()>=300
							){
						if (h>=h1 && h<=h2){
							modeRef=-1;
							ref = q.getClose5();
							if (debug==2){
								System.out.println("[SHORT] "+DateUtils.datePrint(cal)+" "+ref);
							}
						}
					}else{
						if (modeRef==1 && href2==-1){//ibamos ya hacia arriba,luego anotamos el end y el href2
							href2 = q.getClose5();
							modeEnd=-2;
							if (debug==2){
								System.out.println("[SHORT (from LONG)] "+DateUtils.datePrint(cal)
										+" "+ref+" "+href2+" || "+(ref-href2)+" || "+(losses+1)
										);
							}
						}
					}
				}
			}
			
		}//for
		
		double casesPer = cases*100.0/totalDays;
		double casesWin = (cases-losses)*100.0/cases;
		double casesLoss = (losses)*100.0/cases;
		wins=cases-losses;
		double avgW = accWin*0.1/wins;
		double avgL = accLoss*0.1/losses;
		
		double avgrachaw = MathUtils.average(rachasWins);
		double avgrachal = MathUtils.average(rachasLoss);
		System.out.println(
				h1+" "+h2+" "+minRange+" ||| "
				+totalDays+" "+cases+" "+wins+" "+losses
				+" || "
				/*+PrintUtils.Print2dec(casesPer, false)
				+" "+PrintUtils.Print2dec(casesWin, false)
				+" || "+PrintUtils.Print2dec(avgW, false)+" "+PrintUtils.Print2dec(avgL, false)
				+" || "+PrintUtils.Print2dec(casesWin*avgW, false)
				+" "+PrintUtils.Print2dec(casesLoss*avgL, false)
				+" || "+PrintUtils.Print2dec((casesWin*avgW)/(casesLoss*avgL), false)*/
				+" || "+PrintUtils.Print2dec(avgrachaw, false)+" "+PrintUtils.Print2dec(avgrachal, false)
				
				+"|| "+PrintUtils.Print2dec(accP*0.1/wins, false)
				+" "+PrintUtils.Print2dec(accN*0.1/losses, false)
				+" || "+PrintUtils.Print2dec(accP*1.0/accN, false)
				);
		
		return -1;		
	}
	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2018.06.06.csv";
		
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
		//FFNewsClass.readNews(pathNews,news,0);
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
		
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			System.out.println(data.size()+" "+maxMins.size());
			
			for (int y1=2004;y1<=2004;y1++){
				for (int h1=0;h1<=23;h1++){					
					for (int minRange=200;minRange<=200;minRange+=100){
						for (int maxRange=200;maxRange<=200;maxRange+=50){
							for (int thr=0;thr<=0;thr+=100)
								StudyHours.doTest("", data, maxMins, y1+0, y1+14, h1, h1+0,minRange,maxRange,thr, 0);
						}
					}
				}
			}

		}
	}

}
