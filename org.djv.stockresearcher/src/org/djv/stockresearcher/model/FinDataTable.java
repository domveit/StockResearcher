package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.djv.stockresearcher.db.Util;

public class FinDataTable {
	
	String symbol;
	FinDataType type;
	
	List<FinDataPeriod> periods = new ArrayList<FinDataPeriod>();
	List<FinDataRow> rows = new ArrayList<FinDataRow>();
	
	Map<FinDataRow, Map<FinDataPeriod, BigDecimal>> dataMap = new TreeMap<FinDataRow, Map<FinDataPeriod, BigDecimal>>();
	
	public FinDataTable(String symbol, FinDataType type) {
		super();
		this.symbol = symbol;
		this.type = type;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public FinDataType getType() {
		return type;
	}

	public void setType(FinDataType type) {
		this.type = type;
	}

	public List<FinDataPeriod> getPeriods() {
		return periods;
	}
	
	public void addPeriod(FinDataPeriod p){
		periods.add(p);
		Collections.sort(periods);
	}

	public List<FinDataRow> getRows() {
		return rows;
	}

	public void addRow(FinDataRow r){
		rows.add(r);
		Collections.sort(rows);
	}
	
	public Map<FinDataRow, Map<FinDataPeriod, BigDecimal>> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<FinDataRow, Map<FinDataPeriod, BigDecimal>> dataMap) {
		this.dataMap = dataMap;
	}

	public void addData(FinDataPeriod period, FinDataRow row, BigDecimal data){
//		if (data != null){
			Map<FinDataPeriod, BigDecimal> periodMap = dataMap.get(row);
			if (periodMap == null){
				periodMap = new TreeMap<FinDataPeriod, BigDecimal>();
				dataMap.put(row, periodMap);
			}
			periodMap.put(period, data);
//		}
		
		if (!periods.contains(period)){
			addPeriod(period);
		}
		
		if (!rows.contains(row)){
			addRow(row);
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		int colWidth = getMaxRowWidth();
		
		sb.append(Util.padRight("", colWidth));
		for (FinDataPeriod p : periods){
			sb.append(Util.padLeft(p.getName(), 10));
		}
		sb.append("\n");
		
		for (FinDataRow r : rows){
			sb.append(Util.padRight(r.getName(), colWidth));
			Map<FinDataPeriod, BigDecimal> map = dataMap.get(r);
			if (map != null){
				for (FinDataPeriod p : periods){
					BigDecimal val = map.get(p);
					if (val != null){
						sb.append(Util.padLeft(val.toString(), 10));
					} else {
						sb.append(Util.padLeft("", 10));
					}
				}
			}
			sb.append("\n");
		}
		
		return sb.toString();
		
	}

	private int getMaxRowWidth() {
		int max = 10;
		for (FinDataRow row : rows){
			int length = row.getName().length();
			if (max < length){
				max = length;
			}
		}
		return max + 2;
	}

}
