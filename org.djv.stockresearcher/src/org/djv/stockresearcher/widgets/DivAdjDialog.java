package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.djv.stockresearcher.model.AdjustedDiv;
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

public class DivAdjDialog extends Dialog {

	Label symbolLabel;
	Label symbolText;
	
	Label paydateLabel;
	Label paydateText;
	
	Label adjustedDateLabel;
	Text adjustedDateText;
	
	Label adjustedDivLabel;
	Text adjustedDivText;
	
	AdjustedDiv adjDiv;
	
//	String symbol;
//	Date paydate;
//	Date adjustedDate;
//	BigDecimal adjustedDiv;

	public AdjustedDiv getAdjDiv() {
		return adjDiv;
	}

	public void setAdjDiv(AdjustedDiv adjDiv) {
		this.adjDiv = adjDiv;
	}

	public DivAdjDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, true));
		
		symbolLabel = new Label(container, SWT.NONE);
		symbolLabel.setText("Symbol");
		symbolLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		symbolText = new Label(container, SWT.BORDER);
		symbolText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		paydateLabel = new Label(container, SWT.NONE);
		paydateLabel.setText("Payment Date");
		paydateLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		paydateText = new Label(container, SWT.BORDER);
		paydateText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		adjustedDateLabel = new Label(container, SWT.NONE);
		adjustedDateLabel.setText("Adjusted Date");
		adjustedDateLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		adjustedDateText = new Text(container, SWT.BORDER);
		adjustedDateText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		adjustedDivLabel = new Label(container, SWT.NONE);
		adjustedDivLabel.setText("Adjusted Div");
		adjustedDivLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		adjustedDivText = new Text(container, SWT.BORDER);
		adjustedDivText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		
		return container;
	}
	
	@Override
	public int open() {
		symbolText.setText(adjDiv.getSymbol());
		paydateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(adjDiv.getPaydate()));
		if (adjDiv.getAdjustedDate() == null){
			adjustedDateText.setText("");
		} else {
			adjustedDateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(adjDiv.getAdjustedDate()));
		}
		if (adjDiv.getAdjustedDiv() == null){
			adjustedDivText.setText("");
		} else {
			adjustedDivText.setText(String.valueOf(adjDiv.getAdjustedDiv()));
		}
		symbolText.getParent().layout(true);
		

		return super.open();
	}

	@Override
	protected void okPressed() {
		if ("".equals(adjustedDateText.getText())){
			adjDiv.setAdjustedDate(null);
		} else {
			try {
				Date adjustedDate = new SimpleDateFormat("MM/dd/yyyy").parse(adjustedDateText.getText());
				adjDiv.setAdjustedDate(new java.sql.Date(adjustedDate.getTime()));
			} catch (ParseException e) {
				MessageDialog.openError(getParentShell(), "Error", "Invalid Adjusted Date.");
				return;
			}
		}
		
		if ("".equals(adjustedDivText.getText())){
			adjDiv.setAdjustedDiv(null);
		} else {
			try {
				BigDecimal adjustedDiv = new BigDecimal(adjustedDivText.getText());
				adjDiv.setAdjustedDiv(adjustedDiv);
			} catch (Exception e) {
				MessageDialog.openError(getParentShell(), "Error", "Invalid Adjusted Div");
				return;
			}
		}
		super.okPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Adjust Dividend");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

} 
