package com.sourcecard.robotics;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

public class BRCPS_ActionClass {
	static SerialPort chosenPort;
	static SerialPort[] portNames;
	static ArrayList<String>portList;
	
	public BRCPS_ActionClass()
	{
		portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++)
		{
			//portList.add(portNames[i].getSystemPortName());
			System.out.println("Searching for ports ...");
			portList.add(portNames[i].getDescriptivePortName());
			if(portNames[i].getDescriptivePortName().contains("Arduino Uno"))
			{
				System.out.println("found the arduino com port on : port " + portNames[i].getSystemPortName());
				System.out.println("Connecting to Arduino Serial Port...");
				// attempt to connect to the serial port
				chosenPort = SerialPort.getCommPort(portList.get(i).toString());
				chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
			}
		}
	}
	private void processTransaction(String msisdn,String amount,PrintWriter outputSMS)
	{
		//first check if user has data on our database
		//if not present then store number on database then send sms indicating the need to set bank details
		//on our website
		//else if present then proceed to fund user.Send response indicating account funding
		//now sending the SMS
		long num = Long.parseLong(msisdn);
		ResultSet rs = brcps_databasequery.verifyCustomerDetails(num);
		//long sub_no = rs.getLong("msisdn");
		try{
			if(rs.isBeforeFirst())
			{
				System.out.println("customer is subscribed to service");
				//then check if the account number is not null
				if(rs.getLong("account_number") != 0)
				{
					//then account number is present so send transaction request aswell as sms 
					//log request on database
					//send receipt confirmation sms
					outputSMS.println("R:"+msisdn+Main.prop.getProperty("transfer_confirmation1").toString()+amount
							+Main.prop.getProperty("transfer_confirmation1").toString());
				}
				else
				{
					//put the transaction in the pending transaction database
					//send receipt/account request sms
				}
				return;
			}
			else
			{
				System.out.println("customer is not subscribed to service");
				return;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}//endotron
	} 
	
	public void Update()
	{
		// create a new thread that listens for incoming text and populates the graph
		Scanner scanner = new Scanner(chosenPort.getInputStream());
		Thread thread = new Thread(){
			@Override public void run() {
				
				//wait after connecting so that the bootloader can finish whatever it is doing
				try{Thread.sleep(100);}catch(Exception ex){}
				PrintWriter outputSMS = new PrintWriter(chosenPort.getOutputStream()); 
				
				while(scanner.hasNextLine()) {
					try {
						String line = scanner.nextLine();
						//i need to know the format of me2u on the particular network
						//then i proceed to the base work..
						//extracting number,amount from SMS 
						//log sms coming from +234777 but do not understand for manual fix.
						//log request to the database,then send http request to interswitch
						//request--http://localhost/interswitchdispatcher?msisdn=value&&amount=value
						//wait for a response 
						//log success report
						//write to the database
						int characterLength = line.length();
					String legitTransferNum =line.substring(1,4);
					if (legitTransferNum.equals(Main.prop.getProperty("legitTransferNum")));
					{
						//now that we know it is a credit transfer
						/*
						 * SMS received from MTN after Share and Sell:
						 * "N50.00 was transferred to you from 2347032178255 via share n sell service." #74 characters
						 * "N100.00 was transferred to you from 2347032178255 via share n sell service."#75 characters
						 * "N1000.00 was transferred to you from 2347032178255 via share n sell service."#76 characters
						 * "N10000.00 was transferred to you from 2347032178255 via share n sell service."#77 characters
						 */
						switch(characterLength)
						{
							case 74:
							{
								String amount = line.substring(1,6);
								String msisdn = line.substring(36,49 );
								processTransaction(msisdn,amount,outputSMS);
								//outputSMS.println("R:"+msisdn+Main.prop.getProperty("transfer_confirmation1").toString()+amount
								//		+Main.prop.getProperty("transfer_confirmation1").toString());
								System.out.println("received  around N50");
								break;
							}
							case 75:
							{
								String amount = line.substring(1,7);
								String msisdn = line.substring(37,50 );
								System.out.println("received  around N100");
								break;
							}
							case 76:
							{
								String amount = line.substring(1,8);
								String msisdn = line.substring(38,51 );
								System.out.println("received  around N1000");
								break;
							}
							case 77:
							{
								String amount = line.substring(1,9);
								String msisdn = line.substring(39,52 );
								System.out.println("received  around N10000");
								break;
							}
							default:
							{
								
								System.out.println("now in the default response");
							}
						}
					}
						
					} catch(Exception e) {}
				}
				scanner.close();
			}
		};//end of thread
		thread.start();
	}
}
