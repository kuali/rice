/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.removereplace.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kew.removereplace.RemoveReplaceDocument;
import org.kuali.rice.kew.removereplace.dao.RemoveReplaceDocumentDAO;


/**
 * This is a description of what this class does .
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RemoveReplaceDocumentDAOJpaImpl implements RemoveReplaceDocumentDAO {

	@PersistenceContext(unitName="kew-unit")
	EntityManager entityManager;
	
    public void save(RemoveReplaceDocument document) {
    	entityManager.persist(document);
    }

    public RemoveReplaceDocument findById(Long documentId) {
    	Query query = entityManager.createNamedQuery("RemoveReplaceDocument.FindByDocumentId");
    	query.setParameter("documentId", documentId);
    	return (RemoveReplaceDocument)query.getSingleResult();
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
