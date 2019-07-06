package drosa.phil.strategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestStrategy {
	
	private static void addTradeToArray(ArrayList<StrategyTrade> trades,StrategyTrade trade){
		for (int i=0;i<trades.size();i++){
			if (trade.getOpenCal().getTimeInMillis()<=trades.get(i).getOpenCal().getTimeInMillis()){
				//System.out.println("se añade en posicion "+i+" "+trade.toString());
				trades.add(i,trade);
				return;
			}
		}
		trades.add(trade);
	}
	
	public static void addToStrategyResult(StrategyResult allRes,
			StrategyResult res) {
		// TODO Auto-generated method stub
		ArrayList<StrategyTrade> trades = res.getTrades();
		for (int i=0;i<trades.size();i++){
			addTradeToArray(allRes.getTrades(),trades.get(i));
		}
	}

	public static ArrayList<String> retrieveFileNames(String path) {
		// TODO Auto-generated method stub
		ArrayList<String> names = new ArrayList<String>();
		
		File dir = new File(path);
		String[] files = dir.list();
		for (int i=0;i<files.length;i++){
			if (files[i].contains("_.csv")){
				names.add(files[i]);
			}
		}
		
		return names;
	}
	
	public static void calculateMaxAdversion(String header,ArrayList<Quote> data,StrategyResult allRes,Calendar from,Calendar to,
			int day1,int day2,int h1,int h2,int tp,int sl){
		
		Calendar cal = Calendar.getInstance();
		ArrayList<Integer> maxAdvs = new ArrayList<Integer>();
		ArrayList<StrategyTrade> trades = allRes.getTrades();
		
		int totalWins=0;
		int totalLosses=0;
		for (int i=0;i<trades.size();i++){
			StrategyTrade trade = trades.get(i);
			Calendar tradeCal = trade.getCal();
			
			cal.setTimeInMillis(trade.getCal().getTimeInMillis());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//System.out.println("cal: "+DateUtils.datePrint(cal));
			
			if (cal.getTimeInMillis()<from.getTimeInMillis() || cal.getTimeInMillis()>to.getTimeInMillis()) continue;
			if (dayWeek<day1 || dayWeek>day2) continue;
			if (h<h1 || h>h2) continue;
			
			boolean win = trade.isWin();
			if (win){
				double entry = trade.getEntry();
				double slValue = trade.getEntry()-0.0001*trade.getSl(); //para un BUY
				boolean buy = true;
				if ( trade.getTradeType()==TradeType.SELL){
					slValue = trade.getEntry()+0.0001*trade.getSl();
					buy = false;
				}
				
				int index = TradingUtils.getMinuteIndex(data,  tradeCal);
				if (index!=-1){
					//System.out.println("indice= "+DateUtils.datePrint(data.get(index+1).getDate()));
					int maxAdv = TradingUtils.calculateMaxAdv(data,index,entry,tp,buy);
					maxAdvs.add(maxAdv);
					//System.out.println(trade.toString()+" maxAdv="+maxAdv);
				}
				totalWins++;
			}
			else totalLosses++;
		}
		int totalTrades = totalWins+totalLosses;
		//System.out.println("total trades: "+totalTrades+" "+PrintUtils.Print2dec(totalWins*100.0/totalTrades, false)+"%");
		studyMaxAdv(maxAdvs,totalTrades,tp);
	}
	
	private static void studyMaxAdv(ArrayList<Integer> maxAdvs,int totalTrades,int tp) {
		// TODO Auto-generated method stub
		MathUtils.summary("maxAdv", maxAdvs);
		int count0=0;
		int count5=0;
		int count10=0;
		int count15=0;
		int count20=0;
		int count30=0;
		int count40=0;
		int count50=0;
		int count60=0;
		int count70=0;
		int count75=0;
		for (int i=0;i<maxAdvs.size();i++){
			int val = maxAdvs.get(i);
			if (val<=0) count0++;
			if (val<=5) count5++;
			if (val<=10) count10++;
			if (val<=15) count15++;
			if (val<=20) count20++;
			if (val<=30) count30++;
			if (val<=40) count40++;
			if (val<=50) count50++;
			if (val<=60) count60++;
			if (val<=70) count70++;
			if (val<=75) count75++;
		}
		int total = maxAdvs.size();
		total = totalTrades;
		System.out.println(
				PrintUtils.Print(count0*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count0*100.0/total,tp,1),false)+") "
				+PrintUtils.Print(count5*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count5*100.0/total,tp,6),false)+") "
				+PrintUtils.Print(count10*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count10*100.0/total,tp,11),false)+") "
				+PrintUtils.Print(count15*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count15*100.0/total,tp,16),false)+") "
				+PrintUtils.Print(count20*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count20*100.0/total,tp,21),false)+") "
				+PrintUtils.Print(count30*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count30*100.0/total,tp,31),false)+") "
				+PrintUtils.Print(count40*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count40*100.0/total,tp,41),false)+") "
				+PrintUtils.Print(count50*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count50*100.0/total,tp,51),false)+") "
				+PrintUtils.Print(count60*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count60*100.0/total,tp,61),false)+") "
				+PrintUtils.Print(count70*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count70*100.0/total,tp,71),false)+") "
				+PrintUtils.Print(count75*100.0/total)+"% ("+PrintUtils.Print2dec(printPF(count70*100.0/total,tp,76),false)+") "
				);
	}
	
	public static double printPF(double per, int tp,int sl){
		//System.out.println((per*tp*1.0)+" "+((100.0-per)*sl)+" "+sl);
		double pf = ((per*tp*1.0)-(100.0-per)*sl)/100.0;
		return pf;
	}

	public static void strategyStudy(String header, StrategyResult res,
			Calendar from,Calendar to,
			int day1,int day2,int h1,int h2,int tp,int sl){
		
		int wins = 0;
		int totalTrades = 0;
		int bestTrack = 0;
		int worstTrack = 0;
		int actualTrack=0;
		int actualWorstTrack = 0;
		double avgTimeDiff = 0;
		ArrayList<StrategyTrade> trades = res.getTrades();
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		for (int i=0;i<trades.size();i++){
			StrategyTrade trade = trades.get(i);
			cal.setTimeInMillis(trade.getOpenCal().getTimeInMillis());
			cal2.setTimeInMillis(trade.getCloseCal().getTimeInMillis());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//System.out.println("cal: "+DateUtils.datePrint(cal));
			
			if (cal.getTimeInMillis()<from.getTimeInMillis() || cal.getTimeInMillis()>to.getTimeInMillis()) continue;
			if (dayWeek<day1 || dayWeek>day2) continue;
			if (h<h1 || h>h2) continue;
			
			if (trade.isWin()){
				wins++;
				actualTrack++;
				
				if (actualWorstTrack>=worstTrack) worstTrack = actualWorstTrack;
				actualWorstTrack = 0;
			}else{
				if (actualTrack>=bestTrack) bestTrack = actualTrack;
				actualTrack=0;
				
				actualWorstTrack++;
				//System.out.println(trade.toString());
			}
			//System.out.println(trade.toString());
			double diff = (cal2.getTimeInMillis()-cal.getTimeInMillis())*1.0/60000;//min
			avgTimeDiff += diff;
			totalTrades++;
		}
		if (actualTrack>=bestTrack) bestTrack = actualTrack;
		if (actualWorstTrack>=worstTrack) worstTrack = actualWorstTrack;
		double pf =wins*1.0*tp/((totalTrades-wins)*1.0*sl);
		System.out.println(header+" wins% "+totalTrades+" ("+wins+"-"+(totalTrades-wins)+")"
				+" "+PrintUtils.Print2dec(wins*100.0/totalTrades, false)+"%"
				+" avgOpenTime(min)="+PrintUtils.Print2dec(avgTimeDiff/totalTrades, false)
				+" maxW="+bestTrack
				+" maxL="+worstTrack
				+" pf="+PrintUtils.Print2dec(pf, false));

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "C:\\fxdata\\";
		String fileName = path+"20140416_234954_.csv";
		String file1m = path+"EURUSD_UTC_1 Min_Bid_2011.01.01_2014.05.07.csv";
		
		//ArrayList<Quote> dataI 		 = DAO.retrieveData(file1m, DataProvider.DUKASCOPY_FOREX);
		//ArrayList<Quote> dataS 		 = TestLines.calculateCalendarAdjusted(dataI);
		//ArrayList<Quote> data1m 	 = TradingUtils.cleanWeekendData(dataS);
		
		/*for (int i=0;i<data1m.size();i++){
			System.out.println(data1m.get(i).toString());
		}*/
		
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		int day1 = Calendar.MONDAY+2;
		int day2 = Calendar.MONDAY+2;
		int h1 = 8;
		int h2 = 23;
		int tp = 10;
		int sl = 80;
		
		StrategyResult allRes = new StrategyResult();
		ArrayList<String> fileNames = retrieveFileNames(path);
		int count=0;
		for (int i=0;i<fileNames.size();i++){
			String completePath = path+fileNames.get(i);
			//System.out.println("file: "+completePath);
			//System.out.println("file: "+completePath);
			StrategyResult res = DAO.retrieveStrategyResult(completePath);
			if (res.getSl()==sl && res.getTp()==tp){
				count++;
				addToStrategyResult(allRes,res);
			}
		}
		System.out.println("partidas : "+count);
		from.set(2012,0, 0);
		to.set(2014, 11,31);
		for (day1=Calendar.MONDAY+0;day1<=Calendar.MONDAY+4;day1++){
			day2 = day1+0;
			for (h1=15;h1<=15;h1++){
				h2=h1+8;
				String header = "day1= "+day1+" day2= "+day2+" h1="+h1+" h2="+h2;
				TestStrategy.strategyStudy(header,allRes,from,to, day1, day2, h1, h2,tp,sl);
				//TestStrategy.calculateMaxAdversion(header, data1m, allRes, from, to, day1, day2, h1, h2, tp, sl);
			}
		}
		
		
		
	}

	

}
