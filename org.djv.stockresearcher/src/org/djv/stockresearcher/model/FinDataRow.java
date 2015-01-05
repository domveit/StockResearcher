package org.djv.stockresearcher.model;

public class FinDataRow implements Comparable<FinDataRow> {
	
	public String name;
	public Integer ix;
	
	public FinDataRow(String name, Integer ix) {
		super();
		this.name = name;
		this.ix = ix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIx() {
		return ix;
	}

	public void setIx(Integer ix) {
		this.ix = ix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ix == null) ? 0 : ix.hashCode());
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
		FinDataRow other = (FinDataRow) obj;
		if (ix == null) {
			if (other.ix != null)
				return false;
		} else if (!ix.equals(other.ix))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString(){
		return "{" + name + " " + ix +  "}";
	}

	@Override
	public int compareTo(FinDataRow arg0) {
		return this.getIx().compareTo(arg0.getIx());
	}

}
