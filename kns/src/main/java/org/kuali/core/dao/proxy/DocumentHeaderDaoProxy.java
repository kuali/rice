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
package org.kuali.core.dao.proxy;

import java.sql.Date;
import java.util.Collection;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.DocumentHeaderDao;
import org.kuali.rice.util.OrmUtils;

public class DocumentHeaderDaoProxy implements DocumentHeaderDao {
    private DocumentHeaderDao documentHeaderDaoJpa;
    private DocumentHeaderDao documentHeaderDaoOjb;
	
    private DocumentHeaderDao getDao(Class clazz) {
    	return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? documentHeaderDaoJpa : documentHeaderDaoOjb; 
    }
    
    public DocumentHeader getByDocumentHeaderId(String id) {    	
    	return getDao(DocumentHeader.class).getByDocumentHeaderId(id);
    }

    public DocumentHeader getCorrectingDocumentHeader(String documentId) {
    	return getDao(DocumentHeader.class).getCorrectingDocumentHeader(documentId);
    }

    public Collection getByDocumentFinalDate(Date documentFinalDate) {
    	return getDao(DocumentHeader.class).getByDocumentFinalDate(documentFinalDate);
    }

	public void setDocumentHeaderDaoJpa(DocumentHeaderDao documentHeaderDaoJpa) {
		this.documentHeaderDaoJpa = documentHeaderDaoJpa;
	}

	public void setDocumentHeaderDaoOjb(DocumentHeaderDao documentHeaderDaoOjb) {
		this.documentHeaderDaoOjb = documentHeaderDaoOjb;
	}

}