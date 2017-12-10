package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sajas.core.Agent;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.util.ContextUtils;

public class TradeAgent extends Agent implements Comparable<TradeAgent>{
	protected ContinuousSpace<Object> space ;
	
	protected final int initialCash = 50000;
	protected double followersProfit = 0;
	protected double cash = initialCash;
	protected HashMap<String, Integer> currentStock;
	
	protected ArrayList<TradeAgent> followers;
	protected ArrayList<TradeAgent> followedAgents;

	protected ArrayList<SuggestedTrade> suggestedTrades;
	
	protected final double shareMargin = 0.1;
	
	protected int day = 0;

	public TradeAgent(ContinuousSpace<Object> space){
		this.space = space;
		this.currentStock = new HashMap<String, Integer>();
		this.followers = new ArrayList<TradeAgent>();
		this.suggestedTrades = new ArrayList<SuggestedTrade>();
		this.day = 0;
	}
	
	public double getCurrentCash(){
		return cash;
	}
	
	public double getCurrentValue(){
		return cash + Market.getStockValue(currentStock);
	}
	
	public double getFollowersProfit(){
		return followersProfit;
	}
	
	public int getNumStock(){
		return this.currentStock.size();
	}
	
	protected boolean purchaseStock(String company, int ammount){
		if(ammount == 0)
			return false;
		
		double currentPrice = Market.getCompanyStockValue(company);
		
		double cost = currentPrice * ammount;
		
		if(cost > cash)
			return false;
		
		if(currentStock.containsKey(company)){
			currentStock.put(company, currentStock.get(company) + ammount);
		} else {
			currentStock.put(company, ammount);
		}
		
		cash -= cost;
		
		return true;
	}
	
	protected double sellStock(String company){
		double currentPrice = Market.getCompanyStockValue(company);
		
		double earnings = -1;
		if(currentStock.containsKey(company)){
			int ammount = currentStock.get(company);
			earnings = ammount * currentPrice;
			currentStock.remove(company);
			cash += earnings;

			return earnings;
		}
		
		return -1;
	}
	
	public double getSize(){
		if(this.getCurrentValue() <= 45000)
			return 50;
		
		double margin = (this.getCurrentValue() - this.initialCash) / this.initialCash;
		margin *= 5;
		margin += 1;
		
		return 100*margin;
	}
	
	protected double getCurrentStockValue(String company){
		return Market.getCompanyStockValue(company);
	}
	
	protected double getAgentRatio(){
		return (this.getCurrentValue()/this.initialCash) - 1;		
	}
	

	@Override
	public int compareTo(TradeAgent arg0) {
		if(this.getAgentRatio() > arg0.getAgentRatio())
			return 1;
		else if(this.getAgentRatio() == arg0.getAgentRatio())
			return 0;
		else
			return -1;
	}
	
	public void addFollower(TradeAgent agent){
		followers.add(agent);
		
		for(String company: currentStock.keySet()){
			if(willHaveProfit(company)){
				String message = "BUY " + company;
				sendMessage(agent, message);
			}
		}
	}
	
	public void removeFollower(TradeAgent agent){
		followers.remove(agent);
	}
		
	protected void sendMessage(TradeAgent receiver, String text){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		AID receiverAgent = (AID) receiver.getAID();

		message.addReceiver(receiverAgent);
		message.setContent(text);
		send(message);
	}
	
	protected void informFollowers(String action, String company){
		String message = action + " " + company;
		
		for(TradeAgent agent: followers){
			sendMessage(agent, message);
		}
	}
	
	protected void addSuggestedTrade(TradeAgent agent, String company){
		SuggestedTrade trade = new SuggestedTrade(agent, company, day);
		this.suggestedTrades.add(trade);
	}
	
	protected TradeAgent getAgentByAID(AID aid){
		Context<Object> context = ContextUtils.getContext(this);
		
		for(Object obj: context){
			if(obj.getClass() == Market.class)
				continue;
			TradeAgent agent = (TradeAgent) obj;
			if(agent.getAID().equals(aid))
				return agent;	
		}
		return null;
	}
	
	protected void shareProfit(SuggestedTrade trade, double value){
		if(value <= 0)
			return;
		
		TradeAgent agent = trade.getAgent();
		
		double share = value * this.shareMargin;
		
		this.cash -= share;
		
		agent.followersProfit += share;
		agent.cash += share;
	}
	
	protected boolean willHaveProfit(String company){return true;}
}
