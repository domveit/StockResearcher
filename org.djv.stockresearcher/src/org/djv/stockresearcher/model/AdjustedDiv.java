package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;

public class AdjustedDiv {
	
	String symbol;
	Date paydate;
	Date adjustedDate;
	BigDecimal adjustedDiv;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getPaydate() {
		return paydate;
	}
	public void setPaydate(Date paydate) {
		this.paydate = paydate;
	}
	
	public Calendar getPayDateCal() {
		Calendar c = Calendar.getInstance();
		c.setTime(paydate);
		return c;
	}
	public Date getAdjustedDate() {
		return adjustedDate;
	}
	public void setAdjustedDate(Date adjustedDate) {
		this.adjustedDate = adjustedDate;
	}
	public BigDecimal getAdjustedDiv() {
		return adjustedDiv;
	}
	public void setAdjustedDiv(BigDecimal adjustedDiv) {
		this.adjustedDiv = adjustedDiv;
	}
	
	
}
