package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.StockIndustry;

public class YahooEarningsDateBroker implements IEarningsDateBroker {

	@Override
	public Date getEarningsDate(String symbol) {
		Date d = null;
		BufferedReader br = null; 
		try {
			br = YahooFinanceUtil.getYahooCSVNice("http://biz.yahoo.com/rr/?s="+ symbol +"&d=research%2Fearncal");
			if (br == null){
				return null;
			}
			StringBuffer sb = new StringBuffer();
			String s = br.readLine();
			while (s != null){
				sb.append(s);
				sb.append("\n");
				s = br.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (br != null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return d;
	}

}
