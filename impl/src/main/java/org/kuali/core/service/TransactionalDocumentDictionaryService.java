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
package org.kuali.core.service;

import org.kuali.core.document.TransactionalDocument;

/**
 * This interface defines methods that a TransactionalDocumentDictionary Service must provide. Defines the API for the interacting
 * with TransactionalDocument-related entries in the data dictionary.
 */
public interface TransactionalDocumentDictionaryService {
    /**
     * Returns whether or not this document's data dictionary file has flagged it to allow document copies.
     * 
     * @param document
     * @return True if copies are allowed, false otherwise.
     */
    public Boolean getAllowsCopy(TransactionalDocument document);

    /**
     * Returns whether or not this document's data dictionary file has flagged it to allow document error correction of a document
     * (copy and reversal).
     * 
     * @param document
     * @return True if error correction is allowed, false otherwise.
     */
    public Boolean getAllowsErrorCorrection(TransactionalDocument document);

    /**
     * Retrieves a document instance by it's class name.
     * 
     * @param documentTypeName
     * @return A document instance.
     */
    public Class getDocumentClassByName(String documentTypeName);

    /**
     * Retrieves the summary of the transactional document as described in the data dictionary entry.
     * 
     * @param transactionalDocumentTypeName
     * @return The transactional document's summary.
     */
    public String getSummary(String transactionalDocumentTypeName);

    /**
     * Retrieves the full description of the transactional document as described in its data dictionary entry.
     * 
     * @param transactionalDocumentTypeName
     * @return The transactional document's full description.
     */
    public String getDescription(String transactionalDocumentTypeName);

    /**
     * Retrieves the label for the transactional document as described in its data dictionary entry.
     * 
     * @param transactionalDocumentTypeName
     * @return The transactional document's label.
     */
    public String getLabel(String transactionalDocumentTypeName);


    /**
     * @param document
     * @return businessRulesClass associated with the given document's type
     */
    public Class getBusinessRulesClass(TransactionalDocument document);
}