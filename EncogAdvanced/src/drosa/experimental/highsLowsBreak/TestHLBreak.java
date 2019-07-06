package drosa.experimental.highsLowsBreak;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestHLBreak {
	
	
	public static void tradingTouches(ArrayList<QuoteShort> data,
			int h1,int h2,
			int nATR,
			double sl,double tp,
			double comm
			){
		
		double pipWins = 0;
		double pipLosses = 0;
		int totalWins = 0;
		int totalLosses = 0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		int lastDay = 0;
		int max = 0;
		int min = 999999;
		int lastHigh = -999999;
		int lastLow = 999999;
		double atr = 100;
		int totalDays = 0;
		Calendar cal = Calendar.getInstance();
		PositionShort pos = null;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				lastHigh = max;
				lastLow = min;
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
			}
			
			int slPips = (int) (atr*sl);
			int tpPips = (int) (atr*tp);
			double factor = sl/tp;
			if (tpPips<5){
				tpPips = 5;
				slPips = (int) (tpPips*factor);
			}
			int entry = 0;
			int slValue = 0;
			int tpValue = 0;
			PositionType posType = PositionType.NONE;
			if (h1<=h && h<=h2){
				if (lastHigh!=-999999 && q.getOpen5()<lastHigh && q.getHigh5()>lastHigh){
					entry = lastHigh;
					slValue = entry+slPips*10;
					tpValue = entry-tpPips*10;
					posType = PositionType.SHORT;
				}
				
				if (lastLow!=-999999 && q.getOpen5()>lastLow && q.getLow5()<lastLow){
					entry = lastLow;
					slValue = entry-slPips*10;
					tpValue = entry+tpPips*10;
					posType = PositionType.LONG;
				}
			}
			
			if (entry!=0){
				pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(slValue);
				pos.setTp(tpValue);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenIndex(i);
				pos.setPositionType(posType);
				pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
				//System.out.println(pos.toString2());
				positions.add(pos);
			}
			
			int p = 0;
			while (p<positions.size()){
				PositionShort pp = positions.get(p);
				PositionType pType = pp.getPositionType();
				boolean closed = false;
				int win = 0;
				int pipsEarned = 0;
				if (pp.getPositionStatus()==PositionStatus.OPEN){
					if (pType==PositionType.SHORT){
						if (i==pp.getOpenDiff()){
							if (q.getHigh5()>=pp.getSl()){
								win = -1;
								closed = true;
								pipsEarned = -slPips;
							}else if (q.getClose5()<=pp.getTp()){
								win = 1;
								closed = true;
								pipsEarned = tpPips;
							}
						}else{
							if (q.getHigh5()>=pp.getSl()){
								win = -1;
								closed = true;
								pipsEarned = -slPips;
							}else if (q.getLow5()<=pp.getTp()){
								win = 1;
								closed = true;
								pipsEarned = tpPips;
							}
						}
					}
					if (pType == PositionType.LONG){
						if (i==pp.getOpenDiff()){
							if (q.getLow5()<=pp.getSl()){
								win = -1;
								closed = true;
								pipsEarned = -slPips;
							}else if (q.getClose5()>=pp.getTp()){
								win = 1;
								closed = true;
								pipsEarned = tpPips;
							}
						}else{
							if (q.getLow5()<=pp.getSl()){
								win = -1;
								closed = true;
								pipsEarned = -slPips;
							}else if (q.getHigh5()>=pp.getTp()){
								win = 1;
								closed = true;
								pipsEarned = tpPips;
							}
						}
					}
				}
				if (closed){
					pipsEarned-=comm;
					if (pipsEarned>=0){
						totalWins++;
						pipWins+=pipsEarned;
						win=0;
					}
					else if (pipsEarned<0){
						totalLosses++;
						pipLosses+=pipsEarned;
						win=-1;
					}
					pos.setPositionStatus(PositionStatus.CLOSE);
					pos.setWin(win);
					pos.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
					//System.out.println(q.toString()+" || "+pos.toString2());
					positions.remove(p);
				}else{
					p++;
				}
			}
			
			if (q.getHigh5()>max)
				max = q.getHigh5();
			if (q.getLow5()<min)
				min = q.getLow5();
		}
		
		int total = totalWins+totalLosses;
		//double pf = (totalWins*tp-totalLosses*sl)/total;
		double winPer = totalWins*100.0/total;
		double lossPer = 100.0-winPer;
		//double pf = (winPer*tp)/(lossPer*sl);
		double totalPips = pipWins+pipLosses;
		double avgPips = totalPips/total;
		double pf = Math.abs((pipWins)*1.0/(pipLosses));
		
		System.out.println(h1+" "+h2
				+" "+PrintUtils.Print2dec(tp,false,2)
				+" "+PrintUtils.Print2dec(sl,false,2)
				+" || "+total+" "+totalWins
				+" "+PrintUtils.Print2dec(winPer,false,2)
				+" "+pipWins
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2dec(pipWins,false,2)
				+" "+PrintUtils.Print2dec(pipLosses,false,2)
				+" "+PrintUtils.Print2dec(avgPips,false,2)
				//+" "+PrintUtils.Print2(totalPips*1.0/total)
				);
	}
	
	public static double testTouches(String header,ArrayList<QuoteShort> data,
			ArrayList<DailyMaxMin> touches,
			int h1,int h2,int tp,int nbars,boolean debug){
		
		int total = 0;
		int correct = 0;
		int sumDiff=0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<touches.size();i++){
			DailyMaxMin maxMin = touches.get(i);
			int index = maxMin.getIndex();
			int value = maxMin.getValue();
			boolean isMax = maxMin.isMax();
			QuoteShort q = data.get(Math.abs(index));
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h1<=h && h<=h2){
				if (isMax){//high
					QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, index+1, index+nbars);
					int max = q.getHigh5();
					int min = qMaxMin.getLow5();
					if (qMaxMin.getHigh5()>max) max = qMaxMin.getHigh5();
					int diff = max-value;
					int diffm = (max-value)-(value-min);
					
					sumDiff+=(diffm*0.1);
					/*System.out.println(value
							+" "+q.toString()
							+" "+(max-value)+" "+(value-min)+" "+diffm+" "+sumDiff);*/
					if (diff*0.1>=tp) correct++;
					total++;					
				}else{//low
					QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, index+1, index+nbars);
					int min = q.getLow5();
					int max = qMaxMin.getHigh5();
					if (qMaxMin.getLow5()<min) min = qMaxMin.getLow5();
					int diff = value-min;
					int diffm = (value-min)-(max-value);
					sumDiff+=(diffm*0.1);
					/*System.out.println(value
							+" "+q.toString()
							+" "+(value-min)+" "+(max-value)+" "+diffm+" "+sumDiff);*/
					if (diff*0.1>=tp) correct++;
					total++;
				}
			}			
		}
		double touchPer = total*100.0/touches.size();
		double correctPer = correct*100.0/total;
		double avgDiff = sumDiff*1.0/total;
		if (debug)
		System.out.println(
				header
				+" "+h1+" "+h2
				+" "+nbars
				+" || "
				+" "+total
				+" "+PrintUtils.Print2(touchPer)
				+" "+PrintUtils.Print2(correctPer)
				+" "+PrintUtils.Print2(avgDiff)
				+" ||  "+PrintUtils.Print2(total*avgDiff)
				);
		
		return avgDiff;
	}
	
	public static ArrayList<DailyMaxMin> getDOTouches(ArrayList<QuoteShort> data,double corrector){
		
		ArrayList<DailyMaxMin> touches = new ArrayList<DailyMaxMin>();
		
	
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		int lastH = -1;
		int lastL = 999999;
		int actualH = -1;
		int actualL = 999999;
		int DO = -1;
		int valueH = -1;
		int valueL = -1;
		DailyMaxMin dailyhl = null ;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){				
				lastDay = day;
				DO = q.getOpen5();
				lastH = actualH;
				lastL = actualL;				
				actualH = -1;
				actualL = 999999;
				
				if (lastH!=-1 && lastL!=999999){
					int diff = lastH-lastL;
					valueH = (int) (DO+diff*corrector);
					valueL = (int) (DO-diff*corrector);
				}
			}
			
			if (valueH!=-1 && q.getOpen5()<(valueH) && q.getHigh5()>=(valueH)){
				dailyhl = new DailyMaxMin(i,valueH,true);
				touches.add(dailyhl);
			}
			
			if (valueL!=-1 && q.getOpen5()>(valueL) && q.getLow5()<=(valueL)){
				dailyhl = new DailyMaxMin(i,valueL,false);
				touches.add(dailyhl);
			}
			
			if (q.getHigh5()>actualH) actualH = q.getHigh5();
			if (q.getLow5()<actualL) actualL = q.getLow5();
		}
		
		return touches;
	}

	public static ArrayList<DailyMaxMin> getLastDayHLTouches(ArrayList<QuoteShort> data,int corrector){
		
		ArrayList<DailyMaxMin> touches = new ArrayList<DailyMaxMin>();
		
	
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		int lastH = -1;
		int lastL = 999999;
		int actualH = -1;
		int actualL = 999999;
		DailyMaxMin dailyhl = null ;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){				
				lastDay = day;
				lastH = actualH;
				lastL = actualL;				
				actualH = -1;
				actualL = 999999;
			}
			
			if (lastH!=-1 && q.getOpen5()<(lastH+corrector*10) && q.getHigh5()>=(lastH+corrector*10)){
				dailyhl = new DailyMaxMin(i,lastH+corrector*10,true);
				touches.add(dailyhl);
			}
			
			if (lastL!=999999 && q.getOpen5()>(lastL-corrector*10) && q.getLow5()<=(lastL-corrector*10)){
				dailyhl = new DailyMaxMin(i,lastL-corrector*10,false);
				touches.add(dailyhl);
			}
			
			if (q.getHigh5()>actualH) actualH = q.getHigh5();
			if (q.getLow5()<actualL) actualL = q.getLow5();
		}
		
		return touches;
	}
	
	public static ArrayList<DailyMaxMin> getLastWeekHLTouches(ArrayList<QuoteShort> data,int corrector){
		
		ArrayList<DailyMaxMin> touches = new ArrayList<DailyMaxMin>();
		
	
		int lastWeek = -1;
		Calendar cal = Calendar.getInstance();
		int lastH = -1;
		int lastL = 999999;
		int actualH = -1;
		int actualL = 999999;
		DailyMaxMin dailyhl = null ;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			
			if (week!=lastWeek){				
				lastWeek = week;
				lastH = actualH;
				lastL = actualL;				
				actualH = -1;
				actualL = 999999;
			}
			
			if (lastH!=-1 && q.getOpen5()<(lastH+corrector*10) && q.getHigh5()>=(lastH+corrector*10)){
				dailyhl = new DailyMaxMin(i,lastH+corrector*10,true);
				touches.add(dailyhl);
			}
			
			if (lastL!=999999 && q.getOpen5()>(lastL-corrector*10) && q.getLow5()<=(lastL-corrector*10)){
				dailyhl = new DailyMaxMin(i,lastL-corrector*10,false);
				touches.add(dailyhl);
			}
			
			if (q.getHigh5()>actualH) actualH = q.getHigh5();
			if (q.getLow5()<actualL) actualL = q.getLow5();
		}
		
		return touches;
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String path5m   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.12.20.csv";
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.31.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		
		/*for (int corrector = 0;corrector<=0;corrector++){
		//for (double corrector = 0.05;corrector<=2.0;corrector+=0.05){
			String header = PrintUtils.Print2(corrector);
			//ArrayList<DailyMaxMin> touchesD = TestHLBreak.getDOTouches(data,corrector);
			ArrayList<DailyMaxMin> touchesD = TestHLBreak.getLastDayHLTouches(data,corrector);
			//ArrayList<DailyMaxMin> touchesW = TestHLBreak.getLastWeekHLTouches(data,corrector);
			//System.out.println("touchesD: "+touchesD.size());
			//System.out.println("touchesW: "+touchesW.size());
			for (int nbars=100;nbars<=20000;nbars+=100){
				String nH = Integer.valueOf(nbars)+" ";
				for (int h1=18;h1<=23;h1++){
					int h2 = h1+0;
					//for (int nbars=1;nbars<=1000;nbars+=1){
						double avg = TestHLBreak.testTouches(header,data, touchesD, h1, h2, 5, nbars,false);
						nH+=PrintUtils.Print2dec(avg, false, 2)+" ";
						//TestHLBreak.testTouches(header,data, touchesD, h1, h2, 5, nbars);
					//}
				}
				System.out.println(nH);
			}
		}*/
		
		int h1 = 10;
		int h2 = 14;
		int nATR = 5;
		double comm = 1.7;
		for (h1=7;h1<=7;h1++){
			h2 = h1+3;
			for (nATR=5;nATR<=5;nATR++){
				for (double sl = 0.23;sl<=0.23;sl+=0.01){
					//for (double tp= 0.04;tp<=0.20;tp+=0.01){
					for (double tp= 0.01;tp<=100.0;tp+=0.10){
					//for (double sl = tp;sl<=tp;sl+=0.01){
						//for (double sl = 0.10;sl<=0.80;sl+=0.10){
							TestHLBreak.tradingTouches(data, h1, h2, nATR, sl, tp,comm);
						//}
					}
				}
			}
		}
	}

}
