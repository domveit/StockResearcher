package org.djv.stockresearcher.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static int daysBetween(Date expiration) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(expiration);
		c2.set(Calendar.HOUR, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		if (c2.getTime().before(c.getTime())){
			return 0;
		}
		
		int days = 0;
		while (
				(c.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) || 
				(c.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) | 
				(c.get(Calendar.DATE) != c2.get(Calendar.DATE))){
			c.add(Calendar.DATE, 1);
			days++;
		}
		return days;
	}
	

}
