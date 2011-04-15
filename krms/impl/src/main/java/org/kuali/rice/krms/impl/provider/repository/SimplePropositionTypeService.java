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
package org.kuali.rice.krms.impl.provider.repository;

import org.kuali.rice.krms.api.repository.PropositionDefinition;
import org.kuali.rice.krms.framework.engine.Proposition;
import org.kuali.rice.krms.framework.engine.expression.Expression;
import org.kuali.rice.krms.framework.engine.expression.ExpressionBasedProposition;
import org.kuali.rice.krms.framework.type.PropositionTypeService;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SimplePropositionTypeService implements PropositionTypeService {

	@Override
	public Proposition loadProposition(PropositionDefinition propositionDefinition) {
		return new ExpressionBasedProposition(translateToExpression(propositionDefinition));
	}
		
	private static Expression<Boolean> translateToExpression(PropositionDefinition propositionDefinition) {
		// TODO implement me!!! this is the hard part ;)
		return null;
	}

}
