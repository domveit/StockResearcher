 
package org.djv.stockresearcher.parts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Portfolio;
import org.djv.stockresearcher.model.PortfolioData;
import org.djv.stockresearcher.model.Position;
import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.model.TransactionData;
import org.djv.stockresearcher.widgets.PortfolioDialog;
import org.djv.stockresearcher.widgets.TransactionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class PortfolioPart {
	
	ScrolledComposite sc;
	Composite overview;
	
	TabFolder folder;
	private Combo portfolioSelector;
	private Button newButton;
	private Button deleteButton;
	
	private Button newTranButton;
	private Button deleteTranButton;
	
	String[] titles = {"Date", "Action", "Symbol", "Shares", "Price Paid", "Cost"};
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
	    tab1.setText("Overview");
		Composite c1 = createOverviewTab(folder);
		tab1.setControl(c1);
	    
	    TabItem tab2 = new TabItem(folder, SWT.NONE);
	    tab2.setText("Transactions");
		Composite c2 = createTransactionsTab(folder);
		tab2.setControl(c2);
		
		updatePortfolioList();
		for (int i=0; i< titles.length; i++) {
			table.getColumn (i).pack ();
		}
		
	}

	private Composite createOverviewTab(TabFolder parent) {
		sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sc.setLayout(new GridLayout());
		overview = new Composite(sc, SWT.BORDER);
		overview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sc.setContent(overview);
		sc.setMinSize(overview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setAlwaysShowScrollBars(true);
		return sc;
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
			for (Control c : overview.getChildren()){
				c.dispose();
			}
			
			PortfolioData portData = StockDB.getInstance().getPortfolioData(portfolioName);
			
			overview.setLayout(new GridLayout(8, false));
			
			Label h1 = new Label(overview, SWT.LEFT);
			h1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h1.setText("Sector");
			h1.setBackground(new Color(Display.getDefault(), 195, 195, 195));

			Label h2 = new Label(overview, SWT.LEFT);
			h2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h2.setText("Symbol");
			h2.setBackground(new Color(Display.getDefault(), 195, 195, 195));
			
			Label h3 = new Label(overview, SWT.LEFT);
			h3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h3.setText("Shares");
			h3.setBackground(new Color(Display.getDefault(), 195, 195, 195));
			
			Label h4 = new Label(overview, SWT.LEFT);
			h4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h4.setText("Cost");
			h4.setBackground(new Color(Display.getDefault(), 195, 195, 195));
			
			Label h5 = new Label(overview, SWT.LEFT);
			h5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h5.setText("Basis");
			h5.setBackground(new Color(Display.getDefault(), 195, 195, 195));
			
			Label h6 = new Label(overview, SWT.LEFT);
			h6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h6.setText("Price");
			h6.setBackground(new Color(Display.getDefault(), 195, 195, 195));
			
			Label h7 = new Label(overview, SWT.LEFT);
			h7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h7.setText("Value");
			h7.setBackground(new Color(Display.getDefault(), 195, 195, 195));
			
			Label h8 = new Label(overview, SWT.LEFT);
			h8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			h8.setText("Div");
			h8.setBackground(new Color(Display.getDefault(), 195, 195, 195));
			
			for (Integer sector : portData.getPositionMap().keySet()){
				boolean firstRow = true;
				Map<String, Position> pMap = portData.getPositionMap().get(sector);
				for (String sym : pMap.keySet()){
					if (firstRow){
						Label sh1 = new Label(overview, SWT.NONE);
						sh1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
						sh1.setText(StockDB.getInstance().getSectorName(sector));
						sh1.setBackground(new Color(Display.getDefault(), 153, 217, 234));
						firstRow = false;
					} else {
						Label l1 = new Label(overview, SWT.NONE);
						l1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
						l1.setText("");
					}
					Position p = pMap.get(sym);
					
					Label l2 = new Label(overview, SWT.NONE);
					l2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					l2.setText(p.getSd().getStock().getSymbol());
					
					Label l3 = new Label(overview, SWT.NONE);
					l3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					l3.setText(new DecimalFormat("0.0000").format(p.getShares()));
					
					Label l4 = new Label(overview, SWT.NONE);
					l4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					l4.setText(new DecimalFormat("0.00").format(p.getCost()));
					
					Label l5 = new Label(overview, SWT.NONE);
					l5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					l5.setText(new DecimalFormat("0.00").format(p.getBasis()));
					
					Label l6 = new Label(overview, SWT.NONE);
					l6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					l6.setText(new DecimalFormat("0.00").format(p.getSd().getStock().getPrice()));
					
					Label l7 = new Label(overview, SWT.NONE);
					l7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					l7.setText(new DecimalFormat("0.00").format(p.getValue()));
					
					Label l8 = new Label(overview, SWT.NONE);
					l8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					l8.setText(new DecimalFormat("0.00").format(p.getSd().getNormDividend()));

				}
			}
			overview.layout(true);
			sc.setMinSize(overview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			sc.layout(true);
				
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
//				}
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