package TradeHero;

public class Trade {
	
	private String company;
	private final int buy;
	private final int sell;
	
	public Trade(String company, int buy, int sell){
		this.company = company;
		this.buy = buy;
		this.sell = sell;
	}
	
	public String getCompany() {
		return company;
	}
	
	public int getBuy(){
		return buy;
	}
	
	public int getSell(){
		return sell;
	}

}
