package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class StockData {
	
	Stock stock;
	int streak;
	int skipped;
	
	BigDecimal normDividend;
	BigDecimal normYield;
	
	Double dg4 = 0d;
	Double dg8 = 0d;
	double stdDev = 0;
	double wildness = 0d;
	
	Double eps4 = 0d;
	Double eps8 = 0d;

	boolean ranksCalculated = false;

	int yieldRank = 0;
	int stalwartRank = 0;
	int growthRank = 0;
	int finRank = 0;
	double overAllRank = 0;
	
	List<DivData> divData;
	Map<Integer, DivYearData> divYearData;

	Map<String, FinPeriodData> finData;
	
	public StockData() {
		super();
	}
	
	public StockData(String symbol) {
		super();
		stock = new Stock();
		stock.setSymbol(symbol);
	}
	
	public boolean isRanksCalculated() {
		return ranksCalculated;
	}
	public void setRanksCalculated(boolean ranksCalculated) {
		this.ranksCalculated = ranksCalculated;
	}
	
	public int getFinRank() {
		return finRank;
	}
	public void setFinRank(int finRank) {
		this.finRank = finRank;
	}
	
	public Double getEps4() {
		return eps4;
	}
	public void setEps4(Double eps4) {
		this.eps4 = eps4;
	}
	public Double getEps8() {
		return eps8;
	}
	public void setEps8(Double eps8) {
		this.eps8 = eps8;
	}
	public BigDecimal getNormDividend() {
		return normDividend;
	}

	public void setNormDividend(BigDecimal normDividend) {
		this.normDividend = normDividend;
	}

	public BigDecimal getNormYield() {
		return normYield;
	}

	public void setNormYield(BigDecimal normYield) {
		this.normYield = normYield;
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
	public List<DivData> getDivData() {
		return divData;
	}
	public void setDivData(List<DivData> divData) {
		this.divData = divData;
	}
	public int getStreak() {
		return streak;
	}
	public void setStreak(int streak) {
		this.streak = streak;
	}
	public int getSkipped() {
		return skipped;
	}
	public void setSkipped(int skipped) {
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
	
	
	
	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public String toString(){
		String s = "==== stock ====\n";
		s+= stock+ "\n";

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
