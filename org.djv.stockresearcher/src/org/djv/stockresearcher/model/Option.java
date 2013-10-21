package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.Date;

public class Option {
	
	Date expiration;
	BigDecimal strike;
	String symbol;
	BigDecimal last;
	BigDecimal bid;
	BigDecimal ask;
	
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public BigDecimal getStrike() {
		return strike;
	}
	public void setStrike(BigDecimal strike) {
		this.strike = strike;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getLast() {
		return last;
	}
	public void setLast(BigDecimal last) {
		this.last = last;
	}
	public BigDecimal getBid() {
		return bid;
	}
	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}
	public BigDecimal getAsk() {
		return ask;
	}
	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}
	
//	System.err.println("strike=" + strike + " sym=" + sym + " last=" + last + " chg=" + chg + " bid=" + bid + " ask=" + ask);

}
