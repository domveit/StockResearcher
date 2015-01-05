package org.djv.stockresearcher;

import org.djv.stockresearcher.db.BuffetologyService;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.BuffetAnalysis;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class BuffetologyServiceTest {
	
	@Test
	public void test() throws Exception {
		
		StockDB db = new StockDB("stockDBTest");
		
		BuffetologyService bs = new BuffetologyService();
		Stock s = new Stock();
		s.setSymbol("DG");
		s.setExchange("NYSE");
		s.setMarketCap("387.6B");
		StockData sd = new StockData(s);
		
//		MSFinancialDataBroker msbrok = new MSFinancialDataBroker();
//		
//		sd.setIncomeStatement(msbrok.getIncomeStatement(sd));
//		sd.setBalanceSheet(msbrok.getBalanceSheet(sd));
		
		BuffetAnalysis ba = bs.buffetize(sd);
		System.err.println(ba.toString());
	}
	
	@Test
	public void test1() throws Exception {
		
		
		
		BuffetologyService bs = new BuffetologyService();
		Stock s = new Stock();
		s.setSymbol("MWE");
		s.setExchange("NYSE");
		s.setMarketCap("387.6B");
		StockData sd = new StockData(s);
		BuffetAnalysis ba = bs.buffetize(sd);
		System.err.println(ba.toString());
	}

}
