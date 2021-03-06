package com.sourcecard.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import com.sourcard.helpers.*;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class brcps_requests
 */
@WebServlet(asyncSupported = true, description = "this is the index servlet of the whole application",
	urlPatterns = { "/brcps_requests" })
public class brcps_requests extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	public static Properties prop;
	private String configFile = "C:\\brcps_config.conf";
	public static Logger log = null;
	
	long currentTransactionID,previousTransactionID;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public brcps_requests() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		if(OSValidator.isWindows())
		{
			configFile = "C:\\brcps_config.conf";
		}
		else if(OSValidator.isUnix())
		{
			configFile = "brcps_config_reloaded.conf";
		}
		else if(OSValidator.isMac())
		{
			System.out.println("mac OS:: application wont work");
		}
		else if(OSValidator.isSolaris())
		{
			System.out.println("Solaris OS:: application wont work");
		}
		else
		{
			System.out.println("Do not know this Operating System");
		}
		log = Logger.getLogger(brcps_requests.class);
		//initiate the config file
		if (brcps_helpers.FileExist(configFile))
		{
			System.out.println("File exist :" + configFile);
			//log.info("File exist :" + configFile);
			//the file exist so therefore extract the properties into the variable
			prop = brcps_helpers.ReadConfigFile(configFile);
			
		}else
		{
			System.out.println("Creating file :" + configFile);
			//the property file does not exist so therefore create it.
			brcps_helpers.CreateConfigFile(configFile);
			//then copy the created file to the properties variable
			prop = brcps_helpers.ReadConfigFile(configFile);
		}
																										
		System.out.println("Created : " + configFile);
		System.out.println(prop.getProperty("username"));
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		long transactionId = Long.parseLong(request.getParameter("transactionId"));
		String receipient_msisdn = (String)request.getParameter("receipient_msisdn");
		long transfer_amount = Long.parseLong(request.getParameter("transfer_amount"));
		long account_no = Long.parseLong(request.getParameter("account_no"));
	    String bank_code = request.getParameter("bank_code");
		this.currentTransactionID = transactionId;
		
		log.info("Received :: TransactionID:"+transactionId+" ,Msisdn:"+receipient_msisdn+" ,cashout:"+
				transfer_amount+" ,Account:"+account_no+" ,bankcode:"+bank_code);
		//the get request will be used here because of internal debug and testing 
		//but on production the post will be requested
		
		//setting the requests and responses format
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		//getting the session attached to the request
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(30);//session only lasts 15 seconds
		
		//check if it is aa valid http request
		// http://localhost:9999/InterswitchDispatcher/BRCPS_DispatchServlet?transactionId=1561909139&receipient_msisdn=347010060890&transfer_amount=43
		if(brcps_helpers.IsValidRequestParameters(request)&& this.currentTransactionID != this.previousTransactionID)
		{
			this.previousTransactionID = this.currentTransactionID;
			verifyCustomerSubscription(request,response,session);
		}
		else
		{
			PrintWriter out = response.getWriter();
			out.println("Sorry wrong or duplicate request");
		}
		
	}
	//this method will check our subscription database to check if a customer is subscribed
	private void verifyCustomerSubscription(HttpServletRequest request,HttpServletResponse response,HttpSession session) {
		
		AsyncContext asyncCtx = request.startAsync();
	    //the timeout is set to be large but will be pulled from the config file
		long timeout = Long.parseLong(prop.getProperty("brcpstimeout"));
		asyncCtx.setTimeout(timeout);
		asyncCtx.addListener(new brcps_asynclistener());
		
		//getting the request and setting them up
	 	
	 	System.out.println("transaction ID: "+request.getParameter("transactionId"));
	 	System.out.println("receipient msisdn: "+request.getParameter("receipient_msisdn"));
	 	System.out.println("transfer amount: "+request.getParameter("transfer_amount"));
		
		//creating the executor threadpool
		ThreadPoolExecutor executor = (ThreadPoolExecutor)request
			.getServletContext().getAttribute("executor");
		
		System.out.println("ThreadpoolExecutor : "+ (ThreadPoolExecutor)request
				.getServletContext().getAttribute("executor"));
		
		//this is the thread pool used by 
	   	executor.execute(new brcps_asyncrequestprocessor(asyncCtx,session));
	}
	
}
