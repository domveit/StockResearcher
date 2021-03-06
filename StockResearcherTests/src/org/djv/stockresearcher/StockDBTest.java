package org.djv.stockresearcher;

import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.db.SectorIndustryListener;
import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.db.StockDataChangeListener;
import org.djv.stockresearcher.db.StockDataUtil;
import org.djv.stockresearcher.db.dao.SectorDateDAO;
import org.djv.stockresearcher.model.OptionPeriod;
import org.djv.stockresearcher.model.OptionTable;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class StockDBTest {
	
	@Test
	public void test_1() throws Exception {
		StockDB db = new StockDB("stockDBTest");
		new SectorDateDAO(db.getCon()).update(null);
		db.addSectorIndustryListener(new SectorIndustryListener() {
			public void notifyChanged(String industryName, int industriesToUpdate,
					int industriesUpdated, int beginOrEnd) {
			System.err.println((beginOrEnd == 0 ? "BEGIN" : "END") + " " + industryName + " " + industriesUpdated + " / " + industriesToUpdate);
			}
		});
		db.updateSectors();
		db.waitFor();
	}
	
	@Test
	public void test0() throws Exception {
		StockDB db = new StockDB("stockDBTest");
		
		db.addSectorIndustryListener(new SectorIndustryListener() {
			public void notifyChanged(String industryName, int industriesToUpdate,
					int industriesUpdated, int beginOrEnd) {
			System.err.println((beginOrEnd == 0 ? "BEGIN" : "END") + " " + industryName + " " + industriesUpdated + " / " + industriesToUpdate);
			}
		});
		
		db.addStockDataChangeListener(new StockDataChangeListener() {
			public void notifyChanged(StockData sd, int toUpdate, int updated) {
			System.err.println(sd.getStockIndustry().getName() + " " + updated + " / " + toUpdate);
			}
		});
		db.updateSectorAndIndustry("Basic Materials", "Agricultural Chemicals");
		db.waitFor();
	}
		
	@Test
	public void test1() throws Exception {
		StockDB db = new StockDB("stockDBTest");
		
		String[] stocks = {"ACTV", "KMI", "JNJ", "AMID", "ORCL", "SAP", "CRM", "ADBE", "INTU", 
				"MSFT", "BPFH", "HBC", "FB", "AAPL", "K", "KO", "PEP", "SFX", "SXE",
				"APU", "SPH", "NGG", "PNG", "SMLP", "EGAS", "TEG"};
		

		List<StockData> sl = new ArrayList<StockData>();
		for (String s: stocks){
			StockData sd = new StockData(s);
			sl.add(sd);
		}
		
		db.addStockDataChangeListener(new StockDataChangeListener() {
			public void notifyChanged(StockData sd, int toUpdate, int updated) {
			System.err.println(sd.getStockIndustry().getName() + " " + updated + " / " + toUpdate);
			}
		});
		
		db.getDataForStocks(sl);
		db.waitFor();
	}
	
	@Test
	public void test2a() throws Exception {
		StockDB yf = new StockDB("stockDBTest");
		StockData sd = new StockData("0819.HK");
		yf.getDivData(sd,true);
		System.err.println(sd);
	}
	
	@Test
	public void test3() throws Exception {
		StockDB db = new StockDB("stockDBTest");
		StockData sd = new StockData("SHLM");
		sd.getStock().setExchange("NYSE");
		
		List<StockData> sl = new ArrayList<StockData>();
		sl.add(sd);
		
		db.getDataForStocks(sl);
		db.getDivData(sd, true);
		db.getFinData(sd);
		StockDataUtil.calcRankings(sd);
		System.err.println(sd);
	}
}
