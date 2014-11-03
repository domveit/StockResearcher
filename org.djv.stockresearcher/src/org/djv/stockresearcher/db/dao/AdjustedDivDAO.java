package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.djv.stockresearcher.model.AdjustedDiv;

public class AdjustedDivDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS ADJUSTEDDIV "
			+ "("
			+ "SYMBOL CHAR(20), "
			+ "PAYDATE DATE, "
			+ "ADJDATE DATE, "
			+ "ADJDIV DECIMAL(13, 6) ) ";
	
	private static final String SELECT_SQL = 
			"SELECT SYMBOL, PAYDATE, ADJDATE, ADJDIV FROM ADJUSTEDDIV ";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS ADJUSTEDDIVIX1 ON ADJUSTEDDIV (SYMBOL ASC, PAYDATE DESC) ";
	
	private static final String SELECT_SYMDATE_SQL = 
			SELECT_SQL
			+ "WHERE SYMBOL = ? AND PAYDATE = ?";
	
	private static final String INSERT_SQL = "INSERT INTO ADJUSTEDDIV VALUES (?, ?, ?, ?)";
	
	private static final String UPDATE_SQL = "UPDATE ADJUSTEDDIV SET ADJDATE = ?, ADJDIV = ? WHERE SYMBOL = ? AND PAYDATE = ? ";
	
	private static final String DELETE_SQL = "DELETE FROM ADJUSTEDDIV WHERE SYMBOL = ? AND PAYDATE = ?";
	
	public AdjustedDivDAO(Connection con) {
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
	
	public AdjustedDiv getAdjustment(String symbol, Date payDate) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SYMDATE_SQL);
		st.setString(1, symbol);
		st.setDate(2, payDate);
		ResultSet rs = st.executeQuery();
		AdjustedDiv div = null;
		while (rs.next()){
			div = new AdjustedDiv();
			div.setSymbol(rs.getString("SYMBOL"));
			div.setPaydate(rs.getDate("PAYDATE"));
			div.setAdjustedDate(rs.getDate("ADJDATE"));
			div.setAdjustedDiv(rs.getBigDecimal("ADJDIV"));
		}
		return div;
	}
	
	public void delete(String symbol, Date payDate) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setString(1, symbol);
		st.setDate(2, payDate);
		st.executeUpdate();
	}

	public void insert(AdjustedDiv dd) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, dd.getSymbol());
		st.setDate(2, dd.getPaydate());
		st.setDate(3, dd.getAdjustedDate());
		st.setBigDecimal(4, dd.getAdjustedDiv());
		st.executeUpdate();
	}
	
	public void update(AdjustedDiv dd) throws Exception {
		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
		st.setDate(1, dd.getAdjustedDate());
		st.setBigDecimal(2, dd.getAdjustedDiv());
		st.setString(3, dd.getSymbol());
		st.setDate(4, dd.getPaydate());
		st.executeUpdate();
	}
	

}
