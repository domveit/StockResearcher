package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.StockData;

public class MSUtil {
	
	public static BufferedReader buildBufferedReader(StockData sd, IURLBuilder urlBuilder)  {
		String symbol = sd.getStock().getSymbol();
		String exchange = sd.getStock().getExchange();

		String convertedSymbol = symbol;
		int dotIx = symbol.indexOf('.');
		if (dotIx > -1){
			convertedSymbol = symbol.substring(0, dotIx);
		}

		String msExchange = mapYahooExchangeToMorningStar(exchange);
		if (msExchange == null){
			if (symbol.endsWith(".DE")){
				msExchange = "XFRA";
			} else {
				return null;
			}
		}
		if ("XHKG".equals(msExchange)){
			convertedSymbol = "0" + convertedSymbol;
		}

		String urlString = urlBuilder.buildURL(msExchange, convertedSymbol);
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice(urlString);
		if (br == null && "XOTC".equals(msExchange)){
			msExchange = "PINX";
			urlString = urlBuilder.buildURL(msExchange, convertedSymbol);
			br = YahooFinanceUtil.getYahooCSVNice(urlString);
		}

		if (br == null){
			System.err.println("cound not get financials for " + convertedSymbol);
			return null;
		}
		return br;
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
