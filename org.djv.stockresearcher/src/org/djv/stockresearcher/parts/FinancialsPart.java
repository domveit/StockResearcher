 
package org.djv.stockresearcher.parts;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.FinDataPeriod;
import org.djv.stockresearcher.model.FinDataRow;
import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FinancialsPart implements AppStateListener {

	private Combo reportTypeCombo;
	private Table finTable;
	private Composite parent;
	
	@Inject
	public FinancialsPart() {
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		AppState.getInstance().addListener(this);
		
		reportTypeCombo = new Combo(parent, SWT.READ_ONLY);
		reportTypeCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		reportTypeCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshTable();
			}
		});
		reportTypeCombo.add("Key Ratios");
		reportTypeCombo.add("Income Statement");
		reportTypeCombo.add("Balance Sheet");
		reportTypeCombo.add("Cash Flow Statement");
		reportTypeCombo.setText("Key Ratios");
		
		finTable = new Table(parent, SWT.BORDER |SWT.V_SCROLL);
		finTable.setLinesVisible(true);
		finTable.setHeaderVisible(true);
		GridData data2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		finTable.setLayoutData(data2);

		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				refreshTable();
			}
		});
	}
	
	public void refreshTable() {
		String s = reportTypeCombo.getText();
		if ("Key Ratios".equals(s)){
			StockData sd = AppState.getInstance().getSelectedStock();
			try {
				FinDataTable data =  StockDB.getInstance().getKeyRatios(sd);
				buildTable(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("Income Statement".equals(s)) {
			StockData sd = AppState.getInstance().getSelectedStock();
			try {
				FinDataTable data =  StockDB.getInstance().getIncomeStatement(sd);
				buildTable(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  else if ("Balance Sheet".equals(s)) {
			StockData sd = AppState.getInstance().getSelectedStock();
			try {
				FinDataTable data =  StockDB.getInstance().getBalanceSheet(sd);
				buildTable(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("Cash Flow Statement".equals(s)) {
			StockData sd = AppState.getInstance().getSelectedStock();
			try {
				FinDataTable data =  StockDB.getInstance().getCashFlowStatement(sd);
				buildTable(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void buildTable(FinDataTable data) {
		finTable.removeAll();
		
		while ( finTable.getColumnCount() > 0 ) {
		    finTable.getColumns()[ 0 ].dispose();
		}
		
		TableColumn firstCol = new TableColumn (finTable, SWT.NONE);
		firstCol.setText ("");
		
		for (FinDataPeriod p : data.getPeriods()){
			TableColumn periodCol = new TableColumn (finTable, SWT.NONE);
			periodCol.setText(p.getName());
		}
		
		for (FinDataRow r : data.getRows()){

			TableItem ti = new TableItem(finTable, SWT.NONE);
			ti.setText(0, r.getName());
			
			Map<FinDataPeriod, BigDecimal> map = data.getDataMap().get(r);
//			if (map == null || map.isEmpty()){
//				ti.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
//			}
			int ix = 1;
			if (map != null){
				boolean allValuesNull = true;
				for (FinDataPeriod p : data.getPeriods()){
					BigDecimal val = map.get(p);
					if (val != null){
						ti.setText(ix, val.toString());
						allValuesNull = false;
					}
					ix++;
				}
				if (allValuesNull){
					ti.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
				}
				
			}
		}
		for (TableColumn tc : finTable.getColumns()){
			tc.pack();
		}
		parent.layout(true, true);
	}

	@Override
	public void notifyChanged(AppState appState) {
		refreshTable();
	}

}