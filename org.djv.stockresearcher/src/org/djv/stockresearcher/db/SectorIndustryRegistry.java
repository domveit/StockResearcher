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

public class SectorIndustryRegistry {
	
	private static SectorIndustryRegistry instance;
	
	public static SectorIndustryRegistry getInstance(){
		if (instance == null){
			instance = new SectorIndustryRegistry();
		}
		return instance;
	}

	Map<SectorIndustry, Integer> sectorIndustryMap = new HashMap<SectorIndustry, Integer>();
	Map<Integer, SectorIndustry> sectorIndustryMap2 = new HashMap<Integer, SectorIndustry>();
	Map<String, List<String>> industriesBySectorMap = new HashMap<String, List<String>>();
	List<String> allSectors = new ArrayList<String>();

	public SectorIndustryRegistry(){
		try {
			InputStream is = getClass().getResourceAsStream("/org/djv/stockresearcher/db/sectorIndustry.txt");
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
					sectorIndustryMap2.put(id, si);
					
					List<String> l = industriesBySectorMap.get(sectorDesc);
					if (l == null){
						l = new ArrayList<String>();
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
	
	public String getIndustryName(int industryId){
		SectorIndustry sectorIndustry = sectorIndustryMap2.get(industryId);
		if (sectorIndustry == null){
			return "Unknown";
		}
		return sectorIndustry.getIndustryName();
	}
	
	public String getSectorName(int industryId){
		SectorIndustry sectorIndustry = sectorIndustryMap2.get(industryId);
		if (sectorIndustry == null){
			return "Unknown";
		}
		return sectorIndustry.getSectorName();
	}
	
	public List<String> getAllSectors(){
		return allSectors;
	}
	
	public List<String> getIndustriesForSector(String sector){
		return industriesBySectorMap.get(sector);
	}
}
