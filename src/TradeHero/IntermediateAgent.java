package TradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import repast.simphony.space.continuous.ContinuousSpace;

public class IntermediateAgent extends TradeAgent {
	
	private ArrayList<Trade> trades;
	private int numTrades = 10;
	private Random r;
	private ArrayList<String> companies;

	public IntermediateAgent(ContinuousSpace<Object> space, HashMap<String, TreeMap<String, Double>> stocks, HashMap<String, ArrayList<Double>> stockValues, ArrayList<String> companies) {
		super(space, stocks, stockValues);
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
				
				if(buyAt < sellAt && buyAt >= 0 && sellAt <= getNumDays())
					validDays = true;
			}
			String company = companies.get(r.nextInt(numCompanies));
			
			System.out.println(buyAt + " " + sellAt);
			
			Trade t = new Trade(company, buyAt, sellAt);
			trades.add(t);
		}
		
	}

}
