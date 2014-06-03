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
package org.kuali.rice.kew.doctype;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.Boolean01BigDecimalConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import static org.kuali.rice.kew.api.doctype.DocumentTypePolicy.*;

/**
 * Model bean representing a policy of a document type.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@IdClass(DocumentTypePolicyId.class)
@Table(name="KREW_DOC_TYP_PLCY_RELN_T")
public class DocumentTypePolicy extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = -4612246888683336474L;

    @Transient
    private String documentTypeId;

    @Id
    @Column(name = "DOC_PLCY_NM", nullable = false)
    private String policyName;

    @Column(name="PLCY_NM", nullable = false)
    @Convert(converter=Boolean01BigDecimalConverter.class)
    private Boolean policyValue;

    @Column(name="PLCY_VAL")
    private String policyStringValue;

    @Id
    @JoinColumn(name="DOC_TYP_ID", nullable = false)
    @ManyToOne
    private DocumentType documentType;

    @Transient private Boolean inheritedFlag;

    public DocumentTypePolicy() {}

    public DocumentTypePolicy(String documentTypeId, String policyName, Boolean policyValue) {
        this.documentTypeId = documentTypeId;
        this.policyName = policyName;
        this.policyValue = policyValue;
    }

    public String getPolicyDisplayValue() {
        if (policyValue != null) {
            if (policyValue.booleanValue()) {
                return "Active";
            } else {
                return "Inactive";
            }
        }
        return "Inherited";
    }

    public Boolean getInheritedFlag() {
        return inheritedFlag;
    }

    public void setInheritedFlag(Boolean inheritedFlag) {
        this.inheritedFlag = inheritedFlag;
    }

    public boolean isAllowUnrequestedAction() {
        return ALLOW_UNREQUESTED_ACTION.equals(this.getPolicyName());
    }

    public boolean isDefaultApprove() {
        return DEFAULT_APPROVE.equals(this.getPolicyName());
    }

    public boolean isDisApprove() {
        return DISAPPROVE.getCode().equals(this.getPolicyName());
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        /* Cleanse the input.
         * This is surely not the best way to validate the policy name;
         * it would probably be better to use typesafe enums across the board
         * but that would probably entail refactoring large swaths of code, not
         * to mention reconfiguring OJB (can typesafe enums be used?) and dealing
         * with serialization compatibility issues (if any).
         * So instead, let's just be sure to fail-fast.
         */
        org.kuali.rice.kew.api.doctype.DocumentTypePolicy policy = fromCode(policyName);
        this.policyName = policy.getCode();
    }

    public Boolean getPolicyValue() {
        return policyValue;
    }

    public void setPolicyValue(Boolean policyValue) {
        this.policyValue = policyValue;
    }

    public String getPolicyStringValue() {
        return policyStringValue;
    }

    public void setPolicyStringValue(String policyStringValue) {
        this.policyStringValue = policyStringValue;
    }

    /**
     * Return the actual value from the policy
     *
     * If there is a policy string value it will return it, else return the policy value (a boolean).
     * This was needed for building the XML representation to return from the document type service.
     *
     * @return string of policy value
     */
    public String getActualPolicyValue() {
        if (this.policyStringValue != null && this.policyStringValue.length() > 0) {
            return this.policyStringValue;
        }

        return this.policyValue.toString();
    }

    public Object copy(boolean preserveKeys) {
        DocumentTypePolicy clone = new DocumentTypePolicy();
        if (preserveKeys) {
            //clone.setDocumentTypeId(getDocumentTypeId());
            clone.setPolicyName(getPolicyName());
        }
        if (policyValue != null) {
            clone.setPolicyValue(new Boolean(policyValue.booleanValue()));
        }
        if (policyStringValue != null) {
            clone.setPolicyStringValue(new String(policyStringValue));
        }
        return clone;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

}
