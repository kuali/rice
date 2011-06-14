/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.rice.krad.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.workflow.service.KualiWorkflowDocument;


/**
 * Interface for {@link DocumentHeaderBase} 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRNS_DOC_HDR_T")
public class DocumentHeader extends PersistableBusinessObjectBase {

    @Id
	@Column(name="DOC_HDR_ID")
	private String documentNumber;
    @Column(name="FDOC_DESC")
	private String documentDescription;
    @Column(name="ORG_DOC_HDR_ID")
	private String organizationDocumentNumber;
    @Column(name="TMPL_DOC_HDR_ID")
	private String documentTemplateNumber;
    @Column(name="EXPLANATION")
	private String explanation;
    
    @Transient
    private KualiWorkflowDocument workflowDocument;

    /**
     * Constructor - creates empty instances of dependent objects
     * 
     */
    public DocumentHeader() {
        super();
    }

    /**
     * 
     * @return workflowDocument
     */
    public KualiWorkflowDocument getWorkflowDocument() {
        if (workflowDocument == null) {
            throw new RiceRuntimeException("The workflow document is null.  This indicates that the DocumentHeader has not been initialized properly.  This can be caused by not retrieving a document using the DocumentService.");
        }

        return workflowDocument;
    }

    /**
     * @return true if the workflowDocument is not null
     */
    public boolean hasWorkflowDocument() {
        return (workflowDocument != null);
    }


    /**
     * 
     * @param workflowDocument
     */
    public void setWorkflowDocument(KualiWorkflowDocument workflowDocument) {
        this.workflowDocument = workflowDocument;
    }

    /**
     * @return the documentNumber
     */
    public String getDocumentNumber() {
        return this.documentNumber;
    }

    /**
     * @param documentNumber the documentNumber to set
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return the documentDescription
     */
    public String getDocumentDescription() {
        return this.documentDescription;
    }

    /**
     * @param documentDescription the documentDescription to set
     */
    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    /**
     * @return the organizationDocumentNumber
     */
    public String getOrganizationDocumentNumber() {
        return this.organizationDocumentNumber;
    }

    /**
     * @param organizationDocumentNumber the organizationDocumentNumber to set
     */
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    /**
     * @return the documentTemplateNumber
     */
    public String getDocumentTemplateNumber() {
        return this.documentTemplateNumber;
    }

    /**
     * @param documentTemplateNumber the documentTemplateNumber to set
     */
    public void setDocumentTemplateNumber(String documentTemplateNumber) {
        this.documentTemplateNumber = documentTemplateNumber;
    }

    /**
     * Gets the explanation attribute. 
     * @return Returns the explanation.
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Sets the explanation attribute value.
     * @param explanation The explanation to set.
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
