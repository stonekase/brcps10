package com.sourcard.helpers;
import java.sql.*;


public class connectionpgsql implements provider {
	
	static Connection conn = null;
	public static Connection getCon()
	{
		try
		{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(connURL,username,password);
			
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		return conn;
	}

}
