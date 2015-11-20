package com.sourcard.helpers;

public class InterswitchQueryTransactionResult {
	
	private String ResponseCode,
	ServiceProviderId,
	TransactionRef,
	RequestReference,
	Status,
	TransactionSet,
	TransactionResponseCode,
	PaymentDate,
	Amount,
	Surcharge,
	CurrencyCode,
	Customer,
	CustomerEmail,
	CustomerMobile;
	
	
	public void setResponseCode(String responseCode)
	{
		this.ResponseCode = responseCode;
	}
	
	public String getResponseCode() 
	{
		if(this.ResponseCode != null)
		{
			return this.ResponseCode;
		}
		else
		{
			return null;
		}
	}
	public void setServiceProviderId(String serviceProviderId)
	{
		this.ServiceProviderId = serviceProviderId;
	}
	
	public String getServiceProviderId() 
	{
		if(this.ServiceProviderId != null)
		{
			return this.ServiceProviderId;
		}
		else
		{
			return null;
		}
	}
	
	public void setTransactionRef(String transactionRef)
	{
		this.TransactionRef = transactionRef;
	}
	
	public String getTransactionRef() 
	{
		if(this.TransactionRef != null)
		{
			return this.TransactionRef;
		}
		else
		{
			return null;
		}
	}
	public void setRequestReference(String requestReference)
	{
		this.RequestReference = requestReference;
	}
	
	public String getRequestReference() 
	{
		if(this.RequestReference != null)
		{
			return this.RequestReference;
		}
		else
		{
			return null;
		}
	}
	public void setStatus(String status)
	{
		this.Status = status;
	}
	
	public String getStatus() 
	{
		if(this.Status != null)
		{
			return this.Status;
		}
		else
		{
			return null;
		}
	}
	public void setTransactionSet(String transactionSet)
	{
		this.TransactionSet = transactionSet;
	}
	
	public String getTransactionSet() 
	{
		if(this.TransactionSet != null)
		{
			return this.TransactionSet;
		}
		else
		{
			return null;
		}
	}
	public void setTransactionResponseCode(String transactionResponseCode)
	{
		this.TransactionResponseCode = transactionResponseCode;
	}
	
	public String getTransactionResponseCode() 
	{
		if(this.TransactionResponseCode != null)
		{
			return this.TransactionResponseCode;
		}
		else
		{
			return null;
		}
	}
	public void setPaymentDate(String paymentDate)
	{
		this.PaymentDate = paymentDate;
	}
	
	public String getPaymentDate() 
	{
		if(this.PaymentDate != null)
		{
			return this.PaymentDate;
		}
		else
		{
			return null;
		}
	}
	public void setAmount(String amount)
	{
		this.Amount = amount;
	}
	
	public String getAmount() 
	{
		if(this.Amount != null)
		{
			return this.Amount;
		}
		else
		{
			return null;
		}
	}
	public void setSurcharge(String surcharge)
	{
		this.Surcharge = surcharge;
	}
	
	public String getSurcharge() 
	{
		if(this.Surcharge != null)
		{
			return this.Surcharge;
		}
		else
		{
			return null;
		}
	}
	public void setCurrencyCode(String currencyCode)
	{
		this.CurrencyCode = currencyCode;
	}
	
	public String getCurrencyCode() 
	{
		if(this.CurrencyCode != null)
		{
			return this.CurrencyCode;
		}
		else
		{
			return null;
		}
	}
	public void setCustomer(String customer)
	{
		this.Customer = customer;
	}
	
	public String getCustomer() 
	{
		if(this.Customer != null)
		{
			return this.Customer;
		}
		else
		{
			return null;
		}
	}
	public void setCustomerEmail(String customerEmail)
	{
		this.CustomerEmail = customerEmail;
	}
	
	public String getCustomerEmail() 
	{
		if(this.CustomerEmail != null)
		{
			return this.CustomerEmail;
		}
		else
		{
			return null;
		}
	}
	public void setCustomerMobile(String customerMobile)
	{
		this.CustomerMobile = customerMobile;
	}
	
	public String getCustomerMobile() 
	{
		if(this.CustomerMobile != null)
		{
			return this.CustomerMobile;
		}
		else
		{
			return null;
		}
	}
}
