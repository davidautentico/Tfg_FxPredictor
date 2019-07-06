package drosa.trabajo.csv;

public class TApunte {
		
	long idAsiento;
	String idSubcuenta;
	String fecha;
	float debe;
	float haber;
	String descr;
	
	public TApunte(long aidAsiento,String aidSubcuenta,String afecha,String adescr,float adebe,float ahaber){
		this.idAsiento = aidAsiento;
		this.idSubcuenta = aidSubcuenta;
		this.fecha = afecha;
		this.descr = adescr;
		this.debe = adebe;
		this.haber = ahaber;					
	}
	
	public String toString(){
		return idAsiento+";"+idSubcuenta+";"+fecha+";"+descr+";"+debe+";"+haber;
	}
	
	public long getIdAsiento() {
		return idAsiento;
	}
	public void setIdAsiento(long idAsiento) {
		this.idAsiento = idAsiento;
	}
	public String getIdSubcuenta() {
		return idSubcuenta;
	}
	public void setIdSubcuenta(String idSubcuenta) {
		this.idSubcuenta = idSubcuenta;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public float getDebe() {
		return debe;
	}
	public void setDebe(float debe) {
		this.debe = debe;
	}
	public float getHaber() {
		return haber;
	}
	public void setHaber(float haber) {
		this.haber = haber;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}

	

}
