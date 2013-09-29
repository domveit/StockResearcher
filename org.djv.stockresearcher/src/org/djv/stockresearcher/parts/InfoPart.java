 
package org.djv.stockresearcher.parts;

import javax.annotation.PostConstruct;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class InfoPart implements AppStateListener {
	
	Label l;
	
	private StockDB db = StockDB.getInstance();
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		l = new Label(parent, SWT.WRAP);
		AppState.getInstance().addListener(this);
		update();
	}
	
	public void update() {
		l.setText("");
		final StockData sd = AppState.getInstance().getSelectedStock();
		if (sd != null){
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						final String s = db.getCompanyInfo(sd.getStock().getSymbol());
						Display.getDefault().asyncExec(new Runnable(){
							@Override
							public void run() {
								l.setText(s);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			});
			t.start();
		}
	}

	@Override
	public void notifyChanged(AppState appState) {
		update();
	}
	
	
	
}