/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.docsearch.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.impl.document.lookup.DocumentLookupGenerator;
import org.kuali.rice.kew.docsearch.dao.DocumentSearchDAO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Spring JdbcTemplate implementation of DocumentSearchDAO
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentSearchDAOJdbcImpl implements DocumentSearchDAO {

    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchDAOJdbcImpl.class);
    private static final int DEFAULT_FETCH_MORE_ITERATION_LIMIT = 10;
    
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
    }

    @Override
    public DocumentSearchResults.Builder findDocuments(final DocumentLookupGenerator documentLookupGenerator, final DocumentSearchCriteria criteria, final boolean criteriaModified, final List<RemotableAttributeField> searchFields) {
        final int maxResultCap = getMaxResultCap(criteria);
        try {
            final JdbcTemplate template = new JdbcTemplate(dataSource);

            return template.execute(new ConnectionCallback<DocumentSearchResults.Builder>() {
                @Override
                public DocumentSearchResults.Builder doInConnection(final Connection con) throws SQLException {
                    final Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    try {
                        final int fetchIterationLimit = getFetchMoreIterationLimit();
                        final int fetchLimit = fetchIterationLimit * maxResultCap;
                        statement.setFetchSize(maxResultCap + 1);
                        statement.setMaxRows(fetchLimit + 1);

                        PerformanceLogger perfLog = new PerformanceLogger();
                        String sql = documentLookupGenerator.generateSearchSql(criteria, searchFields);
                        perfLog.log("Time to generate search sql from documentLookupGenerator class: " + documentLookupGenerator
                                .getClass().getName(), true);
                        LOG.info("Executing document search with statement max rows: " + statement.getMaxRows());
                        LOG.info("Executing document search with statement fetch size: " + statement.getFetchSize());
                        perfLog = new PerformanceLogger();
                        final ResultSet rs = statement.executeQuery(sql);
                        try {
                            perfLog.log("Time to execute doc search database query.", true);
                            final Statement searchAttributeStatement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            try {
                           		return documentLookupGenerator.processResultSet(criteria, criteriaModified, searchAttributeStatement, rs, maxResultCap, fetchLimit);
                            } finally {
                                try {
                                    searchAttributeStatement.close();
                                } catch (SQLException e) {
                                    LOG.warn("Could not close search attribute statement.");
                                }
                            }
                        } finally {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                LOG.warn("Could not close result set.");
                            }
                        }
                    } finally {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            LOG.warn("Could not close statement.");
                        }
                    }
                }
            });

        } catch (DataAccessException dae) {
            String errorMsg = "DataAccessException: " + dae.getMessage();
            LOG.error("getList() " + errorMsg, dae);
            throw new RuntimeException(errorMsg, dae);
        } catch (Exception e) {
            String errorMsg = "LookupException: " + e.getMessage();
            LOG.error("getList() " + errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Returns the maximum number of results that should be returned from the document lookup.
     *
     * @param criteria the criteria in which to check for a max results value
     * @return the maximum number of results that should be returned from a document lookup
     */
    protected int getMaxResultCap(DocumentSearchCriteria criteria) {
        int maxResults = KEWConstants.DOCUMENT_LOOKUP_DEFAULT_RESULT_CAP;
        if (criteria.getMaxResults() != null) {
            maxResults = criteria.getMaxResults().intValue();
        }
        String resultCapValue = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_LOOKUP_DETAIL_TYPE, KEWConstants.DOC_SEARCH_RESULT_CAP);
        if (StringUtils.isNotBlank(resultCapValue)) {
            try {
                Integer maxResultCap = Integer.parseInt(resultCapValue);
                if (maxResults > maxResultCap) {
                    LOG.warn("Result set cap of " + maxResults + " is greater than parameter " + KEWConstants.DOC_SEARCH_RESULT_CAP + " value of " + maxResultCap);
                    maxResults = maxResultCap;
                } else if (maxResultCap <= 0) {
                    LOG.warn(KEWConstants.DOC_SEARCH_RESULT_CAP + " was less than or equal to zero.  Please use a positive integer.");
                }
            } catch (NumberFormatException e) {
                LOG.warn(KEWConstants.DOC_SEARCH_RESULT_CAP + " is not a valid number.  Value was " + resultCapValue);
            }
        }
        return maxResults;
    }

    protected int getFetchMoreIterationLimit() {
        int fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
        String fetchMoreLimitValue = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_LOOKUP_DETAIL_TYPE, KEWConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT);
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

}
