/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.definition;

import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.kew.rule.bo.RuleAttribute;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeDefinition {

	private RuleAttribute ruleAttribute;
	private ObjectDefinition objectDefinition;

	public AttributeDefinition(RuleAttribute ruleAttribute, ObjectDefinition objectDefinition) {
		this.ruleAttribute = ruleAttribute;
		this.objectDefinition = objectDefinition;
	}

	public ObjectDefinition getObjectDefinition() {
		return objectDefinition;
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition) {
		this.objectDefinition = objectDefinition;
	}

	public RuleAttribute getRuleAttribute() {
		return ruleAttribute;
	}

	public void setRuleAttribute(RuleAttribute ruleAttribute) {
		this.ruleAttribute = ruleAttribute;
	}

}
