package org.djv.stockresearcher.widgets.support;

import java.util.Comparator;

import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.widgets.StockTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableSortListener implements Listener {
	/**
	 * 
	 */
	private final StockTable stockTable;

	/**
	 * @param sectorSearchPart
	 */
	public TableSortListener(StockTable table) {
		this.stockTable = table;
	}

	public void handleEvent(Event e) {
        TableColumn column = (TableColumn)e.widget;
        
        int sortDir = SWT.UP;
        if (column == this.stockTable.getTable().getSortColumn()){
        	 sortDir = this.stockTable.getTable().getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP;
        }

        int index = 0;
        for (index = 0; index < this.stockTable.getTable().getColumnCount(); index ++){
        	if (column == this.stockTable.getTable().getColumn(index)){
        		break;
        	}
        }
        Comparator<String> comp = getComparator(sortDir, index);
        
        TableItem[] items = this.stockTable.getTable().getItems();
        
        for (int i = 1; i < items.length; i++) {
            String value1 = items[i].getText(index);
            for (int j = 0; j < i; j++) {
                String value2 = items[j].getText(index);
                if (comp.compare(value1, value2) < 0) {
                    String[] values = new String[this.stockTable.getStockTableConfig().getColumns().size()];
                    for (int k = 0; k < values.length; k ++){
                    	values[k] = items[i].getText(k);
                    }
                    StockData sd = (StockData) items[i].getData("sd");
                    
                    items[i].dispose();
                    TableItem item = new TableItem(this.stockTable.getTable(), SWT.NONE, j);
                    this.stockTable.getTableItemMap().put(sd.getStock().getSymbol(), item);
                    item.setData("sd", sd);
                    item.setText(values);
                    stockTable.setAllColors(sd, item);
                    items = this.stockTable.getTable().getItems();
                    break;
                }
            }
        }
        this.stockTable.getTable().setSortColumn(column);
		this.stockTable.getTable().setSortDirection(sortDir);
    }

	public Comparator<String> getComparator(int sortDir, int index) {
		StockTableColumn col = this.stockTable.getStockTableConfig().getColumns().get(index);
		try {
			SortDirComparator comp = col.getComparatorClass().newInstance();
			comp.setSortDir(sortDir);
			return comp;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}