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
package org.kuali.rice.kns.test.document;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.document.SessionDocument;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This is a test transactional document for use with testing data dictionary searchable attributes on the doc search. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AccountWithDDAttributesDocument extends TransactionalDocumentBase implements SessionDocument {
	private static final long serialVersionUID = 174220131121010870L;
	
	private Integer accountNumber;
	private String accountOwner;
	private KualiDecimal accountBalance;
	private Timestamp accountOpenDate;
	private String accountState;

    @Override
    protected LinkedHashMap<?,?> toStringMapper() {
        LinkedHashMap<String, String> meMap = new LinkedHashMap<String, String>();
        meMap.put("accountOwner", getAccountOwner());
        meMap.put("accountOpenDate", getAccountOpenDate().toString());
        meMap.put("accountState", getAccountState());
        return meMap;
    }
	
	public Integer getAccountNumber() {
		return this.accountNumber;
	}

	public void setAccountNumber(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountOwner() {
		return this.accountOwner;
	}

	public void setAccountOwner(String accountOwner) {
		this.accountOwner = accountOwner;
	}

	public KualiDecimal getAccountBalance() {
		return this.accountBalance;
	}

	public void setAccountBalance(KualiDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Timestamp getAccountOpenDate() {
		return this.accountOpenDate;
	}

	public void setAccountOpenDate(Timestamp accountOpenDate) {
		this.accountOpenDate = accountOpenDate;
	}

	public String getAccountState() {
		return this.accountState;
	}

	public void setAccountState(String accountState) {
		this.accountState = accountState;
	}
}
