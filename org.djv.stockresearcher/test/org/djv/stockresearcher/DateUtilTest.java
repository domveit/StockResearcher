package org.djv.stockresearcher;

import java.util.Calendar;

import org.djv.stockresearcher.util.DateUtil;
import org.junit.Test;

public class DateUtilTest {
	
	@Test
	public void test1(){
		Calendar c = Calendar.getInstance();
		c.set(2017, 10, 20);

		System.err.println(DateUtil.daysBetween(c.getTime()));
		
	}

}
