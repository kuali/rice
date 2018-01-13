/**
 * Copyright 2005-2018 The Kuali Foundation
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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Model bean representing the valid application document statuses for a document type
 * An instance of this class represents a single valid status for a given document type.
 *
 * The purpose of the Application Document Status is to provide an alternative to the
 * KEW Route Status. Some documents may have a variety of statuses relating to where they are
 * in their lifecycle.  The application document status provides a means to for a document type to have its
 * own set of statuses.
 *
 * A policy defined in the document type definition for a document determines if the Application
 * Document Status is to be used.  In the document definition, a list of valid application statuses
 * for the document may also be defined.  If the list of valid statuses are not defined, then any status
 * value may be assigned by the client.
 *
 * @author Dan Seibert
 */
@Entity
@Table(name = "KREW_DOC_TYP_APP_DOC_STAT_T")
public class ApplicationDocumentStatus extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -2212481684546954746L;

    @EmbeddedId
    private ApplicationDocumentStatusId applicationDocumentStatusId;

    @Column(name = "SEQ_NO")
    private Integer sequenceNumber;

    @ManyToOne
    @JoinColumns( {
            @JoinColumn(name = "CAT_NM", referencedColumnName = "CAT_NM"),
            @JoinColumn(name = "DOC_TYP_ID", referencedColumnName = "DOC_TYP_ID", updatable = false, insertable = false)
    })
    private ApplicationDocumentStatusCategory category;

    @MapsId("documentTypeId")
    @ManyToOne
    @JoinColumn(name = "DOC_TYP_ID")
    private DocumentType documentType;

    /**
     * Just here to keep OJB happy for now.
     */
    @Deprecated
    @Transient
    private String categoryName;

    @Deprecated
    @Transient
    private String documentTypeId;

    @Deprecated
    @Transient
    private String statusName;

    public ApplicationDocumentStatusId getApplicationDocumentStatusId() {
        if (this.applicationDocumentStatusId == null) {
            this.applicationDocumentStatusId = new ApplicationDocumentStatusId();
        }
        return this.applicationDocumentStatusId;
    }

    public void setApplicationDocumentStatusId(ApplicationDocumentStatusId documentStatusId) {
        this.applicationDocumentStatusId = documentStatusId;
    }

    public String getDocumentTypeId() {
        return this.getDocumentType() != null ? getDocumentType().getDocumentTypeId() : "";
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.getApplicationDocumentStatusId().setDocumentTypeId(documentTypeId);
    }

    public String getStatusName() {
        return this.getApplicationDocumentStatusId().getStatusName();
    }

    public void setStatusName(String statusName) {
        this.getApplicationDocumentStatusId().setStatusName(statusName);
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getCategoryName() {
        if (getCategory() == null) {
            return null;
        }
        return getCategory().getApplicationDocumentStatusCategoryId().getCategoryName();
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public ApplicationDocumentStatusCategory getCategory() {
        return category;
    }

    public void setCategory(ApplicationDocumentStatusCategory category) {
        this.category = category;
    }
}
