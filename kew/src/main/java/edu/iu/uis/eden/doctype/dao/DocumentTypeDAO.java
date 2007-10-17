/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.doctype.dao;

import java.util.Collection;
import java.util.List;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DocumentTypeDAO {

    public DocumentType findByDocId(Long docId);

    public DocumentType findByName(String name);

    public void save(DocumentType documentType);

    public Collection find(DocumentType documentType, DocumentType docTypeParentName, boolean climbHierarchy);

    public void delete(DocumentType documentType);

    public List findByRouteHeaderId(Long routeHeaderId);

    public DocumentType getMostRecentDocType(String docTypeName);

    public Integer getMaxVersionNumber(String docTypeName);
    
    public DocumentType getMostRecentDocType(Long documentTypeId);
    
    public List findAllCurrentRootDocuments();
    
    public List findAllCurrent();
    
    public List getChildDocumentTypeIds(Long parentDocumentTypeId);
    
    public List findDocumentTypeAttributes(RuleAttribute ruleAttribute);
 
    public Long findDocumentTypeIdByDocumentId(Long documentId);
}