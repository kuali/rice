/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.doctype;

import java.util.Collection;

import edu.iu.uis.eden.XmlLoader;

/**
 * A service for querying document type stuff for plugins without exposing the document type service.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DocumentTypeQueryService extends XmlLoader {

    public DocumentType findById(Long documentTypeId);
    
    public DocumentType findByName(String name);

    public Collection find(DocumentType documentType, String docGroupName, boolean climbHierarchy);
    
    public DocumentType findRootDocumentType(DocumentType docType);
    
    /**
     * Returns the DocumentType of the Document with the given ID. 
     * 
     * @since 2.3
     */
    public DocumentType findByDocumentId(Long documentId);
    
}
