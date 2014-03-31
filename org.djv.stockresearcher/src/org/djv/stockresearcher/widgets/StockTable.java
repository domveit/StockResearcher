package org.djv.stockresearcher.widgets;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.widgets.support.StockTableColumn;
import org.djv.stockresearcher.widgets.support.StockTableConfig;
import org.djv.stockresearcher.widgets.support.TableSortListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class StockTable extends Composite {
	
	Map<String, TableItem> tableItemMap = new HashMap<String, TableItem>();
	TableSortListener sortListener = new TableSortListener(this);
	
	StockTableConfig stockTableConfig;
	Table table;

	public StockTableConfig getStockTableConfig() {
		return stockTableConfig;
	}

	public void setStockTableConfig(StockTableConfig stockTableConfig) {
		this.stockTableConfig = stockTableConfig;
		
		for (TableColumn tc : table.getColumns()){
			tc.dispose();
		}
		
		for (StockTableColumn col: stockTableConfig.getColumns()) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setData(col);
			column.setText (col.getDescription());
			column.addListener(SWT.Selection, sortListener);
		}
	}

	public Table getTable() {
		return table;
	}

	public Map<String, TableItem> getTableItemMap() {
		return tableItemMap;
	}

	public StockTable(Composite parent, int style) {
		super(parent, style);
		
		this.setLayout(new FillLayout());
		table = new Table (this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
	}
	
	public void addSelectionListener(SelectionListener l){
		table.addSelectionListener(l);
	}
	
	public StockData getSelectedStock(){
		int i = table.getSelectionIndex();
		if (i < 0) {
			return null;
		}
		StockData sd = (StockData)table.getItem(i).getData("sd");
		return sd;
	}
	
	public List<StockData> getSelectedStocks(){
		int[] tsel = table.getSelectionIndices();
		if (tsel== null || tsel.length ==0) {
			return null;
		}
		List<StockData> sdList = new ArrayList<StockData>();
		for (int i: tsel){
			StockData sd = (StockData)table.getItem(i).getData("sd");
			sdList.add(sd);
		}
		return sdList;
	}
	
	public void packColumns(){
		Display.getDefault().timerExec(500, packer);
	}
	
	public void reset(){
		tableItemMap.clear();
		table.removeAll();
		table.setSortColumn(null);
        table.setSortDirection(SWT.NONE);
	}
	
	public void updateItem(final StockData sd, TableItem item, boolean setColors) {
		if (item.isDisposed()){
			return;
		}
		int i = 0;
		for (StockTableColumn col : stockTableConfig.getColumns()){
			String itemText = null;
			if (sd != null){
				Object val = getColValue(sd, col.getSource());
				if (val != null){
					if (val instanceof String){
						itemText = (String)val;
					} else if (val instanceof BigDecimal){
						BigDecimal valBd = (BigDecimal) val;
						if (col.getDecimalFormat() == null){
							itemText = valBd.toString();
						} else {
							 itemText = new DecimalFormat(col.getDecimalFormat()).format(valBd);
						}
					} else if (val instanceof Double){
						Double valBd = (Double) val;
						if (col.getDecimalFormat() == null){
							itemText = valBd.toString();
						} else {
							 itemText = new DecimalFormat(col.getDecimalFormat()).format(valBd);
						}
					} else if (val instanceof Integer){
						Integer valBd = (Integer) val;
						if (col.getDecimalFormat() == null){
							itemText = valBd.toString();
						} else {
							 itemText = new DecimalFormat(col.getDecimalFormat()).format(valBd);
						}
					} else {
					}
				}
			}
			if (itemText == null){
				itemText = col.getNullDisplay();
			} else {
				if (col.isPercentile()){
					itemText += "%";
				}
			}
			
			item.setText (i, itemText);
			
			if (setColors){
				setColor(sd, item, col, i);
			}
			i++;
		}
	}

	private Object getColValue(final StockData sd, String source) {
		Object val = null;
		if (source.startsWith("stock.")){
			if (sd.getStock() != null){
				String fieldName = source.substring("stock.".length());
				val = getFieldValue(sd.getStock(), fieldName);
			}
		} else if (source.startsWith("stockIndustry.")){
			if (sd.getStockIndustry() != null){
				String fieldName = source.substring("stockIndustry.".length());
				val = getFieldValue(sd.getStockIndustry(), fieldName);
			}
		} else if (source.startsWith("sectorIndustry.")){
			if (sd.getSectorIndustry() != null){
				String fieldName = source.substring("sectorIndustry.".length());
				val = getFieldValue(sd.getSectorIndustry(), fieldName);
			}
		} else {
			val = getFieldValue(sd, source);
		}
		return val;
	}
	
	private Object getFieldValue(Object o, String fieldName){
		Field field;
		Object val = null;
		try {
			field = o.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			val = field.get(o);
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
		}
		return val;
	}

	public void setColor(StockData sd, TableItem item, StockTableColumn col, int index) {
		if (col.getColorSource() != null){
			Object val = getColValue(sd, col.getColorSource());
			if (val != null){
				Double colorRank = null;
				if (val instanceof BigDecimal){
					BigDecimal valBd = (BigDecimal) val;
					colorRank = valBd.doubleValue();
				} else if (val instanceof Double){
					colorRank = (Double) val;
				} else if (val instanceof Integer){
					colorRank = new Double((Integer) val);
				} 
				item.setBackground(index, getColorForRank(colorRank));
			}
		}
	};
	
	public Color getColorForRank(double rank) {
		if (rank >= 5){
			int nongreenness = 255 - (int)((Math.pow((rank - 5), 1.5) * 255) / Math.pow(5, 1.5));
			nongreenness = Math.max(nongreenness, 0);
			nongreenness = Math.min(nongreenness, 255);
			return new Color(Display.getDefault(), nongreenness, 255, 0);
		}
		
		if (rank <= 5){
			int nonredness = 255 - (int)((Math.pow((5 - rank), 1.5) * 255) / Math.pow(5, 1.5));
			nonredness = Math.max(nonredness, 0);
			nonredness = Math.min(nonredness, 255);
			return new Color(Display.getDefault(), 255, nonredness, 0);
		}
		return null;
	}
	
	public void addOrUpdateItems(List<StockData> sdList) {
		for (StockData sd : sdList){
			if (sd.getStock().getSymbol() == null){
				continue;
			}
			addOrUpdateItem(sd, false);
		}
	};

	public void addOrUpdateItems(List<StockData> sdList, boolean updateColors) {
		List<String> l = new ArrayList<String>();
		
		for (StockData sd : sdList){
			if (sd.getStock().getSymbol() == null){
				continue;
			}
			addOrUpdateItem(sd, updateColors);
			l.add(sd.getStock().getSymbol());
		}
		List<String> rl = new ArrayList<String>();
		for (String s: tableItemMap.keySet()){
			if (!l.contains(s)){
				rl.add(s);
			}
		}
		
		for (String s : rl){
			removeItem(s);
		}
		
	};
	
	public void addOrUpdateItem(StockData sd) {
		addOrUpdateItem(sd, false);
	}

	public void addOrUpdateItem(StockData sd, boolean updateColors) {
		TableItem item = tableItemMap.get(sd.getStock().getSymbol());
		if (item == null){
			item = new TableItem (table, SWT.NONE);
			tableItemMap.put(sd.getStock().getSymbol(), item);
		}
		updateItem(sd, item, updateColors);
		item.setData("sd", sd);
	}
	
	public void updateItem(StockData sd, boolean updateColors) {
		TableItem item = tableItemMap.get(sd.getStock().getSymbol());;
		if (item != null){
			updateItem(sd, item, updateColors);
			item.setData("sd", sd);
		}
	}
	
	public void removeItem(StockData sd) {
		removeItem(sd.getStock().getSymbol());
	};

	public void removeItem(String s) {
		TableItem item = tableItemMap.get(s);
		if (item != null){
			tableItemMap.remove(s);
			item.dispose();
		}
	};
	
	private Runnable packer = new Runnable() {
		@Override
		public void run() {
			for (int i=0; i< stockTableConfig.getColumns().size(); i++) {
				table.getColumn (i).pack ();
			}
		}
	};

	public void setAllColors(StockData sd, TableItem item) {
		int i = 0;
		for (StockTableColumn col : stockTableConfig.getColumns()){
			setColor(sd, item, col, i);
			i++;
		}
	}
}
