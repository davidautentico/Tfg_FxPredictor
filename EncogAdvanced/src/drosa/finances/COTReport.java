package drosa.finances;

import java.util.Date;

import drosa.utils.DateUtils;

public class COTReport {

	String market;
	Date date;
	long openInterest;
	long NonCommercialLong;
	long NonCommercialShort;
	long NonCommercialSpread;
	long CommercialLong;
	long CommercialShort;
	long TotalReportableLong;	
	long TotalReportableShort;
	long NonReportableLong;	
	long NonReportableShort;
				
	public String getMarket() {
		return market;
	}




	public void setMarket(String market) {
		this.market = market;
	}




	public Date getDate() {
		return date;
	}




	public void setDate(Date date) {
		this.date = date;
	}




	public long getOpenInterest() {
		return openInterest;
	}




	public void setOpenInterest(long openInterest) {
		this.openInterest = openInterest;
	}




	public long getNonCommercialLong() {
		return NonCommercialLong;
	}




	public void setNonCommercialLong(long nonCommercialLong) {
		NonCommercialLong = nonCommercialLong;
	}




	public long getNonCommercialShort() {
		return NonCommercialShort;
	}




	public void setNonCommercialShort(long nonCommercialShort) {
		NonCommercialShort = nonCommercialShort;
	}




	public long getNonCommercialSpread() {
		return NonCommercialSpread;
	}




	public void setNonCommercialSpread(long nonCommercialSpread) {
		NonCommercialSpread = nonCommercialSpread;
	}




	public long getCommercialLong() {
		return CommercialLong;
	}




	public void setCommercialLong(long commercialLong) {
		CommercialLong = commercialLong;
	}




	public long getCommercialShort() {
		return CommercialShort;
	}




	public void setCommercialShort(long commercialShort) {
		CommercialShort = commercialShort;
	}




	public long getTotalReportableLong() {
		return TotalReportableLong;
	}




	public void setTotalReportableLong(long totalReportableLong) {
		TotalReportableLong = totalReportableLong;
	}




	public long getTotalReportableShort() {
		return TotalReportableShort;
	}




	public void setTotalReportableShort(long totalReportableShort) {
		TotalReportableShort = totalReportableShort;
	}




	public long getNonReportableLong() {
		return NonReportableLong;
	}




	public void setNonReportableLong(long nonReportableLong) {
		NonReportableLong = nonReportableLong;
	}




	public long getNonReportableShort() {
		return NonReportableShort;
	}




	public void setNonReportableShort(long nonReportableShort) {
		NonReportableShort = nonReportableShort;
	}




	public static COTReport decodeCOTData(String data){
		COTReport cot = new COTReport();
		
		String market = null;
		int ajuste =0;
		
		if (data.split(",")[1].trim().charAt(data.split(",")[1].trim().length()-1)=='"'){
			market = data.split(",")[0]+data.split(",")[1];
			ajuste=1;
		}else{
			market =data.split(",")[0]; 
		}
				
		/*System.out.println("valor 0: "+data.split(",")[0]);
		System.out.println("valor 1: "+data.split(",")[1]);
		System.out.println("valor 2: "+data.split(",")[2]);*/
		System.out.println("market,date: "+market+','+data.split(",")[2+ajuste].trim());
		cot.setMarket(market);
		cot.setDate(DateUtils.stringToDate(data.split(",")[2+ajuste]));
		cot.setOpenInterest(Long.valueOf(data.split(",")[7+ajuste].trim()));
		
		cot.setNonCommercialLong(Long.valueOf(data.split(",")[8+ajuste].trim()));
		cot.setNonCommercialShort(Long.valueOf(data.split(",")[9+ajuste].trim()));
		cot.setNonCommercialSpread(Long.valueOf(data.split(",")[10+ajuste].trim()));
				
		cot.setCommercialLong(Long.valueOf(data.split(",")[11+ajuste].trim()));
		cot.setCommercialShort(Long.valueOf(data.split(",")[12+ajuste].trim()));
		
		cot.setTotalReportableLong(Long.valueOf(data.split(",")[13+ajuste].trim()));
		cot.setTotalReportableShort(Long.valueOf(data.split(",")[14+ajuste].trim()));
		
		cot.setNonReportableLong(Long.valueOf(data.split(",")[15+ajuste].trim()));
		cot.setNonReportableShort(Long.valueOf(data.split(",")[16+ajuste].trim()));
					
		return cot;
	}
}
