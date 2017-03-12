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
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * FiscalOfficer
 */
@Entity
@Table(name = "TRV_ACCT_FO")
public class AccountManagerGeneratedPK extends DataObjectBase {
	private static final long serialVersionUID = 1555425302284842267L;

    @Id
    @GeneratedValue(generator = "TRV_FO_ID_S")
    @PortableSequenceGenerator(name = "TRV_FO_ID_S")
    @Column(name = "ACCT_FO_ID")
    private Long id;

    @Column(name="ACCT_FO_USER_NAME")
	private String userName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "accountManager")
	private List<AccountNonUpdatableFK> accounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<AccountNonUpdatableFK> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountNonUpdatableFK> accounts) {
        this.accounts = accounts;
    }
}
