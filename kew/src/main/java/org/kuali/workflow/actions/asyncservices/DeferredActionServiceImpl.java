/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.workflow.actions.asyncservices;

import java.io.Serializable;

import org.kuali.workflow.actions.ActionHandler;
import org.kuali.workflow.actions.ActionHandlerRegistryImpl;
import org.kuali.workflow.actions.DeferrableActionHandler;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Service that invokes the deferred part of an action that has already been handled
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DeferredActionServiceImpl implements DeferredActionService {
    /**
     * @see org.kuali.workflow.actions.asyncservices.DeferredActionService#performDeferredWork(java.lang.String, java.lang.Long, edu.iu.uis.eden.user.WorkflowUser, java.lang.Object[])
     */
    public void performDeferredWork(String action, Long routeHeaderId, WorkflowUser user, Object... args) {
        ActionHandler actionHandler;
        try {
            actionHandler = new ActionHandlerRegistryImpl().getActionHandler(action);
        } catch (Exception e) {
            throw new WorkflowRuntimeException("Unable to obtain action handler for action '" + action + "'");
        }

        if (!(actionHandler instanceof DeferrableActionHandler)) {
            throw new WorkflowRuntimeException("Deferred work invoked for non-deferrable ActionHandler for action '" + action + "': " + actionHandler.getClass().getName());
        }
        ((DeferrableActionHandler) actionHandler).performDeferredWork(routeHeaderId, user, args);
    }
}