package org.djv.stockresearcher;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;

import org.djv.stockresearcher.db.dao.AdjustedDivDAO;
import org.djv.stockresearcher.model.AdjustedDiv;
import org.junit.Test;

public class AdjustedDivDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         AdjustedDivDAO dao = new AdjustedDivDAO(con);
         
         dao.createTableIfNotExists();
         
         Date payDate = new Date(new java.util.Date().getTime());
         Date adjustedDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-01").getTime());
         Date adjustedDate2 = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2014-02-01").getTime());
         
         
         dao.delete("TEST", payDate);
         
         {
         AdjustedDiv adiv = new AdjustedDiv();
         adiv.setSymbol("TEST");
         adiv.setPaydate(payDate);
         adiv.setAdjustedDate(adjustedDate);
         adiv.setAdjustedDiv(new BigDecimal("1.11"));
         dao.insert(adiv);
         
         adiv.setAdjustedDate(adjustedDate2);
         adiv.setAdjustedDiv(new BigDecimal("2.22"));
         dao.update(adiv);
         
         AdjustedDiv adivSel = dao.getAdjustment("TEST", payDate);
         assertNotNull (adivSel);
         }
         dao.delete("TEST", payDate);
	}

}
