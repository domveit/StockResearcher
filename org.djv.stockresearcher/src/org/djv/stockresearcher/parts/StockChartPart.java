 
package org.djv.stockresearcher.parts;

import java.io.InputStream;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class StockChartPart implements AppStateListener {
	
	String [] chartItems = {"1d", "5d", "1m", "3m", "6m", "1y", "2y", "5y", "my"};
	private Label chartLabel;
	
	private AppState appState = AppState.getInstance();
	
	@Inject
	public StockChartPart() {
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		buildChartArea(parent);
		appState.addListener(this);
	}
	
	@Focus
	public void onFocus() {
	}
	
	public void buildChartArea(Composite leftSide) {
		Composite chartComposite = new Composite(leftSide, SWT.BORDER);
		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		chartComposite.setLayout(new GridLayout());
		
		Composite chartButtonComp = new Composite(chartComposite, SWT.BORDER);
		chartButtonComp.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
		chartButtonComp.setLayout(new FillLayout());
		
		for (String s : chartItems){
			Button b = new Button(chartButtonComp, SWT.PUSH);
			b.setText(s);
			b.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Button b = (Button) e.widget;
					chartLabel.setData("t", b.getText());
					refreshChart();
				}
			});
		}
		
		chartLabel = new Label(chartComposite, SWT.NONE);
		chartLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		chartLabel.setData("t", "1y");
	}
	
	public void refreshChart() {
		final StockData sd = appState.getSelectedStock();
		final String t = (String)chartLabel.getData("t");
		Thread thr = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					URL url = new URL("http://chart.finance.yahoo.com/z?s=" + sd.getStock().getSymbol() + "&t="+ t +"&q=c&l=off&z=m&a=v&p=s&lang=en-US&region=US");
					InputStream is = url.openStream();
					final Image img = new Image(Display.getDefault(), is);
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							chartLabel.setImage(img);							
						}
					});
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		thr.start();
	}

	@Override
	public void notifyChanged(AppState appState) {
		refreshChart();
	}
	
	
}