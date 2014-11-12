package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.AnalystRatings;
import org.djv.stockresearcher.model.DivData;

public class AnalystRatingsDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS ANALYSTRATINGS "
			+ "("
			+ "SYMBOL CHAR(20), "
			+ "DATADATE DATE, "
			+ "FIVEYRGROWTH DECIMAL(7, 2), "
			+ "AVGRATING DECIMAL(3, 2), "
			+ "STRBUY_NBR INTEGER, "
			+ "BUY_NBR INTEGER, "
			+ "HOLD_NBR INTEGER, "
			+ "SELL_NBR INTEGER, "
			+ "STRSELL_NBR INTEGER )";
	
//	public Date dataDate;
//	public BigDecimal fiveYearGrowthForcast;
//	public BigDecimal averageRating;
//	public Integer strongBuyRatings;
//	public Integer buyRatings;
//	public Integer holdRatings;
//	public Integer sellRatings;
//	public Integer strongSellRatings;
	
	private static final String SELECT_SQL = 
			"SELECT SYMBOL, DATADATE, FIVEYRGROWTH, AVGRATING, STRBUY_NBR, BUY_NBR, HOLD_NBR, SELL_NBR, STRSELL_NBR FROM ANALYSTRATINGS WHERE SYMBOL = ?";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS ANALYSTRATINGSIX1 ON ANALYSTRATINGS (SYMBOL ASC) ";
	
	private static final String INSERT_SQL = "INSERT INTO ANALYSTRATINGS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE_SQL = "UPDATE ANALYSTRATINGS SET DATADATE=?, FIVEYRGROWTH=?, AVGRATING=?, STRBUY_NBR=?, BUY_NBR=?, HOLD_NBR=?, SELL_NBR=?, STRSELL_NBR=? WHERE SYMBOL = ?";
	
	private static final String DELETE_SQL = "DELETE FROM ANALYSTRATINGS WHERE SYMBOL = ?";
	
	public AnalystRatingsDAO(Connection con) {
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
	
	public AnalystRatings select(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		AnalystRatings ar = null;
		if (rs.next()){
			ar = new AnalystRatings();
			ar.setSymbol(rs.getString("SYMBOL"));
			ar.setDataDate(rs.getDate("DATADATE"));
			ar.setFiveYearGrowthForcast(rs.getBigDecimal("FIVEYRGROWTH"));
			ar.setAverageRating(rs.getBigDecimal("AVGRATING"));
			ar.setStrongBuyRatings(rs.getInt("STRBUY_NBR"));
			ar.setBuyRatings(rs.getInt("BUY_NBR"));
			ar.setHoldRatings(rs.getInt("HOLD_NBR"));
			ar.setSellRatings(rs.getInt("STRSELL_NBR"));
			ar.setStrongSellRatings(rs.getInt("SELL_NBR"));
		}
		return ar;
	}
	
	public void delete(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setString(1, symbol);
		st.executeUpdate();
	}

	public void insert(AnalystRatings ar) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, ar.getSymbol());
		st.setDate(2, ar.getDataDate());
		st.setBigDecimal(3, ar.getFiveYearGrowthForcast());
		st.setBigDecimal(4,  ar.getAverageRating());
		st.setInt(5, ar.getStrongBuyRatings());
		st.setInt(6, ar.getBuyRatings());
		st.setInt(7, ar.getHoldRatings());
		st.setInt(8, ar.getSellRatings());
		st.setInt(9, ar.getStrongSellRatings());
		st.executeUpdate();
	}
	
	public void update(AnalystRatings ar) throws Exception {
		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
		
		st.setDate(1, ar.getDataDate());
		st.setBigDecimal(2, ar.getFiveYearGrowthForcast());
		st.setBigDecimal(3,  ar.getAverageRating());
		st.setInt(4, ar.getStrongBuyRatings());
		st.setInt(5, ar.getBuyRatings());
		st.setInt(6, ar.getHoldRatings());
		st.setInt(7, ar.getSellRatings());
		st.setInt(8, ar.getStrongSellRatings());
		
		st.setString(9, ar.getSymbol());
		st.executeUpdate();
	}
	

}
