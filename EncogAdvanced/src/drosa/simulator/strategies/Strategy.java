package drosa.simulator.strategies;

import java.util.ArrayList;

import drosa.finances.Quote;
import drosa.simulator.SimAction;
import drosa.simulator.Simulator;
import drosa.simulator.SimulatorResults;
import drosa.simulator.Trade;

public interface Strategy {

	
	public  void generateActions(SimulatorResults results,ArrayList<Trade> trades,ArrayList<Quote> data,int actual);
}
