package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.io.IOException;

import org.djv.stockresearcher.db.Util;
import org.djv.stockresearcher.model.FinDataPeriod;
import org.djv.stockresearcher.model.FinDataRow;
import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.FinDataType;
import org.djv.stockresearcher.model.StockData;

import au.com.bytecode.opencsv.CSVReader;

public class MSFinancialDataBroker implements IFinancialDataBroker {
	
	
	@Override
	public FinDataTable getCashFlowStatement(StockData sd) throws Exception {
		FinDataTable is = new FinDataTable(sd.getSymbol(), FinDataType.CASH_FLOW_STATEMENT);
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/ReportProcess4CSV.html?&t=" + exchange + ":" + symbol+ "&region=usa&culture=en-US&cur=&reportType=cf&period=12&dataType=A&order=asc&columnYear=5&rounding=3&view=raw&r=35432&denominatorView=raw&number=3";
			}
		});
		
		if (br != null){
			buildFinDataTable(is, br, 2);
		}
		return is;
	}
	
	@Override
	public FinDataTable getBalanceSheet(StockData sd) throws Exception {
		FinDataTable is = new FinDataTable(sd.getSymbol(), FinDataType.BALANCE_SHEET);
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/ReportProcess4CSV.html?&t=" + exchange + ":" + symbol+ "&region=usa&culture=en-US&cur=&reportType=bs&period=12&dataType=A&order=asc&columnYear=5&rounding=3&view=raw&r=35432&denominatorView=raw&number=3";
			}
		});
		if (br != null){
			buildFinDataTable(is, br, 2);
		}
		return is;
	}
	
	@Override
	public FinDataTable getIncomeStatement(StockData sd) throws Exception {
		FinDataTable is = new FinDataTable(sd.getSymbol(), FinDataType.INCOME_STATEMENT);
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/ReportProcess4CSV.html?&t="+ exchange +":"+symbol+"&region=usa&culture=en-US&cur=&reportType=is&period=12&dataType=A&order=asc&columnYear=10&rounding=3&view=raw&r=341667&denominatorView=raw&number=3";
			}
		});
		if (br != null){
			buildFinDataTable(is, br, 2);
		}
		return is;
	}
	
	
	@Override
	public FinDataTable getKeyData(StockData sd) throws Exception {
		FinDataTable kr = new FinDataTable(sd.getSymbol(), FinDataType.KEY_RATIOS);
		BufferedReader br = MSUtil.buildBufferedReader(sd, new IURLBuilder(){
			@Override
			public String buildURL(String exchange, String symbol) {
				return "http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t="+exchange+ ":"+ symbol +"&region=usa&culture=en-US&cur=USD";
			}
		});
		
		if (br != null){
			buildFinDataTable(kr, br, 3);
		}
		return kr;
	}
	
	private void buildFinDataTable(FinDataTable is, BufferedReader br, int periodRow)
			throws IOException {
		CSVReader reader = new CSVReader(br, ',');
		String [] nextLine;
		int ln = 0;
		int pixMax = 0;
		while ((nextLine = reader.readNext()) != null) {
			ln++;
			if (ln < periodRow){
				continue;
			}
			
			if (ln == periodRow){
				pixMax = nextLine.length;
				for (int pIx = 1; pIx < nextLine.length ; pIx ++){
					is.addPeriod(new FinDataPeriod(nextLine[pIx]));
				}
				continue;
			}
			
			FinDataRow row = new FinDataRow(nextLine[0], ln);
			is.addRow(row);
			for (int pIx = 1; pIx < pixMax ; pIx ++){
				if (nextLine.length < pixMax){
					is.addData(is.getPeriods().get(pIx - 1), row, null);
				} else {
					String s = nextLine[pIx];
					is.addData(is.getPeriods().get(pIx - 1), row, Util.convertBd(s));
				}
				
			}
		}
		reader.close();
	}
	
}
