package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DivData {
	
	String symbol;
	Date paydate;
	BigDecimal dividend;
	BigDecimal normalizedDivided;
	
	public BigDecimal getNormalizedDivided() {
		return normalizedDivided;
	}
	public void setNormalizedDivided(BigDecimal normalizedDivided) {
		this.normalizedDivided = normalizedDivided;
	}
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
		s+= "        normalizedDivided= " + normalizedDivided + "\n";
		return s;
	}
	public Calendar getPayDateCal() {
		Calendar c = Calendar.getInstance();
		c.setTime(paydate);
		return c;
	}
	
	
}
