package drosa.experimental.billyt;

import java.util.ArrayList;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class BreakInfo {
	
	int index = -1;
	int order = 0;
	int win = 0;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	
	public static void analyze(String header,ArrayList<BreakInfo> breaks,int tp,int sl) {
		
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=100;i++){
			totals.add(0);
			wins.add(0);
		}
		for (int i=0;i<breaks.size();i++){
			BreakInfo bi = breaks.get(i);
			
			int idx = bi.getOrder();
			int tot = totals.get(idx);
			int w = wins.get(idx);
			
			totals.set(idx, tot+1);
			if (bi.getWin()==1){
				wins.set(idx, w+1);
			}
			
			if (i>=10
					){
				if (breaks.get(i).getWin()==-1 
						&& breaks.get(i-1).getWin()==-1
						&& breaks.get(i-2).getWin()==-1
						&& breaks.get(i-3).getWin()==1
						//&& breaks.get(i-4).getWin()==1
						//&& breaks.get(i-5).getWin()==1
						//&& breaks.get(i-6).getWin()==1
						){
					System.out.println("LOSING STREAK "+breaks.get(i).getIndex());
				}
			}
			//System.out.println(bi.getWin());
		}
		
		String perStr="";
		for (int idx=0;idx<=10;idx++){
			int tot = totals.get(idx);
			int w = wins.get(idx);
			
			double winPer = w*100.0/tot;
			double pf = ((tp-0.0)*winPer)/((sl+0.0)*(100-winPer));
			perStr+=tot+" "+PrintUtils.Print2dec(winPer, false)+" ("+PrintUtils.Print2dec(pf, false)+")";
		}
		
		System.out.println(header+" "+breaks.size()+" || "+perStr.trim());
	}
	
   public static void analyze2(ArrayList<BreakInfo> breaks,int diff) {
		
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=100;i++){
			totals.add(0);
			wins.add(0);
		}
		for (int i=0;i<breaks.size();i++){
			BreakInfo bi = breaks.get(i);
			
			int idx = bi.getOrder();
			if (idx==1){
				BreakInfo bi0 = breaks.get(i-1);
				int diff1 = bi.getIndex()-bi0.getIndex();
				if (diff1<diff) continue;
				
				int tot = totals.get(idx);
				int w = wins.get(idx);
				
				totals.set(idx, tot+1);
				if (bi.getWin()==1){
					wins.set(idx, w+1);
				}			
			}
		}
		
		String perStr="";
		for (int idx=0;idx<=1;idx++){
			int tot = totals.get(idx);
			int w = wins.get(idx);
			
			double winPer = w*100.0/tot;
			perStr+=PrintUtils.Print2dec(winPer, false)+" ";
		}
		
		System.out.println(breaks.size()+" || "+perStr.trim());
	}
   
   
   public static void analyze3(ArrayList<BreakInfo> breaks) {
		
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=100;i++){
			totals.add(0);
			wins.add(0);
		}
		for (int i=0;i<breaks.size();i++){
			BreakInfo bi = breaks.get(i);
			
			int idx = bi.getOrder();
			int tot = totals.get(idx);
			int w = wins.get(idx);
			
			if (idx==1 
					&& breaks.get(i-1).getWin()==1
					&& breaks.get(i-1).getOrder()==0
					){				
				totals.set(idx, tot+1);
				if (bi.getWin()==1){
					wins.set(idx, w+1);
				}		
			}
		}
		
		String perStr="";
		for (int idx=0;idx<=1;idx++){
			int tot = totals.get(idx);
			int w = wins.get(idx);
			
			double winPer = w*100.0/tot;
			perStr+=tot+" "+PrintUtils.Print2dec(winPer, false)+" ";
		}
		
		System.out.println(breaks.size()+" || "+perStr.trim());
	}
   
   public static void analyze4(String header,ArrayList<BreakInfo> breaks,int tp,int sl) {
		
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=100;i++){
			totals.add(0);
			wins.add(0);
		}
		
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		streaks.add(0);
		
		for (int i=0;i<breaks.size();i++){
			BreakInfo bi = breaks.get(i);
			
			int lastIdx = streaks.size()-1;
			int actualStreak = streaks.get(lastIdx);
			int win = bi.getWin();
			
			if (actualStreak>=0){
				if (win==1){
					actualStreak++;
					streaks.set(lastIdx, actualStreak);
				}else if (win==-1){
					streaks.add(-1);
				}
			}else if (actualStreak<=0){
				if (win==-1){
					actualStreak--;
					streaks.set(lastIdx, actualStreak);
				}else if (win==1){
					streaks.add(1);
				}
			}
		}
		
		ArrayList<Integer> streaksN = new ArrayList<Integer>();
		ArrayList<Integer> streaksP = new ArrayList<Integer>();
		for (int i=0;i<streaks.size();i++){
			int streak = streaks.get(i);
			if (streak>=0) streaksP.add(streak);
			else if (streak<0) streaksN.add(-streak);
		}
		
		String header2 = header+" "+breaks.size();
		
		MathUtils.summary("[NEG] "+header2, streaksN);
		MathUtils.summary("[POS] "+header2, streaksP);
	}
   
   public static void analyze7(String header,ArrayList<BreakInfo> breaks,int tp,int sl) {
		
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=100;i++){
			totals.add(0);
			wins.add(0);
		}
		
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		streaks.add(0);
		
		ArrayList<Integer> maxStreaks = new ArrayList<Integer>();
		for (int i=0;i<=50;i++){
			maxStreaks.add(0);
		}
		
		double lotSize = 2.5;
		int actualStreak = 0;
		double totalProfit = 0;
		double totalRonda = 0;
		double maxProfit = 0;
		double maxDD = 0;
		int maxStreak = 0;
		double comm = 2.0;
		
		double grossProfit=0;
		double grossLoss= 0;
		for (int i=0;i<breaks.size();i++){
			BreakInfo bi = breaks.get(i);
			
			int win = bi.getWin();
			
			if (win==-1){
				if (actualStreak<=0) actualStreak--;
				else actualStreak=-1;
				if (actualStreak<=maxStreak) maxStreak=actualStreak;
			}else if (win==1){
				if (actualStreak<0){
					int total = maxStreaks.get(-actualStreak);
					maxStreaks.set(-actualStreak, total+1);
				}
				actualStreak = 1;				
			}
			
			if (win==1){
				totalProfit += (tp-comm)*lotSize;
				totalRonda += (tp-comm)*lotSize; 
				grossProfit += (tp-comm)*lotSize;
			}else if (win==-1){
				totalProfit += (-sl-comm)*lotSize;
				totalRonda +=  (-sl-comm)*lotSize;
				grossLoss += -(-sl-comm)*lotSize;
			}
			
			
			/*System.out.println(win+" || "
					+" "+PrintUtils.Print2dec(totalRonda, false)
					+" "+PrintUtils.Print2dec(totalProfit, false)
					+" || "+PrintUtils.Print2dec(lotSize, false)
			);*/
			
			if (totalRonda>=0){
				totalRonda = 0.0;
				lotSize = 2.5;
			}else{
				lotSize = -totalRonda/(tp-comm);
			}
			
			//if (lotSize>=200) lotSize = 2.5;
			
			if (totalProfit>=maxProfit) maxProfit = totalProfit;
			
			double dd = maxProfit - totalProfit;
			if (dd>=maxDD) maxDD = dd;
			
			
		}
		
		double pf = grossProfit/grossLoss;
		
		String streakStr="";
		int tot = 0;
		int tot2 = 0;
		for (int i=1;i<=40;i++){
			streakStr+=" "+maxStreaks.get(i);
			tot += maxStreaks.get(i);
			if (i>1) tot2 += maxStreaks.get(i); 
		}
		
		double minEdge = 100.0-tp*100.0/(tp+sl);
		double edge2 = 100.0-tot2*100.0/tot;
		double diffEdge = edge2-minEdge;
		
		
		if (-maxStreak<=200)
		System.out.println(tp+" "+sl+" || "
				+" "+PrintUtils.Print2dec(grossProfit-grossLoss, false)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" "+PrintUtils.Print2dec(grossProfit/grossLoss, false)
				+" || "+maxStreak+" || "+breaks.size()+" || "+streakStr
				+" || "+PrintUtils.Print2dec(minEdge, false)+" "+PrintUtils.Print2dec(edge2, false)+" "+PrintUtils.Print2dec(diffEdge, false)
				);
	
	}
   
   
   public static void analyze6(String header,ArrayList<BreakInfo> breaks,int tp,int sl) {
		
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=100;i++){
			totals.add(0);
			wins.add(0);
		}
		
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		streaks.add(0);
		
		double maxDD =0;
		int maxProfit = 0;
		int totalProfit = 0;
		int profitRonda = 0;
		int actualSize = 1;
		int actualStreak = 0;
		int maxLosses = 0;
		for (int i=0;i<breaks.size();i++){
			BreakInfo bi = breaks.get(i);
			
			int win = bi.getWin();
			int profit = actualSize;
			if (win==-1) profit = -actualSize*10;
			
			profitRonda += profit;
			totalProfit +=profit;
			
			//System.out.println(win+" || "+profit+" || "+profitRonda+" "+totalProfit);
			
			if (win==-1){
				if (actualStreak>=0) actualStreak = -1;
				else actualStreak--;
				
				if (-actualStreak>=maxLosses) maxLosses=-actualStreak;
			}else{
				actualStreak=0;
			}
			
			
			if (profitRonda>=1){
				actualSize = 1;
				profitRonda = 0;
			}else{
				if (actualStreak==-1){
					actualSize = 1;
				}
				if (actualStreak==-2){
					actualSize = 2;
				}
				if (actualStreak==-3){
					actualSize = -profitRonda+1;
				}
			}
			
			if (totalProfit>=maxProfit) maxProfit=totalProfit;
			double dd = maxProfit-totalProfit;
			if (dd>=maxDD) maxDD = dd;
		}
		
		double factor = maxProfit*1.0/maxDD;
		System.out.println(maxProfit+" "+totalProfit+" "+PrintUtils.Print2dec(maxDD, false)+" || "+PrintUtils.Print2dec(factor, false)+" || "+maxLosses);
		
		//String header2 = header+" "+breaks.size();
		
		//MathUtils.summary("[NEG] "+header2, streaksN);
		//MathUtils.summary("[POS] "+header2, streaksP);
	}
   
   
   public static void analyze5(String header,ArrayList<BreakInfo> breaks,int tp,int sl) {
		
		ArrayList<Integer> totals = new ArrayList<Integer>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		for (int i=0;i<=100;i++){
			totals.add(0);
			wins.add(0);
		}
		
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		streaks.add(0);
		
		for (int i=0;i<breaks.size();i++){
			BreakInfo bi = breaks.get(i);
			
			int lastIdx = streaks.size()-1;
			int actualStreak = streaks.get(lastIdx);
			int win = bi.getWin();
			
			if (actualStreak>=0){
				if (win==1){
					actualStreak++;
					streaks.set(lastIdx, actualStreak);
				}else if (win==-1){
					streaks.add(-1);
				}
			}else if (actualStreak<=0){
				if (win==-1){
					actualStreak--;
					streaks.set(lastIdx, actualStreak);
				}else if (win==1){
					streaks.add(1);
				}
			}
		}
		
		ArrayList<Integer> streaksN = new ArrayList<Integer>();
		ArrayList<Integer> streaksP = new ArrayList<Integer>();
		for (int i=0;i<streaks.size();i++){
			System.out.println(streaks.get(i));
		}
		
		//String header2 = header+" "+breaks.size();
		
		//MathUtils.summary("[NEG] "+header2, streaksN);
		//MathUtils.summary("[POS] "+header2, streaksP);
	}
	
	

}
