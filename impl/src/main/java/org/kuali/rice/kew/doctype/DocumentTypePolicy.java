/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.doctype;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.kew.bo.WorkflowPersistable;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * Model bean representing a policy of a document type.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@IdClass(org.kuali.rice.kew.doctype.DocumentTypePolicyId.class)
@Entity
@Table(name="KREW_DOC_TYP_PLCY_RELN_T")
public class DocumentTypePolicy implements WorkflowPersistable {

	private static final long serialVersionUID = -4612246888683336474L;
	@Id
	@Column(name="DOC_TYP_ID")
	private Long documentTypeId;
    @Id
	@Column(name="DOC_PLCY_NM")
	private String policyName;
    @Column(name="PLCY_NM")
	private Boolean policyValue;
    @Transient
    private Boolean inheritedFlag;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;

    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DOC_TYP_ID",updatable=false,insertable=false)
	private DocumentType documentType;

    public DocumentTypePolicy() {
    }

    public DocumentTypePolicy(String policyName, Boolean policyValue) {
        this.policyName = policyName;
        this.policyValue = policyValue;
    }

    public String getPolicyDisplayValue() {
        if(policyValue != null){
            if(policyValue.booleanValue()){
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

    public boolean isDefaultApprove() {
        return KEWConstants.DEFAULT_APPROVE_POLICY.equals(policyName);
    }

    public boolean isDisApprove() {
        return KEWConstants.DISAPPROVE_POLICY.equals(policyName);
    }

    public boolean isPreApprove() {
        return KEWConstants.PREAPPROVE_POLICY.equals(policyName);
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        /* Cleanse the input.
         * This is surely not the best way to validate the policy name;
         * it would probably be better to use typesafe enums accross the board
         * but that would probably entail refactoring large swaths of code, not
         * to mention reconfiguring OJB (can typesafe enums be used?) and dealing
         * with serialization compatibility issues (if any).
         * So instead, let's just be sure to fail-fast.
         */
        DocumentTypePolicyEnum policy = DocumentTypePolicyEnum.lookup(policyName);
        this.policyName = policy.getName();
    }

    public Boolean getPolicyValue() {
        return policyValue;
    }

    public void setPolicyValue(Boolean policyValue) {
        this.policyValue = policyValue;
    }

    public Object copy(boolean preserveKeys) {
        DocumentTypePolicy clone = new DocumentTypePolicy();

        if(preserveKeys && documentTypeId != null){
            clone.setDocumentTypeId(new Long(documentTypeId.longValue()));
        }
        if(policyName != null){
            clone.setPolicyName(new String(policyName));
        }

        if(policyValue != null){
            clone.setPolicyValue(new Boolean(policyValue.booleanValue()));
        }

        return clone;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
}
