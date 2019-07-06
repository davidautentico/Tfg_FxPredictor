package drosa.apuestas;

import java.util.ArrayList;

public class TeamSerieInfo {
	
	String team;
	
	ArrayList<Serie> series = new ArrayList<Serie>();
	
	int actualRonda = 0;
	
	double actualProfit = 0;
	
	double finishProfit = 0;

	public int getActualRonda() {
		return actualRonda;
	}

	public void setActualRonda(int actualRonda) {
		this.actualRonda = actualRonda;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public ArrayList<Serie> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<Serie> series) {
		this.series = series;
	}
	
	
	public void updateSerie(String opponent,String serieDescr,int res){
		
		Serie lastSerie = null;
		
		if (series.size()>0){
			lastSerie = series.get(series.size()-1);
		}
		
		if (lastSerie!=null){
			if (!lastSerie.getTeam2().equalsIgnoreCase(opponent)
					|| !lastSerie.getSerieDescr().equalsIgnoreCase(serieDescr)
					){//nueva serie
				lastSerie = new Serie();
				lastSerie.setTeam1(this.team);
				lastSerie.setTeam2(opponent);
				lastSerie.setSerieDescr(serieDescr);
				series.add(lastSerie);
				//System.out.println("añadiendo serie: "+this.team+" "+opponent);
			}
		}else{
			lastSerie = new Serie();
			lastSerie.setTeam1(this.team);
			lastSerie.setTeam2(opponent);
			lastSerie.setSerieDescr(serieDescr);
			series.add(lastSerie);
			//System.out.println("añadiendo serie: "+this.team+" "+opponent);
		}
		
		if (lastSerie.results.size()>0){			
			if (lastSerie.results.get(0)==1){
				if (res==1){					
					actualRonda = 0;
					//this.acceptProfit();
					//System.out.println("[WIN] "+team+" "+actualRonda);
				}
				if (res==-1){
					actualRonda++;
					//System.out.println(team+" "+actualRonda);
				}
			}
		}
		lastSerie.results.add(res);	
	}
	
   public int getLastSerieRes(){
		
		if (series.size()==0) return 0;
		
		Serie lastSerie = series.get(series.size()-1);
		
		return lastSerie.getResults().get(lastSerie.results.size()-1);
	}
   
   public int getLastSerieRes0(){
		
		if (series.size()==0) return 0;
		
		Serie lastSerie = series.get(series.size()-1);
		
		return lastSerie.getResults().get(0);
	}

   public Serie getLastSerie() {
	// TODO Auto-generated method stub
	   if (series.size()==0) return null;
	   
	   return series.get(series.size()-1);
   }

   public double updateProfit(int res,double odds,double stake){
	   
	   if (res==-1) actualProfit -= stake;
	   else actualProfit += stake*(odds-1);
	   
	   return actualProfit;
   }
   
	public double getActualProfit() {
		// TODO Auto-generated method stub
		return actualProfit;
	}

	public double getFinishProfit() {
		return finishProfit;
	}

	public void setFinishProfit(double finishProfit) {
		this.finishProfit = finishProfit;
	}

	public void setActualProfit(double actualProfit) {
		this.actualProfit = actualProfit;
	}
	
	
	//acepta el profit y reinicia
	public void acceptProfit(){
		this.finishProfit += this.actualProfit;
		this.actualProfit = 0;
	}

}
