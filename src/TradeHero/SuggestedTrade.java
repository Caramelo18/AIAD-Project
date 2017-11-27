package TradeHero;

public class SuggestedTrade {
	
	private final TradeAgent agent;
	private final String company;
	private final int day;
	
	public SuggestedTrade(TradeAgent agent, String company, int day){
		this.agent = agent;
		this.company = company;
		this.day = day;
	}
	
	public TradeAgent getAgent(){
		return agent;
	}
	
	public String getCompany(){
		return company;
	}
	
	public int getPurchaseDay(){
		return day;
	}

}
