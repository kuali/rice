/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travel.dataobject;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.krad.bo.VersionedAndGloballyUniqueBase;
import org.kuali.rice.krad.data.provider.annotation.Label;

@Entity
@Table(name="TRV_SUB_ACCT")
public class TravelSubAccount extends VersionedAndGloballyUniqueBase implements Serializable {
	private static final long serialVersionUID = 5768156680246084251L;

	@Id
	@Column(name="ACCT_NUM",length=10)
	@Label("Travel Account Number")
	private String travelAccountNumber;

	@Id
	@Column(name="SUB_ACCT",length=10)
	@Label("Travel Sub Account Number")
	private String subAccount;

	@Column(name="SUB_ACCT_NAME",length=40)
	private String subAccountName;

    public String getTravelAccountNumber() {
        return travelAccountNumber;
    }

    public void setTravelAccountNumber(String number) {
        this.travelAccountNumber = number;
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
}
