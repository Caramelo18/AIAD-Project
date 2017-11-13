package TradeHero;

public class AgentRatio {
	private TradeAgent agent;
	private double ratio;
	
	public AgentRatio(TradeAgent agent, double ratio){
		this.agent = agent;
		this.ratio = ratio;
	}
	
	public TradeAgent getAgent(){
		return agent;
	}
	
	public double getRatio(){
		return ratio;
	}
	
	public void setRatio(double ratio){
		this.ratio = ratio;
	}
}
