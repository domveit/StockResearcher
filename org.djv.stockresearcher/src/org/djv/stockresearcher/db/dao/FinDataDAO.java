package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.FinDataDTO;

public class FinDataDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS FINDATA "
			+ "("
			+ "SYMBOL CHAR(20), "
			+ "TYPE CHAR(1), "
			+ "DATADATE DATE, "
			+ "SEQ INTEGER, "
			+ "FIELDNAME CHAR(100), "
			+ "VALUE DECIMAL(13, 2), "
			+ ")";
	
	private static final String SELECT_SQL = 
			"SELECT "
			+ "DATADATE DATE, "
			+ "SEQ, "
			+ "FIELDNAME, "
			+ "VALUE "
			+ " FROM FINDATA "
			+ " WHERE SYMBOL = ? AND TYPE = ? "
			+ " ORDER BY DATADATE DESC";
	
	private static final String INDEX1_SQL = "CREATE UNIQUE INDEX IF NOT EXISTS FINDATAIX1 ON FINDATA (SYMBOL ASC, TYPE ASC, DATADATE ASC, SEQ ASC) ";
	private static final String INSERT_SQL = "INSERT INTO FINDATA VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE FINDATA SET FIELDNAME = ?, VALUE = ? WHERE SYMBOL = ? AND TYPE = ? AND DATADATE = ? AND SEQ = ? ";
	private static final String DELETE_SQL = "DELETE FROM FINDATA WHERE SYMBOL = ? AND TYPE = ? AND DATADATE = ? AND SEQ = ? ";
	private static final String DELETE_ALL_SQL = "DELETE FROM FINDATA WHERE SYMBOL = ?";
	
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

	public List<FinDataDTO> searchStockAndType(String symbol, String type) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setString(1, symbol);
		st.setString(2, type);
		ResultSet rs = st.executeQuery();
		List<FinDataDTO>  list = new ArrayList<FinDataDTO>();
		
		while (rs.next()){
			FinDataDTO finData = new FinDataDTO();
			finData.setSymbol(symbol);
			finData.setType(type);
			finData.setDataDate(rs.getDate("DATADATE"));
			finData.setSeq(rs.getInt("SEQ"));
			finData.setFieldName(rs.getString("FIELDNAME"));
			finData.setValue(rs.getBigDecimal("VALUE"));
			list.add(finData);
		}
		return list;
	}
	
	public void insert(FinDataDTO dto) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setString(1, dto.getSymbol());
		st.setString(2, dto.getType());
		st.setDate(3, dto.getDataDate());
		st.setInt(4, dto.getSeq());
		st.setString(5, dto.getFieldName());
		st.setBigDecimal(6, dto.getValue());
		st.executeUpdate();
	}
	
	public int update(FinDataDTO dto) throws Exception {
		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
		st.setString(1, dto.getFieldName());
		st.setBigDecimal(2, dto.getValue());
		st.setString(3, dto.getSymbol());
		st.setString(4, dto.getType());
		st.setDate(5, dto.getDataDate());
		st.setInt(6, dto.getSeq());
		
		return st.executeUpdate();
	}
	
	public void delete(FinDataDTO dto) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setString(1, dto.getSymbol());
		st.setString(2, dto.getType());
		st.setDate(3, dto.getDataDate());
		st.setInt(4, dto.getSeq());
		st.executeUpdate();
	}
	
	public void deleteAll(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_ALL_SQL);
		st.setString(1, symbol);
		st.executeUpdate();
	}
	

}
