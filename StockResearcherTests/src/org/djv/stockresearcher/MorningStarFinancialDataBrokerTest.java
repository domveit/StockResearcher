package org.djv.stockresearcher;

import org.djv.stockresearcher.broker.MSFinancialDataBroker;
import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class MorningStarFinancialDataBrokerTest {
	
	@Test
	public void test() throws Exception {
		MSFinancialDataBroker b = new MSFinancialDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		FinDataTable is = b.getIncomeStatement(sd);
		System.err.println(is.toString());
	}
	
	@Test
	public void test2() throws Exception {
		MSFinancialDataBroker b = new MSFinancialDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		FinDataTable is = b.getBalanceSheet(sd);
		System.err.println(is.toString());
	}
	
	@Test
	public void test3() throws Exception {
		MSFinancialDataBroker b = new MSFinancialDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		FinDataTable is = b.getCashFlowStatement(sd);
		System.err.println(is.toString());
	}
	
	@Test
	public void test4() throws Exception {
		MSFinancialDataBroker b = new MSFinancialDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		FinDataTable is = b.getKeyData(sd);
		System.err.println(is.toString());
	}

}
