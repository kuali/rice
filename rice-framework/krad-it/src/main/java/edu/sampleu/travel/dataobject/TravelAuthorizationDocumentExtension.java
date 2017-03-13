/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package edu.sampleu.travel.dataobject;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.krad.data.provider.annotation.ExtensionFor;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "TRVL_AUTH_DOC_EXT_T")
@ExtensionFor(TravelAuthorizationDocument.class)
public class TravelAuthorizationDocumentExtension implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "DOC_HDR_ID", insertable=false, updatable=false)
    protected String documentNumber;

    @Id
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "DOC_HDR_ID", referencedColumnName="TRVL_AUTH_DOC_ID")
    private TravelAuthorizationDocument document;

    @Column(name="ANOTHER_PROP", length=10)
    protected String anotherProperty;

    public String getAnotherProperty() {
        return this.anotherProperty;
    }

    public void setAnotherProperty(String anotherProperty) {
        this.anotherProperty = anotherProperty;
    }

    public TravelAuthorizationDocument getDocument() {
        return this.document;
    }

    public void setDocument(TravelAuthorizationDocument document) {
        this.document = document;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}
