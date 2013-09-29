package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.FinPeriodData;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;
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
			BufferedReader br = YahooFinanceUtil.getYahooCSV("http://finance.yahoo.com/d/quotes.csv?s=" +stockURLParm + "&f=npj1dyrr5x0");
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
//		System.err.println(symbol + " got div date " + d);
		
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
			
//			System.err.println(symbol + " read div from yahoo  " + date + " " + div);
			
			if (d != null && (date.before(d) || date.equals(d))){
				break;
			}
			DivData dd = new DivData();
			dd.setSymbol(symbol);
			dd.setPaydate(new java.sql.Date(date.getTime()));
			dd.setDividend(div);
			new DividendDAO(con).insert(dd);
//			System.err.println(symbol + " inserted div from yahoo  " + date + " " + div);
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
//				System.err.println("Could not map to morningstar exhange " + symbol + ":" + exchange);
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
//			    			System.err.println("could not read year: \""  + period + "\"");
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
				
//				String roe =  nextLine[4];
//				String oneDayPriceChange =  nextLine[1];
//				String debtToEquity =  nextLine[6];
//				String priceToBook =  nextLine[7];
//				String netProfitMargin =  nextLine[8];
//				String priceToFreeCashFlow =  nextLine[9];

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
//					System.err.println("could not namemap look up " + nameLookup);
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
		
//		industryStockMap.put(industryId, sdList);
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
		//http://biz.yahoo.com/p/110conameu.html
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
		                //<a href=\"http://us.rd.yahoo.com/finance/industry/quote/colist/*http://biz.yahoo.com/p/r/remimet.bo.html">RMG Alloy Steel Ltd</a>
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
						StockData sd = new StockData(s);
						sd.setIndustryName(sir.getIndustryName(s.getIndustryId()));
						sd.setSectorName(sir.getSectorName(s.getIndustryId()));
						sd.setWatched(new WatchListDAO(con).exists(symbol));
						sdList.add(sd);
					}
					
					getDataForStocks(sdList);
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
}
