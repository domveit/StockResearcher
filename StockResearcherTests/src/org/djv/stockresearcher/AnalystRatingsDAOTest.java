package org.djv.stockresearcher;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;

import org.djv.stockresearcher.db.dao.AnalystRatingsDAO;
import org.djv.stockresearcher.model.AnalystRatings;
import org.junit.Test;

public class AnalystRatingsDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         
         AnalystRatingsDAO dao = new AnalystRatingsDAO(con);
         
         dao.createTableIfNotExists();
         
         Date payDate = new Date(new java.util.Date().getTime());
         Date adjustedDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-01").getTime());
         Date adjustedDate2 = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2014-02-01").getTime());
         
         
         dao.delete("TEST");
         
         {
         AnalystRatings ar = new AnalystRatings();
         ar.setSymbol("TEST");
         ar.setDataDate(payDate);
         ar.setFiveYearGrowthForcast(new BigDecimal("5.12"));
         ar.setAverageRating(new BigDecimal("4.5"));
         ar.setBuyRatings(1);
         ar.setHoldRatings(2);
         ar.setSellRatings(3);
         ar.setStrongBuyRatings(4);
         ar.setStrongSellRatings(5);
         
         dao.insert(ar);
         
         ar.setDataDate(adjustedDate);
         ar.setFiveYearGrowthForcast(new BigDecimal("4.12"));
         ar.setAverageRating(new BigDecimal("3.5"));
         ar.setBuyRatings(5);
         ar.setHoldRatings(6);
         ar.setSellRatings(7);
         ar.setStrongBuyRatings(8);
         ar.setStrongSellRatings(9);
         dao.update(ar);
         
         AnalystRatings arSel = dao.select("TEST");
         assertNotNull (arSel);
         }
         dao.delete("TEST");
	}

}
