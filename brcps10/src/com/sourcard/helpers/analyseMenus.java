package com.sourcard.helpers;

import java.io.IOException;
import java.io.PrintWriter;

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
					//session.setAttribute("inputB", 2);
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
	
		
	}
}
