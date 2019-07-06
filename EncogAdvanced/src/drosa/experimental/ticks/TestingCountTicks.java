package drosa.experimental.ticks;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import drosa.classes.Tick;
import drosa.experimental.PositionShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TestingCountTicks {
	
	public static void TestLegsWinsAny(ArrayList<Tick> data,
			ArrayList<Integer> maxMins,			
			int h1,int h2,
			int thr,int thr1,
			int thrMaxMin,
			int maxTime,
			int tp,int sl){
		
		
		int maxLosses = 0;
		int maxWins = 0;
		int actualLosses = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>(); 
		
		ArrayList<Integer> legs = new ArrayList<Integer>();
		int actualLeg = 0;
		int actualLen = 0;
		int total = 0;
		int lastDay = -1;
		int upIdx0 = -1;
		int downIdx0 = -1;
		int upIdx1 =1;
		int downIdx1 = -1;
		for (int i=0;i<data.size()-1;i++){
			Tick t0 = data.get(i);
			int h = t0.getHour();
			int day = DateUtils.getDaysValue(t0);
			
			if (lastDay != day){				
				actualLeg = 0;
				actualLen = 0;
				upIdx0 = i;
				downIdx0 = i;
				upIdx1 = i;
				downIdx1 = i;
				lastDay = day;
			}
			int upDiffActual = (data.get(upIdx1).getPrice()-data.get(upIdx0).getPrice())/25;
			int downDiffActual = (data.get(downIdx0).getPrice()-data.get(downIdx1).getPrice())/25;
			int upDiffNew = (t0.getPrice()-data.get(upIdx0).getPrice())/25;
			int downDiffNew = (data.get(downIdx0).getPrice()-t0.getPrice())/25;
			
			int timeUp = t0.getMinuteBar()-data.get(upIdx0).getMinuteBar();
			int timeDown = t0.getMinuteBar()-data.get(downIdx0).getMinuteBar();
			//System.out.println(timeUp+" "+timeDown);
			//int maxMin = maxMins.get(i);
			//proceso inicial de cada dia
			if (actualLeg==0){ 
				if ( upDiffNew>=thr){
					upIdx1 = i;
					actualLeg = 1;
				}else if (downDiffNew>=thr){
					downIdx1 = i;
					actualLeg = -1;
				}
			}else if (actualLeg==1){
				int downDiffReverse = (data.get(upIdx1).getPrice()-t0.getPrice())/25; 
				if (upDiffNew>=upDiffActual){ //actualizamos 
					upIdx1 = i;
				}else if (downDiffReverse>=thr){
					//añadimos leg
					legs.add(upDiffActual);
					downIdx0 = upIdx1;
					downIdx1 = i;
					actualLeg = -1;
				}
			}else if (actualLeg==-1){
				int upDiffReverse = (t0.getPrice()-data.get(downIdx1).getPrice())/25; 
				if (downDiffNew>=downDiffActual){ //actualizamos 
					downIdx1 = i;
				}else if (upDiffReverse>=thr){
					legs.add(-downDiffActual);					
					upIdx0 = downIdx1;
					upIdx1 = i;
					actualLeg = 1;
				}
			}
			
			
			/*int actualSpread = (t0.getAsk()-t0.getBid())/25;
			boolean wrongFeed = false;
			if (t0.getPrice()<t0.getAsk() || t0.getPrice()>t0.getAsk()){
				wrongFeed = true;
				actualLeg=0;
				upIdx0 = i;
				downIdx0 = i;
				upIdx1 = i;
				downIdx1 = i;
			}*/
			
			int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
			if (totalOpen<1
					//&& !wrongFeed
					){
				if (actualLeg==1 
						//&& maxMin>=thrMaxMin
						//&& timeUp>=maxTime
						&& upDiffNew>=thr1){//probamos con market order
					PositionShort p1 = new PositionShort();
					p1.setPositionStatus(PositionStatus.OPEN);
					p1.setPositionType(PositionType.SHORT);
					p1.setEntry(t0.getPrice());
					p1.setSl(t0.getPrice()+sl*25);
					p1.setTp(t0.getPrice()-tp*25);
					if (h1<=h && h<=h2){
						positions.add(p1);
						//System.out.println("[SHORT] price bid "+t0.getPrice()+" "+t0.getBid()+"  || "+t0.toString());
					}
				}else if (actualLeg==-1 
						//&& maxMin<=-thrMaxMin
						//&& timeDown>=maxTime
						&& downDiffNew>=thr1){
					PositionShort p1 = new PositionShort();
					p1.setPositionStatus(PositionStatus.OPEN);
					p1.setPositionType(PositionType.LONG);
					p1.setEntry(t0.getPrice());
					p1.setSl(t0.getPrice()-sl*25);
					p1.setTp(t0.getPrice()+tp*25);
					if (h1<=h && h<=h2){
						positions.add(p1);
					}
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
						if (t0.getPrice()>=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t0.getPrice()<=pos.getTp()){
							closed = true;
							pips = tp;
						}
					}else if (pos.getPositionType()==PositionType.LONG){
						if (t0.getPrice()<=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t0.getPrice()>=pos.getTp()){
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
			
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				h1+" "+h2
				+" "+thr+" "+thr1
				+" "+tp+" "+sl
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
	
	public static void TestLegsWins(ArrayList<Tick> data,
			int h1,int h2,
			int thr,int thrMaxMin,int tp,int sl){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>(); 
		
		ArrayList<Integer> legs = new ArrayList<Integer>();
		int actualLeg = 0;
		int actualLen = 0;
		int total = 0;
		for (int i=1;i<data.size()-1;i++){
			Tick t1 = data.get(i-1);
			Tick t2 = data.get(i);
			int h = t2.getHour();
			
			int diff = (t2.getPrice()-t1.getPrice())/25;
			//System.out.println(t2.getPrice());
			if (diff>0){
				if (actualLeg==-1){
					if (actualLen<=-thr){
						//System.out.println(actualLen);
						total++;
						
					}
					legs.add(actualLen);
					actualLen = 0;
				}
				actualLeg = 1;
				actualLen +=diff;
				
				if (actualLen==thr){
					PositionShort p1 = new PositionShort();
					p1.setPositionStatus(PositionStatus.OPEN);
					p1.setPositionType(PositionType.SHORT);
					p1.setEntry(t2.getPrice());
					p1.setSl(t2.getPrice()+sl*25);
					p1.setTp(t2.getPrice()-tp*25);
					if (h1<=h && h<=h2){
						positions.add(p1);
					}
				}
			}else if (diff<0){
				if (actualLeg==1){
					if (actualLen>=thr){
						//System.out.println(actualLen);
						total++;
					}
					legs.add(actualLen);
					actualLen = 0;
				}
				actualLeg = -1;
				actualLen +=diff;
				
				if (actualLen==-thr){
					PositionShort p1 = new PositionShort();
					p1.setPositionStatus(PositionStatus.OPEN);
					p1.setPositionType(PositionType.LONG);
					p1.setEntry(t2.getPrice());
					p1.setSl(t2.getPrice()-sl*25);
					p1.setTp(t2.getPrice()+tp*25);
					if (h1<=h && h<=h2){
						positions.add(p1);
					}
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
						if (t2.getPrice()>=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t2.getPrice()<=pos.getTp()){
							closed = true;
							pips = tp;
						}
					}else if (pos.getPositionType()==PositionType.LONG){
						if (t2.getPrice()<=pos.getSl()){
							closed = true;
							pips = -sl;
						}else if (t2.getPrice()>=pos.getTp()){
							closed = true;
							pips = tp;
						}
					}
				}
				
				if (closed){
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}
					positions.remove(j);
				}else{
					j++;
				}
			}
			
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				h1+" "+h2
				+" "+thr+" "+tp+" "+sl
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
	}

	
public static void TestLegs2(ArrayList<Tick> data,int thr){
		
		ArrayList<Integer> legs = new ArrayList<Integer>();
		int actualLeg = 0;
		int actualLen = 0;
		int total = 0;
		for (int i=1;i<data.size();i++){
			Tick t1 = data.get(i-1);
			Tick t2 = data.get(i);
			
			int diff = (t2.getPrice()-t1.getPrice())/25;
			//System.out.println(t2.getPrice());
			if (diff>0){
				if (actualLeg==-1){
					if (actualLen<=-thr){
						//System.out.println(actualLen);
						total++;
					}
					legs.add(actualLen);
					actualLen = 0;
				}
				actualLeg = 1;
				actualLen +=diff;
				
			}else if (diff<0){
				if (actualLeg==1){
					if (actualLen>=thr){
						//System.out.println(actualLen);
						total++;
					}
					legs.add(actualLen);
					actualLen = 0;
				}
				actualLeg = -1;
				actualLen +=diff;
			}
		}
		System.out.println(thr+" || "+total);
	}

	public static void TestLegs(ArrayList<Tick> data,int thr){
		
		ArrayList<Integer> legs = new ArrayList<Integer>();
		int actualLeg = 0;
		int actualLen = 0;
		int total = 0;
		for (int i=1;i<data.size();i++){
			Tick t1 = data.get(i-1);
			Tick t2 = data.get(i);
			
			int diff = (t2.getPrice()-t1.getPrice())/25;
			//System.out.println(t2.getPrice());
			if (diff>0){
				if (actualLeg==-1){
					if (actualLen<=-thr){
						//System.out.println(actualLen);
						total++;
					}
					legs.add(actualLen);
					actualLen = 0;
				}
				actualLeg = 1;
				actualLen +=diff;
				
			}else if (diff<0){
				if (actualLeg==1){
					if (actualLen>=thr){
						//System.out.println(actualLen);
						total++;
					}
					legs.add(actualLen);
					actualLen = 0;
				}
				actualLeg = -1;
				actualLen +=diff;
			}
		}
		System.out.println(thr+" || "+total);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String fileNameES1 = "C:\\fxdata\\futuros\\ticks\\ES1.txt";
		String fileNameES2 = "C:\\fxdata\\futuros\\ticks\\ES2.txt";
		String fileName000 = "C:\\fxdata\\futuros\\ticks\\ES_2009_2016_ticks.txt";
		String fileName00 = "C:\\fxdata\\futuros\\ticks\\ES_2009_2012_ticks.txt";
		String fileName0 = "C:\\fxdata\\futuros\\ticks\\ES_2013_2016_ticks.txt";
		String fileName1 = "C:\\fxdata\\futuros\\ticks\\ES_06_09_2015.txt";
		String fileName2 = "C:\\fxdata\\futuros\\ticks\\ES_07_09_2015.txt";
		String fileName3 = "C:\\fxdata\\futuros\\ticks\\ES_08_09_2015.txt";
		String fileName4 = "C:\\fxdata\\futuros\\ticks\\ES_09_09_2015.txt";
		String fileName5 = "C:\\fxdata\\futuros\\ticks\\ES_09_12_2015.txt";
		String fileName6 = "C:\\fxdata\\futuros\\ticks\\ES_10_12_2015.txt";
		String fileName7 = "C:\\fxdata\\futuros\\ticks\\ES_11_12_2015.txt";
		String fileName8 = "C:\\fxdata\\futuros\\ticks\\ES_12_12_2015.txt";
		String fileName9 = "C:\\fxdata\\futuros\\ticks\\ES_12_03_2016.txt";
		String fileName10 = "C:\\fxdata\\futuros\\ticks\\ES_01_03_2016.txt";
		String fileName11 = "C:\\fxdata\\futuros\\ticks\\ES_02_03_2016.txt";
		String fileName12 = "C:\\fxdata\\futuros\\ticks\\ES_03_03_2016.txt";
		String fileName13 = "C:\\fxdata\\futuros\\ticks\\ES_03_06_2016.txt";
		String fileName14 = "C:\\fxdata\\futuros\\ticks\\ES_04_06_2016.txt";
		
		ArrayList<String> fileNames = new ArrayList<String>();
		fileNames.add(fileNameES1);
		fileNames.add(fileNameES2);
		fileNames.add(fileName000);
		fileNames.add(fileName00);
		fileNames.add(fileName0);
		fileNames.add(fileName1);
		fileNames.add(fileName2);
		fileNames.add(fileName3);
		fileNames.add(fileName4);
		fileNames.add(fileName5);
		fileNames.add(fileName6);
		fileNames.add(fileName7);
		fileNames.add(fileName8);
		fileNames.add(fileName9);
		fileNames.add(fileName10);
		fileNames.add(fileName11);
		fileNames.add(fileName12);
		fileNames.add(fileName13);
		fileNames.add(fileName14);
		
		ArrayList<Tick> data = null;
		ArrayList<Integer> maxMins = null;
		int limit = 4;
		//int limit = fileNames.size()-1;
		for (int i=4;i<=limit;i++){
			String fileName = fileNames.get(i);
			if (data!=null) data.clear();
			if (maxMins!=null) maxMins.clear();
			
			int y1=2016;
			int y2=2016;
			data = TickUtils.readTicksDaveMinutes(fileName,y1,y2);
			//maxMins = TickUtils.calculateMaxMinMinuteBar(data);
			System.out.println("data: "+data.size());
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+23;
				for (int thr=1;thr<=1;thr++){
					for (int thr1=4*thr;thr1<=10*thr;thr1+=thr){
						for (int thrMaxMin=15;thrMaxMin<=15;thrMaxMin+=10){
							//TestingCountTicks.TestLegs(data,thr);
							for (int tp=thr1;tp<=thr1;tp++){
								for (int sl=1*tp;sl<=1*tp;sl+=1){
									for (int maxTime=0;maxTime<=0;maxTime++){
										//TestingCountTicks.TestLegsWins(data,h1,h2, thr, tp, sl);
										TestingCountTicks.TestLegsWinsAny(data,maxMins,h1,h2, thr,thr1,thrMaxMin,maxTime, tp, sl);
									}
								}
							}	
						}
					}
				}
			}
		}
	}

}
