/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * 
 */
@Entity
@Table(name="KRNS_DOC_TYP_T")
public class DocumentType extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 2866566562262830639L;

    @Id
	@Column(name="DOC_TYP_CD")
	private String documentTypeCode;
    @Column(name="NM")
	private String documentName;
	@Type(type="yes_no")
    @Column(name="ACTV_IND")
	private boolean documentTypeActiveIndicator;
    @OneToMany(cascade={CascadeType.PERSIST})
	private List<DocumentTypeAttribute> documentTypeAttributes;
    
    /**
     * Default no-arg constructor.
     */
    public DocumentType() {
        documentTypeAttributes = new TypedArrayList(DocumentTypeAttribute.class);
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
     * @return the documentName
     */
    public String getDocumentName() {
        return this.documentName;
    }

    /**
     * @param documentName the documentName to set
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * @return the documentTypeActiveIndicator
     */
    public boolean isDocumentTypeActiveIndicator() {
        return this.documentTypeActiveIndicator;
    }

    /**
     * @param documentTypeActiveIndicator the documentTypeActiveIndicator to set
     */
    public void setDocumentTypeActiveIndicator(boolean documentTypeActiveIndicator) {
        this.documentTypeActiveIndicator = documentTypeActiveIndicator;
    }

    /**
     * @return the documentTypeAttributes
     */
    public List<DocumentTypeAttribute> getDocumentTypeAttributes() {
        return this.documentTypeAttributes;
    }

    /**
     * @param documentTypeAttributes the documentTypeAttributes to set
     */
    public void setDocumentTypeAttributes(List<DocumentTypeAttribute> documentTypeAttributes) {
        this.documentTypeAttributes = documentTypeAttributes;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentTypeCode", this.documentTypeCode);
        return m;
    }

}

