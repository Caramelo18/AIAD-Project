package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import StockData.StockData;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class TradeHeroBuilder implements ContextBuilder<Object> {
	private HashMap<String, TreeMap<String, Double>> stocks;
	private HashMap<String, ArrayList<Double>> stocksValues;
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("TradeHero");
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder . createContinuousSpaceFactory ( null );
		ContinuousSpace < Object > space = spaceFactory . createContinuousSpace ("space", context , new RandomCartesianAdder < Object >() , new repast . simphony . space . continuous . WrapAroundBorders () ,50 , 50);
		
		GridFactory gridFactory = GridFactoryFinder . createGridFactory ( null );
		// Correct import : import repast . simphony . space . grid . WrapAroundBorders ;
		Grid < Object > grid = gridFactory . createGrid ("grid", context ,
		new GridBuilderParameters < Object >( new WrapAroundBorders () , new SimpleGridAdder < Object >() , true , 50 , 50));
		
		
		StockData stockData = new StockData();
		
		stocks = stockData.getStocksDailyValues();
		stocksValues = stockData.getStocksListValues();
		
		for(int i = 0; i < 10; i++){
			MasterAgent m = new MasterAgent(space, grid, stocks, stocksValues, stockData.getCompanies());
			context.add(m);
		}
		
		
		
		for ( Object obj : context ) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo (obj, (int) pt.getX(), (int)pt.getY());
		}
		
		return context;
	}

}
