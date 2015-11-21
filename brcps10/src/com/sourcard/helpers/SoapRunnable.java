package com.sourcard.helpers;

import java.util.concurrent.Callable;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class SoapRunnable implements Callable<SOAPMessage>{
    private String url;
    private SOAPConnection soapConnection;
    private SOAPMessage response;
    private SOAPMessage request;

    public SoapRunnable(SOAPConnection soapConnection,String url,SOAPMessage request) {
        this.url = url;
        this.soapConnection = soapConnection;
        this.request = request;
    }

	@Override
	public SOAPMessage call() throws Exception {
		try {
			response = soapConnection.call(request, url);
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return response;
	}
}
