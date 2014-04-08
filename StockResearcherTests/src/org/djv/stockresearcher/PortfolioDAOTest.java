package org.djv.stockresearcher;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.djv.stockresearcher.db.dao.PortfolioDAO;
import org.djv.stockresearcher.model.Portfolio;
import org.junit.Test;

public class PortfolioDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         PortfolioDAO dao = new PortfolioDAO(con);
         
         dao.createTableIfNotExists();
         dao.delete(100000);
         dao.delete(200000);
         
         Portfolio p1 = new Portfolio();
         p1.setId(100000);
         p1.setName("Testing");
         
         Portfolio p2 = new Portfolio();
         p2.setId(200000);
         p2.setName("Testing a longer name than the other one.");
         
         dao.insert(p1);
         dao.insert(p2);
         
         List<Portfolio> list = dao.getAll();
         assertEquals("1.1", 2, list.size());
         
         Integer next = dao.getNextId();
         assertEquals("1.2", 200001, next.intValue());
         
         p1.setName("Test update");
         dao.update(p1);
         dao.delete(200000);
         
         List<Portfolio> list2 = dao.getAll();
         assertEquals("1.1", 1, list2.size());
         
         dao.delete(100000);
         
         List<Portfolio> list3 = dao.getAll();
         assertEquals("1.1", 0, list3.size());
	}

}
