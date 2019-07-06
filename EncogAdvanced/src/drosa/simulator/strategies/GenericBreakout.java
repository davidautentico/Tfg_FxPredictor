package drosa.simulator.strategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.finances.Quote;
import drosa.simulator.OrderStatus;
import drosa.simulator.OrderType;
import drosa.simulator.SimAction;
import drosa.simulator.Simulator;
import drosa.simulator.SimulatorResults;
import drosa.simulator.Trade;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class GenericBreakout implements Strategy{

	//variables globales
	double dayOpen=0;
	int lastDay=-1;
	double spread=0;
	double limit=0;
	double buySpread=0;
	double sellSpread=0;
	double maxHigh=-999;
	double minLow=999;
	int lastOp= -1;
	int actualOp=-1;
	int maxDayOps=3;
	int dayOps=0;
	int numShares=1;
	int totalLimites=0;
	int totalDays=0;
	double diffPer=0;
	double actualDayLoss=0;
	
	private int calculateShares(double amount,double actualLoss,OrderType orderType,double entry){
		
		if (amount<=0) return 0;
		int sharesMax = (int) (amount/entry);
		return sharesMax;

	}
	
	/**
	 * Configura spread y limit en % sobre la apertura
	 * @param spread
	 * @param limit
	 */
	public void configure(double spread,double limit){
		this.spread = spread;
		this.limit = limit;
	}
	

	@Override
	public void generateActions(SimulatorResults results,
			ArrayList<Trade> trades, ArrayList<Quote> data, int actual) {
		// TODO Auto-generated method stub
		//Quota actual
		Trade lastTrade = null;
		Calendar cal = Calendar.getInstance();
		Quote q = data.get(actual);
		cal.setTime(q.getDate());						
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);			

		//System.out.println("[DATE] "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
		if (dayWeek != lastDay){
			//rango LastDay
			System.out.println("[RANGEDAY] "+PrintUtils.Print(diffPer));
			
			if (trades.size()>0){
				lastTrade = trades.get(trades.size()-1);
				if (lastTrade.getOrderStatus()==OrderStatus.OPEN){
					System.out.println("Cierre open");
					Simulator.closeTrade(results,trades.get(trades.size()-1),q.getClose());
				}
			}
			
			dayOpen = q.getOpen();
			double offset = dayOpen*spread/100.0;
			buySpread = dayOpen + offset;
			sellSpread = dayOpen - offset;
			maxHigh=q.getHigh();
			minLow=q.getLow();
			lastDay = dayWeek;
			dayOps=0;
			lastOp=-1;
			actualOp=-1;
			totalDays++;
			numShares=1;
			actualDayLoss=0;
			System.out.println("[OPENDAY] "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.Print(dayOpen)+" "+PrintUtils.Print(buySpread)+" "+PrintUtils.Print(sellSpread));
		}
		
		if (trades.size()>0)
			lastTrade = trades.get(trades.size()-1);
		
		//comprobamos limites
		if (q.getHigh()>=maxHigh) maxHigh = q.getHigh();
		if (q.getLow()<=minLow)   minLow = q.getLow();
		//supera el limite
		double diff = maxHigh-minLow;
		diffPer = diff*100.0/dayOpen;
		
		//mirar si entra: si no hemos llegado al limite y no hay operación ahora
		if (dayOps<maxDayOps && actualOp==-1){
			
			if (q.getHigh()>=buySpread && lastOp!=1){
				numShares=calculateShares(results.getAvailableBalance(),actualDayLoss,OrderType.BUY,buySpread);
				if (numShares>0){
					//System.out.println("shares: "+numShares);
					Simulator.openTrade(results,trades,numShares,OrderType.BUY,buySpread,sellSpread,-1);	
					actualOp=1;
					dayOps++;
				}
			}else if (q.getLow()<=sellSpread && lastOp!=0){
				numShares=calculateShares(results.getAvailableBalance(),actualDayLoss,OrderType.SELL,sellSpread);
				if (numShares>0){
					Simulator.openTrade(results,trades,numShares,OrderType.SELL,sellSpread,buySpread,-1);
					actualOp=0;
					dayOps++;
				}
			}
		}
		
		//gestionar stopLosses y take Profits definidos
		if (actualOp>=0){
			lastTrade = trades.get(trades.size()-1);
			double stopLoss = trades.get(trades.size()-1).getStopValue();
			if (lastTrade.getOrderType()==OrderType.BUY){
				if (q.getLow()<=stopLoss){
					actualDayLoss+=Simulator.closeTrade(results,trades.get(trades.size()-1),stopLoss);
					lastOp=actualOp;
					actualOp=-1;
				}
			}
			if (lastTrade.getOrderType()==OrderType.SELL){
				if (q.getHigh()>=stopLoss){
					actualDayLoss+=Simulator.closeTrade(results,trades.get(trades.size()-1),stopLoss);
					lastOp=actualOp;
					actualOp=-1;
				}
			}
		}
		
		//gestion stopLosses y takeProfits customs
		if (diffPer>=limit){//se supera el limite, se cierra el trade
			lastTrade = trades.get(trades.size()-1);
			//System.out.println("Cierre limite sin");
			if (lastTrade.getOrderStatus()==OrderStatus.OPEN){
				totalLimites++;
				System.out.println("Cierre limite "+totalLimites+" "+PrintUtils.Print(totalLimites*100.0/totalDays)+"%");
				Simulator.closeTrade(results,trades.get(trades.size()-1),q.getClose());
				lastOp=actualOp;
				actualOp=-1;
			}
			dayOps=maxDayOps;
		}
		
	}
	
	public static void main(String[] args) {
		String path = "C:\\Users\\david\\Documents\\trading\\data\\pittrading\\stocks\\stocks_030813_david_rosa_peinado\\Stocks 030813\\S&P 500 Index Components";
		String header = "IBM.TXT";
		ArrayList<Quote> data  = DAO.retrievePiTradingStocks(path+"\\"+header);
		int barBegin = 0;
		int barEnd = data.size()-1;
		//barBegin = barEnd-barEnd;
		barBegin = barEnd-300000;
		//barEnd = barBegin+30000;
		
		GenericBreakout gb = new GenericBreakout();
		gb.configure(0.3, 1.5);
		Simulator.run(20000, data,barBegin,barEnd, gb);
		
	}

}
