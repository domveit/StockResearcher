package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.djv.stockresearcher.db.Util;
import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.Stock;
import org.djv.stockresearcher.model.StockData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class YahooYQLStockDataBroker implements IStockDataBroker{
	
	public void getPriceOnly(List<StockData> stocks, IStockDataCallbackHandler callBack) {
		List<String> stocksGettingData = new ArrayList<String>();
		String stockURLParm = "";
		for (StockData sd : stocks) {
			if (stockURLParm.length() > 0){
				stockURLParm += ",";
			}
			stockURLParm += ("\"" + sd.getSymbol() + "\"");
			stocksGettingData.add(sd.getSymbol());
		}
		if (stocksGettingData.size() > 0) {
			String YQLquery = 
					"select "
							+ "symbol, "
							+ "LastTradePriceOnly "
							+ "from yahoo.finance.quotes "
							+ "where symbol in (" + stockURLParm +")";
			System.err.println(YQLquery);

			BufferedReader br = null;
			
			try {
				 br = YahooFinanceUtil.getYQLJson(YQLquery);
				if (br == null){
					throw new IllegalStateException ("br is null");
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(br).getAsJsonObject();
					JsonObject query = json.get("query").getAsJsonObject();
					JsonElement jsonElement = query.get("results");
					if (jsonElement != null){
						JsonObject results = jsonElement.getAsJsonObject();
						JsonElement quoteEle = results.get("quote");
						if (quoteEle.isJsonArray()){
							JsonArray quote = results.get("quote").getAsJsonArray();
							for (JsonElement ce: quote){
								handleQuoteElementPo(stocks, ce, callBack);
							}
						} else {
							handleQuoteElementPo(stocks, quoteEle, callBack);
						}
					}
					
				}
			} catch (Exception e){
				if (stocks.size() > 1){
					System.err.println("failed to get stock price data for list " + stockURLParm + " getting data one at a time.");
					for (StockData sd : stocks){
						List<StockData> oneStock = new ArrayList<StockData>();
						oneStock.add(sd);
						getPriceOnly(oneStock, callBack);
					}
				} else {
					callBack.updateStock(stocks.get(0));
				}
			} finally {
				try {
					if (br != null){
						br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void handleQuoteElementPo(List<StockData> stocks, JsonElement ce, IStockDataCallbackHandler callBack)
			throws Exception {
		JsonObject c = ce.getAsJsonObject();
		String symbol = getString(c, "symbol");
		String price = getString(c, "LastTradePriceOnly");

		StockData sd = null;
		for (StockData sdLoop : stocks){
			if (sdLoop.getSymbol().equals(symbol)){
				sd = sdLoop;
				break;
			}
		}
		
		if (sd.getStock() == null){
			System.err.println("Stock not found " + symbol);
			return;
		}
		Stock s = sd.getStock();
		s.setPrice(Util.convertBd(price));
		s.setDataDate(new java.sql.Date(new Date().getTime()));

		callBack.updateStock(sd);
	}

	public void getData(List<StockData> stocks, IStockDataCallbackHandler callBack) {
		List<String> stocksGettingData = new ArrayList<String>();
		String stockURLParm = "";
		for (StockData sd : stocks) {
			if (stockURLParm.length() > 0){
				stockURLParm += ",";
			}
			stockURLParm += ("\"" + sd.getSymbol() + "\"");
			stocksGettingData.add(sd.getSymbol());
		}
		if (stocksGettingData.size() > 0) {
			String YQLquery = 
					"select "
							+ "symbol, "
							+ "LastTradePriceOnly, "
							+ "MarketCapitalization, "
							+ "DividendShare, "
							+ "DividendYield, "
							+ "PERatio, "
							+ "PEGRatio, "
							+ "StockExchange, "
							+ "YearLow, "
							+ "YearHigh, "
							+ "OneyrTargetPrice "
							+ "from yahoo.finance.quotes "
							+ "where symbol in (" + stockURLParm +")";
			System.err.println(YQLquery);
			BufferedReader br = null;
			try {
				br =  YahooFinanceUtil.getYQLJson(YQLquery);
				if (br == null){
					throw new IllegalStateException("br is null");
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(br).getAsJsonObject();
					JsonObject query = json.get("query").getAsJsonObject();
					JsonElement jsonElement = query.get("results");
						
						JsonObject results = jsonElement.getAsJsonObject();
						JsonElement quoteEle = results.get("quote");
						if (quoteEle.isJsonArray()){
							JsonArray quote = results.get("quote").getAsJsonArray();
							for (JsonElement ce: quote){
								handleQuoteElement(stocks, ce, callBack);
							}
						} else {
							handleQuoteElement(stocks, quoteEle, callBack);
						}
					
				}
			} catch (Exception e){
				if (stocks.size() > 1){
					System.err.println("failed to get stock data for list " + stockURLParm + " getting data one at a time.");
					for (StockData sd : stocks){
						List<StockData> oneStock = new ArrayList<StockData>();
						oneStock.add(sd);
						getData(oneStock, callBack);
					}
				} else {
					System.err.println("failed to get stock data for " + stocks.get(0).getSymbol() + ".");
					callBack.updateStock(stocks.get(0));
				}
			} finally {
				try {
					if (br != null){
						br.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void handleQuoteElement(List<StockData> stocks, JsonElement ce, IStockDataCallbackHandler callBack)
			throws Exception {
		JsonObject c = ce.getAsJsonObject();
		String symbol = getString(c, "symbol");
		String price = getString(c, "LastTradePriceOnly");
		String marketCap = getString(c, "MarketCapitalization");
		String dividend = getString(c, "DividendShare");
		String yield = getString(c, "DividendYield");
		String yrHigh = getString(c, "YearHigh");
		String yrLow = getString(c, "YearLow");
		String pe = getString(c, "PERatio");
		String peg = getString(c, "PEGRatio");
		String exchange = getString(c, "StockExchange");
		String oneYrTargetPrice = getString(c, "OneyrTargetPrice");

		StockData sd = null;
		for (StockData sdLoop : stocks){
			if (sdLoop.getSymbol().equals(symbol)){
				sd = sdLoop;
				break;
			}
		}
		
		if (sd.getStock() == null){
			System.err.println("Stock not found " + symbol);
			return;
		}

		Stock s = sd.getStock();
		s.setDividend(Util.convertBd(dividend));
		s.setPe(Util.convertBd(pe));
		s.setPeg(Util.convertBd(peg));
		s.setPrice(Util.convertBd(price));
		s.setYield(Util.convertBd(yield));
		s.setMarketCap(marketCap);
		s.setExchange(exchange);
		s.setYearHigh(Util.convertBd(yrHigh));
		s.setYearLow(Util.convertBd(yrLow));
		s.setOneYrTargetPrice(Util.convertBd(oneYrTargetPrice));
		s.setDataDate(new java.sql.Date(new Date().getTime()));
		callBack.updateStock(sd);
	}

	private String getString(JsonObject c, String string) {
		JsonElement o = c.get(string);
		if (o == null){
			return null;
		}
		if (o.isJsonNull()){
			return null;
		}
		return o.getAsString();
	}


}
