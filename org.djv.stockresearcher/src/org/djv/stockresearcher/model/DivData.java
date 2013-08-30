package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DivData {
	Calendar date;
	BigDecimal dividend;
	BigDecimal normalizedDivided;
	
	public Calendar getDate() {
		return date;
	}
	public BigDecimal getNormalizedDivided() {
		return normalizedDivided;
	}
	public void setNormalizedDivided(BigDecimal normalizedDivided) {
		this.normalizedDivided = normalizedDivided;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public BigDecimal getDividend() {
		return dividend;
	}
	public void setDividend(BigDecimal dividend) {
		this.dividend = dividend;
	}
	
	public String toString(){
		String s = "        date= " + new SimpleDateFormat("MM/dd/yyyy").format(date.getTime()) + "\n";
		s+= "        dividend= " + dividend + "\n";
		s+= "        normalizedDivided= " + normalizedDivided + "\n";
		return s;
	}
	
	
}
