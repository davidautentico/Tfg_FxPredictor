package drosa.strategies;

public interface MoneyManagement extends ParameterEvaluation{
	
	public double getRisk(String type, int interval,double atr);
}
