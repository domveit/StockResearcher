package org.djv.stockresearcher.model;

public class Stock {
	
	private String symbol;
	private Long sector;
	private Long industry;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Long getSector() {
		return sector;
	}
	public void setSector(Long sector) {
		this.sector = sector;
	}
	public Long getIndustry() {
		return industry;
	}
	public void setIndustry(Long industry) {
		this.industry = industry;
	}
	public Stock(String symbol, Long sector, Long industry) {
		super();
		this.symbol = symbol;
		this.sector = sector;
		this.industry = industry;
	}
	

}