package org.djv.stockresearcher.db;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class Util {

	public static BigDecimal convertBd(String s) {
		if (s == null){
			return null;
		}
		s = s.replace(",", "");
		try{
			return new BigDecimal(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean dataExpired(Date d) {
		if (d == null){
			return true;
		}
		Calendar now = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		if (now.get(Calendar.YEAR) > c.get(Calendar.YEAR)){
			return true;
		}
		
		if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR) && now.get(Calendar.MONTH) > c.get(Calendar.MONTH)){
			return true;
		}
		
		if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR) && now.get(Calendar.MONTH) == c.get(Calendar.MONTH) && now.get(Calendar.DATE) > c.get(Calendar.DATE)){
			return true;
		}
		
		return false;
	}
}
