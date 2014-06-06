package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.Date;

public class Lot {
	
	Date date;
	BigDecimal shares;
	BigDecimal basis;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
