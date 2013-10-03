package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TransactionDialog extends Dialog {
	
	String action;
	String symbol;
	BigDecimal shares;
	BigDecimal price;
	Date tranDate;

	Label actionLabel;
	Combo actionCombo;
	
	Label symbolLabel;
	Text symbolText;
	
	Label sharesLabel;
	Text sharesText;
	
	Label priceLabel;
	Text priceText;
	
	Label tranDateLabel;
	Text tranDateText;
	
	
	public String getAction() {
		return action;
	}

	public String getSymbol() {
		return symbol;
	}

	public BigDecimal getShares() {
		return shares;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public TransactionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, true));
				
		tranDateLabel = new Label(container, SWT.NONE);
		tranDateLabel.setText("Tran Date: ");
		tranDateLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		tranDateText = new Text(container, SWT.BORDER);
		tranDateText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		actionLabel = new Label(container, SWT.NONE);
		actionLabel.setText("Action: ");
		actionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		actionCombo = new Combo(container, SWT.BORDER);
		actionCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		actionCombo.setItems(new String[] {"Buy", "Sell"});
		
		symbolLabel = new Label(container, SWT.NONE);
		symbolLabel.setText("Symbol: ");
		symbolLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		symbolText = new Text(container, SWT.BORDER);
		symbolText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		sharesLabel = new Label(container, SWT.NONE);
		sharesLabel.setText("Shares: ");
		sharesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		sharesText = new Text(container, SWT.BORDER);
		sharesText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		priceLabel = new Label(container, SWT.NONE);
		priceLabel.setText("Price: ");
		priceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		priceText = new Text(container, SWT.BORDER);
		priceText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

		return container;
	}

	@Override
	protected void okPressed() {
		action = actionCombo.getText();
		if ("".equals(action)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Action.");
			return;
		}
		if ("Buy".equals(action)){
			action = "B";
		} else if ("Sell".equals(action)){
			action = "S";
		} else {
			MessageDialog.openError(getParentShell(), "Error", "Invalid Action.");
			return;
		}
		
		symbol = symbolText.getText();
		
		if ("".equals(symbol)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Symbol.");
			return;
		}
		
		String sharesStr = sharesText.getText();
		
		if ("".equals(sharesStr)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Shares.");
			return;
		}
		
		try{
			shares = new BigDecimal(sharesStr);
		} catch (Exception e){
			MessageDialog.openError(getParentShell(), "Error", "Invalid Shares.");
			return;
		}
		
		String priceStr = priceText.getText();
		
		if ("".equals(priceStr)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Price.");
			return;
		}
		
		try{
			price = new BigDecimal(priceStr);
		} catch (Exception e){
			MessageDialog.openError(getParentShell(), "Error", "Invalid Price.");
			return;
		}
		
		String tranDateStr = tranDateText.getText();
		
		if ("".equals(tranDateStr)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Tran Date.");
			return;
		}
		
		try{
			tranDate = new SimpleDateFormat("MM/dd/yyyy").parse(tranDateStr);
		} catch (Exception e){
			MessageDialog.openError(getParentShell(), "Error", "Invalid Tran Date.");
			return;
		}
		
		super.okPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create New Transaction");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 800);
	}

} 
