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
package org.kuali.rice.kew.docsearch.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.dao.DocumentSearchDAO;
import org.kuali.rice.kew.doctype.service.DocumentSecurityService;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.springmodules.orm.ojb.OjbFactoryUtils;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class DocumentSearchDAOOjbImpl extends PersistenceBrokerDaoSupport implements DocumentSearchDAO {

    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchDAOOjbImpl.class);

    private static final int DEFAULT_FETCH_MORE_ITERATION_LIMIT = 10;

    public List<DocSearchDTO> getListBoundedByCritera(DocumentSearchGenerator documentSearchGenerator, DocSearchCriteriaDTO criteria, String principalId) throws KEWUserNotFoundException {
        return getList(documentSearchGenerator, criteria, criteria.getThreshold(), principalId);
    }

    public List<DocSearchDTO> getList(DocumentSearchGenerator documentSearchGenerator, DocSearchCriteriaDTO criteria, String principalId) throws KEWUserNotFoundException {
        return getList(documentSearchGenerator, criteria, Integer.valueOf(getSearchResultCap(documentSearchGenerator)), principalId);
    }

    private List<DocSearchDTO> getList(DocumentSearchGenerator documentSearchGenerator, DocSearchCriteriaDTO criteria, Integer searchResultCap, String principalId) throws KEWUserNotFoundException {
        LOG.debug("start getList");
        DocumentSecurityService documentSecurityService = KEWServiceLocator.getDocumentSecurityService();
        List docList = new ArrayList();
        PersistenceBroker broker = null;
        Connection conn = null;
        Statement statement = null;
        Statement searchAttributeStatement = null;
        ResultSet rs = null;
        try {
            broker = getPersistenceBroker(false);
            conn = broker.serviceConnectionManager().getConnection();
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            criteria.setThreshold(searchResultCap);
            if (searchResultCap != null) {
                int fetchLimit = getFetchMoreIterationLimit() * searchResultCap.intValue();
                criteria.setFetchLimit(Integer.valueOf(fetchLimit));
                statement.setFetchSize(searchResultCap.intValue() + 1);
                statement.setMaxRows(fetchLimit + 1);
            } else {
                criteria.setFetchLimit(null);
            }
            PerformanceLogger perfLog = new PerformanceLogger();
            String sql = documentSearchGenerator.generateSearchSql(criteria);
            perfLog.log("Time to generate search sql from documentSearchGenerator class: " + documentSearchGenerator.getClass().getName(), true);
            LOG.info("Executing document search with statement max rows: " + statement.getMaxRows());
            LOG.info("Executing document search with statement fetch size: " + statement.getFetchSize());
            perfLog = new PerformanceLogger();
            rs = statement.executeQuery(sql);
            perfLog.log("Time to execute doc search database query.", true);
            // TODO delyea - look at refactoring
            searchAttributeStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            docList = documentSearchGenerator.processResultSet(searchAttributeStatement, rs, criteria, principalId);
        } catch (SQLException sqle) {
            String errorMsg = "SQLException: " + sqle.getMessage();
            LOG.error("getList() " + errorMsg, sqle);
            throw new RuntimeException(errorMsg, sqle);
        } catch (LookupException le) {
            String errorMsg = "LookupException: " + le.getMessage();
            LOG.error("getList() " + errorMsg, le);
            throw new RuntimeException(errorMsg, le);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOG.warn("Could not close result set.");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOG.warn("Could not close statement.");
                }
            }
            if (searchAttributeStatement != null) {
                try {
                    searchAttributeStatement.close();
                } catch (SQLException e) {
                    LOG.warn("Could not close search attribute statement.");
                }
            }
            if (broker != null) {
                try {
                    OjbFactoryUtils.releasePersistenceBroker(broker, this.getPersistenceBrokerTemplate().getPbKey());
                } catch (Exception e) {
                    LOG.error("Failed closing connection: " + e.getMessage(), e);
                }
            }
        }

        LOG.info("end getlist");
        return docList;
    }

    private int getSearchResultCap(DocumentSearchGenerator docSearchGenerator) {
        int resultCap = docSearchGenerator.getDocumentSearchResultSetLimit();
        String resultCapValue = Utilities.getKNSParameterValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KEWConstants.DOC_SEARCH_RESULT_CAP);
        if (!StringUtils.isBlank(resultCapValue)) {
            try {
                Integer maxResultCap = Integer.parseInt(resultCapValue);
                if (resultCap > maxResultCap) {
                    LOG.warn("Document Search Generator (" + docSearchGenerator.getClass().getName() + ") gives result set cap of " + resultCap + " which is greater than parameter " + KEWConstants.DOC_SEARCH_RESULT_CAP + " value of " + maxResultCap);
                    resultCap = maxResultCap;
                } else if (maxResultCap <= 0) {
                    LOG.warn(KEWConstants.DOC_SEARCH_RESULT_CAP + " was less than or equal to zero.  Please use a positive integer.");
                }
            } catch (NumberFormatException e) {
                LOG.warn(KEWConstants.DOC_SEARCH_RESULT_CAP + " is not a valid number.  Value was " + resultCapValue);
            }
        }
        return resultCap;
    }

    // TODO delyea: use searchable attribute count here?
    private int getFetchMoreIterationLimit() {
        int fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
        String fetchMoreLimitValue = Utilities.getKNSParameterValue(KEWConstants.DEFAULT_KIM_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KEWConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT);
        if (!StringUtils.isBlank(fetchMoreLimitValue)) {
            try {
                fetchMoreLimit = Integer.parseInt(fetchMoreLimitValue);
                if (fetchMoreLimit < 0) {
                    LOG.warn(KEWConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT + " was less than zero.  Please use a value greater than or equal to zero.");
                    fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
                }
            } catch (NumberFormatException e) {
                LOG.warn(KEWConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT + " is not a valid number.  Value was " + fetchMoreLimitValue);
            }
        }
        return fetchMoreLimit;
    }

    //
    //    protected Platform getPlatform() {
    //    	return (Platform)GlobalResourceLoader.getService(KEWServiceLocator.DB_PLATFORM);
    //    }
}