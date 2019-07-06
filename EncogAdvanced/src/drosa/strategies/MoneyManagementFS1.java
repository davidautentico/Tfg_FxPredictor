package drosa.strategies;

import drosa.utils.PrintUtils;

public class MoneyManagementFS1 implements MoneyManagement,ParameterEvaluation { 

	double risk1 =0.01;
	double risk2 =0.01;
	double score=0;
	
	public  MoneyManagementFS1(){
		
	}
	public MoneyManagementFS1(double risk1,double risk2){
		this.risk1 = risk1;
		this.risk2 = risk2;
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
		System.out.println(header
				+" "+PrintUtils.Print(risk1)
				+" "+PrintUtils.Print(risk2)
				+": "+PrintUtils.Print(score));
	}
	
	@Override
	public double getRisk(String type, int interval,double atr) {
		// TODO Auto-generated method stub
		
		if (type.equalsIgnoreCase("NRX") || type.equalsIgnoreCase("IB")){
			return risk1;
		}
		if (type.equalsIgnoreCase("WSX")){
			return risk2;
		}
		return 0.01;
	}
}