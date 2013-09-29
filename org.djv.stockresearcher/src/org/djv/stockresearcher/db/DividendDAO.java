package org.djv.stockresearcher.db;

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
			SELECT_SQL
			+ "WHERE SYMBOL = ? ORDER BY PAYDATE DESC";
	
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
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(SELECT_SYMBOL_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		List<DivData> l = new ArrayList<DivData>();
		while (rs.next()){
			DivData dd = new DivData();
			dd.setSymbol(rs.getString("SYMBOL"));
			dd.setPaydate(rs.getDate("PAYDATE"));
			dd.setDividend(rs.getBigDecimal("DIVIDEND"));
			l.add(dd);
		}
//		long end = System.currentTimeMillis();
//		System.err.println("DividendDAO.getDividendsForSymbol " + (end-beg));
		return l;
	}
	
	public Date getLastDividendOnFileForSymbol(String symbol) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(SELECT_SYMBOL_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		Date d = null;
		if (rs.next()){
			d  = rs.getDate("PAYDATE");
		}
//		long end = System.currentTimeMillis();
//		System.err.println("DividendDAO.getLastDividendOnFileForSymbol " + (end-beg));
		return d;
	}
	
	public void deleteForStock(String symbol) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setString(1, symbol);
		st.executeUpdate();
//		long end = System.currentTimeMillis();
//		System.err.println("DividendDAO.deleteForStock " + (end-beg));
	}

	public void insert(DivData dd) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, dd.getSymbol());
		st.setDate(2, dd.getPaydate());
		st.setBigDecimal(3, dd.getDividend());
		st.executeUpdate();
//		long end = System.currentTimeMillis();
//		System.err.println("DividendDAO.insert " + (end-beg));
	}
	
//	public void update(Stock s) throws Exception {
//		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
//		st.setInt(1, s.getIndustryId());
//		st.setDate(2, s.getDataDate());
//		st.setString(3, s.getExchange());
//		st.setString(4, s.getName());
//		st.setBigDecimal(5, s.getPrice());
//		st.setString(6, s.getMarketCap());
//		st.setBigDecimal(7, s.getDividend());
//		st.setBigDecimal(8, s.getYield());
//		st.setBigDecimal(9, s.getPe());
//		st.setBigDecimal(10, s.getPeg());
//		
//		st.setString(11, s.getSymbol());
//		st.executeUpdate();
//	}
	

}
