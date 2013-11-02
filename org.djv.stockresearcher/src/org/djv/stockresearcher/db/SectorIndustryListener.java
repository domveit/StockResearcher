package org.djv.stockresearcher.db;


public interface SectorIndustryListener {
	
	void notifyChanged(String industryName, int industriesToUpdate, int industriesUpdated, int beginOrEnd);

}
