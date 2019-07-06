package drosa.experimental.spikeStudy;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.SuperStrategy;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.DataCleaning;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TickSpikes {

	private static ArrayList<Integer> getBreaches(ArrayList<QuoteShort> data,
			int begin, int end,int h1,int h2, int offset) {
		
		ArrayList<Integer> breaches = new ArrayList<Integer>();
		
		int total = 0;
		int end2 = end;
		if (end>data.size()-1) end2 = data.size()-1;
		
		if (begin<1) begin =1;
		
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end2;i++){
			QuoteShort q   = data.get(i);
			QuoteShort q_1 = data.get(i-1);
			
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h<h1 || h>h2) continue;
			
			int diffH = q.getHigh5()-q_1.getHigh5();
			int diffL = q_1.getLow5()-q.getLow5();
			
			if (diffH>=offset*10){
				breaches.add(i);
			}
			if (diffL>=offset*10){
				breaches.add(-i);//negativo significa low
			}
			total++;
		}
		
		//System.out.println("total data: "+total);
		return breaches;
	}
	
	public static String getMonth(int num){
		if (num==1) return "01";
		if (num==2) return "02";
		if (num==3) return "03";
		if (num==4) return "04";
		if (num==5) return "05";
		if (num==6) return "06";
		if (num==7) return "07";
		if (num==8) return "08";
		if (num==9) return "09";
		if (num==10) return "10";
		if (num==11) return "11";
		if (num==12) return "12";
		return "00";
	}
	
	public static double testBarsSpikes(String header,ArrayList<QuoteShort> data,
			int begin,int end,String hours,int bars,int offset,int diffOffset,
			int tp,int sl,double comm,boolean modeOffset,boolean debug){
	
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		
		if (end>data.size()-1) end = data.size()-1;
		if (begin<0) begin = 0; 
		
		int wins = 0;
		int losses = 0;
		
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		QuoteShort.getCalendar(cal, data.get(begin));
		for (int i=begin;i<=end-1;i++){
					
			QuoteShort q = data.get(i);	
			QuoteShort q1 = data.get(i+1);	
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int allowed = allowedHours.get(h);
			if (allowed==0) continue;
									
			QuoteShort ql = TradingUtils.getMaxMinShort(data, i-bars, i-1);	
			int diffH = q.getHigh5()-ql.getHigh5();
			int diffL = ql.getLow5()-q.getLow5();			
			if (diffH<offset*10 && diffL<offset*10) continue;
			int entryValue = 0;
			int index = i;
			if (!modeOffset) index = i+1;
			if (diffH>=offset*10){
				int win = 0;
				int diff = 0;
				if (modeOffset){
					entryValue = ql.getHigh5()+10*offset;
				}
				else{
					diff = q.getHigh5()-q.getClose5();
					if (diff*0.7>=diffOffset && q.getClose5()<=q.getOpen5()){
						entryValue = (int) (q1.getOpen5()+0.7*diff);
					}else{
						entryValue = q1.getOpen5();
						continue;
					}
				}
				int tpValue = entryValue-10*tp;
				int slValue = entryValue+10*sl;
				boolean finish=false;
				boolean isOpen = false;
				int indexOpen = -1;
				//for (int j=i;j<=end && !finish;j++){
				for (int j=index;j<=end && !finish;j++){
					QuoteShort qj = data.get(j);
					QuoteShort.getCalendar(cal2, qj);
					if (!isOpen){
						if (qj.getLow5()<=entryValue && entryValue<=qj.getHigh5()){
							isOpen = true;
							indexOpen = j;
							/*System.out.println("[OPEN SELL] "+(int)(diff*0.7)+" "+DateUtils.datePrint(cal2)
									+" "+entryValue+" "+slValue+" "+tpValue
									+" || "+q.toString()
									);*/
						}else if (j-index>=5){
							finish = true;
						}
					}
					if (isOpen && qj.getHigh5()-slValue>=0){
						//System.out.println("[CLOSE SELL(SL)] "+DateUtils.datePrint(cal2));
						losses++;
						win=-1;
						finish = true;
					}else if (isOpen && tpValue-qj.getLow5()>=0 && indexOpen!=j){
						//System.out.println("[CLOSE SELL(TP)] "+DateUtils.datePrint(cal2));
						wins++;
						win=1;
						finish = true;
					}else if (isOpen && tpValue-qj.getClose5()>=0 && indexOpen==j){
						//System.out.println("[CLOSE SELL(TPclose)] "+DateUtils.datePrint(cal2));
						wins++;
						win=1;
						finish = true;
					}
				}
				//System.out.println("found: "+ql.toString()+" || "+q.toString()+" || "+win);
			}
			if (diffL>=offset*10){
				int win = 0;
				int diff = 0;
				if (modeOffset){
					entryValue = ql.getLow5()-10*offset;
				}else{
					diff = q.getClose5()-q.getLow5();
					if (diff*0.7>=diffOffset && q.getClose5()>=q.getOpen5()){
						entryValue = (int) (q1.getOpen5()-0.7*diff);
					}else{
						entryValue = q1.getOpen5();
						continue;
					}
				}
				int tpValue = entryValue+10*tp;
				int slValue = entryValue-10*sl;
				boolean finish=false;
				boolean isOpen = false;
				int indexOpen = -1;
				//System.out.println("TRADE y q1 "+entryValue+" "+slValue+" "+tpValue+" || "+diff+" "+q.toString());
				//for (int j=i;j<=end && !finish;j++){
				for (int j=index;j<=end && !finish;j++){
					QuoteShort qj = data.get(j);
					QuoteShort.getCalendar(cal2, qj);
					if (!isOpen){
						if (qj.getLow5()<=entryValue && entryValue<=qj.getHigh5()){
							isOpen = true;
							indexOpen = j;
							//System.out.println("[OPEN BUY] "+DateUtils.datePrint(cal2)+" "+entryValue+" "+slValue+" "+tpValue);
						}else if (j-index>=5){
							finish = true;
						}
					}
					if (isOpen && slValue-qj.getLow5()>=0){
						//System.out.println("entry SL "+entryValue+" "+t2.toString());
						//System.out.println("[CLOSE BUY(SL)] "+DateUtils.datePrint(cal2));
						losses++;
						win=-1;
						finish = true;
					}else if (isOpen && qj.getHigh5()-tpValue>=0 && indexOpen!=j){
						//System.out.println("[CLOSE BUY(TP)] "+DateUtils.datePrint(cal2));
						wins++;
						win=1;
						finish = true;
					}else if (isOpen && qj.getClose5()-tpValue>=0 && indexOpen==j){
						//System.out.println("[CLOSE BUY(TPclose)] "+DateUtils.datePrint(cal2));
						wins++;
						win=1;
						finish = true;
					}
				}
				//System.out.println("found: "+ql.toString()+" || "+q.toString()+" || "+win);
			}
		}
		double winPer = wins*100.0/(wins+losses);
		double exp = winPer*tp-(100.0-winPer)*sl;
		double pf = wins*tp*1.0/(losses*sl*1.0);
		double pfAdjusted = wins*(tp-comm)*1.0/(losses*(sl+comm)*1.0);
		QuoteShort.getCalendar(cal, data.get(begin));
		if (debug)
		System.out.println(
				DateUtils.datePrint(cal)
				+" "+header
				+" "+offset+" "+tp+" "+sl+" "+wins+" "+losses
				+" "+PrintUtils.Print(winPer)
				+" "+PrintUtils.Print(exp/100.0)
				+" "+PrintUtils.Print(pfAdjusted)
				+" ("+PrintUtils.Print(pf)+")"
				);
		return pf;
	}
	
	public static void testTickSpikes2(String header,ArrayList<QuoteShort> data,
			int begin,int end,int h1,int h2,int bars,int offset,int tp,int sl){
	
		if (end>data.size()-1) end = data.size()-1;
		if (begin<0) begin = 0; 
		
		int wins = 0;
		int losses = 0;
		
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		QuoteShort.getCalendar(cal, data.get(begin));
		String lastFileName ="";
		ArrayList<Tick> ticks = null;
		int indexTick = 0;
		File f = null;
		for (int i=begin;i<=end;i++){
					
			QuoteShort q = data.get(i);						
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h<h1 || h>h2) continue;
									
			String fileName ="C:\\fxdata\\ticks\\dukas\\EURUSD_"+getMonth(month+1)+"_"+year+"_tick_data.csv";
						
			if (!fileName.equalsIgnoreCase(lastFileName)){
				f = new File(fileName);
				if (!f.exists()){
					lastFileName = fileName;
					indexTick = 0;
					continue;
				}
				//System.out.println("filename: "+fileName+" "+wins+" "+losses);
				ticks =  Tick.readFromDisk(fileName, 3);
				lastFileName = fileName;
				indexTick = 0;
			}
			
			if (ticks==null) continue;
			
			
			QuoteShort ql = TradingUtils.getMaxMinShort(data, i-bars, i-1);	
			int diffH = q.getHigh5()-ql.getHigh5();
			int diffL = ql.getLow5()-q.getLow5();			
			if (diffH<offset*10 && diffL<offset*10) continue;
			
			int idx 	  = Tick.findMinuteIndex(ticks, cal, indexTick);
			
			if (idx!=-1){
				Tick.getCalendar(cal2, ticks.get(idx));
				//System.out.println(DateUtils.datePrint(cal)+" "+DateUtils.datePrint(cal2)+" "+ticks.get(idx).toString());
				boolean found = false;
				for (int k=idx;k<ticks.size() && !found;k++){ //para cada tick
					Tick t = ticks.get(k);
					if (diffH>=offset*10 && !found){
						int diff = t.getAsk()-ql.getHigh5();
						if (diff>=offset*10){
							//System.out.println("found: "+ql.toString()+" || "+t.toString());
							found = true;
							int win = 0;
							int entryValue = ql.getHigh5()+10*offset;
							int tpValue = entryValue-10*tp;
							int slValue = entryValue+10*sl;
							boolean finish=false;
							for (int m=k;m<=ticks.size()-1 && !finish;m++){
								Tick t2 = ticks.get(m);
								if (t2.getAsk()-slValue>=0){
									//System.out.println("entry SL "+entryValue+" "+t2.toString());
									losses++;
									finish=true;
									win=-1;
								}else if (tpValue-t2.getAsk()>=0){
									wins++;
									finish=true;
									win=1;
								}
							}//for
							System.out.println("found: "+ql.toString()+" || "+t.toString()+" || "+win);
						}//diff
					}//high && !found

					if (diffL>=offset*10 && !found){
						int diff = ql.getLow5()-t.getBid();//-q_1.getHigh5();
						if (diff>=offset*10){							
							found = true;
							int win = 0;
							int entryValue = ql.getLow5()-10*offset;
							int tpValue = entryValue+10*tp;
							int slValue = entryValue-10*sl;
							boolean finish=false;
							for (int m=k;m<=ticks.size()-1 && !finish;m++){
								Tick t2 = ticks.get(m);
								Tick.getCalendar(cal3, t2);
								int min = cal3.get(Calendar.MINUTE);
								if (slValue-t2.getBid()>=0){
									losses++;
									finish=true;
									win=-1;
								}else if (t2.getBid()-tpValue>=0){
									wins++;
									finish=true;
									win = 1;
								}
							}//for
							System.out.println("found: "+ql.toString()+" || "+t.toString()+ " || "+win);
						}//diff
					}//diffL && !found					
				}//idx	
				indexTick = idx;
			}
		}
		double winPer = wins*100.0/(wins+losses);
		double exp = winPer*tp-(100.0-winPer)*sl;
		System.out.println(header
				+" "+offset+" "+tp+" "+sl+" "+wins+" "+losses
				+" "+PrintUtils.Print(winPer)
				+" "+PrintUtils.Print(exp/100.0)
				);
	}
	
	public static void testTickSpikes(String header,ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxs,
			int begin,int end,int h1,int h2,int offset,int tp,int sl){
	
		int wins = 0;
		int losses = 0;
		ArrayList<Integer> breachIndexes = getBreaches(data,begin,end,h1,h2,offset);
		if (breachIndexes.size()==0) return;
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		QuoteShort.getCalendar(cal, data.get(Math.abs(breachIndexes.get(0))));
		//System.out.println("total Breaches: "+breachIndexes.size()+" "+DateUtils.datePrint(cal));
		String lastFileName ="";
		ArrayList<Tick> ticks = null;
		int indexTick = 0;
		int indexMaxs = 0;
		File f = null;
		int lastDay = -1;
		int maxMinValue = 0;
		for (int i=0;i<breachIndexes.size();i++){
			int index = breachIndexes.get(i);
			QuoteShort q = data.get(Math.abs(index));
			QuoteShort q_1 = data.get(Math.abs(index)-1);
			
			
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h<h1 || h>h2) continue;
									
			String fileName ="C:\\fxdata\\ticks\\dukas\\EURUSD_"+getMonth(month+1)+"_"+year+"_tick_data.csv";
						
			if (!fileName.equalsIgnoreCase(lastFileName)){
				f = new File(fileName);
				if (!f.exists()){
					lastFileName = fileName;
					indexTick = 0;
					continue;
				}
				//System.out.println("filename: "+fileName+" "+wins+" "+losses);
				ticks =  Tick.readFromDisk(fileName, 3);
				lastFileName = fileName;
				indexTick = 0;
			}
			
			if (ticks==null) continue;
					
			int idxMaxMin = Tick.findMinuteIndexQS(maxs, cal, indexMaxs);
			if (idxMaxMin!=-1){
				maxMinValue = maxs.get(idxMaxMin).getExtra();
				indexMaxs = idxMaxMin; 
			}else continue;
			int idx 	  = Tick.findMinuteIndex(ticks, cal, indexTick);
			
			if (idx!=-1){
				Tick.getCalendar(cal2, ticks.get(idx));
				//System.out.println(DateUtils.datePrint(cal)+" "+DateUtils.datePrint(cal2)+" "+ticks.get(idx).toString());
				boolean found = false;
				for (int k=idx;k<ticks.size() && !found;k++){
					Tick t = ticks.get(k);
					if (maxMinValue==1){
						if (!found){
							int diff = t.getAsk()-q_1.getHigh5();
							if (diff>=offset*10){
								//System.out.println("found: "+q_1.toString()+" || "+t.toString());
								found = true;
								int entryValue = q_1.getHigh5()+10*offset;
								int tpValue = entryValue-10*tp;
								int slValue = entryValue+10*sl;
								boolean finish=false;
								for (int m=k;m<=ticks.size()-1 && !finish;m++){
									Tick t2 = ticks.get(m);
									if (t2.getAsk()-slValue>=0){
										//System.out.println("entry SL "+entryValue+" "+t2.toString());
										losses++;
										finish=true;
									}else if (tpValue-t2.getAsk()>=0){
										wins++;
										finish=true;
									}
								}//for
							}//diff
						}//found
					}//isHigh
					
					if (maxMinValue==-1){
						if (!found){
							int diff = q_1.getLow5()-t.getBid();//-q_1.getHigh5();
							if (diff>=offset*10){
								found = true;
								int entryValue = q_1.getLow5()-10*offset;
								int tpValue = entryValue+10*tp;
								int slValue = entryValue-10*sl;
								boolean finish=false;
								for (int m=k;m<=ticks.size()-1 && !finish;m++){
									Tick t2 = ticks.get(m);
									if (slValue-t2.getBid()>=0){
										losses++;
										finish=true;
									}else if (t2.getBid()-tpValue>=0){
										wins++;
										finish=true;
									}
								}//for
							}//diff
						}//found
					}
				}//idx	
				indexTick = idx;
			}
		}
		double winPer = wins*100.0/(wins+losses);
		double exp = winPer*tp-(100.0-winPer)*sl;
		System.out.println(header
				+" "+offset+" "+tp+" "+sl+" "+wins+" "+losses
				+" "+PrintUtils.Print(winPer)
				+" "+PrintUtils.Print(exp/100.0)
				);
	}
	
	public static void convertTickFiles(String symbol,String path,String destPath){
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();
	    Calendar cal = Calendar.getInstance();
	    for (File file : listOfFiles) {
	    	if (file.isFile() && file.getName().contains("Ticks") && file.getName().contains(symbol)){                            
	    		System.out.println(file.getAbsolutePath()+" "+file.getName());
	    		String fileName = file.getAbsolutePath();
	    		ArrayList<Tick> ticks = Tick.readFromDisk(fileName, 2);
	    		
		  		ArrayList<Tick> ticksS 	= TestLines.calculateCalendarAdjustedT(ticks);
		  		ArrayList<Tick> data 	= TradingUtils.cleanWeekendDataT(ticksS);
		  		System.out.println("Initial data size y cleaned: "+ticks.size()+" "+data.size());
		  		//System.out.println("First quote "
		  		//		+" "+DateUtils.datePrint(dataI.get(0).getDate())
		  		//		+" "+DateUtils.datePrint(data.get(400000).getDate()));
		  		int index = ticks.size()/2;
		  		
		  		Tick.getCalendar(cal, data.get(index));
		  		int month = cal.get(Calendar.MONTH);
		  		int year = cal.get(Calendar.YEAR);
	  		
		  		DataCleaning.writeFileTick(symbol,data, month, year,destPath);	  		
	      }
	    }
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.09.csv";
		//String path5m   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\AUDNZD_UTC_5 Mins_Bid_2006.12.12_2014.11.27.csv";
		//String path5m   = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2014.10.31.csv";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
  		//ArrayList<Quote> data15m 	= ConvertLib.convert(data5m, 3);
  		//ArrayList<Quote> data30m 	= ConvertLib.convert(data5m, 6);
  		//ArrayList<Quote> data60m 	= ConvertLib.convert(data5m, 12);
  		//ArrayList<Quote> dailyData 	= ConvertLib.createDailyData(data5m);
		ArrayList<QuoteShort> data5mS  = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		//ArrayList<QuoteShort> data15mS  = QuoteShort.convertQuoteArraytoQuoteShort(data15m);
		//ArrayList<QuoteShort> data30mS  = QuoteShort.convertQuoteArraytoQuoteShort(data30m);
		//ArrayList<QuoteShort> data60mS  = QuoteShort.convertQuoteArraytoQuoteShort(data60m);
		//ArrayList<QuoteShort> dailyDataS  = QuoteShort.convertQuoteArraytoQuoteShort(dailyData);
		//QuoteShort.saveToDisk(data5mS,"c:\\data5digits.csv");
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		//data = data15mS;
		//data = data30mS;
		//data = data60mS;
		
		
		int begin = 1;
		int end = data.size()-1;
		int offset1 = 20;
		String tickFolder = "C:\\fxdata\\ticks\\dukas";
		int sl = 10;
		int tp = 10;
		double comm = 1.5;
		//TickSpikes.convertTickFiles("EURUSD", tickFolder, tickFolder);
		Calendar cal = Calendar.getInstance();
		QuoteShort.getCalendar(cal, data.get(begin));
		System.out.println("total data : "+path5m+" "+data.size()+" "+DateUtils.datePrint(cal));
		for (int bars=1000;bars<=1000;bars+=100){
			double avg=0;
			int total = 0;
			for (begin=400000;begin<=400000;begin+=100000){
				end = begin+900000;
				for (int h1=0;h1<=0;h1++){
					int h2=h1+0;
					//for (int bars=400;bars<=400;bars+=1){
						String header = String.valueOf(bars);
						String line = PrintUtils.Print2Int(bars,5)+" ";
						for (h1=0;h1<=0;h1++){
							//ArrayList<QuoteShort> maxMin1 = TradingUtils.calculateMaxMinByBarShort(data,bars);
							for (offset1=0;offset1<=0;offset1++){
								for (tp=5;tp<=15;tp+=1){
									//System.out.println(tp);
									for (sl=(int) (1.0*tp);sl<=10.0*tp;sl+=1*tp){
										for (int diffOffset=20;diffOffset<=20;diffOffset+=1){			
											//double pf = TickSpikes.testBarsSpikes(header,data,begin, end,String.valueOf(h1),bars, offset1,diffOffset,
											double pf = TickSpikes.testBarsSpikes(header,data,begin, end,"0 1 2 3 4 5 6 7 8 9 23",bars, offset1,diffOffset,
													tp,sl,comm,false,true);
											line+=PrintUtils.Print2dec(pf, false, 2)+" ";
											//System.out.println();
											avg+=pf;
											total++;
										}
									}
								}
							}
						}
						//System.out.println(line);
					//}//bars
				}
			}//begin
			//System.out.println(bars+" "+PrintUtils.Print2(avg*1.0/total));
		}//bars
	}

}
