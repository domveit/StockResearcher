package org.djv.stockresearcher.broker;

import java.util.List;

import org.djv.stockresearcher.model.StockData;

public interface IStockDataBroker {
	
	public void getPriceOnly(List<StockData> stocks, IStockDataCallbackHandler handler) ;
	public void getData(List<StockData> stocks, IStockDataCallbackHandler callBack) ;

}
