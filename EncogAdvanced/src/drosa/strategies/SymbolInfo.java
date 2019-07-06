package drosa.strategies;

public class SymbolInfo {

	String tableName;
	String symbol="";
	String description;
	double minTick=-1;
	double tickValue=-1;
	MarketType marketType;
	ContractType contractType;
	int mult=0;
	int margin=0;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public MarketType getMarketType() {
		return marketType;
	}
	public void setMarketType(MarketType marketType) {
		this.marketType = marketType;
	}
	
	public ContractType getContractType() {
		return contractType;
	}
	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}
	public int getMult() {
		return mult;
	}
	public void setMult(int mult) {
		this.mult = mult;
	}
	public int getMargin() {
		return margin;
	}
	public void setMargin(int margin) {
		this.margin = margin;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getMinTick() {
		return minTick;
	}
	public void setMinTick(double minTick) {
		this.minTick = minTick;
	}
	public double getTickValue() {
		return tickValue;
	}
	public void setTickValue(double tickValue) {
		this.tickValue = tickValue;
	}
	
	
}
