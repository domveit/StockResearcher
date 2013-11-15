package org.djv.stockresearcher.handlers;

import javax.inject.Named;

import org.djv.stockresearcher.parts.PortfolioPart;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

	public class SelectPortfolioHandler {
		
	    @Execute
	    public void execute(IEclipseContext context,
				@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
				@Named(IServiceConstants.ACTIVE_PART) final MContribution contribution,
				@Named("portfolio.selection") String portfolioName) {
	    	
	       PortfolioPart pp = (PortfolioPart) contribution.getObject();
			pp.updatePortfolioList();
			pp.getPortfolioSelector().setText(portfolioName);
			pp.selectPortfolio();
	       
	    }
	}

