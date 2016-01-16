package com.sourcard.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sourcecard.servlets.brcps_requests;

public class brcps_helpers {
	
	final static String USER = "brcps";
	public static Properties prop;
	//count the number of request parameters
	public static boolean IsValidRequestParameters(HttpServletRequest request)
	{
		if(request.getParameterMap().containsKey("transactionId") && request.getParameterMap().containsKey("receipient_msisdn")&&
				request.getParameterMap().containsKey("transfer_amount")&&request.getParameterMap().containsKey("bank_code")
				&&request.getParameterMap().containsKey("account_no")&& request.getParameterMap().size() == 5 && request.getParameterMap().size() != 0)
		{
			return true;
		}
		else
		{
			System.out.println("Bad request received from : "+request.getLocalAddr());
			return false;
		}
	}
	public static void sendSms(String receiverN0,String smsMessage)
	{
		//message to expect to send to customers
		//[+2347010060890,amount,smscontent]
		try{
			//since the transaction has taken place now send interswitch the neccesary details 
			//they need and if you get aa success back from them then you flag a success from them
			//String url = "http://localhost:9999/InterswitchDispatcher/BRCPS_DispatchServlet"
			//+ "";
			
			System.out.println("Entering http request to interswitch Dispatcher");
			HttpClient client = HttpClientBuilder.create().build();
			
			URI uri = new URIBuilder()
			.setScheme(brcps_requests.prop.getProperty("send_sms_scheme").toString())
			.setHost(brcps_requests.prop.getProperty("host").toString())
			.setPort(Integer.parseInt(brcps_requests.prop.getProperty("smsport").toString()))
			.setPath(brcps_requests.prop.getProperty("smspath").toString())
			.setParameter("option",brcps_requests.prop.getProperty("option").toString())
			.setParameter("comm",brcps_requests.prop.getProperty("comm").toString())
			.setParameter("username", brcps_requests.prop.getProperty("smsusername").toString())
			.setParameter("password", brcps_requests.prop.getProperty("smspassword").toString())
			.setParameter("sender", brcps_requests.prop.getProperty("smssender").toString())
			.setParameter("recipient", receiverN0)
			.setParameter("message", smsMessage)
			.build();
			
			HttpGet request =  new HttpGet(uri);
			System.out.println("Request URI : "+ request.getURI());	
			//add request header
			request.addHeader("User-Agent",USER);
			HttpResponse response = client.execute(request);
			System.out.println("executed the http request....");
			
			//get the status code 
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode == HttpStatus.SC_OK)
			{
				System.out.println("sms sent)");
				ResponseHandler<String> handler = new BasicResponseHandler();
				String body = handler.handleResponse(response);
				brcps_requests.log.info("SMS SENT::msisdn:"+receiverN0+",Response:"+body+","
						+ "content:"+smsMessage);
				//Task.log.info(body+":::msisdn: "+receiverN0+",Message:"+smsMessage);
				return;
			}
			else if(statusCode == HttpStatus.SC_GATEWAY_TIMEOUT){
				System.out.println("sms sending timeout");
				ResponseHandler<String> handler = new BasicResponseHandler();
				String body = handler.handleResponse(response);
				brcps_requests.log.error("SMS TIMEOUT::msisdn:"+receiverN0+",Response:"+body+","
						+ "content:"+smsMessage);
				//Task.log.info("msisdn: "+receiverN0+",Message:"+smsMessage);
				return;
			}
			else
			{
				ResponseHandler<String> handler = new BasicResponseHandler();
				String body = handler.handleResponse(response);
				brcps_requests.log.error("SMS FAILED::msisdn:"+receiverN0+",Response:"+body+","
						+ "content:"+smsMessage);
			}
		}catch(Exception ex)
		{
			System.out.println("Error !!!! :"+ ex);
			//Task.log.info("msisdn: "+receiverN0+",Message:"+smsMessage);
			return;
		}
	}
	public static String confirmSMS(String amount,String bankCode,String bankAccount)
	{
		int amt= Integer.parseInt(amount);
		int result = amt-(amt*15)/100;
		return "Transaction Amount: N"+amount+" Airtime,Credit: N"+result+",Bank: "+bankCode+",Account Num: "+bankAccount;
	}
	public static long evaluateCashOut(long amount,long percentage)
	{
		if(percentage <= 100)
		{
			percentage = amount * percentage/100;
			amount = amount - percentage;
		}else
		{
			return -1;
		}
		return amount;
	}
	public static String confirmSMSBankAbsent(String amount,String bankCode,String bankAccount)
	{
		long amt= Long.parseLong(amount);
		long result = evaluateCashOut(amt,15);
		return "Transaction Amount: N"+amount+",Credit: N"+result+",Bank: NIL,Account Num: NIL."
				+ "Text 'HELp' to 2347010060890 to subscribe or visit http://scr/brcps/howto.php?";
	}
	public static String subscribeSms()
	{
		return "To subscribe text(SUBSCRIBE-BANK CODE-ACCOUNT NO)"
				+ " to +2347010060890.eg:(subscribe 1 0000111122) or visit http://scr/brcps/howto.php?";
	}
	public static String updateBankSms(String bankcode,String accountNum)
	{
		return "You have successfully change your account details.Bank:"+bankcode+",Account No:"+accountNum;
	}
	public static String confirmSubscriptionSms(String bankcode,String accountNum)
	{
		return "Your subscription is successful.You can now Pawn your Airtime with BRCPS.Bank: "+bankcode+",Account number:"+accountNum;
	}
	
	public static String transactionComplete()
	{
		return "Transaction is successful.";
	}
	
	public static String delayTransaction(String amount,String bankCode,String bankAccount)
	{
		int amt= Integer.parseInt(amount);
		int result = amt-(amt*15)/100;
		return "Apologies, but your transaction will be delayed ,we are working on it.Thanks Amount: N"+amount+" Airtime,Credit: N"+result+",Bank: "+bankCode+",Account Num: "+bankAccount;
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
	
	public static int sendRequestFunding(long transactionId, String msisdn,long transferAmount)
	{
		//send http request to the apache server who deals with interswitch and responses
		//http://localhost:9999/brcps_request?.........
		//send receipt confirmation sms
		int statusCode=0;
		//====================================================================================================
		//-------------------------------------------------------------------------------------
		try{
			//since the transaction has taken place now send interswitch the neccesary details 
			//they need and if you get aa success back from them then you flag a success from them
			//String url = "http://localhost:9999/InterswitchDispatcher/BRCPS_DispatchServlet"
			//+ "";
			System.out.println("Entering http request to interswitch Dispatcher");
			HttpClient client = HttpClientBuilder.create().build();
			URI uri = new URIBuilder()
			.setScheme(prop.getProperty("http_scheme").toString())
			.setHost(prop.getProperty("http_host").toString())
			.setPort(Integer.parseInt(prop.getProperty("http_port").toString()))
			.setPath(prop.getProperty("http_path").toString())
			.setParameter("transactionId", ""+transactionId)
			.setParameter("transferAmount", ""+transferAmount)
			.setParameter("transaction_msisdn", ""+msisdn)
			.setParameter("param1",prop.getProperty("param1").toString())
			.setParameter("param2",prop.getProperty("param2").toString())
			.setParameter("param3", prop.getProperty("param3").toString())
			.setParameter("param4", prop.getProperty("param4").toString())
			.setParameter("param5", prop.getProperty("param5").toString())
			.setParameter("param6", prop.getProperty("param6").toString())
			.build();
			
			HttpGet request =  new HttpGet(uri);
			System.out.println("Request URI : "+ request.getURI());
			//add request header
			request.addHeader("User-Agent",USER);
			HttpResponse response = client.execute(request);
			System.out.println("executed the http request....");
			
			//get the status code 
			statusCode = response.getStatusLine().getStatusCode();
			
			 return statusCode;
		}catch(Exception ex)
		{
			System.out.println("Error !!!! :"+ ex);
			return HttpStatus.SC_INTERNAL_SERVER_ERROR;
		}
	}
	
	//create properties file
	public static void CreateConfigFile(String filepath)
	{
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(filepath);
			
			
			//brcps server configs
			prop.setProperty("brcpstimeout","100000");
			
			// set the properties value
			prop.setProperty("username","postgres");
			prop.setProperty("password","Madscientist1");
			prop.setProperty("connURL", "jdbc:postgresql://localhost:5432/brcpsturbo");
			
			//interswitch urls
			
			//SMS sending parameters
			prop.setProperty("send_sms_scheme","http");
			prop.setProperty("host","www.tripleclicksms.com");
			prop.setProperty("smsport","80");
			prop.setProperty("smspath","/index.php");
			prop.setProperty("smsusername","stonekase");
			prop.setProperty("smspassword","Madscientist1");
			prop.setProperty("smssender","brcps");
			prop.setProperty("option","com_spc");
			prop.setProperty("comm","spc_api");
			
			//http request parameterss
			prop.setProperty("http_scheme","");
			prop.setProperty("http_host","");
			prop.setProperty("http_port","");
			prop.setProperty("http_path","");
			prop.setProperty("transactionId","");
			prop.setProperty("transferAmount","");
			prop.setProperty("transaction_msisdn","");
			prop.setProperty("param1","");
			prop.setProperty("param2","");
			prop.setProperty("param3","");
			prop.setProperty("param4","");
			prop.setProperty("param5","");
			prop.setProperty("param6","");
			
			//=====================================
			prop.setProperty("querytransactionxmlroot", "Response");
			prop.setProperty("querytransactionparam1","ResponseCode");
			prop.setProperty("querytransactionparam2","ServiceProviderId");
			prop.setProperty("querytransactionparam3","TransactionRef");
			prop.setProperty("querytransactionparam4","RequestReference");
			prop.setProperty("querytransactionparam5","Status");
			prop.setProperty("querytransactionparam6","TransactionSet");
			prop.setProperty("querytransactionparam7","TransactionResponseCode");
			prop.setProperty("querytransactionparam8","PaymentDate");
			prop.setProperty("querytransactionparam9","Amount");
			prop.setProperty("querytransactionparam10","Surcharge");
			prop.setProperty("querytransactionparam11","CurrencyCode");
			prop.setProperty("querytransactionparam12","Customer");
			prop.setProperty("querytransactionparam13","CustomerEmail");
			prop.setProperty("querytransactionparam14","CustomerMobile");
			
			//==============fud transfer info ===============================
			prop.setProperty("fundstransferparam1","BeneficiaryName");
			prop.setProperty("fundstransferparam2","BeneficiaryEmail");
			prop.setProperty("fundstransferparam3","BeneficiaryPhone");
			prop.setProperty("fundstransferparam4","TerminatingEntityName");
			prop.setProperty("fundstransferparam5","TerminatingAccountNumber");
			prop.setProperty("fundstransferparam6","TerminatingAccountType");
			//==================DoTransfer Variables===================
			prop.setProperty("requestprefix","1343");
			prop.setProperty("InitiatingEntityCode","SCC");
			prop.setProperty("Lastname","BRCP");
			prop.setProperty("Othernames","payments");
			prop.setProperty("Email","oakawo@sourcecard.com.ng");
			prop.setProperty("Phone","07010060890");
			prop.setProperty("bLastname","brcps");
			prop.setProperty("bOthernames","customer");
			prop.setProperty("bEmail","oakawo@sourcecard.com.ng");
			prop.setProperty("bPhone","07010060890");
			prop.setProperty("Channel","7");
			prop.setProperty("PaymentMethodCode","CA");
			prop.setProperty("CurrencyCode","566");
			prop.setProperty("TPaymentMethodCode","AC");
			prop.setProperty("TCurrencyCode","566");
			prop.setProperty("CountryCode","NG");
			prop.setProperty("EntityCode","058");
			prop.setProperty("AccountType","10");
			
			//=====================Mascelleneous Variables=================================
			prop.setProperty("soapuri","http://services.interswitchng.com/quicktellerservice/");
			prop.setProperty("soapurl","https://stageserv.interswitchng.com/uat_QuickTellerService/QuickTeller.svc");
			prop.setProperty("soapprefix","quic");
			
			//=======================DoTransfer Response ======================================================
			//replaceme--this variable should be left as an empty string
			prop.setProperty("successcode", "90000");
			prop.setProperty("replaceme", "");
			prop.setProperty("xmlremoveme","<?xml version="+"\"1.0\""+" encoding="+"\"utf-8\""+" ?>");
			prop.setProperty("dotransactionxmlroot", "Response");
			prop.setProperty("dotransactionparam1","ResponseCode");
			prop.setProperty("dotransactionparam2","MAC");
			prop.setProperty("dotransactionparam3","TransactionReference");
			prop.setProperty("dotransactionparam4","TransactionDate");
			prop.setProperty("dotransactionparam5","TransferCode");
			//=================================================================================================
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
	
	//XML processrs for interswitch
	public static Document loadXMLFromString(String xmlString)
	{
		//problem arose from here
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlString));
			
			return builder.parse(is);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void pQueryTrasactionRespnse(Document doc ,Properties prop)
	{
		//optional but recomended
		doc.getDocumentElement().normalize();
		System.out.println("Root element : "+doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName(prop.getProperty("querytransactionxmlroot"));
		System.out.println("-----------------------------------------------------------");
		for(int temp =0; temp<nList.getLength();temp++)
		{
			Node nNode = nList.item(temp);
			System.out.println("\nCurrent Element : "+ nNode.getNodeName());
			if(nNode.getNodeType()== Node.ELEMENT_NODE)
			{
				Element eElement = (Element)nNode;
				System.out.println("Response Code : "+ eElement.getElementsByTagName(prop.getProperty("querytransactionparam1")).item(0).getTextContent());
				System.out.println("Transaction Reference : "+ eElement.getElementsByTagName(prop.getProperty("querytransactionparam3")).item(0).getTextContent());
				System.out.println("Status : "+ eElement.getElementsByTagName(prop.getProperty("querytransactionparam5")).item(0).getTextContent());
			}
		}
	}
	//==============================================================================================================================================================
	public static void pDoTrasactionRespnse(Document doc ,Properties prop,long transactionID,String msisdn)
	{
		InterswitchDotransferResponse dTransfer  = new InterswitchDotransferResponse();
		//optional but recomended
		doc.getDocumentElement().normalize();
		System.out.println("Root element : "+doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName(prop.getProperty("dotransactionxmlroot"));
		System.out.println("-----------------------------------------------------------");
		Node nNode = null;
		for(int temp =0; temp<nList.getLength();temp++)
		{
			nNode = nList.item(temp);
			System.out.println("\nCurrent Element : "+ nNode.getNodeName());
			if(nNode.getNodeType()== Node.ELEMENT_NODE)
			{
				Element eElement = (Element)nNode;
				
				String responseCode = eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam1").toString()).item(0).getTextContent();
				System.out.println("inner response value :: "+responseCode);
				if (responseCode.equals(brcps_requests.prop.getProperty("successcode").toString()))
				{
					System.out.println("Response Code : "+ eElement.getElementsByTagName(prop.getProperty("dotransactionparam1")).item(0).getTextContent().toString());
					System.out.println("MAC : "+ eElement.getElementsByTagName(prop.getProperty("dotransactionparam2")).item(0).getTextContent().toString());
					System.out.println("TransactionReference : "+ eElement.getElementsByTagName(prop.getProperty("dotransactionparam3")).item(0).getTextContent().toString());
					System.out.println("TransactionDate : "+ eElement.getElementsByTagName(prop.getProperty("dotransactionparam4")).item(0).getTextContent().toString());
					System.out.println("TransferCode : "+ eElement.getElementsByTagName(prop.getProperty("dotransactionparam5")).item(0).getTextContent().toString());
					
					//now stock up new instances of interswitchDotransferResponse objects with values
					dTransfer.setResponseCode(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam1")).item(0).getTextContent().toString());
					dTransfer.setMAC(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam2")).item(0).getTextContent().toString());
					dTransfer.setTransactionReference(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam3")).item(0).getTextContent().toString());
					dTransfer.setTransactionDate(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam4")).item(0).getTextContent().toString());
					dTransfer.setTransferCode(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam5")).item(0).getTextContent().toString());
					
					//now move the transaction from pending 
					brcps_databasequery.moveTransactionToPassed(transactionID, 0);//will fix this
					//load the 
					brcps_databasequery.insertIntoTransactionResponse(dTransfer.getResponseCode(),dTransfer.getMAC(),
							dTransfer.getTransactionDate(), dTransfer.getTransactionReference(), dTransfer.getTransferCode(),msisdn);
				}
				else
				{
					//now move the transaction from pending 
					brcps_databasequery.moveTransactionToReconcile(transactionID, 1);//will fix this
				}
			}
		}
	}
	//=======================================================================================================================================================================================
	//========================INTERSWITCH XML Helpers=====================================================
	private final static Hashtable<String, String> htmlEntitiesTable = new Hashtable<String, String>();
	static {
	    htmlEntitiesTable.put("&amp;","&");
	    htmlEntitiesTable.put("&quot;","\"");
	    htmlEntitiesTable.put("&lt;","<");
	    htmlEntitiesTable.put("&gt;",">");
	    htmlEntitiesTable.put("&#58;",":");
	    htmlEntitiesTable.put("&#91;","[");
	    htmlEntitiesTable.put("&#93;","]");
	    htmlEntitiesTable.put("&#61;","=");
	}

	public static String decodeHtmlEntityCharacters(String inputString) throws Exception {
	    Enumeration<String> en = htmlEntitiesTable.keys();

	    while(en.hasMoreElements()){
	        String key = en.nextElement();
	        String val = htmlEntitiesTable.get(key);

	        inputString = inputString.replaceAll(key, val);
	    }

	    return inputString;
	}
	
	public static String get_SHA_512_SecurePassword(String stringToHash)
	{
	    String generatedPassword = null;
	    try {

	        MessageDigest md = MessageDigest.getInstance("SHA-512");
	       // md.update(salt.getBytes("UTF-8"));
	        byte[] bytes = md.digest(stringToHash.getBytes("UTF-8"));
	        StringBuilder sb = new StringBuilder();
	        for(int i=0; i< bytes.length ;i++)
	        {
	            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        generatedPassword = sb.toString();
	    } 
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	    }
	    return generatedPassword;
	}
	//=======================================================
	public static String ProcessDoTransferSoapMessageToString(SOAPMessage soapResponse)
	{
		String xmlstring = null;
		
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			Source sourceContent = soapResponse.getSOAPPart().getContent();
			System.out.print("\nResponse SOAP Message = ");
			//StreamResult result = new StreamResult(System.out);
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(sourceContent, result);
			xmlstring = result.getWriter().toString();
			xmlstring = decodeHtmlEntityCharacters(xmlstring);
			//String removeMe = "<?xml version="+"\"1.0\""+" encoding="+"\"Windows-1252\""+"?>";
			String removeMe = brcps_requests.prop.getProperty("xmlremoveme").toString();
			System.out.println(removeMe);
			xmlstring =xmlstring.replace(removeMe, brcps_requests.prop.getProperty("replaceme").toString()); 
			Charset.forName("UTF-8").encode(xmlstring);
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlstring;
	}
}
