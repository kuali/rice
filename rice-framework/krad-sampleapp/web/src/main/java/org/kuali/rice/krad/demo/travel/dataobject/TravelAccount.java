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
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;
import org.kuali.rice.krad.demo.travel.options.AccountTypeKeyValues;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="TRV_ACCT")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY,UifAutoCreateViewType.LOOKUP})
public class TravelAccount extends DataObjectBase implements Serializable {
	private static final long serialVersionUID = -7739303391609093875L;

	@Id
	@Column(name="ACCT_NUM",length=10)
	@Label("Travel Account Number")
	@Description("Unique identifier for account")
	@UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
	private String number;

	@Column(name="ACCT_NAME",length=40)
	@Label("Travel Account Name")
	@ForceUppercase
	private String name;


    @Column(name="ACCT_TYPE",length=3)
    @Label("Travel Account Type Code")
    @Description("Type code grouping for account")
    @KeyValuesFinderClass(AccountTypeKeyValues.class)
    @UifDisplayHints({
    	@UifDisplayHint(UifDisplayHintType.RADIO),
    	@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT),
    	@UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    protected String accountTypeCode;

    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH})
    @PrimaryKeyJoinColumn(name="ACCT_TYPE", referencedColumnName = "ACCT_TYPE")
    @InheritProperty(name="codeAndDescription",displayHints=@UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)))
    private TravelAccountType accountType;

    @Column(name="SUBSIDIZED_PCT",length=5,precision=2)
    @UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA))
	private KualiPercent subsidizedPercent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_DT")
	@Label("Date Created")
	private Date createDate;

    @Column(name="ACCT_FO_ID",length=40)
    @Size(max=40)
    @UifDisplayHints({@UifDisplayHint(UifDisplayHintType.HIDDEN), @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA),
    	@UifDisplayHint(value=UifDisplayHintType.SECTION,id="fo",label="Fiscal Officer User ID")})
	private String foId;

    @Relationship(foreignKeyFields="foId")
    @Transient
    @InheritProperties({
    		@InheritProperty(name="principalName",label=@Label("Fiscal Officer")),
    		@InheritProperty(name="name",label=@Label("Fiscal Officer Name"),displayHints=@UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)))
    })
	private Person fiscalOfficer;

    @OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade= {CascadeType.ALL}, mappedBy = "account")
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
        if(subAccounts == null) {
            subAccounts = new ArrayList<TravelSubAccount>();
        }
		return subAccounts;
	}

	public void setSubAccounts(List<TravelSubAccount> subAccounts) {
		this.subAccounts = subAccounts;
	}

}
