package drosa.finances;

public class SAPStock {

		String index;
		String symbol;
		String name;
		String sector;
		
		public SAPStock(String index, String symbol, String name,
				String sector) {
			// TODO Auto-generated constructor stub
			this.index=index;
			this.symbol=symbol;
			this.name = name;
			this.sector = sector;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}
		public String getSymbol() {
			return symbol;
		}
		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getSector() {
			return sector;
		}
		public void setSector(String sector) {
			this.sector = sector;
		}
		
		
}
