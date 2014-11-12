package org.djv.stockresearcher.broker;

import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.StockData;

public interface IAnalystDataBroker {
	
	public AnalystRatings getAnalystRatings (StockData sd);

}
