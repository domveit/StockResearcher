package org.djv.stockresearcher.widgets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.djv.stockresearcher.model.StockData;
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
	
	String[] titles = {"Stock", "Name", "MCap", "Price", "Div", "Yield", "PE", "PEG", "Strk", "Skip", "dg 4yr", "dg 8yr", "rg 4yr", "rg 8yr", "Rank", "Exchange", "Industry", "Sector"};
	Table table;

	public Table getTable() {
		return table;
	}

	public Map<String, TableItem> getTableItemMap() {
		return tableItemMap;
	}

	public String[] getTitles() {
		return titles;
	}

	public StockTable(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		table = new Table (this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
			column.addListener(SWT.Selection, sortListener);
		}	
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
		if (setColors){
			setColor(sd, item);
		}
		if (sd.isWatched()){
			item.setText (0, "* " + sd.getStock().getSymbol());
		} else {
			item.setText (0, sd.getStock().getSymbol());
		}
		item.setText (1, sd.getStockIndustry() == null ? "???" :  sd.getStockIndustry().getName());
		item.setText (2, sd.getStock().getMarketCap() == null ? "N/A" : sd.getStock().getMarketCap());
		item.setText (3, (sd.getStock().getPrice() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getPrice()));
		
		if (sd.getNormDividend() != null){
			item.setText (4,  String.valueOf(sd.getNormDividend()));
		} else {
			item.setText (4, (sd.getStock().getDividend() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getDividend()));
		}
		
		if (sd.getNormYield() != null){
			item.setText (5, new DecimalFormat("0.00").format(sd.getNormYield()));
		} else {
			item.setText (5, (sd.getStock().getYield() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getYield()));
		}
		
		item.setText (6, (sd.getStock().getPe() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getPe()));
		item.setText (7, (sd.getStock().getPeg() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getStock().getPeg()));
		item.setText (8, String.valueOf(sd.getStreak()));
		item.setText (9,  String.valueOf(sd.getSkipped()));
		item.setText (10, (sd.getDg4() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg4()) + "%");
		item.setText (11, (sd.getDg8() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg8()) + "%");
		
		item.setText (12, (sd.getEps4() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getEps4()) + "%");
		item.setText (13, (sd.getEps8() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getEps8()) + "%");

		item.setText (14, new DecimalFormat("0.00").format(sd.getOverAllRank()));
		item.setText (15, (sd.getStock().getExchange() == null) ? "" : sd.getStock().getExchange());
		item.setText (16, (sd.getSectorIndustry() == null) ? "" : sd.getSectorIndustry().getIndustryName());
		item.setText (17, (sd.getSectorIndustry() == null) ? "" : sd.getSectorIndustry().getSectorName());
		System.err.println("updated Item " + sd.getStock().getSymbol());
	}

	public void setColor(StockData sd, TableItem item) {
		item.setBackground(14, getColorForRank(sd.getOverAllRank()));
		item.setBackground(8, getColorForRank(sd.getStalwartRank()));
		item.setBackground(9, getColorForRank(sd.getStalwartRank()));
		item.setBackground(5, getColorForRank(sd.getYieldRank()));
		item.setBackground(10, getColorForRank(sd.getGrowthRank()));
		item.setBackground(11, getColorForRank(sd.getGrowthRank()));
		item.setBackground(12, getColorForRank(sd.getFinRank()));
		item.setBackground(13, getColorForRank(sd.getFinRank()));
		
		if (sd.getStock().getMarketCap() == null || sd.getStock().getMarketCap().endsWith("M")){
			item.setBackground(2, new Color(Display.getDefault(), 255, 0, 0));
		}
	};
	
	public Color getColorForRank(double rank) {
		if (rank >= 5){
			int nongreenness = 255 - (int)((Math.pow((rank - 5), 1.5) * 255) / Math.pow(5, 1.5));
			return new Color(Display.getDefault(), nongreenness, 255, nongreenness);
		}
		
		if (rank <= 5){
			int nonredness = 255 - (int)((Math.pow((5 - rank), 1.5) * 255) / Math.pow(5, 1.5));
			return new Color(Display.getDefault(), 255, nonredness, nonredness);
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
			System.err.println("adding Item " + sd.getStock().getSymbol());
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
			System.err.println("removing Item " + s);
			tableItemMap.remove(s);
			item.dispose();
		}
	};
	
	private Runnable packer = new Runnable() {
		@Override
		public void run() {
			for (int i=0; i< titles.length; i++) {
				table.getColumn (i).pack ();
			}
		}
	};
}
