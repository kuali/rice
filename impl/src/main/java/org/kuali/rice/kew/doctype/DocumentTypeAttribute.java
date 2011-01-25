/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.service.RuleAttributeService;
import org.kuali.rice.kew.service.KEWServiceLocator;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Data bean representing an attribute associated at the document type level.  e.g. NoteAttribute, 
 * EmailAttribute, SearchableAttribute, etc.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
//@Sequence(name="KREW_DOC_TYP_ATTR_S", property="documentTypeAttributeId")
@Table(name="KREW_DOC_TYP_ATTR_T")
public class DocumentTypeAttribute implements Comparable, Serializable {

	private static final long serialVersionUID = -4429421648373903566L;

	@Id
	@GeneratedValue(generator="KREW_DOC_TYP_ATTR_S")
	@GenericGenerator(name="KREW_DOC_TYP_ATTR_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_DOC_TYP_ATTR_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="DOC_TYP_ATTRIB_ID")
	private Long documentTypeAttributeId; 
    @Column(name="RULE_ATTR_ID",insertable=false, updatable=false)
	private Long ruleAttributeId;
    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RULE_ATTR_ID")
	private RuleAttribute ruleAttribute;
    @Column(name="DOC_TYP_ID",insertable=false, updatable=false)
	private Long documentTypeId;
    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DOC_TYP_ID")
	private DocumentType documentType;
    @Column(name="ORD_INDX")
	private int orderIndex;
    @Transient
    private Integer lockVerNbr;
    
	//@PrePersist
	public void beforeInsert(){
		OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());		
	}

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

