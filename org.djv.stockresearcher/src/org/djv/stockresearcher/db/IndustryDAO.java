package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IndustryDAO extends H2DAO{
	
	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS INDUSTRY (ID INTEGER, DATADATE DATE)";
	private static final String SELECT_SQL = "SELECT DATADATE FROM INDUSTRY WHERE ID = ?";
	private static final String INSERT_SQL = "INSERT INTO INDUSTRY VALUES (?, ?)";
	private static final String DELETE_SQL = "DELETE FROM INDUSTRY WHERE ID = ?";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS INDUSTRYIX1 ON INDUSTRY (ID ASC) ";
	
	public IndustryDAO(Connection con) {
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
	
	public Date getDataDateForIndustry(int ind) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setInt(1, ind);
		ResultSet rs = st.executeQuery();
		Date d = null;
		if (rs.next()){
			d = rs.getDate("DATADATE");
		}
		st.close();
//		long end = System.currentTimeMillis();
//		System.err.println("IndustryDAO.getDataDateForIndustry " + (end-beg));
		return d;
		
	}
	
	public void setDataDateForIndustry(int ind, java.util.Date d) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setInt(1, ind);
		st.executeUpdate();
		
		PreparedStatement st2 = con.prepareStatement(INSERT_SQL);
		st2.setInt(1, ind);
		st2.setDate(2, new java.sql.Date(d.getTime()));
		st2.executeUpdate();
//		long end = System.currentTimeMillis();
//		System.err.println("IndustryDAO.setDataDateForIndustry " + (end-beg));
	}

}
