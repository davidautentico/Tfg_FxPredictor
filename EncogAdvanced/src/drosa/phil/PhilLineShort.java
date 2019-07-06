package drosa.phil;

import drosa.utils.PrintUtils;

public class PhilLineShort {
	LineType lineType;
	short value;
	
	
		
	public LineType getLineType() {
		return lineType;
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	public short getValue() {
		return value;
	}

	public void setValue(short value) {
		this.value = value;
	}


	public static PhilLineShort createLine(LineType lineType,short value){
		PhilLineShort line = new PhilLineShort();
		line.setLineType(lineType);
		line.setValue(value);
		
		return line;
	}
	
	public String toString(){
		return this.lineType.name()+":"+PrintUtils.Print(this.value);
	}
}
