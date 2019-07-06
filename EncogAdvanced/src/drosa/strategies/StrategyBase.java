package drosa.strategies;

import drosa.strategies.summary.StrategySummary;



public abstract class StrategyBase {
	
	StrategySummary strat = null;
	public abstract void doTest();
	public abstract void customReport();
	
	public StrategySummary getStrat() {
		return strat;
	}
	public void setStrat(StrategySummary strat) {
		this.strat = strat;
	}
}
