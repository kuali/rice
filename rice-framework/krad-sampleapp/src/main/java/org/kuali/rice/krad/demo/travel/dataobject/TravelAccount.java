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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.ForceUppercase;
import org.kuali.rice.krad.data.provider.annotation.InheritProperties;
import org.kuali.rice.krad.data.provider.annotation.InheritProperty;
import org.kuali.rice.krad.data.provider.annotation.KeyValuesFinderClass;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.Relationship;
import org.kuali.rice.krad.data.provider.annotation.ValidCharactersConstraintBeanName;
import org.kuali.rice.krad.demo.travel.options.AccountTypeKeyValues;

@Entity
@Table(name="TRV_ACCT")
public class TravelAccount extends DataObjectBase implements Serializable {
	private static final long serialVersionUID = -7739303391609093875L;

	@Id
	@Column(name="ACCT_NUM",length=10)
	@Label("Travel Account Number")
	@Description("Unique identifier for account")
	@ValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
	private String number;

	@Column(name="ACCT_NAME",length=40)
	@Label("Account Name")
	@ForceUppercase
	private String name;

	@Column(name="SUBSIDIZED_PCT",length=5,precision=2)
	private KualiPercent subsidizedPercent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_DT")
	@Label("Date Created")
	private Date createDate;

    @Column(name="ACCT_FO_ID",length=40)
    @Size(max=40)
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
	@Description("Type code grouping for account")
	@KeyValuesFinderClass(AccountTypeKeyValues.class)
    protected String accountTypeCode;

    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH})
	@JoinColumn(name="ACCT_TYPE", insertable=false, updatable=false)
    @InheritProperty(name="codeAndDescription")
	private TravelAccountType accountType;

    @OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade= {CascadeType.ALL} )
	@JoinColumn(name="ACCT_NUM", nullable=false, insertable=false, updatable=false)
    protected List<TravelSubAccount> subAccounts;

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

	public List<TravelSubAccount> getSubAccounts() {
		return subAccounts;
	}

	public void setSubAccounts(List<TravelSubAccount> subAccounts) {
		this.subAccounts = subAccounts;
	}

}
