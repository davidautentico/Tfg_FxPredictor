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

public class TestLeg10_2016 {
	
	public static ArrayList<LegInfo> getLegs(
			String header,
			ArrayList<QuoteShort> data,
			int minPips,
			boolean isClose
			){
		
		 ArrayList<LegInfo> legs = new  ArrayList<LegInfo>();
		
		int actualLeg = 0;
		int index1 = 0;
		int index2 = 0;
		int hleg = -1;
		int index1x = -1;
		int index2x = -1;
		int index3x = -1;
		int index4x = -1;
		int index5x = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			
			int diffL = q.getOpen5()- data.get(index1).getOpen5();
			int diffS = data.get(index1).getOpen5()-q.getOpen5();
			
			if (actualLeg==0){
				if (diffL>=minPips*10){
					index2 = i;
					actualLeg = 1;	
					hleg = h;
				}else if (diffS>=minPips*10){
					index2 = i;
					actualLeg = -1;
					hleg = h;
				}
			}else if (actualLeg==1){
				int diffLS = data.get(index2).getOpen5()-q.getOpen5();
				if (q.getOpen5()>=data.get(index2).getOpen5()){
					double pips		= (data.get(index2).getOpen5()-data.get(index1).getOpen5())*0.1;
					double factor 	= Math.abs(pips/minPips);
					
					if (factor>=1.0 && index1x==-1) index1x = i;
					if (factor>=2.0 && index2x==-1) index2x = i;
					if (factor>=3.0 && index3x==-1) index3x = i;
					if (factor>=4.0 && index4x==-1) index4x = i;
					if (factor>=5.0 && index5x==-1) index5x = i;
							
					index2 = i;
				}else if (diffLS>=minPips*10){
					LegInfo leg = new LegInfo();
					double pips = (data.get(index2).getOpen5()-data.get(index1).getOpen5())*0.1;
					leg.setParams(1,index1,index2,data.get(index2).getOpen5()-data.get(index1).getOpen5(),hleg,Math.abs(pips/minPips));
					leg.setFactorIndexes(index1x,index2x,index3x,index4x,index5x);
					legs.add(leg);
					
					index1 = index2;
					index2 = i;
					actualLeg = -1;	
					hleg = h;
					
					index1x = i;
					index2x = -1;
					index3x = -1;
					index4x = -1;
					index5x = -1;
				}			
			}else if (actualLeg==-1){
				int diffSL = q.getOpen5()-data.get(index2).getOpen5();
				if (q.getOpen5()<=data.get(index2).getOpen5()){
					double pips		= (data.get(index1).getOpen5()-data.get(index2).getOpen5())*0.1;
					double factor 	= Math.abs(pips/minPips);
					
					if (factor>=1.0 && index1x==-1) index1x = i;
					if (factor>=2.0 && index2x==-1) index2x = i;
					if (factor>=3.0 && index3x==-1) index3x = i;
					if (factor>=4.0 && index4x==-1) index4x = i;
					if (factor>=5.0 && index5x==-1) index5x = i;
					
					index2 = i;
				}else if (diffSL>=minPips*10){
					LegInfo leg = new LegInfo();
					double pips = (data.get(index1).getOpen5()-data.get(index2).getOpen5())*0.1;
					leg.setParams(-1,index1,index2,data.get(index1).getOpen5()-data.get(index2).getOpen5(),hleg,Math.abs(pips/minPips));
					leg.setFactorIndexes(index1x,index2x,index3x,index4x,index5x);
					legs.add(leg);
					
					index1 = index2;
					index2 = i;
					actualLeg = 1;
					hleg = h;
					
					index1x = i;
					index2x = -1;
					index3x = -1;
					index4x = -1;
					index5x = -1;
				}
			}
		}
		
		
		return legs;
	}
	
	public static void studyLegs(
			String header,
			ArrayList<QuoteShort> data,
			int minPips,
			boolean isClose
			){
		
		ArrayList<Double> legs = new ArrayList<Double>();
		
		int actualLeg = 0;
		int index1 = 0;
		int index2 = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			
			
			int diffL = q.getClose5()- data.get(index1).getClose5();
			int diffS = data.get(index1).getClose5()-q.getClose5();
			
			if (actualLeg==0){
				if (diffL>=minPips*10){
					index2 = i;
					actualLeg = 1;					
				}else if (diffS>=minPips*10){
					index2 = i;
					actualLeg = -1;
				}
			}else if (actualLeg==1){
				int diffLS = data.get(index2).getClose5()-q.getClose5();
				if (q.getClose5()>=data.get(index2).getClose5()){
					index2 = i;
				}else if (diffLS>=minPips*10){
					
					legs.add((data.get(index2).getClose5()-data.get(index1).getClose5())*0.1);
					
					index1 = index2;
					index2 = i;
					actualLeg = -1;					
				}			
			}else if (actualLeg==-1){
				int diffSL = q.getClose5()-data.get(index2).getClose5();
				if (q.getClose5()<=data.get(index2).getClose5()){
					index2 = i;
				}else if (diffSL>=minPips*10){
					//legs.add(-(data.get(index1).getClose5()-data.get(index2).getClose5())*0.1);
					legs.add((data.get(index1).getClose5()-data.get(index2).getClose5())*0.1);
					index1 = index2;
					index2 = i;
					actualLeg = 1;
				}
			}
		}
		
		
		int total = legs.size();
		
		MathUtils.summary_complete(header, legs);
	}

	
	public static void studyLegs(ArrayList<LegInfo> legs,int h1,int h2,double factor){
	
		int total = 0;
		int totalf = 0;
		for (int i=0;i<legs.size();i++){
			LegInfo leg = legs.get(i);
			int h = leg.getHleg();
			double f = leg.getFactor();
			
			if (h>=h1 && h<=h2){
				if (f>=factor){
					totalf++;
				}
				
				total++;
			}
			
		}
		
		System.out.println(
				h1+" "+h2+" "+PrintUtils.Print2dec(factor, false)
				+" || "
				+" "+PrintUtils.Print2dec(totalf*100.0/total, false)
				);
		
	}
	
	public static void studyLegsTrade(ArrayList<QuoteShort> data,
			ArrayList<LegInfo> legs,int h1,int h2,
			int factor,int tp,int sl,int maxBars){
		
		int total = 0;
		int totalf = 0;
		int wins = 0;
		int losses = 0;
		double winPips = 0;
		double lostPips = 0;
		double accDiff = 0;
		Calendar cal= Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=0;i<legs.size();i++){
			LegInfo leg = legs.get(i);
			int h = leg.getHleg();
			double f = leg.getFactor();
			
			if (h>=h1 && h<=h2){
				//if (f>=factor){
					int index3x = leg.getFactorIdx(factor);
					if (index3x>=0){
						int idx = index3x+maxBars;
						if (idx>data.size()-1){
							idx = data.size()-1;
						}
											
						
						int diffPips = 0;
						if (leg.getMode()==1){
							int valueTP = data.get(index3x).getOpen5()-10*tp;
							int valueSL = data.get(index3x).getOpen5()+10*sl;
							
							TradingUtils.getMaxMinShortTPSLMaxBars(data, qm, cal,index3x, idx,valueTP, valueSL, maxBars, false);
							diffPips = data.get(index3x).getOpen5()-qm.getClose5();
						}else if (leg.getMode()==-1){
							int valueTP = data.get(index3x).getOpen5()+10*tp;
							int valueSL = data.get(index3x).getOpen5()-10*sl;
							
							TradingUtils.getMaxMinShortTPSLMaxBars(data, qm, cal,index3x, idx,valueTP, valueSL, maxBars, false);
							diffPips = qm.getClose5()-data.get(index3x).getOpen5();
						}

						if (diffPips>=0){
							winPips+=diffPips*0.1;
							wins++;
						}else{
							lostPips+=-diffPips*0.1;
							losses++;
						}
					}
				//}
				
				total++;
			}
			
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (winPips-lostPips)/trades;
		double pf = winPips*1.0/lostPips;
		System.out.println(
				h1+" "+h2+" "+PrintUtils.Print2dec(factor, false)+" "+maxBars
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winPips/wins, false)
				+" "+PrintUtils.Print2dec(lostPips/losses, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}
	
	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.10.04.csv";
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
			
			System.out.println("total data: "+data.size());
			
			for (int minPips=20;minPips<=20;minPips+=10){
				//TestLeg10_2016.studyLegs("", data, minPips, true);
			}
			
			ArrayList<LegInfo> legs = TestLeg10_2016.getLegs("", data, 20, true);
			
			for (int h1=0;h1<=0;h1++){
				for (int h2=9;h2<=9;h2++){					
					//int h2 = h1+5;
					for (int factor=1;factor<=5;factor++){
						//TestLeg10_2016.studyLegs(legs, h1, h2, factor);
						for (int tp=40;tp<=40;tp++)
							TestLeg10_2016.studyLegsTrade(data,legs, h1, h2, factor,tp,40,9999);
					}
				}
			}
			
		}

	}

}
