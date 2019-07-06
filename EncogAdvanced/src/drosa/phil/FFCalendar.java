package drosa.phil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;


public class FFCalendar {
	
	public static String decodeFFDate(int actualY, int actualM, int actualD) {
		// TODO Auto-generated method stub
		String strMonth = "";
		if (actualM==Calendar.JANUARY){
			strMonth = "jan";
		}
		if (actualM==Calendar.FEBRUARY){
			strMonth = "feb";
		}
		if (actualM==Calendar.MARCH){
			strMonth = "mar";
		}
		if (actualM==Calendar.APRIL){
			strMonth = "apr";
		}
		if (actualM==Calendar.MAY){
			strMonth = "may";
		}
		if (actualM==Calendar.JUNE){
			strMonth = "jun";
		}
		if (actualM==Calendar.JULY){
			strMonth = "jul";
		}
		if (actualM==Calendar.AUGUST){
			strMonth = "aug";
		}
		if (actualM==Calendar.SEPTEMBER){
			strMonth = "sep";
		}
		if (actualM==Calendar.OCTOBER){
			strMonth = "oct";
		}
		if (actualM==Calendar.NOVEMBER){
			strMonth = "nov";
		}
		if (actualM==Calendar.DECEMBER){
			strMonth = "dec";
		}
		
		return strMonth+actualD+'.'+actualY;
		
	}
	
	public static ArrayList<Integer> countMatchesIndexes(String or,String toSearch){
		
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		//System.out.println("longitud string: "+or.length());
		int index = or.indexOf(toSearch);
		indexes.add(index);
		//System.out.println("idx added: "+index);
		int basePoint = index+1;
		while (index != -1) {						
			or = or.substring(index + 1);
			//System.out.println("Point actual tamanio nuevo substring: "+basePoint+" "+or.length()+" "+(basePoint+or.length()));
		    index = or.indexOf(toSearch);
		    if (index!=-1){
		    	indexes.add(basePoint+index);
		    	//System.out.println("idx added: "+(basePoint+index));
		    }
		    basePoint+=index+1;
		}
		//System.out.println("No of *is* in the input is : " + count);
		return indexes;
	}
	
	public static int countMatches(String or,String toSearch){
		int index = or.indexOf(toSearch);
		int count = 0;
		while (index != -1) {
		    count++;
		    or = or.substring(index + 1);
		    index = or.indexOf(toSearch);
		}
		//System.out.println("No of *is* in the input is : " + count);
		return count;
	}
	
	public static int decodeFFgmt(String line){
	
		int index = line.indexOf("setGmtOffset");
		
		if (index ==-1) return -999;
		
		int parIndex = line.indexOf(")", index);
		String valueStr = line.substring(index+"setGmtOffset".length()+1,parIndex);	
		
		return Integer.valueOf(valueStr);

	}
	
	private static ArrayList<String> getTimeLines(String newsLine,
			ArrayList<Integer> indexes) {
		
		ArrayList<String> timeLines = new ArrayList<String>();
		// TODO Auto-generated method stub
		for (int i = 0;i<=indexes.size()-2;i++){
			int actualIdx = indexes.get(i);
			int finalIdx  = indexes.get(i+1);
			//System.out.println("idxs: "+actualIdx+" "+finalIdx);
			String timeLine = newsLine.substring(actualIdx, finalIdx);
			timeLines.add(timeLine);
		}
		int actualIdx = indexes.get(indexes.size()-1);
		String timeLine = newsLine.substring(actualIdx);
		timeLines.add(timeLine);
		
		return timeLines;
	}
	

	private static Calendar getNewsTime(Calendar date, String line){
	
		Calendar time = null;
				
		Pattern pAM = Pattern.compile("\\d+:\\d+am");
		Pattern pPM = Pattern.compile("\\d+:\\d+pm");
		
		// Now create matcher object.
	    Matcher mAM = pAM.matcher(line);
	    Matcher mPM = pPM.matcher(line);
	    
	    String res = null;
	    boolean am = false;
	    if (mAM.find()){
	    	res = mAM.group(0);
	    	am=true;
	    }else if (mPM.find()){
	    	res =mPM.group(0);
	    	am=false;
	    }
	    
	    if (res==null){
			return null;
		}
	    
	    int index = res.indexOf(':');
	    int indexF=-1;
	    if (am)
	    	indexF = res.indexOf("am");
	    else
	    	indexF = res.indexOf("pm");
	    String hStr = res.substring(0,index);
	    String mStr = res.substring(index+1,indexF);
		
		int h = -1;
		int m = -1;
		try{
			h = Integer.valueOf(hStr); 
			m = Integer.valueOf(mStr);
		}catch(Exception e){
			return null;
		}
		
		/*if (am){
			System.out.println(h+":"+m+"am");
		}else{
			System.out.println(h+":"+m+"pm");
		}*/
		if (!am && h<=11){
			h+=12;
		}
		
		if (am && h==12){
			h=0;
		}
		
		time = Calendar.getInstance();
				
		time.setTime(date.getTime());
		time.set(Calendar.HOUR_OF_DAY, h);
		time.set(Calendar.MINUTE, m);
		return time;
	}
	
	private static Currency getCurrency(String line){
		
		Currency res = Currency.NULL;
		
		int indexI = line.indexOf("currency");
		
		String curr = line.substring(indexI+10,indexI+13);
		
		for (int i=0;i<Currency.values().length;i++){
			String curr0 = Currency.values()[i].name();
			if (curr.contains(curr0)){
				return Currency.values()[i];
			}
		}
		
		return res;
	}
	
	private static NewsImpact getImpact(String line){
		
		NewsImpact res = NewsImpact.NULL;
		
		int indexI = line.indexOf("title");
		
		String impactStr = line.substring(indexI+6,indexI+16);
		if (impactStr.contains("Low")){
			return NewsImpact.LOW;
		}
		if (impactStr.contains("Medium")){
			return NewsImpact.MEDIUM;
		}
		if (impactStr.contains("High")){
			return NewsImpact.HIGH;
		}

		return res;
	}
	
	private static String getDescription(String line){
		
		String descr=null;

		int indexI = line.indexOf("event");
		String sub = line.substring(indexI);
		int indexF = sub.indexOf("</span");
		
		descr= sub.substring(13,indexF);
		//System.out.println("desc: "+desc);
		
		return descr;
	}
	
	
	private static ArrayList<NewsItem> decodeNews(Calendar date,int gmtOffset, ArrayList<String> timeLines) {
		// TODO Auto-generated method stub
		ArrayList<NewsItem> news = new ArrayList<NewsItem>(); 
		
		Calendar actualTime = null;
		for (int i=0;i<timeLines.size();i++){
			String line   		= timeLines.get(i);
			Calendar time 		= getNewsTime(date,line);
			Currency curr 		= getCurrency(line);
			NewsImpact impact 	= getImpact(line);
			String descr	 	= getDescription(line);
			//System.out.println("update time: "+DateUtils.datePrint(actualTime.getTime()));
			if (time!=null){
				actualTime = time;
				//System.out.println("update time: "+DateUtils.datePrint(actualTime.getTime()));
			}
			
			
			//System.out.println("line: "+line+" descr: "+descr+" impact : "+impact.name());
			if (actualTime!=null 
					&& !descr.contains("Holiday") 
					&& descr.trim().length()>=0
					&& impact!=NewsImpact.NULL
					&& !descr.contains("Meetings")
					&& !descr.contains("Election")
					){
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(actualTime.getTime());
				
				NewsItem newsItem = new NewsItem();
				newsItem.setDate(cal);
				newsItem.setCurrency(curr);
				newsItem.setImpact(impact);
				newsItem.setDescription(descr);
				newsItem.setGmtOffset(gmtOffset);
				news.add(newsItem);
				/*System.out.println("time curr impact: "
						+" "+DateUtils.datePrint(actualTime.getTime())
						+" "+curr.name()
						+" "+impact.name()
						+" "+descr
						+" "+gmtOffset
						);*/
			}
		}
		
	
		return news;
	}
	
	public static ArrayList<NewsItem> getDataFromUrl(Calendar date,String urlStr){
		
		try{
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
	
			InputStream stream = connection.getInputStream();
			// read the contents using an InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));

	        // Lectura del fichero
	        String line;
	        int i=0;
	        ArrayList<String> lines = new ArrayList<String>();
	        int fromIndex = -1;
	       
	        int gmtOffset = 0;
	        String newsLine="";
	        while((line=br.readLine())!=null){
	        	//
	        	int countGMT = countMatches(line,"setGmtOffset");	        	
	        	if (countGMT>0){
	        		gmtOffset = decodeFFgmt(line);
	        		System.out.println("gmyoffset: "+gmtOffset);
	        	}
	        
	        	int countTime = countMatches(line,"<td class=\"time\"");
	        	if (countTime>0){	        		
	        		System.out.println("count time line: "+countTime+" "+line);
	        		newsLine +=line;
	        	}
	        }
	        
	        ArrayList<Integer> indexes = countMatchesIndexes(newsLine,"<td class=\"time\"");
	        System.out.println("FInal news line: "+indexes.size()+" "+newsLine);
	        ArrayList<String> timeLines = getTimeLines(newsLine,indexes);
	        /*for (i=0;i<timeLines.size();i++){
	        	System.out.println(i+" Line: "+timeLines.get(i));
		        
	        }*/
	        ArrayList<NewsItem> news=decodeNews(date,gmtOffset,timeLines);
	        return news;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static void addNews(ArrayList<NewsItem> allNews,ArrayList<NewsItem> news){
		
		for (int i=0;i<news.size();i++){
			NewsItem item = news.get(i);
			NewsItem copyItem = new NewsItem();
			copyItem.copy(item);
			
			allNews.add(copyItem);
		}
	}

	public static void getAllFFData(String fileName,int yearL,int yearF){
		
		ArrayList<NewsItem> allNews = new ArrayList<NewsItem>();
		
		String url = "http://www.forexfactory.com/calendar.php?day=";
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		
		from.set(Calendar.YEAR, yearL);
		from.set(Calendar.DAY_OF_MONTH,1);
		from.set(Calendar.MONTH, Calendar.JANUARY);
		
		to.set(Calendar.YEAR, yearF);
		to.set(Calendar.DAY_OF_MONTH,31);
		//to.set(Calendar.MONTH, Calendar.JANUARY);
		to.set(Calendar.MONTH, Calendar.DECEMBER);
		
		Calendar from2 = Calendar.getInstance();
		while (from.getTimeInMillis()<=to.getTimeInMillis()){
			
			int actualD = from.get(Calendar.DAY_OF_MONTH);
			int actualM = from.get(Calendar.MONTH);
			int actualY = from.get(Calendar.YEAR);
		
			from2.set(Calendar.DAY_OF_MONTH,actualD);
			from2.set(Calendar.MONTH, actualM);
			from2.set(Calendar.YEAR, actualY);
			
			String dateFFStr = decodeFFDate(actualY,actualM,actualD);
			
			String finalUrl = url+dateFFStr;
			//System.out.println("URL: "+finalUrl);
			
			ArrayList<NewsItem> news =  FFCalendar.getDataFromUrl(from2,finalUrl);
			System.out.println("[getAllFFData] URLS : "+" "+finalUrl);
			for (int i=0;i<news.size();i++){
				NewsItem newsItem = news.get(i);
				Calendar cal = newsItem.getDate();
				//System.out.println("original: "+DateUtils.datePrint(cal.getTime()));
				cal.add(Calendar.HOUR_OF_DAY, -newsItem.getGmtOffset());				
				int offset = DateUtils.calculatePepperGMTOffset(cal);
				cal.add(Calendar.HOUR_OF_DAY, offset);				
				newsItem.setDate(cal);	
				allNews.add(newsItem);
				//System.out.println("gmt offset item: "+newsItem.getGmtOffset()+" "+offset+" "+news.get(i).toString());
			}
			//addNews(allNews,news);
			from.add(Calendar.DAY_OF_YEAR, 1);
			//System.out.println("one month");
		}
		//disk
		writeFile(fileName,allNews);					
	}

	public static void writeFile(String fileName,ArrayList<NewsItem> news){
		try{			
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<news.size();i++){
				NewsItem newItem = news.get(i);
				writer.println(DateUtils.datePrint(newItem.getDate().getTime())
						+","+newItem.getCurrency()
						+","+newItem.getImpact()
						+","+newItem.getDescription()
						);
						
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*String line="http:\\holdsladl1:0pmsa234";
		Pattern p = Pattern.compile("\\d+:\\d+am");
		// Now create matcher object.
	     Matcher m = p.matcher(line);
	    
	     System.out.println("groupcount: "+m.groupCount());
	     if (m.find()){
	    	 System.out.println(m.group(0)); 
	     }else{
	    	 System.out.println("NOT FOUND");
	     }*/
	    
		 
		//
		 
		/*Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, 2013);
			
		ArrayList<NewsItem> news = null;
		
		news = FFCalendar.getDataFromUrl(date,"http://www.forexfactory.com/calendar.php?day=nov11.2013");
		for (int i=0;i<news.size();i++){
			System.out.println(news.get(i).toString());
		}*/
		
		String path = "c:\\fxdata";
		getAllFFData(path+"\\news.txt",2017,2017);
		DAO.loadNews(path+"\\news.txt");
	}

}
