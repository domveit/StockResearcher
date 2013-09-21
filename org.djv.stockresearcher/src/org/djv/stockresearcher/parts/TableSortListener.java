package org.djv.stockresearcher.parts;

import java.util.Comparator;

import org.djv.stockresearcher.model.StockData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

final class TableSortListener implements Listener {
	/**
	 * 
	 */
	private final SectorSearchPart sectorSearchPart;

	/**
	 * @param sectorSearchPart
	 */
	TableSortListener(SectorSearchPart sectorSearchPart) {
		this.sectorSearchPart = sectorSearchPart;
	}

	public void handleEvent(Event e) {
        TableColumn column = (TableColumn)e.widget;
        
        int sortDir = SWT.UP;
        if (column == this.sectorSearchPart.stockTable.getSortColumn()){
        	 sortDir = this.sectorSearchPart.stockTable.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP;
        }

        int index = 0;
        for (index = 0; index < this.sectorSearchPart.stockTable.getColumnCount(); index ++){
        	if (column == this.sectorSearchPart.stockTable.getColumn(index)){
        		break;
        	}
        }
        Comparator<String> comp = getComparator(sortDir, index);
        
        TableItem[] items = this.sectorSearchPart.stockTable.getItems();
        
        for (int i = 1; i < items.length; i++) {
            String value1 = items[i].getText(index);
            for (int j = 0; j < i; j++) {
                String value2 = items[j].getText(index);
                if (comp.compare(value1, value2) < 0) {
                    String[] values = new String[this.sectorSearchPart.titles.length];
                    for (int k = 0; k < this.sectorSearchPart.titles.length; k ++){
                    	values[k] = items[i].getText(k);
                    }
                    StockData sd = (StockData) items[i].getData("sd");
                    
                    items[i].dispose();
                    TableItem item = new TableItem(this.sectorSearchPart.stockTable, SWT.NONE, j);
                    this.sectorSearchPart.tableItemMap.put(sd.getStock().getSymbol(), item);
                    item.setData("sd", sd);
                    item.setText(values);
                    sectorSearchPart.setColor(sd, item);
                    items = this.sectorSearchPart.stockTable.getItems();
                    break;
                }
            }
        }
        this.sectorSearchPart.stockTable.setSortColumn(column);
		this.sectorSearchPart.stockTable.setSortDirection(sortDir);
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