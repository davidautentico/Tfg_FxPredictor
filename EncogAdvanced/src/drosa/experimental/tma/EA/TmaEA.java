package drosa.experimental.tma.EA;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.SuperStrategy;
import drosa.experimental.TestOscillations;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.IndicatorLib;
import drosa.phil.TMA;
import drosa.phil.TestLines;
import drosa.phil.TmaShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TmaEA {
	
	public static void testTMA(String header,ArrayList<QuoteShort> data,int begin,int end,
			ArrayList<TmaShort> tmas,String hours,int pips1,int pips2,int sl,int tp,
			double balance,double risk,double comm,int maxAllowed){
		
		double actualBalance = balance;
		double extraNeeded   = 0;
		double profit$        = 0;
		double losses$        = 0;
		double maxBalance = balance;
		double maxDD      = 0.0;
		int totalOpen = 0;
		int maxOpens = 0;
		int wins = 0;
		int losses = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-2) end = data.size()-2;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		PositionShort pos = new PositionShort();
		for (int i=begin;i<=end;i++){
			TmaShort tma = tmas.get(i);
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			int diffUpClose = q.getClose5()-tma.getUpper();
			int diffDownClose = tma.getLower()-q.getClose5();
			
			int allowed = allowedHours.get(h);
			
			if (allowed==1){
				if (diffUpClose>=pips1*10 && diffUpClose<=pips2*10){
					int entryValue = q.getClose5()+20;
					int slValue    = entryValue+sl*10;
					int tpValue    = entryValue-tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.SHORT);
					pos.setPendingIndex(i);
					pos.setRisk(risk);
					positions.add(pos);
				}else if (diffDownClose>=pips1*10 && diffDownClose<=pips2*10){
					int entryValue = q.getClose5()-20;
					int slValue    = entryValue-sl*10;
					int tpValue    = entryValue+tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.LONG);
					pos.setPendingIndex(i);
					pos.setRisk(risk);
					positions.add(pos);
				}
			}//allowed
			
			int opens = 0;
			for (int s=0;s<positions.size();s++){
				PositionShort p = positions.get(s);
				double posRisk = p.getRisk();
				if (p.getPositionStatus()==PositionStatus.PENDING){
					long diffExpiration = i-p.getPendingIndex();
					if (q1.getLow5()<=p.getEntry() && p.getEntry()<=q1.getHigh5() 
							){
						//margen requerido
						double minBalance = TestOscillations.getMinBalanceRequiered(actualBalance,posRisk,maxAllowed,sl);
						if (actualBalance<minBalance){
							extraNeeded += minBalance-actualBalance;
							actualBalance = minBalance;
						}
						
						long microLots = TestOscillations.calculateMicroLots(actualBalance,posRisk,maxAllowed,sl);
						
						p.setPositionStatus(PositionStatus.OPEN);
						p.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
						p.setOpenIndex(i+1);
						p.setMicroLots(microLots);
						totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
						//System.out.println("OPEN "+microLots);
						if (totalOpen>maxOpens) maxOpens = totalOpen;
					}
					
				}
				if (p.getPositionStatus()==PositionStatus.OPEN){
					opens++;
					boolean closed = false;
					double posPips = 0;
					if (p.getPositionType()==PositionType.LONG){
						int posSL = p.getEntry()-p.getSl();
						//int movedTPPips = (int) (posSL*factorTPPips);
						//int tpMoved = p.getEntry()-movedTPPips;
						if (q1.getLow5()<=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getSl()-p.getEntry();
						}else if (q1.getHigh5()>=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getTp()-p.getEntry();
						}/*else if (q.getLow5()<=tpMoved && q.getClose5()<=p.getEntry() && movedTPPips>=20){
							p.setTp(p.getEntry()+20);
						}*/
					}else if (p.getPositionType()==PositionType.SHORT){
						int posSL = p.getSl()-p.getEntry();
						//int movedTPPips = (int) (posSL*factorTPPips);
						//int tpMoved = p.getEntry()+movedTPPips;
						if (q1.getHigh5()>=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getEntry()-p.getSl();
						}else if (q1.getLow5()<=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getEntry()-p.getTp();
						}/*else if (q.getHigh5()>=tpMoved && q.getClose5()>=p.getEntry() && movedTPPips>=2){
							p.setTp(p.getEntry()-20);
						}*/
					}
					if (closed){
						double pipsEarned = (posPips-comm*10)/10;
						
						if (pipsEarned>=0){
							profit$+=pipsEarned*p.getMicroLots()*0.1;
							//System.out.println(pipsEarned+" "+p.getMicroLots());
						}else{
							losses$+=Math.abs(pipsEarned*p.getMicroLots()*0.1);
						}
						
						actualBalance +=pipsEarned*p.getMicroLots()*0.1;
						
						p.getCloseCal().setTimeInMillis(cal1.getTimeInMillis());
						//System.out.println("[CLOSE] "+p.toString2()+" || "+q1.toString());
						if (actualBalance>maxBalance) maxBalance = actualBalance;
						double dd = 100.0-actualBalance*100.0/maxBalance;
						if (dd>maxDD) maxDD = dd;
					}
				}
			}//positions
		}
		int cases = losses+wins;
		double per = wins*100.0/cases;
		double lossPer = 100.0-per;
		double pf$ = profit$/losses$;
		double extra = balance+extraNeeded;
		double gainFactor = maxBalance*1.0/(extra);
		System.out.println(header
				+" "+begin+" "+end
				+" "+pips1+" "+pips2
				//+" "+nBars
				//+" "+nATR
				//+" "+PrintUtils.Print2dec(tpATR,false,1)
				//+" "+PrintUtils.Print2dec(slATR,false,1)
				//+" "+PrintUtils.Print2dec(pipsATR,false,1)
				//+" "+expiration
				+" "+tp
				+" "+sl
				+" "+maxAllowed
				+" || "
				+PrintUtils.Print2Int(losses+wins,4)
				+" "+losses+" "+wins
				+" "+PrintUtils.Print2dec(per,false,3)
				+" "+maxOpens
				//+" "+PrintUtils.Print2Int((int) totalPips,6)
				+" || "+PrintUtils.Print2dec2(actualBalance,true)
				+" "+PrintUtils.Print2(balance+extraNeeded)
				+" "+PrintUtils.Print2(pf$)
				+" "+PrintUtils.Print2dec2(maxBalance,true)
				+" "+PrintUtils.Print2dec2(profit$,true)
				+" "+PrintUtils.Print2dec2(losses$,true)
				+" "+PrintUtils.Print2(maxDD)
				);
		//return gainFactor;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.20.csv";

		
		Sizeof.runGC ();
		String path5m = path5m0;
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
	  	ArrayList<Quote> hourlyData 	= ConvertLib.convert(data5m, 12);
	  	//ArrayList<Quote> dailyData 	= ConvertLib.createDailyData(data5m);
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		ArrayList<QuoteShort> hourlyDataS    = QuoteShort.convertQuoteArraytoQuoteShort(hourlyData);
		//ArrayList<QuoteShort> dailyDataS  = QuoteShort.convertQuoteArraytoQuoteShort(dailyData);
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		//data = hourlyDataS;
		String header ="";
		if (path5m.contains("EURUSD")) header="EURUSD";
		
		
		int begin = data.size()-400000;
		begin     = 1;
		int end   = data.size();
		
		//begin   = 400000;
		
		
		double bandFactor = 2.9;
		int atrPeriod = 100;
		int halfLength = 56;
		ArrayList<TmaShort> tmas = IndicatorLib.calculateTMA_ArrayShort(data, 0,data.size()-1,bandFactor,halfLength,atrPeriod);
		System.out.println("total tmas: "+data.size()+" "+tmas.size());
		/*for (int i=0;i<tmas.size();i++){
			System.out.println(tmas.get(i).toString());
		}*/
		double balance = 1000;
		double risk = 1.0;
		double comm =1.5;
		int maxAllowed=20;
		
		for (int h=0;h<=23;h++){
			for (int tp=10;tp<=10;tp+=1){
				for (int sl=tp*1;sl<=tp*1;sl+=tp){
					for (int pips1=5;pips1<=5;pips1++){
						int pips2=pips1+3;
						TmaEA.testTMA("", data, begin, end, tmas, String.valueOf(h), pips1,pips2, sl, tp, balance, risk, comm, maxAllowed);
					}
				}
			}
		}
	}

}
