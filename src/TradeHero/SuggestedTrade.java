package TradeHero;

public class SuggestedTrade {
	
	private final TradeAgent agent;
	private final String company;
	
	public SuggestedTrade(TradeAgent agent, String company){
		this.agent = agent;
		this.company = company;
	}
	
	public TradeAgent getAgent(){
		return agent;
	}
	
	public String getCompany(){
		return company;
	}

}
