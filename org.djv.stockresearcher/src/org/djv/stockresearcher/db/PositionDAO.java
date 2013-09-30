package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.Stock;

public class PositionDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE POSITION IF NOT EXISTS STOCK "
			+ "("
			+ "PORTFOLIO_ID INTEGER, "
			+ "SYMBOL CHAR(20), "
			+ "SHARES DECIMAL(16, 4), "
			+ "PRICEPAID DECIMAL(11, 4), "
			+ "DATEPAID DATE "
			+ ")";
	
	private static final String SELECT_SQL = 
			"SELECT SYMBOL, SHARES, PRICEPAID, FINDATADATE, SYMBOL, EXCHANGE, NAME, PRICE, MARKETCAP, DIVIDEND, YIELD, PE, PEG FROM STOCK ";
	
	private static final String INDEX1_SQL = 
			"CREATE INDEX IF NOT EXISTS STOCKIX1 ON STOCK (INDID ASC) ";
	
	private static final String INDEX2_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS STOCKIX2 ON STOCK (SYMBOL ASC) ";
	
	private static final String SELECT_IND_SQL = 
			SELECT_SQL
			+ "WHERE INDID = ?";
	
	private static final String SELECT_SYMBOL_SQL = 
			SELECT_SQL
			+ "WHERE SYMBOL = ?";
	
	private static final String SELECT_EXISTS = 
			"SELECT 1 FROM STOCK WHERE SYMBOL = ?";
	
	private static final String INSERT_SQL = "INSERT INTO STOCK VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE_SQL = 
			"UPDATE STOCK SET "
			+ "INDID = ?, "
			+ "DATADATE = ?, "
			+ "DIVDATADATE = ?, "
			+ "FINDATADATE = ?, "
			+ "EXCHANGE = ?, "
			+ "NAME = ?, "
			+ "PRICE = ?, "
			+ "MARKETCAP = ?, "
			+ "DIVIDEND = ?, "
			+ "YIELD = ?, "
			+ "PE = ?, "
			+ "PEG = ? "
			+ "WHERE SYMBOL = ?";
	
	private static final String DELETE_IND_SQL = "DELETE FROM STOCK WHERE INDID = ?";
	
//	String symbol;
//	String exchange;
//	String name;
//	String price;
//	String marketCap;
//	String dividend;
//	Double yield;
//	String pe;
//	String peg;
	
	public PositionDAO(Connection con) {
		super(con);
	}

	public void createTableIfNotExists() throws Exception {
		PreparedStatement st = con.prepareStatement(CREATE_SQL);
		st.executeUpdate();
		st.close();
		
		PreparedStatement ist = con.prepareStatement(INDEX1_SQL);
		ist.executeUpdate();
		ist.close();
		
		PreparedStatement ist2 = con.prepareStatement(INDEX2_SQL);
		ist2.executeUpdate();
		ist2.close();
	}
	
	public List<Stock> getStocksForIndustry(int ind) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(SELECT_IND_SQL);
		st.setInt(1, ind);
		ResultSet rs = st.executeQuery();
		List<Stock> l = new ArrayList<Stock>();
		while (rs.next()){
			Stock s = new Stock();
			s.setSymbol(rs.getString("SYMBOL").trim());
			s.setDividend(rs.getBigDecimal("DIVIDEND"));
			s.setExchange(rs.getString("EXCHANGE"));
			s.setMarketCap(rs.getString("MARKETCAP"));
			s.setName(rs.getString("NAME"));
			s.setPe(rs.getBigDecimal("PE"));
			s.setPeg(rs.getBigDecimal("PEG"));
			s.setPrice(rs.getBigDecimal("PRICE"));
			s.setYield(rs.getBigDecimal("YIELD"));
			s.setDataDate(rs.getDate("DATADATE"));
			s.setDivDataDate(rs.getDate("DIVDATADATE"));
			s.setFinDataDate(rs.getDate("FINDATADATE"));
			s.setIndustryId(rs.getInt("INDID"));
			l.add(s);
		}
//		long end = System.currentTimeMillis();
//		System.err.println("StockDAO.getStocksForIndustry " + (end-beg));
		return l;
	}
	
	public boolean exists(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_EXISTS);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		boolean exists = false;
		if (rs.next()){
			exists = true;
		}
		return exists;
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
			s.setName(rs.getString("NAME"));
			s.setPe(rs.getBigDecimal("PE"));
			s.setPeg(rs.getBigDecimal("PEG"));
			s.setPrice(rs.getBigDecimal("PRICE"));
			s.setYield(rs.getBigDecimal("YIELD"));
			s.setDataDate(rs.getDate("DATADATE"));
			s.setDivDataDate(rs.getDate("DIVDATADATE"));
			s.setFinDataDate(rs.getDate("FINDATADATE"));
			s.setIndustryId(rs.getInt("INDID"));
		}
		return s;
	}
	
//	public List<Stock> getStock(String symbol) throws Exception {
//		PreparedStatement st = con.prepareStatement(SELECT_SYMBOL_SQL);
//		st.setString(1, symbol);
//		ResultSet rs = st.executeQuery();
//		List<Stock> l = new ArrayList<Stock>();
//		while (rs.next()){
//			Stock s = new Stock();
//			s.setSymbol(rs.getString("SYMBOL").trim());
//			s.setDividend(rs.getBigDecimal("DIVIDEND"));
//			s.setExchange(rs.getString("EXCHANGE"));
//			s.setMarketCap(rs.getString("MARKETCAP"));
//			s.setName(rs.getString("NAME"));
//			s.setPe(rs.getBigDecimal("PE"));
//			s.setPeg(rs.getBigDecimal("PEG"));
//			s.setPrice(rs.getBigDecimal("PRICE"));
//			s.setYield(rs.getBigDecimal("YIELD"));
//			s.setDataDate(rs.getDate("DATADATE"));
//			s.setDivDataDate(rs.getDate("DIVDATADATE"));
//			s.setFinDataDate(rs.getDate("FINDATADATE"));
//			s.setIndustryId(rs.getInt("INDID"));
//			l.add(s);
//		}
//		return l;
//	}
	
	public void deleteStocksForIndustry(int ind) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(DELETE_IND_SQL);
		st.setInt(1, ind);
		st.executeUpdate();
//		long end = System.currentTimeMillis();
//		System.err.println("StockDAO.deleteStocksForIndustry " + (end-beg));
	}

	public void insert(Stock s) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setInt(1, s.getIndustryId());
		st.setDate(2, s.getDataDate());
		st.setDate(3, s.getDivDataDate());
		st.setDate(4, s.getFinDataDate());
		st.setString(5, s.getSymbol());
		st.setString(6, s.getExchange());
		st.setString(7, s.getName());
		st.setBigDecimal(8, s.getPrice());
		st.setString(9, s.getMarketCap());
		st.setBigDecimal(10, s.getDividend());
		st.setBigDecimal(11, s.getYield());
		st.setBigDecimal(12, s.getPe());
		st.setBigDecimal(13, s.getPeg());
		st.executeUpdate();
//		long end = System.currentTimeMillis();
//		System.err.println("StockDAO.insert " + (end-beg));
	}
	
	public void update(Stock s) throws Exception {
//		long beg = System.currentTimeMillis();
		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
		st.setInt(1, s.getIndustryId());
		st.setDate(2, s.getDataDate());
		st.setDate(3, s.getDivDataDate());
		st.setDate(4, s.getFinDataDate());
		st.setString(5, s.getExchange());
		st.setString(6, s.getName());
		st.setBigDecimal(7, s.getPrice());
		st.setString(8, s.getMarketCap());
		st.setBigDecimal(9, s.getDividend());
		st.setBigDecimal(10, s.getYield());
		st.setBigDecimal(11, s.getPe());
		st.setBigDecimal(12, s.getPeg());
		
		st.setString(13, s.getSymbol());
		st.executeUpdate();
//		long end = System.currentTimeMillis();
//		System.err.println("StockDAO.update " + (end-beg));
	}
	

}
