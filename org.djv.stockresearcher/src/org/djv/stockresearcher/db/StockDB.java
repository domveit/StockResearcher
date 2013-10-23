package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
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
import java.util.concurrent.TimeUnit;

import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.FinPeriodData;
import org.djv.stockresearcher.model.Option;
import org.djv.stockresearcher.model.Portfolio;
import org.djv.stockresearcher.model.PortfolioData;
import org.djv.stockresearcher.model.Position;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.model.TransactionData;
import org.eclipse.swt.widgets.Display;

import au.com.bytecode.opencsv.CSVReader;

public class StockDB {
	
	private static StockDB instance;
	
	public static StockDB getInstance(){
		if (instance == null){
			instance = new StockDB();
		}
		return instance;
	}
	
	Connection con = null;
	
	public StockDB(){
		createDatabase();
	}

	private void createDatabase() {
        try
        {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDB", "stockDB", "" );
            new IndustryDAO(con).createTableIfNotExists();
            new NameMapDAO(con).createTableIfNotExists();
            new StockDAO(con).createTableIfNotExists();
            new DividendDAO(con).createTableIfNotExists();
            new FinDataDAO(con).createTableIfNotExists();
            new WatchListDAO(con).createTableIfNotExists();
            new PortfolioDAO(con).createTableIfNotExists();
            new TransactionDAO(con).createTableIfNotExists();
        }
        catch( Exception e ){
            e.printStackTrace();
        }
	}
	
	volatile int industriesToUpdate = 0;
	volatile int industriesUpdated = 0;
	
	volatile int stocksToUpdate = 0;
	volatile int stocksUpdated = 0;
	
	List<IndustryStockListener> industryStockListeners = new ArrayList<IndustryStockListener>();
	List<StockDataChangeListener> stockDataChangeListeners = new ArrayList<StockDataChangeListener>();
	List<WatchListListener> watchListListeners = new ArrayList<WatchListListener>();
	
	private SectorIndustryRegistry sir = SectorIndustryRegistry.getInstance();
	
	public void notifyAllIndustryStockListeners(final int industry, final List<StockData> sdl, final int beginOrEnd) {
		for (final IndustryStockListener isl : industryStockListeners){
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					isl.notifyChanged(industry, sdl, industriesToUpdate, industriesUpdated, beginOrEnd);
				}
			});
		}
	}
	
	public void addIndustryStockListener(IndustryStockListener isl) {
		industryStockListeners.add(isl);
	}
	
	public void notifyAllStockDataChangeListeners(final StockData sd) {
		for (final StockDataChangeListener sdcl : stockDataChangeListeners){
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					sdcl.notifyChanged(sd, stocksToUpdate, stocksUpdated);
				}
			});
		}
	}
	
	public void addStockDataChangeListener(StockDataChangeListener sdcl) {
		stockDataChangeListeners.add(sdcl);
	}
	
	public void notifyAllWatchListListeners(final List<StockData> sd) {
		for (final WatchListListener wll : watchListListeners){
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					wll.notifyChanged(sd);
				}
			});
		}
	}
	
	public void addWatchListListener(WatchListListener wll) {
		watchListListeners.add(wll);
	}
	
	public void getDataForStocks(final List<StockData> sdList) throws Exception {
		List<StockData> sdUpdateList = new ArrayList<StockData>();
		for (StockData sd : sdList) {
			if (dataExpired(sd.getStock().getDataDate())){
				sdUpdateList.add(sd);
			} 
		}
				
		int nbrStocks = sdUpdateList.size();
		
		int nbrGroups = (nbrStocks - 1) /50 + 1;
		for (int i = 1; i <= nbrGroups; i ++){
			List<StockData> subList = null;
			if (i == nbrGroups){
				subList = sdUpdateList.subList(50 * (i-1), sdUpdateList.size());
			} else {
				subList = sdUpdateList.subList(50 * (i-1), 50 * i);
			}
			getDataForStocksInternal(subList);
		}
		return;
	}
	
	public void getDataForStocksInternal(List<StockData> stocks) throws Exception {
		List<String> stocksGettingData = new ArrayList<String>();
		String stockURLParm = "";
		for (StockData sd : stocks) {
			if (stockURLParm.length() > 0){
				stockURLParm += "+";
			}
			stockURLParm += sd.getStock().getSymbol();
			stocksGettingData.add(sd.getStock().getSymbol());
		}
		if (stocksGettingData.size() > 0) {
			BufferedReader br = YahooFinanceUtil.getYahooCSV("http://finance.yahoo.com/d/quotes.csv?s=" +stockURLParm + "&f=nl1j1dyrr5x0");
			CSVReader reader = new CSVReader(br);
			int symLoop = 0;
			String [] nextCSVLine = null; 
			while((nextCSVLine = reader.readNext()) != null){
				String symbol = stocksGettingData.get(symLoop);
				String name = nextCSVLine[0];
				String price = nextCSVLine[1];
				String marketCap = nextCSVLine[2];
				String dividend = nextCSVLine[3];
				String yield = nextCSVLine[4];
				String pe = nextCSVLine[5];
				String peg = nextCSVLine[6];
				String exchange = nextCSVLine[7];
				
				Stock s = null;
				for (StockData sdLoop : stocks){
					if (sdLoop.getStock().getSymbol().equals(symbol)){
						s = sdLoop.getStock();
						break;
					}
				}
				
				s.setName(name);
				
				try {
					s.setDividend(new BigDecimal(dividend));
				} catch (Exception e){
					s.setDividend(null);
				}
				try {
					s.setPe(new BigDecimal(pe));
				} catch (Exception e){
					s.setPe(null);
				}
				
				try {
					s.setPeg(new BigDecimal(peg));
				} catch (Exception e){
					s.setPeg(null);
				}
				
				try {
					s.setPrice(new BigDecimal(price));
				} catch (Exception e){
					s.setPrice(null);
				}
				
				try {
					s.setYield(new BigDecimal(yield));
				} catch (Exception e){
					s.setYield(null);
				}
				
				s.setMarketCap(marketCap);
				s.setExchange(exchange);
				s.setDataDate(new java.sql.Date(new Date().getTime()));
				
				new StockDAO(con).update(s);
				symLoop++;
			}
			reader.close();
		}
	}

	public void getDivData(StockData sd) throws Exception {
		if (dataExpired(sd.getStock().getDivDataDate())){
			insertNewDividends(sd);
			sd.getStock().setDivDataDate(new java.sql.Date(new Date().getTime()));
			new StockDAO(con).update(sd.getStock());
			sd.setDivData(null)	;
		}
		if (sd.getDivData() == null){	
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
		s = s.replace(",", "");
		try{
			return new BigDecimal(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public void updateSectorAndIndustry(final String sector, final String industry){
		industriesToUpdate = 0;
		industriesUpdated = 0;
		ExecutorService pool = Executors.newFixedThreadPool(1);
		pool.submit(new Runnable(){
			@Override
			public void run() {
				if ("ALL".equals(industry)){
					List<String> ilist = sir.getIndustriesForSector(sector);
					industriesToUpdate = ilist.size() - 1;
					for (String i : ilist){
						if ("ALL".equals(i)){
							continue;
						}
						int id = sir.getIdForSectorIndustry(sector, i);
						try {
							getStocksForIndustryAndSector(id);
						} catch (Exception e) {
							e.printStackTrace();
						}
		
					}
				} else {
					int id = sir.getIdForSectorIndustry(sector, industry);
					industriesToUpdate = 1;
					try {
						getStocksForIndustryAndSector(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
		
				}
			}
		});
	}


	public void getStocksForIndustryAndSector(int industryId) throws Exception {
		notifyAllIndustryStockListeners(industryId, null, 0);
		Date d = new IndustryDAO(con).getDataDateForIndustry(industryId);
		
		List<StockData> sdList = new ArrayList<StockData>();
		
		if (dataExpired(d)){
			rebuildNameMap(industryId);
			BufferedReader br = YahooFinanceUtil.getYahooCSV("http://biz.yahoo.com/p/csv/" + industryId + "conameu.csv");
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
				
				if (lastName == null){
					lastName = name;
				} else if (lastName.equals(name)){
					nameCount++;
				} else {
					nameCount = 0;
					lastName = name;
				}
				
				String nameLookup = name + nameCount;
				String symbol = new NameMapDAO(con).getSymbolForName(nameLookup);
				
				if (symbol == null){
					continue;
				}
				
				Stock s = new StockDAO(con).select(symbol);
				if (s == null){
					s = new Stock();
					s.setSymbol(symbol);
					s.setMarketCap(marketCap);
					s.setName(name);
					s.setIndustryId(industryId);
					try {
						s.setPe(new BigDecimal(pe));
					} catch (Exception e){
						s.setPe(null);
					}
					try {
						s.setYield(new BigDecimal(yield));
					} catch (Exception e){
						s.setYield(null);
					}
					new StockDAO(con).insert(s);
				}
				StockData sd = new StockData();
				
				sd.setStock(s);
				s.setIndustryId(industryId);
				sd.setIndustryName(sir.getIndustryName(s.getIndustryId()));
				sd.setSectorName(sir.getSectorName(s.getIndustryId()));
				sd.setWatched(new WatchListDAO(con).exists(symbol));
				sdList.add(sd);
			}
		    reader.close();
		    new IndustryDAO(con).setDataDateForIndustry(industryId, new Date());
		} else {
			List<Stock> sList = new StockDAO(con).getStocksForIndustry(industryId);
			for (Stock s : sList){
				StockData sd = new StockData();
				sd.setStock(s);
				sd.setIndustryName(sir.getIndustryName(s.getIndustryId()));
				sd.setSectorName(sir.getSectorName(s.getIndustryId()));
				sd.setWatched(new WatchListDAO(con).exists(s.getSymbol()));
				sdList.add(sd);
			}
		}
		
		getDataForStocks(sdList);
		notifyAllIndustryStockListeners(industryId, sdList, 0);
		updateStockFineData(sdList);
		industriesUpdated ++;
		notifyAllIndustryStockListeners(industryId, null, 1);
	}

	public void updateStockFineData(List<StockData> sdList)
			throws InterruptedException {
		stocksToUpdate = 0;
		stocksUpdated = 0;
		
		ExecutorService pool = Executors.newFixedThreadPool(16);
		for (final StockData sd : sdList){
			stocksToUpdate ++;
			pool.submit(new Runnable(){
				@Override
				public void run() {
					try {
						getDivData(sd);
						getFinData(sd);
						StockDataUtil.calcRankings(sd);
						stocksUpdated++;
						notifyAllStockDataChangeListeners(sd);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.MINUTES);
		notifyAllStockDataChangeListeners(null);
	}

	private void rebuildNameMap(int industryId) throws Exception {
		new NameMapDAO(con).clearIndustry(industryId);
		BufferedReader br = YahooFinanceUtil.getYahooCSV("http://biz.yahoo.com/p/" + industryId + "conameu.html");
		StringBuffer sb = new StringBuffer();
		String s = br.readLine();
		while (s != null){
			sb.append(s);
			sb.append(" ");
			s = br.readLine();
		}
		int startIx = sb.indexOf("<b>Companies</b>");
		String lookFor = "<a href=\"http://us.rd.yahoo.com/finance/industry/quote/colist/*http://biz.yahoo.com/p/";
		String lastName = null;
		int nameCount = 0;
		do {
			int linkIx = sb.indexOf(lookFor, startIx);
			if (linkIx > -1){
				int linkEnd = sb.indexOf("</a>", linkIx + lookFor.length());
				String chopped = sb.substring(linkIx + lookFor.length() + 2, linkEnd);
				String lookFor2 = ".html\">";
				int hIx = chopped.indexOf(lookFor2);
				String stock = chopped.substring(0, hIx).toUpperCase();
				String name = chopped.substring(hIx + lookFor2.length());
				if (lastName == null){
					lastName = name;
				} else if (lastName.equals(name)){
					nameCount++;
				} else {
					nameCount = 0;
					lastName = name;
				}

				new NameMapDAO(con).insert(industryId, name + nameCount, stock);
				startIx = linkEnd + 4;
			} else {
				break;
			}
		} while (startIx > -1);
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
	
	public void addToWatchList(String symbol) throws Exception {
		if (new WatchListDAO(con).exists(symbol)){
			throw new Exception("already on watch list");
		} else {
			new WatchListDAO(con).insert(symbol);
		}
		refreshWatchList();
	}
	
	public void refreshWatchList() throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(1);
		pool.submit(new Runnable(){
			@Override
			public void run() {
				try {
					List<String> list = new WatchListDAO(con).getWatchList();
					List<StockData> sdList = new ArrayList<StockData>();
					for (String symbol : list){
						Stock s = new StockDAO(con).select(symbol);
						if (s == null){
							s = new Stock();
							s.setSymbol(symbol);
							s.setIndustryId(new NameMapDAO(con).getIndustryForSymbol(symbol));
						} 
						if (s.getIndustryId() == 0){
							s.setIndustryId(new NameMapDAO(con).getIndustryForSymbol(symbol));
						}
						StockData sd = new StockData(s);
						sd.setSectorName(sir.getSectorName(s.getIndustryId()));
						sd.setIndustryName(sir.getIndustryName(s.getIndustryId()));
						sd.setWatched(new WatchListDAO(con).exists(sd.getStock().getSymbol()));
						sdList.add(sd);
					}
					
					getDataForStocks(sdList);
					
//					for (StockData sd : sdList){
//						sd.setIndustryName(sir.getIndustryName(sd.getStock().getIndustryId()));
//						sd.setSectorName(sir.getSectorName(sd.getStock().getIndustryId()));
//						
//						sdList.add(sd);
//					}
					notifyAllWatchListListeners(sdList);
					
					updateStockFineData(sdList);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public void removeFromWatchList(String symbol) throws Exception {
		new WatchListDAO(con).delete(symbol);
		refreshWatchList();
	}

	public List<String> getAllSectors() {
		return sir.getAllSectors();
	}

	public List<String> getIndustriesForSector(String sector) {
		return sir.getIndustriesForSector(sector);
	}

	public String getIndustryName(int industry) {
		return sir.getIndustryName(industry);
	}
	
	public String getSectorName(int industry) {
		return sir.getSectorName(industry);
	}

	public void createNewPortfolio(String text) throws Exception {
		PortfolioDAO dao = new PortfolioDAO(con);
		Portfolio p = new Portfolio();
		p.setId(dao.getNextId());
		p.setName(text);
		dao.insert(p);
	}
	
	public List<Portfolio> getPortfolioList() throws Exception {
		PortfolioDAO dao = new PortfolioDAO(con);
		return dao.getAll();
	}

	public PortfolioData getPortfolioData(String name) throws Exception {
		PortfolioDAO dao = new PortfolioDAO(con);
		TransactionDAO tdao = new TransactionDAO(con);
		Portfolio p = dao.selectByName(name);
		if (p == null){
			return null;
		}
		
		PortfolioData pData = new PortfolioData();
		List<Transaction> list = tdao.getTransactionsForPortfolio(p.getId());

		List<String> slist = new ArrayList<String>();
		for (Transaction t : list){
			if (!slist.contains(t.getSymbol())){
				slist.add(t.getSymbol());
			}
		}
		
		List<StockData> sdList = new ArrayList<StockData>();
		for (String sym: slist){
			Stock s = new StockDAO(con).select(sym);
			if (s == null){
				s = new Stock();
				s.setSymbol(sym);
				s.setMarketCap("");
				s.setName("");
				s.setIndustryId(new NameMapDAO(con).getIndustryForSymbol(sym));
				new StockDAO(con).insert(s);
			}
			sdList.add(new StockData(s));
		}
		
		getDataForStocks(sdList);
		for (StockData sd : sdList){
			getDivData(sd);
			getFinData(sd);
			StockDataUtil.calcRankings(sd);
		}
		
		Map<Integer, Map<String, Position>> bigMap = new HashMap<Integer, Map<String, Position>>();
		
		List<TransactionData> tdlist = new ArrayList<TransactionData>();
		for (Transaction t : list){
			StockData currsd = null;
			for (StockData sd : sdList){
				if (sd.getStock().getSymbol().equals(t.getSymbol())){
					TransactionData td = new TransactionData();
					td.setTransaction(t);
					td.setStockData(sd);
					tdlist.add(td);
					currsd = sd;
					break;
				}
			}
			
			int sector = (currsd.getStock().getIndustryId() / 100) * 100;
			
			Map<String, Position> posMap = bigMap.get(sector);
			if (posMap == null){
				posMap = new HashMap<String, Position>();
				bigMap.put(sector, posMap);
			}
			
			Position pos = posMap.get(currsd.getStock().getSymbol());
			if (pos == null){
				pos = new Position();
				pos.setSd(currsd);
				posMap.put(currsd.getStock().getSymbol(), pos);
			}
			if ("B".equals(t.getAction())){
				pos.setShares(pos.getShares().add(t.getShares()));
				pos.setCost(pos.getBasis().add(t.getShares().multiply(t.getPrice())));
			} else if ("S".equals(t.getAction())){
				pos.setShares(pos.getShares().subtract(t.getShares()));
				pos.setCost(pos.getBasis().subtract(t.getShares().multiply(t.getPrice())));				
			}

			if (pos.getShares().compareTo(BigDecimal.ZERO) == 0){
				posMap.remove(currsd.getStock().getSymbol());
			} else {
				pos.setBasis(pos.getCost().divide(pos.getShares(), 2, RoundingMode.HALF_UP));
				pos.setValue(currsd.getStock().getPrice().multiply(pos.getShares()));
			}
		}
		
		pData.setPositionMap(bigMap);
		pData.setTransactionList(tdlist);
		pData.setPortfolio(p);
		
		return pData;
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

	public List<Option> getOptions(String symbol) throws Exception {
		List<Option> list = new ArrayList<Option>();
		
		List<String> perlist = new ArrayList<String>();
		BufferedReader br = YahooFinanceUtil.getYahooCSV("http://finance.yahoo.com/q/op?s=" + symbol + "&m=0-0");
		String s = null;
		while((s = br.readLine()) != null){
			int ix = s.indexOf("View By Expiration");
			if (ix > -1){
				int endix = s.indexOf("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">", ix);
				String options = s.substring(ix, endix);
				int ixu = 0;
				while((ixu = options.indexOf("<a href=\"", ixu)) > -1) {
					int endixu = options.indexOf("</a>", ixu);
					perlist.add(options.substring(endixu - 15, endixu - 8));
					ixu = endixu;
				}
				break;
			}
		}
		for (String per : perlist){
			List<Option> options = getOptionCallsForPeriod(symbol, per);
			list.addAll(options);
		}
		br.close();
		return list;
	}
	
	private List<Option> getOptionCallsForPeriod(String symbol, String period) throws Exception {
		List<Option> list = new ArrayList<Option>();
		BufferedReader br = YahooFinanceUtil.getYahooCSV("http://finance.yahoo.com/q/op?s=" + symbol + "&m=" + period);
		String s = null;
		while((s = br.readLine()) != null){
			int ixc = s.indexOf("Call Options");
			if (ixc > -1){
				ixc = s.indexOf("Open Int</th></tr>", ixc) + 18;
				int endixc = s.indexOf("</td></tr></table><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">", ixc);
				String callTable = s.substring(ixc, endixc);
				List<String> calls = new ArrayList<String>();
				
				int ixcc = 0;
				int endixcc = -1;
				while ((endixcc = callTable.indexOf("</tr>", ixcc)) > -1){
					calls.add(callTable.substring(ixcc, endixcc + 5));
					ixcc = endixcc + 5;
				}
				
				for (String call : calls){
					List<String> callTds = new ArrayList<String>();
					int ixtd = 0;
					int endixtd = -1;
					while ((endixtd = call.indexOf("</td>", ixtd)) > -1){
						callTds.add(call.substring(ixtd, endixtd + 5));
						ixtd = endixtd + 5;
					}
					
					String ret = getNode(callTds.get(0), "<strong>", "</strong></a></td>");
					BigDecimal strike = getBd(ret);
					
					String sym = getNode(callTds.get(1), "0\">", "</a></td>");
					
					String ret3 = getNode(callTds.get(2), "right\"><b>", "</b></td>");
					if (ret3 == null){
						 ret3 = getNode(callTds.get(2), "right\">", "</td>");
					}
					BigDecimal last = getBd(ret3);
					
					String ret5 = getNode(callTds.get(4), "align=\"right\">", "</td>");
					BigDecimal bid = getBd(ret5);
					
					String ret6 = getNode(callTds.get(5), "align=\"right\">", "</td>");
					BigDecimal ask = getBd(ret6);
					
					String date = "20" + sym.substring(sym.length() - 15, sym.length() - 9);
					Date d = new SimpleDateFormat("yyyyMMdd").parse(date);
					Option o = new Option();
					o.setExpiration(d);
					o.setStrike(strike);
					o.setSymbol(sym);
					o.setAsk(ask);
					o.setBid(bid);
					o.setLast(last);
					list.add(o);
				}
				
				break;
			}
		}
		br.close();
		Collections.sort(list, new Comparator<Option>(){
			@Override
			public int compare(Option arg0, Option arg1) {
				int comp = arg0.getExpiration().compareTo(arg1.getExpiration());
				if (comp == 0){
					comp = arg0.getStrike().compareTo(arg1.getStrike());
				}
				return comp;
			}
		});
		
		return list;
	}
	
	public BigDecimal getBd(String str) {
		try {
			return new BigDecimal(str.replace(",", ""));
		} catch (Exception e){
			return null;
		}
	}
	
	private String getNode(String str, String startStr, String endStr) {
		int ix = str.indexOf(startStr, 0);
		if (ix == -1){
			return null;
		}
		int endix = str.indexOf(endStr, ix);
		if (endix == -1){
			return null;
		}
		String findStr = str.substring(ix + startStr.length(), endix);
		return findStr;
	}
}
