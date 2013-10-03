 
package org.djv.stockresearcher.handlers;

import java.util.List;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.Portfolio;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

public class PortfolioMenuContribution {
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		try {
			List<Portfolio> l = StockDB.getInstance().getPortfolioList();
			for (Portfolio p : l){
				MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			    dynamicItem.setLabel("Dynamic Menu Item (" + p.getName() + ")");
			    dynamicItem.setContributorURI("platform:/plugin/org.djv.stockresearcher");
			    dynamicItem.setContributionURI("bundleclass://org.djv.stockresearcher/org.djv.stockresearcher.handlers.DirectMenuItem");    
			    items.add(dynamicItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	

	}
		
}