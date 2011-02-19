/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.web.spring.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Base form for all <code>DocumentView</code> screens
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentFormBase extends UifFormBase {
	protected String docTypeName;
	protected String annotation = "";

	protected Document document;

	protected List<String> additionalScriptFiles;

	public DocumentFormBase() {
		additionalScriptFiles = new ArrayList<String>();
	}

	public List<String> getAdditionalScriptFiles() {
		return this.additionalScriptFiles;
	}

	public void setAdditionalScriptFiles(List<String> additionalScriptFiles) {
		this.additionalScriptFiles = additionalScriptFiles;
	}

	public String getAnnotation() {
		return this.annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getDocTypeName() {
		return this.docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	/**
	 * Retrieves the principal name (network id) for the document's initiator
	 * 
	 * @return String initiator name
	 */
	public String getDocumentInitiatorNetworkId() {
		String initiatorNetworkId = "";
		if (getWorkflowDocument() != null) {
			String initiatorPrincipalId = getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
			Person initiator = KIMServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
			if (initiator != null) {
				initiatorNetworkId = initiator.getPrincipalName();
			}
		}

		return initiatorNetworkId;
	}

	/**
	 * Retrieves the create date for the forms document and formats for
	 * presentation
	 * 
	 * @return String formatted document create date
	 */
	public String getDocumentCreateDate() {
		String createDateStr = "";
		if (getWorkflowDocument() != null && getWorkflowDocument().getCreateDate() != null) {
			createDateStr = KNSServiceLocator.getDateTimeService().toString(getWorkflowDocument().getCreateDate(),
					"hh:mm a MM/dd/yyyy");
		}

		return createDateStr;
	}

	/**
	 * Retrieves the <code>WorkflowDocument</code> instance from the forms
	 * document instance
	 * 
	 * @return WorkflowDocument for the forms document
	 */
	public KualiWorkflowDocument getWorkflowDocument() {
		return getDocument().getDocumentHeader().getWorkflowDocument();
	}

}
