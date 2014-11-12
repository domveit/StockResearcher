package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class AnalystRatings {

	private String symbol;
	private Date dataDate;
	private BigDecimal fiveYearGrowthForcast;
	private BigDecimal averageRating;
	private int strongBuyRatings;
	private int buyRatings;
	private int holdRatings;
	private int sellRatings;
	private int strongSellRatings;
	
	public int getTotalAnalysts(){
		int tot = strongBuyRatings + buyRatings+ holdRatings+ sellRatings+ strongSellRatings;
		return tot;
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	public BigDecimal getFiveYearGrowthForcast() {
		return fiveYearGrowthForcast;
	}
	public void setFiveYearGrowthForcast(BigDecimal fiveYearGrowthForcast) {
		this.fiveYearGrowthForcast = fiveYearGrowthForcast;
	}
	public BigDecimal getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(BigDecimal averageRating) {
		this.averageRating = averageRating;
	}
	public Integer getStrongBuyRatings() {
		return strongBuyRatings;
	}
	public void setStrongBuyRatings(Integer strongBuyRatings) {
		this.strongBuyRatings = strongBuyRatings;
	}
	public Integer getBuyRatings() {
		return buyRatings;
	}
	public void setBuyRatings(Integer buyRatings) {
		this.buyRatings = buyRatings;
	}
	public Integer getHoldRatings() {
		return holdRatings;
	}
	public void setHoldRatings(Integer holdRatings) {
		this.holdRatings = holdRatings;
	}
	public Integer getSellRatings() {
		return sellRatings;
	}
	public void setSellRatings(Integer sellRatings) {
		this.sellRatings = sellRatings;
	}
	public Integer getStrongSellRatings() {
		return strongSellRatings;
	}
	public void setStrongSellRatings(Integer strongSellRatings) {
		this.strongSellRatings = strongSellRatings;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(  "Data as of       " + new SimpleDateFormat("MM/dd/yyyy").format(dataDate));	
		sb.append("\nfive Year growth " + fiveYearGrowthForcast);
		sb.append("\nAverage Rating   " + averageRating);
		sb.append("\nStrong Buy       " + strongBuyRatings);
		sb.append("\nBuy              " + buyRatings);	
		sb.append("\nHold             " + holdRatings);	
		sb.append("\nSell             " + sellRatings);		
		sb.append("\nStrong Sell      " + strongSellRatings);
		return sb.toString();
	}


	//Data as of 10/21/2014	
	//Five-Year Growth Forecast		Industry Avg
	//11.49%		—
	//Average Rating	Last Month		Industry Avg	S&P 500 Avg
	//4.71	—		—	—
	//Rating Scale: 5=Buy, 1=Sell
	//Total Number of Analysts:
	//Buy	12		
	//Outperform	0		
	//Hold	2		
	//Underperform	0		
	//Sell	0	

}
