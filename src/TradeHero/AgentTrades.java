package TradeHero;

import java.util.ArrayList;

public class AgentTrades implements Comparable<AgentTrades>{

	private TradeAgent agent;
	public ArrayList<SuggestedTrade> trades;
	
	public AgentTrades(TradeAgent agent){
		this.agent = agent;
		trades = new ArrayList<SuggestedTrade>();
	}
	
	public TradeAgent getAgent(){
		return agent;
	}
	
	public void addTrade(SuggestedTrade trade){
		trades.add(trade);
	}
	
	public double getCurrentProfit(){
		double currentProfit = 0;
		
		for(SuggestedTrade trade: trades){
			double currentPrice = Market.getCompanyStockValue(trade.getCompany());
			double purchasePrice = Market.getCompanyStockValue(trade.getCompany(), trade.getPurchaseDay());
			
			double increase = (currentPrice - purchasePrice) / purchasePrice;
			
			currentProfit += increase;
		}
		
		return currentProfit;
	}
	@Override
	public int compareTo(AgentTrades arg0) {
		if(this.getCurrentProfit() > arg0.getCurrentProfit())
			return 1;
		else if(this.getCurrentProfit() == arg0.getCurrentProfit())
			return 0;
		else
			return -1;
	}
}
