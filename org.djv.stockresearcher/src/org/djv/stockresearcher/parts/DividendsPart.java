 
package org.djv.stockresearcher.parts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.db.StockDataChangeListener;
import org.djv.stockresearcher.model.AdjustedDiv;
import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.DivYearData;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.widgets.DivAdjDialog;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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

public class DividendsPart implements AppStateListener, StockDataChangeListener {
	
	private Label divChartLabel;
	
	private String[] divTitles = {"Date", "Dividend", "AdjDate", "AdjDiv", "Growth"};
	private Table divTable;
	
	private Composite parent;
	
	@Inject
	public DividendsPart() {
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		this.parent = parent;
		divChartLabel = new Label(parent, SWT.NONE);
		divChartLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		divTable = new Table(parent, SWT.BORDER |SWT.V_SCROLL);
		divTable.setLinesVisible(true);
		divTable.setHeaderVisible(true);
		GridData data2 = new GridData(SWT.FILL, SWT.FILL, false, true);
		divTable.setLayoutData(data2);
		
		divTable.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem[] selection = divTable.getSelection();
				TableItem item = selection[0];
				DivData dd = (DivData) item.getData("dd");
				if (dd == null){
					return;
				}
				
				DivAdjDialog td = new DivAdjDialog(divTable.getShell());
				
				AdjustedDiv adjDiv = new AdjustedDiv();
				td.setAdjDiv(adjDiv);
				
				adjDiv.setSymbol(dd.getSymbol());
				adjDiv.setPaydate(dd.getPaydate());
				adjDiv.setAdjustedDate(dd.getAdjustedDate());
				adjDiv.setAdjustedDiv(dd.getAdjustedDividend());
				
				td.create();
				int result = td.open();
				if (result == Window.OK) {
					try {
						AdjustedDiv ad = td.getAdjDiv();
						StockDB.getInstance().insertUpdateDeleteDivAdjustment(ad);
						StockDB.getInstance().updateStockFineData(Arrays.asList(AppState.getInstance().getSelectedStock()), true);
					} catch (Exception e1) {
						MessageDialog.openError(divTable.getShell(), "Error", e1.getMessage());
					}
				} 
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
			
		});
		
		for (int i=0; i < divTitles.length; i++) {
			TableColumn column = new TableColumn (divTable, SWT.NONE);
			column.setText (divTitles [i]);
		}
		AppState.getInstance().addListener(this);
		StockDB.getInstance().addStockDataChangeListener(this);
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
									Date d = dd.getPaydate();
									if (dd.getAdjustedDate() != null){
										d = dd.getAdjustedDate();
									}
									BigDecimal bd = dd.getDividend();
									if (dd.getAdjustedDividend() != null){
										if (dd.getAdjustedDividend().compareTo(BigDecimal.ZERO) == 0){
											continue;
										}
										bd = dd.getAdjustedDividend();
									}
									series1.add(new Day(d), bd);
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
									item.setText(3,
											String.valueOf(dyd.getPctIncreaseOverPreviousYear())
													+ "%");
					
									for (DivData dd : dyd.getDivDetail()) {
										TableItem item2 = new TableItem(divTable, SWT.NONE);
										item2.setData("dd", dd);
										item2.setText(
												0,
												"     "
														+ new SimpleDateFormat("MM/dd/yyyy")
																.format(dd.getPaydate()));
										item2.setText(1, String.valueOf(dd.getDividend()));
										if (dd.getAdjustedDate() != null) {
											item2.setText(2,new SimpleDateFormat("MM/dd/yyyy")
											.format(dd.getAdjustedDate()));
										}
										if (dd.getAdjustedDividend() != null) {
											item2.setText(3,String.valueOf(dd.getAdjustedDividend()));
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

	@Override
	public void notifyChanged(StockData stockData, int stocksToUpdate,
			int stocksUpdated) {
		if (stockData == AppState.getInstance().getSelectedStock()){
			createChart();
		}
	}
	
	
}