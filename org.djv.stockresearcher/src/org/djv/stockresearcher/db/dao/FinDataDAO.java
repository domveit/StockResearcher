package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.FinKeyData;

public class FinDataDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS FINDATA "
			+ "("
			+ "SYMBOL CHAR(20), "
			+ "PERIOD CHAR(20), "
			+ "YEAR INTEGER, "
			+ "REVENUE DECIMAL(13, 2), "
			+ "GROSS_MARGIN DECIMAL (9, 2), "
			+ "OPERATING_INCOME DECIMAL(13, 2), "
			+ "OPERATING_MARGIN DECIMAL(13, 2), "
			+ "NET_INCOME DECIMAL(13, 2), "
			+ "EPS DECIMAL(9, 2), "
			+ "DIVIDENDS DECIMAL(9, 2), "
			+ "PAYOUT_RATIO DECIMAL(9, 2), "
			+ "SHARES DECIMAL(13, 2), "
			+ "BOOK_VALUE_PS DECIMAL(9, 2), "
			+ "OPERATING_CF DECIMAL(13, 2), "
			+ "CAP_SPENDING DECIMAL(13, 2), "
			+ "FREE_CF DECIMAL(13, 2), "
			+ "FREE_CF_PS DECIMAL(9, 2), "
			+ "WORKING_CAP DECIMAL(13, 2) "
			+ ")";
	
	private static final String SELECT_SQL = 
			"SELECT "
			+ "SYMBOL, "
			+ "PERIOD, "
			+ "YEAR, "
			+ "REVENUE, "
			+ "GROSS_MARGIN, "
			+ "OPERATING_INCOME, "
			+ "OPERATING_MARGIN, "
			+ "NET_INCOME, "
			+ "EPS, "
			+ "DIVIDENDS, "
			+ "PAYOUT_RATIO, "
			+ "SHARES, "
			+ "BOOK_VALUE_PS, "
			+ "OPERATING_CF, "
			+ "CAP_SPENDING, "
			+ "FREE_CF, "
			+ "FREE_CF_PS, "
			+ "WORKING_CAP "
			+ "FROM FINDATA WHERE SYMBOL = ?"
			+ " ORDER BY PERIOD DESC";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS FINDATAIX1 ON FINDATA (SYMBOL ASC, PERIOD ASC) ";
	
	private static final String INSERT_SQL = "INSERT INTO FINDATA VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String DELETE_SQL = "DELETE FROM FINDATA WHERE SYMBOL = ?";
	
	public FinDataDAO(Connection con) {
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
	
	public List<FinKeyData> getFinDataForStock(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		List<FinKeyData> l = new ArrayList<FinKeyData>();
		while (rs.next()){
			FinKeyData fpd = new FinKeyData();
			fpd.setSymbol(rs.getString("SYMBOL").trim());
			fpd.setBookValuePerShare(rs.getBigDecimal("BOOK_VALUE_PS"));
			fpd.setCapitalSpending(rs.getBigDecimal("CAP_SPENDING"));
			fpd.setDividends(rs.getBigDecimal("DIVIDENDS"));
			fpd.setEarningsPerShare(rs.getBigDecimal("EPS"));
			fpd.setFreeCashFlow(rs.getBigDecimal("FREE_CF"));
			fpd.setFreeCashFlowPerShare(rs.getBigDecimal("FREE_CF_PS"));
			fpd.setGrossMargin(rs.getBigDecimal("GROSS_MARGIN"));
			fpd.setNetIncome(rs.getBigDecimal("NET_INCOME"));
			fpd.setOperatingCashFlow(rs.getBigDecimal("OPERATING_CF"));
			fpd.setOperatingIncome(rs.getBigDecimal("OPERATING_INCOME"));
			fpd.setOperatingMargin(rs.getBigDecimal("OPERATING_MARGIN"));
			fpd.setPayoutRatio(rs.getBigDecimal("PAYOUT_RATIO"));
			fpd.setPeriod(rs.getString("PERIOD"));
			fpd.setRevenue(rs.getBigDecimal("REVENUE"));
			fpd.setShares(rs.getBigDecimal("SHARES"));
			fpd.setWorkingCapital(rs.getBigDecimal("BOOK_VALUE_PS"));
			fpd.setPeriod(rs.getString("PERIOD"));
			fpd.setRevenue(rs.getBigDecimal("REVENUE"));
			fpd.setShares(rs.getBigDecimal("SHARES"));
			fpd.setBookValuePerShare(rs.getBigDecimal("WORKING_CAP"));
			fpd.setYear(rs.getInt("YEAR"));
			l.add(fpd);
		}
		return l;
	}
	
	public void insert(FinKeyData fpd) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, fpd.getSymbol());
		st.setString(2, fpd.getPeriod());
		st.setInt(3, fpd.getYear());
		st.setBigDecimal(4, fpd.getRevenue());
		st.setBigDecimal(5, fpd.getGrossMargin());
		
		st.setBigDecimal(6, fpd.getOperatingIncome());
		st.setBigDecimal(7, fpd.getOperatingMargin());
		st.setBigDecimal(8, fpd.getNetIncome());
		st.setBigDecimal(9, fpd.getEarningsPerShare());
		st.setBigDecimal(10, fpd.getDividends());
		st.setBigDecimal(11, fpd.getPayoutRatio());
		st.setBigDecimal(12, fpd.getShares());
		st.setBigDecimal(13, fpd.getBookValuePerShare());
		st.setBigDecimal(14, fpd.getOperatingCashFlow());
		st.setBigDecimal(15, fpd.getCapitalSpending());
		st.setBigDecimal(16, fpd.getFreeCashFlow());
		st.setBigDecimal(17, fpd.getFreeCashFlowPerShare());
		st.setBigDecimal(18, fpd.getWorkingCapital());
		st.executeUpdate();
	}
	
	public void deleteForStock(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setString(1, symbol);
		st.executeUpdate();
	}
}
