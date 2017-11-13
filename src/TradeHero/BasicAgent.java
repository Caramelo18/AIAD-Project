package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import com.google.common.collect.*;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class BasicAgent extends TradeAgent {

	public BasicAgent(ContinuousSpace<Object> space, HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues, ArrayList<String> companies){
		super(space, stocks, stockValues);
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void watch(){
		TreeMultiset<TradeAgent> agents = TreeMultiset.create();
		for(Object ob: space.getObjects()){
			TradeAgent agent = (TradeAgent) ob;
			agents.add(agent);
		}
		
		TradeAgent[] agentsList = new TradeAgent[agents.size()];
		agents.toArray(agentsList);
		
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>) context.getProjection("follow network");
		//net.addEdge(this, agentsList[agents.size() - 1]);
		net.addEdge(this, pickFollow(agentsList));
		//pickFollow(agentsList);
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
		
		System.out.println("pick: " +  pick);
		for(AgentRatio agent: agentsRatios){
			if(pick < agent.getRatio()){
				follow = agent.getAgent();
				break;
			}
		}
		
		for(AgentRatio agent: agentsRatios){
			System.out.println(agent.getAgent().getAgentRatio() + " " + agent.getRatio());
		}
		System.out.println("\n");
		return follow;
	}
}
