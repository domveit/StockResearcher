package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Stock {
	
	Date dataDate;
	Date divDataDate;
	Date finDataDate;
	String symbol;
	String exchange;
	BigDecimal price;
	String marketCap;
	BigDecimal dividend;
	BigDecimal yield;
	BigDecimal pe;
	BigDecimal peg;
	BigDecimal yearHigh;
	BigDecimal yearLow;
	BigDecimal oneYrTargetPrice;
	
	public BigDecimal getOneYrTargetPrice() {
		return oneYrTargetPrice;
	}
	public void setOneYrTargetPrice(BigDecimal oneYrTargetPrice) {
		this.oneYrTargetPrice = oneYrTargetPrice;
	}
	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(String marketCap) {
		this.marketCap = marketCap;
	}
	public BigDecimal getDividend() {
		return dividend;
	}
	public void setDividend(BigDecimal dividend) {
		this.dividend = dividend;
	}
	public BigDecimal getYield() {
		return yield;
	}
	public void setYield(BigDecimal yield) {
		this.yield = yield;
	}
	public BigDecimal getPe() {
		return pe;
	}
	public void setPe(BigDecimal pe) {
		this.pe = pe;
	}
	public BigDecimal getPeg() {
		return peg;
	}
	public void setPeg(BigDecimal peg) {
		this.peg = peg;
	}
	public Date getDivDataDate() {
		return divDataDate;
	}
	public void setDivDataDate(Date divDataDate) {
		this.divDataDate = divDataDate;
	}
	public Date getFinDataDate() {
		return finDataDate;
	}
	public void setFinDataDate(Date finDataDate) {
		this.finDataDate = finDataDate;
	}
	public BigDecimal getYearHigh() {
		return yearHigh;
	}
	public void setYearHigh(BigDecimal yearHigh) {
		this.yearHigh = yearHigh;
	}
	public BigDecimal getYearLow() {
		return yearLow;
	}
	public void setYearLow(BigDecimal yearLow) {
		this.yearLow = yearLow;
	}

}