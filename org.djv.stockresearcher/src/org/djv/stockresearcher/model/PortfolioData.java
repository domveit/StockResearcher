package org.djv.stockresearcher.model;

import java.util.List;
import java.util.Map;

public class PortfolioData {
	
	Portfolio portfolio;
	List<TransactionData> transactionList;
	Map<Integer, Map<String, Position>> positionMap; 

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public Map<Integer, Map<String, Position>> getPositionMap() {
		return positionMap;
	}

	public void setPositionMap(Map<Integer, Map<String, Position>> positionMap) {
		this.positionMap = positionMap;
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
