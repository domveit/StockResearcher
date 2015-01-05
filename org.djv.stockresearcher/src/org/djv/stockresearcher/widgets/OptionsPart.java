 
package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.model.Option;
import org.djv.stockresearcher.model.OptionPeriod;
import org.djv.stockresearcher.model.OptionTable;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.service.OptionListener;
import org.djv.stockresearcher.service.OptionService;
import org.djv.stockresearcher.util.DateUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class OptionsPart implements AppStateListener, OptionListener {
	
	Label label;
	Combo strikeDateCombo;
	Combo nbrStrikesCombo;
	Combo putsCallsCombo;
	Table table;
	StockData currentSd = null;
	Composite parent;
	
	String[] titles = {"Strike", "Bid", "Ask", "Basis", "IValue", "TValue", "Days", "Return", "A. Ret"};
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(4, false));
		
		strikeDateCombo = new Combo(parent, SWT.READ_ONLY);
		strikeDateCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		strikeDateCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				String s = strikeDateCombo.getText();
				Date d = null;
				try {
					d = new SimpleDateFormat("MM/dd/yyyy").parse(s);
				} catch (ParseException e1) {
					e1.printStackTrace();
					return;
				}
				OptionTable ot = (OptionTable) strikeDateCombo.getData("ot");
				if (ot == null){
					return;
				}
				OptionPeriod op = new OptionPeriod(1900 + d.getYear(), d.getMonth() + 1, d.getDate());
//				System.err.println("op = " + op);
				ot.setCurrentPeriod(op);
				label.setText(currentSd.getStock().getSymbol() + " - " + (currentSd.getStockIndustry() == null ? "???" :  (currentSd.getStockIndustry().getName() + " - " + currentSd.getStock().getPrice())) + " getting options data....");
				
				OptionService.getInstance().getOptionCallsForCurrentPeriod(ot);
			}
		});
		
		nbrStrikesCombo = new Combo(parent, SWT.READ_ONLY);
		nbrStrikesCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		nbrStrikesCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				OptionTable ot = (OptionTable) strikeDateCombo.getData("ot");
				if (ot == null){
					return;
				}
				updateTable(ot);
			}
		});
		for (int i = 0; i < 20; i ++){
			nbrStrikesCombo.add(String.valueOf(i));
		}
		nbrStrikesCombo.setText("5");
		
		putsCallsCombo = new Combo(parent, SWT.READ_ONLY);
		putsCallsCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		putsCallsCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				OptionTable ot = (OptionTable) strikeDateCombo.getData("ot");
				if (ot == null){
					return;
				}
				updateTable(ot);
			}
		});
		
		putsCallsCombo.add("Calls");
		putsCallsCombo.add("Puts");
		putsCallsCombo.setText("Calls");
		
		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		table = new Table (parent, SWT.MULTI | SWT.NONE | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
		}	
		
		AppState.getInstance().addListener(this);
		OptionService.getInstance().addListener(this);
		this.parent = parent;
		updateStock();
	}
	
	@Override
	public void notifyChanged(AppState appState) {
		updateStock();
	}

	public void updateStock() {
		final StockData sd = AppState.getInstance().getSelectedStock();
		if (sd == currentSd){
			return;
		}
		currentSd = sd;
		label.setText(sd.getStock().getSymbol() + " - " + (sd.getStockIndustry() == null ? "???" :  (sd.getStockIndustry().getName() + " - " + sd.getStock().getPrice())) + " getting options data....");

		OptionService.getInstance().getOptionTable(sd.getStock().getSymbol());
	}
	
	@Override
	public void notifyChanged(final OptionTable ot, final int type) {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				if (type == OptionListener.TYPE_NEW) {
					strikeDateCombo.removeAll();
					for (OptionPeriod op : ot.getPeriods()){
						String key = new SimpleDateFormat("MM/dd/yyyy").format(op.getDate());
						strikeDateCombo.add(key);
					}
					strikeDateCombo.setData("ot", ot);
					OptionPeriod currentPeriod = ot.getCurrentPeriod();
					if (currentPeriod == null){
						strikeDateCombo.setText("");
					} else {
						String key = new SimpleDateFormat("MM/dd/yyyy").format(currentPeriod.getDate());
						strikeDateCombo.setText(key);
					}
				} 
				
				updateTable(ot);
				label.setText(currentSd.getStock().getSymbol() + " - " + (currentSd.getStockIndustry() == null ? "???" :  (currentSd.getStockIndustry().getName() + " - " + currentSd.getStock().getPrice())));
				nbrStrikesCombo.getParent().layout(true);
			}
		});
	}
	
	public void updateTable(final OptionTable ot) {
		table.removeAll();
		
		List<Option> list = null;
				
		String s = putsCallsCombo.getText();
		if ("Calls".equals(s)){
			list = ot.getCallsForCurrentPeriod();
		} else {
			list = ot.getPutsForCurrentPeriod();
		}
		if (list == null){
			return;
		}
		
		int closestIx = 0;
		double distanceFromPrice = 99999d;
		for (int ix = 0; ix < list.size(); ix++){
			Option o = list.get(ix);
			double thisDistance = Math.abs((o.getStrike().subtract(currentSd.getStock().getPrice()).doubleValue()));
			if (thisDistance < distanceFromPrice){
				distanceFromPrice = thisDistance;
				closestIx = ix;
			}
		}
		
		String ns = nbrStrikesCombo.getText();
		int nbrStrikes = Integer.valueOf(ns);
		
		List<Option> trimmedList = list.subList(Math.max(closestIx - nbrStrikes, 0), Math.min(closestIx + nbrStrikes + 1, list.size()));
		
		for (Option o : trimmedList){
			TableItem item = new TableItem (table, SWT.NONE);
			
	//	String[] titles = {"Expiration Date", "Symbol", "Strike", "Bid", "Ask", "Basis", "IValue", "TValue", "Days", "Return", "A. Ret"};
			
			BigDecimal basis= currentSd.getStock().getPrice().min(o.getStrike());
			BigDecimal iValue= currentSd.getStock().getPrice().subtract(basis);
			if ("P".equals(o.getType())){
				basis= currentSd.getStock().getPrice().max(o.getStrike());
				iValue= basis.subtract(currentSd.getStock().getPrice());
			}
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
			
			item.setText (0, o.getStrike() == null ? "N/A" : new DecimalFormat("0.00").format(o.getStrike()));
			item.setText (1, o.getBid() == null ? "N/A" : new DecimalFormat("0.00").format(o.getBid()));
			item.setText (2, o.getAsk() == null ? "N/A" : new DecimalFormat("0.00").format(o.getAsk()));
			item.setText (3, basis == null ? "N/A" : new DecimalFormat("0.00").format(basis));
			item.setText (4, iValue == null ? "N/A" : new DecimalFormat("0.00").format(iValue));
			item.setText (5, tValue == null ? "N/A" : new DecimalFormat("0.00").format(tValue));
			item.setText (6, String.valueOf(days));
			item.setText (7, retrn == null ? "N/A" : new DecimalFormat("0.00").format(retrn) + "%");
			item.setText (8, annRet == null ? "N/A" : new DecimalFormat("0.00").format(annRet) + "%");
			if (iValue.compareTo(BigDecimal.ZERO) > 0){
				item.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
			}
		}
		
		for (TableColumn col : table.getColumns()){
			col.pack();
		}
	}
}