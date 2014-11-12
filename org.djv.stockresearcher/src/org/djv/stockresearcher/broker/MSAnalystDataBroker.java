package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.djv.stockresearcher.db.Util;
import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.StockData;

public class MSAnalystDataBroker implements IAnalystDataBroker {

	@Override
	public AnalystRatings getAnalystRatings(StockData sd) {
		
		AnalystRatings ratings = new AnalystRatings();
		
		ratings.setDataDate(new java.sql.Date(new Date().getTime()));
		ratings.setSymbol(sd.getSymbol());
		
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/valuation/analyst-opinion-list.action?&t="+ exchange +":"+symbol+"&region=usa&culture=en-US&cur=";
			}
		});
		
		if (br == null){
			return ratings;
		}
			
		StringBuffer sb = new StringBuffer();
			
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}; 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td colspan=\"2\" align=\"left\">Five-Year Growth Forecast</td>", 0);
			int i2 = findEndAfterIx(sb, "<tr class=\"text3\">", i1);
			int ixBeg = findEndAfterIx(sb, "<td colspan=\"2\" align=\"left\">", i2);
			int ixEnd = findAfterIx(sb, "</td>", ixBeg) - 1;
			
			String str = sb.substring(ixBeg, ixEnd);
			ratings.setFiveYearGrowthForcast(Util.convertBd(str));
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Average Rating</td>", 0);
			int i2 = findEndAfterIx(sb, "<tr class=\"text3\">", i1);
			int ixBeg = findEndAfterIx(sb, "<td align=\"left\">", i2);
			int ixEnd = findAfterIx(sb, "</td>", ixBeg);
			
			String str = sb.substring(ixBeg, ixEnd);
			ratings.setAverageRating(Util.convertBd(str));
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Buy</td>", 0);
			int ixBeg = findEndAfterIx(sb, "<td align=\"right\" class=\"no_digital\">", i1);
			int ixEnd = findAfterIx(sb, "</td>", ixBeg);
			
			String str = sb.substring(ixBeg, ixEnd);
			ratings.setStrongBuyRatings(Integer.valueOf(str));
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Outperform</td>", 0);
			int ixBeg = findEndAfterIx(sb, "<td align=\"right\" class=\"no_digital\">", i1);
			int ixEnd = findAfterIx(sb, "</td>", ixBeg);
			
			String str = sb.substring(ixBeg, ixEnd);
			ratings.setBuyRatings(Integer.valueOf(str));
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Hold</td>", 0);
			int ixBeg = findEndAfterIx(sb, "<td align=\"right\" class=\"no_digital\">", i1);
			int ixEnd = findAfterIx(sb, "</td>", ixBeg);
			
			String str = sb.substring(ixBeg, ixEnd);
			ratings.setHoldRatings(Integer.valueOf(str));
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Underperform</td>", 0);
			int ixBeg = findEndAfterIx(sb, "<td align=\"right\" class=\"no_digital\">", i1);
			int ixEnd = findAfterIx(sb, "</td>", ixBeg);
			
			String str = sb.substring(ixBeg, ixEnd);
			ratings.setSellRatings(Integer.valueOf(str));
		} catch (Exception e){
		}	
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Sell</td>", 0);
			int ixBeg = findEndAfterIx(sb, "<td align=\"right\" class=\"no_digital\">", i1);
			int ixEnd = findAfterIx(sb, "</td>", ixBeg);
			
			String str = sb.substring(ixBeg, ixEnd);
			ratings.setStrongSellRatings(Integer.valueOf(str));
		} catch (Exception e){
		}

		return ratings;
	}
	
	private int findEndAfterIx(StringBuffer sb, String findStr, int ix){
		int fix = sb.indexOf(findStr, ix);
		if (fix == -1){
			throw new IllegalArgumentException("string not found");
		}
		return fix + findStr.length();
	}
	
	private int findAfterIx(StringBuffer sb, String findStr, int ix){
		int fix = sb.indexOf(findStr, ix);
		if (fix == -1){
			throw new IllegalArgumentException("string not found");
		}
		return fix;
	}

}
