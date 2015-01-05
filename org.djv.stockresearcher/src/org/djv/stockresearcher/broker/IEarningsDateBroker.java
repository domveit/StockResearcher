package org.djv.stockresearcher.broker;

import java.util.Date;

public interface IEarningsDateBroker {
	
	public Date getEarningsDate(String symbol);

}
