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

public class TestLegs2 {
	
	
	public static void test(ArrayList<QuoteShort> data,int y1,int y2,int h1,int h2,int distance,int maxBars){
		
		
		
		int openValue = 0;
		int lastDay = -1;
		int acc = 0;
		int count = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				openValue = q.getOpen5();
				lastDay = day;
			}
			
			
			if (h>=h1 && h<=h2 && i+maxBars<data.size()){
				int diff = q.getOpen5()-openValue;
				
				if (diff>=distance*10){
					int pips = q.getOpen5()-data.get(i+maxBars).getOpen5();
					acc += pips;
					count++;
				}else if (diff<=-distance*10){
					int pips = data.get(i+maxBars).getOpen5()-q.getOpen5();
					acc += pips;
					count++;
				}
			}			
		}
		
		double avg = acc*0.1/count;
		System.out.println(
				h1+" "+h2
				+" "+distance
				+" "+maxBars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(avg, false)
				);
	}
	
	
public static void test3(ArrayList<QuoteShort> data,
		ArrayList<Integer> maxMins,
		int y1,int y2,int h1,int h2,
		int bars,int maxBars){
					
		int openValue = 0;
		int lastDay = -1;
		int acc = 0;
		int count = 0;
		int atrValue = 1000;
		int max = -1;
		int min = -1;
		int atr = 1000;
		int wins = 0;
		ArrayList<Integer> atrs = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qf = new QuoteShort();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				atrValue = max-min;
				if (atrValue<=0) atrValue = 1000;
				atrs.add(atrValue);
				
				atr = (int) (MathUtils.average(atrs, atrs.size()-14, atrs.size()-1));
				openValue = q.getOpen5();
				lastDay = day;
				
				max = -1;
				min = -1;
				//System.out.println(atr);
			}
			
			int maxMin = maxMins.get(i);
			if (h>=h1 && h<=h2 && i+maxBars<data.size()){
				int diff = q.getOpen5()-openValue;
				
				if (maxMin>=bars){
					TradingUtils.getMaxMinShort(data,qf,calm, i+1, i+maxBars);
					
					int pips =  (q1.getOpen5()-qf.getLow5())-(qf.getHigh5()-q1.getOpen5());
					
					if (pips>=100) wins++;
					
					//int pips = q.getOpen5()-data.get(i+maxBars).getOpen5();
					acc += pips;
					count++;
				}else if (maxMin<=-bars){
					TradingUtils.getMaxMinShort(data,qf,calm, i+1, i+maxBars);
					
					int pips = (qf.getHigh5()-q1.getOpen5())-(q1.getOpen5()-qf.getLow5());
					
					if (pips>=100) wins++;
					
					//int pips = data.get(i+maxBars).getOpen5()-q.getOpen5();
					acc += pips;
					count++;
				}
			}	
			
			if (q.getHigh5()>=max || max==-1) max = q.getHigh5();
			if (q.getLow5()<=min || min==-1) min = q.getLow5();
		}
		
		double avg = acc*0.1/count;
		System.out.println(
				h1+" "+h2
				+" "+PrintUtils.Print2dec(bars, false)
				+" "+maxBars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(wins*100.0/count, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
	}


public static void test4(ArrayList<QuoteShort> data,
		int y1,int y2,int h1,int h2,
		int len,
		int debug
		){
	
		ArrayList<Integer> legs = new ArrayList<Integer>();
					
		int index1 = -1;
		int index2 = -1;
		int lastDay = -1;
		int hIndex1=-1;
		int actualLeg = 0;
		ArrayList<Integer> atrs = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qf = new QuoteShort();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			
			if (index1==-1){
				index1 = i;
				index2 = i;
			}
			
			QuoteShort qIndex1 = data.get(index1);
			QuoteShort qIndex2 = data.get(index2);
			
			int diffH = q.getHigh5()-qIndex1.getLow5();
			int diffL = qIndex1.getHigh5()-q.getLow5();
			
			if (actualLeg==0){
				if (diffH>=len*10){
					actualLeg = 1;
					index1 = i;
					index2 = i;
					hIndex1 = h;
				}else if (diffL>=len*10){
					actualLeg = -1;
					index1 = i;
					index2 = i;
					hIndex1 = h;
				}
			}else if (actualLeg==1){
				int diffL2 = qIndex2.getHigh5()-q.getLow5();
				if (q.getHigh5()>=qIndex2.getHigh5()){//se amplia la leg
					index2 = i;
				}else if (diffL2>=len*10){//nueva leg down
					int size = qIndex2.getHigh5()-qIndex1.getLow5();
					
					if (hIndex1>=h1 && hIndex1<=h2){
						if (debug==2){
							double f = size*0.1/len;							
							System.out.println("[NUEVA LEG DOWN] "+size+" "+hIndex1+" "+f+" "+legs.size());
						}
						legs.add(size);
					}
					
					hIndex1 = h;
					actualLeg = -1;
					index1 = index2;
					index2 = i;
				}
			}else if (actualLeg==-1){
				int diffH2 = q.getHigh5()-qIndex2.getLow5();
				if (q.getLow5()<=qIndex2.getLow5()){//se amplia la leg
					index2 = i;
				}else if (diffH2>=len*10){//nueva leg up
					int size = qIndex1.getHigh5()-qIndex2.getLow5();
					
					if (hIndex1>=h1 && hIndex1<=h2){
						if (debug==2){
							double f = size*0.1/len;							
							System.out.println("[NUEVA LEG UP] "+size+" "+hIndex1+" "+f+" "+legs.size());
						}
						legs.add(size);
					}
					
					hIndex1 = h;
					actualLeg = 1;
					index1 = index2;
					index2 = i;
				}				
			}			
		}
		
		int total = 0;
		int less3 = 0;
		int accLess3 = 0;
		int greater2 = 0;
		int greater4 = 0;
		int greater5 = 0;
		int greater6 = 0;
		int greater7 = 0;
		int greater8 = 0;
		int greater10 = 0;
		for (int i = 0;i<legs.size();i++){
			int size = legs.get(i);
			double factor = size*0.1/len;
			if (factor>=1.0){
									
				if (factor<=2.0){
					accLess3+=size;
					less3++;
				}
				if (factor>=2.01){
					//System.out.println(size);
					greater2++;
				}
				if (factor>=4.01){
					//System.out.println(size);
					greater4++;
				}
				if (size*0.1>=110){
					//System.out.println(size);
					greater5++;
				}
			 if (factor>=6.00){
					//System.out.println(size);
					greater6++;
			 }
				if (factor>=7.00){
					//System.out.println(size);
					greater7++;
				}
				
				if (factor>=8.00){
					//System.out.println(size);
					greater8++;
				}
				
				if (factor>=10.00){
					System.out.println(size);
					greater10++;
				}
				
				total++;
			}
		}
	
		double per3 = less3*100.0/total;
		double per2 = greater2*100.0/total;
		double per4 = greater4*100.0/total;
		double per5 = greater5*100.0/total;
		double per6 = greater6*100.0/total;
		double per7 = greater7*100.0/total;
		double per8 = greater8*100.0/total;
		double per10 = greater10*100.0/total;
		
		System.out.println(len
				
				+" || "
				+" "+total
				+" || "+less3+" "+PrintUtils.Print2dec(accLess3*0.1/less3, false)+" "+PrintUtils.Print2dec(per3, false)
				//+" || "+greater2+" "+PrintUtils.Print2dec(per2, false)
				//+" || "+greater4+" "+PrintUtils.Print2dec(per4, false)
				+" || "+greater5+" "+PrintUtils.Print2dec(per5, false)
				+" || "+greater6+" "+PrintUtils.Print2dec(per6, false)
				+" || "+greater7+" "+PrintUtils.Print2dec(per7, false)
				+" || "+greater8+" "+PrintUtils.Print2dec(per8, false)
				+" || "+greater10+" "+PrintUtils.Print2dec(per10, false)
				);
	}


	public static void printLots(int leg,double factorEntry,int testCase){
		
		double factor = testCase*1.0/leg;
		
		System.out.println(leg+" || "+testCase+" "+PrintUtils.Print2dec(factor, false));
		
		double acc = 0;
		int count = 0;
		double lots = 0;
		double sumaPesos = 0;
		double sumaPesosLots = 0;
		for (int i=leg;i<=testCase;i+=10){
			double f = i*1.0/leg;
			if (f>=factorEntry){
				//i1*f1+i2*f2+y3*fx = media*f+media*fx-> fx =media*f-sumafrequencias
				
				if (i==leg*factorEntry){
					sumaPesos+=1;
					sumaPesosLots+=i;
					System.out.println("ajustes: "	
							+" "+i+" || "+PrintUtils.Print2dec(sumaPesos, false)
						);
				}else{
					
					double value = sumaPesosLots / sumaPesos;
					int obj = i-leg+9;
					double diff = value-obj;
					
					if (diff<=0){
						//(i*nuevoPeso)= obj*sumaPesos+nuevoPeso*obj-value*sumaPesos+;
						//double nuevoPeso*(i-obj) =obj*sumaPesos-value*sumaPesos;
						double nuevoPeso = (obj*sumaPesos-value*sumaPesos)/(i-obj);
						sumaPesos += nuevoPeso;
						sumaPesosLots += nuevoPeso*i;
						System.out.println("ajustes: "	
								+" "+obj
								+" || "+i
								+" ||| "+PrintUtils.Print2dec(nuevoPeso, false)+" || "+PrintUtils.Print2dec(sumaPesos, false)
							);
					}
				}			
			}
		}
		
		double avg = acc*1.0/count;
		double diff = (testCase-leg)-avg;
		
		//System.out.println(leg+" "+testCase+" || "+count+" "+PrintUtils.Print2dec(diff, false));
	}
	
	public static void tesTrading(ArrayList<QuoteShort> data,int y1,int y2){
	
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qf = new QuoteShort();
		int lastDay = -1;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (lastDay!=day){
				
				day = lastDay;
			}
			
		}
	}

	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2017.02.23.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.03.01.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2017.03.01.csv";
		String pathGBPUSD = "C:\\fxdata\\gbpusd_UTC_5 Mins_Bid_2003.05.04_2017.02.24.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2017.02.24.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathUSDJPY);
		
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		int limit = 0;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			data = dataS;
						
			//ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int len=20;len<=60;len+=10){
				TestLegs2.test4(data, 2003, 2017, 0, 9, len,0);
			}
			
			TestLegs2.printLots(20, 1.0, 200);
		}

	}

}
