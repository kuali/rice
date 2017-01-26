/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentRequestAuthorizationCache;
import org.kuali.rice.krad.document.DocumentViewAuthorizerBase;
import org.kuali.rice.krad.document.DocumentViewPresentationControllerBase;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * View type for KRAD documents.
 *
 * <p>
 * Provides commons configuration and default behavior applicable to documents
 * in the KRAD module.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "documentView", parent = "Uif-DocumentView")
public class DocumentView extends FormView {
	private static final long serialVersionUID = 2251983409572774175L;

	private Class<? extends Document> documentClass;

	private boolean allowsNoteAttachments = true;
	private boolean allowsNoteFYI = false;
	private boolean displayTopicFieldInNotes = false;
    private boolean superUserView = false;

    private Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass;

	public DocumentView() {
		super();

        setRequestAuthorizationCacheClass(DocumentRequestAuthorizationCache.class);
	}

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Retrieve the document entry</li>
     * <li>Makes sure that the header is set.</li>
     * <li>Set up the document view authorizer and presentation controller</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        // get document entry
        DocumentEntry documentEntry = getDocumentEntryForView();
        pushObjectToContext(UifConstants.ContextVariableNames.DOCUMENT_ENTRY, documentEntry);

        // default document type on the header
        String documentTypeName = documentEntry.getDocumentTypeName();
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);

        if (getHeader() != null && StringUtils.isBlank(getHeaderText())) {
            setHeaderText(documentType.getLabel());
        }

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

        getObjectPathToConcreteClassMapping().put(getDefaultBindingObjectPath(), getDocumentClass());
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

    /**
     * Returns the maximum allowed length for explanation notes within the document
     *
     * <p>
     *     The max length for the explanation data is calculated as the difference between the max length of
     *     the entire note and the length of the introduction message plus a whitespace.
     * </p>
     *
     * @return int
     */
    @BeanTagAttribute(name = "explanationDataMaxLength")
    public int getExplanationDataMaxLength() {
        return KRADServiceLocatorWeb.getDataDictionaryService().getAttributeMaxLength(Note.class,
                KRADConstants.NOTE_TEXT_PROPERTY_NAME) -
                (CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                        RiceKeyConstants.MESSAGE_DISAPPROVAL_NOTE_TEXT_INTRO) + KRADConstants.BLANK_SPACE).length();
    }

    /**
     * Gets the document class
     *
     * @return Class<? extends Document> the document class.
     */
    @BeanTagAttribute
	public Class<? extends Document> getDocumentClass() {
		return this.documentClass;
	}

    /**
     * Sets the document class
     *
     * @param documentClass
     */
	public void setDocumentClass(Class<? extends Document> documentClass) {
		this.documentClass = documentClass;
	}

    /**
     * Gets boolean that indicates if the document view allows note attachments
     *
     * @return true if the document view allows note attachments
     */
    @BeanTagAttribute
	public boolean isAllowsNoteAttachments() {
		return this.allowsNoteAttachments;
	}

    /**
     * Sets boolean that indicates if the document view allows note attachments
     *
     * @param allowsNoteAttachments
     */
	public void setAllowsNoteAttachments(boolean allowsNoteAttachments) {
		this.allowsNoteAttachments = allowsNoteAttachments;
	}

    /**
     * Gets boolean that indicates if the document view allows note FYI
     *
     * @return true if the document view allows note FYI
     */
    @BeanTagAttribute
	public boolean isAllowsNoteFYI() {
		return this.allowsNoteFYI;
	}

    /**
     * Sets boolean that indicates if the document view allows note FYI
     *
     * @param allowsNoteFYI
     */
	public void setAllowsNoteFYI(boolean allowsNoteFYI) {
		this.allowsNoteFYI = allowsNoteFYI;
	}

    /**
     * Gets boolean that indicates if the document view displays the topic field in notes
     *
     * @return true if the document view displays the topic field in notes
     */
    @BeanTagAttribute
	public boolean isDisplayTopicFieldInNotes() {
		return this.displayTopicFieldInNotes;
	}

    /**
     * Sets boolean that indicates if the document view displays the topic field in notes
     *
     * @param displayTopicFieldInNotes
     */
	public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
		this.displayTopicFieldInNotes = displayTopicFieldInNotes;
	}

    /**
     * Gets attachment types values finder classs
     *
     * @return attachment types values finder class
     */
    @BeanTagAttribute
	public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass() {
		return this.attachmentTypesValuesFinderClass;
	}

    /**
     * Sets attachment types values finder classs
     *
     * @param attachmentTypesValuesFinderClass
     */
	public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass) {
		this.attachmentTypesValuesFinderClass = attachmentTypesValuesFinderClass;
	}

    /**
     * Indicates whether the view is a super user view, used for
     * KEW functionality
     *
     * @return true if the view is a super user viwe
     */
    public boolean isSuperUserView() {
        return superUserView;
    }

    /**
     * @see DocumentView#isSuperUserView()
     */
    public void setSuperUserView(boolean superUserView) {
        checkMutable(true);
        this.superUserView = superUserView;
    }

}
