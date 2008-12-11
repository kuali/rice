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
package org.kuali.rice.kns.web.struts.action;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.document.authorization.DocumentActionFlags;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.exception.DocumentAuthorizationException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

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
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanCopy()) {
            throw buildAuthorizationException("copy", document);
        }

        ((Copyable) tmpForm.getTransactionalDocument()).toCopy();

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected void populateAuthorizationFields(KualiDocumentFormBase formBase){
    	super.populateAuthorizationFields(formBase);
    	Document document = formBase.getDocument();
    	if (formBase.isFormDocumentInitialized()) {
    		
        	Person user = GlobalVariables.getUserSession().getPerson();
        	
        	DocumentPresentationController documentPresentationController = KNSServiceLocator.getDocumentPresentationControllerService().getDocumentPresentationController(document);
            TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(document);
            Set<String> editModes = documentPresentationController.getEditMode(document);
            Map editMode = this.convertSetToMap(editModes);
            
            if (KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getClass().getName()).getUsePessimisticLocking()) {
                editMode = KNSServiceLocator.getDocumentPessimisticLockerService().establishLocks(document, editMode, user);
            }
            formBase.setEditingMode(editMode);
    	}
    	
      
        if (formBase.getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
                throw new DocumentAuthorizationException(GlobalVariables.getUserSession().getPerson().getName(), "view", document.getDocumentHeader().getDocumentNumber());
        }
            
        
    }
}