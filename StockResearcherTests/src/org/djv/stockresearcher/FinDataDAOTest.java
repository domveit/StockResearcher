package org.djv.stockresearcher;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.List;

import org.djv.stockresearcher.db.dao.FinDataDAO;
import org.djv.stockresearcher.model.AdjustedDiv;
import org.djv.stockresearcher.model.FinDataDTO;
import org.junit.Test;

public class FinDataDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         FinDataDAO dao = new FinDataDAO(con);
         
         dao.createTableIfNotExists();
         
         Date dd = new Date(new java.util.Date().getTime());
         Date adjustedDate2 = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2014-02-01").getTime());
         
         dao.deleteAll("TEST");
         
         {
	         List<FinDataDTO> list = dao.searchStockAndType("TEST", "A");
	         assertNotNull(list);
	         assertTrue(list.isEmpty());
         }
         
         {
	         FinDataDTO dto = new FinDataDTO();
	         dto.setSymbol("TEST");
	         dto.setType("A");
	         dto.setDataDate(dd);
	         dto.setSeq(1);
	         dto.setFieldName("Test field");
	         dto.setValue(new BigDecimal("1.00"));
	         dao.insert(dto);
	         dto.setFieldName("Test field2");
	         dto.setValue(new BigDecimal("2.00"));
	         dao.update(dto);
	         
	         List<FinDataDTO> list = dao.searchStockAndType("TEST", "A");
	         assertNotNull (list);
	         assertTrue (list.size() == 1);
	         
	         dao.delete(dto);
	         
	         List<FinDataDTO> list2 = dao.searchStockAndType("TEST", "A");
	         assertNotNull (list2);
	         assertTrue (list2.size() == 0);
         }
         dao.deleteAll("TEST");
	}

}
