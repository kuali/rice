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
package org.kuali.rice.kew.export;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kim.bo.Group;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KewExportDataSet {

	public static final QName DOCUMENT_TYPES = new QName("KEW", "documentTypes");
	public static final QName GROUPS = new QName("KEW", "groups");
	public static final QName RULE_ATTRIBUTES = new QName("KEW", "ruleAttributes");
	public static final QName RULE_TEMPLATES = new QName("KEW", "ruleTemplates");
	public static final QName RULES = new QName("KEW", "rules");
	public static final QName RULE_DELEGATIONS = new QName("KEW", "ruleDelegations");
	public static final QName HELP = new QName("KEW", "help");
	public static final QName EDOCLITES = new QName("KEW", "eDocLites");
	
	private List<DocumentType> documentTypes = new ArrayList<DocumentType>();
	private List<Group> groups = new ArrayList<Group>();
	private List<RuleAttribute> ruleAttributes = new ArrayList<RuleAttribute>();
	private List<RuleTemplate> ruleTemplates = new ArrayList<RuleTemplate>();
	private List<RuleBaseValues> rules = new ArrayList<RuleBaseValues>();
	private List<RuleDelegation> ruleDelegations = new ArrayList<RuleDelegation>();
	private List<HelpEntry> help = new ArrayList<HelpEntry>();

	public List<DocumentType> getDocumentTypes() {
		return documentTypes;
	}

	public List<HelpEntry> getHelp() {
		return help;
	}

	public List<RuleAttribute> getRuleAttributes() {
		return ruleAttributes;
	}

	public List<RuleBaseValues> getRules() {
		return rules;
	}

	public List<RuleTemplate> getRuleTemplates() {
		return ruleTemplates;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<RuleDelegation> getRuleDelegations() {
		return this.ruleDelegations;
	}
	
	public void populateExportDataSet(ExportDataSet exportDataSet) {
		if (documentTypes != null && !documentTypes.isEmpty()) {
			exportDataSet.addDataSet(DOCUMENT_TYPES, documentTypes);
		}
		if (groups != null && !groups.isEmpty()) {
			exportDataSet.addDataSet(GROUPS, groups);
		}
		if (ruleAttributes != null && !ruleAttributes.isEmpty()) {
			exportDataSet.addDataSet(RULE_ATTRIBUTES, ruleAttributes);
		}
		if (ruleTemplates != null && !ruleTemplates.isEmpty()) {
			exportDataSet.addDataSet(RULE_TEMPLATES, ruleTemplates);
		}
		if (rules != null && !rules.isEmpty()) {
			exportDataSet.addDataSet(RULES, rules);
		}
		if (ruleDelegations != null && !ruleDelegations.isEmpty()) {
			exportDataSet.addDataSet(RULE_DELEGATIONS, ruleDelegations);
		}
		if (help != null && !help.isEmpty()) {
			exportDataSet.addDataSet(HELP, help);
		}
	}
	
	public ExportDataSet createExportDataSet() {
		ExportDataSet exportDataSet = new ExportDataSet();
		populateExportDataSet(exportDataSet);
		return exportDataSet;
	}
	
	public static KewExportDataSet fromExportDataSet(ExportDataSet exportDataSet) {
		KewExportDataSet kewExportDataSet = new KewExportDataSet();
		
		List<DocumentType> documentTypes = (List<DocumentType>)exportDataSet.getDataSets().get(DOCUMENT_TYPES);
		if (documentTypes != null) {
			kewExportDataSet.getDocumentTypes().addAll(documentTypes);
		}
		List<Group> groups = (List<Group>)exportDataSet.getDataSets().get(GROUPS);
		if (groups != null) {
			kewExportDataSet.getGroups().addAll(groups);
		}
		List<RuleAttribute> ruleAttributes = (List<RuleAttribute>)exportDataSet.getDataSets().get(RULE_ATTRIBUTES);
		if (ruleAttributes != null) {
			kewExportDataSet.getRuleAttributes().addAll(ruleAttributes);
		}
		List<RuleTemplate> ruleTemplates = (List<RuleTemplate>)exportDataSet.getDataSets().get(RULE_TEMPLATES);
		if (ruleTemplates != null) {
			kewExportDataSet.getRuleTemplates().addAll(ruleTemplates);
		}
		List<RuleBaseValues> rules = (List<RuleBaseValues>)exportDataSet.getDataSets().get(RULES);
		if (rules != null) {
			kewExportDataSet.getRules().addAll(rules);
		}
		List<RuleDelegation> ruleDelegations = (List<RuleDelegation>)exportDataSet.getDataSets().get(RULE_DELEGATIONS);
		if (ruleDelegations != null) {
			kewExportDataSet.getRuleDelegations().addAll(ruleDelegations);
		}
		List<HelpEntry> help = (List<HelpEntry>)exportDataSet.getDataSets().get(HELP);
		if (help != null) {
			kewExportDataSet.getHelp().addAll(help);
		}
		
		return kewExportDataSet;
	}
	
}
