 
package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Option;
import org.djv.stockresearcher.model.OptionPeriod;
import org.djv.stockresearcher.model.OptionTable;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.util.DateUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class OptionsPart implements AppStateListener {
	
	Label label;
	TabFolder folder;
	StockData currentSd = null;
	Composite parent;
	
//	Map<OptionPeriod, TabItem> tabMap = new HashMap<String, TabItem>();
	
	String[] titles = {"Strike", "Bid", "Ask", "Basis", "IValue", "TValue", "Days", "Return", "A. Ret"};
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout());
		
		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				
		AppState.getInstance().addListener(this);
		this.parent = parent;
		updateTabFolder();
	}
	
	@Override
	public void notifyChanged(AppState appState) {
		updateTabFolder();
	}

	public void updateTabFolder() {
		final StockData sd = AppState.getInstance().getSelectedStock();
		if (sd == currentSd){
			return;
		}
		currentSd = sd;
		
		if (folder != null){
			folder.dispose();
		}
		
		folder = new TabFolder(this.parent, SWT.NONE);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		folder.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabFolder f = (TabFolder)e.widget;
		        TabItem ti = f.getSelection()[0];
				OptionTable ot = (OptionTable) ti.getData("optionTable");
				if (ot != null){
					OptionPeriod period = (OptionPeriod) ti.getData("period");
					StockData sd = (StockData) ti.getData("sd");
					ot.setCurrentPeriod(period);
					try {
						StockDB.getInstance().getDataForStocks(Arrays.asList(sd));
						StockDB.getInstance().getOptionBroker().getOptionCallsForCurrentPeriod(ot);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					refreshTabFolder(ti, ot, sd);
				}
			}
		});
		updateTabFolder();
		
		label.setText(sd.getStock().getSymbol() + " - " + (sd.getStockIndustry() == null ? "???" :  (sd.getStockIndustry().getName() + " - " + sd.getStock().getPrice())) + " getting options data....");
		OptionTable ot;
		try {
			ot = StockDB.getInstance().getOptionBroker().getOptionTable(sd.getStock().getSymbol());
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		for (OptionPeriod op : ot.getPeriods()){
			String key = new SimpleDateFormat("MM/dd/yyyy").format(op.getDate());
			TabItem tabItem = new TabItem(folder, SWT.NONE);
			tabItem.setText(key);
			tabItem.setData("optionTable", ot);
			tabItem.setData("period", op);
			tabItem.setData("sd", sd);
			Table table = new Table (folder, SWT.MULTI | SWT.NONE | SWT.FULL_SELECTION);
			table.setLinesVisible (true);
			table.setHeaderVisible (true);
			
			for (int i=0; i<titles.length; i++) {
				TableColumn column = new TableColumn (table, SWT.NONE);
				column.setText (titles [i]);
			}	
			tabItem.setControl(table);
		}
		if (folder.getItemCount() > 0){
			refreshTabFolder(folder.getItem(0), ot, sd);
		}
		this.parent.layout(true);
	}
	
	private void refreshTabFolder(TabItem ti, OptionTable ot, final StockData sd) {
		label.setText(sd.getStock().getSymbol() + " - " + (sd.getStockIndustry() == null ? "???" :  (sd.getStockIndustry().getName() + " - " + sd.getStock().getPrice())) + " getting options data....");
		
		final Table table = (Table)ti.getControl();
		table.removeAll();
		
		List<Option> list = ot.getCallsForCurrentPeriod();
		for (Option o : list){
			TableItem item = new TableItem (table, SWT.NONE);
			
	//	String[] titles = {"Expiration Date", "Symbol", "Strike", "Bid", "Ask", "Basis", "IValue", "TValue", "Days", "Return", "A. Ret"};
			
			BigDecimal basis= sd.getStock().getPrice().min(o.getStrike());
			BigDecimal iValue= sd.getStock().getPrice().subtract(basis);
			int days = DateUtil.daysBetween(o.getExpiration());
			BigDecimal tValue = null;
			BigDecimal retrn = null;
			BigDecimal annRet = null;
			if (o.getBid() != null){
				tValue = o.getBid().subtract(iValue);
				retrn = tValue.multiply(new BigDecimal(100)).divide(basis, 2, RoundingMode.HALF_UP);
				if (days > 0){
					annRet = retrn.multiply(new BigDecimal(360)).divide(new BigDecimal(days), 2, RoundingMode.HALF_UP);
				}
			}
			
	//		item.setText (0, new SimpleDateFormat("MM/dd/yyyy").format(o.getExpiration()));
	//		item.setText (1, o.getSymbol());
			item.setText (0, o.getStrike() == null ? "N/A" : new DecimalFormat("0.00").format(o.getStrike()));
			item.setText (1, o.getBid() == null ? "N/A" : new DecimalFormat("0.00").format(o.getBid()));
			item.setText (2, o.getAsk() == null ? "N/A" : new DecimalFormat("0.00").format(o.getAsk()));
			item.setText (3, basis == null ? "N/A" : new DecimalFormat("0.00").format(basis));
			item.setText (4, iValue == null ? "N/A" : new DecimalFormat("0.00").format(iValue));
			item.setText (5, tValue == null ? "N/A" : new DecimalFormat("0.00").format(tValue));
			item.setText (6, String.valueOf(days));
			item.setText (7, retrn == null ? "N/A" : new DecimalFormat("0.00").format(retrn) + "%");
			item.setText (8, annRet == null ? "N/A" : new DecimalFormat("0.00").format(annRet) + "%");
		}
		
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				for (TableColumn col : table.getColumns()){
					col.pack();
				}
				label.setText(sd.getStock().getSymbol() + " - " + (sd.getStockIndustry() == null ? "???" :  sd.getStockIndustry().getName()) + " - " + sd.getStock().getPrice());
			}
		});
		
	}


	
}