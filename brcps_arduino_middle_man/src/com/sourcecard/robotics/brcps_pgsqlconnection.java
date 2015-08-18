package com.sourcecard.robotics;

import java.sql.Connection;
import java.sql.DriverManager;

public class brcps_pgsqlconnection implements brcps_provider {
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
