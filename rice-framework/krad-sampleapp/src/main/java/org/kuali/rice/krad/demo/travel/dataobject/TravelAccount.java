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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.VersionedAndGloballyUniqueBase;
import org.kuali.rice.krad.data.provider.annotation.InheritProperties;
import org.kuali.rice.krad.data.provider.annotation.InheritProperty;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.OptionsFinderClass;
import org.kuali.rice.krad.data.provider.annotation.Relationship;
import org.kuali.rice.krad.data.provider.annotation.Summary;
import org.kuali.rice.krad.data.provider.annotation.ValidCharactersConstraintBeanName;
import org.kuali.rice.krad.demo.travel.options.AccountTypeKeyValues;

@Entity
@Table(name="TRV_ACCT")
public class TravelAccount extends VersionedAndGloballyUniqueBase implements Serializable {
	private static final long serialVersionUID = -7739303391609093875L;

	@Id
	@Column(name="ACCT_NUM",length=10)
	@Label("Travel Account Number")
	@Summary("Unique identifier for account")
	//@ConstraintText("Must not be more than 10 characters")
	@ValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
	private String number;

	@Column(name="SUB_ACCT",length=10)
	@Label("Travel Sub Account Number")
	private String subAccount;

	@Column(name="ACCT_NAME",length=50)
	@Label("Account Name")
	private String name;

	@Column(name="SUB_ACCT_NAME",length=50)
	private String subAccountName;

	@Column(name="SUBSIDIZED_PCT",length=5,precision=2)
	private KualiPercent subsidizedPercent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_DT")
	@Label("Date Created")
	private Date createDate;

    @Column(name="ACCT_FO_ID",length=14,precision=0)
    @Size(max=5)
	private String foId;

    @Relationship(foreignKeyFields="foId")
    @Transient
    @InheritProperties({
    		@InheritProperty(name="principalName",label=@Label("Fiscal Officer User ID")),
    		@InheritProperty(name="name",label=@Label("Fiscal Officer Name"))
    })
	private Person fiscalOfficer;

	@Column(name="ACCT_TYPE",length=3)
	@Label("Travel Account Type Code")
	@Summary("Type code grouping for account")
	@OptionsFinderClass(AccountTypeKeyValues.class)
    protected String accountTypeCode;

    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH})
	@JoinColumn(name="ACCT_TYPE", insertable=false, updatable=false)
    @InheritProperty(name="codeAndDescription")
	private TravelAccountType accountType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Person getFiscalOfficer() {
    	fiscalOfficer = KimApiServiceLocator.getPersonService().updatePersonIfNecessary(foId, fiscalOfficer);
        return fiscalOfficer;
    }

    public String getFoId() {
        return foId;
    }

    public void setFoId(String foId) {
        this.foId = foId;
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

    public KualiPercent getSubsidizedPercent() {
        return this.subsidizedPercent;
    }

    public void setSubsidizedPercent(KualiPercent subsidizedPercent) {
        this.subsidizedPercent = subsidizedPercent;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	public Date getCreateDate() {
        return this.createDate;
    }

	public TravelAccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(TravelAccountType accountType) {
		this.accountType = accountType;
	}

	public String getAccountTypeCode() {
		return accountTypeCode;
	}

	public void setAccountTypeCode(String accountTypeCode) {
		this.accountTypeCode = accountTypeCode;
	}

}
