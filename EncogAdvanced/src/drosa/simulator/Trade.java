package drosa.simulator;

import java.util.Calendar;

public class Trade {
	long id;
	OrderType orderType;
	OrderStatus orderStatus;
	double quantity;
	Calendar openDate;
	Calendar closeDate;
	double entryValue;
	double openValue;
	double closeValue;
	double stopValue;
	double takeProfitValue;
	private double profit;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
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
	public double getOpenValue() {
		return openValue;
	}
	public void setOpenValue(double openValue) {
		this.openValue = openValue;
	}
	public double getCloseValue() {
		return closeValue;
	}
	public void setCloseValue(double closeValue) {
		this.closeValue = closeValue;
	}
	public double getStopValue() {
		return stopValue;
	}
	public void setStopValue(double stopValue) {
		this.stopValue = stopValue;
	}
	public double getTakeProfitValue() {
		return takeProfitValue;
	}
	public void setTakeProfitValue(double takeProfitValue) {
		this.takeProfitValue = takeProfitValue;
	}
	public double getEntryValue() {
		return entryValue;
	}
	public void setEntryValue(double entryValue) {
		this.entryValue = entryValue;
	}
	public void clear(){
		id=0;
		orderType=OrderType.NONE;
		orderStatus=OrderStatus.NONE;
		quantity=0;
		openDate=null;
		closeDate=null;
		entryValue=0;
		openValue=0;
		closeValue=0;
		stopValue=0;
		takeProfitValue=0;
	}
	public void setProfit(double profit) {
		// TODO Auto-generated method stub
		this.profit = profit;
	}
	public double getProfit() {
		// TODO Auto-generated method stub
		return this.profit;
	}
	
	
	
}
