package org.djv.stockresearcher.model;

import java.math.BigDecimal;

public class TransactionData {
	
	Transaction transaction;
	StockData stockData;
	BigDecimal tranCost;
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
	public BigDecimal getTranCost() {
		return tranCost;
	}
	public void setTranCost(BigDecimal tranCost) {
		this.tranCost = tranCost;
	}
	
}
