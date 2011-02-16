/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.web.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentAuthorizationException;
import org.kuali.rice.kns.exception.UnknownDocumentIdException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Controller
public class KualiDocumentControllerBase {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDocumentControllerBase.class);

    protected static final String[] DOCUMENT_LOAD_COMMANDS = { 
		KEWConstants.ACTIONLIST_COMMAND, 
		KEWConstants.DOCSEARCH_COMMAND, 
		KEWConstants.SUPERUSER_COMMAND, 
		KEWConstants.HELPDESK_ACTIONLIST_COMMAND };

    protected String getBasicViewName() {
    	throw new RuntimeException("A valid view name must be returned.");
    }
    /**
     * This method may be used to funnel all document handling through, we could do useful things like log and record various
     * openings and status Additionally it may be nice to have a single dispatcher that can know how to dispatch to a redirect url
     * for document specific handling but we may not need that as all we should need is the document to be able to load itself based
     * on document id and then which actionforward or redirect is pertinent for the document type.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
	@RequestMapping(params="methodToCall=docHandler")
    public ModelAndView docHandler(@ModelAttribute("KualiForm") KualiTransactionalDocumentFormBase kualiDocumentFormBase, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        String command = kualiDocumentFormBase.getCommand();

        // in all of the following cases we want to load the document
        if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, command) && kualiDocumentFormBase.getDocId() != null) {
            loadDocument(kualiDocumentFormBase);
        }
        else if (KEWConstants.INITIATE_COMMAND.equals(command)) {
            createDocument(kualiDocumentFormBase);
        }
        else {
            LOG.error("docHandler called with invalid parameters");
            throw new IllegalStateException("docHandler called with invalid parameters");
        }

        // attach any extra JS from the data dictionary
        if (LOG.isDebugEnabled()) {
            LOG.debug("kualiDocumentFormBase.getAdditionalScriptFiles(): " + kualiDocumentFormBase.getAdditionalScriptFiles());
        }
        if ( kualiDocumentFormBase.getAdditionalScriptFiles().isEmpty() ) {
            DocumentEntry docEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry( kualiDocumentFormBase.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentType() );
            kualiDocumentFormBase.getAdditionalScriptFiles().addAll(docEntry.getWebScriptFiles());
        }
        if (KEWConstants.SUPERUSER_COMMAND.equalsIgnoreCase(command)) {
            kualiDocumentFormBase.setSuppressAllButtons(true);
        }
//        return mapping.findForward(RiceConstants.MAPPING_BASIC);
        return new ModelAndView(getBasicViewName(), "KualiForm", kualiDocumentFormBase);
    }

    /**
     * This method loads the document by its provided document header id. This has been abstracted out so that it can be overridden
     * in children if the need arises.
     *
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        String docId = kualiDocumentFormBase.getDocId();
        Document doc = null;
        doc = getDocumentService().getByDocumentHeaderId(docId);
        if (doc == null) {
            throw new UnknownDocumentIdException("Document no longer exists.  It may have been cancelled before being saved.");
        }
        KualiWorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
        if (!getDocumentHelperService().getDocumentAuthorizer(doc).canOpen(doc, GlobalVariables.getUserSession().getPerson())) {
        	throw buildAuthorizationException("open", doc);
        }
        // re-retrieve the document using the current user's session - remove the system user from the WorkflowDcument object
        if ( workflowDocument != doc.getDocumentHeader().getWorkflowDocument() ) {
        	LOG.warn( "Workflow document changed via canOpen check" );
        	doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
        }
        kualiDocumentFormBase.setDocument(doc);
        KualiWorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
        kualiDocumentFormBase.setDocTypeName(workflowDoc.getDocumentType());
        // KualiDocumentFormBase.populate() needs this updated in the session
        GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
    }

    /**
     * This method creates a new document of the type specified by the docTypeName property of the given form. This has been
     * abstracted out so that it can be overridden in children if the need arises.
     *
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        Document doc = getDocumentService().getNewDocument(kualiDocumentFormBase.getDocTypeName());

        kualiDocumentFormBase.setDocument(doc);
        kualiDocumentFormBase.setDocTypeName(doc.getDocumentHeader().getWorkflowDocument().getDocumentType());
    }

    /**
     * Convenience method for building authorization exceptions
     *
     * @param action
     * @param document
     */
    protected DocumentAuthorizationException buildAuthorizationException(String action, Document document) {
        return new DocumentAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), action, document.getDocumentNumber());
    }

	public DataDictionaryService getDataDictionaryService() {
		return KNSServiceLocator.getDataDictionaryService();
	}

	public DocumentService getDocumentService() {
		return KNSServiceLocator.getDocumentService();
	}

	public DocumentHelperService getDocumentHelperService() {
		return KNSServiceLocator.getDocumentHelperService();
	}

}
