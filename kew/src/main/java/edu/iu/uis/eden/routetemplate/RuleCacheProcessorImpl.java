/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.routetemplate;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.util.PerformanceLogger;

/**
 * Implementation of the {@link RuleCacheProcessor} which notifies the rule cache of
 * a change to the specified rule.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleCacheProcessorImpl implements RuleCacheProcessor {
	
	public void clearRuleFromCache(Long ruleId) {
        PerformanceLogger logger = new PerformanceLogger();
		RuleBaseValues rule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(ruleId);
		if (rule != null) {
			KEWServiceLocator.getRuleService().notifyCacheOfRuleChange(rule, null);
		}
		logger.log("Time to notify cache of rule change for rule "+(rule == null ? "null" : ""+rule.getRuleBaseValuesId()));
	}

}
