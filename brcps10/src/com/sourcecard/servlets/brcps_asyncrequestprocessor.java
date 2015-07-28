package com.sourcecard.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpSession;
import com.sourcard.helpers.analyseMenus;
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
			//this guy contains the menu class
			if(session.getAttribute("CustomerMenu")== null && session.getAttribute("SubscriptionMenu")== null)
			{
				ResultSet rs = brcps_databasequery.verifyCustomer(subscriber_msisdn);
				
				//long sub_no = rs.getLong("msisdn");
				try{
					if(rs.isBeforeFirst())
					{
						System.out.println("customer is subscribed to service");
						try{
							PrintWriter out = asyncCtx.getResponse().getWriter();
							out.println("1:Sell Airtime \n2:Change bank \n3:Help \n4:unsubscribe");
						} catch (IOException e) {
							e.printStackTrace();
						}
						session.setAttribute("CustomerMenu", "customermenu");
						session.setAttribute("MenuName","mainmenu");
						session.setAttribute("MenuLevel","levelzero");
						//complete the processing
						asyncCtx.complete();
						return;
					}
					else
					{
						System.out.println("customer is not subscribed to service");
						try {
							PrintWriter out = asyncCtx.getResponse().getWriter();
							out.println("1:Subscribe \n2:Help \n3:Quit"); 
						} catch (IOException e) { 
							e.printStackTrace();
						}
							
						session.setAttribute("SubscriptionMenu","subscriptionmenu");
						session.setAttribute("MenuName","mainmenu");
						session.setAttribute("MenuLevel","levelzero");
						//complete the processing
						asyncCtx.complete();
						return;
					}
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}//endotron
			}
		}
		
		//express thy_self
		if((String)session.getAttribute("SubscriptionMenu")=="subscriptionmenu")
		{
			//analyse subscription menu
			analyseMenus.SubscriptionMenu(subscriber_msisdn,(String)session.getAttribute("SubscriptionMenu"),
					session,asyncCtx,input);
		}
		else if((String)session.getAttribute("CustomerMenu")=="customermenu")
		{
			//analyse customer menu 
			analyseMenus.CustomerMenu(subscriber_msisdn,(String)session.getAttribute("CustomerMenu"),
					session,asyncCtx,input);
		}
	}

}
