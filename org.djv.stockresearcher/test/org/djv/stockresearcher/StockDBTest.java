package org.djv.stockresearcher;

import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.db.StockDataUtil;
import org.djv.stockresearcher.model.Option;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class StockDBTest {
	
	@Test
	public void test0() throws Exception {
		StockDB db = new StockDB();
		db.getStocksForIndustryAndSector(910);
	}
		
	@Test
	public void test1() throws Exception {
		StockDB db = new StockDB();
		
		String[] stocks = {"ACTV", "KMI", "JNJ", "AMID", "ORCL", "SAP", "CRM", "ADBE", "INTU", 
				"MSFT", "BPFH", "HBC", "FB", "AAPL", "K", "KO", "PEP", "SFX", "SXE",
				"APU", "SPH", "NGG", "PNG", "SMLP", "EGAS", "TEG"};
		

		List<StockData> sl = new ArrayList<StockData>();
		for (String s: stocks){
			StockData sd = new StockData(s);
			sd.getStock().setIndustryId(1);
			sl.add(sd);
		}
		
		db.getDataForStocks(sl);
		Thread.sleep(30000);
	}
	
//	@Test
//	public void test1b() throws Exception {
//		StockDB db = new StockDB();
//		
//		List<StockData> sl = db.getStocksForIndustryAndSector(110);
//		
//		db.getDataForStocks(sl);
//		for (StockData sd : sl){
////			db.getDivData(sd);
//			System.err.println(sd);
//		}
//	}
//	
//	@Test
//	public void test2() throws Exception {
//		StockDB yf = new StockDB();
//		List<StockData> l3 = yf.getStocksForIndustryAndSector(110);
//		for (StockData sd : l3){
//			System.err.println(sd);
//		}
//	}
//	
	@Test
	public void test2a() throws Exception {
		StockDB yf = new StockDB();
		StockData sd = new StockData("0819.HK");
		yf.getDivData(sd);
		System.err.println(sd);
	}
	
	@Test
	public void test3() throws Exception {
		StockDB db = new StockDB();
		StockData sd = new StockData("SHLM");
		sd.getStock().setExchange("NYSE");
		
		List<StockData> sl = new ArrayList<StockData>();
		sl.add(sd);
		
		db.getDataForStocks(sl);
		db.getDivData(sd);
		db.getFinData(sd);
		StockDataUtil.calcRankings(sd);
		System.err.println(sd);
	}
	
	@Test
	public void test4() throws Exception {
		List<Option> l = StockDB.getInstance().getOptions("KMP");
	}
	
}
