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
package org.kuali.rice.kns.dao.proxy;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DocumentDaoProxy implements DocumentDao {

	private static Logger LOG = Logger.getLogger(DocumentDaoProxy.class);

    private BusinessObjectDao businessObjectDao;
    private DocumentDao documentDaoJpa;
    private DocumentDao documentDaoOjb;
	
    private DocumentDao getDao(Class clazz) {
    	return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? documentDaoJpa : documentDaoOjb; 
    }
    
	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#findByDocumentHeaderId(java.lang.Class, java.lang.String)
	 */
	public Document findByDocumentHeaderId(Class clazz, String id) {
		return getDao(clazz).findByDocumentHeaderId(clazz, id);
	}

	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#findByDocumentHeaderIds(java.lang.Class, java.util.List)
	 */
	public List findByDocumentHeaderIds(Class clazz, List idList) {
		return getDao(clazz).findByDocumentHeaderIds(clazz, idList);
	}

	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#getBusinessObjectDao()
	 */
	public BusinessObjectDao getBusinessObjectDao() {
		return businessObjectDao;
	}
    
	public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }
	
	/**
	 * @see org.kuali.rice.kns.dao.DocumentDao#save(org.kuali.rice.kns.document.Document)
	 */
	public void save(Document document) {
		getDao(document.getClass()).save(document);
	}

	public void setDocumentDaoJpa(DocumentDao documentDaoJpa) {
		this.documentDaoJpa = documentDaoJpa;
	}

	public void setDocumentDaoOjb(DocumentDao documentDaoOjb) {
		this.documentDaoOjb = documentDaoOjb;
	}
	
}