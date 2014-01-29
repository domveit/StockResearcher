package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
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
	
	String[] titles = {"Stock", "Name", "MCap", "Price", "Yr Range", "Div", "Yield", "PE", "PEG", "Strk", "Skip", "dg5", "dg10", "rg4", "rg8", "Rank", "Exchange", "Industry", "Sector", "Value score", "chowder"};
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
		
		String yrRange = ((sd.getStock().getYearLow() == null) ? "???" :  new DecimalFormat("0.00").format(sd.getStock().getYearLow()))
				 + "-" + 
				 ((sd.getStock().getYearHigh() == null) ? "???" :  new DecimalFormat("0.00").format(sd.getStock().getYearHigh()));
		item.setText (4, yrRange);
		
		if (sd.getNormDividend() != null){
			item.setText (5,  String.valueOf(sd.getNormDividend()));
		} else {
			item.setText (5, (sd.getStock().getDividend() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getDividend()));
		}
		
		if (sd.getNormYield() != null){
			item.setText (6, new DecimalFormat("0.00").format(sd.getNormYield()));
		} else {
			item.setText (6, (sd.getStock().getYield() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getYield()));
		}
		
		item.setText (7, (sd.getStock().getPe() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getPe()));
		item.setText (8, (sd.getStock().getPeg() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getStock().getPeg()));
		item.setText (9, String.valueOf(sd.getStreak()));
		item.setText (10,  String.valueOf(sd.getSkipped()));
		item.setText (11, (sd.getDg5() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg5()) + "%");
		item.setText (12, (sd.getDg10() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg10()) + "%");
		
		item.setText (13, (sd.getEps4() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getEps4()) + "%");
		item.setText (14, (sd.getEps8() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getEps8()) + "%");

		item.setText (15, new DecimalFormat("0.00").format(sd.getOverAllRank()));
		item.setText (16, (sd.getStock().getExchange() == null) ? "" : sd.getStock().getExchange());
		item.setText (17, (sd.getSectorIndustry() == null) ? "" : sd.getSectorIndustry().getIndustryName());
		item.setText (18, (sd.getSectorIndustry() == null) ? "" : sd.getSectorIndustry().getSectorName());
		

		item.setText (19, (sd.getYrHighDiff() == null) ? "" : new DecimalFormat("0.00").format(sd.getYrHighDiff()) + "%");
		
		BigDecimal chowder = null;
		if (sd.getNormYield() != null){
			if (sd.getDg5() != null){
				chowder = sd.getNormYield().add(new BigDecimal(sd.getDg5()));
			} 
		} else if (sd.getStock().getYield()!= null){
			if (sd.getDg5() != null){
				chowder = sd.getStock().getYield().add(new BigDecimal(sd.getDg5()));
			} 
		} 
		item.setText (20, (chowder == null) ? "" : new DecimalFormat("0.00").format(chowder) + "%");
	}

	public void setColor(StockData sd, TableItem item) {
		item.setBackground(15, getColorForRank(sd.getOverAllRank()));
		item.setBackground(9, getColorForRank(sd.getStalwartRank()));
		item.setBackground(10, getColorForRank(sd.getStalwartRank()));
		item.setBackground(6, getColorForRank(sd.getYieldRank()));
		item.setBackground(11, getColorForRank(sd.getGrowthRank()));
		item.setBackground(12, getColorForRank(sd.getGrowthRank()));
		item.setBackground(13, getColorForRank(sd.getFinRank()));
		item.setBackground(14, getColorForRank(sd.getFinRank()));
		
		item.setBackground(19, getColorForRank(sd.getValueRank()));
		
		if (sd.getStock().getMarketCap() == null || sd.getStock().getMarketCap().endsWith("M")){
			item.setBackground(2, new Color(Display.getDefault(), 255, 0, 0));
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
			for (int i=0; i< titles.length; i++) {
				table.getColumn (i).pack ();
			}
		}
	};
}
