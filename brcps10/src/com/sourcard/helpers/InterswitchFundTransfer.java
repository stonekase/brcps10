package com.sourcard.helpers;

public class InterswitchFundTransfer {
	
	private String BeneficiaryName,
	BeneficiaryEmail,
	BeneficiaryPhone,
	TerminatingEntityName,
	TerminatingAccountNumber,
	TerminatingAccountType;
	
	public void setBeneficiaryName(String beneficiaryName)
	{
		this.BeneficiaryName = beneficiaryName;
	}
	
	public String getBeneficiaryName() 
	{
		if(this.BeneficiaryName != null)
		{
			return this.BeneficiaryName;
		}
		else
		{
			return null;
		}
	}
	public void setBeneficiaryEmail(String beneficiaryEmail)
	{
		this.BeneficiaryEmail = beneficiaryEmail;
	}
	
	public String getBeneficiaryEmail() 
	{
		if(this.BeneficiaryEmail != null)
		{
			return this.BeneficiaryEmail;
		}
		else
		{
			return null;
		}
	}
	public void setBeneficiaryPhone(String beneficiaryPhone)
	{
		this.BeneficiaryPhone = beneficiaryPhone;
	}
	
	public String getBeneficiaryPhone() 
	{
		if(this.BeneficiaryPhone != null)
		{
			return this.BeneficiaryPhone;
		}
		else
		{
			return null;
		}
	}
	public void setTerminatingEntityName(String terminatingEntityName)
	{
		this.TerminatingEntityName = terminatingEntityName;
	}
	
	public String getTerminatingEntityName() 
	{
		if(this.TerminatingEntityName != null)
		{
			return this.TerminatingEntityName;
		}
		else
		{
			return null;
		}
	}
	public void setTerminatingAccountNumber(String terminatingAccountNumber)
	{
		this.TerminatingAccountNumber = terminatingAccountNumber;
	}
	
	public String getTerminatingAccountNumber() 
	{
		if(this.TerminatingAccountNumber != null)
		{
			return this.TerminatingAccountNumber;
		}
		else
		{
			return null;
		}
	}
	public void setTerminatingAccountType(String terminatingAccountType)
	{
		this.TerminatingAccountType = terminatingAccountType;
	}
	
	public String getTerminatingAccountType() 
	{
		if(this.TerminatingAccountType != null)
		{
			return this.TerminatingAccountType;
		}
		else
		{
			return null;
		}
	}
}
