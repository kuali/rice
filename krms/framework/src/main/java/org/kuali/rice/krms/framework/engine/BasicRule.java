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
package org.kuali.rice.krms.framework.engine;

import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.framework.engine.result.BasicResult;

public class BasicRule implements Rule {
	private static final ResultLogger LOG = ResultLogger.getInstance();

	private String name;
	private Proposition proposition;
	private List<Action> actions;
	
	public BasicRule(String name, Proposition proposition, List<Action> actions) {
		if (proposition == null) {
			throw new IllegalArgumentException("Proposition cannot be null.");
		}
		this.name = name;
		this.proposition = proposition;
		this.actions = actions;
	}
	
	public BasicRule(Proposition proposition, List<Action> actions) {
		this(null, proposition, actions);
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		boolean result = proposition.evaluate(environment).getResult();
		if (actions != null) {
			for (Action action : actions) {
				if (shouldExecuteAction(result)) {
					action.execute(environment);
				}
			}
		}
		if (LOG.isEnabled(environment)){
			LOG.logResult(new BasicResult(ResultEvent.RuleEvaluated, this, environment, result));
		}
		return result;
	}
	
	protected boolean shouldExecuteAction(boolean ruleExecutionResult) {
		return ruleExecutionResult;
	}

	public String getName() {
		return name;
	}
	
	public String toString(){
		return name;
	}
}
