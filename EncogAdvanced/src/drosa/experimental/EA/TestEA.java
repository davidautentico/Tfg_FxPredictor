package drosa.experimental.EA;

import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.CurrencyType;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class TestEA {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\USDCAD_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\GBPAUD_UTC_5 Mins_Bid_2006.03.22_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2005.10.07_2014.11.27.csv";		
		//String path5m   = "c:\\fxdata\\AUDNZD_UTC_5 Mins_Bid_2006.12.12_2014.11.27.csv";
		
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
  		//ArrayList<Quote> dailyData 	= ConvertLib.createDailyData(data5m);
  		//ArrayList<Quote> data10m  = ConvertLib.convert(data5m,2);
  		//ArrayList<Quote> data15m  = ConvertLib.convert(data5m,3);
  		//ArrayList<Quote> data20m  = ConvertLib.convert(data5m,4);
  		//ArrayList<Quote> data1h = ConvertLib.convert(data5m,12);
  		
		ArrayList<QuoteShort> data5mS  = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		//ArrayList<QuoteShort> dailyDataS  = QuoteShort.convertQuoteArraytoQuoteShort(dailyData);
		//ArrayList<QuoteShort> data10mS  = QuoteShort.convertQuoteArraytoQuoteShort(data10m);
		//ArrayList<QuoteShort> data20mS  = QuoteShort.convertQuoteArraytoQuoteShort(data20m);
		//ArrayList<QuoteShort> data15mS  = QuoteShort.convertQuoteArraytoQuoteShort(data15m);
		//ArrayList<QuoteShort> data1hS  = QuoteShort.convertQuoteArraytoQuoteShort(data1h);
		
		//QuoteShort.saveToDisk(data5mS,"c:\\data5digits.csv");
		ArrayList<QuoteShort> data = null;
		
		data = data5mS;
		
		CurrencyType currencyType = CurrencyType.USD_BASED;
		double comm = 1.5;
		if (path5m.contains("EURGBP")){
			comm = 1.4;
			currencyType = CurrencyType.CROSS;
		}
		if (path5m.contains("EURAUD")){
			comm = 1.7;
			currencyType = CurrencyType.CROSS;
		}
		if (path5m.contains("EURUSD")){
			comm = 1.6;
			currencyType =  CurrencyType.USD_BASED;
		}
		if (path5m.contains("GBPUSD")){
			comm = 1.8;
		}
		if (path5m.contains("AUDUSD")){
			comm = 1.5;
		}
		
		int begin		= 200000;
		//begin = 220000;		
		int numBins 	= 1;
		int binSpread 	= 200000;
		int end   		= data.size();
		
		begin       = 1;
		binSpread 	= 60000;
		end   		= 900000;
		numBins     = 400;
		
		
		int tp1 = 12;
		int tp2 = 12;
		int tpInc = 1;
		int sl1 = 26;
		int sl2 = 26;
		int slInc = 1;
		double risk1     = 5.0;
		double risk2     = 5.0;
		double riskInc   = 0.25;
		double riskExtra1 = 20.0;
		double riskExtra2 = 20.0;
		double riskExtraInc = 5;
		int maxPos1 = 5;
		int maxPos2 = 5;
		double capital = 500;		
		double off1 = 0.7;
		double off2 = 0.7;
		double offInc = 0.1;
		int openDiffdf1 = 5;
		int openDiffdf2 = 5;
		int openDiffInc = 10;
		int hCloseOffset1 = 0;
		int hCloseOffset2 = 0;
		int offsetOC1 = 0;
		int offsetOC2 = 0;
		int offsetOCInc = 1;
		int diffOpenParam1 = 20;
		int diffOpenParam2 = 20;
		int diffOpenParamInc = 1;
		int brokerLeverage = 400;
		int bar1 = 150;
		int bar2 = 600;
		int barInc = 50;
		comm = 1.30;
		boolean digits5=true;
		int bePips1 = 0;
		int bePips2 = 0;
		double tpPips1 = 0.7;
		double tpPips2 = 0.7;
		double tpPipsInc = 0.1;
		
		//ArrayList<QuoteShort> maxMin = TradingUtils.calculateMaxMinByBarShort(data,bar1);
		//QuoteShort.saveToDiskExtra(maxMin, "c:\\maxMin5digits.csv");
		
		System.out.println("TEST");
		
		
		begin       = data.size()-40000;
		begin       = 400000;
		binSpread 	= 900000;
		end   		= 900000;
		numBins        = 1;
		
		//for (begin=1;begin<=900000;begin+=binSpread){
			//end = begin+binSpread;
			//System.out.println("BEGIN "+begin);
		for (int h=0;h<=0;h++){
			System.out.println(h);
			for (int m=0;m<=0;m++){
				for (comm=1.45;comm<=1.45;comm+=0.05)
				for (bar1=10;bar1<=10;bar1+=50){
					bar2 = bar1;
					//AlgoFunctions.checkAlgoBySLTPBins(data,null,"0 1 2 3 4 5 6 7 8 9 23",String.valueOf(m), begin, end,numBins,binSpread,
					AlgoFunctions.checkAlgoBySLTPBins(data,null,"0 1 2 3 4 5 6 7 8 9 23","0 1 2 3 4 5 6 7 8 9 10 11", begin, end,numBins,binSpread,
					//AlgoFunctions.checkAlgoBySLTPBins(data,null,String.valueOf(h),"0 1 2 3 4 5 6 7 8 9 10 11", begin, end,numBins,binSpread,
					//AlgoFunctions.checkAlgoBySLTPBins(data,null,String.valueOf(h), begin, end,numBins,binSpread,
							tp1, tp2,tpInc,sl1,sl2,slInc, 
							bar1,bar2,barInc,
							brokerLeverage,
							risk1,risk2,riskInc, 
							riskExtra1,riskExtra2,riskExtraInc,
							maxPos1,maxPos2,off1,off2,offInc,
							openDiffdf1,openDiffdf2,openDiffInc,
							hCloseOffset1,hCloseOffset2,
							offsetOC1,offsetOC2,offsetOCInc,
							diffOpenParam1,diffOpenParam2,diffOpenParamInc,
							capital, currencyType, comm,bePips1,bePips2,tpPips1,tpPips2,tpPipsInc,
							false,digits5);
				}
			}
		}
		
		//}
		
		/*for (int h=0;h<=23;h++)
		AlgoFunctions.checkAlgoBySLTPBins(data,dailyDataS,String.valueOf(h), begin, end,numBins,binSpread,
				tp1, tp2,tpInc,sl1,sl2,slInc, 
				bar1,bar2,barInc,
				brokerLeverage,risk1,risk2,riskInc, 
				maxPos1,maxPos2,off1,off2,offInc,
				openDiffdf1,openDiffdf2,openDiffInc,
				hCloseOffset1,hCloseOffset2,
				offsetOC1,offsetOC2,offsetOCInc,
				diffOpenParam1,diffOpenParam2,diffOpenParamInc,
				capital, currencyType, comm,false,digits5);*/
		
	
	}

}
