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

import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;

/**
 * A {@link WorkflowAttributeDefinitionVO} for the {@link RuleRoutingAttribute}.
 * 
 * @see RuleRoutingAttribute
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleRoutingDefinition extends WorkflowAttributeDefinitionVO {
        
	private static final long serialVersionUID = -5633697385117416044L;
	private static final String RULE_ROUTING_ATTRIBUTE_CLASS = "edu.iu.uis.eden.routetemplate.RuleRoutingAttribute";
        
	public RuleRoutingDefinition(String docTypeName) {
		this();
		this.addConstructorParameter(docTypeName);
	}
	
	private RuleRoutingDefinition() {
		super(RULE_ROUTING_ATTRIBUTE_CLASS);
	}

}
