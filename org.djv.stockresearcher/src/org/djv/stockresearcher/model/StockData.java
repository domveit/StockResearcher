package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class StockData {
	
	String symbol;
	Stock stock;
	StockIndustry stockIndustry;
	SectorIndustry sectorIndustry;

	Integer streak;
	Integer skipped;
	
	BigDecimal normDividend;
	BigDecimal normYield;
	
	Double dg5 = 0d;
	Double dg10 = 0d;
	Double stdDev = 0d;
	Double wildness = 0d;
	
	BigDecimal yrHighDiff;
	BigDecimal oytUpside;
	
	BigDecimal chowder;
	
	Boolean ranksCalculated = false;

	Integer yieldRank = 0;
	Integer stalwartRank = 0;
	Integer growthRank = 0;
	Integer finRank = 0;
	Integer valueRank = 0;
	Integer analRank = 0;
	Double overAllRank = 0d;
	
	String watched = "";
	
	List<DivData> divData;
	Map<Integer, DivYearData> divYearData;

	AnalystRatings analystRatings;
	
//	FinDataTable keyRatios;
//	FinDataTable incomeStatement;
//	FinDataTable balanceSheet;
//	FinDataTable cashFlowStatement;
	
	int buffetscore;
	
//	public FinDataTable getIncomeStatement() {
//		return incomeStatement;
//	}
//
//	public void setIncomeStatement(FinDataTable incomeStatement) {
//		this.incomeStatement = incomeStatement;
//	}
//
//	public FinDataTable getBalanceSheet() {
//		return balanceSheet;
//	}
//
//	public void setBalanceSheet(FinDataTable balanceSheet) {
//		this.balanceSheet = balanceSheet;
//	}
//
//	public FinDataTable getCashFlowStatement() {
//		return cashFlowStatement;
//	}
//
//	public void setCashFlowStatement(FinDataTable cashFlowStatement) {
//		this.cashFlowStatement = cashFlowStatement;
//	}
//
//	public FinDataTable getKeyRatios() {
//		return keyRatios;
//	}
//
//	public void setKeyRatios(FinDataTable keyRatios) {
//		this.keyRatios = keyRatios;
//	}

	public int getBuffetscore() {
		return buffetscore;
	}

	public void setBuffetscore(int buffetscore) {
		this.buffetscore = buffetscore;
	}

	public StockData() {
		super();
	}
	
	public StockData(String symbol) {
		super();
		stock = new Stock();
		stock.setSymbol(symbol);
		this.symbol = symbol;
	}
	
	public StockData(Stock s) {
		super();
		stock = s;
		this.symbol = s.getSymbol();
	}
	
	public BigDecimal getChowder() {
		return chowder;
	}

	public void setChowder(BigDecimal chowder) {
		this.chowder = chowder;
	}

	public Double getStdDev() {
		return stdDev;
	}

	public void setStdDev(Double stdDev) {
		this.stdDev = stdDev;
	}

	public Double getWildness() {
		return wildness;
	}

	public void setWildness(Double wildness) {
		this.wildness = wildness;
	}

	public BigDecimal getOytUpside() {
		return oytUpside;
	}

	public void setOytUpside(BigDecimal oytUpside) {
		this.oytUpside = oytUpside;
	}
	
	public Integer getStreak() {
		return streak;
	}

	public Boolean getRanksCalculated() {
		return ranksCalculated;
	}

	public void setRanksCalculated(Boolean ranksCalculated) {
		this.ranksCalculated = ranksCalculated;
	}

	public Integer getYieldRank() {
		return yieldRank;
	}

	public Integer getAnalRank() {
		return analRank;
	}

	public void setAnalRank(Integer analRank) {
		this.analRank = analRank;
	}

	public void setYieldRank(Integer yieldRank) {
		this.yieldRank = yieldRank;
	}

	public Integer getStalwartRank() {
		return stalwartRank;
	}

	public void setStalwartRank(Integer stalwartRank) {
		this.stalwartRank = stalwartRank;
	}

	public Integer getGrowthRank() {
		return growthRank;
	}

	public void setGrowthRank(Integer growthRank) {
		this.growthRank = growthRank;
	}

	public Integer getFinRank() {
		return finRank;
	}

	public void setFinRank(Integer finRank) {
		this.finRank = finRank;
	}

	public Integer getValueRank() {
		return valueRank;
	}

	public void setValueRank(Integer valueRank) {
		this.valueRank = valueRank;
	}

	public Double getOverAllRank() {
		return overAllRank;
	}

	public void setOverAllRank(Double overAllRank) {
		this.overAllRank = overAllRank;
	}

	public String getWatched() {
		return watched;
	}

	public void setWatched(String watched) {
		this.watched = watched;
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

	public BigDecimal getYrHighDiff() {
		return yrHighDiff;
	}

	public void setYrHighDiff(BigDecimal yrHighDiff) {
		this.yrHighDiff = yrHighDiff;
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

	public List<DivData> getDivData() {
		return divData;
	}
	public void setDivData(List<DivData> divData) {
		this.divData = divData;
	}
	public Map<Integer, DivYearData> getDivYearData() {
		return divYearData;
	}
	public void setDivYearData(Map<Integer, DivYearData> divYearData) {
		this.divYearData = divYearData;
	}
	
	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public StockIndustry getStockIndustry() {
		return stockIndustry;
	}

	public void setStockIndustry(StockIndustry stockIndustry) {
		this.stockIndustry = stockIndustry;
	}

	public SectorIndustry getSectorIndustry() {
		return sectorIndustry;
	}

	public void setSectorIndustry(SectorIndustry sectorIndustry) {
		this.sectorIndustry = sectorIndustry;
	}

	public Double getDg5() {
		return dg5;
	}

	public void setDg5(Double dg5) {
		this.dg5 = dg5;
	}

	public Double getDg10() {
		return dg10;
	}

	public void setDg10(Double dg10) {
		this.dg10 = dg10;
	}

	public String toString(){
		String s = "==== stock ====\n";
		s+= stock+ "\n";

		s+= "streak= " + streak + "\n";
		s+= "skipped= " + skipped + "\n";
		s+= "divGrowthRate5yr= " + dg5 + "\n";
		s+= "divGrowthRate10yr= " + dg10 + "\n";
		
		if (divYearData != null){
			s+= "===== divData ===== \n";
			for (Integer year : divYearData.keySet()){
				DivYearData dyd = divYearData.get(year);
				s+= dyd.toString();
			};
		}
		
		return s;
	}

	public AnalystRatings getAnalystRatings() {
		return analystRatings;
	}

	public void setAnalystRatings(AnalystRatings analystRatings) {
		this.analystRatings = analystRatings;
	}
	
	
	
}
