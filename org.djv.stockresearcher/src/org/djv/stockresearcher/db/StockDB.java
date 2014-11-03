package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.djv.stockresearcher.db.dao.AdjustedDivDAO;
import org.djv.stockresearcher.db.dao.AnalystEstimateDAO;
import org.djv.stockresearcher.db.dao.DividendDAO;
import org.djv.stockresearcher.db.dao.FinDataDAO;
import org.djv.stockresearcher.db.dao.PortfolioDAO;
import org.djv.stockresearcher.db.dao.SectorDateDAO;
import org.djv.stockresearcher.db.dao.SectorIndustryDAO;
import org.djv.stockresearcher.db.dao.StockDAO;
import org.djv.stockresearcher.db.dao.StockIndustryDAO;
import org.djv.stockresearcher.db.dao.TransactionDAO;
import org.djv.stockresearcher.db.dao.WatchListDAO;
import org.djv.stockresearcher.model.AdjustedDiv;
import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.FinPeriodData;
import org.djv.stockresearcher.model.Lot;
import org.djv.stockresearcher.model.Portfolio;
import org.djv.stockresearcher.model.PortfolioData;
import org.djv.stockresearcher.model.Position;
import org.djv.stockresearcher.model.SectorIndustry;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.model.StockIndustry;
import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.model.TransactionData;
import org.djv.stockresearcher.model.TransactionType;
import org.eclipse.swt.widgets.Display;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StockDB {
	
	private static final int STOCK_POOL_NBR_THREADS = 8;

	private static StockDB instance;
	
	ExecutorService pool1 = null;
	
	List<ExecutorService> pools = new ArrayList<ExecutorService>();
	
	public static StockDB getInstance(){
		if (instance == null){
			try {
				instance = new StockDB("stockDB");
			} catch (Exception e) {
				throw new IllegalStateException("Error opening database", e);
			}
		}
		return instance;
	}
	
	Connection con = null;
	
	public StockDB(String db) throws Exception {
		createDatabase(db);
	}
	
	public Connection getCon() {
		return con;
	}

	private void createDatabase(String db) throws Exception {
        openConnection(db);
        new SectorIndustryDAO(con).createTableIfNotExists();
        new StockIndustryDAO(con).createTableIfNotExists();
        new StockDAO(con).createTableIfNotExists();
        new DividendDAO(con).createTableIfNotExists();
        new FinDataDAO(con).createTableIfNotExists();
        new WatchListDAO(con).createTableIfNotExists();
        new PortfolioDAO(con).createTableIfNotExists();
        new TransactionDAO(con).createTableIfNotExists();
        new SectorDateDAO(con).createTableIfNotExists();
        new AnalystEstimateDAO(con).createTableIfNotExists();
        new AdjustedDivDAO(con).createTableIfNotExists();
	}

	public void openConnection(String db) throws ClassNotFoundException,
			SQLException {
		Class.forName("org.h2.Driver");
        String dbURL = "jdbc:h2:~/stockDB/" +db+";AUTO_SERVER=TRUE";
        System.err.println("opening DB " + dbURL);
		con = DriverManager.getConnection(dbURL, "stockDB", "" );
	}
	
	volatile int sectorIndustriesToUpdate = 0;
	volatile int sectorIndustriesUpdated = 0;
	
	volatile int industriesToUpdate = 0;
	volatile int industriesUpdated = 0;
	
	List<SectorIndustryListener> sectorIndustryListeners = new ArrayList<SectorIndustryListener>();
	List<IndustryStockListener> industryStockListeners = new ArrayList<IndustryStockListener>();
	List<StockDataChangeListener> stockDataChangeListeners = new ArrayList<StockDataChangeListener>();
	List<WatchListListener> watchListListeners = new ArrayList<WatchListListener>();
	
	public void notifyAllSectorIndustryListeners(final String industryName, final int beginOrEnd) {
		for (final SectorIndustryListener isl : sectorIndustryListeners){
			isl.notifyChanged(industryName, sectorIndustriesToUpdate, sectorIndustriesUpdated, beginOrEnd);
		}
	}
	
	public void addSectorIndustryListener(SectorIndustryListener isl) {
		sectorIndustryListeners.add(isl);
	}
	
	public void notifyAllIndustryStockListeners(final String industryName, final List<StockData> sdl, final int beginOrEnd) {
		for (final IndustryStockListener isl : industryStockListeners){
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					isl.notifyChanged(industryName, sdl, industriesToUpdate, industriesUpdated, beginOrEnd);
				}
			});
		}
	}
	
	public void addIndustryStockListener(IndustryStockListener isl) {
		industryStockListeners.add(isl);
	}
	
	public void notifyAllStockDataChangeListeners(final StockData sd, final int toUpdate, final int updated) {
		for (final StockDataChangeListener sdcl : stockDataChangeListeners){
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					sdcl.notifyChanged(sd, toUpdate, updated);
				}
			});
		}
	}
	
	public void addStockDataChangeListener(StockDataChangeListener sdcl) {
		stockDataChangeListeners.add(sdcl);
	}
	
	public void notifyAllWatchListListeners(final List<StockData> sd, final boolean added) {
		for (final WatchListListener wll : watchListListeners){
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					wll.notifyChanged(sd, added);
				}
			});
		}
	}
	
	public void addWatchListListener(WatchListListener wll) {
		watchListListeners.add(wll);
	}
	
	public static final int MAX_STOCKS = 50;
	
	volatile int stocksToUpdateCoarse = 0;
	volatile int stocksUpdatedCoarse = 0;
		
	public void getDataForStocks(final List<StockData> sdList) throws Exception {
		stocksToUpdateCoarse = 0;
		stocksUpdatedCoarse = 0;
		List<StockData> sdUpdateList = new ArrayList<StockData>();
		List<StockData> sdUpdateListPo = new ArrayList<StockData>();
		for (StockData sd : sdList) {
			if (dataExpired(sd.getStock().getDataDate())){
				sdUpdateList.add(sd);
			} else {
				sdUpdateListPo.add(sd);
			}
		}
				
		ExecutorService pool = Executors.newFixedThreadPool(STOCK_POOL_NBR_THREADS);
		pools.add(pool);
		
		stocksUpdatedCoarse = 0;
		
		int nbrStocks = sdUpdateList.size();
		int nbrGroups = (nbrStocks - 1) /MAX_STOCKS + 1;
		int nbrStocksPo = sdUpdateListPo.size();
		int nbrGroupsPo = (nbrStocksPo - 1) /MAX_STOCKS + 1;
		
		stocksToUpdateCoarse = nbrStocks + nbrStocksPo;
		
		for (int i = 1; i <= nbrGroups; i ++){
			List<StockData> subList = null;
			if (i == nbrGroups){
				subList = sdUpdateList.subList(MAX_STOCKS * (i-1), sdUpdateList.size());
			} else {
				subList = sdUpdateList.subList(MAX_STOCKS * (i-1), MAX_STOCKS * i);
			}
			final List<StockData> finalSubList = subList;
			pool.submit(new Runnable(){
				@Override
				public void run() {
					try {
						getDataForStocksInternal(finalSubList);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			});
		}
		for (int i = 1; i <= nbrGroupsPo; i ++){
			List<StockData> subList = null;
			if (i == nbrGroupsPo){
				subList = sdUpdateListPo.subList(MAX_STOCKS * (i-1), sdUpdateListPo.size());
			} else {
				subList = sdUpdateListPo.subList(MAX_STOCKS * (i-1), MAX_STOCKS * i);
			}
			final List<StockData> finalSubList = subList;
			pool.submit(new Runnable(){
				@Override
				public void run() {
					try {
						getDataForStocksInternalPo(finalSubList);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			});
		}

		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.MINUTES);
		pools.remove(pool);
		
		notifyAllStockDataChangeListeners(null, stocksToUpdateCoarse, stocksUpdatedCoarse);
		
		return;
	}
	
	public void getDataForStocksInternalPo(List<StockData> stocks) throws Exception {
		List<String> stocksGettingData = new ArrayList<String>();
		String stockURLParm = "";
		for (StockData sd : stocks) {
			if (stockURLParm.length() > 0){
				stockURLParm += ",";
			}
			stockURLParm += ("\"" + sd.getSymbol() + "\"");
			stocksGettingData.add(sd.getSymbol());
		}
		if (stocksGettingData.size() > 0) {
			String YQLquery = 
					"select "
							+ "symbol, "
							+ "LastTradePriceOnly "
							+ "from yahoo.finance.quotes "
							+ "where symbol in (" + stockURLParm +")";
			System.err.println(YQLquery);

			BufferedReader br = YahooFinanceUtil.getYQLJson(YQLquery);
			try {
				if (br == null){
					throw new IllegalStateException ("br is null");
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(br).getAsJsonObject();
					JsonObject query = json.get("query").getAsJsonObject();
					JsonElement jsonElement = query.get("results");
					if (jsonElement != null){
						JsonObject results = jsonElement.getAsJsonObject();
						JsonElement quoteEle = results.get("quote");
						if (quoteEle.isJsonArray()){
							JsonArray quote = results.get("quote").getAsJsonArray();
							for (JsonElement ce: quote){
								handleQuoteElementPo(stocks, ce);
							}
						} else {
							handleQuoteElementPo(stocks, quoteEle);
						}
					}
					
				}
			} catch (Exception e){
				if (stocks.size() > 1){
					System.err.println("failed to get stock price data for list " + stockURLParm + " getting data one at a time.");
					for (StockData sd : stocks){
						List<StockData> oneStock = new ArrayList<StockData>();
						oneStock.add(sd);
						getDataForStocksInternalPo(oneStock);
					}
				} else {
					System.err.println("failed to get stock price data for " + stocks.get(0).getSymbol() + ".");
					stocksUpdatedCoarse++;
					notifyAllStockDataChangeListeners(stocks.get(0), stocksToUpdateCoarse, stocksUpdatedCoarse);
				}
			} finally {
				br.close();
			}
		}
	}

	
	public void handleQuoteElementPo(List<StockData> stocks, JsonElement ce)
			throws Exception {
		JsonObject c = ce.getAsJsonObject();
		String symbol = getString(c, "symbol");
		String price = getString(c, "LastTradePriceOnly");

		StockData sd = null;
		for (StockData sdLoop : stocks){
			if (sdLoop.getSymbol().equals(symbol)){
				sd = sdLoop;
				break;
			}
		}
		
		if (sd.getStock() == null){
			System.err.println("Stock not found " + symbol);
			return;
		}
		Stock s = sd.getStock();
		s.setPrice(convertBd(price));
		s.setDataDate(new java.sql.Date(new Date().getTime()));

		new StockDAO(con).update(s);
		stocksUpdatedCoarse++;
		notifyAllStockDataChangeListeners(sd, stocksToUpdateCoarse, stocksUpdatedCoarse);
	}

	public void getDataForStocksInternal(List<StockData> stocks) throws Exception {
		List<String> stocksGettingData = new ArrayList<String>();
		String stockURLParm = "";
		for (StockData sd : stocks) {
			if (stockURLParm.length() > 0){
				stockURLParm += ",";
			}
			stockURLParm += ("\"" + sd.getSymbol() + "\"");
			stocksGettingData.add(sd.getSymbol());
		}
		if (stocksGettingData.size() > 0) {
			String YQLquery = 
					"select "
							+ "symbol, "
							+ "LastTradePriceOnly, "
							+ "MarketCapitalization, "
							+ "DividendShare, "
							+ "DividendYield, "
							+ "PERatio, "
							+ "PEGRatio, "
							+ "StockExchange, "
							+ "YearLow, "
							+ "YearHigh, "
							+ "OneyrTargetPrice "
							+ "from yahoo.finance.quotes "
							+ "where symbol in (" + stockURLParm +")";
			System.err.println(YQLquery);
			BufferedReader br = YahooFinanceUtil.getYQLJson(YQLquery);
			try {
				if (br == null){
					throw new IllegalStateException("br is null");
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(br).getAsJsonObject();
					JsonObject query = json.get("query").getAsJsonObject();
					JsonElement jsonElement = query.get("results");
						
						JsonObject results = jsonElement.getAsJsonObject();
						JsonElement quoteEle = results.get("quote");
						if (quoteEle.isJsonArray()){
							JsonArray quote = results.get("quote").getAsJsonArray();
							for (JsonElement ce: quote){
								handleQuoteElement(stocks, ce);
							}
						} else {
							handleQuoteElement(stocks, quoteEle);
						}
					
				}
			} catch (Exception e){
				if (stocks.size() > 1){
					System.err.println("failed to get stock data for list " + stockURLParm + " getting data one at a time.");
					for (StockData sd : stocks){
						List<StockData> oneStock = new ArrayList<StockData>();
						oneStock.add(sd);
						getDataForStocksInternal(oneStock);
					}
				} else {
					System.err.println("failed to get stock data for " + stocks.get(0).getSymbol() + ".");
					stocksUpdatedCoarse++;
					notifyAllStockDataChangeListeners(stocks.get(0), stocksToUpdateCoarse, stocksUpdatedCoarse);
				}
			} finally {
				br.close();
			}
		}
	}

	public void handleQuoteElement(List<StockData> stocks, JsonElement ce)
			throws Exception {
		JsonObject c = ce.getAsJsonObject();
		String symbol = getString(c, "symbol");
		String price = getString(c, "LastTradePriceOnly");
		String marketCap = getString(c, "MarketCapitalization");
		String dividend = getString(c, "DividendShare");
		String yield = getString(c, "DividendYield");
		String yrHigh = getString(c, "YearHigh");
		String yrLow = getString(c, "YearLow");
		String pe = getString(c, "PERatio");
		String peg = getString(c, "PEGRatio");
		String exchange = getString(c, "StockExchange");
		String oneYrTargetPrice = getString(c, "OneyrTargetPrice");

		StockData sd = null;
		for (StockData sdLoop : stocks){
			if (sdLoop.getSymbol().equals(symbol)){
				sd = sdLoop;
				break;
			}
		}
		
		if (sd.getStock() == null){
			System.err.println("Stock not found " + symbol);
			return;
		}

		Stock s = sd.getStock();
		s.setDividend(convertBd(dividend));
		s.setPe(convertBd(pe));
		s.setPeg(convertBd(peg));
		s.setPrice(convertBd(price));
		s.setYield(convertBd(yield));
		s.setMarketCap(marketCap);
		s.setExchange(exchange);
		s.setYearHigh(convertBd(yrHigh));
		s.setYearLow(convertBd(yrLow));
		s.setOneYrTargetPrice(convertBd(oneYrTargetPrice));
		s.setDataDate(new java.sql.Date(new Date().getTime()));

		new StockDAO(con).update(s);
		stocksUpdatedCoarse++;
		notifyAllStockDataChangeListeners(sd, stocksToUpdateCoarse, stocksUpdatedCoarse);
	}

	private String getString(JsonObject c, String string) {
		JsonElement o = c.get(string);
		if (o == null){
			return null;
		}
		if (o.isJsonNull()){
			return null;
		}
		return o.getAsString();
	}

	public void getDivData(StockData sd, boolean forceUpdate) throws Exception {
		if (forceUpdate){
			new DividendDAO(con).deleteForStock(sd.getSymbol());
		}
		if (dataExpired(sd.getStock().getDivDataDate()) || forceUpdate){
			insertNewDividends(sd);
			sd.getStock().setDivDataDate(new java.sql.Date(new Date().getTime()));
			new StockDAO(con).update(sd.getStock());
			sd.setDivData(null)	;
		}
		if (sd.getDivData() == null || forceUpdate){	
			List<DivData> ddl = new DividendDAO(con).getDividendsForSymbol(sd.getStock().getSymbol());
			sd.setDivData(ddl);
		}
		StockDataUtil.crunchDividends(sd);
	}

	public void insertNewDividends(StockData sd) throws Exception {
		String symbol = sd.getStock().getSymbol();
		Date d = new DividendDAO(con).getLastDividendOnFileForSymbol(symbol);
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DATE);
		String urlString = "http://ichart.finance.yahoo.com/table.csv?s=" +symbol + "&a=00&b=2&c=1962&d=" + month +"&e=" +day+ "&f=" + year + "&g=v&ignore=.csv";
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice(urlString);
		if (br == null){
			sd.getStock().setDivDataDate(new java.sql.Date(new Date().getTime()));
			new StockDAO(con).update(sd.getStock());
			return;
		}
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
			
			if (d != null && (date.before(d) || date.equals(d))){
				break;
			}
			DivData dd = new DivData();
			dd.setSymbol(symbol);
			dd.setPaydate(new java.sql.Date(date.getTime()));
			dd.setDividend(div);
			new DividendDAO(con).insert(dd);
		}
		br.close();
		
	}
	
	public void getFinData(StockData sd) throws Exception {
		if (dataExpired(sd.getStock().getFinDataDate())){
			new FinDataDAO(con).deleteForStock(sd.getStock().getSymbol());
			insertFinancials(sd);
			sd.getStock().setFinDataDate(new java.sql.Date(new Date().getTime()));
			new StockDAO(con).update(sd.getStock());
		}
		
		List<FinPeriodData> fpdl = new FinDataDAO(con).getFinDataForStock(sd.getStock().getSymbol());
		Map<String, FinPeriodData> finData = new TreeMap<String, FinPeriodData>();
		for (FinPeriodData fpd: fpdl){
			finData.put(fpd.getPeriod(), fpd);
		}
		sd.setFinData(finData);
		StockDataUtil.crunchFinancials(sd);
	}
	
	public void insertFinancials(StockData sd) throws Exception {
		String symbol = sd.getStock().getSymbol();
		String exchange = sd.getStock().getExchange();
		Map<String, FinPeriodData> l = new TreeMap<String, FinPeriodData>();
		Map<Integer, FinPeriodData> listInt = new TreeMap<Integer, FinPeriodData>();
		String convertedSymbol = symbol;
		int dotIx = symbol.indexOf('.');
		if (dotIx > -1){
			convertedSymbol = symbol.substring(0, dotIx);
		}
		
		String msExchange = StockDBUtil.mapYahooExchangeToMorningStar(exchange);
		if (msExchange == null){
			if (symbol.endsWith(".DE")){
				msExchange = "XFRA";
			} else {
				return;
			}
		}
		if ("XHKG".equals(msExchange)){
			convertedSymbol = "0" + convertedSymbol;
		}
		
		String urlString = "http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t="+msExchange+ ":"+ convertedSymbol +"&region=usa&culture=en-US&cur=USD";
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice(urlString);
		if (br == null && "XOTC".equals(msExchange)){
			msExchange = "PINX";
			urlString = "http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t="+msExchange+ ":"+ convertedSymbol +"&region=usa&culture=en-US&cur=USD";
			br = YahooFinanceUtil.getYahooCSVNice(urlString);
		}
		
		if (br == null){
			System.err.println("cound not get financials for " + convertedSymbol);
			return;
		}
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
		    		if ("TTM".equals(period)){
		    			fd.setYear(9999);
		    		} else {
			    		try{
			    			Integer year = Integer.valueOf(period.substring(0, 4));
			    			fd.setYear(year);
			    		} catch (Exception e){
			    			fd.setYear(null);
			    		}
		    		}
		    		fd.setSymbol(symbol);
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
		}
	    reader.close();
	    for (String key : l.keySet()){
	    	FinPeriodData fpd = l.get(key);
	    	if (fpd.getYear() != null){
	    		new FinDataDAO(con).insert(fpd);
	    	}
	    }
	}

	private void setFinValues(Map<Integer, FinPeriodData> listInt, String[] line, String match, String field) {
    	if (line[0].matches(match)){
	    	for (int yr = 1; yr  < line.length ; yr ++){
	    		FinPeriodData fd = listInt.get(yr);
	    		try {
					Field f = FinPeriodData.class.getDeclaredField(field);
					f.setAccessible(true);
					f.set(fd, convertBd(line[yr]));
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
    	}
	}

	public BigDecimal convertBd(String s) {
		if (s == null){
			return null;
		}
		s = s.replace(",", "");
		try{
			return new BigDecimal(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void updateSectors() throws Exception {
		sectorIndustriesToUpdate = 0;
		sectorIndustriesUpdated = 0;
		pooledExecution(new Runnable(){
			@Override
			public void run() {
				try {
					notifyAllSectorIndustryListeners("Getting industry and sector data", 0);
					
					SectorDateDAO sdDAO = new SectorDateDAO(con);
					final SectorIndustryDAO iDAO = new SectorIndustryDAO(con);
					Date d = sdDAO.select();
					if (dataExpired(d)){
						updateSectors(sdDAO, iDAO, d);
						List<String> sectors = iDAO.getAllSectors();
						final Map<String, List<Integer>> sectorMap = new HashMap<String, List<Integer>>();
						
						for (String sector : sectors){
							List<Integer> iList = iDAO.getIndustriesForSector(sector);
							sectorMap.put(sector, iList);
							sectorIndustriesToUpdate += iList.size();
						}
						
						ExecutorService pool = Executors.newFixedThreadPool(STOCK_POOL_NBR_THREADS);
						pools.add(pool);
						for (final String sector : sectorMap.keySet()){
							pool.submit(new Runnable(){
								@Override
								public void run() {
									try {
										List<Integer> iList = sectorMap.get(sector);
										for (Integer ind : iList){
											boolean worked = updateStockIndustryYQL(ind);
											if (!worked){
												updateStockIndustryHTML(ind);
											}
											SectorIndustry si = iDAO.select(ind);
											sectorIndustriesUpdated++;
											notifyAllSectorIndustryListeners("Updated stock list for " + si.getSectorName() + " - " + si.getIndustryName() , 0);
										}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						}
						pool.shutdown();
						pool.awaitTermination(30, TimeUnit.MINUTES);
						pools.remove(pool);

						if (d == null){
							sdDAO.insert(new java.sql.Date(System.currentTimeMillis()));
						} else {
							sdDAO.update(new java.sql.Date(System.currentTimeMillis()));
						}
					}
					notifyAllSectorIndustryListeners("Done" , 1);
				} catch (Exception e){
					e.printStackTrace();
					notifyAllSectorIndustryListeners("Done" , 1);
				}
			}
		});
	}

	private boolean updateStockIndustryYQL(Integer ind) {
		boolean ret = false;
		try {
			StockIndustryDAO stockSectorIndustryDAO = new StockIndustryDAO(con);
			String YQLquery = "select * from yahoo.finance.industry where id=\""
					+ ind + "\"";
			BufferedReader br = YahooFinanceUtil.getYQLJson(YQLquery);
			if (br != null) {
				JsonParser parser = new JsonParser();
				JsonObject json = parser.parse(br).getAsJsonObject();
				JsonObject query = json.get("query").getAsJsonObject();
				JsonObject results = query.get("results").getAsJsonObject();
				JsonObject industry = results.get("industry").getAsJsonObject();
				JsonElement iEle = industry.get("company");
				if (iEle == null) {
					return false;
				}
				if (iEle.isJsonArray()) {
					JsonArray companies = iEle.getAsJsonArray();
					for (JsonElement ce : companies) {
						handleCompany(ind, stockSectorIndustryDAO, ce);
					}
				} else {
					handleCompany(ind, stockSectorIndustryDAO, iEle);
				}
				br.close();
				ret = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void handleCompany(Integer ind, StockIndustryDAO stockSectorIndustryDAO,
			JsonElement ce) throws Exception {
		JsonObject c = ce.getAsJsonObject();
		String name = c.get("name").getAsString();
		String symbol = c.get("symbol").getAsString();
		 
		StockIndustry si = stockSectorIndustryDAO.select(symbol);
		 if (si == null){
			 si = new StockIndustry();
			 si.setIndId(ind);
			 si.setName(name);
			 si.setSymbol(symbol);
			 stockSectorIndustryDAO.insert(si);
		 } else {
			 si.setIndId(ind);
			 si.setName(name);
			 stockSectorIndustryDAO.update(si);
		 }
	}
	
	private boolean updateStockIndustryHTML(int industryId) throws Exception {
		StockIndustryDAO stockSectorIndustryDAO = new StockIndustryDAO(con);
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice("http://biz.yahoo.com/p/" + industryId + "conameu.html");
		if (br == null){
			return false;
		}
		StringBuffer sb = new StringBuffer();
		String s = br.readLine();
		while (s != null){
			sb.append(s);
			sb.append(" ");
			s = br.readLine();
		}
		int startIx = sb.indexOf("<b>Companies</b>");
		String lookFor = "<a href=\"http://us.rd.yahoo.com/finance/industry/quote/colist/*http://biz.yahoo.com/p/";
		do {
			int linkIx = sb.indexOf(lookFor, startIx);
			if (linkIx > -1){
				int linkEnd = sb.indexOf("</a>", linkIx + lookFor.length());
				String chopped = sb.substring(linkIx + lookFor.length() + 2, linkEnd);
				String lookFor2 = ".html\">";
				int hIx = chopped.indexOf(lookFor2);
				String symbol = chopped.substring(0, hIx).toUpperCase();
				String name = chopped.substring(hIx + lookFor2.length());

				StockIndustry si = stockSectorIndustryDAO.select(symbol);
				 if (si == null){
					 si = new StockIndustry();
					 si.setIndId(industryId);
					 si.setName(name);
					 si.setSymbol(symbol);
					 stockSectorIndustryDAO.insert(si);
				 } else {
					 si.setIndId(industryId);
					 si.setName(name);
					 stockSectorIndustryDAO.update(si);
				 }
				startIx = linkEnd + 4;
			} else {
				break;
			}
		} while (startIx > -1);
		
		br.close();
		return true;
	}

	public void updateSectors(SectorDateDAO sdDAO, SectorIndustryDAO iDAO, Date d)
			throws Exception, IOException {
		String YQLquery = "select * from yahoo.finance.sectors";
		BufferedReader br = YahooFinanceUtil.getYQLJson(YQLquery);
		 JsonParser parser = new JsonParser();
		 JsonObject json = parser.parse(br).getAsJsonObject();
		 JsonObject query = json.get("query").getAsJsonObject();
		 JsonObject results = query.get("results").getAsJsonObject();
		 JsonArray sectors = results.get("sector").getAsJsonArray();
		 for (JsonElement sectorE: sectors){
			 JsonObject sector = sectorE.getAsJsonObject();
			 String sectorname = sector.get("name").getAsString();
			 
			 boolean first = true;
			 JsonElement industryE = sector.get("industry");
			 if (industryE.isJsonObject()){
				handleIndustry(iDAO, sectorname, industryE, first);
				if (first){
					first = false;
				}
			 } else {
				 JsonArray indArr = industryE.getAsJsonArray();
				 for (JsonElement industryEe: indArr){
					 handleIndustry(iDAO, sectorname, industryEe, first);
						if (first){
							first = false;
						}
				 }
			 }
		 }

		 br.close();
	}

	public void handleIndustry(SectorIndustryDAO iDAO, String sectorname, JsonElement industryEe, boolean first) throws Exception {
		JsonObject industryO = industryEe.getAsJsonObject();
		 Integer id = industryO.get("id").getAsInt();
		 String iname = industryO.get("name").getAsString();
		 
		 if (first){
			 Integer sectid = id / 100 * 100;
			 String sectname = "ALL";
			 SectorIndustry si = iDAO.select(sectid);
			 if (si == null){
				 si = new SectorIndustry();
				 si.setIndustryId(sectid);
				 si.setIndustryName(sectname);
				 si.setSectorName(sectorname);
				 iDAO.insert(si);
			 } else {
				 si.setIndustryId(sectid);
				 si.setIndustryName(sectname);
				 si.setSectorName(sectorname);
				 iDAO.update(si);
			 }
			 
		 }
		 SectorIndustry si = iDAO.select(id);
		 if (si == null){
			 si = new SectorIndustry();
			 si.setIndustryId(id);
			 si.setIndustryName(iname);
			 si.setSectorName(sectorname);
			 iDAO.insert(si);
		 } else {
			 si.setIndustryId(id);
			 si.setIndustryName(iname);
			 si.setSectorName(sectorname);
			 iDAO.update(si);
		 }
	}
	
	public void updateSectorAndIndustry(final String sector, final String industry){
		industriesToUpdate = 0;
		industriesUpdated = 0;
		pooledExecution(new Runnable(){
			@Override
			public void run() {
				if ("ALL".equals(industry)){
					try {
						List<Integer> ilist = new SectorIndustryDAO(con).getIndustriesForSector(sector);
						industriesToUpdate = ilist.size() - 1;
						for (Integer id : ilist){
							getStocksForIndustryAndSector(id);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						int id = new SectorIndustryDAO(con).getIdForSectorIndustry(sector, industry);
						industriesToUpdate = 1;
						getStocksForIndustryAndSector(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
		
				}
			}
		});
		
	}
	
	public void pooledExecution(Runnable runnable){
		if (pool1 != null){
			pool1.shutdownNow();
			try {
				pool1.awaitTermination(30, TimeUnit.MINUTES);
				pools.remove(pool1);
				pool1 = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pool1 = Executors.newFixedThreadPool(1);
		pools.add(pool1);
		pool1.submit(runnable);
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				pool1.shutdown();
				try {
					pool1.awaitTermination(30, TimeUnit.MINUTES);
					pools.remove(pool1);
					pool1 = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	public void getStocksForIndustryAndSector(int ind) throws Exception {
		SectorIndustry si = new SectorIndustryDAO(con).select(ind);
		notifyAllIndustryStockListeners(si.getIndustryName(), null, 0);
		
		List<StockData> sdList = new ArrayList<StockData>();
		List<StockIndustry> sList = new StockIndustryDAO(con).getAllForIndustry(ind);
		for (StockIndustry s : sList){
			StockData sd = getStockData(s.getSymbol(), s, true);
			sd.setSymbol(s.getSymbol());
			sd.setStockIndustry(s);
			sd.setSectorIndustry(si);
			sd.setWatched((new WatchListDAO(con).exists(s.getSymbol()) ? "*" : ""));
			Stock st = new StockDAO(con).select(s.getSymbol());
			if (st == null){
				st = new Stock();
				st.setSymbol(s.getSymbol());
				new StockDAO(con).insert(st);
			} 
			sd.setStock(st);
			sdList.add(sd);
		}
		
		getDataForStocks(sdList);
		notifyAllIndustryStockListeners(si.getIndustryName(), sdList, 0);
		updateStockFineData(sdList, false);
		industriesUpdated ++;
		notifyAllIndustryStockListeners(si.getIndustryName(), null, 1);
	}

	volatile int stocksToUpdate = 0;
	volatile int stocksUpdated = 0;
	
	public void updateStockFineData(List<StockData> sdList, final boolean forceUpdate)
			throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(STOCK_POOL_NBR_THREADS);
		pools.add(pool);
		stocksToUpdate = 0;
		stocksUpdated = 0;
		
		for (final StockData sd : sdList){
			stocksToUpdate ++;
			pool.submit(new Runnable(){
				@Override
				public void run() {
					try {
						getDivData(sd,forceUpdate);
						getFinData(sd);
						StockDataUtil.calcRankings(sd);
						stocksUpdated++;
						notifyAllStockDataChangeListeners(sd, stocksToUpdate, stocksUpdated);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.MINUTES);
		pools.remove(pool);
		notifyAllStockDataChangeListeners(null, stocksToUpdate, stocksUpdated);
	}
	
	private boolean dataExpired(Date d) {
		if (d == null){
			return true;
		}
		Calendar now = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		if (now.get(Calendar.YEAR) > c.get(Calendar.YEAR)){
			return true;
		}
		
		if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR) && now.get(Calendar.MONTH) > c.get(Calendar.MONTH)){
			return true;
		}
		
		if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR) && now.get(Calendar.MONTH) == c.get(Calendar.MONTH) && now.get(Calendar.DATE) > c.get(Calendar.DATE)){
			return true;
		}
		
		return false;
	}

	public String getCompanyInfo(String symbol) throws Exception {
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice("http://finance.yahoo.com/q/pr?s=" +symbol+ "+Profile");
		if (br == null){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String s = null; ;
		while ((s = br.readLine()) != null){
			sb.append(s);
			sb.append(" ");
		}
		
		String ss = "Business Summary</span></th><th align=\"right\">&nbsp;</th></tr></table><p>";
		int ix = sb.indexOf(ss);
		if (ix > -1){
			int begix = ix + ss.length();
			int endix = sb.indexOf("</p>", begix);
			return sb.substring(begix, endix);
		}
		return "";
	}
	
	public void addToWatchList(final String symbol) throws Exception {
		pooledExecution(new Runnable(){
			@Override
			public void run() {
				try {
					if (new WatchListDAO(con).exists(symbol)){
						throw new Exception("already on watch list");
					} else {
						new WatchListDAO(con).insert(symbol);
					}
					List<StockData> sdList = new ArrayList<StockData>();
					StockData sd = getStockData(symbol, null, false);
					notifyAllStockDataChangeListeners(sd, 1, 0);
					sdList.add(sd);
					
					getDataForStocks(sdList);
					updateStockFineData(sdList, false);
					
					notifyAllWatchListListeners(sdList, true);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public void refreshWatchList() throws Exception {
		pooledExecution(new Runnable(){
			@Override
			public void run() {
				try {
					List<String> list = new WatchListDAO(con).getWatchList();
					List<StockData> sdList = new ArrayList<StockData>();
					for (String symbol : list){
						StockData sd = getStockData(symbol, null, true);
						sdList.add(sd);
					}
					
					getDataForStocks(sdList);
					notifyAllWatchListListeners(sdList, true);
					updateStockFineData(sdList, false);
				} catch (Exception e){
					e.printStackTrace();
				}
			}

			
		});
	}

	public void removeFromWatchList(String symbol) throws Exception {
		new WatchListDAO(con).delete(symbol);
		List<StockData> sdList = new ArrayList<StockData>();
		StockData sd = getStockData(symbol, null, false);
		notifyAllStockDataChangeListeners(sd, 1, 1);
		sdList.add(sd);
		
		notifyAllWatchListListeners(sdList, false);
	}
	
	public void removeAllFromWatchList(List<StockData> sdList) throws Exception {
		List<StockData> sdList2 = new ArrayList<StockData>();
		for (StockData sd : sdList){
			new WatchListDAO(con).delete(sd.getSymbol());
			sd = getStockData(sd.getSymbol(), null, false);
			notifyAllStockDataChangeListeners(sd, 1, 1);
			sdList2.add(sd);
		}
		
		notifyAllWatchListListeners(sdList2, false);
	}

	public List<String> getAllSectors() throws Exception {
		return new SectorIndustryDAO(con).getAllSectors();
	}

	public List<String> getIndustriesForSector(String sector) throws Exception {
		return new SectorIndustryDAO(con).getIndustriesNameForSector(sector);
	}

	public SectorIndustry getIndustry(int industry) throws Exception {
		return new SectorIndustryDAO(con).select(industry);
	}
	
	public void createNewPortfolio(String text) throws Exception {
		PortfolioDAO dao = new PortfolioDAO(con);
		Portfolio p = new Portfolio();
		p.setId(dao.getNextId());
		p.setName(text);
		dao.insert(p);
	}
	
	public StockData getStockData(String symbol, StockIndustry sti, boolean insert) throws Exception {
		if (sti == null) {
			sti = new StockIndustryDAO(con).select(symbol);
		}

		Stock s = new StockDAO(con).select(symbol);
		if (s == null){
			s = new Stock();
			s.setSymbol(symbol);
			s.setMarketCap("");
			if (insert){
				new StockDAO(con).insert(s);
			}
		}
		
		StockData sd = new StockData(s);
		if (sti != null){
			SectorIndustry seci = new SectorIndustryDAO(con).select(sti.getIndId());
			sd.setSectorIndustry(seci);
		}
		sd.setSymbol(s.getSymbol());
		sd.setStockIndustry(sti);
		
		sd.setWatched((new WatchListDAO(con).exists(s.getSymbol()) ? "*" : ""));
		return sd;
	}
	
	public List<Portfolio> getPortfolioList() throws Exception {
		PortfolioDAO dao = new PortfolioDAO(con);
		return dao.getAll();
	}
	
	public void insertUpdateDeleteDivAdjustment(AdjustedDiv aDiv) throws Exception {
		AdjustedDivDAO dao = new AdjustedDivDAO(con);
		if (aDiv.getAdjustedDate() == null && aDiv.getAdjustedDiv() == null){
			dao.delete(aDiv.getSymbol(), aDiv.getPaydate());
		} else {
			AdjustedDiv adivsel = dao.getAdjustment(aDiv.getSymbol(), aDiv.getPaydate());
			if (adivsel == null){
				dao.insert(aDiv);
			} else {
				dao.update(aDiv);
			}
		}
	}

	public PortfolioData getPortfolioData(String name) throws Exception {
		PortfolioData portfolioData = new PortfolioData();
		PortfolioDAO pdao = new PortfolioDAO(con);
		Portfolio portfolio = pdao.selectByName(name);
		if (portfolio == null){
			throw new IllegalArgumentException ("invalid portfolio");
		}
		portfolioData.setPortfolio(portfolio);
		buildTransactionDataList(portfolioData);
		buildPositionMap(portfolioData);
		return portfolioData;
	}

	private void buildPositionMap(PortfolioData portfolioData) {
		BigDecimal cashBalance = new BigDecimal("0.00");
		Map<Integer, Map<String, Position>> bigMap = new HashMap<Integer, Map<String, Position>>();
		for (TransactionData td : portfolioData.getTransactionList()){
			Transaction t = td.getTransaction();
			TransactionType ttype = TransactionType.getFromCode(t.getType());
			switch (ttype){
				case BUY:
				case PUT_ASSIGN:
					BigDecimal buyShares = t.getShares();
					td.setCost(t.getShares().multiply(t.getPrice()).add(t.getCommission()));
					td.setBasis(td.getCost().add(t.getPremium()));
					td.setBasisPerShare(td.getBasis().divide(t.getShares(), 6, RoundingMode.HALF_UP));
					crunchBuyOrSell(bigMap, td, t, buyShares);
					break;
				case SELL:
				case CALL_ASSIGN:
					BigDecimal sellShares = t.getShares().multiply(BigDecimal.valueOf(-1));
					td.setCost(t.getShares().multiply(t.getPrice()).add(t.getCommission()));
					td.setBasis(td.getCost().add(t.getPremium()));
					td.setBasisPerShare(td.getBasis().divide(t.getShares(), 6, RoundingMode.HALF_UP));
					crunchBuyOrSell(bigMap, td, t, sellShares);
					break;
				case DIVIDEND_REINVEST:
					Position posDR = getOrCreatePosition(bigMap, td);
					td.setCost(BigDecimal.ZERO);
					BigDecimal basis = t.getShares().multiply(t.getPrice()).add(t.getCommission());
					BigDecimal bps = basis.divide(t.getShares(), 6, RoundingMode.HALF_UP);
					td.setBasisPerShare(bps);
					td.setBasis(basis);
					
					Lot lotDR = new Lot();
					lotDR.setDate(td.getTransaction().getTranDate());
					lotDR.setShares(td.getTransaction().getShares());
					lotDR.setBasis(basis);
					lotDR.setBasisPerShare(bps);
					lotDR.setCost(BigDecimal.ZERO);
					posDR.getLotList().add(lotDR);
					
					break;
				case CASH_DEPOSIT:
					td.setCost(t.getPrice().multiply(BigDecimal.valueOf(-1)));
					td.setBasis(BigDecimal.ZERO);
					td.setBasisPerShare(BigDecimal.ZERO);
					break;
				case OPTION_SELL:
					BigDecimal cost = t.getShares().multiply(BigDecimal.valueOf(-100)).multiply(t.getPrice()).add(t.getCommission());
					td.setCost(cost);
					td.setBasis(BigDecimal.ZERO);
					td.setBasisPerShare(BigDecimal.ZERO);
					break;
				case CASH_WITHDRAWAL:
					td.setCost(t.getPrice());
					td.setBasis(BigDecimal.ZERO);
					td.setBasisPerShare(BigDecimal.ZERO);
					break;
				case DIVIDEND:
					td.setCost(t.getPrice().multiply(BigDecimal.valueOf(-1)));
					td.setBasis(BigDecimal.ZERO);
					td.setBasisPerShare(BigDecimal.ZERO);
					break;
				default: throw new IllegalArgumentException ("Invalid tran type "  + ttype.getTypeCode());
			}
			cashBalance = cashBalance.subtract(td.getCost());
			td.setCashBalance(cashBalance);
		}
		sumAndCleanup(bigMap);
		portfolioData.setPositionMap(bigMap);
		portfolioData.setCashBalance(cashBalance);
	}

	private void crunchBuyOrSell(Map<Integer, Map<String, Position>> bigMap, TransactionData td, Transaction t, BigDecimal shares) {
		Position position = getOrCreatePosition(bigMap, td);
		Iterator<Lot> i = position.getLotList().iterator();
		while (shares.compareTo(BigDecimal.ZERO) != 0) {
			if (i.hasNext()){
				Lot l = i.next();
				if (l.getShares().compareTo(shares) > 0){
					shares = shares.add(l.getShares());
					i.remove();
				} else if (l.getShares().compareTo(shares) < 0){
					l.setShares(l.getShares().add(shares));
					shares = BigDecimal.ZERO;
				} else {
					shares = BigDecimal.ZERO;
					i.remove();
				}
			} else {
				Lot newLot = new Lot();
				newLot.setDate(td.getTransaction().getTranDate());
				newLot.setShares(shares);
				newLot.setCost(newLot.getShares().multiply(t.getPrice()).add(t.getCommission()));
				newLot.setBasis(td.getBasisPerShare().multiply(shares));
				newLot.setBasisPerShare(td.getBasisPerShare());
				position.getLotList().add(newLot);
				shares = BigDecimal.ZERO;
			}
		}
	}

	public Position getOrCreatePosition(
			Map<Integer, Map<String, Position>> bigMap, TransactionData td) {
		int indId = td.getStockData().getStockIndustry() == null ? 0 : td.getStockData().getStockIndustry().getIndId();
		int sector = (indId / 100) * 100;
		
		Map<String, Position> posMap = bigMap.get(sector);
		if (posMap == null){
			posMap = new HashMap<String, Position>();
			bigMap.put(sector, posMap);
		}
		
		Position pos = posMap.get(td.getStockData().getStock().getSymbol());
		if (pos == null){
			pos = new Position();
			pos.setSd(td.getStockData());
			posMap.put(td.getStockData().getStock().getSymbol(), pos);
		}
		return pos;
	}

	public void sumAndCleanup(Map<Integer, Map<String, Position>> bigMap) {
		List <Integer> deadSectors = new ArrayList<Integer>();
		for (Integer sKey : bigMap.keySet()){
			List <String> deadPositions = new ArrayList<String>();
			Map<String, Position> posMap = bigMap.get(sKey);
			for (String posKey : posMap.keySet() ){
				Position pos = posMap.get(posKey);
				for (Lot l : pos.getLotList()){
					pos.setShares(pos.getShares().add(l.getShares()));
					pos.setCost(pos.getCost().add(l.getCost()));
					pos.setBasis(pos.getBasis().add(l.getBasis()));
				}
				if (pos.getShares().compareTo(BigDecimal.ZERO) == 0){
					deadPositions.add(posKey);
				} else {
					pos.setBasisPerShare(pos.getBasis().divide(pos.getShares(), 6, RoundingMode.HALF_UP));
					pos.setValue(pos.getSd().getStock().getPrice().multiply(pos.getShares()));
				}
			}
			for (String posKey : deadPositions){
				posMap.remove(posKey);
			}
			if (posMap.isEmpty()){
				deadSectors.add(sKey);
			}
		}
		for (Integer sector : deadSectors){
			bigMap.remove(sector);
		}
	}

	public void buildTransactionDataList(PortfolioData portfolioData) throws Exception {
		Portfolio portfolio = portfolioData.getPortfolio();
		TransactionDAO tdao = new TransactionDAO(con);
		
		List<Transaction> transactionList = tdao.getTransactionsForPortfolio(portfolio.getId());
		List<String> stocksNeedingData = new ArrayList<String>();
		
		for (Transaction transaction : transactionList){
			if (!stocksNeedingData.contains(transaction.getSymbol()) && !"".equals(transaction.getSymbol())){
				stocksNeedingData.add(transaction.getSymbol());
			}
		}
		
		List<StockData> sdList = new ArrayList<StockData>();
		for (String symbol: stocksNeedingData){
			StockData sd = getStockData(symbol, null, false);
			sdList.add(sd);
		}
		
		getDataForStocks(sdList);
		for (StockData sd : sdList){
			getDivData(sd,false);
			getFinData(sd);
			StockDataUtil.calcRankings(sd);
		}
		
		List<TransactionData> transactionDataList = new ArrayList<TransactionData>();
		for (Transaction transaction : transactionList){
			TransactionData td = new TransactionData();
			td.setTransaction(transaction);
			for (StockData sd : sdList){
				if (sd.getStock().getSymbol().equals(td.getTransaction().getSymbol())){
					td.setStockData(sd);
					break;
				}
			}
			transactionDataList.add(td);
		}
		portfolioData.setTransactionList(transactionDataList);
	}

	public void deletePortfolio(String name) throws Exception {
		PortfolioDAO dao = new PortfolioDAO(con);
		TransactionDAO tdao = new TransactionDAO(con);
		Portfolio p = dao.selectByName(name);
		if (p == null){
			return;
		}
		tdao.deleteAllForPortfolio(p.getId());
		dao.delete(p.getId());
	}

	public void createNewTransaction(String portfolioName, Transaction t) throws Exception {
		PortfolioDAO dao = new PortfolioDAO(con);
		TransactionDAO tdao = new TransactionDAO(con);
		
		Portfolio p = dao.selectByName(portfolioName);
		if (p == null){
			return;
		}
		
		t.setPortId(p.getId());
		t.setId(tdao.getNextId());
		tdao.insert(t);
	}

	public void deleteTransaction(Integer id) throws Exception {
		TransactionDAO tdao = new TransactionDAO(con);
		tdao.delete(id);
	}
	
	public void updateTransaction(Transaction t) throws Exception {
		TransactionDAO tdao = new TransactionDAO(con);
		tdao.update(t);
	}

	public BigDecimal getBd(String str) {
		try {
			return new BigDecimal(str.replace(",", ""));
		} catch (Exception e){
			return null;
		}
	}
	
	public void close() throws Exception {
		con.commit();
		con.close();
	}

	public void waitFor() {
		for (ExecutorService pool : pools){
			try {
				pool.awaitTermination(30, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addAllToWatchList(final List<StockData> sdList) {
		pooledExecution(new Runnable(){
			@Override
			public void run() {
				try {
					List<StockData> newsdList = new ArrayList<StockData>();
					for (StockData sd: sdList){
						if (!new WatchListDAO(con).exists(sd.getSymbol())){
							new WatchListDAO(con).insert(sd.getSymbol());
						}
						StockData newsd = getStockData(sd.getSymbol(), null, false);
						notifyAllStockDataChangeListeners(sd, 1, 0);
						newsdList.add(newsd);
					}
					
					getDataForStocks(newsdList);
					updateStockFineData(newsdList, false);
					
					notifyAllWatchListListeners(newsdList, true);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}
}
