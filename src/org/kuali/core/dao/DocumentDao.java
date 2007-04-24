/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.dao;

/*
 * Created on Jan 13, 2005
 */

import java.util.Collection;
import java.util.List;

import org.kuali.core.document.Document;

/**
 * This is the data access interface for Document objects.
 * 
 * 
 */
public interface DocumentDao {
    void save(Document document);

    public Document findByDocumentHeaderId(Class clazz, String id);

    public List findByDocumentHeaderIds(Class clazz, List idList);

    public Collection findByDocumentHeaderStatusCode(Class clazz, String statusCode);
    
    public BusinessObjectDao getBusinessObjectDao();

}
