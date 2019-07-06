package drosa.experimental.forexfactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.phil.FFCalendar;
import drosa.phil.NewsItem;
import drosa.utils.DateUtils;

public class FFNews {
	
	private static ArrayList<NewsItem> getDataFromUrl(Calendar date,String urlStr){
		
		try{
			
			URL url = new URL(urlStr);
			InputStream is = url.openStream();
			int ptr = 0;
			StringBuffer buffer = new StringBuffer();
			while ((ptr = is.read()) != -1) {
			    buffer.append((char)ptr);
			}
			
			System.out.println("buffer: "+buffer.toString()+" || "+buffer.length());
			
			/*URL url = new URL(urlStr);
			//URL url = new URL("https://www.forexfactory.com/calendar.php?day=jan1.2017");
			
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
	        	System.out.println(line);
	        }*/
	        
	        //ArrayList<Integer> indexes = countMatchesIndexes(newsLine,"<td class=\"time\"");
	        //System.out.println("FInal news line: "+" "+newsLine+' '+url.getPath()+' '+url.toString());
	        //ArrayList<String> timeLines = getTimeLines(newsLine,indexes);
	        /*for (i=0;i<timeLines.size();i++){
	        	System.out.println(i+" Line: "+timeLines.get(i));
		        
	        }*/
	        //ArrayList<NewsItem> news=decodeNews(date,gmtOffset,timeLines);
	        //return news;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void getAllFFData(int y1,int y2){
		ArrayList<NewsItem> allNews = new ArrayList<NewsItem>();
		
		String url = "http://www.forexfactory.com/calendar.php?day=";
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		
		from.set(Calendar.YEAR, y1);
		from.set(Calendar.DAY_OF_MONTH,1);
		from.set(Calendar.MONTH, Calendar.JANUARY);
		
		to.set(Calendar.YEAR, y2);
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
			
			String dateFFStr = FFCalendar.decodeFFDate(actualY,actualM,actualD);
			
			String finalUrl = url+dateFFStr;
			System.out.println("URL: "+finalUrl);
			ArrayList<NewsItem> news =  FFNews.getDataFromUrl(from2,finalUrl);
			/*ArrayList<NewsItem> news =  FFCalendar.getDataFromUrl(from2,finalUrl);
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
			}*/
			//addNews(allNews,news);
			break;
			//from.add(Calendar.DAY_OF_YEAR, 1);
			//System.out.println("one month");*/
		}
		//disk
		//writeFile(fileName,allNews);					
	}

	public static void main(String[] args) {
		
		FFNews.getAllFFData(2017, 2017);

	}

}
