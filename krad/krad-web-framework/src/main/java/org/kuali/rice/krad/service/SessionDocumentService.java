/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import java.sql.Timestamp;

import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.web.spring.form.DocumentFormBase;
import org.kuali.rice.krad.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.workflow.service.KualiWorkflowDocument;



/**
 * Defines the methods common to all AttachmentService implementations
 *
 *
 */

public interface SessionDocumentService {

	/**
     * Returns KualiDocumentFormBase object. It will check userSession first, if it failed then check database
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @return KualiDocumentFormBase
     * @throws
     */
	public KualiDocumentFormBase getDocumentForm( String documentNumber, String docFormKey, UserSession userSession, String ipAddress);
	
	/**
	 * This method retrieve's a document from the user session from the given docId. 
	 */
	public KualiWorkflowDocument getDocumentFromSession(UserSession userSession, String docId);
	
	/**
	 * This method places a document into the user session. 
	 */
	public void addDocumentToUserSession(UserSession userSession, KualiWorkflowDocument document);
	
	/**
     * Delete KualiDocumentFormBase from session and database.
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @throws
     */
	public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress); 
	
	/**
     * Store KualiDocumentFormBase into session and database.
     *
     * @param KualiDocumentFormBase
     * @param userSession
     * @throws
     */
	public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession, String ipAddress);
	
	
	//public void purgeAllSessionDocumentsFromMemory(); 
	
	/**
     * Delete KualiDocumentFormBases from database.
     *
     * @param documentNumber
     * @throws
     */
	public void purgeAllSessionDocuments(Timestamp expirationDate); 

	/**
	 * 
	 * This method stores a UifFormBase into session and database
	 * 
	 * @param form
	 * @param userSession
	 * @param ipAddress
	 */
	public void setDocumentForm(DocumentFormBase form, UserSession userSession, String ipAddress);

	/**
	 * 
     * Returns DocumentFormBase object from the db
	 * 
	 * @param documentNumber
	 * @param docFormKey
	 * @param userSession
	 * @param ipAddress
	 * @return
	 */
	public DocumentFormBase getUifDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress);

}
