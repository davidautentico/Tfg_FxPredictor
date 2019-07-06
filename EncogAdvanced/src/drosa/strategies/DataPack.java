package drosa.strategies;

import java.util.ArrayList;

import drosa.finances.Quote;

public class DataPack {
	int yearL;
	int yearH;
	String symbol;
	ArrayList<Quote> data = null;
	double tp=50;
	double sl=50;
	
	public int getYearL() {
		return yearL;
	}
	public void setYearL(int yearL) {
		this.yearL = yearL;
	}
	public int getYearH() {
		return yearH;
	}
	public void setYearH(int yearH) {
		this.yearH = yearH;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public ArrayList<Quote> getData() {
		return data;
	}
	public void setData(ArrayList<Quote> data) {
		this.data = data;
	}
	public double getTp() {
		return tp;
	}
	public void setTp(double tp) {
		this.tp = tp;
	}
	public double getSl() {
		return sl;
	}
	public void setSl(double sl) {
		this.sl = sl;
	}
	
	
}
