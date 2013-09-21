package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class YahooFinanceUtil {
	
	public static BufferedReader getYahooCSV(String urlString) throws Exception {
		URL yahooDiv = new URL(urlString);
		URLConnection urlConnectionDiv = yahooDiv.openConnection();
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
