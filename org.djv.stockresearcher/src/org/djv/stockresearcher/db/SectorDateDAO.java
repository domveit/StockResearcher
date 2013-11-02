package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SectorDateDAO extends H2DAO {
	
	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS SECTORDATE (DATADATE DATE)";
	private static final String SELECT_SQL = "SELECT DATADATE FROM SECTORDATE";
	private static final String UPDATE_SQL = "UPDATE SECTORDATE SET DATADATE = ?";
	private static final String INSERT_SQL = "INSERT INTO SECTORDATE VALUES (?)";
	private static final String DELETE_SQL = "DELETE FROM SECTORDATE";
	
	public SectorDateDAO(Connection con) {
		super(con);
	}

	public void createTableIfNotExists() throws Exception {
		PreparedStatement st = con.prepareStatement(CREATE_SQL);
		st.executeUpdate();
		st.close();
	}
	
	public java.sql.Date select() throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		ResultSet rs = st.executeQuery();
		java.sql.Date d = null;
		if (rs.next()){
			d = rs.getDate("DATADATE");
		}
		st.close();
		return d;
	}
	
	public void update(java.sql.Date d) throws Exception {
		PreparedStatement st2 = con.prepareStatement(UPDATE_SQL);
		st2.setDate(1, d);
		st2.executeUpdate();
	}
	
	public void insert(java.sql.Date d) throws Exception {
		PreparedStatement st2 = con.prepareStatement(INSERT_SQL);
		st2.setDate(1, d);
		st2.executeUpdate();
	}
	
	public void delete() throws Exception {
		PreparedStatement st2 = con.prepareStatement(DELETE_SQL);
		st2.executeUpdate();
	}


}
