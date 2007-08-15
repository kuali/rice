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

package org.kuali.core.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.RiceConstants;
import org.kuali.RicePropertyConstants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.KualiWorkflowDocument;


/**
 * Document Header Business Object
 * 
 * 
 */
public class DocumentHeader extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 8330320294549662887L;

    private String documentNumber;
    private String financialDocumentStatusCode;
    private String financialDocumentDescription;
    private KualiDecimal financialDocumentTotalAmount;
    private String organizationDocumentNumber;
    private String financialDocumentInErrorNumber;
    private String financialDocumentTemplateNumber;
    // TODO: remove following field from here, OJB, and database after workflow API to retrieve this is implemented
    private Date documentFinalDate;
    private String explanation;
    
    private transient KualiWorkflowDocument workflowDocument;
    private DocumentStatus documentStatus;
    
    private String correctedByDocumentId;

    /**
     * Constructor - creates empty instances of dependent objects
     * 
     */
    public DocumentHeader() {
        financialDocumentStatusCode = RiceConstants.DocumentStatusCodes.INITIATED;
    }

    /**
     * 
     * @return flexdoc
     */
    public KualiWorkflowDocument getWorkflowDocument() {
        if (workflowDocument == null) {
            throw new RuntimeException("transient FlexDoc is null - this should never happen");
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
     * 
     * @return documentDescription
     */
    public String getFinancialDocumentDescription() {
        return financialDocumentDescription;
    }

    /**
     * 
     * @param documentDescription
     */
    public void setFinancialDocumentDescription(String financialDocumentDescription) {
        this.financialDocumentDescription = financialDocumentDescription;
    }

    /**
     * 
     * @return documentHeaderId
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * 
     * @param documentHeaderId
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * 
     * @return
     */
    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    /**
     * 
     * @param organizationDocumentNumber
     */
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }


    /**
     * @return documentHeaderId of the document from which this document was copied
     */
    public String getFinancialDocumentTemplateNumber() {
        return financialDocumentTemplateNumber;
    }

    /**
     * @param copiedFromDocumentId
     */
    public void setFinancialDocumentTemplateNumber(String setFinancialDocumentTemplateNumber) {
        this.financialDocumentTemplateNumber = setFinancialDocumentTemplateNumber;
    }

    /**
     * @return documentHeaderId of the document which corrects this document
     */
    public String getCorrectedByDocumentId() {
        return correctedByDocumentId;
    }

    /**
     * @param correctedByDocumentId
     */
    public void setCorrectedByDocumentId(String correctedByDocumentId) {
        this.correctedByDocumentId = correctedByDocumentId;
    }

    /**
     * @return documentHeaderId of the document which this document corrects
     */
    public String getFinancialDocumentInErrorNumber() {
        return financialDocumentInErrorNumber;
    }

    /**
     * @param correctedDocumentId
     */
    public void setFinancialDocumentInErrorNumber(String financialDocumentInErrorNumber) {
        this.financialDocumentInErrorNumber = financialDocumentInErrorNumber;
    }


    /**
     * @return Returns the documentStatusCode.
     */
    public String getFinancialDocumentStatusCode() {
        return financialDocumentStatusCode;
    }

    /**
     * @param documentStatusCode The documentStatusCode to set.
     */
    public void setFinancialDocumentStatusCode(String financialDocumentStatusCode) {
        this.financialDocumentStatusCode = financialDocumentStatusCode;
    }

    /**
     * Gets the financialDocumentTotalAmount attribute.
     * 
     * @return Returns the financialDocumentTotalAmount
     * 
     */
    public KualiDecimal getFinancialDocumentTotalAmount() {
        return financialDocumentTotalAmount;
    }

    /**
     * Sets the financialDocumentTotalAmount attribute.
     * 
     * @param financialDocumentTotalAmount The financialDocumentTotalAmount to set.
     * 
     */
    public void setFinancialDocumentTotalAmount(KualiDecimal financialDocumentTotalAmount) {
        this.financialDocumentTotalAmount = financialDocumentTotalAmount;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(RicePropertyConstants.DOCUMENT_NUMBER, documentNumber);

        return m;
    }

    /**
     * Gets the documentFinalDate attribute.
     * 
     * @return Returns the documentFinalDate.
     */
    public Date getDocumentFinalDate() {
        return documentFinalDate;
    }

    /**
     * Sets the documentFinalDate attribute value.
     * 
     * @param documentFinalDate The documentFinalDate to set.
     */
    public void setDocumentFinalDate(Date documentFinalDate) {
        this.documentFinalDate = documentFinalDate;
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

    /**
     * Gets the documentStatus attribute. 
     * @return Returns the documentStatus.
     */
    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    /**
     * Sets the documentStatus attribute value.
     * @param documentStatus The documentStatus to set.
     * @deprecated
     */
    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }

}