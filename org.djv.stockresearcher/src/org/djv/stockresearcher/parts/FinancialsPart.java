 
package org.djv.stockresearcher.parts;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.FinPeriodData;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FinancialsPart implements AppStateListener {
	
	private StockDB db = StockDB.getInstance();
	
	private String[] finTitles;
	private Table finTable;
	private Composite parent;
	
	@Inject
	public FinancialsPart() {
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		AppState.getInstance().addListener(this);
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				buildFinTable();
			}
		});
	}
	
	@Focus
	public void onFocus() {
	}

	@Override
	public void notifyChanged(AppState appState) {
		buildFinTable();
	}

	private void buildFinTable() {
		if (finTable != null){
			finTable.dispose();
			finTable = null;
		}
		final StockData sd = AppState.getInstance().getSelectedStock();
		if (sd == null){
			return;
		}
		if (sd.getFinData() == null){
			return;
		}

		Map<String, FinPeriodData> finDataMap = sd.getFinData();
		if (finTable != null){
			finTable.dispose();
			finTable = null;
		}
		finTable = new Table(parent, SWT.BORDER |SWT.V_SCROLL);
		finTable.setLinesVisible(true);
		finTable.setHeaderVisible(true);
		GridData data2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		finTable.setLayoutData(data2);

		TableColumn firstCol = new TableColumn (finTable, SWT.NONE);
		firstCol.setText ("");

		TableItem revItem = new TableItem(finTable, SWT.NONE);
		revItem.setText(0, "Revenue");

		TableItem gmItem = new TableItem(finTable, SWT.NONE);
		gmItem.setText(0, "Gross Margin");

		TableItem oiItem = new TableItem(finTable, SWT.NONE);
		oiItem.setText(0, "Operating Income");

		TableItem omItem = new TableItem(finTable, SWT.NONE);
		omItem.setText(0, "Operating Margin");

		TableItem niItem = new TableItem(finTable, SWT.NONE);
		niItem.setText(0, "Net Income");

		TableItem epsItem = new TableItem(finTable, SWT.NONE);
		epsItem.setText(0, "Earnings PS");

		TableItem dItem = new TableItem(finTable, SWT.NONE);
		dItem.setText(0, "Dividends");

		TableItem prItem = new TableItem(finTable, SWT.NONE);
		prItem.setText(0, "Payout Ratio");

		TableItem sItem = new TableItem(finTable, SWT.NONE);
		sItem.setText(0, "Shares");

		TableItem bvItem = new TableItem(finTable, SWT.NONE);
		bvItem.setText(0, "Book Value PS");

		TableItem ocfItem = new TableItem(finTable, SWT.NONE);
		ocfItem.setText(0, "Operating Cash Flow");

		TableItem csItem = new TableItem(finTable, SWT.NONE);
		csItem.setText(0, "Cap Spending");

		TableItem fcfItem = new TableItem(finTable, SWT.NONE);
		fcfItem.setText(0, "Free Cash Flow");						

		TableItem fcfpsItem = new TableItem(finTable, SWT.NONE);
		fcfpsItem.setText(0, "Free Cash Flow PS");

		TableItem wcItem = new TableItem(finTable, SWT.NONE);
		wcItem.setText(0, "Working Capital");

		if (finDataMap == null){
			return;
		}

		int i = 1;
		for (String s : finDataMap.keySet()){
			TableColumn column = new TableColumn (finTable, SWT.NONE);
			FinPeriodData fd = finDataMap.get(s);
			column.setText(s);
			revItem.setText(i, formatBdWhole(fd.getRevenue()));
			gmItem.setText(i, formatBdPct(fd.getGrossMargin()));
			oiItem.setText(i, formatBdWhole(fd.getOperatingIncome()));
			omItem.setText(i, formatBdPct(fd.getOperatingMargin()));
			niItem.setText(i, formatBdWhole(fd.getNetIncome()));
			epsItem.setText(i, formatBd(fd.getEarningsPerShare()));
			dItem.setText(i, formatBd(fd.getDividends()));
			prItem.setText(i, formatBdPct(fd.getPayoutRatio()));
			sItem.setText(i, formatBdWhole(fd.getShares()));
			bvItem.setText(i, formatBd(fd.getBookValuePerShare()));
			ocfItem.setText(i, formatBdWhole(fd.getOperatingCashFlow()));
			csItem.setText(i, formatBdWhole(fd.getCapitalSpending()));
			fcfItem.setText(i, formatBdWhole(fd.getFreeCashFlow()));						
			fcfpsItem.setText(i, formatBd(fd.getFreeCashFlowPerShare()));
			wcItem.setText(i, formatBdWhole(fd.getWorkingCapital()));
			i++;
		}

		for (TableColumn tc : finTable.getColumns()){
			tc.pack();
		}
		parent.layout(true, true);
	}

	public String formatBdWhole(BigDecimal bd) {
		return  bd == null ? "" : new DecimalFormat("###,###,###,##0").format(bd);
	}

	public String formatBd(BigDecimal bd) {
		return  bd == null ? "" : new DecimalFormat("###,###,###,##0.00").format(bd);
	}

	public String formatBdPct(BigDecimal bd) {
		return  bd == null ? "" : new DecimalFormat("###,##0.00").format(bd) + "%";
	}
}