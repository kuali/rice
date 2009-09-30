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
package org.kuali.rice.kew.doctype;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.kuali.rice.kew.bo.KewPersistableBusinessObjectBase;


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
 * 
 * @author Dan Seibert
 *
 */
@IdClass(org.kuali.rice.kew.doctype.ApplicationDocumentStatusId.class)
@Entity
@Table(name="KREW_DOC_TYP_APP_DOC_STAT_T")
public class ApplicationDocumentStatus extends KewPersistableBusinessObjectBase {
	private static final long serialVersionUID = -2212481684546954746L;

	@Id
	@Column(name="DOC_TYP_ID")
	private Long documentTypeId;
	@Id
	@Column(name="DOC_STAT_NM")
	private String statusName;
 	
	
    public Long getDocumentTypeId() {
		return this.documentTypeId;
	}

	public void setDocumentTypeId(Long documentTypeId) {
		this.documentTypeId = documentTypeId;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentTypeId", this.documentTypeId);
        m.put("statusName", this.statusName);
		return m;
	}
}
