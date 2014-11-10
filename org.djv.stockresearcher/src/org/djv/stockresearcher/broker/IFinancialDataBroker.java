package org.djv.stockresearcher.broker;

import java.util.Map;

import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.FinKeyData;
import org.djv.stockresearcher.model.StockData;

public interface IFinancialDataBroker {

	public FinDataTable getIncomeStatement(StockData sd) throws Exception ;
	public FinDataTable getBalanceSheet(StockData sd) throws Exception ;
	public FinDataTable getCashFlowStatement(StockData sd) throws Exception ;
	public Map<String, FinKeyData> getKeyData(StockData sd) throws Exception;
}