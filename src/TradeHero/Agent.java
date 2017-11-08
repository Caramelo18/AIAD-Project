package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import repast.simphony.space.continuous.ContinuousSpace;

public class Agent implements Comparable<Agent>{
	protected ContinuousSpace<Object> space ;
	protected HashMap<String, TreeMap<String, Double>> stocksDailyValues;
	protected HashMap<String, ArrayList<Double>> stocksListValues;
	protected String[] days;
	
	protected int initialCash = 50000;
	protected double cash = initialCash;
	protected HashMap<String, Integer> currentStock;
	
	protected int day = 0;

	public Agent(ContinuousSpace<Object> space, HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues){
		this.space = space;
		this.stocksDailyValues = stocks;
		this.stocksListValues = stockValues;
		this.currentStock = new HashMap<String, Integer>();
		
		getNumDays();
	}
	
	protected int getNumDays(){
		int numDays = 99999;
		for(String key: stocksListValues.keySet()){
			ArrayList<Double> companyStock = stocksListValues.get(key);
			if(companyStock.size() < numDays)
				numDays = companyStock.size();
		}
		return numDays;
	}
	
	public double getStockValue(){
		double value = 0;
		for(String key: currentStock.keySet()){
			int numStock = currentStock.get(key);
			ArrayList<Double> companyStock = stocksListValues.get(key);
			double dailyValue = companyStock.get(day);
			
			value += dailyValue * numStock;
		}
		
		return value;
	}
	
	public double getCurrentCash(){
		return cash;
	}
	
	public double getCurrentValue(){
		return cash + getStockValue();
	}
	
	protected boolean purchaseStock(String company, int ammount){
		ArrayList<Double> companyStock = stocksListValues.get(company);
		
		double currentPrice = companyStock.get(day);
		
		double cost = currentPrice * ammount;
		
		if(cost > cash)
			return false;
		
		if(currentStock.containsKey(company)){
			currentStock.put(company, currentStock.get(company) + ammount);
		} else {
			currentStock.put(company, ammount);
		}
		
		cash -= cost;
		
		return true;
	}
	
	protected boolean sellStock(String company){
		ArrayList<Double> companyStock = stocksListValues.get(company);
		
		double currentPrice = companyStock.get(day);
		
		double earnings = -1;
		if(currentStock.containsKey(company)){
			int ammount = currentStock.get(company);
			earnings = ammount * currentPrice;
			currentStock.remove(company);
			cash += earnings;
			return true;
		}
		
		return false;
	}
	
	public double getSize(){
		double margin = (this.getCurrentValue() - this.initialCash) / this.initialCash;
		margin *= 5;
		margin += 1;
		
		return 100*margin;
	}
	
	protected double getCurrentStockValue(String company){
		ArrayList<Double> companyStock = stocksListValues.get(company);

		return companyStock.get(day);
	}
	
	protected double getAgentRatio(){
		return this.getCurrentValue()/this.initialCash;		
	}
	

	@Override
	public int compareTo(Agent arg0) {
		if(this.getAgentRatio() > arg0.getAgentRatio())
			return 1;
		else if(this.getAgentRatio() == arg0.getAgentRatio())
			return 0;
		else
			return -1;
	}
}
