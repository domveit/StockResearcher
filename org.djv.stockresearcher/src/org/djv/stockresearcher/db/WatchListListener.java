package org.djv.stockresearcher.db;

import java.util.List;

import org.djv.stockresearcher.model.StockData;

public interface WatchListListener {
	
	void notifyChanged(List<StockData> stockData, boolean added);

}
