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
package org.kuali.rice.kew.export;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.applicationconstants.ApplicationConstant;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.edl.bo.EDocLiteAssociation;
import org.kuali.rice.kew.edl.bo.EDocLiteStyle;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kew.workgroup.WorkgroupType;


/**
 * A set of data to be exported to a KEW XML file.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ExportDataSet {

    private List<ApplicationConstant> applicationConstants = new ArrayList<ApplicationConstant>();
    private List<DocumentType> documentTypes = new ArrayList<DocumentType>();
    private List<Workgroup> workgroups = new ArrayList<Workgroup>();
    private List<WorkflowUser> users = new ArrayList<WorkflowUser>();
    private List<RuleAttribute> ruleAttributes = new ArrayList<RuleAttribute>();
    private List<RuleTemplate> ruleTemplates = new ArrayList<RuleTemplate>();
    private List<RuleBaseValues> rules = new ArrayList<RuleBaseValues>();
    private List<HelpEntry> help = new ArrayList<HelpEntry>();
    private List<EDocLiteAssociation> edocLites = new ArrayList<EDocLiteAssociation>();
    private List<EDocLiteStyle> styles = new ArrayList<EDocLiteStyle>();
    private List<WorkgroupType> workgroupTypes = new ArrayList<WorkgroupType>();

    public List<ApplicationConstant> getApplicationConstants() {
        return applicationConstants;
    }
    public List<DocumentType> getDocumentTypes() {
        return documentTypes;
    }
    public List<HelpEntry> getHelp() {
        return help;
    }
    public List<EDocLiteStyle> getStyles() {
        return styles;
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
    public List<WorkflowUser> getUsers() {
        return users;
    }
    public List<Workgroup> getWorkgroups() {
        return workgroups;
    }
	public List<EDocLiteAssociation> getEdocLites() {
		return edocLites;
	}
	public List<WorkgroupType> getWorkgroupTypes() {
		return workgroupTypes;
	}
}