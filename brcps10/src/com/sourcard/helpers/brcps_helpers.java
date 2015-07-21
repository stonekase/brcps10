package com.sourcard.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

public class brcps_helpers {
	
	public static String shortcodeInput ="111";
	
	//count the number of request parameters
	public static boolean IsValidRequestParameters(HttpServletRequest request)
	{
		if(request.getParameterMap().containsKey("msisdn") && request.getParameterMap().containsKey("input")
			&& request.getParameterMap().size() == 2 && request.getParameterMap().size() != 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//transaction id generator
	public static long GenerateUniqueId()
	{
		UUID idOne = UUID.randomUUID();
		UUID idTwo = UUID.randomUUID();
		String str = idOne.toString() + idTwo.toString();
		int uid = str.hashCode();
		String filterStr = ""+uid;
		str=filterStr.replaceAll("-", "");
		
		System.out.println("transaction id generated .tansID: "+Long.parseLong(str));
		
		return Long.parseLong(str);
	}
	
	//check if file exist
	public static boolean FileExist(String filepath)
	{
		
		File f = new File(filepath);
		 
		  if(f.exists()){
			 return true;
		  }else{
			  return false;
		  }
	}
	
	//read a config file based on the computer directory given
	public static Properties ReadConfigFile(String filepath)
	{
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(filepath);
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return prop;
	}
	
	//create properties file
	public static void CreateConfigFile(String filepath)
	{
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(filepath);
			// set the properties value
			prop.setProperty("username","postgres");
			prop.setProperty("password","Madscientist1");
			prop.setProperty("connURL", "jdbc:postgresql://localhost:5433/brcps");
			
			// save properties to project root folder
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
