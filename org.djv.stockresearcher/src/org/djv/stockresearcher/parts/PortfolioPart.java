 
package org.djv.stockresearcher.parts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Portfolio;
import org.djv.stockresearcher.model.PortfolioData;
import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.model.TransactionData;
import org.djv.stockresearcher.widgets.PortfolioDialog;
import org.djv.stockresearcher.widgets.TransactionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class PortfolioPart {
	
	TabFolder folder;
	private Combo portfolioSelector;
	private Button newButton;
	private Button deleteButton;
	
	private Button newTranButton;
	private Button deleteTranButton;
	
	String[] titles = {"Date", "Action", "Symbol", "Shares", "Price Paid", "Cost", "Current Price", "Value", "Gain/Loss", "Gain/Loss Pct"};
	Table table;
	
	private Shell shell;
	
	@PostConstruct
	public void postConstruct(final Composite parent) {
		this.shell = parent.getShell();
	    
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(3, false));
		
		portfolioSelector = new Combo(parent, SWT.BORDER);
		portfolioSelector.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		portfolioSelector.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectPortfolio();
			}
		});
		
		newButton = new Button(parent, SWT.NONE);
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
		
		deleteButton = new Button(parent, SWT.NONE);
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
		
	    folder = new TabFolder(parent, SWT.NONE);
	    folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	    
	    TabItem tab1 = new TabItem(folder, SWT.NONE);
	    tab1.setText("Transactions");
		Composite c = createTransactionsTab(folder);
		tab1.setControl(c);
		
		updatePortfolioList();
		for (int i=0; i< titles.length; i++) {
			table.getColumn (i).pack ();
		}
		
	}

	public Composite createTransactionsTab(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c.setLayout(new GridLayout(3, false));
 
		newTranButton = new Button(c, SWT.NONE);
		newTranButton.setText("New Transaction");
		newTranButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		
		newTranButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portfolioName = portfolioSelector.getText();
				if ("".equals(portfolioName)){
					MessageDialog.openError(parent.getShell(), "Error", "No portfolio selected.");
					return;
				}
				TransactionDialog td = new TransactionDialog(parent.getShell());
				td.create();
				int result = td.open();
				if (result == Window.OK) {
					try {
						
						Transaction t = new Transaction();
						
						t.setAction(td.getAction());
						t.setPrice(td.getPrice());
						t.setShares(td.getShares());
						t.setSymbol(td.getSymbol());
						t.setTranDate(new java.sql.Date(td.getTranDate().getTime()));
								
						StockDB.getInstance().createNewTransaction(portfolioName, t);
						selectPortfolio();
					} catch (Exception e1) {
						MessageDialog.openError(parent.getShell(), "Error", e1.getMessage());
					}
				} 
			}

		});
		
		deleteTranButton = new Button(c, SWT.NONE);
		deleteTranButton.setText("Delete Transaction");
		deleteTranButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		
		deleteTranButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = table.getSelectionIndex();
				if (i < 0) {
					return;
				}
				TransactionData td = (TransactionData)table.getItem(i).getData("tran");
				if (td != null){
					boolean result = MessageDialog.openConfirm(shell, "Are you sure?", "Are you sure?");
					if (result){
						try {
							StockDB.getInstance().deleteTransaction(td.getTransaction().getId());
							selectPortfolio();
						} catch (Exception e1) {
							MessageDialog.openError(parent.getShell(), "Error", e1.getMessage());
						}
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
		
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = table.getSelectionIndex();
				if (i < 0) {
					return;
				}
				TransactionData td = (TransactionData)table.getItem(i).getData("tran");
				if (td != null){
					AppState.getInstance().setSelectedStock(td.getStockData());
				}
			}
		});
		return c;
	}

	public Combo getPortfolioSelector() {
		return portfolioSelector;
	}


	public void updatePortfolioList() {
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
	
	public void selectPortfolio() {
		table.removeAll();
		String portfolioName = portfolioSelector.getText();
		if ("".equals(portfolioName)){
			return;
		}
		try {
			PortfolioData portData = StockDB.getInstance().getPortfolioData(portfolioName);
			for (TransactionData td : portData.getTransactionList()){
				TableItem item = new TableItem (table, SWT.NONE);
				item.setData("tran", td);
				
				BigDecimal shares = td.getTransaction().getShares();
				BigDecimal currPrice = td.getStockData().getStock().getPrice();
				BigDecimal tranPrice = td.getTransaction().getPrice();
				BigDecimal tranCost = tranPrice.multiply(shares);
				BigDecimal tranValue = currPrice.multiply(shares);
				BigDecimal gainOrLoss = tranValue.subtract(tranCost);
				
//				String[] titles = {"TranId", "Action", "Date", "Symbol", "Price", "Shares"};
				item.setText (0, String.valueOf(td.getTransaction().getTranDate()));
				item.setText (1, td.getTransaction().getActionText());
				item.setText (2, td.getTransaction().getSymbol());
				
				item.setText (3, new DecimalFormat("0.00").format(shares));
				item.setText (4, new DecimalFormat("0.00").format(tranPrice));
				item.setText (5, new DecimalFormat("0.00").format(tranCost));
				item.setText (6, new DecimalFormat("0.00").format(currPrice));
				item.setText (7, new DecimalFormat("0.00").format(tranValue));
				item.setText (8, new DecimalFormat("0.00").format(gainOrLoss));
				item.setText (9, new DecimalFormat("0.00").format(tranValue.subtract(tranCost).multiply(new BigDecimal(100)).divide(tranCost, 2, RoundingMode.HALF_UP)) + "%");
				
				if (tranPrice.compareTo(currPrice) > 0){
					item.setBackground(6, new Color(Display.getDefault(), 255, 0, 0));
					item.setBackground(7, new Color(Display.getDefault(), 255, 0, 0));
					item.setBackground(8, new Color(Display.getDefault(), 255, 0, 0));
					item.setBackground(9, new Color(Display.getDefault(), 255, 0, 0));
				} else {
					item.setBackground(6, new Color(Display.getDefault(), 0, 255, 0));
					item.setBackground(7, new Color(Display.getDefault(), 0, 255, 0));
					item.setBackground(8, new Color(Display.getDefault(), 0, 255, 0));
					item.setBackground(9, new Color(Display.getDefault(), 0, 255, 0));
				}
			}
			for (int i=0; i< titles.length; i++) {
				table.getColumn (i).pack ();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
		
	}
}