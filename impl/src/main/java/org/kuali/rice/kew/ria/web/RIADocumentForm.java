/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kew.ria.web;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.ria.bo.RIADocTypeMap;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;


/**
 * PDF Form used by the PDF document.
 * 
 * @author mpk35
 *
 */
public class RIADocumentForm extends KualiTransactionalDocumentFormBase {
	
	private static final long serialVersionUID = 3931062558243923369L;
	private RIADocTypeMap riaDocTypeMap;
	private DocumentType documentType;

	/**
	 * the document type name sent from url
	 */
	private String riaDocTypeName;
	
	public RIADocumentForm() {
        super();
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}


	public RIADocTypeMap getRiaDocTypeMap() {
		return this.riaDocTypeMap;
	}


	public void setRiaDocTypeMap(RIADocTypeMap riaDocTypeMap) {
		this.riaDocTypeMap = riaDocTypeMap;
	}


	public String getRiaDocTypeName() {
		return this.riaDocTypeName;
	}


	public void setRiaDocTypeName(String riaDocTypeName) {
		this.riaDocTypeName = riaDocTypeName;
	}
}
