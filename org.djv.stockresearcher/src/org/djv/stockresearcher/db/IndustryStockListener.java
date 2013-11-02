package org.djv.stockresearcher.db;

import java.util.List;

import org.djv.stockresearcher.model.StockData;

public interface IndustryStockListener {
	
	void notifyChanged(String industryName, List<StockData> stockData, int industriesToUpdate, int industriesUpdated, int beginOrEnd);

}
