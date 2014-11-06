package org.djv.stockresearcher.broker;

import java.util.List;

import org.djv.stockresearcher.model.SectorIndustry;
import org.djv.stockresearcher.model.StockIndustry;

public interface ISectorIndustryDataBroker {
	
	public List<SectorIndustry> getSectors() throws Exception;
	public List<StockIndustry> getStocksForIndustry(Integer ind); 

}
