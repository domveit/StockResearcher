package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.FinPeriodData;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.e4.core.di.annotations.Creatable;

import au.com.bytecode.opencsv.CSVReader;

@Creatable
public class StockDB {
	
	final ExecutorService pool = Executors.newFixedThreadPool(8);
	Map<String, StockData> stockMap = new HashMap<String, StockData>();
	Map<Integer, List<StockData>> industryStockMap = new HashMap<Integer, List<StockData>>();
	
	List<IndustryStockListener> industryStockListeners = new ArrayList<IndustryStockListener>();
	
	List<StockDataChangeListener> stockDataChangeListeners = new ArrayList<StockDataChangeListener>();
	
	@Inject
	private SectorIndustryRegistry sir;
	
	public void notifyAllIndustryStockListeners(int industry, List<StockData> sdl) {
		for (IndustryStockListener isl : industryStockListeners){
			isl.notifyChanged(industry, sdl);
		}
	}
	
	public void addIndustryStockListener(IndustryStockListener isl) {
		industryStockListeners.add(isl);
	}
	
	public void notifyAllStockDataChangeListeners(StockData sd) {
		for (StockDataChangeListener sdcl : stockDataChangeListeners){
			sdcl.notifyChanged(sd);
		}
	}
	
	public void addStockDataChangeListener(StockDataChangeListener sdcl) {
		stockDataChangeListeners.add(sdcl);
	}
	
	public void getDataForStocks(final List<StockData> stocks) throws Exception {
		int nbrStocks = stocks.size();
		
		int nbrGroups = (nbrStocks - 1) /50 + 1;
		for (int i = 1; i <= nbrGroups; i ++){
			List<StockData> subList = null;
			if (i == nbrGroups){
				subList = stocks.subList(50 * (i-1), stocks.size());
			} else {
				subList = stocks.subList(50 * (i-1), 50 * i);
			}
			final List<StockData> subListFinal = subList;
			pool.execute(new Runnable(){
				@Override
				public void run() {
					try {
						getDataForStocksInternal(subListFinal);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});
		}
		return;
	}
	
	public void getDataForStocksInternal(List<StockData> stocks) throws Exception {
		YahooFinanceUtil.createStockFiles(stocks);
		
		for (StockData sd : stocks) {
			Reader br = new InputStreamReader(new FileInputStream(StockDBUtil.getStockDataFile(sd.getSymbol())));
			CSVReader reader = new CSVReader(br);
			String [] nextCSVLine = reader.readNext();
			if (nextCSVLine	!= null){ 
				String name = nextCSVLine[0];
				String price = nextCSVLine[1];
				String marketCap = nextCSVLine[2];
				String dividend = nextCSVLine[3];
				String yield = nextCSVLine[4];
				String pe = nextCSVLine[5];
				String peg = nextCSVLine[6];
				String exchange = nextCSVLine[7];
				
				sd.setName(name);
				sd.setDividend(dividend);
				sd.setPe(pe);
				sd.setPeg(peg);
				sd.setPrice(price);
				sd.setYield("N/A".equals(yield)? null: Double.parseDouble(yield));
				sd.setMarketCap(marketCap);
				sd.setExchange(exchange);
			}
			notifyAllStockDataChangeListeners(sd);
			reader.close();
			
			final StockData finalSD = sd;
			pool.execute(new Runnable(){
				@Override
				public void run() {
					try {
						getDivData(finalSD);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});
		}
	}


	public void getDivData(StockData stockData) throws Exception {
		System.err.println("getting div data for " + stockData.getSymbol());
		List<DivData> ddl = getHistoricalDividend(stockData.getSymbol());
		stockData.setDivData(ddl);
		StockDataUtil.crunchDividends(stockData);
		notifyAllStockDataChangeListeners(stockData);
	}

	public List<DivData> getHistoricalDividend(String symbol) throws Exception {
		List<DivData> l = new ArrayList<DivData>();
		File divFile= StockDBUtil.getDivDataFile(symbol);
		if (!divFile.exists()){
			YahooFinanceUtil.createDivFile(symbol);
		}
		
		if (!divFile.exists()){
			return l;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(divFile)));
		String sDiv = null;
		while ((sDiv = br.readLine()) != null){
			if (sDiv.startsWith("Date")){
				continue;
			}
			StringTokenizer st = new StringTokenizer(sDiv, ",");
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(st.nextToken());
			BigDecimal div = new BigDecimal(st.nextToken());
			if (div.compareTo(BigDecimal.ZERO) == 0){
				continue;
			}
			DivData dd = new DivData();
			Calendar c2 = Calendar.getInstance();
			c2.setTime(date);
			dd.setDate(c2);
			dd.setDividend(div);
			l.add(dd);
		}
		br.close();
		
		Collections.sort(l, new Comparator<DivData>() {
			@Override
			public int compare(DivData dd1, DivData dd2) {
				return dd2.getDate().compareTo(dd1.getDate());
			}
		});
		return l;
	}
	
	public void getFinData(StockData stockData) throws Exception {
		if (stockData.getFinData() == null){
			System.err.println("getting fin data for " + stockData.getSymbol());
			Map<String, FinPeriodData> fdl = getFinDataHist(stockData.getSymbol(), stockData.getExchange());
			stockData.setFinData(fdl);
			StockDataUtil.crunchFinancials(stockData);
		}
	}
	
	public Map<String, FinPeriodData> getFinDataHist(String symbol, String exchange) throws Exception {
		Map<String, FinPeriodData> l = new TreeMap<String, FinPeriodData>();
		Map<Integer, FinPeriodData> listInt = new TreeMap<Integer, FinPeriodData>();
		String convertedSymbol = symbol;
		int dotIx = symbol.indexOf('.');
		if (dotIx > -1){
			convertedSymbol = symbol.substring(0, dotIx);
		}
		
		String msExchange = StockDBUtil.mapYahooExchangeToMorningStar(exchange);
		if (msExchange == null){
			System.err.println("Could not map to morningstar exhange " + symbol + ":" + exchange);
			return l;
		}
		if ("XHKG".equals(msExchange)){
			convertedSymbol = "0" + convertedSymbol;
			
		}
		File f= StockDBUtil.getFinDataFile(symbol);
		if (!f.exists()){
			boolean worked = YahooFinanceUtil.createFinFile(symbol, convertedSymbol, msExchange);
			// sometimes "other OTC" is "PINX"
			if (!worked && "XOTC".equals(msExchange)){
				YahooFinanceUtil.createFinFile(symbol, convertedSymbol, "PINX");
			}
		}
		
		if (!f.exists()){
			return l;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		
		CSVReader reader = new CSVReader(br, ',');
	    String [] nextLine;
	    int ln = 0;
	    while ((nextLine = reader.readNext()) != null) {
	    	ln++;
	    	if (ln < 3){
	    		continue;
	    	}
	    	if (ln == 3){
		    	for (int yr = 1; yr< nextLine.length ; yr ++){
		    		FinPeriodData fd = new FinPeriodData();
		    		String period = nextLine[yr];
		    		fd.setPeriod(period);
		    		l.put(period, fd);
		    		listInt.put(yr, fd);
		    	}
		    	continue;
	    	}
	    	
	    	setFinValues(listInt, nextLine, "Revenue .{3} Mil", "revenue");
	    	setFinValues(listInt, nextLine, "Gross Margin %", "grossMargin");
	    	setFinValues(listInt, nextLine, "Operating Income .{3} Mil", "operatingIncome");
	    	setFinValues(listInt, nextLine, "Operating Margin %", "operatingMargin");
	    	setFinValues(listInt, nextLine, "Net Income .{3} Mil", "netIncome");
	    	setFinValues(listInt, nextLine, "Earnings Per Share .{3}", "earningsPerShare");
	    	setFinValues(listInt, nextLine, "Dividends .{3}", "dividends");
	    	setFinValues(listInt, nextLine, "Payout Ratio %", "payoutRatio");
	    	setFinValues(listInt, nextLine, "Shares Mil", "shares");
	    	setFinValues(listInt, nextLine, "Book Value Per Share .{3}", "bookValuePerShare");
	    	setFinValues(listInt, nextLine, "Operating Cash Flow .{3} Mil", "operatingCashFlow");
	    	setFinValues(listInt, nextLine, "Cap Spending .{3} Mil", "capitalSpending");
	    	setFinValues(listInt, nextLine, "Free Cash Flow .{3} Mil", "freeCashFlow");
	    	setFinValues(listInt, nextLine, "Free Cash Flow Per Share .{3}", "freeCashFlowPerShare");
	    	setFinValues(listInt, nextLine, "Working Capital .{3} Mil", "workingCapital");
	    	
	    	
//	    	Revenue USD Mil,"18,878","22,045","24,801","28,484","34,922","39,540","36,117","40,040","43,218","46,061","47,880"
//	    	Gross Margin %,70.1,68.6,67.2,65.8,64.0,64.5,63.9,64.0,61.4,61.2,60.9
//	    	Operating Income USD Mil,"4,882","6,292","7,416","6,996","8,621","9,442","7,322","9,164","7,674","10,065","10,753"
//	    	Operating Margin %,25.9,28.5,29.9,24.6,24.7,23.9,20.3,22.9,17.8,21.9,22.5
//	    	Net Income USD Mil,"3,578","4,401","5,741","5,580","7,333","8,052","6,134","7,767","6,490","8,041","9,630"
//	    	Earnings Per Share USD,0.50,0.62,0.87,0.89,1.17,1.31,1.05,1.33,1.17,1.49,1.80
//	    	Dividends USD,,,,,,,,,0.12,0.28,0.53
//	    	Payout Ratio %,,,,,,,,,10.3,18.8,29.5
//	    	Shares Mil,"7,223","7,057","6,612","6,272","6,265","6,163","5,857","5,848","5,563","5,404","5,361"
//	    	Book Value Per Share USD,,3.82,3.63,3.94,5.17,5.83,6.68,7.83,8.69,9.68,10.63
//	    	Operating Cash Flow USD Mil,"5,240","7,121","7,568","7,899","10,104","12,089","9,897","10,173","10,079","11,491","11,996"
//	    	Cap Spending USD Mil,-717,-613,-692,-772,"-1,251","-1,268","-1,005","-1,008","-1,174","-1,126","-1,139"
//	    	Free Cash Flow USD Mil,"4,523","6,508","6,876","7,127","8,853","10,821","8,892","9,165","8,905","10,365","10,857"
//	    	Free Cash Flow Per Share USD,,0.92,1.04,1.14,1.41,1.76,1.52,1.57,1.60,1.92,
//	    	Working Capital USD Mil,"5,121","5,640","3,520","14,363","18,216","21,841","30,522","32,188","39,725","44,202",
		}
	    reader.close();
		
		return l;
	}

	private void setFinValues(Map<Integer, FinPeriodData> listInt, String[] line, String match, String field) {
    	if (line[0].matches(match)){
	    	for (int yr = 1; yr  < line.length ; yr ++){
	    		FinPeriodData fd = listInt.get(yr);
	    		try {
					Field f = FinPeriodData.class.getDeclaredField(field);
					f.setAccessible(true);
					f.set(fd, line[yr]);
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
    	}
	}
	
	
	public void updateSectorAndIndustry(String sector, String industry){
		if ("ALL".equals(industry)){
			List<String> ilist = sir.getIndustriesForSector(sector);
			for (String i : ilist){
				if ("ALL".equals(i)){
					continue;
				}
				final int id = sir.getIdForSectorIndustry(sector, i);
				pool.submit(new Runnable(){
					@Override
					public void run() {
						try {
							List<StockData> list = getStocksForIndustryAndSector(id);
							getDataForStocks(list);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		} else {
			final int id = sir.getIdForSectorIndustry(sector, industry);
			pool.submit(new Runnable(){
				@Override
				public void run() {
					try {
						List<StockData> list = getStocksForIndustryAndSector(id);
						getDataForStocks(list);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}


	private List<StockData> getStocksForIndustryAndSector(int industryId) throws Exception {
		System.err.println("getting Stocks for " + industryId);
		 Map<String, String> nameMap = getNameMap(industryId);
		
		List<StockData> l = new ArrayList<StockData>();
		
		File stockIndustryFile= StockDBUtil.getStockIndustryFile(industryId);
		if (!stockIndustryFile.exists()){
			YahooFinanceUtil.createStockIndustryFile(industryId);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stockIndustryFile)));
		CSVReader reader = new CSVReader(br, ',');
	    String [] nextLine;

		String lastName = null;
		int nameCount = 0;
		
		int count = 0;
	    while ((nextLine = reader.readNext()) != null) {
			if (count < 3 ){
				count++;
				continue;
			}
			
			String name = nextLine[0];
			if (name.trim().length() ==0){
				continue;
			}
			
			String marketCap =  nextLine[2];
			String pe =  nextLine[3];
			String yield =  nextLine[5];
			
			String roe =  nextLine[4];
			String oneDayPriceChange =  nextLine[1];
			String debtToEquity =  nextLine[6];
			String priceToBook =  nextLine[7];
			String netProfitMargin =  nextLine[8];
			String priceToFreeCashFlow =  nextLine[9];

			
			if (lastName == null){
				lastName = name;
			} else if (lastName.equals(name)){
				nameCount++;
			} else {
				nameCount = 0;
				lastName = name;
			}
			
			String nameLookup = name + nameCount;
			String symbol = nameMap.get(nameLookup);
			
			if (symbol == null){
				System.err.println("could not look up " + nameLookup);
				continue;
			}
			
			StockData sd = stockMap.get(symbol);
			if (sd == null){
				sd = new StockData();
				stockMap.put(symbol, sd);
			}
			
			sd.setSymbol(symbol);
			
			sd.setPe(pe);
			try {
				double yieldDouble = Double.parseDouble(yield);	
				sd.setYield(yieldDouble);
			} catch (Exception e){
			}
			sd.setMarketCap(marketCap);
			sd.setName(name);
			
			l.add(sd);
		}
	    
		industryStockMap.put(industryId, l);
		notifyAllIndustryStockListeners(industryId, l);
	    
	    reader.close();
		return l;
	}

	private Map<String, String> getNameMap(int industryId) throws MalformedURLException, IOException {
		Map<String, String> nameMap = new HashMap<String, String>();
		File nameMapFile= StockDBUtil.getNameMapFile(industryId);
		if (!nameMapFile.exists()){
			YahooFinanceUtil.createNameMapFile(industryId);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nameMapFile)));
		String sDiv = null;
		while ((sDiv = br.readLine()) != null){
			StringTokenizer st = new StringTokenizer(sDiv, ";");
			String name = st.nextToken();
			String stock = st.nextToken();
			nameMap.put(name, stock);
		}
		br.close();
		return nameMap;
	}

}
