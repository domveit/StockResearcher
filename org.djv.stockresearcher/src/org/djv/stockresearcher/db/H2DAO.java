package org.djv.stockresearcher.db;

import java.sql.Connection;

public class H2DAO {

	Connection con = null;
	
	public H2DAO(Connection con){
		this.con = con;
	}
}
