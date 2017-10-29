package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;

public class TradeHeroBuilder implements ContextBuilder<Object> {
	private HashMap<String, TreeMap<String, Double>> stocks;
	private HashMap<String, ArrayList<Double>> stocksValues;
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("TradeHero");
		
		ArrayList<String> companies = new ArrayList<String>();
		companies.add("MSFT"); //Microsoft
		companies.add("FB");   //Facebook
		companies.add("TWTR"); //Twitter
		companies.add("GOOGL");//Google
		companies.add("AMZN"); //Amazon
		companies.add("AAPL"); //Apple
		companies.add("INTC"); //Intel
		
		stocks = new HashMap<String, TreeMap<String, Double>>();
		stocksValues = new HashMap<String, ArrayList<Double>>();
		for(String stockUnit: companies){
			Stock s = new Stock(stockUnit);
			stocks.put(stockUnit, s.getStockDays());
			stocksValues.put(stockUnit, s.getStockValues());
		}
		
		
		MasterAgent m = new MasterAgent(stocks, stocksValues);
		context.add(m);
		
		return null;
	}

}
