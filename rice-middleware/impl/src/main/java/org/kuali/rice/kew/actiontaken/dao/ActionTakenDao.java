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
package org.kuali.rice.kew.actiontaken.dao;

import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.action.ActionType;

import java.sql.Timestamp;


/**
 * Data Access Object for {@link ActionTakenValue}s.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ActionTakenDao {

    /**
     * Returns the Timestamp of the last action of the given type taken against the document.
     *
     * <p>Both {@code documentId} and {@code actionType} must be supplied to this query. Otherwise an
     * {@code IllegalArgumentException} will be thrown.</p>
     *
     * @param documentId the id of the document to check
     * @param actionType the type of the action to look for
     *
     * @return the Timestamp of the last action of the given type taken against the document, or null if the document
     * has no actions
     *
     * @throws IllegalArgumentException if documentId is null or blank, or if actionType is null
     */
    Timestamp getLastActionTakenDate(String documentId, ActionType actionType);

}
