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
package org.kuali.workflow.actions;

import java.io.Serializable;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * A class that handles actions taken/submitted by the client, which can also
 * perform some "deferred" work after control has been returned to the client
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DeferrableActionHandler extends ActionHandler {
    public void performDeferredWork(Long routeHeaderId, WorkflowUser user, Object... args);
}