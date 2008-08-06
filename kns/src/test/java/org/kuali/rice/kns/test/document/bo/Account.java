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

package org.kuali.rice.kns.test.document.bo;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class Account extends PersistableBusinessObjectBase {
    
	private String number;
    private String name;
    private Long amId;
    private AccountManager accountManager;    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getAmId() {
        return this.amId;
    }

    public void setAmId(Long id) {
        this.amId = id;
    }

    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap propMap = new LinkedHashMap();
        propMap.put("number", getNumber());
        propMap.put("name", getName());
        return propMap;
    }

 
}