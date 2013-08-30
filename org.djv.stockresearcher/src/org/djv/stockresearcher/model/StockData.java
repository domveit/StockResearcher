package org.djv.stockresearcher.model;

import java.util.List;
import java.util.Map;

public class StockData {
	
	String symbol;
	String exchange;
	String name;
	String price;
	String marketCap;
	String dividend;
	Double yield;
	String pe;
	String peg;
	Integer streak;
	Integer skipped;
	
	String normDividend;
	Double normYield;
	
	Double dg4 = 0d;
	Double dg8 = 0d;
	double stdDev = 0;
	double wildness = 0d;
	
	int yieldRank = 0;
	int stalwartRank = 0;
	int growthRank = 0;
	
	List<DivData> divData;
	Map<Integer, DivYearData> divYearData;

	Map<String, FinPeriodData> finData;
	
	public StockData() {
		super();
	}
	public StockData(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	public String getNormDividend() {
		return normDividend;
	}
	public void setNormDividend(String normDividend) {
		this.normDividend = normDividend;
	}
	public Double getNormYield() {
		return normYield;
	}
	public void setNormYield(Double normYield) {
		this.normYield = normYield;
	}
	
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public Map<String, FinPeriodData> getFinData() {
		return finData;
	}
	public void setFinData(Map<String, FinPeriodData> finData) {
		this.finData = finData;
	}

	public int getYieldRank() {
		return yieldRank;
	}
	public void setYieldRank(int yieldRank) {
		this.yieldRank = yieldRank;
	}
	public int getStalwartRank() {
		return stalwartRank;
	}
	public void setStalwartRank(int stalwartRank) {
		this.stalwartRank = stalwartRank;
	}
	public int getGrowthRank() {
		return growthRank;
	}
	public void setGrowthRank(int growthRank) {
		this.growthRank = growthRank;
	}
	public double getOverAllRank() {
		return overAllRank;
	}
	public void setOverAllRank(double overAllRank) {
		this.overAllRank = overAllRank;
	}
	double overAllRank = 0;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public List<DivData> getDivData() {
		return divData;
	}
	public void setDivData(List<DivData> divData) {
		this.divData = divData;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(String marketCap) {
		this.marketCap = marketCap;
	}
	public String getDividend() {
		return dividend;
	}
	public void setDividend(String dividend) {
		this.dividend = dividend;
	}
	public Double getYield() {
		return yield;
	}
	public void setYield(Double yield) {
		this.yield = yield;
	}
	public String getPe() {
		return pe;
	}
	public void setPe(String pe) {
		this.pe = pe;
	}
	public String getPeg() {
		return peg;
	}
	public void setPeg(String peg) {
		this.peg = peg;
	}
	public Integer getStreak() {
		return streak;
	}
	public void setStreak(Integer streak) {
		this.streak = streak;
	}
	public Integer getSkipped() {
		return skipped;
	}
	public void setSkipped(Integer skipped) {
		this.skipped = skipped;
	}
	public Map<Integer, DivYearData> getDivYearData() {
		return divYearData;
	}
	public void setDivYearData(Map<Integer, DivYearData> divYearData) {
		this.divYearData = divYearData;
	}
	
	public Double getDg4() {
		return dg4;
	}
	public void setDg4(Double dg4) {
		this.dg4 = dg4;
	}
	public Double getDg8() {
		return dg8;
	}
	public void setDg8(Double dg8) {
		this.dg8 = dg8;
	}

	public double getStdDev() {
		return stdDev;
	}
	public void setStdDev(double stdDev) {
		this.stdDev = stdDev;
	}
	
	public double getWildness() {
		return wildness;
	}
	public void setWildness(double wildness) {
		this.wildness = wildness;
	}
	public String toString(){
		String s = "symbol= " + symbol + "\n";
		s+= "name= " + name + "\n";
		s+= "price= " + price + "\n";
		s+= "marketCap= " + marketCap + "\n";
		s+= "dividend= " + dividend + "\n";
		s+= "yield= " + yield + "\n";
		s+= "pe= " + pe + "\n";
		s+= "peg= " + peg + "\n";
		s+= "streak= " + streak + "\n";
		s+= "skipped= " + skipped + "\n";
		s+= "divGrowthRate5yr= " + dg4 + "\n";
		s+= "divGrowthRate10yr= " + dg8 + "\n";
		s+= "stdDev= " + stdDev + "\n";
		
		if (divYearData != null){
			s+= "===== divData ===== \n";
			for (Integer year : divYearData.keySet()){
				DivYearData dyd = divYearData.get(year);
				s+= dyd.toString();
			};
		}
		
		if (finData != null){
			s+= "===== finData ===== \n";
			for (String fdKey : finData.keySet()){
				FinPeriodData fd = finData.get(fdKey);
				s+= fd.toString();
			};
		}
		return s;
	}
	
	
	
}
