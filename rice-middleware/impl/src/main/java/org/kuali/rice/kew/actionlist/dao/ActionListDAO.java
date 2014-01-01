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
package org.kuali.rice.kew.actionlist.dao;

import java.util.List;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;


/**
 * Data Access object for the Action List.
 *
 * @see ActionItem
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ActionListDAO {

    /**
     * Get the total count of items in the given person's action list.
     */
    public int getCount(String principalId);

    /**
     * Get the maximum last action taken date and total count for items in the person's action list.
     *
     * This is used to help with the action list caching and detection of changes.
     */
    public List<Object> getMaxActionItemDateAssignedAndCountForUser(String principalId);

    /**
     * Pulls a proxied version of the document route header with only the properties needed by the
     * action list display.
     */
    DocumentRouteHeaderValue getMinimalRouteHeader( String documentId );
}
