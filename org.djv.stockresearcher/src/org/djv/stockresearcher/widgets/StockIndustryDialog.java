package org.djv.stockresearcher.widgets;

import org.djv.stockresearcher.db.SectorIndustryListener;
import org.djv.stockresearcher.db.StockDB;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StockIndustryDialog extends Dialog {

	private TextProgressBar progressBar;

	public StockIndustryDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
		
		progressBar = new TextProgressBar(container, SWT.NONE);
		progressBar.setText("");
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		StockDB.getInstance().addSectorIndustryListener(new SectorIndustryListener() {
			@Override
			public void notifyChanged(final String industryName, final int industriesToUpdate,
					final int industriesUpdated, final int beginOrEnd) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (beginOrEnd == 1){
							okPressed();
						} else {
							progressBar.setSelection(industriesUpdated);
							progressBar.setMaximum(industriesToUpdate);
							progressBar.setText(industryName);
						}
					}
				});
			}
		});
		try {
			StockDB.getInstance().updateSectors();
		} catch (Exception e) {
			MessageDialog.openError(parent.getShell(), "Error", e.getMessage());
		}
		return container;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Updating Sector Data");
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(600, 300);
	}

} 
