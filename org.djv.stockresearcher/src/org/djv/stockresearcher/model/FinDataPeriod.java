package org.djv.stockresearcher.model;

public class FinDataPeriod implements Comparable{
	
	String name;
	Integer year;
	Integer month;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public FinDataPeriod(String name) {
		super();
		this.name = name;
		if ("TTM".equals(name)){
			this.year = 9999;
			this.month = 1;
		} else {
			this.year = Integer.valueOf(name.substring(0, 4));
			this.month = Integer.valueOf(name.substring(5, 7));
		}
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
	public String toString(){
		return "{" + name + ", " + year + "}";
	}
	
	
	@Override
	public int compareTo(Object arg0) {
		FinDataPeriod p0 = (FinDataPeriod) arg0;
		int comp =  this.getYear().compareTo(p0.getYear());
		if (comp != 0){
			return comp;
		}
		return this.getMonth().compareTo(p0.getMonth());
	}
	
}
