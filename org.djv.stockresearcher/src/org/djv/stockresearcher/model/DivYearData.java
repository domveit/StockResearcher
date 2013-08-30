package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DivYearData {
	
	Integer year;
	BigDecimal div = new BigDecimal("0.0000");
	BigDecimal normalizedDiv = new BigDecimal("0.0000");
	BigDecimal pctIncreaseOverPreviousYear = new BigDecimal("0.00");
	List<DivData> divDetail = new ArrayList<DivData>();
	
	public Integer getYear() {
		return year;
	}
	public BigDecimal getNormalizedDiv() {
		return normalizedDiv;
	}
	public void setNormalizedDiv(BigDecimal normalizedDiv) {
		this.normalizedDiv = normalizedDiv;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public List<DivData> getDivDetail() {
		return divDetail;
	}
	public void setDivDetail(List<DivData> divDetail) {
		this.divDetail = divDetail;
	}
	public BigDecimal getDiv() {
		return div;
	}
	public void setDiv(BigDecimal div) {
		this.div = div;
	}
	public BigDecimal getPctIncreaseOverPreviousYear() {
		return pctIncreaseOverPreviousYear;
	}
	public void setPctIncreaseOverPreviousYear(
			BigDecimal pctIncreaseOverPreviousYear) {
		this.pctIncreaseOverPreviousYear = pctIncreaseOverPreviousYear;
	}
	
	public String toString(){
		String s = "    year= " + year + "\n";
		s+= "    div= " + div + "\n";
		s+= "    pctIncreaseOverPreviousYear= " + pctIncreaseOverPreviousYear + "\n";
		for (DivData dd : divDetail){
			s+= dd.toString();
		};
		return s;
	}

}
