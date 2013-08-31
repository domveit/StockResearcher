package org.djv.stockresearcher.db;

import java.util.List;

import org.djv.stockresearcher.model.StockData;

public interface IndustryStockListener {
	
	void notifyChanged(int industry, List<StockData> stockData);

}
