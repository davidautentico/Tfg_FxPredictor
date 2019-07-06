package drosa.experimental;

import java.util.ArrayList;

import drosa.finances.QuoteShort;

public class MaxMinRisk {

	int maxMin  = 0;
	double risk = 0;
	
	
	public int getMaxMin() {
		return maxMin;
	}


	public void setMaxMin(int maxMin) {
		this.maxMin = maxMin;
	}


	public double getRisk() {
		return risk;
	}


	public void setRisk(double risk) {
		this.risk = risk;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * maxMinRisks debe estar ordenado de mayor a menor
	 * @param maxMinRisks
	 * @param maxMin0
	 * @return
	 */
	public static double calculateRisk(ArrayList<MaxMinRisk> maxMinRisks,
			QuoteShort maxMin0) {
		// TODO Auto-generated method stub
		int value = Math.abs(maxMin0.getExtra());
		for (int i=0;i<maxMinRisks.size();i++){
			MaxMinRisk mmr = maxMinRisks.get(i);
			//if (value>=mmr.getMaxMin()) return mmr.getRisk();
			//if (value>=mmr.getMaxMin() && value<=(mmr.getMaxMin()+50000)) return mmr.getRisk();//para pruebas
			if (value>=mmr.getMaxMin()) return mmr.getRisk();//para pruebas
		}
		return -1.0;
	}


	public static void load(ArrayList<MaxMinRisk> maxMinRisks, String values) {
		// TODO Auto-generated method stub
		if (maxMinRisks==null) maxMinRisks = new ArrayList<MaxMinRisk>();
		maxMinRisks.clear();
		String risks[] = values.split(",");
		for (int i=0;i<risks.length;i++){
			int maxMin = Integer.valueOf(risks[i].split(" ")[0]);
			double risk   = Double.valueOf(risks[i].split(" ")[1]);
			MaxMinRisk mmr = new MaxMinRisk();
			mmr.setMaxMin(maxMin);
			mmr.setRisk(risk);
			maxMinRisks.add(mmr);
		}
		
	}

}
