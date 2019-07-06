package drosa.experimental.zznbrum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestTrends {
	
	public static ArrayList<TrendClass> getTrends(ArrayList<QuoteShort> data,int minSize,boolean reset){
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int lastDay = -1;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				
				lastDay = day;
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=-1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
				}
			}
		}
		
		return trends;
		
	}
	
	public static void doTest(ArrayList<Integer> trends,int minTrend){
		
		for (int i=0;i<trends.size();i++){
			int actualTrend = trends.get(i);
			
			
		}
	}
	
	private static int evaluatePositions(ArrayList<Integer> positions,int pos,int tipo){
	
		int res = 0;
		int comm = 10;//comision de 1 pip
		for (int i=0;i<positions.size();i++){
			int p = positions.get(i);
			
			if (tipo==1){
				res += pos-p-comm;
			}else if (tipo==-1){
				res += p-pos-comm;
			}
		}
		
		
		return res;
	
	}
	
	private static void testPositions(ArrayList<Integer> trends,int trendLen, 
			double minFactor,
			int maxLossFactor,
			int nTrends,
			int stop,
			int debug
			) {
		// TODO Auto-generated method stub
		
		int accProfit = 0;
		int count = 0;
		int positives = 0;
		int accProfit1 = 0;
		int accProfit2 = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=0;i<trends.size();i++){
			int len = trends.get(i);
			double factor = len*1.0/trendLen;
			
			if (factor>=minFactor){
				int begin = i;
				int end = i+nTrends;
				if (end>=trends.size()-1) end = trends.size()-1;
				if (debug==1)
					System.out.println("[**TEST SYSTEM**] 0");
				int pos = 0;
				int posLast = 0;
				ArrayList<Integer> shorts = new ArrayList<Integer>();
				ArrayList<Integer> longs= new ArrayList<Integer>();
				int totalProfit = 0;
				for (int t=begin;t<=end;t++){										
					int tsize = trends.get(t);
					posLast = pos;
					pos += tsize;
					
					
					
					if (tsize>=trendLen && shorts.size()==0){
						int posShort = posLast+trendLen;
						//shorts.add(posShort);
						longs.add(posShort);
						
						int shortProfit = evaluatePositions(shorts,posShort,-1);//evaluamos en posLast+trendLen
						int longProfit = evaluatePositions(longs,posShort,1);//evaluamos en posLast+trendLen
						totalProfit = shortProfit + longProfit;
						if (debug==1)
						System.out.println("[OPEN SHORT] "+(posShort)
								+" || "+posLast+" "+posShort+" "+pos
								+" ||| "+shortProfit+" "+longProfit+" || "+totalProfit 
								);
						posLast = posShort;
					}else if (tsize<=-trendLen && longs.size()==0){
						int posLong = posLast-trendLen;
						//longs.add(posLong);
						shorts.add(posLong);
						int shortProfit = evaluatePositions(shorts,posLong,-1);//evaluamos en posLast+trendLen
						int longProfit = evaluatePositions(longs,posLong,1);//evaluamos en posLast+trendLen
						totalProfit = shortProfit + longProfit;
						if (debug==1)
						System.out.println("[OPEN Long] "+(posLong)
								+" || "+posLast+" "+posLong+" "+pos
								+" ||| "+shortProfit+" "+longProfit+" || "+totalProfit 
								);
						posLast = posLong;
					}
					//System.out.println("[TEST SYSTEM] "+(pos+tsize));
					if (totalProfit<=-stop) break;
					if (factor>=maxLossFactor){//>=2.0 pierdo la longitud
						totalProfit = (int) (-(maxLossFactor-1.0)*trendLen);
						break;
					}
				}
				count++;
				if (totalProfit>=0){
					positives++;
				}
				
				
				if (totalProfit>=0){
					winPips += totalProfit;
				}else{
					lostPips += -totalProfit; 
					if (debug==2){
						System.out.println(totalProfit);
					}
				}
								
				accProfit += totalProfit;
				
				if (factor>=minFactor && factor<2.0){
					accProfit1 += totalProfit;
				}else if (factor>=2.0){
					accProfit2 += totalProfit;
				}
			}										
		}
		System.out.println(count+" "+positives
				+" || "+PrintUtils.Print2dec(positives*100.0/count, false)
				+" || "+PrintUtils.Print2dec(accProfit, false)
				+" || "+PrintUtils.Print2dec(accProfit1, false)
				+" || "+PrintUtils.Print2dec(accProfit2, false)
				+" || "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)
				+" || "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false)
				);
	}
	
	public static void testTradingLegs(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int thr,
			int maxBars,
			int maxTrades,
			int aTarget,
			int maxPipsWins,
			int maxPipsLosses,
			int mode,
			int maDays,
			int debug
			){
	
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		int basketEntry = 0;
		int pipsProfit = 0;
		int pipsLost = 0;
		int wins = 0;
		int losses = 0;
		int baskets = 0;
		int totalTrades = 0;
		int comm = 00;
		
		ArrayList<Integer> days = new ArrayList<Integer>();
		ArrayList<Integer> vol = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		int  ma = -1;
		int volAvr = -1;
		int std = -1;
		int target = maxPipsWins;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int mins = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			//days.add(q.getOpen5());
			vol.add(q1.getHigh5()-q1.getLow5());
			volAvr = (int) MathUtils.average(vol, vol.size()-12, vol.size()-1);
			
			if (day!=lastDay){
				if (max!=-1){
					int r = max-min;
				}
				days.add(q.getOpen5());
				if (maDays>=1){
					ma = (int) MathUtils.average(days, days.size()-maDays, days.size()-1);
					std = (int) Math.sqrt(MathUtils.variance(days, days.size()-maDays, days.size()-1));
				}
				lastDay = day;
			}
			
			
			if (h>=h1 && h<=h2
					&& positions.size()<maxTrades
					//&& volAvr>=100
					){
				int maxMin = maxMins.get(i-1);				
				if (maxMin>=thr
						//&& q.getOpen5()<=ma
						//&& sizec<=20
						//&& q.getOpen5()<=ma
						//&& q1.getOpen5()<=q1.getClose5()-140
						){
					PositionCore newPos = new PositionCore();
					newPos.setEntry(q.getOpen5());
					newPos.setPositionStatus(PositionStatus.OPEN);
					newPos.setPositionType(PositionType.SHORT);
					if (mode==1)
						newPos.setPositionType(PositionType.LONG);
					
					positions.add(newPos);
					if (basketEntry==-1){
						basketEntry = i;
						target = maxPipsWins;
					}
				}else if (maxMin<=-thr
						//&& q.getOpen5()<=ma
						//&& sizec<=20
						//&& q.getOpen5()<=ma
						//&& q1.getOpen5()>=q1.getClose5()+140
						){
					PositionCore newPos = new PositionCore();
					newPos.setEntry(q.getOpen5());
					newPos.setPositionStatus(PositionStatus.OPEN);
					newPos.setPositionType(PositionType.LONG);
					if (mode==1)
						newPos.setPositionType(PositionType.SHORT);
					positions.add(newPos);
					if (basketEntry==-1){
						basketEntry = i;
						target = maxPipsWins;
					}
				}
			}
			
			//evaluacion posiciones ( en conjunto)
			int actualProfit = 0;
			int maxProfit = 0;
			int numTrades = 0;
			int j = 0;		
			boolean forceClosed = false;
			int sizec = q.getHigh5()-q.getLow5();
			while (j<positions.size()){
				PositionCore pos = positions.get(j);
				if (pos.getPositionStatus()==PositionStatus.OPEN){
					int profit = 0;
					int maxProfitT = 0;
					if (pos.getPositionType()==PositionType.LONG){
						profit = q.getClose5()-pos.getEntry();
						maxProfitT = q.getHigh5()-pos.getEntry();					
					}else if (pos.getPositionType()==PositionType.SHORT){
						profit = pos.getEntry()-q.getClose5();
						maxProfitT = pos.getEntry()-q.getLow5();												
					}
					actualProfit += profit-comm;
					maxProfit += maxProfitT-comm;
					//maxProfit = actualProfit;
				}
				j++;
			}
			//
			
			if (positions.size()>0){
				//CIERRE POR PERDIDAS
				if (positions.size()>0 && actualProfit<=-maxPipsLosses){
					totalTrades += positions.size();
					pipsLost += -actualProfit;
					
					if (debug==1){
						System.out.println("[BASKET LOSS] "+positions.size()+" "+actualProfit);
					}
					totalTrades+=positions.size();
					positions.clear();
					basketEntry = -1;
					losses++;
					baskets++;	
					
					//System.out.println(totalTrades+" "+baskets);
				}
				//cierre por ganancias
				if (positions.size()>0 && actualProfit>=aTarget){//cerramos si es positivo
					pipsProfit += actualProfit;
					wins++;
					if (debug==1){
						System.out.println("[BASKET WIN TIME] "+positions.size()+" "+actualProfit+" "+pipsProfit);
					}
					totalTrades+=positions.size();
					positions.clear();
					basketEntry = -1;
					baskets++;
				}
				//SE PASO EL TIEMPO
				if (positions.size()>0){
					if (i>=basketEntry+maxBars){					
						if (actualProfit>=0){//cerramos si es positivo
							pipsProfit += actualProfit;
							wins++;
							if (debug==1){
								System.out.println("[BASKET WIN TIME] "+positions.size()+" "+actualProfit+" "+pipsProfit);
							}
							totalTrades+=positions.size();
							positions.clear();
							basketEntry = -1;
							baskets++;
						}else{
							totalTrades += positions.size();
							pipsLost += -actualProfit;
							
							if (debug==1){
								System.out.println("[BASKET LOSS] "+positions.size()+" "+actualProfit);
							}
							totalTrades+=positions.size();
							positions.clear();
							basketEntry = -1;
							losses++;
							baskets++;	
						}
					}
				}	
			}//pos
			
			if (max==-1 || q.getHigh5()>=max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<=min) min = q.getLow5();
		}//for
		
		
		System.out.println(
				h1+" "+h2+" "+thr+" "+maxBars+" "+maxTrades+" "+maxPipsLosses
				+" || "+totalTrades+" "+baskets+" "+wins
				+" || "+PrintUtils.Print2dec(wins*100.0/baskets, false)
				+" || "+PrintUtils.Print2dec((pipsProfit-pipsLost)*0.1/totalTrades, false)
				+" "+PrintUtils.Print2dec((pipsProfit-pipsLost)*0.1/baskets, false)
				+" || "+PrintUtils.Print2dec(pipsProfit*1.0/pipsLost, false)
				);
	}
	
	private static void studyTrendsAvg(ArrayList<Integer> trends, int n,int debug) {
		// TODO Auto-generated method stub
		ArrayList<Double> values = new ArrayList<Double>();
		for (int i=n-1;i<trends.size();i++){
			double avg = MathUtils.average(trends, i-n, i);
			values.add(avg);
			
			if (debug==1){
				System.out.println(PrintUtils.Print2dec(avg, false));
			}
		}
		
		double media = MathUtils.averageD(values, 0, values.size()-1);
		double dt = Math.sqrt(MathUtils.varianceD(values));
		
		System.out.println(
				n
				+" || "+values.size()+" "+PrintUtils.Print2dec(media*0.1, false)+" "+PrintUtils.Print2dec(dt*0.1, false)
				);
		
	}
	
	private static void studyTrendsAvg2(ArrayList<Integer> trends,int size, int n,int minDiff,int debug) {
		// TODO Auto-generated method stub
		ArrayList<Double> values = new ArrayList<Double>();
		int target = 2*size*n;
		
		int totalc = 0;
		int diffs = 0;
		for (int i=n;i<trends.size();i++){
			int acc = 0;
			for (int j=i-n;j<=i-1;j++){
				acc += trends.get(j);
			}
			int diff = target-acc;
			
			if (diff>=minDiff){
				diffs += trends.get(i); 
				totalc++;
			}
			
			if (debug==1)
			System.out.println(
					target
					+" "+acc
					+" "+PrintUtils.Print2dec(diff*0.1,false)
					+" "+PrintUtils.Print2dec(trends.get(i)*0.1,false)
					);			
		}
		
		System.out.println(
				minDiff
				+" ||  "+totalc
				+" "+PrintUtils.Print2dec(diffs*0.1/totalc,false)
				);	

		
	}
	
	private static void studyTrendsAvg3(String header,ArrayList<TrendClass> trends,int minDiff,int debug) {
		// TODO Auto-generated method stub
		ArrayList<Double> values = new ArrayList<Double>();
		
		
		int totalc = 0;
		int acc = 0;
		int diffDays = 0;
		int lastDay = -1;
		for (int i=0;i<trends.size();i++){
			TrendClass t = trends.get(i);
			int day = t.getCal().get(Calendar.DAY_OF_YEAR);
			if (t.getSize()>=minDiff){
				acc += t.getSize();
				totalc++;
				if (day!=lastDay){
					diffDays++;
					lastDay = day;
				}
				if (debug==1)
				System.out.println(DateUtils.datePrint(t.getCal())+" || "+t.getSize());
			}
		}
		
		System.out.println(
				header
				+" || "+trends.size()+" "+PrintUtils.Print2dec(trends.size()/14,false)
				+" ||  "+totalc
				+" "+PrintUtils.Print2dec(totalc*100.0/trends.size(),false)+"%"
				+" "+PrintUtils.Print2dec(acc*0.1/totalc,false)
				+" || "+diffDays
				);	

		
	}
	
	
	public static void tradeTrends(String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int minSize,double initialBalance,
			double riskPerTrade,
			double maxF1,double maxF2,
			int debug,
			double factordebug,
			ArrayList<TrendClass> trends
			){
	
		
		int countTotalLegs = 0;
		int countFilterLegs = 0;
		double balance = initialBalance;
		double avgPrice = 0;
		int tradesCount = 0;
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		
		double accFactor = 0;
		
		int basketMode = 0;
		double basketAvgPrice = 0;
		int basketEntry = 0;
		int basketCount = 0;
		double basketPipValue = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		double maxFactor = 0.0;
		int lastDay = -1;
		int dayTrade = 0;
		int actualDayOrder = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){				
				dayTrade = 0;
				lastDay=day;
				//System.out.println("["+DateUtils.datePrint(cal)+"]");
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
					//nueva trend, iniciamos basket contrario short
					basketMode	= -1;
					basketAvgPrice = q.getClose5();
					basketEntry = q.getClose5();
					basketCount = 1;
					
					if (debug==2){
						System.out.println("[OPEN SHORT] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
					//nueva trend, iniciamos basket contrario long
					basketMode = 1;
					basketAvgPrice = q.getClose5();
					basketEntry = q.getClose5();
					basketCount = 1;
					
					if (debug==2){
						System.out.println("[OPEN LONG] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					countTotalLegs++;
					if (maxFactor>=maxF1 && maxFactor<=maxF2){
						countFilterLegs++;
						accFactor +=maxFactor;
						
						QuoteShort.getCalendar(cal1, data.get(index1));
						QuoteShort.getCalendar(cal2, data.get(index2));
						
						TrendClass tc = new TrendClass();
						tc.setMillisIndex1(cal1.getTimeInMillis());
						tc.setMillisIndex2(cal2.getTimeInMillis());
						tc.setSize(size);
						tc.setFactor(maxFactor);
						tc.setMode(mode);
						trends.add(tc);
						
						int h = cal1.get(Calendar.HOUR_OF_DAY);
						
						if (debug==4
								&& maxFactor>=factordebug
								){
							System.out.println(
									"[L] "
									+" "+size
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									);
						}
						
						if (debug==5
								&& actualDayOrder==0
								&& maxFactor>=factordebug
								){
							System.out.println(
									"[L] "
									+" "+size
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									+" || "+dayTrade
									);
						}						
					}
					
										
					//nueva trend short
					QuoteShort.getCalendar(cal1, data.get(index1));
					QuoteShort.getCalendar(cal2, data.get(index2));
					actualDayOrder = 1;
					if (cal2.get(Calendar.DAY_OF_YEAR)!=cal1.get(Calendar.DAY_OF_YEAR)){
						actualDayOrder = 0;
					}
					
					mode=-1;
					index1 = index2;
					index2 = i;
					
					dayTrade++;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					double factor = (data.get(index1).getHigh5()-q.getLow5())*1.0/minSize;
					if (debug==3
							&& maxFactor>=11.0
							){						
						System.out.println("[NEW LEG SHORT] "+q.toString()
						+" || "+PrintUtils.Print2dec(factor, false)
						+" || "+data.get(index1).getHigh5()+" "+q.getLow5()
						+" ||| "+PrintUtils.Print2dec(maxFactor, false)
						);
					}
					maxFactor = factor;
					
					if (basketMode==-1){//cerramos, se ha puesto por fin en nuestra direccion
						//cerramos basket
						double profitPips = (basketAvgPrice-q.getClose5())*basketCount;//profit en pips
						double profitPips$ = profitPips*basketPipValue;
						
						if (debug==2){
							System.out.println("[CLOSED SHORT] "+q.toString()
							+" || "+basketAvgPrice+" "+basketCount+" "+PrintUtils.Print2(profitPips, false)
							);
						}
					}
					//nueva trend, iniciamos basket en el otro sentido
					basketMode=1;
					basketAvgPrice = q.getClose5();
					basketEntry = q.getClose5();
					basketCount = 1;
					
					if (debug==2){
						System.out.println("[OPEN LONG] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
					
					//vemos si tenemos que aumentar
					if (basketMode==-1){
						int diff = (int) (q.getClose5()-basketAvgPrice);
						
						//diff>=minSize, aumentamos el numero de trades,ajustando el size para cuando se vuelva
						if (diff>=minSize*10){
							//
						}
					}
					
					
					double factor = (q.getHigh5()-data.get(index1).getLow5())*1.0/minSize;
					if (factor>=maxFactor) maxFactor = factor;
					if (debug==1){						
						if (factor>=1000.0){
							System.out.println("[LEG LONG] "+q.toString()
							+" || "+PrintUtils.Print2dec(factor, false)+" || "+q.getHigh5()+" "+data.get(index1).getLow5()
									);
						}
					}
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					countTotalLegs++;
					if (maxFactor>=maxF1 && maxFactor<=maxF2){
						countFilterLegs++;
						accFactor +=maxFactor;
						
						QuoteShort.getCalendar(cal1, data.get(index1));
						QuoteShort.getCalendar(cal2, data.get(index2));
												
						TrendClass tc = new TrendClass();
						tc.setMillisIndex1(cal1.getTimeInMillis());
						tc.setMillisIndex2(cal2.getTimeInMillis());
						tc.setSize(size);
						tc.setFactor(maxFactor);
						tc.setMode(mode);
						trends.add(tc);
						
						int h = cal1.get(Calendar.HOUR_OF_DAY);
												
						if (debug==4
								&& maxFactor>=factordebug
								){
							QuoteShort.getCalendar(cal1, data.get(index1));
							QuoteShort.getCalendar(cal2, data.get(index2));
							System.out.println(
									"[S] "
									+" "+PrintUtils.Print2Int(size, 4)
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									);
						}
						
						if (debug==5
								&& actualDayOrder==0
								&& maxFactor>=factordebug
								){
							QuoteShort.getCalendar(cal1, data.get(index1));
							QuoteShort.getCalendar(cal2, data.get(index2));
							System.out.println(
									"[S] "
									+" "+PrintUtils.Print2Int(size, 4)
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									+" || "+dayTrade
									);
						}
					}
					
					//if (h<=9)
					//nueva trend long
					QuoteShort.getCalendar(cal1, data.get(index1));
					QuoteShort.getCalendar(cal2, data.get(index2));
					actualDayOrder = 1;
					if (cal2.get(Calendar.DAY_OF_YEAR)!=cal1.get(Calendar.DAY_OF_YEAR)){
						actualDayOrder = 0;
					}
					
					mode=1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					double factor = (q.getHigh5()-data.get(index1).getLow5())*1.0/minSize;
					if (debug==3
							&& maxFactor>=10.0
							){
						
						System.out.println("[NEW LEG LONG] "+q.toString()
						+" || "+PrintUtils.Print2dec(factor, false)
						+" || "+q.getHigh5()+" "+data.get(index1).getLow5()
						+" ||| "+PrintUtils.Print2dec(maxFactor, false)
						);
					}
					maxFactor = factor;
					
					//nueva trend, iniciamos basket					
					if (basketMode==1){//cerramos, se ha puesto por fin en nuestra direccion
						//cerramos basket
						double profitPips = (q.getClose5()-basketAvgPrice)*basketCount;//profit en pips
						double profitPips$ = profitPips*basketPipValue;
						
						if (debug==2){
							System.out.println("[CLOSED LONG] "+q.toString()
							+" || "+basketAvgPrice+" "+basketCount+" "+PrintUtils.Print2(profitPips, false)
							);
						}
					}
					
					//nueva trend, iniciamos basket en el otro sentido
					basketMode		= -1;
					basketAvgPrice 	= q.getClose5();
					basketEntry 	= q.getClose5();
					basketCount 	= 1;
					
					if (debug==2){
						System.out.println("[OPEN SHORT] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
					double factor = (data.get(index1).getHigh5()-q.getLow5())*1.0/minSize;
					if (factor>=maxFactor) maxFactor = factor;
					if (debug==1){
						
						if (factor>=1000.0){
							System.out.println("[LEG SHORT] "+q.toString()
							+" || "+PrintUtils.Print2dec(factor, false)
							+" || "+data.get(index1).getHigh5()+" "+q.getLow5()
							);
						}
					}
				}
			}
			
			// se entra en q.getClose()
			
		}
		
		double avgFactor = accFactor*1.0/countFilterLegs;
		System.out.println(
				minSize
				+" "+PrintUtils.Print2dec(maxF1, false)+" "+PrintUtils.Print2dec(maxF2, false)
				+" || "+countTotalLegs+" "+countFilterLegs
				+" "+PrintUtils.Print2dec(avgFactor, false)
				+" "+PrintUtils.Print2dec(countFilterLegs*100.0/countTotalLegs, false)
				
				);
		
	}
	
	public static void tradeTrendsClose(String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int minSize,double initialBalance,
			double riskPerTrade,
			double maxF1,double maxF2,
			int debug,
			double factordebug,
			ArrayList<TrendClass> trends
			){
	
		
		int countTotalLegs = 0;
		int countFilterLegs = 0;
		double balance = initialBalance;
		double avgPrice = 0;
		int tradesCount = 0;
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		
		double accFactor = 0;
		
		int basketMode = 0;
		double basketAvgPrice = 0;
		int basketEntry = 0;
		int basketCount = 0;
		double basketPipValue = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		double maxFactor = 0.0;
		int lastDay = -1;
		int dayTrade = 0;
		int actualDayOrder = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){				
				dayTrade = 0;
				lastDay=day;
				//System.out.println("["+DateUtils.datePrint(cal)+"]");
			}
			
			int actualSizeH1 = q.getClose5()-data.get(index1).getClose5();
			int actualSizeL1 = data.get(index1).getClose5()-q.getClose5();
			int actualSizeH2 = q.getClose5()-data.get(index2).getClose5();
			int actualSizeL2 = data.get(index2).getClose5()-q.getClose5();
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
					//nueva trend, iniciamos basket contrario short
					basketMode	= -1;
					basketAvgPrice = q.getClose5();
					basketEntry = q.getClose5();
					basketCount = 1;
					
					if (debug==2){
						System.out.println("[OPEN SHORT] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
					//nueva trend, iniciamos basket contrario long
					basketMode = 1;
					basketAvgPrice = q.getClose5();
					basketEntry = q.getClose5();
					basketCount = 1;
					
					if (debug==2){
						System.out.println("[OPEN LONG] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getClose5()-data.get(index1).getClose5();
					countTotalLegs++;
					if (maxFactor>=maxF1 && maxFactor<=maxF2){
						countFilterLegs++;
						accFactor +=maxFactor;
						
						QuoteShort.getCalendar(cal1, data.get(index1));
						QuoteShort.getCalendar(cal2, data.get(index2));
						
						TrendClass tc = new TrendClass();
						tc.setMillisIndex1(cal1.getTimeInMillis());
						tc.setMillisIndex2(cal2.getTimeInMillis());
						tc.setSize(size);
						tc.setFactor(maxFactor);
						tc.setMode(mode);
						trends.add(tc);
						
						int h = cal1.get(Calendar.HOUR_OF_DAY);
						
						if (debug==4
								&& maxFactor>=factordebug
								){
							System.out.println(
									"[L] "
									+" "+size
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									);
						}
						
						if (debug==5
								&& actualDayOrder==0
								&& maxFactor>=factordebug
								){
							System.out.println(
									"[L] "
									+" "+size
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									+" || "+dayTrade
									);
						}						
					}
					
										
					//nueva trend short
					QuoteShort.getCalendar(cal1, data.get(index1));
					QuoteShort.getCalendar(cal2, data.get(index2));
					actualDayOrder = 1;
					if (cal2.get(Calendar.DAY_OF_YEAR)!=cal1.get(Calendar.DAY_OF_YEAR)){
						actualDayOrder = 0;
					}
					
					mode=-1;
					index1 = index2;
					index2 = i;
					
					dayTrade++;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					double factor = (data.get(index1).getClose5()-q.getClose5())*1.0/minSize;
					if (debug==3
							&& maxFactor>=11.0
							){						
						System.out.println("[NEW LEG SHORT] "+q.toString()
						+" || "+PrintUtils.Print2dec(factor, false)
						+" || "+data.get(index1).getClose5()+" "+q.getClose5()
						+" ||| "+PrintUtils.Print2dec(maxFactor, false)
						);
					}
					maxFactor = factor;
					
					if (basketMode==-1){//cerramos, se ha puesto por fin en nuestra direccion
						//cerramos basket
						double profitPips = (basketAvgPrice-q.getClose5())*basketCount;//profit en pips
						double profitPips$ = profitPips*basketPipValue;
						
						if (debug==2){
							System.out.println("[CLOSED SHORT] "+q.toString()
							+" || "+basketAvgPrice+" "+basketCount+" "+PrintUtils.Print2(profitPips, false)
							);
						}
					}
					//nueva trend, iniciamos basket en el otro sentido
					basketMode=1;
					basketAvgPrice = q.getClose5();
					basketEntry = q.getClose5();
					basketCount = 1;
					
					if (debug==2){
						System.out.println("[OPEN LONG] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}else if (q.getClose5()>=data.get(index2).getClose5()){
					index2 = i;
					
					//vemos si tenemos que aumentar
					if (basketMode==-1){
						int diff = (int) (q.getClose5()-basketAvgPrice);
						
						//diff>=minSize, aumentamos el numero de trades,ajustando el size para cuando se vuelva
						if (diff>=minSize*10){
							//
						}
					}
					
					
					double factor = (q.getClose5()-data.get(index1).getClose5())*1.0/minSize;
					if (factor>=maxFactor) maxFactor = factor;
					if (debug==1){						
						if (factor>=1000.0){
							System.out.println("[LEG LONG] "+q.toString()
							+" || "+PrintUtils.Print2dec(factor, false)+" || "+q.getClose5()+" "+data.get(index1).getClose5()
									);
						}
					}
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getClose5()-data.get(index2).getClose5();
					countTotalLegs++;
					if (maxFactor>=maxF1 && maxFactor<=maxF2){
						countFilterLegs++;
						accFactor +=maxFactor;
						
						QuoteShort.getCalendar(cal1, data.get(index1));
						QuoteShort.getCalendar(cal2, data.get(index2));
												
						TrendClass tc = new TrendClass();
						tc.setMillisIndex1(cal1.getTimeInMillis());
						tc.setMillisIndex2(cal2.getTimeInMillis());
						tc.setSize(size);
						tc.setFactor(maxFactor);
						tc.setMode(mode);
						trends.add(tc);
						
						int h = cal1.get(Calendar.HOUR_OF_DAY);
												
						if (debug==4
								&& maxFactor>=factordebug
								){
							QuoteShort.getCalendar(cal1, data.get(index1));
							QuoteShort.getCalendar(cal2, data.get(index2));
							System.out.println(
									"[S] "
									+" "+PrintUtils.Print2Int(size, 4)
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									);
						}
						
						if (debug==5
								&& actualDayOrder==0
								&& maxFactor>=factordebug
								){
							QuoteShort.getCalendar(cal1, data.get(index1));
							QuoteShort.getCalendar(cal2, data.get(index2));
							System.out.println(
									"[S] "
									+" "+PrintUtils.Print2Int(size, 4)
									+" || "+PrintUtils.Print2dec(size*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									+" || "+dayTrade
									);
						}
					}
					
					//if (h<=9)
					//nueva trend long
					QuoteShort.getCalendar(cal1, data.get(index1));
					QuoteShort.getCalendar(cal2, data.get(index2));
					actualDayOrder = 1;
					if (cal2.get(Calendar.DAY_OF_YEAR)!=cal1.get(Calendar.DAY_OF_YEAR)){
						actualDayOrder = 0;
					}
					
					mode=1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					double factor = (q.getClose5()-data.get(index1).getClose5())*1.0/minSize;
					if (debug==3
							&& maxFactor>=10.0
							){
						
						System.out.println("[NEW LEG LONG] "+q.toString()
						+" || "+PrintUtils.Print2dec(factor, false)
						+" || "+q.getClose5()+" "+data.get(index1).getClose()
						+" ||| "+PrintUtils.Print2dec(maxFactor, false)
						);
					}
					maxFactor = factor;
					
					//nueva trend, iniciamos basket					
					if (basketMode==1){//cerramos, se ha puesto por fin en nuestra direccion
						//cerramos basket
						double profitPips = (q.getClose5()-basketAvgPrice)*basketCount;//profit en pips
						double profitPips$ = profitPips*basketPipValue;
						
						if (debug==2){
							System.out.println("[CLOSED LONG] "+q.toString()
							+" || "+basketAvgPrice+" "+basketCount+" "+PrintUtils.Print2(profitPips, false)
							);
						}
					}
					
					//nueva trend, iniciamos basket en el otro sentido
					basketMode		= -1;
					basketAvgPrice 	= q.getClose5();
					basketEntry 	= q.getClose5();
					basketCount 	= 1;
					
					if (debug==2){
						System.out.println("[OPEN SHORT] "+q.toString()
						+" || "+basketAvgPrice
						);
					}
				}else if (q.getClose5()<=data.get(index2).getClose5()){
					index2 = i;
					double factor = (data.get(index1).getClose5()-q.getClose5())*1.0/minSize;
					if (factor>=maxFactor) maxFactor = factor;
					if (debug==1){
						
						if (factor>=1000.0){
							System.out.println("[LEG SHORT] "+q.toString()
							+" || "+PrintUtils.Print2dec(factor, false)
							+" || "+data.get(index1).getClose5()+" "+q.getClose5()
							);
						}
					}
				}
			}
			
			// se entra en q.getClose()
			
		}
		
		double avgFactor = accFactor*1.0/countFilterLegs;
		System.out.println(
				minSize
				+" "+PrintUtils.Print2dec(maxF1, false)+" "+PrintUtils.Print2dec(maxF2, false)
				+" || "+countTotalLegs+" "+countFilterLegs
				+" "+PrintUtils.Print2dec(avgFactor, false)
				+" "+PrintUtils.Print2dec(countFilterLegs*100.0/countTotalLegs, false)
				
				);
		
	}
	
	private static void doStudy(ArrayList<TrendClass> trends,double factorSize) {
	
		ArrayList<Integer> hoursIndex1 = new ArrayList<Integer>();
		ArrayList<Integer> monsters = new ArrayList<Integer>();
		for (int i = 0;i<=23;i++) hoursIndex1.add(0);
		for (int i = 0;i<=23;i++) monsters.add(0);
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<trends.size();i++){
			TrendClass t = trends.get(i);
			cal.setTimeInMillis(t.getMillisIndex1());
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int count = hoursIndex1.get(h);
			hoursIndex1.set(h, count+1);	
			
			if (t.getFactor()>=factorSize){
				count = monsters.get(h);
				monsters.set(h, count+1);
			}
		}		
		
		for (int i=0;i<=23;i++){
			System.out.println(i
			+" "+hoursIndex1.get(i)
			+" "+monsters.get(i)
			+" || "+PrintUtils.Print2dec(monsters.get(i)*100.0/hoursIndex1.get(i),false)
			);
		}
	}
	
	private static void doStudyInterval(
			String header,
			ArrayList<TrendClass> trends,int h1,int h2,
			double f1,double f2) {
		
		ArrayList<Integer> hoursIndex1 = new ArrayList<Integer>();
		ArrayList<Integer> monsters = new ArrayList<Integer>();
		for (int i = 0;i<=23;i++) hoursIndex1.add(0);
		for (int i = 0;i<=23;i++) monsters.add(0);
		
		int totals = 0;
		int n=0;
		double avgf=0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<trends.size();i++){
			TrendClass t = trends.get(i);
			cal.setTimeInMillis(t.getMillisIndex1());
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			if (h>=h1 && h<=h2
					//&& dayWeek == Calendar.MONDAY+4
					){
				int count = hoursIndex1.get(h);
				hoursIndex1.set(h, count+1);	
				
				if (t.getFactor()>=f1
						&& t.getFactor()<=f2
						){
					count = monsters.get(h);
					monsters.set(h, count+1);
					avgf += t.getFactor(); 
					totals++;
				}
				n++;
			}
		}		
		
		double per = totals*100.0/n;
		System.out.println(
				header
				+" "+PrintUtils.Print2dec(f1, false)
				+" "+PrintUtils.Print2dec(f2, false)
				+" || "+trends.size()
				+" "+n
				+" "+totals
				+" "+PrintUtils.Print2dec(avgf*1.0/totals, false)
				+" "+PrintUtils.Print2dec(per, false)
				
				);
		
		/*for (int i=0;i<=23;i++){
			System.out.println(i
			+" "+hoursIndex1.get(i)
			+" "+monsters.get(i)
			+" || "+PrintUtils.Print2dec(monsters.get(i)*100.0/hoursIndex1.get(i),false)
			);
		}*/
	}
	
	private static void doStudyFollowers(
			String header,
			ArrayList<TrendClass> trends,int h1,int h2,
			double f1,double f2,double f3,double f4
			) {
		
		ArrayList<Integer> hoursIndex1 = new ArrayList<Integer>();
		ArrayList<Integer> monsters = new ArrayList<Integer>();
		for (int i = 0;i<=23;i++) hoursIndex1.add(0);
		for (int i = 0;i<=23;i++) monsters.add(0);
		
		int totals = 0;
		int n=0;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<trends.size();i++){
			TrendClass t1 = trends.get(i-1);
			TrendClass t = trends.get(i);
			
			cal.setTimeInMillis(t1.getMillisIndex1());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h>=h1 && h<=h2){
				if (t1.getFactor()>=f1
						&& t1.getFactor()<=f2
						){
					if (t.getFactor()>=f3
							&& t.getFactor()<=f4
							){
						totals++;
					}
					n++;
				}
			}
		}		
		
		double per = totals*100.0/n;
		System.out.println(
				header
				+" "+PrintUtils.Print2dec(f1, false)
				+" "+PrintUtils.Print2dec(f2, false)
				+" "+PrintUtils.Print2dec(f3, false)
				+" "+PrintUtils.Print2dec(f4, false)
				+" || "+trends.size()
				+" "+n+" "+totals
				+" "+PrintUtils.Print2dec(per, false)
				);
		
	}
		
	private static void doStudyTrendDayOrders(
				String header,
				ArrayList<TrendClass> trends,
				int minSize,
				double f,int order,
				int debug
				) {
			
			ArrayList<Integer> hoursIndex1 = new ArrayList<Integer>();
			ArrayList<Integer> monsters = new ArrayList<Integer>();
			for (int i = 0;i<=23;i++) hoursIndex1.add(0);
			for (int i = 0;i<=23;i++) monsters.add(0);
			
			int totals = 0;
			int n=0;
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			int currentDayOrder = 0;
			int lastDay = -1;
			int total = 0;
			int totalOrders = 0;
			double avgF = 0;
			for (int i=0;i<trends.size();i++){
				//TrendClass t1 = trends.get(i-1);
				TrendClass t = trends.get(i);
				
				cal.setTimeInMillis(t.getMillisIndex1());
				cal1.setTimeInMillis(t.getMillisIndex1());
				cal2.setTimeInMillis(t.getMillisIndex2());
				
				int day = cal.get(Calendar.DAY_OF_YEAR);
				if (day!=lastDay){					
					currentDayOrder = 0;
					lastDay = day;
				}
				
				if (currentDayOrder==order){
					totalOrders++;
				}
				
				if (currentDayOrder==order
						&& t.getFactor()>=f
						){
					if (debug==1){
						if (t.mode==1){
							System.out.println(
									"[L] "
									+" "+PrintUtils.Print2Int(t.getSize(), 4)
									+" || "+PrintUtils.Print2dec(t.getSize()*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									+" || "+order
									);						
						}else  if (t.mode==-1){
							System.out.println(
									"[S] "
									+" "+PrintUtils.Print2Int(t.getSize(), 4)
									+" || "+PrintUtils.Print2dec(t.getSize()*1.0/minSize, false)
									+" || "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2)
									+" || "+order
									);
						}
					}
					total++;
					avgF += t.getFactor();
				}
					
				
				currentDayOrder++;
			}	
			
			avgF = avgF*1.0/total;
			
			System.out.println(
					order					
					+" || "+total
					+" || "+PrintUtils.Print2dec(total*100.0/totalOrders, false)
					+" || "+PrintUtils.Print2dec(avgF, false)
			);
	}


	public static void main(String[] args) throws Exception {
		
		//String pathEUR ="C:\\fxdata\\EURUSD_UTC_5 Secs_Bid_2015.12.31_2017.09.18.csv";
		//String pathEUR ="C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2016.12.31_2017.09.15.csv";
		String pathEUR ="C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.12.31_2017.09.14.csv";
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEUR);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);	
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			int sizeTrend = 400;
			
			for (sizeTrend=200;sizeTrend<=200;sizeTrend+=10){
				for (double maxFactor1=1.0;maxFactor1<=1.0;maxFactor1+=1.0){					
					double maxFactor2 = maxFactor1+0.99;
					for (maxFactor2=15.0;maxFactor2<=15.0;maxFactor2+=0.99){
						trends.clear();
						//TestTrends.tradeTrends("",data,2010,2017,0,23, sizeTrend, 10000, 0.01,maxFactor1,maxFactor2,0,3.0,trends);
						TestTrends.tradeTrendsClose("",data,2010,2017,0,23, sizeTrend, 10000, 0.01,maxFactor1,maxFactor2,4,11.0,trends);
						ArrayList<Integer> prices = new ArrayList<Integer>();
						int price = 100000;
						int mode = 1;
						prices.add(price);
						for (int t=0;t<trends.size();t++){
							price += mode*trends.get(t).size;
							prices.add(price);
							//System.out.println(price+" || "+mode*trends.get(t).size);
							mode=-mode;
							
						}
						
						/*for (int order=0;order<=20;order++){
							TestTrends.doStudyTrendDayOrders("", trends, sizeTrend, 9.0, order,0);
						}*/
						//SimulateModel.doTrade(prices);
						//TestTrends.doStudy(trends,6.0);
						/*for (int h=0;h<=0;h++){				
							int h2 = h+23;
							String header = h+" ";
							for (double af1=1.0;af1<=20.0;af1+=1.0){
								double af2=af1+0.99;	
								TestTrends.doStudyInterval(header,trends,h,h2,af1,af2);
								//for (double af3=1.0;af3<=1.0;af3+=1.0){
									//double af4=af3+20.99;	
									//TestTrends.doStudyFollowers(header,trends,h,h2,af1,af2,af3,af4);
								//}
							}
						}*/
					}
				}				
			}	
			
		}
		
	}

	
	

	

}
