 
package org.djv.stockresearcher.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Portfolio;
import org.djv.stockresearcher.model.PortfolioData;
import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.widgets.PortfolioDialog;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class PortfolioPart {
	
	private Combo portfolioSelector;
	private Button newButton;
	private Button deleteButton;
	
	String[] titles = {"TranId", "Action", "Date", "Symbol", "Price", "Shares"};
	Table table;
	
	private Shell shell;
	
	@PostConstruct
	public void postConstruct(final Composite parent) {
		this.shell = parent.getShell();
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c.setLayout(new GridLayout(3, false));
		
		portfolioSelector = new Combo(c, SWT.BORDER);
		portfolioSelector.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		portfolioSelector.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectPortfolio();
			}
		});
		
		
		newButton = new Button(c, SWT.NONE);
		newButton.setText("New Portfolio");
		newButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		
		newButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				PortfolioDialog pd = new PortfolioDialog(parent.getShell());
				pd.create();
				int result = pd.open();
				if (result == Window.OK) {
					try {
						StockDB.getInstance().createNewPortfolio(pd.getName());
						updatePortfolioList();
						portfolioSelector.setText(pd.getName());
						selectPortfolio();
						
					} catch (Exception e1) {
						MessageDialog.openError(parent.getShell(), "Error", e1.getMessage());
					}
				} 
			}

		});
		
		deleteButton = new Button(c, SWT.NONE);
		deleteButton.setText("Delete Portfolio");
		deleteButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		
		deleteButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portfolioName = portfolioSelector.getText();
				if ("".equals(portfolioName)){
					return;
				}
				boolean result = MessageDialog.openConfirm(shell, "Are you sure?", "Delete \"" + portfolioName + "\".  Are you sure?");
				if (result){
					try {
						StockDB.getInstance().deletePortfolio(portfolioName);
						updatePortfolioList();
						portfolioSelector.setText("");
						selectPortfolio();
					} catch (Exception e1) {
						MessageDialog.openError(parent.getShell(), "Error", e1.getMessage());
					}
				}
			}

		});
		
		table = new Table (c, SWT.MULTI | SWT.NONE | SWT.FULL_SELECTION);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
//			column.addListener(SWT.Selection, sortListener);
		}	
		
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		updatePortfolioList();
		for (int i=0; i< titles.length; i++) {
			table.getColumn (i).pack ();
		}
		
	}

	private void updatePortfolioList() {
		try {
			List<String> list = new ArrayList<String>();
			list.add("");
			List<Portfolio> l = StockDB.getInstance().getPortfolioList();
			for (Portfolio p : l){
				list.add(p.getName());
			}
			portfolioSelector.setItems(list.toArray(new String[0]));
			portfolioSelector.getParent().layout();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
		}		
	}
	
	private void selectPortfolio() {
		table.removeAll();
		String portfolioName = portfolioSelector.getText();
		if ("".equals(portfolioName)){
			return;
		}
		try {
			PortfolioData portData = StockDB.getInstance().getPortfolioData(portfolioName);
			for (Transaction t : portData.getTransactionList()){
				TableItem item = new TableItem (table, SWT.NONE);
				item.setData("tran", t);
				
//				String[] titles = {"TranId", "Action", "Date", "Symbol", "Price", "Shares"};
				item.setText (0, String.valueOf(t.getId()));
				item.setText (1, t.getAction());
				item.setText (2, String.valueOf(t.getTranDate()));
				item.setText (3, t.getSymbol());
				item.setText (4, String.valueOf(t.getPrice()));
				item.setText (5, String.valueOf(t.getShares()));
			}
		} catch (Exception e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
		
	}
}