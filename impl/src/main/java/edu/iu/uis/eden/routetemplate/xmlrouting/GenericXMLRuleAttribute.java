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
package edu.iu.uis.eden.routetemplate.xmlrouting;

import java.util.Map;

import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 * A {@link WorkflowAttribute} which is configured via an XML definition.
 * Since it has no specific getters and setters for it's various
 * properties, it uses a Map of parameters to represent it's
 * attribute properties.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface GenericXMLRuleAttribute extends WorkflowAttribute {

	public void setRuleAttribute(RuleAttribute ruleAttribute);
	public void setParamMap(Map paramMap);
	public Map getParamMap();
}
