/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.uif.service;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.UifConstants.ViewType;

import java.util.Map;

/**
 * Provides service methods for retrieving and updating <code>View</code> instances. The UIF
 * interacts with this service from the client layer to pull information from the View dictionary
 * and manage the View instance through its lifecycle
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewService {

    /**
     * Returns the <code>View</code> entry identified by the given id
     *
     * <p>
     * The id matches the id configured for the View through the dictionary. A new view instance
     * is returned that is in the created state
     * </p>
     *
     * @param viewId - unique id for view configured on its definition
     * @return View instance associated with the id or Null if id is not found
     */
    public View getViewById(String viewId);

    /**
     * Retrieves the <code>View</code> instance that is of the given view type and matches the
     * given parameters (that are applicable for that type). If more than one views exists for the
     * type and parameters, the view type may choose a default or throw an exception
     *
     * <p>
     * If a view if found for the type parameters, a new instance is returned that is in the
     * created state
     * </p>
     *
     * @param viewType - name that identifies the view type
     * @param parameters - Map of parameter key/value pairs that are used to select the
     * view, the parameters allowed depend on the view type
     * @return View instance or Null if a matching view was not found
     */
    public View getViewByType(ViewType viewType, Map<String, String> parameters);

    /**
     * Retrieves the view id for the view associated with the given view type and view type parameters
     *
     * @param viewType name that identifies the view type
     * @param parameters Map of parameter key/value pairs that are used to select the
     * view, the parameters allowed depend on the view type
     * @return id for the view or null if a matching view was not found
     */
    public String getViewIdForViewType(ViewType viewType, Map<String, String> parameters);

    // TODO: remove once can get beans by type
    public ViewTypeService getViewTypeService(UifConstants.ViewType viewType);

}
