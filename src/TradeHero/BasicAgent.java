package TradeHero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.*;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class BasicAgent extends TradeAgent {
	
	private int agentsToFollow = 3;
	private int numCompanies = agentsToFollow * 3;
	private final double unfollowProbability = 0.05;	
	
	public BasicAgent(ContinuousSpace<Object> space, HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues, ArrayList<String> companies){
		super(space, stocks, stockValues);
		followedAgents = new ArrayList<TradeAgent>();
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void watch(){
		receiveMessages();

		//for(String company: currentStock.keySet()){
			//System.out.println(this.getAID() + " " + day + " " + currentStock.size());
		//}
		if (willUnfollow()){
			stopFollowingAgent();
		}
		if(agentsToFollow <= 0 || !isFollowing())
		{
			day++;
			return;
		} 
		
		TreeMultiset<TradeAgent> agents = TreeMultiset.create();
		for(Object ob: space.getObjects()){
			TradeAgent agent = (TradeAgent) ob;
			agents.add(agent);
		}
		
		TradeAgent[] agentsList = new TradeAgent[agents.size()];
		agents.toArray(agentsList);
		
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>) context.getProjection("follow network");

		TradeAgent follow = pickFollow(agentsList);
		if(follow != null && follow != this){
			if(!followedAgents.contains(follow) && !followers.contains(follow)){
				net.addEdge(this, follow);
				followedAgents.add(follow);
				follow.addFollower(this);
				agentsToFollow--;
			}
		}	
		day++;
	}
	
	public TradeAgent pickFollow(TradeAgent[] agents){
		TradeAgent follow = null;
		ArrayList<AgentRatio> agentsRatios = new ArrayList<AgentRatio>(); 
		for(int i = 0; i < agents.length; i++){
			if(agents[i].getAgentRatio() > 0){
				agentsRatios.add(new AgentRatio(agents[i], agents[i].getAgentRatio()));	
			}
		}
		
		for(int i = 1; i < agentsRatios.size(); i++){
			double prevRatio = agentsRatios.get(i-1).getRatio();
			double ratio = agentsRatios.get(i).getRatio();
			agentsRatios.get(i).setRatio(ratio + prevRatio);
		}
		
		double max = 0;
		if(agentsRatios.size() > 1)
			max = agentsRatios.get(agentsRatios.size() - 1).getRatio();
		Random rand = new Random();
		double pick = rand.nextDouble()*max;
		
		//System.out.println("pick: " +  pick);
		for(AgentRatio agent: agentsRatios){
			if(pick < agent.getRatio()){
				follow = agent.getAgent();
				break;
			}
		}
		
		/*for(AgentRatio agent: agentsRatios){
			System.out.println(agent.getAgent().getAgentRatio() + " " + agent.getRatio());
		}
		System.out.println("\n");*/
		return follow;
	}
	
	public boolean isFollowing(){
		Random rand = new Random();
		double val = rand.nextGaussian()*25 + 50;
		//System.out.println("Random: " + val);
		if(day >= val)
			return true;
		else
			return false;
	}
	
	protected void receiveMessages(){
		ACLMessage message = receive();
		
		while(message != null){
			String content = message.getContent();
			
			AID senderAID = message.getSender();
			TradeAgent sender = getAgentByAID(senderAID);
			
			String[] messageParts = content.split(" ");
			String action = messageParts[0];
			String company = messageParts[1];
			//System.out.println("RECEIVE " + content + " " + this.getAID() + " " + day);
			if(action.equals("BUY")){
				int numStock = getNumActionsToBuy(company);
				this.purchaseStock(company, numStock);
				this.informFollowers("BUY", company);
				this.addSuggestedTrade(sender, company);
			}
			else if (action.equals("SELL")){
				double value = this.sellStock(company);
				this.informFollowers("SELL", company);
				//System.out.println("Selling " + company);
				
				for(SuggestedTrade trade: suggestedTrades){
					if(trade.getCompany().equals(company)){
						this.shareProfit(trade, value);
					}
				}
			}
			message = receive();
		}
	}
	
	protected int getNumActionsToBuy(String company){
		double spend = cash/numCompanies;
		numCompanies--;
		double num = spend/getCurrentStockValue(company);
		return  (int)num;
	}
	
	@Override
	protected boolean willHaveProfit(String company){
		return true;
	}
	
	protected void stopFollowingAgent(){
		if(!willUnfollow() && followedAgents.size() == 0)
			return;
		
		ArrayList<AgentTrades> agentTrades = new ArrayList<AgentTrades>();
		for(TradeAgent agent: followedAgents){
			agentTrades.add(new AgentTrades(agent));
		}
		
		for(SuggestedTrade suggestedTrade: suggestedTrades){
			for(AgentTrades agents: agentTrades){
				if(agents.getAgent().equals(suggestedTrade.getAgent())){
					agents.addTrade(suggestedTrade);
				}
			}
		}
		
		Collections.sort(agentTrades);
		/*
		for(AgentTrades agent: agentTrades){
			System.out.println(agent.getAgent().getAID() + " " + agent.getCurrentProfit());
		}*/
		
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>) context.getProjection("follow network");
		
		TradeAgent worstFollowed = agentTrades.get(0).getAgent();
		net.removeEdge(net.getEdge(this, worstFollowed));
		
		int soldActions = 0;
		for(SuggestedTrade trade: suggestedTrades){
			if(trade.getAgent() == worstFollowed){
				this.sellStock(trade.getCompany());
				soldActions++;
			}
		}
		followedAgents.remove(worstFollowed);
		worstFollowed.removeFollower(this);
		
		agentsToFollow++;
		numCompanies += soldActions;
		
		
	}
	
	protected boolean willUnfollow(){
		Random rand = new Random();
		double val = rand.nextGaussian()*25 + 60;

		if(day < val)
			return false;
		
		val = rand.nextDouble();
		
		if(val < unfollowProbability)
			return true;
		
		return false;
	}
}
