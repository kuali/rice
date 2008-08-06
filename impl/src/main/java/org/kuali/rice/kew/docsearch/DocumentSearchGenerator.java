/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.docsearch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.kuali.rice.kew.WorkflowServiceError;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.WorkflowUser;


/**
 * TODO delyea - documentation
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DocumentSearchGenerator {
	public static final int DEFAULT_SEARCH_RESULT_CAP = 500;

	public void setSearchableAttributes(List searchableAttributes);
	public void setSearchingUser(WorkflowUser searchingUser);
    public List<WorkflowServiceError> performPreSearchConditions(WorkflowUser user, DocSearchCriteriaVO searchCriteria);
    public List<WorkflowServiceError> validateSearchableAttributes(DocSearchCriteriaVO searchCriteria);
    public String generateSearchSql(DocSearchCriteriaVO searchCriteria) throws KEWUserNotFoundException;
    /**
     * @deprecated Removed as of version 0.9.3.  Use {@link #processResultSet(Statement, ResultSet, DocSearchCriteriaVO, WorkflowUser)} instead.
     */
    public List<DocSearchVO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaVO searchCriteria) throws KEWUserNotFoundException, SQLException;
    /**
     * This method processes search results in the given <code>resultSet</code> into {@link DocSearchVO} objects
     * 
     * @param searchAttributeStatement - statement to use when fetching search attributes
     * @param resultSet - resultSet containing data from document search main tables
     * @param searchCriteria - criteria used to perform the search
     * @param user - user who performed the search
     * @return a list of DocSearchVO objects (one for each route header id)
     * @throws KEWUserNotFoundException
     * @throws SQLException
     */
    public List<DocSearchVO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaVO searchCriteria, WorkflowUser user) throws KEWUserNotFoundException, SQLException;    public DocSearchCriteriaVO clearSearch(DocSearchCriteriaVO searchCriteria);
    public int getDocumentSearchResultSetLimit();
}
