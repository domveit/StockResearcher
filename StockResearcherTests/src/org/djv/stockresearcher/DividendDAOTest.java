package org.djv.stockresearcher;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.List;

import org.djv.stockresearcher.db.dao.DividendDAO;
import org.djv.stockresearcher.model.DivData;
import org.junit.Test;

public class DividendDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         DividendDAO dao = new DividendDAO(con);
         
         dao.createTableIfNotExists();
         
         Date payDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-01").getTime());
         Date payDate2 = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2014-02-01").getTime());
         
         dao.deleteForStock("TEST");
         
         {
         DivData div = new DivData();
         div.setSymbol("TEST");
         div.setPaydate(payDate);
         div.setDividend(new BigDecimal("1.11"));
         dao.insert(div);
         
         DivData div2 = new DivData();
         div2.setSymbol("TEST");
         div2.setPaydate(payDate2);
         div2.setDividend(new BigDecimal("2.22"));
         dao.insert(div2);
         
         List<DivData> list = dao.getDividendsForSymbol("TEST");
         assertNotNull (list);
         
         Date d = dao.getLastDividendOnFileForSymbol("TEST");
         assertNotNull (d);
         }
         
         dao.deleteForStock("TEST");
	}

}
