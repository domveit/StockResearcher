package org.djv.stockresearcher.broker;

import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.StockData;

public interface IFinancialDataBroker {

	public FinDataTable getKeyData(StockData sd) throws Exception;
	public FinDataTable getIncomeStatement(StockData sd) throws Exception ;
	public FinDataTable getBalanceSheet(StockData sd) throws Exception ;
	public FinDataTable getCashFlowStatement(StockData sd) throws Exception ;
	
}