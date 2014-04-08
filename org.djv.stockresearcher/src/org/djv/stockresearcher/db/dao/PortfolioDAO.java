package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.Portfolio;

public class PortfolioDAO extends H2DAO{
	
	private static final String CREATE_SQL = 
			"CREATE TABLE IF NOT EXISTS PORTFOLIO "
			+ "("
			+ "ID INTEGER, "
			+ "NAME CHAR(100) "
			+ ")";
	
	private static final String SELECT_SQL = 
			"SELECT ID, NAME FROM PORTFOLIO ";
	
	private static final String SELECT_NAME_SQL = 
			SELECT_SQL + "WHERE NAME = ? ";
	
	private static final String NEXTID_SQL = 
			"SELECT COALESCE(MAX(ID), 0) + 1 as MAXID FROM PORTFOLIO ";
	
	private static final String INDEX1_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS PORTIX1 ON PORTFOLIO (ID ASC) ";
	
	private static final String INSERT_SQL = "INSERT INTO PORTFOLIO VALUES (?, ?)";
	
	private static final String UPDATE_SQL = 
			"UPDATE PORTFOLIO SET "
			+ "NAME = ? "
			+ "WHERE ID = ?";
	
	private static final String DELETE_ID_SQL = "DELETE FROM PORTFOLIO WHERE ID = ?";
	
	private static final String DELETE_NAME_SQL = "DELETE FROM PORTFOLIO WHERE NAME = ?";
	
	public PortfolioDAO(Connection con) {
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
	
	public List<Portfolio> getAll() throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		ResultSet rs = st.executeQuery();
		List<Portfolio> l = new ArrayList<Portfolio>();
		while (rs.next()){
			Portfolio s = new Portfolio();
			s.setName(rs.getString("NAME").trim());
			s.setId(rs.getInt("ID"));
			l.add(s);
		}
		return l;
	}
	
	public void delete(int ind) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_ID_SQL);
		st.setInt(1, ind);
		st.executeUpdate();
	}

	public void insert(Portfolio p) throws Exception {
		PreparedStatement st = con.prepareStatement(INSERT_SQL);
		st.setInt(1, p.getId());
		st.setString(2, p.getName());
		st.executeUpdate();
	}
	
	public void update(Portfolio p) throws Exception {
		PreparedStatement st = con.prepareStatement(UPDATE_SQL);
		st.setString(1, p.getName());
		st.setInt(2, p.getId());
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

	public Portfolio selectByName(String name) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_NAME_SQL);
		st.setString(1, name);
		ResultSet rs = st.executeQuery();
		Portfolio p = null;
		if (rs.next()){
			p = new Portfolio();
			p.setName(rs.getString("NAME").trim());
			p.setId(rs.getInt("ID"));
		}
		return p;
	}

	public void deleteByName(String name) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_NAME_SQL);
		st.setString(1, name);
		st.executeUpdate();
	}
	

}
