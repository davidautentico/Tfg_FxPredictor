package drosa.strategies;

import drosa.utils.PrintUtils;

public class MoneyManagementFS4 implements MoneyManagement,ParameterEvaluation {
	
	double mult =0.0001;
	double atr=0;
	double score=0;
	

	public  MoneyManagementFS4(){
		
	}
	public MoneyManagementFS4(double mult){
		this.mult = mult;
	}
	
	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return this.score;
	}

	@Override
	public void setScore(double score) {
		// TODO Auto-generated method stub
		this.score = score;
	}

	@Override
	public void printInfo(String header) {
		// TODO Auto-generated method stub
		System.out.println(header
				+": "+PrintUtils.Print(atr));
	}
	

	@Override
	public double getRisk(String type, int interval, double atr) {
		// TODO Auto-generated method stub
		this.atr=atr;
		
		double pips = atr/mult;
		//System.out.println("pips: "+PrintUtils.Print(pips));
		double risk = (200.0/pips)/100.0;
		return risk;
	}

}
