package StockData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import TradeHero.Stock;

public class StockData implements Serializable{
	
	private HashMap<String, TreeMap<String, Double>> stocksDailyValues;
	private HashMap<String, ArrayList<Double>> stocksListValues;
	private ArrayList<String> companies;
	
	public StockData(){
		File f = new File("StockData.ser");
		
		if(f.exists() && !f.isDirectory()){
			load();
			//System.out.println("Loading");
		} else {
			fetchData();
			save();
			//System.out.println("Saving");
		}
	}
	
	private void fetchData(){
		companies = new ArrayList<String>();
		companies.add("MSFT"); //Microsoft
		companies.add("FB");   //Facebook
		companies.add("TWTR"); //Twitter
		companies.add("GOOGL");//Google
		companies.add("AMZN"); //Amazon
		companies.add("AAPL"); //Apple
		companies.add("INTC"); //Intel
		companies.add("AKAM"); //Akamai
		companies.add("CARS"); //Cars.com
		companies.add("SITO"); //SITO
		companies.add("PYPL"); //Paypal
		companies.add("ORCL"); //Oracle
		
		stocksDailyValues = new HashMap<String, TreeMap<String, Double>>();
		stocksListValues = new HashMap<String, ArrayList<Double>>();
		for(String stockUnit: companies){
			Stock s = new Stock(stockUnit);
			stocksDailyValues.put(stockUnit, s.getStockDays());
			stocksListValues.put(stockUnit, s.getStockValues());
		}
	}
	
	public void save(){
		try {
	         FileOutputStream fileOut = new FileOutputStream("StockData.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(this);
	         out.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in StockData.ser");
	    } catch (IOException i) {
	         i.printStackTrace();
	    }
	}
	
	public void load(){
		StockData s = null;
		try {
	         FileInputStream fileIn = new FileInputStream("StockData.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         s = (StockData) in.readObject();
	         in.close();
	         fileIn.close();
	    } catch (IOException i) {
	         i.printStackTrace();
	         return;
	    } catch (ClassNotFoundException c) {
	         System.out.println("StockData class not found");
	         c.printStackTrace();
	         fetchData();
	         save();
	         return;
	    }
		
		this.stocksDailyValues = s.getStocksDailyValues();
		this.stocksListValues = s.getStocksListValues();
		this.companies = s.getCompanies();
	}
	
	public HashMap<String, TreeMap<String, Double>> getStocksDailyValues(){
		return this.stocksDailyValues;
	}
	
	public HashMap<String, ArrayList<Double>> getStocksListValues(){
		return this.stocksListValues;
	}
	
	public ArrayList<String> getCompanies(){
		return companies;
	}

}
