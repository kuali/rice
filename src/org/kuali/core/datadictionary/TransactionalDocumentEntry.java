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
package org.kuali.core.datadictionary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TransactionalDocumentEntry
 */
public class TransactionalDocumentEntry extends DocumentEntry {
    // logger
    private static Log LOG = LogFactory.getLog(TransactionalDocumentEntry.class);

    private boolean allowsErrorCorrection;

    public TransactionalDocumentEntry() {
        super();

        LOG.debug("creating new TransactionalDocumentEntry");
    }

    public void setAllowsErrorCorrection(boolean allowsErrorCorrection) {
        LOG.debug("calling setAllowsErrorCorrection '" + allowsErrorCorrection + "'");

        this.allowsErrorCorrection = allowsErrorCorrection;
    }

    public boolean getAllowsErrorCorrection() {
        return allowsErrorCorrection;
    }

    /**
     * Validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DocumentEntry#completeValidation()
     */
    @Override
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils) {
        super.completeValidation(validationCompletionUtils);
    }

    @Override
    public String toString() {
        return "TransactionalDocumentEntry for documentType " + getDocumentTypeName();
    }
}