 
package org.djv.stockresearcher.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class InfoPart {
	
	Label l;
	
	@Inject
	private StockDB db;
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		l = new Label(parent, SWT.WRAP);
		update();
		AppState.getInstance().addListener(new AppStateListener(){
			@Override
			public void notifyChanged(AppState appState) {
				update();
			}
		});
	}
	
	public void update() {
		l.setText("");
		StockData sd = AppState.getInstance().getSelectedStock();
		if (sd != null){
			String s;
			try {
				s = db.getCompanyInfo(sd.getStock().getSymbol());
				System.err.println((int)s.charAt(143));
				s.replaceAll("" + (char) 194, "");
				l.setText(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
}