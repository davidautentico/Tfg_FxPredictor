package drosa.strategies;

import drosa.utils.PrintUtils;

public class MoneyManagementFS0 implements MoneyManagement,ParameterEvaluation {

	double risk =0.01;
	double score=0;
	
	public  MoneyManagementFS0(){
		
	}
	public MoneyManagementFS0(double risk){
		this.risk = risk;
	}
	
	@Override
	public double getRisk(String type, int interval,double atr) {
		// TODO Auto-generated method stub
		return risk;
	}
	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return this.score;
	}
	@Override
	public void setScore(double score) {
		// TODO Auto-generated method stub
		this.score=score;
		
	}
	@Override
	public void printInfo(String header) {
		// TODO Auto-generated method stub
		System.out.println(header+" "+PrintUtils.Print(risk)+": "+PrintUtils.Print(score));
	}

}
