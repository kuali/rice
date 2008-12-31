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

import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;


/**
 * This interface defines methods that a DocumentType service must provide. Performs lookup/translation between documentType name
 * (used within kuali to specify an implementer of the Document interface), documentType class (an actual Class which implements the
 * Document interface), and documentTypeId (used by workflow to specify documentType).
 * 
 * 
 */
public interface DocumentTypeService {
    /**
     * Given a documentTypeName, returns the associated implementation Class.
     * 
     * @param documentTypeName
     * @return Class implementing the Document interface
     * @throws IllegalArgumentException if the given documentTypeName is null
     * @throws UnknownDocumentTypeException if the given documentTypeName isn't mapped to a documentType
     */
    public Class getClassByName(String documentTypeName);

    /**
     * Given a documentClass, returns the associated documentTypeName.
     * 
     * @param documentClass
     * @return documentTypeName
     * @throws IllegalArgumentException if the given documentClass is null
     * @throws UnknownDocumentTypeException if the given documnentClass isn't mapped to a documentType
     */
    public String getDocumentTypeNameByClass(Class documentClass);

    /**
     * Given a documentClass, returns the associated documentTypeCode listed in its data dictionary file. Note that this will not
     * always work properly for maintenance documents, because the data dictionary maps most maintenance document types to the same
     * MaintenanceDocumentBase class (which is in most cases not really a base class because it has very few subclasses... mostly only
     * client specific subclasses).
     * 
     * @param documentClass
     * @return
     * @throws IllegalArgumentException if the given documentClass is null
     * @throws UnknownDocumentTypeException if the given documentClass isn't mapped to a documentTypeCode
     */
    public String getDocumentTypeCodeByClass(Class documentClass);

    /**
     * Given a documentTypeCode, returns the associated DocumentType from the database.
     * 
     * @param documentTypeCode
     * @return DocumentType
     * @throws UnknownDocumentTypeException if the given documentTypeCode isn't mapped to a DocumentType
     */
    public DocumentType getDocumentTypeByCode(String documentTypeCode);

    /**
     * Given a documentTypeName, returns from the database the DocumentType which is associated with that documentTypeName in the
     * data dictionary via its documentTypeCode.
     * 
     * @param documentTypeName
     * @return DocumentType
     * @throws UnknownDocumentTypeException if the given documentTypeCode isn't mapped to a DocumentType
     */
    public DocumentType getDocumentTypeByName(String documentTypeName);

    /**
     * Given a documentTypeCode, returns the associated DocumentType from the database.
     * 
     * @param documentTypeCode
     * @return DocumentType
     */
    public DocumentType getPotentialDocumentTypeByCode(String documentTypeCode);

    /**
     * Given a documentTypeName, returns from the database the DocumentType which is associated with that documentTypeName in the
     * data dictionary via its documentTypeCode.
     * 
     * @param documentTypeName
     * @return DocumentType
     */
    public DocumentType getPotentialDocumentTypeByName(String documentTypeName);
    
    /**
     * @param documentTypeName
     * @return DocumentPresentationController for the given documentType
     */
    public DocumentPresentationController getDocumentPresentationController(String documentTypeName);

    /**
     * @param document
     * @return DocumentPresentationController for the given document's documentType
     */
    public DocumentPresentationController getDocumentPresentationController(Document document);

    /**
     * @param documentTypeName
     * @return DocumentAuthorizer for the given documentType
     */
    public DocumentAuthorizer getDocumentAuthorizer(String documentTypeName);

    /**
     * @param document
     * @return DocumentAuthorizer for the given document's documentType
     */
    public DocumentAuthorizer getDocumentAuthorizer(Document document);
}