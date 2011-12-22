/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentViewAuthorizerBase;
import org.kuali.rice.krad.document.DocumentViewPresentationControllerBase;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;

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

	private Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass;

	public DocumentView() {
		super();
	}

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Retrieve the document entry</li>
     * <li>Set up the document view authorizer and presentation controller</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        // get document entry
        DocumentEntry documentEntry = getDocumentEntryForView();
        pushObjectToContext(UifConstants.ContextVariableNames.DOCUMENT_ENTRY, documentEntry);

        // setup authorizer and presentation controller using the configured authorizer and pc for document
        if (getAuthorizer() == null) {
            setAuthorizer(new DocumentViewAuthorizerBase());
        }

        if (getAuthorizer() instanceof DocumentViewAuthorizerBase) {
            DocumentViewAuthorizerBase documentViewAuthorizerBase = (DocumentViewAuthorizerBase) getAuthorizer();
            if (documentViewAuthorizerBase.getDocumentAuthorizer() == null) {
                documentViewAuthorizerBase.setDocumentAuthorizerClass(documentEntry.getDocumentAuthorizerClass());
            }
        }

        if (getPresentationController() == null) {
            setPresentationController(new DocumentViewPresentationControllerBase());
        }

        if (getPresentationController() instanceof DocumentViewPresentationControllerBase) {
            DocumentViewPresentationControllerBase documentViewPresentationControllerBase =
                    (DocumentViewPresentationControllerBase) getPresentationController();
            if (documentViewPresentationControllerBase.getDocumentPresentationController() == null) {
                documentViewPresentationControllerBase.setDocumentPresentationControllerClass(
                        documentEntry.getDocumentPresentationControllerClass());
            }
        }
    }

    /**
     * Retrieves the associated {@link DocumentEntry} for the document view
     *
     * @return DocumentEntry entry (exception thrown if one is not found)
     */
    protected DocumentEntry getDocumentEntryForView() {
        DocumentEntry documentEntry = KRADServiceLocatorWeb.getDocumentDictionaryService().getDocumentEntryByClass(
                getDocumentClass());

        if (documentEntry == null) {
            throw new RuntimeException(
                    "Unable to find document entry for document class: " + getDocumentClass().getName());
        }

        return documentEntry;
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

}
