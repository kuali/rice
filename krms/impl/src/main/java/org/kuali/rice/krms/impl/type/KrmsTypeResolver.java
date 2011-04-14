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
package org.kuali.rice.krms.impl.type;

import org.kuali.rice.krms.api.repository.ActionDefinition;
import org.kuali.rice.krms.api.repository.PropositionDefinition;
import org.kuali.rice.krms.api.type.KrmsTypeDefinition;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.kuali.rice.krms.framework.type.PropositionTypeService;

/**
 * TODO ... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KrmsTypeResolver {

	PropositionTypeService getPropositionTypeService(PropositionDefinition propositionDefinition, KrmsTypeDefinition typeDefinition);
	
	ActionTypeService getActionTypeService(ActionDefinition actionDefinition, KrmsTypeDefinition typeDefinition);
	
}
