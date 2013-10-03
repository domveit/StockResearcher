package org.djv.stockresearcher.model;

public class TransactionData {
	
	Transaction transaction;
	StockData stockData;
	
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
	

}
