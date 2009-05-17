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
package org.kuali.rice.kim.lookup;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = 1L;
	
	private DocumentHelperService documentHelperService;
	
    @Override
	public boolean allowsNewOrCopyAction(String documentTypeName) {
		// TODO : to let it rendering 'create new' and 'edit'/'copy' button
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(documentTypeName);
        DocumentPresentationController documentPresentationController = getDocumentHelperService().getDocumentPresentationController(documentTypeName);
        // make sure this person is authorized to initiate
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        return documentPresentationController.canInitiate(documentTypeName) && documentAuthorizer.canInitiate(documentTypeName, currentUser);

	}

	protected DocumentHelperService getDocumentHelperService() {
	    if ( documentHelperService == null ) {
	        documentHelperService = KNSServiceLocator.getDocumentHelperService();
		}
	    return this.documentHelperService;
	}

}