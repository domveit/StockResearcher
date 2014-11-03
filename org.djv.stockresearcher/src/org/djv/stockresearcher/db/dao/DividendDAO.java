package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.DivData;

public class DividendDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS DIVIDEND "
			+ "("
			+ "SYMBOL CHAR(20), "
			+ "PAYDATE DATE, "
			+ "DIVIDEND DECIMAL(13, 6) ) ";
	
	private static final String SELECT_SQL = 
			"SELECT SYMBOL, PAYDATE, DIVIDEND FROM DIVIDEND ";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS DIVIDENDIX1 ON DIVIDEND (SYMBOL ASC, PAYDATE DESC) ";
	
	private static final String SELECT_SYMBOL_SQL = 
			"SELECT A.SYMBOL, A.PAYDATE, DIVIDEND, ADJDATE, ADJDIV FROM DIVIDEND A "
			+ " LEFT OUTER JOIN ADJUSTEDDIV B ON A.SYMBOL = B.SYMBOL AND A.PAYDATE = B.PAYDATE " + 
			" WHERE A.SYMBOL = ? ORDER BY A.PAYDATE DESC";
	
	private static final String INSERT_SQL = "INSERT INTO DIVIDEND VALUES (?, ?, ?)";
	
	private static final String DELETE_SQL = "DELETE FROM DIVIDEND WHERE SYMBOL = ?";
	
	public DividendDAO(Connection con) {
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
	
	public List<DivData> getDividendsForSymbol(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SYMBOL_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		List<DivData> l = new ArrayList<DivData>();
		while (rs.next()){
			DivData dd = new DivData();
			dd.setSymbol(rs.getString("SYMBOL"));
			dd.setPaydate(rs.getDate("PAYDATE"));
			dd.setDividend(rs.getBigDecimal("DIVIDEND"));
			
			dd.setAdjustedDate(rs.getDate("ADJDATE"));
			dd.setAdjustedDividend(rs.getBigDecimal("ADJDIV"));
			l.add(dd);
		}
		return l;
	}
	
	public Date getLastDividendOnFileForSymbol(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SYMBOL_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		Date d = null;
		if (rs.next()){
			d  = rs.getDate("PAYDATE");
		}
		return d;
	}
	
	public void deleteForStock(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setString(1, symbol);
		st.executeUpdate();
	}

	public void insert(DivData dd) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, dd.getSymbol());
		st.setDate(2, dd.getPaydate());
		st.setBigDecimal(3, dd.getDividend());
		st.executeUpdate();
	}
	

}
