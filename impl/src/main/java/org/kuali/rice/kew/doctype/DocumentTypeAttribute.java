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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.WorkflowPersistable;
import org.kuali.rice.kew.rule.RuleAttribute;
import org.kuali.rice.kew.rule.service.RuleAttributeService;


/**
 * Data bean representing an attribute associated at the document type level.  e.g. NoteAttribute, 
 * EmailAttribute, SearchableAttribute, etc.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="EN_DOC_TYP_ATTRIB_T")
public class DocumentTypeAttribute implements WorkflowPersistable, Comparable {

	private static final long serialVersionUID = -4429421648373903566L;

	@Id
	@Column(name="DOC_TYP_ATTRIB_ID")
	private Long documentTypeAttributeId; 
    @Column(name="RULE_ATTRIB_ID")
	private Long ruleAttributeId;
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="RULE_ATTRIB_ID", insertable=false, updatable=false)
	private RuleAttribute ruleAttribute;
    @Column(name="DOC_TYP_ID")
	private Long documentTypeId;
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DOC_TYP_ID", insertable=false, updatable=false)
	private DocumentType documentType;
    @Column(name="ORD_INDX")
	private int orderIndex;
    @Transient
    private Integer lockVerNbr;
    
	/**
	 * @param documentTypeAttributeId The documentTypeAttributeId to set.
	 */
	public void setDocumentTypeAttributeId(Long documentTypeAttributeId) {
		this.documentTypeAttributeId = documentTypeAttributeId;
	}

	/**
	 * @return Returns the documentTypeAttributeId.
	 */
	public Long getDocumentTypeAttributeId() {
		return documentTypeAttributeId;
	}

	/**
	 * @param documentTypeId The documentTypeId to set.
	 */
	public void setDocumentTypeId(Long documentTypeId) {
		this.documentTypeId = documentTypeId;
	}

	/**
	 * @return Returns the documentTypeId.
	 */
	public Long getDocumentTypeId() {
		return documentTypeId;
	}

	/**
	 * @param ruleAttributeId The ruleAttributeId to set.
	 */
	public void setRuleAttributeId(Long ruleAttributeId) {
		this.ruleAttributeId = ruleAttributeId;
        if (ruleAttributeId == null) {
        	ruleAttribute = null;
        } else {
            ruleAttribute = getRuleAttributeService().findByRuleAttributeId(ruleAttributeId);
        }
	}

	/**
	 * @return Returns the ruleAttributeId.
	 */
	public Long getRuleAttributeId() {
		return ruleAttributeId;
	}

	/**
	 * @param ruleAttribute The ruleAttribute to set.
	 */
	public void setRuleAttribute(RuleAttribute ruleAttribute) {
		this.ruleAttribute = ruleAttribute;
	}

	/**
	 * @return Returns the ruleAttribute.
	 */
	public RuleAttribute getRuleAttribute() {
		return ruleAttribute;
	}

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.WorkflowPersistable#copy(boolean)
	 */
	public Object copy(boolean preserveKeys) {
		return null;
	}

    private RuleAttributeService getRuleAttributeService() {
        return (RuleAttributeService) KEWServiceLocator.getService(KEWServiceLocator.RULE_ATTRIBUTE_SERVICE);
    }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
        if (o instanceof DocumentTypeAttribute) {
            return this.getRuleAttribute().getName().compareTo(((DocumentTypeAttribute) o).getRuleAttribute().getName());
        }
        return 0;
     }

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	
    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

}

