package org.djv.stockresearcher.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.djv.stockresearcher.model.SectorIndustry;
import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
public class SectorIndustryRegistry {

	Map<SectorIndustry, Integer> sectorIndustryMap = new HashMap<SectorIndustry, Integer>();
	Map<String, List<String>> industriesBySectorMap = new HashMap<String, List<String>>();
	List<String> allSectors = new ArrayList<String>();

	public SectorIndustryRegistry(){
		try {
			InputStream is = getClass().getResourceAsStream("/org/djv/stockresearcher/db/sectorindustry.txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String s = br.readLine();
			while (s != null) {
				StringTokenizer st = new StringTokenizer(s, ";");
				try {
					Integer id = Integer.valueOf(st.nextToken().trim());
					String sectorDesc = st.nextToken().trim();
					String industryDesc = st.nextToken().trim();
					SectorIndustry si = new SectorIndustry(sectorDesc, industryDesc);
					sectorIndustryMap.put(si, id);
					
					List<String> l = industriesBySectorMap.get(sectorDesc);
					if (l == null){
						l = new ArrayList<String>();
						l.add("ALL");
						industriesBySectorMap.put(sectorDesc, l);
						allSectors.add(sectorDesc);
					} 
					l.add(industryDesc);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				s = br.readLine();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public Integer getIdForSectorIndustry(String sector, String industry){
		return sectorIndustryMap.get(new SectorIndustry(sector, industry));
	}
	
	public List<String> getAllSectors(){
		return allSectors;
	}
	
	public List<String> getIndustriesForSector(String sector){
		return industriesBySectorMap.get(sector);
	}
}
