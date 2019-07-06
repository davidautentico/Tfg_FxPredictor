package drosa.strategies;

import java.util.ArrayList;
import java.util.Collections;

import drosa.strategies.auxiliar.Position;
import drosa.utils.PrintUtils;

public class MonteCarloAnalysis {	
	//Basic simulation: reward to risk ratio,win to loss ratio,average losing trade(%)
		
	private static MonteCarloResult simulation(double initialBalance,ArrayList<Position> positions, 
			ArrayList<Integer> orderList){
			MonteCarloResult mc = new MonteCarloResult();
			double balance = initialBalance;
			double maxBalance = initialBalance;
			double maxDD = 0.0;
			int wins=0;
			int losses=0;
			int maxConsecWins=0;
			int currentConsecWins=0;
			int maxConsecLosses=0;
			int currentConsecLosses=0;
			for (int i=0;i<orderList.size();i++){
				int index = orderList.get(i);
				Position pos = positions.get(index);
				//cierre posición		
				double pipValue=0.01;	
				double pipsStop = Math.abs((pos.getEntryValue()-pos.getOriginalStopLoss()))/pos.getMult(); 
				double posValue = (double)pipsStop*pipValue;		
				double tradeRisk = pos.getRisk();
				double maxAllowed = (balance*tradeRisk);
				int maxPos = (int) (maxAllowed/posValue);
				pipValue = maxPos*pipValue;
				
				balance += pipValue*pos.getPips();
				/*System.out.println(PrintUtils.Print(pos.getEntryValue())+
						" "+PrintUtils.Print(pos.getOriginalStopLoss())+
						" "+PrintUtils.Print(pos.getMult()));*/
						
				/*System.out.println("balance traderisk pipvalue posPips pipsStop: "
						+PrintUtils.Print(balance)+" "+PrintUtils.Print(tradeRisk)+" "+PrintUtils.Print(pipValue)
						+" "+PrintUtils.Print(pos.getPips())+" "+PrintUtils.Print(pipsStop));
				*/		
				//System.out.println("bal: "+PrintUtils.Print(balance));
				
				
				if (pos.getPips()>=0){
					wins++;
					if (currentConsecLosses>maxConsecLosses){
						maxConsecLosses=currentConsecLosses;
					}
					currentConsecLosses=0;
					currentConsecWins++;
					
				}else{
					losses++;
					if (currentConsecWins>maxConsecWins){
						maxConsecWins=currentConsecWins;
					}
					currentConsecWins=0;
					currentConsecLosses++;					
				}
				
				if (balance>=maxBalance){
					maxBalance = balance;
				}else{					
					double actualDD = 100.0-balance*100.0/maxBalance;					
					if (actualDD>=maxDD){
						maxDD = actualDD;						
					}
				}						
			}
			mc.setWorstDD(maxDD);
			mc.setAvgBalance(balance);
			mc.setAvgMaxDD(maxDD);
			mc.setWorstConWins(maxConsecWins);
			mc.setWorstConLosses(maxConsecLosses);			
			//System.out.println("bal: "+PrintUtils.Print(balance));
						
			return mc;
	}
	
	public static void test(double initialBalance,ArrayList<Position> positions,int iterations){
		ArrayList<Integer> list = new ArrayList<Integer>(positions.size());
		for(int i = 0; i <positions.size(); i++){
		  list.add(i);
		}
		
		ArrayList<MonteCarloResult> results = new ArrayList<MonteCarloResult>();
		double avgBalance = 0.0;
		double avgMaxDD=0.0;
		double worstDD=0.0;
		int worstConsecWins=99999;
		int worstConsecLosses=0;
		int bestConsecWins=0;
		for (int i=0;i<iterations;i++){//para cada iteración una simulación		
			//System.out.println("iteration: "+(i+1));
			ArrayList<Integer> orderList = generateRandomInts(list);
			//ArrayList<Integer> orderList = list;
			MonteCarloResult mc = simulation(initialBalance,positions,orderList);
			results.add(mc);
			avgBalance+=mc.getAvgBalance();
			avgMaxDD+=mc.getAvgMaxDD();
			if (mc.getAvgMaxDD()>worstDD){
				worstDD = mc.getAvgMaxDD();
			}
			if (mc.getWorstConLosses()>worstConsecLosses){
				worstConsecLosses = mc.getWorstConLosses();
			}
			if (mc.getWorstConWins()< worstConsecWins){
				worstConsecWins = mc.getWorstConWins();
			}
			if (mc.getWorstConWins()> bestConsecWins){
				bestConsecWins = mc.getWorstConWins();
			}
		}
		avgBalance/=iterations;
		avgMaxDD/=iterations;
		
		//FINAL REPORT
		System.out.println("******** MONTE CARLO TEST ********");
		System.out.println("Num Iterations: "+iterations);
		System.out.println("Num Trades: "+positions.size());
		System.out.println("Average Balance: "+PrintUtils.Print(avgBalance));
		System.out.println("Average Max DD: "+PrintUtils.Print(avgMaxDD)+"%");
		System.out.println("Worst DD: "+PrintUtils.Print(worstDD)+"%");
		System.out.println("Worst Consec Losses: "+PrintUtils.Print(worstConsecLosses));
		System.out.println("Worst Consec Wins: "+PrintUtils.Print(worstConsecWins));	
		System.out.println("Best Consec Wins: "+PrintUtils.Print(bestConsecWins));
	}

	private static ArrayList<Integer> generateRandomInts(ArrayList<Integer> list) {
		// TODO Auto-generated method stub
		Collections.shuffle(list);
		return list;
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		for(int i = 0; i < 10; i++)
		{
		  list.add(i);
		}
		list = generateRandomInts(list);
		for(int i = 0; i < 10; i++)
		{
		  System.out.println(list.get(i));
		}
		list = generateRandomInts(list);
		for(int i = 0; i < 10; i++)
		{
		  System.out.println(list.get(i));
		}
		
		/*Collections.shuffle(list);

		Object[] randomNumbers = (Object[])list.toArray();

		for(int i = 0; i < 10; i++)
		{
		  System.out.println(randomNumbers[i]);
		}*/
	}

}
