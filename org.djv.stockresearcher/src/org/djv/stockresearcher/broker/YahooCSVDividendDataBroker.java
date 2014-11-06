package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.StockData;

public class YahooCSVDividendDataBroker implements IDividendDataBroker{

	public List<DivData> getNewDividends(StockData sd, Date startDate) throws Exception {
		
		List<DivData> divs = new ArrayList<DivData>();
		
		String symbol = sd.getStock().getSymbol();

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DATE);
		String urlString = "http://ichart.finance.yahoo.com/table.csv?s=" +symbol + "&a=00&b=2&c=1962&d=" + month +"&e=" +day+ "&f=" + year + "&g=v&ignore=.csv";
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice(urlString);
		if (br == null){
			return divs;
		}
		try {
			String sDiv = null;
			while ((sDiv = br.readLine()) != null){
				if (sDiv.startsWith("Date")){
					continue;
				}
				StringTokenizer st = new StringTokenizer(sDiv, ",");
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(st.nextToken());
	
				BigDecimal div = new BigDecimal(st.nextToken());
				if (div.compareTo(BigDecimal.ZERO) == 0){
					continue;
				}
				
				if (startDate != null && (date.before(startDate) || date.equals(startDate))){
					break;
				}
				DivData dd = new DivData();
				dd.setSymbol(symbol);
				dd.setPaydate(new java.sql.Date(date.getTime()));
				dd.setDividend(div);
				divs.add(dd);
			}
		} finally {
			br.close();
		}
		return divs;
		
	}
}
