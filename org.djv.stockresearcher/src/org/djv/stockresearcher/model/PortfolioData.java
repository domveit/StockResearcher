package org.djv.stockresearcher.model;

import java.util.List;

public class PortfolioData {
	
	Portfolio portfolio;
	List<TransactionData> transactionList;

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public List<TransactionData> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<TransactionData> transactionList) {
		this.transactionList = transactionList;
	}

	
}
