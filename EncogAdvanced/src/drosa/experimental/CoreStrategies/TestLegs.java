package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestLegs {
	
public static void test2(ArrayList<QuoteShort> data,
		ArrayList<Integer> maxMins,
		int year1,int year2,int h3,
		int minDiff,
		int testPips,
		int imax,
		int thr,
		int maxBars,
		double comm,
		boolean debug
		){
		
		int wins = 0;
		int losses = 0;
		double winPips = 0;
		double lostPips = 0;
		int test10 = 0;
		int totalTest2 = 0;
		int lastDay = -1;
		int index1 = 0;
		int index2 = 0;
		int actualTrend = 0;
		int trendSize0 = 0;
		int totalTest = 0;
		int totalTest50 = 0;
		int totalTest80 = 0;
		int avgSize = 0;
		int totalFail = 0;
		int avgFail = 0;
		boolean enabledTest = false;
		int trendSize=0;
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		Calendar cali2 = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<year1 || y>year2) continue;
			
			if (day!=lastDay && h==h3){
				
				if (i>=imax){
					
					int index = i+3;
					int idx2 = i+3;
					
					idx2 = getNextThr(data,maxMins,i+3,i+12,thr);
					if (idx2>=0) index = idx2+1;
					
					int begin = index-imax;
					int end = index+maxBars;
					if (begin<=0) begin=0;
					if (index>=data.size()-1) break;
					if (end>=data.size()-1) end = data.size()-1;
					
					//System.out.println(i+" "+index+" "+maxMins.get(index-1));
					int openValue = data.get(index).getOpen5();
					int m = maxMins.get(index-1);
					int md = openValue-data.get(begin).getOpen5();
					if (md>=minDiff*10 && (m>=thr || thr==0)){//fue LONG
						//System.out.println(i+" "+index+" "+maxMins.get(index-1));
						TradingUtils.getMaxMinShort(data, qm, cal, index, end);
						double d = openValue-qm.getLow5();
						avgSize += d*0.1;
						totalTest++;
						
						//d-=comm*10;
						
						if (d*0.1>=testPips){
							test10++;
							wins++;
							winPips += testPips;
						}else{
							totalFail++;
							d = openValue-qm.getClose5();//-comm*10;
							avgFail += d;
							//System.out.println("[FAIL] "+DateUtils.datePrint(cal)+" "+(openValue-qm.getClose5())*0.1+" || "+idx2);
							
							if (d*0.1>=0){
								wins++;
								winPips += d*0.1;
							}else{								
								losses++;
								lostPips += -d*0.1;
							}
						}
					}else if (md<=-minDiff*10 && (m<=-thr || thr==0)){
						//System.out.println(i+" "+index+" "+maxMins.get(index-1));
						TradingUtils.getMaxMinShort(data, qm, cal, index, end);
						double d = qm.getHigh5()-openValue;//-comm*10;
						avgSize += d*0.1;
						totalTest++;
						if (d*0.1>=testPips){
							test10++;
							
							wins++;
							winPips += testPips;
						}else{
							totalFail++;
							d = qm.getClose5()-openValue;//comm*10;
							avgFail += d;
							//System.out.println("[FAIL] "+DateUtils.datePrint(cal)+" "+(qm.getHigh5()-openValue)*0.1);
							
							if (d*0.1>=0){
								wins++;
								winPips += d*0.1;
							}else{								
								losses++;
								lostPips += -d*0.1;
							}
						}
					}
				}
				
				lastDay = day;
			}
			
		}
		
		double avg = avgSize*1.0/totalTest;
		double avg10 = test10*100.0/totalTest;
		
		double per50 = totalTest50*100.0/totalTest;
		double per80 = totalTest80*100.0/totalTest;
		
		double per2_10 = test10*100.0/totalTest2;
		
		double avgF = avgFail*0.1/totalFail;
		//int wins = totalTest-totalFail;
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = (winPips*1.0-comm*wins)/(lostPips+comm*losses);
		double avgTrade = (winPips-lostPips)*1.0/trades-comm;
		//double pf = Math.abs((testPips-comm)*wins*1.0/((avgF+comm)*totalFail));
		//double avgTrade = ((testPips*wins)-(avgF*totalFail)-(wins+totalFail)*comm)/(wins+totalFail);
		
		//if (pf>=1.40)
		System.out.println(
				h3+" || "+minDiff+" "+imax+" "+thr+" "+testPips+" "+maxBars
				+" || "
				//+totalTest
				//+" "+PrintUtils.Print2dec(avg, false)+" "+PrintUtils.Print2dec(avg10, false)
				//+" || "+PrintUtils.Print2dec(avgF, false)
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(pf*totalTest, false)
				+" || "+PrintUtils.Print2dec(avgTrade, false)
				);
		
	}

	
	private static int getNextThr(ArrayList<QuoteShort> data,ArrayList<Integer> maxMins, int idx,int end, int thr) {
	// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		if (end>=data.size()-1) end =data.size()-1;
		for (int i=idx;i<=end;i++){
			QuoteShort.getCalendar(cal, data.get(i));
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//if (h>=2) return -1;
			if (maxMins.get(i)>=thr) return i;
			if (maxMins.get(i)<=-thr) return i;			
		}
	return -1;
}


	public static void test(ArrayList<QuoteShort> data,int minSize,int testSize){
		
		int test10 = 0;
		int totalTest2 = 0;
		int lastDay = -1;
		int index1 = 0;
		int index2 = 0;
		int actualTrend = 0;
		int trendSize0 = 0;
		int totalTest = 0;
		int totalTest50 = 0;
		int totalTest80 = 0;
		int avgSize = 0;
		boolean enabledTest = false;
		int trendSize=0;
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				
				if (trendSize>=testSize){
					//System.out.println("[TREND 0] "+DateUtils.datePrint(cal)+" "+actualTrend+" || "+trendSize);
					enabledTest= true;
					if (trendSize0==0){
						trendSize0 = trendSize;
					}
					TradingUtils.getMaxMinShort(data, qm, cal, i, i+24*12);
					
					
					if (actualTrend==1){
						int d = qm.getHigh5()-q.getOpen5();
						if (d>=100){
							test10++;
						}
						totalTest2++;
					}else if (actualTrend==-1){
						int d = q.getOpen5()-qm.getLow5();
						if (d>=100){
							test10++;
						}
						totalTest2++;
					}
				}
				
				lastDay = day;
			}
			
			
			int sizeH = q.getClose5()-data.get(index1).getClose5();
			int sizeL = data.get(index1).getClose5()-q.getClose5();
			int sizeH2 = q.getClose5()-data.get(index2).getClose5();
			int sizeL2 = data.get(index2).getClose5()-q.getClose5();
			trendSize = (int) (Math.abs(data.get(index2).getClose5()-data.get(index1).getClose5())*0.1);
			if (actualTrend==0){
				if (sizeH>=minSize*10){
					actualTrend = 1;
					index2 = i;
				}else if (sizeL>=minSize*10){
					actualTrend = -1;
					index2 = i;
				}
			
			}else if (actualTrend==1){
				if (q.getClose5()>=data.get(index2).getClose5()){
					index2 = i;
					
				}else if (sizeL2>=minSize*10){
					//System.out.println("[TREND LONG] "+trendSize+" || "+h);
					if (enabledTest){
						int diffSize = trendSize-trendSize0;
						//System.out.println("[TREND SHORT] "+trendSize+" || "+h+" ||| "+diffSize);
						totalTest++;
						avgSize += diffSize;
						if (diffSize>=50) totalTest50++;
						if (diffSize>=80) totalTest80++;
					}
					
					
					index1 = index2;
					index2 = i;
					trendSize = (int) (Math.abs(data.get(index2).getClose5()-data.get(index1).getClose5())*0.1);
					actualTrend = -1;
					trendSize0 = 0;
					
					
					
					enabledTest = false;
				}
			}else if (actualTrend==-1){
				if (q.getClose5()<=data.get(index2).getClose5()){
					index2 = i;
				}else if (sizeH2>=minSize*10){
					//System.out.println("[TREND SHORT] "+trendSize+" || "+h);
					if (enabledTest){
						int diffSize = trendSize-trendSize0;
						//System.out.println("[TREND SHORT] "+trendSize+" || "+h+" ||| "+diffSize);
						totalTest++;
						avgSize += diffSize;
						if (diffSize>=50) totalTest50++;
						if (diffSize>=80) totalTest80++;
					}
					index1 = index2;
					index2 = i;
					trendSize = (int) (Math.abs(data.get(index2).getClose5()-data.get(index1).getClose5())*0.1);
					actualTrend = 1;
					trendSize0 = 0;
					
					enabledTest = false;
				}
			}
			
		}
		
		double avg = avgSize*1.0/totalTest;
		double per50 = totalTest50*100.0/totalTest;
		double per80 = totalTest80*100.0/totalTest;
		
		double per2_10 = test10*100.0/totalTest2;
		System.out.println(
				minSize
				+" "+testSize
				
				+" || "+totalTest+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(per50, false)
				+" "+PrintUtils.Print2dec(per80, false)
				+" "+PrintUtils.Print2dec(per50/per80, false)
				+" || "+totalTest2+" "+PrintUtils.Print2dec(per2_10, false)
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

			//0->70,5,108 1->100,10,108 2->190,10,96 4->480,10,54 5->140,10,24 6->130,10,12 7->170,10,12 8->240,10,180 9->200,10,12 
			for (int year1=2009;year1<=2009;year1++){
				int year2 = year1 + 7;
				for (int minDiff=0; minDiff<=0; minDiff+=5){
					//for (int testSize=1*legSize;testSize<=1*legSize;testSize+=legSize)
						for (int imax=24*12;imax<=24*12;imax+=5*12){
							for (int h3=9;h3<=9;h3++){
								for (int thr=50;thr<=500;thr+=10){
									for (int testPips=10;testPips<=10;testPips+=5){
										for (int maxBars=12;maxBars<=180;maxBars+=12){											
											TestLegs.test2(data,maxMins,year1,year2,h3,  minDiff,testPips,imax,thr,maxBars,1.5,true);
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
