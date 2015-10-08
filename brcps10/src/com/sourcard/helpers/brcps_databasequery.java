package com.sourcard.helpers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class brcps_databasequery {
	
	static Connection conn ;
	static PreparedStatement pst;
	static Statement stmt = null;
	static ResultSet rs = null;
	
	public static int deleteFromTransactions(String tablename,long msisdn,long transaction_id)
	{
		int numRowsChanged = 0;
		conn = connectionpgsql.getCon();
		
		try {
			
			conn.setAutoCommit(false);
			String sql ="DELETE FROM "+ tablename+" WHERE msisdn = ? and transaction_id = ?";
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setLong(1,msisdn);
			pst.setLong(2,transaction_id);
			numRowsChanged = pst.executeUpdate();
			pst.close();
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return numRowsChanged;
	}
	
	
	public static int moveTransactionToPassed(long transaction_id,int transaction_status)
	{
		int response = 0;
		Connection conn1 = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		PreparedStatement pst3 = null;
		
		conn1 = connectionpgsql.getCon();
		try {
			conn1.setAutoCommit(false);
			
			String sql2 ="UPDATE tbl_transactions_pending set transaction_status = ? WHERE transaction_id = ?";
			//create the prepared statement to fight against injection to my code
			pst2 =  conn1.prepareStatement(sql2);
			pst2.setInt(1,transaction_status);
			pst2.setLong(2,transaction_id);
			pst2.executeUpdate();
			
			String sql1 ="INSERT into tbl_transactions_passed(msisdn,amount,transaction_time,transaction_status,transaction_id,cashout_amount) "
					+ "select msisdn,amount,transaction_time,transaction_status,transaction_id,cashout_amount from tbl_transactions_pending where transaction_id="+transaction_id;
			//create the prepared statement to fight against injection to my code
			pst1 =  conn1.prepareStatement(sql1);
			pst1.executeUpdate();
			
			String sql3 ="DELETE FROM tbl_transactions_pending WHERE transaction_id = ?";
			pst3 =  conn1.prepareStatement(sql3);
			pst3.setLong(1,transaction_id);
			pst3.executeUpdate();
			
			response = 1;
			conn1.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn1.rollback();
				conn1.close();
				response = 0;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		finally
		{
		     try 
		     {
		    	 if (pst1 != null) 
				 {
					 pst1.close();
			     }
			     if (pst2 != null) {
			        pst2.close();
			     }
			     if (pst3 != null) {
			        pst3.close();
			     }
				conn1.setAutoCommit(true);
		     } 
		     catch (SQLException e) 
		     {
				 e.printStackTrace();
			 }
		}
		return response;
	}
	public static int moveTransactionToFailed(long transaction_id,int transaction_status)
	{
		int numRowsChanged = 0;
		Connection conn1 = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		PreparedStatement pst3 = null;
		
		conn1 = connectionpgsql.getCon();
		try {
			conn1.setAutoCommit(false);
			
			String sql2 ="UPDATE tbl_transactions_pending set transaction_status = ? WHERE transaction_id = ?";
			//create the prepared statement to fight against injection to my code
			pst2 =  conn1.prepareStatement(sql2);
			pst2.setInt(1,transaction_status);
			pst2.setLong(2,transaction_id);
			pst2.executeUpdate();
			
			String sql1 ="INSERT into tbl_transactions_failed(msisdn,amount,transaction_time,transaction_status,transaction_id,cashout_amount) "
					+ "select msisdn,amount,transaction_time,transaction_status,transaction_id,cashout_amount from tbl_transactions_pending where transaction_id="+transaction_id;
			//create the prepared statement to fight against injection to my code
			pst1 =  conn1.prepareStatement(sql1);
			pst1.executeUpdate();
			
			String sql3 ="DELETE FROM tbl_transactions_pending WHERE transaction_id = ?";
			pst3 =  conn1.prepareStatement(sql3);
			pst3.setLong(1,transaction_id);
			pst3.executeUpdate();
			
			numRowsChanged = 1;
			conn1.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn1.rollback();
				conn1.close();
				numRowsChanged = 0;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		finally
		{
		     try 
		     {
		    	 if (pst1 != null) 
				 {
					 pst1.close();
			     }
			     if (pst2 != null) {
			        pst2.close();
			     }
			     if (pst3 != null) {
			        pst3.close();
			     }
				conn1.setAutoCommit(true);
		     } 
		     catch (SQLException e) 
		     {
				 e.printStackTrace();
			 }
		}
		return numRowsChanged;
	}
	public static ResultSet verifyCustomer(long msisdn)
	{
		try
		{
			conn.setAutoCommit(false);
			conn = connectionpgsql.getCon();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT msisdn FROM tbl_subscribers where msisdn ="+msisdn);
			conn.commit();
			conn.close();
		}
		catch(Exception ex)
		{
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(ex);
		}
		return rs;
	}
}
