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
package org.kuali.rice.krad.web.service;

import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller service that provides handling for query methods suggest as performing a lookup, field suggest,
 * and field query.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface QueryControllerService {

    /**
     * Handles the perform lookup action by building up a URL to the lookup view and configuring
     * a redirect.
     *
     * @param form form instance containing the model data
     * @return ModelAndView configured for redirecting to the lookup view
     */
    ModelAndView performLookup(UifFormBase form);

    /**
     * Invoked to provides options (execute a query) for a field that contains a suggest widget.
     *
     * @param form form instance containing the model data
     * @return AttributeQueryResult containing the results of the suggest query
     */
    AttributeQueryResult performFieldSuggest(UifFormBase form);

    /**
     * Invoked to execute the attribute query associated with a field given the query parameters
     * found in the request
     *
     * @param form form instance containing the model data
     * @return AttributeQueryResult containing the results of the field query
     */
    AttributeQueryResult performFieldQuery(UifFormBase form);
}
