package org.djv.stockresearcher.model;

import java.util.Map;

public class FinData {
	
	Map<String, FinPeriodData> finData;

	public Map<String, FinPeriodData> getFinData() {
		return finData;
	}

	public void setFinData(Map<String, FinPeriodData> finData) {
		this.finData = finData;
	}

}
