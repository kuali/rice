/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.workflow.tools.xml;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleExtension {

	private Attribute attribute;
	private Template template;
	private List<RuleExtensionValue> extensionValues = new ArrayList<RuleExtensionValue>();

	public Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public List<RuleExtensionValue> getExtensionValues() {
		return extensionValues;
	}
	public void setExtensionValues(List<RuleExtensionValue> extensionValues) {
		this.extensionValues = extensionValues;
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}

}
