package org.djv.stockresearcher.broker;

import java.math.BigDecimal;
import java.util.List;

import org.djv.stockresearcher.model.AnalystEstimates;
import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.HistPrice;
import org.djv.stockresearcher.model.StockData;

public interface IAnalystDataBroker {
	
	public AnalystRatings getAnalystRatings (StockData sd);
	public AnalystEstimates getAnalystEstimates(StockData sd);
	public BigDecimal getAveragePE(StockData sd, int nbrYears);
	public List<HistPrice> getMonthlyPrices(StockData sd, int nbrYears);
	
}
