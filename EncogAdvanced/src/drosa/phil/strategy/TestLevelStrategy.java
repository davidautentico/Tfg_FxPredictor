package drosa.phil.strategy;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.phil.LineType;
import drosa.phil.PhilDay;
import drosa.phil.PhilLine;
import drosa.phil.StudyRangeFibs;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestLevelStrategy {

	public static PhilDay findPDay(ArrayList<PhilDay> pDays,Calendar cal,int index){
	
		for (int i=index;i<pDays.size();i++){
			PhilDay actual = pDays.get(i);
			
			if (DateUtils.isSameDay(actual.getDay(), cal)){
				return actual;
			}
		}
		
		return null;
	}
	
	public static void strategyLevelStudy(String header, StrategyResult res,ArrayList<PhilDay> pDays,
			Calendar from,Calendar to,
			int day1,int day2,int h1,int h2,int tp,int sl,LineType testLevel,int pipsL,int pipsH){
		
		int wins = 0;
		int totalTrades = 0;
		int bestTrack = 0;
		int worstTrack = 0;
		int actualTrack=0;
		int actualWorstTrack = 0;
		ArrayList<StrategyTrade> trades = res.getTrades();
		Calendar cal = Calendar.getInstance();
		int index = 0;
		for (int i=0;i<trades.size();i++){ //conseguimos trades
			StrategyTrade trade = trades.get(i);
			cal.setTimeInMillis(trade.getCal().getTimeInMillis());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);			
			if (cal.getTimeInMillis()<from.getTimeInMillis() || cal.getTimeInMillis()>to.getTimeInMillis()) continue;
			if (dayWeek<day1 || dayWeek>day2) continue;
			if (h<h1 || h>h2) continue;
			//System.out.println("trade: "+trade.toString());
			PhilDay pDay = findPDay(pDays,cal,index);
			if (pDay!=null){
				ArrayList<PhilLine> lines = pDay.getLines();
				double LINE=-1;				
				for (int j=0;j<lines.size();j++){
					if (lines.get(j).getLineType()==testLevel)
						LINE = lines.get(j).getValue();
				}
				
				int pipsDiff = TradingUtils.getPipsDiff(trade.getEntry(), LINE);
				if (pipsDiff>=pipsL && pipsDiff<=pipsH && trade.getTradeType()==TradeType.SELL){
					//System.out.println("trade valido: "+trade.toString());
					if (trade.isWin()){
						wins++;
					}
					totalTrades++;
				}
			}			
		}
		System.out.println(header+" totalTrades= "+totalTrades+" wins="+PrintUtils.Print4dec(wins*100.0/totalTrades));
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "C:\\fxdata\\";
		String file1H = path+"\\EURUSD_UTC_Hourly_Bid_2009.01.01_2014.05.14.csv";
		
		
		ArrayList<PhilDay> pDays = StudyRangeFibs.calculateFIBS(file1H,3,100);
		System.out.println("pDays: "+pDays.size());
		
		LineType lineType 	= LineType.FIBR1;
		
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		int day1 = Calendar.MONDAY+2;
		int day2 = Calendar.MONDAY+2;
		int h1 = 8;
		int h2 = 23;
		int tp = 10;
		int sl = 80;
		
		StrategyResult allRes = new StrategyResult();
		ArrayList<String> fileNames = TestStrategy.retrieveFileNames(path);
		int count=0;
		for (int i=0;i<fileNames.size();i++){
			String completePath = path+fileNames.get(i);
			//System.out.println("file: "+completePath);
			StrategyResult res = DAO.retrieveStrategyResult(completePath);
			if (res.getSl()==sl && res.getTp()==tp){
				count++;
				TestStrategy.addToStrategyResult(allRes,res);
			}
		}
		System.out.println("partidas trades : "+count+" "+allRes.getTrades().size());
		from.set(2013,0, 0);
		to.set(2014, 4,31);
		for (int pipsL=-15;pipsL<=-15;pipsL++){
			int pipsH = pipsL+5;
			for (day1=Calendar.MONDAY+0;day1<=Calendar.MONDAY+4;day1++){
				day2 = day1+0;
				for (h1=0;h1<=0;h1++){
					h2=h1+23;
					String header = "day1= "+day1+" day2= "+day2+" h1="+h1+" h2="+h2;
					//TestStrategy.strategyStudy(header,allRes,from,to, day1, day2, h1, h2,tp,sl);
					TestLevelStrategy.strategyLevelStudy(header,allRes,pDays,from,to, day1, day2, h1, h2,tp,sl,lineType,pipsL,pipsH);
					//TestStrategy.calculateMaxAdversion(header, data1m, allRes, from, to, day1, day2, h1, h2, tp, sl);
				}
			}
		}
	}

}
