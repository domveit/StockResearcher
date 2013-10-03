package org.djv.stockresearcher.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Shell;

	public class SelectPortfolioHandler {
		
	    @Execute
	    public void execute(IEclipseContext context,
				@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
				@Named(IServiceConstants.ACTIVE_PART) final MContribution contribution, EModelService service, MWindow window) {
	    	
	       System.err.println("Direct Menu Item selected");
	        
	       
	    }
	}

