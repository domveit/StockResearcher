package org.djv.stockresearcher.broker;

import java.util.Map;

import org.djv.stockresearcher.model.FinPeriodData;
import org.djv.stockresearcher.model.StockData;

public interface IFinancialDataBroker {

	
	public Map<String, FinPeriodData> getFinancialData(StockData sd) throws Exception;
}