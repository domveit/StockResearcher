package org.djv.stockresearcher.broker;

import org.djv.stockresearcher.model.StockData;

public interface IStockDataCallbackHandler {
	
	public void updateStock(StockData sd);

}
