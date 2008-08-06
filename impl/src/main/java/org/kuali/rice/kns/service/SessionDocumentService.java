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
package org.kuali.rice.kns.service;

import java.sql.Timestamp;


import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.UserSession;



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
	
	public KualiDocumentFormBase getDocumentForm( String documentNumber, String docFormKey, UserSession userSession);
	
	/**
     * Delete KualiDocumentFormBase from session and database.
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @throws
     */
	public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession ); 
	
	/**
     * Store KualiDocumentFormBase into session and database.
     *
     * @param KualiDocumentFormBase
     * @param userSession
     * @throws
     */
	public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession );
	
	
	//public void purgeAllSessionDocumentsFromMemory(); 
	
	/**
     * Delete KualiDocumentFormBases from database.
     *
     * @param documentNumber
     * @throws
     */
	public void purgeAllSessionDocuments(Timestamp expirationDate); 


}