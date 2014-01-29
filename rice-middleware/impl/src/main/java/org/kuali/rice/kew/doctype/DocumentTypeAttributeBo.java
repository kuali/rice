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

import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.api.doctype.DocumentTypeAttributeContract;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;


/**
 * Data bean representing an attribute associated at the document type level.  e.g. NoteAttribute,
 * EmailAttribute, SearchableAttribute, etc.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KREW_DOC_TYP_ATTR_T")
public class DocumentTypeAttributeBo implements DocumentTypeAttributeContract, Comparable, Serializable {

	private static final long serialVersionUID = -4429421648373903566L;

	@Id
    @PortableSequenceGenerator(name = "KREW_DOC_TYP_ATTR_S")
	@GeneratedValue(generator = "KREW_DOC_TYP_ATTR_S")
	@Column(name = "DOC_TYP_ATTRIB_ID")
	private String id;

    @Column(name="RULE_ATTR_ID", insertable = false, updatable = false)
	private String ruleAttributeId;

    @ManyToOne
	@JoinColumn(name = "RULE_ATTR_ID")
	private RuleAttribute ruleAttribute;

    @Column(name = "DOC_TYP_ID", insertable = false, updatable = false)
	private String documentTypeId;

    @ManyToOne
	@JoinColumn(name = "DOC_TYP_ID")
	private DocumentType documentType;

    @Column(name = "ORD_INDX")
	private int orderIndex;

    @Transient
    private Integer lockVerNbr;

	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Returns the id.
	 */
	@Override
    public String getId() {
		return id;
	}

	/**
	 * @param documentTypeId The documentTypeId to set.
	 */
	public void setDocumentTypeId(String documentTypeId) {
		this.documentTypeId = documentTypeId;
	}

	/**
	 * @return Returns the documentTypeId.
	 */
	@Override
    public String getDocumentTypeId() {
        if (documentTypeId == null && getDocumentType() != null) {
            documentTypeId = getDocumentType().getDocumentTypeId();
        }
		return documentTypeId;
	}

	/**
	 * @param ruleAttributeId The ruleAttributeId to set.
	 */
	public void setRuleAttributeId(String ruleAttributeId) {
		this.ruleAttributeId = ruleAttributeId;
        if (ruleAttributeId == null) {
        	ruleAttribute = null;
        } else {
            ruleAttribute = RuleAttribute.from(KewApiServiceLocator.getExtensionRepositoryService().getExtensionById(ruleAttributeId));
            //ruleAttribute = getRuleAttributeService().findByRuleAttributeId(ruleAttributeId);
        }
	}

	/**
	 * @return Returns the ruleAttributeId.
	 */
	public String getRuleAttributeId() {
        if (ruleAttributeId == null && getRuleAttribute() != null) {
            ruleAttributeId = getRuleAttribute().getId();
        }
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
	@Override
    public RuleAttribute getRuleAttribute() {
		return ruleAttribute;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
    public int compareTo(Object o) {
        if (o instanceof DocumentTypeAttributeBo) {
            return this.getRuleAttribute().getName().compareTo(((DocumentTypeAttributeBo) o).getRuleAttribute().getName());
        }
        return 0;
     }

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

    @Override
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

    public static DocumentTypeAttribute to(DocumentTypeAttributeBo documentTypeAttributeBo) {
        if (documentTypeAttributeBo == null) {
            return null;
        }
        DocumentTypeAttribute.Builder builder = DocumentTypeAttribute.Builder.create(documentTypeAttributeBo);
        return builder.build();
    }

    public static DocumentTypeAttributeBo from(DocumentTypeAttribute dta) {
        // DocumentType BO and DTO are not symmetric
        // set what fields we can
        DocumentTypeAttributeBo bo = new DocumentTypeAttributeBo();
        bo.setDocumentTypeId(dta.getDocumentTypeId());
        if (dta.getRuleAttribute() != null) {
            bo.setRuleAttributeId(dta.getRuleAttribute().getId());
            bo.setRuleAttribute(RuleAttribute.from(dta.getRuleAttribute()));
        }
        bo.setId(dta.getId());
        bo.setOrderIndex(dta.getOrderIndex());

        return bo;
    }
}

