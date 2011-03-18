/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch;

import java.io.Serializable;

/**
 * This class contains all the information needed for document search, validation and indexing. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentSearchContext implements Serializable {

	protected String documentId;
	protected String documentTypeName;
	protected String documentContent;
	
	/**
	 * @return the documentContent
	 */
	public String getDocumentContent() {
		return this.documentContent;
	}
	/**
	 * @param documentContent the documentContent to set
	 */
	public void setDocumentContent(String documentContent) {
		this.documentContent = documentContent;
	}
	/**
	 * @return the documentId
	 */
	public String getDocumentId() {
		return this.documentId;
	}
	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	/**
	 * @return the documentTypeName
	 */
	public String getDocumentTypeName() {
		return this.documentTypeName;
	}
	/**
	 * @param documentTypeName the documentTypeName to set
	 */
	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}
	
}
