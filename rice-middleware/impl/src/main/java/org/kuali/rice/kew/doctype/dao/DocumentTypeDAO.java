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
package org.kuali.rice.kew.doctype.dao;

import java.util.Collection;
import java.util.List;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.bo.RuleAttribute;


/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentTypeDAO {

    /**
     * Find Document Type by name (case sensitive by default)
     * @param name
     * @return DocumentType or null
     */
    DocumentType findByName(String name);

    /**
     * Find Document Type by name
     * @param name
     * @param caseSensitive
     * @return DocumentType or null
     */
    DocumentType findByName(String name, boolean caseSensitive);

    /**
     * Find Document Types by document type and parent name
     * @param documentType
     * @param docTypeParentName
     * @param climbHierarchy
     * @return Collection of matching document types
     */
    Collection<DocumentType> find(DocumentType documentType, DocumentType docTypeParentName, boolean climbHierarchy);

    /**
     * Get Max version number of document type
     * @param docTypeName
     * @return max version number
     */
    Integer getMaxVersionNumber(String docTypeName);

    /**
     * Find all current document types
     * @return List of current DocumentTypes
     */
    List findAllCurrent();

    /**
     * Find all current with name
     * @param name
     * @return List of Document Type by name
     */
    List findAllCurrentByName(String name);

    /**
     * Get Child Document Type ids by parent id
     * @param parentDocumentTypeId
     * @return List of child document type ids
     */
    List<String> getChildDocumentTypeIds(String parentDocumentTypeId);

    /**
     * Find document type id by name
     * @param documentTypeName
     * @return document type id
     */
    String findDocumentTypeIdByName(String documentTypeName);

    /**
     * Find Document type name by id
     * @param documentTypeId
     * @return document type name
     */
    String findDocumentTypeNameById(String documentTypeId);

    /**
     * Find Document Type by document id
     * @param documentId
     * @return DocumentType
     */
    DocumentType findDocumentTypeByDocumentId(String documentId);

    /**
     * Increments the optimstic locking version number for the document type with the given id.
     *
     * @param documentTypeId the id of the document type for which to increment the version number
     */
    void incrementOptimisticLock(String documentTypeId);
}
