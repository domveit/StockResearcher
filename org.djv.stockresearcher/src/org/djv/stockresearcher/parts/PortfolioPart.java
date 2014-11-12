 
package org.djv.stockresearcher.parts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Lot;
import org.djv.stockresearcher.model.Portfolio;
import org.djv.stockresearcher.model.PortfolioData;
import org.djv.stockresearcher.model.Position;
import org.djv.stockresearcher.model.SectorIndustry;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.model.TransactionData;
import org.djv.stockresearcher.model.TransactionType;
import org.djv.stockresearcher.util.Colors;
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
import org.eclipse.swt.widgets.Display;
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
	private Button refreshButton;
	
	private Button newTranButton;
	private Button editTranButton;
	private Button deleteTranButton;
	
	String[] tranTitles = {"Date", "Action", "Symbol", "Shares", "Price Paid", "Commission", "Premium", "Basis", "Basis PS", "Cost", "Balance"};
	Table tranTable;
	
	String[] posTitles = {"Sector", "Symbol", "Shares", "Basis", "Price", "Cost", "Value", "Gain", "Gain Pct" ,"Div", "Yield", "YOC", "Weight"};
	Table posTable;
	
	PortfolioData portData;
	
	List<String> showLots = new ArrayList<String>();
	
	private Shell shell;
	
	@PostConstruct
	public void postConstruct(final Composite parent) {
		this.shell = parent.getShell();
	    
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(4, false));
		
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
		
		refreshButton = new Button(parent, SWT.NONE);
		refreshButton.setText("Refresh");
		refreshButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		
		refreshButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portfolioName = portfolioSelector.getText();
				if ("".equals(portfolioName)){
					return;
				}
				try {
					selectPortfolio();
				} catch (Exception e1) {
					MessageDialog.openError(parent.getShell(), "Error", e1.getMessage());
				}
			}
		});
		
	    folder = new TabFolder(parent, SWT.NONE);
	    folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
	    
	    TabItem tab1 = new TabItem(folder, SWT.NONE);
	    tab1.setText("Overview");
		Composite c1 = createOverviewTab(folder);
		tab1.setControl(c1);
	    
	    TabItem tab2 = new TabItem(folder, SWT.NONE);
	    tab2.setText("Transactions");
		Composite c2 = createTransactionsTab(folder);
		tab2.setControl(c2);
		
		updatePortfolioList();
		for (int i=0; i< tranTitles.length; i++) {
			tranTable.getColumn (i).pack ();
		}
		
	}

	private Composite createOverviewTab(TabFolder parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c.setLayout(new GridLayout(1, false));
 
		posTable = new Table (c, SWT.MULTI | SWT.NONE | SWT.FULL_SELECTION);
		posTable.setLinesVisible (true);
		posTable.setHeaderVisible (true);
		
		for (int i=0; i<posTitles.length; i++) {
			TableColumn column = new TableColumn (posTable, SWT.NONE);
			column.setText (posTitles [i]);
		}	
		posTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		posTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = posTable.getSelectionIndex();
				if (i < 0) {
					return;
				}
				StockData sd = (StockData)posTable.getItem(i).getData("sd");
				if (sd != null){
					AppState.getInstance().setSelectedStock(sd);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				int i = posTable.getSelectionIndex();
				if (i < 0) {
					return;
				}
				StockData sd = (StockData)posTable.getItem(i).getData("sd");
				if (sd == null){
					return;
				}
				if (showLots.contains(sd.getSymbol())){
					showLots.remove(sd.getSymbol());
				} else {
					showLots.add(sd.getSymbol());
				}
				if (portData != null) {
					try {
						rebuildPortfolioTable(portData);
					} catch (Exception e1) {
						MessageDialog.openError(posTable.getShell(), "Error", e1.getMessage());
					}
				}
				
			}
		});
		
		return c;
	}

	public Composite createTransactionsTab(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c.setLayout(new GridLayout(5, false));
 
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
				
				Transaction t = new Transaction();
				td.setTransaction(t);
				
				StockData sd = AppState.getInstance().getSelectedStock();
				if (sd != null){
					t.setSymbol(sd.getSymbol());
				}
				td.create();
				td.setMode("NEW");
				int result = td.open();
				if (result == Window.OK) {
					try {
						t = td.getTransaction();
						StockDB.getInstance().createNewTransaction(portfolioName, t);
						selectPortfolio();
					} catch (Exception e1) {
						MessageDialog.openError(parent.getShell(), "Error", e1.getMessage());
					}
				} 
			}

		});
		
		editTranButton = new Button(c, SWT.NONE);
		editTranButton.setText("Edit Transaction");
		editTranButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		
		editTranButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tranTable.getSelectionIndex();
				if (i < 0) {
					return;
				}
				TransactionData td = (TransactionData)tranTable.getItem(i).getData("tran");
				Transaction t = td.getTransaction();
				
				TransactionDialog tdialog = new TransactionDialog(parent.getShell());
				tdialog.setTransaction(t);
				tdialog.setMode("EDIT");
				tdialog.create();
				int result = tdialog.open();
				if (result == Window.OK) {
					try {
						t = tdialog.getTransaction();
						StockDB.getInstance().updateTransaction(t);
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
				int i = tranTable.getSelectionIndex();
				if (i < 0) {
					return;
				}
				TransactionData td = (TransactionData)tranTable.getItem(i).getData("tran");
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
		
		tranTable = new Table (c, SWT.MULTI | SWT.NONE | SWT.FULL_SELECTION);
		tranTable.setLinesVisible (true);
		tranTable.setHeaderVisible (true);
		
		for (int i=0; i<tranTitles.length; i++) {
			TableColumn column = new TableColumn (tranTable, SWT.NONE);
			column.setText (tranTitles [i]);
//			column.addListener(SWT.Selection, sortListener);
		}	
		
		tranTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
		
		tranTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = tranTable.getSelectionIndex();
				if (i < 0) {
					return;
				}
				TransactionData td = (TransactionData)tranTable.getItem(i).getData("tran");
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
		try {
			String portfolioName = portfolioSelector.getText();
			if ("".equals(portfolioName)){
				return;
			}
			portData = StockDB.getInstance().getPortfolioData(portfolioName);
			rebuildPortfolioTable(portData);
			rebuildTranTable(portData);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
		
	}

	private void rebuildTranTable(PortfolioData portData) {
		tranTable.removeAll();
		
		for (TransactionData td : portData.getTransactionList()){
			TableItem item = new TableItem (tranTable, SWT.NONE);
			item.setData("tran", td);
			
			BigDecimal shares = td.getTransaction().getShares();
			BigDecimal tranPrice = td.getTransaction().getPrice();
			BigDecimal commission = td.getTransaction().getCommission();

			item.setText (0, String.valueOf(td.getTransaction().getTranDate()));
			item.setText (1, TransactionType.getDisplayFromCode(td.getTransaction().getType()));
			item.setText (2, td.getTransaction().getSymbol());
			item.setText (3, new DecimalFormat("#,##0.00##").format(shares));
			item.setText (4, new DecimalFormat("#,##0.00####").format(tranPrice));
			item.setText (5, new DecimalFormat("#,##0.00##").format(commission));
			item.setText (6, new DecimalFormat("#,##0.00##").format(td.getTransaction().getPremium()));
			item.setText (7, new DecimalFormat("#,##0.00##").format(td.getBasis()));
			item.setText (8, new DecimalFormat("#,##0.00####").format(td.getBasisPerShare()));
			item.setText (9, new DecimalFormat("#,###,##0.00").format(td.getCost()));
			item.setText (10, new DecimalFormat("#,###,##0.00").format(td.getCashBalance()));
		}
		for (int i=0; i< tranTitles.length; i++) {
			tranTable.getColumn (i).pack ();
		}
	}

	private void rebuildPortfolioTable(PortfolioData portData) throws Exception {
		posTable.removeAll();
		
		BigDecimal totalCost = new BigDecimal("0.0000");
		BigDecimal totalValue = new BigDecimal("0.00");
		BigDecimal totalDiv = new BigDecimal("0.00");
		BigDecimal totalGain = new BigDecimal("0.00");
		
		for (Integer sector : portData.getPositionMap().keySet()){
			Map<String, Position> pMap = portData.getPositionMap().get(sector);
			for (String sym : pMap.keySet()){
				Position p = pMap.get(sym);
				
				BigDecimal gainCalc = p.getValue().subtract(p.getCost());
				BigDecimal divCalc = null;
				if (p.getSd().getStock().getDividend() != null) {
					divCalc = p.getSd().getStock().getDividend().multiply(p.getShares());
				} else {
					divCalc = BigDecimal.ZERO;
				}
				
				totalCost = totalCost.add(p.getCost());
				totalValue = totalValue.add(p.getValue());
				totalDiv = totalDiv.add(divCalc);
				totalGain = totalGain.add(gainCalc);
			}
		}
		
		totalCost = totalCost.add(portData.getCashBalance());
		totalValue = totalValue.add(portData.getCashBalance());
		
		for (Integer sector : portData.getPositionMap().keySet()){
			BigDecimal sectorCost = new BigDecimal("0.00");
			BigDecimal sectorValue = new BigDecimal("0.00");
			BigDecimal sectorDiv = new BigDecimal("0.00");
			BigDecimal sectorGain = new BigDecimal("0.00");
			
			SectorIndustry industry = StockDB.getInstance().getIndustry(sector);
			String sectorStr = ((industry == null) ? "" : industry.getSectorName());
			
			Map<String, Position> pMap = portData.getPositionMap().get(sector);
			
			for (String sym : pMap.keySet()){
				Position p = pMap.get(sym);
				TableItem item = new TableItem (posTable, SWT.NONE);
				item.setData("sd", p.getSd());
				
				String symbol = p.getSd().getStock().getSymbol();
				String shares = new DecimalFormat("#,##0.00##").format(p.getShares());
				String basis = new DecimalFormat("#,##0.00####").format(p.getBasisPerShare());
				String price = new DecimalFormat("#,##0.00####").format(p.getSd().getStock().getPrice());
				String cost = new DecimalFormat("#,###,##0.00").format(p.getCost());
				String value = new DecimalFormat("#,###,##0.00").format(p.getValue());
				
				BigDecimal gainCalc = p.getValue().subtract(p.getCost());
				String gain = new DecimalFormat("#,###,##0.00").format(gainCalc);
				
				BigDecimal gainPctCalc = gainCalc.multiply(BigDecimal.valueOf(100)).divide(p.getCost(), 2, RoundingMode.HALF_UP);
				String gainPct = new DecimalFormat("0.00").format(gainPctCalc) + "%";
									
				BigDecimal divCalc = null ;
				if (p.getSd().getStock().getDividend() != null) {
					divCalc = p.getSd().getStock().getDividend().multiply(p.getShares());
				} else {
					divCalc =  BigDecimal.ZERO;
				}
				String div = new DecimalFormat("#,###,##0.00").format(divCalc);
				
				BigDecimal yCalc = divCalc.multiply(BigDecimal.valueOf(100)).divide(p.getValue(), 2, RoundingMode.HALF_UP);
				String y = new DecimalFormat("0.00").format(yCalc)+ "%";
				
				BigDecimal yocCalc = divCalc.multiply(BigDecimal.valueOf(100)).divide(p.getCost(), 2, RoundingMode.HALF_UP);
				String yoc = new DecimalFormat("0.00").format(yocCalc)+ "%";
				
				BigDecimal wCalc = p.getValue().multiply(BigDecimal.valueOf(100)).divide(totalValue, 2, RoundingMode.HALF_UP);
				String w = new DecimalFormat("0.00").format(wCalc)+ "%";
				
				item.setText (0, sectorStr);
				item.setText (1, symbol);
				item.setText (2, shares);
				item.setText (3, basis);
				item.setText (4, price);
				item.setText (5,cost);
				item.setText (6,value);
				item.setText (7,gain);
				item.setText (8,gainPct);
				item.setText (9,div);
				item.setText (10,y);
				item.setText (11,yoc);
				item.setText (12,w);
				
				sectorCost = sectorCost.add(p.getCost());
				sectorValue = sectorValue.add(p.getValue());
				sectorDiv = sectorDiv.add(divCalc);
				sectorGain = sectorGain.add(gainCalc);
				
				if (showLots.contains(sym))
				for (Lot l : p.getLotList()){
					TableItem lotItem = new TableItem (posTable, SWT.NONE);
					lotItem.setText (0, "");
					lotItem.setText (1, new SimpleDateFormat("MM/dd/yyyy").format(l.getDate()));
					String lshares = new DecimalFormat("#,##0.00##").format(l.getShares());
					String lbps = new DecimalFormat("#,##0.00##").format(l.getBasisPerShare());
					String lcost = new DecimalFormat("#,##0.00##").format(l.getCost());
					lotItem.setText (2, lshares);
					lotItem.setText (3, lbps);
					lotItem.setText (4, "");
					lotItem.setText (5, lcost);
					lotItem.setText (6, "");
					lotItem.setText (7, "");
					lotItem.setText (8, "");
					lotItem.setText (9, "");
					lotItem.setText (10, "");
					lotItem.setText (11, "");
					lotItem.setText (12, "");
				}
			}
			
			BigDecimal sectorYield = sectorDiv.multiply(BigDecimal.valueOf(100)).divide(sectorValue, 2, RoundingMode.HALF_UP);
			BigDecimal sectorYoc = sectorDiv.multiply(BigDecimal.valueOf(100)).divide(sectorCost, 2, RoundingMode.HALF_UP);
			BigDecimal sectorWeight = sectorValue.multiply(BigDecimal.valueOf(100)).divide(totalValue, 2, RoundingMode.HALF_UP);
			
			String shares = "";
			String basis = "";
			String price = "";
			String cost = new DecimalFormat("#,###,##0.00").format(sectorCost);
			String value = new DecimalFormat("#,###,##0.00").format(sectorValue);
			String gain = new DecimalFormat("#,###,##0.00").format(sectorGain);
			String y = new DecimalFormat("0.00").format(sectorYield)+ "%";
			String yoc = new DecimalFormat("0.00").format(sectorYoc)+ "%";
			String w = new DecimalFormat("0.00").format(sectorWeight)+ "%";
			
			BigDecimal gainPctCalc = sectorGain.multiply(BigDecimal.valueOf(100)).divide(sectorCost, 2, RoundingMode.HALF_UP);
			String gainPct = new DecimalFormat("0.00").format(gainPctCalc) + "%";
					
			String div = new DecimalFormat("#,###,##0.00").format(sectorDiv);
			
			TableItem subTotItem = new TableItem (posTable, SWT.NONE);
			subTotItem.setText (0, sectorStr);
			subTotItem.setText (1, "Subtotals");
			subTotItem.setBackground(Colors.LTGRAY);
			subTotItem.setText (2, shares);
			subTotItem.setText (3, basis);
			subTotItem.setText (4, price);
			subTotItem.setText (5,cost);
			subTotItem.setText (6,value);
			subTotItem.setText (7,gain);
			subTotItem.setText (8,gainPct);
			subTotItem.setText (9,div);
			subTotItem.setText (10,y);
			subTotItem.setText (11,yoc);
			subTotItem.setText (12,w);
		}
		
		TableItem cashItem = new TableItem (posTable, SWT.NONE);
		
		String cw = "";
		String bal = new DecimalFormat("#,###,##0.00").format(portData.getCashBalance());
		if (totalValue.compareTo(BigDecimal.ZERO) != 0){
			BigDecimal cashWeight = portData.getCashBalance().multiply(BigDecimal.valueOf(100)).divide(totalValue, 2, RoundingMode.HALF_UP);
			cw = new DecimalFormat("0.00").format(cashWeight)+ "%";
		}
		
		cashItem.setBackground(Colors.LTGRAY);
		cashItem.setText (0, "");
		cashItem.setText (1, "Cash");
		cashItem.setText (2, "");
		cashItem.setText (3, "");
		cashItem.setText (4, "");
		cashItem.setText (5,bal);
		cashItem.setText (6,bal);
		cashItem.setText (7,"0.00");
		cashItem.setText (8,"0.00%");
		cashItem.setText (9,"0.00");
		cashItem.setText (10,"0.00%");
		cashItem.setText (11,"0.00%");
		cashItem.setText (12,cw);

		BigDecimal totalYield = null;
		if (totalValue.compareTo(BigDecimal.ZERO) != 0){
			totalYield = totalDiv.multiply(BigDecimal.valueOf(100)).divide(totalValue, 2, RoundingMode.HALF_UP);
		}
		BigDecimal totalYoc = null;
		if (totalCost.compareTo(BigDecimal.ZERO) != 0 ){
			totalYoc = totalDiv.multiply(BigDecimal.valueOf(100)).divide(totalCost, 2, RoundingMode.HALF_UP);
		}
		
		TableItem totItem = new TableItem (posTable, SWT.NONE);
		
//			String[] posTitles = {"Sector", "Symbol", "Basis", "Price", "Cost", "Value", "Div"};
		
		String shares = "";
		String basis = "";
		String price = "";
		String cost = new DecimalFormat("#,###,##0.00").format(totalCost);
		String value = new DecimalFormat("#,###,##0.00").format(totalValue);
		String gain = new DecimalFormat("#,###,##0.00").format(totalGain);
		String y = (totalYield == null) ? "N/A" : new DecimalFormat("0.00").format(totalYield)+ "%";
		String yoc = (totalYoc == null) ? "N/A" : new DecimalFormat("0.00").format(totalYoc)+ "%";
		
		BigDecimal gainPctCalc = null;
		String gainPct = "N/A";
		if (totalCost.compareTo(BigDecimal.ZERO) != 0){
			gainPctCalc = totalGain.multiply(BigDecimal.valueOf(100)).divide(totalCost, 2, RoundingMode.HALF_UP);
			gainPct = new DecimalFormat("0.00").format(gainPctCalc) + "%";
		}
				
		String div = new DecimalFormat("#,###,##0.00").format(totalDiv);
		
		totItem.setBackground(Colors.GRAY);
		
		totItem.setText (0, "");
		totItem.setText (1, "Total");
		totItem.setText (2, shares);
		totItem.setText (3, basis);
		totItem.setText (4, price);
		totItem.setText (5,cost);
		totItem.setText (6,value);
		totItem.setText (7,gain);
		totItem.setText (8,gainPct);
		totItem.setText (9,div);
		totItem.setText (10,y);
		totItem.setText (11,yoc);
		totItem.setText (12,"100%");

		for (int i=0; i< posTitles.length; i++) {
			posTable.getColumn (i).pack ();
		}
	}

}