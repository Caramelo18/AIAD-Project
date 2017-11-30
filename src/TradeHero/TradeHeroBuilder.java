package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import StockData.StockData;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.ui.RunOptionsModel;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.wrapper.ContainerController;

public class TradeHeroBuilder implements ContextBuilder<Object> {
	private HashMap<String, TreeMap<String, Double>> stocks;
	private HashMap<String, ArrayList<Double>> stocksValues;
	private ContainerController mainContainer;
	private boolean launchedJADE = false;
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("TradeHero");
		
		for(Object agent: context){
			context.remove(agent);
			mainContainer.removeLocalAgent((Agent) agent);
		}
		
		if(!launchedJADE)
			launchJADE();
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder . createContinuousSpaceFactory ( null );
		ContinuousSpace < Object > space = spaceFactory . createContinuousSpace ("space", context , new RandomCartesianAdder < Object >() , new repast . simphony . space . continuous . WrapAroundBorders () ,50 , 50);
				
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("follow network", context, true);
		netBuilder.buildNetwork();

		StockData stockData = new StockData();
		
		stocks = stockData.getStocksDailyValues();
		stocksValues = stockData.getStocksListValues();
		
		Random rand = new Random();
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		int masterAgentCount = params.getInteger("master_agent_count");
		int basicAgentCount = params.getInteger("basic_agent_count");
		
		for(int i = 0; i < masterAgentCount; i++){
			MasterAgent m = new MasterAgent(space, stocks, stocksValues, stockData.getCompanies());
			context.add(m);
			try {
				mainContainer.acceptNewAgent("MasterAgent"+rand.nextInt(Integer.MAX_VALUE), m).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < basicAgentCount; i++){
			BasicAgent b = new BasicAgent(space, stocks, stocksValues, stockData.getCompanies());
			context.add(b);
			try {
				mainContainer.acceptNewAgent("BasicAgent"+rand.nextInt(Integer.MAX_VALUE), b).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
		
		IntermediateAgent i = new IntermediateAgent(space, stocks, stocksValues, stockData.getCompanies());
		
		/*for(int i = 0; i < 10; i++){
			MasterAgent m = new MasterAgent(space, stocks, stocksValues, stockData.getCompanies());
			context.add(m);
			BasicAgent b = new BasicAgent(space, stocks, stocksValues, stockData.getCompanies());
			context.add(b);
			try {
				mainContainer.acceptNewAgent("BasicAgent"+rand.nextInt(Integer.MAX_VALUE), b).start();
				mainContainer.acceptNewAgent("MasterAgent"+rand.nextInt(Integer.MAX_VALUE), m).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}*/
		
		//RunEnvironment.getInstance().setScheduleTickDelay(20);
		RunOptionsModel options = new RunOptionsModel();
		options.setScheduleTickDelay(200);
		options.setStopAt(101);
		options.simStarted();
		
		
		return context;
	}
	
	public void launchJADE(){		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		launchedJADE = true;
	}
	
}
