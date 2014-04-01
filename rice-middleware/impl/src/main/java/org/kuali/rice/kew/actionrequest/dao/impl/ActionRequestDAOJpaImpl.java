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
package org.kuali.rice.kew.actionrequest.dao.impl;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.dao.ActionRequestDAO;
import org.kuali.rice.kew.api.action.ActionRequestStatus;
import org.kuali.rice.kew.api.action.RecipientType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * This is a description of what this class does - sgibson don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionRequestDAOJpaImpl implements ActionRequestDAO {
    
    private EntityManager entityManager;

    /**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	/**
	 * @param entityManager the entityManager to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

    public List<ActionRequestValue> findPendingRootRequestsByDocumentType(String documentTypeId) {
        TypedQuery<ActionRequestValue> query =
                entityManager.createNamedQuery("ActionRequestValue.FindPendingRootRequestsByDocumentType", ActionRequestValue.class);
        query.setParameter("documentTypeId", documentTypeId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("actionRequestStatus1", ActionRequestStatus.INITIALIZED.getCode());
        query.setParameter("actionRequestStatus2", ActionRequestStatus.ACTIVATED.getCode());
        return query.getResultList();
    }

    public List<String> getRequestGroupIds(String documentId) {
        TypedQuery<String> query = entityManager.createNamedQuery("ActionRequestValue.GetRequestGroupIds", String.class);
        query.setParameter("documentId", documentId);
        query.setParameter("currentIndicator", Boolean.TRUE);
        query.setParameter("recipientTypeCd", RecipientType.GROUP.getCode());
        return query.getResultList();
    }

}