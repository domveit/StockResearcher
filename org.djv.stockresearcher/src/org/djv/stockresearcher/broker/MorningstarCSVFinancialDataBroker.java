package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.djv.stockresearcher.db.Util;
import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.FinPeriodData;
import org.djv.stockresearcher.model.StockData;

import au.com.bytecode.opencsv.CSVReader;

public class MorningstarCSVFinancialDataBroker implements IFinancialDataBroker {

	@Override
	public Map<String, FinPeriodData> getFinancialData(StockData sd) throws Exception {
		
		Map<String, FinPeriodData> finMap = new TreeMap<String, FinPeriodData>();
		
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
					return finMap;
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
				System.err.println("cound not get financials for " + convertedSymbol);
				return finMap;
			}
			CSVReader reader = new CSVReader(br, ',');
		    String [] nextLine;
		    Map<Integer, FinPeriodData> listInt = new TreeMap<Integer, FinPeriodData>();
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
			    		finMap.put(period, fd);
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
		    return finMap;
		}

		private void setFinValues(Map<Integer, FinPeriodData> listInt, String[] line, String match, String field) {
	    	if (line[0].matches(match)){
		    	for (int yr = 1; yr  < line.length ; yr ++){
		    		FinPeriodData fd = listInt.get(yr);
		    		try {
						Field f = FinPeriodData.class.getDeclaredField(field);
						f.setAccessible(true);
						f.set(fd, Util.convertBd(line[yr]));
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}
	    	}
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
