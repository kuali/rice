/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kew.stats.dao.impl;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.stats.Stats;
import org.kuali.rice.kew.stats.dao.StatsDAO;
import org.kuali.rice.kew.api.KewApiConstants;
import org.omg.CORBA.PUBLIC_MEMBER;

import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This is a description of what this class does - ddean don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StatsDaoJpa implements StatsDAO {

    public static final String STATS_DOCUMENTS_ROUTED_REPORT = "select count(drhv) as cnt, drhv.docRouteStatus "
                            + "from DocumentRouteHeaderValue drhv "
                            + "where drhv.createDate between :beginDate and :endDate group by drhv.docRouteStatus";
    public static final String STATS_NUM_ACTIVE_ITEMS_REPORT = "select count(ai) from ActionItem ai";
    public static final String STATS_NUM_INITIATED_DOCS_BY_DOC_TYPE_REPORT = "select count(drhv), dt.name from "
            + "DocumentRouteHeaderValue drhv, DocumentType dt where drhv.createDate > :createDate and "
            + "drhv.documentTypeId = dt.documentTypeId group by dt.name";
    public static final String STATS_NUM_USERS_REPORT = "select count(distinct(uo.workflowId)) from UserOptions uo";
    public static final String STATS_NUM_DOC_TYPES_REPORT =
                    "select count(dt) from DocumentType dt where dt.currentInd = true";

    @PersistenceContext(unitName = "kew")
    private EntityManager entityManager;
    
    @Override
	public void DocumentsRoutedReport(Stats stats, Date begDate, Date endDate) throws SQLException, LookupException {
        Query query = getEntityManager().createQuery(STATS_DOCUMENTS_ROUTED_REPORT);
        query.setParameter("beginDate", new Timestamp(begDate.getTime()));
        query.setParameter("endDate", new Timestamp(endDate.getTime()));
        
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        
        for (Object[] result : resultList) {
            String actionType = result[1].toString();
            String number = result[0].toString();
            if (actionType.equals(KewApiConstants.ROUTE_HEADER_CANCEL_CD)) {
                stats.setCanceledNumber(number);
            } else if (actionType.equals(KewApiConstants.ROUTE_HEADER_DISAPPROVED_CD)) {
                stats.setDisapprovedNumber(number);
            } else if (actionType.equals(KewApiConstants.ROUTE_HEADER_ENROUTE_CD)) {
                stats.setEnrouteNumber(number);
            } else if (actionType.equals(KewApiConstants.ROUTE_HEADER_EXCEPTION_CD)) {
                stats.setExceptionNumber(number);
            } else if (actionType.equals(KewApiConstants.ROUTE_HEADER_FINAL_CD)) {
                stats.setFinalNumber(number);
            } else if (actionType.equals(KewApiConstants.ROUTE_HEADER_INITIATED_CD)) {
                stats.setInitiatedNumber(number);
            } else if (actionType.equals(KewApiConstants.ROUTE_HEADER_PROCESSED_CD)) {
                stats.setProcessedNumber(number);
            } else if (actionType.equals(KewApiConstants.ROUTE_HEADER_SAVED_CD)) {
                stats.setSavedNumber(number);
            }
        }
    }

    @Override
	public void NumActiveItemsReport(Stats stats) throws SQLException, LookupException {
        stats.setNumActionItems(getEntityManager().createQuery(STATS_NUM_ACTIVE_ITEMS_REPORT)
                .getSingleResult().toString());
    }

    @Override
	public void NumInitiatedDocsByDocTypeReport(Stats stats) throws SQLException, LookupException {
        Query query = getEntityManager().createQuery(STATS_NUM_INITIATED_DOCS_BY_DOC_TYPE_REPORT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -29);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);        
        query.setParameter("createDate", new Timestamp(calendar.getTime().getTime()));
        
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        
        List<KeyValue> numDocs = new ArrayList<KeyValue>(resultList.size());
        for (Object[] result : resultList) {
            numDocs.add(new ConcreteKeyValue(result[1].toString(),result[0].toString()));
        }
        
        stats.setNumInitiatedDocsByDocType(numDocs);
    }

    @Override
	public void NumUsersReport(Stats stats) throws SQLException, LookupException {
        stats.setNumUsers(getEntityManager().createQuery(STATS_NUM_USERS_REPORT).getSingleResult().toString());
    }

    @Override
	public void NumberOfDocTypesReport(Stats stats) throws SQLException, LookupException {
        stats.setNumDocTypes(getEntityManager().createQuery(
                STATS_NUM_DOC_TYPES_REPORT).getSingleResult().toString());
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
