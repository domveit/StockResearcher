package org.djv.stockresearcher.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.StockIndustry;

public class StockIndustryDAO extends H2DAO {
	
	private static final String CREATE_SQL 		= "CREATE TABLE IF NOT EXISTS STOCKINDUSTRY (INDID INTEGER, NAME CHAR(100), SYMBOL CHAR(20))";
	private static final String SELECT_SQL 		= 	"SELECT INDID, NAME FROM STOCKINDUSTRY WHERE SYMBOL = ?";
	
	private static final String SELECT_IND_SQL 		= 	"SELECT NAME, SYMBOL FROM STOCKINDUSTRY WHERE INDID = ?";
	
	private static final String INSERT_SQL		= "INSERT INTO STOCKINDUSTRY VALUES (?, ?, ?)";
	private static final String UPDATE_SQL		= "UPDATE STOCKINDUSTRY SET INDID = ?, NAME = ? WHERE SYMBOL = ?";
	private static final String DELETE_SQL 		= "DELETE FROM STOCKINDUSTRY WHERE SYMBOL = ?";
	
	private static final String INDEX1_SQL = 
			"CREATE INDEX IF NOT EXISTS STOCKINDUSTRYIX1 ON STOCKINDUSTRY (NAME ASC) ";
	
	private static final String INDEX2_SQL = 
			"CREATE INDEX IF NOT EXISTS STOCKINDUSTRYIX2 ON STOCKINDUSTRY (INDID ASC) ";
	
	private static final String INDEX3_SQL = 
			"CREATE UNIQUE INDEX IF NOT EXISTS STOCKINDUSTRYIX3 ON STOCKINDUSTRY (SYMBOL ASC) ";
	
	public StockIndustryDAO(Connection con) {
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
		
		PreparedStatement ist3 = con.prepareStatement(INDEX3_SQL);
		ist3.executeUpdate();
		ist3.close();
	}
	
	public StockIndustry select(String symbol) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setString(1, symbol);
		ResultSet rs = st.executeQuery();
		StockIndustry si = null;
		if (rs.next()){
			si = new StockIndustry();
			si.setIndId(rs.getInt("INDID"));
			si.setSymbol(symbol);
			si.setName(rs.getString("NAME"));
		}
		st.close();
		return si;
	}
	
	public void insert(StockIndustry si) throws Exception {
		PreparedStatement st2 = con.prepareStatement(INSERT_SQL);
		st2.setInt(1, si.getIndId());
		st2.setString(2, si.getName());
		st2.setString(3, si.getSymbol());
		st2.executeUpdate();
	}
	
	public void update(StockIndustry si) throws Exception {
		PreparedStatement st2 = con.prepareStatement(UPDATE_SQL);
		st2.setInt(1, si.getIndId());
		st2.setString(2, si.getName());
		st2.setString(3, si.getSymbol());
		st2.executeUpdate();
	}
	
	public void delete(String symbol) throws Exception {
		PreparedStatement st2 = con.prepareStatement(DELETE_SQL);
		st2.setString(1, symbol);
		st2.executeUpdate();
	}

	public List<StockIndustry> getAllForIndustry(int ind) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_IND_SQL);
		st.setInt(1, ind);
		ResultSet rs = st.executeQuery();
		List<StockIndustry> list = new ArrayList<StockIndustry>();
		while (rs.next()){
			StockIndustry si = new StockIndustry();
			si.setIndId(ind);
			si.setSymbol(rs.getString("SYMBOL"));
			si.setName(rs.getString("NAME"));
			list.add(si);
		}
		st.close();
		return list;
	}

}
