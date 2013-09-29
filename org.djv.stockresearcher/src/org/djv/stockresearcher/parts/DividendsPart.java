 
package org.djv.stockresearcher.parts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.DivYearData;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;

public class DividendsPart implements AppStateListener {
	
	private Label divChartLabel;
	
	private String[] divTitles = {"Date", "Dividend", "Norm", "Growth"};
	private Table divTable;
	
	private Composite parent;
	
	@Inject
	public DividendsPart() {
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(2, true));
		this.parent = parent;
		divChartLabel = new Label(parent, SWT.NONE);
		divChartLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		divTable = new Table(parent, SWT.BORDER |SWT.V_SCROLL);
		divTable.setLinesVisible(true);
		divTable.setHeaderVisible(true);
		GridData data2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		divTable.setLayoutData(data2);
		
		for (int i=0; i < divTitles.length; i++) {
			TableColumn column = new TableColumn (divTable, SWT.NONE);
			column.setText (divTitles [i]);
		}
		AppState.getInstance().addListener(this);
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				createChart();
			}
		});
	}
	
	private void createChart() {
		final StockData sd = AppState.getInstance().getSelectedStock();
		if (sd == null){
			return;
		}
		if (sd.getDivYearData() == null){
			return;
		}
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					TimeSeries series1 = new TimeSeries("Individual Payouts");
					TimeSeries series2 = new TimeSeries("Yearly Payouts");
					for (Integer y : sd.getDivYearData().keySet()) {
						if (Calendar.getInstance().get(Calendar.YEAR) != y) {
							DivYearData dyd = sd.getDivYearData().get(y);
							if (dyd.getDivDetail().isEmpty()) {
								Calendar c = Calendar.getInstance();
								c.set(Calendar.MONTH, 6);
								c.set(Calendar.DATE, 1);
								c.set(Calendar.YEAR, y);
								series1.add(new Day(c.getTime()), 0.00);
							} else {
								for (DivData dd : dyd.getDivDetail()) {
									series1.add(new Day(dd.getPaydate()),
											dd.getDividend());
								}
							}
							series2.add(new Year(y), dyd.getDiv().doubleValue());
						}
					}
			
					TimeSeriesCollection dataset = new TimeSeriesCollection();
					dataset.addSeries(series1);
					dataset.addSeries(series2);
			
					// Create the actuall JFreeChart chart
					JFreeChart divChart = ChartFactory.createTimeSeriesChart("Dividends",
							"Date", "Div", dataset, true, false, false);
			
					// Write the image to a buffer in memory using AWT
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ChartUtilities.writeChartAsPNG(out, divChart, 400, 200);
					out.close();
			
					// Load the image from the same buffer using SWT
					ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
					final Image divChartImage = new Image(Display.getDefault(), in);
					in.close();
			
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							divChartLabel.setImage(divChartImage);
							
							divTable.removeAll();
							if (sd.getDivYearData() != null) {
								Calendar c = Calendar.getInstance();
								int currYear = c.get(Calendar.YEAR);
					
								for (int y = currYear; y > 1900; y--) {
									DivYearData dyd = sd.getDivYearData().get(y);
									if (dyd == null) {
										continue;
									}
									TableItem item = new TableItem(divTable, SWT.NONE);
									item.setFont(new Font(Display.getDefault(), "Tahoma", 10,
											SWT.BOLD));
									item.setText(0, String.valueOf(dyd.getYear()));
									item.setText(1, String.valueOf(dyd.getDiv()));
									if (dyd.getDiv().compareTo(dyd.getNormalizedDiv()) != 0) {
										item.setText(2, String.valueOf(dyd.getNormalizedDiv()));
									}
									item.setText(3,
											String.valueOf(dyd.getPctIncreaseOverPreviousYear())
													+ "%");
					
									for (DivData dd : dyd.getDivDetail()) {
										TableItem item2 = new TableItem(divTable, SWT.NONE);
										item2.setText(
												0,
												"     "
														+ new SimpleDateFormat("MM/dd/yyyy")
																.format(dd.getPaydate()));
										item2.setText(1, String.valueOf(dd.getDividend()));
										if (dd.getDividend().compareTo(dd.getNormalizedDivided()) != 0) {
											item2.setText(2,
													String.valueOf(dd.getNormalizedDivided()));
										}
									}
								}
					
								for (int i = 0; i < divTitles.length; i++) {
									divTable.getColumn(i).pack();
								}
							}
							parent.layout(true, true);
						}
						
					});
					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
		});
		t.start();
	}
	
	@Focus
	public void onFocus() {
	}

	@Override
	public void notifyChanged(AppState appState) {
		createChart();
	}
	
	
}