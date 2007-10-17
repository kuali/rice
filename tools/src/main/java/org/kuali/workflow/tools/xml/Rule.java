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
public class Rule {

	private DocumentType documentType;
	private Template template;
	private String description;
	private Boolean ignorePrevious;
	private List<RuleExtension> ruleExtensions = new ArrayList<RuleExtension>();
	private List<Responsibility> responsibilities = new ArrayList<Responsibility>();

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	public Boolean getIgnorePrevious() {
		return ignorePrevious;
	}
	public void setIgnorePrevious(Boolean ignorePrevious) {
		this.ignorePrevious = ignorePrevious;
	}
	public List<Responsibility> getResponsibilities() {
		return responsibilities;
	}
	public void setResponsibilities(List<Responsibility> responsibilities) {
		this.responsibilities = responsibilities;
	}
	public List<RuleExtension> getRuleExtensions() {
		return ruleExtensions;
	}
	public void setRuleExtensions(List<RuleExtension> ruleExtensions) {
		this.ruleExtensions = ruleExtensions;
	}
	public RuleExtension getRuleExtensionForAttribute(String attributeName) {
		for (RuleExtension ruleExtension : ruleExtensions) {
			if (ruleExtension.getAttribute().getName().equals(attributeName)) {
				return ruleExtension;
			}
		}
		return null;
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}

	public String toString() {
		String toString = "Rule: \n"+
			"[documentType: " + (documentType == null ? "null" : documentType.getName()) + "]\n"+
			"[template: " + (template == null ? "null" : template.getName()) + "]\n" +
			"[ignorePrevious: " + ignorePrevious + "]\n" +
			"[description: " + description + "]\n" +
			"[extensions: ";
			for (RuleExtension extension : getRuleExtensions()) {
				for (RuleExtensionValue extensionValue : extension.getExtensionValues()) {
					toString += "["+extensionValue.getKey()+"="+extensionValue.getValue()+"]";
				}
			}
			toString += "]\n";
		return toString;
	}

}
