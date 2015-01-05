package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class YahooFinanceUtil {
	
	public static BufferedReader getYQLJson(String query) {
		String rQuery = query.replaceAll(" ", "%20");
		rQuery = rQuery.replaceAll(",", "%2C");
		rQuery = rQuery.replaceAll("\"", "%22");
		String queryURL = "http://query.yahooapis.com/v1/public/yql?q="
				+ rQuery
				+ "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
		return getYahooCSVNice(queryURL);
	}
	
	
	public static BufferedReader getYahooCSV(String urlString) throws Exception {
		URL yahooDiv = new URL(urlString);
		URLConnection urlConnectionDiv = yahooDiv.openConnection();
		urlConnectionDiv.setConnectTimeout(10000);
		urlConnectionDiv.setReadTimeout(10000);
		InputStream isDiv = urlConnectionDiv.getInputStream();
		InputStreamReader isrDiv = new InputStreamReader(isDiv, "UTF-8");
		BufferedReader brDiv = new BufferedReader(isrDiv);
		return brDiv;
	}
	
	public static BufferedReader getYahooCSVNice(String urlString)  {
		try {
			return getYahooCSV(urlString);
		} catch (Exception e) {
			return null;
		}
	}

}
