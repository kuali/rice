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
package org.kuali.rice.kew.actionlist.dao.impl;

import org.kuali.rice.kew.actionlist.dao.ActionListDAO;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

/**
 * JPA implementation of the action list DAO for functions not easily handled by the data layer.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListDAOJpaImpl implements ActionListDAO {

    protected EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount(String principalId) {
        TypedQuery<Long> query = entityManager.createNamedQuery("ActionItem.DistinctDocumentsForPrincipalId",Long.class);
        query.setParameter("principalId",principalId);
        return query.getSingleResult().intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getMaxActionItemDateAssignedAndCountForUser(String principalId) {
        TypedQuery<Object[]> query = (TypedQuery<Object[]>) entityManager.createNamedQuery("ActionItem.GetMaxDateAndCountForPrincipalId", (new Object[0]).getClass() );
        query.setParameter("principalId",principalId);
        return Arrays.asList( query.getSingleResult() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentRouteHeaderValue getMinimalRouteHeader(String documentId) {
        // This graph is defined on the DocumentRouteHeaderValue class.
        EntityGraph<DocumentRouteHeaderValue> entityGraph =
                (EntityGraph<DocumentRouteHeaderValue>) entityManager.createEntityGraph("DocumentRouteHeaderValue.ActionListAttributesOnly");
        TypedQuery<DocumentRouteHeaderValue> query = entityManager.createQuery("SELECT rh FROM DocumentRouteHeaderValue rh WHERE rh.documentId = :documentId", DocumentRouteHeaderValue.class );
        // By using the graph - all properties but those on the graph should have
        // a lazy proxy in place.  Attempting to access any of those *should* cause the
        // rest of the properties to load.
        query.setHint("javax.persistence.fetchgraph", entityGraph);
        query.setParameter("documentId", documentId);
        List<DocumentRouteHeaderValue> result = query.getResultList();
        if ( result.isEmpty() ) {
            return null;
        }
        return result.get(0);
    }

}
