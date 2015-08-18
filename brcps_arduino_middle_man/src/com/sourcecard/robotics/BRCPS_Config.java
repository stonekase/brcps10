package com.sourcecard.robotics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;

public class BRCPS_Config {
	
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
			
			return prop;//personal
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
				prop.setProperty("connURL", "jdbc:postgresql://localhost:5433/test_brcps");
				
				//Response SMS Messages
				prop.setProperty("legitTransferNum","777");
				prop.setProperty("transfer_confirmation1","We have received your transfer of ");
				prop.setProperty("transfer_confirmation2"," you will receive a confirmation SMS on fund transfer.");
				prop.setProperty("invalid_sms","Invalid Request,Please visit http://www.scr.com.ng/brcps/howTo.php");
				prop.setProperty("transaction_confirm_sms","Your account have been credited with N amount,Thank you for patronising BRCPS.");
				prop.setProperty("advert1", "You can now sell you credit,please visit http://www.scr.com.ng/brcps/howTo.php for more info.");
				prop.setProperty("advert2", "your best way to sell your Airtime,please visit http://www.scr.com.ng/brcps/howTo.php for more info.");
				prop.setProperty("advert3","You can now sell your credit conveniently using BRCPS ,please visit http://www.scr.com.ng/brcps/howTo.php for more info.");
				prop.setProperty("bank_account_activation", "You can activate/deactivate your bank via sms. E.G:(activate-brcps bank code -account number) to the appropriate number for network.");
				
				
				//database queries
				
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
