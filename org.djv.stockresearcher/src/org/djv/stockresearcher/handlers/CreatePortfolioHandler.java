 
package org.djv.stockresearcher.handlers;

import javax.inject.Named;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.parts.PortfolioPart;
import org.djv.stockresearcher.widgets.PortfolioDialog;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class CreatePortfolioHandler {
	
	@Execute
	public void execute(IEclipseContext context,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			@Named(IServiceConstants.ACTIVE_PART) final MContribution contribution) {
		
		PortfolioDialog pd = new PortfolioDialog(shell);
		pd.create();
		int result = pd.open();
		if (result == Window.OK) {
			try {
				StockDB.getInstance().createNewPortfolio(pd.getName());
				PortfolioPart pp = (PortfolioPart) contribution.getObject();
				pp.updatePortfolioList();
				pp.getPortfolioSelector().setText(pd.getName());
				pp.selectPortfolio();
			} catch (Exception e1) {
				MessageDialog.openError(shell, "Error", e1.getMessage());
			}
		} 
		
	}
		
}