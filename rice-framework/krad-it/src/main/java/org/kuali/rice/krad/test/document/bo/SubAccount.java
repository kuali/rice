/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.test.document.bo;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.annotation.Label;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TRV_SUB_ACCT")
public class SubAccount extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="sub_acct",length=10)
    private String subAccount;

    @Column(name="sub_acct_name",length=40)
    private String subAccountName;

    @Column(name="acct_num",length=10)
    private String accountNumber;

    public SubAccount() {}

    public SubAccount(String subAccount, String subAccountName) {
        super();
        this.subAccount = subAccount;
        this.subAccountName = subAccountName;
    }

    public String getSubAccount() {
        return this.subAccount;
    }

    public void setSubAccount(String subAccount) {
        this.subAccount = subAccount;
    }

    public String getSubAccountName() {
        return this.subAccountName;
    }

    public void setSubAccountName(String subAccountName) {
        this.subAccountName = subAccountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
