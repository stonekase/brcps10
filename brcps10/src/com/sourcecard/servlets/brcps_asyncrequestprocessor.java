package com.sourcecard.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpSession;

import com.sourcard.helpers.brcps_databasequery;
import com.sourcard.helpers.brcps_helpers;

public class brcps_asyncrequestprocessor implements Runnable{
	
	//private variables
	AsyncContext asyncCtx;
	long subscriber_msisdn;
	String input;
	HttpSession session;
	int bank_code;
	long account_no;
	

	public brcps_asyncrequestprocessor(AsyncContext asyncCtx,HttpSession session) {
		
		this.asyncCtx = asyncCtx;
		this.session = session;
		this.subscriber_msisdn = Long.parseLong(asyncCtx.getRequest().getParameter("msisdn"));
		this.input = (String)asyncCtx.getRequest().getParameter("input");
	}

	@Override
	public void run() 
	{
		System.out.println("Async Supported? "+ asyncCtx.getRequest().isAsyncSupported());
		System.out.println("msisdn:"+asyncCtx.getRequest().getParameter("msisdn"));
	 	System.out.println("input: "+asyncCtx.getRequest().getParameter("input"));
		try {
			if(session.isNew() && !input.equals(brcps_helpers.shortcodeInput))
			{
				System.out.println("typing an invlid input to the app");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Invalid input"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				asyncCtx.complete();
				if (session != null) {
				    session.invalidate();
				}
				return;
				
			}
			else
			{
				respondRequest(subscriber_msisdn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//verification function
	private void respondRequest(long msisdn) throws SQLException
	{
		
		if(session.getAttribute("inputA")== null && session.getAttribute("inputB")== null)
		{
			ResultSet rs = brcps_databasequery.verifyCustomer(msisdn);
			
			//long sub_no = rs.getLong("msisdn");
			if(rs.isBeforeFirst())
			{
				System.out.println("entered all null1");
				System.out.println("customer is subscribed to service");
				try {
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("1:Sell Airtime \n2:Change bank \n3:Help \n4:unsubscribe");
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 1);
				//complete the processing
				asyncCtx.complete();
				return;
			}
			else
			{
				System.out.println("entered all null2");
				System.out.println("customer is not subscribed to service");
				try {
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("1:Subscribe \n2:Help \n3:Quit"); 
				} catch (IOException e) { 
					e.printStackTrace();
				}
				
				session.setAttribute("inputB",1);
				//complete the processing
				asyncCtx.complete();
				return;
			}//endotron
		}
	//=========================================================SECTION A IN CODE===================================================================	
		//this starts a whole new thread of its own
		if (session.getAttribute("inputA")!= null && !session.isNew())/** this loop got me worked up : careful with the loops**/
		{
			System.out.println("entered inputA , Session:"+(int)session.getAttribute("inputA"));
			
			if((int)session.getAttribute("inputA")==1 && input.equals("1"))
			{
				System.out.println("you have choosen to sell Airtime");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("you have choosen to sell Airtime:not configured"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (session != null) {
				    session.invalidate();
				}
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputA")==1 && input.equals("2"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg1:"); 
					out.println("1:GTB \n2:Citibank \n3:Diamond Bank \n4:EcoBank \n5:Fidelity Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 22);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputA")==22 && input.equals("0"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg2:"); 
					out.println("6:First Bank \n7:FCMB \n8:FSDH \n9:GTB \n10:Heritage Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 32);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputA")==32 && input.equals("0"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg3:"); 
					out.println("11:Keystone Bank \n12:Skye \n13:Stanbic IBTC \n14:Sterling Bank \n15:Union Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 42);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputA")==42 && input.equals("0"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg4:"); 
					out.println("16:UBA \n17:Unity Bank \n18:Wema Bank \n19:Zenith Bank  \n00:Prev page");
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 52);
				asyncCtx.complete();
				return;
			}//now the reverse check for 00---------------------------------------------------------------------------------------
			else if((int)session.getAttribute("inputA")==52 && input.equals("00"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg3:"); 
					out.println("11:Keystone Bank \n12:Skye \n13:Stanbic IBTC \n14:Sterling Bank \n15:Union Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 42);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputA")==42 && input.equals("00"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg2:"); 
					out.println("6:First Bank \n7:FCMB \n8:FSDH \n9:GTB \n10:Heritage Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 32);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputA")==32 && input.equals("00"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg1:"); 
					out.println("1:GTB \n2:Citibank \n3:Diamond Bank \n4:EcoBank \n5:Fidelity Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 22);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputA")==32 && input.equals("00"))
			{
				System.out.println("you have choosen to change bank account info");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg1:"); 
					out.println("1:GTB \n2:Citibank \n3:Diamond Bank \n4:EcoBank \n5:Fidelity Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 22);
				asyncCtx.complete();
				return;
			}else if((int)session.getAttribute("inputA")==22 && input.equals("00"))
			{
				System.out.println("customer is subscribed to service");
				try {
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("1:Sell Airtime \n2:Change bank \n3:Help \n4:unsubscribe");
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputA", 1);
				//complete the processing
				asyncCtx.complete();
				return;
			}//---------------------------------------------------------------------------------------------------------------------
			else if((int)session.getAttribute("inputA")==1 && input.equals("3"))
			{
				System.out.println("you have choosen to receive help sms");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Invalid input"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				asyncCtx.complete();
				return;
			} 
			else if((int)session.getAttribute("inputA")==1 && input.equals("4"))
			{
				System.out.println("you have choosen to unsubscribe");
				try 
				{ 
					int numRow = brcps_databasequery.deleteFromSubscribers(subscriber_msisdn);
					if (numRow == 0)
					{
						PrintWriter out = asyncCtx.getResponse().getWriter();
						out.println("unable to unsubscribe user");
					}
					else
					{
						int numRows2 = brcps_databasequery.deleteFromAccountDetails(subscriber_msisdn);
						if(numRows2 == 0)
						{
							PrintWriter out = asyncCtx.getResponse().getWriter();
							out.println("unable to unsubscribe user");
						}
						else
						{
							//we will send the customer unsubscribe sms
							PrintWriter out = asyncCtx.getResponse().getWriter();
							out.println("Successfully unsubscribed from the service"); 
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				asyncCtx.complete();
				if (session != null) {
				    session.invalidate();
				}
				return;
			}
			else 
			{
				System.out.println("you typed in an invalid input,last else loop");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Invalid input");  
				} catch (IOException e) {
					e.printStackTrace();
				}
				asyncCtx.complete();
				if (session != null) {
				    session.invalidate();
				}
				return;
			}
			
		}//===============================================================SECTION B IN CODE============================================================
		else if(session.getAttribute("inputB")!= null && !session.isNew())/** start of inputB if-else expansion**/
		{
			System.out.println("I did enter here inputB mainloop, Session: "+(int)session.getAttribute("inputB"));
//--------------------------------------------------------------------------------------------------------------------------------------
			//converting every inner input back to the normal input
			if((int)session.getAttribute("inputB")== 3 && !input.equals("0") && !input.equals("00") && input.length() !=10)
			{
				session.setAttribute("inputB",2);
			}
			if((int)session.getAttribute("inputB")== 4 && !input.equals("0") && !input.equals("00") && input.length() !=10)
			{
				session.setAttribute("inputB",2);
			}
//----------------------------------------------------------------------------------------------------------------------------------------------		
			
			if((int)session.getAttribute("inputB")==1 && input.equals("00"))
			{
				System.out.println("typing an invlid input to the app");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Invalid input"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				asyncCtx.complete();
				if (session != null) {
				    session.invalidate();
				}
				return;
			}
			else if((int)session.getAttribute("inputB")==2 && input.equals("00"))
			{
				System.out.println("entered inputB0 entered");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("1:Subscribe \n2:Help \n3:Quit"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 1);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")== 3 && input.equals("00"))
			{
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg1:");
					out.println("1:GTB \n2:Citibank \n3:Diamond Bank \n4:EcoBank \n5:Fidelity Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 2);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")==4 && input.equals("00"))
			{
				try
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg2:");
					out.println("6:First Bank \n7:FCMB \n8:FSDH \n9:GTB \n10:Heritage Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 3);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")==5 && input.equals("00"))
			{
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg3:");
					out.println("11:Keystone Bank \n12:Skye \n13:Stanbic IBTC \n14:Sterling Bank \n15:Union Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 4);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")==1 && input.equals("0"))
			{
				System.out.println("entered inputB0 entered");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("1:Subscribe \n2:Help \n3:Quit"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 1);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")==1 && input.equals("1"))
			{
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg1:");
					out.println("1:GTB \n2:Citibank \n3:Diamond Bank \n4:EcoBank \n5:Fidelity Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 2);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")== 2 && input.equals("0"))
			{
				try
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg2:");
					out.println("6:First Bank \n7:FCMB \n8:FSDH \n9:GTB \n10:Heritage Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 3);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")== 3 && input.equals("0"))
			{
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg3:");
					out.println("11:Keystone Bank \n12:Skye \n13:Stanbic IBTC \n14:Sterling Bank \n15:Union Bank \n0:Next Page \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 4);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")== 4 && input.equals("0"))
			{
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please choose Bank pg4:");
					out.println("16:UBA \n17:Unity Bank \n18:Wema Bank \n19:Zenith Bank  \n00:Prev page"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 5);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")== 1 && input.equals("2"))
			{
				try
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("An SMS has been sent to you.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				asyncCtx.complete();
				if (session != null) {
				    session.invalidate();
				}
				return;
			}
			else if((int)session.getAttribute("inputB")== 1 && input.equals("3"))
			{
				if (session != null) {
				    session.invalidate();
				}
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")== 2 && !input.equals("0"))
			{
				//setting the input here to the bank code
				//bank_code = input;
				session.setAttribute("bank_code",input);
				
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Please type bank bvn No:");
				} catch (IOException e) {
					e.printStackTrace();
				}
				session.setAttribute("inputB", 3);
				asyncCtx.complete();
				return;
			}
			else if((int)session.getAttribute("inputB")== 3 && !input.equals("0"))
			{
				//copy input into the account_no variable
				if(!input.equals("0") && input.length() == 10)
				{
					int statusCode1,statusCode2;
					
					bank_code = Integer.parseInt((String)session.getAttribute("bank_code"));
					account_no = Long.parseLong(input);
					
					//i need to check if this guy was really inserted into the first DB table
					//before i proceed to the seond DB table
					statusCode1 = brcps_databasequery.insertIntoSubscribers(subscriber_msisdn);
					if(statusCode1 == 0)
					{
						//the insert failed so therefore abort mission
						try 
						{
							PrintWriter out = asyncCtx.getResponse().getWriter();
							out.println("Subscription Failed,Please try Again!!");
						} catch (IOException e) {
							e.printStackTrace();
						}
						asyncCtx.complete();
						if (session != null) {
						    session.invalidate();
						} 
						return;
					}
					else
					{
						System.out.println("Msisdn: "+subscriber_msisdn+" , Bank Code : "+bank_code+ " ,Account No : "+account_no);
						statusCode2 = brcps_databasequery.insertIntoAccountDetails(subscriber_msisdn, bank_code, account_no); 
						if(statusCode2 == 0)
						{
							int statusCode3;
							//remove the previously inserted field from tbl_subscribers in the database
							statusCode3 = brcps_databasequery.deleteFromSubscribers(subscriber_msisdn);
							if(statusCode3 == 0)
							{
								System.out.println("Msisdn: "+subscriber_msisdn+" Did not delete from tbl_subscribers,please delete manually");
							}
							else
							{
								System.out.println("Msisdn: "+subscriber_msisdn+" deleted from the subscriber table");
							}
							
							try 
							{
								PrintWriter out = asyncCtx.getResponse().getWriter();
								out.println("Subscription Failed,Try again");
							} catch (IOException e) {
								e.printStackTrace();
							}
							asyncCtx.complete();
							if (session != null) {
							    session.invalidate();
							}
							return;
						}
						else
						{
							try
							{
								PrintWriter out = asyncCtx.getResponse().getWriter();
								out.println("Successful Subscription,congrats!!!");
							} catch (IOException e) {
								e.printStackTrace();
							}
							asyncCtx.complete();
							if (session != null) {
							    session.invalidate();
							}
							return;
						}
					}
				}
				else
				{
					try
					{
						PrintWriter out = asyncCtx.getResponse().getWriter();
						out.println("Invalid bank Account number.");
					} catch (IOException e) {
						e.printStackTrace();
					}
					asyncCtx.complete();
					if (session != null) {
					    session.invalidate();
					}
					return;
				}
				
			}
			else
			{
				System.out.println("typing an invlid input to the app");
				try 
				{
					PrintWriter out = asyncCtx.getResponse().getWriter();
					out.println("Invalid input"); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				asyncCtx.complete();
				if (session != null) {
				    session.invalidate();
				}
				return;
			}
		}/** The end of inputB if-else expansion.pheeew **/
		else
		{
			System.out.println("typing an invlid input to the app");
			try 
			{
				PrintWriter out = asyncCtx.getResponse().getWriter();
				out.println("Invalid input"); 
			} catch (IOException e) {
				e.printStackTrace();
			}
			asyncCtx.complete();
			if (session != null) {
			    session.invalidate();
			}
			return;
		}
	}
}
