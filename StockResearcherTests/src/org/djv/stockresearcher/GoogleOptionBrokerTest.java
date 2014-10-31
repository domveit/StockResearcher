package org.djv.stockresearcher;

import org.djv.stockresearcher.db.GoogleOptionBroker;
import org.djv.stockresearcher.model.OptionPeriod;
import org.djv.stockresearcher.model.OptionTable;
import org.junit.Test;

public class GoogleOptionBrokerTest {
	
	@Test
	public void test() throws Exception {
		OptionTable t =  new GoogleOptionBroker().getOptionTable("AAPL");
		t.dump();
		for (OptionPeriod p : t.getPeriods()){
			t.setCurrentPeriod(p);
			new GoogleOptionBroker().getOptionCallsForCurrentPeriod(t);
			t.dump();
		}

		
	}
	
}
