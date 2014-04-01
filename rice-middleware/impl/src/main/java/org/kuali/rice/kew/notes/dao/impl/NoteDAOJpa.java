/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.notes.dao.impl;

import org.kuali.rice.kew.notes.dao.NoteDAO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class NoteDAOJpa implements NoteDAO {

	EntityManager entityManager;

    public List getNotesByDocumentId(String documentId) {
    	Query query = entityManager.createNamedQuery("KewNote.FindNoteByDocumentId");
    	query.setParameter("documentId", documentId);
        return (List) query.getResultList();        
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
