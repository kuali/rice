/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.bo;

import java.util.LinkedHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class is the object used to set attributes on the {@link DocumentType} object
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRNS_DOC_TYP_ATTR_T")
public class DocumentTypeAttribute extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 3620282816261362785L;
    
	@Id
	@Column(name="DOC_TYP_ATTR_ID")
    protected Long id;
	@Column(name="CD")
    protected String key;
	@Column(name="LBL")
    protected String label;
	@Column(name="VAL")
    protected String value;
	@Column(name="ACTV_IND")
	protected boolean active;
	@Column(name="DOC_TYP_CD")
	protected String documentTypeCode;
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DOC_TYP_CD", insertable=false, updatable=false)
	protected DocumentType documentType;

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the documentTypeCode
     */
    public String getDocumentTypeCode() {
        return this.documentTypeCode;
    }

    /**
     * @param documentTypeCode the documentTypeCode to set
     */
    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    /**
	 * @return the documentType
	 */
	public DocumentType getDocumentType() {
		return this.documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	/**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("id", getId());
        m.put("key", getKey());
        m.put("label", getLabel());
        m.put("value", getValue());
        m.put("active", isActive());
        m.put("documentTypeCode", getDocumentTypeCode());

        return m;
    }

}
