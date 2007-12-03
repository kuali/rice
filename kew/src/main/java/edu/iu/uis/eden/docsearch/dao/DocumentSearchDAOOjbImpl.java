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
package edu.iu.uis.eden.docsearch.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.springmodules.orm.ojb.OjbFactoryUtils;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.docsearch.DocSearchCriteriaVO;
import edu.iu.uis.eden.docsearch.DocumentSearchGenerator;
import edu.iu.uis.eden.doctype.DocumentSecurityService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.Utilities;

public class DocumentSearchDAOOjbImpl extends PersistenceBrokerDaoSupport implements DocumentSearchDAO {

    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchDAOOjbImpl.class);

	private static final int DEFAULT_FETCH_MORE_ITERATION_LIMIT = 10;

    public List getList(DocumentSearchGenerator documentSearchGenerator,DocSearchCriteriaVO criteria) throws EdenUserNotFoundException {
        LOG.info("start getList");
        int searchResultCap = getSearchResultCap();
        int fetchMoreIterationLimit = getFetchMoreIterationLimit();
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
            String sql = documentSearchGenerator.generateSearchSql(criteria);
            LOG.info("Executing document search w/fetch size="+searchResultCap+": " + sql);
            int fetchLimit = fetchMoreIterationLimit * searchResultCap;
            criteria.setThreshhold(searchResultCap);
            criteria.setFetchLimit(fetchLimit);
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            statement.setMaxRows(fetchLimit+1);
            statement.setFetchSize(searchResultCap+1);
            PerformanceLogger perfLog = new PerformanceLogger();
            rs = statement.executeQuery(sql);
            perfLog.log("Time to execute doc search database query.", true);
            // TODO delyea - look at refactoring
            searchAttributeStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            docList = documentSearchGenerator.processResultSet(searchAttributeStatement, rs, criteria);
        } catch (SQLException sqle) {
        	String errorMsg = "SQLException: " + sqle.getMessage();
            LOG.error("getList() " + errorMsg, sqle);
            throw new RuntimeException(errorMsg,sqle);
        } catch (LookupException le) {
        	String errorMsg = "LookupException: " + le.getMessage();
            LOG.error("getList() " + errorMsg, le);
            throw new RuntimeException(errorMsg,le);
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

    private int getSearchResultCap() {
    	int resultCap = DocSearchCriteriaVO.DEFAULT_SEARCH_RESULT_CAP;
    	String resultCapValue = Utilities.getApplicationConstant(EdenConstants.DOC_SEARCH_RESULT_CAP_KEY);
    	if (!StringUtils.isBlank(resultCapValue)) {
    		try {
    			resultCap = Integer.parseInt(resultCapValue);
    			if (resultCap <= 0) {
    				LOG.warn(EdenConstants.DOC_SEARCH_RESULT_CAP_KEY + " was less than or equal to zero.  Please use a positive integer.");
    				resultCap = DocSearchCriteriaVO.DEFAULT_SEARCH_RESULT_CAP;
    			}
    		} catch (NumberFormatException e) {
    			LOG.warn(EdenConstants.DOC_SEARCH_RESULT_CAP_KEY + " is not a valid number.  Value was " + resultCapValue);
    		}
    	}
    	return resultCap;
    }

    private int getFetchMoreIterationLimit() {
    	int fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
    	String fetchMoreLimitValue = Utilities.getApplicationConstant(EdenConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT_KEY);
    	if (!StringUtils.isBlank(fetchMoreLimitValue)) {
    		try {
    			fetchMoreLimit = Integer.parseInt(fetchMoreLimitValue);
    			if (fetchMoreLimit < 0) {
    				LOG.warn(EdenConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT_KEY + " was less than zero.  Please use a value greater than or equal to zero.");
    				fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
    			}
    		} catch (NumberFormatException e) {
    			LOG.warn(EdenConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT_KEY + " is not a valid number.  Value was " + fetchMoreLimitValue);
    		}
    	}
    	return fetchMoreLimit;
    }

//
//    protected Platform getPlatform() {
//    	return (Platform)GlobalResourceLoader.getService(KEWServiceLocator.DB_PLATFORM);
//    }
}