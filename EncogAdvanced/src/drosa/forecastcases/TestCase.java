package drosa.forecastcases;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import drosa.indicators.Indicator;

public class TestCase {
	
	GregorianCalendar fromDate;
	GregorianCalendar toDate;
	
	ArrayList<DataFeed> datafeeds = new ArrayList<DataFeed>();
	ArrayList<Indicator> inputs = new ArrayList<Indicator>();
	ArrayList<Indicator> outputs = new ArrayList<Indicator>();
	
	int maxdaysPrediction = 1; //dias que se predicen como m�ximo
	
	
	

	public ArrayList<DataFeed> getDatafeeds() {
		return datafeeds;
	}

	public void setDatafeeds(ArrayList<DataFeed> datafeeds) {
		this.datafeeds = datafeeds;
	}

	public int getMaxdaysPrediction() {
		return maxdaysPrediction;
	}

	public void setMaxdaysPrediction(int maxdaysPrediction) {
		this.maxdaysPrediction = maxdaysPrediction;
	}

	public GregorianCalendar getFromDate() {
		return fromDate;
	}

	public void setFromDate(GregorianCalendar fromDate) {
		this.fromDate = fromDate;
	}

	public GregorianCalendar getToDate() {
		return toDate;
	}

	public void setToDate(GregorianCalendar toDate) {
		this.toDate = toDate;
	}

	public ArrayList<Indicator> getInputs() {
		return inputs;
	}

	public void setInputs(ArrayList<Indicator> inputs) {
		this.inputs = inputs;
	}

	public ArrayList<Indicator> getOutputs() {
		return outputs;
	}

	public void setOutputs(ArrayList<Indicator> outputs) {
		this.outputs = outputs;
	}
	
	
	
	
	
}
