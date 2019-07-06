package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestPriceBufferGlobal {

	public static void doTest(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			ArrayList<StrategyConfig> configs,
			int maxTrades,
			int idxTest
			){
		//
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			StrategyConfig config = configs.get(h);
			
			//modulo de entrada
			if (positions.size()<maxTrades){
				if (config!=null && config.isEnabled()){
					int thr = config.getThr();
					int begin = i-config.getBarsBack();
					int end = i-1;
					int index = TestPriceBuffer.getMinMaxBuff(maxMins,begin,end,thr);
					if (index>=0){
						int maxMin = maxMins.get(index);						
						if (maxMin>=thr){
							PositionCore pos = new PositionCore();
							pos.setEntry(q.getOpen5());
							pos.setTp(q.getOpen5()-10*config.getTp());
							pos.setSl(q.getOpen5()+10*config.getSl());
							pos.setEntryIndex(i);
							pos.setMaxIndex(i+config.getMaxBars());
							pos.setPositionType(PositionType.SHORT);
							pos.setIndexMinMax(end-index);
							positions.add(pos);
						}else if (maxMin<=-thr){
							PositionCore pos = new PositionCore();
							pos.setEntry(q.getOpen5());
							pos.setTp(q.getOpen5()+10*config.getTp());
							pos.setSl(q.getOpen5()-10*config.getSl());
							pos.setEntryIndex(i);
							pos.setMaxIndex(i+config.getMaxBars());
							pos.setPositionType(PositionType.LONG);
							pos.setIndexMinMax(end-index);
							positions.add(pos);
						}
					}
					
				}
			}
						
			//evaluacion trades			
			int j = 0;			
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				if (pos.getPositionType()==PositionType.SHORT){
					if (i>=pos.getMaxIndex()){						
						isClosed = true;
					}else{
						if (q.getHigh5()>=pos.getSl()){
							isClosed = true;
						}else if (q.getLow5()<=pos.getTp()){
							isClosed = true;
						}
					}
					
					if (isClosed){
						pips = pos.getEntry()-q.getClose5();
					}
				}else if (pos.getPositionType()==PositionType.LONG){
					if (i>=pos.getMaxIndex()){						
						isClosed = true;
					}else{
						if (q.getLow5()<=pos.getSl()){
							isClosed = true;
						}else if (q.getHigh5()>=pos.getTp()){
							isClosed = true;
						}
					}
					if (isClosed){
						pips = q.getClose5()-pos.getEntry();
					}
				}
				
				if (isClosed){
					if (idxTest==-1 ||pos.getIndexMinMax()==idxTest){
						if (pips>=0){
							wins++;
							winPips+=pips;
						}else{
							losses++;
							lostPips+=-pips;
						}
					}
					positions.remove(j);
				}else{
					j++;
				}
			}
		}//data
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		
		
		System.out.println(
				maxTrades
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				
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
			
			//0,1,2: 200,6,8,48,240 
			//0: 	140,10,11,88,240
			//1: 	190,12,11,88,240			
			//2: 	180,12,11,88,240			
			//3:	400,12,11,88,240 			
			//4:	450,1,11,88,84  			
			//5:    450,6,10,50,120 
			//6:    200,3,20,60,36  
			//7:	500,1,20,60,36
			//8: 	550,4,13,66,12 
			//9:	500,2,8,56,17
			
			
			//19:   500,1,20,60,228
			//20:  1200,2,17,51,192
			//22:   850,2,10,60,228
			//23: 	200,6,11,88,144
		
			
			ArrayList<StrategyConfig> configs = new ArrayList<StrategyConfig>();
			for (int c=0;c<=23;c++) configs.add(null);
			
			StrategyConfig config = new StrategyConfig();config.setParams(0, 140, 11, 88, 240, 10, true);configs.set(0, config);
			StrategyConfig config1 = new StrategyConfig();config1.setParams(1, 190, 11, 88, 240, 12, true);configs.set(1, config1);
			StrategyConfig config2 = new StrategyConfig();config2.setParams(2, 180, 11, 88, 240, 12, true);configs.set(2, config2);
			StrategyConfig config3 = new StrategyConfig();config3.setParams(3, 400, 11, 88, 240, 12, true);configs.set(3, config3);
			StrategyConfig config4 = new StrategyConfig();config4.setParams(4, 450, 11, 88, 84, 1, true);configs.set(4, config4);
			StrategyConfig config5 = new StrategyConfig();config5.setParams(5, 450, 10, 50, 120, 6, true);configs.set(5, config5);
			StrategyConfig config6 = new StrategyConfig();config6.setParams(6, 200, 20, 60, 36, 3, true);configs.set(6, config6);
			StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 500, 20, 60, 36, 1, true);configs.set(7, config7);
			StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 550, 13, 66, 12, 4, true);configs.set(8, config8);
			StrategyConfig config9 = new StrategyConfig();config9.setParams(9, 500, 8, 56, 17, 2, true);configs.set(9, config9);
			StrategyConfig config23 = new StrategyConfig();config23.setParams(23, 200, 11, 88, 144, 6, true);configs.set(23, config23);
			
			
			for (int y1=2012;y1<=2012;y1++){
				int y2 = y1+4;
				for (int maxTrades=1;maxTrades<=100;maxTrades++){
					TestPriceBufferGlobal.doTest(data,maxMins,y1,y2,configs,maxTrades,-1);
				}
			}
			
			/*for (int nback=12;nback<=12;nback++){
				if (nback>0){
					for (int c=0;c<=23;c++){
						if (configs.get(c)!=null){
							configs.get(c).setBarsBack(nback);
							
						}
					}
				}
				
				for (int y1=2009;y1<=2009;y1++){
					int y2 = y1+7;
					for (int maxTrades=20;maxTrades<=20;maxTrades++){						
						for (int idxTest=0;idxTest<=nback;idxTest++){
							TestPriceBufferGlobal.doTest(data,maxMins,y1,y2,configs,maxTrades,idxTest);
						}
					}
				}
			}*/
		}

		System.out.println("programa finalizado");

	}

}
