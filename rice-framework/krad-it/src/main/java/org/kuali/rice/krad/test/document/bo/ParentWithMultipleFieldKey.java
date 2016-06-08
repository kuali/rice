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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@IdClass(ParentWithMultipleFieldKeyId.class)
@Entity
@Table(name="KRTST_PARENT_WITH_MULTI_KEY_T")
public class ParentWithMultipleFieldKey {

    @Id
    @Column(name="FIN_COA_CD",length=2)
    String chartOfAccountsCode;

    @Id
    @Column(name="ACCOUNT_NBR",length=7)
    String accountNumber;

    @Column(name="ORG_CD",length=4)
    String organizationCode;

    @ManyToOne(fetch=FetchType.LAZY,cascade= {CascadeType.REFRESH})
    @JoinColumns( {
              @JoinColumn(name="FIN_COA_CD",referencedColumnName="FIN_COA_CD",updatable=false,insertable=false)
            , @JoinColumn(name="ORG_CD",referencedColumnName="ORG_CD",updatable=false,insertable=false)
    } )
    TwoKeyChildObject organization;

    public ParentWithMultipleFieldKey() {}



    public ParentWithMultipleFieldKey(String chartOfAccountsCode, String accountNumber, String organizationCode) {
        super();
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.organizationCode = organizationCode;
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

    public String getOrganizationCode() {
        return this.organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public TwoKeyChildObject getOrganization() {
        return this.organization;
    }

    public void setOrganization(TwoKeyChildObject organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ParentWithMultipleFieldKey [chartOfAccountsCode=").append(this.chartOfAccountsCode)
                .append(", accountNumber=").append(this.accountNumber).append(", organizationCode=")
                .append(this.organizationCode).append(", organization=").append(this.organization).append("]");
        return builder.toString();
    }


}
