package com.sourcecard.robotics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class brcps_databasequery {
	static Connection conn ;
	static PreparedStatement pst;
	static Statement stmt = null;
	static ResultSet rs = null;
	
	public static ResultSet verifyCustomerDetails(long msisdn)
	{
		try
		{
			conn = brcps_pgsqlconnection.getCon();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT msisdn FROM tbl_subscribers where msisdn ="+msisdn);
			conn.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		return rs;
	}

}
