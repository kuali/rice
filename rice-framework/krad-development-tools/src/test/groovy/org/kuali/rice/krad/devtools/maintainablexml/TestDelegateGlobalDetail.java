/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.devtools.maintainablexml;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.bo.GlobalBusinessObjectDetailBase;

import java.sql.Date;

/**
 *
 */
public class TestDelegateGlobalDetail extends GlobalBusinessObjectDetailBase {

    private static final long serialVersionUID = -8089154029664644867L;

    private String accountDelegateUniversalId;
    private String financialDocumentTypeCode;
    private KualiDecimal approvalFromThisAmount;
    private KualiDecimal approvalToThisAmount;
    private boolean accountDelegatePrimaryRoutingIndicator;
    private Date accountDelegateStartDate;

    private Person accountDelegate;
    private transient DocumentTypeEBO financialSystemDocumentTypeCode;

    /**
     * Default constructor.
     */
    public TestDelegateGlobalDetail() {
        super();
    }

    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        if ( StringUtils.isBlank( financialDocumentTypeCode ) ) {
            financialSystemDocumentTypeCode = null;
        } else {
            if ( financialSystemDocumentTypeCode == null || !StringUtils.equals(financialDocumentTypeCode, financialSystemDocumentTypeCode.getName() ) ) {
            }
        }
        return financialSystemDocumentTypeCode;
    }

    /**
     * Gets the accountDelegateUniversalId attribute.
     *
     * @return Returns the accountDelegateUniversalId
     */
    public String getAccountDelegateUniversalId() {
        return accountDelegateUniversalId;
    }

    /**
     * Sets the accountDelegateUniversalId attribute.
     *
     * @param accountDelegateUniversalId The accountDelegateUniversalId to set.
     */
    public void setAccountDelegateUniversalId(String accountDelegateUniversalId) {
        this.accountDelegateUniversalId = accountDelegateUniversalId;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     *
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     *
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the approvalFromThisAmount attribute.
     *
     * @return Returns the approvalFromThisAmount
     */
    public KualiDecimal getApprovalFromThisAmount() {
        return approvalFromThisAmount;
    }

    /**
     * Sets the approvalFromThisAmount attribute.
     *
     * @param approvalFromThisAmount The approvalFromThisAmount to set.
     */
    public void setApprovalFromThisAmount(KualiDecimal approvalFromThisAmount) {
        this.approvalFromThisAmount = approvalFromThisAmount;
    }


    /**
     * Gets the approvalToThisAmount attribute.
     *
     * @return Returns the approvalToThisAmount
     */
    public KualiDecimal getApprovalToThisAmount() {
        return approvalToThisAmount;
    }

    /**
     * Sets the approvalToThisAmount attribute.
     *
     * @param approvalToThisAmount The approvalToThisAmount to set.
     */
    public void setApprovalToThisAmount(KualiDecimal approvalToThisAmount) {
        this.approvalToThisAmount = approvalToThisAmount;
    }


    /**
     * Gets the accountDelegatePrimaryRoutingIndicator attribute.
     *
     * @return Returns the accountDelegatePrimaryRoutingIndicator
     */
    public boolean getAccountDelegatePrimaryRoutingIndicator() {
        return accountDelegatePrimaryRoutingIndicator;
    }

    /**
     * Sets the accountDelegatePrimaryRoutingIndicator attribute.
     *
     * @param accountDelegatePrimaryRoutingIndicator The accountDelegatePrimaryRoutingIndicator to set.
     * @deprecated
     */
    public void setAccountDelegatePrimaryRoutingIndicator(boolean accountDelegatePrimaryRoutingIndicator) {
        this.accountDelegatePrimaryRoutingIndicator = accountDelegatePrimaryRoutingIndicator;
    }


    /**
     * Gets the accountDelegateStartDate attribute.
     *
     * @return Returns the accountDelegateStartDate
     */
    public Date getAccountDelegateStartDate() {
        return accountDelegateStartDate;
    }

    /**
     * Sets the accountDelegateStartDate attribute.
     *
     * @param accountDelegateStartDate The accountDelegateStartDate to set.
     */
    public void setAccountDelegateStartDate(Date accountDelegateStartDate) {
        this.accountDelegateStartDate = accountDelegateStartDate;
    }

    public Person getAccountDelegate() {
        return accountDelegate;
    }

    /**
     * @param accountDelegate The accountDelegate to set.
     * @deprecated
     */
    public void setAccountDelegate(Person accountDelegate) {
        this.accountDelegate = accountDelegate;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                TestDelegateGlobalDetail other = (TestDelegateGlobalDetail) obj;
                if (StringUtils.equalsIgnoreCase(getDocumentNumber(), other.getDocumentNumber())) {
                    if (StringUtils.equalsIgnoreCase(this.financialDocumentTypeCode, other.financialDocumentTypeCode)) {
                        if (this.accountDelegatePrimaryRoutingIndicator == other.accountDelegatePrimaryRoutingIndicator) {
                            if (StringUtils.equalsIgnoreCase(this.accountDelegateUniversalId, other.accountDelegateUniversalId)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}

