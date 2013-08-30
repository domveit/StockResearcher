package org.djv.stockresearcher.db;

import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
public class StockDBbackup {

//	String CREATE_STOCK_SQL = "CREATE TABLE IF NOT EXISTS STOCK "
//			+ " ( STOCK_ID CHAR(10) PRIMARY KEY NOT NULL,"
//			+ " SECTOR_ID INTEGER NOT NULL, "
//			+ " INDUSTRY_ID INTEGER NOT NULL) ";
//
//	String CREATE_STOCK_INDEX = "CREATE INDEX IF NOT EXISTS STOCK_ID_INDEX ON STOCK(STOCK_ID)";
//	String CREATE_STOCK_SECTOR_INDUSTRY_INDEX = "CREATE INDEX IF NOT EXISTS STOCK_SECTOR_INDUSTRY_INDEX ON STOCK(SECTOR_ID, INDUSTRY_ID)";
//
//	String CREATE_SECTOR_SQL = "CREATE TABLE IF NOT EXISTS SECTOR "
//			+ "(SECTOR_ID INTEGER PRIMARY KEY NOT NULL, "
//			+ " DESC CHAR(30) NOT NULL)";
//
//	String CREATE_SECTOR_INDEX = "CREATE INDEX IF NOT EXISTS SECTOR_INDEX ON SECTOR(SECTOR_ID)";
//	String CREATE_SECTOR_DESC_INDEX = "CREATE INDEX IF NOT EXISTS SECTOR_DESC_INDEX ON SECTOR(DESC)";
//	
//	String CREATE_INDUSTRY_SQL = "CREATE TABLE IF NOT EXISTS INDUSTRY "
//			+ "( " 
//			+ " INDUSTRY_ID INTEGER PRIMARY KEY NOT NULL, "
//			+ " SECTOR_ID INTEGER,"
//			+ " DESC CHAR(30) NOT NULL)";
//
//	String CREATE_INDUSTRY_INDEX = "CREATE INDEX IF NOT EXISTS INDUSTRY_INDEX ON INDUSTRY(INDUSTRY_ID)";
//	String CREATE_INDUSTRY_SECTOR_INDEX = "CREATE INDEX IF NOT EXISTS INDUSTRY_SECTOR_INDEX ON INDUSTRY(SECTOR_ID)";
//	String CREATE_INDUSTRY_DESC_INDEX = "CREATE INDEX IF NOT EXISTS INDUSTRY_DESC_INDEX ON INDUSTRY(DESC)";
//	
//	SqlJetDb db;
//
//	public StockDB() {
//		try {
//			File dbFile = new File("stocks6.db");
//			System.err.println(dbFile.getAbsolutePath());
//			db = SqlJetDb.open(dbFile, true);
//			db.beginTransaction(SqlJetTransactionMode.WRITE);
//			try {
//				db.getOptions().setUserVersion(1);
//				db.createTable(CREATE_STOCK_SQL);
//				db.createIndex(CREATE_STOCK_INDEX);
//				db.createIndex(CREATE_STOCK_SECTOR_INDUSTRY_INDEX);
//				db.createTable(CREATE_SECTOR_SQL);
//				db.createIndex(CREATE_SECTOR_INDEX);
//				db.createIndex(CREATE_SECTOR_DESC_INDEX);
//				db.createTable(CREATE_INDUSTRY_SQL);
//				db.createIndex(CREATE_INDUSTRY_INDEX);
//				db.createIndex(CREATE_INDUSTRY_SECTOR_INDEX);
//				db.createIndex(CREATE_INDUSTRY_DESC_INDEX);
//			} finally {
//				db.commit();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.err.println("created stockDB");
//
//	}
//	
//	public List<Industry> getIndustriesForSector(String sector) throws Exception {
//		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
//		try {
//			ISqlJetTable sectorTbl = db.getTable("SECTOR");
//			List<Sector> sectors = getSectors(sectorTbl.lookup("SECTOR_DESC_INDEX", sector));
//			long sectorId = sectors.get(0).getId();
//			
//			ISqlJetTable table = db.getTable("INDUSTRY");
//			return getIndustries(table.lookup("INDUSTRY_SECTOR_INDEX", sectorId));
//		} finally {
//			db.commit();
//		}
//	}
//	
//	public String getIndustry(Long industry) throws Exception {
//		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
//		try {
//			ISqlJetTable table = db.getTable("INDUSTRY");
//			List<Industry> l = getIndustries(table.lookup("INDUSTRY_INDEX", industry));
//			return l.get(0).getDesc();
//		} finally {
//			db.commit();
//		}
//	}
//	
//	private List<Industry> getIndustries(ISqlJetCursor cursor) throws Exception {
//		List<Industry> l = new ArrayList<Industry>();
//		try {
//			if (!cursor.eof()) {
//				do {
//					String desc = cursor.getString("DESC");
//					long id = cursor.getInteger("INDUSTRY_ID");
//					long sectorid = cursor.getInteger("SECTOR_ID");
//					Industry s = new Industry(id, sectorid, desc);
//					l.add(s);
//				} while (cursor.next());
//			}
//		} finally {
//			cursor.close();
//		}
//		return l;
//	}
//
//	public List<Sector> getAllSectors() throws Exception {
//		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
//		try {
//			ISqlJetTable table = db.getTable("SECTOR");
//			return getSectors(table.open());
//		} finally {
//			db.commit();
//		}
//	}
//	
//	public String getSector(Long sector) throws Exception {
//		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
//		try {
//			ISqlJetTable table = db.getTable("SECTOR");
//			List<Sector> sectors = getSectors(table.lookup("SECTOR_INDEX", sector));
//			return sectors.get(0).getDesc();
//		} finally {
//			db.commit();
//		}
//	}
//	
//	private List<Sector> getSectors(ISqlJetCursor cursor) throws Exception {
//		List<Sector> sectors = new ArrayList<Sector>();
//		try {
//			if (!cursor.eof()) {
//				do {
//					String desc = cursor.getString("DESC");
//					long id = cursor.getInteger("SECTOR_ID");
//					Sector s = new Sector(id, desc);
//					sectors.add(s);
//				} while (cursor.next());
//			}
//		} finally {
//			cursor.close();
//		}
//		return sectors;
//	}
//	
//	
//	// stocks
//	public List<Stock> getAllStocks() throws Exception {
//		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
//		try {
//			ISqlJetTable table = db.getTable("STOCK");
//			return getStocks(table.open());
//		} finally {
//			db.commit();
//		}
//	}
//	
//	public List<Stock> getAllStocksForSector(String sector, String industry) throws Exception {
//		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
//		try {
//			ISqlJetTable sectorTbl = db.getTable("SECTOR");
//			List<Sector> sectors = getSectors(sectorTbl.lookup("SECTOR_DESC_INDEX", sector));
//			long sectorId = sectors.get(0).getId();
//			
//			ISqlJetTable industryTbl = db.getTable("INDUSTRY");
//			List<Industry> il = getIndustries(industryTbl.lookup("INDUSTRY_DESC_INDEX", industry));
//			long industryId = il.get(0).getId();
//			
//			ISqlJetTable stocksTbl = db.getTable("STOCK");
//			return getStocks(stocksTbl.lookup("STOCK_SECTOR_INDUSTRY_INDEX", sectorId, industryId));
//		} finally {
//			db.commit();
//		}
//	}
//	
//	private List<Stock> getStocks(ISqlJetCursor cursor) throws SqlJetException {
//		List<Stock> stocks = new ArrayList<Stock>();
//		try {
//			if (!cursor.eof()) {
//				do {
//					String symbol = cursor.getString("STOCK_ID");
//					long sector = cursor.getInteger("SECTOR_ID");
//					long industry = cursor.getInteger("INDUSTRY_ID");
//					Stock s = new Stock(symbol, sector, industry);
//					stocks.add(s);
//				} while (cursor.next());
//			}
//		} finally {
//			cursor.close();
//		}
//		return stocks;
//	}
//
//	// import
//
//	public void importAllStocks() throws Exception {
//		
//		List<Sector> sectors = getSectorsFromProps();
//
//		for (Sector s : sectors) {
//			db.beginTransaction(SqlJetTransactionMode.WRITE);
//			try {
//				ISqlJetTable table = db.getTable("SECTOR");
//				List<Sector> existSectors = getSectors(table.lookup("SECTOR_INDEX",s.getId()));
//				if (existSectors.isEmpty()) {
//					table.insert(s.getId(), s.getDesc());
//				}
//			} finally {
//				db.commit();
//			}
//		}
//		
//		List<Industry> industries = getIndustriesFromProps();
//
//		for (Industry i : industries) {
//			db.beginTransaction(SqlJetTransactionMode.WRITE);
//			try {
//				ISqlJetTable table = db.getTable("INDUSTRY");
//				List<Industry> existSectors = getIndustries(table.lookup("INDUSTRY_INDEX", i.getId()));
//				if (existSectors.isEmpty()) {
//					table.insert(i.getId(), i.getSectorId(), i.getDesc());
//				}
//			} finally {
//				db.commit();
//			}
//		}
//		
//		List<Stock> stocks = getStocksFromProps();
//
//		for (Stock s : stocks) {
//			db.beginTransaction(SqlJetTransactionMode.WRITE);
//			try {
//				ISqlJetTable table = db.getTable("STOCK");
//				List<Stock> existStocks = getStocks(table.lookup("STOCK_ID_INDEX", s.getSymbol()));
//				if (existStocks.isEmpty()) {
//					table.insert(s.getSymbol(), s.getSector(), s.getIndustry());
//				}
//			} finally {
//				db.commit();
//			}
//		}
//	}
//	
//	private List<Stock> getStocksFromProps() throws IOException {
//		List<Stock> stocks = new ArrayList<Stock>();
//		InputStream is = this.getClass().getClassLoader()
//				.getResourceAsStream("stocks.properties");
//		InputStreamReader isr = new InputStreamReader(is);
//		BufferedReader br = new BufferedReader(isr);
//		String s = br.readLine();
//		while (s != null) {
//			if (s.trim().length() == 0 || "*".equals(s.substring(0, 1))) {
//				s = br.readLine();
//				continue;
//			}
//			StringTokenizer st = new StringTokenizer(s, ",");
//			try {
//				String symbol = st.nextToken().trim();
//				Long sector = Long.valueOf(st.nextToken().trim());
//				Long industry = Long.valueOf(st.nextToken().trim());
//				industry = sector * 100 + industry;
//				Stock stock = new Stock(symbol, sector, industry);
//				stocks.add(stock);
//			} catch (Exception e) {
//				System.err.println("error in line: " + s);
//				e.printStackTrace();
//			}
//
//			s = br.readLine();
//		}
//		return stocks;
//	}
//	
//
//	private List<Sector> getSectorsFromProps() throws Exception {
//		List<Sector> sectors = new ArrayList<Sector>();
//		InputStream is = this.getClass().getClassLoader()
//				.getResourceAsStream("sectors.properties");
//		InputStreamReader isr = new InputStreamReader(is);
//		BufferedReader br = new BufferedReader(isr);
//		String s = br.readLine();
//		while (s != null) {
//			if (s.trim().length() == 0 || "*".equals(s.substring(0, 1))) {
//				s = br.readLine();
//				continue;
//			}
//			StringTokenizer st = new StringTokenizer(s, "=");
//			try {
//				Long id = Long.valueOf(st.nextToken().trim());
//				String desc = st.nextToken().trim();
//				Sector sector = new Sector(id, desc);
//				sectors.add(sector);
//			} catch (Exception e) {
//				System.err.println("error in line: " + s);
//				e.printStackTrace();
//			}
//
//			s = br.readLine();
//		}
//		return sectors;
//	}
//
//	
//	private List<Industry> getIndustriesFromProps() throws Exception {
//		List<Industry> sectors = new ArrayList<Industry>();
//		InputStream is = this.getClass().getClassLoader()
//				.getResourceAsStream("industries.properties");
//		InputStreamReader isr = new InputStreamReader(is);
//		BufferedReader br = new BufferedReader(isr);
//		String s = br.readLine();
//		while (s != null) {
//			if (s.trim().length() == 0 || "*".equals(s.substring(0, 1))) {
//				s = br.readLine();
//				continue;
//			}
//			StringTokenizer st = new StringTokenizer(s, "=");
//			try {
//				Long industry = Long.valueOf(st.nextToken().trim());
//				Long sector = industry / 100;
//				String desc = st.nextToken().trim();
//				Industry ind = new Industry(industry, sector, desc);
//				sectors.add(ind);
//			} catch (Exception e) {
//				System.err.println("error in line: " + s);
//				e.printStackTrace();
//			}
//
//			s = br.readLine();
//		}
//		return sectors;
//	}




}
