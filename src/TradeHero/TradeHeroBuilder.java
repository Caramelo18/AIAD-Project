package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import StockData.StockData;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;

public class TradeHeroBuilder implements ContextBuilder<Object> {
	private HashMap<String, TreeMap<String, Double>> stocks;
	private HashMap<String, ArrayList<Double>> stocksValues;
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("TradeHero");
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder . createContinuousSpaceFactory ( null );
		ContinuousSpace < Object > space = spaceFactory . createContinuousSpace ("space", context , new RandomCartesianAdder < Object >() , new repast . simphony . space . continuous . WrapAroundBorders () ,50 , 50);
				
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("follow network", context, true);
		netBuilder.buildNetwork();

		StockData stockData = new StockData();
		
		stocks = stockData.getStocksDailyValues();
		stocksValues = stockData.getStocksListValues();
		
		for(int i = 0; i < 10; i++){
			MasterAgent m = new MasterAgent(space, stocks, stocksValues, stockData.getCompanies());
			context.add(m);
			BasicAgent b = new BasicAgent(space, stocks, stocksValues, stockData.getCompanies());
			context.add(b);
		}
				
		return context;
	}

}
