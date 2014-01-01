/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.kuali.rice.krad.bo.DocumentHeader;

/**
 * Provides basic functions for interacting with the DocumentHeader object in the Rice application framework.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentHeaderService {

    /**
     * This method retrieves a document header using the given documentHeaderId
     *
     * @param documentHeaderId - the id of the document to retrieve the document header for
     * @return the document header associated with the given document header id
     */
    public DocumentHeader getDocumentHeaderById(String documentHeaderId);


    /**
     * This method saves a document header object and returns the copy after the save.
     * Any code using the document header must use the instance returned from this method
     * for any future operations.
     *
     * @param documentHeader - the document header object to save
     */
    public DocumentHeader saveDocumentHeader(DocumentHeader documentHeader);

    /**
     * This method deletes a document header object
     *
     * @param documentHeader - the document header to be removed
     */
    public void deleteDocumentHeader(DocumentHeader documentHeader);

}
