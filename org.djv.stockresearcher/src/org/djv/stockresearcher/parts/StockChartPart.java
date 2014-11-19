 
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
	
//	Bollinger_Bands	 Bollinger Bands	b
//	Parabolic_SAR	 Parabolic Stop And Reverse	p
//	Splits	 Splits	s
//	Volume	 Volume (inside chart)	v
	
	String [] techInd1 = {"b", "p", "s", "v"};
	
	List<Button> maButtons = new ArrayList<Button>();
	
	ExpandItem item1 ;
	
	private Composite chartComp;
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
		
		Composite chartSettings = new Composite(bar, SWT.BORDER);
		chartSettings.setLayout(new GridLayout(2, false));
		
		{
			
			Label l2 = new Label(chartSettings, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("Chart Type");
			
			Composite chartTypeComp = new Composite(chartSettings, SWT.NONE);
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
		
		SelectionAdapter pSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.widget;
				boolean selected = (boolean) b.getData("selected");
				selected = !selected;
				b.setData("selected", selected);
				String pInd = (String)b.getData("pInd");
				
				@SuppressWarnings("unchecked")
				List<String> pVals = (List<String>)chartLabel.getData("pVals");
				
				if (selected){
					pVals.add(pInd);
				} else {
					pVals.remove(pInd);
				}
				refreshChart();
			}
		};
		
		SelectionAdapter aSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.widget;
				boolean selected = (boolean) b.getData("selected");
				selected = !selected;
				b.setData("selected", selected);
				String aInd = (String)b.getData("aInd");
				
				@SuppressWarnings("unchecked")
				List<String> aVals = (List<String>)chartLabel.getData("aVals");
				
				if (selected){
					aVals.add(aInd);
				} else {
					aVals.remove(aInd);
				}
				refreshChart();
			}
		};
		
		{
			
			Label l2 = new Label(chartSettings, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("Moving Avg");
			
			Composite maComp = new Composite(chartSettings, SWT.NONE);
			maComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			maComp.setLayout(new FillLayout());
			
			for (String s : movingAvg){
				Button b = new Button(maComp, SWT.PUSH);
				maButtons.add(b);
				b.setText(s);
				b.setData("selected", false);
				b.setData("pInd", s);
				b.addSelectionListener(pSelectionAdapter);
			}
		}

		{
			Label l2 = new Label(chartSettings, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("EMA");
			
			Composite emaComp = new Composite(chartSettings, SWT.NONE);
			emaComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			emaComp.setLayout(new FillLayout());
			
			for (String s : emaList){
				Button b = new Button(emaComp, SWT.PUSH);
				maButtons.add(b);
				b.setText(s);
				b.setData("selected", false);
				b.setData("pInd", s);
				b.addSelectionListener(pSelectionAdapter);
			}
		}
		
		{
			Label l2 = new Label(chartSettings, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("Tech1");
			
			Composite buttonComp = new Composite(chartSettings, SWT.NONE);
			buttonComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			buttonComp.setLayout(new FillLayout());
			
			Button b = new Button(buttonComp, SWT.PUSH);
			b.setText("Bollinger Bands");
			b.setData("selected", false);
			b.setData("pInd", "b");
			b.addSelectionListener(pSelectionAdapter);
			
			Button b1 = new Button(buttonComp, SWT.PUSH);
			b1.setText("Parabolic_SAR");
			b1.setData("selected", false);
			b1.setData("pInd", "p");
			b1.addSelectionListener(pSelectionAdapter);
			
			Button b2 = new Button(buttonComp, SWT.PUSH);
			b2.setText("Splits");
			b2.setData("selected", false);
			b2.setData("pInd", "s");
			b2.addSelectionListener(pSelectionAdapter);
			
			Button b3 = new Button(buttonComp, SWT.PUSH);
			b3.setText("Volume");
			b3.setData("selected", false);
			b3.setData("pInd", "v");
			b3.addSelectionListener(pSelectionAdapter);
		}
		
		{
			Label l2 = new Label(chartSettings, SWT.NONE);
			l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l2.setText("Tech2");
			
			Composite buttonComp = new Composite(chartSettings, SWT.NONE);
			buttonComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			buttonComp.setLayout(new FillLayout());
			
//			ast_Stoch	 Stochastic	fs
//			MACD	 Moving-Average-Convergence-Divergence	m26-12-9
//			MFI	 Money Flow Index	f14
//			ROC	 Rate of Change	p12
//			RSI	 Relative Strength Index	r14
//			Slow_Stoch	 Slow Stochastic	ss
//			Vol	 Volume	v
//			Vol_MA	 Volume with Moving Average	vm
//			W_R	 Williams Percent Range	w14
			
			Button b = new Button(buttonComp, SWT.PUSH);
			b.setText("Fast Stoch");
			b.setData("selected", false);
			b.setData("aInd", "fs");
			b.addSelectionListener(aSelectionAdapter);
			
			Button b2 = new Button(buttonComp, SWT.PUSH);
			b2.setText("Slow Stoch");
			b2.setData("selected", false);
			b2.setData("aInd", "ss");
			b2.addSelectionListener(aSelectionAdapter);
			
			Button b3 = new Button(buttonComp, SWT.PUSH);
			b3.setText("MACD 26");
			b3.setData("selected", false);
			b3.setData("aInd", "m26");
			b3.addSelectionListener(aSelectionAdapter);
			
			Button b4 = new Button(buttonComp, SWT.PUSH);
			b4.setText("MACD 12");
			b4.setData("selected", false);
			b4.setData("aInd", "m12");
			b4.addSelectionListener(aSelectionAdapter);
			
			Button b5 = new Button(buttonComp, SWT.PUSH);
			b5.setText("MACD 9");
			b5.setData("selected", false);
			b5.setData("aInd", "m9");
			b5.addSelectionListener(aSelectionAdapter);
			
			Button b6 = new Button(buttonComp, SWT.PUSH);
			b6.setText("RSI 14");
			b6.setData("selected", false);
			b6.setData("aInd", "r14");
			b6.addSelectionListener(aSelectionAdapter);
		}

		chartComp = new Composite(bar, SWT.BORDER);
		chartComp.setLayout(new GridLayout(2, false));
		
		{
			Label l = new Label(chartComp, SWT.NONE);
			l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			l.setText("Time Period");
				
			Composite timePeriodComp = new Composite(chartComp, SWT.NONE);
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

		chartLabel = new Label(chartComp, SWT.NONE);
		chartLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		chartLabel.setData("t", "1y");
		chartLabel.setData("q", "l");
		chartLabel.setData("pVals", new ArrayList<String>());
		chartLabel.setData("aVals", new ArrayList<String>());
		
		ExpandItem item0 = new ExpandItem (bar, SWT.NONE, 0);
		item0.setText("More chart settings");
		item0.setHeight(chartSettings.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl(chartSettings);

		item1 = new ExpandItem (bar, SWT.NONE, 1);
		item1.setText("Chart");
		item1.setHeight(chartComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setControl(chartComp);
		item1.setExpanded(true);
	}
	
	public void refreshChart() {
		final StockData sd = appState.getSelectedStock();
		final String t = (String)chartLabel.getData("t");
		final String q = (String)chartLabel.getData("q");
		@SuppressWarnings("unchecked")
		final List<String> pVals = (List<String>)chartLabel.getData("pVals");
		
		@SuppressWarnings("unchecked")
		final List<String> aVals = (List<String>)chartLabel.getData("aVals");
		
		Thread thr = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					String urlStr = "http://chart.finance.yahoo.com/z?s=" + sd.getStock().getSymbol() + "&t="+ t +"&q=" + q + "&l=off&z=m&lang=en-US&region=US";
					if (!pVals.isEmpty()){
						boolean first = true;
						String p = "";
						for (String pVal: pVals){
							if (first){
								first = false;
							} else {
								p = p + ",";
							}
							p = p + pVal;
						}
						urlStr += "&p=" + p;
					}
					
					if (!aVals.isEmpty()){
						boolean first = true;
						String a = "";
						for (String aVal: aVals){
							if (first){
								first = false;
							} else {
								a = a + ",";
							}
							a = a + aVal;
						}
						urlStr += "&a=" + a;
					}
					System.err.println(urlStr);
					URL url = new URL(urlStr);
					InputStream is = url.openStream();
					final Image img = new Image(Display.getDefault(), is);
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							chartLabel.setImage(img);		
							item1.setHeight(chartComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
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