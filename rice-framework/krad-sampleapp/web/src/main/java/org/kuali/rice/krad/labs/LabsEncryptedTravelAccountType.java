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
package org.kuali.rice.krad.labs;

import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.NonPersistentProperty;
import org.kuali.rice.krad.data.provider.annotation.ShortLabel;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;
import org.kuali.rice.krad.demo.travel.dataobject.TravelAccountType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Labs version of {@code TravelAccountType} to demonstrate encryption.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="TRV_ACCT_TYPE")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY,UifAutoCreateViewType.LOOKUP})
public class LabsEncryptedTravelAccountType extends DataObjectBase {
    private static final long serialVersionUID = -7663911201081500300L;

    @Id
    @Column(name="ACCT_TYPE",length=3)
    @Label("Travel Account Type Code")
    @ShortLabel("Code")
    @UifValidCharactersConstraintBeanName("AnyCharacterPatternConstraint")
    private String accountTypeCode;

    @Column(name="ACCT_TYPE_NAME",length=40)
    @Label("Account Type Name")
    @ShortLabel("Name")
    @NotNull
    @UifValidCharactersConstraintBeanName("AnyCharacterPatternConstraint")
    private String name;


    public String getAccountTypeCode() {
        return accountTypeCode;
    }


    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @NonPersistentProperty
    @Label("Account Type")
    public String getCodeAndDescription() {
        if (accountTypeCode != null) {
            return accountTypeCode + " - " + name;
        }

        return "";
    }
}