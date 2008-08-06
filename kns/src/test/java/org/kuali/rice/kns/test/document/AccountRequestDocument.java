/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.document.SessionDocument;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.test.document.bo.Account;



public class AccountRequestDocument extends TransactionalDocumentBase implements SessionDocument {

    private String requester;
    private String reason1;
    private String reason2;
    private String requestType;
    private String accountTypeCode;

    private List<Account> accounts;

    public AccountRequestDocument() {
        accounts = new ArrayList<Account>();
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> meMap = new LinkedHashMap<String, String>();
        meMap.put("requester", getRequester());
        meMap.put("reason1", getReason1());
        meMap.put("reason2", getReason2());
        return meMap;
    }

    public String getReason2() {
        return reason2;
    }

    public void setReason2(String reason2) {
        this.reason2 = reason2;
    }

    public String getReason1() {
        return reason1;
    }

    public void setReason1(String reason1) {
        this.reason1 = reason1;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Account getAccount(int index) {
        while(accounts.size() - 1 < index) {
            accounts.add(new Account());
        }
        return accounts.get(index);
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setAccountTypeCode(String accountType) {
        this.accountTypeCode = accountType;
    }

    public String getAccountTypeCode() {
        return accountTypeCode;
    }

}
