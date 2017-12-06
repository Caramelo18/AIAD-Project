package TradeHero;

import repast.simphony.engine.schedule.ScheduledMethod;
import java.util.*;

public class Market {
	private static int day = 0;
	protected static HashMap<String, TreeMap<String, Double>> stocksDailyValues;
	protected static HashMap<String, ArrayList<Double>> stocksListValues;

	public Market(HashMap<String, TreeMap<String, Double>> stocksDailyValues, HashMap<String, ArrayList<Double>> stocksListValues){
		Market.stocksDailyValues = stocksDailyValues;
		Market.stocksListValues = stocksListValues;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void day(){
		System.out.println("Market: " + day);
		++day;
	}
	

	
	protected static int getNumDays(){
		int numDays = 99999;
		for(String key: stocksListValues.keySet()){
			ArrayList<Double> companyStock = stocksListValues.get(key);
			if(companyStock.size() < numDays)
				numDays = companyStock.size();
		}
		return numDays;
	}
	
	public static double getStockValue(HashMap<String, Integer> currentStock){
		double value = 0;
		
		for(String key: currentStock.keySet()){
			int numStock = currentStock.get(key);
			ArrayList<Double> companyStock = stocksListValues.get(key);
			double dailyValue = companyStock.get(day);
			
			value += dailyValue * numStock;
		}
		
		return value;
	}
	
	public static HashMap<String, ArrayList<Double>> getStocksListValues(TradeAgent agent){
		HashMap<String, ArrayList<Double>> ret = null;
		
		if(agent.getClass() != MasterAgent.class)
			return ret;
		else{
			return Market.stocksListValues;
		}
	}
}
