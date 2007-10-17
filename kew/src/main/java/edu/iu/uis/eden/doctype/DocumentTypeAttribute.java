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
package edu.iu.uis.eden.doctype;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleAttributeService;

/**
 * Data bean representing an attribute associated at the document type level.  e.g. NoteAttribute, 
 * EmailAttribute, SearchableAttribute, etc.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentTypeAttribute implements WorkflowPersistable, Comparable {

	private static final long serialVersionUID = -4429421648373903566L;

	private Long documentTypeAttributeId; 
    private Long ruleAttributeId;
    private RuleAttribute ruleAttribute;
    private Long documentTypeId;
    private DocumentType documentType;
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
	 * @see edu.iu.uis.eden.WorkflowPersistable#copy(boolean)
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

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

}
