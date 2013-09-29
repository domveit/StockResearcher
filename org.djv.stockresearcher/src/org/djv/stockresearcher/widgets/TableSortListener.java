package org.djv.stockresearcher.widgets;

import java.util.Comparator;

import org.djv.stockresearcher.model.StockData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class TableSortListener implements Listener {
	/**
	 * 
	 */
	private final StockTable stockTable;

	/**
	 * @param sectorSearchPart
	 */
	TableSortListener(StockTable table) {
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
                    String[] values = new String[this.stockTable.getTitles().length];
                    for (int k = 0; k < this.stockTable.getTitles().length; k ++){
                    	values[k] = items[i].getText(k);
                    }
                    StockData sd = (StockData) items[i].getData("sd");
                    
                    items[i].dispose();
                    TableItem item = new TableItem(this.stockTable.getTable(), SWT.NONE, j);
                    this.stockTable.getTableItemMap().put(sd.getStock().getSymbol(), item);
                    item.setData("sd", sd);
                    item.setText(values);
                    stockTable.setColor(sd, item);
                    items = this.stockTable.getTable().getItems();
                    break;
                }
            }
        }
        this.stockTable.getTable().setSortColumn(column);
		this.stockTable.getTable().setSortDirection(sortDir);
    }

	public Comparator<String> getComparator(int sortDir, int index) {
		Comparator<String> comp = null;
        switch(index){
        	case 2:
        		 comp = new MarketCapComparator(sortDir);
        		 break;
        	case 3:
        	case 4:
        	case 5:
        	case 6:
        	case 7:
        	case 8:
        	case 9:
        	case 10:
        	case 11:
        	case 12:
        		 comp = new ForgivingBigDecimalComparator(sortDir);
        		 break;
        	default:
        		 comp = new StringComparator(sortDir);
        }
		return comp;
	}
}