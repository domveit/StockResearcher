 
package org.djv.stockresearcher.parts;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.AnalystEstimate;
import org.djv.stockresearcher.model.AnalystEstimates;
import org.djv.stockresearcher.model.FinKeyData;
import org.djv.stockresearcher.model.HistPrice;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class EPSPart implements AppStateListener  {
	
	private Label epsChartLabel;
	private Composite parent;
	
	@Inject
	public EPSPart() {
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		this.parent = parent;
		epsChartLabel = new Label(parent, SWT.NONE);
		epsChartLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
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
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					BigDecimal averagePe = StockDB.getInstance().getAnalystBroker().getAveragePE(sd, 5);
					
					TimeSeriesCollection dataset = new TimeSeriesCollection();
					Map<String, FinKeyData> finMap = sd.getFinData();
					Integer lastYear = null;
					Integer lastMonth = null;
					BigDecimal lastEps = null;
					if (finMap != null){
						TimeSeries histSeries = new TimeSeries("Avg PE (" + averagePe + ")");
						for (String key : finMap.keySet()){
							FinKeyData data = finMap.get(key);
							if (data.getEarningsPerShare() != null && data.getYear() != null && data.getMonth() != null){
								if (data.getYear() == 9999){
									{
									Calendar c = Calendar.getInstance();
									c.roll(Calendar.MONTH, -1);
									int month = c.get(Calendar.MONTH) + 1;
									int year = c.get(Calendar.YEAR);
									histSeries.add(new Month(month, year), Math.max(data.getEarningsPerShare().multiply(averagePe).doubleValue(), 0));
									lastYear = year;
									lastMonth = month;
									lastEps = data.getEarningsPerShare().multiply(averagePe);
									}
									continue;
								}
								if (data.getMonth() == -1){
									continue;
								}
								System.err.println(data.getMonth());
								histSeries.add(new Month(data.getMonth(), data.getYear()), Math.max(data.getEarningsPerShare().multiply(averagePe).doubleValue(), 0));
								lastYear = data.getYear();
								lastMonth = data.getMonth();
								lastEps = data.getEarningsPerShare().multiply(averagePe);
							}

						}
						dataset.addSeries(histSeries);
					}
					
					AnalystEstimates ae = StockDB.getInstance().getAnalystBroker().getAnalystEstimates(sd);
					
					if (ae != null){
						TimeSeries meanSeries = new TimeSeries("Mean Est");
						TimeSeries highSeries = new TimeSeries("High Est");
						TimeSeries lowSeries = new TimeSeries("Low Est");
						if (lastYear != null){
							meanSeries.add(new Month(lastMonth, lastYear),  Math.max(lastEps.doubleValue(), 0));
							highSeries.add(new Month(lastMonth, lastYear),  Math.max(lastEps.doubleValue(), 0));
							lowSeries.add(new Month(lastMonth, lastYear),  Math.max(lastEps.doubleValue(), 0));
						}
						AnalystEstimate oneYrEstimate = ae.getOneYrEstimate();
						if (oneYrEstimate != null){
							if (oneYrEstimate.getMean() != null){
								meanSeries.addOrUpdate(new Month(oneYrEstimate.getMonth(), oneYrEstimate.getYear()), Math.max(oneYrEstimate.getMean().multiply(averagePe).doubleValue(), 0));
							}
							if (oneYrEstimate.getHigh() != null){
								highSeries.addOrUpdate(new Month(oneYrEstimate.getMonth(), oneYrEstimate.getYear()), Math.max(oneYrEstimate.getHigh().multiply(averagePe).doubleValue(), 0));
							}
							if (oneYrEstimate.getLow() != null){
								lowSeries.addOrUpdate(new Month(oneYrEstimate.getMonth(), oneYrEstimate.getYear()), Math.max(oneYrEstimate.getLow().multiply(averagePe).doubleValue(), 0));
							}
						}
						
						AnalystEstimate twoYrEstimate = ae.getTwoYrEstimate();
						if (twoYrEstimate != null){
							if (twoYrEstimate.getMean() != null){
								meanSeries.addOrUpdate(new Month(twoYrEstimate.getMonth(), twoYrEstimate.getYear()), Math.max(twoYrEstimate.getMean().multiply(averagePe).doubleValue(), 0));
							}
							if (twoYrEstimate.getHigh() != null){
								highSeries.addOrUpdate(new Month(twoYrEstimate.getMonth(), twoYrEstimate.getYear()), Math.max(twoYrEstimate.getHigh().multiply(averagePe).doubleValue(), 0));
							}
							if (twoYrEstimate.getLow() != null){
								lowSeries.addOrUpdate(new Month(twoYrEstimate.getMonth(), twoYrEstimate.getYear()), Math.max(twoYrEstimate.getLow().multiply(averagePe).doubleValue(), 0));
							}
						}
						dataset.addSeries(meanSeries);
						dataset.addSeries(highSeries);
						dataset.addSeries(lowSeries);
					}
					
					List<HistPrice> prices = StockDB.getInstance().getAnalystBroker().getMonthlyPrices(sd, 10);
					TimeSeries priceSeries = new TimeSeries("Prices");
					
					for (HistPrice price: prices){
						Calendar c = Calendar.getInstance();
						c.setTime(price.getDate());
						priceSeries.add(new Month(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)), price.getPrice().doubleValue());
					}
					dataset.addSeries(priceSeries);
					
					// Create the actuall JFreeChart chart
					JFreeChart divChart = ChartFactory.createTimeSeriesChart("EPS forecast for " + sd.getSymbol(), "Date", "EPS", dataset, true, false, false);
			
					XYPlot plot = (XYPlot) divChart.getPlot();
					plot.getRenderer().setSeriesPaint(4, Color.BLACK);
					
					// Write the image to a buffer in memory using AWT
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ChartUtilities.writeChartAsPNG(out, divChart, 600, 300);
					out.close();
			
					// Load the image from the same buffer using SWT
					ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
					final Image divChartImage = new Image(Display.getDefault(), in);
					in.close();
			
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							epsChartLabel.setImage(divChartImage);
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

	@Override
	public void notifyChanged(AppState appState) {
		createChart();
	}
	
	
}