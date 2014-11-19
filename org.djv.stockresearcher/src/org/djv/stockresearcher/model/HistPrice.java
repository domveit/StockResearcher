package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.Date;

public class HistPrice {
	
	public Date date;
	public BigDecimal price;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
