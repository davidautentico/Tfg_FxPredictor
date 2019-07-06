package drosa.strategies;

import drosa.utils.PrintUtils;

public class ParametersFS implements ParameterEvaluation {

	double stopLoss=0.0;
	double breakoutPoint=0.0;
	double score=0;
	
	public double getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}
	public double getBreakoutPoint() {
		return breakoutPoint;
	}
	public void setBreakoutPoint(double breakoutPoint) {
		this.breakoutPoint = breakoutPoint;
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
		System.out.println(header+" "+PrintUtils.Print(this.breakoutPoint)
				+" "+PrintUtils.Print(this.stopLoss)
				+": "+PrintUtils.Print(score));
	}
}
