package drosa.apuestas;

public class Streak {
	
	int value = 0;
	int peso = 0; //cada partido tiene un peso
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getPeso() {
		return peso;
	}
	public void setPeso(int peso) {
		this.peso = peso;
	}
}
