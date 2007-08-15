/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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