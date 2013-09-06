package org.djv.stockresearcher.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StockDBUtil {
	
	private static String STOCK_REPO = "~\\stockResearcher\\Data";	
	
	public static File getStockDataFile(String symbol) {
		new File(STOCK_REPO).mkdirs();
		File stockFile = new File(STOCK_REPO + "\\Stock_" + symbol+ "_" + datePrefix() + ".csv");
		System.err.println(stockFile.getAbsolutePath());
		return stockFile;
	}

	public static File getDivDataFile(String symbol) {
		new File(STOCK_REPO).mkdirs();
		File stockFile = new File(STOCK_REPO + "\\Stock_" + symbol+ "_dd_" + datePrefix() + ".csv");
		return stockFile;
	}
	
	public static File getFinDataFile(String symbol) {
		new File(STOCK_REPO).mkdirs();
		File stockFile = new File(STOCK_REPO + "\\Stock_" + symbol+ "_ fin_" + datePrefix() + ".csv");
		return stockFile;
	}
	
	public static File getFinLookupFile(String symbol) {
		new File(STOCK_REPO).mkdirs();
		File stockFile = new File(STOCK_REPO + "\\Stock_" + symbol+ "_finlk_" + datePrefix() + ".csv");
		return stockFile;
	}
	
	public static File getStockIndustryFile(int industry) {
		new File(STOCK_REPO).mkdirs();
		File stockFile = new File(STOCK_REPO + "\\Industry_" + industry + "_" + datePrefix() + ".csv");
		return stockFile;
	}
	
	public static File getNameMapFile(int industry) {
		new File(STOCK_REPO).mkdirs();
		File stockFile = new File(STOCK_REPO + "\\Industry_" + industry + "_nm_"+ datePrefix() +".csv");
		return stockFile;
	}
	
	public static String datePrefix() {
		Calendar c = Calendar.getInstance();
		String datePrefix = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
		return datePrefix;
	}

	static Map<String, String> exchangeMap = new HashMap<String, String>();
	
	static {
		exchangeMap.put("NYSE", "XNYS");
		exchangeMap.put("NZSE", "XASX"); // questionable
		exchangeMap.put("ASX", "XASX");
		exchangeMap.put("CDNX", "XTSX");
		exchangeMap.put("HKSE", "XHKG");
		exchangeMap.put("London", "XLON");
		exchangeMap.put("Milan", "MTAA");
		exchangeMap.put("NCM", "XNAS");
		exchangeMap.put("NGM", "XNAS");
		exchangeMap.put("NasdaqNM", "XNAS");
		exchangeMap.put("OTC BB", "XOTC");
		exchangeMap.put("Other OTC", "XOTC"); 
		exchangeMap.put("Paris", "XPAR");
		exchangeMap.put("Toronto", "XTSE");
		exchangeMap.put("XETRA", "XETR");
		exchangeMap.put("AMEX", "XASE");
		exchangeMap.put("", "");
		exchangeMap.put("", "");
		exchangeMap.put("", "");
		exchangeMap.put("", "");
		exchangeMap.put("", "");
	}
	
	public static String mapYahooExchangeToMorningStar(String exchange) {
		return exchangeMap.get(exchange);
	}
}
