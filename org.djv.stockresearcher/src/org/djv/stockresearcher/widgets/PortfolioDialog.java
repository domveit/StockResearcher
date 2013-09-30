package org.djv.stockresearcher.widgets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PortfolioDialog extends Dialog {

	Label nameLabel;
	Text nameText;
	String name;

	public String getName() {
		return name;
	}

	public PortfolioDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, true));
		
		nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Portfolio Name");
		nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		nameText = new Text(container, SWT.NONE);
		nameText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		return container;
	}

	@Override
	protected void okPressed() {
		name = nameText.getText();
		if ("".equals(name)){
			MessageDialog.openError(getParentShell(), "Error", "Enter portfolio name.");
			return;
		}
		super.okPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create new Portfolio");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 200);
	}

} 
