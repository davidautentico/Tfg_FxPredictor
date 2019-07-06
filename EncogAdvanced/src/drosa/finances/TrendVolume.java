package drosa.finances;

public class TrendVolume {
	String symbol;
	PeriodInformation period;
	long volumeTotal;
	long volumeAverage;
	
	//mensuales
	long via3M; //volumen anterior al inicial 3 meses antes
	long via2M;
	long via1M;
	long viM; //volumen inicial mensual
	long vip1M;//volumen posterior al inicial 1 mes
	long vip2M;
	long vfM; //volumen final mensual
	long vfa1M; //volumen anterior al final 1 mes
	long vfa2M;
	long vfa3M;
	
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public PeriodInformation getPeriod() {
		return period;
	}
	public void setPeriod(PeriodInformation period) {
		this.period = period;
	}
	public long getVolumeTotal() {
		return volumeTotal;
	}
	public void setVolumeTotal(long volumeTotal) {
		this.volumeTotal = volumeTotal;
	}
	public long getVolumeAverage() {
		return volumeAverage;
	}
	public void setVolumeAverage(long volumeAverage) {
		this.volumeAverage = volumeAverage;
	}
	public long getVia3M() {
		return via3M;
	}
	public void setVia3M(long via3m) {
		via3M = via3m;
	}
	public long getVia2M() {
		return via2M;
	}
	public void setVia2M(long via2m) {
		via2M = via2m;
	}
	public long getVia1M() {
		return via1M;
	}
	public void setVia1M(long via1m) {
		via1M = via1m;
	}
	public long getViM() {
		return viM;
	}
	public void setViM(long viM) {
		this.viM = viM;
	}
	public long getVip1M() {
		return vip1M;
	}
	public void setVip1M(long vip1m) {
		vip1M = vip1m;
	}
	public long getVip2M() {
		return vip2M;
	}
	public void setVip2M(long vip2m) {
		vip2M = vip2m;
	}
	public long getVfM() {
		return vfM;
	}
	public void setVfM(long vfM) {
		this.vfM = vfM;
	}
	public long getVfa1M() {
		return vfa1M;
	}
	public void setVfa1M(long vfa1m) {
		vfa1M = vfa1m;
	}
	public long getVfa2M() {
		return vfa2M;
	}
	public void setVfa2M(long vfa2m) {
		vfa2M = vfa2m;
	}
	public long getVfa3M() {
		return vfa3M;
	}
	public void setVfa3M(long vfa3m) {
		vfa3M = vfa3m;
	}
}
