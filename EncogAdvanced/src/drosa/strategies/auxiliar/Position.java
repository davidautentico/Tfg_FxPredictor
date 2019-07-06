package drosa.strategies.auxiliar;

import java.util.Calendar;
import java.util.Date;
import drosa.strategies.*;
import drosa.utils.DateUtils;

public class Position {
	String id="";
	PositionStatus positionStatus = PositionStatus.CLOSE;
	PositionType type = PositionType.NONE;
	Calendar openDate = Calendar.getInstance();
	Calendar closeDate = Calendar.getInstance();
	double entryValue = 0.0;
	double entryStop = 0.0;
	double entryLimit = 0.0;
	double stopLoss = 0.0;
	double originalStopLoss=0.0;
	EntryType entryType = EntryType.NONE;
	int entryBar=-1;
	int shares=0;
	double pipValue=0;//valor en $ de 1 pip en esta posicion
	int result; //0:lose 1 win
	double maxDDPer=0.0; //porcentage drawndow
	double maxValue=0.0;
	double profitPer=0.0;
	double maxDDPips=0; 
	double maxProfitPips=0; 
	int maxProfitDays;
	double maxProfitHours;
	int trailingLevel=0;//1=1/2
	boolean overOpen = false;
	double pips;
	int dayOpenOffset=0;
	int weekOpenOffset=0;
	double lastWeekCloseOffset=0;
	boolean englobada = false;
	boolean aboveLastWeekClose=false;
	private double mult=0;
	private String symbol=null;
	private double tickValue;
	private double risk;
	boolean newHigh=false;
	boolean newLow=false;
	QuoteClassification clas = null;
	private double takeProfit;
	
	
	public boolean isEnglobada() {
		return englobada;
	}
	public void setEnglobada(boolean englobada) {
		this.englobada = englobada;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Calendar getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Calendar openDate) {		
		this.openDate = openDate;
	}
	public Calendar getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(Calendar closeDate) {
		this.closeDate = closeDate;
	}
	public double getPipValue() {
		return pipValue;
	}
	public void setPipValue(double pipValue) {
		this.pipValue = pipValue;
	}
	public PositionType getType() {
		return type;
	}
	public void setType(PositionType type) {
		this.type = type;
	}
	public double getEntryValue() {
		return entryValue;
	}
	public void setEntryValue(double entryValue) {
		this.entryValue = entryValue;
	}
	public double getEntryStop() {
		return entryStop;
	}
	public void setEntryStop(double entryStop) {
		this.entryStop = entryStop;
	}
	
	
	public double getMaxProfitHours() {
		return maxProfitHours;
	}
	public void setMaxProfitHours(double maxProfitHours) {
		this.maxProfitHours = maxProfitHours;
	}
	public double getEntryLimit() {
		return entryLimit;
	}
	public void setEntryLimit(double entryLimit) {
		this.entryLimit = entryLimit;
	}
	public double getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}
	
	public double getOriginalStopLoss() {
		return originalStopLoss;
	}
	public void setOriginalStopLoss(double originalStopLoss) {
		this.originalStopLoss = originalStopLoss;
	}
	public EntryType getEntryType() {
		return entryType;
	}
	public void setEntryType(EntryType entryType) {
		this.entryType = entryType;
	}
	public int getEntryBar() {
		return entryBar;
	}
	public void setEntryBar(int entryBar) {
		this.entryBar = entryBar;
	}
	public int getShares() {
		return shares;
	}
	public void setShares(int shares) {
		this.shares = shares;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public double getMaxDDPer() {
		return maxDDPer;
	}
	public void setMaxDDPer(double maxDDPer) {
		this.maxDDPer = maxDDPer;
	}
	
	public int getMaxProfitDays() {
		return maxProfitDays;
	}
	public void setMaxProfitDays(int maxProfitDays) {
		this.maxProfitDays = maxProfitDays;
	}
	public double getMaxDDPips() {
		return maxDDPips;
	}
	public void setMaxDDPips(double maxDDPips) {
		this.maxDDPips = maxDDPips;
	}
	public double getMaxProfitPips() {
		return maxProfitPips;
	}
	public void setMaxProfitPips(double maxProfitPips) {
		this.maxProfitPips = maxProfitPips;
	}
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	public double getProfitPer() {
		return profitPer;
	}
	public void setProfitPer(double profitPer) {
		this.profitPer = profitPer;
	}
	public int getDayOpenOffset() {
		return dayOpenOffset;
	}
	public void setDayOpenOffset(int dayOpenOffset) {
		this.dayOpenOffset = dayOpenOffset;
	}
	public double getActualResult(double value){
		double diff=0;
		if (type==PositionType.LONG){
			diff = value-this.entryValue;
		}
		if (type==PositionType.SHORT){
			diff = this.entryValue-value;
		}
		return diff;
	}
	
	public int getTrailingLevel() {
		return trailingLevel;
	}
	public void setTrailingLevel(int trailingLevel) {
		this.trailingLevel = trailingLevel;
	}
	
	
	public boolean isOverOpen() {
		return overOpen;
	}
	public void setOverOpen(boolean overOpen) {
		this.overOpen = overOpen;
	}
	
	public double getPips() {
		return pips;
	}
	public void setPips(double pips) {
		this.pips = pips;
	}
	public void copy(Position pos) {
		// TODO Auto-generated method stub
		this.type =pos.type;
		this.openDate.setTimeInMillis(pos.getOpenDate().getTimeInMillis());
		this.closeDate.setTimeInMillis(pos.getCloseDate().getTimeInMillis());
		this.entryValue = pos.entryValue;
		this.entryStop =pos.entryStop;
		this.entryLimit = pos.entryLimit;
		this.stopLoss = pos.stopLoss;
		this.entryType = pos.entryType;
		this.entryBar=pos.entryBar;
		this.shares=pos.shares;
		this.pipValue=pos.pipValue;//valor en $ de 1 pip en esta posicion
		this.result=pos.result; //0:lose 1 win
		this.maxDDPer=pos.maxDDPer; //porcentage drawndow
		this.maxValue=pos.maxValue;
		this.profitPer=pos.profitPer;
		this.maxDDPips=pos.maxDDPips; 
		this.maxProfitPips=pos.maxProfitPips; 
		this.maxProfitDays=pos.maxProfitDays;
		this.maxProfitHours=pos.maxProfitHours;
		this.trailingLevel = pos.trailingLevel;
		this.originalStopLoss = pos.originalStopLoss;
		this.overOpen = pos.isOverOpen();
		this.pips=pos.pips;
		this.dayOpenOffset=pos.dayOpenOffset;
		this.weekOpenOffset=pos.weekOpenOffset;
		this.englobada=pos.englobada;
		this.lastWeekCloseOffset = pos.lastWeekCloseOffset;
		this.id=pos.id;
		this.mult=pos.mult;
		this.symbol=pos.symbol;
		this.tickValue=pos.getTickValue();
		this.risk=pos.risk;
		this.newHigh=pos.newHigh;
		this.newLow=pos.newLow;
		this.clas=pos.clas;
		this.positionStatus=pos.positionStatus;
		this.takeProfit=pos.takeProfit;
	}
	public void reset() {
		// TODO Auto-generated method stub
		this.type =PositionType.NONE;
		this.openDate = null;
		this.closeDate = null;
		this.entryValue = 0;
		this.entryStop =0;
		this.entryLimit = 0;
		this.stopLoss = 0;
		this.entryType = EntryType.NONE;
		this.entryBar=0;
		this.shares=0;
		this.pipValue=0;
		this.result=0;
		this.maxDDPer=0;
		this.maxValue=0;
		this.profitPer=0;
		this.maxDDPips=0; 
		this.maxProfitDays=0;
		this.maxProfitHours=0;
		this.maxProfitPips=0; 
		this.trailingLevel = 0;
		this.originalStopLoss=0;
		this.overOpen = false;
		this.pips = 0;
		this.dayOpenOffset=0;
		this.weekOpenOffset=0;
		this.englobada=false;
		this.lastWeekCloseOffset=0;
		this.id=null;
		this.mult=0;
		this.symbol=null;
		this.tickValue=1;
		this.risk=0;
		this.newHigh=false;
		this.newLow=false;
		this.clas=null;
		this.positionStatus=PositionStatus.CLOSE;
		this.takeProfit=0;
	}
	public void setWeekOpenOffset(int weekOpenOffset) {
		// TODO Auto-generated method stub
		this.weekOpenOffset=weekOpenOffset;
	}
	public int getWeekOpenOffset() {
		return weekOpenOffset;
	}
	public boolean isAboveLastWeekClose() {
		return aboveLastWeekClose;
	}
	public void setAboveLastWeekClose(boolean aboveLastWeekClose) {
		this.aboveLastWeekClose = aboveLastWeekClose;
	}
	public double getLastWeekCloseOffset() {
		return lastWeekCloseOffset;
	}
	public void setLastWeekCloseOffset(double lastWeekCloseOffset) {
		this.lastWeekCloseOffset = lastWeekCloseOffset;
	}
	public void setMult(double d) {
		// TODO Auto-generated method stub
		this.mult = d;
	}
	public double getMult() {
		return mult;
	}
	
	public double calculateActualPips(double value,double mult,double spread) {
		// TODO Auto-generated method stub
		double diff=0.0;
		
		if (this.type==PositionType.LONG){
			diff = value-this.getEntryValue();
		}
		if (this.type==PositionType.SHORT){
			diff = this.getEntryValue()-value;
		}
		double pips = diff*1/mult-spread;
		
		return pips;
	}
	public String getSymbol() {
		// TODO Auto-generated method stub
		return this.symbol;
	}
	public void setSymbol(String symbol) {
		// TODO Auto-generated method stub
		this.symbol=symbol;
	}
	public double getTickValue() {
		// TODO Auto-generated method stub
		return this.tickValue;
	}
	public void setTickValue(double tickValue) {
		this.tickValue = tickValue;
	}
	public double getRisk() {
		return risk;
	}
	public void setRisk(double risk) {
		this.risk = risk;
	}
	public boolean isNewHigh() {
		return newHigh;
	}
	public void setNewHigh(boolean newHigh) {
		this.newHigh = newHigh;
	}
	public boolean isNewLow() {
		return newLow;
	}
	public void setNewLow(boolean newLow) {
		this.newLow = newLow;
	}
	public QuoteClassification getClas() {
		return clas;
	}
	public void setClas(QuoteClassification clas) {
		this.clas = clas;
	}
	public PositionStatus getPositionStatus() {
		return positionStatus;
	}
	public void setPositionStatus(PositionStatus positionStatus) {
		this.positionStatus = positionStatus;
	}
	public void setTakeProfit(double takeProfit) {
		// TODO Auto-generated method stub
		this.takeProfit = takeProfit;
		
	}
	public double getTakeProfit() {
		// TODO Auto-generated method stub
		return this.takeProfit;
	}
	
	

	
}
