package org.djv.stockresearcher;

import org.djv.stockresearcher.broker.MorningstarCSVFinancialDataBroker;
import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class MorningStarFinancialDataBrokerTest {
	
	@Test
	public void test() throws Exception {
		MorningstarCSVFinancialDataBroker b = new MorningstarCSVFinancialDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		FinDataTable is = b.getIncomeStatement(sd);
		System.err.println(is.toString());
	}
	
	@Test
	public void test2() throws Exception {
		MorningstarCSVFinancialDataBroker b = new MorningstarCSVFinancialDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		FinDataTable is = b.getBalanceSheet(sd);
		System.err.println(is.toString());
	}
	
	@Test
	public void test3() throws Exception {
		MorningstarCSVFinancialDataBroker b = new MorningstarCSVFinancialDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		FinDataTable is = b.getCashFlowStatement(sd);
		System.err.println(is.toString());
	}

}
