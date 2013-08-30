package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.djv.stockresearcher.model.StockData;

public class YahooFinanceUtil {
	
	public static void createStockFiles(List<StockData> stocks)
			throws MalformedURLException, IOException, FileNotFoundException {
		List<String> stocksGettingData = new ArrayList<String>();
		String stockURLParm = "";
		for (StockData sd : stocks) {
			if (stockURLParm.length() > 0){
				stockURLParm += "+";
			}
			File stockFile = StockDBUtil.getStockDataFile(sd.getSymbol());
			if (stockFile.exists()){
				continue;
			}
			stockURLParm += sd.getSymbol();
			stocksGettingData.add(sd.getSymbol());
		}
		if (stocksGettingData.size() > 0) {
			BufferedReader br = getYahooCSV("http://finance.yahoo.com/d/quotes.csv?s=" +stockURLParm + "&f=npj1dyrr5x0");
			int i = 0;
			String nextLine = null;
			while ((nextLine = br.readLine()) != null) {
				PrintWriter pw = new PrintWriter(StockDBUtil.getStockDataFile(stocksGettingData.get(i)));
				pw.println(nextLine);
				pw.close();
				i++;
			}
			br.close();
		}
	}
	
	public static void createNameMapFile(int industryId)
			throws FileNotFoundException, MalformedURLException, IOException {
		File nameMapFile= StockDBUtil.getNameMapFile(industryId);
		PrintWriter pw = new PrintWriter(nameMapFile);
		//http://biz.yahoo.com/p/110conameu.html
		BufferedReader br = getYahooCSV("http://biz.yahoo.com/p/" + industryId + "conameu.html");
		StringBuffer sb = new StringBuffer();
		String s = br.readLine();
		while (s != null){
			sb.append(s);
			sb.append(" ");
			s = br.readLine();
		}
		int startIx = sb.indexOf("<b>Companies</b>");
		String lookFor = "<a href=\"http://us.rd.yahoo.com/finance/industry/quote/colist/*http://biz.yahoo.com/p/";
		                //<a href=\"http://us.rd.yahoo.com/finance/industry/quote/colist/*http://biz.yahoo.com/p/r/remimet.bo.html">RMG Alloy Steel Ltd</a>
		String lastName = null;
		int nameCount = 0;
		do {
			int linkIx = sb.indexOf(lookFor, startIx);
			if (linkIx > -1){
				int linkEnd = sb.indexOf("</a>", linkIx + lookFor.length());
				String chopped = sb.substring(linkIx + lookFor.length() + 2, linkEnd);
				String lookFor2 = ".html\">";
				int hIx = chopped.indexOf(lookFor2);
				String stock = chopped.substring(0, hIx).toUpperCase();
				String name = chopped.substring(hIx + lookFor2.length());
				if (lastName == null){
					lastName = name;
				} else if (lastName.equals(name)){
					nameCount++;
				} else {
					nameCount = 0;
					lastName = name;
				}

				pw.println(name + nameCount + ";" +  stock);
				startIx = linkEnd + 4;
			} else {
				break;
			}
		} while (startIx > -1);
		pw.close();
	}
	
	private static BufferedReader getYahooCSV(String urlString)
			throws MalformedURLException, IOException {
		URL yahooDiv = new URL(urlString);
		URLConnection urlConnectionDiv = yahooDiv.openConnection();
		InputStream isDiv = urlConnectionDiv.getInputStream();
		InputStreamReader isrDiv = new InputStreamReader(isDiv);
		BufferedReader brDiv = new BufferedReader(isrDiv);
		return brDiv;
	}
	
	public static boolean createFinFile(String origSymbol, String convSymbol, String exchange) throws Exception {
		String urlString = "http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t="+exchange+ ":"+ convSymbol +"&region=usa&culture=en-US&cur=USD";
		File stockIndustryFile = StockDBUtil.getFinDataFile(origSymbol);
		int nbrLines = copyURLToCSVFile(urlString, stockIndustryFile);
		return nbrLines > 0;
	}
	
	public static void createFinLookupFile(String symbol) throws Exception {
		String urlString = "http://qt.morningstar.com/gidindex/acq.ashx?callback=booya&out=j&reg=USA&key="+ symbol+ "&js=JD9&range=32&noCacheIE=1377414851443";
		File stockIndustryFile = StockDBUtil.getFinLookupFile(symbol);
		copyURLToCSVFile(urlString, stockIndustryFile);
	}
	
	public static void createStockIndustryFile(int industryId) throws Exception {
		String urlString = "http://biz.yahoo.com/p/csv/" + industryId + "conameu.csv";
		File stockIndustryFile = StockDBUtil.getStockIndustryFile(industryId);
		copyURLToCSVFile(urlString, stockIndustryFile);
	}
	
	public static void createDivFile(String symbol) throws Exception {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DATE);
		String urlString = "http://ichart.finance.yahoo.com/table.csv?s=" +symbol + "&a=00&b=2&c=1962&d=" + month +"&e=" +day+ "&f=" + year + "&g=v&ignore=.csv";
		File divFile = StockDBUtil.getDivDataFile(symbol);
		copyURLToCSVFile(urlString, divFile);
	}

	public static int copyURLToCSVFile(String urlString, File f)
			throws FileNotFoundException, IOException {
		int nbrLinesWritten = 0;
		BufferedReader urlReader;
		try {
			 urlReader = getYahooCSV(urlString);
		} catch (Exception e){
			PrintWriter pw = new PrintWriter(f);
			pw.close();
			return 0;
		}
		
		PrintWriter pw = new PrintWriter(f);
		String divLine = null;
		while ((divLine = urlReader.readLine() )!= null){
			pw.println(divLine);
			nbrLinesWritten++;
		}
		pw.close();
		urlReader.close();
		return nbrLinesWritten;
	}
	

}
