package drosa.phil;


import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestWithNews {

	
	public static boolean existsImpact(ArrayList<NewsItem> news,NewsImpact impact){
		for (int i=0;i<news.size();i++){
			if (news.get(i).getImpact()==impact){
				//System.out.println(news.get(i).toString());
				return true;
			}
		}
		return false;
	}
	
	public static void rangeStudy(ArrayList<Range> ranges,ArrayList<NewsItem> news, Calendar from, Calendar to, 
			int dayL,int dayH,int hourL,int hourH,
			ArrayList<Currency> currs,NewsImpact impact){
		
		double avg = 0;
		int count = 0;
		for (int i=0;i<ranges.size();i++){
			Range range 	= ranges.get(i);
			Calendar cal 	= range.getDate();
			int dayWeek 	= cal.get(Calendar.DAY_OF_WEEK);
			int rangeInt 	= range.getRange();
			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			if (cal.getTimeInMillis()<from.getTimeInMillis() || cal.getTimeInMillis()>to.getTimeInMillis()) continue;
			
			if (impact == NewsImpact.NULL){
				avg+=rangeInt;
				count++;
			}else{
				ArrayList<NewsItem> newsFound = NewsItem.findAllNewsItem(news,currs,cal);
				//System.out.println("newsFound : "+newsFound.size());
				if (newsFound!=null && existsImpact(newsFound,impact)){
					avg+=rangeInt;
					count++;
				}
			}
		}
		System.out.println("avg: "+PrintUtils.Print2(avg*1.0/count));
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path   = "c:\\fxdata";
		
		int yearF      	 	= 2013;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2013;
		int monthL 			= Calendar.DECEMBER;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		
		ArrayList<Quote>	allBreachings= new ArrayList<Quote>();
		ArrayList<NewsItem>	news = new ArrayList<NewsItem>();
		ArrayList<Range> ranges = new ArrayList<Range>();
		
		from.set(yearF, monthF,1);
		to.set(yearL, monthL,31);
		
		//load ranges
		ranges = DAO.loadRanges(path+"\\EURUSD_ranges.txt");
  		
		//load news
		news = DAO.loadNews(path+"\\news.txt");
		
		ArrayList<Currency> currs = new ArrayList<Currency>();
		currs.add(Currency.EUR);
		currs.add(Currency.USD);
		
		int dayL = Calendar.MONDAY+0;
		int dayH = Calendar.MONDAY+0;
		
		rangeStudy(ranges,news,from,to,dayL,dayH,0,23,currs,NewsImpact.NULL);
		rangeStudy(ranges,news,from,to,dayL+1,dayH+1,0,23,currs,NewsImpact.NULL);
		rangeStudy(ranges,news,from,to,dayL+2,dayH+2,0,23,currs,NewsImpact.NULL);
		rangeStudy(ranges,news,from,to,dayL+3,dayH+3,0,23,currs,NewsImpact.NULL);
		rangeStudy(ranges,news,from,to,dayL+4,dayH+4,0,23,currs,NewsImpact.NULL);
		
		
		//rangeStudy(ranges,news,from,to,dayL,dayH,0,23,currs,NewsImpact.LOW);
		//rangeStudy(ranges,news,from,to,dayL,dayH,0,23,currs,NewsImpact.MEDIUM);
		//rangeStudy(ranges,news,from,to,dayL,dayH,0,23,currs,NewsImpact.HIGH);
		
	}

}
