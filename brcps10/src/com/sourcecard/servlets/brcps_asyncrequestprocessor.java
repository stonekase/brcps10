package com.sourcecard.servlets;

import java.io.StringReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.sourcard.helpers.InterswitchDotransferResponse;
import com.sourcard.helpers.SoapRunnable;
import com.sourcard.helpers.brcps_helpers;

public class brcps_asyncrequestprocessor implements Runnable{
	//private variables
	AsyncContext asyncCtx;
	long transactionId;
	String receipient_msisdn;
	HttpSession session;
	long transfer_amount;
	long account_no;
	int bank_code;
	//transaction tag
	final String USER = "brcps";
	
	//DoTransfer Parameters
	String MAC,TransferCode,Amount,TAmount,AccountNumber;
	
	
	public brcps_asyncrequestprocessor(AsyncContext asyncCtx,HttpSession session) {
		this.asyncCtx = asyncCtx;
		this.session = session;
		this.transactionId = Long.parseLong(asyncCtx.getRequest().getParameter("transactionId"));
		this.receipient_msisdn = (String)asyncCtx.getRequest().getParameter("receipient_msisdn");
		this.transfer_amount = Long.parseLong(asyncCtx.getRequest().getParameter("transfer_amount"));
		this.account_no = Long.parseLong(asyncCtx.getRequest().getParameter("account_no"));
		this.bank_code = Integer.parseInt(asyncCtx.getRequest().getParameter("bank_code"));
		 
	}
	@Override
	public void run() 
	{
		InterswitchDotransferResponse dTransfer  = new InterswitchDotransferResponse();
		System.out.println("beginning to run");
		//now we check if all variables are ok then we send a request to interswitch
		//on-success we send a request to interswitch(DONE on debug response)
		//after one gets a success then a confirmation sms is sent to the customer
		Future<SOAPMessage> future = null;
		 if(transactionId > 0 && receipient_msisdn != null && transfer_amount > 0)
		 {
			 brcps_requests.log.info("Received :: TransactionID:"+transactionId+" ,Msisdn:"+receipient_msisdn+" ,cashout:"+
						transfer_amount+" ,Account:"+account_no+" ,bankcode:"+bank_code);
			
			try {
				SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
				SOAPConnection soapConnection = soapConnectionFactory.createConnection();
				//long startTime = System.currentTimeMillis();
				
				ExecutorService executor = Executors.newSingleThreadExecutor();
			    future = executor.submit(new SoapRunnable(soapConnection,
			    		brcps_requests.prop.getProperty("soapurl").trim().toString(),
			    		doTransferDocument(""+transfer_amount)));
			    
				 SOAPMessage soapResponse = future.get(90, TimeUnit.SECONDS);
				// long elapsedTime = System.currentTimeMillis() - startTime;
				 //convert soap message to a string 
				String doTransferResString = brcps_helpers.ProcessDoTransferSoapMessageToString(soapResponse);
				//convert the soapmessage string to a Document file
				Document doTransferResDoc = brcps_helpers.loadXMLFromString(doTransferResString);
				//process and load the xml document into a getter setter for use by the application
				Node node = brcps_helpers.pDoTrasactionRespnse(doTransferResDoc ,brcps_requests.prop);
				//now this node can be manipulated for any type of activity
				if(node.getNodeType()== Node.ELEMENT_NODE)
				{
					//first check if we got a success else log the responsecode gotten and try to make sense out of it
					Element eElement = (Element)node;
					System.out.println("Response Code : "+ 
					eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam1")).item(0).getTextContent());
					String responseCode = eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam1")).item(0).getTextContent();
					if (responseCode.equals(brcps_requests.prop.getProperty("successcode").toString()))
					{
						//now stock up new instances of interswitchDotransferResponse objects with values
						dTransfer.setResponseCode(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam1")).item(0).getTextContent());
						dTransfer.setMAC(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam2")).item(0).getTextContent());
						dTransfer.setTransactionReference(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam3")).item(0).getTextContent());
						dTransfer.setTransactionDate(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam4")).item(0).getTextContent());
						dTransfer.setTransferCode(eElement.getElementsByTagName(brcps_requests.prop.getProperty("dotransactionparam5")).item(0).getTextContent());
						
						//move the data from the pending table to the passed transactions table
						//insert data into the response table
						//send success sms to client
						//transaction complete
						//asyncCtx.complete();
					}
					else
					{}
					
				}
				
				
			} catch (UnsupportedOperationException | SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			 asyncCtx.complete();
		 }
		 else
		 {
			 //i will log the null transactions as an error
			 brcps_requests.log.error("Received :: TransactionID:"+transactionId+" ,Msisdn:"+receipient_msisdn+" ,cashout:"+
						transfer_amount+" ,Account:"+account_no+" ,bankcode:"+bank_code);
			 asyncCtx.complete();
		 }
	}

	private SOAPMessage doTransferDocument(String amount) throws Exception
    {
		String soapuri = brcps_requests.prop.getProperty("soapuri");
		String soapPrefix = brcps_requests.prop.getProperty("soapprefix");
		
		String sha = ""+amount+brcps_requests.prop.getProperty("CurrencyCode").trim().toString()+
				brcps_requests.prop.getProperty("PaymentMethodCode").trim().toString()+amount+
				brcps_requests.prop.getProperty("TCurrencyCode").trim().toString()+	
				brcps_requests.prop.getProperty("TPaymentMethodCode").trim().toString()+
				brcps_requests.prop.getProperty("CountryCode").trim().toString();
		
		this.MAC = brcps_helpers.get_SHA_512_SecurePassword(sha);
		
		String xml =
				 "<quic:DoTransfer xmlns:quic='http://services.interswitchng.com/quicktellerservice/'>"
				 +"<!--Optional:-->"
				 +"<quic:xmlParams>"
				 +"<![CDATA[<RequestDetails>"
				 +"<InitiatingEntityCode>SCC</InitiatingEntityCode>"
				 +"<MAC>"+MAC+"</MAC>"
				 +"<TransferCode>"+transactionId+"</TransferCode>"
				 +"<Sender>"
				 +"<Lastname>"+brcps_requests.prop.getProperty("Lastname").trim().toString()+"</Lastname>"
				 +"<Othernames>"+brcps_requests.prop.getProperty("Othernames").trim().toString()+"</Othernames>"
				 +"<Email>"+brcps_requests.prop.getProperty("Email").trim().toString()+"</Email>"
				 +"<Phone>"+brcps_requests.prop.getProperty("Phone").trim().toString()+"</Phone>"
				 +"</Sender>"
				 +"<Beneficiary>"
				 +"<Lastname>"+brcps_requests.prop.getProperty("bLastname").trim().toString()+"</Lastname>"
				 +"<Othernames>"+brcps_requests.prop.getProperty("bOthernames").trim().toString()+"</Othernames>"
				 +"<Email>"+brcps_requests.prop.getProperty("bEmail").trim().toString()+"</Email>"
				 +"<Phone>"+brcps_requests.prop.getProperty("bPhone").trim().toString()+"</Phone>"
				 +"</Beneficiary>"
				 +"<Initiation>"
				 +"<Amount>"+amount+"</Amount>"
				 +"<Channel>"+brcps_requests.prop.getProperty("Channel").trim().toString()+"</Channel>"
				 +"<PaymentMethodCode>"+brcps_requests.prop.getProperty("PaymentMethodCode").trim().toString()+"</PaymentMethodCode>"
				 +"<CurrencyCode>"+brcps_requests.prop.getProperty("CurrencyCode").trim().toString()+"</CurrencyCode>"
				 +"<Processor>"
				 +"<Lastname></Lastname>"
				 +"<Othernames></Othernames>"
				 +"</Processor>"
				 +"<DepositSlip></DepositSlip>"
				 +"</Initiation>"
				 +"<Termination>"
				 +"<PaymentMethodCode>"+brcps_requests.prop.getProperty("TPaymentMethodCode").trim().toString()+"</PaymentMethodCode>"
				 +"<Amount>"+amount+"</Amount>"
				 +"<CurrencyCode>"+brcps_requests.prop.getProperty("TCurrencyCode").trim().toString()+"</CurrencyCode>"
				 +"<CountryCode>"+brcps_requests.prop.getProperty("CountryCode").trim().toString()+"</CountryCode>"
				 +"<EntityCode>"+brcps_requests.prop.getProperty("EntityCode").trim().toString()+"</EntityCode>"
				 +"<AccountReceivable>"
				 +"<AccountNumber>"+account_no+"</AccountNumber>"
				 +"<AccountType>"+brcps_requests.prop.getProperty("AccountType").trim().toString()+"</AccountType>"
				 +"</AccountReceivable>"
				 +"</Termination>"
				 +"</RequestDetails>]]></quic:xmlParams>"
				 +"</quic:DoTransfer>";
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	     factory.setNamespaceAware(true);
	     DocumentBuilder builder = factory.newDocumentBuilder();
	     Document doc = builder.parse(new InputSource(new StringReader(xml)));
	     
	     SOAPMessage message = MessageFactory.newInstance().createMessage();
	     message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
	     SOAPPart soapPart = message.getSOAPPart();
	     
		 //SOAP Envelop
		 SOAPEnvelope envelope = soapPart.getEnvelope();
		// envelope.setPrefix("soapenv");
		 //envelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
		 envelope.addNamespaceDeclaration(soapPrefix,soapuri );
		 
	     SOAPBody body = envelope.getBody();
	    // body.setPrefix("soapenv");
	     body.addDocument(doc);
	     
	     MimeHeaders headers = message.getMimeHeaders();
		 //headers.addHeader("SOAPAction",  "QueryTransaction");
	     headers.addHeader("SOAPAction",  "DoTransfer");
	     message.saveChanges();
	     message.writeTo(System.out);
		
		return message;
	}
	
}
