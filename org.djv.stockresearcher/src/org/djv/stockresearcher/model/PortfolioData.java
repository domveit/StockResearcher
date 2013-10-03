package org.djv.stockresearcher.model;

import java.util.List;

public class PortfolioData {
	
	Portfolio portfolio;
	List<Transaction> transactionList;

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}
	
	
	
}
