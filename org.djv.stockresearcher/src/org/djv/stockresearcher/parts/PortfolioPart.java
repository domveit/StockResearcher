 
package org.djv.stockresearcher.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Portfolio;
import org.djv.stockresearcher.model.PortfolioData;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PortfolioPart {
	
	private Combo portfolioSelector;
	private Button newButton;
	private Button deleteButton;
	
	private Label test;
	
	private Shell shell;
	
	@PostConstruct
	public void postConstruct(final Composite parent) {
		this.shell = parent.getShell();
		Composite c = new Composite(parent, SWT.BORDER);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c.setLayout(new GridLayout(3, false));
		
		portfolioSelector = new Combo(c, SWT.NONE);
		portfolioSelector.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		portfolioSelector.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectPortfolio();
			}
		});
		
		
		newButton = new Button(c, SWT.NONE);
		newButton.setText("New");
		newButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
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
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
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
		
		test = new Label(c, SWT.NONE);
		test.setText("");
		test.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		
		updatePortfolioList();
		
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
		String portfolioName = portfolioSelector.getText();
		if ("".equals(portfolioName)){
			test.setText("");
			return;
		}
		try {
			PortfolioData portData = StockDB.getInstance().getPortfolioData(portfolioName);
			test.setText(portData.getPortfolio().getName());
		} catch (Exception e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
		
	}
}