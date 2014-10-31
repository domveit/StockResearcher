package org.djv.stockresearcher.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class OptionPeriod {
	
	int y;
	int m;
	int d;
	
	public OptionPeriod (int year, int month, int date){
		this.y = year;
		this.m = month;
		this.d = date;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + d;
		result = prime * result + m;
		result = prime * result + y;
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
		OptionPeriod other = (OptionPeriod) obj;
		if (d != other.d)
			return false;
		if (m != other.m)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public String toString(){
		return new SimpleDateFormat("yyyy-MM-dd").format(getDate());
	}
	
	public Date getDate(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, d);
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m - 1);
		return c.getTime();
	}

	public int getY() {
		return y;
	}

	public int getM() {
		return m;
	}

	public int getD() {
		return d;
	}

}
