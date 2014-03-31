 
package org.djv.stockresearcher.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.db.StockDataChangeListener;
import org.djv.stockresearcher.db.WatchListListener;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.widgets.AddWatchDialog;
import org.djv.stockresearcher.widgets.StockTable;
import org.djv.stockresearcher.widgets.TextProgressBar;
import org.djv.stockresearcher.widgets.support.StockTableColumn;
import org.djv.stockresearcher.widgets.support.StockTableConfig;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class WatchListPart implements StockDataChangeListener, WatchListListener{
	
	Map<String, TableItem> tableItemMap = new HashMap<String, TableItem>();
	
	private Combo sectorCombo;
	
	Button refreshButton;
	Button addButton;
	Button removeButton;
	private TextProgressBar stockProgressLabel;
	
	StockTable table;
	
	Shell shell;
	
	private StockDB db = StockDB.getInstance();
	
	@PostConstruct
	public void postConstruct(final Composite parent) {
		this.shell = parent.getShell();
		createStockTable(parent);
		db.addStockDataChangeListener(this);
		db.addWatchListListener(this);
		try {
			db.refreshWatchList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createStockTable(final Composite parent) {
		Composite stockComboComposite = new Composite(parent, SWT.BORDER);
		stockComboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stockComboComposite.setLayout(new GridLayout(5, false));
		
		sectorCombo = new Combo(stockComboComposite, SWT.NONE);
		sectorCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		sectorCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					db.refreshWatchList();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		refreshSectors();
		
		refreshButton = new Button(stockComboComposite, SWT.NONE);
		refreshButton.setText("Refresh");
		refreshButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		refreshButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					db.refreshWatchList();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		addButton = new Button(stockComboComposite, SWT.NONE);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		addButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddWatchDialog pd = new AddWatchDialog(parent.getShell());
				pd.create();
				int result = pd.open();
				if (result == Window.OK) {
					try {
						StockDB.getInstance().addToWatchList(pd.getSymbol());
					} catch (Exception e1) {
						MessageDialog.openError(parent.getShell(), "Error", e1.getMessage());
					}
				} 
			
			}
		});
		
		removeButton = new Button(stockComboComposite, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		removeButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<StockData> sdList = table.getSelectedStocks();
				
				if(sdList != null && sdList.size() > 0){
					String list = "";
					for (StockData sd : sdList){
						list += sd.getSymbol() + " ";
					}
					boolean result = MessageDialog.openConfirm(shell, "Are you sure?", "Remove " + list+ ". Are you sure?");
					if (result){
						try {
							db.removeAllFromWatchList(sdList);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		stockProgressLabel = new TextProgressBar(stockComboComposite, SWT.NONE);
		stockProgressLabel.setText("");
		stockProgressLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		table = new StockTable (stockComboComposite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1);
		table.setLayoutData(data);
		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableRowSelect();
			}
		});
		table.setStockTableConfig(getStockTableConfig());
		table.packColumns();
	}
	private StockTableConfig getStockTableConfig() {
		StockTableConfig stockTableConfig = new StockTableConfig();
		stockTableConfig.getColumns().add(StockTableColumn.WATCHED);
		stockTableConfig.getColumns().add(StockTableColumn.STOCK);
		stockTableConfig.getColumns().add(StockTableColumn.NAME);
		stockTableConfig.getColumns().add(StockTableColumn.MARKET_CAP);
		stockTableConfig.getColumns().add(StockTableColumn.EXCHANGE);
		stockTableConfig.getColumns().add(StockTableColumn.SECTOR);
		stockTableConfig.getColumns().add(StockTableColumn.INDUSTRY);
		stockTableConfig.getColumns().add(StockTableColumn.PE);
		stockTableConfig.getColumns().add(StockTableColumn.PEG);
		
		stockTableConfig.getColumns().add(StockTableColumn.PRICE);
		stockTableConfig.getColumns().add(StockTableColumn.YIELD);
		stockTableConfig.getColumns().add(StockTableColumn.NORM_YIELD);
		stockTableConfig.getColumns().add(StockTableColumn.DIVIDEND);
		stockTableConfig.getColumns().add(StockTableColumn.NORM_DIVIDEND);
		stockTableConfig.getColumns().add(StockTableColumn.YIELD_RANK);
		
		stockTableConfig.getColumns().add(StockTableColumn.YRLOW);
		stockTableConfig.getColumns().add(StockTableColumn.YRHIGH);
		stockTableConfig.getColumns().add(StockTableColumn.YRRANK);
		stockTableConfig.getColumns().add(StockTableColumn.YR_TARGET_PRICE);
		stockTableConfig.getColumns().add(StockTableColumn.YR_UPSIDE);
		stockTableConfig.getColumns().add(StockTableColumn.VALUE_RANK);
		
		stockTableConfig.getColumns().add(StockTableColumn.STREAK);
		stockTableConfig.getColumns().add(StockTableColumn.SKIPPED);
		stockTableConfig.getColumns().add(StockTableColumn.STALWART_RANK);
		
		stockTableConfig.getColumns().add(StockTableColumn.DG5);
		stockTableConfig.getColumns().add(StockTableColumn.DG10);
		stockTableConfig.getColumns().add(StockTableColumn.GROWTH_RANK);
		
		stockTableConfig.getColumns().add(StockTableColumn.RG5);
		stockTableConfig.getColumns().add(StockTableColumn.RG10);
		stockTableConfig.getColumns().add(StockTableColumn.FIN_RANK);
		
		stockTableConfig.getColumns().add(StockTableColumn.CHOWDER);
		stockTableConfig.getColumns().add(StockTableColumn.OVERALL_RANK);		
		return stockTableConfig;
	}

	protected void refreshWatchList() {
		// TODO Auto-generated method stub
		
	}

	private void refreshSectors() {
		sectorCombo.removeAll();
		try {
			List<String> sectorList = new ArrayList<String>();
			sectorList.add("ALL");
			sectorList.addAll(db.getAllSectors());
			sectorCombo.setItems(sectorList.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sectorCombo.getParent().layout(true,  true);
	}

	
	@Focus
	public void onFocus() {
	}
	
	private void tableRowSelect() {
		StockData sd = table.getSelectedStock();
		if (sd != null){
			AppState.getInstance().setSelectedStock(sd);
		}
	}

	@Override
	public void notifyChanged(final List<StockData> sdList, boolean addedOrRemoved) {
		String sector = "ALL";
		int ix = sectorCombo.getSelectionIndex();
		if (ix > -1){
			sector = sectorCombo.getItem(ix);
		}
		
		if (!table.isDisposed()){
			if (sdList!= null){
				if (addedOrRemoved){
					for (StockData sd: sdList){
						if ("".equals(sector) || "ALL".equals(sector) || (sd.getSectorIndustry() != null && sd.getSectorIndustry().getSectorName().equals(sector))){
							table.addOrUpdateItem(sd);
						} else {
							table.removeItem(sd);
						}
					}
					table.packColumns();
				} else {
					for (StockData sd: sdList){
						table.removeItem(sd);
					}
				}
			}
		}
	}

	@Override
	public void notifyChanged(final StockData sd, final int toUpdate, final int updated) {
		String sector = "ALL";
		int ix = sectorCombo.getSelectionIndex();
		if (ix > -1){
			sector = sectorCombo.getItem(ix);
		}
		
		if (sd == null ||  toUpdate == updated){
			stockProgressLabel.setText("");
			stockProgressLabel.setMaximum(0);
			stockProgressLabel.setSelection(0);
		} else {
			stockProgressLabel.setMaximum(toUpdate);
			stockProgressLabel.setSelection(updated);
			stockProgressLabel.setText("Updating " + sd.getStock().getSymbol()  + "(" + updated + "/" + toUpdate + ")");
		}
		if (sd != null){
			if (!table.isDisposed()){
				if ("*".equals(sd.getWatched())){
					if ("".equals(sector) || "ALL".equals(sector) || (sd.getSectorIndustry() != null && sd.getSectorIndustry().getSectorName().equals(sector))){
						if (sd.getRanksCalculated()){
							table.addOrUpdateItem(sd,true);
						} else {
							table.addOrUpdateItem(sd,false);
						}
					} else {
						table.removeItem(sd);
					}
				} else {
					table.removeItem(sd);
				}
			}
		}
	}

	
}