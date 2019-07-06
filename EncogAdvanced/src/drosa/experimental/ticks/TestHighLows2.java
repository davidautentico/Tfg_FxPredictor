package drosa.experimental.ticks;

import java.util.ArrayList;

import drosa.classes.Tick;
import drosa.experimental.PositionShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TestHighLows2 {
	
	public static void TestHL(ArrayList<Tick> data,
			ArrayList<Integer> maxMins,			
			int h1,int h2,
			int maxTime,
			int tp,int sl,
			int offset,
			int minRange
			){
		
		
		int maxLosses = 0;
		int maxWins = 0;
		int actualLosses = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>(); 
		

		int total = 0;
		int lastDay = -1;
		int lastH = -1;
		int lastL = -1;
		int actualH = -1;
		int actualL = -1;
		int dayTrades = 0;
		int dayOpen = 0;
		for (int i=1;i<data.size()-1;i++){
			Tick t = data.get(i);
			Tick t1 = data.get(i-1);
			int h = t.getHour();
			int day = DateUtils.getDaysValue(t);
			
			if (lastDay != day){
				lastH = actualH;
				lastL = actualL;
				actualH = -1;
				actualL = -1;
				dayTrades = 0;
				dayOpen = t.getPrice();
				lastDay = day;
			}
			
			
			int actualRange = Math.abs((t.getPrice()-dayOpen)/25);
			if (t.getPrice()>=lastH+offset*25 
					&& actualRange>=minRange
					//&& t1.getPrice()<lastH
					){
				int entry = t.getPrice(); 
				PositionShort p1 = new PositionShort();
				p1.setPositionStatus(PositionStatus.OPEN);
				p1.setPositionType(PositionType.SHORT);
				p1.setEntry(entry);
				p1.setSl( entry+sl*25);
				p1.setTp( entry-tp*25);
				if (h1<=h && h<=h2 
						&& (positions.size()==0 || (positions.size()>0 && entry > positions.get(positions.size()-1).getEntry()))
						&& dayTrades<=1
						){
					positions.add(p1);
					dayTrades++;
					//System.out.println("[SHORT] price bid "+t0.getPrice()+" "+t0.getBid()+"  || "+t0.toString());
				}
			}else if (t.getPrice()<=lastL-offset*25 
					&& actualRange>=minRange
					){
				int entry = t.getPrice(); 
				PositionShort p1 = new PositionShort();
				p1.setPositionStatus(PositionStatus.OPEN);
				p1.setPositionType(PositionType.LONG);
				p1.setEntry(entry );
				p1.setSl(entry-sl*25);
				p1.setTp(entry+tp*25);
				if (h1<=h && h<=h2 
						&& (positions.size()==0 || (positions.size()>0 && entry < positions.get(positions.size()-1).getEntry()))
						&& dayTrades<=1
						){
					positions.add(p1);
					dayTrades++;
				}
			}
			
			
			//
			int j = 0;
			while (j<positions.size()){
				PositionShort pos = positions.get(j);
				
				boolean closed = false;
				int pips = 0;
				if (pos.getPositionStatus()==PositionStatus.OPEN){
					if (pos.getPositionType()==PositionType.SHORT){
						if (t.getPrice()>=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t.getPrice()<=pos.getTp()){
							closed = true;
							pips = tp;
						}
					}else if (pos.getPositionType()==PositionType.LONG){
						if (t.getPrice()<=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t.getPrice()>=pos.getTp()){
							closed = true;
							pips = tp;
						}
					}
				}
				
				if (closed){
					if (pips>=0){
						wins++;
						winPips += pips;
						actualLosses = 0;
					}else{
						losses++;
						lostPips += -pips;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}
					positions.remove(j);
				}else{
					j++;
				}
			}
			
			if (actualH==-1 || t.getPrice()>=actualH){
				actualH = t.getPrice();
			}
			if (actualL==-1 || t.getPrice()<=actualL){
				actualL = t.getPrice();
			}
			
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				h1+" "+h2
				+" "+tp+" "+sl
				+" "+offset
				+" || "
				+" "+trades
				+" "+losses
				+" "+maxLosses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
		/*int acc = 0;
		for (int i=0;i<legs.size();i++){
			//System.out.println(legs.get(i));
			acc += Math.abs(legs.get(i));
		}
		double avg = acc*1.0/legs.size();
		System.out.println(
				thr
				+" || "
				+PrintUtils.Print2(avg, false)
				);*/
	}
	
	public static void TestHL(ArrayList<Tick> data,
			ArrayList<Integer> maxMins,			
			int h1,int h2,
			int maxTime,
			int tp,int sl,
			int offset,
			int minRange
			){
		
		
		int maxLosses = 0;
		int maxWins = 0;
		int actualLosses = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>(); 
		

		int total = 0;
		int lastDay = -1;
		int lastH = -1;
		int lastL = -1;
		int actualH = -1;
		int actualL = -1;
		int dayTrades = 0;
		int dayOpen = 0;
		for (int i=1;i<data.size()-1;i++){
			Tick t = data.get(i);
			Tick t1 = data.get(i-1);
			int h = t.getHour();
			int day = DateUtils.getDaysValue(t);
			
			if (lastDay != day){
				lastH = actualH;
				lastL = actualL;
				actualH = -1;
				actualL = -1;
				dayTrades = 0;
				dayOpen = t.getPrice();
				lastDay = day;
			}
			
			
			int actualRange = Math.abs((t.getPrice()-dayOpen)/25);
			if (t.getPrice()>=lastH+offset*25 
					&& actualRange>=minRange
					//&& t1.getPrice()<lastH
					){
				int entry = t.getPrice(); 
				PositionShort p1 = new PositionShort();
				p1.setPositionStatus(PositionStatus.OPEN);
				p1.setPositionType(PositionType.SHORT);
				p1.setEntry(entry);
				p1.setSl( entry+sl*25);
				p1.setTp( entry-tp*25);
				if (h1<=h && h<=h2 
						&& (positions.size()==0 || (positions.size()>0 && entry > positions.get(positions.size()-1).getEntry()))
						&& dayTrades<=1
						){
					positions.add(p1);
					dayTrades++;
					//System.out.println("[SHORT] price bid "+t0.getPrice()+" "+t0.getBid()+"  || "+t0.toString());
				}
			}else if (t.getPrice()<=lastL-offset*25 
					&& actualRange>=minRange
					){
				int entry = t.getPrice(); 
				PositionShort p1 = new PositionShort();
				p1.setPositionStatus(PositionStatus.OPEN);
				p1.setPositionType(PositionType.LONG);
				p1.setEntry(entry );
				p1.setSl(entry-sl*25);
				p1.setTp(entry+tp*25);
				if (h1<=h && h<=h2 
						&& (positions.size()==0 || (positions.size()>0 && entry < positions.get(positions.size()-1).getEntry()))
						&& dayTrades<=1
						){
					positions.add(p1);
					dayTrades++;
				}
			}
			
			
			//
			int j = 0;
			while (j<positions.size()){
				PositionShort pos = positions.get(j);
				
				boolean closed = false;
				int pips = 0;
				if (pos.getPositionStatus()==PositionStatus.OPEN){
					if (pos.getPositionType()==PositionType.SHORT){
						if (t.getPrice()>=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t.getPrice()<=pos.getTp()){
							closed = true;
							pips = tp;
						}
					}else if (pos.getPositionType()==PositionType.LONG){
						if (t.getPrice()<=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t.getPrice()>=pos.getTp()){
							closed = true;
							pips = tp;
						}
					}
				}
				
				if (closed){
					if (pips>=0){
						wins++;
						winPips += pips;
						actualLosses = 0;
					}else{
						losses++;
						lostPips += -pips;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}
					positions.remove(j);
				}else{
					j++;
				}
			}
			
			if (actualH==-1 || t.getPrice()>=actualH){
				actualH = t.getPrice();
			}
			if (actualL==-1 || t.getPrice()<=actualL){
				actualL = t.getPrice();
			}
			
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				h1+" "+h2
				+" "+tp+" "+sl
				+" "+offset
				+" || "
				+" "+trades
				+" "+losses
				+" "+maxLosses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
		/*int acc = 0;
		for (int i=0;i<legs.size();i++){
			//System.out.println(legs.get(i));
			acc += Math.abs(legs.get(i));
		}
		double avg = acc*1.0/legs.size();
		System.out.println(
				thr
				+" || "
				+PrintUtils.Print2(avg, false)
				);*/
	}

	public static void main(String[] args) {
		String fileNameES1 = "C:\\fxdata\\futuros\\ticks\\ES1.txt";
		String fileNameES2 = "C:\\fxdata\\futuros\\ticks\\ES2.txt";
		String fileName000 = "C:\\fxdata\\futuros\\ticks\\ES_2009_2016_ticks.txt";
		String fileName00 = "C:\\fxdata\\futuros\\ticks\\ES_2009_2012_ticks.txt";
		String fileName0 = "C:\\fxdata\\futuros\\ticks\\ES_2013_2016_ticks.txt";

		
		ArrayList<String> fileNames = new ArrayList<String>();
		fileNames.add(fileNameES1);
		fileNames.add(fileNameES2);
		fileNames.add(fileName000);
		fileNames.add(fileName00);
		fileNames.add(fileName0);
		
		ArrayList<Tick> data = null;
		ArrayList<Integer> maxMins = null;
		int limit = 4;
		//int limit = fileNames.size()-1;
		for (int i=4;i<=limit;i++){
			String fileName = fileNames.get(i);
			if (data!=null) data.clear();
			if (maxMins!=null) maxMins.clear();
			
			int y1=2009;
			int y2=2016;
			data = TickUtils.readTicksDaveMinutes(fileName,y1,y2);
			//maxMins = TickUtils.calculateMaxMinMinuteBar(data);
			System.out.println("data: "+data.size());
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+23;
				for (int tp=10;tp<=10;tp++){
					for (int sl=1*tp;sl<=40*tp;sl+=tp){
						for (int maxTime=0;maxTime<=0;maxTime++){
							//TestingCountTicks.TestLegsWins(data,h1,h2, thr, tp, sl);
							for (int offset=0;offset<=0;offset++){
								for (int minRange = 0;minRange<=0;minRange+=5){
									TestHighLows2.TestHL(data,maxMins,h1,h2,maxTime, tp, sl,offset,minRange);
								}
							}
						}
					}
				}					
			}
		}//limit

	}

}
