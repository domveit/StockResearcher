package org.djv.stockresearcher.widgets;

import static org.djv.stockresearcher.model.ValidatorType.OPTIONAL;
import static org.djv.stockresearcher.model.ValidatorType.REQUIRED;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.model.TransactionType;
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
	
	Transaction transaction;
	
	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	Label tranTypeLabel;
	Combo tranTypeCombo;
	
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
	
	Label premiumLabel;
	Text premiumText;

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
		tranDateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		tranTypeLabel = new Label(container, SWT.NONE);
		tranTypeLabel.setText("Action: ");
		tranTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		tranTypeCombo = new Combo(container, SWT.BORDER);
		tranTypeCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		tranTypeCombo.setItems(TransactionType.getTextOptions());
		tranTypeCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo)e.widget;
				TransactionType tt = TransactionType.getFromDisplay(c.getText());
				setFieldsForSelection(tt);
			}
		});

		symbolLabel = new Label(container, SWT.NONE);
		symbolLabel.setText("Symbol: ");
		symbolLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		symbolText = new Text(container, SWT.BORDER);
		symbolText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		sharesLabel = new Label(container, SWT.NONE);
		sharesLabel.setText("Shares: ");
		sharesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		sharesText = new Text(container, SWT.BORDER);
		sharesText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		priceLabel = new Label(container, SWT.NONE);
		priceLabel.setText("Price: ");
		priceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		priceText = new Text(container, SWT.BORDER);
		priceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		commissionLabel = new Label(container, SWT.NONE);
		commissionLabel.setText("Commission: ");
		commissionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		commissionText = new Text(container, SWT.BORDER);
		commissionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		premiumLabel = new Label(container, SWT.NONE);
		premiumLabel.setText("Premium: ");
		premiumLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		premiumText = new Text(container, SWT.BORDER);
		premiumText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		return container;
	}
	
	@Override
	public int open() {
		if (transaction.getTranDate() == null){
			tranDateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		} else {
			tranDateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(transaction.getTranDate()));
		}
		
		if (transaction.getType() == null){
			tranTypeCombo.select(0);
		} else {
			TransactionType tt = TransactionType.getFromCode(transaction.getType());
			tranTypeCombo.setText(tt.getTypeText());
			setFieldsForSelection(tt);
		}
		return super.open();
	}

	private void setFieldsForSelection(TransactionType tt) {
		if (tt.getSymbolRequired() == REQUIRED ||  tt.getSymbolRequired() == OPTIONAL){
			symbolText.setEnabled(true);
			if (transaction.getSymbol() == null){
				symbolText.setText("");
			} else {
				symbolText.setText(transaction.getSymbol());
			}
		} else {
			symbolText.setEnabled(false);
			symbolText.setText("");
		}
		
		if (tt.getSharesRequired() == REQUIRED ||  tt.getSharesRequired() == OPTIONAL){
			sharesText.setEnabled(true);
			if (transaction.getShares() == null){
				sharesText.setText("");
			} else {
				sharesText.setText(new DecimalFormat("0.0000").format(transaction.getShares()));
			}
		} else {
			sharesText.setEnabled(false);
			sharesText.setText("");
		}
		
		if (tt.getPriceRequired() == REQUIRED ||  tt.getPriceRequired() == OPTIONAL){
			priceText.setEnabled(true);
			if (transaction.getPrice() == null){
				priceText.setText("");
			} else {
				priceText.setText(new DecimalFormat("0.000000").format(transaction.getPrice()));
			}
		} else {
			priceText.setEnabled(false);
			priceText.setText("");
		}
		
		if (tt.getCommissionRequired() == REQUIRED ||  tt.getCommissionRequired() == OPTIONAL){
			commissionText.setEnabled(true);
			if (transaction.getCommission() == null){
				commissionText.setText("");
			} else {
				commissionText.setText(new DecimalFormat("0.0000").format(transaction.getCommission()));
			}
		} else {
			commissionText.setEnabled(false);
			commissionText.setText("");
		}
		
		if (tt.getPremiumRequired() == REQUIRED ||  tt.getPremiumRequired() == OPTIONAL){
			premiumText.setEnabled(true);
			if (transaction.getPremium() == null){
				premiumText.setText("");
			} else {
				premiumText.setText(new DecimalFormat("0.0000").format(transaction.getPremium()));
			}
		} else {
			premiumText.setEnabled(false);
			premiumText.setText("");
		}
	}

	@Override
	protected void okPressed() {
		String tranDateStr = tranDateText.getText();
		tranDateStr = tranDateStr.replace('.', '/');
		if ("".equals(tranDateStr)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Tran Date.");
			return;
		} else {
			try{
				transaction.setTranDate(new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy").parse(tranDateStr).getTime()));
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Tran Date.");
				return;
			}
		}
		
		String tranType = tranTypeCombo.getText();
		if ("".equals(tranType)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Tran Type.");
			return;
		}
		TransactionType tt = TransactionType.getFromDisplay(tranType);
		transaction.setType(tt.getTypeCode());
		
		String symbol = symbolText.getText();
		if ("".equals(symbol)) {
			if 	(tt.getSymbolRequired() == REQUIRED){
				MessageDialog.openError(getParentShell(), "Error", "Symbol is required.");
				return;
			} 
		}
		transaction.setSymbol(symbol);
		
		String sharesStr = sharesText.getText();
		if ("".equals(sharesStr)){
			if (tt.getSharesRequired() == REQUIRED) {
				MessageDialog.openError(getParentShell(), "Error", "Enter Shares.");
				return;
			} else {
				transaction.setShares(BigDecimal.ZERO);
			}
		} else {
			try{
				transaction.setShares(new BigDecimal(sharesStr));
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Shares.");
				return;
			}
		}
		
		String priceStr = priceText.getText();
		
		if ("".equals(priceStr)){
			if (tt.getPriceRequired() == REQUIRED) {
				MessageDialog.openError(getParentShell(), "Error", "Enter Price.");
				return;
			} else {
				transaction.setPrice(BigDecimal.ZERO);
			}
		} else {
			try{
				transaction.setPrice(new BigDecimal(priceStr));
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Price.");
				return;
			}
		}

		String commissionStr = commissionText.getText();
		if ("".equals(commissionStr)){
			if (tt.getCommissionRequired() == REQUIRED) {
				MessageDialog.openError(getParentShell(), "Error", "Enter Commission.");
				return;
			} else {
				transaction.setCommission(BigDecimal.ZERO);
			}
		} else {
			try{
				transaction.setCommission(new BigDecimal(commissionStr));
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Commission.");
				return;
			}
		}
		
		String premiumStr = premiumText.getText();
		if ("".equals(premiumStr)){
			if (tt.getPremiumRequired() == REQUIRED) {
				MessageDialog.openError(getParentShell(), "Error", "Enter Premium.");
				return;
			} else {
				transaction.setPremium(BigDecimal.ZERO);
			}
		} else {
			try{
				transaction.setPremium(new BigDecimal(premiumStr));
			} catch (Exception e){
				MessageDialog.openError(getParentShell(), "Error", "Invalid Premium.");
				return;
			}
		}
		
		super.okPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create/Edit Transaction");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 400);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

} 
