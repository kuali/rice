/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.doctype.service;

import java.util.List;

import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.bo.RuleAttribute;


/**
 * Service for data access and some cache behavior of document types.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentTypeService extends DocumentTypeQueryService, XmlExporter {

    public void versionAndSave(DocumentType documentType);
    public void save(DocumentType documentType);
    public void save(DocumentType documentType, boolean flushCache);
    public void flushCache();
    public List findAllCurrentRootDocuments();
    public List findAllCurrent();
    public List<DocumentType> findPreviousInstances(String documentTypeName);
    public List getChildDocumentTypes(String documentTypeId);
    public void clearCacheForAttributeUpdate(RuleAttribute ruleAttribute);

    /**
     *
     * This method is similar to the findByName method except it is case insensitive.
     *
     * @param name
     * @return
     */
    public DocumentType findByNameCaseInsensitive(String name);
}
