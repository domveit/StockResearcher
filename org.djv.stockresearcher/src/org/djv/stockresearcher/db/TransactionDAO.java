package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.Transaction;

public class TransactionDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS TRANSACTION "
			+ "("
			+ "ID INTEGER, "
			+ "PORT_ID INTEGER, "
			+ "ACTION CHAR(1), "
			+ "SYMBOL CHAR(20), "
			+ "SHARES DECIMAL(16, 4), "
			+ "PRICE DECIMAL(11, 4), "
			+ "TRANDATE DATE "
			+ ")";
	
	private static final String SELECT_SQL = 
			"SELECT ID, PORT_ID, ACTION, SYMBOL, SHARES, PRICE, TRANDATE FROM TRANSACTION ";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS TRANIX1 ON TRANSACTION (ID ASC) ";
	
	private static final String INDEX2_SQL = 
			"CREATE INDEX IF NOT EXISTS STOCKIX2 ON TRANSACTION (PORT_ID ASC) ";
	
	private static final String SELECT_PORT_SQL = 
			SELECT_SQL
			+ "WHERE PORT_ID = ?";
	
	private static final String SELECT_ID_SQL = 
			SELECT_SQL
			+ "WHERE ID = ?";
	
	private static final String INSERT_SQL = "INSERT INTO TRANSACTION VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE_SQL = 
			"UPDATE TRANSACTION SET "
			+ "PORT_ID = ?, "
			+ "ACTION = ?, "
			+ "SYMBOL = ?, "
			+ "SHARES = ?, "
			+ "PRICE = ?, "
			+ "TRANDATE = ? "
			+ "WHERE ID = ?";
	
	private static final String DELETE_SQL = "DELETE FROM TRANSACTION WHERE ID = ?";
	
	private static final String DELETE_PORT_SQL = "DELETE FROM TRANSACTION WHERE PORT_ID = ?";
	
	private static final String NEXTID_SQL = 
			"SELECT COALESCE(MAX(ID), 0) + 1 as MAXID FROM TRANSACTION ";
	
	
	public TransactionDAO(Connection con) {
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
	
	public List<Transaction> getTransactionsForPortfolio(int portId) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_PORT_SQL);
		st.setInt(1, portId);
		ResultSet rs = st.executeQuery();
		List<Transaction> l = new ArrayList<Transaction>();
		while (rs.next()){
			Transaction t = new Transaction();
			t.setTranDate(rs.getDate("TRANDATE"));
			t.setId(rs.getInt("ID"));
			t.setPortId(rs.getInt("PORT_ID"));
			t.setPrice(rs.getBigDecimal("PRICE"));
			t.setShares(rs.getBigDecimal("SHARES"));
			t.setAction(rs.getString("ACTION"));
			l.add(t);
		}
		return l;
	}
	
	public Transaction select(int id) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_ID_SQL);
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		Transaction t = null;
		if (rs.next()){
			t = new Transaction();
			t.setTranDate(rs.getDate("TRANDATE"));
			t.setId(rs.getInt("ID"));
			t.setPortId(rs.getInt("PORT_ID"));
			t.setPrice(rs.getBigDecimal("PRICE"));
			t.setShares(rs.getBigDecimal("SHARES"));
			t.setAction(rs.getString("ACTION"));
		}
		return t;
	}
	
	public void deleteAllForPortfolio(int portId) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_PORT_SQL);
		st.setInt(1, portId);
		st.executeUpdate();
	}
	
	public void delete(int id) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setInt(1, id);
		st.executeUpdate();
	}

	public void insert(Transaction t) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setInt(1, t.getId());
		st.setInt(2, t.getPortId());
		st.setString(3, t.getAction());
		st.setString(4, t.getSymbol());
		st.setBigDecimal(5, t.getShares());
		st.setBigDecimal(6, t.getPrice());
		st.setDate(7, t.getTranDate());
		st.executeUpdate();
	}
	
	public void update(Transaction t) throws Exception {
		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
		st.setInt(1, t.getPortId());
		st.setString(2, t.getAction());
		st.setString(3, t.getSymbol());
		st.setBigDecimal(4, t.getShares());
		st.setBigDecimal(5, t.getPrice());
		st.setDate(6, t.getTranDate());
		st.setInt(7, t.getId());
		st.executeUpdate();
	}
	
	public Integer getNextId() throws Exception {
		PreparedStatement st = con.prepareStatement(NEXTID_SQL);
		ResultSet rs = st.executeQuery();
		Integer max = null;
		if (rs.next()){
			max = (rs.getInt("MAXID"));
		}
		return max;
	}

	

}
