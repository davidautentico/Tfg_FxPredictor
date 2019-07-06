package drosa.experimental.edge;

import drosa.utils.PrintUtils;

public class Edge {
	
	
	public static double calculateEdge(double tp,double sl,double percentWin){
		
		double pWin = sl/(tp+sl);//fair odds;
		
		return percentWin-pWin*100.0;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("edge: "+Edge.calculateEdge(0.12, 0.12, 64)+" kelly: "+Edge.calculateKelly(0.08, 0.01, 16));
	}

	public static double calculateKelly(double tpFactor, double slFactor,
			double perWin) {
		// TODO Auto-generated method stub
		
		//System.out.println("[Calculate edge] tp sl win "+PrintUtils.Print2(tpFactor)+" "+PrintUtils.Print2(tpFactor)+" "+PrintUtils.Print2(slFactor)+" "+PrintUtils.Print2(perWin));
		double oddReceived = tpFactor/slFactor;
		double perLoss = 100.0-perWin;
		double kelly = (oddReceived*perWin-perLoss)/oddReceived;
		
		return kelly;
	}

}
