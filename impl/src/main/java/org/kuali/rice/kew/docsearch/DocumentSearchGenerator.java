/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.docsearch;

import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.krad.util.MessageMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


/**
 * TODO delyea - documentation
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentSearchGenerator {
	public static final int DEFAULT_SEARCH_RESULT_CAP = 500;

	public void setSearchingUser(String principalId);
    public List<WorkflowServiceError> performPreSearchConditions(String principalId, DocSearchCriteriaDTO searchCriteria);
    public List<RemotableAttributeError> validateSearchableAttributes(DocSearchCriteriaDTO searchCriteria);
    public String generateSearchSql(DocSearchCriteriaDTO searchCriteria);
    /**
     * @deprecated Removed as of version 0.9.3.  Use {@link DocumentSearchGenerator#processResultSet(java.sql.Statement, java.sql.ResultSet, DocSearchCriteriaDTO, String)}
     */
    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaDTO searchCriteria) throws SQLException;


    /**
     * This method processes search results in the given <code>resultSet</code> into {@link DocSearchDTO} objects
     *
     * @param searchAttributeStatement - statement to use when fetching search attributes
     * @param resultSet - resultSet containing data from document search main tables
     * @param searchCriteria - criteria used to perform the search
     * @param principalId - user who performed the search
     * @return a list of DocSearchDTO objects (one for each document id)
     * @throws SQLException
     */
    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaDTO searchCriteria, String principalId) throws SQLException;
    public DocSearchCriteriaDTO clearSearch(DocSearchCriteriaDTO searchCriteria);

    public int getDocumentSearchResultSetLimit();

    /**
     *
     * This method returns if processResultSet should be called.
     *
     * @return
     */
    public boolean isProcessResultSet();
    public void setProcessResultSet(boolean isProcessResultSet);

    public MessageMap getMessageMap(DocSearchCriteriaDTO searchCriteria);

}
