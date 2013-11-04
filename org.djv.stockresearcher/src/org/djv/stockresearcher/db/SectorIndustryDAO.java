package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.SectorIndustry;

public class SectorIndustryDAO extends H2DAO{
	
	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS SECTORINDUSTRY (ID INTEGER, SECTORNAME CHAR(40), INDUSTRYNAME CHAR(40), DATADATE DATE)";
	private static final String SELECT_SQL = "SELECT SECTORNAME, INDUSTRYNAME, DATADATE FROM SECTORINDUSTRY WHERE ID = ?";
	
	private static final String SELECT_ID_FOR_SECTOR_SQL = "SELECT ID FROM SECTORINDUSTRY WHERE SECTORNAME = ?";
	
	private static final String SELECT_ALL_SECTOR_SQL = "SELECT DISTINCT(SECTORNAME) AS SNAME FROM SECTORINDUSTRY ORDER BY SNAME";
	private static final String SELECT_INDNAME_FOR_SECTOR_SQL = "SELECT INDUSTRYNAME FROM SECTORINDUSTRY WHERE SECTORNAME = ?";
	private static final String SELECT_ID_FOR_SECTOR_IND_SQL = "SELECT ID FROM SECTORINDUSTRY WHERE SECTORNAME = ? AND INDUSTRYNAME = ?";
	
	private static final String UPDATE_SQL = "UPDATE SECTORINDUSTRY SET SECTORNAME = ?, INDUSTRYNAME = ?, DATADATE = ? WHERE ID = ?";
	private static final String INSERT_SQL = "INSERT INTO SECTORINDUSTRY VALUES (?, ?, ?, ?)";
	private static final String DELETE_SQL = "DELETE FROM SECTORINDUSTRY WHERE ID = ?";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS INDUSTRYIX1 ON SECTORINDUSTRY (ID ASC) ";
	
	public SectorIndustryDAO(Connection con) {
		super(con);
	}

	public void createTableIfNotExists() throws Exception {
		PreparedStatement st = con.prepareStatement(CREATE_SQL);
		st.executeUpdate();
		st.close();
		
		PreparedStatement ist = con.prepareStatement(INDEX1_SQL);
		ist.executeUpdate();
		ist.close();
	}
	
	public SectorIndustry select(int ind) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setInt(1, ind);
		ResultSet rs = st.executeQuery();
		SectorIndustry si = null;
		if (rs.next()){
			si = new SectorIndustry();
			si.setIndustryId(ind);
			si.setIndustryName(rs.getString("INDUSTRYNAME"));
			si.setSectorName(rs.getString("SECTORNAME"));
			si.setDataDate(rs.getDate("DATADATE"));
			
		}
		st.close();
		return si;
	}
	
	public void insert(SectorIndustry si) throws Exception {
		PreparedStatement st2 = con.prepareStatement(INSERT_SQL);
		st2.setInt(1, si.getIndustryId());
		st2.setString(2, si.getSectorName());
		st2.setString(3, si.getIndustryName());
		st2.setDate(4, si.getDataDate());
		int nbr = st2.executeUpdate();
		if (nbr != 1){
			throw new IllegalStateException("insert did not return 1");
		}
	}
	
	public void update(SectorIndustry si) throws Exception {
		PreparedStatement st2 = con.prepareStatement(UPDATE_SQL);
		
		st2.setString(1, si.getSectorName());
		st2.setString(2, si.getIndustryName());
		st2.setDate(3, si.getDataDate());
		st2.setInt(4, si.getIndustryId());
		int nbr = st2.executeUpdate();
		if (nbr != 1){
			throw new IllegalStateException("update did not return 1");
		}
	}
	
	public void delete(Integer ind) throws Exception {
		PreparedStatement st2 = con.prepareStatement(DELETE_SQL);
		st2.setInt(1, ind);
		int nbr = st2.executeUpdate();
		if (nbr != 1){
			throw new IllegalStateException("deete did not return 1");
		}
	}

	public List<Integer> getIndustriesForSector(String sector) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_ID_FOR_SECTOR_SQL);
		st.setString(1, sector);
		ResultSet rs = st.executeQuery();
		List<Integer> list =new ArrayList<Integer>();
		while (rs.next()){
			Integer s = rs.getInt("ID");
			list.add(s);
		}
		st.close();
		return list;
	}

	public Integer getIdForSectorIndustry(String sector, String industry) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_ID_FOR_SECTOR_IND_SQL);
		st.setString(1, sector);
		st.setString(2, industry);
		ResultSet rs = st.executeQuery();
		Integer i = null;
		if (rs.next()){
			i = rs.getInt("ID");
		}
		st.close();
		return i;
	}

	public List<String> getAllSectors() throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_ALL_SECTOR_SQL);
		ResultSet rs = st.executeQuery();
		List<String> list =new ArrayList<String>();
		while (rs.next()){
			String s = rs.getString("SNAME");
			list.add(s);
		}
		st.close();
		return list;
	}

	public List<String> getIndustriesNameForSector(String sector) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_INDNAME_FOR_SECTOR_SQL);
		st.setString(1, sector);
		ResultSet rs = st.executeQuery();
		List<String> list =new ArrayList<String>();
		while (rs.next()){
			String s = rs.getString("INDUSTRYNAME");
			list.add(s);
		}
		st.close();
		return list;
	}

}
