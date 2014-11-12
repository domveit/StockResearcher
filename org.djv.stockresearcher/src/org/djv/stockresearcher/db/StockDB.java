package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.djv.stockresearcher.broker.IAnalystDataBroker;
import org.djv.stockresearcher.broker.IDividendDataBroker;
import org.djv.stockresearcher.broker.IFinancialDataBroker;
import org.djv.stockresearcher.broker.ISectorIndustryDataBroker;
import org.djv.stockresearcher.broker.IStockDataBroker;
import org.djv.stockresearcher.broker.IStockDataCallbackHandler;
import org.djv.stockresearcher.broker.MSAnalystDataBroker;
import org.djv.stockresearcher.broker.MSFinancialDataBroker;
import org.djv.stockresearcher.broker.YahooCSVDividendDataBroker;
import org.djv.stockresearcher.broker.YahooHTMLSectorIndustryDataBroker;
import org.djv.stockresearcher.broker.YahooYQLStockDataBroker;
import org.djv.stockresearcher.db.dao.AdjustedDivDAO;
import org.djv.stockresearcher.db.dao.AnalystRatingsDAO;
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
import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.FinKeyData;
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

public class StockDB {
	
	private static StockDB instance;
	Connection con = null;
	private static final int STOCK_POOL_NBR_THREADS = 8;

	ExecutorService pool1 = null;
	List<ExecutorService> pools = new ArrayList<ExecutorService>();
	
	private IStockDataBroker 			stockDataBroker 		= new YahooYQLStockDataBroker();
	private IDividendDataBroker 		divBroker 				= new YahooCSVDividendDataBroker();
	private IFinancialDataBroker 		finBroker 				= new MSFinancialDataBroker();
	private ISectorIndustryDataBroker 	sectorIndustryBroker 	= new YahooHTMLSectorIndustryDataBroker();
	private IAnalystDataBroker 			analystBroker 			= new MSAnalystDataBroker();
	
	public IDividendDataBroker getDivBroker() {
		return divBroker;
	}

	public IFinancialDataBroker getFinBroker() {
		return finBroker;
	}

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
        new AnalystRatingsDAO(con).createTableIfNotExists();
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
			if (Util.dataExpired(sd.getStock().getDataDate())){
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
						IStockDataCallbackHandler handler = new IStockDataCallbackHandler(){
							@Override
							public void updateStock(StockData sd) {
								try {
									new StockDAO(con).update(sd.getStock());
								} catch (Exception e) {
									e.printStackTrace();
								}
								stocksUpdatedCoarse++;
								notifyAllStockDataChangeListeners(sd, stocksToUpdateCoarse, stocksUpdatedCoarse);
							}
							
						};
						stockDataBroker.getData(finalSubList, handler);
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
						IStockDataCallbackHandler handler = new IStockDataCallbackHandler(){
							@Override
							public void updateStock(StockData sd) {
								try {
									new StockDAO(con).update(sd.getStock());
								} catch (Exception e) {
									e.printStackTrace();
								}
								stocksUpdatedCoarse++;
								notifyAllStockDataChangeListeners(sd, stocksToUpdateCoarse, stocksUpdatedCoarse);
							}
							
						};
						stockDataBroker.getPriceOnly(finalSubList, handler);
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

	volatile int stocksUpdated = 0;
	volatile int stocksToUpdate = 0;

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
						getAnalData(sd);
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

	public void getDivData(StockData sd, boolean forceUpdate) throws Exception {
		if (forceUpdate){
			new DividendDAO(con).deleteForStock(sd.getSymbol());
		}
		if (Util.dataExpired(sd.getStock().getDivDataDate()) || forceUpdate){
			Date startDate = new DividendDAO(con).getLastDividendOnFileForSymbol(sd.getSymbol());
			List<DivData> divList = divBroker.getNewDividends(sd, startDate);
			for (DivData dd : divList){
				new DividendDAO(con).insert(dd);
			}
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
	
	public void getFinData(StockData sd) throws Exception {
		if (Util.dataExpired(sd.getStock().getFinDataDate())){
			new FinDataDAO(con).deleteForStock(sd.getStock().getSymbol());
			Map<String, FinKeyData> finData = finBroker.getKeyData(sd);
		    for (String key : finData.keySet()){
		    	FinKeyData fpd = finData.get(key);
		    	if (fpd.getYear() != null){
		    		new FinDataDAO(con).insert(fpd);
		    	}
		    }
			sd.getStock().setFinDataDate(new java.sql.Date(new Date().getTime()));
			new StockDAO(con).update(sd.getStock());
		}
		
		List<FinKeyData> fpdl = new FinDataDAO(con).getFinDataForStock(sd.getStock().getSymbol());
		Map<String, FinKeyData> finDataDb = new TreeMap<String, FinKeyData>();
		for (FinKeyData fpd: fpdl){
			finDataDb.put(fpd.getPeriod(), fpd);
		}
		sd.setFinData(finDataDb);
		StockDataUtil.crunchFinancials(sd);
	}
	
	public void getAnalData(StockData sd) throws Exception {
		AnalystRatingsDAO dao = new AnalystRatingsDAO(con);
		AnalystRatings ar = dao.select(sd.getSymbol());
		if (ar == null) {
//			System.err.println("inserting anal data " + sd.getSymbol());
			ar = analystBroker.getAnalystRatings(sd);
			dao.insert(ar);
			
		} else {
//			System.err.println(ar.getDataDate());
			if (Util.dataExpired(ar.getDataDate())){
//				System.err.println("updating anal data " + sd.getSymbol());
				ar = analystBroker.getAnalystRatings(sd);
				if (ar != null){
					dao.update(ar);
				}
				sd.setAnalystRatings(ar);
			} else {
//				System.err.println("skipping anal data " + sd.getSymbol());
			}
		}
		sd.setAnalystRatings(ar);
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
					if (Util.dataExpired(d)){
						List<SectorIndustry> sectorInds = sectorIndustryBroker.getSectors();
						for(SectorIndustry s : sectorInds){
							 SectorIndustry si = iDAO.select(s.getIndustryId());
							 if (si == null){
								 iDAO.insert(s);
							 } else {
								 iDAO.update(s);
							 }
						}
						
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
											List<StockIndustry> stockInds = sectorIndustryBroker.getStocksForIndustry(ind);
											for(StockIndustry s : stockInds){
												StockIndustryDAO stockIndustryDAO = new StockIndustryDAO(con);
												StockIndustry si = stockIndustryDAO.select(s.getSymbol());
												 if (si == null){
													 stockIndustryDAO.insert(si);
												 } else {
													 stockIndustryDAO.update(si);
												 }
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
	
	private void getStocksForIndustryAndSector(int ind) throws Exception {
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
					{
						BigDecimal buyShares = t.getShares();
						td.setCost(t.getShares().multiply(t.getPrice()).add(t.getCommission()));
						td.setBasis(td.getCost().add(t.getPremium()));
						td.setBasisPerShare(td.getBasis().divide(t.getShares(), 6, RoundingMode.HALF_UP));
						Position buyPos = getOrCreatePosition(bigMap, td);
						Iterator<Lot> buyIter = buyPos.getLotList().iterator();
						while (buyShares.compareTo(BigDecimal.ZERO) != 0) {
							if (buyIter.hasNext()){
								Lot l = buyIter.next();
								// handle short Lots
								if (l.getShares().compareTo(BigDecimal.ZERO) < 0){
									BigDecimal addLot = l.getShares().add(buyShares);
									if (addLot.compareTo(BigDecimal.ZERO) > 0){
										buyShares = buyShares.add(l.getShares());
										buyIter.remove();
									} else if (addLot.compareTo(BigDecimal.ZERO) < 0){
										l.setShares(addLot);
										BigDecimal cost = l.getShares().multiply(l.getBasisPerShare());
										l.setCost(cost);
										l.setBasis(cost);
										buyShares = BigDecimal.ZERO;
									} else {
										buyShares = BigDecimal.ZERO;
										buyIter.remove();
									}
								}
							} else {
								Lot newLot = new Lot();
								newLot.setDate(td.getTransaction().getTranDate());
								newLot.setShares(buyShares);
								BigDecimal cost = newLot.getShares().multiply(t.getPrice()).add(t.getCommission());
								newLot.setCost(cost);
								newLot.setBasis(cost);
								newLot.setBasisPerShare(cost.divide(buyShares, 6, RoundingMode.HALF_UP));
								buyPos.getLotList().add(newLot);
								buyShares = BigDecimal.ZERO;
							}
						}
					}
					break;
				case SELL:
				case CALL_ASSIGN:
					{
						BigDecimal sellShares = t.getShares();
						td.setCost(t.getShares().multiply(t.getPrice()).multiply(BigDecimal.valueOf(-1)).add(t.getCommission()));
						td.setBasis(td.getCost().add(t.getPremium()));
						td.setBasisPerShare(td.getBasis().divide(t.getShares(), 6, RoundingMode.HALF_UP));
						Position sellPos = getOrCreatePosition(bigMap, td);
						Iterator<Lot> sellIter = sellPos.getLotList().iterator();
						while (sellShares.compareTo(BigDecimal.ZERO) != 0) {
							if (sellIter.hasNext()){
								Lot l = sellIter.next();
								// handle long lots
								if (l.getShares().compareTo(BigDecimal.ZERO) > 0){
									BigDecimal addLot = l.getShares().subtract(sellShares);
									if (addLot.compareTo(BigDecimal.ZERO) < 0){
										sellShares = sellShares.subtract(l.getShares());
										sellIter.remove();
									} else if (addLot.compareTo(BigDecimal.ZERO) > 0){
										l.setShares(addLot);
										BigDecimal cost = l.getShares().multiply(l.getBasisPerShare());
										l.setCost(cost);
										l.setBasis(cost);
										sellShares = BigDecimal.ZERO;
									} else {
										sellShares = BigDecimal.ZERO;
										sellIter.remove();
									}
								}
							} else {
								Lot newLot = new Lot();
								newLot.setDate(td.getTransaction().getTranDate());
								newLot.setShares(sellShares.multiply(BigDecimal.valueOf(-1)));
								BigDecimal cost = newLot.getShares().multiply(t.getPrice()).add(t.getCommission());
								newLot.setCost(cost);
								newLot.setBasis(cost);
								newLot.setBasisPerShare(cost.divide(sellShares, 6, RoundingMode.HALF_UP));
								sellPos.getLotList().add(newLot);
								sellShares = BigDecimal.ZERO;
							}
						}
					}
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
	
	private void pooledExecution(Runnable runnable){
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
