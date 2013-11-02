package org.djv.stockresearcher;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;

import org.djv.stockresearcher.db.SectorDateDAO;
import org.junit.Test;

public class SectorDateDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         SectorDateDAO dao = new SectorDateDAO(con);
         
         dao.createTableIfNotExists();
         dao.delete();
         
         Date sd = dao.select();
         assertNull(sd);
         
         dao.insert(new java.sql.Date(System.currentTimeMillis()));
         
         Date sd2 = dao.select();
         assertNotNull(sd2);
         
         dao.update(new java.sql.Date(System.currentTimeMillis()));
         Date sd3 = dao.select();
         assertNotNull(sd3);
         
         dao.delete();
         
         Date sd4 = dao.select();
         assertNull(sd4);
	}

}
