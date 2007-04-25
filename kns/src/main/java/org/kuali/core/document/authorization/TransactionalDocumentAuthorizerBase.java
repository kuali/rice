/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

/**
 * Base class for all TransactionalDocumentAuthorizers.
 */
public class TransactionalDocumentAuthorizerBase extends DocumentAuthorizerBase implements TransactionalDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(TransactionalDocumentAuthorizerBase.class);

    /**
     * Adds settings for transactional-document-specific flags.
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        LOG.debug("calling TransactionalDocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");
        TransactionalDocumentActionFlags flags = new TransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));

        TransactionalDocument transactionalDocument = (TransactionalDocument) document;
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();

        LOG.debug("calling canInitiate from getDocumentActionFlags()");

        // if a user can't initiate a document of this type then they can't copy or error-correct one, either
        if (!canCopy(workflowDocument.getDocumentType(), user)) {
            flags.setCanCopy(false);
            flags.setCanErrorCorrect(false);
        }
        else {
            flags.setCanCopy(transactionalDocument.getAllowsCopy() && !workflowDocument.stateIsInitiated());
            flags.setCanErrorCorrect(transactionalDocument.getAllowsErrorCorrection() && (workflowDocument.stateIsApproved() || workflowDocument.stateIsProcessed() || workflowDocument.stateIsFinal()));
        }
        
        // if document implements AmountTotaling interface, then we should display the total
        if (document instanceof AmountTotaling) {
            flags.setHasAmountTotal(true);
        }
        else {
            flags.setHasAmountTotal(false);
        }

        return flags;
    }
}