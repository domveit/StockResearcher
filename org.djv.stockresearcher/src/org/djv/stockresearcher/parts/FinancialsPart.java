 
package org.djv.stockresearcher.parts;

import java.util.HashMap;
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
	
	@Inject
	private StockDB db;
	
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
		try {
			buildFinTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Focus
	public void onFocus() {
	}

	@Override
	public void notifyChanged(AppState appState) {
		try {
			buildFinTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildFinTable() throws Exception {
		if (finTable != null){
			finTable.dispose();
			finTable = null;
		}
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				final StockData sd = AppState.getInstance().getSelectedStock();
				try {
					db.getFinData(sd);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
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
						
//				    	Revenue USD Mil,"18,878","22,045","24,801","28,484","34,922","39,540","36,117","40,040","43,218","46,061","47,880"
//				    	Gross Margin %,70.1,68.6,67.2,65.8,64.0,64.5,63.9,64.0,61.4,61.2,60.9
//				    	Operating Income USD Mil,"4,882","6,292","7,416","6,996","8,621","9,442","7,322","9,164","7,674","10,065","10,753"
//				    	Operating Margin %,25.9,28.5,29.9,24.6,24.7,23.9,20.3,22.9,17.8,21.9,22.5
//				    	Net Income USD Mil,"3,578","4,401","5,741","5,580","7,333","8,052","6,134","7,767","6,490","8,041","9,630"
//				    	Earnings Per Share USD,0.50,0.62,0.87,0.89,1.17,1.31,1.05,1.33,1.17,1.49,1.80
//				    	Dividends USD,,,,,,,,,0.12,0.28,0.53
//				    	Payout Ratio %,,,,,,,,,10.3,18.8,29.5
//				    	Shares Mil,"7,223","7,057","6,612","6,272","6,265","6,163","5,857","5,848","5,563","5,404","5,361"
//				    	Book Value Per Share USD,,3.82,3.63,3.94,5.17,5.83,6.68,7.83,8.69,9.68,10.63
//				    	Operating Cash Flow USD Mil,"5,240","7,121","7,568","7,899","10,104","12,089","9,897","10,173","10,079","11,491","11,996"
//				    	Cap Spending USD Mil,-717,-613,-692,-772,"-1,251","-1,268","-1,005","-1,008","-1,174","-1,126","-1,139"
//				    	Free Cash Flow USD Mil,"4,523","6,508","6,876","7,127","8,853","10,821","8,892","9,165","8,905","10,365","10,857"
//				    	Free Cash Flow Per Share USD,,0.92,1.04,1.14,1.41,1.76,1.52,1.57,1.60,1.92,
//				    	Working Capital USD Mil,"5,121","5,640","3,520","14,363","18,216","21,841","30,522","32,188","39,725","44,202",
						
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
						
//				    	Shares Mil,"7,223","7,057","6,612","6,272","6,265","6,163","5,857","5,848","5,563","5,404","5,361"
//				    	Book Value Per Share USD,,3.82,3.63,3.94,5.17,5.83,6.68,7.83,8.69,9.68,10.63
//				    	Operating Cash Flow USD Mil,"5,240","7,121","7,568","7,899","10,104","12,089","9,897","10,173","10,079","11,491","11,996"
//				    	Cap Spending USD Mil,-717,-613,-692,-772,"-1,251","-1,268","-1,005","-1,008","-1,174","-1,126","-1,139"
//				    	Free Cash Flow USD Mil,"4,523","6,508","6,876","7,127","8,853","10,821","8,892","9,165","8,905","10,365","10,857"
//				    	Free Cash Flow Per Share USD,,0.92,1.04,1.14,1.41,1.76,1.52,1.57,1.60,1.92,
//				    	Working Capital USD Mil,"5,121","5,640","3,520","14,363","18,216","21,841","30,522","32,188","39,725","44,202",
						
						
						int i = 1;
						for (String s : finDataMap.keySet()){
							TableColumn column = new TableColumn (finTable, SWT.NONE);
							FinPeriodData fd = finDataMap.get(s);
							column.setText(s);
							revItem.setText(i, fd.getRevenue());
							gmItem.setText(i, fd.getGrossMargin() + "%");
							oiItem.setText(i, fd.getOperatingIncome());
							omItem.setText(i, fd.getOperatingMargin() + "%");
							niItem.setText(i, fd.getNetIncome());
							epsItem.setText(i, fd.getEarningsPerShare());
							dItem.setText(i, fd.getDividends());
							prItem.setText(i, fd.getPayoutRatio() + "%");
							sItem.setText(i, fd.getShares());
							bvItem.setText(i, fd.getBookValuePerShare());
							ocfItem.setText(i, fd.getOperatingCashFlow());
							csItem.setText(i, fd.getCapitalSpending());
							fcfItem.setText(i, fd.getFreeCashFlow());						
							fcfpsItem.setText(i, fd.getFreeCashFlowPerShare());
							wcItem.setText(i, fd.getWorkingCapital());
							i++;
						}
						
						for (TableColumn tc : finTable.getColumns()){
							tc.pack();
						}
						
						parent.layout(true, true);
					}
				});
			}
		});
		t.start();
	}
}