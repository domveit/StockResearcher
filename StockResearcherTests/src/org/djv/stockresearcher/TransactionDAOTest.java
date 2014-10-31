package org.djv.stockresearcher;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.util.List;

import org.djv.stockresearcher.db.dao.TransactionDAO;
import org.djv.stockresearcher.model.Transaction;
import org.djv.stockresearcher.model.TransactionType;
import org.junit.Test;

public class TransactionDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         TransactionDAO dao = new TransactionDAO(con);
         
         dao.createTableIfNotExists();
         dao.deleteAllForPortfolio(100000);
         dao.deleteAllForPortfolio(200000);
         
         Transaction t1 = new Transaction();
         t1.setId(100001);
         t1.setPortId(100000);
         t1.setType(TransactionType.BUY.getTypeCode());
         t1.setTranDate(new Date(System.currentTimeMillis()));
         t1.setPrice(new BigDecimal("1234567.1234"));
         t1.setShares(new BigDecimal("123456789012.1234"));
         t1.setCommission(new BigDecimal("123456789012.1234"));
         t1.setSymbol("TEST");
         
         Transaction t2 = new Transaction();
         t2.setId(100002);
         t2.setPortId(100000);
         t2.setType(TransactionType.SELL.getTypeCode());
         t2.setTranDate(new Date(System.currentTimeMillis()));
         t2.setPrice(new BigDecimal("10.00"));
         t2.setShares(new BigDecimal("100.0"));
         t1.setCommission(new BigDecimal("150.12"));
         t2.setSymbol("TEST2");
         
         Transaction t3 = new Transaction();
         t3.setId(200001);
         t3.setPortId(200000);
         t3.setType(TransactionType.BUY.getTypeCode());
         t3.setTranDate(new Date(System.currentTimeMillis()));
         t3.setPrice(new BigDecimal("1234567.1234"));
         t3.setShares(new BigDecimal("123456789012.1234"));
         t1.setCommission(new BigDecimal("123456789012.1234"));
         t3.setSymbol("TEST");
         
         Transaction t4 = new Transaction();
         t4.setId(200002);
         t4.setPortId(200000);
         t4.setType(TransactionType.SELL.getTypeCode());
         t4.setTranDate(new Date(System.currentTimeMillis()));
         t4.setPrice(new BigDecimal("10.00"));
         t4.setShares(new BigDecimal("100.0"));
         t1.setCommission(new BigDecimal("150.12"));
         t4.setSymbol("TEST2");
         
         dao.insert(t1);
         dao.insert(t2);
         dao.insert(t3);
         dao.insert(t4);
         
         List<Transaction> list = dao.getTransactionsForPortfolio(100000);
         assertEquals("1.1", 2, list.size());
         
         List<Transaction> list2 = dao.getTransactionsForPortfolio(200000);
         assertEquals("1.1", 2, list2.size());
         
         Integer next = dao.getNextId();
         assertEquals("1.2", 200003, next.intValue());
         
         t2.setShares(new BigDecimal("200.0"));
         dao.update(t2);
         
         Transaction t2updated = dao.select(t2.getId());
         assertEquals(t2updated.getShares(), new BigDecimal("200.0000"));
         
         dao.delete(100002);
         dao.delete(200002);
         
         List<Transaction> list3 = dao.getTransactionsForPortfolio(100000);
         assertEquals("1.1", 1, list3.size());
         
         List<Transaction> list4 = dao.getTransactionsForPortfolio(200000);
         assertEquals("1.1", 1, list4.size());
         
         dao.deleteAllForPortfolio(100000);
         dao.deleteAllForPortfolio(200000);
         
         List<Transaction> list5 = dao.getTransactionsForPortfolio(100000);
         assertEquals("1.1", 0, list5.size());
         
         List<Transaction> list6 = dao.getTransactionsForPortfolio(200000);
         assertEquals("1.1", 0, list6.size());
	}

}
