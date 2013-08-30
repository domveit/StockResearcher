package org.djv.stockresearcher.model;

public class SectorIndustry {
	
	String sectorName;
	String industryName;
	
	public SectorIndustry(String sectorName, String industryName) {
		super();
		this.sectorName = sectorName;
		this.industryName = industryName;
	}
	
	public String getSectorName() {
		return sectorName;
	}
	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((industryName == null) ? 0 : industryName.hashCode());
		result = prime * result
				+ ((sectorName == null) ? 0 : sectorName.hashCode());
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
		SectorIndustry other = (SectorIndustry) obj;
		if (industryName == null) {
			if (other.industryName != null)
				return false;
		} else if (!industryName.equals(other.industryName))
			return false;
		if (sectorName == null) {
			if (other.sectorName != null)
				return false;
		} else if (!sectorName.equals(other.sectorName))
			return false;
		return true;
	}
	
	

}
