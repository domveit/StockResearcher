 
package org.djv.stockresearcher.parts;

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
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class WatchListPart implements StockDataChangeListener, WatchListListener{
	
	Map<String, TableItem> tableItemMap = new HashMap<String, TableItem>();
	
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
		stockComboComposite.setLayout(new GridLayout(4, false));
		
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
				StockData sd = table.getSelectedStock();
				if(sd != null){
					boolean result = MessageDialog.openConfirm(shell, "Are you sure?", "Remove \"" + sd.getStock().getSymbol() + "\". Are you sure?");
					if (result){
						try {
							db.removeFromWatchList(sd.getStock().getSymbol());
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
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		table.setLayoutData(data);
		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableRowSelect();
			}
		});
		table.packColumns();
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
		if (!table.isDisposed()){
			if (sdList!= null){
				if (addedOrRemoved){
					for (StockData sd: sdList){
						table.addOrUpdateItem(sd);
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
				if (sd.isWatched()){
					table.addOrUpdateItem(sd, true);
				} else {
					table.removeItem(sd);
				}
			}
		}
	}

	
}