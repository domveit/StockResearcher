package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.djv.stockresearcher.db.Util;
import org.djv.stockresearcher.model.FinDataPeriod;
import org.djv.stockresearcher.model.FinDataRow;
import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.FinKeyData;
import org.djv.stockresearcher.model.StockData;

import au.com.bytecode.opencsv.CSVReader;

public class MSFinancialDataBroker implements IFinancialDataBroker {
	
	
	@Override
	public FinDataTable getCashFlowStatement(StockData sd) throws Exception {
		FinDataTable is = new FinDataTable();
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/ReportProcess4CSV.html?&t=" + exchange + ":" + symbol+ "&region=usa&culture=en-US&cur=&reportType=cf&period=12&dataType=A&order=asc&columnYear=5&rounding=3&view=raw&r=35432&denominatorView=raw&number=3";
			}
		});
		
		buildFinDataTable(is, br);
		return is;
	}
	
	@Override
	public FinDataTable getBalanceSheet(StockData sd) throws Exception {
		FinDataTable is = new FinDataTable();
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/ReportProcess4CSV.html?&t=" + exchange + ":" + symbol+ "&region=usa&culture=en-US&cur=&reportType=bs&period=12&dataType=A&order=asc&columnYear=5&rounding=3&view=raw&r=35432&denominatorView=raw&number=3";
			}
		});
		
		buildFinDataTable(is, br);
		return is;
	}
	
	@Override
	public FinDataTable getIncomeStatement(StockData sd) throws Exception {
		FinDataTable is = new FinDataTable();
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/ReportProcess4CSV.html?&t="+ exchange +":"+symbol+"&region=usa&culture=en-US&cur=&reportType=is&period=12&dataType=A&order=asc&columnYear=10&rounding=3&view=raw&r=341667&denominatorView=raw&number=3";
			}
		});
		
		buildFinDataTable(is, br);
		return is;
	}
	
	private void buildFinDataTable(FinDataTable is, BufferedReader br)
			throws IOException {
		CSVReader reader = new CSVReader(br, ',');
		String [] nextLine;
		int ln = 0;
		while ((nextLine = reader.readNext()) != null) {
			ln++;
			if (ln < 2){
				continue;
			}
			
			if (ln == 2){
				for (int pIx = 1; pIx < nextLine.length ; pIx ++){
					is.addPeriod(new FinDataPeriod(nextLine[pIx]));
				}
				continue;
			}
			
			FinDataRow row = new FinDataRow(nextLine[0], ln);
			is.addRow(row);
			for (int pIx = 1; pIx < nextLine.length ; pIx ++){
				is.addData(is.getPeriods().get(pIx - 1), row, Util.convertBd(nextLine[pIx]));
			}
		}
		reader.close();
	}
	
	@Override
	public Map<String, FinKeyData> getKeyData(StockData sd) throws Exception {
		Map<String, FinKeyData> finMap = new TreeMap<String, FinKeyData>();
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t="+exchange+ ":"+ symbol +"&region=usa&culture=en-US&cur=USD";
			}
		});
		if (br == null){
			return finMap;
		}
		CSVReader reader = new CSVReader(br, ',');
		String [] nextLine;
		Map<Integer, FinKeyData> listInt = new TreeMap<Integer, FinKeyData>();
		int ln = 0;
		while ((nextLine = reader.readNext()) != null) {
			ln++;
			if (ln < 3){
				continue;
			}
			if (ln == 3){
				for (int yr = 1; yr< nextLine.length ; yr ++){
					FinKeyData fd = new FinKeyData();
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
					fd.setSymbol(sd.getSymbol());
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

	
	
	private void setFinValues(Map<Integer, FinKeyData> listInt, String[] line, String match, String field) {
		if (line[0].matches(match)){
			for (int yr = 1; yr  < line.length ; yr ++){
				FinKeyData fd = listInt.get(yr);
				try {
					Field f = FinKeyData.class.getDeclaredField(field);
					f.setAccessible(true);
					f.set(fd, Util.convertBd(line[yr]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}



}
