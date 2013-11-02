package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Stock {
	
//	Integer industryId;
	Date dataDate;
	Date divDataDate;
	Date finDataDate;
	String symbol;
	String exchange;
//	String name;
	BigDecimal price;
	String marketCap;
	BigDecimal dividend;
	BigDecimal yield;
	BigDecimal pe;
	BigDecimal peg;
	
//	public Integer getIndustryId() {
//		return industryId;
//	}
//	public void setIndustryId(Integer industryId) {
//		this.industryId = industryId;
//	}
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
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
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

}