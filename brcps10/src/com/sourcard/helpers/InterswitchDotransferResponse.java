package com.sourcard.helpers;

public class InterswitchDotransferResponse {
	
	private String ResponseCode,
	MAC,
	TransactionReference,
	TransactionDate,
	TransferCode;
	
	public void setResponseCode(String responseCode)
	{
		this.ResponseCode = responseCode;
	}
	public String getResponseCode()
	{
		if(ResponseCode == null)
		{
			 throw new NullPointerException("ResponseCode is null");
		}else{return this.ResponseCode;}
	 }
	 public void setMAC(String mac)
	 {
		this.MAC = mac;
	 }
	 public String getMAC()
	 {
		if(MAC == null)
		{
			throw new NullPointerException("MAC is null");
		}else{return this.MAC;}
	 }
	 
	 public void setTransactionReference(String transactionReference)
	 {
		this.TransactionReference = transactionReference;
	 }
	 public String getTransactionReference()
	 {
		if(TransactionReference == null)
		{
			throw new NullPointerException("TransactionReference is null");
		}else{return this.TransactionReference;}
	 }
	 
	 public void setTransactionDate(String transactionDate)
	 {
		this.TransactionDate = transactionDate;
	 }
	 public String getTransactionDate()
	 {
		if(TransactionDate == null)
		{
			throw new NullPointerException("TransactionDate is null");
		}else{return this.TransactionDate;}
	 }
	 
	 public void setTransferCode(String transferCode)
	 {
		this.TransferCode = transferCode;
	 }
	 public String getTransferCode()
	 {
		if(TransferCode == null)
		{
			throw new NullPointerException("TransferCode is null");
		}else{return this.TransferCode;}
	 }
}
