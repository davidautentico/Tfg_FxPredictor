package drosa.experimental.ticks;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.classes.Tick;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class LastTouch {
	
	public static void lastTouch(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,	
			int y1,int y2,
			int h1,int h2,
			int minPips,
			int thr,
			int nbars,
			int tp,
			int sl,
			int offset,
			int volThr1,
			int volThr2
			){
		
		int winPips = 0;
		int lostPips = 0;
		int total = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		long accWinVol = 0;
		long accLostVol = 0;
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		QuoteShort qm1 = new QuoteShort();
		for (int i=1;i<data.size()-1-offset;i++){
			int index = i+offset;
			QuoteShort q = data.get(index-1);			
			QuoteShort q1 = data.get(index);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (lastDay != day){
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i).getExtra();
			
			if (maxMin>=thr){
				
				int valueTP = q1.getOpen5()-tp*25;
				int valueSL = q1.getOpen5()+sl*25;
				TradingUtils.getMaxMinShort(data, qm1, cal, index-thr, index -1);
				TradingUtils.getMaxMinShortTPSL(data, qm, cal, index , index+nbars, valueTP, valueSL);
				int pipsBefore = (q1.getOpen5()-qm1.getLow5())/25;
				int pips = (q1.getOpen5()-qm.getClose5())/25;		
				double vol = TradingUtils.getAccVolume(data,index-5,index-1); 
				if (vol<volThr1 || vol>volThr2) continue;
				if (pipsBefore<minPips) continue;
				if (pips>=0){
					winPips += pips;
					wins++;					
					accWinVol += vol;
				}else{
					lostPips += -pips;
					losses++;
					accLostVol += vol;
				}
				total++;
			}else if (maxMin<=-thr){
				int valueTP = q1.getOpen5()+tp*25;
				int valueSL = q1.getOpen5()-sl*25;
				TradingUtils.getMaxMinShort(data, qm1, cal, index-thr, index-1);
				TradingUtils.getMaxMinShortTPSL(data, qm, cal, index, index+nbars, valueTP, valueSL);
				int pipsBefore = (qm1.getHigh5()-q1.getOpen5())/25;
				int pips = (qm.getClose5()-q1.getOpen5())/25;
				double vol = TradingUtils.getAccVolume(data,index-5,index-1); 
				if (vol<volThr1 || vol>volThr2) continue;
				if (pipsBefore<minPips) continue;
				if (pips>=0){
					winPips += pips;
					wins++;
					accWinVol += vol;
				}else{
					lostPips += -pips;
					losses++;
					accLostVol += vol;
				}				
				total++;
			}
		}
				
		double avg = (winPips-lostPips)*1.0/total;	
		double avgWin = winPips*1.0/wins;
		double avgLoss = lostPips*1.0/losses;
		double winPer= wins*100.0/total;		
		double pf = winPips*1.0/lostPips;
		
		double avgVolWin = accWinVol*1.0/wins;
		double avgLostWin = accLostVol*1.0/losses;

		double avgVol = (accWinVol+accLostVol)/total;
		System.out.println(
				thr
				+" "+nbars
				+" "+tp
				+" "+sl
				+" "+minPips
				+" "+volThr1
				+" "+volThr2
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgWin, false)
				+" "+PrintUtils.Print2dec(avgLoss, false)
				+" | "+PrintUtils.Print2dec(pf, false)
				+" | "+PrintUtils.Print2dec(avgVol, false)
				);
	}

	public static void main(String[] args) {
		
		String fileNameES1 = "C:\\fxdata\\futuros\\ticks\\ES1.txt";
		String fileNameES2 = "C:\\fxdata\\futuros\\ticks\\ES2.txt";
		String fileName000 = "C:\\fxdata\\futuros\\ticks\\ES_2009_2016_ticks.txt";
		String fileName00 = "C:\\fxdata\\futuros\\ticks\\ES_2009_2012_ticks.txt";
		String fileName0 = "C:\\fxdata\\futuros\\ticks\\ES_2013_2016_ticks.txt";
		String fileNameES1m = "C:\\fxdata\\ES.txt";

		
		ArrayList<String> fileNames = new ArrayList<String>();
		fileNames.add(fileNameES1);
		fileNames.add(fileNameES2);
		fileNames.add(fileName000);
		fileNames.add(fileName00);
		fileNames.add(fileName0);
		fileNames.add(fileNameES1m);
		
		ArrayList<Tick> data = null;
		ArrayList<Integer> maxMins = null;
		int limit = 5;
		//int limit = fileNames.size()-1;
		for (int i=5;i<=limit;i++){
			String fileName = fileNames.get(i);
			if (data!=null) data.clear();
			if (maxMins!=null) maxMins.clear();
			
			ArrayList<QuoteShort> data1 = DAO.retrieveDataDOW(fileName, DataProvider.KIBOTES);
			ArrayList<QuoteShort> maxMins1 = TradingUtils.calculateMaxMinByBarShortAbsolute(data1);
			//data = TickUtils.readTicksDaveMinutes(fileName,y1,y2);
			//maxMins = TickUtils.calculateMaxMinMinuteBar(data);
			System.out.println("data: "+data1.size());
			for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+7;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+23;
					
					//TestingCountTicks.TestLegsWins(data,h1,h2, thr, tp, sl);
					for (int thr=250;thr<=10000;thr+=250){
						for (int nbars = 1000;nbars <=1000;nbars+=1){	
							for (int tp=10;tp<=10;tp++){
								for (int sl = 99999;sl<=99999;sl++){
									for (int minPips=0;minPips<=0;minPips+=1){
										for (int offset=1;offset<=1;offset++){
											for (int vol1=0;vol1<=0;vol1+=500){
												for (int vol2=vol1+1000;vol2<=vol1+1000;vol2+=200){
													LastTouch.lastTouch(data1,maxMins1,y1,y2,h1,h2,minPips,thr, nbars,tp,sl,offset,vol1,vol2);
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
		}//limit
	}

}
