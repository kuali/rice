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
package org.kuali.rice.kns.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.dao.DocumentHeaderDao;

/**
 * This class is the JPA implementation of the DocumentHeaderDao interface.
 */
public class DocumentHeaderDaoJpa implements DocumentHeaderDao {
	
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentHeaderDaoJpa.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	/**
     * @see org.kuali.rice.kns.dao.DocumentHeaderDao#getDocumentHeaderBaseClass()
     */
    public Class getDocumentHeaderBaseClass() {
        LOG.debug("Method getDocumentHeaderBaseClass() returning class " + DocumentHeader.class.getName());
        return DocumentHeader.class;
    }
	
    /**
     * @see org.kuali.dao.DocumentHeaderDao#getByDocumentHeaderId(java.lang.Long)
     */
    public DocumentHeader getByDocumentHeaderId(String id) {
		org.kuali.rice.core.jpa.criteria.Criteria criteria = new org.kuali.rice.core.jpa.criteria.Criteria(DocumentHeader.class.getName());
		criteria.eq("FDOC_NBR", id);		
		return (DocumentHeader) new org.kuali.rice.core.jpa.criteria.QueryByCriteria(entityManager, criteria).toQuery().getSingleResult();
    }

}