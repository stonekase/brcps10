package com.sourcard.helpers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

public class brcps_databasequery {
	
	static Connection conn ;
	static PreparedStatement pst;
	static Statement stmt = null;
	static ResultSet rs = null;
	
	public static ResultSet verifyCustomer(long msisdn)
	{
		try
		{
			conn = connectionpgsql.getCon();
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
	
	public static ResultSet selectBank(int bank_code)
	{
		try
		{
			conn = connectionpgsql.getCon();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT bank_name FROM tbl_banks where bank_code ="+ bank_code);
			conn.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		return rs;
	}
	public static int deleteFromSubscribers(long msisdn)
	{
		int numRowsChanged = 0;
		conn = connectionpgsql.getCon();
		try {
			String sql ="DELETE FROM tbl_subscribers WHERE msisdn = ?";
			
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setLong(1,msisdn);
			numRowsChanged = pst.executeUpdate();
			
			pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return numRowsChanged;
	}
	public static int insertIntoSubscribers(long msisdn)
	{
		int numRowsChanged = 0;
		conn = connectionpgsql.getCon();
		try {
			String sql ="INSERT into tbl_subscribers(msisdn,created_time) VALUES(?,?)";
			//current timestamp
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setLong(1,msisdn);
			pst.setTimestamp(2,currentTimestamp);
			numRowsChanged = pst.executeUpdate();
			
			pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRowsChanged;
	}
	public static int insertIntoUnSubscribers(long msisdn)
	{
		int numRowsChanged = 0;
		conn = connectionpgsql.getCon();
		try {
			String sql ="INSERT into tbl_unsubscribers(msisdn,unsubscribe_time) VALUES(?,?)";
			//current timestamp
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setLong(1,msisdn);
			pst.setTimestamp(2,currentTimestamp);
			numRowsChanged = pst.executeUpdate();
			
			pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRowsChanged;
	}
	public static int insertIntoAccountDetails(long msisdn,int bank_code,long account_no)
	{
		int numRowsChanged = 0;
		conn = connectionpgsql.getCon();
		try {
			String sql ="INSERT into tbl_account_details(msisdn,bank_code,account_no,created_time) VALUES(?,?,?,?)";
			
			//current timestamp
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setLong(1,msisdn);
			pst.setInt(2,bank_code);
			pst.setLong(3,account_no);
			pst.setTimestamp(4,currentTimestamp);
			numRowsChanged = pst.executeUpdate();
			
			pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRowsChanged;
	}
	public static int deleteFromAccountDetails(long msisdn)
	{
		int numRowsChanged = 0;
		conn = connectionpgsql.getCon();
		try {
			String sql ="DELETE FROM tbl_account_details WHERE msisdn = ?";
			
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setLong(1,msisdn);
			numRowsChanged = pst.executeUpdate();
			
			pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return numRowsChanged;
	}
	public static int updateAccountDetails(long msisdn,int bank_code,long account_no)
	{
		int numRowsUpdated = 0;
		try {
			conn =  connectionpgsql.getCon();
			stmt = conn.createStatement();
			String sql ="UPDATE tbl_account_details set bank_code="+bank_code+",account_no="+account_no+" where msisdn="+msisdn;
			numRowsUpdated = stmt.executeUpdate(sql);
			conn.close();
			stmt.close();
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numRowsUpdated;
	}
}
