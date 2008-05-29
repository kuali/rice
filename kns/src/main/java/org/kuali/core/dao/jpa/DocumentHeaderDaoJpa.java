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
package org.kuali.core.dao.jpa;

import java.sql.Date;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.DocumentHeaderDao;

/**
 * This class is the JPA implementation of the DocumentHeaderDao interface.
 */
public class DocumentHeaderDaoJpa implements DocumentHeaderDao {
	
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentHeaderDaoJpa.class);

	@PersistenceContext
	private EntityManager entityManager;
	
    /**
     * @see org.kuali.dao.DocumentHeaderDao#getByDocumentHeaderId(java.lang.Long)
     */
    public DocumentHeader getByDocumentHeaderId(String id) {
		org.kuali.rice.jpa.criteria.Criteria criteria = new org.kuali.rice.jpa.criteria.Criteria(DocumentHeader.class.getName());
		criteria.eq("FDOC_NBR", id);		
		return (DocumentHeader) new org.kuali.rice.jpa.criteria.QueryByCriteria(entityManager, criteria).toQuery().getSingleResult();
    }

    /**
     * @see org.kuali.dao.DocumentHeaderDao#getCorrectingDocumentHeader(java.lang.Long)
     */
    public DocumentHeader getCorrectingDocumentHeader(String documentId) {
		org.kuali.rice.jpa.criteria.Criteria criteria = new org.kuali.rice.jpa.criteria.Criteria(DocumentHeader.class.getName());
		criteria.eq("financialDocumentInErrorNumber", documentId);		
		return (DocumentHeader) new org.kuali.rice.jpa.criteria.QueryByCriteria(entityManager, criteria).toQuery().getSingleResult();
    }

    /**
     * @see org.kuali.core.dao.DocumentHeaderDao#getByDocumentFinalDate(Date documentFinalDate)
     */
    public Collection getByDocumentFinalDate(Date documentFinalDate) {
		org.kuali.rice.jpa.criteria.Criteria criteria = new org.kuali.rice.jpa.criteria.Criteria(DocumentHeader.class.getName());
		criteria.eq(RicePropertyConstants.DOCUMENT_FINAL_DATE, documentFinalDate);		
		return new org.kuali.rice.jpa.criteria.QueryByCriteria(entityManager, criteria).toQuery().getResultList();
    }

}