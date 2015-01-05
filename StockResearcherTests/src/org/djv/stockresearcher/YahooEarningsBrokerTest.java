package org.djv.stockresearcher;

import java.util.Date;

import org.djv.stockresearcher.broker.IEarningsDateBroker;
import org.djv.stockresearcher.broker.YahooEarningsDateBroker;
import org.junit.Test;

public class YahooEarningsBrokerTest {
	
	@Test
	public void test() throws Exception {
		IEarningsDateBroker b = new YahooEarningsDateBroker();
		Date d = b.getEarningsDate("MSFT");
	}

}
