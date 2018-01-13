/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krms.framework.type;

import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.framework.engine.Action;

/**
 * Interface defining the loading of an {@link Action} from a {@link ActionDefinition}
 *
 * @see Action
 * @see ActionDefinition
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface ActionTypeService extends RemotableAttributeOwner {

    /**
     * Load the {@link Action} given the {@link ActionDefinition}
     * @param actionDefinition {@link ActionDefinition} to create the {@link Action} from
     * @return {@link Action} created from the given {@link ActionDefinition}
     */
	public Action loadAction(ActionDefinition actionDefinition);

}
