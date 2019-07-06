package drosa.experimental.highsLowsBreak;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.billyt.TestDailyBreakout;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.TradingUtils;

public class TestHL {
	
	public static void testReverse(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int h1,int h2,
			int maxMin,
			int tp,int sl,
			boolean breakMode
			){
		
		//stats
			int wins = 0;
			int losses = 0;
			int lastDay = -1;
			ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
			ArrayList<PositionShort> closedPositions = new ArrayList<PositionShort>();
			int order = 0;
			Calendar cal = Calendar.getInstance();
			for (int i=1;i<data.size();i++){
				QuoteShort q_1 = data.get(i-1);
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				
				if (day!=lastDay){
					order = 0;
					lastDay = day;
				}
				
				int valueThr = maxMins.get(i-1).getExtra();

				int entry = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType positionType = PositionType.NONE;
				
				if (maxMin<=valueThr
							//&& q.getOpen5()<=lastMax 
							//&& PositionShort.countTotal(positions, PositionStatus.OPEN)==0
							){
					entry = q.getOpen5();
					slValue = entry-sl*10;
					tpValue = entry+tp*10;
					positionType = PositionType.LONG;
						
					if (!breakMode){
						slValue = entry+sl*10;
						tpValue = entry-tp*10;
						positionType = PositionType.SHORT;
					}
					order = Math.abs(order)+1;
				}else if (-maxMin>=valueThr
							//&& PositionShort.countTotal(positions, PositionStatus.OPEN)==0
							){
					entry = q.getOpen5();
					slValue = entry+sl*10;
					tpValue = entry-tp*10;
					positionType = PositionType.SHORT;
						
					if (!breakMode){
						slValue = entry-sl*10;
						tpValue = entry+tp*10;
						positionType = PositionType.LONG;
					}
					order = -(Math.abs(order)+1); //los low van en -
				}

				if (entry!=-1){
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(positionType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					pos.setOpenIndex(i);
					pos.setDayOrder(order);
					positions.add(pos);
				}
					
				int s = 0;
				//if (positions.size()>0) System.out.println(q1.toString());
				while (s<positions.size()){
					PositionShort p = positions.get(s);
					boolean removed = false;
					
					if (p.getPositionStatus()==PositionStatus.OPEN){
						boolean closed = false;
						double posPips = 0;
						if (p.getPositionType()==PositionType.LONG){
							if (q.getHigh5()>=p.getTp()){//funciona porque vamos en esa direccion
								p.setPositionStatus(PositionStatus.CLOSE);
								closed  = true;
								posPips = p.getTp()-p.getEntry();
							}else if (q.getLow5()<=p.getSl() 
									//&& (i)>p.getOpenIndex()
									){//optimista
								p.setPositionStatus(PositionStatus.CLOSE);
								closed  = true;
								posPips = p.getSl()-p.getEntry();
							}						
						}else if (p.getPositionType()==PositionType.SHORT){
							if (q.getLow5()<=p.getTp()){//funciona porque vamos en esa direccion
								p.setPositionStatus(PositionStatus.CLOSE);
								closed  = true;
								posPips = p.getEntry()-p.getTp();
							}else if (q.getHigh5()>=p.getSl() 
									//&& (i)>p.getOpenIndex()
									){//optimista
								p.setPositionStatus(PositionStatus.CLOSE);
								closed  = true;
								posPips = p.getEntry()-p.getSl();
							}							
						}
																					
						if (closed){
							double pipsEarned = posPips*1.0/10;
							p.setWinPips(pipsEarned);
							//System.out.println("pipsearned before comission after comm: "+posPips+" "+pipsEarned);
							if (pipsEarned>=0){
								wins++;
								p.setWin(1);
								
							}else{
								losses++;
								p.setWin(-1);
							}
							/*System.out.println("[CLOSED] "
									+" "+DateUtils.datePrint(p.getOpenCal())
									+" "+DateUtils.datePrint(cal)
									+" "+lastMax+" "+lastMin
									+" "+p.getPositionType()+" "+p.getWin()
									);*/
												
							PositionShort closedPos = new PositionShort();
							closedPos.copy(p);
							closedPositions.add(closedPos);
							
							positions.remove(s);//borramos y no avanzamos
							removed = true;
						}else{//notClosed
							//s++;//avanzamos
						}
					}
					
					if (!removed){
						s++;
					}
				}//positions
					
			}//i
				//
			String header = maxMin+"";
			for (int o=1;o<=1;o++){
					//PositionShort.studyPositions(header,closedPositions,h1,h2,tp,sl,o);
			}
			PositionShort.printSummary(closedPositions, 400);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2007.01.31_2010.12.31.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2007.01.01_2015.08.15.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2013.01.01_2015.08.15.csv";
				String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2004.01.01_2015.08.15.csv";
				String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_1 Min_Bid_2010.01.01_2015.08.15.csv";
				String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2004.01.01_2015.08.15.csv";
				String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_1 Min_Bid_2010.01.01_2015.08.15.csv";
				String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2004.01.01_2015.08.15.csv";
				
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(pathEURUSD);paths.add(pathEURJPY);
				paths.add(pathGBPUSD);paths.add(pathGBPJPY);
				paths.add(pathUSDJPY);
				
				int limit = 0;
				for (int i=0;i<=limit;i++){
					String path = paths.get(i);			
					ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
					ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
					ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
					ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
					ArrayList<QuoteShort> data = null;
					dataI.clear();
					dataS.clear();
					data5m.clear();
					data = data5mS;
					//System.out.println("data: "+data.size());
					int begin = 4000000;
					int end = data.size();
					int tp = 5;
					int sl = 20;
					int offset = 0;
					int limitPerDay = 1;
					boolean breakMode = false;
					
					ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
					
					for (int h1=0;h1<=0;h1++){
						int h2=h1+9;
						for (int minBars=500;minBars<=500;minBars+=100){
							for (tp=12;tp<=12;tp+=1)
								for (sl=(int) (3.0*tp);sl<=3.0*tp;sl+=1*tp)
									//for (sl=33;sl<=33;sl+=1)
										//for (tp=1*sl;tp<=100*sl;tp+=5*sl)
										TestHL.testReverse(data, maxMinsExt, h1, h2, minBars, tp, sl, false);
								//}
							//}
						}
					}
				}

	}

}
