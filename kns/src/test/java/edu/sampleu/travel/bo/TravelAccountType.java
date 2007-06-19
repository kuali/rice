// Created on Oct 18, 2006

package edu.sampleu.travel.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;



public class TravelAccountType extends PersistableBusinessObjectBase {
    
    private String accountTypeCode;
    private String name;
    

    public String getAccountTypeCode() {
		return accountTypeCode;
	}


	public void setAccountTypeCode(String accountTypeCode) {
		this.accountTypeCode = accountTypeCode;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getCodeAndDescription() {
		return accountTypeCode + " - " + name;
	}

	@Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap propMap = new LinkedHashMap();
        propMap.put("accountTypeCode", accountTypeCode);
        propMap.put("name", name);
        return propMap;
    }

 
}