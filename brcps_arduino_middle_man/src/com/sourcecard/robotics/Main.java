package com.sourcecard.robotics;

import java.util.Properties;
public class Main {
	
	public static Properties prop;
	private static String configFile = "C:\\brcps_config_reloaded.conf";

	public static void main(String[] args) {
		//initiate the config file
		if (BRCPS_Config.FileExist(configFile))
		{
			System.out.println("File exist :" + configFile);
			//the file exist so therefore extract the properties into the variable
			prop = BRCPS_Config.ReadConfigFile(configFile);
			
		}else
		{
			System.out.println("Creating file :" + configFile);
			//the property file does not exist so therefore create it.
			BRCPS_Config.CreateConfigFile(configFile);
			//then copy the created file to the properties variable
			prop = BRCPS_Config.ReadConfigFile(configFile);
		}
	}

}
