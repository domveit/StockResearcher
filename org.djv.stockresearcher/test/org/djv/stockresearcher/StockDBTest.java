package org.djv.stockresearcher;

import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.db.StockDB;
import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.StockData;
import org.junit.Test;

public class StockDBTest {
	@Test
	public void test1() throws Exception {
		StockDB db = new StockDB();
		
		String[] stocks = {"ACTV", "KMI", "JNJ", "AMID", "ORCL", "SAP", "CRM", "ADBE", "INTU", 
				"MSFT", "BPFH", "HBC", "FB", "AAPL", "K", "KO", "PEP", "SFX", "SXE",
				"APU", "SPH", "NGG", "PNG", "SMLP", "EGAS", "TEG"};
		
		List<StockData> sl = new ArrayList<StockData>();
		for (String s: stocks){
			StockData sd = new StockData();
			sd.setSymbol(s);
			sl.add(sd);
		}
		
		db.getDataForStocks(sl);
		for (StockData sd : sl){
			db.getDivData(sd);
			System.err.println(sd);
		}
	}
	
	@Test
	public void test1b() throws Exception {
		StockDB db = new StockDB();
		
		List<StockData> sl = db.getStocksForIndustryAndSector(110);
		
		db.getDataForStocks(sl);
		for (StockData sd : sl){
//			db.getDivData(sd);
			System.err.println(sd);
		}
	}
	
	@Test
	public void test2() throws Exception {
		StockDB yf = new StockDB();
		List<StockData> l3 = yf.getStocksForIndustryAndSector(110);
		for (StockData sd : l3){
			System.err.println(sd);
		}
	}
	
	@Test
	public void test2a() throws Exception {
		StockDB yf = new StockDB();
		StockData sd = new StockData();
		sd.setSymbol("0819.HK");
		yf.getDivData(sd);
		System.err.println(sd);
	}
	
	@Test
	public void test4() throws Exception {
		YahooFinanceUtil.createFinLookupFile("cs");
	}
	
	@Test
	public void test3() throws Exception {
		StockDB yf = new StockDB();
		StockData sd = new StockData();
		sd.setSymbol("PVD");
		sd.setExchange("NYSE");
		yf.getFinData(sd);
		System.err.println(sd);
	}
	
}
