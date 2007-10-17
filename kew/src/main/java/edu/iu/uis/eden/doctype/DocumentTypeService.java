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

import java.util.List;

import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.xml.export.XmlExporter;

/**
 * Service for data access and some cache behavior of document types.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DocumentTypeService extends DocumentTypeQueryService, XmlExporter {

    public DocumentTypeVO getDocumentTypeVO(Long documentTypeId);
    public DocumentTypeVO getDocumentTypeVO(String documentTypeName);
    public void versionAndSave(DocumentType documentType);
    public void save(DocumentType documentType); 
    public List findAllCurrentRootDocuments();
    public List findAllCurrent();
    public List getChildDocumentTypes(DocumentType documentType);
    public void clearCacheForAttributeUpdate(RuleAttribute ruleAttribute);
}