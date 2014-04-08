package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WatchListDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS WATCHLIST "
			+ "(" 
			+ "SYMBOL CHAR(20))";
	
	private static final String INDEX1_SQL = 
			"CREATE INDEX IF NOT EXISTS WATCHLISTIX1 ON WATCHLIST (SYMBOL ASC) ";
	
	private static final String SELECT_ALL_SQL = 
			" SELECT SYMBOL FROM WATCHLIST ORDER BY SYMBOL";
	
	private static final String SELECT_EXISTS_SQL = 
			" SELECT 1 FROM WATCHLIST WHERE SYMBOL = ?";
	
	private static final String INSERT_SQL = "INSERT INTO WATCHLIST VALUES (?)";
	
	private static final String DELETE_IND_SQL = "DELETE FROM WATCHLIST WHERE SYMBOL = ?";
	
	public WatchListDAO(Connection con) {
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
	
	public boolean exists(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_EXISTS_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		boolean exists = false;
		if (rs.next()){
			exists = true;
		}
		return exists;
	}
	
	public List<String> getWatchList() throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_ALL_SQL);
		ResultSet rs = st.executeQuery();
		List<String> l = new ArrayList<String>();
		while (rs.next()){
			String s = rs.getString("SYMBOL").trim();
			l.add(s);
		}
		return l;
	}
	
	public void delete(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_IND_SQL);
		st.setString(1, symbol);
		st.executeUpdate();
	}

	public void insert(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, symbol);
		st.executeUpdate();
	}
}
