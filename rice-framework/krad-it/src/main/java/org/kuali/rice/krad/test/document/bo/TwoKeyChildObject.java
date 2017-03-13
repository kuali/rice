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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="KRTST_TWO_KEY_CHILD_T")
public class TwoKeyChildObject {

    @Id
    @Column(name="FIN_COA_CD",length=2)
    String chartOfAccountsCode;

    @Id
    @Column(name="ORG_CD",length=4)
    String organizationCode;

    public TwoKeyChildObject() {
    }

    public TwoKeyChildObject(String chartOfAccountsCode, String organizationCode) {
        super();
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.organizationCode = organizationCode;
    }

    public String getChartOfAccountsCode() {
        return this.chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return this.organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TwoKeyChildObject [chartOfAccountsCode=").append(this.chartOfAccountsCode)
                .append(", organizationCode=").append(this.organizationCode).append("]");
        return builder.toString();
    }


}
