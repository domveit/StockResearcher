package org.djv.stockresearcher;

import org.djv.stockresearcher.db.BuffetologyService;
import org.djv.stockresearcher.model.BuffetAnalysis;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class BuffetologyServiceTest {
	
	@Test
	public void test() throws Exception {
		
		BuffetologyService bs = new BuffetologyService();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		BuffetAnalysis ba = bs.buffetize(sd);
		System.err.println(ba.toString());
		
		
	}

}
