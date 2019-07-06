package drosa.strategies;

import drosa.utils.PrintUtils;

public class MoneyManagementFS2 implements MoneyManagement,ParameterEvaluation {
	
	int umbral1=1;
	double risk1 =0.01;
	double risk2 =0.01;
	double risk3 =0.01;
	double score=0;
	
	public  MoneyManagementFS2(){
		
	}
	public MoneyManagementFS2(int umbral1,double risk1,double risk2,double risk3){
		this.risk1 = risk1;
		this.risk2 = risk2;
		this.umbral1=umbral1;
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
				+" "+umbral1
				+" "+PrintUtils.Print(risk1)
				+" "+PrintUtils.Print(risk2)
				+" "+PrintUtils.Print(risk3)
				+": "+PrintUtils.Print(score));
	}
	
	@Override
	public double getRisk(String type, int interval,double atr) {
		// TODO Auto-generated method stub
		
		if (type.equalsIgnoreCase("NRX") || type.equalsIgnoreCase("IB")){
			if (interval<=umbral1)
				return risk1;
			return risk2;
		}
		if (type.equalsIgnoreCase("WSX")){
			if (interval<=umbral1)
				return risk1;
			return risk3;
		}
		return 0.01;
	}
}
