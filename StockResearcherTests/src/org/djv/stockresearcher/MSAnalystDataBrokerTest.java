package org.djv.stockresearcher;

import java.math.BigDecimal;
import java.util.List;

import org.djv.stockresearcher.broker.MSAnalystDataBroker;
import org.djv.stockresearcher.model.AnalystEstimates;
import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.HistPrice;
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
	
	@Test
	public void test1() throws Exception {
		MSAnalystDataBroker b = new MSAnalystDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		AnalystEstimates e = b.getAnalystEstimates(sd);
		System.err.println(e);
	}
	
	@Test
	public void test2() throws Exception {
		MSAnalystDataBroker b = new MSAnalystDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		BigDecimal bd = b.getAveragePE(sd, 7);
		System.err.println(bd);
	}
	
	@Test
	public void test3() throws Exception {
		MSAnalystDataBroker b = new MSAnalystDataBroker();
		Stock s = new Stock();
		s.setSymbol("MSFT");
		s.setExchange("NasdaqNM");
		StockData sd = new StockData(s);
		List<HistPrice> prices = b.getMonthlyPrices(sd, 10);
	}

}
