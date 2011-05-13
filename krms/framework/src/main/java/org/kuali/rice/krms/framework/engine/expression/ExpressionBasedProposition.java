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
package org.kuali.rice.krms.framework.engine.expression;

import java.util.Collections;
import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.framework.engine.Proposition;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ExpressionBasedProposition implements Proposition {

	private final Expression<Boolean> expression;
	
	public ExpressionBasedProposition(Expression<Boolean> expression) {
		this.expression = expression;
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		return expression.invoke(environment).booleanValue();
	}


    @Override
    public List<Proposition> getChildren() {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isCompound() {
        return false;
    }
}
