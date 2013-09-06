package org.djv.stockresearcher.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class StockH2DB {
	
	public StockH2DB(){
		createDatabase();
	}

	private void createDatabase() {
        try
        {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/stockDB", "test", "" );
            Statement stmt = con.createStatement();
            stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS stock ( symbol char(15) )" );
 
            ResultSet rs = stmt.executeQuery("SELECT * FROM table1");
            while( rs.next() )
            {
                String name = rs.getString("user");
                System.out.println( name );
            }
            stmt.close();
            con.close();
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage() );
        }
	}

}
