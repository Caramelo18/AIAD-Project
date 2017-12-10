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
		Market.day = 0;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 0)
	public void day(){
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
			double dailyValue = Market.getCompanyStockValue(key);
			
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

	public static double getCompanyStockValue(String company) {
		ArrayList<Double> companyValues = Market.stocksListValues.get(company);
		
		if(Market.day >= companyValues.size())
			return companyValues.get(companyValues.size() - 1);
		
		return companyValues.get(Market.day);
	}

	public static double getCompanyStockValue(String company, int day) {
		if(Market.day < day) 
			return (Double) null;
		
		return Market.stocksListValues.get(company).get(day);
	}
}
