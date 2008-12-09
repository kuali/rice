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
package org.kuali.rice.kns.document.authorization;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Base class for all TransactionalDocumentAuthorizers.
 */
public class TransactionalDocumentAuthorizerBase extends DocumentAuthorizerBase implements TransactionalDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(TransactionalDocumentAuthorizerBase.class);

    /**
     * Adds settings for transactional-document-specific flags.
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, Person)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling TransactionalDocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
        }
        TransactionalDocumentActionFlags flags = new TransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));

        TransactionalDocument transactionalDocument = (TransactionalDocument) document;
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();

        if (!canCopy(workflowDocument.getDocumentType(), user)) {
            flags.setCanCopy(false);
            flags.setCanErrorCorrect(false);
        }
        else {
            flags.setCanCopy(transactionalDocument.getAllowsCopy() && !workflowDocument.stateIsInitiated());
            flags.setCanErrorCorrect(transactionalDocument.getAllowsErrorCorrection() && (workflowDocument.stateIsApproved() || workflowDocument.stateIsProcessed() || workflowDocument.stateIsFinal()));
        }
        return flags;
    }
    
    @Override
    protected void populatePermissionDetails(Document document, Map<String, String> attributes) {
        super.populatePermissionDetails(document, attributes);
        attributes.put(KimAttributes.NAMESPACE_CODE, getKualiModuleService().getResponsibleModuleService(document.getClass()).getModuleConfiguration().getNamespaceCode() );
        attributes.put(KimAttributes.COMPONENT_NAME, document.getClass().getName());
    }
    
    @Override
    protected void populateRoleQualification(Document document, Map<String, String> attributes) {
        super.populateRoleQualification(document, attributes);
        attributes.put(KimAttributes.NAMESPACE_CODE, getKualiModuleService().getResponsibleModuleService(document.getClass()).getModuleConfiguration().getNamespaceCode() );
        attributes.put(KimAttributes.COMPONENT_NAME, document.getClass().getName());
    }
}
