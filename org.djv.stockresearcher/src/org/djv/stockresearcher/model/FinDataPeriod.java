package org.djv.stockresearcher.model;

public class FinDataPeriod {
	
	String name;
	Integer year;
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
	public FinDataPeriod(String name) {
		super();
		this.name = name;
		if ("TTM".equals(name)){
			this.year = 9999;
		} else {
			this.year = Integer.valueOf(name.substring(0, 4));
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

	public String toString(){
		return "{" + name + ", " + year + "}";
	}
	
}
