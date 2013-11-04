package org.djv.stockresearcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.djv.stockresearcher.db.SectorIndustryDAO;
import org.djv.stockresearcher.model.SectorIndustry;
import org.junit.Test;

public class SectorIndustryDAOTest {
	
	@Test
	public void test() throws Exception {
		 Class.forName("org.h2.Driver");
         Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB/stockDBTest", "stockDB", "" );
         SectorIndustryDAO dao = new SectorIndustryDAO(con);
         
         dao.createTableIfNotExists();
         
         dao.delete(1);
         dao.delete(2);
         dao.delete(3);
         dao.delete(4);
         
         {
         SectorIndustry si = dao.select(1);
         assertNull(si);
         }
         
         {
         SectorIndustry si2 = new SectorIndustry();
         si2.setIndustryId(1);
         si2.setSectorName("Sector 1");
         si2.setIndustryName("Industry 1");
         dao.insert(si2);
         }
         
         {
         SectorIndustry si3 = new SectorIndustry();
         si3.setIndustryId(2);
         si3.setSectorName("Sector 1");
         si3.setIndustryName("Industry 2");
         dao.insert(si3);
         }
         
         {
         SectorIndustry si = new SectorIndustry();
         si.setIndustryId(3);
         si.setSectorName("Sector 2");
         si.setIndustryName("Industry 3");
         dao.insert(si);
         }
         
         {
         SectorIndustry si = new SectorIndustry();
         si.setIndustryId(4);
         si.setSectorName("Sector 3");
         si.setIndustryName("Industry 4");
         dao.insert(si);
         }
         
         SectorIndustry si = dao.select(1);
         assertNotNull(si);
         assertEquals("Sector 1", si.getSectorName());
         assertEquals("Industry 1", si.getIndustryName());
         assertNull(si.getDataDate());
         
         si.setIndustryName("Industry 5");
         si.setDataDate(new java.sql.Date(System.currentTimeMillis()));
         dao.update(si);
         
         SectorIndustry si2 = dao.select(1);
         assertNotNull(si2);
         assertEquals("Sector 1", si2.getSectorName());
         assertEquals("Industry 5", si2.getIndustryName());
         assertNotNull(si2.getDataDate());

         List<String> sl = dao.getAllSectors();
         assertEquals(3, sl.size());

         int id = dao.getIdForSectorIndustry("Sector 1", "Industry 2");
         assertEquals(2, id);
         
         List<Integer> isl = dao.getIndustriesForSector("Sector 1");
         assertEquals(2, isl.size());
         
         List<String> isl2 = dao.getIndustriesNameForSector("Sector 1");
         assertEquals(2, isl2.size());
         
         dao.delete(1);
         dao.delete(2);
         dao.delete(3);
         dao.delete(4);
         
	}

}
