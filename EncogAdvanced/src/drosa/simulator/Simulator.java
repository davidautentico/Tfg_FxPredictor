package drosa.simulator;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.Quote;
import drosa.simulator.strategies.Strategy;
import drosa.utils.PrintUtils;

public class Simulator {
	
	public static void openTrade(SimulatorResults results,
			ArrayList<Trade> trades,double shares,OrderType orderType,
			double openValue,double stopLoss,double takeProfit) {
		// TODO Auto-generated method stub
		Trade trade = new Trade();
		trade.setOrderType(orderType);
		trade.setOrderStatus(OrderStatus.OPEN);
		trade.setOpenValue(openValue);
		trade.setStopValue(stopLoss);
		trade.setTakeProfitValue(takeProfit);
		trade.setQuantity(shares);
		trades.add(trade);
		//Pos value
		double posValue = shares*openValue;
		double actualAvail = results.getAvailableBalance();
		actualAvail-=posValue;
		results.setAvailableBalance(actualAvail);
		System.out.println("  >>[OPEN] "+trade.getOrderType()
				+" "+PrintUtils.Print(openValue)
				+" "+PrintUtils.Print(stopLoss)
				+" ("+shares
				+" "+PrintUtils.Print(posValue)+"$"
				+" "+PrintUtils.Print(actualAvail)+"$"
				+")"
				);
	}
	
	public static double closeTrade(SimulatorResults results,Trade trade, double closeValue){
		double profit =0;
		if (trade.getOrderStatus()==OrderStatus.OPEN){
			trade.setOrderStatus(OrderStatus.CLOSED);
			trade.setCloseValue(closeValue);
	
			double shares = trade.getQuantity();
			if (trade.getOrderType()==OrderType.BUY){
				profit = shares*(closeValue-trade.getOpenValue()); 
			}
			if (trade.getOrderType()==OrderType.SELL){
				profit = shares*(trade.getOpenValue()-closeValue); 
			}
			trade.setProfit(profit);
			
			results.addTrade(trade);
			double posValue = trade.getQuantity()*closeValue;
			double actualAvail = results.getAvailableBalance();
			
			if (trade.getOrderType()==OrderType.SELL){
				double initialValue = trade.getQuantity()*trade.openValue;
				double finalValue = trade.getQuantity()*trade.closeValue;
				double pf = initialValue-finalValue;
				posValue = initialValue+pf;
			}
			actualAvail+=posValue;
			results.setAvailableBalance(actualAvail);
			System.out.println("  >>[CLOSE] "+trade.getOrderType()
					+" "+PrintUtils.Print(trade.getOpenValue())
					+" "+PrintUtils.Print(trade.getCloseValue())
					+" ("+shares
					+" "+PrintUtils.Print(posValue)+"$"
					+" "+PrintUtils.Print(actualAvail)+"$"
					+")"
					+" "+PrintUtils.Print(profit)+"$"
					+" "+PrintUtils.Print(results.getTotalProfit())+"$"
					);
		}
		return profit;
	}
	
	public static void run(double initialBalance,ArrayList<Quote> data,int begin,int end,Strategy strat){
		ArrayList<Trade> trades = new ArrayList<Trade>();
		SimulatorResults results = new SimulatorResults();
		results.setInitialBalance(initialBalance);
		trades.clear();
		for (int i=begin;i<=end;i++){
			//Obtención de las nuevas acciones para el siguiente turno
			strat.generateActions(results,trades,data,i);
		}
	}

	
	

}
