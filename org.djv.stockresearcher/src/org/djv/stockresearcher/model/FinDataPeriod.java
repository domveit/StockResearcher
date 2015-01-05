package org.djv.stockresearcher.model;

import java.util.Calendar;

import org.djv.stockresearcher.db.StockDataUtil;

public class FinDataPeriod implements Comparable<FinDataPeriod> {
	
	String name;
	Calendar dataDate;
	
	public String getName() {
		return name;
	}
	
	public Calendar getDataDate() {
		return dataDate;
	}

	public void setDataDate(Calendar dataDate) {
		this.dataDate = dataDate;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Integer getYear() {
		return dataDate.get(Calendar.YEAR);
	}
	public Integer getMonth() {
		return dataDate.get(Calendar.MONTH) + 1;
	}
	
	public FinDataPeriod(String name) {
		super();
		this.name = name;
		if ("TTM".equals(name)){
			dataDate = StockDataUtil.TTM_CAL;
		} else {
			dataDate = Calendar.getInstance();
			int year = Integer.valueOf(name.substring(0, 4));
			int month = Integer.valueOf(name.substring(5, 7));
			dataDate.set(Calendar.YEAR, year);
			dataDate.set(Calendar.MONTH, month - 1);
			dataDate.set(Calendar.DATE, 1);
		}
	}
	
	public FinDataPeriod(String name, Calendar cal) {
		super();
		this.name = name;
		this.dataDate = cal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FinDataPeriod other = (FinDataPeriod) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(FinDataPeriod arg0) {
		FinDataPeriod p0 = (FinDataPeriod) arg0;
		int comp =  this.getYear().compareTo(p0.getYear());
		if (comp != 0){
			return comp;
		}
		return this.getMonth().compareTo(p0.getMonth());
	}
	
}
