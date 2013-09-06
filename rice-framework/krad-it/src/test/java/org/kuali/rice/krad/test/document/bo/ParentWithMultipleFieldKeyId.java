/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.test.document.bo;

import javax.persistence.Column;
import javax.persistence.Id;

public class ParentWithMultipleFieldKeyId {

    @Id
    @Column(name="FIN_COA_CD")
    String chartOfAccountsCode;

    @Id
    @Column(name="ACCOUNT_NBR")
    String accountNumber;

    public ParentWithMultipleFieldKeyId() {
    }

    public ParentWithMultipleFieldKeyId(String chartOfAccountsCode, String accountNumber) {
        super();
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
    }

    public String getChartOfAccountsCode() {
        return this.chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


}
