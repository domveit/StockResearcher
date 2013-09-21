package org.djv.stockresearcher.db;

import java.util.HashMap;
import java.util.Map;

public class StockDBUtil {

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
