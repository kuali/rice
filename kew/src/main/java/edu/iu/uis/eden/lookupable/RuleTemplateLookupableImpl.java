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
package edu.iu.uis.eden.lookupable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.util.Utilities;

/**
 * A {@link WorkflowLookupable} implementation for {@link RuleTemplate}.
 * 
 * @see RuleTemplate
 * @see RuleTemplateService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplateLookupableImpl implements WorkflowLookupable, Exportable {


    private List rows;
    private static List columns = establishColumns();
    private static final String title = "Rule Template Lookup";
    private static final String returnLocation = "Lookup.do";

    private static final String RULE_TEMPLATE_FIELD_LABEL = "Name";
    private static final String DESCRIPTION_FIELD_LABEL = "Description";

    private static final String RULE_TEMPLATE_FIELD_HELP = "";
    private static final String DESCRIPTION_FIELD_HELP = "";

    private static final String RULE_TEMPLATE_PROPERTY_NAME = "ruleTemplateName";
    private static final String DESCRIPTION_PROPERTY_NAME = "description";

    private static final String RULE_TEMPLATE_ID_PROPERTY_NAME = "ruleTemplate.ruleTemplateId";
    private static final String BACK_LOCATION = "backLocation";
    private static final String DOC_FORM_KEY = "docFormKey";

    public RuleTemplateLookupableImpl() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field(RULE_TEMPLATE_FIELD_LABEL, RULE_TEMPLATE_FIELD_HELP, Field.TEXT, false, RULE_TEMPLATE_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(DESCRIPTION_FIELD_LABEL, DESCRIPTION_FIELD_HELP, Field.TEXT, false, DESCRIPTION_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));
    }

    private static List establishColumns() {
        List columnList = new ArrayList();
        columnList.add(new Column("Rule Template Id", Column.COLUMN_IS_SORTABLE_VALUE, "ruleTemplateId"));
        columnList.add(new Column("Rule Template Name", Column.COLUMN_IS_SORTABLE_VALUE, "name"));
        columnList.add(new Column("Rule Template Description", Column.COLUMN_IS_SORTABLE_VALUE, "description"));
        columnList.add(new Column("Rule Template Delegate", Column.COLUMN_IS_SORTABLE_VALUE, "delegateTemplateName"));
        columnList.add(new Column("Rule Template Actions", Column.COLUMN_NOT_SORTABLE_VALUE, "ruleTemplateActionsUrl"));
        return columnList;
    }

    public void changeIdToName(Map fieldValues) {
        
    }
    /**
     * getSearchResults - searches for a fiscal organization information based on the criteria passed in by the map.
     * 
     * @return Returns a list of FiscalOrganization objects that match the result.
     */
    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
        String name = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);
        String description = (String) fieldValues.get(DESCRIPTION_PROPERTY_NAME);
        String backLocation = (String) fieldValues.get(BACK_LOCATION);
        String docFormKey = (String) fieldValues.get(DOC_FORM_KEY);

        String ruleTemplateIdReturn = (String) fieldConversions.get(RULE_TEMPLATE_ID_PROPERTY_NAME);

        RuleTemplateService ruleTemplateService = (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
        RuleTemplate ruleTemplate = new RuleTemplate();

        if (name != null && !"".equals(name.trim())) {
            name = name.replace('*', '%');
            ruleTemplate.setName("%"+name.trim()+"%");
        }

        if (description != null && !"".equals(description.trim())) {
            description = description.replace('*', '%');
            ruleTemplate.setDescription("%"+description.trim()+"%");
        }

        Iterator ruleTemplates = ruleTemplateService.findByRuleTemplate(ruleTemplate).iterator();
        List displayList = new ArrayList();
        while (ruleTemplates.hasNext()) {
            RuleTemplate record = (RuleTemplate) ruleTemplates.next();

            StringBuffer returnUrl = new StringBuffer("<a href=\"");
            returnUrl.append(backLocation).append("?methodToCall=refresh&docFormKey=").append(docFormKey).append("&");
            if (!Utilities.isEmpty(ruleTemplateIdReturn)) {
                returnUrl.append(ruleTemplateIdReturn);
            } else {
                returnUrl.append(RULE_TEMPLATE_ID_PROPERTY_NAME);
            }
            returnUrl.append("=").append(record.getRuleTemplateId()).append("\">return value</a>");
            record.setReturnUrl(returnUrl.toString());

            displayList.add(record);
        }
        return displayList;
    }
    
    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        return false;
    }

    public List getDefaultReturnType(){
        List returnTypes = new ArrayList();
        returnTypes.add(RULE_TEMPLATE_ID_PROPERTY_NAME);
        return returnTypes;
    }

    public String getNoReturnParams(Map fieldConversions) {
        String ruleTemplateIdReturn = (String) fieldConversions.get(RULE_TEMPLATE_ID_PROPERTY_NAME);
        StringBuffer noReturnParams = new StringBuffer("&");
        if (!Utilities.isEmpty(ruleTemplateIdReturn)) {
            noReturnParams.append(ruleTemplateIdReturn);
        } else {
            noReturnParams.append(RULE_TEMPLATE_ID_PROPERTY_NAME);
        }
        noReturnParams.append("=&").append(RULE_TEMPLATE_PROPERTY_NAME).append("=");
        return noReturnParams.toString();
    }

    public String getLookupInstructions() {
        return Utilities.getApplicationConstant(EdenConstants.RULE_TEMPLATE_SEARCH_INSTRUCTION_KEY);
    }

    public String getTitle() {
        return title;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public List getColumns() {
        return columns;
    }

    public String getHtmlMenuBar() {
        return ""; // "<a href=\"RuleTemplate.do\" >Create new Rule Template</a>";
    }

    public List getRows() {
        return rows;
    }
    
    public ExportDataSet export(ExportFormat format, Object exportCriteria) throws Exception {
        List searchResults = (List)exportCriteria;
        ExportDataSet dataSet = new ExportDataSet(format);
        dataSet.getRuleTemplates().addAll(searchResults);
        return dataSet;
    }

    public List getSupportedExportFormats() {
        return EdenConstants.STANDARD_FORMATS;
    }
    
}
