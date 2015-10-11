package com.sourcecard.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpSession;
import org.apache.http.HttpStatus;
import com.sourcard.helpers.brcps_databasequery;
import com.sourcard.helpers.brcps_helpers;

public class brcps_asyncrequestprocessor implements Runnable{
	//private variables
	AsyncContext asyncCtx;
	long transactionId;
	String receipient_msisdn;
	HttpSession session;
	long transfer_amount;
	long account_no;
	int bank_code;
	//transaction tag
	final String USER = "brcps";
	
	public brcps_asyncrequestprocessor(AsyncContext asyncCtx,HttpSession session) {
		this.asyncCtx = asyncCtx;
		this.session = session;
		this.transactionId = Long.parseLong(asyncCtx.getRequest().getParameter("transactionId"));
		this.receipient_msisdn = (String)asyncCtx.getRequest().getParameter("receipient_msisdn");
		this.transfer_amount = Long.parseLong(asyncCtx.getRequest().getParameter("transfer_amount"));
		this.account_no = Long.parseLong(asyncCtx.getRequest().getParameter("account_no"));
		this.bank_code = Integer.parseInt(asyncCtx.getRequest().getParameter("bank_code"));
	}
	@Override
	public void run() 
	{
		System.out.println("beginning to run");
		//now we check if all variables are ok then we send a request to interswitch
		//on-success we send a request to interswitch(DONE on debug response)
		//after one gets a success then a confirmation sms is sent to the customer
		 if(transactionId != 0 && receipient_msisdn != null && transfer_amount !=0)
		 {
			 try {
					PrintWriter out = asyncCtx.getResponse().getWriter();
					int statusCode = HttpStatus.SC_OK; //brcps_helpers.sendRequestFunding(transactionId, receipient_msisdn, transfer_amount);
					if (statusCode == HttpStatus.SC_OK)
					{
						int response = brcps_databasequery.moveTransactionToPassed(transactionId, 0);
						if(response == 1)
						{
							System.out.println("successfully moved and updated transaction");
							//brcps_helpers.sendSms(receipient_msisdn,brcps_helpers.transactionComplete());
						}
						else
						{
							//this needs to be logged and send delayed sms
							System.out.println("failed to move and update transaction");
						}
					}
					else if(statusCode == HttpStatus.SC_GATEWAY_TIMEOUT)
					{
						System.out.println("Http request timeout");
						//brcps_helpers.sendSms(receipient_msisdn,brcps_helpers.delayTransaction(""+transfer_amount,""+bank_code,""+account_no));
						//now move that transaction from the pending to the failed transactions
						int response = brcps_databasequery.moveTransactionToFailed(transactionId, 1);
						if(response == 1)
						{
							System.out.println("successfully moved to failed transactions");
							//send delay sms
							//brcps_helpers.sendSms(receipient_msisdn,brcps_helpers.delayTransaction(""+transfer_amount,""+bank_code,""+account_no));
						}
						else
						{
							//this needs to be logged and send delayed sms
							System.out.println("failed to move and update transaction");
							//brcps_helpers.sendSms(receipient_msisdn,brcps_helpers.delayTransaction(""+transfer_amount,""+bank_code,""+account_no));
						}
					}
					else
					{
						System.out.println("for some reason it did not go through");
						int response = brcps_databasequery.moveTransactionToFailed(transactionId, 1);
						if(response == 1)
						{
							System.out.println("successfully moved to failed transactions");
							//send delay sms
							//brcps_helpers.sendSms(receipient_msisdn,brcps_helpers.delayTransaction(""+transfer_amount,""+bank_code,""+account_no));
						}
						else
						{
							//this needs to be logged and send delayed sms
							System.out.println("failed to move and update transaction");
							//brcps_helpers.sendSms(receipient_msisdn,brcps_helpers.delayTransaction(""+transfer_amount,""+bank_code,""+account_no));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			asyncCtx.complete();
		 }
	}
}
