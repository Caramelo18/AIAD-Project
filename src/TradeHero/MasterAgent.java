package TradeHero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

import java.util.Set;
import java.util.TreeMap;

public class MasterAgent extends Agent{
	
	private ArrayList<Trade> trades;
	
	
	public MasterAgent(ContinuousSpace<Object> space, Grid<Object> grid, HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues, ArrayList<String> companies){
		super(space, grid, stocks, stockValues);
		
		Random r = new Random();
		trades = new ArrayList<Trade>();
		for(int i = 0; i < companies.size() / 3; i++){
			int j = r.nextInt(companies.size());
			String company = companies.get(j);
			trades.add(getMaximumProfit(company));
		}
	}
	
	private Trade getMaximumProfit(String name){
		ArrayList<Double> values = this.stocksListValues.get(name);
		
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

		return new Trade(name, buyAt, sellAt);
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void day() {
		//System.out.println(day + " " + getCurrentCash());
		System.out.println(trades);	
		System.out.println(currentStock);
		for(Trade t: trades){
			if(t.getBuy() == day){
				purchaseStock(t.getCompany(), 30);
				//System.out.println("buy " + t.getCompany());
			}
			else if(t.getSell() == day){
				sellStock(t.getCompany());
				//System.out.println("sell " + t.getCompany());
			}
		}
		day++;
		
		if(day > this.getNumDays()){
			RunEnvironment.getInstance().endRun();
		}
	}
}
