package com.sourcard.helpers;

import com.sourcecard.servlets.brcps_requests;

public interface provider {
	//this interface will pull its files from the config file
	
	//String username = "postgres";
	//String password = "Madscientist1";
	//String connURL = "jdbc:postgresql://localhost:5433/brcps";
//-------------------------------------------------------------------------	
	String username = brcps_requests.prop.getProperty("username").toString().trim();
	String password = brcps_requests.prop.getProperty("password").toString().trim();
	String connURL =  brcps_requests.prop.getProperty("connURL").toString().trim();
}
