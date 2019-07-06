package drosa.apuestas;

import java.io.IOException;
import java.util.ArrayList;

import drosa.utils.HttpDownloadUtility;

public class DownloadHistoricalResults {

	
	public static String decodeSeason(int year){
		if (year==1990) return "9091";
		if (year==1991) return "9192";
		if (year==1992) return "9293";
		if (year==1993) return "9394";
		if (year==1994) return "9495";
		if (year==1995) return "9596";
		if (year==1996) return "9697";
		if (year==1997) return "9798";
		if (year==1998) return "9899";
		if (year==1999) return "9900";
		if (year==2000) return "0001";
		if (year==2001) return "0102";
		if (year==2002) return "0203";
		if (year==2003) return "0304";
		if (year==2004) return "0405";
		if (year==2005) return "0506";
		if (year==2006) return "0607";
		if (year==2007) return "0708";
		if (year==2008) return "0809";
		if (year==2009) return "0910";
		if (year==2010) return "1011";
		if (year==2011) return "1112";
		if (year==2012) return "1213";
		if (year==2013) return "1314";
		if (year==2014) return "1415";
		if (year==2015) return "1516";
		
		return "0000";
	}
	
	public static void main(String[] args) throws InterruptedException{
		// TODO Auto-generated method stub
		ArrayList<String> leagues = new ArrayList<String>();
		leagues.add("SP1");
		leagues.add("SP2");
		leagues.add("SC0");
		leagues.add("SC1");
		leagues.add("SC2");
		leagues.add("SC3");
		leagues.add("E0");
		leagues.add("E1");
		leagues.add("E2");
		leagues.add("E3");
		leagues.add("EC");
		leagues.add("D1");
		leagues.add("D2");
		leagues.add("I1");
		leagues.add("I2");
		leagues.add("F1");
		leagues.add("F2");
		leagues.add("N1");
		leagues.add("B1");
		leagues.add("D1");
		leagues.add("P1");
		leagues.add("T1");
		leagues.add("G1");
		for (int year = 1990;year<=2014;year++){
			//String fileUrl = "http://www.football-data.co.uk/mmz4281/1112/SC0.csv";
			String season = decodeSeason(year);
			for (int i=0;i<leagues.size();i++){
				String prefix = leagues.get(i);
				String fileUrl = "http://www.football-data.co.uk/mmz4281/"+season+"/"+prefix+".csv";
				String fileTarget = prefix+"_"+year+".csv";
				System.out.println(fileUrl);
				try{
					HttpDownloadUtility.downloadFile(fileUrl, "D:\\apuestas\\futbol\\",fileTarget);
				}catch(Exception e){
					System.out.println("[ERROR] "+fileUrl+" exc: "+e.getMessage());
				}
				Thread.sleep(100);
			}
		}
		
		
	}

}
