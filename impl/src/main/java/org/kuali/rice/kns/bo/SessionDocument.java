/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;


/*
 * Defines methods a business object should implement.
 */
public class SessionDocument extends PersistableBusinessObjectBase{
    
	private static final long serialVersionUID = 2866566562262830639L;
	
	private String documentNumber;
	private String sessionId;
	private Timestamp lastUpdatedDate;
	private byte[] serializedDocumentForm;
	//private KualiDocumentFormBase serializedDocumentForm;
	
	
	/**
	 * @return the serializedDocumentForm
	 */
	public byte[] getSerializedDocumentForm() {
		return this.serializedDocumentForm;
	}

	/**
	 * @param serializedDocumentForm the serializedDocumentForm to set
	 */
	public void setSerializedDocumentForm(byte[] serializedDocumentForm) {
		this.serializedDocumentForm = serializedDocumentForm;
	}


	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	
	/**
	 * @return the lastUpdatedDate
	 */
	public Timestamp getLastUpdatedDate() {
		return this.lastUpdatedDate;
	}

	/**
	 * @param lastUpdatedDate the lastUpdatedDate to set
	 */
	public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
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


	
	 protected LinkedHashMap toStringMapper() {
	        LinkedHashMap m = new LinkedHashMap();
	        m.put("documentNumber", this.documentNumber);
	        m.put("sessionId", this.sessionId);
	        return m;
	    }

}
