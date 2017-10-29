package TradeHero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class MasterAgent {
	private HashMap<String, TreeMap<String, Double>> stocks;
	private HashMap<String, ArrayList<Double>> stockValues;
	private String[] days;
	
	private int initialCash = 50000;
	private int cash = initialCash;
	private int stockValue = 0;
	private HashMap<String, Integer> currentStock;
	
	private int i = 0;
	
	public MasterAgent(HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues){
		this.stocks = stocks;
		this.stockValues = stockValues;
		this.currentStock = new HashMap<String, Integer>();
		
		System.out.println(stockValues.get("FB"));
		getMaximumProfit("FB");
	}
	
	private void getMaximumProfit(String name){
		ArrayList<Double> values = this.stockValues.get(name);
		
		Double maxProfit = (double) -500;
		int sellAt = 0;
		int buyAt = 0;
		
		for(int i = 0; i < values.size(); i++){
			for(int j = i + 1; j < values.size(); j++){
				double currentValue = values.get(j);
				
				double margin = currentValue - values.get(i);
				if(margin > maxProfit){
					maxProfit = margin;
					sellAt = j;
					buyAt = i;
				}
			}
		}
		
		
		System.out.println(maxProfit);
		System.out.println(buyAt);
		System.out.println(sellAt);
	}
	
	
}
