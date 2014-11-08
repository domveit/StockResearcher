package org.djv.stockresearcher.model;

import java.util.Map;

public class FinKeyDataTable {
	
	Map<String, FinKeyData> finData;

	public Map<String, FinKeyData> getFinData() {
		return finData;
	}

	public void setFinData(Map<String, FinKeyData> finData) {
		this.finData = finData;
	}

}
