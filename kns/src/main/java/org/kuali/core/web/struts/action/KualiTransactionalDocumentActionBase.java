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
package org.kuali.core.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;

/**
 * This class handles UI actions for all shared methods of transactional documents.
 */
public class KualiTransactionalDocumentActionBase extends KualiDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiTransactionalDocumentActionBase.class);

  
    /**
     * Method that will take the current document and call its copy method if Copyable.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiTransactionalDocumentFormBase tmpForm = (KualiTransactionalDocumentFormBase) form;

        Document document = tmpForm.getDocument();
        TransactionalDocumentActionFlags flags = (TransactionalDocumentActionFlags) getDocumentActionFlags(document);
        if (!flags.getCanCopy()) {
            throw buildAuthorizationException("copy", document);
        }

        ((Copyable) tmpForm.getTransactionalDocument()).toCopy();

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This action method triggers a correct of the transactional document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward correct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiTransactionalDocumentFormBase tmpForm = (KualiTransactionalDocumentFormBase) form;

        Document document = tmpForm.getDocument();
        TransactionalDocumentActionFlags flags = (TransactionalDocumentActionFlags) getDocumentActionFlags(document);
        if (!flags.getCanErrorCorrect()) {
            throw buildAuthorizationException("error correct", document);
        }

        ((Correctable) tmpForm.getTransactionalDocument()).toErrorCorrection();

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

 
}