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

public class AddWatchDialog extends Dialog {
	
	String symbol;
	
	Label symbolLabel;
	Text symbolText;

	public String getSymbol() {
		return symbol;
	}

	public AddWatchDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, true));
				
		symbolLabel = new Label(container, SWT.NONE);
		symbolLabel.setText("Symbol: ");
		symbolLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		symbolText = new Text(container, SWT.BORDER);
		symbolText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

		return container;
	}

	@Override
	protected void okPressed() {
		symbol = symbolText.getText();
		
		if ("".equals(symbol)){
			MessageDialog.openError(getParentShell(), "Error", "Enter Symbol.");
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
		return new Point(400, 400);
	}

} 
