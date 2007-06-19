// Created on Oct 18, 2006

package edu.sampleu.travel.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectExtensionBase;



public class TravelAccountExtension extends PersistableBusinessObjectExtensionBase {
    
    private String number;
    private String accountTypeCode;
    private TravelAccountType accountType; 
    
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap propMap = new LinkedHashMap();
        propMap.put("number", getNumber());
        propMap.put("accountTypeCode", getAccountTypeCode());
        return propMap;
    }

    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

	public TravelAccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(TravelAccountType accountType) {
		this.accountType = accountType;
	}

 
}