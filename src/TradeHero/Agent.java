package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Agent {
	protected ContinuousSpace<Object> space ;
	protected Grid<Object> grid ;
	protected HashMap<String, TreeMap<String, Double>> stocks;
	protected HashMap<String, ArrayList<Double>> stockValues;
	protected String[] days;
	
	protected int initialCash = 50000;
	protected double cash = initialCash;
	protected HashMap<String, Integer> currentStock;
	
	protected int day = 0;

	public Agent(ContinuousSpace<Object> space, Grid<Object> grid, HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues){
		this.space = space;
		this.grid = grid;
		this.stocks = stocks;
		this.stockValues = stockValues;
		this.currentStock = new HashMap<String, Integer>();
		
		getNumDays();
		System.out.println(stockValues.get("GOOGL"));
	}
	
	protected int getNumDays(){
		int numDays = 99999;
		for(String key: stockValues.keySet()){
			ArrayList<Double> companyStock = stockValues.get(key);
			if(companyStock.size() < numDays)
				numDays = companyStock.size();
		}
		return numDays;
	}
	
	public double getStockValue(){
		double value = 0;
		for(String key: currentStock.keySet()){
			int numStock = currentStock.get(key);
			ArrayList<Double> companyStock = stockValues.get(key);
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
		ArrayList<Double> companyStock = stockValues.get(company);
		
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
		ArrayList<Double> companyStock = stockValues.get(company);
		
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
	
}
