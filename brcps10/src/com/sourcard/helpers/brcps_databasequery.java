package com.sourcard.helpers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

public class brcps_databasequery {
	public static int deleteFromTransactions(String tablename,long msisdn,long transaction_id)
	{
		Connection conn= null ;
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
			} catch (SQLException e1) 
			{
				e1.printStackTrace();
			}
		}
		return numRowsChanged;
	}
	public static ResultSet verifyCustomerDetails(long msisdn)
	{
		Connection conn = null ;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = connectionpgsql.getCon();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM tbl_subscribers where msisdn ="+msisdn);
			conn.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		return rs;
	}
	
	//========================delicate respone table===========================
	public static int insertIntoTransactionResponse(String responseCode,String mac,String transactionDate,
			String transactionReference,String transferCode,String msisdn)
	{
		Connection conn= null;
		int numRowsChanged = 0;
		conn = connectionpgsql.getCon();
		try {
			String sql ="INSERT into tbl_transaction_response(response_code,mac,transaction_date,transaction_reference,transfer_code,msisdn,created_date) VALUES(?,?,?,?,?,?,?)";
			//current timestamp
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setString(1,responseCode);
			pst.setString(2,mac);
			pst.setString(3,transactionDate);
			pst.setString(4,transactionReference);
			pst.setString(5,transferCode);
			pst.setString(6,msisdn);
			pst.setTimestamp(7,currentTimestamp);
			
			numRowsChanged = pst.executeUpdate();
			pst.close();
			conn.close();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return numRowsChanged;
	}
	//==================================the movement guys==========================================
	public static int moveTransactionToFailed(long transaction_id,int transaction_status)
	{
		Connection conn1 = null;
		int numRowsChanged = 0;
		conn1 = connectionpgsql.getCon();
		try {
			//current timestamp
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			String sql ="WITH moved_rows AS (DELETE FROM tbl_transactions_pending a"
					+ " WHERE a.transaction_id="+transaction_id
					+" RETURNING a.id,a.msisdn,a.amount,a.transaction_time,"+transaction_status+",a.transaction_id,a.cashout_amount,"+"'"+currentTimestamp+"'::timestamp)"
					+" INSERT INTO tbl_transactions_failed"
					+" SELECT * FROM moved_rows";
			System.out.println("failed query::"+sql);
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn1.prepareStatement(sql);
			numRowsChanged = pst.executeUpdate();
			pst.close();
			conn1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRowsChanged;
	}
	public static int moveTransactionToReconcile(long transaction_id,int transaction_status)
	{
		Connection conn1 = null;
		int numRowsChanged = 0;
		conn1 = connectionpgsql.getCon();
		try {
			//current timestamp
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			String sql ="WITH moved_rows AS (DELETE FROM tbl_transactions_pending a"
					+ " WHERE a.transaction_id="+transaction_id
					+" RETURNING a.id,a.msisdn,a.amount,a.transaction_time,"+transaction_status+",a.transaction_id,a.cashout_amount,"+"'"+currentTimestamp+"'::timestamp)"
					+" INSERT INTO tbl_transactions_reconcile"
					+" SELECT * FROM moved_rows";
			
			System.out.println("reconcile query::"+sql);
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn1.prepareStatement(sql);
			numRowsChanged = pst.executeUpdate();
			pst.close();
			conn1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRowsChanged;
	}
	public static int moveTransactionToPassed(long transaction_id,int transaction_status)
	{
		Connection conn1 = null;
		int numRowsChanged = 0;
		conn1 = connectionpgsql.getCon();
		try {
			//current timestamp
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			String sql ="WITH moved_rows AS (DELETE FROM tbl_transactions_pending a"
					+ " WHERE a.transaction_id="+transaction_id
					+" RETURNING a.id,a.msisdn,a.amount,a.transaction_time,"+transaction_status+",a.transaction_id,a.cashout_amount,"+"'"+currentTimestamp+"'::timestamp)"
					+" INSERT INTO tbl_transactions_passed"
					+" SELECT * FROM moved_rows";
			
			System.out.println("passed query::"+sql);
			//create the prepared statement to fight against injection to my code
			PreparedStatement pst =  conn1.prepareStatement(sql);
			numRowsChanged = pst.executeUpdate();
			pst.close();
			conn1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRowsChanged;
	}
}
