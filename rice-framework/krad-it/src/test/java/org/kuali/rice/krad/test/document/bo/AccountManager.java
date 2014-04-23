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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * FiscalOfficer
 */
@Entity
@Table(name="TRV_ACCT_FO")
public class AccountManager extends PersistableBusinessObjectBase {
	private static final long serialVersionUID = 1555425302284842267L;

    @Column(name="acct_fo_user_name")
	private String userName;
	@Id
	@GeneratedValue(generator="TRV_FO_ID_S")
	@Column(name="acct_fo_id")
	private Long amId;
	@Transient
	private String defaultType;
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
	@JoinColumn(name="acct_fo_id",referencedColumnName="acct_fo_id",insertable=false,updatable=false)
	private List<Account> accounts;

    public AccountManager() {}



	public AccountManager(Long amId, String userName) {
        super();
        this.amId = amId;
        this.userName = userName;
    }

    public void setUserName(String userId) {
		userName = userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getDefaultType() {
        return this.defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AccountManager)) return false;
        AccountManager am = (AccountManager) o;
        return StringUtils.equals(userName, am.getUserName()) &&
               ObjectUtils.equals(amId, am.getAmId());
	}

	public Long getAmId() {
		return amId;
	}

	public void setAmId(Long id) {
		this.amId = id;
	}

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
