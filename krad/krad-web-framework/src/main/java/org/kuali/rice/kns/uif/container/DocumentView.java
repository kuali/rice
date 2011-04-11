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
package org.kuali.rice.kns.uif.container;

import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;

/**
 * View type for KRAD documents
 * 
 * <p>
 * Provides commons configuration and default behavior applicable to documents
 * in the KRAD module.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentView extends FormView {
	private static final long serialVersionUID = 2251983409572774175L;

	private Class<? extends Document> documentClass;

	private boolean allowsNoteAttachments = true;
	private boolean allowsNoteFYI = false;
	private boolean displayTopicFieldInNotes = false;

	// TODO: figure out what this is used for
	protected Class<? extends DerivedValuesSetter> derivedValuesSetterClass;
	private Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass;

	public DocumentView() {
		super();
	}

	public Class<? extends Document> getDocumentClass() {
		return this.documentClass;
	}

	public void setDocumentClass(Class<? extends Document> documentClass) {
		this.documentClass = documentClass;
	}

	public boolean isAllowsNoteAttachments() {
		return this.allowsNoteAttachments;
	}

	public void setAllowsNoteAttachments(boolean allowsNoteAttachments) {
		this.allowsNoteAttachments = allowsNoteAttachments;
	}

	public boolean isAllowsNoteFYI() {
		return this.allowsNoteFYI;
	}

	public void setAllowsNoteFYI(boolean allowsNoteFYI) {
		this.allowsNoteFYI = allowsNoteFYI;
	}

	public boolean isDisplayTopicFieldInNotes() {
		return this.displayTopicFieldInNotes;
	}

	public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
		this.displayTopicFieldInNotes = displayTopicFieldInNotes;
	}

	public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass() {
		return this.attachmentTypesValuesFinderClass;
	}

	public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass) {
		this.attachmentTypesValuesFinderClass = attachmentTypesValuesFinderClass;
	}

	public Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass() {
		return this.derivedValuesSetterClass;
	}

	public void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetterClass) {
		this.derivedValuesSetterClass = derivedValuesSetterClass;
	}

}
