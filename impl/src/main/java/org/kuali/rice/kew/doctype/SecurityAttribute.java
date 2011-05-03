/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.doctype;

import java.io.Serializable;

import org.kuali.rice.kim.bo.Person;


/**
 * This is an attribute used to implement custom document security for document search and the route log.
 * SecurityAttributes are configured to be associated with the document type against which they should
 * be applied.  For each route log or row that is returned from a document search, this authorization
 * methods will be executed. 
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface SecurityAttribute extends Serializable {

    /**
     * Determines whether or not a Person is authorized to see a given row in document search results.
     * The row being checked corresponds to the documentId given.
     *
     * @param currentUser the Person who is executing the search
     * @param docTypeName the name of the Document Type of the Document being checked for authorization
     * @param documentId the ID of the Document to check authorization for
     * @param initiatorPrincipalId the principal ID of the initiator of the document
     * 
     * @return true if the Person is authorized to view the row in document search, false otherwise
     */
    public Boolean docSearchAuthorized(Person currentUser, String docTypeName, String documentId, String initiatorPrincipalId);

    /**
     * Determines whether or not a Person is authorized to open the route log for the document with the given ID.
     *
     * @param currentUser the Person who is attempting to view the route log
     * @param docTypeName the name of the Document Type of the Document being checked for authorization
     * @param documentId the ID of the Document that the user is trying to view the route log for
     * @param initiatorPrincipalId the principal ID of the initiator of the document
     * 
     * @return true if the Person is authorized to view the route log, false otherwise
     */
    public Boolean routeLogAuthorized(Person currentUser, String docTypeName, String documentId, String initiatorPrincipalId);

}
