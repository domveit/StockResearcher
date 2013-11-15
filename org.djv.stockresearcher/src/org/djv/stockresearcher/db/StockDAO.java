package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.djv.stockresearcher.model.Stock;

public class StockDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS STOCK "
			+ "("
			+ "SYMBOL CHAR(20), "
			+ "DATADATE DATE, "
			+ "DIVDATADATE DATE, "
			+ "FINDATADATE DATE, "
			+ "EXCHANGE CHAR(20), "
			+ "PRICE DECIMAL(11, 4), "
			+ "MARKETCAP CHAR(20), "
			+ "DIVIDEND DECIMAL(13, 6), "
			+ "YIELD DECIMAL(9, 2), "
			+ "PE DECIMAL(9, 2), "
			+ "PEG DECIMAL(9,2), "
			+ "YRHIGH DECIMAL(11, 4), "
			+ "YRLOW DECIMAL(11, 4) "
			+ ")";
	
	private static final String SELECT_SQL = 
			"SELECT SYMBOL, DATADATE, DIVDATADATE, FINDATADATE, EXCHANGE, PRICE, MARKETCAP, DIVIDEND, YIELD, PE, PEG, YRHIGH, YRLOW FROM STOCK ";
	
	private static final String INDEX2_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS STOCKIX2 ON STOCK (SYMBOL ASC) ";
	
	private static final String SELECT_SYMBOL_SQL = 
			SELECT_SQL
			+ "WHERE SYMBOL = ?";
	
	private static final String INSERT_SQL = "INSERT INTO STOCK VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE_SQL = 
			"UPDATE STOCK SET "
			+ "DATADATE = ?, "
			+ "DIVDATADATE = ?, "
			+ "FINDATADATE = ?, "
			+ "EXCHANGE = ?, "
			+ "PRICE = ?, "
			+ "MARKETCAP = ?, "
			+ "DIVIDEND = ?, "
			+ "YIELD = ?, "
			+ "PE = ?, "
			+ "PEG = ? "
			+ "YRHIGH = ?, "
			+ "YRLOW = ? "
			+ "WHERE SYMBOL = ?";
	
	public StockDAO(Connection con) {
		super(con);
	}

	public void createTableIfNotExists() throws Exception {
		PreparedStatement st = con.prepareStatement(CREATE_SQL);
		st.executeUpdate();
		st.close();
		
		PreparedStatement ist2 = con.prepareStatement(INDEX2_SQL);
		ist2.executeUpdate();
		ist2.close();
	}
	
	public Stock select(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SYMBOL_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		Stock s = null;
		if (rs.next()){
			s = new Stock();
			s.setSymbol(rs.getString("SYMBOL").trim());
			s.setDividend(rs.getBigDecimal("DIVIDEND"));
			s.setExchange(rs.getString("EXCHANGE"));
			s.setMarketCap(rs.getString("MARKETCAP"));
			s.setPe(rs.getBigDecimal("PE"));
			s.setPeg(rs.getBigDecimal("PEG"));
			s.setPrice(rs.getBigDecimal("PRICE"));
			s.setYield(rs.getBigDecimal("YIELD"));
			s.setDataDate(rs.getDate("DATADATE"));
			s.setDivDataDate(rs.getDate("DIVDATADATE"));
			s.setFinDataDate(rs.getDate("FINDATADATE"));
			s.setYearHigh(rs.getBigDecimal("YRHIGH"));
			s.setYearLow(rs.getBigDecimal("YRLOW"));
		}
		return s;
	}
	
	public void insert(Stock s) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, s.getSymbol());
		st.setDate(2, s.getDataDate());
		st.setDate(3, s.getDivDataDate());
		st.setDate(4, s.getFinDataDate());
		st.setString(5, s.getExchange());
		st.setBigDecimal(6, s.getPrice());
		st.setString(7, s.getMarketCap());
		st.setBigDecimal(8, s.getDividend());
		st.setBigDecimal(9, s.getYield());
		st.setBigDecimal(10, s.getPe());
		st.setBigDecimal(11, s.getPeg());
		st.setBigDecimal(12, s.getYearHigh());
		st.setBigDecimal(13, s.getYearLow());
		st.executeUpdate();
	}
	
	public void update(Stock s) throws Exception {
		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
		st.setDate(1, s.getDataDate());
		st.setDate(2, s.getDivDataDate());
		st.setDate(3, s.getFinDataDate());
		st.setString(4, s.getExchange());
		st.setBigDecimal(5, s.getPrice());
		st.setString(6, s.getMarketCap());
		st.setBigDecimal(7, s.getDividend());
		st.setBigDecimal(8, s.getYield());
		st.setBigDecimal(9, s.getPe());
		st.setBigDecimal(10, s.getPeg());
		st.setBigDecimal(11, s.getYearHigh());
		st.setBigDecimal(12, s.getYearLow());
		
		st.setString(13, s.getSymbol());
		st.executeUpdate();
	}
	

}
