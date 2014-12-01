package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.Calendar;

public class HistPrice {
	
	public Calendar date;
	public BigDecimal price;
	
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
