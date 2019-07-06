package drosa.experimental.HedgeStudy;

import java.util.ArrayList;

import drosa.utils.PrintUtils;

public class HedgeAnalysis {

	public static ArrayList<Integer> extractSequence(String valueStr) {
		ArrayList<Integer> seqInt = new ArrayList<Integer>();
		
		String[] values = valueStr.trim().split(" ");
		
		for (int i=0;i<values.length;i++){
			try{
				int v = Integer.valueOf(values[i]);
				seqInt.add(v);
			}catch(Exception e){
				
			}
		}
		return seqInt;
	}
	
	public static void test(double capital,double riskWin, double riskLoss,String sequence){
		
		int totalWins = 0;
		
		double balanceSL = capital;
		double balanceHedge = capital;
		double equitityHedge = capital;
		ArrayList<Integer> trades = extractSequence(sequence);
		
		System.out.println("CAPITAL INICIAL: "+PrintUtils.Print2(capital)+" || "+PrintUtils.Print2(balanceHedge));
		boolean hedged = false;
		double lastAmountRisked = equitityHedge*riskLoss/100.0;
		double lastAmountGained = equitityHedge*riskWin/100.0;
		for (int i=0;i<trades.size();i++){
			int win = trades.get(i);
			String slInfo="";
			String slHedge="";
			double actualAmountRisked = lastAmountRisked;
			double actualAmountGained = lastAmountGained;
			//SL
			if (win==1){
				totalWins++;
				balanceSL*=(1.0+riskWin/100.0);
				//parteHedged
				equitityHedge+=lastAmountGained;
				if (equitityHedge*riskWin/100.0>lastAmountGained){ //solo si el riesgo es ya mayor que lo arriesgado antes salgo del hedge
					lastAmountGained = equitityHedge*riskWin/100.0;
					lastAmountRisked = equitityHedge*riskLoss/100.0;
				}
			}else{
				balanceSL*=(1.0-riskLoss/100.0);
				//parte hedge
				equitityHedge-=lastAmountRisked; //si pierdo lastAmount sigue siendo la misma
			}
			slHedge += PrintUtils.Print2(equitityHedge)+" ("+PrintUtils.Print2(actualAmountGained)
					+"--"+PrintUtils.Print2(actualAmountRisked)+")";
			slInfo += PrintUtils.Print2(balanceSL);	
			String diff = PrintUtils.Print2((equitityHedge*100.0/balanceSL)-100.0);
			System.out.println("TRADE "+i+" -> "+trades.get(i)+" || "+slInfo+" || "+slHedge+" || "+diff);
		}
		double perWin = totalWins*100.0/trades.size();
		double perLoss = 100.0-perWin;
		double pf = (perWin*riskWin)/(perLoss*riskLoss);
		System.out.println(trades.size()+" "+PrintUtils.Print2(perWin)+" "+PrintUtils.Print2(pf));
	}
	
public static void test(double capital,double riskWin, double riskLoss,ArrayList<Integer> trades){
		
		int totalWins = 0;
		int totalHedges = 0;
		
		double balanceSL = capital;
		double balanceHedge = capital;
		double equitityHedge = capital;
		
		System.out.println("CAPITAL INICIAL: "+PrintUtils.Print2(capital)+" || "+PrintUtils.Print2(balanceHedge));
		boolean hedged = false;
		double lastAmountRisked = equitityHedge*riskLoss/100.0;
		double lastAmountGained = equitityHedge*riskWin/100.0;
		for (int i=0;i<trades.size();i++){
			int win = trades.get(i);
			String slInfo="";
			String slHedge="";
			double actualAmountRisked = lastAmountRisked;
			double actualAmountGained = lastAmountGained;
			//SL
			if (win==1){
				totalWins++;
				balanceSL*=(1.0+riskWin/100.0);
				//parteHedged
				equitityHedge+=lastAmountGained;
				if (equitityHedge*riskWin/100.0>lastAmountGained){ //solo si el riesgo es ya mayor que lo arriesgado antes salgo del hedge
					lastAmountGained = equitityHedge*riskWin/100.0;
					lastAmountRisked = equitityHedge*riskLoss/100.0;
				}
			}else{
				balanceSL*=(1.0-riskLoss/100.0);
				//parte hedge
				equitityHedge-=lastAmountRisked; //si pierdo lastAmount sigue siendo la misma
				totalHedges++;
			}
			slHedge += PrintUtils.Print2(equitityHedge)+" ("+PrintUtils.Print2(actualAmountGained)
					+"--"+PrintUtils.Print2(actualAmountRisked)+")";
			slInfo += PrintUtils.Print2(balanceSL);	
			String diff = PrintUtils.Print2((equitityHedge*100.0/balanceSL)-100.0);
			System.out.println("TRADE "+i+" -> "+trades.get(i)+" || "+slInfo+" || "+slHedge+" || "+diff);
		}
		double perWin = totalWins*100.0/trades.size();
		double perLoss = 100.0-perWin;
		double pf = (perWin*riskWin)/(perLoss*riskLoss);
		int tp = 13;
		int sl = 20;
		double comm = 1.4;
		
		System.out.println(trades.size()+" "+PrintUtils.Print2(perWin)+" "+PrintUtils.Print2(pf)+" "+totalHedges);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String seq1 = "1 1 1 1 0 0 1 1 1 1 0 1 1 1 0 1 0 1 1 1 1 1 0 1 1 1 1 1 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 1 1 1 1 1"
				+" 1 1 1 0 1 0 0 1 1 1 0 0 0 1 1 1 1 1 1 0 0 1 1 0 1 1 1 1 1 1 1 1 1 1 1 1 0 1 0 1 1 1 1 1 1 1 1 0 1"
				+" 0 1 0 0 0 1 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 1 1 1 0 1"
				+" 0 0 1 0 1 1 1 0 1 1 1 1 1 0 1 0 1 1 1 1 1 1 1 1 1 1 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1"
				;
		
		
		//HedgeAnalysis.test(200.0,4.0, 8.0, seq1);
		HedgeAnalysis.test(200.0,4.0, 8.0, HedgeAnalysis.extractSequence(seq1));
	}

}
