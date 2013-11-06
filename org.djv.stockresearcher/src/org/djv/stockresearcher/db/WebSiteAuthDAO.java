package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.djv.stockresearcher.model.WebSiteAuth;

public class WebSiteAuthDAO extends H2DAO{
	
	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS WEBSITEAUTH (HOST CHAR(100), USERNAME CHAR(100), PASS_ENC CHAR(1000), TWOFACTORSCHEME CHAR(3))";
	private static final String SELECT_SQL = "SELECT USERNAME, PASS_ENC_CHAR, TWOFACTORSCHEME FROM WEBSITEAUTH WHERE HOST = ?";
	private static final String UPDATE_SQL = "UPDATE WEBSITEAUTH SET USERNAME=?, PASS_ENC_CHAR=?, TWOFACTORSCHEME=? WHERE HOST = ?";
	private static final String INSERT_SQL = "INSERT INTO WEBSITEAUTH VALUES (?, ?, ?, ?)";
	private static final String DELETE_SQL = "DELETE FROM WEBSITEAUTH WHERE HOST = ?";
	private static final String INDEX1_SQL = "CREATE UNIQUE INDEX IF NOT EXISTS WEBSITEAUTHIX1 ON WEBSITEAUTH (HOST ASC) ";
	
	public WebSiteAuthDAO(Connection con) {
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
	
	public WebSiteAuth select(String host) throws Exception {
		PreparedStatement st = con.prepareStatement(SELECT_SQL);
		st.setString(1, host);
		ResultSet rs = st.executeQuery();
		WebSiteAuth o = null;
		if (rs.next()){
			o = new WebSiteAuth();
			o.setPassEnc(rs.getString("PASS_ENC"));
			o.setHost(host);
			o.setTwoFactorScheme(rs.getString("TWOFACTORSCHEME"));
			o.setUserName(rs.getString("USERNAME"));
		}
		st.close();
		return o;
	}
	
	public void insert(WebSiteAuth o) throws Exception {
		PreparedStatement st2 = con.prepareStatement(INSERT_SQL);
		st2.setString(1, o.getHost());
		st2.setString(2, o.getUserName());
		st2.setString(3, o.getPassEnc());
		st2.setString(4, o.getTwoFactorScheme());
		int nbr = st2.executeUpdate();
		if (nbr != 1){
			throw new IllegalStateException("insert did not return 1");
		}
	}
	
	public void update(WebSiteAuth o) throws Exception {
		PreparedStatement st2 = con.prepareStatement(UPDATE_SQL);
		st2.setString(1, o.getUserName());
		st2.setString(2, o.getPassEnc());
		st2.setString(3, o.getTwoFactorScheme());
		st2.setString(4, o.getHost());
		int nbr = st2.executeUpdate();
		if (nbr != 1){
			throw new IllegalStateException("update did not return 1");
		}
	}
	
	public void delete(String host) throws Exception {
		PreparedStatement st2 = con.prepareStatement(DELETE_SQL);
		st2.setString(1, host);
		int nbr = st2.executeUpdate();
		if (nbr != 1){
			throw new IllegalStateException("delete did not return 1");
		}
	}


}
