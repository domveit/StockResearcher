package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NameMapDAO extends H2DAO{
	
	private static final String CREATE_SQL 		= "CREATE TABLE IF NOT EXISTS NAMEMAP (INDID INTEGER, NAME CHAR(100), SYMBOL CHAR(20))";
	private static final String SELECT_SQL 		= "SELECT SYMBOL FROM NAMEMAP WHERE NAME = ?";
	private static final String SELECT_IND_SQL 	= "SELECT INDID FROM NAMEMAP WHERE SYMBOL = ?";
	private static final String INSERT_SQL		= "INSERT INTO NAMEMAP VALUES (?, ?, ?)";
	private static final String DELETE_SQL 		= "DELETE FROM NAMEMAP WHERE INDID = ?";
	
	
	private static final String INDEX1_SQL = 
			"CREATE INDEX IF NOT EXISTS NAMEMAPIX1 ON NAMEMAP (NAME ASC) ";
	
	private static final String INDEX2_SQL = 
			"CREATE INDEX IF NOT EXISTS NAMEMAPIX2 ON NAMEMAP (INDID ASC) ";
	
	public NameMapDAO(Connection con) {
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
	
	public String getSymbolForName(String name) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setString(1, name);
		ResultSet rs = st.executeQuery();
		String sym = null;
		if (rs.next()){
			sym = rs.getString("SYMBOL");
		}
		st.close();
		return sym;
	}
	
	
	public Integer getIndustryForSymbol(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_IND_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		Integer ind = 0;
		if (rs.next()){
			ind = rs.getInt("INDID");
		}
		st.close();
		return ind;
	}
	
	public void clearIndustry(int ind) throws Exception {
		PreparedStatement st = con.prepareStatement(DELETE_SQL);
		st.setInt(1, ind);
		st.executeUpdate();
	}
	
	public void insert(int ind, String name, String sym) throws Exception {
		PreparedStatement st2 = con.prepareStatement(INSERT_SQL);
		st2.setInt(1, ind);
		st2.setString(2, name);
		st2.setString(3, sym);
		st2.executeUpdate();
	}

}
