package drosa.apuestas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class Baseball {
	
	//sport = 0: nhl,1: nba
	public static ArrayList<Serie> readFromDisk(String folder,int y1,int y2,int type,String prefix){
		
		ArrayList<Serie> series1 = new ArrayList<Serie>();
		
		File dir = new File(folder);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
		    for (File child : directoryListing) {
		    	if (child.isDirectory()) continue;
		    	ArrayList<Serie> series = Serie.readFromDisk(child.getAbsolutePath());
				for (int i=0;i<series.size();i++) series1.add(series.get(i));	
				//System.out.println("a cargar: "+child.getAbsolutePath()+" "+series1.size());
		    }
		 } else {
				
									
		}
		
		return series1;
	}
		
	public static int isSerieWin(Serie s,int winRes){
		
		
		if (s.getResults().size()!=3) return 0;
		
		int total = 0;
		for (int i=0;i<s.getResults().size();i++){
			total += s.getResults().get(i); 
		}
		
		if (total>=1) return 1;
		else if (total==0)  return 0;
		else return -1;

	}
	
	public static void test1(){
		
		String folder = "c:\\baseball";
		ArrayList<Serie> series1 = Baseball.readFromDisk(folder, -1,-1,-1,"");
		System.out.println("data : "+series1.size());
		
		int total2 = 0;
		int wins2 = 0;
		int total1 = 0;
		int wins1 = 0;
		int total0 = 0;
		int losses = 0;
		for (int i=0;i<series1.size()-2;i++){
			Serie s = series1.get(i);
			Serie s1 = series1.get(i+1);
			Serie s2 = series1.get(i+2);
			if (s.getResults().size()!=3) continue;
		
			int win1 = isSerieWin(s,1);
			if (s.getResults().get(0)==1) total0++;
			if (win1==-1
					&& s.getResults().get(0)==1
					&& s.getTeam1().equalsIgnoreCase(s1.getTeam1())
					){
				losses++;
				if (s1.getResults().size()!=3) continue; 
				
				if (s1.getResults().get(0)==1){
					total1++;
					if (s1.getResults().get(1)==1 || s1.getResults().get(2)==1){
						wins1++;
						//System.out.println(s.toString()+" || "+s1.toString());
					}else{
						if (s1.getTeam1().equalsIgnoreCase(s2.getTeam1())
								&& s2.getResults().size()==3
								){
							
							if (s2.getResults().get(0)==1){
								total2++;
								if (s2.getResults().get(1)==1 || s2.getResults().get(2)==1){
									wins2++;
									//System.out.println(s.toString()+" || "+s1.toString());
								}
							}
							
						}
						
					}
				}
			}
		}
		
		System.out.println(
				total0+"  "+losses+" "+PrintUtils.Print2dec(losses*100.0/total0, false)
				+" || "+total1+" "+PrintUtils.Print2dec(wins1*100.0/total1, false)
				+" || "+total2+" "+PrintUtils.Print2dec(wins2*100.0/total2, false)
				);
	}
	
	public static void testInsight(int y1,int y2){
		String folder = "c:\\baseball\\insight\\";
		
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
		
		//comprobamos las series
		int totalGlobal0 = 0;
		int totalGlobal = 0;
		int totalGlobal1 = 0;
		int totalGlobal1losses = 0;
		int totalGlobal1win = 0;
		int totalGlobal_1 = 0;
		int totalGlobal2 = 0;
		int totalGlobal2win = 0;
		
		int totalOddsGlobal = 0;
		double avgOddsGlobal = 0;
		
		int total1Global = 0;
		int total1WinsGlobal = 0;
		int total1OddsGlobal = 0;
		double avgOdds1Global = 0;
		for (Entry<String, ArrayList<Serie>> entry : teamSeries.entrySet()) {
			String team = entry.getKey();
			ArrayList<Serie> series = entry.getValue();
			int total = 0;
			int totalOdds = 0;
			double avgOdds = 0;
			int total1 = 0;
			int total1Wins = 0;
			int totalOdds1 = 0;
			double avgOdds1 = 0;
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				Serie s1 = series.get(i+1);
				if (s0.getResults().size()!=3) continue;
				if (s1.getResults().size()!=3) continue;
				//if (s2.getResults().size()<3) continue;
				//if (s2.getResults().size()!=3) continue;
				//if (s3.getResults().size()!=3) continue;
				//if (s4.getResults().size()!=3) continue;
				
				
				if (s0.getResults().get(0)==1){
					totalGlobal0++;
					avgOdds1Global += s0.getHomeOdds().get(1);
					total1OddsGlobal++;
					if (s0.getHomeOdds().get(1)==-1){
						avgOdds1Global += s0.getHomeOdds().get(2);
						total1OddsGlobal++;
					}
					if (s0.getResults().get(1)==-1 && s0.getResults().get(2)==-1){
						totalGlobal++;
						if (s1.getResults().get(0)==1){
							totalGlobal1++;
							if (s1.getResults().get(1)==1 || s1.getResults().get(2)==1){
								totalGlobal1win++;							
							}else{
								totalGlobal1losses++;
								/*if (s2.getResults().get(0)==1){
									totalGlobal2++;
									if (s2.getResults().get(1)==1 || s2.getResults().get(2)==1){
										totalGlobal2win++;							
									}
								}*/
							}
							
							
							avgOddsGlobal += (s1.getHomeOdds().get(1));
							totalOddsGlobal++;
							if (s1.getResults().get(1)==-1){
								avgOddsGlobal += (s1.getHomeOdds().get(2));
								totalOddsGlobal++;
							}
						}else if (s1.getResults().get(0)==-1){
							totalGlobal_1++;
						} 
					}		
				}
			
			}
			
			
		}
		
		double winPer = 100.0-totalGlobal*100.0/totalGlobal0;
		double avgOdds = avgOdds1Global*1.0/total1OddsGlobal;
		double profit0 = winPer*(avgOdds-1);
		double loss0 = (100.0-winPer)*(-3);
		System.out.println(
				totalGlobal0+" "+totalGlobal
				+" "+PrintUtils.Print2dec(100.0-totalGlobal*100.0/totalGlobal0, false)
				+" "+PrintUtils.Print2dec(totalGlobal*100.0/totalGlobal0, false)
				+" "+PrintUtils.Print2dec(avgOdds1Global*1.0/total1OddsGlobal, false)
				+" "+PrintUtils.Print2dec(profit0, false)
				+" "+PrintUtils.Print2dec(loss0, false)
				+" "+PrintUtils.Print2dec(profit0+loss0, false)
				+" || "
						+PrintUtils.Print2dec(totalGlobal1*100.0/totalGlobal, false)
				+" "+PrintUtils.Print2dec(totalGlobal_1*100.0/totalGlobal, false)
				+" || "+" "+PrintUtils.Print2dec(totalGlobal1win*100.0/totalGlobal1, false)
				+" "+PrintUtils.Print2dec(avgOddsGlobal*1.0/totalOddsGlobal, false)
				+" || "+totalGlobal1losses+" "+totalGlobal2+" "+PrintUtils.Print2dec(totalGlobal2win*100.0/totalGlobal2, false)
				//totalGlobal+" "+totalOddsGlobal+" "+PrintUtils.Print2dec(avgOddsGlobal*1.0/totalOddsGlobal, false)
				/*total1Global
				+" "+PrintUtils.Print2dec(total1WinsGlobal*100.0/total1Global, false)
				+" "+total1OddsGlobal
				+" "+PrintUtils.Print2dec(avgOdds1Global*1.0/total1OddsGlobal, false)*/
				);
		
		
	}
	
	public static double testSystemBank(BetStats betStats,
			double bank,
			int y1,int y2,int begin,int end,int debug,
			double awinRate,
			double oddsThr1,double oddsThr2,
			boolean showSummary
			){
		String folder = "c:\\baseball\\insight\\";
		
		double balance = bank;
						
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		//System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
		
	
		
		double profitGlobal = 0;
		double lossGlobal = 0;
		double risk = 0;
		int total = 0;
		int win11 = 0;
		int win12 = 0;
		int win20 = 0;
		int win21 = 0;
		int win22 = 0;
	    int losses = 0;
	    int total20 = 0;
	    int total21 = 0;
	    int actualLossCountGlobal = 0;
		int actualLossWinsGlobal = 0;
	    double accOdds = 0;
	    int totalOdds = 0;
	    double actualLoss = 0.0;
	    double maxStake = 500;
		if (end>=teamSeries.size()-1) end=teamSeries.size()-1;
		for (int e=begin;e<=end;e++){
			//Entry<String, ArrayList<Serie>> entry = teamSeries.get(e);
			String team = (String) teamSeries.keySet().toArray()[e];
			ArrayList<Serie> series = teamSeries.get(team);	
			double profit = 0;
			double loss = 0;
			int actualLossCount = 0;
			int actualLossWins = 0;
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				//Serie s1 = series.get(i+1);
				//if (debug==1)
					//System.out.println("[sAll] "+s0.toString());
				if (s0.getResults().size()<3) continue;
				//if (s1.getResults().size()!=3) continue;
				
				//if (!s0.getTeam1().equals("New York Yankees")) continue;
				
				if (s0.getResults().get(0)!=1) continue;
				
				if (debug==1)
					System.out.println("[s0] "+s0.toString());
				
				double stake = 1;
				double minStake = 1;//1%
				if (stake>=maxStake) stake = maxStake;
				
				
				//odds1
				accOdds += s0.getHomeOdds().get(1);
				totalOdds++;
				
				boolean isActualLossCheck = false;
				
				actualLoss = loss>profit?(loss-profit):0;
				
				
				if (s0.getHomeOdds().get(1)<oddsThr1) continue;
				if (actualLoss>0){						
					minStake = 100;//10%
					actualLossCount++;
					isActualLossCheck = true;
					//if (debug==1){
						System.out.println("[MAXLOSS] recuperar  "+PrintUtils.Print2dec(actualLoss,false));
					//}
				}else{
					//if (s0.getHomeOdds().get(1)<1.8) continue;
				}
				
				
				if (s0.getResults().get(1)==1){
					win11++;
					
					actualLoss = loss>profit?(loss-profit):0;
					
					stake = actualLoss*1.0/(s0.getHomeOdds().get(1)-1)+1;
					if (stake<=minStake) stake =minStake;
					if (stake>=maxStake) stake = maxStake;
					//ganancia primera
					profit += s0.getHomeOdds().get(1)*stake-stake;
					actualLoss -= s0.getHomeOdds().get(1)*stake-stake;
					
					
					betStats.addBet(1, 0, 0);
					risk += stake; 
					
					balance += s0.getHomeOdds().get(1)*stake-stake;
					if (debug==1)
						System.out.println("[WIN 01] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)
						+" "+loss
						+" || "+PrintUtils.Print2dec(profit-loss, false)
						+" || "+PrintUtils.Print2dec(balance, false)
						);
					
					total++;
				}else{ 
					//tomamos la perdida
					actualLoss = loss>profit?(loss-profit):0;
					stake = actualLoss*1.0/(s0.getHomeOdds().get(1)-1)+1;
					if (stake<=minStake) stake =minStake;
					if (stake>=maxStake) stake = maxStake;
					loss += stake;
					
					actualLoss +=stake;
					
					balance += -stake;
					if (debug==1)
					System.out.println("[LOSS 01] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+PrintUtils.Print2dec(balance, false));
					betStats.addBet(1, 0, 0);
					risk += stake; 
															
					total++;
					
					accOdds += s0.getHomeOdds().get(2);
					totalOdds++;
					if (s0.getResults().get(2)==1){												
						//segunda apuesta
						actualLoss = loss>profit?(loss-profit):0;
						stake = actualLoss*1.0/(s0.getHomeOdds().get(2)-1)+1;
						if (stake<=minStake) stake =minStake;
						if (stake>=maxStake) stake = maxStake;
						win12++;
						
						//sumamos la ganancia
						profit += s0.getHomeOdds().get(2)*stake-stake;
								
						
						balance += s0.getHomeOdds().get(2)*stake-stake;
						if (debug==1)
						System.out.println("[WIN 02] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+PrintUtils.Print2dec(balance, false));
						betStats.addBet(1, 0, 0);
						risk += stake; 
						
						
						
						total++;
					}else{//s02	
						//tomamos la perdida
						stake = actualLoss*1.0/(s0.getHomeOdds().get(2)-1)+1;
						if (stake<=minStake) stake =minStake;
						if (stake>=maxStake) stake = maxStake;
						loss += stake;
						
						actualLoss += stake;
						
						balance += -stake;
						if (debug==1)
						System.out.println("[LOSS 02] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+PrintUtils.Print2dec(balance, false));
						betStats.addBet(1, 0, 0);
						risk += stake;
						
						
						
						total++;
						for (int j=i+1;j<series.size()-1;j++){
							Serie s1 = series.get(j);
							if (s1.getResults().size()<3) continue;
							if (s1.getResults().get(0)!=1) continue;//comentado en debug
							
							if (s1.getHomeOdds().get(1)<oddsThr2) continue;
							
							if (debug==1)
								System.out.println("[s1] "+s1.toString());
							
							total20++;
							//odds 3
							accOdds += s1.getHomeOdds().get(1);
							totalOdds++;
							if (s1.getResults().get(1)==1){
								//tercera apuesta	
								stake = actualLoss*1.0/(s1.getHomeOdds().get(1)-1)+1;
								if (stake<=minStake) stake =minStake;
								if (stake>=maxStake) stake = maxStake;
								win20++;
								total++;
								//sumamos la ganancia
								profit += s1.getHomeOdds().get(1)*stake-stake;
								
								
								balance += s1.getHomeOdds().get(1)*stake-stake;
								if (debug==1)
								System.out.println("[WIN 11] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+PrintUtils.Print2dec(balance, false));								
								betStats.addBet(1, 0, 0);
								risk += stake;
								
								
								
								total++;
							}else{
								total21++;
								//tomamos la perdida de la tercera
								stake = actualLoss*1.0/(s1.getHomeOdds().get(1)-1)+1;
								if (stake<=minStake) stake =minStake;
								if (stake>=maxStake) stake = maxStake;
								loss += stake;
								
								actualLoss += stake;
								
								balance += -stake;
								if (debug==1)
								System.out.println("[LOSS 11] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+PrintUtils.Print2dec(balance, false));
								betStats.addBet(1, 0, 0);
								risk += stake;
								
								
								
								total++;
								//odds 4
								accOdds += s1.getHomeOdds().get(2);
								totalOdds++;
								
								//if (s1.getHomeOdds().get(2)<1.3) continue;
								
								if (s1.getResults().get(2)==1){																				
									//cuarta apuesta	
									stake = actualLoss*1.0/(s1.getHomeOdds().get(2)-1)+1;
									if (stake<=minStake) stake =minStake;
									if (stake>=maxStake) stake = maxStake;
									win20++;
									//sumamos la ganancia
									profit += s1.getHomeOdds().get(2)*stake-stake;
									
								
									balance += s1.getHomeOdds().get(2)*stake-stake;
									if (debug==1)
									System.out.println("[WIN 12] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+PrintUtils.Print2dec(balance, false));
									betStats.addBet(1, 0, 0);
									risk += stake;
									
									
									
									total++;
								}else{									
									//tomamos la perdida de la cuarta
									stake = actualLoss*1.0/(s1.getHomeOdds().get(2)-1)+1;
									if (stake<=minStake) stake =minStake;
									if (stake>=maxStake) stake = maxStake;
									loss += stake;
									actualLoss += stake;
									
									balance += -stake;
									if (debug>=1)
									System.out.println("[LOSS 12] ("+i+","+j+") "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" ||| "+actualLoss+" || "+PrintUtils.Print2dec(balance, false));
									betStats.addBet(1, 0, 0);
									risk += stake;																											
									total++;
									
									//reiniciamos debug, no profitable
									if (actualLoss>=100){
										profitGlobal += profit;
										lossGlobal += loss;
										profit = 0.0;
										loss = 0.0;
									}
								}
							}
							i=j+1;
							break;
						}//for
					}//else02
				}//else
				
				if (isActualLossCheck && profit>loss){
					actualLossWins++;
				}				
			}
			//acumulamos
			profitGlobal += profit;
			lossGlobal += loss;
			actualLossCountGlobal += actualLossCount;
			actualLossWinsGlobal += actualLossWins;
								
		}//team
	
		double lossPer = losses*100.0/total;
		double totalProfit = profitGlobal-lossGlobal;
		double yield = totalProfit*100.0/risk; 
		if (showSummary)
		System.out.println(total
				+" || "+PrintUtils.Print2dec(lossPer, false)
				+" || "+PrintUtils.Print2dec(profitGlobal, false)
				+" || "+PrintUtils.Print2dec(lossGlobal, false)
				+" || "+PrintUtils.Print2dec(yield, false)
				+" || "+PrintUtils.Print2dec(win20*100.0/total20, false)
				+" || "+PrintUtils.Print2dec(accOdds*1.0/totalOdds, false)
				+" || "+PrintUtils.Print2dec(actualLossWinsGlobal*100.0/actualLossCountGlobal,false)
				+" ||| "+PrintUtils.Print2dec(balance,false)
				);
		
		betStats.addBet(0, totalProfit, risk);
		
		return 0;		
	
	}
	public static double testSystem(BetStats betStats,
			int y1,int y2,int begin,int end,int debug,
			double awinRate,
			double oddsThr1,double oddsThr2,
			boolean showSummary
			){
		String folder = "c:\\baseball\\insight\\";
		
						
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		//System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
		
	
		
		double profitGlobal = 0;
		double lossGlobal = 0;
		double risk = 0;
		int total = 0;
		int win11 = 0;
		int win12 = 0;
		int win20 = 0;
		int win21 = 0;
		int win22 = 0;
	    int losses = 0;
	    int total20 = 0;
	    int total21 = 0;
	    int actualLossCountGlobal = 0;
		int actualLossWinsGlobal = 0;
	    double accOdds = 0;
	    int totalOdds = 0;
	    double actualLoss = 0.0;
		if (end>=teamSeries.size()-1) end=teamSeries.size()-1;
		for (int e=begin;e<=end;e++){
			//Entry<String, ArrayList<Serie>> entry = teamSeries.get(e);
			String team = (String) teamSeries.keySet().toArray()[e];
			ArrayList<Serie> series = teamSeries.get(team);	
			double profit = 0;
			double loss = 0;
			int actualLossCount = 0;
			int actualLossWins = 0;
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				//Serie s1 = series.get(i+1);
				//if (debug==1)
					//System.out.println("[sAll] "+s0.toString());
				if (s0.getResults().size()<3) continue;
				//if (s1.getResults().size()!=3) continue;
				
				//if (!s0.getTeam1().equals("New York Yankees")) continue;
				
				if (s0.getResults().get(0)!=1) continue;
				
				if (debug==1)
					System.out.println("[s0] "+s0.toString());
				
				double stake = 1.0;
				int minStake = 1;
				
				
				
				//odds1
				accOdds += s0.getHomeOdds().get(1);
				totalOdds++;
				
				boolean isActualLossCheck = false;
				
				actualLoss = loss>profit?(loss-profit):0;
				
				//reiniciamos debug, no profitable
				if (actualLoss>=100){
					profitGlobal += profit;
					lossGlobal += loss;
					profit = 0.0;
					loss = 0.0;
				}
				if (s0.getHomeOdds().get(1)<oddsThr1) continue;
				if (actualLoss>0){						
					minStake = 20;
					actualLossCount++;
					isActualLossCheck = true;
					
				}else{
					//if (s0.getHomeOdds().get(1)<1.8) continue;
				}
				
				
				if (s0.getResults().get(1)==1){
					win11++;
					
					actualLoss = loss>profit?(loss-profit):0;
					
					stake = actualLoss*1.0/(s0.getHomeOdds().get(1)-1)+1;
					if (stake<=minStake) stake =minStake;
					//ganancia primera
					profit += s0.getHomeOdds().get(1)*stake-stake;
					actualLoss -= s0.getHomeOdds().get(1)*stake-stake;
					
					if (debug==1)
					System.out.println("[WIN 01] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false));
					betStats.addBet(1, 0, 0);
					risk += stake; 	
					total++;
				}else{ 
					//tomamos la perdida
					actualLoss = loss>profit?(loss-profit):0;
					stake = actualLoss*1.0/(s0.getHomeOdds().get(1)-1)+1;
					if (stake<=minStake) stake =minStake;
					loss += stake;
					
					actualLoss +=stake;
					
					if (debug==1)
					System.out.println("[LOSS 01] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false));
					betStats.addBet(1, 0, 0);
					risk += stake; 
					total++;
					
					accOdds += s0.getHomeOdds().get(2);
					totalOdds++;
					if (s0.getResults().get(2)==1){												
						//segunda apuesta
						actualLoss = loss>profit?(loss-profit):0;
						stake = actualLoss*1.0/(s0.getHomeOdds().get(2)-1)+1;
						if (stake<=minStake) stake =minStake;
						win12++;
						
						//sumamos la ganancia
						profit += s0.getHomeOdds().get(2)*stake-stake;
								
						actualLoss -= s0.getHomeOdds().get(1)*stake-stake;
						
						if (debug==1)
						System.out.println("[WIN 02] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false));
						betStats.addBet(1, 0, 0);
						risk += stake; 
						total++;
					}else{//s02	
						//tomamos la perdida
						stake = actualLoss*1.0/(s0.getHomeOdds().get(2)-1)+1;
						if (stake<=minStake) stake =minStake;
						loss += stake;
						
						actualLoss += stake;
						
						if (debug==1)
						System.out.println("[LOSS 02] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false));
						betStats.addBet(1, 0, 0);
						risk += stake;
						total++;
						for (int j=i+1;j<series.size()-1;j++){
							Serie s1 = series.get(j);
							if (s1.getResults().size()<3) continue;
							if (s1.getResults().get(0)!=1) continue;//comentado en debug
							
							if (s1.getHomeOdds().get(1)<oddsThr2) continue;
							
							if (debug==1)
								System.out.println("[s1] "+s1.toString());
							
							total20++;
							//odds 3
							accOdds += s1.getHomeOdds().get(1);
							totalOdds++;
							if (s1.getResults().get(1)==1){
								//tercera apuesta	
								stake = actualLoss*1.0/(s1.getHomeOdds().get(1)-1)+1;
								if (stake<=minStake) stake =minStake;
								win20++;
								total++;
								//sumamos la ganancia
								profit += s1.getHomeOdds().get(1)*stake-stake;
								
								actualLoss -= s0.getHomeOdds().get(1)*stake-stake;
								
								if (debug==1)
								System.out.println("[WIN 11] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false));								
								betStats.addBet(1, 0, 0);
								risk += stake;
								total++;
							}else{
								total21++;
								//tomamos la perdida de la tercera
								stake = actualLoss*1.0/(s1.getHomeOdds().get(1)-1)+1;
								if (stake<=minStake) stake =minStake;
								loss += stake;
								
								actualLoss += stake;
								
								if (debug==1)
								System.out.println("[LOSS 11] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false));
								betStats.addBet(1, 0, 0);
								risk += stake;
								total++;
								//odds 4
								accOdds += s1.getHomeOdds().get(2);
								totalOdds++;
								
								//if (s1.getHomeOdds().get(2)<1.3) continue;
								
								if (s1.getResults().get(2)==1){																				
									//cuarta apuesta	
									stake = actualLoss*1.0/(s1.getHomeOdds().get(2)-1)+1;
									if (stake<=minStake) stake =minStake;
									win20++;
									//sumamos la ganancia
									profit += s1.getHomeOdds().get(2)*stake-stake;
									
									actualLoss -= s0.getHomeOdds().get(1)*stake-stake;
									
									if (debug==1)
									System.out.println("[WIN 12] "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false));
									betStats.addBet(1, 0, 0);
									risk += stake;
									total++;
								}else{									
									//tomamos la perdida de la cuarta
									stake = actualLoss*1.0/(s1.getHomeOdds().get(2)-1)+1;
									if (stake<=minStake) stake =minStake;
									loss += stake;
									actualLoss += stake;
									
									if (debug>=1)
									System.out.println("[LOSS 12] ("+i+","+j+") "+PrintUtils.Print2dec(stake,false)+" "+PrintUtils.Print2dec(profit,false)+" "+loss+" || "+PrintUtils.Print2dec(profit-loss, false)+" ||| "+actualLoss);
									betStats.addBet(1, 0, 0);
									risk += stake;
									total++;
								}
							}
							i=j+1;
							break;
						}//for
					}//else02
				}//else
				
				if (isActualLossCheck && profit>loss){
					actualLossWins++;
				}				
			}
			//acumulamos
			profitGlobal += profit;
			lossGlobal += loss;
			actualLossCountGlobal += actualLossCount;
			actualLossWinsGlobal += actualLossWins;
								
		}//team
	
		double lossPer = losses*100.0/total;
		double totalProfit = profitGlobal-lossGlobal;
		double yield = totalProfit*100.0/risk; 
		if (showSummary)
		System.out.println(total
				+" || "+PrintUtils.Print2dec(lossPer, false)
				+" || "+PrintUtils.Print2dec(profitGlobal, false)
				+" || "+PrintUtils.Print2dec(lossGlobal, false)
				+" || "+PrintUtils.Print2dec(yield, false)
				+" || "+PrintUtils.Print2dec(win20*100.0/total20, false)
				+" || "+PrintUtils.Print2dec(accOdds*1.0/totalOdds, false)
				+" || "+PrintUtils.Print2dec(actualLossWinsGlobal*100.0/actualLossCountGlobal,false)
				);
		
		betStats.addBet(0, totalProfit, risk);
		
		return 0;		
	}

	private static void updateSeriesTeam(ArrayList<Serie> series, String hTeam, String aTeam, int res,double homeOdds,double awayOdds,String dateStr) {
		
		Serie lastSerie = null;
		if (series.size()>=1) lastSerie = series.get(series.size()-1);
		if (lastSerie==null){
			lastSerie = new Serie();
			lastSerie.setTeam1(hTeam);
			lastSerie.setTeam2(aTeam);
			lastSerie.getResults().add(res);
			lastSerie.getHomeOdds().add(homeOdds);
			lastSerie.getAwayOdds().add(awayOdds);
			lastSerie.getDates().add(dateStr);
			series.add(lastSerie);
		}else{
			if (lastSerie.getTeam2().equalsIgnoreCase(aTeam)){
				lastSerie.getResults().add(res);
				lastSerie.getHomeOdds().add(homeOdds);
				lastSerie.getAwayOdds().add(awayOdds);
				lastSerie.getDates().add(dateStr);
			}else{//new serie
				lastSerie = new Serie();
				lastSerie.setTeam1(hTeam);
				lastSerie.setTeam2(aTeam);
				lastSerie.getResults().add(res);
				lastSerie.getHomeOdds().add(homeOdds);
				lastSerie.getAwayOdds().add(awayOdds);
				lastSerie.getDates().add(dateStr);
				series.add(lastSerie);
			}
		}
		
	}
	
	public static double testSudySequences(BetStats betStats,
			double bank,
			int y1,int y2,int begin,int end,int debug,
			double awinRate,
			double oddsThr1,double oddsThr2,
			boolean showSummary
			){
		String folder = "c:\\baseball\\insight\\";
		
		double balance = bank;
						
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		//System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
		
	
		int cases = 0;
		int losses = 0;
		int cases1 = 0;
		int losses1 = 0;
		int cases3 = 0;
		int losses3 = 0;
		double accOdds1 = 0.0;
		int countOdds =0;
		
		int cases0 = 0;
		int losses0 = 0;
		double accOdds0 = 0.0;
		int countOdds0 =0;
		
		double profit = 0;
		double loss = 0;
		int risk = 0;
		int w=0;
		int l=0;
		double wOdds0 = 0;
		double cOdds0 = 0;
		if (end>=teamSeries.size()-1) end=teamSeries.size()-1;
		for (int e=begin;e<=end;e++){
			//Entry<String, ArrayList<Serie>> entry = teamSeries.get(e);
			String team = (String) teamSeries.keySet().toArray()[e];
			ArrayList<Serie> series = teamSeries.get(team);				
			
			ArrayList<Integer> cleanSeries = new ArrayList<Integer>();
			ArrayList<Serie> cleanSeries0 = new ArrayList<Serie>();
			
			//if (!team.equals("Tampa Bay Rays")) continue;
			int lastIndex = -1;
			int actualLosses = 0;
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				//if (debug==1)
					//System.out.println("[toStudy] "+s0.toString());
				if (s0.getResults().size()!=3) continue;
				
				if (actualLosses==4) break;
				//testprofit estudio
				if (cleanSeries0.size()>=1 && cleanSeries0.size()-1>lastIndex){
					Serie s = cleanSeries0.get(cleanSeries0.size()-1);
					lastIndex = cleanSeries0.size()-1;
					if (s.getResults().get(0)==1){
						int stake = 1;
						if (actualLosses==2) stake = 2;
						if (actualLosses==4) stake = 10;
						
						if (stake==0) continue;
						//if (actualLosses==6) stake = 48;
						if (s.getResults().get(1)==1){
							profit += (s.getHomeOdds().get(1)-1)*stake;
							w++;
							wOdds0+= s.getHomeOdds().get(1);
							cOdds0++;
							risk += stake;
							if (debug==2){								
								System.out.println("[win01] "+PrintUtils.Print2dec(profit-loss, false)+" || "+w+" "+l+" "+PrintUtils.Print2dec(wOdds0/cOdds0, false)+" || "+s.toString());
							}
							actualLosses = 0;
						}else{
							loss += stake;
							l++;
							actualLosses++;
							risk += stake;
							if (debug==2)
								System.out.println("[loss01] "+actualLosses+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+w+" "+l+" "+PrintUtils.Print2dec(wOdds0/cOdds0, false)+" || "+s.toString());
							
							stake = 1*stake;							
							
							if (s.getResults().get(2)==1){
								profit += (s.getHomeOdds().get(2)-1)*stake;
								w++;
								wOdds0+= s.getHomeOdds().get(1);
								cOdds0++;
								actualLosses = 0;
								risk += stake;
								if (debug==2)
									System.out.println("[win02] "+PrintUtils.Print2dec(profit-loss, false)+" || "+w+" "+l+" "+PrintUtils.Print2dec(wOdds0/cOdds0, false)+" || "+s.toString());
								
							}else{
								loss += stake;
								l++;
								actualLosses++;
								risk += stake;
								if (debug==2)
									System.out.println("[loss02] "+actualLosses+" || "+PrintUtils.Print2dec(profit-loss, false)+" || "+w+" "+l+" "+PrintUtils.Print2dec(wOdds0/cOdds0, false)+" || "+s.toString());
							}
						}
					}
				}
				
				
				if (s0.getResults().get(0)==1){
					
					double odds01 = s0.getHomeOdds().get(1);
					if (odds01<oddsThr1) continue;
					
					int res = 1;//ganado por defecto
					if (s0.getResults().get(1)==-1 && s0.getResults().get(2)==-1) res = 0;
					
					if (debug==1)
						System.out.println("[toStudy] "+res+" || "+s0.toString());
					
															
					cleanSeries.add(res);
					cleanSeries0.add(s0);
					if (cleanSeries.size()>=4){												
						if (cleanSeries.get(cleanSeries.size()-2)==0
								&& cleanSeries.get(cleanSeries.size()-3)==0
								&& cleanSeries.get(cleanSeries.size()-4)==0
								){
							cases3++;
							if (cleanSeries.get(cleanSeries.size()-1)==0){
								losses3++;
							}
						}						
					}
					if (cleanSeries.size()>=3){
						if (cleanSeries.get(cleanSeries.size()-1)==0
								&& cleanSeries.get(cleanSeries.size()-2)==0
								&& cleanSeries.get(cleanSeries.size()-3)==0
								){
							/*if (debug==1)
							System.out.println("[BAD] "+cleanSeries0.get(cleanSeries.size()-3)
									+ " ||| "+cleanSeries0.get(cleanSeries.size()-2)
									+ " ||| "+cleanSeries0.get(cleanSeries.size()-1)
									);*/
						}
						
						if (cleanSeries.get(cleanSeries.size()-2)==0
								&& cleanSeries.get(cleanSeries.size()-3)==0){
							cases++;
							if (cleanSeries.get(cleanSeries.size()-1)==0){
								losses++;
							}
						}
						
					}
					if (cleanSeries.size()>=2){		
						if (true
								&& cleanSeries.get(cleanSeries.size()-2)==1
								){
							cases0++;
							if (cleanSeries.get(cleanSeries.size()-1)==0){
								losses0++;
								if (debug==1){
									//System.out.println("[FULL LOSE]");
								}
							}
							Serie s1 = cleanSeries0.get(cleanSeries.size()-1);
							accOdds0 += s1.getHomeOdds().get(1);
							countOdds0++;
							if (s1.getResults().get(1)==0){
								accOdds0 += s1.getHomeOdds().get(2);
								countOdds0++;
							}
						}
						
						if (true
								//&& cleanSeries.get(cleanSeries.size()-3)==1
								&& cleanSeries.get(cleanSeries.size()-2)==0
								){
							cases1++;
							
							if (cleanSeries.get(cleanSeries.size()-1)==0){
								losses1++;
								if (debug==1){
									System.out.println("[FULL LOSE]");
								}
							}
							if (debug==1){
								System.out.println("[case] "+losses1+" / "+cases1);
							}
							Serie s1 = cleanSeries0.get(cleanSeries.size()-1);
							accOdds1 += s1.getHomeOdds().get(1);
							countOdds++;
							if (s1.getResults().get(1)==0){
								accOdds1 += s1.getHomeOdds().get(2);
								countOdds++;
							}
						}						
					}
				}				
			}
			
			String seriesStr ="";
			for (int j=0;j<cleanSeries.size();j++){
				seriesStr += cleanSeries.get(j)+" ";
			}
			if (debug==1)
			System.out.println(team+" "+seriesStr);
								
		}//team
		
		double winPer = 100.0-losses1*100.0/countOdds;
		double avgOdds = accOdds1*1.0/cases1;
		double factor = winPer*avgOdds;
		
		double winPer0 = 100.0-losses0*100.0/countOdds0;
		double avgOdds0 = accOdds0*1.0/cases0;
		double factor0 = winPer0*avgOdds0;
		
		double factorTotal = (factor0*cases0+factor*cases1)/(cases0+cases1); 
		System.out.println(
				" "+PrintUtils.Print2dec(oddsThr1, false)
				+" || "+PrintUtils.Print2dec(factorTotal, false)
				+" || "+cases0
				+" "+PrintUtils.Print2dec(100.0-losses0*100.0/countOdds0, false)
				+" "+PrintUtils.Print2dec(accOdds0*1.0/cases0, false)
				+" "+PrintUtils.Print2dec(factor0, false)
				+" || "+cases
				+" "+PrintUtils.Print2dec(100.0-losses1*100.0/countOdds, false)
				+" "+PrintUtils.Print2dec(accOdds1*1.0/cases1, false)
				+" "+PrintUtils.Print2dec(factor, false)
				+" || "+PrintUtils.Print2dec(100.0-losses*100.0/cases, false)			
				+" || "+PrintUtils.Print2dec(100.0-losses3*100.0/cases3, false)	
				+" ||| "+(w+l)+" "+PrintUtils.Print2dec(profit-loss, false)+" "+PrintUtils.Print2dec((profit-loss)*100.0/risk, false)		
				);
	
		
		
		return 0;		
	
	}
	
	
	public static int getStake(double amount,double odds){
		
		double target = amount+1.0;
		
		if (amount<=0) return 1;
		
		int stake = (int) (Math.ceil(target)/(odds-1));
		if (stake*(odds-1)<target) stake = stake+1;
		
		if (stake<1) stake = 1;
		if (stake>50) stake = 0;
		
		return stake;
	}
	
	public static double testSudySequences2(BetStats betStats,
			double bank,
			int y1,int y2,int begin,int end,int debug,
			double awinRate,
			double oddsThr1,double oddsThr2,
			boolean showSummary
			){
		String folder = "c:\\baseball\\insight\\";
		
		double balance = bank;
						
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		//System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
		
	
		int cases = 0;
		int losses = 0;
		int cases1 = 0;
		int losses1 = 0;
		int cases3 = 0;
		int losses3 = 0;
		double accOdds1 = 0.0;
		int countOdds =0;
		
		int cases0 = 0;
		int losses0 = 0;
		double accOdds0 = 0.0;
		int countOdds0 =0;
		
		double profit = 0;
		double loss = 0;
		int risk = 0;
		int w=0;
		int l=0;
		double wOdds0 = 0;
		double cOdds0 = 0;
		int maxLosses = 0;
		if (end>=teamSeries.size()-1) end=teamSeries.size()-1;
		for (int e=begin;e<=end;e++){
			//Entry<String, ArrayList<Serie>> entry = teamSeries.get(e);
			String team = (String) teamSeries.keySet().toArray()[e];
			ArrayList<Serie> series = teamSeries.get(team);				
			
			ArrayList<Integer> cleanSeries = new ArrayList<Integer>();
			ArrayList<Serie> cleanSeries0 = new ArrayList<Serie>();
			
			//if (!team.equals("Tampa Bay Rays")) continue;
			int lastIndex = -1;
			int actualLosses = 0;
			int conLosses = 0;
			double tprofit = 0;
			double tloss = 0;
			int stake = 1;
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				if (debug==2){								
					System.out.println(">>>>>[MATCH] "+s0.toString());
				}
				if (s0.getResults().size()<3) continue;
				
				if (cleanSeries0.size()>=1 && cleanSeries0.size()-1>lastIndex){
					Serie s = cleanSeries0.get(cleanSeries0.size()-1);
					lastIndex = cleanSeries0.size()-1;
					
					//solo le metemos mano a la serie si ha empezado ganando
					if (s.getResults().get(0)!=1) continue;
					
					if (debug==2){								
						//System.out.println("[SERIE] "+s.toString());
					}	
					
					//cada vez que iniciamos una serie inicializamos stakes
					stake = 1;
					if (actualLosses==2) stake = 2;
					if (actualLosses==4) stake = 10;
					
					if (debug==2){								
						System.out.println(">>>>>[SERIE] "+s.toString());
					}
					for (int j=1;j<s.getResults().size();j++){
						
						double odds01 = s.getHomeOdds().get(j);//solo se agrega si las odds son buenas
						if (odds01<oddsThr1){
							System.out.println("[SERIE NO BET] "+PrintUtils.Print2dec(s.getHomeOdds().get(j), false)
									+" || "+s.toString());
							continue;
						}
												
						int res = s.getResults().get(j);
						//int stake = Baseball.getStake(tloss-tprofit,s.getHomeOdds().get(j));
						//si el stake es 0 reiniciamos y salimos, a otra serie
						if (stake==0){
							if (actualLosses>0){
								if (debug==2){								
									System.out.println("[STOPPED] "+PrintUtils.Print2dec(tprofit-tloss, false));
								}								
							}
							actualLosses=0;//reiniciamos losses
							tprofit=0;
							tloss=0;
							break;
						}
						risk += stake; //tomamos el riesgo
						if (res==1){
							profit += (s.getHomeOdds().get(j)-1)*stake;
							tprofit += (s.getHomeOdds().get(j)-1)*stake;
							w++;
							wOdds0+= s.getHomeOdds().get(j);
							cOdds0++;	
							if (debug==2){								
								System.out.println("[win] "+j
								+" || "+PrintUtils.Print2dec(profit-loss, false)
								+" || "+PrintUtils.Print2dec(tprofit-tloss, false)
								+" || "+w+" "+l+" "+PrintUtils.Print2dec(wOdds0/cOdds0, false)
								+" || "+s.toString()
								+" || "+stake
								+" || "+PrintUtils.Print2dec(s.getHomeOdds().get(j), false)+" "+PrintUtils.Print2dec((s.getHomeOdds().get(j)-1)*stake, false)
								+" || "+actualLosses
								);
							}
							actualLosses = 0;
							conLosses = 0;
							tprofit=0;
							tloss=0;
							break;//salimos del bucle en caso de victoria y de esta serie
						}else{
							loss += stake;
							tloss += stake;
							//actualLosses$$ += stake;
							l++;
							actualLosses++;
							conLosses++;
							if (conLosses>=maxLosses) maxLosses = conLosses;
							if (debug==2)
								System.out.println("[loss] "+j
								 +" || "+PrintUtils.Print2dec(profit-loss, false)
								 +" || "+PrintUtils.Print2dec(tprofit-tloss, false)
							     +" || "+w+" "+l+" "+PrintUtils.Print2dec(wOdds0/cOdds0, false)
								+" || "+s.toString()
								+" || "+stake
								+" || "+actualLosses
								);
						}						
					}				
				}
				
				//if (debug==1)
					//System.out.println("[toStudy] "+s0.toString());
				
				//añadimos series
				if (s0.getResults().get(0)==1){
					
					//double odds01 = s0.getHomeOdds().get(1);//solo se agrega si las odds son buenas
					//if (odds01<oddsThr1) continue;
					
					int res = 1;//ganado por defecto
					if (s0.getResults().get(1)==-1 && s0.getResults().get(2)==-1) res = 0;
					
					if (debug==1)
						System.out.println("[toStudy] "+res+" || "+s0.toString());
																				
					cleanSeries.add(res);
					cleanSeries0.add(s0);										
				}				
			}
			
			String seriesStr ="";
			for (int j=0;j<cleanSeries.size();j++){
				seriesStr += cleanSeries.get(j)+" ";
			}
			if (debug==1)
			System.out.println(team+" "+seriesStr);
								
		}//team
		
		double winPer = 100.0-losses1*100.0/countOdds;
		double avgOdds = accOdds1*1.0/cases1;
		double factor = winPer*avgOdds;
		
		double winPer0 = 100.0-losses0*100.0/countOdds0;
		double avgOdds0 = accOdds0*1.0/cases0;
		double factor0 = winPer0*avgOdds0;
		
		double factorTotal = (factor0*cases0+factor*cases1)/(cases0+cases1); 
		System.out.println(
				" "+PrintUtils.Print2dec(oddsThr1, false)				
				+" ||| "+(w+l)+" "+PrintUtils.Print2dec(profit-loss, false)+" "+PrintUtils.Print2dec((profit-loss)*100.0/risk, false)		
				+" || "+maxLosses
				);
	
		betStats.setMaxLosses(maxLosses);
		
		return 0;		
	
	}
	
	public static void updateArray(ArrayList<Integer> lossesArray,int losses){
		lossesArray.set(losses,lossesArray.get(losses)+1);
	}
	
	public static double testSudySequences3(BetStats betStats,
			double bank,
			int y1,int y2,int begin,int end,int debug,
			double awinRate,
			double oddsThr1,double oddsThr2,
			boolean showSummary
			){
		String folder = "c:\\baseball\\insight\\";
		
		double balance = bank;
						
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		//System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
	
		int losses = 0;
		int wins = 0;
		int partialLosses = 0;
		double totalProfit = 0;
		double totalRisk = 0;
		double totalLoss = 0.0;
		ArrayList<Integer> lossesArray = new ArrayList<Integer>();
		for (int i=0;i<=5;i++) lossesArray.add(0);
		if (end>=teamSeries.size()-1) end=teamSeries.size()-1;
		for (int e=begin;e<=end;e++){
			//Entry<String, ArrayList<Serie>> entry = teamSeries.get(e);
			String team = (String) teamSeries.keySet().toArray()[e];
			ArrayList<Serie> series = teamSeries.get(team);				
			
			ArrayList<Integer> cleanSeries = new ArrayList<Integer>();
			ArrayList<Serie> cleanSeries0 = new ArrayList<Serie>();
			
			int actualLosses=0;		
			double profit = 0.0;
			double loss = 0.0;
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				if (debug==2){								
					//System.out.println(">>>>>[NUEVA SERIE] "+s0.toString());
				}
				if (s0.getResults().size()!=3) continue;
				
				
				
				int levelLoss = 0;
				if (s0.getResults().get(0)==1){
					if (debug==2){								
						System.out.println(">>>>>[NUEVA SERIE] "+s0.toString());
					}
					for (int s=1;s<s0.getResults().size();s++){
						int res = s0.getResults().get(s);
						double odds = s0.getHomeOdds().get(s);
						if (odds>=oddsThr1){
							if (res==1){
								double stake = (loss+1.0)/(odds-1.0); //siempre quiero sacr +1
								totalRisk += stake;
									
								wins++;
								
								actualLosses = 0;
								
								totalProfit+=1;//siempre que gano es +1								
								if (debug==2)
									System.out.println("[WIN] "+wins
											+" || "+PrintUtils.Print2dec(loss,false)+" || "+PrintUtils.Print2dec(stake,false)
											+" || "+PrintUtils.Print2dec(totalProfit-totalLoss,false)
											);
								profit = 0;
								loss = 0;
								break;
							}else{
								//aqui es donde se incrementan las losses
								double stake = (loss+1.0)/(odds-1.0); //siempre quiero sacr +1
								loss += stake;
								totalRisk += stake;
										
								actualLosses++;
								if (actualLosses>=4){	
									break;
								}
							}
						}else{
							if (res==1){
								//se aceptan perdidas porque ha ganado con cuota baja
								if (actualLosses>=4){
									partialLosses++;
									if (debug==2)
									System.out.println("[PARTIAL LOSS (win)] "+partialLosses+" || "+PrintUtils.Print2dec(-loss,false)
											+" || "+PrintUtils.Print2dec(totalProfit-totalLoss,false));
									updateArray(lossesArray,actualLosses);
									actualLosses=0;	
									//se suman las perdidas
									totalLoss+=loss;
									profit = 0;
									loss = 0;
									break;
								}
							}
						}
					}//serie 0
					if (actualLosses>=4){		
						updateArray(lossesArray,actualLosses);
						actualLosses =0;
						losses++;
						totalLoss+=loss;
						if (debug==2){												
							System.out.println("[FINAL LOSS] "+losses+" || "+PrintUtils.Print2dec(-loss,false)
									+" || "+PrintUtils.Print2dec(totalProfit-totalLoss,false));
						}	
						
						
						profit = 0;
						loss = 0;
					}						
				}//series
			}//team
			if (actualLosses>0){
				partialLosses++;
				totalLoss+=loss;
				if (debug==2)
				System.out.println("[PARTIAL LOSS (win) END SEQUENCE] "+partialLosses+" || "+PrintUtils.Print2dec(-loss,false)
						+" || "+PrintUtils.Print2dec(totalProfit-totalLoss,false));
				updateArray(lossesArray,actualLosses);
				actualLosses=0;						
				profit = 0;
				loss = 0;
			}
			
			
		
								
		}//team
		
		
	
		int total = wins+losses+partialLosses;
		double winPer = wins*100.0/total;
		
		System.out.println(PrintUtils.Print2dec(oddsThr1,false)
				+" || "
				+" "+total+" "+wins+" "+partialLosses+" "+losses
				+" || "+PrintUtils.Print2dec(winPer,false)
				+" || "+PrintUtils.Print2dec(totalProfit-totalLoss,false)
				+" || "+PrintUtils.Print2dec((totalProfit-totalLoss)*100.0/(totalRisk),false)
	    );
				
		return 0;		
	
	}
	
	
	public static double testSudySequences4(BetStats betStats,
			double bank,
			int y1,int y2,int begin,int end,int debug,
			double awinRate,
			double oddsThr1,double oddsThr2,
			boolean showSummary
			){
		String folder = "c:\\baseball\\insight\\";
		
		double balance = bank;
		
		
		ArrayList<Integer> stats = new ArrayList<Integer>();
		stats.add(0);stats.add(0);
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		//System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
	
		int losses = 0;
		int wins = 0;
		int partialLosses = 0;
		double totalProfit = 0;
		double totalRisk = 0;
		double totalLoss = 0.0;
		ArrayList<Integer> lossesArray = new ArrayList<Integer>();
		for (int i=0;i<=5;i++) lossesArray.add(0);
		if (end>=teamSeries.size()-1) end=teamSeries.size()-1;
		for (int e=begin;e<=end;e++){
			//Entry<String, ArrayList<Serie>> entry = teamSeries.get(e);
			String team = (String) teamSeries.keySet().toArray()[e];
			ArrayList<Serie> series = teamSeries.get(team);				
			
			ArrayList<Integer> cleanSeries = new ArrayList<Integer>();
			ArrayList<Serie> cleanSeries0 = new ArrayList<Serie>();
			
			int actualLosses=0;		
			double profit = 0.0;
			double loss = 0.0;
			ArrayList<Integer> teamSeq = new ArrayList<Integer>();
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				if (debug==2){								
					//System.out.println(">>>>>[NUEVA SERIE] "+s0.toString());
				}
				if (s0.getResults().size()!=3) continue;
				
				
				
				int levelLoss = 0;
				if (s0.getResults().get(0)==1){
					//teamSeq.add(9);//nueva secuencia
					if (debug==2){								
						//System.out.println(">>>>>[NUEVA SERIE] "+s0.toString());
					}
					for (int s=1;s<s0.getResults().size();s++){
						int res = s0.getResults().get(s);
						double odds = s0.getHomeOdds().get(s);
						if (odds>=oddsThr1) teamSeq.add(res);
					}
				}//serie 0
			}//teamSeries
			System.out.println(team+" || "+printArrayCal(teamSeq,stats));
		}//team
		
		double per = stats.get(1)*100.0/stats.get(0);
		
		System.out.println(PrintUtils.Print2dec(per, false));
				
		return 0;		
	
	}
	
	public static int testStudySeries(BetStats betStats,
			double bank,
			int y1,int y2,int begin,int end,int debug,
			double awinRate,
			double oddsThr1,double oddsThr2,
			boolean showSummary
			){
		String folder = "c:\\baseball\\insight\\";
		
		double balance = bank;
						
		ArrayList<Integer> stats = new ArrayList<Integer>();
		stats.add(0);stats.add(0);
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder, y1, y2, 1, 2);
		HashMap<String,ArrayList<Serie>> teamSeries = new HashMap<String,ArrayList<Serie>>();
		//System.out.println("data : "+matches.size());
		for (int i=0;i<matches.size();i++){
			Match  m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getWinTeam();
			//System.out.println(res);
			if (!teamSeries.containsKey(hTeam)){
				teamSeries.put(hTeam, new ArrayList<Serie>());
			}
			if (!teamSeries.containsKey(aTeam)){
				teamSeries.put(aTeam, new ArrayList<Serie>());
			}
			
			ArrayList<Serie> hSeries = teamSeries.get(hTeam);
			ArrayList<Serie> aSeries = teamSeries.get(aTeam);
			
			String dateStr = m.getDay()+"-"+m.getMonth()+"-"+m.getYear();
			updateSeriesTeam(hSeries,hTeam,aTeam,res,m.getHomeOdds(),m.getAwayOdds(),dateStr);
			updateSeriesTeam(aSeries,aTeam,hTeam,-res,m.getAwayOdds(),m.getHomeOdds(),dateStr);						
		}
	
		int losses = 0;
		int wins = 0;
		int partialLosses = 0;
		double totalProfit = 0;
		double totalRisk = 0;
		double totalLoss = 0.0;
		ArrayList<Integer> lossesArray = new ArrayList<Integer>();
		for (int i=0;i<=5;i++) lossesArray.add(0);
		if (end>=teamSeries.size()-1) end=teamSeries.size()-1;
		for (int e=begin;e<=end;e++){
			//Entry<String, ArrayList<Serie>> entry = teamSeries.get(e);
			String team = (String) teamSeries.keySet().toArray()[e];
			ArrayList<Serie> series = teamSeries.get(team);				
			
			ArrayList<Integer> cleanSeries = new ArrayList<Integer>();
			ArrayList<Serie> cleanSeries0 = new ArrayList<Serie>();
			
			int actualLosses=0;		
			double profit = 0.0;
			double loss = 0.0;
			ArrayList<Integer> teamSeq = new ArrayList<Integer>();
			for (int i=0;i<series.size()-1;i++){
				Serie s0 = series.get(i);
				if (debug==2){								
					//System.out.println(">>>>>[NUEVA SERIE] "+s0.toString());
				}
				if (s0.getResults().size()!=3) continue;
				
				
				
				int levelLoss = 0;
				if (s0.getResults().get(0)==1){
					//teamSeq.add(9);//nueva secuencia
					if (debug==2){								
						//System.out.println(">>>>>[NUEVA SERIE] "+s0.toString());
					}
					int res = -1;
					if (s0.getResults().size()==3 && (s0.getResults().get(1)==1 || s0.getResults().get(2)==1)){
						res = 1;
					}
					
					
					teamSeq.add(res);
				}//serie 0
			}//teamSeries
			String teamStr = String.format("%22s", team);
			
			if (debug==2)
				System.out.println(teamStr+" || "+printArrayCal(teamSeq,stats));
			else
				printArrayCal(teamSeq,stats);
		}//team
		
		double per = stats.get(1)*100.0/stats.get(0);
		
		System.out.println(stats.get(0)+" "+stats.get(1)+" || "+PrintUtils.Print2dec(100.0-per, false));
				
		return  0;		
	
	}

	private static String printArray(ArrayList<Integer> teamSeq) {
		String str = "";
		for (int i=0;i<teamSeq.size();i++){
			str+=teamSeq.get(i)+" ";
		}
		
		return str;
	}
	
	private static int getPuntos(int racha){
		
		if (racha>=1) return racha;
		else{
			if (racha==-1) return 0;
			if (racha==-2) return 0;
			if (racha==-3) return 0;
			if (racha==-4) return -15;
			
			if (racha==-5) return -15;
			if (racha==-6) return -15;
			if (racha==-7) return -15;
			if (racha==-8) return -30;
			
			if (racha==-9) return -30;
			if (racha==-10) return -30;
			if (racha==-11) return -30;
		}
		
		return 0;
	}
	
	private static String printArrayCal(ArrayList<Integer> teamSeq,ArrayList<Integer> stats) {
		String str = "";
		int actualLosses = 0;
		int maxLosses = 0;
		int actualRacha = 0;
		
		int totalPuntos = 0;
		
		int cases2 = 0;
		int losses2 = 0;
		ArrayList<Integer> rachas = new ArrayList<Integer>();
		for (int i=0;i<teamSeq.size();i++){
			int res = teamSeq.get(i); 
			//str+=teamSeq.get(i)+" ";
			
			if (res==1){
				if (actualRacha<=0){
					if (actualRacha<=-3){
						cases2++;
					}
					if (actualRacha<=-4){
						losses2++;
					}
					
					str+=actualRacha+" ";
					totalPuntos +=  getPuntos(actualRacha);
					actualRacha = 0;					
					rachas.add(actualRacha);
					
				}
				actualRacha++;
				
				actualLosses = 0;
			}else if (res==-1){
				
				if (actualRacha>=0){
					str+=actualRacha+" ";

					totalPuntos +=  getPuntos(actualRacha);
					actualRacha = 0;
					rachas.add(actualRacha);
				}
				actualRacha--;
			    
				actualLosses++;
				if (actualLosses>=maxLosses) maxLosses = actualLosses;
			}else if (res==0){
				//
			}
		}
		
		double losses2per = losses2*100.0/cases2;
		double win2per = 100.0-losses2*100.0/cases2;
		stats.set(0,stats.get(0)+cases2);
		stats.set(1,stats.get(1)+losses2);
		
		totalPuntos +=  getPuntos(actualRacha);
		//System.out.println(stats.get(1)+" "+losses2);
		return PrintUtils.Print2dec(losses2per, false)+" "+maxLosses+" || "+str;//+ " ||| "+maxLosses+" |||| "+totalPuntos;
	}
	
	public static void testNewApproach(BetStats betStats,int y1, int y2,double oddsThr,double factor, int debug){
	
		String folder = "c:\\baseball";
		String folder2 = "c:\\baseball\\insight\\";
		
		ArrayList<Match> matches = SportsInsight.readAndReorderMLB(folder2, y1, y2, 1, 2);
		HashMap<String,TeamSerieInfo> teamsInfo = new HashMap<String,TeamSerieInfo>();
		int lastDay = -1;
		
		double totalRisk = 0;
		double totalProfit = 0;
		double globalProfit = 0;
		double totalLoss = 0;
		double stake = 1;
		
		double maxStake = 1;
		
		int cases = 0;
		int wins = 0;
		double avg = 0.0;
		ArrayList<Integer> arrayWins= new ArrayList<Integer>();
		for (int i=0;i<matches.size();i++){
			
			Match m = matches.get(i);
			
			int day = m.getDay();
			int month = m.getMonth();
			
			/*if (!m.getHomeTeam().equalsIgnoreCase("Chicago White Sox")
					&& !m.getAwayTeam().equalsIgnoreCase("Chicago White Sox")					
					){
				continue;
			}*/
			
			if (day!=lastDay){
				globalProfit = getTotalProfit(teamsInfo);
				
				/*if (globalProfit>=0){
					resetTeams(teamsInfo);
				}*/
				avg = MathUtils.average(arrayWins, arrayWins.size()-200, arrayWins.size()-1);
				
				/*if (avg<=-0.15){
					resetTeams(teamsInfo);
				}*/
				if (debug==2)
				System.out.println("[NUEVA JORNADA] "+PrintUtils.Print2dec(globalProfit, false));
				if (debug==3){
					
					System.out.println("[NUEVA JORNADA] "+PrintUtils.Print2dec(globalProfit, false)
					+" "+PrintUtils.Print2dec(avg, false)+" "+cases);
				}
				lastDay = day;
			}
			
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
									
			if (!teamsInfo.containsKey(hTeam)){
				teamsInfo.put(hTeam,new TeamSerieInfo());
			}
			
			if (!teamsInfo.containsKey(aTeam)){
				teamsInfo.put(aTeam,new TeamSerieInfo());
			}
			
			TeamSerieInfo hInfo = teamsInfo.get(hTeam);
			hInfo.setTeam(hTeam);
			TeamSerieInfo aInfo = teamsInfo.get(aTeam);
			aInfo.setTeam(aTeam);
			
			Serie hSerie = hInfo.getLastSerie();
			Serie aSerie = aInfo.getLastSerie();
			
			String prefix="";
			
			String serieDescr = m.getHomeTeam()+'_'+m.getAwayTeam();
			int betInt = 0;
			boolean isStop = false;
			if (hSerie==null || aSerie==null || (!hSerie.getTeam1().equalsIgnoreCase(aSerie.getTeam2()) ||!hSerie.getSerieDescr().equalsIgnoreCase(serieDescr))){
				prefix="[0] [-] [-] [-]";
			}else if (hSerie!=null && aSerie!=null && hSerie.getTeam1().equalsIgnoreCase(aSerie.getTeam2())){
				
				String betStr ="";
				String actualRonda="";
				
				if (hSerie.getResults().get(0)==1){
					betStr = "H";
					betInt = 1;
					actualRonda = String.valueOf(hInfo.getActualRonda());
					
					
					if (hSerie.getResults().size()>1 && hInfo.getActualRonda()==0){
						//hInfo.acceptProfit();
						isStop = true;
					}
				}else if (hSerie.getResults().get(0)==-1){
					betStr = "A";
					betInt = -1;
					actualRonda = String.valueOf(aInfo.getActualRonda());
					
					if (aSerie.getResults().size()>1 && aInfo.getActualRonda()==0){
						//aInfo.acceptProfit();
						isStop = true;
					}
				}
				
				String betResStr="W";
				if (betInt!=res) betResStr="L";
					
				prefix="["+hSerie.getResults().size()+"] "+"["+betStr+"] "+"["+betResStr+"] "+"["+actualRonda+"]";
			}
			
			/*if (!isStop){
				if (betInt==1 && m.getHomeOdds()<1.8) isStop = true;
				if (betInt==-1 && m.getAwayOdds()<1.8) isStop = true;
			}*/
			
			
			if (!isStop){
				if (debug==2)
				System.out.println(prefix+" "+m.toString()
				+" || "+PrintUtils.Print2dec(hInfo.getActualProfit(),false)
				+" "+PrintUtils.Print2dec(aInfo.getActualProfit(),false)
				);						
				if (betInt==0){
					hInfo.updateSerie(m.getAwayTeam(),serieDescr, res);
					aInfo.updateSerie(m.getHomeTeam(),serieDescr, -res);
				}else if (betInt==1){
					//actualProfit
					
					if (m.getHomeOdds()>=oddsThr){
						double actualProfit = hInfo.getActualProfit();
						
						if (actualProfit>=0 && month==9){
							
						}else{
							//calculo de stake si es necesarioñ
							stake = 1;
							if (actualProfit<0){
								/*if (actualProfit<=-50 
										//&& globalProfit>0
										){
									aInfo.acceptProfit();
								}else{
									stake = -actualProfit/(m.getHomeOdds()-1)+1.0;
								}*/
								
								stake = -actualProfit/(m.getHomeOdds()-1)+1.0;
								
								if (hInfo.getActualRonda()==4){
									hInfo.acceptProfit();
									stake = 1;
								}
							}			
							
							
							if (stake>=maxStake) maxStake = stake;
							
							if (globalProfit<=-40) stake = 10;
							
							/*if (stake>=1000000){
								hInfo.acceptProfit();
								stake = 50;
							}*/
							
							if (avg<=factor){
								totalRisk += stake;							
								actualProfit = hInfo.updateProfit(res, m.getHomeOdds(), stake);
							}
						}
						cases++;
						if (res==1) wins++;
						arrayWins.add(res);
					}else{
						if (hInfo.getActualRonda()==4){
							hInfo.acceptProfit();							
						}
					}
					
					//System.out.println(stake+" || "+actualProfit);
					
				
					hInfo.updateSerie(m.getAwayTeam(),serieDescr, res);
					aInfo.updateSerie(m.getHomeTeam(),serieDescr, 0);
				}else if (betInt==-1){
					if (m.getAwayOdds()>=oddsThr){
						//actualProfit
						double actualProfit = aInfo.getActualProfit();
						
						if (actualProfit>=0 && month==9){
							
						}else{
							//calculo de stake si es necesarioñ
							stake = 1;
							if (actualProfit<0){
								/*if (actualProfit<=-50 && globalProfit>=0
										){
									aInfo.acceptProfit();
								}else{
									stake = -actualProfit/(m.getAwayOdds()-1)+1.0;
									
								}*/
								
								stake = -actualProfit/(m.getAwayOdds()-1)+1.0;
								
								if (aInfo.getActualRonda()==4){
									aInfo.acceptProfit();
									stake = 1;
								}
							}
							
							if (globalProfit<=-40) stake = 10;
							
							if (stake>=maxStake) maxStake = stake;
							
							/*if (stake>=1000000){
								hInfo.acceptProfit();
								stake = 50;
							}*/
							
							if (avg<=factor){
								totalRisk += stake;
								actualProfit = aInfo.updateProfit(-res, m.getAwayOdds(), stake);
							}
						}
						cases++;
						if (res==-1) wins++;
						arrayWins.add(-res);
					}else{
						if (aInfo.getActualRonda()==4){
							aInfo.acceptProfit();							
						}
					}
					
					
					
					hInfo.updateSerie(m.getAwayTeam(),serieDescr, 0);
					aInfo.updateSerie(m.getHomeTeam(),serieDescr, -res);
				}
			}
			
		}
		
		totalProfit = getTotalProfit(teamsInfo);
		
		betStats.setProfit(betStats.getProfit()+totalProfit);
		betStats.setRisk(betStats.getRisk()+totalRisk);
		
		if (debug>=1){		
			double yield = totalProfit*100.0/totalRisk;
			System.out.println("[END ] "+y1
					+" "+PrintUtils.Print2dec(factor, false)
					+" "+cases
					+" "+PrintUtils.Print2dec(totalProfit-totalLoss, false)
			+" "+PrintUtils.Print2dec(totalRisk, false)+" "+PrintUtils.Print2dec(maxStake, false)+" "+PrintUtils.Print2dec(yield, false)
			+" || "+PrintUtils.Print2dec(wins*100.0/cases, false)
			);
		}
		
	}

	private static void resetTeams(HashMap<String, TeamSerieInfo> teamsInfo) {
		
		
		for (Entry<String, TeamSerieInfo> entry : teamsInfo.entrySet()){
			String team = entry.getKey();
			entry.getValue().acceptProfit();
		}

		
	}

	private static double getTotalProfit(HashMap<String, TeamSerieInfo> teamsInfo) {

		double totalProfit = 0;
		
		for (Entry<String, TeamSerieInfo> entry : teamsInfo.entrySet()){
			String team = entry.getKey();
			totalProfit += entry.getValue().getFinishProfit()+entry.getValue().getActualProfit();
		}
		
		return totalProfit;
	}

	public static void main(String[] args) {
		String folder = "c:\\baseball";
		String folder2 = "c:\\baseball\\insight";
		
		for (double factor=2012.0;factor<=2012.0;factor+=0.01){
			for (double oddsThr=1.8;oddsThr<=1.80;oddsThr+=0.10){
				BetStats betStats = new BetStats();
				betStats.setProfit(0.0);
				betStats.setRisk(0.0);
				for (int y=2014;y<=2014;y++){
					Baseball.testNewApproach(betStats,y, y,oddsThr,factor,2);
				}
				System.out.println(
						PrintUtils.Print2dec(factor, false)
						+" "+PrintUtils.Print2dec(oddsThr, false)
						+" || "+PrintUtils.Print2dec(betStats.getProfit()*100.0/betStats.getRisk(), false)
						);
			}
		}
		
		

	}

}
