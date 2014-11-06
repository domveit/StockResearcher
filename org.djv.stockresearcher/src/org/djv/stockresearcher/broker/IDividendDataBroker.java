package org.djv.stockresearcher.broker;

import java.util.Date;
import java.util.List;

import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.StockData;

public interface IDividendDataBroker {
	
	public List<DivData> getNewDividends(StockData sd, Date startDate) throws Exception;

}
