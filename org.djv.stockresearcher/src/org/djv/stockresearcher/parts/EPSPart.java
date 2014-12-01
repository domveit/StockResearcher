 
package org.djv.stockresearcher.parts;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.model.AnalystEstimate;
import org.djv.stockresearcher.model.AnalystEstimates;
import org.djv.stockresearcher.model.FinDataPeriod;
import org.djv.stockresearcher.model.FinDataRow;
import org.djv.stockresearcher.model.FinDataTable;
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
					
					BigDecimal averagePe = BigDecimal.ZERO;
					
					List<HistPrice> prices = StockDB.getInstance().getAnalystBroker().getMonthlyPrices(sd, 7);
					FinDataTable incomeStatement = StockDB.getInstance().getFinBroker().getIncomeStatement(sd);

					Map<FinDataPeriod, BigDecimal> fairPriceMap = new TreeMap<FinDataPeriod, BigDecimal>();
					
					List<FinDataRow> isRows = incomeStatement.getRows();
					FinDataRow earningsRow = null;
					FinDataRow dilutedSharesRow = null;
					for (FinDataRow row : isRows){
						if (row.getName().startsWith("Net income available")){
							earningsRow = row;
						}
						if (row.getName().startsWith("Diluted")){
							dilutedSharesRow = row;
						}
					}
					if (earningsRow != null && dilutedSharesRow != null){
						BigDecimal avgMarketCap = BigDecimal.ZERO;
						BigDecimal earningsBucket = BigDecimal.ZERO;
						
						int factor = 5;
						List<FinDataPeriod> isPeriods = incomeStatement.getPeriods();
						for (FinDataPeriod isPeriod : isPeriods){
							int y = isPeriod.getYear();
							if (y == 9999){
								y = Calendar.getInstance().get(Calendar.YEAR);
							}
							Map<FinDataPeriod, BigDecimal> earningsRowMap = incomeStatement.getDataMap().get(earningsRow);
							
							BigDecimal earnings = earningsRowMap.get(isPeriod);
							if (earnings == null){
								earnings = BigDecimal.ZERO;
							}
							System.err.println("earnings for " + y + " " + earnings);
							
							Map<FinDataPeriod, BigDecimal> dilutedSharesMap = incomeStatement.getDataMap().get(dilutedSharesRow);
							
							BigDecimal dilutedShares = dilutedSharesMap.get(isPeriod);
							if (dilutedShares == null){
								dilutedShares = BigDecimal.ZERO;
							}
							System.err.println("dilutedShares for " + y + " " + dilutedShares);
							
							BigDecimal avgPrice = BigDecimal.ZERO;
							BigDecimal priceBucket = BigDecimal.ZERO;
							int nbrPrices = 0;
							for (HistPrice hp : prices){
								if (hp.getDate().get(Calendar.YEAR) == y){
									priceBucket = priceBucket.add(hp.getPrice());
									nbrPrices++;
								}
							}
							if (dilutedShares != null && dilutedShares.compareTo(BigDecimal.ZERO) != 0) {
								BigDecimal avgEPS = earnings.divide(dilutedShares, 2, RoundingMode.HALF_UP);
								if (nbrPrices > 0){
									avgPrice = priceBucket.divide(BigDecimal.valueOf(nbrPrices), 2, RoundingMode.HALF_UP);
									System.err.println("avg mkt cap for " + y + " " + avgPrice);
									System.err.println("avg EPS for " + y + " " + avgEPS);
									if (avgEPS.compareTo(BigDecimal.ZERO) != 0){
										System.err.println("avg PE for " + y + " " + avgPrice.divide(avgEPS, 2, RoundingMode.HALF_UP));
									}
									earningsBucket = earningsBucket.add(earnings.multiply(BigDecimal.valueOf(factor)));
									avgMarketCap = avgMarketCap.add(avgPrice.multiply(dilutedShares).multiply(BigDecimal.valueOf(factor)));

								}
								fairPriceMap.put(isPeriod,avgEPS );
								factor += 3;
							}
						}
						if (earningsBucket.compareTo(BigDecimal.ZERO) > 0){
							averagePe = avgMarketCap.divide(earningsBucket, 2, RoundingMode.HALF_UP);
							System.err.println("avg PE " +averagePe);
						}
					}
					
					TimeSeriesCollection dataset = new TimeSeriesCollection();
//					Map<String, FinKeyData> finMap = sd.getFinData();
					Integer lastYear = null;
					Integer lastMonth = null;
					BigDecimal lastEps = null;
					TimeSeries histSeries = new TimeSeries("Avg PE (" + averagePe + ")");
					for (FinDataPeriod isPeriod : fairPriceMap.keySet()){
						BigDecimal epsForPeriod = fairPriceMap.get(isPeriod);
						if (epsForPeriod != null && isPeriod.getYear() != null && isPeriod.getMonth() != null){
							if (isPeriod.getYear() == 9999){
								{
								Calendar c = Calendar.getInstance();
								c.roll(Calendar.MONTH, -1);
								int month = c.get(Calendar.MONTH) + 1;
								int year = c.get(Calendar.YEAR);
								histSeries.add(new Month(month, year), Math.max(epsForPeriod.multiply(averagePe).doubleValue(), 0));
								lastYear = year;
								lastMonth = month;
								lastEps = epsForPeriod.multiply(averagePe);
								}
								continue;
							}
							if ( isPeriod.getMonth() == -1){
								continue;
							}
							lastYear = isPeriod.getYear();
							lastMonth = isPeriod.getMonth();
							histSeries.add(new Month( lastMonth,  lastYear), Math.max(epsForPeriod.multiply(averagePe).doubleValue(), 0));

							lastEps = epsForPeriod.multiply(averagePe);
						}

					}
					dataset.addSeries(histSeries);
					
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
					
					
					TimeSeries priceSeries = new TimeSeries("Prices");
					
					for (HistPrice price: prices){
						Calendar c = price.getDate();
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