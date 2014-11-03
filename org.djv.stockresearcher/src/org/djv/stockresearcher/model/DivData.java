package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DivData {
	
	String symbol;
	Date paydate;
	BigDecimal dividend;
	Date adjustedDate;
	BigDecimal adjustedDividend;
	
	public BigDecimal getDividend() {
		return dividend;
	}
	public void setDividend(BigDecimal dividend) {
		this.dividend = dividend;
	}
	
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
	public String toString(){
		String s = "        date= " + new SimpleDateFormat("MM/dd/yyyy").format(paydate) + "\n";
		s+= "        dividend= " + dividend + "\n";
		return s;
	}
	public Calendar getPayDateCal() {
		Calendar c = Calendar.getInstance();
		c.setTime(paydate);
		return c;
	}
	public Calendar getAdjustedDateCal() {
		if (adjustedDate == null){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(adjustedDate);
		return c;
	}
	public Date getAdjustedDate() {
		return adjustedDate;
	}
	public void setAdjustedDate(Date adjustedDate) {
		this.adjustedDate = adjustedDate;
	}
	public BigDecimal getAdjustedDividend() {
		return adjustedDividend;
	}
	public void setAdjustedDividend(BigDecimal adjustedDividend) {
		this.adjustedDividend = adjustedDividend;
	}
	
	
}
