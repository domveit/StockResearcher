package org.djv.stockresearcher.model;

import java.sql.Date;
import java.text.SimpleDateFormat;


public class AnalystEstimates {
	
	private String symbol;
	private Date dataDate;
	
	private AnalystEstimate oneYrEstimate;
	private AnalystEstimate twoYrEstimate;
	
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
	public AnalystEstimate getOneYrEstimate() {
		return oneYrEstimate;
	}
	public void setOneYrEstimate(AnalystEstimate oneYrEstimate) {
		this.oneYrEstimate = oneYrEstimate;
	}
	public AnalystEstimate getTwoYrEstimate() {
		return twoYrEstimate;
	}
	public void setTwoYrEstimate(AnalystEstimate twoYrEstimate) {
		this.twoYrEstimate = twoYrEstimate;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(  "Data as of       " + new SimpleDateFormat("MM/dd/yyyy").format(dataDate));	
		sb.append("\nsymbol " + symbol);
		if (getOneYrEstimate() != null){
			sb.append("\nOne Year   ");
			sb.append("\nYear       " + getOneYrEstimate().getYear());
			sb.append("\nMonth       " + getOneYrEstimate().getMonth());
			sb.append("\nHigh       " + getOneYrEstimate().getHigh());	
			sb.append("\nMean       " + getOneYrEstimate().getMean());	
			sb.append("\nLow        " + getOneYrEstimate().getLow());	
		}
		
		if (getTwoYrEstimate() != null){
			sb.append("\nTwo Year   ");
			sb.append("\nYear       " + getTwoYrEstimate().getYear());
			sb.append("\nMonth       " + getTwoYrEstimate().getMonth());
			sb.append("\nHigh       " + getTwoYrEstimate().getHigh());	
			sb.append("\nMean       " + getTwoYrEstimate().getMean());	
			sb.append("\nLow        " + getTwoYrEstimate().getLow());	
		}
		return sb.toString();
	}
	
}
