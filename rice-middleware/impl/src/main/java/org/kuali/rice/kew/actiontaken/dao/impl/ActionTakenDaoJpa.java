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
package org.kuali.rice.kew.actiontaken.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.actiontaken.dao.ActionTakenDao;
import org.kuali.rice.kew.api.action.ActionType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;


/**
 * JPA implementation of the {@link org.kuali.rice.kew.actiontaken.dao.ActionTakenDao}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionTakenDaoJpa implements ActionTakenDao {

	private EntityManager entityManager;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionTakenDaoJpa.class);

    public static final String GET_LAST_ACTION_TAKEN_DATE_NAME = "ActionTakenValue.getLastActionTakenDate";
    public static final String GET_LAST_ACTION_TAKEN_DATE_QUERY =
            "SELECT max(a.actionDate) from ActionTakenValue a where a.documentId = :documentId and a.actionTaken=:actionTaken";

    public Timestamp getLastActionTakenDate(String documentId, ActionType actionType) {
        if (StringUtils.isBlank(documentId) || actionType == null) {
            throw new IllegalArgumentException("Both documentId and actionType must be non-null");
        }
        TypedQuery<Timestamp> query =
                entityManager.createNamedQuery(GET_LAST_ACTION_TAKEN_DATE_NAME, Timestamp.class);
        query.setParameter("documentId", documentId);
        query.setParameter("actionTaken", actionType.getCode());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
