package drosa.data;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.finances.Quote;
import drosa.finances.QuoteBidAsk;
import drosa.finances.QuoteShort;
import drosa.phil.DataCleaning;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class ConvertLib {
	
	public static ArrayList<Quote> createDailyDataShort(ArrayList<Quote> data,ArrayList<Calendar> gmtAdjusted){
        
        System.out.println("data size: "+data.size());
        ArrayList<Quote> days = new ArrayList<Quote>();
        int lastDay=-1;
        Quote lastDayQ=new Quote();
        Quote actualDayQ= new Quote();
        for (int i=0;i<data.size();i++){
            Quote q0 = data.get(i);
            Quote q = new Quote();
            q.copy(q0);
            Calendar qCal = gmtAdjusted.get(i);
            int h = qCal.get(Calendar.HOUR_OF_DAY);
            int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
            int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
            q.setDate(qCal.getTime());
            
            //System.out.println("actualDay lastDay h: "+actualDay+" "+lastDay+" "+h+" "+dayWeek);
            //no contamos los sabados ni domingos
            if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
		continue;
            }
            
            if (actualDay!=lastDay && h==0){
		if (lastDay!=-1){
                    Quote qNew = new Quote();
                    qNew.copy(actualDayQ);
                    days.add(qNew);
		//System.out.println("anyadiendo dailyData: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
		}
		lastDayQ.copy(actualDayQ);
		actualDayQ.copy(q);
		lastDay=actualDay;
            }
			
            //actualiamos actualDayQ
            actualDayQ.setClose(q.getClose());
            if (q.getHigh()>actualDayQ.getHigh())
		actualDayQ.setHigh(q.getHigh());
            if (q.getLow()<actualDayQ.getLow())
		actualDayQ.setLow(q.getLow());
        }
        
        return days;
    }
	
	 public static ArrayList<Quote> createDailyData(ArrayList<Quote> data,ArrayList<Calendar> gmtAdjusted){
	        
	        System.out.println("data size: "+data.size());
	        ArrayList<Quote> days = new ArrayList<Quote>();
	        int lastDay=-1;
	        Quote lastDayQ=new Quote();
	        Quote actualDayQ= new Quote();
	        for (int i=0;i<data.size();i++){
	            Quote q0 = data.get(i);
	            Quote q = new Quote();
	            q.copy(q0);
	            Calendar qCal = gmtAdjusted.get(i);
	            int h = qCal.get(Calendar.HOUR_OF_DAY);
	            int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
	            int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
	            q.setDate(qCal.getTime());
	            
	            //System.out.println("actualDay lastDay h: "+actualDay+" "+lastDay+" "+h+" "+dayWeek);
	            //no contamos los sabados ni domingos
	            if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
			continue;
	            }
	            
	            if (actualDay!=lastDay && h==0){
			if (lastDay!=-1){
	                    Quote qNew = new Quote();
	                    qNew.copy(actualDayQ);
	                    days.add(qNew);
			//System.out.println("anyadiendo dailyData: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
			}
			lastDayQ.copy(actualDayQ);
			actualDayQ.copy(q);
			lastDay=actualDay;
	            }
				
	            //actualiamos actualDayQ
	            actualDayQ.setClose(q.getClose());
	            if (q.getHigh()>actualDayQ.getHigh())
			actualDayQ.setHigh(q.getHigh());
	            if (q.getLow()<actualDayQ.getLow())
			actualDayQ.setLow(q.getLow());
	        }
	        
	        return days;
	    }
	 
	 public static ArrayList<QuoteShort> createDailyDataShort(ArrayList<QuoteShort> data){
	        
	        ArrayList<QuoteShort> days = new ArrayList<QuoteShort>();
	        int lastDay=-1;
	        QuoteShort lastDayQ=new QuoteShort();
	        QuoteShort actualDayQ= new QuoteShort();
	        Calendar qCal = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	        	QuoteShort q0 = data.get(i);
	        	QuoteShort q = new QuoteShort();
	            q.copy(q0);
	            QuoteShort.getCalendar(qCal, q0);
	            int h = qCal.get(Calendar.HOUR_OF_DAY);
	            int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
	            int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
	            q.setCal(qCal);
	            
	            //System.out.println("actualDay lastDay h: "+actualDay+" "+lastDay+" "+h+" "+dayWeek);
	            //no contamos los sabados ni domingos
	            if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            	continue;
	            }
	            
	            if (actualDay!=lastDay){
	            	if (lastDay!=-1){
	            		QuoteShort qNew = new QuoteShort();
	                    qNew.copy(actualDayQ);
	                    days.add(qNew);
	                    //System.out.println("andiendo dailyData: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
	            	}
	            	lastDayQ.copy(actualDayQ);
	            	actualDayQ.copy(q);
	            	lastDay=actualDay;
	            }
				
	            //actualiamos actualDayQ
	            actualDayQ.setClose5(q.getClose5());
	            if (q.getHigh5()>actualDayQ.getHigh5())
	            	actualDayQ.setHigh5(q.getHigh5());
	            if (q.getLow5()<actualDayQ.getLow5())
	            	actualDayQ.setLow5(q.getLow5());
	        }
	        //LAST
	        QuoteShort qNew = new QuoteShort();
	        qNew.copy(actualDayQ);
	        days.add(qNew);
         
	        return days;
	    }
	 
	 public static int get4h(Calendar cal){
		 int h = cal.get(Calendar.HOUR_OF_DAY);
		 int min = cal.get(Calendar.MINUTE);
		 
		 if (h>=0 && h<4) return 0;
		 if (h>=4 && h<8) return 4;
		 if (h>=8 && h<12) return 8;
		 if (h>=12 && h<16) return 12;
		 if (h>=16 && h<20) return 16;
		 if (h>=20 && (h<=23 && min<=59)) return 20;
		 return 0;
	 }
	 
	 public static ArrayList<QuoteShort> create4H(ArrayList<QuoteShort> data){
	        
	        ArrayList<QuoteShort> fourHs = new ArrayList<QuoteShort>();
	        
	        int last4h = -1;
	        QuoteShort qActual= new QuoteShort();
	        Calendar cal = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	        	QuoteShort q = data.get(i);
	            QuoteShort.getCalendar(cal, q);
	            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
	            //no contamos los sabados ni domingos
	            if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            	continue;
	            }
	            
	            int actual4h = get4h(cal);
	            //System.out.println(DateUtils.datePrint(cal)+" "+actual4h);
	            if (actual4h!=last4h){
	            	if (last4h!=-1){
	            		QuoteShort qNew = new QuoteShort();
	            		qNew.copy(qActual);
	            		fourHs.add(qNew);
	            		//System.out.println("añadida: "+qNew.toString());
	            	}
	            	qActual.copy(q);
	            	//System.out.println(qActual.toString());
	            	last4h = actual4h;
	            }
	            
	            if (q.getHigh5()>=qActual.getHigh5())
	            	qActual.setHigh5(q.getHigh5());
	            if (q.getLow5()<=qActual.getLow5())
	            	qActual.setLow5(q.getLow5());
	            qActual.setClose5(q.getClose5());
	        }
	        //LAST
	        QuoteShort qNew = new QuoteShort();
	        qNew.copy(qActual);
	        fourHs.add(qNew);
      
	        return fourHs;
	    }
	 
	 public static ArrayList<Quote> createDailyData(ArrayList<Quote> data){
	        	        
	        ArrayList<Quote> days = new ArrayList<Quote>();
	        int lastDay=-1;
	        Quote lastDayQ=new Quote();
	        Quote actualDayQ= new Quote();
	        Calendar qCal = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	            Quote q0 = data.get(i);
	            Quote q = new Quote();
	            q.copy(q0);
	            qCal.setTime(q0.getDate());
	            int h = qCal.get(Calendar.HOUR_OF_DAY);
	            int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
	            int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
	            q.setDate(qCal.getTime());
	            
	            //System.out.println("actualDay lastDay h: "+actualDay+" "+lastDay+" "+h+" "+dayWeek);
	            //no contamos los sabados ni domingos
	            if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            	continue;
	            }
	            
	            if (actualDay!=lastDay){
	            	if (lastDay!=-1){
	                    Quote qNew = new Quote();
	                    qNew.copy(actualDayQ);
	                    days.add(qNew);
	                    //System.out.println("andiendo dailyData: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
	            	}
	            	lastDayQ.copy(actualDayQ);
	            	actualDayQ.copy(q);
	            	lastDay=actualDay;
	            }
				
	            //actualiamos actualDayQ
	            actualDayQ.setClose(q.getClose());
	            if (q.getHigh()>actualDayQ.getHigh())
	            	actualDayQ.setHigh(q.getHigh());
	            if (q.getLow()<actualDayQ.getLow())
	            	actualDayQ.setLow(q.getLow());
	        }
	        //LAST
	        Quote qNew = new Quote();
            qNew.copy(actualDayQ);
            days.add(qNew);
            
	        return days;
	    }
	    
	    public static ArrayList<Quote> createWeeklyData(ArrayList<Quote> data) {
			// TODO Auto-generated method stub
			ArrayList<Quote> weeks = new ArrayList<Quote>(); 
			int lastWeek=-1;
			Quote lastWeekQ=new Quote();
			Quote actualWeekQ= new Quote();
			//System.out.println("tamaï¿½o data en calculate: "+data.size());
			Calendar qCal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){			
				Quote q = data.get(i);
	            qCal.setTime(q.getDate());
				int h = qCal.get(Calendar.HOUR_OF_DAY);
				int actualWeek = qCal.get(Calendar.WEEK_OF_YEAR);
				int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
				//no contamos los sabados ni domingos
				if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
					continue;
				}
				
				if (actualWeek!=lastWeek){
					if (lastWeek!=-1){
						Quote qNew = new Quote();
						qNew.copy(actualWeekQ);
						weeks.add(qNew);
						//System.out.println("aï¿½adiendo dailyData: "+DateUtils.datePrint(qNew.getDate()));
					}
					lastWeekQ.copy(actualWeekQ);
					actualWeekQ.copy(q);
					lastWeek=actualWeek;
				}
				
				//actualiamos actualDayQ
				actualWeekQ.setClose(q.getClose());
				if (q.getHigh()>actualWeekQ.getHigh())
					actualWeekQ.setHigh(q.getHigh());
				if (q.getLow()<actualWeekQ.getLow())
					actualWeekQ.setLow(q.getLow());			
			}
			
			Quote qNew = new Quote();
			qNew.copy(actualWeekQ);
			weeks.add(qNew);
			
			return weeks;
		}
	    
	    public static ArrayList<Quote> createWeeklyData(ArrayList<Quote> data, ArrayList<Calendar> gmtAdjusted) {
			// TODO Auto-generated method stub
			ArrayList<Quote> weeks = new ArrayList<Quote>(); 
			int lastWeek=-1;
			Quote lastWeekQ=new Quote();
			Quote actualWeekQ= new Quote();
			//System.out.println("tamaï¿½o data en calculate: "+data.size());
			for (int i=0;i<data.size();i++){			
				Quote q = data.get(i);
	                        Calendar qCal = gmtAdjusted.get(i);
				int h = qCal.get(Calendar.HOUR_OF_DAY);
				int actualWeek = qCal.get(Calendar.WEEK_OF_YEAR);
				int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
				//no contamos los sabados ni domingos
				if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
					continue;
				}
				
				if (actualWeek!=lastWeek){
					if (lastWeek!=-1){
						Quote qNew = new Quote();
						qNew.copy(actualWeekQ);
						weeks.add(qNew);
						//System.out.println("aï¿½adiendo dailyData: "+DateUtils.datePrint(qNew.getDate()));
					}
					lastWeekQ.copy(actualWeekQ);
					actualWeekQ.copy(q);
					lastWeek=actualWeek;
				}
				
				//actualiamos actualDayQ
				actualWeekQ.setClose(q.getClose());
				if (q.getHigh()>actualWeekQ.getHigh())
					actualWeekQ.setHigh(q.getHigh());
				if (q.getLow()<actualWeekQ.getLow())
					actualWeekQ.setLow(q.getLow());			
			}
			
			Quote qNew = new Quote();
			qNew.copy(actualWeekQ);
			weeks.add(qNew);
			
			return weeks;
		}
	    
	    public  static ArrayList<Quote> createMonthlyData(ArrayList<Quote> data) {
			// TODO Auto-generated method stub
			ArrayList<Quote> months = new ArrayList<Quote>(); 
			int lastMonth=-1;
			Quote lastMonthQ=new Quote();
			Quote actualMonthQ= new Quote();
			Calendar qCal = Calendar.getInstance();
			//System.out.println("tamaï¿½o data en calculate: "+data.size());
			for (int i=0;i<data.size();i++){			
				Quote q = data.get(i);
				qCal.setTime(q.getDate());
				int h = qCal.get(Calendar.HOUR_OF_DAY);
				int actualMonth = qCal.get(Calendar.MONTH);
				int dayMonth = qCal.get(Calendar.DAY_OF_MONTH);
				//no contamos los sabados ni domingos
				if (dayMonth==Calendar.SATURDAY || dayMonth==Calendar.SUNDAY){
					continue;
				}
				
				if (actualMonth!=lastMonth){
	                            if (lastMonth!=-1){
	                                Quote qNew = new Quote();
	                                qNew.copy(actualMonthQ);
	                                months.add(qNew);
	                            }
	                            lastMonthQ.copy(actualMonthQ);
	                            actualMonthQ.copy(q);
	                            lastMonth=actualMonth;
	                        }
				
				//actualiamos actualDayQ
				actualMonthQ.setClose(q.getClose());
				if (q.getHigh()>actualMonthQ.getHigh())
					actualMonthQ.setHigh(q.getHigh());
				if (q.getLow()<actualMonthQ.getLow())
					actualMonthQ.setLow(q.getLow());			
			}
			Quote qNew = new Quote();
            qNew.copy(actualMonthQ);
            months.add(qNew);
            
			return months;
		}
	    
	    public  static ArrayList<Quote> createMonthlyData(ArrayList<Quote> data, ArrayList<Calendar> gmtAdjusted) {
			// TODO Auto-generated method stub
			ArrayList<Quote> months = new ArrayList<Quote>(); 
			int lastMonth=-1;
			Quote lastMonthQ=new Quote();
			Quote actualMonthQ= new Quote();
			//System.out.println("tamaï¿½o data en calculate: "+data.size());
			for (int i=0;i<data.size();i++){			
				Quote q = data.get(i);
				Calendar qCal = gmtAdjusted.get(i);
				int h = qCal.get(Calendar.HOUR_OF_DAY);
				int actualMonth = qCal.get(Calendar.MONTH);
				int dayMonth = qCal.get(Calendar.DAY_OF_MONTH);
				//no contamos los sabados ni domingos
				if (dayMonth==Calendar.SATURDAY || dayMonth==Calendar.SUNDAY){
					continue;
				}
				
				if (actualMonth!=lastMonth){
	                            if (lastMonth!=-1){
	                                Quote qNew = new Quote();
	                                qNew.copy(actualMonthQ);
	                                months.add(qNew);
	                            }
	                            lastMonthQ.copy(actualMonthQ);
	                            actualMonthQ.copy(q);
	                            lastMonth=actualMonth;
	                        }
				
				//actualiamos actualDayQ
				actualMonthQ.setClose(q.getClose());
				if (q.getHigh()>actualMonthQ.getHigh())
					actualMonthQ.setHigh(q.getHigh());
				if (q.getLow()<actualMonthQ.getLow())
					actualMonthQ.setLow(q.getLow());			
			}
			 Quote qNew = new Quote();
             qNew.copy(actualMonthQ);
             months.add(qNew);
             
			return months;
		}
	        
	    
	    public static ArrayList<Quote> convert(ArrayList<Quote> data,int factor){
            ArrayList<Quote> result = new ArrayList<Quote>();
            
            int j=0;
            Quote qNew = null;
            for (int i=0;i<data.size();i++){
                Quote q = data.get(i);
                //System.out.println("original: "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.Print(q));
                if (i%(factor)==0){//insertamos
                    if (qNew!=null){//añadimos una nueva
                        result.add(qNew);
                        //System.out.println("QUote added: "+qNew.toString());
                        qNew = null;
                    }
                    qNew = new Quote();
                    qNew.copy(q);
                }else{
                    if (q.getHigh()>qNew.getHigh())
                        qNew.setHigh(q.getHigh());
                    if (q.getLow()<qNew.getLow())
                        qNew.setLow(q.getLow());
                    qNew.setClose(q.getClose());
                } 
            }
            if ((data.size()-1)%factor!=0){//insertamos
                if (qNew!=null){//añadimos una nueva
                        result.add(qNew);
                        //System.out.println("QUote added: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.Print(qNew));
                        qNew = null;
                }
            }
            return result;
        }
	    
	    public static ArrayList<QuoteShort> convertQHour(ArrayList<QuoteShort> data,int factor){
            ArrayList<QuoteShort> result = new ArrayList<QuoteShort>();
            
            int j=-1;
            QuoteShort qNew = null;
            Calendar cal = Calendar.getInstance();
           
            for (int i=0;i<data.size();i++){
                QuoteShort q = data.get(i);
                QuoteShort.getCalendar(cal, q);
                int min = cal.get(Calendar.MINUTE);
                
                if (j==-1 && min==0){
                	j=0;
                }
                
                //System.out.println("original: "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.Print(q));
                if (j%(factor)==0){//insertamos
                    if (qNew!=null){//añadimos una nueva
                        result.add(qNew);
                        //System.out.println("QUote added: "+qNew.toString());
                        qNew = null;
                    }
                    qNew = new QuoteShort();
                    qNew.copy(q);
                }else{
                    if (q.getHigh()>qNew.getHigh())
                        qNew.setHigh(q.getHigh());
                    if (q.getLow()<qNew.getLow())
                        qNew.setLow(q.getLow());
                    qNew.setClose(q.getClose());
                } 
                if (j>=0){
                	j++;
                }
            }
            if ((data.size()-1)%factor!=0){//insertamos
                if (qNew!=null){//añadimos una nueva
                        result.add(qNew);
                        //System.out.println("QUote added: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.Print(qNew));
                        qNew = null;
                }
            }
            return result;
        }
	    
	    public static ArrayList<QuoteBidAsk> convertBidAsk(ArrayList<QuoteBidAsk> data,int factor){
            ArrayList<QuoteBidAsk> result = new ArrayList<QuoteBidAsk>();
            
            int j=0;
            QuoteBidAsk qNew = null;
            Calendar cal = Calendar.getInstance();
            for (int i=0;i<data.size();i++){
                QuoteBidAsk q = data.get(i);
                cal.setTimeInMillis(q.getCal().getTimeInMillis());
                //System.out.println("original: "+q.toString());
                if (i%(factor)==0){//insertamos
                    if (qNew!=null){//añadimos una nueva
                        result.add(qNew);
                        //System.out.println("QUote added: "+qNew.toString());
                        qNew = null;
                    }
                    qNew = new QuoteBidAsk();
                    qNew.copy(q);
                    qNew.setOpenBid(q.getOpenBid());
                    qNew.setOpenAsk(q.getOpenAsk());
                    qNew.setHighBid(q.getHighBid());
                    qNew.setHighAsk(q.getHighAsk());
                    qNew.setLowBid(q.getLowBid());
                    qNew.setLowAsk(q.getLowAsk());
                    qNew.setCloseBid(q.getCloseBid());
                    qNew.setCloseAsk(q.getCloseAsk());
        		}else{
                    if (q.getHighBid()>qNew.getHighBid()){
                        qNew.setHighBid(q.getHighBid());
                        qNew.setHighAsk(q.getHighAsk());
                    }
                    if (q.getLowBid()<qNew.getLowBid()){
                        qNew.setLowBid(q.getBid());
                        qNew.setLowAsk(q.getAsk());
                    }
                    qNew.setCloseBid(q.getCloseBid());
                    qNew.setCloseAsk(q.getCloseAsk());
                } 
            }
            if ((data.size()-1)%factor!=0){//insertamos
                if (qNew!=null){//añadimos una nueva
                        result.add(qNew);
                        //System.out.println("QUote added: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.Print(qNew));
                        qNew = null;
                }
            }
            return result;
        }
	    
	    public static void convertTo10secodsFormat(String fileSource,String fileDest){
	    
	    	ArrayList<QuoteShort> dataI 		= null;
			ArrayList<QuoteShort> dataS 		= null;
			
	    	dataI 		= DAO.retrieveDataShort5m(fileSource, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			DataCleaning.writeFileShort(data,fileDest);
	    }
	    
	    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String fileSource = "C:\\fxdata\\ScalpingTraining\\data\\GBPUSD_UTC_10 Secs_Bid_2015.10.01_2015.12.31.csv";
		String fileDest = "C:\\fxdata\\ScalpingTraining\\data\\GBPUSD_12_2015_10s_data.csv";
		
		ConvertLib.convertTo10secodsFormat(fileSource, fileDest);
		
		System.out.println("Finalizado");

	}

}
