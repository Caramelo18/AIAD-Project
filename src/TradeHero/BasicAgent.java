package TradeHero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

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
	private HashMap<TradeAgent, Integer> followHistory;
	private int stuckAgents = 0;
	private int badAgents = 0;
	
	public BasicAgent(ContinuousSpace<Object> space, ArrayList<String> companies){
		super(space);
		followedAgents = new ArrayList<TradeAgent>();
		followHistory = new HashMap<TradeAgent, Integer>();
	}
	
	public int getStuckAgents(){
		return stuckAgents;
	}
	
	public int getBadAgents(){
		return badAgents;
	}
	
	public int getBasicAgentsFollowed(){
		int num = 0;
		
		for(TradeAgent agent: followedAgents){
			if(agent.getClass().equals(BasicAgent.class))
				++num;
		}
		
		return num;
	}
	
	public int getIntermediateAgentsFollowed(){
		int num = 0;
		
		for(TradeAgent agent: followedAgents){
			if(agent.getClass().equals(IntermediateAgent.class))
				++num;
		}
		
		return num;
	}
	
	public int getMasterAgentsFollowed(){
		int num = 0;
		
		for(TradeAgent agent: followedAgents){
			if(agent.getClass().equals(MasterAgent.class))
				++num;
		}
		
		return num;
	}
	
	public int getNumAgentsFollowed(){
		return followedAgents.size();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void watch(){
		receiveMessages();

		stopFollowingAgents();

		if(agentsToFollow <= 0 || !isFollowing())
		{
			day++;
			return;
		} 
		
		followAgent();
			
		day++;
	}
	
	public void followAgent(){
		TreeMultiset<TradeAgent> agents = TreeMultiset.create();
		for(Object ob: space.getObjects()){
			if(ob.getClass() == Market.class)
				continue;
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
				followHistory.put(follow, day);
				agentsToFollow--;
			}
		}		
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


		for(AgentRatio agent: agentsRatios){
			if(pick < agent.getRatio()){
				follow = agent.getAgent();
				break;
			}
		}

		return follow;
	}
	
	public boolean isFollowing(){
		Random rand = new Random();
		double val = rand.nextGaussian()*25 + 50;

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

			if(action.equals("BUY")){
				int numStock = getNumActionsToBuy(company);
				this.purchaseStock(company, numStock);
				this.informFollowers("BUY", company);
				this.addSuggestedTrade(sender, company);
			}
			else if (action.equals("SELL")){
				double value = this.sellStock(company);
				this.informFollowers("SELL", company);
				
				for(SuggestedTrade trade: suggestedTrades){
					if(trade.getCompany().equals(company) && trade.getAgent().equals(sender)){
						this.shareProfit(trade, value);
						suggestedTrades.remove(trade);
						break;
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
		if(day >= (Market.getNumDays() - 2))
			return false;
		
		return true;
	}
	
	protected void stopFollowingAgents(){
		if(willReconsiderStuckAgents() && followedAgents.size() > 0)
			reconsiderStuckAgents();
		
		if(willReconsiderBadAgents() && followedAgents.size() > 0)
			reconsiderBadAgents();
				
		
	}
	
	protected boolean willReconsiderStuckAgents(){
		if(day < (Market.getNumDays() / 3) || day > (Market.getNumDays() - 15))
			return false;
				
		return true;
	}
	
	protected boolean willReconsiderBadAgents(){
		Random rand = new Random();
		double val1 = rand.nextGaussian()*10 + 60;
		double val2 = rand.nextGaussian()*10 + 60;
		
		if(val2 < val1){
			double copy = val1;
			val1 = val2;
			val2 = copy;
		}
		
		if(day >= val1 && day <= val2){
			val1 = rand.nextDouble();
			
			if(val1 > unfollowProbability)
				return false;
			
			return true;
		}
		
		return false;
	}
	
	private void reconsiderStuckAgents(){
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
		
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>) context.getProjection("follow network");
		
		for(AgentTrades trades: agentTrades){
			int followDay = followHistory.get(trades.getAgent());
			int daysFollowing = day - followDay;
			if(trades.getCurrentProfit() == 0 && daysFollowing > 7){
				TradeAgent badAgent = trades.getAgent();
				net.removeEdge(net.getEdge(this, badAgent));
				
				int soldActions = 0;
				for(SuggestedTrade trade: suggestedTrades){
					if(trade.getAgent() == badAgent){
						this.sellStock(trade.getCompany());
						soldActions++;
					}
				}
				
				followedAgents.remove(badAgent);
				badAgent.removeFollower(this);
				followHistory.remove(badAgent);
				
				agentsToFollow++;
				numCompanies += soldActions;
				stuckAgents++;
			}
		}
	}
	
	private void reconsiderBadAgents(){
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
		
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>) context.getProjection("follow network");
		
		AgentTrades badAgentTrades = agentTrades.get(0);
				
		TradeAgent badAgent = badAgentTrades.getAgent();
		net.removeEdge(net.getEdge(this, badAgent));
		
		int soldActions = 0;
		for(SuggestedTrade trade: suggestedTrades){
			if(trade.getAgent() == badAgent){
				double cash = this.sellStock(trade.getCompany());
				this.shareProfit(trade, cash);
				soldActions++;
			}
		}
		
		followedAgents.remove(badAgent);
		badAgent.removeFollower(this);
		followHistory.remove(badAgent);
		
		agentsToFollow++;
		badAgents++;
		numCompanies += soldActions;
				
	}
}
