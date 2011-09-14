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
package org.kuali.rice.kew.api.document.lookup;

import java.util.List;

/**
 * Defines the contract for results returned from a document lookup.  A document lookup returns multiple results, each
 * one representing a document and it's document attributes.  The results additional include information about the
 * criteria that was used to execute the lookup, as well as whether or not it was modified after it was submitted for
 * execution of the document lookup.
 *
 * <p>Additionally, results from the document lookup might be filtered for a particular principal for security purposes.
 * In these cases, the document lookup results include information on the number of results that were filtered out
 * because the principal executing the lookup did not have permission to view them.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupResultsContract {

    /**
     * Returns the unmodifiable list of lookup results.  Each of these result objects represents a document returned
     * from the lookup.
     *
     * @return an unmodifiable list of lookup results, will never be null but may be null
     */
    List<? extends DocumentLookupResultContract> getLookupResults();

    /**
     * Returns the criteria that was used to execute the lookup.  This may not be the same criteria that was submitted
     * to the document lookup api since it is possible for criteria to be modified by backend processing of the
     * submitted criteria.  See {@link #isCriteriaModified()} for more information.
     *
     * @return the criteria used to execute this lookup, will never be null
     */
    DocumentLookupCriteriaContract getCriteria();

    /**
     * Returns true if the criteria on this lookup result was modified from the original criteria submitted by the
     * executor of the document lookup.  This may happen in cases where the document lookup implementation modifies the
     * given criteria.  This may be possible through document lookup customization hooks, or may happen as part of a
     * process of "defaulting" certain portions of the criteria.
     *
     * @return a boolean indicating whether or not the criteria was modified from it's original form prior to lookup
     * execution
     */
    boolean isCriteriaModified();

    /**
     * Returns true if the results of the lookup returned more rows then the document lookup framework is allowed to
     * return back to the caller of the api.  The implementation of the document lookup is permitted to cap the number
     * of results returned and a result cap can also be specified on the criteria itself.
     *
     * @return true if there are more results available for the requested lookup then can be included in the list of
     * results
     */
    boolean isOverThreshold();

    /**
     * Return the number of results that matched the criteria but are not included on this results instance because they
     * principal executing the document lookup did not have permissions to view them.
     *
     * @return the number of results that were filtered for security reasons
     */
    int getNumberOfSecurityFilteredResults();

}
