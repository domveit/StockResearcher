package org.djv.stockresearcher.widgets.support;

import java.util.ArrayList;
import java.util.List;

public class StockTableConfig {
	
	List<StockTableColumn> columns = new ArrayList<StockTableColumn>();

	public List<StockTableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<StockTableColumn> columns) {
		this.columns = columns;
	}
	
}
