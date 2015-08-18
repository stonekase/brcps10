package com.sourcecard.robotics;

public interface brcps_provider {
	
	//this interface will pull its files from the config file
	
		//String username = "postgres";
		//String password = "Madscientist1";
		//String connURL = "jdbc:postgresql://localhost:5433/brcps";
	//-------------------------------------------------------------------------	
		String username = Main.prop.getProperty("username").toString().trim();
		String password = Main.prop.getProperty("password").toString().trim();
		String connURL =  Main.prop.getProperty("connURL").toString().trim();

}
