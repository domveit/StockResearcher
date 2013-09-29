package org.djv.stockresearcher.db;

import org.djv.stockresearcher.model.StockData;

public interface StockDataChangeListener {
	
	void notifyChanged(StockData stockData, int stocksToUpdate, int stocksUpdated);

}
