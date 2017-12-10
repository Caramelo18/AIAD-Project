package TradeHero;

import java.util.ArrayList;
import java.util.Random;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;

public class IntermediateAgent extends TradeAgent {
	
	private ArrayList<Trade> trades;
	private int numTrades = 10;
	private int stockPurchased = 0;
	private Random r;
	private ArrayList<String> companies;

	public IntermediateAgent(ContinuousSpace<Object> space, ArrayList<String> companies) {
		super(space);
		this.trades = new ArrayList<Trade>();
		this.r = new Random();
		this.companies = companies;
		
		this.generateTrades();
	}
	
	private void generateTrades(){
		int numCompanies = companies.size();
		
		for(int i = 0; i < numTrades; i++){
			boolean validDays = false;
			int buyAt = 0;
			int sellAt = 1;
			while(!validDays){
				buyAt = (int) (r.nextGaussian() * 10 + 25);
				sellAt = (int) (r.nextGaussian() * 15 + 85);
				
				if(buyAt < sellAt && buyAt >= 0 && sellAt < (Market.getNumDays() - 2))
					validDays = true;
			}
			String company = companies.get(r.nextInt(numCompanies));
			
			Trade t = new Trade(company, buyAt, sellAt);
			trades.add(t);
		}		
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void day() {
		for(Trade t: trades){
			if(t.getBuy() == day){
				int num = getNumActionsToBuy(t.getCompany());
				purchaseStock(t.getCompany(), num);
				informFollowers("BUY", t.getCompany());
			}
			else if(t.getSell() == day){
				sellStock(t.getCompany());
				informFollowers("SELL", t.getCompany());
			}
		}
		
		day++;
	}
	
	private int getNumActionsToBuy(String company){
		double spend = cash/(numTrades - stockPurchased);
		stockPurchased++;
		double num = spend/getCurrentStockValue(company);
		return  (int)num;
	}

}
