package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.djv.stockresearcher.db.Util;

public class FinDataTable {
	
	List<FinDataPeriod> periods = new ArrayList<FinDataPeriod>();
	List<FinDataRow> rows = new ArrayList<FinDataRow>();
	
	Map<FinDataRow, Map<FinDataPeriod, BigDecimal>> dataMap = new TreeMap<FinDataRow, Map<FinDataPeriod, BigDecimal>>();
	
	public List<FinDataPeriod> getPeriods() {
		return periods;
	}
	
	public void addPeriod(FinDataPeriod p){
		periods.add(p);
	}

	public List<FinDataRow> getRows() {
		return rows;
	}

	public void addRow(FinDataRow r){
		rows.add(r);
	}
	
	public Map<FinDataRow, Map<FinDataPeriod, BigDecimal>> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<FinDataRow, Map<FinDataPeriod, BigDecimal>> dataMap) {
		this.dataMap = dataMap;
	}

	public void addData(FinDataPeriod period, FinDataRow column, BigDecimal data){
		if (data != null){
			Map<FinDataPeriod, BigDecimal> periodMap = dataMap.get(column);
			if (periodMap == null){
				periodMap = new HashMap<FinDataPeriod, BigDecimal>();
				dataMap.put(column, periodMap);
			}
			periodMap.put(period, data);
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
