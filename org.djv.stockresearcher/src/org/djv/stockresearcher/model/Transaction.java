package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Transaction {
	
	public static final String ACTION_BUY = "B";
	public static final String ACTION_SELL = "S";
	public static final String ACTION_CASH_DEPOSIT = "D";
	public static final String ACTION_CASH_WITHDRAWAL = "W";
	public static final String ACTION_DIVIDEND = "V";
	public static final String ACTION_DIVIDEND_REINVEST = "R";
	
	Integer id;
	Integer portId;
	String action;
	String symbol;
	BigDecimal shares;
	BigDecimal price;
	BigDecimal commission;
	Date tranDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPortId() {
		return portId;
	}
	public void setPortId(Integer portId) {
		this.portId = portId;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getShares() {
		return shares;
	}
	public void setShares(BigDecimal shares) {
		this.shares = shares;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Date getTranDate() {
		return tranDate;
	}
	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}
	public BigDecimal getCommission() {
		return commission;
	}
	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}
	public String getActionText(){
		switch(action){
			case ACTION_BUY: return "Buy";
			case ACTION_SELL: return "Sell";
			case ACTION_CASH_DEPOSIT: return "Cash Deposit";
			case ACTION_CASH_WITHDRAWAL: return "Cash Withdrawal";
			case ACTION_DIVIDEND: return "Dividend";
			case ACTION_DIVIDEND_REINVEST: return "Dividend Reinvest";
			default: return "???";
		}
	}
}
