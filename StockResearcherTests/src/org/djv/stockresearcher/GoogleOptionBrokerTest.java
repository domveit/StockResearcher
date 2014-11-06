package org.djv.stockresearcher;

import org.djv.stockresearcher.broker.GoogleJSONOptionDataBroker;
import org.djv.stockresearcher.model.OptionPeriod;
import org.djv.stockresearcher.model.OptionTable;
import org.junit.Test;

public class GoogleOptionBrokerTest {
	
	@Test
	public void test() throws Exception {
		OptionTable t =  new GoogleJSONOptionDataBroker().getOptionTable("AAPL");
		t.dump();
		for (OptionPeriod p : t.getPeriods()){
			t.setCurrentPeriod(p);
			new GoogleJSONOptionDataBroker().getOptionCallsForCurrentPeriod(t);
			t.dump();
		}

		
	}
	
}
