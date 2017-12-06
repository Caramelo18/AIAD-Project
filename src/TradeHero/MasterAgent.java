package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;


public class MasterAgent extends TradeAgent{
	
	private ArrayList<Trade> trades;
	private int numCompanies = 3;
	
	
	public MasterAgent(ContinuousSpace<Object> space, ArrayList<String> companies){
		super(space);
		
		Random r = new Random();
		trades = new ArrayList<Trade>();
		for(int i = 0; i < numCompanies; i++){
			int j = r.nextInt(companies.size());
			String company = companies.get(j);
			trades.add(getMaximumProfit(company));
		}
	}
	
	private Trade getMaximumProfit(String name){
		ArrayList<Double> values = Market.getStocksListValues(this).get(name);
		
		Double maxProfit = (double) -99999;
		int sellAt = -1;
		int buyAt = -1;
		
		for(int i = 0; i < values.size(); i++){
			for(int j = i + 1; j < values.size() - 2; j++){
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
		System.out.println("Master: " + day);
		for(Trade t: trades){
			if(t.getBuy() == day){
				int num = getNumActionsToBuy(t.getCompany());
				purchaseStock(t.getCompany(), num);
				//System.out.println("Sending Buy");
				informFollowers("BUY", t.getCompany());
			}
			else if(t.getSell() == day){
				sellStock(t.getCompany());
				//System.out.println("Sending Sell");
				informFollowers("SELL", t.getCompany());
			}
		}
		day++;
		//System.out.println(this.getAID() + " " + day + " " + currentStock.size());

		//System.out.println("Followers profit: " + this.followersProfit + " " + this.cash);
		
		//TODO: is this ok?
		RunEnvironment.getInstance().setScheduleTickDelay(20);
	}
	
	private int getNumActionsToBuy(String company){
		double spend = cash/numCompanies;
		numCompanies--;
		double num = spend/getCurrentStockValue(company);
		return  (int)num;
	}
	
	@Override
	protected boolean willHaveProfit(String company){
		Trade t = null;
		
		for(Trade curr: trades){
			if(curr.getCompany() == company){
				t = curr;
				break;
			}
		}
		if(day >= t.getSell())
			return false;
		
		double currentPrice = Market.getStocksListValues(this).get(t.getCompany()).get(day + 1);
		double sellPrice = Market.getStocksListValues(this).get(t.getCompany()).get(t.getSell() + 1) * (1 - this.shareMargin);
		
		if(sellPrice > currentPrice)
			return true;
		
		return false;
	}
}
