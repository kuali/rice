/*
 * Copyright 2006-2014 The Kuali Foundation
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

import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TRV_ACCT")
public class AccountNonUpdatableFK extends DataObjectBase {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
    @PortableSequenceGenerator(name = "KRTST_GENERATED_PK_S")
    @Column(name="acct_num")
	private String number;

	@Column(name="acct_name")
    private String name;

	@Column(name="acct_fo_id", insertable = false, updatable = false)
    private Long accountManagerId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="acct_fo_id")
    private AccountManagerGeneratedPK accountManager;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAccountManagerId() {
        return accountManagerId;
    }

    public void setAccountManagerId(Long accountManagerId) {
        this.accountManagerId = accountManagerId;
    }

    public AccountManagerGeneratedPK getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManagerGeneratedPK accountManager) {
        this.accountManager = accountManager;
    }

}
