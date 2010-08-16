/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.actiontaken.dao;

import org.kuali.rice.kew.actiontaken.ActionTakenValue;

import java.util.Collection;
import java.util.List;


/**
 * Data Access Object for {@link ActionTakenValue}s.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ActionTakenDAO {

    public ActionTakenValue load(Long id);

    public void saveActionTaken(ActionTakenValue actionTaken);

    public void deleteActionTaken(ActionTakenValue actionTaken);

    public ActionTakenValue findByActionTakenId(Long actionTakenId);

    public Collection<ActionTakenValue> findByRouteHeaderId(Long routeHeaderId);

    public Collection<ActionTakenValue> findByDocIdAndAction(Long docId, String action);

    public List<ActionTakenValue> findByRouteHeaderIdWorkflowId(Long routeHeaderId, String workflowId);

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId);

    public void deleteByRouteHeaderId(Long routeHeaderId);

    public boolean hasUserTakenAction(String workflowId, Long routeHeaderId);

}
