package drosa.experimental.basicStrategies;

import java.util.ArrayList;

import drosa.experimental.PositionShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;

public class Transaction {
	
	ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
	
	double netPosition = 0.0;//microlots
	int currentPips = 0;
	int actualMode = 0;//1: long , -1: short
	double currentValue = 0.0;
	int currentMicroLots = 0;
	int ref = -1;
	int refH = -1;
	int refL = -1;
	int refHtp = -1;
	int refLtp = -1;
	int stage = 1;
	double target$$ = 0;
	double maxAllowed$$ = 0;
	int id = 0;
	
	int orden = 0;
	
	
	
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getMaxAllowed$$() {
		return maxAllowed$$;
	}
	public double getTarget$$() {
		return target$$;
	}
	public void setTarget$$(double target$$) {
		this.target$$ = target$$;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public int getRefHtp() {
		return refHtp;
	}
	public void setRefHtp(int refHtp) {
		this.refHtp = refHtp;
	}
	public int getRefLtp() {
		return refLtp;
	}
	public void setRefLtp(int refLtp) {
		this.refLtp = refLtp;
	}
	public int getCurrentMicroLots() {
		return currentMicroLots;
	}
	public void setCurrentMicroLots(int newMicroLots) {
		this.currentMicroLots = newMicroLots;
	}
	public int getRef() {
		return ref;
	}
	public void setRef(int ref) {
		this.ref = ref;
	}
	public void setRefH(int refH) {
		this.refH = refH;
	}
	public int getRefL() {
		return refL;
	}
	
	public int getRefH() {
		return refH;
	}
	public void setRefL(int refL) {
		this.refL = refL;
	}
	public ArrayList<PositionShort> getPositions() {
		return positions;
	}
	public void setPositions(ArrayList<PositionShort> positions) {
		this.positions = positions;
	}
	public double getNetPosition() {
		return netPosition;
	}
	public void setNetPosition(double netPosition) {
		this.netPosition = netPosition;
	}
	
	public int getActualMode() {
		return actualMode;
	}
	
	public void setActualMode(int actualMode) {
		this.actualMode = actualMode;
	}		
	
	public int getCurrentPips() {
		return currentPips;
	}
	public void setCurrentPips(int currentPips) {
		this.currentPips = currentPips;
	}
	public double getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}
	
	
	public void update(int value){

		for (int i=0;i<positions.size();i++){
			
			PositionShort p = positions.get(i);
			
			if (p.getPositionStatus()==PositionStatus.OPEN){
				
				if (p.getPositionType()==PositionType.LONG){
					currentPips += value-p.getEntry();					
					this.netPosition += p.getMicroLots();
					
					this.currentValue += (value-p.getEntry())*p.getMicroLots();
				}else if (p.getPositionType()==PositionType.SHORT){
					currentPips += p.getEntry()-value;
					this.netPosition -= p.getMicroLots();
					
					this.currentValue += (p.getEntry()-value)*p.getMicroLots();
				}
			}
		}
		
		actualMode = 0;
		if (netPosition>0) actualMode = 1;
		if (netPosition<0) actualMode = -1;

	}
	
	public double getFloatingPosition$$(int value) {
		// TODO Auto-generated method stub
		
		if (actualMode==1){
			int pips		= value - this.refH;
			double netPips$$ 	= (double) (pips*0.1*this.currentMicroLots)*0.1;
			return this.netPosition + netPips$$;
		}else if (actualMode==-1){
			int pips		= this.refL - value;
			double netPips$$ 	= (double) (pips*0.1*this.currentMicroLots)*0.1;
			return this.netPosition + netPips$$;
		}
		
		return 0;
	}
	
	public double getFloatingPositionFactor(int value) {
		// TODO Auto-generated method stub
		
		if (actualMode==1){
			int pips		= value - this.refH;
			double netPips$$ 	= (double) (pips*this.currentMicroLots);
			return (this.netPosition + netPips$$)/(this.refHtp-this.refH);//la unidad basica son los thr
		}else if (actualMode==-1){
			int pips		= this.refL - value;
			double netPips$$ 	= (double) (pips*this.currentMicroLots);
			return (this.netPosition + netPips$$)/(this.refHtp-this.refH);
		}
		
		return 0;
	}
	public double getNetPositionFactor() {
		return (this.netPosition)/(this.refHtp-this.refH);
	}
	public void incStage() {
		// TODO Auto-generated method stub
		stage++;
	}
	public void setMaxAllowed$$(double amount) {
		// TODO Auto-generated method stub
		this.maxAllowed$$ = amount;
	}
	

}
