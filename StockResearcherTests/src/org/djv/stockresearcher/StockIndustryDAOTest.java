package org.djv.stockresearcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.djv.stockresearcher.db.dao.StockIndustryDAO;
import org.djv.stockresearcher.model.StockIndustry;
import org.junit.Test;

public class StockIndustryDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         StockIndustryDAO dao = new StockIndustryDAO(con);
         
         dao.createTableIfNotExists();
         
         dao.delete("TEST1");
         dao.delete("TEST2");
         dao.delete("TEST3");
         
         {
         StockIndustry si = dao.select("TEST1");
         assertNull(si);
         }
         
         {
        	 StockIndustry si2 = new StockIndustry();
        	 si2.setIndId(100);
        	 si2.setName("TEST stock 1");
        	 si2.setSymbol("TEST1");
        	 dao.insert(si2);
         }
         
         {
        	 StockIndustry si2 = new StockIndustry();
        	 si2.setIndId(100);
        	 si2.setName("TEST stock 2");
        	 si2.setSymbol("TEST2");
        	 dao.insert(si2);
         }
         
         {
        	 StockIndustry si2 = new StockIndustry();
        	 si2.setIndId(200);
        	 si2.setName("TEST stock 3");
        	 si2.setSymbol("TEST3");
        	 dao.insert(si2);
         }
         
//    	 si2.setIndId(100);
//    	 si2.setName("TEST stock 1");
//    	 si2.setSymbol("TEST1");
//    	 dao.insert(si2);
    	 
         StockIndustry si = dao.select("TEST1");
         assertNotNull(si);
         assertEquals(100, si.getIndId().intValue());
         assertEquals("TEST stock 1", si.getName());
         
         si.setName("TEST stock 1b");
         si.setIndId(200);
         dao.update(si);
         
         StockIndustry si2 = dao.select("TEST1");
         assertNotNull(si2);
         assertEquals(200, si2.getIndId().intValue());
         assertEquals("TEST stock 1b", si2.getName());

         List<StockIndustry> sl = dao.getAllForIndustry(100);
         assertEquals(1, sl.size());

         List<StockIndustry> isl = dao.getAllForIndustry(200);
         assertEquals(2, isl.size());
         
         dao.delete("TEST1");
         dao.delete("TEST2");
         dao.delete("TEST3");
         
	}

}
