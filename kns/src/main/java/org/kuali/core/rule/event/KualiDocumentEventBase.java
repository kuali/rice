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
package org.kuali.core.rule.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.RicePropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.util.ObjectUtils;

/**
 * Abstract superclass for document-related events.
 */
abstract public class KualiDocumentEventBase implements KualiDocumentEvent {
    private static final Logger LOG = Logger.getLogger(KualiDocumentEventBase.class);

    private final String description;
    private final String errorPathPrefix;
    protected Document document;

    /**
     * 
     * As a general rule, business rule classes should not change the original object. This constructor was created so that
     * PreRulesCheckEvent, a UI level rule checker, can make changes.
     * 
     * @param description
     * @param errorPathPrefix
     */
    protected KualiDocumentEventBase(String description, String errorPathPrefix) {

        if (!(this instanceof PreRulesCheckEvent)) {
            throw new Error("THIS CONSTRUCTOR SHOULD ONLY BE USED AT THE UI LAYER");
        }

        this.description = description;
        this.errorPathPrefix = errorPathPrefix;
    }

    /**
     * Constructs a KualiEvent with the given description and errorPathPrefix for the given document.
     * 
     * @param errorPathPrefix
     * @param document
     * @param description
     */
    public KualiDocumentEventBase(String description, String errorPathPrefix, Document document) {
        this.description = description;
        this.errorPathPrefix = errorPathPrefix;

        try {
            // by doing a deep copy, we are ensuring that the business rule class can't update
            // the original object by reference
            this.document = (Document) ObjectUtils.deepCopy(document);
            // have to manually set the FlexDoc b/c it is transient and the deepCopy won't actually copy that object over
            // for a serialization based copy
            this.document.getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        }
        catch (Exception e) {
            LOG.warn( "Unable to perform deep copy on document", e);
            // just set to the passed in document
            this.document = document;
            // throw new RuntimeException("Failed to invoke deep copy of document.", e);
        }

        LOG.debug(description);
    }


    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getDocument()
     */
    public final Document getDocument() {
        return document;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getName()
     */
    public final String getName() {
        return this.getClass().getName();
    }

    /**
     * @return a description of this event
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getErrorPathPrefix()
     */
    public String getErrorPathPrefix() {
        return errorPathPrefix;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        if (getDocument() == null) {
            throw new IllegalArgumentException("invalid (null) event document");
        }
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#generateEvents()
     */
    public List generateEvents() {
        return new ArrayList();
    }

    /**
     * Provides null-safe access to the documentNumber of the given document.
     * 
     * @param document
     * @return String containing the documentNumber of the given document, or some indication of why the documentNumber isn't
     *         accessible
     */
    protected static String getDocumentId(Document document) {
        String docId = "(null document)";

        if (document != null) {
            String documentNumber = document.getDocumentNumber();
            if (StringUtils.isBlank(documentNumber)) {
                docId = "(blank " + RicePropertyConstants.DOCUMENT_NUMBER + ")";
            }
            else {
                docId = documentNumber;
            }
        }

        return docId;
    }
}