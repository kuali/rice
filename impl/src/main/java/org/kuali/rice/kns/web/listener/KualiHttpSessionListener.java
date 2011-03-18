/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.PessimisticLock;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;


/**
 * This class is used to handle session timeouts where {@link PessimisticLock} objects should
 * be removed from a document 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiHttpSessionListener implements HttpSessionListener {

    /**
     *  EMPTY METHOD IMPLEMENTATION
     */
    public void sessionCreated(HttpSessionEvent se) {
        // no operation required at this time
    }

    /**
     * This method checks for the existence of a document based on session variables and deletes any locks
     * associated with the document that belong to the current user
     * 
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        String documentNumber = (String) se.getSession().getAttribute(KNSConstants.DOCUMENT_HTTP_SESSION_KEY);
        if (StringUtils.isNotBlank(documentNumber)) {
            try {
                // document service needs the usersession to operate but we need the document from document service to verify it exists
                GlobalVariables.setUserSession((UserSession)se.getSession().getAttribute(KNSConstants.USER_SESSION_KEY));
                Document document = KNSServiceLocatorWeb.getDocumentService().getByDocumentHeaderId(documentNumber);
                if (ObjectUtils.isNotNull(document)) {
                    KNSServiceLocatorWeb.getPessimisticLockService().releaseAllLocksForUser(document.getPessimisticLocks(), GlobalVariables.getUserSession().getPerson());
                }
            } catch (WorkflowException e) {
                throw new RuntimeException(e);
            } finally {
                GlobalVariables.setUserSession(null);
            }
           
        }
    }

}

