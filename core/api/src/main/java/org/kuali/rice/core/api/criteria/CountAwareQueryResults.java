/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;


/**
 * Indicates a set of {@link QueryResults} which provide information related to
 * the count of rows returned by the query as well as whether or not the query
 * returned all available results.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface CountAwareQueryResults<T> extends QueryResults<T> {

	/**
	 * Gets the total number of records that match the executed query.  Note
	 * that this number will not necessarily match the number of results
	 * returned by {@link #getResults()} as the query may cut off the number
	 * of records returned by the actual query request.  In these cases, the
	 * total row count represents the total number of results which would be
	 * returned by the query if there was no cap on the results returned (i.e.
	 * the equivalent of the result of a "count" query in SQL).
	 * 
	 * <p>The total row count is optional depending on whether or not the
	 * client requested the total row count when invoking the query.  It's also
	 * possible that the query is unable to produce a total row count depending
	 * on the back-end implementation, in which cases this value will also not
	 * be available.
     *
     * <p>
     * Will never be less than 0.
     *
     * <p>
	 * 
	 * @return the total number of rows, or null if the total row count was not
	 * requested by the query or could not be determined 
	 */
	Integer getTotalRowCount();

	/**
	 * Indicates if there are more results available for the query immediately
	 * following the last result that was returned.  In this case, the records
	 * returned in {@link #getResults()} will not include the complete result
	 * set for the query.  This could be because the query only requested a
	 * certain number of rows, or that the query couldn't return the number
	 * of rows that were requested.
	 * 
	 * <p>It is intended that this value be used to facilitate paging or
	 * reporting in the client in cases where that is desired.
	 * 
	 * @return true if there are more results available for the query, false otherwise
	 */
	boolean isMoreResultsAvailable();
	
}
