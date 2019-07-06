package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestTrendTrading {
	
	public static void trading(String header,ArrayList<QuoteShort> data,ArrayList<Trend> trends,int h1,int h2,int tp,int sl){
	
		int totalTrades = 0;
		int wins = 0;
		int losses = 0;
		Calendar cal = Calendar.getInstance();	
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			int type = t.getType();
			int index0 = t.getIndex0();
			if (t.getIndex0()<0) continue;
			QuoteShort q0 = data.get(t.getIndex0());
			QuoteShort.getCalendar(cal, q0);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h<h1 || h>h2) continue;
			
			int tpValue = q0.getClose5()+tp*10;
			int slValue = q0.getClose5()-sl*10;
			boolean isBull = true;
			if (type==-1){
				tpValue = q0.getClose5()-tp*10;
				slValue = q0.getClose5()+sl*10;
				isBull = false;
			}
			
			int tpIndex = -1;
			int slIndex = -1;
			if (isBull){
				tpIndex = TradingUtils.getMaxMinIndex(data, index0+1, data.size()-1, tpValue, true);
				slIndex = TradingUtils.getMaxMinIndex(data, index0+1, data.size()-1, slValue, false);
			}else{
				tpIndex = TradingUtils.getMaxMinIndex(data, index0+1, data.size()-1, tpValue, false);
				slIndex = TradingUtils.getMaxMinIndex(data, index0+1, data.size()-1, slValue, true);
			}
			
			if (tpIndex==-1 && slIndex==-1) continue;//indeterminado
			if (tpIndex!=-1 && slIndex!=-1){
				if (slIndex<=tpIndex) losses++;
				else wins++;
				continue;
			}
			if (slIndex!=-1){
				losses++;
				continue;
			}
			if (tpIndex!=-1) wins++;
		}
		totalTrades = wins+losses;
		double winPer = wins*100.0/totalTrades;
		double exp = ((winPer*tp)-((100.0-winPer)*sl))/100.0;
		System.out.println(header+" "+tp+" "+sl
				+" "+totalTrades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(exp, false)
				);
	}

	public static void main(String[] args) {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.12.31_2015.10.06.csv";
		
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathEURJPY);
		paths.add(pathGBPUSD);paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		
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
			
			int beginInicial = 1;
			int begin = beginInicial;
			int end = data.size()-1;
			int boxes = 1;
			int boxSize = end/boxes;
			System.out.println("total data: "+data.size()+" "+boxSize);
			for (int box=1;box<=boxes;box++){
				begin = (box-1)*boxSize;
				if (begin<=beginInicial) begin = beginInicial;
				end = begin+boxSize;
				for (int minSize=20;minSize<=20;minSize+=5){
					for (int thr=20;thr<=20;thr+=5){
						ArrayList<Trend> trends = Trend.calculateTrends(data, begin, end, minSize,thr,true);
						for (int tp=(int) (minSize*1.0);tp<=minSize*1.0;tp+=minSize){
							for (int sl=1*tp;sl<=1*tp;sl+=tp){
								//Trend.printSummary("",trends);
								for (int h1=0;h1<=0;h1++){
									for (int h2=h1;h2<=22;h2++){
										for (int h3=h2+1;h3<=h2+1;h3++){
											for (int h4 = h3+1;h4<=h3+1;h4++){
												String header = begin+" "+end;
												for (int offset=0;offset<=0;offset++)
												Trend.printSummaryDaily2Periodsv2(header,trends,h1,h2,offset,h3,h4);
												//Trend.printSummaryDaily("",trends,h1,h2);
												
												//String header=path.substring(10, 16)+" "+minSize+" "+thr+" "+h1+" "+h2;
												//Trend.printSummary(header,trends);
												//Trend.printSummaryByHour(header,trends,2004,2015,0,8);
												//TestTrendTrading.trading(header, data, trends, h1, h2, tp, sl);
											}
										}
									}
								}
							}
						}
					}
				}
			}//box
			
		}//currencies
	
	}

}
