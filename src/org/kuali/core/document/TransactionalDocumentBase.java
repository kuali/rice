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
package org.kuali.core.document;

import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the base class implementation for all transaction processing eDocs. 
 */
public abstract class TransactionalDocumentBase extends DocumentBase implements TransactionalDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionalDocumentBase.class);

    /**
     * Default constructor.
     */
    public TransactionalDocumentBase() {
        super();
    }

    /**
     * @see org.kuali.core.document.TransactionalDocument#getAllowsCopy()
     * Checks if copy is set to true in data dictionary and the document instance implements
     * Copyable.
     */
    public boolean getAllowsCopy() {
        return KNSServiceLocator.getTransactionalDocumentDictionaryService().getAllowsCopy(this).booleanValue() && this instanceof Copyable;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocument#getAllowsErrorCorrection()
     * Checks if error correction is set to true in data dictionary and the document instance implements
     * Correctable. Furthermore, a document cannot be error corrected twice.
     */
    public boolean getAllowsErrorCorrection() {
        boolean allowErrorCorrection = KNSServiceLocator.getTransactionalDocumentDictionaryService().getAllowsErrorCorrection(this).booleanValue() && this instanceof Correctable;
        allowErrorCorrection = allowErrorCorrection && getDocumentHeader().getCorrectedByDocumentId() == null;

        return allowErrorCorrection;
    }

    /**
     * @see org.kuali.core.document.Correctable#toErrorCorrection()
     */
    public void toErrorCorrection() throws WorkflowException, IllegalStateException {
        if (!this.getAllowsErrorCorrection()) {
            throw new IllegalStateException(this.getClass().getName() + " does not support document-level error correction");
        }

        String sourceDocumentHeaderId = getDocumentNumber();
        setNewDocumentHeader();
        getDocumentHeader().setFinancialDocumentInErrorNumber(sourceDocumentHeaderId);
        addCopyErrorDocumentNote("error-correction for document " + sourceDocumentHeaderId);
    }
    
}