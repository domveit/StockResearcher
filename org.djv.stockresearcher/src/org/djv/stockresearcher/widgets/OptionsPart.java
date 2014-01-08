 
package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Option;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.util.DateUtil;
import org.eclipse.swt.SWT;
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
	
	Map<String, TabItem> tabMap = new HashMap<String, TabItem>();
	
	String[] titles = {"Strike", "Bid", "Ask", "Basis", "IValue", "TValue", "Days", "Return", "A. Ret"};
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout());
		
		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				
		folder = new TabFolder(parent, SWT.NONE);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		AppState.getInstance().addListener(this);
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
		for (TabItem item : folder.getItems()){
			item.dispose();
		}
		tabMap.clear();
		
		label.setText(sd.getStock().getSymbol() + " - " + (sd.getStockIndustry() == null ? "???" :  (sd.getStockIndustry().getName() + " - " + sd.getStock().getPrice())) + " getting options data....");
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					List<Option> optionList = StockDB.getInstance().getOptions(sd.getStock().getSymbol());
					for (final Option o : optionList){
						Display.getDefault().asyncExec(new Runnable(){
							@Override
							public void run() {
								String key = new SimpleDateFormat("MM/dd/yyyy").format(o.getExpiration());
								if (o.getSymbol().startsWith(sd.getStock().getSymbol()+ "7")){
									key = key + " mini";
								}
								TabItem tabItem = tabMap.get(key);
								if (tabItem == null){
									tabItem = new TabItem(folder, SWT.NONE);
									tabItem.setText(key);
									tabMap.put(key, tabItem);
									Table table = new Table (folder, SWT.MULTI | SWT.NONE | SWT.FULL_SELECTION);
									table.setLinesVisible (true);
									table.setHeaderVisible (true);
									
									for (int i=0; i<titles.length; i++) {
										TableColumn column = new TableColumn (table, SWT.NONE);
										column.setText (titles [i]);
									}	
									tabItem.setControl(table);
								}
							
								TableItem item = new TableItem ((Table)tabItem.getControl(), SWT.NONE);
								
//							String[] titles = {"Expiration Date", "Symbol", "Strike", "Bid", "Ask", "Basis", "IValue", "TValue", "Days", "Return", "A. Ret"};
								
								BigDecimal basis= sd.getStock().getPrice().min(o.getStrike());
								BigDecimal iValue= sd.getStock().getPrice().subtract(basis);
								int days = DateUtil.daysBetween(o.getExpiration());
								BigDecimal tValue = null;
								BigDecimal retrn = null;
								BigDecimal annRet = null;
								if (o.getBid() != null){
									tValue = o.getBid().subtract(iValue);
									retrn = tValue.multiply(new BigDecimal(100)).divide(basis, 2, RoundingMode.HALF_UP);
									annRet = retrn.multiply(new BigDecimal(360)).divide(new BigDecimal(days), 2, RoundingMode.HALF_UP);
								}
								
//								item.setText (0, new SimpleDateFormat("MM/dd/yyyy").format(o.getExpiration()));
//								item.setText (1, o.getSymbol());
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
						});
					}
					
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							for (Control c : folder.getTabList()){
								Table t = (Table)c;
								for (TableColumn col : t.getColumns()){
									col.pack();
								}
							}
							
							label.setText(sd.getStock().getSymbol() + " - " + sd.getStockIndustry() == null ? "???" :  sd.getStockIndustry().getName() + " - " + sd.getStock().getPrice());
						
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}


	
}