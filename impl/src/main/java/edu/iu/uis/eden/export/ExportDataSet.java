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
package edu.iu.uis.eden.export;

import java.util.ArrayList;
import java.util.List;

import org.kuali.workflow.workgroup.WorkgroupType;

import edu.iu.uis.eden.edl.EDocLiteStyle;

/**
 * A set of data to be exported to a KEW XML file.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ExportDataSet {

    private ExportFormat format;

    private List applicationConstants = new ArrayList();
    private List documentTypes = new ArrayList();
    private List workgroups = new ArrayList();
    private List users = new ArrayList();
    private List ruleAttributes = new ArrayList();
    private List ruleTemplates = new ArrayList();
    private List rules = new ArrayList();
    private List help = new ArrayList();
    private List edocLites = new ArrayList();
    private List<EDocLiteStyle> styles = new ArrayList<EDocLiteStyle>();
    private List<WorkgroupType> workgroupTypes = new ArrayList<WorkgroupType>();

    public ExportDataSet(ExportFormat format) {
        this.format = format;
    }

    public ExportFormat getFormat() {
        return format;
    }
    public List getApplicationConstants() {
        return applicationConstants;
    }
    public List getDocumentTypes() {
        return documentTypes;
    }
    public List getHelp() {
        return help;
    }
    public List<EDocLiteStyle> getStyles() {
        return styles;
    }
    public List getRuleAttributes() {
        return ruleAttributes;
    }
    public List getRules() {
        return rules;
    }
    public List getRuleTemplates() {
        return ruleTemplates;
    }
    public List getUsers() {
        return users;
    }
    public List getWorkgroups() {
        return workgroups;
    }
	public List getEdocLites() {
		return edocLites;
	}
	public void setEdocLites(List edocLites) {
		this.edocLites = edocLites;
	}
	public List<WorkgroupType> getWorkgroupTypes() {
		return workgroupTypes;
	}
}