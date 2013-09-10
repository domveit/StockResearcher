/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.djv.stockresearcher.parts;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.IndustryStockListener;
import org.djv.stockresearcher.db.SectorIndustryRegistry;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.db.StockDataChangeListener;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SectorSearchPart implements IndustryStockListener, StockDataChangeListener {
	
	private Combo sectorCombo;
	private Combo industryCombo;
	private Button searchButton;
	private Label progressLabel;
	
	Map<String, TableItem> tableItemMap = new HashMap<String, TableItem>();
	
	String[] titles = {"Stock", "Name", "MCap", "Price", "Div", "Yield", "PE", "PEG", "Strk", "Skip", "dg4", "dg8", "Rank", "Exchange"};
	Table stockTable;
	
	private Shell shell;
	
	Listener sortListener = new TableSortListener(this);
	
	@Inject
	private SectorIndustryRegistry sir;
	
	Runnable tablePacker = new Runnable(){
		@Override
		public void run() {
			for (int i=0; i< titles.length; i++) {
				stockTable.getColumn (i).pack ();
			}		
			progressLabel.setText("");
		}
	};
	
	@Inject
	private StockDB db;
	
	@PostConstruct
	public void postConstruct(final Composite parent) {
		parent.getShell().setMaximized(true);
		shell = parent.getShell();
		buildStockTable(parent);
		db.addIndustryStockListener(this);
		db.addStockDataChangeListener(this);
	}

	public void buildStockTable(Composite leftSide) {
		Composite stockComboComposite = new Composite(leftSide, SWT.BORDER);
		stockComboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stockComboComposite.setLayout(new GridLayout(4, false));
		
		sectorCombo = new Combo(stockComboComposite, SWT.NONE);
		sectorCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		sectorCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshIndustryCombo();
			}
		});
		
		refreshSectors();
		
		industryCombo = new Combo(stockComboComposite, SWT.NONE);
		industryCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		searchButton = new Button(stockComboComposite, SWT.NONE);
		searchButton.setText("Search");
		searchButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		searchButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sIndex = sectorCombo.getSelectionIndex();
				if (sIndex == -1){
					return;
				}
				final String sector = sectorCombo.getItem(sIndex);
				
				int iIndex = industryCombo.getSelectionIndex();
				if (iIndex == -1){
					return;
				}
				final String industry = industryCombo.getItem(iIndex);
				
				tableItemMap.clear();
				stockTable.removeAll();
		        stockTable.setSortColumn(null);
				stockTable.setSortDirection(SWT.NONE);
				
				db.updateSectorAndIndustry(sector, industry);
			}
		});
		
		progressLabel = new Label(stockComboComposite, SWT.NONE);
		progressLabel.setText("");
		progressLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		stockTable = new Table (stockComboComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		stockTable.setLinesVisible (true);
		stockTable.setHeaderVisible (true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		stockTable.setLayoutData(data);
		
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (stockTable, SWT.NONE);
			column.setText (titles [i]);
			column.addListener(SWT.Selection, sortListener);
		}	
		
		stockTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableRowSelect();
			}
		});
		tablePacker.run();
	}

	public void tableRowSelect() {
		int i = stockTable.getSelectionIndex();
		if (i < 0) {
			return;
		}
		StockData sd = (StockData)stockTable.getItem(i).getData("sd");
		AppState.getInstance().setSelectedStock(sd);
	}
	
	private void refreshIndustryCombo() {
		String sector = sectorCombo.getItem(sectorCombo.getSelectionIndex());
		List<String> cl = sir.getIndustriesForSector(sector);
		industryCombo.removeAll();
		industryCombo.setItems(cl.toArray(new String[0]));
		industryCombo.getParent().layout(true,  true);
	}

	private void refreshSectors() {
		sectorCombo.removeAll();
		sectorCombo.setItems(sir.getAllSectors().toArray(new String[0]));
		sectorCombo.getParent().layout(true,  true);
	}

	@Focus
	public void setFocus() {
	}
	
	@Override
	public void notifyChanged(final int industry, final List<StockData> sdList) {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				if (!stockTable.isDisposed()){
					progressLabel.setText("Updating Industry " + industry);
					for (StockData sd : sdList){
						if (sd.getSymbol() == null){
							continue;
						}
						TableItem item = tableItemMap.get(sd.getSymbol());;
						if (item == null){
							item = new TableItem (stockTable, SWT.NONE);
							item.setData("sd", sd);
							tableItemMap.put(sd.getSymbol(), item);
						}
						updateItem(sd, item, false);
					}
				}
			}
		});
	}

	@Override
	public void notifyChanged(final StockData sd) {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				TableItem item = tableItemMap.get(sd.getSymbol());
				if (item != null & !stockTable.isDisposed() && !item.isDisposed()){
					progressLabel.setText("Updating Stock " + sd.getSymbol());
					if (sd.isRanksCalculated() && sd.getOverAllRank() == 0.00){
						tableItemMap.remove(sd);
						item.dispose();
						
					} else {
						updateItem(sd, item, sd.isRanksCalculated());
					}
				}
			}
		});
	}
	
	public void updateItem(final StockData sd, TableItem item, boolean setColors) {
		if (setColors){
			setColor(sd, item);
		}
		
		item.setText (0, sd.getSymbol());
		item.setText (1, sd.getName());
		item.setText (2, sd.getMarketCap());
		item.setText (3, (sd.getPrice() == null) ? "N/A" : sd.getPrice());
		
		if (sd.getNormDividend() != null){
			item.setText (4,  sd.getNormDividend());
		} else {
			item.setText (4, (sd.getDividend() == null) ? "N/A" : sd.getDividend());
		}
		
		if (sd.getNormYield() != null){
			item.setText (5, new DecimalFormat("0.00").format(sd.getNormYield()));
		} else {
			item.setText (5, (sd.getYield() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getYield()));
		}
		
		item.setText (6, (sd.getPe() == null) ? "N/A" : sd.getPe());
		item.setText (7, (sd.getPeg() == null) ? "N/A" : sd.getPeg());
		item.setText (8, String.valueOf(sd.getStreak()));
		item.setText (9,  String.valueOf(sd.getSkipped()));
		item.setText (10, (sd.getDg4() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg4()) + "%");
		item.setText (11, (sd.getDg8() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg8()) + "%");

		item.setText (12, new DecimalFormat("0.00").format(sd.getOverAllRank()));
		item.setText (13, (sd.getExchange() == null) ? "" : sd.getExchange());
		Display.getDefault().timerExec(2000, tablePacker);
	}

	public void setColor(StockData sd, TableItem item) {
		item.setBackground(12, getColorForRank(sd.getOverAllRank()));
		item.setBackground(8, getColorForRank(sd.getStalwartRank()));
		item.setBackground(9, getColorForRank(sd.getStalwartRank()));
		item.setBackground(5, getColorForRank(sd.getYieldRank()));
		item.setBackground(10, getColorForRank(sd.getGrowthRank()));
		item.setBackground(11, getColorForRank(sd.getGrowthRank()));
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
	};
    
}
