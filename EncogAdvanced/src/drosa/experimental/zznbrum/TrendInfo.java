package drosa.experimental.zznbrum;

public class TrendInfo {
	
	int leg = 0;
	int maxExtension = 0;
	int actualExtensionClose = 0;
	
	public int getLeg() {
		return leg;
	}
	public void setLeg(int leg) {
		this.leg = leg;
	}
	public int getMaxExtension() {
		return maxExtension;
	}
	public void setMaxExtension(int maxExtension) {
		this.maxExtension = maxExtension;
	}
	public int getActualExtensionClose() {
		return actualExtensionClose;
	}
	public void setActualExtensionClose(int actualExtensionClose) {
		this.actualExtensionClose = actualExtensionClose;
	}
	
	public String toString(){
		
		return leg+" "+maxExtension+" "+actualExtensionClose;	
	}

}
