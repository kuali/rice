/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.framework.engine.expression;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.framework.engine.Function;

/**
 * TODO...
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public final class FunctionExpression implements Expression<Object> {

	private final Function function;
	private final List<Expression<? extends Object>> arguments;

	public FunctionExpression(Function function,
			List<Expression<? extends Object>> arguments) {
		this.function = function;
		this.arguments = arguments;
	}

	@Override
	public Object invoke(ExecutionEnvironment environment) {
		List<Object> argumentValues = new ArrayList<Object>(arguments.size());
		for (Expression<? extends Object> argument : arguments) {
			argumentValues.add(argument.invoke(environment));
		}
		return function.invoke(argumentValues);
	}

}
