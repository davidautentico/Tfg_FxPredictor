package drosa.finances;

import java.util.Calendar;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class Tick {
	
	Calendar	date = null;
	double		ask  = 0;
	double		bid	 = 0;
	double		askVolume = 0;
	double 		bidVolume	= 0;
	
	

	public Calendar getDate() {
		return date;
	}



	public void setDate(Calendar date) {
		this.date = date;
	}



	public double getAsk() {
		return ask;
	}



	public void setAsk(double ask) {
		this.ask = ask;
	}



	public double getBid() {
		return bid;
	}



	public void setBid(double bid) {
		this.bid = bid;
	}



	public double getAskVolume() {
		return askVolume;
	}



	public void setAskVolume(double askVolume) {
		this.askVolume = askVolume;
	}



	public double getBidVolume() {
		return bidVolume;
	}



	public void setBidVolume(double bidVolume) {
		this.bidVolume = bidVolume;
	}

	public String toString(){
		return DateUtils.datePrintMs(this.date)
				+" "+PrintUtils.Print5dec(this.ask)
				+" "+PrintUtils.Print5dec(this.bid)
				+" "+PrintUtils.Print2dec(this.askVolume,false)
				+" "+PrintUtils.Print2dec(this.bidVolume,false)
				;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
