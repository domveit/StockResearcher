 
package org.djv.stockresearcher.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Portfolio;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class PortfolioMenuContribution {
	
	@Inject
	ECommandService cmdService;
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, EModelService modelService, MApplication app) {
		try {
			Command cmd = cmdService.getCommand("org.djv.stockresearcher.command.selectPortfolio");
			
			MCommand mc = null;
			for (MCommand mci : app.getCommands()) {
				if ("org.djv.stockresearcher.command.selectPortfolio".equals(mci)) { //$NON-NLS-1$
					mc = mci;
					break;
				}
			}
			List<Portfolio> l = StockDB.getInstance().getPortfolioList();
			for (Portfolio p : l){
				MHandledMenuItem dynamicItem = MMenuFactory.INSTANCE.createHandledMenuItem();
			    dynamicItem.setLabel("Dynamic Menu Item (" + p.getName() + ")");
			    dynamicItem.setContributorURI("platform:/plugin/org.djv.stockresearcher");
			    dynamicItem.setCommand(mc);
			    
				MParameter parameter = MCommandsFactory.INSTANCE.createParameter();
				parameter.setName("portfolio");
				parameter.setValue(p.getName());
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("portfolio", p.getName());
				
				ParameterizedCommand pc = ParameterizedCommand.generateCommand(cmd, params);
				dynamicItem.setWbCommand(pc);
				
				dynamicItem.getParameters().add(parameter);
				items.add(dynamicItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	

	}
		
}