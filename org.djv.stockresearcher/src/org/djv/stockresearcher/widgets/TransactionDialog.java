package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.djv.stockresearcher.model.Transaction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	
	String mode;
	String action;
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	String symbol;
	BigDecimal shares;
	BigDecimal price;
	BigDecimal commission;
	Date tranDate;

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setShares(BigDecimal shares) {
		this.shares = shares;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	Label actionLabel;
	Combo actionCombo;
	
	Label symbolLabel;
	Text symbolText;
	
	Label sharesLabel;
	Text sharesText;
	
	Label priceLabel;
	Text priceText;
	
	Label commissionLabel;
	Text commissionText;
	
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
		
		if (tranDate == null){
			tranDateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		} else {
			tranDateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(tranDate));
		}
		
		actionLabel = new Label(container, SWT.NONE);
		actionLabel.setText("Action: ");
		actionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		actionCombo = new Combo(container, SWT.BORDER);
		actionCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		actionCombo.setItems(new String[] {"", "Buy", "Sell", "Cash Deposit", "Cash Withdrawal", "Dividend"});
		actionCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo)e.widget;
				int i = c.getSelectionIndex();
				setFieldsForSelection(i);
			}
		});

		if (action == null){
			actionCombo.select(0);
		} else {
			switch (action){
				case Transaction.ACTION_BUY: 
					actionCombo.select(1);
					setFieldsForSelection(1);
					break;
				case Transaction.ACTION_SELL: 
					actionCombo.select(2);
					setFieldsForSelection(2);
					break;
				case Transaction.ACTION_CASH_DEPOSIT: 
					actionCombo.select(3);
					setFieldsForSelection(3);
					break;
				case Transaction.ACTION_CASH_WITHDRAWAL: 
					actionCombo.select(4);
					setFieldsForSelection(4);
					break;
				case Transaction.ACTION_DIVIDEND: 
					actionCombo.select(5);
					setFieldsForSelection(5);
					break;
			}
		}
		
		symbolLabel = new Label(container, SWT.NONE);
		symbolLabel.setText("Symbol: ");
		symbolLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		symbolText = new Text(container, SWT.BORDER);
		symbolText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if (symbol == null){
			symbolText.setText("");
		} else {
			symbolText.setText(symbol);
		}
		
		sharesLabel = new Label(container, SWT.NONE);
		sharesLabel.setText("Shares: ");
		sharesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		sharesText = new Text(container, SWT.BORDER);
		sharesText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		if (shares == null){
			sharesText.setText("100.0000");
		} else {
			symbolText.setText(new DecimalFormat("0.0000").format(shares));
		}
		
		priceLabel = new Label(container, SWT.NONE);
		priceLabel.setText("Price: ");
		priceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		priceText = new Text(container, SWT.BORDER);
		priceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if (price == null){
			priceText.setText("0.0000");
		} else {
			priceText.setText(new DecimalFormat("0.0000").format(price));
		}
		
		commissionLabel = new Label(container, SWT.NONE);
		commissionLabel.setText("Commisssion: ");
		commissionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		commissionText = new Text(container, SWT.BORDER);
		commissionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if (commission == null){
			commissionText.setText("0.0000");
		} else {
			commissionText.setText(new DecimalFormat("0.0000").format(commission));
		}

		return container;
	}
	
	private void setFieldsForSelection(int i) {
		switch (i){
		case 0: 
			symbolText.setEnabled(false);
			sharesText.setEnabled(false);
			priceText.setEnabled(false);
			commissionText.setEnabled(false);
			break;
		case 1: 
		case 2:
			symbolText.setEnabled(true);
			sharesText.setEnabled(true);
			priceText.setEnabled(true);
			commissionText.setEnabled(true);
			break;
		case 3:
		case 4:
			symbolText.setText("");
			symbolText.setEnabled(false);
			sharesText.setText("");
			sharesText.setEnabled(false);
			priceText.setEnabled(true);
			commissionText.setText("");
			commissionText.setEnabled(false);
			break;
		case 5: 
			symbolText.setEnabled(true);
			sharesText.setText("");
			sharesText.setEnabled(false);
			priceText.setEnabled(true);
			commissionText.setText("");
			commissionText.setEnabled(false);
			break;
	}
	}

	@Override
	protected void okPressed() {
		
		String tranDateStr = tranDateText.getText();
		
		if ("".equals(tranDateStr)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Tran Date.");
			return;
		} else {
			try{
				tranDate = new SimpleDateFormat("MM/dd/yyyy").parse(tranDateStr);
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Tran Date.");
				return;
			}
		}
		
		String actionStr = actionCombo.getText();
		if ("".equals(action)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Action.");
			return;
		}
		switch (actionStr){
			case "Buy": action = "B"; break;
			case "Sell": action = "S"; break;
			case "Cash Deposit": action = "D"; break;
			case "Cash Withdrawal": action = "W"; break;
			case "Dividend": action = "V"; break;
			default: 
				MessageDialog.openError(getParentShell(), "Error", "Invalid Action.");
				return;
		}
		
		symbol = symbolText.getText();
		if ("".equals(symbol)) {
			if 	("B".equals(action) || "S".equals(action) || ("V".equals(action))){
				MessageDialog.openError(getParentShell(), "Error", "Symbol is required.");
				return;
			} 
		}
		
		String sharesStr = sharesText.getText();
		if ("".equals(sharesStr)){
			if ("B".equals(action) || "S".equals(action)) {
				MessageDialog.openError(getParentShell(), "Error", "Enter Shares.");
				return;
			} else {
				shares = new BigDecimal("0.00");
			}
		} else {
			try{
				shares = new BigDecimal(sharesStr);
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Shares.");
				return;
			}
		}
		
		String priceStr = priceText.getText();
		
		if ("".equals(priceStr)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Price.");
			return;
		} else {
			try{
				price = new BigDecimal(priceStr);
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Price.");
				return;
			}
		}
		

		String commissionStr = commissionText.getText();
		if ("".equals(commissionStr)){
			commission = new BigDecimal("0.00");
		} else {
			try{
				commission = new BigDecimal(commissionStr);
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Commission.");
				return;
			}
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
		return new Point(300, 400);
	}

} 
