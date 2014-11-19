package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.djv.stockresearcher.db.Util;
import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.AnalystEstimate;
import org.djv.stockresearcher.model.AnalystEstimates;
import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.HistPrice;
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
			
		StringBuffer sb = Util.dumpBRtoStringBuffer(br);
		
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

	@Override
	public AnalystEstimates getAnalystEstimates(StockData sd) {
		AnalystEstimates est = new AnalystEstimates();
		
		est.setDataDate(new java.sql.Date(new Date().getTime()));
		est.setSymbol(sd.getSymbol());
		
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/valuation/annual-estimate-list.action?&t="+ exchange +":"+symbol+"&region=usa&culture=en-US&cur=&r=" + new Date().getTime() + "&_=" + new Date().getTime();
			}
		});
		
		if (br == null){
			return est;
		}
			
		StringBuffer sb = Util.dumpBRtoStringBuffer(br);
		
		AnalystEstimate oneYr = new AnalystEstimate();
		AnalystEstimate twoYr = new AnalystEstimate();
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"right\" class=\"str\">", 0);
			int ixEnd1 = findAfterIx(sb, "</td>", i1);
			
			String yr1Str = sb.substring(i1, ixEnd1);
			Integer yr1 = Integer.valueOf(yr1Str.substring(3));
			Integer m1 = Integer.valueOf(yr1Str.substring(0, 2));
			
			int i2 = findEndAfterIx(sb, "<td align=\"right\" class=\"str\">", i1);
			int ixEnd2 = findAfterIx(sb, "</td>", i2);
			
			String yr2Str = sb.substring(i2, ixEnd2);
			Integer yr2 = Integer.valueOf(yr2Str.substring(3));
			Integer m2 = Integer.valueOf(yr2Str.substring(0, 2));
			
			oneYr.setYear(yr1);
			twoYr.setYear(yr2);
			oneYr.setMonth(m1);
			twoYr.setMonth(m2);
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">High</td>", 0);
			int ixBeg1 = findEndAfterIx(sb, "<td align=\"right\">", i1);
			int ixEnd1 = findAfterIx(sb, "</td>", ixBeg1);
			
			String yr1HighStr = sb.substring(ixBeg1, ixEnd1);
			BigDecimal yr1High = Util.convertBd(yr1HighStr);
			
			int ixBeg2 = findEndAfterIx(sb, "<td align=\"right\">", ixBeg1);
			int ixEnd2 = findAfterIx(sb, "</td>", ixBeg2);
			
			String yr2HighStr = sb.substring(ixBeg2, ixEnd2);
			BigDecimal yr2High = Util.convertBd(yr2HighStr);
			
			oneYr.setHigh(yr1High);
			twoYr.setHigh(yr2High);
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Low</td>", 0);
			int ixBeg1 = findEndAfterIx(sb, "<td align=\"right\">", i1);
			int ixEnd1 = findAfterIx(sb, "</td>", ixBeg1);
			
			String yr1LowStr = sb.substring(ixBeg1, ixEnd1);
			BigDecimal yr1Low = Util.convertBd(yr1LowStr);
			
			int ixBeg2 = findEndAfterIx(sb, "<td align=\"right\">", ixBeg1);
			int ixEnd2 = findAfterIx(sb, "</td>", ixBeg2);
			
			String yr2LowStr = sb.substring(ixBeg2, ixEnd2);
			BigDecimal yr2Low = Util.convertBd(yr2LowStr);
			
			oneYr.setLow(yr1Low);
			twoYr.setLow(yr2Low);
		} catch (Exception e){
		}
		
		try{
			int i1 = findEndAfterIx(sb, "<td align=\"left\">Mean</td>", 0);
			int ixBeg1 = findEndAfterIx(sb, "<td align=\"right\">", i1);
			int ixEnd1 = findAfterIx(sb, "</td>", ixBeg1);
			
			String yr1MeanStr = sb.substring(ixBeg1, ixEnd1);
			BigDecimal yr1Mean = Util.convertBd(yr1MeanStr);
			
			int ixBeg2 = findEndAfterIx(sb, "<td align=\"right\">", ixBeg1);
			int ixEnd2 = findAfterIx(sb, "</td>", ixBeg2);
			
			String yr2MeanStr = sb.substring(ixBeg2, ixEnd2);
			BigDecimal yr2Mean = Util.convertBd(yr2MeanStr);
			
			oneYr.setMean(yr1Mean);
			twoYr.setMean(yr2Mean);
		} catch (Exception e){
		}
		
		est.setOneYrEstimate(oneYr);
		est.setTwoYrEstimate(twoYr);
		
		
		return est;
		
	}

	
	@Override
	public BigDecimal getAveragePE(StockData sd, int nbrYears) {
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/valuation/valuation-history.action?&t="+ exchange +":"+symbol+"&region=usa&culture=en-US&cur=&type=price-earnings";
			}
		});
		
		if (br == null){
			return null;
		}
			
		StringBuffer sb = Util.dumpBRtoStringBuffer(br);
		System.err.println(sb);
		
		BigDecimal tot = BigDecimal.ZERO;
		int nbrDiv = 0; 
		
		try{
			int ixEnd = findEndAfterIx(sb, "<th scope=\"row\" abbr=\"Price/Earnings for ", 0);
			for (int i = 0; i < 10; i ++){
				int ixBeg1 = findEndAfterIx(sb, "<td class=\"row_data\">", ixEnd);
				ixEnd = findAfterIx(sb, "</td>", ixBeg1);
				String epsStr = sb.substring(ixBeg1, ixEnd);
				BigDecimal eps = Util.convertBd(epsStr);
				if (eps != null && (10 - nbrYears) <= i){
					System.err.println(eps);
					if (eps.compareTo(new BigDecimal(100)) > 0){
						tot = tot.add(new BigDecimal(100));
					} else {
						tot = tot.add(eps);
					}
					nbrDiv++;
				}
			}
		} catch (Exception e){
		}
		if (nbrDiv > 0){
			return tot.divide(new BigDecimal(nbrDiv), 2, RoundingMode.HALF_UP);
		} else {
			return null;
		}
	}

	@Override
	public List<HistPrice> getMonthlyPrices(StockData sd, int nbrYears) {
		
		List<HistPrice> prices = new ArrayList<HistPrice>();
		
		String symbol = sd.getStock().getSymbol();

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int yrm10 = year - nbrYears;
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DATE);
		
		String urlString = "http://real-chart.finance.yahoo.com/table.csv?s=" +symbol +  "&a=" + month +"&b=" +day+ "&c=" + yrm10 + "&d=" + month +"&e=" +day+ "&f=" + year + "&g=m&ignore=.csv";
		
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice(urlString);
		if (br == null){
			return prices;
		}
		try {
			String sDiv = null;
			while ((sDiv = br.readLine()) != null){
				System.err.println(sDiv);
				if (sDiv.startsWith("Date")){
					continue;
				}
				StringTokenizer st = new StringTokenizer(sDiv, ",");
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(st.nextToken());
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				BigDecimal open = Util.convertBd(st.nextToken());
				HistPrice hp = new HistPrice();
				hp.setDate(date);
				hp.setPrice(open);
				prices.add(hp);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prices;
		
	}

}
