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
package org.kuali.rice.krad.demo.travel.dataobject;

import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.provider.annotation.ForceUppercase;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="TRV_SUB_ACCT")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY,UifAutoCreateViewType.LOOKUP})
public class TravelSubAccount extends DataObjectBase {

	private static final long serialVersionUID = 5768156680246084251L;

    @Id
    @Column(name = "ACCT_NUM",length = 10)
    @Label("Travel Account Number")
    @NotNull
    private String travelAccountNumber;

	@Id
    @ForceUppercase
	@Column(name="SUB_ACCT",length=10)
	@Label("Travel Sub Account Number")
    @NotNull
	private String subAccount;

	@Column(name="SUB_ACCT_NAME",length=40)
    @NotNull
	private String subAccountName;

    @ManyToOne
    @JoinColumn(name = "ACCT_NUM" ,insertable=false, updatable=false)
    TravelAccount account;

    public String getTravelAccountNumber() {
        return this.travelAccountNumber;
    }

    public void setTravelAccountNumber(String travelAccountNumber) {
        this.travelAccountNumber = travelAccountNumber;
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

    public TravelAccount getAccount() {
        return this.account;
    }

    public void setAccount(TravelAccount account) {
        this.account = account;
    }
}
