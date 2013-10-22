package org.djv.stockresearcher.model;

import java.math.BigDecimal;

public class Position {
	
	StockData sd;
	BigDecimal shares = new BigDecimal("0.00");
	BigDecimal basis = new BigDecimal("0.00");
	
	public StockData getSd() {
		return sd;
	}
	public void setSd(StockData sd) {
		this.sd = sd;
	}
	public BigDecimal getShares() {
		return shares;
	}
	public void setShares(BigDecimal shares) {
		this.shares = shares;
	}
	public BigDecimal getBasis() {
		return basis;
	}
	public void setBasis(BigDecimal basis) {
		this.basis = basis;
	}
	
	

}
