package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.utils.DateUtils;

public class NewsItem {
	
	Calendar date;
	Currency currency;
	String description;
	NewsImpact impact;
	double previous;
	double expected;
	int gmtOffset;
	
	
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public NewsImpact getImpact() {
		return impact;
	}
	public void setImpact(NewsImpact impact) {
		this.impact = impact;
	}
	public double getPrevious() {
		return previous;
	}
	public void setPrevious(double previous) {
		this.previous = previous;
	}
	public double getExpected() {
		return expected;
	}
	public void setExpected(double expected) {
		this.expected = expected;
	}
	
	
	public int getGmtOffset() {
		return gmtOffset;
	}
	public void setGmtOffset(int gmtOffset) {
		this.gmtOffset = gmtOffset;
	}
	public String toString(){
		String res = DateUtils.datePrint(this.getDate().getTime())
				+" "+this.getCurrency().name()
				+" "+this.getImpact().name()
				+" "+this.getDescription()
				;
				
		return res;
	}
	public void copy(NewsItem item) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(item.getDate().getTime());
		this.currency    = item.getCurrency();
		this.description = item.getDescription();
		this.impact      = item.getImpact();
		
	}
	
	public static NewsItem findNewsItem(ArrayList<NewsItem> news, Calendar cal) {
		// TODO Auto-generated method stub
		
		for (int i=0;i<news.size();i++){
			NewsItem item = news.get(i);
			Calendar calItem = item.getDate();
			int itemY = calItem.get(Calendar.YEAR);
			int itemM = calItem.get(Calendar.MONTH);
			int itemD = calItem.get(Calendar.DAY_OF_MONTH);
			
			int searchY = cal.get(Calendar.YEAR);
			int searchM = cal.get(Calendar.MONTH);
			int searchD = cal.get(Calendar.DAY_OF_MONTH);
			
			if (itemY==searchY && itemM==searchM && itemD==searchD)
				return item;
									
		}
		return null;
	}
	
	private static boolean existsCurrency(ArrayList<Currency> currs,Currency toSearch){
		for (int i=0;i<currs.size();i++){
			if (currs.get(i) == toSearch) return true;
		}
		return false;
	}
	
	public static ArrayList<NewsItem>  findAllNewsItem(ArrayList<NewsItem> news, ArrayList<Currency> currs, Calendar cal) {
		// TODO Auto-generated method stub
		 ArrayList<NewsItem> newsFound =  new ArrayList<NewsItem>(); 
		for (int i=0;i<news.size();i++){
			NewsItem item = news.get(i);
			Calendar calItem = item.getDate();
			int itemY = calItem.get(Calendar.YEAR);
			int itemM = calItem.get(Calendar.MONTH);
			int itemD = calItem.get(Calendar.DAY_OF_MONTH);
			
			int searchY = cal.get(Calendar.YEAR);
			int searchM = cal.get(Calendar.MONTH);
			int searchD = cal.get(Calendar.DAY_OF_MONTH);
			
			if (existsCurrency(currs,item.getCurrency())
					&& itemY==searchY && itemM==searchM && itemD==searchD){
				newsFound.add(item);
			}													
		}
		return newsFound;
	}
	
	public static boolean  existsTypeNew(ArrayList<NewsItem> news,String newStr){
		
		for (int i=0;i<news.size();i++){
			NewsItem n = news.get(i);
			if (n.getDescription().toUpperCase().contains(newStr.toUpperCase()))
				return true;
		}
		return false;
	}
	public static boolean existsTypeNew(ArrayList<NewsItem> news,
			ArrayList<String> blockList) {
		// TODO Auto-generated method stub
		for (int i=0;i<blockList.size();i++){
			boolean res = existsTypeNew(news,blockList.get(i));
			if (res==true) return true;
		}
		return false;
	}

}
