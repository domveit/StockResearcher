package org.djv.stockresearcher.db.dao;

import java.sql.Connection;

public class H2DAO {

	Connection con = null;
	
	public H2DAO(Connection con){
		this.con = con;
	}
}
