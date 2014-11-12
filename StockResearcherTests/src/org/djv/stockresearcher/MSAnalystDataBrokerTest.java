package org.djv.stockresearcher;

import org.djv.stockresearcher.broker.MSAnalystDataBroker;
import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class MSAnalystDataBrokerTest {
	
	@Test
	public void test() throws Exception {
		MSAnalystDataBroker b = new MSAnalystDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		AnalystRatings ar = b.getAnalystRatings(sd);
		System.err.println(ar);
	}

}
