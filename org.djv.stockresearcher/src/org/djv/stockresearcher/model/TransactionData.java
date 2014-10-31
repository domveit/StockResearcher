package org.djv.stockresearcher.model;

import java.math.BigDecimal;

public class TransactionData {
	
	Transaction transaction;
	StockData stockData;
	BigDecimal cost;
	BigDecimal basis;
	BigDecimal basisPerShare;
	BigDecimal cashBalance;
	
	public Transaction getTransaction() {
		return transaction;
	}
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	public StockData getStockData() {
		return stockData;
	}
	public void setStockData(StockData stockData) {
		this.stockData = stockData;
	}
	public BigDecimal getCashBalance() {
		return cashBalance;
	}
	public void setCashBalance(BigDecimal cashBalance) {
		this.cashBalance = cashBalance;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public BigDecimal getBasis() {
		return basis;
	}
	public void setBasis(BigDecimal basis) {
		this.basis = basis;
	}
	public BigDecimal getBasisPerShare() {
		return basisPerShare;
	}
	public void setBasisPerShare(BigDecimal basisPerShare) {
		this.basisPerShare = basisPerShare;
	}
	
}
