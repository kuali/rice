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

import java.util.List;

import org.kuali.rice.kns.document.Document;

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

}
