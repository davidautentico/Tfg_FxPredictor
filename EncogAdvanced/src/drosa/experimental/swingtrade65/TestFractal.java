package drosa.experimental.swingtrade65;

import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.billyt.TestSimplyDailyBreak;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestFractal {
	
	public static void doTradeReverseExit(ArrayList<QuoteShort> data,
			ArrayList<Integer> fractalsH,ArrayList<Integer> fractalsL
			){
	
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int entry = 0;
		int tpValue = -1;
		int slValue = -1;
		int position = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			
			int fractalH = fractalsH.get(i); //fractal High vigente
			int fractalL = fractalsL.get(i); //fractal Low vigente
			
			if (position==1){//actual position is LONG
				if (q.getHigh5()>=fractalH){
					//
				}else if (q.getLow5()<=fractalL){//REVERSE position
					//cerramos posicion y abrimos una nueva
					int pips = q.getOpen5()-entry;
					if (pips>=0){
						wins++;
						winPips +=pips;
					}else{
						losses++;
						lostPips += (-pips);
					}
					position = 0;
				}
			}else if (position==-1){//actual position is SHORT
				if (q.getHigh5()>=fractalH){
					//cerramos posicion y abrimos una nueva
					int pips = entry-q.getOpen5();
					if (pips>=0){
						wins++;
						winPips +=pips;
					}else{
						losses++;
						lostPips += (-pips);
					}
					position = 0;
				}else if (q.getLow5()<=fractalL){
					//
				}
			}
			
			if (position==0){
				if (q.getHigh5()>=fractalH){
					int diffPips = fractalH-fractalL;
					int pips  = diffPips;
					if (diffPips<=0 || diffPips>=1000) pips = 1000;//100
					entry = fractalH;
					slValue = entry-pips;
					position = 1;
				}else if (q.getLow5()<=fractalL){
					int diffPips = fractalH-fractalL;
					int pips  = diffPips;
					if (diffPips<=0 || diffPips>=1000) pips = 1000;//100
					entry = fractalL;
					slValue = entry+pips;
					position = -1;
				}
			}
		}
		
		int totalTrades = wins+losses;
		double winPer = wins*100.0/totalTrades;
		double exp = (winPips*wins-lostPips*losses)*0.1/totalTrades;
		double pf = (winPips*wins*1.0)/(lostPips*losses);
		
		System.out.println(
				totalTrades
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(exp, false)
				+" "+PrintUtils.Print2(pf, false)
				);
	}

	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		
		int limit = paths.size()-1;
		limit = 0;
		for (int i = 0;i<=limit;i++){
			Sizeof.runGC ();
			String path = paths.get(i);	
			ArrayList<Quote> dataI 		= null;
			ArrayList<Quote> dataS 		= null;
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				//provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				//provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				//provider="dukasc";
			}										
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	
		  	dataS.clear();
			ArrayList<QuoteShort> data5mS  = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			data5m.clear();
			ArrayList<QuoteShort> data4h = ConvertLib.create4H(data5mS);
			
			ArrayList<QuoteShort> data = null;
			
			data = data4h;
			System.out.println(data.size());
			/*for (int d=0;d<data.size();d++){
				System.out.println(data.get(d).toString());
			}*/
			
			for (int n=3;n<=9999;n+=2){
				ArrayList<Integer> fractalsH = TradingUtils.calculateFractals(data,n,true);
				ArrayList<Integer> fractalsL = TradingUtils.calculateFractals(data,n,false);
				
				int lastFractal = -1;
				int diff = 0;
				int fracs = 0;
				for (int f=0;f<fractalsL.size();f++){
					int fi = fractalsL.get(f);
					if (fi<0) continue;
					QuoteShort qf = data.get(fi);
					//System.out.println(data.get(f).toString()+" || "+fi+" "+qf.getHigh5());
					if (fi!=lastFractal){
						if (lastFractal>=0){
							diff+=(fi-lastFractal);
							fracs++;
						}
						lastFractal = fi;
					}
				}
				double avg = diff*1.0/fracs;
				System.out.println(n
						+" "+PrintUtils.Print2(avg, false)
						+" "+PrintUtils.Print2(avg*1.0/n, false)
						);
			}
			
			//TestFractal.doTradeReverseExit(data, fractalsH, fractalsL);
			data5mS.clear();
		}
	}//main

}
