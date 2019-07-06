package drosa.experimental.traderDale;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.data.DataUtils;
import drosa.data.TickQuote;
import drosa.experimental.PositionShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TestTraderDale {
	
	
	public static void readTraderDaleCSV(String fileName,ArrayList<PositionTraderDale> positionsDale){
		
	}
	
	
	//evalua cada posicion
		public static void testDaleData(
				ArrayList<PositionTraderDale> positionsDale,
				ArrayList<TickQuote> data,
				int tp,
				int sl,
				int debug
				){
			

			ArrayList<Integer> results = new ArrayList<Integer>();
			results.add(0);
			results.add(0);
			results.add(0);
			
			// = DataUtils.retrieveTickQuotes(aFileName,1);
			
			ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
			for (int i=0;i<positionsDale.size();i++){
				PositionTraderDale posDale = positionsDale.get(i);
				//override parameters
				//posDale.setFileName(aFileName);
				posDale.setTp(tp);
				posDale.setSl(sl);
				
				//String fileName = posDale.fileName;
				
				
				
				positions.clear();
				PositionShort posShort = new PositionShort();
				posShort.setEntry(posDale.getEntry());
				posShort.setPositionStatus(PositionStatus.PENDING);
				posShort.getPendingCal().set(Calendar.DAY_OF_MONTH, posDale.getDay());
				posShort.getPendingCal().set(Calendar.MONTH,posDale.getMonth()-1);
				posShort.getPendingCal().set(Calendar.YEAR,posDale.getYear());
				if (posDale.getMode()==1){
					posShort.setPositionType(PositionType.LONG);
					posShort.setTp(posDale.getEntry()+10*posDale.getTp());
					posShort.setSl(posDale.getEntry()-10*posDale.getSl());
				}else if (posDale.getMode()==-1){
					posShort.setPositionType(PositionType.SHORT);
					posShort.setTp(posDale.getEntry()-10*posDale.getTp());
					posShort.setSl(posDale.getEntry()+10*posDale.getSl());
				} 
				positions.add(posShort);
				
				TestTraderDale.test(data, positions,results, debug);
			}
			
			int total = results.get(0);
			int wins = results.get(1);
			int losses = results.get(2);
			double winPer = wins*100/total;
			double avg = (wins*tp-losses*sl)*1.0/total;
			double pf = (wins*tp*1.0)/(losses*sl);
			System.out.println(
					tp+" "+sl
					+" || "
					+total
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(pf, false)
					);
			
		}
	
	//evalua cada posicion
	public static void testDale(
			ArrayList<PositionTraderDale> positionsDale,
			String aFileName,
			int tp,
			int sl,
			int debug
			){
		

		ArrayList<Integer> results = new ArrayList<Integer>();
		results.add(0);
		results.add(0);
		results.add(0);
		
		ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(aFileName,1);
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		for (int i=0;i<positionsDale.size();i++){
			PositionTraderDale posDale = positionsDale.get(i);
			//override parameters
			posDale.setFileName(aFileName);
			posDale.setTp(tp);
			posDale.setSl(sl);
			
			String fileName = posDale.fileName;
			
			
			
			positions.clear();
			PositionShort posShort = new PositionShort();
			posShort.setEntry(posDale.getEntry());
			posShort.setPositionStatus(PositionStatus.PENDING);
			posShort.getPendingCal().set(Calendar.DAY_OF_MONTH, posDale.getDay());
			posShort.getPendingCal().set(Calendar.MONTH,posDale.getMonth()-1);
			posShort.getPendingCal().set(Calendar.YEAR,posDale.getYear());
			if (posDale.getMode()==1){
				posShort.setPositionType(PositionType.LONG);
				posShort.setTp(posDale.getEntry()+10*posDale.getTp());
				posShort.setSl(posDale.getEntry()-10*posDale.getSl());
			}else if (posDale.getMode()==-1){
				posShort.setPositionType(PositionType.SHORT);
				posShort.setTp(posDale.getEntry()-10*posDale.getTp());
				posShort.setSl(posDale.getEntry()+10*posDale.getSl());
			} 
			positions.add(posShort);
			
			TestTraderDale.test(data, positions,results, debug);
		}
		
		int total = results.get(0);
		int wins = results.get(1);
		int losses = results.get(2);
		double winPer = wins*100/total;
		double avg = (wins*tp-losses*sl)*1.0/total;
		double pf = (wins*tp*1.0)/(losses*sl);
		System.out.println(
				tp+" "+sl
				+" || "
				+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}
	
	
	//evalua cada posicion
	public static void test(ArrayList<TickQuote> data,
			ArrayList<PositionShort> positions,
			ArrayList<Integer> results,
			int debug
			){
		
		int total = results.get(0);
		int wins = results.get(1);
		int losses = results.get(2);
		
		Calendar calPosition = Calendar.getInstance();
		Calendar calActual = Calendar.getInstance();
		for (int j=0;j<positions.size();j++){
			PositionShort pos = positions.get(j);
			int mode = 0;
			int win = 0;
			int posDay = pos.getPendingCal().get(Calendar.DAY_OF_MONTH);
			int posMonth = pos.getPendingCal().get(Calendar.MONTH);
			int posdm = pos.getPendingCal().get(Calendar.DAY_OF_MONTH)*
					(pos.getPendingCal().get(Calendar.MONTH));
			
			if (debug==2)
				System.out.println("[POSITION EVALUATED] "+pos.toString()+" || "+posdm+" "+DateUtils.datePrint(pos.getPendingCal()));
			for (int i=0;i<data.size();i++){
				TickQuote t = data.get(i);
				int dm = t.getDay()*(t.getMonth()-1);
				int h = t.getHh();
				//System.out.println("[cal] "+t.toString()+" || "+dm);
				if (mode==0 
						&& h>=9
						&& posDay == t.getDay()
						&& posMonth == t.getMonth()-1
						&& pos.getPositionStatus()==PositionStatus.PENDING){
					//System.out.println("[MATCHED] day: "+t.toString()+" || "+pos.getEntry());
					if (pos.getPositionType()==PositionType.LONG){//se activa en ask
						if (t.getAsk()<=pos.getEntry()){//ORDENES limit buy
							mode = 1;
							pos.setPositionStatus(PositionStatus.OPEN);
							if (debug==2)
								System.out.println("[LONG TRIGGER] "+t.toString()
									+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl()
									);
						}						
					}else if (pos.getPositionType()==PositionType.SHORT){//se activa en bid
						if (t.getBid()>=pos.getEntry()){
							mode = -1;
							pos.setPositionStatus(PositionStatus.OPEN);
							if (debug==2)
								System.out.println("[SHORT TRIGGER] "+t.toString()
									+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl()
									);
						}
					}
				}else if (mode==1){
					if (t.getBid()>=pos.getTp()){
						win=1;
						mode = 0;
						total++;
						wins++;
						if (debug>=1)
							System.out.println("[LONG TP] "+t.toString());
						pos.setPositionStatus(PositionStatus.CLOSE);
					}else if (t.getBid()<=pos.getSl()){
						win=-1;
						mode = 0;
						total++;
						losses++;
						pos.setPositionStatus(PositionStatus.CLOSE);
						if (debug>=1)
							System.out.println("[LONG SL] "+t.toString());
					}
				}else if (mode==-1){
					if (t.getAsk()<=pos.getTp()){
						win=1;
						mode = 0;
						total++;
						wins++;
						pos.setPositionStatus(PositionStatus.CLOSE);
						if (debug>=1)
							System.out.println("[SHORT TP] "+t.toString());
					}else if (t.getAsk()>=pos.getSl()){
						win=-1;
						mode = 0;
						total++;
						losses++;
						pos.setPositionStatus(PositionStatus.CLOSE);
						if (debug>=1)
							System.out.println("[SHORT SL] "+t.toString());
					}
				}
				
			}
		}
		
		results.set(0, total);
		results.set(1, wins);
		results.set(2, losses);
	}

	public static void main(String[] args) {
		
		//String fileName ="c:\\fxdata\\USDJPY_UTC_Ticks_Bid_2017.04.30_2017.05.18.csv";
		String fileNameEURUSD ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.10.31_2017.05.19.csv";
		String fileNameUSDCAD ="c:\\fxdata\\USDCAD_UTC_Ticks_Bid_2017.03.31_2017.05.19.csv";
		String fileNameAUDUSD ="c:\\fxdata\\AUDUSD_UTC_Ticks_Bid_2016.07.31_2016.12.30.csv";
		String fileNameUSDJPY ="c:\\fxdata\\USDJPY_UTC_Ticks_Bid_2016.10.31_2017.05.19.csv";
		
		//ArrayList<PositionTraderDale> trades = DataUtils.retrieveTraderDaleTrade("c:\\fxdata\\traderDaleUSDJPY.csv");
		//ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileNameUSDJPY,1);
		
		ArrayList<PositionTraderDale> trades = DataUtils.retrieveTraderDaleTrade("c:\\fxdata\\traderDaleAUDUSD.csv");
		ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileNameAUDUSD,1);
				
		
		//ArrayList<PositionTraderDale> trades = DataUtils.retrieveTraderDaleTrade("c:\\fxdata\\traderDaleEURUSD.csv");
		//ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileNameEURUSD,1);
		System.out.println("total: "+data.size()+" "+trades.size());
		for (int tp=10;tp<=10;tp++){
			for (int sl=12;sl<=12;sl+=1){
				TestTraderDale.testDaleData(trades,data,tp,sl, 1);
			}
		}
			

	}

}
