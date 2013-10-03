 
package org.djv.stockresearcher.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Portfolio;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

public class PortfolioMenuContribution {
	
	@Inject
	private ECommandService cmdService;
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		try {
			List<Portfolio> l = StockDB.getInstance().getPortfolioList();
			for (Portfolio p : l){
				MHandledMenuItem dynamicItem = MMenuFactory.INSTANCE.createHandledMenuItem();
			    dynamicItem.setLabel("Dynamic Menu Item (" + p.getName() + ")");
			    dynamicItem.setContributorURI("platform:/plugin/org.djv.stockresearcher");
			    
			    Map<String, String> parameters = new HashMap<String, String>();
			    parameters.put("portfolio", p.getName());
			    ParameterizedCommand cmd = cmdService.createCommand("org.djv.stockresearcher.command.selectPortfolio", parameters);
			    dynamicItem.setWbCommand(cmd);
			    items.add(dynamicItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	

	}
		
}