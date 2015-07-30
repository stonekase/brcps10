package com.sourcard.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpSession;
public class analyseMenus {
	
	public static void CustomerMenu(long msisdn,String menuName,HttpSession session,AsyncContext asyncCtx,String input)
	{
		//using the switch statement to analyse the request
		try
		{
			PrintWriter out = asyncCtx.getResponse().getWriter();
			out.println("Not Configured!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		asyncCtx.complete();
		if (session != null) {
		    session.invalidate();
		}
		return;
	}
	public static void SubscriptionMenu(long Msisdn,String menuName,HttpSession session,AsyncContext asyncCtx,String input)
	{
		//using the switch statement to analyse the request
		if (((String)session.getAttribute("MenuName")).equals("mainmenu") && ((String)session.getAttribute("MenuLevel")).equals("levelzero") )
		{
			switch(input)
			{
				case "1":
				{
					try 
					{
						PrintWriter out = asyncCtx.getResponse().getWriter();
						out.println("Please choose Bank pg1:");
						out.println("1:GTB \n2:Citibank \n3:Diamond Bank \n4:EcoBank \n5:Fidelity Bank \n0:Next Page \n00:Prev page"); 
					} catch (IOException e) {
						e.printStackTrace();
					}
					session.setAttribute("MenuName", "bankpg1");
					session.setAttribute("MenuLevel", "levelone");
					asyncCtx.complete();
					return;
				}
				case "2":
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
				case "3":
				{
					try
					{
						PrintWriter out = asyncCtx.getResponse().getWriter();
						out.println("Application exiting.");
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (session != null) {
					    session.invalidate();
					}
					asyncCtx.complete();
					return;
				}
				default :
				{
					try
					{
						PrintWriter out = asyncCtx.getResponse().getWriter();
						out.println("Invalid Input");
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
		else if(((String)session.getAttribute("MenuName")).equals("bankpg1") &&((String)session.getAttribute("MenuLevel")).equals("levelone"))
		{
			switch(input)
			{
				case "0":
				{
					try 
					{
						PrintWriter out = asyncCtx.getResponse().getWriter();
						out.println("Please choose Bank pg2:"); 
						out.println("6:First Bank \n7:FCMB \n8:FSDH \n9:GTB \n10:Heritage Bank \n0:Next Page \n00:Prev page"); 
					} catch (IOException e) {
						e.printStackTrace();
					}
					session.setAttribute("MenuName", "bankpg2");
					session.setAttribute("MenuLevel", "levelone");
					asyncCtx.complete();
					return;
				}
				case "00":
				{
					ResultSet rs = brcps_databasequery.verifyCustomer(Msisdn);
					
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
				case "1":
				{
					setBank(session,asyncCtx,input);
				}
				case "2":
				{
					setBank(session,asyncCtx,input);
				}
				case "3":
				{
					setBank(session,asyncCtx,input);
				}
				case "4":
				{
					setBank(session,asyncCtx,input);
				}
				case "5":
				{
					setBank(session,asyncCtx,input);
				}
				default:
				{
					try
					{
						PrintWriter out = asyncCtx.getResponse().getWriter();
						out.println("Invalid Input");
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
	
		
	}
	
	private static void setBank(HttpSession session,AsyncContext asyncCtx,String input)
	{
		session.setAttribute("bank_code",input);
		
		try 
		{
			PrintWriter out = asyncCtx.getResponse().getWriter();
			out.println("Please type bank bvn No:");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		session.setAttribute("MenuName","mainmenu");
		session.setAttribute("MenuLevel","levelzero");
		asyncCtx.complete();
		return;
	}
}
