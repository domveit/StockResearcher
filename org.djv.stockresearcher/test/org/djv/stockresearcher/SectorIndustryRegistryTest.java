package org.djv.stockresearcher;

import java.util.List;

import org.djv.stockresearcher.db.SectorIndustryRegistry;
import org.junit.Test;

public class SectorIndustryRegistryTest {
	
	@Test
	public void test1() throws Exception {
		SectorIndustryRegistry sir = new SectorIndustryRegistry();
		List<String> sl = sir.getAllSectors();
		for (String s: sl){
			System.err.println(s);
		}
		List<String> sl2 = sir.getIndustriesForSector(sl.get(0));
		for (String si : sl2){
			System.err.println(si);
		}
		int id = sir.getIdForSectorIndustry(sl.get(0), sl2.get(0));
		System.err.println(id);
	}
	
}
