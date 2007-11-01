/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.routeheader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.dao.CannotAcquireLockException;
import org.springmodules.orm.ojb.OjbFactoryUtils;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.database.platform.Platform;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.LockingException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValueContent;

public class DocumentRouteHeaderDAOOjbImpl extends PersistenceBrokerDaoSupport implements DocumentRouteHeaderDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentRouteHeaderDAOOjbImpl.class);

    public void saveRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.getPersistenceBrokerTemplate().store(routeHeader);
        routeHeader.getDocumentContent().setRouteHeaderId(routeHeader.getRouteHeaderId());
        this.getPersistenceBrokerTemplate().store(routeHeader.getDocumentContent());
    }

    public DocumentRouteHeaderValueContent getContent(Long routeHeaderId) {
    	Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        return (DocumentRouteHeaderValueContent)this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(DocumentRouteHeaderValueContent.class, crit));
    }

    public void clearRouteHeaderSearchValues(DocumentRouteHeaderValue routeHeader) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeader.getRouteHeaderId());
        this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(SearchableAttributeValue.class, crit));
    }

    public void lockRouteHeader(final Long routeHeaderId, final boolean wait) {

        /*
         * String sql = (wait ? LOCK_SQL_WAIT : LOCK_SQL_NOWAIT); try { getJdbcTemplate().update(sql, new Object[] { routeHeaderId }); } catch (CannotAcquireLockException e) { throw new LockingException("Could not aquire lock on document, routeHeaderId=" + routeHeaderId, e); }
         */

    	this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
                PreparedStatement statement = null;
                try {
                    Connection connection = broker.serviceConnectionManager().getConnection();
                    String sql = getPlatform().getLockRouteHeaderQuerySQL(routeHeaderId, wait);
                    statement = connection.prepareStatement(sql);
                    statement.setLong(1, routeHeaderId.longValue());
                    statement.execute();
                    return null;
                } catch (SQLException e) {
                    throw new LockingException("Could not aquire lock on document, routeHeaderId=" + routeHeaderId, e);
                } catch (LookupException e) {
                    throw new LockingException("Could not aquire lock on document, routeHeaderId=" + routeHeaderId, e);
                } catch (CannotAcquireLockException e) {
                    throw new LockingException("Could not aquire lock on document, routeHeaderId=" + routeHeaderId, e);
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
        });

    }

    public DocumentRouteHeaderValue findRouteHeader(Long routeHeaderId) {
    	return findRouteHeader(routeHeaderId, false);
    }

    public DocumentRouteHeaderValue findRouteHeader(Long routeHeaderId, boolean clearCache) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        if (clearCache) {
        	this.getPersistenceBrokerTemplate().clearCache();
        }
        return (DocumentRouteHeaderValue) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(DocumentRouteHeaderValue.class, crit));
    }


    public void deleteRouteHeader(DocumentRouteHeaderValue routeHeader) {
    	// need to clear action list cache for users who have this item in their action list
    	ActionListService actionListSrv = KEWServiceLocator.getActionListService();
    	Collection actionItems = actionListSrv.findByRouteHeaderId(routeHeader.getRouteHeaderId());
    	for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
    		ActionItem actionItem = (ActionItem) iter.next();
    		try {
    			KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getUser());
    		} catch (EdenUserNotFoundException e) {
    			LOG.error("error saving refreshUserOption", e);
    		}
    	}
    	this.getPersistenceBrokerTemplate().delete(routeHeader);
    }

    public Long getNextRouteHeaderId() {
        return (Long)this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
            	return getPlatform().getNextValSQL("SEQ_DOCUMENT_ROUTE_HEADER", broker);
                    }
        });
    }

    protected Platform getPlatform() {
    	return (Platform)GlobalResourceLoader.getService(KEWServiceLocator.DB_PLATFORM);
    }

    public Collection findPendingByResponsibilityIds(Set responsibilityIds) {
        Collection routeHeaderIds = new ArrayList();
        if (responsibilityIds.isEmpty()) {
            return routeHeaderIds;
        }
        PersistenceBroker broker = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            broker = getPersistenceBroker(false);
            conn = broker.serviceConnectionManager().getConnection();
            String respIds = "(";
            int index = 0;
            for (Iterator iterator = responsibilityIds.iterator(); iterator.hasNext(); index++) {
                Long responsibilityId = (Long) iterator.next();
                respIds += responsibilityId + (index == responsibilityIds.size()-1 ? "" : ",");
            }
            respIds += ")";
            String query = "SELECT DISTINCT(doc_hdr_id) FROM EN_ACTN_RQST_T "+
            	"WHERE (ACTN_RQST_STAT_CD='" +
            	EdenConstants.ACTION_REQUEST_INITIALIZED+
            	"' OR ACTN_RQST_STAT_CD='"+
            	EdenConstants.ACTION_REQUEST_ACTIVATED+
            	"') AND ACTN_RQST_RESP_ID IN "+respIds;
            LOG.debug("Query to find pending documents for requeue: " + query);
            rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                routeHeaderIds.add(new Long(rs.getLong(1)));
            }
            rs.close();
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw new WorkflowRuntimeException(sqle);
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw new WorkflowRuntimeException(le);
        } finally {
            try {
                if (broker != null) {
                    OjbFactoryUtils.releasePersistenceBroker(broker, this.getPersistenceBrokerTemplate().getPbKey());
                }
            } catch (Exception e) {
                LOG.error("Failed closing connection: " + e.getMessage(), e);
            }
        }
        return routeHeaderIds;
    }


    public boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue) {
    	Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", documentId);
        crit.addEqualTo("searchableAttributeKey", searchableAttributeKey);
        crit.addEqualTo("searchableAttributeKey", searchableAttributeValue);
        int count = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(SearchableAttributeValue.class, crit));
        return count > 0;
    }

    public String getMessageEntityByDocumentId(Long documentId) {
    	if (documentId == null) {
    		throw new IllegalArgumentException("Encountered a null document ID.");
    	}
    	String messageEntity = null;
        PersistenceBroker broker = null;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            broker = this.getPersistenceBroker(false);
            conn = broker.serviceConnectionManager().getConnection();
            String query = "SELECT DT.MESSAGE_ENTITY_NM FROM EN_DOC_TYP_T DT, EN_DOC_HDR_T DH "+
            	"WHERE DH.DOC_TYP_ID=DT.DOC_TYP_ID AND "+
            	"DH.DOC_HDR_ID=?";
            statement = conn.prepareStatement(query);
            statement.setLong(1, documentId);
            rs = statement.executeQuery();
            if (rs.next()) {
                messageEntity = rs.getString(1);
                if (rs.wasNull()) {
                	messageEntity = null;
                }
            }
            rs.close();
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw new WorkflowRuntimeException(sqle);
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw new WorkflowRuntimeException(le);
        } finally {
            try {
                if (broker != null) {
                    OjbFactoryUtils.releasePersistenceBroker(broker, this.getPersistenceBrokerTemplate().getPbKey());
                }
            } catch (Exception e) {
                LOG.error("Failed closing connection: " + e.getMessage(), e);
            }
        }
        return messageEntity;
    }

    public String getDocumentStatus(Long documentId) {
	Criteria crit = new Criteria();
    	crit.addEqualTo("routeHeaderId", documentId);
    	ReportQueryByCriteria query = QueryFactory.newReportQuery(DocumentRouteHeaderValue.class, crit);
    	query.setAttributes(new String[] { "docRouteStatus" });
    	String status = null;
    	Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    	while (iter.hasNext()) {
    	    Object[] row = (Object[]) iter.next();
    	    status = (String)row[0];
    	}
    	return status;
    }

}