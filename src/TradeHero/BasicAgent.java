package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.google.common.collect.*;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class BasicAgent extends Agent {

	public BasicAgent(ContinuousSpace<Object> space, HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues, ArrayList<String> companies){
		super(space, stocks, stockValues);
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void watch(){
		TreeMultiset<Agent> agents = TreeMultiset.create();
		for(Object ob: space.getObjects()){
			Agent agent = (Agent) ob;
			agents.add(agent);
		}
		
		Agent[] agentsList = new Agent[agents.size()];
		agents.toArray(agentsList);
		
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>) context.getProjection("follow network");
		net.addEdge(this, agentsList[agents.size() - 1]);
		

		day++;
	}
}
