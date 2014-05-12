 
package org.djv.stockresearcher.parts;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;

public class StockChartPart implements AppStateListener {
	
	String [] chartTimePeriods = {"1d", "5d", "1m", "3m", "6m", "1y", "2y", "5y", "my"};
	String [] chartTypes = {"l", "b", "c"};
	String [] movingAvg = {"m5", "m10", "m20", "m50", "m100", "m200"};
	String [] emaList = {"e5", "e10", "e20", "e50", "e100", "e200"};
	List<Button> maButtons = new ArrayList<Button>();
	
	ExpandItem item1 ;
	
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
		
		ExpandBar bar = new ExpandBar (leftSide, SWT.V_SCROLL);
		Image image = Display.getDefault().getSystemImage(SWT.ICON_QUESTION);
		
		Composite chartComposite = new Composite(bar, SWT.BORDER);
		chartComposite.setLayout(new GridLayout(2, false));
		{
			Label l = new Label(chartComposite, SWT.NONE);
			l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l.setText("Time Period");
				
			Composite timePeriodComp = new Composite(chartComposite, SWT.NONE);
			timePeriodComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			timePeriodComp.setLayout(new FillLayout());
			
			for (String s : chartTimePeriods){
				Button b = new Button(timePeriodComp, SWT.PUSH);
				b.setText(s);
				b.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button) e.widget;
						chartLabel.setData("t", b.getText());
						refreshChart();
					}
				});
			}
		}
		
		{
			
			Label l2 = new Label(chartComposite, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("Chart Type");
			
			Composite chartTypeComp = new Composite(chartComposite, SWT.NONE);
			chartTypeComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			chartTypeComp.setLayout(new FillLayout());

			for (String s : chartTypes){
				Button b = new Button(chartTypeComp, SWT.PUSH);
				b.setText(s);
				b.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button) e.widget;
						chartLabel.setData("q", b.getText());
						refreshChart();
					}
				});
			}
		}
		{
			
			Label l2 = new Label(chartComposite, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("Moving Avg");
			
			Composite maComp = new Composite(chartComposite, SWT.NONE);
			maComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			maComp.setLayout(new FillLayout());
			
			for (String s : movingAvg){
				Button b = new Button(maComp, SWT.PUSH);
				maButtons.add(b);
				b.setText(s);
				b.setData("selected", false);
				b.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button) e.widget;
						boolean selected = (boolean) b.getData("selected");
						b.setData("selected", !selected);
						
						boolean first = true;
						String str = "";
						for (Button bi : maButtons){
							boolean selectedi = (boolean) bi.getData("selected");
							if (selectedi){
								if (first){
									str = str + bi.getText();
									first = false;
								} else {
									str = str + "," + bi.getText();
								}
							}
						}
						chartLabel.setData("p", str);
						refreshChart();
					}
				});
			}
		}
		
		{
			
			Label l2 = new Label(chartComposite, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("EMA");
			
			Composite emaComp = new Composite(chartComposite, SWT.NONE);
			emaComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			emaComp.setLayout(new FillLayout());
			
			for (String s : emaList){
				Button b = new Button(emaComp, SWT.PUSH);
				maButtons.add(b);
				b.setText(s);
				b.setData("selected", false);
				b.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button) e.widget;
						boolean selected = (boolean) b.getData("selected");
						b.setData("selected", !selected);
						
						boolean first = true;
						String str = "";
						for (Button bi : maButtons){
							boolean selectedi = (boolean) bi.getData("selected");
							if (selectedi){
								if (first){
									str = str + bi.getText();
									first = false;
								} else {
									str = str + "," + bi.getText();
								}
							}
						}
						chartLabel.setData("p", str);
						refreshChart();
					}
				});
			}
		}
		
		chartLabel = new Label(bar, SWT.NONE);
		chartLabel.setData("t", "1y");
		chartLabel.setData("q", "l");
		chartLabel.setData("p", "");
		
		ExpandItem item0 = new ExpandItem (bar, SWT.NONE, 0);
		item0.setText("Chart settings");
		item0.setHeight(chartComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl(chartComposite);

		item1 = new ExpandItem (bar, SWT.NONE, 1);
		item1.setText("Chart");
		item1.setHeight(chartLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setControl(chartLabel);
		item1.setExpanded(true);
	}
	
	public void refreshChart() {
		final StockData sd = appState.getSelectedStock();
		final String t = (String)chartLabel.getData("t");
		final String q = (String)chartLabel.getData("q");
		final String p = (String)chartLabel.getData("p");
		Thread thr = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					String urlStr = "http://chart.finance.yahoo.com/z?s=" + sd.getStock().getSymbol() + "&t="+ t +"&q=" + q + "&l=off&z=m&a=v&lang=en-US&region=US";
					if (!"".equals(p)){
						urlStr += "&p=" + p;
					}
					System.err.println(urlStr);
					URL url = new URL(urlStr);
					InputStream is = url.openStream();
					final Image img = new Image(Display.getDefault(), is);
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							chartLabel.setImage(img);		
							item1.setHeight(chartLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
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